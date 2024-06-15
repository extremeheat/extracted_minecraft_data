package net.minecraft.world.effect;

import java.util.function.ToIntFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

class InfestedMobEffect extends MobEffect {
   private final float chanceToSpawn;
   private final ToIntFunction<RandomSource> spawnedCount;

   protected InfestedMobEffect(MobEffectCategory var1, int var2, float var3, ToIntFunction<RandomSource> var4) {
      super(var1, var2, ParticleTypes.INFESTED);
      this.chanceToSpawn = var3;
      this.spawnedCount = var4;
   }

   @Override
   public void onMobHurt(LivingEntity var1, int var2, DamageSource var3, float var4) {
      if (var1.getRandom().nextFloat() <= this.chanceToSpawn) {
         int var5 = this.spawnedCount.applyAsInt(var1.getRandom());

         for (int var6 = 0; var6 < var5; var6++) {
            this.spawnSilverfish(var1.level(), var1, var1.getX(), var1.getY() + (double)var1.getBbHeight() / 2.0, var1.getZ());
         }
      }
   }

   private void spawnSilverfish(Level var1, LivingEntity var2, double var3, double var5, double var7) {
      Silverfish var9 = EntityType.SILVERFISH.create(var1);
      if (var9 != null) {
         RandomSource var10 = var2.getRandom();
         float var11 = 1.5707964F;
         float var12 = Mth.randomBetween(var10, -1.5707964F, 1.5707964F);
         Vector3f var13 = var2.getLookAngle().toVector3f().mul(0.3F).mul(1.0F, 1.5F, 1.0F).rotateY(var12);
         var9.moveTo(var3, var5, var7, var1.getRandom().nextFloat() * 360.0F, 0.0F);
         var9.setDeltaMovement(new Vec3(var13));
         var1.addFreshEntity(var9);
         var9.playSound(SoundEvents.SILVERFISH_HURT);
      }
   }
}
