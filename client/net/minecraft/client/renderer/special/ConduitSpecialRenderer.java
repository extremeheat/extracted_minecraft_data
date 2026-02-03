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
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3fc;

public class ConduitSpecialRenderer implements NoDataSpecialModelRenderer {
   private final MaterialSet materials;
   private final ModelPart model;

   public ConduitSpecialRenderer(final MaterialSet materials, final ModelPart model) {
      super();
      this.materials = materials;
      this.model = model;
   }

   public void submit(final ItemDisplayContext type, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      poseStack.pushPose();
      poseStack.translate(0.5F, 0.5F, 0.5F);
      submitNodeCollector.submitModelPart(this.model, poseStack, ConduitRenderer.SHELL_TEXTURE.renderType(RenderTypes::entitySolid), lightCoords, overlayCoords, this.materials.get(ConduitRenderer.SHELL_TEXTURE), false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, outlineColor);
      poseStack.popPose();
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      poseStack.translate(0.5F, 0.5F, 0.5F);
      this.model.getExtentsForGui(poseStack, output);
   }

   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

      public Unbaked() {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(final SpecialModelRenderer.BakingContext context) {
         return new ConduitSpecialRenderer(context.materials(), context.entityModelSet().bakeLayer(ModelLayers.CONDUIT_SHELL));
      }
   }
}
