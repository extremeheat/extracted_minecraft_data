package net.minecraft.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
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
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableObject;

public class ExtraCodecs {
   public static final Codec<UUID> UUID;
   public static final Codec<Integer> NON_NEGATIVE_INT;
   public static final Codec<Integer> POSITIVE_INT;
   public static final Codec<Float> POSITIVE_FLOAT;
   public static final Codec<Pattern> PATTERN;
   public static final Codec<Instant> INSTANT_ISO8601;
   public static final Codec<byte[]> BASE64_STRING;
   public static final Codec<TagOrElementLocation> TAG_OR_ELEMENT_ID;
   public static final Function<Optional<Long>, OptionalLong> toOptionalLong;
   public static final Function<OptionalLong, Optional<Long>> fromOptionalLong;

   public ExtraCodecs() {
      super();
   }

   public static <F, S> Codec<Either<F, S>> xor(Codec<F> var0, Codec<S> var1) {
      return new XorCodec(var0, var1);
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
      Codec var8 = (new EitherCodec(var6, var7)).xmap((var0x) -> {
         return var0x.map((var0) -> {
            return var0;
         }, (var0) -> {
            return var0;
         });
      }, Either::left);
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
            return var5.isPresent() ? var3 : DataResult.error("(" + (String)var4.getValue() + " -> using default)", Pair.of(var0, var2));
         }

         public <T> DataResult<T> coApply(DynamicOps<T> var1, A var2, DataResult<T> var3) {
            return var3;
         }

