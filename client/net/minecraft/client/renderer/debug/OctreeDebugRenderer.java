package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Octree;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableInt;

public class OctreeDebugRenderer {
   private final Minecraft minecraft;

   public OctreeDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, Frustum var2, MultiBufferSource var3, double var4, double var6, double var8) {
      Octree var10 = this.minecraft.levelRenderer.getSectionOcclusionGraph().getOctree();
      MutableInt var11 = new MutableInt(0);
      var10.visitNodes((var10x, var11x, var12) -> this.renderNode(var10x, var1, var3, var4, var6, var8, var12, var11x, var11), var2);
   }

   private void renderNode(
      Octree.Node var1, PoseStack var2, MultiBufferSource var3, double var4, double var6, double var8, int var10, boolean var11, MutableInt var12
   ) {
      AABB var13 = var1.getAABB();
      double var14 = var13.getXsize();
      long var16 = Math.round(var14 / 16.0);
      if (var16 == 1L) {
         var12.add(1);
         double var18 = var13.getCenter().x;
         double var20 = var13.getCenter().y;
         double var22 = var13.getCenter().z;
         DebugRenderer.renderFloatingText(var2, var3, String.valueOf(var12.getValue()), var18, var20, var22, -1, 0.3F);
      }

      VertexConsumer var24 = var3.getBuffer(RenderType.lines());
      long var19 = var16 + 5L;
      ShapeRenderer.renderLineBox(
         var2,
         var24,
         var13.deflate(0.1 * (double)var10).move(-var4, -var6, -var8),
         getColorComponent(var19, 0.3F),
         getColorComponent(var19, 0.8F),
         getColorComponent(var19, 0.5F),
         var11 ? 0.4F : 1.0F
      );
   }

   private static float getColorComponent(long var0, float var2) {
      float var3 = 0.1F;
      return Mth.frac(var2 * (float)var0) * 0.9F + 0.1F;
   }
}
