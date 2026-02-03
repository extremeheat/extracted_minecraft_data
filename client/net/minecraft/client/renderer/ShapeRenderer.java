package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class ShapeRenderer {
   public ShapeRenderer() {
      super();
   }

   public static void renderShape(final PoseStack poseStack, final VertexConsumer builder, final VoxelShape shape, final double x, final double y, final double z, final int color, final float width) {
      PoseStack.Pose pose = poseStack.last();
      shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
         Vector3f normal = (new Vector3f((float)(x2 - x1), (float)(y2 - y1), (float)(z2 - z1))).normalize();
         builder.addVertex(pose, (float)(x1 + x), (float)(y1 + y), (float)(z1 + z)).setColor(color).setNormal(pose, normal).setLineWidth(width);
         builder.addVertex(pose, (float)(x2 + x), (float)(y2 + y), (float)(z2 + z)).setColor(color).setNormal(pose, normal).setLineWidth(width);
      });
   }
}
