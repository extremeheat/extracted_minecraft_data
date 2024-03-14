package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.TimeUtil;

public class StatsComponent extends JComponent {
   private static final DecimalFormat DECIMAL_FORMAT = Util.make(
      new DecimalFormat("########0.000"), var0 -> var0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT))
   );
   private final int[] values = new int[256];
   private int vp;
   private final String[] msgs = new String[11];
   private final MinecraftServer server;
   private final Timer timer;

   public StatsComponent(MinecraftServer var1) {
      super();
      this.server = var1;
      this.setPreferredSize(new Dimension(456, 246));
      this.setMinimumSize(new Dimension(456, 246));
      this.setMaximumSize(new Dimension(456, 246));
      this.timer = new Timer(500, var1x -> this.tick());
      this.timer.start();
      this.setBackground(Color.BLACK);
   }

   private void tick() {
      long var1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      this.msgs[0] = "Memory use: " + var1 / 1024L / 1024L + " mb (" + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory() + "% free)";
      this.msgs[1] = "Avg tick: " + DECIMAL_FORMAT.format((double)this.server.getAverageTickTimeNanos() / (double)TimeUtil.NANOSECONDS_PER_MILLISECOND) + " ms";
      this.values[this.vp++ & 0xFF] = (int)(var1 * 100L / Runtime.getRuntime().maxMemory());
      this.repaint();
   }

   @Override
   public void paint(Graphics var1) {
      var1.setColor(new Color(16777215));
      var1.fillRect(0, 0, 456, 246);

      for(int var2 = 0; var2 < 256; ++var2) {
         int var3 = this.values[var2 + this.vp & 0xFF];
         var1.setColor(new Color(var3 + 28 << 16));
         var1.fillRect(var2, 100 - var3, 1, var3);
      }

      var1.setColor(Color.BLACK);

      for(int var4 = 0; var4 < this.msgs.length; ++var4) {
         String var5 = this.msgs[var4];
         if (var5 != null) {
            var1.drawString(var5, 32, 116 + var4 * 16);
         }
      }
   }

   public void close() {
      this.timer.stop();
   }
}
