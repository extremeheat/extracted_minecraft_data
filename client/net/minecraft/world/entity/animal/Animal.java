package net.minecraft.world.entity.animal;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;

public abstract class Animal extends AgeableMob {
   protected static final int PARENT_AGE_AFTER_BREEDING = 6000;
   private int inLove;
   @Nullable
   private UUID loveCause;

   protected Animal(EntityType<? extends Animal> var1, Level var2) {
      super(var1, var2);
      this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
   }

   public static AttributeSupplier.Builder createAnimalAttributes() {
      return Mob.createMobAttributes().add(Attributes.TEMPT_RANGE, 10.0);
   }

   @Override
   protected void customServerAiStep() {
      if (this.getAge() != 0) {
         this.inLove = 0;
      }

      super.customServerAiStep();
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (this.getAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         this.inLove--;
         if (this.inLove % 10 == 0) {
            double var1 = this.random.nextGaussian() * 0.02;
            double var3 = this.random.nextGaussian() * 0.02;
            double var5 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), var1, var3, var5);
         }
      }
   }

   @Override
   protected void actuallyHurt(DamageSource var1, float var2) {
      this.resetLove();
      super.actuallyHurt(var1, var2);
   }

   @Override
   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return var2.getBlockState(var1.below()).is(Blocks.GRASS_BLOCK) ? 10.0F : var2.getPathfindingCostFromLightLevels(var1);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("InLove", this.inLove);
      if (this.loveCause != null) {
         var1.putUUID("LoveCause", this.loveCause);
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.inLove = var1.getInt("InLove");
      this.loveCause = var1.hasUUID("LoveCause") ? var1.getUUID("LoveCause") : null;
   }

   public static boolean checkAnimalSpawnRules(EntityType<? extends Animal> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      boolean var5 = EntitySpawnReason.ignoresLightRequirements(var2) || isBrightEnoughToSpawn(var1, var3);
      return var1.getBlockState(var3.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && var5;
   }

   protected static boolean isBrightEnoughToSpawn(BlockAndTintGetter var0, BlockPos var1) {
      return var0.getRawBrightness(var1, 0) > 8;
   }

   @Override
   public int getAmbientSoundInterval() {
      return 120;
   }

   @Override
   public boolean removeWhenFarAway(double var1) {
      return false;
   }

   @Override
   protected int getBaseExperienceReward() {
      return 1 + this.level().random.nextInt(3);
   }

   public abstract boolean isFood(ItemStack var1);

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (this.isFood(var3)) {
         int var4 = this.getAge();
         if (!this.level().isClientSide && var4 == 0 && this.canFallInLove()) {
            this.usePlayerItem(var1, var2, var3);
            this.setInLove(var1);
            this.playEatingSound();
            return InteractionResult.SUCCESS_SERVER;
         }

         if (this.isBaby()) {
            this.usePlayerItem(var1, var2, var3);
            this.ageUp(getSpeedUpSecondsWhenFeeding(-var4), true);
            this.playEatingSound();
            return InteractionResult.SUCCESS;
         }
      }

      return super.mobInteract(var1, var2);
   }

   protected void playEatingSound() {
   }

   protected void usePlayerItem(Player var1, InteractionHand var2, ItemStack var3) {
      var3.consume(1, var1);
   }

   public boolean canFallInLove() {
      return this.inLove <= 0;
   }

   public void setInLove(@Nullable Player var1) {
      this.inLove = 600;
      if (var1 != null) {
         this.loveCause = var1.getUUID();
      }

      this.level().broadcastEntityEvent(this, (byte)18);
   }

   public void setInLoveTime(int var1) {
      this.inLove = var1;
   }

   public int getInLoveTime() {
      return this.inLove;
   }

   @Nullable
   public ServerPlayer getLoveCause() {
      if (this.loveCause == null) {
         return null;
      } else {
         Player var1 = this.level().getPlayerByUUID(this.loveCause);
         return var1 instanceof ServerPlayer ? (ServerPlayer)var1 : null;
      }
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
      } else {
         return var1.getClass() != this.getClass() ? false : this.isInLove() && var1.isInLove();
      }
   }

   public void spawnChildFromBreeding(ServerLevel var1, Animal var2) {
      AgeableMob var3 = this.getBreedOffspring(var1, var2);
      if (var3 != null) {
         var3.setBaby(true);
         var3.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
         this.finalizeSpawnChildFromBreeding(var1, var2, var3);
         var1.addFreshEntityWithPassengers(var3);
      }
   }

   public void finalizeSpawnChildFromBreeding(ServerLevel var1, Animal var2, @Nullable AgeableMob var3) {
      Optional.ofNullable(this.getLoveCause()).or(() -> Optional.ofNullable(var2.getLoveCause())).ifPresent(var3x -> {
         var3x.awardStat(Stats.ANIMALS_BRED);
         CriteriaTriggers.BRED_ANIMALS.trigger(var3x, this, var2, var3);
      });
      this.setAge(6000);
      var2.setAge(6000);
      this.resetLove();
      var2.resetLove();
      var1.broadcastEntityEvent(this, (byte)18);
      if (var1.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
         var1.addFreshEntity(new ExperienceOrb(var1, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
      }
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 18) {
         for (int var2 = 0; var2 < 7; var2++) {
            double var3 = this.random.nextGaussian() * 0.02;
            double var5 = this.random.nextGaussian() * 0.02;
            double var7 = this.random.nextGaussian() * 0.02;
            this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), var3, var5, var7);
         }
      } else {
         super.handleEntityEvent(var1);
      }
   }
}
