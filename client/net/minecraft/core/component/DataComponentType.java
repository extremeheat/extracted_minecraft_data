package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface DataComponentType<T> {
   Codec<DataComponentType<?>> CODEC = Codec.lazyInitialized(() -> BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec());
   StreamCodec<RegistryFriendlyByteBuf, DataComponentType<?>> STREAM_CODEC = StreamCodec.recursive(
      var0 -> ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE)
   );
   Codec<DataComponentType<?>> PERSISTENT_CODEC = CODEC.validate(
      var0 -> var0.isTransient()
            ? DataResult.error(() -> "Encountered transient component " + BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(var0))
            : DataResult.success(var0)
   );
   Codec<Map<DataComponentType<?>, Object>> VALUE_MAP_CODEC = Codec.dispatchedMap(PERSISTENT_CODEC, DataComponentType::codecOrThrow);

   static <T> DataComponentType.Builder<T> builder() {
      return new DataComponentType.Builder<>();
   }

   @Nullable
   Codec<T> codec();

   default Codec<T> codecOrThrow() {
      Codec var1 = this.codec();
      if (var1 == null) {
         throw new IllegalStateException(this + " is not a persistent component");
      } else {
         return var1;
      }
   }

   default boolean isTransient() {
      return this.codec() == null;
   }

   StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec();

   public static class Builder<T> {
      @Nullable
      private Codec<T> codec;
      @Nullable
      private StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

      public Builder() {
         super();
      }

      public DataComponentType.Builder<T> persistent(Codec<T> var1) {
         this.codec = var1;
         return this;
      }

      public DataComponentType.Builder<T> networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf, T> var1) {
         this.streamCodec = var1;
         return this;
      }

      public DataComponentType<T> build() {
         StreamCodec var1 = Objects.requireNonNullElseGet(
            this.streamCodec, () -> ByteBufCodecs.fromCodecWithRegistries(Objects.requireNonNull(this.codec, "Missing Codec for component"))
         );
         return new DataComponentType.Builder.SimpleType<>(this.codec, var1);
      }

      static class SimpleType<T> implements DataComponentType<T> {
         @Nullable
         private final Codec<T> codec;
         private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

         SimpleType(@Nullable Codec<T> var1, StreamCodec<? super RegistryFriendlyByteBuf, T> var2) {
            super();
            this.codec = var1;
            this.streamCodec = var2;
         }

         @Nullable
         @Override
         public Codec<T> codec() {
            return this.codec;
         }

         @Override
         public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
            return this.streamCodec;
         }

         @Override
         public String toString() {
            return Util.getRegisteredName(BuiltInRegistries.DATA_COMPONENT_TYPE, this);
         }
      }
   }
}
