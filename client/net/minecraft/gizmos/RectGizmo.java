package net.minecraft.gizmos;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public record RectGizmo(Vec3 a, Vec3 b, Vec3 c, Vec3 d, GizmoStyle style) implements Gizmo {
   public RectGizmo {
      super();
   }

   public static RectGizmo fromCuboidFace(final Vec3 cuboidCornerA, final Vec3 cuboidCornerB, final Direction face, final GizmoStyle style) {
      RectGizmo var10000;
      switch (face) {
         case DOWN -> var10000 = new RectGizmo(new Vec3(cuboidCornerA.x, cuboidCornerA.y, cuboidCornerA.z), new Vec3(cuboidCornerB.x, cuboidCornerA.y, cuboidCornerA.z), new Vec3(cuboidCornerB.x, cuboidCornerA.y, cuboidCornerB.z), new Vec3(cuboidCornerA.x, cuboidCornerA.y, cuboidCornerB.z), style);
         case UP -> var10000 = new RectGizmo(new Vec3(cuboidCornerA.x, cuboidCornerB.y, cuboidCornerA.z), new Vec3(cuboidCornerA.x, cuboidCornerB.y, cuboidCornerB.z), new Vec3(cuboidCornerB.x, cuboidCornerB.y, cuboidCornerB.z), new Vec3(cuboidCornerB.x, cuboidCornerB.y, cuboidCornerA.z), style);
         case NORTH -> var10000 = new RectGizmo(new Vec3(cuboidCornerA.x, cuboidCornerA.y, cuboidCornerA.z), new Vec3(cuboidCornerA.x, cuboidCornerB.y, cuboidCornerA.z), new Vec3(cuboidCornerB.x, cuboidCornerB.y, cuboidCornerA.z), new Vec3(cuboidCornerB.x, cuboidCornerA.y, cuboidCornerA.z), style);
         case SOUTH -> var10000 = new RectGizmo(new Vec3(cuboidCornerA.x, cuboidCornerA.y, cuboidCornerB.z), new Vec3(cuboidCornerB.x, cuboidCornerA.y, cuboidCornerB.z), new Vec3(cuboidCornerB.x, cuboidCornerB.y, cuboidCornerB.z), new Vec3(cuboidCornerA.x, cuboidCornerB.y, cuboidCornerB.z), style);
         case WEST -> var10000 = new RectGizmo(new Vec3(cuboidCornerA.x, cuboidCornerA.y, cuboidCornerA.z), new Vec3(cuboidCornerA.x, cuboidCornerA.y, cuboidCornerB.z), new Vec3(cuboidCornerA.x, cuboidCornerB.y, cuboidCornerB.z), new Vec3(cuboidCornerA.x, cuboidCornerB.y, cuboidCornerA.z), style);
         case EAST -> var10000 = new RectGizmo(new Vec3(cuboidCornerB.x, cuboidCornerA.y, cuboidCornerA.z), new Vec3(cuboidCornerB.x, cuboidCornerB.y, cuboidCornerA.z), new Vec3(cuboidCornerB.x, cuboidCornerB.y, cuboidCornerB.z), new Vec3(cuboidCornerB.x, cuboidCornerA.y, cuboidCornerB.z), style);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public void emit(final GizmoPrimitives primitives, final float alphaMultiplier) {
      if (this.style.hasFill()) {
         int color = this.style.multipliedFill(alphaMultiplier);
         primitives.addQuad(this.a, this.b, this.c, this.d, color);
      }

      if (this.style.hasStroke()) {
         int color = this.style.multipliedStroke(alphaMultiplier);
         primitives.addLine(this.a, this.b, color, this.style.strokeWidth());
         primitives.addLine(this.b, this.c, color, this.style.strokeWidth());
         primitives.addLine(this.c, this.d, color, this.style.strokeWidth());
         primitives.addLine(this.d, this.a, color, this.style.strokeWidth());
      }

   }
}
