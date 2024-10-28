package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.VarInt;

public class IdDispatchCodec<B extends ByteBuf, V, T> implements StreamCodec<B, V> {
   private static final int UNKNOWN_TYPE = -1;
   private final Function<V, ? extends T> typeGetter;
   private final List<Entry<B, V, T>> byId;
   private final Object2IntMap<T> toId;

   IdDispatchCodec(Function<V, ? extends T> var1, List<Entry<B, V, T>> var2, Object2IntMap<T> var3) {
      super();
      this.typeGetter = var1;
      this.byId = var2;
      this.toId = var3;
   }

   public V decode(B var1) {
      int var2 = VarInt.read(var1);
      if (var2 >= 0 && var2 < this.byId.size()) {
         Entry var3 = (Entry)this.byId.get(var2);

         try {
            return var3.serializer.decode(var1);
         } catch (Exception var5) {
            throw new DecoderException("Failed to decode packet '" + String.valueOf(var3.type) + "'", var5);
         }
      } else {
         throw new DecoderException("Received unknown packet id " + var2);
      }
   }

   public void encode(B var1, V var2) {
      Object var3 = this.typeGetter.apply(var2);
      int var4 = this.toId.getOrDefault(var3, -1);
      if (var4 == -1) {
         throw new EncoderException("Sending unknown packet '" + String.valueOf(var3) + "'");
      } else {
         VarInt.write(var1, var4);
         Entry var5 = (Entry)this.byId.get(var4);

         try {
            StreamCodec var6 = var5.serializer;
            var6.encode(var1, var2);
         } catch (Exception var7) {
            throw new EncoderException("Failed to encode packet '" + String.valueOf(var3) + "'", var7);
         }
      }
   }

   public static <B extends ByteBuf, V, T> Builder<B, V, T> builder(Function<V, ? extends T> var0) {
      return new Builder(var0);
   }

   // $FF: synthetic method
   public void encode(final Object var1, final Object var2) {
      this.encode((ByteBuf)var1, var2);
   }

   // $FF: synthetic method
   public Object decode(final Object var1) {
      return this.decode((ByteBuf)var1);
   }

   private static record Entry<B, V, T>(StreamCodec<? super B, ? extends V> serializer, T type) {
      final StreamCodec<? super B, ? extends V> serializer;
      final T type;

      Entry(StreamCodec<? super B, ? extends V> serializer, T type) {
         super();
         this.serializer = serializer;
         this.type = type;
      }

      public StreamCodec<? super B, ? extends V> serializer() {
         return this.serializer;
      }

      public T type() {
         return this.type;
      }
   }

   public static class Builder<B extends ByteBuf, V, T> {
      private final List<Entry<B, V, T>> entries = new ArrayList();
      private final Function<V, ? extends T> typeGetter;

      Builder(Function<V, ? extends T> var1) {
         super();
         this.typeGetter = var1;
      }

      public Builder<B, V, T> add(T var1, StreamCodec<? super B, ? extends V> var2) {
         this.entries.add(new Entry(var2, var1));
         return this;
      }

      public IdDispatchCodec<B, V, T> build() {
         Object2IntOpenHashMap var1 = new Object2IntOpenHashMap();
         var1.defaultReturnValue(-2);
         Iterator var2 = this.entries.iterator();

         Entry var3;
         int var5;
         do {
            if (!var2.hasNext()) {
               return new IdDispatchCodec(this.typeGetter, List.copyOf(this.entries), var1);
            }

            var3 = (Entry)var2.next();
            int var4 = var1.size();
            var5 = var1.putIfAbsent(var3.type, var4);
         } while(var5 == -2);

         throw new IllegalStateException("Duplicate registration for type " + String.valueOf(var3.type));
      }
   }
}
