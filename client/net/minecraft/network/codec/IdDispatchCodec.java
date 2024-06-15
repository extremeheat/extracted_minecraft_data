package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.network.VarInt;

public class IdDispatchCodec<B extends ByteBuf, V, T> implements StreamCodec<B, V> {
   private static final int UNKNOWN_TYPE = -1;
   private final Function<V, ? extends T> typeGetter;
   private final List<IdDispatchCodec.Entry<B, V, T>> byId;
   private final Object2IntMap<T> toId;

   IdDispatchCodec(Function<V, ? extends T> var1, List<IdDispatchCodec.Entry<B, V, T>> var2, Object2IntMap<T> var3) {
      super();
      this.typeGetter = var1;
      this.byId = var2;
      this.toId = var3;
   }

   public V decode(B var1) {
      int var2 = VarInt.read(var1);
      if (var2 >= 0 && var2 < this.byId.size()) {
         IdDispatchCodec.Entry var3 = this.byId.get(var2);

         try {
            return (V)var3.serializer.decode((B)var1);
         } catch (Exception var5) {
            throw new DecoderException("Failed to decode packet '" + var3.type + "'", var5);
         }
      } else {
         throw new DecoderException("Received unknown packet id " + var2);
      }
   }

   public void encode(B var1, V var2) {
      Object var3 = this.typeGetter.apply((V)var2);
      int var4 = this.toId.getOrDefault(var3, -1);
      if (var4 == -1) {
         throw new EncoderException("Sending unknown packet '" + var3 + "'");
      } else {
         VarInt.write(var1, var4);
         IdDispatchCodec.Entry var5 = this.byId.get(var4);

         try {
            StreamCodec var6 = var5.serializer;
            var6.encode((B)var1, (V)var2);
         } catch (Exception var7) {
            throw new EncoderException("Failed to encode packet '" + var3 + "'", var7);
         }
      }
   }

   public static <B extends ByteBuf, V, T> IdDispatchCodec.Builder<B, V, T> builder(Function<V, ? extends T> var0) {
      return new IdDispatchCodec.Builder<>(var0);
   }

   public static class Builder<B extends ByteBuf, V, T> {
      private final List<IdDispatchCodec.Entry<B, V, T>> entries = new ArrayList<>();
      private final Function<V, ? extends T> typeGetter;

      Builder(Function<V, ? extends T> var1) {
         super();
         this.typeGetter = var1;
      }

      public IdDispatchCodec.Builder<B, V, T> add(T var1, StreamCodec<? super B, ? extends V> var2) {
         this.entries.add(new IdDispatchCodec.Entry<>(var2, (T)var1));
         return this;
      }

      public IdDispatchCodec<B, V, T> build() {
         Object2IntOpenHashMap var1 = new Object2IntOpenHashMap();
         var1.defaultReturnValue(-2);

         for (IdDispatchCodec.Entry var3 : this.entries) {
            int var4 = var1.size();
            int var5 = var1.putIfAbsent(var3.type, var4);
            if (var5 != -2) {
               throw new IllegalStateException("Duplicate registration for type " + var3.type);
            }
         }

         return new IdDispatchCodec<>(this.typeGetter, List.copyOf(this.entries), var1);
      }
   }

   static record Entry<B, V, T>(StreamCodec<? super B, ? extends V> serializer, T type) {

      Entry(StreamCodec<? super B, ? extends V> serializer, T type) {
         super();
         this.serializer = serializer;
         this.type = (T)type;
      }
   }
}
