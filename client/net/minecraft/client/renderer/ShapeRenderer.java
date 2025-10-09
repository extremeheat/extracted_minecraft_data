package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class ShapeRenderer {
   public ShapeRenderer() {
      super();
   }

   public static void renderShape(PoseStack var0, VertexConsumer var1, VoxelShape var2, double var3, double var5, double var7, int var9, float var10) {
      PoseStack.Pose var11 = var0.last();
      var2.forAllEdges((var10x, var12, var14, var16, var18, var20) -> {
         Vector3f var22 = (new Vector3f((float)(var16 - var10x), (float)(var18 - var12), (float)(var20 - var14))).normalize();
         var1.addVertex(var11, (float)(var10x + var3), (float)(var12 + var5), (float)(var14 + var7)).setColor(var9).setNormal(var11, var22).setLineWidth(var10);
         var1.addVertex(var11, (float)(var16 + var3), (float)(var18 + var5), (float)(var20 + var7)).setColor(var9).setNormal(var11, var22).setLineWidth(var10);
      });
   }
}
