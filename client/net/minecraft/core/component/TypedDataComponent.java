package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record TypedDataComponent<T>(DataComponentType<T> type, T value) {
   public static final StreamCodec<RegistryFriendlyByteBuf, TypedDataComponent<?>> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, TypedDataComponent<?>>() {
      public TypedDataComponent<?> decode(RegistryFriendlyByteBuf var1) {
         DataComponentType var2 = (DataComponentType)DataComponentType.STREAM_CODEC.decode(var1);
         return decodeTyped(var1, var2);
      }

      private static <T> TypedDataComponent<T> decodeTyped(RegistryFriendlyByteBuf var0, DataComponentType<T> var1) {
         return new TypedDataComponent(var1, var1.streamCodec().decode(var0));
      }

      public void encode(RegistryFriendlyByteBuf var1, TypedDataComponent<?> var2) {
         encodeCap(var1, var2);
      }

      private static <T> void encodeCap(RegistryFriendlyByteBuf var0, TypedDataComponent<T> var1) {
         DataComponentType.STREAM_CODEC.encode(var0, var1.type());
         var1.type().streamCodec().encode(var0, var1.value());
      }

      // $FF: synthetic method
      public void encode(final Object var1, final Object var2) {
         this.encode((RegistryFriendlyByteBuf)var1, (TypedDataComponent)var2);
      }

      // $FF: synthetic method
      public Object decode(final Object var1) {
         return this.decode((RegistryFriendlyByteBuf)var1);
      }
   };

   public TypedDataComponent(DataComponentType<T> var1, T var2) {
      super();
      this.type = var1;
      this.value = var2;
   }

   static TypedDataComponent<?> fromEntryUnchecked(Map.Entry<DataComponentType<?>, Object> var0) {
      return createUnchecked((DataComponentType)var0.getKey(), var0.getValue());
   }

   public static <T> TypedDataComponent<T> createUnchecked(DataComponentType<T> var0, Object var1) {
      return new TypedDataComponent(var0, var1);
   }

   public void applyTo(PatchedDataComponentMap var1) {
      var1.set(this.type, this.value);
   }

   public <D> DataResult<D> encodeValue(DynamicOps<D> var1) {
      Codec var2 = this.type.codec();
      return var2 == null ? DataResult.error(() -> {
         return "Component of type " + String.valueOf(this.type) + " is not encodable";
      }) : var2.encodeStart(var1, this.value);
   }

   public String toString() {
      String var10000 = String.valueOf(this.type);
      return var10000 + "=>" + String.valueOf(this.value);
   }

   public DataComponentType<T> type() {
      return this.type;
   }

   public T value() {
      return this.value;
   }
}
