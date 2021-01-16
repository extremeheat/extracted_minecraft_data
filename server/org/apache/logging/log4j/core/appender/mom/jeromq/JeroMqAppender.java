package org.apache.logging.log4j.core.appender.mom.jeromq;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.Strings;

@Plugin(
   name = "JeroMQ",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class JeroMqAppender extends AbstractAppender {
   private static final int DEFAULT_BACKLOG = 100;
   private static final int DEFAULT_IVL = 100;
   private static final int DEFAULT_RCV_HWM = 1000;
   private static final int DEFAULT_SND_HWM = 1000;
   private final JeroMqManager manager;
   private final List<String> endpoints;
   private int sendRcFalse;
   private int sendRcTrue;

   private JeroMqAppender(String var1, Filter var2, Layout<? extends Serializable> var3, boolean var4, List<String> var5, long var6, long var8, boolean var10, byte[] var11, boolean var12, long var13, long var15, long var17, long var19, int var21, long var22, long var24, long var26, int var28, long var29, int var31, long var32, long var34, long var36, boolean var38) {
      super(var1, var2, var3, var4);
      this.manager = JeroMqManager.getJeroMqManager(var1, var6, var8, var10, var11, var12, var13, var15, var17, var19, var21, var22, var24, var26, var28, var29, var31, var32, var34, var36, var38, var5);
      this.endpoints = var5;
   }

   @PluginFactory
   public static JeroMqAppender createAppender(@Required(message = "No name provided for JeroMqAppender") @PluginAttribute("name") String var0, @PluginElement("Layout") Layout<?> var1, @PluginElement("Filter") Filter var2, @PluginElement("Properties") Property[] var3, @PluginAttribute("ignoreExceptions") boolean var4, @PluginAttribute(value = "affinity",defaultLong = 0L) long var5, @PluginAttribute(value = "backlog",defaultLong = 100L) long var7, @PluginAttribute("delayAttachOnConnect") boolean var9, @PluginAttribute("identity") byte[] var10, @PluginAttribute(value = "ipv4Only",defaultBoolean = true) boolean var11, @PluginAttribute(value = "linger",defaultLong = -1L) long var12, @PluginAttribute(value = "maxMsgSize",defaultLong = -1L) long var14, @PluginAttribute(value = "rcvHwm",defaultLong = 1000L) long var16, @PluginAttribute(value = "receiveBufferSize",defaultLong = 0L) long var18, @PluginAttribute(value = "receiveTimeOut",defaultLong = -1L) int var20, @PluginAttribute(value = "reconnectIVL",defaultLong = 100L) long var21, @PluginAttribute(value = "reconnectIVLMax",defaultLong = 0L) long var23, @PluginAttribute(value = "sendBufferSize",defaultLong = 0L) long var25, @PluginAttribute(value = "sendTimeOut",defaultLong = -1L) int var27, @PluginAttribute(value = "sndHwm",defaultLong = 1000L) long var28, @PluginAttribute(value = "tcpKeepAlive",defaultInt = -1) int var30, @PluginAttribute(value = "tcpKeepAliveCount",defaultLong = -1L) long var31, @PluginAttribute(value = "tcpKeepAliveIdle",defaultLong = -1L) long var33, @PluginAttribute(value = "tcpKeepAliveInterval",defaultLong = -1L) long var35, @PluginAttribute("xpubVerbose") boolean var37) {
      if (var1 == null) {
         var1 = PatternLayout.createDefaultLayout();
      }

      ArrayList var38;
      if (var3 == null) {
         var38 = new ArrayList(0);
      } else {
         var38 = new ArrayList(var3.length);
         Property[] var39 = var3;
         int var40 = var3.length;

         for(int var41 = 0; var41 < var40; ++var41) {
            Property var42 = var39[var41];
            if ("endpoint".equalsIgnoreCase(var42.getName())) {
               String var43 = var42.getValue();
               if (Strings.isNotEmpty(var43)) {
                  var38.add(var43);
               }
            }
         }
      }

      LOGGER.debug((String)"Creating JeroMqAppender with name={}, filter={}, layout={}, ignoreExceptions={}, endpoints={}", (Object)var0, var2, var1, var4, var38);
      return new JeroMqAppender(var0, var2, (Layout)var1, var4, var38, var5, var7, var9, var10, var11, var12, var14, var16, var18, var20, var21, var23, var25, var27, var28, var30, var31, var33, var35, var37);
   }

   public synchronized void append(LogEvent var1) {
      Layout var2 = this.getLayout();
      byte[] var3 = var2.toByteArray(var1);
      if (this.manager.send(this.getLayout().toByteArray(var1))) {
         ++this.sendRcTrue;
      } else {
         ++this.sendRcFalse;
         LOGGER.error((String)"Appender {} could not send message {} to JeroMQ {}", (Object)this.getName(), this.sendRcFalse, var3);
      }

   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = super.stop(var1, var3, false);
      var4 &= this.manager.stop(var1, var3);
      this.setStopped();
      return var4;
   }

   int getSendRcFalse() {
      return this.sendRcFalse;
   }

   int getSendRcTrue() {
      return this.sendRcTrue;
   }

   void resetSendRcs() {
      this.sendRcTrue = this.sendRcFalse = 0;
   }

   public String toString() {
      return "JeroMqAppender{name=" + this.getName() + ", state=" + this.getState() + ", manager=" + this.manager + ", endpoints=" + this.endpoints + '}';
   }
}
