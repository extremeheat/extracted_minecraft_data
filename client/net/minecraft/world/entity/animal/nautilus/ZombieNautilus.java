package net.minecraft.world.entity.animal.nautilus;

import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class ZombieNautilus extends AbstractNautilus {
   public ZombieNautilus(EntityType<? extends ZombieNautilus> var1, Level var2) {
      super(var1, var2);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return AbstractNautilus.createAttributes().add(Attributes.MOVEMENT_SPEED, 1.100000023841858);
   }

   @Nullable
   public ZombieNautilus getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return null;
   }

   protected EquipmentSlot sunProtectionSlot() {
      return EquipmentSlot.BODY;
   }

   protected Brain.Provider<ZombieNautilus> brainProvider() {
      return ZombieNautilusAi.brainProvider();
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return ZombieNautilusAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public Brain<ZombieNautilus> getBrain() {
      return super.getBrain();
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("zombieNautilusBrain");
      this.getBrain().tick(var1, this);
      var2.pop();
      var2.push("zombieNautilusActivityUpdate");
      ZombieNautilusAi.updateActivity(this);
      var2.pop();
      super.customServerAiStep(var1);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_NAUTILUS_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ZOMBIE_NAUTILUS_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_NAUTILUS_DEATH;
   }

   protected SoundEvent getDashSound() {
      return SoundEvents.ZOMBIE_NAUTILUS_DASH;
   }

   protected SoundEvent getDashReadySound() {
      return SoundEvents.ZOMBIE_NAUTILUS_DASH_READY;
   }

   protected void playEatingSound() {
      this.makeSound(SoundEvents.ZOMBIE_NAUTILUS_EAT);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ZOMBIE_NAUTILUS_SWIM;
   }

   // $FF: synthetic method
   @Nullable
   public AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }
}
