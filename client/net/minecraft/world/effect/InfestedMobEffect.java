package net.minecraft.world.effect;

import java.util.function.ToIntFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.level.Level;

class InfestedMobEffect extends MobEffect {
   private final float chanceToSpawn;
   private final ToIntFunction<RandomSource> spawnedCount;

   protected InfestedMobEffect(MobEffectCategory var1, int var2, float var3, ToIntFunction<RandomSource> var4) {
      super(var1, var2, ParticleTypes.INFESTED);
      this.chanceToSpawn = var3;
      this.spawnedCount = var4;
   }

   public void onMobHurt(LivingEntity var1, int var2, DamageSource var3, float var4) {
      if (var1.getRandom().nextFloat() <= this.chanceToSpawn) {
         int var5 = this.spawnedCount.applyAsInt(var1.getRandom());

         for(int var6 = 0; var6 < var5; ++var6) {
            this.spawnSilverfish(var1.level(), var1.getX(), var1.getY() + 0.5, var1.getZ());
         }
      }

   }

   private void spawnSilverfish(Level var1, double var2, double var4, double var6) {
      Silverfish var8 = (Silverfish)EntityType.SILVERFISH.create(var1);
      if (var8 != null) {
         var8.moveTo(var2, var4, var6, var1.getRandom().nextFloat() * 360.0F, 0.0F);
         var1.addFreshEntity(var8);
      }
   }
}
