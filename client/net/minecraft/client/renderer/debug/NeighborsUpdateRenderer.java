package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;

public class NeighborsUpdateRenderer implements DebugRenderer.SimpleDebugRenderer {
   public NeighborsUpdateRenderer() {
      super();
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9, Frustum var10) {
      int var11 = DebugSubscriptions.NEIGHBOR_UPDATES.expireAfterTicks();
      double var12 = 1.0 / (double)(var11 * 2);
      HashMap var14 = new HashMap();
      var9.forEachEvent(DebugSubscriptions.NEIGHBOR_UPDATES, (var1x, var2x, var3x) -> {
         long var4 = (long)(var3x - var2x);
         LastUpdate var6 = (LastUpdate)var14.getOrDefault(var1x, NeighborsUpdateRenderer.LastUpdate.NONE);
         var14.put(var1x, var6.tryCount((int)var4));
      });
      VertexConsumer var15 = var2.getBuffer(RenderType.lines());

      for(Map.Entry var17 : var14.entrySet()) {
         BlockPos var18 = (BlockPos)var17.getKey();
         LastUpdate var19 = (LastUpdate)var17.getValue();
         AABB var20 = (new AABB(BlockPos.ZERO)).inflate(0.002).deflate(var12 * (double)var19.age).move((double)var18.getX(), (double)var18.getY(), (double)var18.getZ()).move(-var3, -var5, -var7);
         ShapeRenderer.renderLineBox(var1.last(), var15, var20.minX, var20.minY, var20.minZ, var20.maxX, var20.maxY, var20.maxZ, 1.0F, 1.0F, 1.0F, 1.0F);
      }

      for(Map.Entry var22 : var14.entrySet()) {
         BlockPos var23 = (BlockPos)var22.getKey();
         LastUpdate var24 = (LastUpdate)var22.getValue();
         DebugRenderer.renderFloatingText(var1, var2, String.valueOf(var24.count), var23.getX(), var23.getY(), var23.getZ(), -1);
      }

   }

   static record LastUpdate(int count, int age) {
      final int count;
      final int age;
      static final LastUpdate NONE = new LastUpdate(0, 2147483647);

      private LastUpdate(int var1, int var2) {
         super();
         this.count = var1;
         this.age = var2;
      }

      public LastUpdate tryCount(int var1) {
         if (var1 == this.age) {
            return new LastUpdate(this.count + 1, var1);
         } else {
            return var1 < this.age ? new LastUpdate(1, var1) : this;
         }
      }
   }
}
