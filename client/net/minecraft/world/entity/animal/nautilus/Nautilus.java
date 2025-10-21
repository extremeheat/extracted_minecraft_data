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
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.level.Level;

public class Nautilus extends AbstractNautilus {
   private static final int NAUTILUS_TOTAL_AIR_SUPPLY = 300;

   public Nautilus(EntityType<? extends Nautilus> var1, Level var2) {
      super(var1, var2);
   }

   protected Brain.Provider<Nautilus> brainProvider() {
      return NautilusAi.brainProvider();
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      return NautilusAi.makeBrain(this.brainProvider().makeBrain(var1));
   }

   public Brain<Nautilus> getBrain() {
      return super.getBrain();
   }

   @Nullable
   public Nautilus getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Nautilus var3 = EntityType.NAUTILUS.create(var1, EntitySpawnReason.BREEDING);
      if (var3 != null && this.isTame()) {
         var3.setOwnerReference(this.getOwnerReference());
         var3.setTame(true, true);
      }

      return var3;
   }

   protected void customServerAiStep(ServerLevel var1) {
      ProfilerFiller var2 = Profiler.get();
      var2.push("nautilusBrain");
      this.getBrain().tick(var1, this);
      var2.pop();
      var2.push("nautilusActivityUpdate");
      NautilusAi.updateActivity(this);
      var2.pop();
      super.customServerAiStep(var1);
   }

   protected SoundEvent getAmbientSound() {
      return this.isBaby() ? SoundEvents.BABY_NAUTILUS_AMBIENT : SoundEvents.NAUTILUS_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return this.isBaby() ? SoundEvents.BABY_NAUTILUS_HURT : SoundEvents.NAUTILUS_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isBaby() ? SoundEvents.BABY_NAUTILUS_DEATH : SoundEvents.NAUTILUS_DEATH;
   }

   protected SoundEvent getDashSound() {
      return SoundEvents.NAUTILUS_DASH;
   }

   protected SoundEvent getDashReadySound() {
      return SoundEvents.NAUTILUS_DASH_READY;
   }

   protected void playEatingSound() {
      SoundEvent var1 = this.isBaby() ? SoundEvents.BABY_NAUTILUS_EAT : SoundEvents.NAUTILUS_EAT;
      this.makeSound(var1);
   }

   protected SoundEvent getSwimSound() {
      return this.isBaby() ? SoundEvents.BABY_NAUTILUS_SWIM : SoundEvents.NAUTILUS_SWIM;
   }

   public int getMaxAirSupply() {
      return 300;
   }

   protected void handleAirSupply(ServerLevel var1, int var2) {
      if (this.isAlive() && !this.isInWater()) {
         this.setAirSupply(var2 - 1);
         if (this.getAirSupply() <= -20) {
            this.setAirSupply(0);
            this.hurtServer(var1, this.damageSources().dryOut(), 2.0F);
         }
      } else {
         this.setAirSupply(300);
      }

   }

   public void baseTick() {
      int var1 = this.getAirSupply();
      super.baseTick();
      if (!this.isNoAi()) {
         Level var3 = this.level();
         if (var3 instanceof ServerLevel) {
            ServerLevel var2 = (ServerLevel)var3;
            this.handleAirSupply(var2, var1);
         }
      }

   }

   // $FF: synthetic method
   @Nullable
   public AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }
}
