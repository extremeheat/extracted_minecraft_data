package net.minecraft.network.syncher;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface EntityDataSerializer<T> {
   StreamCodec<? super RegistryFriendlyByteBuf, T> codec();

   default EntityDataAccessor<T> createAccessor(int var1) {
      return new EntityDataAccessor<>(var1, this);
   }

   T copy(T var1);

   static <T> EntityDataSerializer<T> forValueType(StreamCodec<? super RegistryFriendlyByteBuf, T> var0) {
      return () -> var0;
   }

   public interface ForValueType<T> extends EntityDataSerializer<T> {
      @Override
      default T copy(T var1) {
         return (T)var1;
      }
   }
}
