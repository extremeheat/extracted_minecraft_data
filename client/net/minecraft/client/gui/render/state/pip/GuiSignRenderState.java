package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiLayer;
import net.minecraft.client.model.Model;
import net.minecraft.world.level.block.state.properties.WoodType;

public record GuiSignRenderState(Model signModel, WoodType woodType, int x0, int y0, int x1, int y1, float z, float scale, GuiLayer layer, @Nullable ScreenRectangle scissorArea) implements PictureInPictureRenderState {
   public GuiSignRenderState(Model var1, WoodType var2, int var3, int var4, int var5, int var6, float var7, float var8, GuiLayer var9, @Nullable ScreenRectangle var10) {
      super();
      this.signModel = var1;
      this.woodType = var2;
      this.x0 = var3;
      this.y0 = var4;
      this.x1 = var5;
      this.y1 = var6;
      this.z = var7;
      this.scale = var8;
      this.layer = var9;
      this.scissorArea = var10;
   }
}
