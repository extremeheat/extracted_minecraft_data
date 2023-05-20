package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;

public class RemoveBlockGoal extends MoveToBlockGoal {
   private final Block blockToRemove;
   private final Mob removerMob;
   private int ticksSinceReachedGoal;
   private static final int WAIT_AFTER_BLOCK_FOUND = 20;

   public RemoveBlockGoal(Block var1, PathfinderMob var2, double var3, int var5) {
      super(var2, var3, 24, var5);
      this.blockToRemove = var1;
      this.removerMob = var2;
   }

   @Override
   public boolean canUse() {
      if (!this.removerMob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         return false;
      } else if (this.nextStartTick > 0) {
         --this.nextStartTick;
         return false;
      } else if (this.findNearestBlock()) {
         this.nextStartTick = reducedTickDelay(20);
         return true;
      } else {
         this.nextStartTick = this.nextStartTick(this.mob);
         return false;
      }
   }

   @Override
   public void stop() {
      super.stop();
      this.removerMob.fallDistance = 1.0F;
   }

   @Override
   public void start() {
      super.start();
      this.ticksSinceReachedGoal = 0;
   }

   public void playDestroyProgressSound(LevelAccessor var1, BlockPos var2) {
   }

   public void playBreakSound(Level var1, BlockPos var2) {
   }

   @Override
   public void tick() {
      super.tick();
      Level var1 = this.removerMob.level;
      BlockPos var2 = this.removerMob.blockPosition();
      BlockPos var3 = this.getPosWithBlock(var2, var1);
      RandomSource var4 = this.removerMob.getRandom();
      if (this.isReachedTarget() && var3 != null) {
         if (this.ticksSinceReachedGoal > 0) {
            Vec3 var5 = this.removerMob.getDeltaMovement();
            this.removerMob.setDeltaMovement(var5.x, 0.3, var5.z);
            if (!var1.isClientSide) {
               double var6 = 0.08;
               ((ServerLevel)var1)
                  .sendParticles(
                     new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.EGG)),
                     (double)var3.getX() + 0.5,
                     (double)var3.getY() + 0.7,
                     (double)var3.getZ() + 0.5,
                     3,
                     ((double)var4.nextFloat() - 0.5) * 0.08,
                     ((double)var4.nextFloat() - 0.5) * 0.08,
                     ((double)var4.nextFloat() - 0.5) * 0.08,
                     0.15000000596046448
                  );
            }
         }

         if (this.ticksSinceReachedGoal % 2 == 0) {
            Vec3 var12 = this.removerMob.getDeltaMovement();
            this.removerMob.setDeltaMovement(var12.x, -0.3, var12.z);
            if (this.ticksSinceReachedGoal % 6 == 0) {
               this.playDestroyProgressSound(var1, this.blockPos);
            }
         }

         if (this.ticksSinceReachedGoal > 60) {
            var1.removeBlock(var3, false);
            if (!var1.isClientSide) {
               for(int var13 = 0; var13 < 20; ++var13) {
                  double var14 = var4.nextGaussian() * 0.02;
                  double var8 = var4.nextGaussian() * 0.02;
                  double var10 = var4.nextGaussian() * 0.02;
                  ((ServerLevel)var1)
                     .sendParticles(
                        ParticleTypes.POOF,
                        (double)var3.getX() + 0.5,
                        (double)var3.getY(),
                        (double)var3.getZ() + 0.5,
                        1,
                        var14,
                        var8,
                        var10,
                        0.15000000596046448
                     );
               }

               this.playBreakSound(var1, var3);
            }
         }

         ++this.ticksSinceReachedGoal;
      }
   }

   @Nullable
   private BlockPos getPosWithBlock(BlockPos var1, BlockGetter var2) {
      if (var2.getBlockState(var1).is(this.blockToRemove)) {
         return var1;
      } else {
         BlockPos[] var3 = new BlockPos[]{var1.below(), var1.west(), var1.east(), var1.north(), var1.south(), var1.below().below()};

         for(BlockPos var7 : var3) {
            if (var2.getBlockState(var7).is(this.blockToRemove)) {
               return var7;
            }
         }

         return null;
      }
   }

   @Override
   protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
      ChunkAccess var3 = var1.getChunk(SectionPos.blockToSectionCoord(var2.getX()), SectionPos.blockToSectionCoord(var2.getZ()), ChunkStatus.FULL, false);
      if (var3 == null) {
         return false;
      } else {
         return var3.getBlockState(var2).is(this.blockToRemove) && var3.getBlockState(var2.above()).isAir() && var3.getBlockState(var2.above(2)).isAir();
      }
   }
}
