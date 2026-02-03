package net.minecraft.client.gui.components.debugchart;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ResultField;
import org.jspecify.annotations.Nullable;

public class ProfilerPieChart {
   public static final int RADIUS = 105;
   public static final int PIE_CHART_THICKNESS = 10;
   private static final int MARGIN = 5;
   private final Font font;
   private @Nullable ProfileResults profilerPieChartResults;
   private String profilerTreePath = "root";
   private int bottomOffset = 0;

   public ProfilerPieChart(final Font font) {
      super();
      this.font = font;
   }

   public void setPieChartResults(final @Nullable ProfileResults results) {
      this.profilerPieChartResults = results;
   }

   public void setBottomOffset(final int bottomOffset) {
      this.bottomOffset = bottomOffset;
   }

   public void render(final GuiGraphics graphics) {
      if (this.profilerPieChartResults != null) {
         List<ResultField> list = this.profilerPieChartResults.getTimes(this.profilerTreePath);
         ResultField rootNode = (ResultField)list.removeFirst();
         int chartCenterX = graphics.guiWidth() - 105 - 10;
         int left = chartCenterX - 105;
         int right = chartCenterX + 105;
         int var10000 = list.size();
         Objects.requireNonNull(this.font);
         int textUnderChartHeight = var10000 * 9;
         int bottom = graphics.guiHeight() - this.bottomOffset - 5;
         int textStartY = bottom - textUnderChartHeight;
         int chartHalfSizeY = 62;
         int chartCenterY = textStartY - 62 - 5;
         graphics.fill(left - 5, chartCenterY - 62 - 5, right + 5, bottom + 5, -1873784752);
         graphics.submitProfilerChartRenderState(list, left, chartCenterY - 62 + 10, right, chartCenterY + 62);
         DecimalFormat format = new DecimalFormat("##0.00", DecimalFormatSymbols.getInstance(Locale.ROOT));
         String rootNodeName = ProfileResults.demanglePath(rootNode.name);
         String topText = "";
         if (!"unspecified".equals(rootNodeName)) {
            topText = topText + "[0] ";
         }

         if (rootNodeName.isEmpty()) {
            topText = topText + "ROOT ";
         } else {
            topText = topText + rootNodeName + " ";
         }

         int col = -1;
         int topTextY = chartCenterY - 62;
         graphics.drawString(this.font, (String)topText, left, topTextY, -1);
         String var26 = format.format(rootNode.globalPercentage);
         topText = var26 + "%";
         graphics.drawString(this.font, (String)topText, right - this.font.width(topText), topTextY, -1);

         for(int i = 0; i < list.size(); ++i) {
            ResultField result = (ResultField)list.get(i);
            StringBuilder string = new StringBuilder();
            if ("unspecified".equals(result.name)) {
               string.append("[?] ");
            } else {
               string.append("[").append(i + 1).append("] ");
            }

            String msg = string.append(result.name).toString();
            Objects.requireNonNull(this.font);
            int textY = textStartY + i * 9;
            graphics.drawString(this.font, msg, left, textY, result.getColor());
            var26 = format.format(result.percentage);
            msg = var26 + "%";
            graphics.drawString(this.font, msg, right - 50 - this.font.width(msg), textY, result.getColor());
            var26 = format.format(result.globalPercentage);
            msg = var26 + "%";
            graphics.drawString(this.font, msg, right - this.font.width(msg), textY, result.getColor());
         }

      }
   }

   public void profilerPieChartKeyPress(int key) {
      if (this.profilerPieChartResults != null) {
         List<ResultField> list = this.profilerPieChartResults.getTimes(this.profilerTreePath);
         if (!list.isEmpty()) {
            ResultField node = (ResultField)list.remove(0);
            if (key == 0) {
               if (!node.name.isEmpty()) {
                  int pos = this.profilerTreePath.lastIndexOf(30);
                  if (pos >= 0) {
                     this.profilerTreePath = this.profilerTreePath.substring(0, pos);
                  }
               }
            } else {
               --key;
               if (key < list.size() && !"unspecified".equals(((ResultField)list.get(key)).name)) {
                  if (!this.profilerTreePath.isEmpty()) {
                     this.profilerTreePath = this.profilerTreePath + "\u001e";
                  }

                  String var10001 = this.profilerTreePath;
                  this.profilerTreePath = var10001 + ((ResultField)list.get(key)).name;
               }
            }

         }
      }
   }
}
