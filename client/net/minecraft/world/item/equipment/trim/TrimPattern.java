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
import net.minecraft.util.ExtraCodecs;

public record TrimPattern(Identifier assetId, Component description, boolean decal) {
   public static final Codec<TrimPattern> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("asset_id").forGetter(TrimPattern::assetId), ComponentSerialization.CODEC.fieldOf("description").forGetter(TrimPattern::description), ExtraCodecs.optionalAlwaysPresentFieldOf(Codec.BOOL, "decal", false).forGetter(TrimPattern::decal)).apply(i, TrimPattern::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, TrimPattern> DIRECT_STREAM_CODEC;
   public static final Codec<Holder<TrimPattern>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TrimPattern>> STREAM_CODEC;

   public TrimPattern {
      super();
   }

   public Component copyWithStyle(final Holder<TrimMaterial> material) {
      return this.description.copy().withStyle(((TrimMaterial)material.value()).description().getStyle());
   }

   static {
      DIRECT_STREAM_CODEC = StreamCodec.composite(Identifier.STREAM_CODEC, TrimPattern::assetId, ComponentSerialization.STREAM_CODEC, TrimPattern::description, ByteBufCodecs.BOOL, TrimPattern::decal, TrimPattern::new);
      CODEC = RegistryCodecs.holder(Registries.TRIM_PATTERN, DIRECT_CODEC);
      STREAM_CODEC = ByteBufCodecs.holder(Registries.TRIM_PATTERN, DIRECT_STREAM_CODEC);
   }
}
