package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface StringRepresentable {
   int PRE_BUILT_MAP_THRESHOLD = 16;

   String getSerializedName();

   static <E extends Enum<E> & StringRepresentable> StringRepresentable.EnumCodec<E> fromEnum(Supplier<E[]> var0) {
      Enum[] var1 = (Enum[])var0.get();
      if (var1.length > 16) {
         Map var2 = Arrays.stream(var1)
            .collect(Collectors.toMap(var0x -> ((StringRepresentable)var0x).getSerializedName(), (Function<? super Enum, ? extends Enum>)(var0x -> var0x)));
         return new StringRepresentable.EnumCodec<>((E[])var1, var1x -> (E)(var1x == null ? null : var2.get(var1x)));
      } else {
         return new StringRepresentable.EnumCodec<>((E[])var1, var1x -> {
            for(Enum var5 : var1) {
               if (((StringRepresentable)var5).getSerializedName().equals(var1x)) {
                  return (E)var5;
               }
            }

            return null;
         });
      }
   }

   static Keyable keys(final StringRepresentable[] var0) {
      return new Keyable() {
         public <T> Stream<T> keys(DynamicOps<T> var1) {
            return Arrays.stream(var0).map(StringRepresentable::getSerializedName).map(var1::createString);
         }
      };
   }

   @Deprecated
   public static class EnumCodec<E extends Enum<E> & StringRepresentable> implements Codec<E> {
      private Codec<E> codec;
      private Function<String, E> resolver;

      public EnumCodec(E[] var1, Function<String, E> var2) {
         super();
         this.codec = ExtraCodecs.orCompressed(
            ExtraCodecs.stringResolverCodec(var0 -> var0.getSerializedName(), var2),
            ExtraCodecs.idResolverCodec(var0 -> var0.ordinal(), var1x -> (E)(var1x >= 0 && var1x < var1.length ? var1[var1x] : null), -1)
         );
         this.resolver = var2;
      }

      public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> var1, T var2) {
         return this.codec.decode(var1, var2);
      }

      public <T> DataResult<T> encode(E var1, DynamicOps<T> var2, T var3) {
         return this.codec.encode(var1, var2, var3);
      }

      @Nullable
      public E byName(@Nullable String var1) {
         return this.resolver.apply(var1);
      }
   }
}
