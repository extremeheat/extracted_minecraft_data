package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class BreezeDebugRenderer {
   private static final int JUMP_TARGET_LINE_COLOR = ARGB.color(255, 255, 100, 255);
   private static final int TARGET_LINE_COLOR = ARGB.color(255, 100, 255, 255);
   private static final int INNER_CIRCLE_COLOR = ARGB.color(255, 0, 255, 0);
   private static final int MIDDLE_CIRCLE_COLOR = ARGB.color(255, 255, 165, 0);
   private static final int OUTER_CIRCLE_COLOR = ARGB.color(255, 255, 0, 0);
   private static final int CIRCLE_VERTICES = 20;
   private static final float SEGMENT_SIZE_RADIANS = 0.31415927F;
   private final Minecraft minecraft;

   public BreezeDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      ClientLevel var10 = this.minecraft.level;
      var9.forEachEntity(DebugSubscriptions.BREEZES, (var10x, var11) -> {
         Optional var10000 = var11.attackTarget();
         Objects.requireNonNull(var10);
         var10000.map(var10::getEntity).map((var1x) -> var1x.getPosition(this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true))).ifPresent((var9) -> {
            drawLine(var1, var2, var3, var5, var7, var10x.position(), var9, TARGET_LINE_COLOR);
            Vec3 var10 = var9.add(0.0, 0.009999999776482582, 0.0);
            drawCircle(var1.last().pose(), var3, var5, var7, var2.getBuffer(RenderType.debugLineStrip(2.0)), var10, 4.0F, INNER_CIRCLE_COLOR);
            drawCircle(var1.last().pose(), var3, var5, var7, var2.getBuffer(RenderType.debugLineStrip(2.0)), var10, 8.0F, MIDDLE_CIRCLE_COLOR);
            drawCircle(var1.last().pose(), var3, var5, var7, var2.getBuffer(RenderType.debugLineStrip(2.0)), var10, 24.0F, OUTER_CIRCLE_COLOR);
         });
         var11.jumpTarget().ifPresent((var9) -> {
            drawLine(var1, var2, var3, var5, var7, var10x.position(), var9.getCenter(), JUMP_TARGET_LINE_COLOR);
            DebugRenderer.renderFilledBox(var1, var2, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(var9)).move(-var3, -var5, -var7), 1.0F, 0.0F, 0.0F, 1.0F);
         });
      });
   }

   private static void drawLine(PoseStack var0, MultiBufferSource var1, double var2, double var4, double var6, Vec3 var8, Vec3 var9, int var10) {
      VertexConsumer var11 = var1.getBuffer(RenderType.debugLineStrip(2.0));
      var11.addVertex(var0.last(), (float)(var8.x - var2), (float)(var8.y - var4), (float)(var8.z - var6)).setColor(var10);
      var11.addVertex(var0.last(), (float)(var9.x - var2), (float)(var9.y - var4), (float)(var9.z - var6)).setColor(var10);
   }

   private static void drawCircle(Matrix4f var0, double var1, double var3, double var5, VertexConsumer var7, Vec3 var8, float var9, int var10) {
      for(int var11 = 0; var11 < 20; ++var11) {
         drawCircleVertex(var11, var0, var1, var3, var5, var7, var8, var9, var10);
      }

      drawCircleVertex(0, var0, var1, var3, var5, var7, var8, var9, var10);
   }

   private static void drawCircleVertex(int var0, Matrix4f var1, double var2, double var4, double var6, VertexConsumer var8, Vec3 var9, float var10, int var11) {
      float var12 = (float)var0 * 0.31415927F;
      Vec3 var13 = var9.add((double)var10 * Math.cos((double)var12), 0.0, (double)var10 * Math.sin((double)var12));
      var8.addVertex(var1, (float)(var13.x - var2), (float)(var13.y - var4), (float)(var13.z - var6)).setColor(var11);
   }
}
