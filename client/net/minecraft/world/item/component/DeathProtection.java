package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

public record DeathProtection(List<ConsumeEffect> deathEffects) {
   public static final Codec<DeathProtection> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ConsumeEffect.CODEC.listOf().optionalFieldOf("death_effects", List.of()).forGetter(DeathProtection::deathEffects)).apply(var0, DeathProtection::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, DeathProtection> STREAM_CODEC;
   public static final DeathProtection TOTEM_OF_UNDYING;

   public DeathProtection(List<ConsumeEffect> var1) {
      super();
      this.deathEffects = var1;
   }

   public void applyEffects(ItemStack var1, LivingEntity var2) {
      for(ConsumeEffect var4 : this.deathEffects) {
         var4.apply(var2.level(), var1, var2);
      }

   }

   static {
      STREAM_CODEC = StreamCodec.composite(ConsumeEffect.STREAM_CODEC.apply(ByteBufCodecs.list()), DeathProtection::deathEffects, DeathProtection::new);
      TOTEM_OF_UNDYING = new DeathProtection(List.of(new ClearAllStatusEffectsConsumeEffect(), new ApplyStatusEffectsConsumeEffect(List.of(new MobEffectInstance(MobEffects.REGENERATION, 900, 1), new MobEffectInstance(MobEffects.ABSORPTION, 100, 1), new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0)))));
   }
}
