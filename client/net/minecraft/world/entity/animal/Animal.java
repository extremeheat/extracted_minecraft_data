package net.minecraft.world.entity.animal;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class Animal extends AgeableMob {
   protected static final int PARENT_AGE_AFTER_BREEDING = 6000;
   private static final int DEFAULT_IN_LOVE_TIME = 0;
   private int inLove = 0;
   private @Nullable EntityReference<ServerPlayer> loveCause;

   protected Animal(EntityType<? extends Animal> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
   }

   public static AttributeSupplier.Builder createAnimalAttributes() {
      return Mob.createMobAttributes().add(Attributes.TEMPT_RANGE, 10.0);
   }

   protected void customServerAiStep(ServerLevel var1) {
      if (this.getAge() != 0) {
         this.inLove = 0;
      }

      super.customServerAiStep(var1);
   }

   public void aiStep() {
      super.aiStep();
      if (this.getAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         --this.inLove;
         if (this.inLove % 10 == 0) {
            double var1 = this.random.nextGaussian() * 0.02;
            double var3 = this.random.nextGaussian() * 0.02;
            double var5 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), var1, var3, var5);
         }
      }

   }

   protected void actuallyHurt(ServerLevel var1, DamageSource var2, float var3) {
      this.resetLove();
      super.actuallyHurt(var1, var2, var3);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return var2.getBlockState(var1.below()).is(Blocks.GRASS_BLOCK) ? 10.0F : var2.getPathfindingCostFromLightLevels(var1);
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("InLove", this.inLove);
      EntityReference.store(this.loveCause, var1, "LoveCause");
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.inLove = var1.getIntOr("InLove", 0);
      this.loveCause = EntityReference.<ServerPlayer>read(var1, "LoveCause");
   }

   public static boolean checkAnimalSpawnRules(EntityType<? extends Animal> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      boolean var5 = EntitySpawnReason.ignoresLightRequirements(var2) || isBrightEnoughToSpawn(var1, var3);
      return var1.getBlockState(var3.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && var5;
   }

   protected static boolean isBrightEnoughToSpawn(BlockAndTintGetter var0, BlockPos var1) {
      return var0.getRawBrightness(var1, 0) > 8;
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public boolean removeWhenFarAway(double var1) {
      return false;
   }

   protected int getBaseExperienceReward(ServerLevel var1) {
      return 1 + this.random.nextInt(3);
   }

   public abstract boolean isFood(ItemStack var1);

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (this.isFood(var3)) {
         int var4 = this.getAge();
         if (var1 instanceof ServerPlayer) {
            ServerPlayer var5 = (ServerPlayer)var1;
            if (var4 == 0 && this.canFallInLove()) {
               this.usePlayerItem(var1, var2, var3);
               this.setInLove(var5);
               this.playEatingSound();
               return InteractionResult.SUCCESS_SERVER;
            }
         }

         if (this.isBaby()) {
            this.usePlayerItem(var1, var2, var3);
            this.ageUp(getSpeedUpSecondsWhenFeeding(-var4), true);
            this.playEatingSound();
            return InteractionResult.SUCCESS;
         }

         if (this.level().isClientSide()) {
            return InteractionResult.CONSUME;
         }
      }

      return super.mobInteract(var1, var2);
   }

   protected void playEatingSound() {
   }

   public boolean canFallInLove() {
      return this.inLove <= 0;
   }

   public void setInLove(@Nullable Player var1) {
      this.inLove = 600;
      if (var1 instanceof ServerPlayer var2) {
         this.loveCause = EntityReference.of(var2);
      }

      this.level().broadcastEntityEvent(this, (byte)18);
   }

   public void setInLoveTime(int var1) {
      this.inLove = var1;
   }

   public int getInLoveTime() {
      return this.inLove;
   }

   public @Nullable ServerPlayer getLoveCause() {
      return (ServerPlayer)EntityReference.get(this.loveCause, this.level(), ServerPlayer.class);
   }

   public boolean isInLove() {
      return this.inLove > 0;
   }

   public void resetLove() {
      this.inLove = 0;
   }

   public boolean canMate(Animal var1) {
      if (var1 == this) {
         return false;
      } else if (var1.getClass() != this.getClass()) {
         return false;
      } else {
         return this.isInLove() && var1.isInLove();
      }
   }

   public void spawnChildFromBreeding(ServerLevel var1, Animal var2) {
      AgeableMob var3 = this.getBreedOffspring(var1, var2);
      if (var3 != null) {
         var3.setBaby(true);
         var3.snapTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
         this.finalizeSpawnChildFromBreeding(var1, var2, var3);
         var1.addFreshEntityWithPassengers(var3);
      }
   }

   public void finalizeSpawnChildFromBreeding(ServerLevel var1, Animal var2, @Nullable AgeableMob var3) {
      Optional.ofNullable(this.getLoveCause()).or(() -> Optional.ofNullable(var2.getLoveCause())).ifPresent((var3x) -> {
         var3x.awardStat(Stats.ANIMALS_BRED);
         CriteriaTriggers.BRED_ANIMALS.trigger(var3x, this, var2, var3);
      });
      this.setAge(6000);
      var2.setAge(6000);
      this.resetLove();
      var2.resetLove();
      var1.broadcastEntityEvent(this, (byte)18);
      if ((Boolean)var1.getGameRules().get(GameRules.MOB_DROPS)) {
         var1.addFreshEntity(new ExperienceOrb(var1, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
      }

   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 18) {
         for(int var2 = 0; var2 < 7; ++var2) {
            double var3 = this.random.nextGaussian() * 0.02;
            double var5 = this.random.nextGaussian() * 0.02;
            double var7 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), var3, var5, var7);
         }
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public Vec3 getDismountLocationForPassenger(LivingEntity var1) {
      Direction var2 = this.getMotionDirection();
      if (var2.getAxis() == Direction.Axis.Y) {
         return super.getDismountLocationForPassenger(var1);
      } else {
         int[][] var3 = DismountHelper.offsetsForDirection(var2);
         BlockPos var4 = this.blockPosition();
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
         UnmodifiableIterator var6 = var1.getDismountPoses().iterator();

         while(var6.hasNext()) {
            Pose var7 = (Pose)var6.next();
            AABB var8 = var1.getLocalBoundsForPose(var7);

            for(int[] var12 : var3) {
               var5.set(var4.getX() + var12[0], var4.getY(), var4.getZ() + var12[1]);
               double var13 = this.level().getBlockFloorHeight(var5);
               if (DismountHelper.isBlockFloorValid(var13)) {
                  Vec3 var15 = Vec3.upFromBottomCenterOf(var5, var13);
                  if (DismountHelper.canDismountTo(this.level(), var1, var8.move(var15))) {
                     var1.setPose(var7);
                     return var15;
                  }
               }
            }
         }

         return super.getDismountLocationForPassenger(var1);
      }
   }
}
