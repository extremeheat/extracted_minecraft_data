package net.minecraft.server.packs.metadata;

import com.mojang.serialization.Codec;
import java.util.Optional;

public record MetadataSectionType<T>(String name, Codec<T> codec) {
   public MetadataSectionType {
      super();
   }

   public WithValue<T> withValue(final T value) {
      return new WithValue<T>(this, value);
   }

   public static record WithValue<T>(MetadataSectionType<T> type, T value) {
      public WithValue {
         super();
      }

      public <U> Optional<U> unwrapToType(final MetadataSectionType<U> type) {
         return type == this.type ? Optional.of(this.value) : Optional.empty();
      }
   }
}
