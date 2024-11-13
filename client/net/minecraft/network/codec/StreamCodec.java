package net.minecraft.network.codec;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import io.netty.buffer.ByteBuf;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface StreamCodec<B, V> extends StreamDecoder<B, V>, StreamEncoder<B, V> {
   static <B, V> StreamCodec<B, V> of(final StreamEncoder<B, V> var0, final StreamDecoder<B, V> var1) {
      return new StreamCodec<B, V>() {
         public V decode(B var1x) {
            return (V)var1.decode(var1x);
         }

         public void encode(B var1x, V var2) {
            var0.encode(var1x, var2);
         }
      };
   }

   static <B, V> StreamCodec<B, V> ofMember(final StreamMemberEncoder<B, V> var0, final StreamDecoder<B, V> var1) {
      return new StreamCodec<B, V>() {
         public V decode(B var1x) {
            return (V)var1.decode(var1x);
         }

         public void encode(B var1x, V var2) {
            var0.encode(var2, var1x);
         }
      };
   }

   static <B, V> StreamCodec<B, V> unit(final V var0) {
      return new StreamCodec<B, V>() {
         public V decode(B var1) {
            return (V)var0;
         }

         public void encode(B var1, V var2) {
            if (!var2.equals(var0)) {
               String var10002 = String.valueOf(var2);
               throw new IllegalStateException("Can't encode '" + var10002 + "', expected '" + String.valueOf(var0) + "'");
            }
         }
      };
   }

   default <O> StreamCodec<B, O> apply(CodecOperation<B, V, O> var1) {
      return var1.apply(this);
   }

   default <O> StreamCodec<B, O> map(final Function<? super V, ? extends O> var1, final Function<? super O, ? extends V> var2) {
      return new StreamCodec<B, O>() {
         public O decode(B var1x) {
            return (O)var1.apply(StreamCodec.this.decode(var1x));
         }

         public void encode(B var1x, O var2x) {
            StreamCodec.this.encode(var1x, var2.apply(var2x));
         }
      };
   }

   default <O extends ByteBuf> StreamCodec<O, V> mapStream(final Function<O, ? extends B> var1) {
      return new StreamCodec<O, V>() {
         public V decode(O var1x) {
            Object var2 = var1.apply(var1x);
            return (V)StreamCodec.this.decode(var2);
         }

         public void encode(O var1x, V var2) {
            Object var3 = var1.apply(var1x);
            StreamCodec.this.encode(var3, var2);
         }

         // $FF: synthetic method
         public void encode(final Object var1x, final Object var2) {
            this.encode((ByteBuf)var1x, var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1x) {
            return this.decode((ByteBuf)var1x);
         }
      };
   }

   default <U> StreamCodec<B, U> dispatch(final Function<? super U, ? extends V> var1, final Function<? super V, ? extends StreamCodec<? super B, ? extends U>> var2) {
      return new StreamCodec<B, U>() {
         public U decode(B var1x) {
            Object var2x = StreamCodec.this.decode(var1x);
            StreamCodec var3 = (StreamCodec)var2.apply(var2x);
            return (U)var3.decode(var1x);
         }

         public void encode(B var1x, U var2x) {
            Object var3 = var1.apply(var2x);
            StreamCodec var4 = (StreamCodec)var2.apply(var3);
            StreamCodec.this.encode(var1x, var3);
            var4.encode(var1x, var2x);
         }
      };
   }

   static <B, C, T1> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> var0, final Function<C, T1> var1, final Function<T1, C> var2) {
      return new StreamCodec<B, C>() {
         public C decode(B var1x) {
            Object var2x = var0.decode(var1x);
            return (C)var2.apply(var2x);
         }

         public void encode(B var1x, C var2x) {
            var0.encode(var1x, var1.apply(var2x));
         }
      };
   }

   static <B, C, T1, T2> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> var0, final Function<C, T1> var1, final StreamCodec<? super B, T2> var2, final Function<C, T2> var3, final BiFunction<T1, T2, C> var4) {
      return new StreamCodec<B, C>() {
         public C decode(B var1x) {
            Object var2x = var0.decode(var1x);
            Object var3x = var2.decode(var1x);
            return (C)var4.apply(var2x, var3x);
         }

         public void encode(B var1x, C var2x) {
            var0.encode(var1x, var1.apply(var2x));
            var2.encode(var1x, var3.apply(var2x));
         }
      };
   }

   static <B, C, T1, T2, T3> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> var0, final Function<C, T1> var1, final StreamCodec<? super B, T2> var2, final Function<C, T2> var3, final StreamCodec<? super B, T3> var4, final Function<C, T3> var5, final Function3<T1, T2, T3, C> var6) {
      return new StreamCodec<B, C>() {
         public C decode(B var1x) {
            Object var2x = var0.decode(var1x);
            Object var3x = var2.decode(var1x);
            Object var4x = var4.decode(var1x);
            return (C)var6.apply(var2x, var3x, var4x);
         }

         public void encode(B var1x, C var2x) {
            var0.encode(var1x, var1.apply(var2x));
            var2.encode(var1x, var3.apply(var2x));
            var4.encode(var1x, var5.apply(var2x));
         }
      };
   }

   static <B, C, T1, T2, T3, T4> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> var0, final Function<C, T1> var1, final StreamCodec<? super B, T2> var2, final Function<C, T2> var3, final StreamCodec<? super B, T3> var4, final Function<C, T3> var5, final StreamCodec<? super B, T4> var6, final Function<C, T4> var7, final Function4<T1, T2, T3, T4, C> var8) {
      return new StreamCodec<B, C>() {
         public C decode(B var1x) {
            Object var2x = var0.decode(var1x);
            Object var3x = var2.decode(var1x);
            Object var4x = var4.decode(var1x);
            Object var5x = var6.decode(var1x);
            return (C)var8.apply(var2x, var3x, var4x, var5x);
         }

         public void encode(B var1x, C var2x) {
            var0.encode(var1x, var1.apply(var2x));
            var2.encode(var1x, var3.apply(var2x));
            var4.encode(var1x, var5.apply(var2x));
            var6.encode(var1x, var7.apply(var2x));
         }
      };
   }

   static <B, C, T1, T2, T3, T4, T5> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> var0, final Function<C, T1> var1, final StreamCodec<? super B, T2> var2, final Function<C, T2> var3, final StreamCodec<? super B, T3> var4, final Function<C, T3> var5, final StreamCodec<? super B, T4> var6, final Function<C, T4> var7, final StreamCodec<? super B, T5> var8, final Function<C, T5> var9, final Function5<T1, T2, T3, T4, T5, C> var10) {
      return new StreamCodec<B, C>() {
         public C decode(B var1x) {
            Object var2x = var0.decode(var1x);
            Object var3x = var2.decode(var1x);
            Object var4x = var4.decode(var1x);
            Object var5x = var6.decode(var1x);
            Object var6x = var8.decode(var1x);
            return (C)var10.apply(var2x, var3x, var4x, var5x, var6x);
         }

         public void encode(B var1x, C var2x) {
            var0.encode(var1x, var1.apply(var2x));
            var2.encode(var1x, var3.apply(var2x));
            var4.encode(var1x, var5.apply(var2x));
            var6.encode(var1x, var7.apply(var2x));
            var8.encode(var1x, var9.apply(var2x));
         }
      };
   }

   static <B, C, T1, T2, T3, T4, T5, T6> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> var0, final Function<C, T1> var1, final StreamCodec<? super B, T2> var2, final Function<C, T2> var3, final StreamCodec<? super B, T3> var4, final Function<C, T3> var5, final StreamCodec<? super B, T4> var6, final Function<C, T4> var7, final StreamCodec<? super B, T5> var8, final Function<C, T5> var9, final StreamCodec<? super B, T6> var10, final Function<C, T6> var11, final Function6<T1, T2, T3, T4, T5, T6, C> var12) {
      return new StreamCodec<B, C>() {
         public C decode(B var1x) {
            Object var2x = var0.decode(var1x);
            Object var3x = var2.decode(var1x);
            Object var4x = var4.decode(var1x);
            Object var5x = var6.decode(var1x);
            Object var6x = var8.decode(var1x);
            Object var7x = var10.decode(var1x);
            return (C)var12.apply(var2x, var3x, var4x, var5x, var6x, var7x);
         }

         public void encode(B var1x, C var2x) {
            var0.encode(var1x, var1.apply(var2x));
            var2.encode(var1x, var3.apply(var2x));
            var4.encode(var1x, var5.apply(var2x));
            var6.encode(var1x, var7.apply(var2x));
            var8.encode(var1x, var9.apply(var2x));
            var10.encode(var1x, var11.apply(var2x));
         }
      };
   }

   static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> var0, final Function<C, T1> var1, final StreamCodec<? super B, T2> var2, final Function<C, T2> var3, final StreamCodec<? super B, T3> var4, final Function<C, T3> var5, final StreamCodec<? super B, T4> var6, final Function<C, T4> var7, final StreamCodec<? super B, T5> var8, final Function<C, T5> var9, final StreamCodec<? super B, T6> var10, final Function<C, T6> var11, final StreamCodec<? super B, T7> var12, final Function<C, T7> var13, final Function7<T1, T2, T3, T4, T5, T6, T7, C> var14) {
      return new StreamCodec<B, C>() {
         public C decode(B var1x) {
            Object var2x = var0.decode(var1x);
            Object var3x = var2.decode(var1x);
            Object var4x = var4.decode(var1x);
            Object var5x = var6.decode(var1x);
            Object var6x = var8.decode(var1x);
            Object var7x = var10.decode(var1x);
            Object var8x = var12.decode(var1x);
            return (C)var14.apply(var2x, var3x, var4x, var5x, var6x, var7x, var8x);
         }

         public void encode(B var1x, C var2x) {
            var0.encode(var1x, var1.apply(var2x));
            var2.encode(var1x, var3.apply(var2x));
            var4.encode(var1x, var5.apply(var2x));
            var6.encode(var1x, var7.apply(var2x));
            var8.encode(var1x, var9.apply(var2x));
            var10.encode(var1x, var11.apply(var2x));
            var12.encode(var1x, var13.apply(var2x));
         }
      };
   }

   static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(final StreamCodec<? super B, T1> var0, final Function<C, T1> var1, final StreamCodec<? super B, T2> var2, final Function<C, T2> var3, final StreamCodec<? super B, T3> var4, final Function<C, T3> var5, final StreamCodec<? super B, T4> var6, final Function<C, T4> var7, final StreamCodec<? super B, T5> var8, final Function<C, T5> var9, final StreamCodec<? super B, T6> var10, final Function<C, T6> var11, final StreamCodec<? super B, T7> var12, final Function<C, T7> var13, final StreamCodec<? super B, T8> var14, final Function<C, T8> var15, final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> var16) {
      return new StreamCodec<B, C>() {
         public C decode(B var1x) {
            Object var2x = var0.decode(var1x);
            Object var3x = var2.decode(var1x);
            Object var4x = var4.decode(var1x);
            Object var5x = var6.decode(var1x);
            Object var6x = var8.decode(var1x);
            Object var7x = var10.decode(var1x);
            Object var8x = var12.decode(var1x);
            Object var9x = var14.decode(var1x);
            return (C)var16.apply(var2x, var3x, var4x, var5x, var6x, var7x, var8x, var9x);
         }

         public void encode(B var1x, C var2x) {
            var0.encode(var1x, var1.apply(var2x));
            var2.encode(var1x, var3.apply(var2x));
            var4.encode(var1x, var5.apply(var2x));
            var6.encode(var1x, var7.apply(var2x));
            var8.encode(var1x, var9.apply(var2x));
            var10.encode(var1x, var11.apply(var2x));
            var12.encode(var1x, var13.apply(var2x));
            var14.encode(var1x, var15.apply(var2x));
         }
      };
   }

   static <B, T> StreamCodec<B, T> recursive(final UnaryOperator<StreamCodec<B, T>> var0) {
      return new StreamCodec<B, T>() {
         private final Supplier<StreamCodec<B, T>> inner = Suppliers.memoize(() -> (StreamCodec)var0.apply(this));

         public T decode(B var1) {
            return (T)((StreamCodec)this.inner.get()).decode(var1);
         }

         public void encode(B var1, T var2) {
            ((StreamCodec)this.inner.get()).encode(var1, var2);
         }
      };
   }

   default <S extends B> StreamCodec<S, V> cast() {
      return this;
   }

   @FunctionalInterface
   public interface CodecOperation<B, S, T> {
      StreamCodec<B, T> apply(StreamCodec<B, S> var1);
   }
}
