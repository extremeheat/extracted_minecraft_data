package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import org.joml.Vector3fc;

public class ConduitSpecialRenderer implements NoDataSpecialModelRenderer {
   private final SpriteGetter sprites;
   private final ModelPart model;

   public ConduitSpecialRenderer(final SpriteGetter sprites, final ModelPart model) {
      super();
      this.sprites = sprites;
      this.model = model;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      submitNodeCollector.submitModelPart(this.model, poseStack, ConduitRenderer.SHELL_TEXTURE.renderType(RenderTypes::entitySolid), lightCoords, overlayCoords, this.sprites.get(ConduitRenderer.SHELL_TEXTURE), false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, outlineColor);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      this.model.getExtentsForGui(poseStack, output);
   }

   public static record Unbaked() implements NoDataSpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

      public Unbaked() {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public ConduitSpecialRenderer bake(final SpecialModelRenderer.BakingContext context) {
         return new ConduitSpecialRenderer(context.sprites(), context.entityModelSet().bakeLayer(ModelLayers.CONDUIT_SHELL));
      }
   }
}
