package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;

public interface StringRepresentable {
   int PRE_BUILT_MAP_THRESHOLD = 16;

   String getSerializedName();

   static <E extends Enum<E> & StringRepresentable> StringRepresentable.EnumCodec<E> fromEnum(Supplier<E[]> var0) {
      return fromEnumWithMapping(var0, var0x -> var0x);
   }

   static <E extends Enum<E> & StringRepresentable> StringRepresentable.EnumCodec<E> fromEnumWithMapping(Supplier<E[]> var0, Function<String, String> var1) {
      Enum[] var2 = (Enum[])var0.get();
      Function var3 = createNameLookup(var2, var1);
      return new StringRepresentable.EnumCodec<>((E[])var2, var3);
   }

   static <T extends StringRepresentable> Codec<T> fromValues(Supplier<T[]> var0) {
      StringRepresentable[] var1 = (StringRepresentable[])var0.get();
      Function var2 = createNameLookup(var1, var0x -> var0x);
      ToIntFunction var3 = Util.createIndexLookup(Arrays.asList(var1));
      return new StringRepresentable.StringRepresentableCodec<>((T[])var1, var2, var3);
   }

   static <T extends StringRepresentable> Function<String, T> createNameLookup(T[] var0, Function<String, String> var1) {
      if (var0.length > 16) {
         Map var2 = Arrays.stream(var0).collect(Collectors.toMap(var1x -> (String)var1.apply(var1x.getSerializedName()), var0x -> (StringRepresentable)var0x));
         return var1x -> (T)(var1x == null ? null : var2.get(var1x));
      } else {
         return var2x -> {
            for (StringRepresentable var6 : var0) {
               if (((String)var1.apply(var6.getSerializedName())).equals(var2x)) {
                  return (T)var6;
               }
            }

            return null;
         };
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
   public static class EnumCodec<E extends Enum<E> & StringRepresentable> extends StringRepresentable.StringRepresentableCodec<E> {
      private final Function<String, E> resolver;

      public EnumCodec(E[] var1, Function<String, E> var2) {
         super((E[])var1, var2, var0 -> var0.ordinal());
         this.resolver = var2;
      }

      @Nullable
      public E byName(@Nullable String var1) {
         return this.resolver.apply(var1);
      }

      public E byName(@Nullable String var1, E var2) {
         return Objects.requireNonNullElse(this.byName(var1), (E)var2);
      }

      public E byName(@Nullable String var1, Supplier<? extends E> var2) {
         return Objects.requireNonNullElseGet(this.byName(var1), var2);
      }
   }

   public static class StringRepresentableCodec<S extends StringRepresentable> implements Codec<S> {
      private final Codec<S> codec;

      public StringRepresentableCodec(S[] var1, Function<String, S> var2, ToIntFunction<S> var3) {
         super();
         this.codec = ExtraCodecs.orCompressed(
            Codec.stringResolver(StringRepresentable::getSerializedName, var2),
            ExtraCodecs.idResolverCodec(var3, var1x -> (S)(var1x >= 0 && var1x < var1.length ? var1[var1x] : null), -1)
         );
      }

      public <T> DataResult<Pair<S, T>> decode(DynamicOps<T> var1, T var2) {
         return this.codec.decode(var1, var2);
      }

      public <T> DataResult<T> encode(S var1, DynamicOps<T> var2, T var3) {
         return this.codec.encode(var1, var2, var3);
      }
   }
}
