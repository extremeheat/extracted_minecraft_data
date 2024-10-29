package net.minecraft.world.effect;

import java.util.function.ToIntFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
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

   public void onMobHurt(ServerLevel var1, LivingEntity var2, int var3, DamageSource var4, float var5) {
      if (var2.getRandom().nextFloat() <= this.chanceToSpawn) {
         int var6 = this.spawnedCount.applyAsInt(var2.getRandom());

         for(int var7 = 0; var7 < var6; ++var7) {
            this.spawnSilverfish(var1, var2, var2.getX(), var2.getY() + (double)var2.getBbHeight() / 2.0, var2.getZ());
         }
      }

   }

   private void spawnSilverfish(ServerLevel var1, LivingEntity var2, double var3, double var5, double var7) {
      Silverfish var9 = (Silverfish)EntityType.SILVERFISH.create(var1, EntitySpawnReason.TRIGGERED);
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
