package net.minecraft.world.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

public record TrimPattern(ResourceLocation assetId, Component description, boolean decal) {
   public static final Codec<TrimPattern> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("asset_id").forGetter(TrimPattern::assetId), ComponentSerialization.CODEC.fieldOf("description").forGetter(TrimPattern::description), Codec.BOOL.fieldOf("decal").orElse(false).forGetter(TrimPattern::decal)).apply(var0, TrimPattern::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, TrimPattern> DIRECT_STREAM_CODEC;
   public static final Codec<Holder<TrimPattern>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<TrimPattern>> STREAM_CODEC;

   public TrimPattern(ResourceLocation var1, Component var2, boolean var3) {
      super();
      this.assetId = var1;
      this.description = var2;
      this.decal = var3;
   }

   public Component copyWithStyle(Holder<TrimMaterial> var1) {
      return this.description.copy().withStyle(((TrimMaterial)var1.value()).description().getStyle());
   }

   static {
      DIRECT_STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, TrimPattern::assetId, ComponentSerialization.STREAM_CODEC, TrimPattern::description, ByteBufCodecs.BOOL, TrimPattern::decal, TrimPattern::new);
      CODEC = RegistryFileCodec.<Holder<TrimPattern>>create(Registries.TRIM_PATTERN, DIRECT_CODEC);
      STREAM_CODEC = ByteBufCodecs.holder(Registries.TRIM_PATTERN, DIRECT_STREAM_CODEC);
   }
}
