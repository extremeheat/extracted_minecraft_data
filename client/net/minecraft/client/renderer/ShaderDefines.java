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
   public static final Codec<ShaderDefines> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("values", Map.of()).forGetter(ShaderDefines::values), Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("flags", Set.of()).forGetter(ShaderDefines::flags)).apply(i, ShaderDefines::new));

   public ShaderDefines {
      super();
   }

   public static Builder builder() {
      return new Builder();
   }

   public ShaderDefines withOverrides(final ShaderDefines defines) {
      if (this.isEmpty()) {
         return defines;
      } else if (defines.isEmpty()) {
         return this;
      } else {
         ImmutableMap.Builder<String, String> newValues = ImmutableMap.builderWithExpectedSize(this.values.size() + defines.values.size());
         newValues.putAll(this.values);
         newValues.putAll(defines.values);
         ImmutableSet.Builder<String> newFlags = ImmutableSet.builderWithExpectedSize(this.flags.size() + defines.flags.size());
         newFlags.addAll(this.flags);
         newFlags.addAll(defines.flags);
         return new ShaderDefines(newValues.buildKeepingLast(), newFlags.build());
      }
   }

   public String asSourceDirectives() {
      StringBuilder directives = new StringBuilder();

      for(Map.Entry<String, String> entry : this.values.entrySet()) {
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();
         directives.append("#define ").append(key).append(" ").append(value).append('\n');
      }

      for(String flag : this.flags) {
         directives.append("#define ").append(flag).append('\n');
      }

      return directives.toString();
   }

   public boolean isEmpty() {
      return this.values.isEmpty() && this.flags.isEmpty();
   }

   public static class Builder {
      private final ImmutableMap.Builder<String, String> values = ImmutableMap.builder();
      private final ImmutableSet.Builder<String> flags = ImmutableSet.builder();

      private Builder() {
         super();
      }

      public Builder define(final String key, final String value) {
         if (value.isBlank()) {
            throw new IllegalArgumentException("Cannot define empty string");
         } else {
            this.values.put(key, escapeNewLines(value));
            return this;
         }
      }

      private static String escapeNewLines(final String value) {
         return value.replaceAll("\n", "\\\\\n");
      }

      public Builder define(final String key, final float value) {
         this.values.put(key, String.valueOf(value));
         return this;
      }

      public Builder define(final String key, final int value) {
         this.values.put(key, String.valueOf(value));
         return this;
      }

      public Builder define(final String key) {
         this.flags.add(key);
         return this;
      }

      public ShaderDefines build() {
         return new ShaderDefines(this.values.build(), this.flags.build());
      }
   }
}
