package org.apache.logging.log4j.core.config.json;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.ConfiguratonFileWatcher;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.apache.logging.log4j.core.config.status.StatusConfiguration;
import org.apache.logging.log4j.core.util.Patterns;

public class JsonConfiguration extends AbstractConfiguration implements Reconfigurable {
   private static final String[] VERBOSE_CLASSES = new String[]{ResolverUtil.class.getName()};
   private final List<JsonConfiguration.Status> status = new ArrayList();
   private JsonNode root;

   public JsonConfiguration(LoggerContext var1, ConfigurationSource var2) {
      super(var1, var2);
      File var3 = var2.getFile();

      try {
         InputStream var5 = var2.getInputStream();
         Throwable var6 = null;

         byte[] var4;
         try {
            var4 = toByteArray(var5);
         } catch (Throwable var19) {
            var6 = var19;
            throw var19;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var18) {
                     var6.addSuppressed(var18);
                  }
               } else {
                  var5.close();
               }
            }

         }

         ByteArrayInputStream var22 = new ByteArrayInputStream(var4);
         this.root = this.getObjectMapper().readTree(var22);
         JsonNode var7;
         if (this.root.size() == 1) {
            for(Iterator var23 = this.root.iterator(); var23.hasNext(); this.root = var7) {
               var7 = (JsonNode)var23.next();
            }
         }

         this.processAttributes(this.rootNode, this.root);
         StatusConfiguration var24 = (new StatusConfiguration()).withVerboseClasses(VERBOSE_CLASSES).withStatus(this.getDefaultStatus());
         Iterator var25 = this.rootNode.getAttributes().entrySet().iterator();

         while(var25.hasNext()) {
            Entry var8 = (Entry)var25.next();
            String var9 = (String)var8.getKey();
            String var10 = this.getStrSubstitutor().replace((String)var8.getValue());
            if ("status".equalsIgnoreCase(var9)) {
               var24.withStatus(var10);
            } else if ("dest".equalsIgnoreCase(var9)) {
               var24.withDestination(var10);
            } else if ("shutdownHook".equalsIgnoreCase(var9)) {
               this.isShutdownHookEnabled = !"disable".equalsIgnoreCase(var10);
            } else if ("shutdownTimeout".equalsIgnoreCase(var9)) {
               this.shutdownTimeoutMillis = Long.parseLong(var10);
            } else if ("verbose".equalsIgnoreCase((String)var8.getKey())) {
               var24.withVerbosity(var10);
            } else if ("packages".equalsIgnoreCase(var9)) {
               this.pluginPackages.addAll(Arrays.asList(var10.split(Patterns.COMMA_SEPARATOR)));
            } else if ("name".equalsIgnoreCase(var9)) {
               this.setName(var10);
            } else if ("monitorInterval".equalsIgnoreCase(var9)) {
               int var11 = Integer.parseInt(var10);
               if (var11 > 0) {
                  this.getWatchManager().setIntervalSeconds(var11);
                  if (var3 != null) {
                     ConfiguratonFileWatcher var12 = new ConfiguratonFileWatcher(this, this.listeners);
                     this.getWatchManager().watchFile(var3, var12);
                  }
               }
            } else if ("advertiser".equalsIgnoreCase(var9)) {
               this.createAdvertiser(var10, var2, var4, "application/json");
            }
         }

         var24.initialize();
         if (this.getName() == null) {
            this.setName(var2.getLocation());
         }
      } catch (Exception var21) {
         LOGGER.error((String)("Error parsing " + var2.getLocation()), (Throwable)var21);
      }

   }

   protected ObjectMapper getObjectMapper() {
      return (new ObjectMapper()).configure(Feature.ALLOW_COMMENTS, true);
   }

   public void setup() {
      Iterator var1 = this.root.fields();
      List var2 = this.rootNode.getChildren();

      while(var1.hasNext()) {
         Entry var3 = (Entry)var1.next();
         JsonNode var4 = (JsonNode)var3.getValue();
         if (var4.isObject()) {
            LOGGER.debug("Processing node for object {}", var3.getKey());
            var2.add(this.constructNode((String)var3.getKey(), this.rootNode, var4));
         } else if (var4.isArray()) {
            LOGGER.error("Arrays are not supported at the root configuration.");
         }
      }

      LOGGER.debug("Completed parsing configuration");
      if (this.status.size() > 0) {
         Iterator var5 = this.status.iterator();

         while(var5.hasNext()) {
            JsonConfiguration.Status var6 = (JsonConfiguration.Status)var5.next();
            LOGGER.error((String)"Error processing element {}: {}", (Object)var6.name, (Object)var6.errorType);
         }
      }

   }

   public Configuration reconfigure() {
      try {
         ConfigurationSource var1 = this.getConfigurationSource().resetInputStream();
         return var1 == null ? null : new JsonConfiguration(this.getLoggerContext(), var1);
      } catch (IOException var2) {
         LOGGER.error((String)"Cannot locate file {}", (Object)this.getConfigurationSource(), (Object)var2);
         return null;
      }
   }

   private Node constructNode(String var1, Node var2, JsonNode var3) {
      PluginType var4 = this.pluginManager.getPluginType(var1);
      Node var5 = new Node(var2, var1, var4);
      this.processAttributes(var5, var3);
      Iterator var6 = var3.fields();
      List var7 = var5.getChildren();

      while(true) {
         while(var6.hasNext()) {
            Entry var8 = (Entry)var6.next();
            JsonNode var9 = (JsonNode)var8.getValue();
            if (!var9.isArray() && !var9.isObject()) {
               LOGGER.debug((String)"Node {} is of type {}", (Object)var8.getKey(), (Object)var9.getNodeType());
            } else {
               if (var4 == null) {
                  this.status.add(new JsonConfiguration.Status(var1, var9, JsonConfiguration.ErrorType.CLASS_NOT_FOUND));
               }

               if (!var9.isArray()) {
                  LOGGER.debug("Processing node for object {}", var8.getKey());
                  var7.add(this.constructNode((String)var8.getKey(), var5, var9));
               } else {
                  LOGGER.debug("Processing node for array {}", var8.getKey());

                  for(int var10 = 0; var10 < var9.size(); ++var10) {
                     String var11 = this.getType(var9.get(var10), (String)var8.getKey());
                     PluginType var12 = this.pluginManager.getPluginType(var11);
                     Node var13 = new Node(var5, (String)var8.getKey(), var12);
                     this.processAttributes(var13, var9.get(var10));
                     if (var11.equals(var8.getKey())) {
                        LOGGER.debug((String)"Processing {}[{}]", (Object)var8.getKey(), (Object)var10);
                     } else {
                        LOGGER.debug((String)"Processing {} {}[{}]", (Object)var11, var8.getKey(), var10);
                     }

                     Iterator var14 = var9.get(var10).fields();
                     List var15 = var13.getChildren();

                     while(true) {
                        while(var14.hasNext()) {
                           Entry var16 = (Entry)var14.next();
                           if (((JsonNode)var16.getValue()).isObject()) {
                              LOGGER.debug("Processing node for object {}", var16.getKey());
                              var15.add(this.constructNode((String)var16.getKey(), var13, (JsonNode)var16.getValue()));
                           } else if (((JsonNode)var16.getValue()).isArray()) {
                              JsonNode var17 = (JsonNode)var16.getValue();
                              String var18 = (String)var16.getKey();
                              LOGGER.debug((String)"Processing array for object {}", (Object)var18);

                              for(int var19 = 0; var19 < var17.size(); ++var19) {
                                 var15.add(this.constructNode(var18, var13, var17.get(var19)));
                              }
                           }
                        }

                        var7.add(var13);
                        break;
                     }
                  }
               }
            }
         }

         String var20;
         if (var4 == null) {
            var20 = "null";
         } else {
            var20 = var4.getElementName() + ':' + var4.getPluginClass();
         }

         String var21 = var5.getParent() == null ? "null" : (var5.getParent().getName() == null ? "root" : var5.getParent().getName());
         LOGGER.debug((String)"Returning {} with parent {} of type {}", (Object)var5.getName(), var21, var20);
         return var5;
      }
   }

   private String getType(JsonNode var1, String var2) {
      Iterator var3 = var1.fields();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (((String)var4.getKey()).equalsIgnoreCase("type")) {
            JsonNode var5 = (JsonNode)var4.getValue();
            if (var5.isValueNode()) {
               return var5.asText();
            }
         }
      }

      return var2;
   }

   private void processAttributes(Node var1, JsonNode var2) {
      Map var3 = var1.getAttributes();
      Iterator var4 = var2.fields();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         if (!((String)var5.getKey()).equalsIgnoreCase("type")) {
            JsonNode var6 = (JsonNode)var5.getValue();
            if (var6.isValueNode()) {
               var3.put(var5.getKey(), var6.asText());
            }
         }
      }

   }

   public String toString() {
      return this.getClass().getSimpleName() + "[location=" + this.getConfigurationSource() + "]";
   }

   private static class Status {
      private final JsonNode node;
      private final String name;
      private final JsonConfiguration.ErrorType errorType;

      public Status(String var1, JsonNode var2, JsonConfiguration.ErrorType var3) {
         super();
         this.name = var1;
         this.node = var2;
         this.errorType = var3;
      }

      public String toString() {
         return "Status [name=" + this.name + ", errorType=" + this.errorType + ", node=" + this.node + "]";
      }
   }

   private static enum ErrorType {
      CLASS_NOT_FOUND;

      private ErrorType() {
      }
   }
}
