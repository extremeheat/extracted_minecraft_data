package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class BannerSpecialRenderer implements SpecialModelRenderer<BannerPatternLayers> {
   private final BannerRenderer bannerRenderer;
   private final DyeColor baseColor;

   public BannerSpecialRenderer(DyeColor var1, BannerRenderer var2) {
      super();
      this.bannerRenderer = var2;
      this.baseColor = var1;
   }

   @Nullable
   public BannerPatternLayers extractArgument(ItemStack var1) {
      return (BannerPatternLayers)var1.get(DataComponents.BANNER_PATTERNS);
   }

   public void render(@Nullable BannerPatternLayers var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, boolean var7) {
      this.bannerRenderer.renderInHand(var3, var4, var5, var6, this.baseColor, (BannerPatternLayers)Objects.requireNonNullElse(var1, BannerPatternLayers.EMPTY));
   }

   // $FF: synthetic method
   @Nullable
   public Object extractArgument(final ItemStack var1) {
      return this.extractArgument(var1);
   }

   public static record Unbaked(DyeColor baseColor) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(DyeColor.CODEC.fieldOf("color").forGetter(Unbaked::baseColor)).apply(var0, Unbaked::new);
      });

      public Unbaked(DyeColor var1) {
         super();
         this.baseColor = var1;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(EntityModelSet var1) {
         return new BannerSpecialRenderer(this.baseColor, new BannerRenderer(var1));
      }

      public DyeColor baseColor() {
         return this.baseColor;
      }
   }
}
