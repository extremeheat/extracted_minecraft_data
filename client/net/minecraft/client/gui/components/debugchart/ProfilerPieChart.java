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
         double var12 = 0.0;

         for (ResultField var15 : var2) {
            int var16 = Mth.floor(var15.percentage / 4.0) + 1;
            VertexConsumer var17 = var1.bufferSource().getBuffer(RenderType.debugTriangleFan());
            int var18 = ARGB.opaque(var15.getColor());
            int var19 = ARGB.multiply(var18, -8355712);
            PoseStack.Pose var20 = var1.pose().last();
            var17.addVertex(var20, (float)var4, (float)var11, 10.0F).setColor(var18);

            for (int var21 = var16; var21 >= 0; var21--) {
               float var22 = (float)((var12 + var15.percentage * (double)var21 / (double)var16) * 6.2831854820251465 / 100.0);
               float var23 = Mth.sin(var22) * 105.0F;
               float var24 = Mth.cos(var22) * 105.0F * 0.5F;
               var17.addVertex(var20, (float)var4 + var23, (float)var11 - var24, 10.0F).setColor(var18);
            }

            var17 = var1.bufferSource().getBuffer(RenderType.debugQuads());

            for (int var38 = var16; var38 > 0; var38--) {
               float var40 = (float)((var12 + var15.percentage * (double)var38 / (double)var16) * 6.2831854820251465 / 100.0);
               float var44 = Mth.sin(var40) * 105.0F;
               float var46 = Mth.cos(var40) * 105.0F * 0.5F;
               float var25 = (float)((var12 + var15.percentage * (double)(var38 - 1) / (double)var16) * 6.2831854820251465 / 100.0);
               float var26 = Mth.sin(var25) * 105.0F;
               float var27 = Mth.cos(var25) * 105.0F * 0.5F;
               if (!((var46 + var27) / 2.0F > 0.0F)) {
                  var17.addVertex(var20, (float)var4 + var44, (float)var11 - var46, 10.0F).setColor(var19);
                  var17.addVertex(var20, (float)var4 + var44, (float)var11 - var46 + 10.0F, 10.0F).setColor(var19);
                  var17.addVertex(var20, (float)var4 + var26, (float)var11 - var27 + 10.0F, 10.0F).setColor(var19);
                  var17.addVertex(var20, (float)var4 + var26, (float)var11 - var27, 10.0F).setColor(var19);
               }
            }

            var12 += var15.percentage;
         }

         DecimalFormat var28 = new DecimalFormat("##0.00");
         var28.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
         String var29 = ProfileResults.demanglePath(var3.name);
         String var30 = "";
         if (!"unspecified".equals(var29)) {
            var30 = var30 + "[0] ";
         }

         if (var29.isEmpty()) {
            var30 = var30 + "ROOT ";
         } else {
            var30 = var30 + var29 + " ";
         }

         int var34 = 16777215;
         int var35 = var11 - 62;
         var1.drawString(this.font, var30, var5, var35, 16777215);
         var30 = var28.format(var3.globalPercentage) + "%";
         var1.drawString(this.font, var30, var6 - this.font.width(var30), var35, 16777215);

         for (int var36 = 0; var36 < var2.size(); var36++) {
            ResultField var37 = (ResultField)var2.get(var36);
            StringBuilder var39 = new StringBuilder();
            if ("unspecified".equals(var37.name)) {
               var39.append("[?] ");
            } else {
               var39.append("[").append(var36 + 1).append("] ");
            }

            String var41 = var39.append(var37.name).toString();
            int var45 = var9 + var36 * 9;
            var1.drawString(this.font, var41, var5, var45, var37.getColor());
            var41 = var28.format(var37.percentage) + "%";
            var1.drawString(this.font, var41, var6 - 50 - this.font.width(var41), var45, var37.getColor());
            var41 = var28.format(var37.globalPercentage) + "%";
            var1.drawString(this.font, var41, var6 - this.font.width(var41), var45, var37.getColor());
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
