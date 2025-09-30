package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.joml.Vector3f;

public class ShieldSpecialRenderer implements SpecialModelRenderer<DataComponentMap> {
   private final MaterialSet materials;
   private final ShieldModel model;

   public ShieldSpecialRenderer(MaterialSet var1, ShieldModel var2) {
      super();
      this.materials = var1;
      this.model = var2;
   }

   @Nullable
   public DataComponentMap extractArgument(ItemStack var1) {
      return var1.immutableComponents();
   }

   public void submit(@Nullable DataComponentMap var1, ItemDisplayContext var2, PoseStack var3, SubmitNodeCollector var4, int var5, int var6, boolean var7, int var8) {
      BannerPatternLayers var9 = var1 != null ? (BannerPatternLayers)var1.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY) : BannerPatternLayers.EMPTY;
      DyeColor var10 = var1 != null ? (DyeColor)var1.get(DataComponents.BASE_COLOR) : null;
      boolean var11 = !var9.layers().isEmpty() || var10 != null;
      var3.pushPose();
      var3.scale(1.0F, -1.0F, -1.0F);
      Material var12 = var11 ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
      var4.submitModelPart(this.model.handle(), var3, this.model.renderType(var12.atlasLocation()), var5, var6, this.materials.get(var12), false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, var8);
      if (var11) {
         BannerRenderer.submitPatterns(this.materials, var3, var4, var5, var6, this.model, Unit.INSTANCE, var12, false, (DyeColor)Objects.requireNonNullElse(var10, DyeColor.WHITE), var9, var7, (ModelFeatureRenderer.CrumblingOverlay)null, var8);
      } else {
         var4.submitModelPart(this.model.plate(), var3, this.model.renderType(var12.atlasLocation()), var5, var6, this.materials.get(var12), false, var7, -1, (ModelFeatureRenderer.CrumblingOverlay)null, var8);
      }

      var3.popPose();
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      var2.scale(1.0F, -1.0F, -1.0F);
      this.model.root().getExtentsForGui(var2, var1);
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

      public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext var1) {
         return new ShieldSpecialRenderer(var1.materials(), new ShieldModel(var1.entityModelSet().bakeLayer(ModelLayers.SHIELD)));
      }

      static {
         MAP_CODEC = MapCodec.unit(INSTANCE);
      }
   }
}
