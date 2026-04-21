package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class ShapeOutlineFeatureRenderer {
   public ShapeOutlineFeatureRenderer() {
      super();
   }

   public void renderTranslucent(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context, final boolean afterTerrain) {
      Vector3f normal = new Vector3f();

      for(Submit submit : nodeCollection.getShapeOutlineSubmits()) {
         if (submit.afterTerrain() == afterTerrain) {
            PoseStack.Pose pose = submit.pose();
            int color = submit.color();
            float width = submit.width();
            VertexConsumer builder = context.bufferSource().getBuffer(submit.renderType());
            submit.shape().forAllEdges((x1, y1, z1, x2, y2, z2) -> {
               normal.set((float)(x2 - x1), (float)(y2 - y1), (float)(z2 - z1)).normalize();
               builder.addVertex(pose, (float)x1, (float)y1, (float)z1).setColor(color).setNormal(pose, normal).setLineWidth(width);
               builder.addVertex(pose, (float)x2, (float)y2, (float)z2).setColor(color).setNormal(pose, normal).setLineWidth(width);
            });
         }
      }

   }

   public static record Submit(PoseStack.Pose pose, VoxelShape shape, RenderType renderType, int color, float width, boolean afterTerrain) {
      public Submit {
         super();
      }
   }
}
