package net.minecraft.client.gui.render.state.pip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.model.Model;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jspecify.annotations.Nullable;

public record GuiSignRenderState(Model.Simple signModel, WoodType woodType, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {
   public GuiSignRenderState(Model.Simple var1, WoodType var2, int var3, int var4, int var5, int var6, float var7, @Nullable ScreenRectangle var8) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, PictureInPictureRenderState.getBounds(var3, var4, var5, var6, var8));
   }

   public GuiSignRenderState(Model.Simple var1, WoodType var2, int var3, int var4, int var5, int var6, float var7, @Nullable ScreenRectangle var8, @Nullable ScreenRectangle var9) {
      super();
      this.signModel = var1;
      this.woodType = var2;
      this.x0 = var3;
      this.y0 = var4;
      this.x1 = var5;
      this.y1 = var6;
      this.scale = var7;
      this.scissorArea = var8;
      this.bounds = var9;
   }
}
