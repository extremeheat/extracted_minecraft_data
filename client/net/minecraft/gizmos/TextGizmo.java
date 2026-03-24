package net.minecraft.gizmos;

import java.util.OptionalDouble;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;

public record TextGizmo(Vec3 pos, String text, Style style) implements Gizmo {
   public TextGizmo {
      super();
   }

   public void emit(final GizmoPrimitives primitives, final float alphaMultiplier) {
      Style newStyle;
      if (alphaMultiplier < 1.0F) {
         newStyle = new Style(ARGB.multiplyAlpha(this.style.color, alphaMultiplier), this.style.scale, this.style.adjustLeft);
      } else {
         newStyle = this.style;
      }

      primitives.addText(this.pos, this.text, newStyle);
   }

   public static record Style(int color, float scale, OptionalDouble adjustLeft) {
      public static final float DEFAULT_SCALE = 0.32F;

      public Style {
         super();
      }

      public static Style whiteAndCentered() {
         return new Style(-1, 0.32F, OptionalDouble.empty());
      }

      public static Style forColorAndCentered(final int argb) {
         return new Style(argb, 0.32F, OptionalDouble.empty());
      }

      public static Style forColor(final int argb) {
         return new Style(argb, 0.32F, OptionalDouble.of(0.0));
      }

      public Style withScale(final float scale) {
         return new Style(this.color, scale, this.adjustLeft);
      }

      public Style withLeftAlignment(final float adjustLeft) {
         return new Style(this.color, this.scale, OptionalDouble.of((double)adjustLeft));
      }
   }
}
