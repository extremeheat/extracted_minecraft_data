package net.minecraft.world.effect;

import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.function.ToIntFunction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

class OozingMobEffect extends MobEffect {
   private static final int RADIUS_TO_CHECK_SLIMES = 2;
   public static final int SLIME_SIZE = 2;
   private final ToIntFunction<RandomSource> spawnedCount;

   protected OozingMobEffect(MobEffectCategory var1, int var2, ToIntFunction<RandomSource> var3) {
      super(var1, var2, ParticleTypes.ITEM_SLIME);
      this.spawnedCount = var3;
   }

   @VisibleForTesting
   protected static int numberOfSlimesToSpawn(int var0, int var1, int var2) {
      return Mth.clamp(0, var0 - var1, var2);
   }

   @Override
   public void onMobRemoved(LivingEntity var1, int var2, Entity.RemovalReason var3) {
      if (var3 == Entity.RemovalReason.KILLED) {
         int var4 = this.spawnedCount.applyAsInt(var1.getRandom());
         Level var5 = var1.level();
         int var6 = var5.getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
         ArrayList var7 = new ArrayList();
         var5.getEntities(EntityType.SLIME, var1.getBoundingBox().inflate(2.0), var1x -> var1x != var1, var7, var6);
         int var8 = numberOfSlimesToSpawn(var6, var7.size(), var4);

         for (int var9 = 0; var9 < var8; var9++) {
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
