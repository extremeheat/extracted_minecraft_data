package net.minecraft.world.entity;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LightningBolt extends Entity {
   private static final int START_LIFE = 2;
   private static final double DAMAGE_RADIUS = 3.0;
   private static final double DETECTION_RADIUS = 15.0;
   private int life;
   public long seed;
   private int flashes;
   private boolean visualOnly;
   @Nullable
   private ServerPlayer cause;
   private final Set<Entity> hitEntities = Sets.newHashSet();
   private int blocksSetOnFire;

   public LightningBolt(EntityType<? extends LightningBolt> var1, Level var2) {
      super(var1, var2);
      this.noCulling = true;
      this.life = 2;
      this.seed = this.random.nextLong();
      this.flashes = this.random.nextInt(3) + 1;
   }

   public void setVisualOnly(boolean var1) {
      this.visualOnly = var1;
   }

   @Override
   public SoundSource getSoundSource() {
      return SoundSource.WEATHER;
   }

   @Nullable
   public ServerPlayer getCause() {
      return this.cause;
   }

   public void setCause(@Nullable ServerPlayer var1) {
      this.cause = var1;
   }

   private void powerLightningRod() {
      BlockPos var1 = this.getStrikePosition();
      BlockState var2 = this.level().getBlockState(var1);
      if (var2.is(Blocks.LIGHTNING_ROD)) {
         ((LightningRodBlock)var2.getBlock()).onLightningStrike(var2, this.level(), var1);
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.life == 2) {
         if (this.level().isClientSide()) {
            this.level()
               .playLocalSound(
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  SoundEvents.LIGHTNING_BOLT_THUNDER,
                  SoundSource.WEATHER,
                  10000.0F,
                  0.8F + this.random.nextFloat() * 0.2F,
                  false
               );
            this.level()
               .playLocalSound(
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  SoundEvents.LIGHTNING_BOLT_IMPACT,
                  SoundSource.WEATHER,
                  2.0F,
                  0.5F + this.random.nextFloat() * 0.2F,
                  false
               );
         } else {
            Difficulty var1 = this.level().getDifficulty();
            if (var1 == Difficulty.NORMAL || var1 == Difficulty.HARD) {
               this.spawnFire(4);
            }

            this.powerLightningRod();
            clearCopperOnLightningStrike(this.level(), this.getStrikePosition());
            this.gameEvent(GameEvent.LIGHTNING_STRIKE);
         }
      }

      this.life--;
      if (this.life < 0) {
         if (this.flashes == 0) {
            if (this.level() instanceof ServerLevel) {
               List var4 = this.level()
                  .getEntities(
                     this,
                     new AABB(this.getX() - 15.0, this.getY() - 15.0, this.getZ() - 15.0, this.getX() + 15.0, this.getY() + 6.0 + 15.0, this.getZ() + 15.0),
                     var1x -> var1x.isAlive() && !this.hitEntities.contains(var1x)
                  );

               for (ServerPlayer var3 : ((ServerLevel)this.level()).getPlayers(var1x -> var1x.distanceTo(this) < 256.0F)) {
                  CriteriaTriggers.LIGHTNING_STRIKE.trigger(var3, this, var4);
               }
            }

            this.discard();
         } else if (this.life < -this.random.nextInt(10)) {
            this.flashes--;
            this.life = 1;
            this.seed = this.random.nextLong();
            this.spawnFire(0);
         }
      }

      if (this.life >= 0) {
         if (!(this.level() instanceof ServerLevel)) {
            this.level().setSkyFlashTime(2);
         } else if (!this.visualOnly) {
            List var5 = this.level()
               .getEntities(
                  this,
                  new AABB(this.getX() - 3.0, this.getY() - 3.0, this.getZ() - 3.0, this.getX() + 3.0, this.getY() + 6.0 + 3.0, this.getZ() + 3.0),
                  Entity::isAlive
               );

            for (Entity var7 : var5) {
               var7.thunderHit((ServerLevel)this.level(), this);
            }

            this.hitEntities.addAll(var5);
            if (this.cause != null) {
               CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.cause, var5);
            }
         }
      }
   }

   private BlockPos getStrikePosition() {
      Vec3 var1 = this.position();
      return BlockPos.containing(var1.x, var1.y - 1.0E-6, var1.z);
   }

   private void spawnFire(int var1) {
      if (!this.visualOnly && !this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         BlockPos var2 = this.blockPosition();
         BlockState var3 = BaseFireBlock.getState(this.level(), var2);
         if (this.level().getBlockState(var2).isAir() && var3.canSurvive(this.level(), var2)) {
            this.level().setBlockAndUpdate(var2, var3);
            this.blocksSetOnFire++;
         }

         for (int var4 = 0; var4 < var1; var4++) {
            BlockPos var5 = var2.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            var3 = BaseFireBlock.getState(this.level(), var5);
            if (this.level().getBlockState(var5).isAir() && var3.canSurvive(this.level(), var5)) {
               this.level().setBlockAndUpdate(var5, var3);
               this.blocksSetOnFire++;
            }
         }
      }
   }

   private static void clearCopperOnLightningStrike(Level var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      BlockPos var3;
      BlockState var4;
      if (var2.is(Blocks.LIGHTNING_ROD)) {
         var3 = var1.relative(var2.getValue(LightningRodBlock.FACING).getOpposite());
         var4 = var0.getBlockState(var3);
      } else {
         var3 = var1;
         var4 = var2;
      }

      if (var4.getBlock() instanceof WeatheringCopper) {
         var0.setBlockAndUpdate(var3, WeatheringCopper.getFirst(var0.getBlockState(var3)));
         BlockPos.MutableBlockPos var5 = var1.mutable();
         int var6 = var0.random.nextInt(3) + 3;

         for (int var7 = 0; var7 < var6; var7++) {
            int var8 = var0.random.nextInt(8) + 1;
            randomWalkCleaningCopper(var0, var3, var5, var8);
         }
      }
   }

   private static void randomWalkCleaningCopper(Level var0, BlockPos var1, BlockPos.MutableBlockPos var2, int var3) {
      var2.set(var1);

      for (int var4 = 0; var4 < var3; var4++) {
         Optional var5 = randomStepCleaningCopper(var0, var2);
         if (var5.isEmpty()) {
            break;
         }

         var2.set((Vec3i)var5.get());
      }
   }

   private static Optional<BlockPos> randomStepCleaningCopper(Level var0, BlockPos var1) {
      for (BlockPos var3 : BlockPos.randomInCube(var0.random, 10, var1, 1)) {
         BlockState var4 = var0.getBlockState(var3);
         if (var4.getBlock() instanceof WeatheringCopper) {
            WeatheringCopper.getPrevious(var4).ifPresent(var2 -> var0.setBlockAndUpdate(var3, var2));
            var0.levelEvent(3002, var3, -1);
            return Optional.of(var3);
         }
      }

      return Optional.empty();
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      double var3 = 64.0 * getViewScale();
      return var1 < var3 * var3;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
   }

   public int getBlocksSetOnFire() {
      return this.blocksSetOnFire;
   }

   public Stream<Entity> getHitEntities() {
      return this.hitEntities.stream().filter(Entity::isAlive);
   }
}
