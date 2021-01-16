package org.apache.logging.log4j.core.config.plugins.visitors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public abstract class AbstractPluginVisitor<A extends Annotation> implements PluginVisitor<A> {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   protected final Class<A> clazz;
   protected A annotation;
   protected String[] aliases;
   protected Class<?> conversionType;
   protected StrSubstitutor substitutor;
   protected Member member;

   protected AbstractPluginVisitor(Class<A> var1) {
      super();
      this.clazz = var1;
   }

   public PluginVisitor<A> setAnnotation(Annotation var1) {
      Annotation var2 = (Annotation)Objects.requireNonNull(var1, "No annotation was provided");
      if (this.clazz.isInstance(var2)) {
         this.annotation = var2;
      }

      return this;
   }

   public PluginVisitor<A> setAliases(String... var1) {
      this.aliases = var1;
      return this;
   }

   public PluginVisitor<A> setConversionType(Class<?> var1) {
      this.conversionType = (Class)Objects.requireNonNull(var1, "No conversion type class was provided");
      return this;
   }

   public PluginVisitor<A> setStrSubstitutor(StrSubstitutor var1) {
      this.substitutor = (StrSubstitutor)Objects.requireNonNull(var1, "No StrSubstitutor was provided");
      return this;
   }

   public PluginVisitor<A> setMember(Member var1) {
      this.member = var1;
      return this;
   }

   protected static String removeAttributeValue(Map<String, String> var0, String var1, String... var2) {
      Iterator var3 = var0.entrySet().iterator();

      while(true) {
         String var5;
         String var6;
         do {
            if (!var3.hasNext()) {
               return null;
            }

            Entry var4 = (Entry)var3.next();
            var5 = (String)var4.getKey();
            var6 = (String)var4.getValue();
            if (var5.equalsIgnoreCase(var1)) {
               var0.remove(var5);
               return var6;
            }
         } while(var2 == null);

         String[] var7 = var2;
         int var8 = var2.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String var10 = var7[var9];
            if (var5.equalsIgnoreCase(var10)) {
               var0.remove(var5);
               return var6;
            }
         }
      }
   }

   protected Object convert(String var1, Object var2) {
      return var2 instanceof String ? TypeConverters.convert(var1, this.conversionType, Strings.trimToNull((String)var2)) : TypeConverters.convert(var1, this.conversionType, var2);
   }
}
