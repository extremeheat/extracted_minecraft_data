package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;

public class StatsComponent extends JComponent {
   private static final DecimalFormat field_120040_a = (DecimalFormat)Util.func_200696_a(new DecimalFormat("########0.000"), (var0) -> {
      var0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   private final int[] field_120038_b = new int[256];
   private int field_120039_c;
   private final String[] field_120036_d = new String[11];
   private final MinecraftServer field_120037_e;

   public StatsComponent(MinecraftServer var1) {
      super();
      this.field_120037_e = var1;
      this.setPreferredSize(new Dimension(456, 246));
      this.setMinimumSize(new Dimension(456, 246));
      this.setMaximumSize(new Dimension(456, 246));
      (new Timer(500, (var1x) -> {
         this.func_120034_a();
      })).start();
      this.setBackground(Color.BLACK);
   }

   private void func_120034_a() {
      long var1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      this.field_120036_d[0] = "Memory use: " + var1 / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
      this.field_120036_d[1] = "Avg tick: " + field_120040_a.format(this.func_120035_a(this.field_120037_e.field_71311_j) * 1.0E-6D) + " ms";
      this.field_120038_b[this.field_120039_c++ & 255] = (int)(var1 * 100L / Runtime.getRuntime().maxMemory());
      this.repaint();
   }

   private double func_120035_a(long[] var1) {
      long var2 = 0L;
      long[] var4 = var1;
      int var5 = var1.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         long var7 = var4[var6];
         var2 += var7;
      }

      return (double)var2 / (double)var1.length;
   }

   public void paint(Graphics var1) {
      var1.setColor(new Color(16777215));
      var1.fillRect(0, 0, 456, 246);

      int var2;
      for(var2 = 0; var2 < 256; ++var2) {
         int var3 = this.field_120038_b[var2 + this.field_120039_c & 255];
         var1.setColor(new Color(var3 + 28 << 16));
         var1.fillRect(var2, 100 - var3, 1, var3);
      }

      var1.setColor(Color.BLACK);

      for(var2 = 0; var2 < this.field_120036_d.length; ++var2) {
         String var4 = this.field_120036_d[var2];
         if (var4 != null) {
            var1.drawString(var4, 32, 116 + var2 * 16);
         }
      }

   }
}
