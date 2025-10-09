package net.minecraft.gizmos;

import net.minecraft.world.phys.Vec3;

public record PointGizmo(Vec3 pos, int color, float size) implements Gizmo {
   public PointGizmo(Vec3 var1, int var2, float var3) {
      super();
      this.pos = var1;
      this.color = var2;
      this.size = var3;
   }

   public void emit(GizmoPrimitives var1) {
      var1.addPoint(this.pos, this.color, this.size);
   }
}
