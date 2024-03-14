package net.minecraft.network.protocol;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;

public interface Packet<T extends PacketListener> {
   PacketType<? extends Packet<T>> type();

   void handle(T var1);

   default boolean isSkippable() {
      return false;
   }

   default boolean isTerminal() {
      return false;
   }

   static <B extends ByteBuf, T extends Packet<?>> StreamCodec<B, T> codec(StreamMemberEncoder<B, T> var0, StreamDecoder<B, T> var1) {
      return StreamCodec.ofMember(var0, var1);
   }
}
