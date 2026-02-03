package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TypedDataComponent<T>(DataComponentType<T> type, T value) {
   public static final StreamCodec<RegistryFriendlyByteBuf, TypedDataComponent<?>> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, TypedDataComponent<?>>() {
      public TypedDataComponent<?> decode(final RegistryFriendlyByteBuf input) {
         DataComponentType<?> type = (DataComponentType)DataComponentType.STREAM_CODEC.decode(input);
         return decodeTyped(input, type);
      }

      private static <T> TypedDataComponent<T> decodeTyped(final RegistryFriendlyByteBuf input, final DataComponentType<T> type) {
         return new TypedDataComponent<T>(type, type.streamCodec().decode(input));
      }

      public void encode(final RegistryFriendlyByteBuf output, final TypedDataComponent<?> value) {
         encodeCap(output, value);
      }

      private static <T> void encodeCap(final RegistryFriendlyByteBuf output, final TypedDataComponent<T> component) {
         DataComponentType.STREAM_CODEC.encode(output, component.type());
         component.type().streamCodec().encode(output, component.value());
      }
   };

   public TypedDataComponent {
      super();
   }

   static TypedDataComponent<?> fromEntryUnchecked(final Map.Entry<DataComponentType<?>, Object> entry) {
      return createUnchecked((DataComponentType)entry.getKey(), entry.getValue());
   }

   public static <T> TypedDataComponent<T> createUnchecked(final DataComponentType<T> type, final Object value) {
      return new TypedDataComponent<T>(type, value);
   }

   public void applyTo(final PatchedDataComponentMap components) {
      components.set(this.type, this.value);
   }

   public <D> DataResult<D> encodeValue(final DynamicOps<D> ops) {
      Codec<T> codec = this.type.codec();
      return codec == null ? DataResult.error(() -> "Component of type " + String.valueOf(this.type) + " is not encodable") : codec.encodeStart(ops, this.value);
   }

   public String toString() {
      String var10000 = String.valueOf(this.type);
      return var10000 + "=>" + String.valueOf(this.value);
   }
}
