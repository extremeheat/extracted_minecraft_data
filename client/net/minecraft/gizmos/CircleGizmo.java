package net.minecraft.gizmos;

import net.minecraft.world.phys.Vec3;

public record CircleGizmo(Vec3 pos, float radius, Plane plane, GizmoStyle style) implements Gizmo {
   private static final int CIRCLE_VERTICES = 20;
   private static final float SEGMENT_SIZE_RADIANS = 0.31415927F;

   public CircleGizmo {
      super();
   }

   public void emit(final GizmoPrimitives primitives, final float alphaMultiplier) {
      if (this.style.hasStroke() || this.style.hasFill()) {
         Vec3[] points = new Vec3[21];

         for(int i = 0; i < 20; ++i) {
            float theta = (float)i * 0.31415927F;
            Vec3 var10000;
            switch (this.plane.ordinal()) {
               case 0 -> var10000 = this.pos.add((double)((float)((double)this.radius * Math.cos((double)theta))), 0.0, (double)((float)((double)this.radius * Math.sin((double)theta))));
               case 1 -> var10000 = this.pos.add((double)((float)((double)this.radius * Math.cos((double)theta))), (double)((float)((double)this.radius * Math.sin((double)theta))), 0.0);
               case 2 -> var10000 = this.pos.add(0.0, (double)((float)((double)this.radius * Math.cos((double)theta))), (double)((float)((double)this.radius * Math.sin((double)theta))));
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            Vec3 point = var10000;
            points[i] = point;
         }

         points[20] = points[0];
         if (this.style.hasFill()) {
            int color = this.style.multipliedFill(alphaMultiplier);
            primitives.addTriangleFan(points, color);
         }

         if (this.style.hasStroke()) {
            int color = this.style.multipliedStroke(alphaMultiplier);

            for(int i = 0; i < 20; ++i) {
               primitives.addLine(points[i], points[i + 1], color, this.style.strokeWidth());
            }
         }

      }
   }

   public static enum Plane {
      XZ,
      XY,
      YZ;

      private Plane() {
      }

      // $FF: synthetic method
      private static Plane[] $values() {
         return new Plane[]{XZ, XY, YZ};
      }
   }
}
