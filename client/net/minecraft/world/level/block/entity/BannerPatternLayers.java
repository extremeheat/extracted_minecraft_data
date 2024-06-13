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

public record BannerPatternLayers(List<BannerPatternLayers.Layer> layers) {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final BannerPatternLayers EMPTY = new BannerPatternLayers(List.of());
   public static final Codec<BannerPatternLayers> CODEC = BannerPatternLayers.Layer.CODEC.listOf().xmap(BannerPatternLayers::new, BannerPatternLayers::layers);
   public static final StreamCodec<RegistryFriendlyByteBuf, BannerPatternLayers> STREAM_CODEC = BannerPatternLayers.Layer.STREAM_CODEC
      .apply(ByteBufCodecs.list())
      .map(BannerPatternLayers::new, BannerPatternLayers::layers);

   public BannerPatternLayers(List<BannerPatternLayers.Layer> layers) {
      super();
      this.layers = (List<BannerPatternLayers.Layer>)layers;
   }

   public BannerPatternLayers removeLast() {
      return new BannerPatternLayers(List.copyOf(this.layers.subList(0, this.layers.size() - 1)));
   }

   public static class Builder {
      private final com.google.common.collect.ImmutableList.Builder<BannerPatternLayers.Layer> layers = ImmutableList.builder();

      public Builder() {
         super();
      }

      @Deprecated
      public BannerPatternLayers.Builder addIfRegistered(HolderGetter<BannerPattern> var1, ResourceKey<BannerPattern> var2, DyeColor var3) {
         Optional var4 = var1.get(var2);
         if (var4.isEmpty()) {
            BannerPatternLayers.LOGGER.warn("Unable to find banner pattern with id: '{}'", var2.location());
            return this;
         } else {
            return this.add((Holder<BannerPattern>)var4.get(), var3);
         }
      }

      public BannerPatternLayers.Builder add(Holder<BannerPattern> var1, DyeColor var2) {
         return this.add(new BannerPatternLayers.Layer(var1, var2));
      }

      public BannerPatternLayers.Builder add(BannerPatternLayers.Layer var1) {
         this.layers.add(var1);
         return this;
      }

      public BannerPatternLayers.Builder addAll(BannerPatternLayers var1) {
         this.layers.addAll(var1.layers);
         return this;
      }

      public BannerPatternLayers build() {
         return new BannerPatternLayers(this.layers.build());
      }
   }

   public static record Layer(Holder<BannerPattern> pattern, DyeColor color) {
      public static final Codec<BannerPatternLayers.Layer> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  BannerPattern.CODEC.fieldOf("pattern").forGetter(BannerPatternLayers.Layer::pattern),
                  DyeColor.CODEC.fieldOf("color").forGetter(BannerPatternLayers.Layer::color)
               )
               .apply(var0, BannerPatternLayers.Layer::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, BannerPatternLayers.Layer> STREAM_CODEC = StreamCodec.composite(
         BannerPattern.STREAM_CODEC,
         BannerPatternLayers.Layer::pattern,
         DyeColor.STREAM_CODEC,
         BannerPatternLayers.Layer::color,
         BannerPatternLayers.Layer::new
      );

      public Layer(Holder<BannerPattern> pattern, DyeColor color) {
         super();
         this.pattern = pattern;
         this.color = color;
      }

      public MutableComponent description() {
         String var1 = this.pattern.value().translationKey();
         return Component.translatable(var1 + "." + this.color.getName());
      }
   }
}
