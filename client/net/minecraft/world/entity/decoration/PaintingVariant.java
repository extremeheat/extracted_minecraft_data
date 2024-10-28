package net.minecraft.world.entity.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public record PaintingVariant(int width, int height, ResourceLocation assetId, Optional<Component> title, Optional<Component> author) {
   public static final Codec<PaintingVariant> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ExtraCodecs.intRange(1, 16).fieldOf("width").forGetter(PaintingVariant::width), ExtraCodecs.intRange(1, 16).fieldOf("height").forGetter(PaintingVariant::height), ResourceLocation.CODEC.fieldOf("asset_id").forGetter(PaintingVariant::assetId), ComponentSerialization.CODEC.optionalFieldOf("title").forGetter(PaintingVariant::title), ComponentSerialization.CODEC.optionalFieldOf("author").forGetter(PaintingVariant::author)).apply(var0, PaintingVariant::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, PaintingVariant> DIRECT_STREAM_CODEC;
   public static final Codec<Holder<PaintingVariant>> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, Holder<PaintingVariant>> STREAM_CODEC;

   public PaintingVariant(int var1, int var2, ResourceLocation var3, Optional<Component> var4, Optional<Component> var5) {
      super();
      this.width = var1;
      this.height = var2;
      this.assetId = var3;
      this.title = var4;
      this.author = var5;
   }

   public int area() {
      return this.width() * this.height();
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public ResourceLocation assetId() {
      return this.assetId;
   }

   public Optional<Component> title() {
      return this.title;
   }

   public Optional<Component> author() {
      return this.author;
   }

   static {
      DIRECT_STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT, PaintingVariant::width, ByteBufCodecs.VAR_INT, PaintingVariant::height, ResourceLocation.STREAM_CODEC, PaintingVariant::assetId, ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC, PaintingVariant::title, ComponentSerialization.TRUSTED_OPTIONAL_STREAM_CODEC, PaintingVariant::author, PaintingVariant::new);
      CODEC = RegistryFileCodec.create(Registries.PAINTING_VARIANT, DIRECT_CODEC);
      STREAM_CODEC = ByteBufCodecs.holder(Registries.PAINTING_VARIANT, DIRECT_STREAM_CODEC);
   }
}
