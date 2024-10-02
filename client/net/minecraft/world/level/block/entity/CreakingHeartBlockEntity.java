package net.minecraft.world.level.block.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.TargetColorParticleOption;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.monster.creaking.CreakingTransient;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class CreakingHeartBlockEntity extends BlockEntity {
   private static final int PLAYER_DETECTION_RANGE = 32;
   public static final int DISTANCE_TO_HOME_SQ = 1024;
   private static final int DISTANCE_CREAKING_TOO_FAR_SQ = 1156;
   private static final int SPAWN_RANGE_XZ = 16;
   private static final int SPAWN_RANGE_Y = 8;
   private static final int ATTEMPTS_PER_SPAWN = 5;
   private static final int UPDATE_TICKS = 20;
   private static final int HURT_CALL_TOTAL_TICKS = 100;
   private static final int NUMBER_OF_HURT_CALLS = 10;
   private static final int HURT_CALL_INTERVAL = 10;
   private static final int HURT_CALL_PARTICLE_TICKS = 50;
   @Nullable
   private CreakingTransient creaking;
   private int ticker;
   private int emitter;
   @Nullable
   private Vec3 emitterTarget;

   public CreakingHeartBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.CREAKING_HEART, var1, var2);
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, CreakingHeartBlockEntity var3) {
      if (var3.emitter > 0) {
         if (var3.emitter > 50) {
            var3.emitParticles((ServerLevel)var0, 1, true);
            var3.emitParticles((ServerLevel)var0, 1, false);
         }

         if (var3.emitter % 10 == 0 && var0 instanceof ServerLevel var4 && var3.emitterTarget != null) {
            if (var3.creaking != null) {
               var3.emitterTarget = var3.creaking.getBoundingBox().getCenter();
            }

            Vec3 var5 = Vec3.atCenterOf(var1);
            float var6 = 0.2F + 0.8F * (float)(100 - var3.emitter) / 100.0F;
            Vec3 var7 = var5.subtract(var3.emitterTarget).scale((double)var6).add(var3.emitterTarget);
            BlockPos var8 = BlockPos.containing(var7);
            float var9 = (float)var3.emitter / 2.0F / 100.0F + 0.5F;
            var4.playSound(null, var8, SoundEvents.CREAKING_HEART_HURT, SoundSource.BLOCKS, var9, 1.0F);
         }

         var3.emitter--;
      }

      if (var3.ticker-- < 0) {
         var3.ticker = 20;
         if (var3.creaking != null) {
            if (CreakingHeartBlock.canSummonCreaking(var0) && !(var3.creaking.distanceToSqr(Vec3.atBottomCenterOf(var1)) > 1156.0)) {
               if (var3.creaking.isRemoved()) {
                  var3.creaking = null;
               }

               if (!CreakingHeartBlock.hasRequiredLogs(var2, var0, var1) && var3.creaking == null) {
                  var0.setBlock(var1, var2.setValue(CreakingHeartBlock.CREAKING, CreakingHeartBlock.CreakingHeartState.DISABLED), 3);
               }
            } else {
               var3.removeProtector(null);
            }
         } else if (!CreakingHeartBlock.hasRequiredLogs(var2, var0, var1)) {
            var0.setBlock(var1, var2.setValue(CreakingHeartBlock.CREAKING, CreakingHeartBlock.CreakingHeartState.DISABLED), 3);
         } else {
            if (!CreakingHeartBlock.canSummonCreaking(var0)) {
               if (var2.getValue(CreakingHeartBlock.CREAKING) == CreakingHeartBlock.CreakingHeartState.ACTIVE) {
                  var0.setBlock(var1, var2.setValue(CreakingHeartBlock.CREAKING, CreakingHeartBlock.CreakingHeartState.DORMANT), 3);
                  return;
               }
            } else if (var2.getValue(CreakingHeartBlock.CREAKING) == CreakingHeartBlock.CreakingHeartState.DORMANT) {
               var0.setBlock(var1, var2.setValue(CreakingHeartBlock.CREAKING, CreakingHeartBlock.CreakingHeartState.ACTIVE), 3);
               return;
            }

            if (var2.getValue(CreakingHeartBlock.CREAKING) == CreakingHeartBlock.CreakingHeartState.ACTIVE) {
               if (var0.getDifficulty() != Difficulty.PEACEFUL) {
                  Player var10 = var0.getNearestPlayer((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), 32.0, false);
                  if (var10 != null) {
                     var3.creaking = spawnProtector((ServerLevel)var0, var3);
                     if (var3.creaking != null) {
                        var3.creaking.makeSound(SoundEvents.CREAKING_SPAWN);
                        var0.playSound(null, var3.getBlockPos(), SoundEvents.CREAKING_HEART_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   private static CreakingTransient spawnProtector(ServerLevel var0, CreakingHeartBlockEntity var1) {
      BlockPos var2 = var1.getBlockPos();
      Optional var3 = SpawnUtil.trySpawnMob(
         EntityType.CREAKING_TRANSIENT, EntitySpawnReason.SPAWNER, var0, var2, 5, 16, 8, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER_NO_LEAVES
      );
      if (var3.isEmpty()) {
         return null;
      } else {
         CreakingTransient var4 = (CreakingTransient)var3.get();
         var0.gameEvent(var4, GameEvent.ENTITY_PLACE, var4.position());
         var4.spawnAnim();
         var4.bindToCreakingHeart(var2);
         return var4;
      }
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public void creakingHurt() {
      if (this.creaking != null) {
         if (this.level instanceof ServerLevel var1) {
            this.emitParticles(var1, 20, false);
            this.emitter = 100;
            this.emitterTarget = this.creaking.getBoundingBox().getCenter();
         }
      }
   }

   private void emitParticles(ServerLevel var1, int var2, boolean var3) {
      if (this.creaking != null) {
         int var4 = var3 ? 16545810 : 6250335;
         RandomSource var5 = var1.random;

         for (double var6 = 0.0; var6 < (double)var2; var6++) {
            Vec3 var8 = this.creaking
               .getBoundingBox()
               .getMinPosition()
               .add(
                  var5.nextDouble() * this.creaking.getBoundingBox().getXsize(),
                  var5.nextDouble() * this.creaking.getBoundingBox().getYsize(),
                  var5.nextDouble() * this.creaking.getBoundingBox().getZsize()
               );
            Vec3 var9 = Vec3.atLowerCornerOf(this.getBlockPos()).add(var5.nextDouble(), var5.nextDouble(), var5.nextDouble());
            if (var3) {
               Vec3 var10 = var8;
               var8 = var9;
               var9 = var10;
            }

            TargetColorParticleOption var11 = new TargetColorParticleOption(var9, var4);
            var1.sendParticles(var11, var8.x, var8.y, var8.z, 1, 0.0, 0.0, 0.0, 0.0);
         }
      }
   }

   public void removeProtector(@Nullable DamageSource var1) {
      if (this.creaking != null) {
         this.creaking.tearDown(var1);
         this.creaking = null;
      }
   }

   public boolean isProtector(Creaking var1) {
      return this.creaking == var1;
   }
}
