package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import javax.script.SimpleBindings;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptRef;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "ScriptCondition",
   category = "Core",
   printObject = true
)
public class ScriptCondition {
   private static Logger LOGGER = StatusLogger.getLogger();
   private final AbstractScript script;
   private final Configuration configuration;

   public ScriptCondition(AbstractScript var1, Configuration var2) {
      super();
      this.script = (AbstractScript)Objects.requireNonNull(var1, "script");
      this.configuration = (Configuration)Objects.requireNonNull(var2, "configuration");
      if (!(var1 instanceof ScriptRef)) {
         var2.getScriptManager().addScript(var1);
      }

   }

   public List<PathWithAttributes> selectFilesToDelete(Path var1, List<PathWithAttributes> var2) {
      SimpleBindings var3 = new SimpleBindings();
      var3.put("basePath", var1);
      var3.put("pathList", var2);
      var3.putAll(this.configuration.getProperties());
      var3.put("configuration", this.configuration);
      var3.put("substitutor", this.configuration.getStrSubstitutor());
      var3.put("statusLogger", LOGGER);
      Object var4 = this.configuration.getScriptManager().execute(this.script.getName(), var3);
      return (List)var4;
   }

   @PluginFactory
   public static ScriptCondition createCondition(@PluginElement("Script") AbstractScript var0, @PluginConfiguration Configuration var1) {
      if (var0 == null) {
         LOGGER.error("A Script, ScriptFile or ScriptRef element must be provided for this ScriptCondition");
         return null;
      } else if (var0 instanceof ScriptRef && var1.getScriptManager().getScript(var0.getName()) == null) {
         LOGGER.error((String)"ScriptCondition: No script with name {} has been declared.", (Object)var0.getName());
         return null;
      } else {
         return new ScriptCondition(var0, var1);
      }
   }
}
