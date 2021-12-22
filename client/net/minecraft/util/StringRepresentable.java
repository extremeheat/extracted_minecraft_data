package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface StringRepresentable {
   String getSerializedName();

   static <E extends Enum<E> & StringRepresentable> Codec<E> fromEnum(Supplier<E[]> var0, Function<String, E> var1) {
      Enum[] var2 = (Enum[])var0.get();
      return ExtraCodecs.orCompressed(ExtraCodecs.stringResolverCodec((var0x) -> {
         return ((StringRepresentable)var0x).getSerializedName();
      }, var1), ExtraCodecs.idResolverCodec((var0x) -> {
         return ((Enum)var0x).ordinal();
      }, (var1x) -> {
         return var1x >= 0 && var1x < var2.length ? var2[var1x] : null;
      }, -1));
   }

   static Keyable keys(final StringRepresentable[] var0) {
      return new Keyable() {
         public <T> Stream<T> keys(DynamicOps<T> var1) {
            Stream var10000 = Arrays.stream(var0).map(StringRepresentable::getSerializedName);
            Objects.requireNonNull(var1);
            return var10000.map(var1::createString);
         }
      };
   }
}
