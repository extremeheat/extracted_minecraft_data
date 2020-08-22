package net.minecraft.world.entity.monster;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class Husk extends Zombie {
   public Husk(EntityType var1, Level var2) {
      super(var1, var2);
   }

   public static boolean checkHuskSpawnRules(EntityType var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      return checkMonsterSpawnRules(var0, var1, var2, var3, var4) && (var2 == MobSpawnType.SPAWNER || var1.canSeeSky(var3));
   }

   protected boolean isSunSensitive() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.HUSK_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.HUSK_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HUSK_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.HUSK_STEP;
   }

   public boolean doHurtTarget(Entity var1) {
      boolean var2 = super.doHurtTarget(var1);
      if (var2 && this.getMainHandItem().isEmpty() && var1 instanceof LivingEntity) {
         float var3 = this.level.getCurrentDifficultyAt(new BlockPos(this)).getEffectiveDifficulty();
         ((LivingEntity)var1).addEffect(new MobEffectInstance(MobEffects.HUNGER, 140 * (int)var3));
      }

      return var2;
   }

   protected boolean convertsInWater() {
      return true;
   }

   protected void doUnderWaterConversion() {
      this.convertTo(EntityType.ZOMBIE);
      this.level.levelEvent((Player)null, 1041, new BlockPos(this), 0);
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }
}
