package net.minecraft.client.gui.render.state;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.joml.Matrix3x2f;

public record GuiItemRenderState(String name, Matrix3x2f pose, ItemStackRenderState itemStackRenderState, int x, int y, @Nullable ScreenRectangle scissorArea) {
   public GuiItemRenderState(String var1, Matrix3x2f var2, ItemStackRenderState var3, int var4, int var5, @Nullable ScreenRectangle var6) {
      super();
      this.name = var1;
      this.pose = var2;
      this.itemStackRenderState = var3;
      this.x = var4;
      this.y = var5;
      this.scissorArea = var6;
   }
}
