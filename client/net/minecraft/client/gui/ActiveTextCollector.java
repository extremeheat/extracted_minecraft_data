package net.minecraft.client.gui;

import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.gui.font.ActiveArea;
import net.minecraft.client.gui.font.EmptyArea;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiTextRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;

public interface ActiveTextCollector {
   double PERIOD_PER_SCROLLED_PIXEL = 0.5;
   double MIN_SCROLL_PERIOD = 3.0;

   Parameters defaultParameters();

   void defaultParameters(Parameters var1);

   default void accept(int var1, int var2, FormattedCharSequence var3) {
      this.accept(TextAlignment.LEFT, var1, var2, this.defaultParameters(), var3);
   }

   default void accept(int var1, int var2, Component var3) {
      this.accept(TextAlignment.LEFT, var1, var2, this.defaultParameters(), var3.getVisualOrderText());
   }

   default void accept(TextAlignment var1, int var2, int var3, Parameters var4, Component var5) {
      this.accept(var1, var2, var3, var4, var5.getVisualOrderText());
   }

   void accept(TextAlignment var1, int var2, int var3, Parameters var4, FormattedCharSequence var5);

   default void accept(TextAlignment var1, int var2, int var3, Component var4) {
      this.accept(var1, var2, var3, var4.getVisualOrderText());
   }

   default void accept(TextAlignment var1, int var2, int var3, FormattedCharSequence var4) {
      this.accept(var1, var2, var3, this.defaultParameters(), var4);
   }

   void acceptScrolling(Component var1, int var2, int var3, int var4, int var5, int var6, Parameters var7);

   default void acceptScrolling(Component var1, int var2, int var3, int var4, int var5, int var6) {
      this.acceptScrolling(var1, var2, var3, var4, var5, var6, this.defaultParameters());
   }

   default void acceptScrollingWithDefaultCenter(Component var1, int var2, int var3, int var4, int var5) {
      this.acceptScrolling(var1, (var2 + var3) / 2, var2, var3, var4, var5);
   }

   default void defaultScrollingHelper(Component var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, Parameters var9) {
      int var10 = (var5 + var6 - var8) / 2 + 1;
      int var11 = var4 - var3;
      if (var7 > var11) {
         int var12 = var7 - var11;
         double var13 = (double)Util.getMillis() / 1000.0;
         double var15 = Math.max((double)var12 * 0.5, 3.0);
         double var17 = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * var13 / var15)) / 2.0 + 0.5;
         double var19 = Mth.lerp(var17, 0.0, (double)var12);
         Parameters var21 = var9.withScissor(var3, var4, var5, var6);
         this.accept(TextAlignment.LEFT, var3 - (int)var19, var10, var21, var1.getVisualOrderText());
      } else {
         int var22 = Mth.clamp(var2, var3 + var7 / 2, var4 - var7 / 2);
         this.accept(TextAlignment.CENTER, var22, var10, var1);
      }

   }

   static void findElementUnderCursor(GuiTextRenderState var0, float var1, float var2, final Consumer<Style> var3) {
      ScreenRectangle var4 = var0.bounds();
      if (var4 != null && var4.containsPoint((int)var1, (int)var2)) {
         Vector2f var5 = var0.pose.invert(new Matrix3x2f()).transformPosition(new Vector2f(var1, var2));
         final float var6 = var5.x();
         final float var7 = var5.y();
         var0.ensurePrepared().visit(new Font.GlyphVisitor() {
            public void acceptGlyph(TextRenderable.Styled var1) {
               this.acceptActiveArea(var1);
            }

            public void acceptEmptyArea(EmptyArea var1) {
               this.acceptActiveArea(var1);
            }

            private void acceptActiveArea(ActiveArea var1) {
               if (ActiveTextCollector.isPointInRectangle(var6, var7, var1.activeLeft(), var1.activeTop(), var1.activeRight(), var1.activeBottom())) {
                  var3.accept(var1.style());
               }

            }
         });
      }
   }

   static boolean isPointInRectangle(float var0, float var1, float var2, float var3, float var4, float var5) {
      return var0 >= var2 && var0 < var4 && var1 >= var3 && var1 < var5;
   }

   public static record Parameters(Matrix3x2fc pose, float opacity, @Nullable ScreenRectangle scissor) {
      public Parameters(Matrix3x2fc var1) {
         this(var1, 1.0F, (ScreenRectangle)null);
      }

      public Parameters(Matrix3x2fc var1, float var2, @Nullable ScreenRectangle var3) {
         super();
         this.pose = var1;
         this.opacity = var2;
         this.scissor = var3;
      }

      public Parameters withPose(Matrix3x2fc var1) {
         return new Parameters(var1, this.opacity, this.scissor);
      }

      public Parameters withScale(float var1) {
         return this.withPose(this.pose.scale(var1, var1, new Matrix3x2f()));
      }

      public Parameters withOpacity(float var1) {
         return this.opacity == var1 ? this : new Parameters(this.pose, var1, this.scissor);
      }

      public Parameters withScissor(ScreenRectangle var1) {
         return var1.equals(this.scissor) ? this : new Parameters(this.pose, this.opacity, var1);
      }

      public Parameters withScissor(int var1, int var2, int var3, int var4) {
         ScreenRectangle var5 = (new ScreenRectangle(var1, var3, var2 - var1, var4 - var3)).transformAxisAligned(this.pose);
         if (this.scissor != null) {
            var5 = (ScreenRectangle)Objects.requireNonNullElse(this.scissor.intersection(var5), ScreenRectangle.empty());
         }

         return this.withScissor(var5);
      }
   }

   public static class ClickableStyleFinder implements ActiveTextCollector {
      private static final Parameters INITIAL = new Parameters(new Matrix3x2f());
      private final Font font;
      private final int testX;
      private final int testY;
      private Parameters defaultParameters;
      private boolean includeInsertions;
      private @Nullable Style result;
      private final Consumer<Style> styleScanner;

      public ClickableStyleFinder(Font var1, int var2, int var3) {
         super();
         this.defaultParameters = INITIAL;
         this.styleScanner = (var1x) -> {
            if (var1x.getClickEvent() != null || this.includeInsertions && var1x.getInsertion() != null) {
               this.result = var1x;
            }

         };
         this.font = var1;
         this.testX = var2;
         this.testY = var3;
      }

      public Parameters defaultParameters() {
         return this.defaultParameters;
      }

      public void defaultParameters(Parameters var1) {
         this.defaultParameters = var1;
      }

      public void accept(TextAlignment var1, int var2, int var3, Parameters var4, FormattedCharSequence var5) {
         int var6 = var1.calculateLeft(var2, this.font, var5);
         GuiTextRenderState var7 = new GuiTextRenderState(this.font, var5, var4.pose(), var6, var3, ARGB.white(var4.opacity()), 0, true, true, var4.scissor());
         ActiveTextCollector.findElementUnderCursor(var7, (float)this.testX, (float)this.testY, this.styleScanner);
      }

      public void acceptScrolling(Component var1, int var2, int var3, int var4, int var5, int var6, Parameters var7) {
         int var8 = this.font.width((FormattedText)var1);
         Objects.requireNonNull(this.font);
         byte var9 = 9;
         this.defaultScrollingHelper(var1, var2, var3, var4, var5, var6, var8, var9, var7);
      }

      public ClickableStyleFinder includeInsertions(boolean var1) {
         this.includeInsertions = var1;
         return this;
      }

      public @Nullable Style result() {
         return this.result;
      }
   }
}
