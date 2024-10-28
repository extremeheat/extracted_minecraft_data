package net.minecraft.client.gui.components.debugchart;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
         int var10000 = var2.size();
         Objects.requireNonNull(this.font);
         int var7 = var10000 * 9;
         int var8 = var1.guiHeight() - this.bottomOffset - 5;
         int var9 = var8 - var7;
         boolean var10 = true;
         int var11 = var9 - 62 - 5;
         var1.fill(var5 - 5, var11 - 62 - 5, var6 + 5, var8 + 5, -1873784752);
         var1.drawSpecial((var4x) -> {
            double var5 = 0.0;

            ResultField var8;
            for(Iterator var7 = var2.iterator(); var7.hasNext(); var5 += var8.percentage) {
               var8 = (ResultField)var7.next();
               int var9 = Mth.floor(var8.percentage / 4.0) + 1;
               VertexConsumer var10 = var4x.getBuffer(RenderType.debugTriangleFan());
               int var11x = ARGB.opaque(var8.getColor());
               int var12 = ARGB.multiply(var11x, -8355712);
               PoseStack.Pose var13 = var1.pose().last();
               var10.addVertex(var13, (float)var4, (float)var11, 10.0F).setColor(var11x);

               int var14;
               float var15;
               float var16;
               float var17;
               for(var14 = var9; var14 >= 0; --var14) {
                  var15 = (float)((var5 + var8.percentage * (double)var14 / (double)var9) * 6.2831854820251465 / 100.0);
                  var16 = Mth.sin(var15) * 105.0F;
                  var17 = Mth.cos(var15) * 105.0F * 0.5F;
                  var10.addVertex(var13, (float)var4 + var16, (float)var11 - var17, 10.0F).setColor(var11x);
               }

               var10 = var4x.getBuffer(RenderType.debugQuads());

               for(var14 = var9; var14 > 0; --var14) {
                  var15 = (float)((var5 + var8.percentage * (double)var14 / (double)var9) * 6.2831854820251465 / 100.0);
                  var16 = Mth.sin(var15) * 105.0F;
                  var17 = Mth.cos(var15) * 105.0F * 0.5F;
                  float var18 = (float)((var5 + var8.percentage * (double)(var14 - 1) / (double)var9) * 6.2831854820251465 / 100.0);
                  float var19 = Mth.sin(var18) * 105.0F;
                  float var20 = Mth.cos(var18) * 105.0F * 0.5F;
                  if (!((var17 + var20) / 2.0F > 0.0F)) {
                     var10.addVertex(var13, (float)var4 + var16, (float)var11 - var17, 10.0F).setColor(var12);
                     var10.addVertex(var13, (float)var4 + var16, (float)var11 - var17 + 10.0F, 10.0F).setColor(var12);
                     var10.addVertex(var13, (float)var4 + var19, (float)var11 - var20 + 10.0F, 10.0F).setColor(var12);
                     var10.addVertex(var13, (float)var4 + var19, (float)var11 - var20, 10.0F).setColor(var12);
                  }
               }
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
         String var22 = var12.format(var3.globalPercentage);
         var14 = var22 + "%";
         var1.drawString(this.font, var14, var6 - this.font.width(var14), var16, 16777215);

         for(int var17 = 0; var17 < var2.size(); ++var17) {
            ResultField var18 = (ResultField)var2.get(var17);
            StringBuilder var19 = new StringBuilder();
            if ("unspecified".equals(var18.name)) {
               var19.append("[?] ");
            } else {
               var19.append("[").append(var17 + 1).append("] ");
            }

            String var20 = var19.append(var18.name).toString();
            Objects.requireNonNull(this.font);
            int var21 = var9 + var17 * 9;
            var1.drawString(this.font, var20, var5, var21, var18.getColor());
            var22 = var12.format(var18.percentage);
            var20 = var22 + "%";
            var1.drawString(this.font, var20, var6 - 50 - this.font.width(var20), var21, var18.getColor());
            var22 = var12.format(var18.globalPercentage);
            var20 = var22 + "%";
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
               --var1;
               if (var1 < var2.size() && !"unspecified".equals(((ResultField)var2.get(var1)).name)) {
                  if (!this.profilerTreePath.isEmpty()) {
                     this.profilerTreePath = this.profilerTreePath + "\u001e";
                  }

                  String var10001 = this.profilerTreePath;
                  this.profilerTreePath = var10001 + ((ResultField)var2.get(var1)).name;
               }
            }

         }
      }
   }
}
