package net.minecraft.world.entity.monster;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;

public abstract class Monster extends PathfinderMob implements Enemy {
   protected Monster(EntityType<? extends Monster> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 5;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   public void aiStep() {
      this.updateSwingTime();
      this.updateNoActionTime();
      super.aiStep();
   }

   protected void updateNoActionTime() {
      float var1 = this.getLightLevelDependentMagicValue();
      if (var1 > 0.5F) {
         this.noActionTime += 2;
      }

   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.HOSTILE_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.HOSTILE_SPLASH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.HOSTILE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HOSTILE_DEATH;
   }

   public LivingEntity.Fallsounds getFallSounds() {
      return new LivingEntity.Fallsounds(SoundEvents.HOSTILE_SMALL_FALL, SoundEvents.HOSTILE_BIG_FALL);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return -var2.getPathfindingCostFromLightLevels(var1);
   }

   public static boolean isDarkEnoughToSpawn(ServerLevelAccessor var0, BlockPos var1, RandomSource var2) {
      if (var0.getBrightness(LightLayer.SKY, var1) > var2.nextInt(32)) {
         return false;
      } else {
         DimensionType var3 = var0.dimensionType();
         int var4 = var3.monsterSpawnBlockLightLimit();
         if (var4 < 15 && var0.getBrightness(LightLayer.BLOCK, var1) > var4) {
            return false;
         } else {
            int var5 = var0.getLevel().isThundering() ? var0.getMaxLocalRawBrightness(var1, 10) : var0.getMaxLocalRawBrightness(var1);
            return var5 <= var3.monsterSpawnLightTest().sample(var2);
         }
      }
   }

   public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> var0, ServerLevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getDifficulty() != Difficulty.PEACEFUL && (MobSpawnType.ignoresLightRequirements(var2) || isDarkEnoughToSpawn(var1, var3, var4)) && checkMobSpawnRules(var0, var1, var2, var3, var4);
   }

   public static boolean checkAnyLightMonsterSpawnRules(EntityType<? extends Monster> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(var0, var1, var2, var3, var4);
   }

   public static AttributeSupplier.Builder createMonsterAttributes() {
      return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE);
   }

   public boolean shouldDropExperience() {
      return true;
   }

   protected boolean shouldDropLoot() {
      return true;
   }

   public boolean isPreventingPlayerRest(Player var1) {
      return true;
   }

   public ItemStack getProjectile(ItemStack var1) {
      if (var1.getItem() instanceof ProjectileWeaponItem) {
         Predicate var2 = ((ProjectileWeaponItem)var1.getItem()).getSupportedHeldProjectiles();
         ItemStack var3 = ProjectileWeaponItem.getHeldProjectile(this, var2);
         return var3.isEmpty() ? new ItemStack(Items.ARROW) : var3;
      } else {
         return ItemStack.EMPTY;
      }
   }
}
