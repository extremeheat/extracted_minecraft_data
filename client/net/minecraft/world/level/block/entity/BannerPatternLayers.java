package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import org.slf4j.Logger;

public record BannerPatternLayers(List<Layer> layers) {
   final List<Layer> layers;
   static final Logger LOGGER = LogUtils.getLogger();
   public static final BannerPatternLayers EMPTY = new BannerPatternLayers(List.of());
   public static final Codec<BannerPatternLayers> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, BannerPatternLayers> STREAM_CODEC;

   public BannerPatternLayers(List<Layer> layers) {
      super();
      this.layers = layers;
   }

   public BannerPatternLayers removeLast() {
      return new BannerPatternLayers(List.copyOf(this.layers.subList(0, this.layers.size() - 1)));
   }

   public List<Layer> layers() {
      return this.layers;
   }

   static {
      CODEC = BannerPatternLayers.Layer.CODEC.listOf().xmap(BannerPatternLayers::new, BannerPatternLayers::layers);
      STREAM_CODEC = BannerPatternLayers.Layer.STREAM_CODEC.apply(ByteBufCodecs.list()).map(BannerPatternLayers::new, BannerPatternLayers::layers);
   }

   public static record Layer(Holder<BannerPattern> pattern, DyeColor color) {
      public static final Codec<Layer> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(BannerPattern.CODEC.fieldOf("pattern").forGetter(Layer::pattern), DyeColor.CODEC.fieldOf("color").forGetter(Layer::color)).apply(var0, Layer::new);
      });
      public static final StreamCodec<RegistryFriendlyByteBuf, Layer> STREAM_CODEC;

      public Layer(Holder<BannerPattern> pattern, DyeColor color) {
         super();
         this.pattern = pattern;
         this.color = color;
      }

      public MutableComponent description() {
         String var1 = ((BannerPattern)this.pattern.value()).translationKey();
         return Component.translatable(var1 + "." + this.color.getName());
      }

      public Holder<BannerPattern> pattern() {
         return this.pattern;
      }

      public DyeColor color() {
         return this.color;
      }

      static {
         STREAM_CODEC = StreamCodec.composite(BannerPattern.STREAM_CODEC, Layer::pattern, DyeColor.STREAM_CODEC, Layer::color, Layer::new);
      }
   }

   public static class Builder {
      private final ImmutableList.Builder<Layer> layers = ImmutableList.builder();

      public Builder() {
         super();
      }

      /** @deprecated */
      @Deprecated
      public Builder addIfRegistered(HolderGetter<BannerPattern> var1, ResourceKey<BannerPattern> var2, DyeColor var3) {
         Optional var4 = var1.get(var2);
         if (var4.isEmpty()) {
            BannerPatternLayers.LOGGER.warn("Unable to find banner pattern with id: '{}'", var2.location());
            return this;
         } else {
            return this.add((Holder)var4.get(), var3);
         }
      }

      public Builder add(Holder<BannerPattern> var1, DyeColor var2) {
         return this.add(new Layer(var1, var2));
      }

      public Builder add(Layer var1) {
         this.layers.add(var1);
         return this;
      }

      public Builder addAll(BannerPatternLayers var1) {
         this.layers.addAll(var1.layers);
         return this;
      }

      public BannerPatternLayers build() {
         return new BannerPatternLayers(this.layers.build());
      }
   }
}
