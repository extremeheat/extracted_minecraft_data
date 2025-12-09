package net.minecraft.gizmos;

import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;

public record LineGizmo(Vec3 start, Vec3 end, int color, float width) implements Gizmo {
   public static final float DEFAULT_WIDTH = 3.0F;

   public LineGizmo(Vec3 var1, Vec3 var2, int var3, float var4) {
      super();
      this.start = var1;
      this.end = var2;
      this.color = var3;
      this.width = var4;
   }

   public void emit(GizmoPrimitives var1, float var2) {
      var1.addLine(this.start, this.end, ARGB.multiplyAlpha(this.color, var2), this.width);
   }
}
