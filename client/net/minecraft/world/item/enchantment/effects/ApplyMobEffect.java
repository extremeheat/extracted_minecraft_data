package net.minecraft.world.item.enchantment.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;

public record ApplyMobEffect(
   HolderSet<MobEffect> toApply, LevelBasedValue minDuration, LevelBasedValue maxDuration, LevelBasedValue minAmplifier, LevelBasedValue maxAmplifier
) implements EnchantmentEntityEffect {
   public static final MapCodec<ApplyMobEffect> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               RegistryCodecs.homogeneousList(Registries.MOB_EFFECT).fieldOf("to_apply").forGetter(ApplyMobEffect::toApply),
               LevelBasedValue.CODEC.fieldOf("min_duration").forGetter(ApplyMobEffect::minDuration),
               LevelBasedValue.CODEC.fieldOf("max_duration").forGetter(ApplyMobEffect::maxDuration),
               LevelBasedValue.CODEC.fieldOf("min_amplifier").forGetter(ApplyMobEffect::minAmplifier),
               LevelBasedValue.CODEC.fieldOf("max_amplifier").forGetter(ApplyMobEffect::maxAmplifier)
            )
            .apply(var0, ApplyMobEffect::new)
   );

   public ApplyMobEffect(
      HolderSet<MobEffect> toApply, LevelBasedValue minDuration, LevelBasedValue maxDuration, LevelBasedValue minAmplifier, LevelBasedValue maxAmplifier
   ) {
      super();
      this.toApply = toApply;
      this.minDuration = minDuration;
      this.maxDuration = maxDuration;
      this.minAmplifier = minAmplifier;
      this.maxAmplifier = maxAmplifier;
   }

   @Override
   public void apply(ServerLevel var1, int var2, EnchantedItemInUse var3, Entity var4, Vec3 var5) {
      if (var4 instanceof LivingEntity var6) {
         RandomSource var7 = var6.getRandom();
         Optional var8 = this.toApply.getRandomElement(var7);
         if (var8.isPresent()) {
            int var9 = Math.round(Mth.randomBetween(var7, this.minDuration.calculate(var2), this.maxDuration.calculate(var2)) * 20.0F);
            int var10 = Math.min(0, Math.round(Mth.randomBetween(var7, this.minAmplifier.calculate(var2), this.maxAmplifier.calculate(var2))));
            var6.addEffect(new MobEffectInstance((Holder<MobEffect>)var8.get(), var9, var10));
         }
      }
   }

   @Override
   public MapCodec<ApplyMobEffect> codec() {
      return CODEC;
   }
}
