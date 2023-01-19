package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.Codec.ResultFunction;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.Vector3f;

public class ExtraCodecs {
   public static final Codec<JsonElement> JSON = Codec.PASSTHROUGH
      .xmap(var0 -> (JsonElement)var0.convert(JsonOps.INSTANCE).getValue(), var0 -> new Dynamic(JsonOps.INSTANCE, var0));
   public static final Codec<Component> COMPONENT = JSON.flatXmap(var0 -> {
      try {
         return DataResult.success(Component.Serializer.fromJson(var0));
      } catch (JsonParseException var2) {
         return DataResult.error(var2.getMessage());
      }
   }, var0 -> {
      try {
         return DataResult.success(Component.Serializer.toJsonTree(var0));
      } catch (IllegalArgumentException var2) {
         return DataResult.error(var2.getMessage());
      }
   });
   public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT
      .listOf()
      .comapFlatMap(
         var0 -> Util.fixedSize(var0, 3).map(var0x -> new Vector3f(var0x.get(0), var0x.get(1), var0x.get(2))),
         var0 -> ImmutableList.of(var0.x(), var0.y(), var0.z())
      );
   public static final Codec<Integer> NON_NEGATIVE_INT = intRangeWithMessage(0, 2147483647, var0 -> "Value must be non-negative: " + var0);
   public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, 2147483647, var0 -> "Value must be positive: " + var0);
   public static final Codec<Float> POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, 3.4028235E38F, var0 -> "Value must be positive: " + var0);
   public static final Codec<Pattern> PATTERN = Codec.STRING.comapFlatMap(var0 -> {
      try {
         return DataResult.success(Pattern.compile(var0));
      } catch (PatternSyntaxException var2) {
         return DataResult.error("Invalid regex pattern '" + var0 + "': " + var2.getMessage());
      }
   }, Pattern::pattern);
   public static final Codec<Instant> INSTANT_ISO8601 = instantCodec(DateTimeFormatter.ISO_INSTANT);
   public static final Codec<byte[]> BASE64_STRING = Codec.STRING.comapFlatMap(var0 -> {
      try {
         return DataResult.success(Base64.getDecoder().decode(var0));
      } catch (IllegalArgumentException var2) {
         return DataResult.error("Malformed base64 string");
      }
   }, var0 -> Base64.getEncoder().encodeToString(var0));
   public static final Codec<ExtraCodecs.TagOrElementLocation> TAG_OR_ELEMENT_ID = Codec.STRING
      .comapFlatMap(
         var0 -> var0.startsWith("#")
               ? ResourceLocation.read(var0.substring(1)).map(var0x -> new ExtraCodecs.TagOrElementLocation(var0x, true))
               : ResourceLocation.read(var0).map(var0x -> new ExtraCodecs.TagOrElementLocation(var0x, false)),
         ExtraCodecs.TagOrElementLocation::decoratedId
      );
   public static final Function<Optional<Long>, OptionalLong> toOptionalLong = var0 -> var0.map(OptionalLong::of).orElseGet(OptionalLong::empty);
   public static final Function<OptionalLong, Optional<Long>> fromOptionalLong = var0 -> var0.isPresent() ? Optional.of(var0.getAsLong()) : Optional.empty();
   public static final Codec<BitSet> BIT_SET = Codec.LONG_STREAM.xmap(var0 -> BitSet.valueOf(var0.toArray()), var0 -> Arrays.stream(var0.toLongArray()));
   private static final Codec<Property> PROPERTY = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.STRING.fieldOf("name").forGetter(Property::getName),
               Codec.STRING.fieldOf("value").forGetter(Property::getValue),
               Codec.STRING.optionalFieldOf("signature").forGetter(var0x -> Optional.ofNullable(var0x.getSignature()))
            )
            .apply(var0, (var0x, var1, var2) -> new Property(var0x, var1, (String)var2.orElse(null)))
   );
   @VisibleForTesting
   public static final Codec<PropertyMap> PROPERTY_MAP = Codec.either(Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()), PROPERTY.listOf())
      .xmap(var0 -> {
         PropertyMap var1 = new PropertyMap();
         var0.ifLeft(var1x -> var1x.forEach((var1xx, var2) -> {
               for(String var4 : var2) {
                  var1.put(var1xx, new Property(var1xx, var4));
               }
            })).ifRight(var1x -> {
            for(Property var3 : var1x) {
               var1.put(var3.getName(), var3);
            }
         });
         return var1;
      }, var0 -> Either.right(var0.values().stream().toList()));
   public static final Codec<GameProfile> GAME_PROFILE = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.mapPair(
                     UUIDUtil.AUTHLIB_CODEC.xmap(Optional::of, var0x -> (UUID)var0x.orElse(null)).optionalFieldOf("id", Optional.empty()),
                     Codec.STRING.xmap(Optional::of, var0x -> (String)var0x.orElse(null)).optionalFieldOf("name", Optional.empty())
                  )
                  .flatXmap(ExtraCodecs::mapIdNameToGameProfile, ExtraCodecs::mapGameProfileToIdName)
                  .forGetter(Function.identity()),
               PROPERTY_MAP.optionalFieldOf("properties", new PropertyMap()).forGetter(GameProfile::getProperties)
            )
            .apply(var0, (var0x, var1) -> {
               var1.forEach((var1x, var2) -> var0x.getProperties().put(var1x, var2));
               return var0x;
            })
   );
   public static final Codec<String> NON_EMPTY_STRING = validate(
      Codec.STRING, var0 -> var0.isEmpty() ? DataResult.error("Expected non-empty string") : DataResult.success(var0)
   );

   public ExtraCodecs() {
      super();
   }

   public static <F, S> Codec<Either<F, S>> xor(Codec<F> var0, Codec<S> var1) {
      return new ExtraCodecs.XorCodec(var0, var1);
   }

   public static <P, I> Codec<I> intervalCodec(
      Codec<P> var0, String var1, String var2, BiFunction<P, P, DataResult<I>> var3, Function<I, P> var4, Function<I, P> var5
   ) {
      Codec var6 = Codec.list(var0).comapFlatMap(var1x -> Util.fixedSize(var1x, 2).flatMap(var1xx -> {
            Object var2x = var1xx.get(0);
            Object var3x = var1xx.get(1);
            return (DataResult)var3.apply(var2x, var3x);
         }), var2x -> ImmutableList.of(var4.apply(var2x), var5.apply(var2x)));
      Codec var7 = RecordCodecBuilder.create(
            var3x -> var3x.group(var0.fieldOf(var1).forGetter(Pair::getFirst), var0.fieldOf(var2).forGetter(Pair::getSecond)).apply(var3x, Pair::of)
         )
         .comapFlatMap(var1x -> (DataResult)var3.apply(var1x.getFirst(), var1x.getSecond()), var2x -> Pair.of(var4.apply(var2x), var5.apply(var2x)));
      Codec var8 = new ExtraCodecs.EitherCodec(var6, var7).xmap(var0x -> var0x.map(var0xx -> var0xx, var0xx -> var0xx), Either::left);
      return Codec.either(var0, var8)
         .comapFlatMap(var1x -> (DataResult)var1x.map(var1xx -> (DataResult)var3.apply(var1xx, var1xx), DataResult::success), var2x -> {
            Object var3x = var4.apply(var2x);
            Object var4x = var5.apply(var2x);
            return Objects.equals(var3x, var4x) ? Either.left(var3x) : Either.right(var2x);
         });
   }

   public static <A> ResultFunction<A> orElsePartial(final A var0) {
      return new ResultFunction<A>() {
         public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> var1, T var2, DataResult<Pair<A, T>> var3) {
            MutableObject var4 = new MutableObject();
            Optional var5 = var3.resultOrPartial(var4::setValue);
            return var5.isPresent() ? var3 : DataResult.error("(" + (String)var4.getValue() + " -> using default)", Pair.of(var0, var2));
         }

         public <T> DataResult<T> coApply(DynamicOps<T> var1, A var2, DataResult<T> var3) {
            return var3;
         }

         @Override
         public String toString() {
            return "OrElsePartial[" + var0 + "]";
         }
      };
   }

   public static <E> Codec<E> idResolverCodec(ToIntFunction<E> var0, IntFunction<E> var1, int var2) {
      return Codec.INT
         .flatXmap(
            var1x -> (DataResult)Optional.ofNullable(var1.apply(var1x))
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error("Unknown element id: " + var1x)),
            var2x -> {
               int var3 = var0.applyAsInt(var2x);
               return var3 == var2 ? DataResult.error("Element with unknown id: " + var2x) : DataResult.success(var3);
            }
         );
   }

   public static <E> Codec<E> stringResolverCodec(Function<E, String> var0, Function<String, E> var1) {
      return Codec.STRING
         .flatXmap(
            var1x -> (DataResult)Optional.ofNullable(var1.apply(var1x))
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error("Unknown element name:" + var1x)),
            var1x -> (DataResult)Optional.ofNullable((String)var0.apply(var1x))
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error("Element with unknown name: " + var1x))
         );
   }

   public static <E> Codec<E> orCompressed(final Codec<E> var0, final Codec<E> var1) {
      return new Codec<E>() {
         public <T> DataResult<T> encode(E var1x, DynamicOps<T> var2, T var3) {
            return var2.compressMaps() ? var1.encode(var1x, var2, var3) : var0.encode(var1x, var2, var3);
         }

         public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> var1x, T var2) {
            return var1x.compressMaps() ? var1.decode(var1x, var2) : var0.decode(var1x, var2);
         }

         @Override
         public String toString() {
            return var0 + " orCompressed " + var1;
         }
      };
   }

   public static <E> Codec<E> overrideLifecycle(Codec<E> var0, final Function<E, Lifecycle> var1, final Function<E, Lifecycle> var2) {
      return var0.mapResult(new ResultFunction<E>() {
         public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> var1x, T var2x, DataResult<Pair<E, T>> var3) {
            return (DataResult<Pair<E, T>>)var3.result().map(var2xxx -> var3.setLifecycle((Lifecycle)var1.apply(var2xxx.getFirst()))).orElse((T)var3);
         }

         public <T> DataResult<T> coApply(DynamicOps<T> var1x, E var2x, DataResult<T> var3) {
            return var3.setLifecycle((Lifecycle)var2.apply(var2x));
         }

         @Override
         public String toString() {
            return "WithLifecycle[" + var1 + " " + var2 + "]";
         }
      });
   }

   public static <T> Codec<T> validate(Codec<T> var0, Function<T, DataResult<T>> var1) {
      return var0.flatXmap(var1, var1);
   }

   private static Codec<Integer> intRangeWithMessage(int var0, int var1, Function<Integer, String> var2) {
      return validate(
         Codec.INT, var3 -> var3.compareTo(var0) >= 0 && var3.compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error((String)var2.apply(var3))
      );
   }

   private static Codec<Float> floatRangeMinExclusiveWithMessage(float var0, float var1, Function<Float, String> var2) {
      return validate(
         Codec.FLOAT, var3 -> var3.compareTo(var0) > 0 && var3.compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error((String)var2.apply(var3))
      );
   }

   public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> var0) {
      return validate(var0, var0x -> var0x.isEmpty() ? DataResult.error("List must have contents") : DataResult.success(var0x));
   }

   public static <T> Codec<HolderSet<T>> nonEmptyHolderSet(Codec<HolderSet<T>> var0) {
      return validate(
         var0, var0x -> var0x.unwrap().right().filter(List::isEmpty).isPresent() ? DataResult.error("List must have contents") : DataResult.success(var0x)
      );
   }

   public static <A> Codec<A> lazyInitializedCodec(Supplier<Codec<A>> var0) {
      return new ExtraCodecs.LazyInitializedCodec<>(var0);
   }

   public static <E> MapCodec<E> retrieveContext(final Function<DynamicOps<?>, DataResult<E>> var0) {
      class 1ContextRetrievalCodec extends MapCodec<E> {
         _ContextRetrievalCodec/* $QF was: 1ContextRetrievalCodec*/() {
            super();
         }

         public <T> RecordBuilder<T> encode(E var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
            return var3;
         }

         public <T> DataResult<E> decode(DynamicOps<T> var1, MapLike<T> var2) {
            return (DataResult<E>)var0.apply((T)var1);
         }

         public String toString() {
            return "ContextRetrievalCodec[" + var0 + "]";
         }

         public <T> Stream<T> keys(DynamicOps<T> var1) {
            return Stream.empty();
         }
      }

      return new 1ContextRetrievalCodec();
   }

   public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> ensureHomogenous(Function<E, T> var0) {
      return var1 -> {
         Iterator var2 = var1.iterator();
         if (var2.hasNext()) {
            Object var3 = var0.apply(var2.next());

            while(var2.hasNext()) {
               Object var4 = var2.next();
               Object var5 = var0.apply(var4);
               if (var5 != var3) {
                  return DataResult.error("Mixed type list: element " + var4 + " had type " + var5 + ", but list is of type " + var3);
               }
            }
         }

         return DataResult.success(var1, Lifecycle.stable());
      };
   }

   public static <A> Codec<A> catchDecoderException(final Codec<A> var0) {
      return Codec.of(var0, new Decoder<A>() {
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
            try {
               return var0.decode(var1, var2);
            } catch (Exception var4) {
               return DataResult.error("Cauch exception decoding " + var2 + ": " + var4.getMessage());
            }
         }
      });
   }

   public static Codec<Instant> instantCodec(DateTimeFormatter var0) {
      return Codec.STRING.comapFlatMap(var1 -> {
         try {
            return DataResult.success(Instant.from(var0.parse(var1)));
         } catch (Exception var3) {
            return DataResult.error(var3.getMessage());
         }
      }, var0::format);
   }

   public static MapCodec<OptionalLong> asOptionalLong(MapCodec<Optional<Long>> var0) {
      return var0.xmap(toOptionalLong, fromOptionalLong);
   }

   private static DataResult<GameProfile> mapIdNameToGameProfile(Pair<Optional<UUID>, Optional<String>> var0) {
      try {
         return DataResult.success(new GameProfile((UUID)((Optional)var0.getFirst()).orElse(null), (String)((Optional)var0.getSecond()).orElse(null)));
      } catch (Throwable var2) {
         return DataResult.error(var2.getMessage());
      }
   }

   private static DataResult<Pair<Optional<UUID>, Optional<String>>> mapGameProfileToIdName(GameProfile var0) {
      return DataResult.success(Pair.of(Optional.ofNullable(var0.getId()), Optional.ofNullable(var0.getName())));
   }

   public static Codec<String> sizeLimitedString(int var0, int var1) {
      return validate(
         Codec.STRING,
         var2 -> {
            int var3 = var2.length();
            if (var3 < var0) {
               return DataResult.error("String \"" + var2 + "\" is too short: " + var3 + ", expected range [" + var0 + "-" + var1 + "]");
            } else {
               return var3 > var1
                  ? DataResult.error("String \"" + var2 + "\" is too long: " + var3 + ", expected range [" + var0 + "-" + var1 + "]")
                  : DataResult.success(var2);
            }
         }
      );
   }

   static final class EitherCodec<F, S> implements Codec<Either<F, S>> {
      private final Codec<F> first;
      private final Codec<S> second;

      public EitherCodec(Codec<F> var1, Codec<S> var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> var1, T var2) {
         DataResult var3 = this.first.decode(var1, var2).map(var0 -> var0.mapFirst(Either::left));
         if (!var3.error().isPresent()) {
            return var3;
         } else {
            DataResult var4 = this.second.decode(var1, var2).map(var0 -> var0.mapFirst(Either::right));
            return !var4.error().isPresent() ? var4 : var3.apply2((var0, var1x) -> var1x, var4);
         }
      }

      public <T> DataResult<T> encode(Either<F, S> var1, DynamicOps<T> var2, T var3) {
         return (DataResult<T>)var1.map(var3x -> this.first.encode(var3x, var2, var3), var3x -> this.second.encode(var3x, var2, var3));
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            ExtraCodecs.EitherCodec var2 = (ExtraCodecs.EitherCodec)var1;
            return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.first, this.second);
      }

      @Override
      public String toString() {
         return "EitherCodec[" + this.first + ", " + this.second + "]";
      }
   }

   static record LazyInitializedCodec<A>(Supplier<Codec<A>> a) implements Codec<A> {
      private final Supplier<Codec<A>> delegate;

      LazyInitializedCodec(Supplier<Codec<A>> var1) {
         super();
         com.google.common.base.Supplier var2 = Suppliers.memoize(var1::get);
         this.delegate = var2;
      }

      public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
         return ((Codec)this.delegate.get()).decode(var1, var2);
      }

      public <T> DataResult<T> encode(A var1, DynamicOps<T> var2, T var3) {
         return ((Codec)this.delegate.get()).encode(var1, var2, var3);
      }
   }

   public static record TagOrElementLocation(ResourceLocation a, boolean b) {
      private final ResourceLocation id;
      private final boolean tag;

      public TagOrElementLocation(ResourceLocation var1, boolean var2) {
         super();
         this.id = var1;
         this.tag = var2;
      }

      @Override
      public String toString() {
         return this.decoratedId();
      }

      private String decoratedId() {
         return this.tag ? "#" + this.id : this.id.toString();
      }
   }

   static final class XorCodec<F, S> implements Codec<Either<F, S>> {
      private final Codec<F> first;
      private final Codec<S> second;

      public XorCodec(Codec<F> var1, Codec<S> var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> var1, T var2) {
         DataResult var3 = this.first.decode(var1, var2).map(var0 -> var0.mapFirst(Either::left));
         DataResult var4 = this.second.decode(var1, var2).map(var0 -> var0.mapFirst(Either::right));
         Optional var5 = var3.result();
         Optional var6 = var4.result();
         if (var5.isPresent() && var6.isPresent()) {
            return DataResult.error(
               "Both alternatives read successfully, can not pick the correct one; first: " + var5.get() + " second: " + var6.get(), (Pair)var5.get()
            );
         } else {
            return var5.isPresent() ? var3 : var4;
         }
      }

      public <T> DataResult<T> encode(Either<F, S> var1, DynamicOps<T> var2, T var3) {
         return (DataResult<T>)var1.map(var3x -> this.first.encode(var3x, var2, var3), var3x -> this.second.encode(var3x, var2, var3));
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            ExtraCodecs.XorCodec var2 = (ExtraCodecs.XorCodec)var1;
            return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.first, this.second);
      }

      @Override
      public String toString() {
         return "XorCodec[" + this.first + ", " + this.second + "]";
      }
   }
}
