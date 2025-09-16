package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;

public class GameEventListenerRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final float BOX_HEIGHT = 1.0F;

   public GameEventListenerRenderer() {
      super();
   }

   private void forEachListener(DebugValueAccess var1, ListenerVisitor var2) {
      var1.forEachBlock(DebugSubscriptions.GAME_EVENT_LISTENERS, (var1x, var2x) -> var2.accept(var1x.getCenter(), var2x.listenerRadius()));
      var1.forEachEntity(DebugSubscriptions.GAME_EVENT_LISTENERS, (var1x, var2x) -> var2.accept(var1x.position(), var2x.listenerRadius()));
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      VertexConsumer var11 = var2.getBuffer(RenderType.lines());
      this.forEachListener(var9, (var8, var9x) -> {
         double var10 = (double)var9x * 2.0;
         DebugRenderer.renderVoxelShape(var1, var11, Shapes.create(AABB.ofSize(var8, var10, var10, var10)), -var3, -var5, -var7, 1.0F, 1.0F, 0.0F, 0.35F, true);
      });
      VertexConsumer var12 = var2.getBuffer(RenderType.debugFilledBox());
      this.forEachListener(var9, (var8, var9x) -> ShapeRenderer.addChainedFilledBoxVertices(var1, var12, var8.x() - 0.25 - var3, var8.y() - var5, var8.z() - 0.25 - var7, var8.x() + 0.25 - var3, var8.y() - var5 + 1.0, var8.z() + 0.25 - var7, 1.0F, 1.0F, 0.0F, 0.35F));
      this.forEachListener(var9, (var2x, var3x) -> {
         DebugRenderer.renderFloatingText(var1, var2, "Listener Origin", var2x.x(), var2x.y() + 1.7999999523162842, var2x.z(), -1, 0.025F);
         DebugRenderer.renderFloatingText(var1, var2, BlockPos.containing(var2x).toString(), var2x.x(), var2x.y() + 1.5, var2x.z(), -6959665, 0.025F);
      });
      var9.forEachEvent(DebugSubscriptions.GAME_EVENTS, (var2x, var3x, var4) -> {
         Vec3 var5 = var2x.pos();
         double var6 = 0.4;
         AABB var8 = AABB.ofSize(var5.add(0.0, 0.5, 0.0), 0.4, 0.9, 0.4);
         renderFilledBox(var1, var2, var8, 1.0F, 1.0F, 1.0F, 0.2F);
         DebugRenderer.renderFloatingText(var1, var2, var2x.event().getRegisteredName(), var5.x, var5.y + 0.8500000238418579, var5.z, -7564911, 0.0075F);
      });
   }

   private static void renderFilledBox(PoseStack var0, MultiBufferSource var1, AABB var2, float var3, float var4, float var5, float var6) {
      Camera var7 = Minecraft.getInstance().gameRenderer.getMainCamera();
      if (var7.isInitialized()) {
         Vec3 var8 = var7.getPosition().reverse();
         DebugRenderer.renderFilledBox(var0, var1, var2.move(var8), var3, var4, var5, var6);
      }
   }

   @FunctionalInterface
   interface ListenerVisitor {
      void accept(Vec3 var1, int var2);
   }
}
