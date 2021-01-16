package org.apache.logging.log4j.core.config.plugins.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.ConstraintValidator;
import org.apache.logging.log4j.core.config.plugins.validation.ConstraintValidators;
import org.apache.logging.log4j.core.config.plugins.visitors.PluginVisitor;
import org.apache.logging.log4j.core.config.plugins.visitors.PluginVisitors;
import org.apache.logging.log4j.core.util.Builder;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.apache.logging.log4j.core.util.TypeUtil;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StringBuilders;

public class PluginBuilder implements Builder<Object> {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final PluginType<?> pluginType;
   private final Class<?> clazz;
   private Configuration configuration;
   private Node node;
   private LogEvent event;

   public PluginBuilder(PluginType<?> var1) {
      super();
      this.pluginType = var1;
      this.clazz = var1.getPluginClass();
   }

   public PluginBuilder withConfiguration(Configuration var1) {
      this.configuration = var1;
      return this;
   }

   public PluginBuilder withConfigurationNode(Node var1) {
      this.node = var1;
      return this;
   }

   public PluginBuilder forLogEvent(LogEvent var1) {
      this.event = var1;
      return this;
   }

   public Object build() {
      this.verify();

      try {
         LOGGER.debug((String)"Building Plugin[name={}, class={}].", (Object)this.pluginType.getElementName(), (Object)this.pluginType.getPluginClass().getName());
         Builder var1 = createBuilder(this.clazz);
         if (var1 != null) {
            this.injectFields(var1);
            return var1.build();
         }
      } catch (Exception var4) {
         LOGGER.error((String)"Unable to inject fields into builder class for plugin type {}, element {}.", (Object)this.clazz, this.node.getName(), var4);
      }

      try {
         Method var5 = findFactoryMethod(this.clazz);
         Object[] var2 = this.generateParameters(var5);
         return var5.invoke((Object)null, var2);
      } catch (Exception var3) {
         LOGGER.error((String)"Unable to invoke factory method in class {} for element {}.", (Object)this.clazz, this.node.getName(), var3);
         return null;
      }
   }

   private void verify() {
      Objects.requireNonNull(this.configuration, "No Configuration object was set.");
      Objects.requireNonNull(this.node, "No Node object was set.");
   }

   private static Builder<?> createBuilder(Class<?> var0) throws InvocationTargetException, IllegalAccessException {
      Method[] var1 = var0.getDeclaredMethods();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Method var4 = var1[var3];
         if (var4.isAnnotationPresent(PluginBuilderFactory.class) && Modifier.isStatic(var4.getModifiers()) && TypeUtil.isAssignable(Builder.class, var4.getReturnType())) {
            ReflectionUtil.makeAccessible((AccessibleObject)var4);
            return (Builder)var4.invoke((Object)null);
         }
      }

