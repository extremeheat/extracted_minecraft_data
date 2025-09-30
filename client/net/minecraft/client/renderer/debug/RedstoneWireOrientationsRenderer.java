package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import org.joml.Vector3f;

public class RedstoneWireOrientationsRenderer implements DebugRenderer.SimpleDebugRenderer {
   public RedstoneWireOrientationsRenderer() {
      super();
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      VertexConsumer var11 = var2.getBuffer(RenderType.lines());
      var9.forEachBlock(DebugSubscriptions.REDSTONE_WIRE_ORIENTATIONS, (var8, var9x) -> {
         Vector3f var10 = var8.getBottomCenter().subtract(var3, var5 - 0.1, var7).toVector3f();
         ShapeRenderer.renderVector(var1, var11, var10, var9x.getFront().getUnitVec3().scale(0.5), -16776961);
         ShapeRenderer.renderVector(var1, var11, var10, var9x.getUp().getUnitVec3().scale(0.4), -65536);
         ShapeRenderer.renderVector(var1, var11, var10, var9x.getSide().getUnitVec3().scale(0.3), -256);
      });
   }
}
