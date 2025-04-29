package net.minecraft.client.gui.render.state;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.joml.Matrix3x2f;

public record GuiItemRenderState(String name, Matrix3x2f pose, ItemStackRenderState itemStackRenderState, int x, int y, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements ScreenArea {
   public GuiItemRenderState(String var1, Matrix3x2f var2, ItemStackRenderState var3, int var4, int var5, @Nullable ScreenRectangle var6) {
      this(var1, var2, var3, var4, var5, var6, getBounds(var4, var5, var2, var6));
   }

   public GuiItemRenderState(String var1, Matrix3x2f var2, ItemStackRenderState var3, int var4, int var5, @Nullable ScreenRectangle var6, @Nullable ScreenRectangle var7) {
      super();
      this.name = var1;
      this.pose = var2;
      this.itemStackRenderState = var3;
      this.x = var4;
      this.y = var5;
      this.scissorArea = var6;
      this.bounds = var7;
   }

   @Nullable
   public static ScreenRectangle getBounds(int var0, int var1, Matrix3x2f var2, @Nullable ScreenRectangle var3) {
      ScreenRectangle var4 = (new ScreenRectangle(var0, var1, 16, 16)).transformMaxBounds(var2);
      return var3 != null ? var3.intersection(var4) : var4;
   }
}
