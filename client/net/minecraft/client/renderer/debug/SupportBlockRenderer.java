package net.minecraft.client.renderer.debug;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleSupplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SupportBlockRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;
   private double lastUpdateTime = 4.9E-324;
   private List<Entity> surroundEntities = Collections.emptyList();

   public SupportBlockRenderer(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void emitGizmos(double var1, double var3, double var5, DebugValueAccess var7, Frustum var8, float var9) {
      double var10 = (double)Util.getNanos();
      if (var10 - this.lastUpdateTime > 1.0E8) {
         this.lastUpdateTime = var10;
         Entity var12 = this.minecraft.gameRenderer.getMainCamera().entity();
         this.surroundEntities = ImmutableList.copyOf(var12.level().getEntities(var12, var12.getBoundingBox().inflate(16.0)));
      }

      LocalPlayer var15 = this.minecraft.player;
      if (var15 != null && var15.mainSupportingBlockPos.isPresent()) {
         this.drawHighlights(var15, () -> 0.0, -65536);
      }

      for(Entity var14 : this.surroundEntities) {
         if (var14 != var15) {
            this.drawHighlights(var14, () -> this.getBias(var14), -16711936);
         }
      }

   }

   private void drawHighlights(Entity var1, DoubleSupplier var2, int var3) {
      var1.mainSupportingBlockPos.ifPresent((var4) -> {
         double var5 = var2.getAsDouble();
         BlockPos var7 = var1.getOnPos();
         this.highlightPosition(var7, 0.02 + var5, var3);
         BlockPos var8 = var1.getOnPosLegacy();
         if (!var8.equals(var7)) {
            this.highlightPosition(var8, 0.04 + var5, -16711681);
         }

      });
   }

   private double getBias(Entity var1) {
      return 0.02 * (double)(String.valueOf((double)var1.getId() + 0.132453657).hashCode() % 1000) / 1000.0;
   }

   private void highlightPosition(BlockPos var1, double var2, int var4) {
      double var5 = (double)var1.getX() - 2.0 * var2;
      double var7 = (double)var1.getY() - 2.0 * var2;
      double var9 = (double)var1.getZ() - 2.0 * var2;
      double var11 = var5 + 1.0 + 4.0 * var2;
      double var13 = var7 + 1.0 + 4.0 * var2;
      double var15 = var9 + 1.0 + 4.0 * var2;
      Gizmos.cuboid(new AABB(var5, var7, var9, var11, var13, var15), GizmoStyle.stroke(ARGB.color(0.4F, var4)));
      VoxelShape var17 = this.minecraft.level.getBlockState(var1).getCollisionShape(this.minecraft.level, var1, CollisionContext.empty()).move((Vec3i)var1);
      GizmoStyle var18 = GizmoStyle.stroke(var4);

      for(AABB var20 : var17.toAabbs()) {
         Gizmos.cuboid(var20, var18);
      }

   }
}
