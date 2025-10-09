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

   public void emit(GizmoPrimitives var1) {
      double var2 = this.aabb.minX;
      double var4 = this.aabb.minY;
      double var6 = this.aabb.minZ;
      double var8 = this.aabb.maxX;
      double var10 = this.aabb.maxY;
      double var12 = this.aabb.maxZ;
      if (this.style.hasFill()) {
         int var14 = this.style.fill();
         var1.addQuad(new Vec3(var8, var4, var6), new Vec3(var8, var10, var6), new Vec3(var8, var10, var12), new Vec3(var8, var4, var12), var14);
         var1.addQuad(new Vec3(var2, var4, var6), new Vec3(var2, var4, var12), new Vec3(var2, var10, var12), new Vec3(var2, var10, var6), var14);
         var1.addQuad(new Vec3(var2, var4, var6), new Vec3(var2, var10, var6), new Vec3(var8, var10, var6), new Vec3(var8, var4, var6), var14);
         var1.addQuad(new Vec3(var2, var4, var12), new Vec3(var8, var4, var12), new Vec3(var8, var10, var12), new Vec3(var2, var10, var12), var14);
         var1.addQuad(new Vec3(var2, var10, var6), new Vec3(var2, var10, var12), new Vec3(var8, var10, var12), new Vec3(var8, var10, var6), var14);
         var1.addQuad(new Vec3(var2, var4, var6), new Vec3(var8, var4, var6), new Vec3(var8, var4, var12), new Vec3(var2, var4, var12), var14);
      }

      if (this.style.hasStroke()) {
         int var15 = this.style.stroke();
         var1.addLine(new Vec3(var2, var4, var6), new Vec3(var8, var4, var6), this.coloredCornerStroke ? ARGB.multiply(var15, -34953) : var15, this.style.strokeWidth());
         var1.addLine(new Vec3(var2, var4, var6), new Vec3(var2, var10, var6), this.coloredCornerStroke ? ARGB.multiply(var15, -8913033) : var15, this.style.strokeWidth());
         var1.addLine(new Vec3(var2, var4, var6), new Vec3(var2, var4, var12), this.coloredCornerStroke ? ARGB.multiply(var15, -8947713) : var15, this.style.strokeWidth());
         var1.addLine(new Vec3(var8, var4, var6), new Vec3(var8, var10, var6), var15, this.style.strokeWidth());
         var1.addLine(new Vec3(var8, var10, var6), new Vec3(var2, var10, var6), var15, this.style.strokeWidth());
         var1.addLine(new Vec3(var2, var10, var6), new Vec3(var2, var10, var12), var15, this.style.strokeWidth());
         var1.addLine(new Vec3(var2, var10, var12), new Vec3(var2, var4, var12), var15, this.style.strokeWidth());
         var1.addLine(new Vec3(var2, var4, var12), new Vec3(var8, var4, var12), var15, this.style.strokeWidth());
         var1.addLine(new Vec3(var8, var4, var12), new Vec3(var8, var4, var6), var15, this.style.strokeWidth());
         var1.addLine(new Vec3(var2, var10, var12), new Vec3(var8, var10, var12), var15, this.style.strokeWidth());
         var1.addLine(new Vec3(var8, var4, var12), new Vec3(var8, var10, var12), var15, this.style.strokeWidth());
         var1.addLine(new Vec3(var8, var10, var6), new Vec3(var8, var10, var12), var15, this.style.strokeWidth());
      }

   }
}
