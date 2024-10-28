package net.minecraft.world.entity.projectile.windcharge;

import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.Vec3;

public class WindCharge extends AbstractWindCharge {
   private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR;
   private static final float RADIUS = 1.2F;
   private int noDeflectTicks = 5;

   public WindCharge(EntityType<? extends AbstractWindCharge> var1, Level var2) {
      super(var1, var2);
   }

   public WindCharge(Player var1, Level var2, double var3, double var5, double var7) {
      super(EntityType.WIND_CHARGE, var2, var1, var3, var5, var7);
   }

   public WindCharge(Level var1, double var2, double var4, double var6, Vec3 var8) {
      super(EntityType.WIND_CHARGE, var2, var4, var6, var8, var1);
   }

   public void tick() {
      super.tick();
      if (this.noDeflectTicks > 0) {
         --this.noDeflectTicks;
      }

   }

   public boolean deflect(ProjectileDeflection var1, @Nullable Entity var2, @Nullable Entity var3, boolean var4) {
      return this.noDeflectTicks > 0 ? false : super.deflect(var1, var2, var3, var4);
   }

   protected void explode(Vec3 var1) {
      this.level().explode(this, (DamageSource)null, EXPLOSION_DAMAGE_CALCULATOR, var1.x(), var1.y(), var1.z(), 1.2F, false, Level.ExplosionInteraction.TRIGGER, ParticleTypes.GUST_EMITTER_SMALL, ParticleTypes.GUST_EMITTER_LARGE, SoundEvents.WIND_CHARGE_BURST);
   }

   static {
      EXPLOSION_DAMAGE_CALCULATOR = new SimpleExplosionDamageCalculator(true, false, Optional.of(1.22F), BuiltInRegistries.BLOCK.getTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity()));
   }
}
