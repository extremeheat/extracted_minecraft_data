package net.minecraft.server.gui;

import com.mojang.util.QueueLogAppender;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftServerGui extends JComponent {
   private static final Font field_164249_a = new Font("Monospaced", 0, 12);
   private static final Logger field_164248_b = LogManager.getLogger();
   private final DedicatedServer field_120021_b;
   private Thread field_206932_d;

   public static void func_120016_a(final DedicatedServer var0) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception var3) {
      }

      MinecraftServerGui var1 = new MinecraftServerGui(var0);
      JFrame var2 = new JFrame("Minecraft server");
      var2.add(var1);
      var2.pack();
      var2.setLocationRelativeTo((Component)null);
      var2.setVisible(true);
      var2.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent var1) {
            var0.func_71263_m();

            while(!var0.func_71241_aa()) {
               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var3) {
                  var3.printStackTrace();
               }
            }

            System.exit(0);
         }
      });
      var1.func_206931_a();
   }

   public MinecraftServerGui(DedicatedServer var1) {
      super();
      this.field_120021_b = var1;
      this.setPreferredSize(new Dimension(854, 480));
      this.setLayout(new BorderLayout());

      try {
         this.add(this.func_120018_d(), "Center");
         this.add(this.func_120019_b(), "West");
      } catch (Exception var3) {
         field_164248_b.error("Couldn't build server GUI", var3);
      }

   }

   private JComponent func_120019_b() throws Exception {
      JPanel var1 = new JPanel(new BorderLayout());
      var1.add(new StatsComponent(this.field_120021_b), "North");
      var1.add(this.func_120020_c(), "Center");
      var1.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
      return var1;
   }

   private JComponent func_120020_c() throws Exception {
      PlayerListComponent var1 = new PlayerListComponent(this.field_120021_b);
      JScrollPane var2 = new JScrollPane(var1, 22, 30);
      var2.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
      return var2;
   }

   private JComponent func_120018_d() throws Exception {
      JPanel var1 = new JPanel(new BorderLayout());
      JTextArea var2 = new JTextArea();
      JScrollPane var3 = new JScrollPane(var2, 22, 30);
      var2.setEditable(false);
      var2.setFont(field_164249_a);
      JTextField var4 = new JTextField();
      var4.addActionListener((var2x) -> {
         String var3 = var4.getText().trim();
         if (!var3.isEmpty()) {
            this.field_120021_b.func_195581_a(var3, this.field_120021_b.func_195573_aM());
         }

         var4.setText("");
      });
      var2.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent var1) {
         }
      });
      var1.add(var3, "Center");
      var1.add(var4, "South");
      var1.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
      this.field_206932_d = new Thread(() -> {
         String var3x;
         while((var3x = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null) {
            this.func_164247_a(var2, var3, var3x);
         }

      });
      this.field_206932_d.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(field_164248_b));
      this.field_206932_d.setDaemon(true);
      return var1;
   }

   public void func_206931_a() {
      this.field_206932_d.start();
   }

   public void func_164247_a(JTextArea var1, JScrollPane var2, String var3) {
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> {
            this.func_164247_a(var1, var2, var3);
         });
      } else {
         Document var4 = var1.getDocument();
         JScrollBar var5 = var2.getVerticalScrollBar();
         boolean var6 = false;
         if (var2.getViewport().getView() == var1) {
            var6 = (double)var5.getValue() + var5.getSize().getHeight() + (double)(field_164249_a.getSize() * 4) > (double)var5.getMaximum();
         }

         try {
            var4.insertString(var4.getLength(), var3, (AttributeSet)null);
         } catch (BadLocationException var8) {
         }

         if (var6) {
            var5.setValue(2147483647);
         }

      }
   }
}
