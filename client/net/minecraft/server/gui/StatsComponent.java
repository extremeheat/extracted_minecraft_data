package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;

public class StatsComponent extends JComponent {
   private static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat)Util.make(new DecimalFormat("########0.000"), (var0) -> {
      var0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   private final int[] values = new int[256];
   // $FF: renamed from: vp int
   private int field_25;
   private final String[] msgs = new String[11];
   private final MinecraftServer server;
   private final Timer timer;

   public StatsComponent(MinecraftServer var1) {
      super();
      this.server = var1;
      this.setPreferredSize(new Dimension(456, 246));
      this.setMinimumSize(new Dimension(456, 246));
      this.setMaximumSize(new Dimension(456, 246));
      this.timer = new Timer(500, (var1x) -> {
         this.tick();
      });
      this.timer.start();
      this.setBackground(Color.BLACK);
   }

   private void tick() {
      long var1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      this.msgs[0] = "Memory use: " + var1 / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
      String[] var10000 = this.msgs;
      DecimalFormat var10002 = DECIMAL_FORMAT;
      double var10003 = this.getAverage(this.server.tickTimes);
      var10000[1] = "Avg tick: " + var10002.format(var10003 * 1.0E-6D) + " ms";
      this.values[this.field_25++ & 255] = (int)(var1 * 100L / Runtime.getRuntime().maxMemory());
      this.repaint();
   }

   private double getAverage(long[] var1) {
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
         int var3 = this.values[var2 + this.field_25 & 255];
         var1.setColor(new Color(var3 + 28 << 16));
         var1.fillRect(var2, 100 - var3, 1, var3);
      }

      var1.setColor(Color.BLACK);

      for(var2 = 0; var2 < this.msgs.length; ++var2) {
         String var4 = this.msgs[var2];
         if (var4 != null) {
            var1.drawString(var4, 32, 116 + var2 * 16);
         }
      }

   }

   public void close() {
      this.timer.stop();
   }
}
