package net.minecraft.gizmos;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Gizmos {
   static final ThreadLocal<@Nullable GizmoCollector> collector = new ThreadLocal();

   private Gizmos() {
      super();
   }

   public static TemporaryCollection withCollector(GizmoCollector var0) {
      TemporaryCollection var1 = new TemporaryCollection();
      collector.set(var0);
      return var1;
   }

   public static GizmoProperties addGizmo(Gizmo var0) {
      GizmoCollector var1 = (GizmoCollector)collector.get();
      if (var1 == null) {
         throw new IllegalStateException("Gizmos cannot be created here! No GizmoCollector has been registered.");
      } else {
         return var1.add(var0);
      }
   }

   public static GizmoProperties cuboid(AABB var0, GizmoStyle var1) {
      return cuboid(var0, var1, false);
   }

   public static GizmoProperties cuboid(AABB var0, GizmoStyle var1, boolean var2) {
      return addGizmo(new CuboidGizmo(var0, var1, var2));
   }

   public static GizmoProperties cuboid(BlockPos var0, GizmoStyle var1) {
      return cuboid(new AABB(var0), var1);
   }

   public static GizmoProperties cuboid(BlockPos var0, float var1, GizmoStyle var2) {
      return cuboid((new AABB(var0)).inflate((double)var1), var2);
   }

   public static GizmoProperties circle(Vec3 var0, float var1, GizmoStyle var2) {
      return addGizmo(new CircleGizmo(var0, var1, var2));
   }

   public static GizmoProperties line(Vec3 var0, Vec3 var1, int var2) {
      return addGizmo(new LineGizmo(var0, var1, var2, 3.0F));
   }

   public static GizmoProperties line(Vec3 var0, Vec3 var1, int var2, float var3) {
      return addGizmo(new LineGizmo(var0, var1, var2, var3));
   }

   public static GizmoProperties arrow(Vec3 var0, Vec3 var1, int var2) {
      return addGizmo(new ArrowGizmo(var0, var1, var2, 2.5F));
   }

   public static GizmoProperties arrow(Vec3 var0, Vec3 var1, int var2, float var3) {
      return addGizmo(new ArrowGizmo(var0, var1, var2, var3));
   }

   public static GizmoProperties rect(Vec3 var0, Vec3 var1, Direction var2, GizmoStyle var3) {
      return addGizmo(RectGizmo.fromCuboidFace(var0, var1, var2, var3));
   }

   public static GizmoProperties rect(Vec3 var0, Vec3 var1, Vec3 var2, Vec3 var3, GizmoStyle var4) {
      return addGizmo(new RectGizmo(var0, var1, var2, var3, var4));
   }

   public static GizmoProperties point(Vec3 var0, int var1, float var2) {
      return addGizmo(new PointGizmo(var0, var1, var2));
   }

   public static GizmoProperties billboardTextOverBlock(String var0, BlockPos var1, int var2, int var3, float var4) {
      double var5 = 1.3;
      double var7 = 0.2;
      GizmoProperties var9 = billboardText(var0, Vec3.atLowerCornerWithOffset(var1, 0.5, 1.3 + (double)var2 * 0.2, 0.5), TextGizmo.Style.forColorAndCentered(var3).withScale(var4));
      var9.setAlwaysOnTop();
      return var9;
   }

   public static GizmoProperties billboardTextOverMob(Entity var0, int var1, String var2, int var3, float var4) {
      double var5 = 2.4;
      double var7 = 0.25;
      double var9 = (double)var0.getBlockX() + 0.5;
      double var11 = var0.getY() + 2.4 + (double)var1 * 0.25;
      double var13 = (double)var0.getBlockZ() + 0.5;
      float var15 = 0.5F;
      GizmoProperties var16 = billboardText(var2, new Vec3(var9, var11, var13), TextGizmo.Style.forColor(var3).withScale(var4).withLeftAlignment(0.5F));
      var16.setAlwaysOnTop();
      return var16;
   }

   public static GizmoProperties billboardText(String var0, Vec3 var1, TextGizmo.Style var2) {
      return addGizmo(new TextGizmo(var1, var0, var2));
   }

   public static class TemporaryCollection implements AutoCloseable {
      private final @Nullable GizmoCollector old;
      private boolean closed;

      TemporaryCollection() {
         super();
         this.old = (GizmoCollector)Gizmos.collector.get();
      }

      public void close() {
         if (!this.closed) {
            this.closed = true;
            Gizmos.collector.set(this.old);
         }

      }
   }
}
