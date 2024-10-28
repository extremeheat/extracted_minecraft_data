package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

public record BannerPattern(ResourceLocation assetId, String translationKey) {
   public static final Codec<BannerPattern> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ResourceLocation.CODEC.fieldOf("asset_id").forGetter(BannerPattern::assetId), Codec.STRING.fieldOf("translation_key").forGetter(BannerPattern::translationKey)).apply(var0, BannerPattern::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, BannerPattern> DIRECT_STREAM_CODEC;
   public static final Codec<Holder<BannerPattern>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<BannerPattern>> STREAM_CODEC;

   public BannerPattern(ResourceLocation assetId, String translationKey) {
      super();
      this.assetId = assetId;
      this.translationKey = translationKey;
   }

   public ResourceLocation assetId() {
      return this.assetId;
   }

   public String translationKey() {
      return this.translationKey;
   }

   static {
      DIRECT_STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, BannerPattern::assetId, ByteBufCodecs.STRING_UTF8, BannerPattern::translationKey, BannerPattern::new);
      CODEC = RegistryFileCodec.create(Registries.BANNER_PATTERN, DIRECT_CODEC);
      STREAM_CODEC = ByteBufCodecs.holder(Registries.BANNER_PATTERN, DIRECT_STREAM_CODEC);
   }
}
