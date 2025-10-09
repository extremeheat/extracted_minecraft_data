package net.minecraft.gizmos;

import java.util.OptionalDouble;
import net.minecraft.world.phys.Vec3;

public record TextGizmo(Vec3 pos, String text, Style style) implements Gizmo {
   public TextGizmo(Vec3 var1, String var2, Style var3) {
      super();
      this.pos = var1;
      this.text = var2;
      this.style = var3;
   }

   public void emit(GizmoPrimitives var1) {
      var1.addText(this.pos, this.text, this.style);
   }

   public static record Style(int color, float scale, OptionalDouble adjustLeft) {
      public static final float DEFAULT_SCALE = 0.32F;

      public Style(int var1, float var2, OptionalDouble var3) {
         super();
         this.color = var1;
         this.scale = var2;
         this.adjustLeft = var3;
      }

      public static Style whiteAndCentered() {
         return new Style(-1, 0.32F, OptionalDouble.empty());
      }

      public static Style forColorAndCentered(int var0) {
         return new Style(var0, 0.32F, OptionalDouble.empty());
      }

      public static Style forColor(int var0) {
         return new Style(var0, 0.32F, OptionalDouble.of(0.0));
      }

      public Style withScale(float var1) {
         return new Style(this.color, var1, this.adjustLeft);
      }

      public Style withLeftAlignment(float var1) {
         return new Style(this.color, this.scale, OptionalDouble.of((double)var1));
      }
   }
}
