package net.minecraft.client.gui.components.debugchart;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ResultField;

public class ProfilerPieChart {
   private static final int RADIUS = 105;
   private static final int MARGIN = 5;
   private static final int CHART_Z_OFFSET = 10;
   private final Font font;
   @Nullable
   private ProfileResults profilerPieChartResults;
   private String profilerTreePath = "root";
   private int bottomOffset = 0;

   public ProfilerPieChart(Font var1) {
      super();
      this.font = var1;
   }

   public void setPieChartResults(@Nullable ProfileResults var1) {
      this.profilerPieChartResults = var1;
   }

   public void setBottomOffset(int var1) {
      this.bottomOffset = var1;
   }

   public void render(GuiGraphics var1) {
      if (this.profilerPieChartResults != null) {
         List var2 = this.profilerPieChartResults.getTimes(this.profilerTreePath);
         ResultField var3 = (ResultField)var2.removeFirst();
         int var4 = var1.guiWidth() - 105 - 10;
         int var5 = var4 - 105;
         int var6 = var4 + 105;
         int var7 = var2.size() * 9;
         int var8 = var1.guiHeight() - this.bottomOffset - 5;
         int var9 = var8 - var7;
         byte var10 = 62;
         int var11 = var9 - 62 - 5;
         var1.fill(var5 - 5, var11 - 62 - 5, var6 + 5, var8 + 5, -1873784752);
         var1.drawSpecial(var4x -> {
            double var5x = 0.0;

            for (ResultField var8x : var2) {
               int var9x = Mth.floor(var8x.percentage / 4.0) + 1;
               VertexConsumer var10x = var4x.getBuffer(RenderType.debugTriangleFan());
               int var11x = ARGB.opaque(var8x.getColor());
               int var12x = ARGB.multiply(var11x, -8355712);
               PoseStack.Pose var13x = var1.pose().last();
               var10x.addVertex(var13x, (float)var4, (float)var11, 10.0F).setColor(var11x);

               for (int var14x = var9x; var14x >= 0; var14x--) {
                  float var15x = (float)((var5x + var8x.percentage * (double)var14x / (double)var9x) * 6.2831854820251465 / 100.0);
                  float var16x = Mth.sin(var15x) * 105.0F;
                  float var17x = Mth.cos(var15x) * 105.0F * 0.5F;
                  var10x.addVertex(var13x, (float)var4 + var16x, (float)var11 - var17x, 10.0F).setColor(var11x);
               }

               var10x = var4x.getBuffer(RenderType.debugQuads());

               for (int var22x = var9x; var22x > 0; var22x--) {
                  float var23x = (float)((var5x + var8x.percentage * (double)var22x / (double)var9x) * 6.2831854820251465 / 100.0);
                  float var24x = Mth.sin(var23x) * 105.0F;
                  float var25x = Mth.cos(var23x) * 105.0F * 0.5F;
                  float var18x = (float)((var5x + var8x.percentage * (double)(var22x - 1) / (double)var9x) * 6.2831854820251465 / 100.0);
                  float var19x = Mth.sin(var18x) * 105.0F;
                  float var20x = Mth.cos(var18x) * 105.0F * 0.5F;
                  if (!((var25x + var20x) / 2.0F > 0.0F)) {
                     var10x.addVertex(var13x, (float)var4 + var24x, (float)var11 - var25x, 10.0F).setColor(var12x);
                     var10x.addVertex(var13x, (float)var4 + var24x, (float)var11 - var25x + 10.0F, 10.0F).setColor(var12x);
                     var10x.addVertex(var13x, (float)var4 + var19x, (float)var11 - var20x + 10.0F, 10.0F).setColor(var12x);
                     var10x.addVertex(var13x, (float)var4 + var19x, (float)var11 - var20x, 10.0F).setColor(var12x);
                  }
               }

               var5x += var8x.percentage;
            }
         });
         DecimalFormat var12 = new DecimalFormat("##0.00");
         var12.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
         String var13 = ProfileResults.demanglePath(var3.name);
         String var14 = "";
         if (!"unspecified".equals(var13)) {
            var14 = var14 + "[0] ";
         }

         if (var13.isEmpty()) {
            var14 = var14 + "ROOT ";
         } else {
            var14 = var14 + var13 + " ";
         }

         int var15 = 16777215;
         int var16 = var11 - 62;
         var1.drawString(this.font, var14, var5, var16, 16777215);
         var14 = var12.format(var3.globalPercentage) + "%";
         var1.drawString(this.font, var14, var6 - this.font.width(var14), var16, 16777215);

         for (int var17 = 0; var17 < var2.size(); var17++) {
            ResultField var18 = (ResultField)var2.get(var17);
            StringBuilder var19 = new StringBuilder();
            if ("unspecified".equals(var18.name)) {
               var19.append("[?] ");
            } else {
               var19.append("[").append(var17 + 1).append("] ");
            }

            String var20 = var19.append(var18.name).toString();
            int var21 = var9 + var17 * 9;
            var1.drawString(this.font, var20, var5, var21, var18.getColor());
            var20 = var12.format(var18.percentage) + "%";
            var1.drawString(this.font, var20, var6 - 50 - this.font.width(var20), var21, var18.getColor());
            var20 = var12.format(var18.globalPercentage) + "%";
            var1.drawString(this.font, var20, var6 - this.font.width(var20), var21, var18.getColor());
         }
      }
   }

   public void profilerPieChartKeyPress(int var1) {
      if (this.profilerPieChartResults != null) {
         List var2 = this.profilerPieChartResults.getTimes(this.profilerTreePath);
         if (!var2.isEmpty()) {
            ResultField var3 = (ResultField)var2.remove(0);
            if (var1 == 0) {
               if (!var3.name.isEmpty()) {
                  int var4 = this.profilerTreePath.lastIndexOf(30);
                  if (var4 >= 0) {
                     this.profilerTreePath = this.profilerTreePath.substring(0, var4);
                  }
               }
            } else {
               var1--;
               if (var1 < var2.size() && !"unspecified".equals(((ResultField)var2.get(var1)).name)) {
                  if (!this.profilerTreePath.isEmpty()) {
                     this.profilerTreePath = this.profilerTreePath + "\u001e";
                  }

                  this.profilerTreePath = this.profilerTreePath + ((ResultField)var2.get(var1)).name;
               }
            }
         }
      }
   }
}
