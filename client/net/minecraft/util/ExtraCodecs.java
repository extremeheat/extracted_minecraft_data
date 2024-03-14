package net.minecraft.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.UnsignedBytes;
import com.google.gson.JsonElement;
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
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.MapCodec.MapCodecCodec;
import com.mojang.serialization.codecs.BaseMapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ExtraCodecs {
   public static final Codec<JsonElement> JSON = converter(JsonOps.INSTANCE);
   public static final Codec<Object> JAVA = converter(JavaOps.INSTANCE);
   public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT
      .listOf()
      .comapFlatMap(
         var0 -> Util.fixedSize(var0, 3).map(var0x -> new Vector3f(var0x.get(0), var0x.get(1), var0x.get(2))), var0 -> List.of(var0.x(), var0.y(), var0.z())
      );
   public static final Codec<Quaternionf> QUATERNIONF_COMPONENTS = Codec.FLOAT
      .listOf()
      .comapFlatMap(
         var0 -> Util.fixedSize(var0, 4).map(var0x -> new Quaternionf(var0x.get(0), var0x.get(1), var0x.get(2), var0x.get(3))),
         var0 -> List.of(var0.x, var0.y, var0.z, var0.w)
      );
   public static final Codec<AxisAngle4f> AXISANGLE4F = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.FLOAT.fieldOf("angle").forGetter(var0x -> var0x.angle),
               VECTOR3F.fieldOf("axis").forGetter(var0x -> new Vector3f(var0x.x, var0x.y, var0x.z))
            )
            .apply(var0, AxisAngle4f::new)
   );
   public static final Codec<Quaternionf> QUATERNIONF = withAlternative(QUATERNIONF_COMPONENTS, AXISANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new));
   public static Codec<Matrix4f> MATRIX4F = Codec.FLOAT.listOf().comapFlatMap(var0 -> Util.fixedSize(var0, 16).map(var0x -> {
         Matrix4f var1 = new Matrix4f();

         for(int var2 = 0; var2 < var0x.size(); ++var2) {
            var1.setRowColumn(var2 >> 2, var2 & 3, var0x.get(var2));
         }

         return var1.determineProperties();
      }), var0 -> {
      FloatArrayList var1 = new FloatArrayList(16);

      for(int var2 = 0; var2 < 16; ++var2) {
         var1.add(var0.getRowColumn(var2 >> 2, var2 & 3));
      }

      return var1;
   });
   public static final Codec<Integer> UNSIGNED_BYTE = Codec.BYTE
      .flatComapMap(
         UnsignedBytes::toInt,
         var0 -> var0 > 255 ? DataResult.error(() -> "Unsigned byte was too large: " + var0 + " > 255") : DataResult.success(var0.byteValue())
      );
   public static final Codec<Integer> NON_NEGATIVE_INT = intRangeWithMessage(0, 2147483647, var0 -> "Value must be non-negative: " + var0);
   public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, 2147483647, var0 -> "Value must be positive: " + var0);
   public static final Codec<Float> POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, 3.4028235E38F, var0 -> "Value must be positive: " + var0);
   public static final Codec<Pattern> PATTERN = Codec.STRING.comapFlatMap(var0 -> {
      try {
         return DataResult.success(Pattern.compile(var0));
      } catch (PatternSyntaxException var2) {
         return DataResult.error(() -> "Invalid regex pattern '" + var0 + "': " + var2.getMessage());
      }
   }, Pattern::pattern);
   public static final Codec<Instant> INSTANT_ISO8601 = temporalCodec(DateTimeFormatter.ISO_INSTANT).xmap(Instant::from, Function.identity());
   public static final Codec<byte[]> BASE64_STRING = Codec.STRING.comapFlatMap(var0 -> {
      try {
         return DataResult.success(Base64.getDecoder().decode(var0));
      } catch (IllegalArgumentException var2) {
         return DataResult.error(() -> "Malformed base64 string");
      }
   }, var0 -> Base64.getEncoder().encodeToString(var0));
   public static final Codec<String> ESCAPED_STRING = Codec.STRING
      .comapFlatMap(var0 -> DataResult.success(StringEscapeUtils.unescapeJava(var0)), StringEscapeUtils::escapeJava);
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
               Codec.STRING.fieldOf("name").forGetter(Property::name),
               Codec.STRING.fieldOf("value").forGetter(Property::value),
               Codec.STRING.optionalFieldOf("signature").forGetter(var0x -> Optional.ofNullable(var0x.signature()))
            )
            .apply(var0, (var0x, var1, var2) -> new Property(var0x, var1, (String)var2.orElse(null)))
   );
   public static final Codec<PropertyMap> PROPERTY_MAP = Codec.either(Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()), PROPERTY.listOf())
      .xmap(var0 -> {
         PropertyMap var1 = new PropertyMap();
         var0.ifLeft(var1x -> var1x.forEach((var1xx, var2) -> {
               for(String var4 : var2) {
                  var1.put(var1xx, new Property(var1xx, var4));
               }
            })).ifRight(var1x -> {
            for(Property var3 : var1x) {
               var1.put(var3.name(), var3);
            }
         });
         return var1;
      }, var0 -> Either.right(var0.values().stream().toList()));
   public static final Codec<String> PLAYER_NAME = validate(
      sizeLimitedString(0, 16),
      var0 -> StringUtil.isValidPlayerName(var0)
            ? DataResult.success(var0)
            : DataResult.error(() -> "Player name contained disallowed characters: '" + var0 + "'")
   );
   private static final MapCodec<GameProfile> GAME_PROFILE_WITHOUT_PROPERTIES = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(UUIDUtil.AUTHLIB_CODEC.fieldOf("id").forGetter(GameProfile::getId), PLAYER_NAME.fieldOf("name").forGetter(GameProfile::getName))
            .apply(var0, GameProfile::new)
   );
   public static final Codec<GameProfile> GAME_PROFILE = RecordCodecBuilder.create(
      var0 -> var0.group(
               GAME_PROFILE_WITHOUT_PROPERTIES.forGetter(Function.identity()),
               PROPERTY_MAP.optionalFieldOf("properties", new PropertyMap()).forGetter(GameProfile::getProperties)
            )
            .apply(var0, (var0x, var1) -> {
               var1.forEach((var1x, var2) -> var0x.getProperties().put(var1x, var2));
               return var0x;
            })
   );
   public static final Codec<String> NON_EMPTY_STRING = validate(
      Codec.STRING, var0 -> var0.isEmpty() ? DataResult.error(() -> "Expected non-empty string") : DataResult.success(var0)
   );
   public static final Codec<Integer> CODEPOINT = Codec.STRING.comapFlatMap(var0 -> {
      int[] var1 = var0.codePoints().toArray();
      return var1.length != 1 ? DataResult.error(() -> "Expected one codepoint, got: " + var0) : DataResult.success(var1[0]);
   }, Character::toString);
   public static Codec<String> RESOURCE_PATH_CODEC = validate(
      Codec.STRING,
      var0 -> !ResourceLocation.isValidPath(var0)
            ? DataResult.error(() -> "Invalid string to use as a resource path element: " + var0)
            : DataResult.success(var0)
   );

   public ExtraCodecs() {
      super();
   }

   public static <T> Codec<T> converter(DynamicOps<T> var0) {
      return Codec.PASSTHROUGH.xmap(var1 -> var1.convert(var0).getValue(), var1 -> new Dynamic(var0, var1));
   }

   public static <F, S> Codec<Either<F, S>> xor(Codec<F> var0, Codec<S> var1) {
      return new ExtraCodecs.XorCodec(var0, var1);
   }

   public static <P, I> Codec<I> intervalCodec(
      Codec<P> var0, String var1, String var2, BiFunction<P, P, DataResult<I>> var3, Function<I, P> var4, Function<I, P> var5
   ) {
      Codec var6 = Codec.list(var0).comapFlatMap(var1x -> Util.fixedSize(var1x, 2).flatMap(var1xx -> {
            Object var2xx = var1xx.get(0);
            Object var3xx = var1xx.get(1);
            return (DataResult)var3.apply(var2xx, var3xx);
         }), var2x -> ImmutableList.of(var4.apply(var2x), var5.apply(var2x)));
      Codec var7 = RecordCodecBuilder.create(
            var3x -> var3x.group(var0.fieldOf(var1).forGetter(Pair::getFirst), var0.fieldOf(var2).forGetter(Pair::getSecond)).apply(var3x, Pair::of)
         )
         .comapFlatMap(var1x -> (DataResult)var3.apply(var1x.getFirst(), var1x.getSecond()), var2x -> Pair.of(var4.apply(var2x), var5.apply(var2x)));
      Codec var8 = withAlternative(var6, var7);
      return Codec.either(var0, var8)
         .comapFlatMap(var1x -> (DataResult)var1x.map(var1xx -> (DataResult)var3.apply(var1xx, var1xx), DataResult::success), var2x -> {
            Object var3xx = var4.apply(var2x);
            Object var4xx = var5.apply(var2x);
            return Objects.equals(var3xx, var4xx) ? Either.left(var3xx) : Either.right(var2x);
         });
   }

   public static <A> ResultFunction<A> orElsePartial(final A var0) {
      return new ResultFunction<A>() {
         public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> var1, T var2, DataResult<Pair<A, T>> var3) {
            MutableObject var4 = new MutableObject();
            Optional var5 = var3.resultOrPartial(var4::setValue);
            return var5.isPresent() ? var3 : DataResult.error(() -> "(" + (String)var4.getValue() + " -> using default)", Pair.of(var0, var2));
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
                  .orElseGet(() -> DataResult.error(() -> "Unknown element id: " + var1x)),
            var2x -> {
               int var3 = var0.applyAsInt(var2x);
               return var3 == var2 ? DataResult.error(() -> "Element with unknown id: " + var2x) : DataResult.success(var3);
            }
         );
   }

   public static <E> Codec<E> stringResolverCodec(Function<E, String> var0, Function<String, E> var1) {
      return Codec.STRING
         .flatXmap(
            var1x -> (DataResult)Optional.ofNullable(var1.apply(var1x))
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> "Unknown element name:" + var1x)),
            var1x -> (DataResult)Optional.ofNullable((String)var0.apply(var1x))
                  .map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> "Element with unknown name: " + var1x))
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

   public static <E> MapCodec<E> orCompressed(final MapCodec<E> var0, final MapCodec<E> var1) {
      return new MapCodec<E>() {
         public <T> RecordBuilder<T> encode(E var1x, DynamicOps<T> var2, RecordBuilder<T> var3) {
            return var2.compressMaps() ? var1.encode(var1x, var2, var3) : var0.encode(var1x, var2, var3);
         }

         public <T> DataResult<E> decode(DynamicOps<T> var1x, MapLike<T> var2) {
            return var1x.compressMaps() ? var1.decode(var1x, var2) : var0.decode(var1x, var2);
         }

         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return var1.keys(var1x);
         }

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

   public static <E> Codec<E> overrideLifecycle(Codec<E> var0, Function<E, Lifecycle> var1) {
      return overrideLifecycle(var0, var1, var1);
   }

   public static <F, S> ExtraCodecs.EitherCodec<F, S> either(Codec<F> var0, Codec<S> var1) {
      return new ExtraCodecs.EitherCodec<>(var0, var1);
   }

   public static <K, V> ExtraCodecs.StrictUnboundedMapCodec<K, V> strictUnboundedMap(Codec<K> var0, Codec<V> var1) {
      return new ExtraCodecs.StrictUnboundedMapCodec<>(var0, var1);
   }

   public static <T> Codec<T> validate(Codec<T> var0, Function<T, DataResult<T>> var1) {
      return var0 instanceof MapCodecCodec var2 ? validate(var2.codec(), var1).codec() : var0.flatXmap(var1, var1);
   }

   public static <T> MapCodec<T> validate(MapCodec<T> var0, Function<T, DataResult<T>> var1) {
      return var0.flatXmap(var1, var1);
   }

   private static Codec<Integer> intRangeWithMessage(int var0, int var1, Function<Integer, String> var2) {
      return validate(
         Codec.INT,
         var3 -> var3.compareTo(var0) >= 0 && var3.compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error(() -> (String)var2.apply(var3))
      );
   }

   public static Codec<Integer> intRange(int var0, int var1) {
      return intRangeWithMessage(var0, var1, var2 -> "Value must be within range [" + var0 + ";" + var1 + "]: " + var2);
   }

   private static Codec<Float> floatRangeMinExclusiveWithMessage(float var0, float var1, Function<Float, String> var2) {
      return validate(
         Codec.FLOAT,
         var3 -> var3.compareTo(var0) > 0 && var3.compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error(() -> (String)var2.apply(var3))
      );
   }

   public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> var0) {
      return validate(var0, var0x -> var0x.isEmpty() ? DataResult.error(() -> "List must have contents") : DataResult.success(var0x));
   }

   public static <T> Codec<HolderSet<T>> nonEmptyHolderSet(Codec<HolderSet<T>> var0) {
      return validate(
         var0,
         var0x -> var0x.unwrap().right().filter(List::isEmpty).isPresent() ? DataResult.error(() -> "List must have contents") : DataResult.success(var0x)
      );
   }

   public static <T> Codec<T> recursive(String var0, Function<Codec<T>, Codec<T>> var1) {
      return new ExtraCodecs.RecursiveCodec<>(var0, var1);
   }

   public static <T> MapCodec<T> recursiveMap(String var0, Function<Codec<T>, MapCodec<T>> var1) {
      return new ExtraCodecs.RecursiveMapCodec<>(var0, var1);
   }

   public static <A> Codec<A> lazyInitializedCodec(Supplier<Codec<A>> var0) {
      return new ExtraCodecs.RecursiveCodec<>(var0.toString(), var1 -> (Codec<A>)var0.get());
   }

   public static <A> MapCodec<Optional<A>> strictOptionalField(Codec<A> var0, String var1) {
      return new ExtraCodecs.StrictOptionalFieldCodec<>(var1, var0);
   }

   public static <A> MapCodec<A> strictOptionalField(Codec<A> var0, String var1, A var2) {
      return strictOptionalField(var0, var1).xmap(var1x -> var1x.orElse(var2), var1x -> Objects.equals(var1x, var2) ? Optional.empty() : Optional.of(var1x));
   }

   public static <E> MapCodec<E> retrieveContext(final Function<DynamicOps<?>, DataResult<E>> var0) {
      class 1ContextRetrievalCodec extends MapCodec<E> {
         _ContextRetrievalCodec/* $VF was: 1ContextRetrievalCodec*/() {
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
                  return DataResult.error(() -> "Mixed type list: element " + var4 + " had type " + var5 + ", but list is of type " + var3);
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
               return DataResult.error(() -> "Caught exception decoding " + var2 + ": " + var4.getMessage());
            }
         }
      });
   }

   public static Codec<TemporalAccessor> temporalCodec(DateTimeFormatter var0) {
      return Codec.STRING.comapFlatMap(var1 -> {
         try {
            return DataResult.success(var0.parse(var1));
         } catch (Exception var3) {
            return DataResult.error(var3::getMessage);
         }
      }, var0::format);
   }

   public static MapCodec<OptionalLong> asOptionalLong(MapCodec<Optional<Long>> var0) {
      return var0.xmap(toOptionalLong, fromOptionalLong);
   }

   public static Codec<String> sizeLimitedString(int var0, int var1) {
      return validate(
         Codec.STRING,
         var2 -> {
            int var3 = var2.length();
            if (var3 < var0) {
               return DataResult.error(() -> "String \"" + var2 + "\" is too short: " + var3 + ", expected range [" + var0 + "-" + var1 + "]");
            } else {
               return var3 > var1
                  ? DataResult.error(() -> "String \"" + var2 + "\" is too long: " + var3 + ", expected range [" + var0 + "-" + var1 + "]")
                  : DataResult.success(var2);
            }
         }
      );
   }

   public static <T> Codec<List<T>> sizeLimitedList(Codec<List<T>> var0, int var1) {
      return validate(
         var0,
         var1x -> var1x.size() > var1
               ? DataResult.error(() -> "List is too long: " + var1x.size() + ", expected range [0-" + var1 + "]")
                  .setPartial(() -> List.copyOf(var1x.subList(0, var1)))
               : DataResult.success(var1x)
      );
   }

   public static <K, V> Codec<Map<K, V>> sizeLimitedMap(Codec<Map<K, V>> var0, int var1) {
      return validate(
         var0,
         var1x -> var1x.size() > var1
               ? DataResult.error(() -> "Map is too long: " + var1x.size() + ", expected range [0-" + var1 + "]")
               : DataResult.success(var1x)
      );
   }

   public static <T> Codec<T> withAlternative(Codec<T> var0, Codec<? extends T> var1) {
      return Codec.either(var0, var1).xmap(var0x -> var0x.map(var0xx -> var0xx, var0xx -> var0xx), Either::left);
   }

   public static <T, U> Codec<T> withAlternative(Codec<T> var0, Codec<U> var1, Function<U, T> var2) {
      return Codec.either(var0, var1).xmap(var1x -> var1x.map(var0xx -> var0xx, var2), Either::left);
   }

   public static <T> Codec<Object2BooleanMap<T>> object2BooleanMap(Codec<T> var0) {
      return Codec.unboundedMap(var0, Codec.BOOL).xmap(Object2BooleanOpenHashMap::new, Object2ObjectOpenHashMap::new);
   }

   @Deprecated
   public static <K, V> MapCodec<V> dispatchOptionalValue(
      final String var0,
      final String var1,
      final Codec<K> var2,
      final Function<? super V, ? extends K> var3,
      final Function<? super K, ? extends Codec<? extends V>> var4
   ) {
      return new MapCodec<V>() {
         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return Stream.of((T[])(var1x.createString(var0), var1x.createString(var1)));
         }

         public <T> DataResult<V> decode(DynamicOps<T> var1x, MapLike<T> var2x) {
            Object var3x = var2x.get(var0);
            return var3x == null ? DataResult.error(() -> "Missing \"" + var0 + "\" in: " + var2x) : var2.decode(var1x, var3x).flatMap(var4xx -> {
               Object var5 = Objects.requireNonNullElseGet(var2x.get(var1), var1x::emptyMap);
               return ((Codec)var4.apply(var4xx.getFirst())).decode(var1x, var5).map(Pair::getFirst);
            });
         }

         public <T> RecordBuilder<T> encode(V var1x, DynamicOps<T> var2x, RecordBuilder<T> var3x) {
            Object var4x = var3.apply(var1x);
            var3x.add(var0, var2.encodeStart(var2x, var4x));
            DataResult var5 = this.encode((Codec)var4.apply(var4x), (V)var1x, var2x);
            if (var5.result().isEmpty() || !Objects.equals(var5.result().get(), var2x.emptyMap())) {
               var3x.add(var1, var5);
            }

            return var3x;
         }

         private <T, V2 extends V> DataResult<T> encode(Codec<V2> var1x, V var2x, DynamicOps<T> var3x) {
            return var1x.encodeStart(var3x, var2x);
         }
      };
   }

   public static <K, V> Codec<Map<K, V>> unboundedDispatchMap(final Codec<K> var0, final Function<K, Codec<? extends V>> var1) {
      return new Codec<Map<K, V>>() {
         public <T> DataResult<T> encode(Map<K, V> var1x, DynamicOps<T> var2, T var3) {
            RecordBuilder var4 = var2.mapBuilder();

            for(Entry var6 : var1x.entrySet()) {
               var4.add(var0.encodeStart(var2, var6.getKey()), this.encodeValue((Codec)var1.apply(var6.getKey()), (V)var6.getValue(), var2));
            }

            return var4.build(var3);
         }

         private <T, V2 extends V> DataResult<T> encodeValue(Codec<V2> var1x, V var2, DynamicOps<T> var3) {
            return var1x.encodeStart(var3, var2);
         }

         public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> var1x, T var2) {
            return var1x.getMap(var2)
               .flatMap(
                  var4 -> {
                     Object2ObjectArrayMap var5 = new Object2ObjectArrayMap();
                     Builder var6 = Stream.builder();
                     DataResult var7 = var4.entries()
                        .reduce(
                           DataResult.success(com.mojang.datafixers.util.Unit.INSTANCE, Lifecycle.stable()),
                           (var5x, var6x) -> {
                              DataResult var7xx = var0.parse(var1x, var6x.getFirst());
                              DataResult var8xx = var7xx.map(var1).flatMap(var2xxx -> var2xxx.parse(var1x, var6x.getSecond()).map(Function.identity()));
                              DataResult var9xx = var7xx.apply2stable(Pair::of, var8xx);
                              Optional var10 = var9xx.resultOrPartial(var0xxxx -> {
                              });
                              if (var10.isPresent()) {
                                 Object var11 = var5.putIfAbsent(((Pair)var10.get()).getFirst(), ((Pair)var10.get()).getSecond());
                                 if (var11 != null) {
                                    var6.add(var6x);
                                    return var5x.apply2stable(
                                       (var0xxxx, var1xxxxxxx) -> var0xxxx,
                                       DataResult.error(() -> "Duplicate entry for key: '" + ((Pair)var10.get()).getFirst() + "'")
                                    );
                                 }
                              } else {
                                 var6.add(var6x);
                              }
            
                              return var5x.apply2stable((var0xxxx, var1xxxxxxx) -> var0xxxx, var9xx);
                           },
                           (var0xx, var1xxxxx) -> var0xx.apply2stable((var0xxxx, var1xxxxxxx) -> var0xxxx, var1xxxxx)
                        );
                     Map var8 = Map.copyOf(var5);
                     Object var9 = var1x.createMap(var6.build());
                     return var7.map(var2xx -> Pair.of(var8, var2)).setPartial(Pair.of(var8, var2)).mapError(var1xxxxx -> var1xxxxx + " missed input: " + var9);
                  }
               );
         }
      };
   }

   public static <A> Codec<Optional<A>> optionalEmptyMap(final Codec<A> var0) {
      return new Codec<Optional<A>>() {
         public <T> DataResult<Pair<Optional<A>, T>> decode(DynamicOps<T> var1, T var2) {
            return isEmptyMap(var1, (T)var2)
               ? DataResult.success(Pair.of(Optional.empty(), var2))
               : var0.decode(var1, var2).map(var0xx -> var0xx.mapFirst(Optional::of));
         }

         private static <T> boolean isEmptyMap(DynamicOps<T> var0x, T var1) {
            Optional var2 = var0x.getMap(var1).result();
            return var2.isPresent() && ((MapLike)var2.get()).entries().findAny().isEmpty();
         }

         public <T> DataResult<T> encode(Optional<A> var1, DynamicOps<T> var2, T var3) {
            return var1.isEmpty() ? DataResult.success(var2.emptyMap()) : var0.encode(var1.get(), var2, var3);
         }
      };
   }

   public static final class EitherCodec<F, S> implements Codec<Either<F, S>> {
      private final Codec<F> first;
      private final Codec<S> second;

      public EitherCodec(Codec<F> var1, Codec<S> var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> var1, T var2) {
         DataResult var3 = this.first.decode(var1, var2).map(var0 -> var0.mapFirst(Either::left));
         if (var3.error().isEmpty()) {
            return var3;
         } else {
            DataResult var4 = this.second.decode(var1, var2).map(var0 -> var0.mapFirst(Either::right));
            return var4.error().isEmpty() ? var4 : var3.apply2((var0, var1x) -> var1x, var4);
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

   static class RecursiveCodec<T> implements Codec<T> {
      private final String name;
      private final Supplier<Codec<T>> wrapped;

      RecursiveCodec(String var1, Function<Codec<T>, Codec<T>> var2) {
         super();
         this.name = var1;
         this.wrapped = Suppliers.memoize(() -> (Codec)var2.apply(this));
      }

      public <S> DataResult<Pair<T, S>> decode(DynamicOps<S> var1, S var2) {
         return ((Codec)this.wrapped.get()).decode(var1, var2);
      }

      public <S> DataResult<S> encode(T var1, DynamicOps<S> var2, S var3) {
         return ((Codec)this.wrapped.get()).encode(var1, var2, var3);
      }

      @Override
      public String toString() {
         return "RecursiveCodec[" + this.name + "]";
      }
   }

   static class RecursiveMapCodec<T> extends MapCodec<T> {
      private final String name;
      private final Supplier<MapCodec<T>> wrapped;

      RecursiveMapCodec(String var1, Function<Codec<T>, MapCodec<T>> var2) {
         super();
         this.name = var1;
         this.wrapped = Suppliers.memoize(() -> (MapCodec)var2.apply((T)this.codec()));
      }

      public <S> RecordBuilder<S> encode(T var1, DynamicOps<S> var2, RecordBuilder<S> var3) {
         return ((MapCodec)this.wrapped.get()).encode(var1, var2, var3);
      }

      public <S> DataResult<T> decode(DynamicOps<S> var1, MapLike<S> var2) {
         return ((MapCodec)this.wrapped.get()).decode(var1, var2);
      }

      public <S> Stream<S> keys(DynamicOps<S> var1) {
         return ((MapCodec)this.wrapped.get()).keys(var1);
      }

      public String toString() {
         return "RecursiveMapCodec[" + this.name + "]";
      }
   }

   static final class StrictOptionalFieldCodec<A> extends MapCodec<Optional<A>> {
      private final String name;
      private final Codec<A> elementCodec;

      public StrictOptionalFieldCodec(String var1, Codec<A> var2) {
         super();
         this.name = var1;
         this.elementCodec = var2;
      }

      public <T> DataResult<Optional<A>> decode(DynamicOps<T> var1, MapLike<T> var2) {
         Object var3 = var2.get(this.name);
         return var3 == null ? DataResult.success(Optional.empty()) : this.elementCodec.parse(var1, var3).map(Optional::of);
      }

      public <T> RecordBuilder<T> encode(Optional<A> var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
         return var1.isPresent() ? var3.add(this.name, this.elementCodec.encodeStart(var2, var1.get())) : var3;
      }

      public <T> Stream<T> keys(DynamicOps<T> var1) {
         return Stream.of((T)var1.createString(this.name));
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof ExtraCodecs.StrictOptionalFieldCodec)) {
            return false;
         } else {
            ExtraCodecs.StrictOptionalFieldCodec var2 = (ExtraCodecs.StrictOptionalFieldCodec)var1;
            return Objects.equals(this.name, var2.name) && Objects.equals(this.elementCodec, var2.elementCodec);
         }
      }

      public int hashCode() {
         return Objects.hash(this.name, this.elementCodec);
      }

      public String toString() {
         return "StrictOptionalFieldCodec[" + this.name + ": " + this.elementCodec + "]";
      }
   }

   public static record StrictUnboundedMapCodec<K, V>(Codec<K> a, Codec<V> b) implements Codec<Map<K, V>>, BaseMapCodec<K, V> {
      private final Codec<K> keyCodec;
      private final Codec<V> elementCodec;

      public StrictUnboundedMapCodec(Codec<K> var1, Codec<V> var2) {
         super();
         this.keyCodec = var1;
         this.elementCodec = var2;
      }

      public <T> DataResult<Map<K, V>> decode(DynamicOps<T> var1, MapLike<T> var2) {
         com.google.common.collect.ImmutableMap.Builder var3 = ImmutableMap.builder();

         for(Pair var5 : var2.entries().toList()) {
            DataResult var6 = this.keyCodec().parse(var1, var5.getFirst());
            DataResult var7 = this.elementCodec().parse(var1, var5.getSecond());
            DataResult var8 = var6.apply2stable(Pair::of, var7);
            if (var8.error().isPresent()) {
               return DataResult.error(() -> {
                  PartialResult var2xx = (PartialResult)var8.error().get();
                  String var3xx;
                  if (var6.result().isPresent()) {
                     var3xx = "Map entry '" + var6.result().get() + "' : " + var2xx.message();
                  } else {
                     var3xx = var2xx.message();
                  }

                  return var3xx;
               });
            }

            if (!var8.result().isPresent()) {
               return DataResult.error(() -> "Empty or invalid map contents are not allowed");
            }

            Pair var9 = (Pair)var8.result().get();
            var3.put(var9.getFirst(), var9.getSecond());
         }

         ImmutableMap var10 = var3.build();
         return DataResult.success(var10);
      }

      public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> var1, T var2) {
         return var1.getMap(var2).setLifecycle(Lifecycle.stable()).flatMap(var2x -> this.decode(var1, var2x)).map(var1x -> Pair.of(var1x, var2));
      }

      public <T> DataResult<T> encode(Map<K, V> var1, DynamicOps<T> var2, T var3) {
         return this.encode(var1, var2, var2.mapBuilder()).build(var3);
      }

      public String toString() {
         return "StrictUnboundedMapCodec[" + this.keyCodec + " -> " + this.elementCodec + "]";
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

      public String toString() {
         return this.decoratedId();
      }

      private String decoratedId() {
         return this.tag ? "#" + this.id : this.id.toString();
      }
   }

   static record XorCodec<F, S>(Codec<F> a, Codec<S> b) implements Codec<Either<F, S>> {
      private final Codec<F> first;
      private final Codec<S> second;

      XorCodec(Codec<F> var1, Codec<S> var2) {
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
               () -> "Both alternatives read successfully, can not pick the correct one; first: " + var5.get() + " second: " + var6.get(), (Pair)var5.get()
            );
         } else if (var5.isPresent()) {
            return var3;
         } else {
            return var6.isPresent() ? var4 : var3.apply2((var0, var1x) -> var1x, var4);
         }
      }

      public <T> DataResult<T> encode(Either<F, S> var1, DynamicOps<T> var2, T var3) {
         return (DataResult<T>)var1.map(var3x -> this.first.encode(var3x, var2, var3), var3x -> this.second.encode(var3x, var2, var3));
      }

      public String toString() {
         return "XorCodec[" + this.first + ", " + this.second + "]";
      }
   }
}
