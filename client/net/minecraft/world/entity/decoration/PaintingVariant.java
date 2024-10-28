package net.minecraft.world.entity.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public record PaintingVariant(int width, int height, ResourceLocation assetId) {
   public static final Codec<PaintingVariant> DIRECT_CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ExtraCodecs.intRange(1, 16).fieldOf("width").forGetter(PaintingVariant::width), ExtraCodecs.intRange(1, 16).fieldOf("height").forGetter(PaintingVariant::height), ResourceLocation.CODEC.fieldOf("asset_id").forGetter(PaintingVariant::assetId)).apply(var0, PaintingVariant::new);
   });
   public static final Codec<Holder<PaintingVariant>> CODEC;

   public PaintingVariant(int width, int height, ResourceLocation assetId) {
      super();
      this.width = width;
      this.height = height;
      this.assetId = assetId;
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

   static {
      CODEC = RegistryFileCodec.create(Registries.PAINTING_VARIANT, DIRECT_CODEC);
   }
}
