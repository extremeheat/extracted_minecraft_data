package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.util.debug.DebugStructureInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StructureRenderer implements DebugRenderer.SimpleDebugRenderer {
   public StructureRenderer() {
      super();
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      VertexConsumer var10 = var2.getBuffer(RenderType.lines());
      var9.forEachChunk(DebugSubscriptions.STRUCTURES, (var8, var9x) -> {
         for(DebugStructureInfo var11 : var9x) {
            renderBox(var1, var3, var5, var7, var10, var11.boundingBox(), 1.0F, 1.0F, 1.0F, 1.0F);

            for(DebugStructureInfo.Piece var13 : var11.pieces()) {
               if (var13.isStart()) {
                  renderBox(var1, var3, var5, var7, var10, var13.boundingBox(), 0.0F, 1.0F, 0.0F, 1.0F);
               } else {
                  renderBox(var1, var3, var5, var7, var10, var13.boundingBox(), 0.0F, 0.0F, 1.0F, 1.0F);
               }
            }
         }

      });
   }

   private static void renderBox(PoseStack var0, double var1, double var3, double var5, VertexConsumer var7, BoundingBox var8, float var9, float var10, float var11, float var12) {
      ShapeRenderer.renderLineBox(var0.last(), var7, (double)var8.minX() - var1, (double)var8.minY() - var3, (double)var8.minZ() - var5, (double)(var8.maxX() + 1) - var1, (double)(var8.maxY() + 1) - var3, (double)(var8.maxZ() + 1) - var5, var9, var10, var11, var12, var9, var10, var11);
   }
}
