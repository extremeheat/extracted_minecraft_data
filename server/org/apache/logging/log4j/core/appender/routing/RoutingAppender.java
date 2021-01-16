package org.apache.logging.log4j.core.appender.routing;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javax.script.Bindings;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptManager;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(
   name = "Routing",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class RoutingAppender extends AbstractAppender {
   public static final String STATIC_VARIABLES_KEY = "staticVariables";
   private static final String DEFAULT_KEY = "ROUTING_APPENDER_DEFAULT";
   private final Routes routes;
   private Route defaultRoute;
   private final Configuration configuration;
   private final ConcurrentMap<String, AppenderControl> appenders;
   private final RewritePolicy rewritePolicy;
   private final PurgePolicy purgePolicy;
   private final AbstractScript defaultRouteScript;
   private final ConcurrentMap<Object, Object> scriptStaticVariables;

   @PluginBuilderFactory
   public static <B extends RoutingAppender.Builder<B>> B newBuilder() {
      return (RoutingAppender.Builder)(new RoutingAppender.Builder()).asBuilder();
   }

   private RoutingAppender(String var1, Filter var2, boolean var3, Routes var4, RewritePolicy var5, Configuration var6, PurgePolicy var7, AbstractScript var8) {
      super(var1, var2, (Layout)null, var3);
      this.appenders = new ConcurrentHashMap();
      this.scriptStaticVariables = new ConcurrentHashMap();
      this.routes = var4;
      this.configuration = var6;
      this.rewritePolicy = var5;
      this.purgePolicy = var7;
      if (this.purgePolicy != null) {
         this.purgePolicy.initialize(this);
      }

      this.defaultRouteScript = var8;
      Route var9 = null;
      Route[] var10 = var4.getRoutes();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Route var13 = var10[var12];
         if (var13.getKey() == null) {
            if (var9 == null) {
               var9 = var13;
            } else {
               this.error("Multiple default routes. Route " + var13.toString() + " will be ignored");
            }
         }
      }

      this.defaultRoute = var9;
   }

   public void start() {
      Route var4;
      if (this.defaultRouteScript != null) {
         if (this.configuration == null) {
            this.error("No Configuration defined for RoutingAppender; required for Script element.");
         } else {
            ScriptManager var1 = this.configuration.getScriptManager();
            var1.addScript(this.defaultRouteScript);
            Bindings var2 = var1.createBindings(this.defaultRouteScript);
            var2.put("staticVariables", this.scriptStaticVariables);
            Object var3 = var1.execute(this.defaultRouteScript.getName(), var2);
            var4 = this.routes.getRoute(Objects.toString(var3, (String)null));
            if (var4 != null) {
               this.defaultRoute = var4;
            }
         }
      }

      Route[] var7 = this.routes.getRoutes();
      int var8 = var7.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         var4 = var7[var9];
         if (var4.getAppenderRef() != null) {
            Appender var5 = this.configuration.getAppender(var4.getAppenderRef());
            if (var5 != null) {
               String var6 = var4 == this.defaultRoute ? "ROUTING_APPENDER_DEFAULT" : var4.getKey();
               this.appenders.put(var6, new AppenderControl(var5, (Level)null, (Filter)null));
            } else {
               this.error("Appender " + var4.getAppenderRef() + " cannot be located. Route ignored");
            }
         }
      }

      super.start();
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      super.stop(var1, var3, false);
      Map var4 = this.configuration.getAppenders();
      Iterator var5 = this.appenders.entrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         Appender var7 = ((AppenderControl)var6.getValue()).getAppender();
         if (!var4.containsKey(var7.getName())) {
            if (var7 instanceof LifeCycle2) {
               ((LifeCycle2)var7).stop(var1, var3);
            } else {
               var7.stop();
            }
         }
      }

      this.setStopped();
      return true;
   }

   public void append(LogEvent var1) {
      if (this.rewritePolicy != null) {
         var1 = this.rewritePolicy.rewrite(var1);
      }

      String var2 = this.routes.getPattern(var1, this.scriptStaticVariables);
      String var3 = var2 != null ? this.configuration.getStrSubstitutor().replace(var1, var2) : this.defaultRoute.getKey();
      AppenderControl var4 = this.getControl(var3, var1);
      if (var4 != null) {
         var4.callAppender(var1);
      }

      if (this.purgePolicy != null) {
         this.purgePolicy.update(var3, var1);
      }

   }

   private synchronized AppenderControl getControl(String var1, LogEvent var2) {
      AppenderControl var3 = (AppenderControl)this.appenders.get(var1);
      if (var3 != null) {
         return var3;
      } else {
         Route var4 = null;
         Route[] var5 = this.routes.getRoutes();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Route var8 = var5[var7];
            if (var8.getAppenderRef() == null && var1.equals(var8.getKey())) {
               var4 = var8;
               break;
            }
         }

         if (var4 == null) {
            var4 = this.defaultRoute;
            var3 = (AppenderControl)this.appenders.get("ROUTING_APPENDER_DEFAULT");
            if (var3 != null) {
               return var3;
            }
         }

         if (var4 != null) {
            Appender var9 = this.createAppender(var4, var2);
            if (var9 == null) {
               return null;
            }

            var3 = new AppenderControl(var9, (Level)null, (Filter)null);
            this.appenders.put(var1, var3);
         }

         return var3;
      }
   }

   private Appender createAppender(Route var1, LogEvent var2) {
      Node var3 = var1.getNode();
      Iterator var4 = var3.getChildren().iterator();

      Node var5;
      do {
         if (!var4.hasNext()) {
            this.error("No Appender was configured for route " + var1.getKey());
            return null;
         }

         var5 = (Node)var4.next();
      } while(!var5.getType().getElementName().equals("appender"));

      Node var6 = new Node(var5);
      this.configuration.createConfiguration(var6, var2);
      if (var6.getObject() instanceof Appender) {
         Appender var7 = (Appender)var6.getObject();
         var7.start();
         return var7;
      } else {
         this.error("Unable to create Appender of type " + var5.getName());
         return null;
      }
   }

   public Map<String, AppenderControl> getAppenders() {
      return Collections.unmodifiableMap(this.appenders);
   }

   public void deleteAppender(String var1) {
      LOGGER.debug("Deleting route with " + var1 + " key ");
      AppenderControl var2 = (AppenderControl)this.appenders.remove(var1);
      if (null != var2) {
         LOGGER.debug("Stopping route with " + var1 + " key");
         var2.getAppender().stop();
      } else {
         LOGGER.debug("Route with " + var1 + " key already deleted");
      }

   }

   /** @deprecated */
   @Deprecated
   public static RoutingAppender createAppender(String var0, String var1, Routes var2, Configuration var3, RewritePolicy var4, PurgePolicy var5, Filter var6) {
      boolean var7 = Booleans.parseBoolean(var1, true);
      if (var0 == null) {
         LOGGER.error("No name provided for RoutingAppender");
         return null;
      } else if (var2 == null) {
         LOGGER.error("No routes defined for RoutingAppender");
         return null;
      } else {
         return new RoutingAppender(var0, var6, var7, var2, var4, var3, var5, (AbstractScript)null);
      }
   }

   public Route getDefaultRoute() {
      return this.defaultRoute;
   }

   public AbstractScript getDefaultRouteScript() {
      return this.defaultRouteScript;
   }

   public PurgePolicy getPurgePolicy() {
      return this.purgePolicy;
   }

   public RewritePolicy getRewritePolicy() {
      return this.rewritePolicy;
   }

   public Routes getRoutes() {
      return this.routes;
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public ConcurrentMap<Object, Object> getScriptStaticVariables() {
      return this.scriptStaticVariables;
   }

   // $FF: synthetic method
   RoutingAppender(String var1, Filter var2, boolean var3, Routes var4, RewritePolicy var5, Configuration var6, PurgePolicy var7, AbstractScript var8, Object var9) {
      this(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static class Builder<B extends RoutingAppender.Builder<B>> extends AbstractAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<RoutingAppender> {
      @PluginElement("Script")
      private AbstractScript defaultRouteScript;
      @PluginElement("Routes")
      private Routes routes;
      @PluginElement("RewritePolicy")
      private RewritePolicy rewritePolicy;
      @PluginElement("PurgePolicy")
      private PurgePolicy purgePolicy;

      public Builder() {
         super();
      }

      public RoutingAppender build() {
         String var1 = this.getName();
         if (var1 == null) {
            RoutingAppender.LOGGER.error("No name defined for this RoutingAppender");
            return null;
         } else if (this.routes == null) {
            RoutingAppender.LOGGER.error((String)"No routes defined for RoutingAppender {}", (Object)var1);
            return null;
         } else {
            return new RoutingAppender(var1, this.getFilter(), this.isIgnoreExceptions(), this.routes, this.rewritePolicy, this.getConfiguration(), this.purgePolicy, this.defaultRouteScript);
         }
      }

      public Routes getRoutes() {
         return this.routes;
      }

      public AbstractScript getDefaultRouteScript() {
         return this.defaultRouteScript;
      }

      public RewritePolicy getRewritePolicy() {
         return this.rewritePolicy;
      }

      public PurgePolicy getPurgePolicy() {
         return this.purgePolicy;
      }

      public B withRoutes(Routes var1) {
         this.routes = var1;
         return (RoutingAppender.Builder)this.asBuilder();
      }

      public B withDefaultRouteScript(AbstractScript var1) {
         this.defaultRouteScript = var1;
         return (RoutingAppender.Builder)this.asBuilder();
      }

      public B withRewritePolicy(RewritePolicy var1) {
         this.rewritePolicy = var1;
         return (RoutingAppender.Builder)this.asBuilder();
      }

      public void withPurgePolicy(PurgePolicy var1) {
         this.purgePolicy = var1;
      }
   }
}
