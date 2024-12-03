package net.minecraft.world.item.consume_effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record RemoveStatusEffectsConsumeEffect(HolderSet<MobEffect> effects) implements ConsumeEffect {
   public static final MapCodec<RemoveStatusEffectsConsumeEffect> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(RegistryCodecs.homogeneousList(Registries.MOB_EFFECT).fieldOf("effects").forGetter(RemoveStatusEffectsConsumeEffect::effects)).apply(var0, RemoveStatusEffectsConsumeEffect::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, RemoveStatusEffectsConsumeEffect> STREAM_CODEC;

   public RemoveStatusEffectsConsumeEffect(Holder<MobEffect> var1) {
      this(HolderSet.direct(var1));
   }

   public RemoveStatusEffectsConsumeEffect(HolderSet<MobEffect> var1) {
      super();
      this.effects = var1;
   }

   public ConsumeEffect.Type<RemoveStatusEffectsConsumeEffect> getType() {
      return ConsumeEffect.Type.REMOVE_EFFECTS;
   }

   public boolean apply(Level var1, ItemStack var2, LivingEntity var3) {
      boolean var4 = false;

      for(Holder var6 : this.effects) {
         if (var3.removeEffect(var6)) {
            var4 = true;
         }
      }

      return var4;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderSet(Registries.MOB_EFFECT), RemoveStatusEffectsConsumeEffect::effects, RemoveStatusEffectsConsumeEffect::new);
   }
}
