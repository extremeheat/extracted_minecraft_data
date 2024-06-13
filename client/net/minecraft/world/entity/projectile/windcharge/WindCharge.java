package net.minecraft.world.entity.projectile.windcharge;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.Vec3;

public class WindCharge extends AbstractWindCharge {
   private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new SimpleExplosionDamageCalculator(
      true, false, Optional.of(1.1F), BuiltInRegistries.BLOCK.getTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
   );
   private static final float RADIUS = 1.2F;

   public WindCharge(EntityType<? extends AbstractWindCharge> var1, Level var2) {
      super(var1, var2);
   }

   public WindCharge(Player var1, Level var2, double var3, double var5, double var7) {
      super(EntityType.WIND_CHARGE, var2, var1, var3, var5, var7);
   }

   public WindCharge(Level var1, double var2, double var4, double var6, Vec3 var8) {
      super(EntityType.WIND_CHARGE, var2, var4, var6, var8, var1);
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
            Level.ExplosionInteraction.TRIGGER,
            ParticleTypes.GUST_EMITTER_SMALL,
            ParticleTypes.GUST_EMITTER_LARGE,
            SoundEvents.WIND_CHARGE_BURST
         );
   }
}
