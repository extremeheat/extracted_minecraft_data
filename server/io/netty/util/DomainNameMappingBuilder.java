package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public final class DomainNameMappingBuilder<V> {
   private final V defaultValue;
   private final Map<String, V> map;

   public DomainNameMappingBuilder(V var1) {
      this(4, var1);
   }

   public DomainNameMappingBuilder(int var1, V var2) {
      super();
      this.defaultValue = ObjectUtil.checkNotNull(var2, "defaultValue");
      this.map = new LinkedHashMap(var1);
   }

   public DomainNameMappingBuilder<V> add(String var1, V var2) {
      this.map.put(ObjectUtil.checkNotNull(var1, "hostname"), ObjectUtil.checkNotNull(var2, "output"));
      return this;
   }

   public DomainNameMapping<V> build() {
      return new DomainNameMappingBuilder.ImmutableDomainNameMapping(this.defaultValue, this.map);
   }

   private static final class ImmutableDomainNameMapping<V> extends DomainNameMapping<V> {
      private static final String REPR_HEADER = "ImmutableDomainNameMapping(default: ";
      private static final String REPR_MAP_OPENING = ", map: {";
      private static final String REPR_MAP_CLOSING = "})";
      private static final int REPR_CONST_PART_LENGTH = "ImmutableDomainNameMapping(default: ".length() + ", map: {".length() + "})".length();
      private final String[] domainNamePatterns;
      private final V[] values;
      private final Map<String, V> map;

      private ImmutableDomainNameMapping(V var1, Map<String, V> var2) {
         super((Map)null, var1);
         Set var3 = var2.entrySet();
         int var4 = var3.size();
         this.domainNamePatterns = new String[var4];
         this.values = (Object[])(new Object[var4]);
         LinkedHashMap var5 = new LinkedHashMap(var2.size());
         int var6 = 0;

         for(Iterator var7 = var3.iterator(); var7.hasNext(); ++var6) {
            Entry var8 = (Entry)var7.next();
            String var9 = normalizeHostname((String)var8.getKey());
            Object var10 = var8.getValue();
            this.domainNamePatterns[var6] = var9;
            this.values[var6] = var10;
            var5.put(var9, var10);
         }

         this.map = Collections.unmodifiableMap(var5);
      }

      /** @deprecated */
      @Deprecated
      public DomainNameMapping<V> add(String var1, V var2) {
         throw new UnsupportedOperationException("Immutable DomainNameMapping does not support modification after initial creation");
      }

      public V map(String var1) {
         if (var1 != null) {
            var1 = normalizeHostname(var1);
            int var2 = this.domainNamePatterns.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               if (matches(this.domainNamePatterns[var3], var1)) {
                  return this.values[var3];
               }
            }
         }

         return this.defaultValue;
      }

      public Map<String, V> asMap() {
         return this.map;
      }

      public String toString() {
         String var1 = this.defaultValue.toString();
         int var2 = this.domainNamePatterns.length;
         if (var2 == 0) {
            return "ImmutableDomainNameMapping(default: " + var1 + ", map: {" + "})";
         } else {
            String var3 = this.domainNamePatterns[0];
            String var4 = this.values[0].toString();
            int var5 = var3.length() + var4.length() + 3;
            int var6 = estimateBufferSize(var1.length(), var2, var5);
            StringBuilder var7 = (new StringBuilder(var6)).append("ImmutableDomainNameMapping(default: ").append(var1).append(", map: {");
            appendMapping(var7, var3, var4);

            for(int var8 = 1; var8 < var2; ++var8) {
               var7.append(", ");
               this.appendMapping(var7, var8);
            }

            return var7.append("})").toString();
         }
      }

      private static int estimateBufferSize(int var0, int var1, int var2) {
         return REPR_CONST_PART_LENGTH + var0 + (int)((double)(var2 * var1) * 1.1D);
      }

      private StringBuilder appendMapping(StringBuilder var1, int var2) {
         return appendMapping(var1, this.domainNamePatterns[var2], this.values[var2].toString());
      }

      private static StringBuilder appendMapping(StringBuilder var0, String var1, String var2) {
         return var0.append(var1).append('=').append(var2);
      }

      // $FF: synthetic method
      ImmutableDomainNameMapping(Object var1, Map var2, Object var3) {
         this(var1, var2);
      }
   }
}
