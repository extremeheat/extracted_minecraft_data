package net.minecraft.client.gui.render.state;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiLayer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.joml.Matrix3x2f;

public record GuiItemRenderState(String name, Matrix3x2f pose, ItemStackRenderState itemStackRenderState, int x, int y, float z, GuiLayer layer, @Nullable ScreenRectangle scissorArea) {
   public GuiItemRenderState(String var1, Matrix3x2f var2, ItemStackRenderState var3, int var4, int var5, float var6, GuiLayer var7, @Nullable ScreenRectangle var8) {
      super();
      this.name = var1;
      this.pose = var2;
      this.itemStackRenderState = var3;
      this.x = var4;
      this.y = var5;
      this.z = var6;
      this.layer = var7;
      this.scissorArea = var8;
   }
}
