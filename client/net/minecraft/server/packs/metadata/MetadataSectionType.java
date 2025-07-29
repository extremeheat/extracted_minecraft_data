package net.minecraft.server.packs.metadata;

import com.mojang.serialization.Codec;
import java.util.Optional;

public record MetadataSectionType<T>(String name, Codec<T> codec) {
   public MetadataSectionType(String var1, Codec<T> var2) {
      super();
      this.name = var1;
      this.codec = var2;
   }

   public WithValue<T> withValue(T var1) {
      return new WithValue<T>(this, var1);
   }

   public static record WithValue<T>(MetadataSectionType<T> type, T value) {
      public WithValue(MetadataSectionType<T> var1, T var2) {
         super();
         this.type = var1;
         this.value = var2;
      }

      public <U> Optional<U> unwrapToType(MetadataSectionType<U> var1) {
         return var1 == this.type ? Optional.of(this.value) : Optional.empty();
      }
   }
}
