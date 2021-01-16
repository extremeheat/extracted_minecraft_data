package org.apache.logging.log4j.core.script;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.util.FileWatcher;
import org.apache.logging.log4j.core.util.WatchManager;
import org.apache.logging.log4j.status.StatusLogger;

public class ScriptManager implements FileWatcher, Serializable {
   private static final long serialVersionUID = -2534169384971965196L;
   private static final String KEY_THREADING = "THREADING";
   private static final Logger logger = StatusLogger.getLogger();
   private final Configuration configuration;
   private final ScriptEngineManager manager = new ScriptEngineManager();
   private final ConcurrentMap<String, ScriptManager.ScriptRunner> scriptRunners = new ConcurrentHashMap();
   private final String languages;
   private final WatchManager watchManager;

   public ScriptManager(Configuration var1, WatchManager var2) {
      super();
      this.configuration = var1;
      this.watchManager = var2;
      List var3 = this.manager.getEngineFactories();
      StringBuilder var4;
      Iterator var5;
      ScriptEngineFactory var6;
      if (logger.isDebugEnabled()) {
         var4 = new StringBuilder();
         logger.debug("Installed script engines");
         var5 = var3.iterator();

         while(var5.hasNext()) {
            var6 = (ScriptEngineFactory)var5.next();
            String var7 = (String)var6.getParameter("THREADING");
            if (var7 == null) {
               var7 = "Not Thread Safe";
            }

            StringBuilder var8 = new StringBuilder();

            String var10;
            for(Iterator var9 = var6.getNames().iterator(); var9.hasNext(); var8.append(var10)) {
               var10 = (String)var9.next();
               if (var8.length() > 0) {
                  var8.append(", ");
               }
            }

            if (var4.length() > 0) {
               var4.append(", ");
            }

            var4.append(var8);
            boolean var13 = var6.getScriptEngine() instanceof Compilable;
            logger.debug(var6.getEngineName() + " Version: " + var6.getEngineVersion() + ", Language: " + var6.getLanguageName() + ", Threading: " + var7 + ", Compile: " + var13 + ", Names: {" + var8.toString() + "}");
         }

         this.languages = var4.toString();
      } else {
         var4 = new StringBuilder();
         var5 = var3.iterator();

         while(var5.hasNext()) {
            var6 = (ScriptEngineFactory)var5.next();

            String var12;
            for(Iterator var11 = var6.getNames().iterator(); var11.hasNext(); var4.append(var12)) {
               var12 = (String)var11.next();
               if (var4.length() > 0) {
                  var4.append(", ");
               }
            }
         }

         this.languages = var4.toString();
      }

   }

   public void addScript(AbstractScript var1) {
      ScriptEngine var2 = this.manager.getEngineByName(var1.getLanguage());
      if (var2 == null) {
         logger.error("No ScriptEngine found for language " + var1.getLanguage() + ". Available languages are: " + this.languages);
      } else {
         if (var2.getFactory().getParameter("THREADING") == null) {
            this.scriptRunners.put(var1.getName(), new ScriptManager.ThreadLocalScriptRunner(var1));
         } else {
            this.scriptRunners.put(var1.getName(), new ScriptManager.MainScriptRunner(var2, var1));
         }

         if (var1 instanceof ScriptFile) {
            ScriptFile var3 = (ScriptFile)var1;
            Path var4 = var3.getPath();
            if (var3.isWatched() && var4 != null) {
               this.watchManager.watchFile(var4.toFile(), this);
            }
         }

      }
   }

   public Bindings createBindings(AbstractScript var1) {
      return this.getScriptRunner(var1).createBindings();
   }

   public AbstractScript getScript(String var1) {
      ScriptManager.ScriptRunner var2 = (ScriptManager.ScriptRunner)this.scriptRunners.get(var1);
      return var2 != null ? var2.getScript() : null;
   }

   public void fileModified(File var1) {
      ScriptManager.ScriptRunner var2 = (ScriptManager.ScriptRunner)this.scriptRunners.get(var1.toString());
      if (var2 == null) {
         logger.info("{} is not a running script");
      } else {
         ScriptEngine var3 = var2.getScriptEngine();
         AbstractScript var4 = var2.getScript();
         if (var3.getFactory().getParameter("THREADING") == null) {
            this.scriptRunners.put(var4.getName(), new ScriptManager.ThreadLocalScriptRunner(var4));
         } else {
            this.scriptRunners.put(var4.getName(), new ScriptManager.MainScriptRunner(var3, var4));
         }

      }
   }

