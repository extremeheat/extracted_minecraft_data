package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidPort;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.HtmlLayout;
import org.apache.logging.log4j.core.net.SmtpManager;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(
   name = "SMTP",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class SmtpAppender extends AbstractAppender {
   private static final int DEFAULT_BUFFER_SIZE = 512;
   private final SmtpManager manager;

   private SmtpAppender(String var1, Filter var2, Layout<? extends Serializable> var3, SmtpManager var4, boolean var5) {
      super(var1, var2, var3, var5);
      this.manager = var4;
   }

   @PluginFactory
   public static SmtpAppender createAppender(@PluginConfiguration Configuration var0, @PluginAttribute("name") @Required String var1, @PluginAttribute("to") String var2, @PluginAttribute("cc") String var3, @PluginAttribute("bcc") String var4, @PluginAttribute("from") String var5, @PluginAttribute("replyTo") String var6, @PluginAttribute("subject") String var7, @PluginAttribute("smtpProtocol") String var8, @PluginAttribute("smtpHost") String var9, @PluginAttribute(value = "smtpPort",defaultString = "0") @ValidPort String var10, @PluginAttribute("smtpUsername") String var11, @PluginAttribute(value = "smtpPassword",sensitive = true) String var12, @PluginAttribute("smtpDebug") String var13, @PluginAttribute("bufferSize") String var14, @PluginElement("Layout") Layout<? extends Serializable> var15, @PluginElement("Filter") Filter var16, @PluginAttribute("ignoreExceptions") String var17) {
      if (var1 == null) {
         LOGGER.error("No name provided for SmtpAppender");
         return null;
      } else {
         boolean var18 = Booleans.parseBoolean(var17, true);
         int var19 = AbstractAppender.parseInt(var10, 0);
         boolean var20 = Boolean.parseBoolean(var13);
         int var21 = var14 == null ? 512 : Integer.parseInt(var14);
         if (var15 == null) {
            var15 = HtmlLayout.createDefaultLayout();
         }

         if (var16 == null) {
            var16 = ThresholdFilter.createFilter((Level)null, (Filter.Result)null, (Filter.Result)null);
         }

         Object var22 = var0 != null ? var0 : new DefaultConfiguration();
         SmtpManager var23 = SmtpManager.getSmtpManager((Configuration)var22, var2, var3, var4, var5, var6, var7, var8, var9, var19, var11, var12, var20, var16.toString(), var21);
         return var23 == null ? null : new SmtpAppender(var1, (Filter)var16, (Layout)var15, var23, var18);
      }
   }

   public boolean isFiltered(LogEvent var1) {
      boolean var2 = super.isFiltered(var1);
      if (var2) {
         this.manager.add(var1);
      }

      return var2;
   }

   public void append(LogEvent var1) {
      this.manager.sendEvents(this.getLayout(), var1);
   }
}
