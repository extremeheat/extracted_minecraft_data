package net.minecraft.client.renderer.state.gui.pip;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.model.Model;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jspecify.annotations.Nullable;

public record GuiSignRenderState(Model.Simple signModel, WoodType woodType, int x0, int y0, int x1, int y1, float scale, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {
   public GuiSignRenderState(final Model.Simple signModel, final WoodType woodType, final int x0, final int y0, final int x1, final int y1, final float scale, final @Nullable ScreenRectangle scissorArea) {
      this(signModel, woodType, x0, y0, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
   }

   public GuiSignRenderState {
      super();
   }
}
