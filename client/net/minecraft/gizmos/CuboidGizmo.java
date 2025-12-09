package net.minecraft.gizmos;

import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record CuboidGizmo(AABB aabb, GizmoStyle style, boolean coloredCornerStroke) implements Gizmo {
   public CuboidGizmo(AABB var1, GizmoStyle var2, boolean var3) {
      super();
      this.aabb = var1;
      this.style = var2;
      this.coloredCornerStroke = var3;
   }

   public void emit(GizmoPrimitives var1, float var2) {
      double var3 = this.aabb.minX;
      double var5 = this.aabb.minY;
      double var7 = this.aabb.minZ;
      double var9 = this.aabb.maxX;
      double var11 = this.aabb.maxY;
      double var13 = this.aabb.maxZ;
      if (this.style.hasFill()) {
         int var15 = this.style.multipliedFill(var2);
         var1.addQuad(new Vec3(var9, var5, var7), new Vec3(var9, var11, var7), new Vec3(var9, var11, var13), new Vec3(var9, var5, var13), var15);
         var1.addQuad(new Vec3(var3, var5, var7), new Vec3(var3, var5, var13), new Vec3(var3, var11, var13), new Vec3(var3, var11, var7), var15);
         var1.addQuad(new Vec3(var3, var5, var7), new Vec3(var3, var11, var7), new Vec3(var9, var11, var7), new Vec3(var9, var5, var7), var15);
         var1.addQuad(new Vec3(var3, var5, var13), new Vec3(var9, var5, var13), new Vec3(var9, var11, var13), new Vec3(var3, var11, var13), var15);
         var1.addQuad(new Vec3(var3, var11, var7), new Vec3(var3, var11, var13), new Vec3(var9, var11, var13), new Vec3(var9, var11, var7), var15);
         var1.addQuad(new Vec3(var3, var5, var7), new Vec3(var9, var5, var7), new Vec3(var9, var5, var13), new Vec3(var3, var5, var13), var15);
      }

      if (this.style.hasStroke()) {
         int var16 = this.style.multipliedStroke(var2);
         var1.addLine(new Vec3(var3, var5, var7), new Vec3(var9, var5, var7), this.coloredCornerStroke ? ARGB.multiply(var16, -34953) : var16, this.style.strokeWidth());
         var1.addLine(new Vec3(var3, var5, var7), new Vec3(var3, var11, var7), this.coloredCornerStroke ? ARGB.multiply(var16, -8913033) : var16, this.style.strokeWidth());
         var1.addLine(new Vec3(var3, var5, var7), new Vec3(var3, var5, var13), this.coloredCornerStroke ? ARGB.multiply(var16, -8947713) : var16, this.style.strokeWidth());
         var1.addLine(new Vec3(var9, var5, var7), new Vec3(var9, var11, var7), var16, this.style.strokeWidth());
         var1.addLine(new Vec3(var9, var11, var7), new Vec3(var3, var11, var7), var16, this.style.strokeWidth());
         var1.addLine(new Vec3(var3, var11, var7), new Vec3(var3, var11, var13), var16, this.style.strokeWidth());
         var1.addLine(new Vec3(var3, var11, var13), new Vec3(var3, var5, var13), var16, this.style.strokeWidth());
         var1.addLine(new Vec3(var3, var5, var13), new Vec3(var9, var5, var13), var16, this.style.strokeWidth());
         var1.addLine(new Vec3(var9, var5, var13), new Vec3(var9, var5, var7), var16, this.style.strokeWidth());
         var1.addLine(new Vec3(var3, var11, var13), new Vec3(var9, var11, var13), var16, this.style.strokeWidth());
         var1.addLine(new Vec3(var9, var5, var13), new Vec3(var9, var11, var13), var16, this.style.strokeWidth());
         var1.addLine(new Vec3(var9, var11, var7), new Vec3(var9, var11, var13), var16, this.style.strokeWidth());
      }

   }
}
