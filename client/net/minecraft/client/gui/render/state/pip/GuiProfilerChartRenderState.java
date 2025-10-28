package net.minecraft.client.gui.render.state.pip;

import java.util.List;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.profiling.ResultField;
import org.jspecify.annotations.Nullable;

public record GuiProfilerChartRenderState(List<ResultField> chartData, int x0, int y0, int x1, int y1, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements PictureInPictureRenderState {
   public GuiProfilerChartRenderState(List<ResultField> var1, int var2, int var3, int var4, int var5, @Nullable ScreenRectangle var6) {
      this(var1, var2, var3, var4, var5, var6, PictureInPictureRenderState.getBounds(var2, var3, var4, var5, var6));
   }

   public GuiProfilerChartRenderState(List<ResultField> var1, int var2, int var3, int var4, int var5, @Nullable ScreenRectangle var6, @Nullable ScreenRectangle var7) {
      super();
      this.chartData = var1;
      this.x0 = var2;
      this.y0 = var3;
      this.x1 = var4;
      this.y1 = var5;
      this.scissorArea = var6;
      this.bounds = var7;
   }

   public float scale() {
      return 1.0F;
   }
}
