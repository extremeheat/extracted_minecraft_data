package org.apache.logging.log4j.core.filter;

import javax.script.SimpleBindings;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptRef;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "ScriptFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
public final class ScriptFilter extends AbstractFilter {
   private static Logger logger = StatusLogger.getLogger();
   private final AbstractScript script;
   private final Configuration configuration;

   private ScriptFilter(AbstractScript var1, Configuration var2, Filter.Result var3, Filter.Result var4) {
      super(var3, var4);
      this.script = var1;
      this.configuration = var2;
      if (!(var1 instanceof ScriptRef)) {
         var2.getScriptManager().addScript(var1);
      }

   }

   public Filter.Result filter(org.apache.logging.log4j.core.Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      SimpleBindings var6 = new SimpleBindings();
      var6.put("logger", var1);
      var6.put("level", var2);
      var6.put("marker", var3);
      var6.put("message", new SimpleMessage(var4));
      var6.put("parameters", var5);
      var6.put("throwable", (Object)null);
      var6.putAll(this.configuration.getProperties());
      var6.put("substitutor", this.configuration.getStrSubstitutor());
      Object var7 = this.configuration.getScriptManager().execute(this.script.getName(), var6);
      return var7 != null && Boolean.TRUE.equals(var7) ? this.onMatch : this.onMismatch;
   }

   public Filter.Result filter(org.apache.logging.log4j.core.Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      SimpleBindings var6 = new SimpleBindings();
      var6.put("logger", var1);
      var6.put("level", var2);
      var6.put("marker", var3);
      var6.put("message", var4 instanceof String ? new SimpleMessage((String)var4) : new ObjectMessage(var4));
      var6.put("parameters", (Object)null);
      var6.put("throwable", var5);
      var6.putAll(this.configuration.getProperties());
      var6.put("substitutor", this.configuration.getStrSubstitutor());
      Object var7 = this.configuration.getScriptManager().execute(this.script.getName(), var6);
      return var7 != null && Boolean.TRUE.equals(var7) ? this.onMatch : this.onMismatch;
   }

   public Filter.Result filter(org.apache.logging.log4j.core.Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      SimpleBindings var6 = new SimpleBindings();
      var6.put("logger", var1);
      var6.put("level", var2);
      var6.put("marker", var3);
      var6.put("message", var4);
      var6.put("parameters", (Object)null);
      var6.put("throwable", var5);
      var6.putAll(this.configuration.getProperties());
      var6.put("substitutor", this.configuration.getStrSubstitutor());
      Object var7 = this.configuration.getScriptManager().execute(this.script.getName(), var6);
      return var7 != null && Boolean.TRUE.equals(var7) ? this.onMatch : this.onMismatch;
   }

   public Filter.Result filter(LogEvent var1) {
      SimpleBindings var2 = new SimpleBindings();
      var2.put("logEvent", var1);
      var2.putAll(this.configuration.getProperties());
      var2.put("substitutor", this.configuration.getStrSubstitutor());
      Object var3 = this.configuration.getScriptManager().execute(this.script.getName(), var2);
      return var3 != null && Boolean.TRUE.equals(var3) ? this.onMatch : this.onMismatch;
   }

   public String toString() {
      return this.script.getName();
   }

   @PluginFactory
   public static ScriptFilter createFilter(@PluginElement("Script") AbstractScript var0, @PluginAttribute("onMatch") Filter.Result var1, @PluginAttribute("onMismatch") Filter.Result var2, @PluginConfiguration Configuration var3) {
      if (var0 == null) {
         LOGGER.error("A Script, ScriptFile or ScriptRef element must be provided for this ScriptFilter");
         return null;
      } else if (var0 instanceof ScriptRef && var3.getScriptManager().getScript(var0.getName()) == null) {
         logger.error((String)"No script with name {} has been declared.", (Object)var0.getName());
         return null;
      } else {
         return new ScriptFilter(var0, var3, var1, var2);
      }
   }
}
