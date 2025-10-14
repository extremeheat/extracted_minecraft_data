package net.minecraft.gizmos;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public record RectGizmo(Vec3 a, Vec3 b, Vec3 c, Vec3 d, GizmoStyle style) implements Gizmo {
   public RectGizmo(Vec3 var1, Vec3 var2, Vec3 var3, Vec3 var4, GizmoStyle var5) {
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.style = var5;
   }

   public static RectGizmo fromCuboidFace(Vec3 var0, Vec3 var1, Direction var2, GizmoStyle var3) {
      RectGizmo var10000;
      switch (var2) {
         case DOWN -> var10000 = new RectGizmo(new Vec3(var0.x, var0.y, var0.z), new Vec3(var1.x, var0.y, var0.z), new Vec3(var1.x, var0.y, var1.z), new Vec3(var0.x, var0.y, var1.z), var3);
         case UP -> var10000 = new RectGizmo(new Vec3(var0.x, var1.y, var0.z), new Vec3(var0.x, var1.y, var1.z), new Vec3(var1.x, var1.y, var1.z), new Vec3(var1.x, var1.y, var0.z), var3);
         case NORTH -> var10000 = new RectGizmo(new Vec3(var0.x, var0.y, var0.z), new Vec3(var0.x, var1.y, var0.z), new Vec3(var1.x, var1.y, var0.z), new Vec3(var1.x, var0.y, var0.z), var3);
         case SOUTH -> var10000 = new RectGizmo(new Vec3(var0.x, var0.y, var1.z), new Vec3(var1.x, var0.y, var1.z), new Vec3(var1.x, var1.y, var1.z), new Vec3(var0.x, var1.y, var1.z), var3);
         case WEST -> var10000 = new RectGizmo(new Vec3(var0.x, var0.y, var0.z), new Vec3(var0.x, var0.y, var1.z), new Vec3(var0.x, var1.y, var1.z), new Vec3(var0.x, var1.y, var0.z), var3);
         case EAST -> var10000 = new RectGizmo(new Vec3(var1.x, var0.y, var0.z), new Vec3(var1.x, var1.y, var0.z), new Vec3(var1.x, var1.y, var1.z), new Vec3(var1.x, var0.y, var1.z), var3);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void emit(GizmoPrimitives var1, float var2) {
      if (this.style.hasFill()) {
         int var3 = this.style.multipliedFill(var2);
         var1.addQuad(this.a, this.b, this.c, this.d, var3);
      }

      if (this.style.hasStroke()) {
         int var4 = this.style.multipliedStroke(var2);
         var1.addLine(this.a, this.b, var4, this.style.strokeWidth());
         var1.addLine(this.b, this.c, var4, this.style.strokeWidth());
         var1.addLine(this.c, this.d, var4, this.style.strokeWidth());
         var1.addLine(this.d, this.a, var4, this.style.strokeWidth());
      }

   }
}
