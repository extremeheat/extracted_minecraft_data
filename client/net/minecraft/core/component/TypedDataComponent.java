package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map.Entry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TypedDataComponent<T>(DataComponentType<T> b, T c) {
   private final DataComponentType<T> type;
   private final T value;
   public static final StreamCodec<RegistryFriendlyByteBuf, TypedDataComponent<?>> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, TypedDataComponent<?>>(
      
   ) {
      public TypedDataComponent<?> decode(RegistryFriendlyByteBuf var1) {
         DataComponentType var2 = DataComponentType.STREAM_CODEC.decode(var1);
         return decodeTyped(var1, var2);
      }

      private static <T> TypedDataComponent<T> decodeTyped(RegistryFriendlyByteBuf var0, DataComponentType<T> var1) {
         return new TypedDataComponent<>(var1, (T)var1.streamCodec().decode(var0));
      }

      public void encode(RegistryFriendlyByteBuf var1, TypedDataComponent<?> var2) {
         encodeCap(var1, var2);
      }

      private static <T> void encodeCap(RegistryFriendlyByteBuf var0, TypedDataComponent<T> var1) {
         DataComponentType.STREAM_CODEC.encode(var0, var1.type());
         var1.type().streamCodec().encode(var0, var1.value());
      }
   };

   public TypedDataComponent(DataComponentType<T> var1, T var2) {
      super();
      this.type = var1;
      this.value = (T)var2;
   }

   static TypedDataComponent<?> fromEntryUnchecked(Entry<DataComponentType<?>, Object> var0) {
      return createUnchecked((DataComponentType<T>)var0.getKey(), var0.getValue());
   }

   static <T> TypedDataComponent<T> createUnchecked(DataComponentType<T> var0, Object var1) {
      return new TypedDataComponent<>(var0, (T)var1);
   }

   public void applyTo(PatchedDataComponentMap var1) {
      var1.set(this.type, this.value);
   }

   public <D> DataResult<D> encodeValue(DynamicOps<D> var1) {
      Codec var2 = this.type.codec();
      return var2 == null ? DataResult.error(() -> "Component of type " + this.type + " is not encodable") : var2.encodeStart(var1, this.value);
   }

   public String toString() {
      return this.type + "=>" + this.value;
   }
}
