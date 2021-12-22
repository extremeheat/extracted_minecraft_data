package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SpectralArrow extends AbstractArrow {
   private int duration = 200;

   public SpectralArrow(EntityType<? extends SpectralArrow> var1, Level var2) {
      super(var1, var2);
   }

   public SpectralArrow(Level var1, LivingEntity var2) {
      super(EntityType.SPECTRAL_ARROW, var2, var1);
   }

   public SpectralArrow(Level var1, double var2, double var4, double var6) {
      super(EntityType.SPECTRAL_ARROW, var2, var4, var6, var1);
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide && !this.inGround) {
         this.level.addParticle(ParticleTypes.INSTANT_EFFECT, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected ItemStack getPickupItem() {
      return new ItemStack(Items.SPECTRAL_ARROW);
   }

   protected void doPostHurtEffects(LivingEntity var1) {
      super.doPostHurtEffects(var1);
      MobEffectInstance var2 = new MobEffectInstance(MobEffects.GLOWING, this.duration, 0);
      var1.addEffect(var2, this.getEffectSource());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Duration")) {
         this.duration = var1.getInt("Duration");
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Duration", this.duration);
   }
}
