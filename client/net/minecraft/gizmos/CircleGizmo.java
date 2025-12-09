package net.minecraft.gizmos;

import net.minecraft.world.phys.Vec3;

public record CircleGizmo(Vec3 pos, float radius, GizmoStyle style) implements Gizmo {
   private static final int CIRCLE_VERTICES = 20;
   private static final float SEGMENT_SIZE_RADIANS = 0.31415927F;

   public CircleGizmo(Vec3 var1, float var2, GizmoStyle var3) {
      super();
      this.pos = var1;
      this.radius = var2;
      this.style = var3;
   }

   public void emit(GizmoPrimitives var1, float var2) {
      if (this.style.hasStroke() || this.style.hasFill()) {
         Vec3[] var3 = new Vec3[21];

         for(int var4 = 0; var4 < 20; ++var4) {
            float var5 = (float)var4 * 0.31415927F;
            Vec3 var6 = this.pos.add((double)((float)((double)this.radius * Math.cos((double)var5))), 0.0, (double)((float)((double)this.radius * Math.sin((double)var5))));
            var3[var4] = var6;
         }

         var3[20] = var3[0];
         if (this.style.hasFill()) {
            int var7 = this.style.multipliedFill(var2);
            var1.addTriangleFan(var3, var7);
         }

         if (this.style.hasStroke()) {
            int var8 = this.style.multipliedStroke(var2);

            for(int var9 = 0; var9 < 20; ++var9) {
               var1.addLine(var3[var9], var3[var9 + 1], var8, this.style.strokeWidth());
            }
         }

      }
   }
}
