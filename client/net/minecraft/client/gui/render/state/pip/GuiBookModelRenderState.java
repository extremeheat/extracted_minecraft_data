package net.minecraft.client.gui.render.state.pip;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiLayer;
import net.minecraft.client.model.BookModel;
import net.minecraft.resources.ResourceLocation;

public record GuiBookModelRenderState(BookModel bookModel, ResourceLocation texture, float open, float flip, int x0, int y0, int x1, int y1, float z, float scale, GuiLayer layer, @Nullable ScreenRectangle scissorArea) implements PictureInPictureRenderState {
   public GuiBookModelRenderState(BookModel var1, ResourceLocation var2, float var3, float var4, int var5, int var6, int var7, int var8, float var9, float var10, GuiLayer var11, @Nullable ScreenRectangle var12) {
      super();
      this.bookModel = var1;
      this.texture = var2;
      this.open = var3;
      this.flip = var4;
      this.x0 = var5;
      this.y0 = var6;
      this.x1 = var7;
      this.y1 = var8;
      this.z = var9;
      this.scale = var10;
      this.layer = var11;
      this.scissorArea = var12;
   }
}
