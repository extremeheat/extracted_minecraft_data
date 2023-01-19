package net.minecraft.network.syncher;

import java.util.Optional;
import net.minecraft.core.IdMap;
import net.minecraft.network.FriendlyByteBuf;

public interface EntityDataSerializer<T> {
   void write(FriendlyByteBuf var1, T var2);

   T read(FriendlyByteBuf var1);

   default EntityDataAccessor<T> createAccessor(int var1) {
      return new EntityDataAccessor<>(var1, this);
   }

   T copy(T var1);

   static <T> EntityDataSerializer<T> simple(final FriendlyByteBuf.Writer<T> var0, final FriendlyByteBuf.Reader<T> var1) {
      return new EntityDataSerializer.ForValueType<T>() {
         @Override
         public void write(FriendlyByteBuf var1x, T var2) {
            var0.accept(var1x, var2);
         }

         @Override
         public T read(FriendlyByteBuf var1x) {
            return (T)var1.apply((T)var1x);
         }
      };
   }

   static <T> EntityDataSerializer<Optional<T>> optional(FriendlyByteBuf.Writer<T> var0, FriendlyByteBuf.Reader<T> var1) {
      return simple(var0.asOptional(), var1.asOptional());
   }

   static <T extends Enum<T>> EntityDataSerializer<T> simpleEnum(Class<T> var0) {
      return simple(FriendlyByteBuf::writeEnum, var1 -> var1.readEnum(var0));
   }

   static <T> EntityDataSerializer<T> simpleId(IdMap<T> var0) {
      return simple((var1, var2) -> var1.writeId(var0, var2), var1 -> var1.readById(var0));
   }

   public interface ForValueType<T> extends EntityDataSerializer<T> {
      @Override
      default T copy(T var1) {
         return (T)var1;
      }
   }
}
