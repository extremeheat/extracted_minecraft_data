package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class ShieldSpecialRenderer implements SpecialModelRenderer<DataComponentMap> {
   private final ShieldModel model;

   public ShieldSpecialRenderer(ShieldModel var1) {
      super();
      this.model = var1;
   }

   @Nullable
   public DataComponentMap extractArgument(ItemStack var1) {
      return var1.immutableComponents();
   }

   public void render(@Nullable DataComponentMap var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, boolean var7) {
      BannerPatternLayers var8 = var1 != null ? (BannerPatternLayers)var1.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY) : BannerPatternLayers.EMPTY;
      DyeColor var9 = var1 != null ? (DyeColor)var1.get(DataComponents.BASE_COLOR) : null;
      boolean var10 = !var8.layers().isEmpty() || var9 != null;
      var3.pushPose();
      var3.scale(1.0F, -1.0F, -1.0F);
      Material var11 = var10 ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
      VertexConsumer var12 = var11.sprite().wrap(ItemRenderer.getFoilBuffer(var4, this.model.renderType(var11.atlasLocation()), var2 == ItemDisplayContext.GUI, var7));
      this.model.handle().render(var3, var12, var5, var6);
      if (var10) {
         BannerRenderer.renderPatterns(var3, var4, var5, var6, this.model.plate(), var11, false, (DyeColor)Objects.requireNonNullElse(var9, DyeColor.WHITE), var8, var7, false);
      } else {
         this.model.plate().render(var3, var12, var5, var6);
      }

      var3.popPose();
   }

   // $FF: synthetic method
   @Nullable
   public Object extractArgument(final ItemStack var1) {
      return this.extractArgument(var1);
   }

   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final Unbaked INSTANCE = new Unbaked();
      public static final MapCodec<Unbaked> MAP_CODEC;

      public Unbaked() {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(EntityModelSet var1) {
         return new ShieldSpecialRenderer(new ShieldModel(var1.bakeLayer(ModelLayers.SHIELD)));
      }

      static {
         MAP_CODEC = MapCodec.unit(INSTANCE);
      }
   }
}
