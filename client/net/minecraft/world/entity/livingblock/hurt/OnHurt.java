package net.minecraft.world.entity.livingblock.hurt;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.livingblock.LivingBlock;

@FunctionalInterface
public interface OnHurt {
   OnHurt DO_NOTHING = (var0, var1, var2, var3, fatalDamage) -> {
   };

   void apply(final LivingBlock livingBlock, final ServerLevel level, final DamageSource source, float damage, final boolean fatalDamage);
}