   public Object execute(String var1, final Bindings var2) {
      final ScriptManager.ScriptRunner var3 = (ScriptManager.ScriptRunner)this.scriptRunners.get(var1);
      if (var3 == null) {
         logger.warn("No script named {} could be found");
         return null;
      } else {
         return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               return var3.execute(var2);
            }
         });
      }
   }

   private ScriptManager.ScriptRunner getScriptRunner(AbstractScript var1) {
      return (ScriptManager.ScriptRunner)this.scriptRunners.get(var1.getName());
   }

   private class ThreadLocalScriptRunner extends ScriptManager.AbstractScriptRunner {
      private final AbstractScript script;
      private final ThreadLocal<ScriptManager.MainScriptRunner> runners = new ThreadLocal<ScriptManager.MainScriptRunner>() {
         protected ScriptManager.MainScriptRunner initialValue() {
            ScriptEngine var1 = ScriptManager.this.manager.getEngineByName(ThreadLocalScriptRunner.this.script.getLanguage());
            return ScriptManager.this.new MainScriptRunner(var1, ThreadLocalScriptRunner.this.script);
         }
      };

      public ThreadLocalScriptRunner(AbstractScript var2) {
         super(null);
         this.script = var2;
      }

      public Object execute(Bindings var1) {
         return ((ScriptManager.MainScriptRunner)this.runners.get()).execute(var1);
      }

      public AbstractScript getScript() {
         return this.script;
      }

      public ScriptEngine getScriptEngine() {
         return ((ScriptManager.MainScriptRunner)this.runners.get()).getScriptEngine();
      }
   }

   private class MainScriptRunner extends ScriptManager.AbstractScriptRunner {
      private final AbstractScript script;
      private final CompiledScript compiledScript;
      private final ScriptEngine scriptEngine;

      public MainScriptRunner(final ScriptEngine var2, final AbstractScript var3) {
         super(null);
         this.script = var3;
         this.scriptEngine = var2;
         CompiledScript var4 = null;
         if (var2 instanceof Compilable) {
            ScriptManager.logger.debug((String)"Script {} is compilable", (Object)var3.getName());
            var4 = (CompiledScript)AccessController.doPrivileged(new PrivilegedAction<CompiledScript>() {
               public CompiledScript run() {
                  try {
                     return ((Compilable)var2).compile(var3.getScriptText());
                  } catch (Throwable var2x) {
                     ScriptManager.logger.warn("Error compiling script", var2x);
                     return null;
                  }
               }
            });
         }

         this.compiledScript = var4;
      }

      public ScriptEngine getScriptEngine() {
         return this.scriptEngine;
      }

      public Object execute(Bindings var1) {
         if (this.compiledScript != null) {
            try {
               return this.compiledScript.eval(var1);
            } catch (ScriptException var3) {
               ScriptManager.logger.error((String)("Error running script " + this.script.getName()), (Throwable)var3);
               return null;
            }
         } else {
            try {
               return this.scriptEngine.eval(this.script.getScriptText(), var1);
            } catch (ScriptException var4) {
               ScriptManager.logger.error((String)("Error running script " + this.script.getName()), (Throwable)var4);
               return null;
            }
         }
      }

      public AbstractScript getScript() {
         return this.script;
      }
   }

   private interface ScriptRunner {
      Bindings createBindings();

      Object execute(Bindings var1);

      AbstractScript getScript();

      ScriptEngine getScriptEngine();
   }

   private abstract class AbstractScriptRunner implements ScriptManager.ScriptRunner {
      private static final String KEY_STATUS_LOGGER = "statusLogger";
      private static final String KEY_CONFIGURATION = "configuration";

      private AbstractScriptRunner() {
         super();
      }

      public Bindings createBindings() {
         SimpleBindings var1 = new SimpleBindings();
         var1.put("configuration", ScriptManager.this.configuration);
         var1.put("statusLogger", ScriptManager.logger);
         return var1;
      }

      // $FF: synthetic method
      AbstractScriptRunner(Object var2) {
         this();
      }
   }
}
