package net.minecraft.client.gui.render.state.pip;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiLayer;
import net.minecraft.util.profiling.ResultField;

public record GuiProfilerChartRenderState(List<ResultField> chartData, int x0, int y0, int x1, int y1, float z, GuiLayer layer, @Nullable ScreenRectangle scissorArea) implements PictureInPictureRenderState {
   public GuiProfilerChartRenderState(List<ResultField> var1, int var2, int var3, int var4, int var5, float var6, GuiLayer var7, @Nullable ScreenRectangle var8) {
      super();
      this.chartData = var1;
      this.x0 = var2;
      this.y0 = var3;
      this.x1 = var4;
      this.y1 = var5;
      this.z = var6;
      this.layer = var7;
      this.scissorArea = var8;
   }

   public float scale() {
      return 1.0F;
   }
}
