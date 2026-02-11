package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.equipment.ShieldModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.SpriteId;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class ShieldSpecialRenderer implements SpecialModelRenderer<DataComponentMap> {
   private final SpriteGetter sprites;
   private final ShieldModel model;

   public ShieldSpecialRenderer(final SpriteGetter sprites, final ShieldModel model) {
      super();
      this.sprites = sprites;
      this.model = model;
   }

   public @Nullable DataComponentMap extractArgument(final ItemStack stack) {
      return stack.immutableComponents();
   }

   public void submit(final @Nullable DataComponentMap components, final ItemDisplayContext type, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      BannerPatternLayers patterns = components != null ? (BannerPatternLayers)components.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY) : BannerPatternLayers.EMPTY;
      DyeColor baseColor = components != null ? (DyeColor)components.get(DataComponents.BASE_COLOR) : null;
      boolean hasPatterns = !patterns.layers().isEmpty() || baseColor != null;
      poseStack.pushPose();
      poseStack.scale(1.0F, -1.0F, -1.0F);
      SpriteId base = hasPatterns ? Sheets.SHIELD_BASE : Sheets.SHIELD_BASE_NO_PATTERN;
      submitNodeCollector.submitModel(this.model, Unit.INSTANCE, poseStack, this.model.renderType(base.atlasLocation()), lightCoords, overlayCoords, -1, this.sprites.get(base), outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      if (hasPatterns) {
         BannerRenderer.submitPatterns(this.sprites, poseStack, submitNodeCollector, lightCoords, overlayCoords, this.model, Unit.INSTANCE, false, (DyeColor)Objects.requireNonNullElse(baseColor, DyeColor.WHITE), patterns, (ModelFeatureRenderer.CrumblingOverlay)null);
      }

      if (hasFoil) {
         submitNodeCollector.submitModel(this.model, Unit.INSTANCE, poseStack, RenderTypes.entityGlint(), lightCoords, overlayCoords, -1, this.sprites.get(base), 0, (ModelFeatureRenderer.CrumblingOverlay)null);
      }

      poseStack.popPose();
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      poseStack.scale(1.0F, -1.0F, -1.0F);
      this.model.root().getExtentsForGui(poseStack, output);
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

      public SpecialModelRenderer<?> bake(final SpecialModelRenderer.BakingContext context) {
         return new ShieldSpecialRenderer(context.sprites(), new ShieldModel(context.entityModelSet().bakeLayer(ModelLayers.SHIELD)));
      }

      static {
         MAP_CODEC = MapCodec.unit(INSTANCE);
      }
   }
}
