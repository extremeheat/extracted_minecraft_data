package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;

public class EntityBlockIntersectionDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final float PADDING = 0.02F;

   public EntityBlockIntersectionDebugRenderer() {
      super();
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      var9.forEachBlock(DebugSubscriptions.ENTITY_BLOCK_INTERSECTIONS, (var2x, var3x) -> {
         float var4 = ARGB.redFloat(var3x.color());
         float var5 = ARGB.greenFloat(var3x.color());
         float var6 = ARGB.blueFloat(var3x.color());
         float var7 = ARGB.alphaFloat(var3x.color());
         DebugRenderer.renderFilledBox(var1, var2, var2x, 0.02F, var4, var5, var6, var7);
      });
   }
}