         public String toString() {
            return "OrElsePartial[" + var0 + "]";
         }
      };
   }

   public static <E> Codec<E> idResolverCodec(ToIntFunction<E> var0, IntFunction<E> var1, int var2) {
      return Codec.INT.flatXmap((var1x) -> {
         return (DataResult)Optional.ofNullable(var1.apply(var1x)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Unknown element id: " + var1x);
         });
      }, (var2x) -> {
         int var3 = var0.applyAsInt(var2x);
         return var3 == var2 ? DataResult.error("Element with unknown id: " + var2x) : DataResult.success(var3);
      });
   }

   public static <E> Codec<E> stringResolverCodec(Function<E, String> var0, Function<String, E> var1) {
      return Codec.STRING.flatXmap((var1x) -> {
         return (DataResult)Optional.ofNullable(var1.apply(var1x)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Unknown element name:" + var1x);
         });
      }, (var1x) -> {
         return (DataResult)Optional.ofNullable((String)var0.apply(var1x)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Element with unknown name: " + var1x);
         });
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
            return var0 + " orCompressed " + var1;
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
            return "WithLifecycle[" + var1 + " " + var2 + "]";
         }
      });
   }

   private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRangeWithMessage(N var0, N var1, Function<N, String> var2) {
      return (var3) -> {
         return ((Comparable)var3).compareTo(var0) >= 0 && ((Comparable)var3).compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error((String)var2.apply(var3));
      };
   }

   private static Codec<Integer> intRangeWithMessage(int var0, int var1, Function<Integer, String> var2) {
      Function var3 = checkRangeWithMessage(var0, var1, var2);
      return Codec.INT.flatXmap(var3, var3);
   }

   private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRangeMinExclusiveWithMessage(N var0, N var1, Function<N, String> var2) {
      return (var3) -> {
         return ((Comparable)var3).compareTo(var0) > 0 && ((Comparable)var3).compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error((String)var2.apply(var3));
      };
   }

   private static Codec<Float> floatRangeMinExclusiveWithMessage(float var0, float var1, Function<Float, String> var2) {
      Function var3 = checkRangeMinExclusiveWithMessage(var0, var1, var2);
      return Codec.FLOAT.flatXmap(var3, var3);
   }

   public static <T> Function<List<T>, DataResult<List<T>>> nonEmptyListCheck() {
      return (var0) -> {
         return var0.isEmpty() ? DataResult.error("List must have contents") : DataResult.success(var0);
      };
   }

   public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> var0) {
      return var0.flatXmap(nonEmptyListCheck(), nonEmptyListCheck());
   }

   public static <T> Function<HolderSet<T>, DataResult<HolderSet<T>>> nonEmptyHolderSetCheck() {
      return (var0) -> {
         return var0.unwrap().right().filter(List::isEmpty).isPresent() ? DataResult.error("List must have contents") : DataResult.success(var0);
      };
   }

   public static <T> Codec<HolderSet<T>> nonEmptyHolderSet(Codec<HolderSet<T>> var0) {
      return var0.flatXmap(nonEmptyHolderSetCheck(), nonEmptyHolderSetCheck());
   }

   public static <A> Codec<A> lazyInitializedCodec(Supplier<Codec<A>> var0) {
      return new LazyInitializedCodec(var0);
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
            return "ContextRetrievalCodec[" + var0 + "]";
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
      PrimitiveCodec var10000 = Codec.STRING;
      Function var10001 = (var1) -> {
         try {
            return DataResult.success(Instant.from(var0.parse(var1)));
         } catch (Exception var3) {
            return DataResult.error(var3.getMessage());
         }
      };
      Objects.requireNonNull(var0);
      return var10000.comapFlatMap(var10001, var0::format);
   }

   public static MapCodec<OptionalLong> asOptionalLong(MapCodec<Optional<Long>> var0) {
      return var0.xmap(toOptionalLong, fromOptionalLong);
   }

   static {
      UUID = UUIDUtil.CODEC;
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
            return DataResult.error("Invalid regex pattern '" + var0 + "': " + var2.getMessage());
         }
      }, Pattern::pattern);
      INSTANT_ISO8601 = instantCodec(DateTimeFormatter.ISO_INSTANT);
      BASE64_STRING = Codec.STRING.comapFlatMap((var0) -> {
         try {
            return DataResult.success(Base64.getDecoder().decode(var0));
         } catch (IllegalArgumentException var2) {
            return DataResult.error("Malformed base64 string");
         }
      }, (var0) -> {
         return Base64.getEncoder().encodeToString(var0);
      });
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
   }

   private static final class XorCodec<F, S> implements Codec<Either<F, S>> {
      private final Codec<F> first;
      private final Codec<S> second;

      public XorCodec(Codec<F> var1, Codec<S> var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> var1, T var2) {
         DataResult var3 = this.first.decode(var1, var2).map((var0) -> {
            return var0.mapFirst(Either::left);
         });
         DataResult var4 = this.second.decode(var1, var2).map((var0) -> {
            return var0.mapFirst(Either::right);
         });
         Optional var5 = var3.result();
         Optional var6 = var4.result();
         if (var5.isPresent() && var6.isPresent()) {
            return DataResult.error("Both alternatives read successfully, can not pick the correct one; first: " + var5.get() + " second: " + var6.get(), (Pair)var5.get());
         } else {
            return var5.isPresent() ? var3 : var4;
         }
      }

      public <T> DataResult<T> encode(Either<F, S> var1, DynamicOps<T> var2, T var3) {
         return (DataResult)var1.map((var3x) -> {
            return this.first.encode(var3x, var2, var3);
         }, (var3x) -> {
            return this.second.encode(var3x, var2, var3);
         });
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            XorCodec var2 = (XorCodec)var1;
            return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.first, this.second});
      }

      public String toString() {
         return "XorCodec[" + this.first + ", " + this.second + "]";
      }

      // $FF: synthetic method
      public DataResult encode(Object var1, DynamicOps var2, Object var3) {
         return this.encode((Either)var1, var2, var3);
      }
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
         DataResult var3 = this.first.decode(var1, var2).map((var0) -> {
            return var0.mapFirst(Either::left);
         });
         if (!var3.error().isPresent()) {
            return var3;
         } else {
            DataResult var4 = this.second.decode(var1, var2).map((var0) -> {
               return var0.mapFirst(Either::right);
            });
            return !var4.error().isPresent() ? var4 : var3.apply2((var0, var1x) -> {
               return var1x;
            }, var4);
         }
      }

      public <T> DataResult<T> encode(Either<F, S> var1, DynamicOps<T> var2, T var3) {
         return (DataResult)var1.map((var3x) -> {
            return this.first.encode(var3x, var2, var3);
         }, (var3x) -> {
            return this.second.encode(var3x, var2, var3);
         });
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            EitherCodec var2 = (EitherCodec)var1;
            return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.first, this.second});
      }

      public String toString() {
         return "EitherCodec[" + this.first + ", " + this.second + "]";
      }

      // $FF: synthetic method
      public DataResult encode(Object var1, DynamicOps var2, Object var3) {
         return this.encode((Either)var1, var2, var3);
      }
   }

   static record LazyInitializedCodec<A>(Supplier<Codec<A>> a) implements Codec<A> {
      private final Supplier<Codec<A>> delegate;

      LazyInitializedCodec(Supplier<Codec<A>> var1) {
         super();
         Objects.requireNonNull(var1);
         com.google.common.base.Supplier var2 = Suppliers.memoize(var1::get);
         this.delegate = var2;
      }

      public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
         return ((Codec)this.delegate.get()).decode(var1, var2);
      }

      public <T> DataResult<T> encode(A var1, DynamicOps<T> var2, T var3) {
         return ((Codec)this.delegate.get()).encode(var1, var2, var3);
      }

      public Supplier<Codec<A>> delegate() {
         return this.delegate;
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

      public ResourceLocation id() {
         return this.id;
      }

      public boolean tag() {
         return this.tag;
      }
   }
}
