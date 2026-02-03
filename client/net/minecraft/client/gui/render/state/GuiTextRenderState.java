package net.minecraft.client.gui.render.state;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

public final class GuiTextRenderState implements ScreenArea {
   public final Font font;
   public final FormattedCharSequence text;
   public final Matrix3x2fc pose;
   public final int x;
   public final int y;
   public final int color;
   public final int backgroundColor;
   public final boolean dropShadow;
   final boolean includeEmpty;
   public final @Nullable ScreenRectangle scissor;
   private Font.@Nullable PreparedText preparedText;
   private @Nullable ScreenRectangle bounds;

   public GuiTextRenderState(final Font font, final FormattedCharSequence text, final Matrix3x2fc pose, final int x, final int y, final int color, final int backgroundColor, final boolean dropShadow, final boolean includeEmpty, final @Nullable ScreenRectangle scissor) {
      super();
      this.font = font;
      this.text = text;
      this.pose = pose;
      this.x = x;
      this.y = y;
      this.color = color;
      this.backgroundColor = backgroundColor;
      this.dropShadow = dropShadow;
      this.includeEmpty = includeEmpty;
      this.scissor = scissor;
   }

   public Font.PreparedText ensurePrepared() {
      if (this.preparedText == null) {
         this.preparedText = this.font.prepareText(this.text, (float)this.x, (float)this.y, this.color, this.dropShadow, this.includeEmpty, this.backgroundColor);
         ScreenRectangle bounds = this.preparedText.bounds();
         if (bounds != null) {
            bounds = bounds.transformMaxBounds(this.pose);
            this.bounds = this.scissor != null ? this.scissor.intersection(bounds) : bounds;
         }
      }

      return this.preparedText;
   }

   public @Nullable ScreenRectangle bounds() {
      this.ensurePrepared();
      return this.bounds;
   }
}
