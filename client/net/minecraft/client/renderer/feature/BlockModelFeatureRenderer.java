package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

public class BlockModelFeatureRenderer extends RenderTypeFeatureRenderer<Submit> {
   public static final FeatureRendererType<Submit> TYPE = FeatureRendererType.<Submit>create("Block Model");
   private static final Direction[] DIRECTIONS = Direction.values();
   private final QuadInstance quadInstance = new QuadInstance();

   public BlockModelFeatureRenderer() {
      super();
   }

   protected void buildGroup(final FeatureFrameContext context, final List<Submit> submits) {
      for(Submit submit : submits) {
         VertexConsumer buffer = this.getVertexBuilder(submit.renderType());
         VertexConsumer wrappedBuffer = (VertexConsumer)(submit.sheetedDecalPose() != null ? new SheetedDecalTextureGenerator(buffer, submit.sheetedDecalPose(), 1.0F) : buffer);
         this.quadInstance.setLightCoords(submit.lightCoords());
         this.quadInstance.setOverlayCoords(submit.overlayCoords());

         for(BlockStateModelPart part : submit.modelParts()) {
            putPartQuads(part, submit.pose(), this.quadInstance, submit.tintColor(), submit.tintLayers(), wrappedBuffer);
         }
      }

   }

   private static void putPartQuads(final BlockStateModelPart part, final PoseStack.Pose pose, final QuadInstance quadInstance, final int baseTintColor, final int[] tintLayers, final VertexConsumer buffer) {
      for(Direction direction : DIRECTIONS) {
         for(BakedQuad quad : part.getQuads(direction)) {
            putQuad(pose, quad, quadInstance, baseTintColor, tintLayers, buffer);
         }
      }

      for(BakedQuad quad : part.getQuads((Direction)null)) {
         putQuad(pose, quad, quadInstance, baseTintColor, tintLayers, buffer);
      }

   }

   private static void putQuad(final PoseStack.Pose pose, final BakedQuad quad, final QuadInstance instance, final int baseTintColor, final int[] tintLayers, final VertexConsumer buffer) {
      int tintIndex = quad.materialInfo().tintIndex();
      boolean useTintLayer = tintIndex != -1 && tintIndex < tintLayers.length;
      instance.setColor(useTintLayer ? ARGB.multiply(baseTintColor, tintLayers[tintIndex]) : baseTintColor);
      buffer.putBakedQuad(pose, quad, instance);
   }

   public static record Submit(PoseStack.Pose pose, RenderType renderType, List<BlockStateModelPart> modelParts, int[] tintLayers, int lightCoords, int overlayCoords, int tintColor, PoseStack.@Nullable Pose sheetedDecalPose) implements TranslucentSubmit {
      public Submit {
         super();
      }

      public float distanceToCameraSq() {
         return TranslucentSubmit.computeDistanceToCameraSq(this.pose.pose(), 0.5F, 0.5F, 0.5F);
      }

      public FeatureRendererType<Submit> featureType() {
         return BlockModelFeatureRenderer.TYPE;
      }
   }
}
