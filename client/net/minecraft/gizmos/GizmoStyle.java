package net.minecraft.gizmos;

public record GizmoStyle(int stroke, float strokeWidth, int fill) {
   private static final float DEFAULT_WIDTH = 2.5F;

   public GizmoStyle(int var1, float var2, int var3) {
      super();
      this.stroke = var1;
      this.strokeWidth = var2;
      this.fill = var3;
   }

   public static GizmoStyle stroke(int var0) {
      return new GizmoStyle(var0, 2.5F, 0);
   }

   public static GizmoStyle stroke(int var0, float var1) {
      return new GizmoStyle(var0, var1, 0);
   }

   public static GizmoStyle fill(int var0) {
      return new GizmoStyle(0, 0.0F, var0);
   }

   public static GizmoStyle strokeAndFill(int var0, float var1, int var2) {
      return new GizmoStyle(var0, var1, var2);
   }

   public boolean hasFill() {
      return this.fill != 0;
   }

   public boolean hasStroke() {
      return this.stroke != 0 && this.strokeWidth > 0.0F;
   }
}
