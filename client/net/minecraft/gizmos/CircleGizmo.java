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

   public void emit(GizmoPrimitives var1) {
      if (this.style.hasStroke() || this.style.hasFill()) {
         Vec3[] var2 = new Vec3[21];

         for(int var3 = 0; var3 < 20; ++var3) {
            float var4 = (float)var3 * 0.31415927F;
            Vec3 var5 = this.pos.add((double)((float)((double)this.radius * Math.cos((double)var4))), 0.0, (double)((float)((double)this.radius * Math.sin((double)var4))));
            var2[var3] = var5;
         }

         var2[20] = var2[0];
         if (this.style.hasFill()) {
            int var6 = this.style.fill();
            var1.addTriangleFan(var2, var6);
         }

         if (this.style.hasStroke()) {
            int var7 = this.style.stroke();

            for(int var8 = 0; var8 < 20; ++var8) {
               var1.addLine(var2[var8], var2[var8 + 1], var7, this.style.strokeWidth());
            }
         }

      }
   }
}
