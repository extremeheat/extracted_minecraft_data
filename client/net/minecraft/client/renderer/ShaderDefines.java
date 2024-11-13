package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record ShaderDefines(Map<String, String> values, Set<String> flags) {
   public static final ShaderDefines EMPTY = new ShaderDefines(Map.of(), Set.of());
   public static final Codec<ShaderDefines> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("values", Map.of()).forGetter(ShaderDefines::values), Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("flags", Set.of()).forGetter(ShaderDefines::flags)).apply(var0, ShaderDefines::new));

   public ShaderDefines(Map<String, String> var1, Set<String> var2) {
      super();
      this.values = var1;
      this.flags = var2;
   }

   public static Builder builder() {
      return new Builder();
   }

   public ShaderDefines withOverrides(ShaderDefines var1) {
      if (this.isEmpty()) {
         return var1;
      } else if (var1.isEmpty()) {
         return this;
      } else {
         ImmutableMap.Builder var2 = ImmutableMap.builderWithExpectedSize(this.values.size() + var1.values.size());
         var2.putAll(this.values);
         var2.putAll(var1.values);
         ImmutableSet.Builder var3 = ImmutableSet.builderWithExpectedSize(this.flags.size() + var1.flags.size());
         var3.addAll(this.flags);
         var3.addAll(var1.flags);
         return new ShaderDefines(var2.buildKeepingLast(), var3.build());
      }
   }

   public String asSourceDirectives() {
      StringBuilder var1 = new StringBuilder();

      for(Map.Entry var3 : this.values.entrySet()) {
         String var4 = (String)var3.getKey();
         String var5 = (String)var3.getValue();
         var1.append("#define ").append(var4).append(" ").append(var5).append('\n');
      }

      for(String var7 : this.flags) {
         var1.append("#define ").append(var7).append('\n');
      }

      return var1.toString();
   }

   public boolean isEmpty() {
      return this.values.isEmpty() && this.flags.isEmpty();
   }

   public static class Builder {
      private final ImmutableMap.Builder<String, String> values = ImmutableMap.builder();
      private final ImmutableSet.Builder<String> flags = ImmutableSet.builder();

      Builder() {
         super();
      }

      public Builder define(String var1, String var2) {
         if (var2.isBlank()) {
            throw new IllegalArgumentException("Cannot define empty string");
         } else {
            this.values.put(var1, escapeNewLines(var2));
            return this;
         }
      }

      private static String escapeNewLines(String var0) {
         return var0.replaceAll("\n", "\\\\\n");
      }

      public Builder define(String var1, float var2) {
         this.values.put(var1, String.valueOf(var2));
         return this;
      }

      public Builder define(String var1) {
         this.flags.add(var1);
         return this;
      }

      public ShaderDefines build() {
         return new ShaderDefines(this.values.build(), this.flags.build());
      }
   }
}
