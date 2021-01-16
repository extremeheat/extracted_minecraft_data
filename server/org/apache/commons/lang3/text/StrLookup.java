package org.apache.commons.lang3.text;

import java.util.Map;

public abstract class StrLookup<V> {
   private static final StrLookup<String> NONE_LOOKUP = new StrLookup.MapStrLookup((Map)null);
   private static final StrLookup<String> SYSTEM_PROPERTIES_LOOKUP = new StrLookup.SystemPropertiesStrLookup();

   public static StrLookup<?> noneLookup() {
      return NONE_LOOKUP;
   }

   public static StrLookup<String> systemPropertiesLookup() {
      return SYSTEM_PROPERTIES_LOOKUP;
   }

   public static <V> StrLookup<V> mapLookup(Map<String, V> var0) {
      return new StrLookup.MapStrLookup(var0);
   }

   protected StrLookup() {
      super();
   }

   public abstract String lookup(String var1);

   private static class SystemPropertiesStrLookup extends StrLookup<String> {
      private SystemPropertiesStrLookup() {
         super();
      }

      public String lookup(String var1) {
         if (var1.length() > 0) {
            try {
               return System.getProperty(var1);
            } catch (SecurityException var3) {
            }
         }

         return null;
      }

      // $FF: synthetic method
      SystemPropertiesStrLookup(Object var1) {
         this();
      }
   }

   static class MapStrLookup<V> extends StrLookup<V> {
      private final Map<String, V> map;

      MapStrLookup(Map<String, V> var1) {
         super();
         this.map = var1;
      }

      public String lookup(String var1) {
         if (this.map == null) {
            return null;
         } else {
            Object var2 = this.map.get(var1);
            return var2 == null ? null : var2.toString();
         }
      }
   }
}
