package org.apache.logging.log4j.core.appender;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginNode;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "AppenderSet",
   category = "Core",
   printObject = true,
   deferChildren = true
)
public class AppenderSet {
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private final Configuration configuration;
   private final Map<String, Node> nodeMap;

   @PluginBuilderFactory
   public static AppenderSet.Builder newBuilder() {
      return new AppenderSet.Builder();
   }

   private AppenderSet(Configuration var1, Map<String, Node> var2) {
      super();
      this.configuration = var1;
      this.nodeMap = var2;
   }

   public Appender createAppender(String var1, String var2) {
      Node var3 = (Node)this.nodeMap.get(var1);
      if (var3 == null) {
         LOGGER.error("No node named {} in {}", var1, this);
         return null;
      } else {
         var3.getAttributes().put("name", var2);
         if (var3.getType().getElementName().equals("appender")) {
            Node var4 = new Node(var3);
            this.configuration.createConfiguration(var4, (LogEvent)null);
            if (var4.getObject() instanceof Appender) {
               Appender var5 = (Appender)var4.getObject();
               var5.start();
               return var5;
            } else {
               LOGGER.error("Unable to create Appender of type " + var3.getName());
               return null;
            }
         } else {
            LOGGER.error("No Appender was configured for name {} " + var1);
            return null;
         }
      }
   }

   // $FF: synthetic method
   AppenderSet(Configuration var1, Map var2, Object var3) {
      this(var1, var2);
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<AppenderSet> {
      @PluginNode
      private Node node;
      @PluginConfiguration
      @Required
      private Configuration configuration;

      public Builder() {
         super();
      }

      public AppenderSet build() {
         if (this.configuration == null) {
            AppenderSet.LOGGER.error("Configuration is missing from AppenderSet {}", this);
            return null;
         } else if (this.node == null) {
            AppenderSet.LOGGER.error("No node in AppenderSet {}", this);
            return null;
         } else {
            List var1 = this.node.getChildren();
            if (var1 == null) {
               AppenderSet.LOGGER.error("No children node in AppenderSet {}", this);
               return null;
            } else {
               HashMap var2 = new HashMap(var1.size());
               Iterator var3 = var1.iterator();

               while(var3.hasNext()) {
                  Node var4 = (Node)var3.next();
                  String var5 = (String)var4.getAttributes().get("name");
                  if (var5 == null) {
                     AppenderSet.LOGGER.error("The attribute 'name' is missing from from the node {} in AppenderSet {}", var4, var1);
                  } else {
                     var2.put(var5, var4);
                  }
               }

               return new AppenderSet(this.configuration, var2);
            }
         }
      }

      public Node getNode() {
         return this.node;
      }

      public Configuration getConfiguration() {
         return this.configuration;
      }

      public AppenderSet.Builder withNode(Node var1) {
         this.node = var1;
         return this;
      }

      public AppenderSet.Builder withConfiguration(Configuration var1) {
         this.configuration = var1;
         return this;
      }

      public String toString() {
         return this.getClass().getName() + " [node=" + this.node + ", configuration=" + this.configuration + "]";
      }
   }
}
