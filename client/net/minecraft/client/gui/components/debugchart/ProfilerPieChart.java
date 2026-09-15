package net.minecraft.client.gui.components.debugchart;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ResultField;
import org.jspecify.annotations.Nullable;

public class ProfilerPieChart {
   public static final int RADIUS = 105;
   public static final int PIE_CHART_THICKNESS = 10;
   private static final DecimalFormat PERCENTAGE_FORMAT;
   private static final int MARGIN = 5;
   private static final int WIDTH = 260;
   private static final int SUBSEQUENT_LINES_INDENT = 10;
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

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int scaledScreenWidth, final int scaledScreenHeight) {
      if (this.profilerPieChartResults != null) {
         List<ResultField> list = this.profilerPieChartResults.getTimes(this.profilerTreePath);
         ResultField currentNode = (ResultField)list.removeFirst();
         int chartCenterX = scaledScreenWidth - 130 - 10;
         int left = chartCenterX - 130;
         int right = chartCenterX + 130;
         int var10000 = list.size();
         Objects.requireNonNull(this.font);
         int textUnderChartHeight = var10000 * 9;
         int bottom = scaledScreenHeight - this.bottomOffset - 5;
         int textStartY = bottom - textUnderChartHeight;
         int chartHalfSizeY = 62;
         int chartCenterY = textStartY - 62 - 5;
         String globalPercentage = PERCENTAGE_FORMAT.format(currentNode.globalPercentage) + "%";
         int globalPercentageWidth = this.font.width(globalPercentage);
         int zeroPrefixWidth = this.font.width("[0] ");
         int topTextMaxWidth = right - globalPercentageWidth - 5 - left - zeroPrefixWidth;
         String currentNodeName = ProfileResults.demanglePath(currentNode.name);
         List<String> currentNodeNameLines = this.splitNodeName(currentNodeName, topTextMaxWidth, topTextMaxWidth - 10);
         var10000 = chartCenterY - 62;
         int var10001 = currentNodeNameLines.size() - 1;
         Objects.requireNonNull(this.font);
         int currentNodeNameTop = var10000 - var10001 * 9;
         graphics.fill(left - 5, currentNodeNameTop - 5, right + 5, bottom + 5, -1873784752);
         graphics.profilerChart(list, left, chartCenterY - 62 + 10, right, chartCenterY + 62);
         String firstLineText = "";
         if (!"unspecified".equals(currentNodeName) && !"root".equals(currentNodeName)) {
            firstLineText = firstLineText + "[0] ";
         }

         firstLineText = firstLineText + (String)currentNodeNameLines.getFirst();
         int col = -1;
         graphics.text(this.font, (String)firstLineText, left, currentNodeNameTop, -1);

         for(int i = 1; i < currentNodeNameLines.size(); ++i) {
            Font var33 = this.font;
            String var10002 = (String)currentNodeNameLines.get(i);
            int var10003 = left + 10 + zeroPrefixWidth;
            Objects.requireNonNull(this.font);
            graphics.text(var33, (String)var10002, var10003, currentNodeNameTop + i * 9, -1);
         }

         graphics.text(this.font, (String)globalPercentage, right - globalPercentageWidth, currentNodeNameTop, -1);

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
            graphics.text(this.font, msg, left, textY, result.getColor());
            msg = PERCENTAGE_FORMAT.format(result.percentage) + "%";
            graphics.text(this.font, msg, right - 50 - this.font.width(msg), textY, result.getColor());
            msg = PERCENTAGE_FORMAT.format(result.globalPercentage) + "%";
            graphics.text(this.font, msg, right - this.font.width(msg), textY, result.getColor());
         }

      }
   }

   private List<String> splitNodeName(final String nodeName, final int firstLineMaxWidth, final int maxWidth) {
      String[] nodeNameSplit = nodeName.split("\\.");
      List<String> lines = new ArrayList();
      String currentLine = "";
      int nameIndex = 0;

      while(nameIndex < nodeNameSplit.length) {
         String currentName = nodeNameSplit[nameIndex];
         String currentNameWithPeriod = (nameIndex != 0 ? "." : "") + currentName;
         String newLine = currentLine + currentNameWithPeriod;
         int newWidth = this.font.width(newLine);
         if (newWidth > (!lines.isEmpty() ? maxWidth : firstLineMaxWidth)) {
            if (currentLine.isEmpty()) {
               lines.add(currentNameWithPeriod);
               ++nameIndex;
            } else {
               lines.add(currentLine);
               currentLine = "";
            }
         } else {
            currentLine = newLine;
            ++nameIndex;
         }
      }

      if (!currentLine.isEmpty()) {
         lines.add(currentLine);
      }

      return lines;
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

   static {
      PERCENTAGE_FORMAT = new DecimalFormat("##0.00", DecimalFormatSymbols.getInstance(Locale.ROOT));
   }
}
