package net.minecraft.client.renderer.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.StructuresDebugPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class StructureRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private final Map<ResourceKey<Level>, Map<String, BoundingBox>> postMainBoxes = Maps.newIdentityHashMap();
   private final Map<ResourceKey<Level>, Map<String, StructuresDebugPayload.PieceInfo>> postPieces = Maps.newIdentityHashMap();
   private static final int MAX_RENDER_DIST = 500;

   public StructureRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      Camera var9 = this.minecraft.gameRenderer.getMainCamera();
      ResourceKey var10 = this.minecraft.level.dimension();
      BlockPos var11 = BlockPos.containing(var9.getPosition().x, 0.0, var9.getPosition().z);
      VertexConsumer var12 = var2.getBuffer(RenderType.lines());
      if (this.postMainBoxes.containsKey(var10)) {
         for(BoundingBox var14 : ((Map)this.postMainBoxes.get(var10)).values()) {
            if (var11.closerThan(var14.getCenter(), 500.0)) {
               ShapeRenderer.renderLineBox(var1, var12, (double)var14.minX() - var3, (double)var14.minY() - var5, (double)var14.minZ() - var7, (double)(var14.maxX() + 1) - var3, (double)(var14.maxY() + 1) - var5, (double)(var14.maxZ() + 1) - var7, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      Map var17 = (Map)this.postPieces.get(var10);
      if (var17 != null) {
         for(StructuresDebugPayload.PieceInfo var15 : var17.values()) {
            BoundingBox var16 = var15.boundingBox();
            if (var11.closerThan(var16.getCenter(), 500.0)) {
               if (var15.isStart()) {
                  ShapeRenderer.renderLineBox(var1, var12, (double)var16.minX() - var3, (double)var16.minY() - var5, (double)var16.minZ() - var7, (double)(var16.maxX() + 1) - var3, (double)(var16.maxY() + 1) - var5, (double)(var16.maxZ() + 1) - var7, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, 0.0F);
               } else {
                  ShapeRenderer.renderLineBox(var1, var12, (double)var16.minX() - var3, (double)var16.minY() - var5, (double)var16.minZ() - var7, (double)(var16.maxX() + 1) - var3, (double)(var16.maxY() + 1) - var5, (double)(var16.maxZ() + 1) - var7, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F);
               }
            }
         }
      }

   }

   public void addBoundingBox(BoundingBox var1, List<StructuresDebugPayload.PieceInfo> var2, ResourceKey<Level> var3) {
      ((Map)this.postMainBoxes.computeIfAbsent(var3, (var0) -> new HashMap())).put(var1.toString(), var1);
      Map var4 = (Map)this.postPieces.computeIfAbsent(var3, (var0) -> new HashMap());

      for(StructuresDebugPayload.PieceInfo var6 : var2) {
         var4.put(var6.boundingBox().toString(), var6);
      }

   }

   public void clear() {
      this.postMainBoxes.clear();
      this.postPieces.clear();
   }
}
