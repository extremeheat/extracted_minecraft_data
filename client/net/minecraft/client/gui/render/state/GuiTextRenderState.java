package net.minecraft.client.gui.render.state;

import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2f;

public final class GuiTextRenderState implements ScreenArea {
   public final Font font;
   public final FormattedCharSequence text;
   public final Matrix3x2f pose;
   public final int x;
   public final int y;
   public final int color;
   public final int backgroundColor;
   public final boolean dropShadow;
   @Nullable
   public final ScreenRectangle scissor;
   @Nullable
   private Font.PreparedText preparedText;
   @Nullable
   private ScreenRectangle bounds;

   public GuiTextRenderState(Font var1, FormattedCharSequence var2, Matrix3x2f var3, int var4, int var5, int var6, int var7, boolean var8, @Nullable ScreenRectangle var9) {
      super();
      this.font = var1;
      this.text = var2;
      this.pose = var3;
      this.x = var4;
      this.y = var5;
      this.color = var6;
      this.backgroundColor = var7;
      this.dropShadow = var8;
      this.scissor = var9;
   }

   public Font.PreparedText ensurePrepared() {
      if (this.preparedText == null) {
         this.preparedText = this.font.prepareText(this.text, (float)this.x, (float)this.y, this.color, this.dropShadow, this.backgroundColor);
         ScreenRectangle var1 = this.preparedText.bounds();
         if (var1 != null) {
            var1 = var1.transformMaxBounds(this.pose);
            this.bounds = this.scissor != null ? this.scissor.intersection(var1) : var1;
         }
      }

      return this.preparedText;
   }

   @Nullable
   public ScreenRectangle bounds() {
      this.ensurePrepared();
      return this.bounds;
   }
}
