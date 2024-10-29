package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.protocol.common.custom.BreezeDebugPayload;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.entity.EntityTypeTest;
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
   private final Map<Integer, BreezeDebugPayload.BreezeInfo> perEntity = new HashMap();

   public BreezeDebugRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      LocalPlayer var9 = this.minecraft.player;
      var9.level().getEntities((EntityTypeTest)EntityType.BREEZE, var9.getBoundingBox().inflate(100.0), (var0) -> {
         return true;
      }).forEach((var10) -> {
         Optional var11 = Optional.ofNullable((BreezeDebugPayload.BreezeInfo)this.perEntity.get(var10.getId()));
         var11.map(BreezeDebugPayload.BreezeInfo::attackTarget).map((var1x) -> {
            return var9.level().getEntity(var1x);
         }).map((var1x) -> {
            return var1x.getPosition(this.minecraft.getDeltaTracker().getGameTimeDeltaPartialTick(true));
         }).ifPresent((var9x) -> {
            drawLine(var1, var2, var3, var5, var7, var10.position(), var9x, TARGET_LINE_COLOR);
            Vec3 var10x = var9x.add(0.0, 0.009999999776482582, 0.0);
            drawCircle(var1.last().pose(), var3, var5, var7, var2.getBuffer(RenderType.debugLineStrip(2.0)), var10x, 4.0F, INNER_CIRCLE_COLOR);
            drawCircle(var1.last().pose(), var3, var5, var7, var2.getBuffer(RenderType.debugLineStrip(2.0)), var10x, 8.0F, MIDDLE_CIRCLE_COLOR);
            drawCircle(var1.last().pose(), var3, var5, var7, var2.getBuffer(RenderType.debugLineStrip(2.0)), var10x, 24.0F, OUTER_CIRCLE_COLOR);
         });
         var11.map(BreezeDebugPayload.BreezeInfo::jumpTarget).ifPresent((var9x) -> {
            drawLine(var1, var2, var3, var5, var7, var10.position(), var9x.getCenter(), JUMP_TARGET_LINE_COLOR);
            DebugRenderer.renderFilledBox(var1, var2, AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(var9x)).move(-var3, -var5, -var7), 1.0F, 0.0F, 0.0F, 1.0F);
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

   public void clear() {
      this.perEntity.clear();
   }

   public void add(BreezeDebugPayload.BreezeInfo var1) {
      this.perEntity.put(var1.id(), var1);
   }
}
