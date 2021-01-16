package org.apache.logging.log4j.core.config.composite;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.filter.CompositeFilter;

public class DefaultMergeStrategy implements MergeStrategy {
   private static final String APPENDERS = "appenders";
   private static final String PROPERTIES = "properties";
   private static final String LOGGERS = "loggers";
   private static final String SCRIPTS = "scripts";
   private static final String FILTERS = "filters";
   private static final String STATUS = "status";
   private static final String NAME = "name";
   private static final String REF = "ref";

   public DefaultMergeStrategy() {
      super();
   }

   public void mergeRootProperties(Node var1, AbstractConfiguration var2) {
      Iterator var3 = var2.getRootNode().getAttributes().entrySet().iterator();

      label56:
      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         boolean var5 = false;
         Iterator var6 = var1.getAttributes().entrySet().iterator();

         while(true) {
            Entry var7;
            do {
               if (!var6.hasNext()) {
                  if (!var5) {
                     var1.getAttributes().put(var4.getKey(), var4.getValue());
                  }
                  continue label56;
               }

               var7 = (Entry)var6.next();
            } while(!((String)var7.getKey()).equalsIgnoreCase((String)var4.getKey()));

            if (((String)var4.getKey()).equalsIgnoreCase("status")) {
               Level var10 = Level.getLevel(((String)var7.getValue()).toUpperCase());
               Level var11 = Level.getLevel(((String)var4.getValue()).toUpperCase());
               if (var10 != null && var11 != null) {
                  if (var11.isLessSpecificThan(var10)) {
                     var7.setValue(var4.getValue());
                  }
               } else if (var11 != null) {
                  var7.setValue(var4.getValue());
               }
            } else if (((String)var4.getKey()).equalsIgnoreCase("monitorInterval")) {
               int var8 = Integer.parseInt((String)var4.getValue());
               int var9 = Integer.parseInt((String)var7.getValue());
               if (var9 == 0 || var8 < var9) {
                  var7.setValue(var4.getValue());
               }
            } else {
               var7.setValue(var4.getValue());
            }

            var5 = true;
         }
      }

   }

   public void mergConfigurations(Node var1, Node var2, PluginManager var3) {
      Iterator var4 = var2.getChildren().iterator();

      while(var4.hasNext()) {
         Node var5 = (Node)var4.next();
         boolean var6 = this.isFilterNode(var5);
         boolean var7 = false;
         Iterator var8 = var1.getChildren().iterator();

         label157:
         while(true) {
            label155:
            while(true) {
               if (!var8.hasNext()) {
                  break label157;
               }

               Node var9 = (Node)var8.next();
               if (var6) {
                  if (this.isFilterNode(var9)) {
                     this.updateFilterNode(var1, var9, var5, var3);
                     var7 = true;
                     break label157;
                  }
               } else if (var9.getName().equalsIgnoreCase(var5.getName())) {
                  String var10 = var9.getName().toLowerCase();
                  byte var11 = -1;
                  switch(var10.hashCode()) {
                  case -926053069:
                     if (var10.equals("properties")) {
                        var11 = 0;
                     }
                     break;
                  case 342277347:
                     if (var10.equals("loggers")) {
                        var11 = 3;
                     }
                     break;
                  case 1926514952:
                     if (var10.equals("scripts")) {
                        var11 = 1;
                     }
                     break;
                  case 2009213964:
                     if (var10.equals("appenders")) {
                        var11 = 2;
                     }
                  }

                  Node var15;
                  switch(var11) {
                  case 0:
                  case 1:
                  case 2:
                     Iterator var22 = var5.getChildren().iterator();

                     Node var23;
                     for(; var22.hasNext(); var9.getChildren().add(var23)) {
                        var23 = (Node)var22.next();
                        Iterator var24 = var9.getChildren().iterator();

                        while(var24.hasNext()) {
                           var15 = (Node)var24.next();
                           if (((String)var15.getAttributes().get("name")).equals(var23.getAttributes().get("name"))) {
                              var9.getChildren().remove(var15);
                              break;
                           }
                        }
                     }

                     var7 = true;
                     break;
                  case 3:
                     HashMap var12 = new HashMap();
                     Iterator var13 = var9.getChildren().iterator();

                     Node var14;
                     while(var13.hasNext()) {
                        var14 = (Node)var13.next();
                        var12.put(var14.getName(), var14);
                     }

                     var13 = var5.getChildren().iterator();

                     while(true) {
                        label103:
                        while(var13.hasNext()) {
                           var14 = (Node)var13.next();
                           var15 = this.getLoggerNode(var9, (String)var14.getAttributes().get("name"));
                           Node var16 = new Node(var9, var14.getName(), var14.getType());
                           if (var15 != null) {
                              var15.getAttributes().putAll(var14.getAttributes());
                              Iterator var17 = var14.getChildren().iterator();

                              while(true) {
                                 while(true) {
                                    if (!var17.hasNext()) {
                                       continue label103;
                                    }

                                    Node var18 = (Node)var17.next();
                                    Iterator var20;
                                    Node var21;
                                    if (this.isFilterNode(var18)) {
                                       boolean var25 = false;
                                       var20 = var15.getChildren().iterator();

                                       while(var20.hasNext()) {
                                          var21 = (Node)var20.next();
                                          if (this.isFilterNode(var21)) {
                                             this.updateFilterNode(var16, var21, var18, var3);
                                             var25 = true;
                                             break;
                                          }
                                       }

                                       if (!var25) {
                                          Node var26 = new Node(var16, var18.getName(), var18.getType());
                                          var15.getChildren().add(var26);
                                       }
                                    } else {
                                       Node var19 = new Node(var16, var18.getName(), var18.getType());
                                       var19.getAttributes().putAll(var18.getAttributes());
                                       var19.getChildren().addAll(var18.getChildren());
                                       if (var19.getName().equalsIgnoreCase("AppenderRef")) {
                                          var20 = var15.getChildren().iterator();

                                          while(var20.hasNext()) {
                                             var21 = (Node)var20.next();
                                             if (this.isSameReference(var21, var19)) {
                                                var15.getChildren().remove(var21);
                                                break;
                                             }
                                          }
                                       } else {
                                          var20 = var15.getChildren().iterator();

                                          while(var20.hasNext()) {
                                             var21 = (Node)var20.next();
                                             if (this.isSameName(var21, var19)) {
                                                var15.getChildren().remove(var21);
                                                break;
                                             }
                                          }
                                       }

                                       var15.getChildren().add(var19);
                                    }
                                 }
                              }
                           } else {
                              var16.getAttributes().putAll(var14.getAttributes());
                              var16.getChildren().addAll(var14.getChildren());
                              var9.getChildren().add(var16);
                           }
                        }

                        var7 = true;
                        continue label155;
                     }
                  default:
                     var9.getChildren().addAll(var5.getChildren());
                     var7 = true;
                  }
               }
            }
         }

         if (!var7) {
            if (var5.getName().equalsIgnoreCase("Properties")) {
               var1.getChildren().add(0, var5);
            } else {
               var1.getChildren().add(var5);
            }
         }
      }

   }

   private Node getLoggerNode(Node var1, String var2) {
      Iterator var3 = var1.getChildren().iterator();

      Node var4;
      String var5;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         var4 = (Node)var3.next();
         var5 = (String)var4.getAttributes().get("name");
         if (var2 == null && var5 == null) {
            return var4;
         }
      } while(var5 == null || !var5.equals(var2));

      return var4;
   }

   private void updateFilterNode(Node var1, Node var2, Node var3, PluginManager var4) {
      if (CompositeFilter.class.isAssignableFrom(var2.getType().getPluginClass())) {
         Node var5 = new Node(var2, var3.getName(), var3.getType());
         var5.getChildren().addAll(var3.getChildren());
         var5.getAttributes().putAll(var3.getAttributes());
         var2.getChildren().add(var5);
      } else {
         PluginType var10 = var4.getPluginType("filters");
         Node var6 = new Node(var2, "filters", var10);
         Node var7 = new Node(var6, var3.getName(), var3.getType());
         var7.getAttributes().putAll(var3.getAttributes());
         List var8 = var6.getChildren();
         var8.add(var2);
         var8.add(var7);
         List var9 = var1.getChildren();
         var9.remove(var2);
         var9.add(var6);
      }

   }

   private boolean isFilterNode(Node var1) {
      return Filter.class.isAssignableFrom(var1.getType().getPluginClass());
   }

   private boolean isSameName(Node var1, Node var2) {
      String var3 = (String)var1.getAttributes().get("name");
      return var3 != null && var3.toLowerCase().equals(((String)var2.getAttributes().get("name")).toLowerCase());
   }

   private boolean isSameReference(Node var1, Node var2) {
      String var3 = (String)var1.getAttributes().get("ref");
      return var3 != null && var3.toLowerCase().equals(((String)var2.getAttributes().get("ref")).toLowerCase());
   }
}
