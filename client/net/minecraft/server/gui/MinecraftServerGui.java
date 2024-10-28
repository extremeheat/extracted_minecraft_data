package net.minecraft.server.gui;

import com.google.common.collect.Lists;
import com.mojang.logging.LogQueues;
import com.mojang.logging.LogUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
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
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.server.dedicated.DedicatedServer;
import org.slf4j.Logger;

public class MinecraftServerGui extends JComponent {
   private static final Font MONOSPACED = new Font("Monospaced", 0, 12);
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String TITLE = "Minecraft server";
   private static final String SHUTDOWN_TITLE = "Minecraft server - shutting down!";
   private final DedicatedServer server;
   private Thread logAppenderThread;
   private final Collection<Runnable> finalizers = Lists.newArrayList();
   final AtomicBoolean isClosing = new AtomicBoolean();

   public static MinecraftServerGui showFrameFor(final DedicatedServer var0) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception var3) {
      }

      final JFrame var1 = new JFrame("Minecraft server");
      final MinecraftServerGui var2 = new MinecraftServerGui(var0);
      var1.setDefaultCloseOperation(2);
      var1.add(var2);
      var1.pack();
      var1.setLocationRelativeTo((Component)null);
      var1.setVisible(true);
      var1.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent var1x) {
            if (!var2.isClosing.getAndSet(true)) {
               var1.setTitle("Minecraft server - shutting down!");
               var0.halt(true);
               var2.runFinalizers();
            }

         }
      });
      Objects.requireNonNull(var1);
      var2.addFinalizer(var1::dispose);
      var2.start();
      return var2;
   }

   private MinecraftServerGui(DedicatedServer var1) {
      super();
      this.server = var1;
      this.setPreferredSize(new Dimension(854, 480));
      this.setLayout(new BorderLayout());

      try {
         this.add(this.buildChatPanel(), "Center");
         this.add(this.buildInfoPanel(), "West");
      } catch (Exception var3) {
         LOGGER.error("Couldn't build server GUI", var3);
      }

   }

   public void addFinalizer(Runnable var1) {
      this.finalizers.add(var1);
   }

   private JComponent buildInfoPanel() {
      JPanel var1 = new JPanel(new BorderLayout());
      StatsComponent var2 = new StatsComponent(this.server);
      Collection var10000 = this.finalizers;
      Objects.requireNonNull(var2);
      var10000.add(var2::close);
      var1.add(var2, "North");
      var1.add(this.buildPlayerPanel(), "Center");
      var1.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
      return var1;
   }

   private JComponent buildPlayerPanel() {
      PlayerListComponent var1 = new PlayerListComponent(this.server);
      JScrollPane var2 = new JScrollPane(var1, 22, 30);
      var2.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
      return var2;
   }

   private JComponent buildChatPanel() {
      JPanel var1 = new JPanel(new BorderLayout());
      JTextArea var2 = new JTextArea();
      JScrollPane var3 = new JScrollPane(var2, 22, 30);
      var2.setEditable(false);
      var2.setFont(MONOSPACED);
      JTextField var4 = new JTextField();
      var4.addActionListener((var2x) -> {
         String var3 = var4.getText().trim();
         if (!var3.isEmpty()) {
            this.server.handleConsoleInput(var3, this.server.createCommandSourceStack());
         }

         var4.setText("");
      });
      var2.addFocusListener(new FocusAdapter(this) {
         public void focusGained(FocusEvent var1) {
         }
      });
      var1.add(var3, "Center");
      var1.add(var4, "South");
      var1.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
      this.logAppenderThread = new Thread(() -> {
         String var3x;
         while((var3x = LogQueues.getNextLogEvent("ServerGuiConsole")) != null) {
            this.print(var2, var3, var3x);
         }

      });
      this.logAppenderThread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      this.logAppenderThread.setDaemon(true);
      return var1;
   }

   public void start() {
      this.logAppenderThread.start();
   }

   public void close() {
      if (!this.isClosing.getAndSet(true)) {
         this.runFinalizers();
      }

   }

   void runFinalizers() {
      this.finalizers.forEach(Runnable::run);
   }

   public void print(JTextArea var1, JScrollPane var2, String var3) {
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> {
            this.print(var1, var2, var3);
         });
      } else {
         Document var4 = var1.getDocument();
         JScrollBar var5 = var2.getVerticalScrollBar();
         boolean var6 = false;
         if (var2.getViewport().getView() == var1) {
            var6 = (double)var5.getValue() + var5.getSize().getHeight() + (double)(MONOSPACED.getSize() * 4) > (double)var5.getMaximum();
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
