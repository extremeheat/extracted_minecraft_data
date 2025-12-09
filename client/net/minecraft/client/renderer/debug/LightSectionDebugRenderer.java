package net.minecraft.client.renderer.debug;

import java.time.Duration;
import java.time.Instant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import org.jspecify.annotations.Nullable;

public class LightSectionDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final Duration REFRESH_INTERVAL = Duration.ofMillis(500L);
   private static final int RADIUS = 10;
   private static final int LIGHT_AND_BLOCKS_COLOR = ARGB.colorFromFloat(0.25F, 1.0F, 1.0F, 0.0F);
   private static final int LIGHT_ONLY_COLOR = ARGB.colorFromFloat(0.125F, 0.25F, 0.125F, 0.0F);
   private final Minecraft minecraft;
   private final LightLayer lightLayer;
   private Instant lastUpdateTime = Instant.now();
   private @Nullable SectionData data;

   public LightSectionDebugRenderer(Minecraft var1, LightLayer var2) {
      super();
      this.minecraft = var1;
      this.lightLayer = var2;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      Instant var10 = Instant.now();
      if (this.data == null || Duration.between(this.lastUpdateTime, var10).compareTo(REFRESH_INTERVAL) > 0) {
         this.lastUpdateTime = var10;
         this.data = new SectionData(this.minecraft.level.getLightEngine(), SectionPos.of(this.minecraft.player.blockPosition()), 10, this.lightLayer);
      }

      renderEdges(this.data.lightAndBlocksShape, this.data.minPos, LIGHT_AND_BLOCKS_COLOR);
      renderEdges(this.data.lightShape, this.data.minPos, LIGHT_ONLY_COLOR);
      renderFaces(this.data.lightAndBlocksShape, this.data.minPos, LIGHT_AND_BLOCKS_COLOR);
      renderFaces(this.data.lightShape, this.data.minPos, LIGHT_ONLY_COLOR);
   }

   private static void renderFaces(DiscreteVoxelShape var0, SectionPos var1, int var2) {
      var0.forAllFaces((var2x, var3, var4, var5) -> {
         int var6 = var3 + var1.getX();
         int var7 = var4 + var1.getY();
         int var8 = var5 + var1.getZ();
         renderFace(var2x, var6, var7, var8, var2);
      });
   }

   private static void renderEdges(DiscreteVoxelShape var0, SectionPos var1, int var2) {
      var0.forAllEdges((var2x, var3, var4, var5, var6, var7) -> {
         int var8 = var2x + var1.getX();
         int var9 = var3 + var1.getY();
         int var10 = var4 + var1.getZ();
         int var11 = var5 + var1.getX();
         int var12 = var6 + var1.getY();
         int var13 = var7 + var1.getZ();
         renderEdge(var8, var9, var10, var11, var12, var13, var2);
      }, true);
   }

   private static void renderFace(Direction var0, int var1, int var2, int var3, int var4) {
      Vec3 var5 = new Vec3((double)SectionPos.sectionToBlockCoord(var1), (double)SectionPos.sectionToBlockCoord(var2), (double)SectionPos.sectionToBlockCoord(var3));
      Vec3 var6 = var5.add(16.0, 16.0, 16.0);
      Gizmos.rect(var5, var6, var0, GizmoStyle.fill(var4));
   }

   private static void renderEdge(int var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      double var7 = (double)SectionPos.sectionToBlockCoord(var0);
      double var9 = (double)SectionPos.sectionToBlockCoord(var1);
      double var11 = (double)SectionPos.sectionToBlockCoord(var2);
      double var13 = (double)SectionPos.sectionToBlockCoord(var3);
      double var15 = (double)SectionPos.sectionToBlockCoord(var4);
      double var17 = (double)SectionPos.sectionToBlockCoord(var5);
      int var19 = ARGB.opaque(var6);
      Gizmos.line(new Vec3(var7, var9, var11), new Vec3(var13, var15, var17), var19);
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

         for(int var6 = 0; var6 < var5; ++var6) {
            for(int var7 = 0; var7 < var5; ++var7) {
               for(int var8 = 0; var8 < var5; ++var8) {
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
