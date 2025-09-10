package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.phys.AABB;

public class NeighborsUpdateRenderer implements DebugRenderer.SimpleDebugRenderer {
   public NeighborsUpdateRenderer() {
      super();
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7, DebugValueAccess var9) {
      int var10 = DebugSubscriptions.NEIGHBOR_UPDATES.expireAfterTicks();
      double var11 = 1.0 / (double)(var10 * 2);
      HashMap var13 = new HashMap();
      var9.forEachEvent(DebugSubscriptions.NEIGHBOR_UPDATES, (var1x, var2x, var3x) -> {
         long var4 = (long)(var3x - var2x);
         LastUpdate var6 = (LastUpdate)var13.getOrDefault(var1x, NeighborsUpdateRenderer.LastUpdate.NONE);
         var13.put(var1x, var6.tryCount((int)var4));
      });
      VertexConsumer var14 = var2.getBuffer(RenderType.lines());

      for(Map.Entry var16 : var13.entrySet()) {
         BlockPos var17 = (BlockPos)var16.getKey();
         LastUpdate var18 = (LastUpdate)var16.getValue();
         AABB var19 = (new AABB(BlockPos.ZERO)).inflate(0.002).deflate(var11 * (double)var18.age).move((double)var17.getX(), (double)var17.getY(), (double)var17.getZ()).move(-var3, -var5, -var7);
         ShapeRenderer.renderLineBox(var1.last(), var14, var19.minX, var19.minY, var19.minZ, var19.maxX, var19.maxY, var19.maxZ, 1.0F, 1.0F, 1.0F, 1.0F);
      }

      for(Map.Entry var21 : var13.entrySet()) {
         BlockPos var22 = (BlockPos)var21.getKey();
         LastUpdate var23 = (LastUpdate)var21.getValue();
         DebugRenderer.renderFloatingText(var1, var2, String.valueOf(var23.count), var22.getX(), var22.getY(), var22.getZ(), -1);
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
