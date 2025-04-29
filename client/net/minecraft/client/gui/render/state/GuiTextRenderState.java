package net.minecraft.client.gui.render.state;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import org.joml.Matrix3x2f;

public record GuiTextRenderState(TextRenderState textRenderState, Matrix3x2f pose, int x, int y, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements ScreenArea {
   public GuiTextRenderState(TextRenderState var1, Matrix3x2f var2, int var3, int var4, @Nullable ScreenRectangle var5) {
      this(var1, var2, var3, var4, var5, getBounds(var3, var4, var1, var2, var5));
   }

   public GuiTextRenderState(TextRenderState var1, Matrix3x2f var2, int var3, int var4, @Nullable ScreenRectangle var5, @Nullable ScreenRectangle var6) {
      super();
      this.textRenderState = var1;
      this.pose = var2;
      this.x = var3;
      this.y = var4;
      this.scissorArea = var5;
      this.bounds = var6;
   }

   @Nullable
   public static ScreenRectangle getBounds(int var0, int var1, TextRenderState var2, Matrix3x2f var3, @Nullable ScreenRectangle var4) {
      ScreenRectangle var5 = (new ScreenRectangle(var0 - 1, var1 - 1, (int)Math.ceil((double)var2.endX()) - (var0 - 1), var1 + var2.lineHeight() - (var1 - 1))).transformMaxBounds(var3);
      return var4 != null ? var4.intersection(var5) : var5;
   }
}
