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
import org.joml.Vector2fc;
import org.jspecify.annotations.Nullable;

public interface ActiveTextCollector {
   double PERIOD_PER_SCROLLED_PIXEL = 0.5;
   double MIN_SCROLL_PERIOD = 3.0;

   Parameters defaultParameters();

   void defaultParameters(Parameters newParameters);

   default void accept(final int x, final int y, final FormattedCharSequence text) {
      this.accept(TextAlignment.LEFT, x, y, this.defaultParameters(), text);
   }

   default void accept(final int x, final int y, final Component text) {
      this.accept(TextAlignment.LEFT, x, y, this.defaultParameters(), text.getVisualOrderText());
   }

   default void accept(final TextAlignment alignment, final int anchorX, final int y, final Parameters parameters, final Component text) {
      this.accept(alignment, anchorX, y, parameters, text.getVisualOrderText());
   }

   void accept(TextAlignment alignment, int anchorX, int y, Parameters parameters, FormattedCharSequence text);

   default void accept(final TextAlignment alignment, final int anchorX, final int y, final Component text) {
      this.accept(alignment, anchorX, y, text.getVisualOrderText());
   }

   default void accept(final TextAlignment alignment, final int anchorX, final int y, final FormattedCharSequence text) {
      this.accept(alignment, anchorX, y, this.defaultParameters(), text);
   }

   void acceptScrolling(Component message, int centerX, int left, int right, int top, int bottom, Parameters parameters);

   default void acceptScrolling(final Component message, final int centerX, final int left, final int right, final int top, final int bottom) {
      this.acceptScrolling(message, centerX, left, right, top, bottom, this.defaultParameters());
   }

   default void acceptScrollingWithDefaultCenter(final Component message, final int left, final int right, final int top, final int bottom) {
      this.acceptScrolling(message, (left + right) / 2, left, right, top, bottom);
   }

   default void defaultScrollingHelper(final Component message, final int centerX, final int left, final int right, final int top, final int bottom, final int lineWidth, final int lineHeight, final Parameters parameters) {
      int textTop = (top + bottom - lineHeight) / 2 + 1;
      int availableMessageWidth = right - left;
      if (lineWidth > availableMessageWidth) {
         int maxPosition = lineWidth - availableMessageWidth;
         double time = (double)Util.getMillis() / 1000.0;
         double period = Math.max((double)maxPosition * 0.5, 3.0);
         double alpha = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * time / period)) / 2.0 + 0.5;
         double pos = Mth.lerp(alpha, 0.0, (double)maxPosition);
         Parameters localParameters = parameters.withScissor(left, right, top, bottom);
         this.accept(TextAlignment.LEFT, left - (int)pos, textTop, localParameters, message.getVisualOrderText());
      } else {
         int textX = Mth.clamp(centerX, left + lineWidth / 2, right - lineWidth / 2);
         this.accept(TextAlignment.CENTER, textX, textTop, message);
      }

   }

   static void findElementUnderCursor(final GuiTextRenderState text, final float testX, final float testY, final Consumer<Style> output) {
      ScreenRectangle bounds = text.bounds();
      if (bounds != null && bounds.containsPoint((int)testX, (int)testY)) {
         Vector2fc localMousePos = text.pose.invert(new Matrix3x2f()).transformPosition(new Vector2f(testX, testY));
         final float localMouseX = localMousePos.x();
         final float localMouseY = localMousePos.y();
         text.ensurePrepared().visit(new Font.GlyphVisitor() {
            public void acceptGlyph(final TextRenderable.Styled glyph) {
               this.acceptActiveArea(glyph);
            }

            public void acceptEmptyArea(final EmptyArea empty) {
               this.acceptActiveArea(empty);
            }

            private void acceptActiveArea(final ActiveArea glyph) {
               if (ActiveTextCollector.isPointInRectangle(localMouseX, localMouseY, glyph.activeLeft(), glyph.activeTop(), glyph.activeRight(), glyph.activeBottom())) {
                  output.accept(glyph.style());
               }

            }
         });
      }
   }

   static boolean isPointInRectangle(final float x, final float y, final float left, final float top, final float right, final float bottom) {
      return x >= left && x < right && y >= top && y < bottom;
   }

   public static record Parameters(Matrix3x2fc pose, float opacity, @Nullable ScreenRectangle scissor) {
      public Parameters(final Matrix3x2fc pose) {
         this(pose, 1.0F, (ScreenRectangle)null);
      }

      public Parameters {
         super();
      }

      public Parameters withPose(final Matrix3x2fc pose) {
         return new Parameters(pose, this.opacity, this.scissor);
      }

      public Parameters withScale(final float scale) {
         return this.withPose(this.pose.scale(scale, scale, new Matrix3x2f()));
      }

      public Parameters withOpacity(final float opacity) {
         return this.opacity == opacity ? this : new Parameters(this.pose, opacity, this.scissor);
      }

      public Parameters withScissor(final ScreenRectangle scissor) {
         return scissor.equals(this.scissor) ? this : new Parameters(this.pose, this.opacity, scissor);
      }

      public Parameters withScissor(final int left, final int right, final int top, final int bottom) {
         ScreenRectangle newScissor = (new ScreenRectangle(left, top, right - left, bottom - top)).transformAxisAligned(this.pose);
         if (this.scissor != null) {
            newScissor = (ScreenRectangle)Objects.requireNonNullElse(this.scissor.intersection(newScissor), ScreenRectangle.empty());
         }

         return this.withScissor(newScissor);
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

      public ClickableStyleFinder(final Font font, final int testX, final int testY) {
         super();
         this.defaultParameters = INITIAL;
         this.styleScanner = (style) -> {
            if (style.getClickEvent() != null || this.includeInsertions && style.getInsertion() != null) {
               this.result = style;
            }

         };
         this.font = font;
         this.testX = testX;
         this.testY = testY;
      }

      public Parameters defaultParameters() {
         return this.defaultParameters;
      }

      public void defaultParameters(final Parameters newParameters) {
         this.defaultParameters = newParameters;
      }

      public void accept(final TextAlignment alignment, final int anchorX, final int y, final Parameters parameters, final FormattedCharSequence text) {
         int leftX = alignment.calculateLeft(anchorX, this.font, text);
         GuiTextRenderState renderState = new GuiTextRenderState(this.font, text, parameters.pose(), leftX, y, ARGB.white(parameters.opacity()), 0, true, true, parameters.scissor());
         ActiveTextCollector.findElementUnderCursor(renderState, (float)this.testX, (float)this.testY, this.styleScanner);
      }

      public void acceptScrolling(final Component message, final int centerX, final int left, final int right, final int top, final int bottom, final Parameters parameters) {
         int lineWidth = this.font.width((FormattedText)message);
         Objects.requireNonNull(this.font);
         int lineHeight = 9;
         this.defaultScrollingHelper(message, centerX, left, right, top, bottom, lineWidth, lineHeight, parameters);
      }

      public ClickableStyleFinder includeInsertions(final boolean flag) {
         this.includeInsertions = flag;
         return this;
      }

      public @Nullable Style result() {
         return this.result;
      }
   }
}
