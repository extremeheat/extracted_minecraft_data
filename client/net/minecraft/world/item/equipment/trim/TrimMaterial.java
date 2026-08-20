package net.minecraft.world.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record TrimMaterial(Identifier paletteId, Component description) {
   public static final Codec<TrimMaterial> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("palette_id").forGetter(TrimMaterial::paletteId), ComponentSerialization.CODEC.fieldOf("description").forGetter(TrimMaterial::description)).apply(i, TrimMaterial::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, TrimMaterial> DIRECT_STREAM_CODEC;
   public static final Codec<Holder<TrimMaterial>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TrimMaterial>> STREAM_CODEC;

   public TrimMaterial {
      super();
   }

   static {
      DIRECT_STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, TrimMaterial::paletteId, ComponentSerialization.STREAM_CODEC, TrimMaterial::description, TrimMaterial::new);
      CODEC = RegistryCodecs.holder(Registries.TRIM_MATERIAL, DIRECT_CODEC);
      STREAM_CODEC = ByteBufCodecs.holder(Registries.TRIM_MATERIAL, DIRECT_STREAM_CODEC);
   }
}
