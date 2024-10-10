package net.minecraft.util;

import com.google.common.collect.ImmutableList;
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
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.Codec.ResultFunction;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
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
import org.joml.Vector4f;

public class ExtraCodecs {
   public static final Codec<JsonElement> JSON = converter(JsonOps.INSTANCE);
   public static final Codec<Object> JAVA = converter(JavaOps.INSTANCE);
   public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT
      .listOf()
      .comapFlatMap(
         var0 -> Util.fixedSize(var0, 3).map(var0x -> new Vector3f((Float)var0x.get(0), (Float)var0x.get(1), (Float)var0x.get(2))),
         var0 -> List.of(var0.x(), var0.y(), var0.z())
      );
   public static final Codec<Vector4f> VECTOR4F = Codec.FLOAT
      .listOf()
      .comapFlatMap(
         var0 -> Util.fixedSize(var0, 4).map(var0x -> new Vector4f((Float)var0x.get(0), (Float)var0x.get(1), (Float)var0x.get(2), (Float)var0x.get(3))),
         var0 -> List.of(var0.x(), var0.y(), var0.z(), var0.w())
      );
   public static final Codec<Quaternionf> QUATERNIONF_COMPONENTS = Codec.FLOAT
      .listOf()
      .comapFlatMap(
         var0 -> Util.fixedSize(var0, 4)
               .map(var0x -> new Quaternionf((Float)var0x.get(0), (Float)var0x.get(1), (Float)var0x.get(2), (Float)var0x.get(3)).normalize()),
         var0 -> List.of(var0.x, var0.y, var0.z, var0.w)
      );
   public static final Codec<AxisAngle4f> AXISANGLE4F = RecordCodecBuilder.create(
      var0 -> var0.group(
               Codec.FLOAT.fieldOf("angle").forGetter(var0x -> var0x.angle),
               VECTOR3F.fieldOf("axis").forGetter(var0x -> new Vector3f(var0x.x, var0x.y, var0x.z))
            )
            .apply(var0, AxisAngle4f::new)
   );
   public static final Codec<Quaternionf> QUATERNIONF = Codec.withAlternative(QUATERNIONF_COMPONENTS, AXISANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new));
   public static final Codec<Matrix4f> MATRIX4F = Codec.FLOAT.listOf().comapFlatMap(var0 -> Util.fixedSize(var0, 16).map(var0x -> {
         Matrix4f var1 = new Matrix4f();

         for (int var2 = 0; var2 < var0x.size(); var2++) {
            var1.setRowColumn(var2 >> 2, var2 & 3, (Float)var0x.get(var2));
         }

         return var1.determineProperties();
      }), var0 -> {
      FloatArrayList var1 = new FloatArrayList(16);

      for (int var2 = 0; var2 < 16; var2++) {
         var1.add(var0.getRowColumn(var2 >> 2, var2 & 3));
      }

      return var1;
   });
   public static final Codec<Integer> RGB_COLOR_CODEC = Codec.withAlternative(
      Codec.INT, VECTOR3F, var0 -> ARGB.colorFromFloat(1.0F, var0.x(), var0.y(), var0.z())
   );
   public static final Codec<Integer> ARGB_COLOR_CODEC = Codec.withAlternative(
      Codec.INT, VECTOR4F, var0 -> ARGB.colorFromFloat(var0.w(), var0.x(), var0.y(), var0.z())
   );
   public static final Codec<Integer> UNSIGNED_BYTE = Codec.BYTE
      .flatComapMap(
         UnsignedBytes::toInt,
         var0 -> var0 > 255 ? DataResult.error(() -> "Unsigned byte was too large: " + var0 + " > 255") : DataResult.success(var0.byteValue())
      );
   public static final Codec<Integer> NON_NEGATIVE_INT = intRangeWithMessage(0, 2147483647, var0 -> "Value must be non-negative: " + var0);
   public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, 2147483647, var0 -> "Value must be positive: " + var0);
   public static final Codec<Float> NON_NEGATIVE_FLOAT = floatRangeMinInclusiveWithMessage(0.0F, 3.4028235E38F, var0 -> "Value must be non-negative: " + var0);
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
               Codec.STRING.lenientOptionalFieldOf("signature").forGetter(var0x -> Optional.ofNullable(var0x.signature()))
            )
            .apply(var0, (var0x, var1, var2) -> new Property(var0x, var1, (String)var2.orElse(null)))
   );
   public static final Codec<PropertyMap> PROPERTY_MAP = Codec.either(Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()), PROPERTY.listOf())
      .xmap(var0 -> {
         PropertyMap var1 = new PropertyMap();
         var0.ifLeft(var1x -> var1x.forEach((var1xx, var2) -> {
               for (String var4 : var2) {
                  var1.put(var1xx, new Property(var1xx, var4));
               }
            })).ifRight(var1x -> {
            for (Property var3 : var1x) {
               var1.put(var3.name(), var3);
            }
         });
         return var1;
      }, var0 -> Either.right(var0.values().stream().toList()));
   public static final Codec<String> PLAYER_NAME = Codec.string(0, 16)
      .validate(
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
               PROPERTY_MAP.lenientOptionalFieldOf("properties", new PropertyMap()).forGetter(GameProfile::getProperties)
            )
            .apply(var0, (var0x, var1) -> {
               var1.forEach((var1x, var2) -> var0x.getProperties().put(var1x, var2));
               return var0x;
            })
   );
   public static final Codec<String> NON_EMPTY_STRING = Codec.STRING
      .validate(var0 -> var0.isEmpty() ? DataResult.error(() -> "Expected non-empty string") : DataResult.success(var0));
   public static final Codec<Integer> CODEPOINT = Codec.STRING.comapFlatMap(var0 -> {
      int[] var1 = var0.codePoints().toArray();
      return var1.length != 1 ? DataResult.error(() -> "Expected one codepoint, got: " + var0) : DataResult.success(var1[0]);
   }, Character::toString);
   public static final Codec<String> RESOURCE_PATH_CODEC = Codec.STRING
      .validate(
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
      Codec var8 = Codec.withAlternative(var6, var7);
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
            var1x -> Optional.ofNullable(var1.apply(var1x))
                  .<DataResult>map(DataResult::success)
                  .orElseGet(() -> DataResult.error(() -> "Unknown element id: " + var1x)),
            var2x -> {
               int var3 = var0.applyAsInt(var2x);
               return var3 == var2 ? DataResult.error(() -> "Element with unknown id: " + var2x) : DataResult.success(var3);
            }
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
            return var3.result().map(var2xxx -> var3.setLifecycle((Lifecycle)var1.apply(var2xxx.getFirst()))).orElse(var3);
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

   public static <K, V> ExtraCodecs.StrictUnboundedMapCodec<K, V> strictUnboundedMap(Codec<K> var0, Codec<V> var1) {
      return new ExtraCodecs.StrictUnboundedMapCodec<>(var0, var1);
   }

   private static Codec<Integer> intRangeWithMessage(int var0, int var1, Function<Integer, String> var2) {
      return Codec.INT
         .validate(var3 -> var3.compareTo(var0) >= 0 && var3.compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error(() -> (String)var2.apply(var3)));
   }

   public static Codec<Integer> intRange(int var0, int var1) {
      return intRangeWithMessage(var0, var1, var2 -> "Value must be within range [" + var0 + ";" + var1 + "]: " + var2);
   }

   private static Codec<Float> floatRangeMinInclusiveWithMessage(float var0, float var1, Function<Float, String> var2) {
      return Codec.FLOAT
         .validate(var3 -> var3.compareTo(var0) >= 0 && var3.compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error(() -> (String)var2.apply(var3)));
   }

   private static Codec<Float> floatRangeMinExclusiveWithMessage(float var0, float var1, Function<Float, String> var2) {
      return Codec.FLOAT
         .validate(var3 -> var3.compareTo(var0) > 0 && var3.compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error(() -> (String)var2.apply(var3)));
   }

   public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> var0) {
      return var0.validate(var0x -> var0x.isEmpty() ? DataResult.error(() -> "List must have contents") : DataResult.success(var0x));
   }

   public static <T> Codec<HolderSet<T>> nonEmptyHolderSet(Codec<HolderSet<T>> var0) {
      return var0.validate(
         var0x -> var0x.unwrap().right().filter(List::isEmpty).isPresent() ? DataResult.error(() -> "List must have contents") : DataResult.success(var0x)
      );
   }

   public static <M extends Map<?, ?>> Codec<M> nonEmptyMap(Codec<M> var0) {
      return var0.validate(var0x -> var0x.isEmpty() ? DataResult.error(() -> "Map must have contents") : DataResult.success(var0x));
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
            return (DataResult<E>)var0.apply(var1);
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

            while (var2.hasNext()) {
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

   public static <K, V> Codec<Map<K, V>> sizeLimitedMap(Codec<Map<K, V>> var0, int var1) {
      return var0.validate(
         var1x -> var1x.size() > var1
               ? DataResult.error(() -> "Map is too long: " + var1x.size() + ", expected range [0-" + var1 + "]")
               : DataResult.success(var1x)
      );
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
            return Stream.of((T[])(new Object[]{var1x.createString(var0), var1x.createString(var1)}));
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
