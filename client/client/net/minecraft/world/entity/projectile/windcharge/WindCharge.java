package net.minecraft.world.entity.projectile.windcharge;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class WindCharge extends AbstractWindCharge {
   private static final WindCharge.WindChargePlayerDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new WindCharge.WindChargePlayerDamageCalculator();
   private static final float RADIUS = 1.2F;

   public WindCharge(EntityType<? extends AbstractWindCharge> var1, Level var2) {
      super(var1, var2);
   }

   public WindCharge(Player var1, Level var2, double var3, double var5, double var7) {
      super(EntityType.WIND_CHARGE, var2, var1, var3, var5, var7);
   }

   public WindCharge(Level var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(EntityType.WIND_CHARGE, var2, var4, var6, var8, var10, var12, var1);
   }

   @Override
   protected void explode() {
      this.level()
         .explode(
            this,
            null,
            EXPLOSION_DAMAGE_CALCULATOR,
            this.getX(),
            this.getY(),
            this.getZ(),
            1.2F,
            false,
            Level.ExplosionInteraction.BLOW,
            ParticleTypes.GUST_EMITTER_SMALL,
            ParticleTypes.GUST_EMITTER_LARGE,
            SoundEvents.WIND_CHARGE_BURST
         );
   }

   public static final class WindChargePlayerDamageCalculator extends AbstractWindCharge.WindChargeDamageCalculator {
      public WindChargePlayerDamageCalculator() {
         super();
      }

      @Override
      public float getKnockbackMultiplier() {
         return 1.1F;
      }
   }
}