      return null;
   }

   private void injectFields(Builder<?> var1) throws IllegalAccessException {
      List var2 = TypeUtil.getAllDeclaredFields(var1.getClass());
      AccessibleObject.setAccessible((AccessibleObject[])var2.toArray(new Field[0]), true);
      StringBuilder var3 = new StringBuilder();
      boolean var4 = false;
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Field var6 = (Field)var5.next();
         var3.append(var3.length() == 0 ? simpleName(var1) + "(" : ", ");
         Annotation[] var7 = var6.getDeclaredAnnotations();
         String[] var8 = extractPluginAliases(var7);
         Annotation[] var9 = var7;
         int var10 = var7.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            Annotation var12 = var9[var11];
            if (!(var12 instanceof PluginAliases)) {
               PluginVisitor var13 = PluginVisitors.findVisitor(var12.annotationType());
               if (var13 != null) {
                  Object var14 = var13.setAliases(var8).setAnnotation(var12).setConversionType(var6.getType()).setStrSubstitutor(this.configuration.getStrSubstitutor()).setMember(var6).visit(this.configuration, this.node, this.event, var3);
                  if (var14 != null) {
                     var6.set(var1, var14);
                  }
               }
            }
         }

         Collection var15 = ConstraintValidators.findValidators(var7);
         Object var16 = var6.get(var1);
         Iterator var17 = var15.iterator();

         while(var17.hasNext()) {
            ConstraintValidator var18 = (ConstraintValidator)var17.next();
            if (!var18.isValid(var6.getName(), var16)) {
               var4 = true;
            }
         }
      }

      var3.append(var3.length() == 0 ? var1.getClass().getSimpleName() + "()" : ")");
      LOGGER.debug(var3.toString());
      if (var4) {
         throw new ConfigurationException("Arguments given for element " + this.node.getName() + " are invalid");
      } else {
         this.checkForRemainingAttributes();
         this.verifyNodeChildrenUsed();
      }
   }

   private static String simpleName(Object var0) {
      if (var0 == null) {
         return "null";
      } else {
         String var1 = var0.getClass().getName();
         int var2 = var1.lastIndexOf(46);
         return var2 < 0 ? var1 : var1.substring(var2 + 1);
      }
   }

   private static Method findFactoryMethod(Class<?> var0) {
      Method[] var1 = var0.getDeclaredMethods();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Method var4 = var1[var3];
         if (var4.isAnnotationPresent(PluginFactory.class) && Modifier.isStatic(var4.getModifiers())) {
            ReflectionUtil.makeAccessible((AccessibleObject)var4);
            return var4;
         }
      }

      throw new IllegalStateException("No factory method found for class " + var0.getName());
   }

   private Object[] generateParameters(Method var1) {
      StringBuilder var2 = new StringBuilder();
      Class[] var3 = var1.getParameterTypes();
      Annotation[][] var4 = var1.getParameterAnnotations();
      Object[] var5 = new Object[var4.length];
      boolean var6 = false;

      for(int var7 = 0; var7 < var4.length; ++var7) {
         var2.append(var2.length() == 0 ? var1.getName() + "(" : ", ");
         String[] var8 = extractPluginAliases(var4[var7]);
         Annotation[] var9 = var4[var7];
         int var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            Annotation var12 = var9[var11];
            if (!(var12 instanceof PluginAliases)) {
               PluginVisitor var13 = PluginVisitors.findVisitor(var12.annotationType());
               if (var13 != null) {
                  Object var14 = var13.setAliases(var8).setAnnotation(var12).setConversionType(var3[var7]).setStrSubstitutor(this.configuration.getStrSubstitutor()).setMember(var1).visit(this.configuration, this.node, this.event, var2);
                  if (var14 != null) {
                     var5[var7] = var14;
                  }
               }
            }
         }

         Collection var15 = ConstraintValidators.findValidators(var4[var7]);
         Object var16 = var5[var7];
         String var17 = "arg[" + var7 + "](" + simpleName(var16) + ")";
         Iterator var18 = var15.iterator();

         while(var18.hasNext()) {
            ConstraintValidator var19 = (ConstraintValidator)var18.next();
            if (!var19.isValid(var17, var16)) {
               var6 = true;
            }
         }
      }

      var2.append(var2.length() == 0 ? var1.getName() + "()" : ")");
      this.checkForRemainingAttributes();
      this.verifyNodeChildrenUsed();
      LOGGER.debug(var2.toString());
      if (var6) {
         throw new ConfigurationException("Arguments given for element " + this.node.getName() + " are invalid");
      } else {
         return var5;
      }
   }

   private static String[] extractPluginAliases(Annotation... var0) {
      String[] var1 = null;
      Annotation[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Annotation var5 = var2[var4];
         if (var5 instanceof PluginAliases) {
            var1 = ((PluginAliases)var5).value();
         }
      }

      return var1;
   }

   private void checkForRemainingAttributes() {
      Map var1 = this.node.getAttributes();
      if (!var1.isEmpty()) {
         StringBuilder var2 = new StringBuilder();

         String var4;
         for(Iterator var3 = var1.keySet().iterator(); var3.hasNext(); StringBuilders.appendDqValue(var2, var4)) {
            var4 = (String)var3.next();
            if (var2.length() == 0) {
               var2.append(this.node.getName());
               var2.append(" contains ");
               if (var1.size() == 1) {
                  var2.append("an invalid element or attribute ");
               } else {
                  var2.append("invalid attributes ");
               }
            } else {
               var2.append(", ");
            }
         }

         LOGGER.error(var2.toString());
      }

   }

   private void verifyNodeChildrenUsed() {
      List var1 = this.node.getChildren();
      if (!this.pluginType.isDeferChildren() && !var1.isEmpty()) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            Node var3 = (Node)var2.next();
            String var4 = this.node.getType().getElementName();
            String var5 = var4.equals(this.node.getName()) ? this.node.getName() : var4 + ' ' + this.node.getName();
            LOGGER.error((String)"{} has no parameter that matches element {}", (Object)var5, (Object)var3.getName());
         }
      }

   }
}
