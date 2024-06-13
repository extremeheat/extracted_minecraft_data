package net.minecraft.client.renderer.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class LightSectionDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final Duration REFRESH_INTERVAL = Duration.ofMillis(500L);
   private static final int RADIUS = 10;
   private static final Vector4f LIGHT_AND_BLOCKS_COLOR = new Vector4f(1.0F, 1.0F, 0.0F, 0.25F);
   private static final Vector4f LIGHT_ONLY_COLOR = new Vector4f(0.25F, 0.125F, 0.0F, 0.125F);
   private final Minecraft minecraft;
   private final LightLayer lightLayer;
   private Instant lastUpdateTime = Instant.now();
   @Nullable
   private LightSectionDebugRenderer.SectionData data;

   public LightSectionDebugRenderer(Minecraft var1, LightLayer var2) {
      super();
      this.minecraft = var1;
      this.lightLayer = var2;
   }

   @Override
   public void render(PoseStack var1, MultiBufferSource var2, double var3, double var5, double var7) {
      Instant var9 = Instant.now();
      if (this.data == null || Duration.between(this.lastUpdateTime, var9).compareTo(REFRESH_INTERVAL) > 0) {
         this.lastUpdateTime = var9;
         this.data = new LightSectionDebugRenderer.SectionData(
            this.minecraft.level.getLightEngine(), SectionPos.of(this.minecraft.player.blockPosition()), 10, this.lightLayer
         );
      }

      renderEdges(var1, this.data.lightAndBlocksShape, this.data.minPos, var2, var3, var5, var7, LIGHT_AND_BLOCKS_COLOR);
      renderEdges(var1, this.data.lightShape, this.data.minPos, var2, var3, var5, var7, LIGHT_ONLY_COLOR);
      VertexConsumer var10 = var2.getBuffer(RenderType.debugSectionQuads());
      renderFaces(var1, this.data.lightAndBlocksShape, this.data.minPos, var10, var3, var5, var7, LIGHT_AND_BLOCKS_COLOR);
      renderFaces(var1, this.data.lightShape, this.data.minPos, var10, var3, var5, var7, LIGHT_ONLY_COLOR);
   }

   private static void renderFaces(
      PoseStack var0, DiscreteVoxelShape var1, SectionPos var2, VertexConsumer var3, double var4, double var6, double var8, Vector4f var10
   ) {
      var1.forAllFaces((var10x, var11, var12, var13) -> {
         int var14 = var11 + var2.getX();
         int var15 = var12 + var2.getY();
         int var16 = var13 + var2.getZ();
         renderFace(var0, var3, var10x, var4, var6, var8, var14, var15, var16, var10);
      });
   }

   private static void renderEdges(
      PoseStack var0, DiscreteVoxelShape var1, SectionPos var2, MultiBufferSource var3, double var4, double var6, double var8, Vector4f var10
   ) {
      var1.forAllEdges((var10x, var11, var12, var13, var14, var15) -> {
         int var16 = var10x + var2.getX();
         int var17 = var11 + var2.getY();
         int var18 = var12 + var2.getZ();
         int var19 = var13 + var2.getX();
         int var20 = var14 + var2.getY();
         int var21 = var15 + var2.getZ();
         VertexConsumer var22 = var3.getBuffer(RenderType.debugLineStrip(1.0));
         renderEdge(var0, var22, var4, var6, var8, var16, var17, var18, var19, var20, var21, var10);
      }, true);
   }

   private static void renderFace(
      PoseStack var0, VertexConsumer var1, Direction var2, double var3, double var5, double var7, int var9, int var10, int var11, Vector4f var12
   ) {
      float var13 = (float)((double)SectionPos.sectionToBlockCoord(var9) - var3);
      float var14 = (float)((double)SectionPos.sectionToBlockCoord(var10) - var5);
      float var15 = (float)((double)SectionPos.sectionToBlockCoord(var11) - var7);
      float var16 = var13 + 16.0F;
      float var17 = var14 + 16.0F;
      float var18 = var15 + 16.0F;
      float var19 = var12.x();
      float var20 = var12.y();
      float var21 = var12.z();
      float var22 = var12.w();
      Matrix4f var23 = var0.last().pose();
      switch (var2) {
         case DOWN:
            var1.addVertex(var23, var13, var14, var15).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var16, var14, var15).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var16, var14, var18).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var13, var14, var18).setColor(var19, var20, var21, var22);
            break;
         case UP:
            var1.addVertex(var23, var13, var17, var15).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var13, var17, var18).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var16, var17, var18).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var16, var17, var15).setColor(var19, var20, var21, var22);
            break;
         case NORTH:
            var1.addVertex(var23, var13, var14, var15).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var13, var17, var15).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var16, var17, var15).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var16, var14, var15).setColor(var19, var20, var21, var22);
            break;
         case SOUTH:
            var1.addVertex(var23, var13, var14, var18).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var16, var14, var18).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var16, var17, var18).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var13, var17, var18).setColor(var19, var20, var21, var22);
            break;
         case WEST:
            var1.addVertex(var23, var13, var14, var15).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var13, var14, var18).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var13, var17, var18).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var13, var17, var15).setColor(var19, var20, var21, var22);
            break;
         case EAST:
            var1.addVertex(var23, var16, var14, var15).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var16, var17, var15).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var16, var17, var18).setColor(var19, var20, var21, var22);
            var1.addVertex(var23, var16, var14, var18).setColor(var19, var20, var21, var22);
      }
   }

   private static void renderEdge(
      PoseStack var0,
      VertexConsumer var1,
      double var2,
      double var4,
      double var6,
      int var8,
      int var9,
      int var10,
      int var11,
      int var12,
      int var13,
      Vector4f var14
   ) {
      float var15 = (float)((double)SectionPos.sectionToBlockCoord(var8) - var2);
      float var16 = (float)((double)SectionPos.sectionToBlockCoord(var9) - var4);
      float var17 = (float)((double)SectionPos.sectionToBlockCoord(var10) - var6);
      float var18 = (float)((double)SectionPos.sectionToBlockCoord(var11) - var2);
      float var19 = (float)((double)SectionPos.sectionToBlockCoord(var12) - var4);
      float var20 = (float)((double)SectionPos.sectionToBlockCoord(var13) - var6);
      Matrix4f var21 = var0.last().pose();
      var1.addVertex(var21, var15, var16, var17).setColor(var14.x(), var14.y(), var14.z(), 1.0F);
      var1.addVertex(var21, var18, var19, var20).setColor(var14.x(), var14.y(), var14.z(), 1.0F);
   }

   static final class SectionData {
      final DiscreteVoxelShape lightAndBlocksShape;
      final DiscreteVoxelShape lightShape;
      final SectionPos minPos;

      SectionData(LevelLightEngine var1, SectionPos var2, int var3, LightLayer var4) {
         super();
         int var5 = var3 * 2 + 1;
         this.lightAndBlocksShape = new BitSetDiscreteVoxelShape(var5, var5, var5);
         this.lightShape = new BitSetDiscreteVoxelShape(var5, var5, var5);

         for (int var6 = 0; var6 < var5; var6++) {
            for (int var7 = 0; var7 < var5; var7++) {
               for (int var8 = 0; var8 < var5; var8++) {
                  SectionPos var9 = SectionPos.of(var2.x() + var8 - var3, var2.y() + var7 - var3, var2.z() + var6 - var3);
                  LayerLightSectionStorage.SectionType var10 = var1.getDebugSectionType(var4, var9);
                  if (var10 == LayerLightSectionStorage.SectionType.LIGHT_AND_DATA) {
                     this.lightAndBlocksShape.fill(var8, var7, var6);
                     this.lightShape.fill(var8, var7, var6);
                  } else if (var10 == LayerLightSectionStorage.SectionType.LIGHT_ONLY) {
                     this.lightShape.fill(var8, var7, var6);
                  }
               }
            }
         }

         this.minPos = SectionPos.of(var2.x() - var3, var2.y() - var3, var2.z() - var3);
      }
   }
}
