package io.netty.util;

/** @deprecated */
@Deprecated
public final class DomainMappingBuilder<V> {
   private final DomainNameMappingBuilder<V> builder;

   public DomainMappingBuilder(V var1) {
      super();
      this.builder = new DomainNameMappingBuilder(var1);
   }

   public DomainMappingBuilder(int var1, V var2) {
      super();
      this.builder = new DomainNameMappingBuilder(var1, var2);
   }

   public DomainMappingBuilder<V> add(String var1, V var2) {
      this.builder.add(var1, var2);
      return this;
   }

   public DomainNameMapping<V> build() {
      return this.builder.build();
   }
}
