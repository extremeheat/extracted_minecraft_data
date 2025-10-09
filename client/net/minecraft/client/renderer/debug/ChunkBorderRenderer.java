package net.minecraft.client.renderer.debug;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ChunkBorderRenderer implements DebugRenderer.SimpleDebugRenderer {
   private static final float THICK_WIDTH = 4.0F;
   private static final float THIN_WIDTH = 1.0F;
   private final Minecraft minecraft;
   private static final int CELL_BORDER = ARGB.color(255, 0, 155, 155);
   private static final int YELLOW = ARGB.color(255, 255, 255, 0);
   private static final int MAJOR_LINES = ARGB.colorFromFloat(1.0F, 0.25F, 0.25F, 1.0F);

   public ChunkBorderRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      Entity var10 = this.minecraft.gameRenderer.getMainCamera().entity();
      float var11 = (float)this.minecraft.level.getMinY();
      float var12 = (float)(this.minecraft.level.getMaxY() + 1);
      SectionPos var13 = SectionPos.of(var10.blockPosition());
      double var14 = (double)var13.minBlockX();
      double var16 = (double)var13.minBlockZ();

      for(int var18 = -16; var18 <= 32; var18 += 16) {
         for(int var19 = -16; var19 <= 32; var19 += 16) {
            Gizmos.line(new Vec3(var14 + (double)var18, (double)var11, var16 + (double)var19), new Vec3(var14 + (double)var18, (double)var12, var16 + (double)var19), ARGB.colorFromFloat(0.5F, 1.0F, 0.0F, 0.0F), 4.0F);
         }
      }

      for(int var21 = 2; var21 < 16; var21 += 2) {
         int var26 = var21 % 4 == 0 ? CELL_BORDER : YELLOW;
         Gizmos.line(new Vec3(var14 + (double)var21, (double)var11, var16), new Vec3(var14 + (double)var21, (double)var12, var16), var26, 1.0F);
         Gizmos.line(new Vec3(var14 + (double)var21, (double)var11, var16 + 16.0), new Vec3(var14 + (double)var21, (double)var12, var16 + 16.0), var26, 1.0F);
      }

      for(int var22 = 2; var22 < 16; var22 += 2) {
         int var27 = var22 % 4 == 0 ? CELL_BORDER : YELLOW;
         Gizmos.line(new Vec3(var14, (double)var11, var16 + (double)var22), new Vec3(var14, (double)var12, var16 + (double)var22), var27, 1.0F);
         Gizmos.line(new Vec3(var14 + 16.0, (double)var11, var16 + (double)var22), new Vec3(var14 + 16.0, (double)var12, var16 + (double)var22), var27, 1.0F);
      }

      for(int var23 = this.minecraft.level.getMinY(); var23 <= this.minecraft.level.getMaxY() + 1; var23 += 2) {
         float var28 = (float)var23;
         int var20 = var23 % 8 == 0 ? CELL_BORDER : YELLOW;
         Gizmos.line(new Vec3(var14, (double)var28, var16), new Vec3(var14, (double)var28, var16 + 16.0), var20, 1.0F);
         Gizmos.line(new Vec3(var14, (double)var28, var16 + 16.0), new Vec3(var14 + 16.0, (double)var28, var16 + 16.0), var20, 1.0F);
         Gizmos.line(new Vec3(var14 + 16.0, (double)var28, var16 + 16.0), new Vec3(var14 + 16.0, (double)var28, var16), var20, 1.0F);
         Gizmos.line(new Vec3(var14 + 16.0, (double)var28, var16), new Vec3(var14, (double)var28, var16), var20, 1.0F);
      }

      for(int var24 = 0; var24 <= 16; var24 += 16) {
         for(int var29 = 0; var29 <= 16; var29 += 16) {
            Gizmos.line(new Vec3(var14 + (double)var24, (double)var11, var16 + (double)var29), new Vec3(var14 + (double)var24, (double)var12, var16 + (double)var29), MAJOR_LINES, 4.0F);
         }
      }

      Gizmos.cuboid(new AABB((double)var13.minBlockX(), (double)var13.minBlockY(), (double)var13.minBlockZ(), (double)(var13.maxBlockX() + 1), (double)(var13.maxBlockY() + 1), (double)(var13.maxBlockZ() + 1)), GizmoStyle.stroke(MAJOR_LINES, 1.0F)).setAlwaysOnTop();

      for(int var25 = this.minecraft.level.getMinY(); var25 <= this.minecraft.level.getMaxY() + 1; var25 += 16) {
         Gizmos.line(new Vec3(var14, (double)var25, var16), new Vec3(var14, (double)var25, var16 + 16.0), MAJOR_LINES, 4.0F);
         Gizmos.line(new Vec3(var14, (double)var25, var16 + 16.0), new Vec3(var14 + 16.0, (double)var25, var16 + 16.0), MAJOR_LINES, 4.0F);
         Gizmos.line(new Vec3(var14 + 16.0, (double)var25, var16 + 16.0), new Vec3(var14 + 16.0, (double)var25, var16), MAJOR_LINES, 4.0F);
         Gizmos.line(new Vec3(var14 + 16.0, (double)var25, var16), new Vec3(var14, (double)var25, var16), MAJOR_LINES, 4.0F);
      }

   }
}
