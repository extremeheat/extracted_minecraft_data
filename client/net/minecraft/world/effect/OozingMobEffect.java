package net.minecraft.world.effect;

import java.util.function.ToIntFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;

class OozingMobEffect extends MobEffect {
   private final ToIntFunction<RandomSource> spawnedCount;

   protected OozingMobEffect(MobEffectCategory var1, int var2, ToIntFunction<RandomSource> var3) {
      super(var1, var2, ParticleTypes.ITEM_SLIME);
      this.spawnedCount = var3;
   }

   @Override
   public void onMobRemoved(LivingEntity var1, int var2, Entity.RemovalReason var3) {
      if (var3 == Entity.RemovalReason.KILLED) {
         int var4 = this.spawnedCount.applyAsInt(var1.getRandom());

         for(int var5 = 0; var5 < var4; ++var5) {
            this.spawnSlimeOffspring(var1.level(), var1.getX(), var1.getY() + 0.5, var1.getZ());
         }
      }
   }

   private void spawnSlimeOffspring(Level var1, double var2, double var4, double var6) {
      Slime var8 = EntityType.SLIME.create(var1);
      if (var8 != null) {
         var8.setSize(2, true);
         var8.moveTo(var2, var4, var6, var1.getRandom().nextFloat() * 360.0F, 0.0F);
         var1.addFreshEntity(var8);
      }
   }
}
