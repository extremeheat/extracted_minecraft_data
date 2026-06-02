package net.minecraft.world.effect;

import java.util.function.ToIntFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

class InfestedMobEffect extends MobEffect {
   private final float chanceToSpawn;
   private final ToIntFunction<RandomSource> spawnedCount;

   protected InfestedMobEffect(final MobEffectCategory category, final int color, final float chanceToSpawn, final ToIntFunction<RandomSource> spawnedCount) {
      super(category, color, ParticleTypes.INFESTED);
      this.chanceToSpawn = chanceToSpawn;
      this.spawnedCount = spawnedCount;
   }

   public void onMobHurt(final ServerLevel level, final LivingEntity mob, final int amplifier, final DamageSource source, final float damage) {
      if (mob.getRandom().nextFloat() <= this.chanceToSpawn) {
         int count = this.spawnedCount.applyAsInt(mob.getRandom());

         for(int i = 0; i < count; ++i) {
            this.spawnSilverfish(level, mob, mob.getX(), mob.getY() + (double)mob.getBbHeight() / 2.0, mob.getZ());
         }
      }

   }

   private void spawnSilverfish(final ServerLevel level, final LivingEntity mob, final double x, final double y, final double z) {
      Silverfish silverfish = (Silverfish)EntityTypes.SILVERFISH.create(level, (EntitySpawnReason)EntitySpawnReason.TRIGGERED);
      if (silverfish != null) {
         RandomSource random = mob.getRandom();
         float angle = 1.5707964F;
         float randomAngle = Mth.randomBetween(random, -1.5707964F, 1.5707964F);
         Vector3f viewDirection = mob.getLookAngle().toVector3f().mul(0.3F).mul(1.0F, 1.5F, 1.0F).rotateY(randomAngle);
         silverfish.snapTo(x, y, z, level.getRandom().nextFloat() * 360.0F, 0.0F);
         silverfish.setDeltaMovement(new Vec3(viewDirection));
         level.addFreshEntity(silverfish);
         silverfish.playSound(SoundEvents.SILVERFISH_HURT);
      }
   }
}
