package net.minecraft.util;

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
import com.mojang.serialization.JavaOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.BaseMapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
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
   public static final Codec<JsonElement> JSON;
   public static final Codec<Object> JAVA;
   public static final Codec<Vector3f> VECTOR3F;
   public static final Codec<Vector4f> VECTOR4F;
   public static final Codec<Quaternionf> QUATERNIONF_COMPONENTS;
   public static final Codec<AxisAngle4f> AXISANGLE4F;
   public static final Codec<Quaternionf> QUATERNIONF;
   public static Codec<Matrix4f> MATRIX4F;
   public static final Codec<Integer> ARGB_COLOR_CODEC;
   public static final Codec<Integer> UNSIGNED_BYTE;
   public static final Codec<Integer> NON_NEGATIVE_INT;
   public static final Codec<Integer> POSITIVE_INT;
   public static final Codec<Float> POSITIVE_FLOAT;
   public static final Codec<Pattern> PATTERN;
   public static final Codec<Instant> INSTANT_ISO8601;
   public static final Codec<byte[]> BASE64_STRING;
   public static final Codec<String> ESCAPED_STRING;
   public static final Codec<TagOrElementLocation> TAG_OR_ELEMENT_ID;
   public static final Function<Optional<Long>, OptionalLong> toOptionalLong;
   public static final Function<OptionalLong, Optional<Long>> fromOptionalLong;
   public static final Codec<BitSet> BIT_SET;
   private static final Codec<Property> PROPERTY;
   public static final Codec<PropertyMap> PROPERTY_MAP;
   public static final Codec<String> PLAYER_NAME;
   private static final MapCodec<GameProfile> GAME_PROFILE_WITHOUT_PROPERTIES;
   public static final Codec<GameProfile> GAME_PROFILE;
   public static final Codec<String> NON_EMPTY_STRING;
   public static final Codec<Integer> CODEPOINT;
   public static Codec<String> RESOURCE_PATH_CODEC;

   public ExtraCodecs() {
      super();
   }

   public static <T> Codec<T> converter(DynamicOps<T> var0) {
      return Codec.PASSTHROUGH.xmap((var1) -> {
         return var1.convert(var0).getValue();
      }, (var1) -> {
         return new Dynamic(var0, var1);
      });
   }

   public static <P, I> Codec<I> intervalCodec(Codec<P> var0, String var1, String var2, BiFunction<P, P, DataResult<I>> var3, Function<I, P> var4, Function<I, P> var5) {
      Codec var6 = Codec.list(var0).comapFlatMap((var1x) -> {
         return Util.fixedSize((List)var1x, 2).flatMap((var1) -> {
            Object var2 = var1.get(0);
            Object var3x = var1.get(1);
            return (DataResult)var3.apply(var2, var3x);
         });
      }, (var2x) -> {
         return ImmutableList.of(var4.apply(var2x), var5.apply(var2x));
      });
      Codec var7 = RecordCodecBuilder.create((var3x) -> {
         return var3x.group(var0.fieldOf(var1).forGetter(Pair::getFirst), var0.fieldOf(var2).forGetter(Pair::getSecond)).apply(var3x, Pair::of);
      }).comapFlatMap((var1x) -> {
         return (DataResult)var3.apply(var1x.getFirst(), var1x.getSecond());
      }, (var2x) -> {
         return Pair.of(var4.apply(var2x), var5.apply(var2x));
      });
      Codec var8 = Codec.withAlternative(var6, var7);
      return Codec.either(var0, var8).comapFlatMap((var1x) -> {
         return (DataResult)var1x.map((var1) -> {
            return (DataResult)var3.apply(var1, var1);
         }, DataResult::success);
      }, (var2x) -> {
         Object var3 = var4.apply(var2x);
         Object var4x = var5.apply(var2x);
         return Objects.equals(var3, var4x) ? Either.left(var3) : Either.right(var2x);
      });
   }

   public static <A> Codec.ResultFunction<A> orElsePartial(final A var0) {
      return new Codec.ResultFunction<A>() {
         public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> var1, T var2, DataResult<Pair<A, T>> var3) {
            MutableObject var4 = new MutableObject();
            Objects.requireNonNull(var4);
            Optional var5 = var3.resultOrPartial(var4::setValue);
            return var5.isPresent() ? var3 : DataResult.error(() -> {
               return "(" + (String)var4.getValue() + " -> using default)";
            }, Pair.of(var0, var2));
         }

         public <T> DataResult<T> coApply(DynamicOps<T> var1, A var2, DataResult<T> var3) {
            return var3;
         }

         public String toString() {
            return "OrElsePartial[" + String.valueOf(var0) + "]";
         }
      };
   }

   public static <E> Codec<E> idResolverCodec(ToIntFunction<E> var0, IntFunction<E> var1, int var2) {
      return Codec.INT.flatXmap((var1x) -> {
         return (DataResult)Optional.ofNullable(var1.apply(var1x)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               return "Unknown element id: " + var1x;
            });
         });
      }, (var2x) -> {
         int var3 = var0.applyAsInt(var2x);
         return var3 == var2 ? DataResult.error(() -> {
            return "Element with unknown id: " + String.valueOf(var2x);
         }) : DataResult.success(var3);
      });
   }

   public static <E> Codec<E> orCompressed(final Codec<E> var0, final Codec<E> var1) {
      return new Codec<E>() {
         public <T> DataResult<T> encode(E var1x, DynamicOps<T> var2, T var3) {
            return var2.compressMaps() ? var1.encode(var1x, var2, var3) : var0.encode(var1x, var2, var3);
         }

         public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> var1x, T var2) {
            return var1x.compressMaps() ? var1.decode(var1x, var2) : var0.decode(var1x, var2);
         }

         public String toString() {
            String var10000 = String.valueOf(var0);
            return var10000 + " orCompressed " + String.valueOf(var1);
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
            String var10000 = String.valueOf(var0);
            return var10000 + " orCompressed " + String.valueOf(var1);
         }
      };
   }

   public static <E> Codec<E> overrideLifecycle(Codec<E> var0, final Function<E, Lifecycle> var1, final Function<E, Lifecycle> var2) {
      return var0.mapResult(new Codec.ResultFunction<E>() {
         public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> var1x, T var2x, DataResult<Pair<E, T>> var3) {
            return (DataResult)var3.result().map((var2xx) -> {
               return var3.setLifecycle((Lifecycle)var1.apply(var2xx.getFirst()));
            }).orElse(var3);
         }

         public <T> DataResult<T> coApply(DynamicOps<T> var1x, E var2x, DataResult<T> var3) {
            return var3.setLifecycle((Lifecycle)var2.apply(var2x));
         }

         public String toString() {
            String var10000 = String.valueOf(var1);
            return "WithLifecycle[" + var10000 + " " + String.valueOf(var2) + "]";
         }
      });
   }

   public static <E> Codec<E> overrideLifecycle(Codec<E> var0, Function<E, Lifecycle> var1) {
      return overrideLifecycle(var0, var1, var1);
   }

   public static <K, V> StrictUnboundedMapCodec<K, V> strictUnboundedMap(Codec<K> var0, Codec<V> var1) {
      return new StrictUnboundedMapCodec(var0, var1);
   }

   private static Codec<Integer> intRangeWithMessage(int var0, int var1, Function<Integer, String> var2) {
      return Codec.INT.validate((var3) -> {
         return var3.compareTo(var0) >= 0 && var3.compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error(() -> {
            return (String)var2.apply(var3);
         });
      });
   }

   public static Codec<Integer> intRange(int var0, int var1) {
      return intRangeWithMessage(var0, var1, (var2) -> {
         return "Value must be within range [" + var0 + ";" + var1 + "]: " + var2;
      });
   }

   private static Codec<Float> floatRangeMinExclusiveWithMessage(float var0, float var1, Function<Float, String> var2) {
      return Codec.FLOAT.validate((var3) -> {
         return var3.compareTo(var0) > 0 && var3.compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error(() -> {
            return (String)var2.apply(var3);
         });
      });
   }

   public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> var0) {
      return var0.validate((var0x) -> {
         return var0x.isEmpty() ? DataResult.error(() -> {
            return "List must have contents";
         }) : DataResult.success(var0x);
      });
   }

   public static <T> Codec<HolderSet<T>> nonEmptyHolderSet(Codec<HolderSet<T>> var0) {
      return var0.validate((var0x) -> {
         return var0x.unwrap().right().filter(List::isEmpty).isPresent() ? DataResult.error(() -> {
            return "List must have contents";
         }) : DataResult.success(var0x);
      });
   }

   public static <E> MapCodec<E> retrieveContext(final Function<DynamicOps<?>, DataResult<E>> var0) {
      class 1ContextRetrievalCodec extends MapCodec<E> {
         _ContextRetrievalCodec/* $FF was: 1ContextRetrievalCodec*/() {
            super();
         }

         public <T> RecordBuilder<T> encode(E var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
            return var3;
         }

         public <T> DataResult<E> decode(DynamicOps<T> var1, MapLike<T> var2) {
            return (DataResult)var0.apply(var1);
         }

         public String toString() {
            return "ContextRetrievalCodec[" + String.valueOf(var0) + "]";
         }

         public <T> Stream<T> keys(DynamicOps<T> var1) {
            return Stream.empty();
         }
      }

      return new 1ContextRetrievalCodec();
   }

   public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> ensureHomogenous(Function<E, T> var0) {
      return (var1) -> {
         Iterator var2 = var1.iterator();
         if (var2.hasNext()) {
            Object var3 = var0.apply(var2.next());

            while(var2.hasNext()) {
               Object var4 = var2.next();
               Object var5 = var0.apply(var4);
               if (var5 != var3) {
                  return DataResult.error(() -> {
                     String var10000 = String.valueOf(var4);
                     return "Mixed type list: element " + var10000 + " had type " + String.valueOf(var5) + ", but list is of type " + String.valueOf(var3);
                  });
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
               return DataResult.error(() -> {
                  String var10000 = String.valueOf(var2);
                  return "Caught exception decoding " + var10000 + ": " + var4.getMessage();
               });
            }
         }
      });
   }

   public static Codec<TemporalAccessor> temporalCodec(DateTimeFormatter var0) {
      PrimitiveCodec var10000 = Codec.STRING;
      Function var10001 = (var1) -> {
         try {
            return DataResult.success(var0.parse(var1));
         } catch (Exception var3) {
            Objects.requireNonNull(var3);
            return DataResult.error(var3::getMessage);
         }
      };
      Objects.requireNonNull(var0);
      return var10000.comapFlatMap(var10001, var0::format);
   }

   public static MapCodec<OptionalLong> asOptionalLong(MapCodec<Optional<Long>> var0) {
      return var0.xmap(toOptionalLong, fromOptionalLong);
   }

   public static <K, V> Codec<Map<K, V>> sizeLimitedMap(Codec<Map<K, V>> var0, int var1) {
      return var0.validate((var1x) -> {
         return var1x.size() > var1 ? DataResult.error(() -> {
            int var10000 = var1x.size();
            return "Map is too long: " + var10000 + ", expected range [0-" + var1 + "]";
         }) : DataResult.success(var1x);
      });
   }

   public static <T> Codec<Object2BooleanMap<T>> object2BooleanMap(Codec<T> var0) {
      return Codec.unboundedMap(var0, Codec.BOOL).xmap(Object2BooleanOpenHashMap::new, Object2ObjectOpenHashMap::new);
   }

   /** @deprecated */
   @Deprecated
   public static <K, V> MapCodec<V> dispatchOptionalValue(final String var0, final String var1, final Codec<K> var2, final Function<? super V, ? extends K> var3, final Function<? super K, ? extends Codec<? extends V>> var4) {
      return new MapCodec<V>() {
         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return Stream.of(var1x.createString(var0), var1x.createString(var1));
         }

         public <T> DataResult<V> decode(DynamicOps<T> var1x, MapLike<T> var2x) {
            Object var3x = var2x.get(var0);
            return var3x == null ? DataResult.error(() -> {
               return "Missing \"" + var0 + "\" in: " + String.valueOf(var2x);
            }) : var2.decode(var1x, var3x).flatMap((var4x) -> {
               Object var10000 = var2x.get(var1);
               Objects.requireNonNull(var1x);
               Object var5 = Objects.requireNonNullElseGet(var10000, var1x::emptyMap);
               return ((Codec)var4.apply(var4x.getFirst())).decode(var1x, var5).map(Pair::getFirst);
            });
         }

         public <T> RecordBuilder<T> encode(V var1x, DynamicOps<T> var2x, RecordBuilder<T> var3x) {
            Object var4x = var3.apply(var1x);
            var3x.add(var0, var2.encodeStart(var2x, var4x));
            DataResult var5 = this.encode((Codec)var4.apply(var4x), var1x, var2x);
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
            return isEmptyMap(var1, var2) ? DataResult.success(Pair.of(Optional.empty(), var2)) : var0.decode(var1, var2).map((var0x) -> {
               return var0x.mapFirst(Optional::of);
            });
         }

         private static <T> boolean isEmptyMap(DynamicOps<T> var0x, T var1) {
            Optional var2 = var0x.getMap(var1).result();
            return var2.isPresent() && ((MapLike)var2.get()).entries().findAny().isEmpty();
         }

         public <T> DataResult<T> encode(Optional<A> var1, DynamicOps<T> var2, T var3) {
            return var1.isEmpty() ? DataResult.success(var2.emptyMap()) : var0.encode(var1.get(), var2, var3);
         }

         // $FF: synthetic method
         public DataResult encode(final Object var1, final DynamicOps var2, final Object var3) {
            return this.encode((Optional)var1, var2, var3);
         }
      };
   }

   static {
      JSON = converter(JsonOps.INSTANCE);
      JAVA = converter(JavaOps.INSTANCE);
      VECTOR3F = Codec.FLOAT.listOf().comapFlatMap((var0) -> {
         return Util.fixedSize((List)var0, 3).map((var0x) -> {
            return new Vector3f((Float)var0x.get(0), (Float)var0x.get(1), (Float)var0x.get(2));
         });
      }, (var0) -> {
         return List.of(var0.x(), var0.y(), var0.z());
      });
      VECTOR4F = Codec.FLOAT.listOf().comapFlatMap((var0) -> {
         return Util.fixedSize((List)var0, 4).map((var0x) -> {
            return new Vector4f((Float)var0x.get(0), (Float)var0x.get(1), (Float)var0x.get(2), (Float)var0x.get(3));
         });
      }, (var0) -> {
         return List.of(var0.x(), var0.y(), var0.z(), var0.w());
      });
      QUATERNIONF_COMPONENTS = Codec.FLOAT.listOf().comapFlatMap((var0) -> {
         return Util.fixedSize((List)var0, 4).map((var0x) -> {
            return (new Quaternionf((Float)var0x.get(0), (Float)var0x.get(1), (Float)var0x.get(2), (Float)var0x.get(3))).normalize();
         });
      }, (var0) -> {
         return List.of(var0.x, var0.y, var0.z, var0.w);
      });
      AXISANGLE4F = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.FLOAT.fieldOf("angle").forGetter((var0x) -> {
            return var0x.angle;
         }), VECTOR3F.fieldOf("axis").forGetter((var0x) -> {
            return new Vector3f(var0x.x, var0x.y, var0x.z);
         })).apply(var0, AxisAngle4f::new);
      });
      QUATERNIONF = Codec.withAlternative(QUATERNIONF_COMPONENTS, AXISANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new));
      MATRIX4F = Codec.FLOAT.listOf().comapFlatMap((var0) -> {
         return Util.fixedSize((List)var0, 16).map((var0x) -> {
            Matrix4f var1 = new Matrix4f();

            for(int var2 = 0; var2 < var0x.size(); ++var2) {
               var1.setRowColumn(var2 >> 2, var2 & 3, (Float)var0x.get(var2));
            }

            return var1.determineProperties();
         });
      }, (var0) -> {
         FloatArrayList var1 = new FloatArrayList(16);

         for(int var2 = 0; var2 < 16; ++var2) {
            var1.add(var0.getRowColumn(var2 >> 2, var2 & 3));
         }

         return var1;
      });
      ARGB_COLOR_CODEC = Codec.withAlternative(Codec.INT, VECTOR4F, (var0) -> {
         return FastColor.ARGB32.colorFromFloat(var0.w(), var0.x(), var0.y(), var0.z());
      });
      UNSIGNED_BYTE = Codec.BYTE.flatComapMap(UnsignedBytes::toInt, (var0) -> {
         return var0 > 255 ? DataResult.error(() -> {
            return "Unsigned byte was too large: " + var0 + " > 255";
         }) : DataResult.success(var0.byteValue());
      });
      NON_NEGATIVE_INT = intRangeWithMessage(0, 2147483647, (var0) -> {
         return "Value must be non-negative: " + var0;
      });
      POSITIVE_INT = intRangeWithMessage(1, 2147483647, (var0) -> {
         return "Value must be positive: " + var0;
      });
      POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, 3.4028235E38F, (var0) -> {
         return "Value must be positive: " + var0;
      });
      PATTERN = Codec.STRING.comapFlatMap((var0) -> {
         try {
            return DataResult.success(Pattern.compile(var0));
         } catch (PatternSyntaxException var2) {
            return DataResult.error(() -> {
               return "Invalid regex pattern '" + var0 + "': " + var2.getMessage();
            });
         }
      }, Pattern::pattern);
      INSTANT_ISO8601 = temporalCodec(DateTimeFormatter.ISO_INSTANT).xmap(Instant::from, Function.identity());
      BASE64_STRING = Codec.STRING.comapFlatMap((var0) -> {
         try {
            return DataResult.success(Base64.getDecoder().decode(var0));
         } catch (IllegalArgumentException var2) {
            return DataResult.error(() -> {
               return "Malformed base64 string";
            });
         }
      }, (var0) -> {
         return Base64.getEncoder().encodeToString(var0);
      });
      ESCAPED_STRING = Codec.STRING.comapFlatMap((var0) -> {
         return DataResult.success(StringEscapeUtils.unescapeJava(var0));
      }, StringEscapeUtils::escapeJava);
      TAG_OR_ELEMENT_ID = Codec.STRING.comapFlatMap((var0) -> {
         return var0.startsWith("#") ? ResourceLocation.read(var0.substring(1)).map((var0x) -> {
            return new TagOrElementLocation(var0x, true);
         }) : ResourceLocation.read(var0).map((var0x) -> {
            return new TagOrElementLocation(var0x, false);
         });
      }, TagOrElementLocation::decoratedId);
      toOptionalLong = (var0) -> {
         return (OptionalLong)var0.map(OptionalLong::of).orElseGet(OptionalLong::empty);
      };
      fromOptionalLong = (var0) -> {
         return var0.isPresent() ? Optional.of(var0.getAsLong()) : Optional.empty();
      };
      BIT_SET = Codec.LONG_STREAM.xmap((var0) -> {
         return BitSet.valueOf(var0.toArray());
      }, (var0) -> {
         return Arrays.stream(var0.toLongArray());
      });
      PROPERTY = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.STRING.fieldOf("name").forGetter(Property::name), Codec.STRING.fieldOf("value").forGetter(Property::value), Codec.STRING.lenientOptionalFieldOf("signature").forGetter((var0x) -> {
            return Optional.ofNullable(var0x.signature());
         })).apply(var0, (var0x, var1, var2) -> {
            return new Property(var0x, var1, (String)var2.orElse((Object)null));
         });
      });
      PROPERTY_MAP = Codec.either(Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()), PROPERTY.listOf()).xmap((var0) -> {
         PropertyMap var1 = new PropertyMap();
         var0.ifLeft((var1x) -> {
            var1x.forEach((var1xx, var2) -> {
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  String var4 = (String)var3.next();
                  var1.put(var1xx, new Property(var1xx, var4));
               }

            });
         }).ifRight((var1x) -> {
            Iterator var2 = var1x.iterator();

            while(var2.hasNext()) {
               Property var3 = (Property)var2.next();
               var1.put(var3.name(), var3);
            }

         });
         return var1;
      }, (var0) -> {
         return Either.right(var0.values().stream().toList());
      });
      PLAYER_NAME = Codec.string(0, 16).validate((var0) -> {
         return StringUtil.isValidPlayerName(var0) ? DataResult.success(var0) : DataResult.error(() -> {
            return "Player name contained disallowed characters: '" + var0 + "'";
         });
      });
      GAME_PROFILE_WITHOUT_PROPERTIES = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(UUIDUtil.AUTHLIB_CODEC.fieldOf("id").forGetter(GameProfile::getId), PLAYER_NAME.fieldOf("name").forGetter(GameProfile::getName)).apply(var0, GameProfile::new);
      });
      GAME_PROFILE = RecordCodecBuilder.create((var0) -> {
         return var0.group(GAME_PROFILE_WITHOUT_PROPERTIES.forGetter(Function.identity()), PROPERTY_MAP.lenientOptionalFieldOf("properties", new PropertyMap()).forGetter(GameProfile::getProperties)).apply(var0, (var0x, var1) -> {
            var1.forEach((var1x, var2) -> {
               var0x.getProperties().put(var1x, var2);
            });
            return var0x;
         });
      });
      NON_EMPTY_STRING = Codec.STRING.validate((var0) -> {
         return var0.isEmpty() ? DataResult.error(() -> {
            return "Expected non-empty string";
         }) : DataResult.success(var0);
      });
      CODEPOINT = Codec.STRING.comapFlatMap((var0) -> {
         int[] var1 = var0.codePoints().toArray();
         return var1.length != 1 ? DataResult.error(() -> {
            return "Expected one codepoint, got: " + var0;
         }) : DataResult.success(var1[0]);
      }, Character::toString);
      RESOURCE_PATH_CODEC = Codec.STRING.validate((var0) -> {
         return !ResourceLocation.isValidPath(var0) ? DataResult.error(() -> {
            return "Invalid string to use as a resource path element: " + var0;
         }) : DataResult.success(var0);
      });
   }

   public static record StrictUnboundedMapCodec<K, V>(Codec<K> keyCodec, Codec<V> elementCodec) implements Codec<Map<K, V>>, BaseMapCodec<K, V> {
      public StrictUnboundedMapCodec(Codec<K> var1, Codec<V> var2) {
         super();
         this.keyCodec = var1;
         this.elementCodec = var2;
      }

      public <T> DataResult<Map<K, V>> decode(DynamicOps<T> var1, MapLike<T> var2) {
         ImmutableMap.Builder var3 = ImmutableMap.builder();
         Iterator var4 = var2.entries().toList().iterator();

         while(var4.hasNext()) {
            Pair var5 = (Pair)var4.next();
            DataResult var6 = this.keyCodec().parse(var1, var5.getFirst());
            DataResult var7 = this.elementCodec().parse(var1, var5.getSecond());
            DataResult var8 = var6.apply2stable(Pair::of, var7);
            Optional var9 = var8.error();
            if (var9.isPresent()) {
               String var11 = ((DataResult.Error)var9.get()).message();
               return DataResult.error(() -> {
                  if (var6.result().isPresent()) {
                     String var10000 = String.valueOf(var6.result().get());
                     return "Map entry '" + var10000 + "' : " + var11;
                  } else {
                     return var11;
                  }
               });
            }

            if (!var8.result().isPresent()) {
               return DataResult.error(() -> {
                  return "Empty or invalid map contents are not allowed";
               });
            }

            Pair var10 = (Pair)var8.result().get();
            var3.put(var10.getFirst(), var10.getSecond());
         }

         ImmutableMap var12 = var3.build();
         return DataResult.success(var12);
      }

      public <T> DataResult<Pair<Map<K, V>, T>> decode(DynamicOps<T> var1, T var2) {
         return var1.getMap(var2).setLifecycle(Lifecycle.stable()).flatMap((var2x) -> {
            return this.decode(var1, var2x);
         }).map((var1x) -> {
            return Pair.of(var1x, var2);
         });
      }

      public <T> DataResult<T> encode(Map<K, V> var1, DynamicOps<T> var2, T var3) {
         return this.encode((Map)var1, var2, (RecordBuilder)var2.mapBuilder()).build(var3);
      }

      public String toString() {
         String var10000 = String.valueOf(this.keyCodec);
         return "StrictUnboundedMapCodec[" + var10000 + " -> " + String.valueOf(this.elementCodec) + "]";
      }

      public Codec<K> keyCodec() {
         return this.keyCodec;
      }

      public Codec<V> elementCodec() {
         return this.elementCodec;
      }

      // $FF: synthetic method
      public DataResult encode(final Object var1, final DynamicOps var2, final Object var3) {
         return this.encode((Map)var1, var2, var3);
      }
   }

   public static record TagOrElementLocation(ResourceLocation id, boolean tag) {
      public TagOrElementLocation(ResourceLocation var1, boolean var2) {
         super();
         this.id = var1;
         this.tag = var2;
      }

      public String toString() {
         return this.decoratedId();
      }

      private String decoratedId() {
         return this.tag ? "#" + String.valueOf(this.id) : this.id.toString();
      }

      public ResourceLocation id() {
         return this.id;
      }

      public boolean tag() {
         return this.tag;
      }
   }
}
