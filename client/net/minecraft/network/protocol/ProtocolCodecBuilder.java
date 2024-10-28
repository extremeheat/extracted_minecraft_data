package net.minecraft.network.protocol;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.codec.IdDispatchCodec;
import net.minecraft.network.codec.StreamCodec;

public class ProtocolCodecBuilder<B extends ByteBuf, L extends PacketListener> {
   private final IdDispatchCodec.Builder<B, Packet<? super L>, PacketType<? extends Packet<? super L>>> dispatchBuilder = IdDispatchCodec.builder(Packet::type);
   private final PacketFlow flow;

   public ProtocolCodecBuilder(PacketFlow var1) {
      super();
      this.flow = var1;
   }

   public <T extends Packet<? super L>> ProtocolCodecBuilder<B, L> add(PacketType<T> var1, StreamCodec<? super B, T> var2) {
      if (var1.flow() != this.flow) {
         String var10002 = String.valueOf(var1);
         throw new IllegalArgumentException("Invalid packet flow for packet " + var10002 + ", expected " + this.flow.name());
      } else {
         this.dispatchBuilder.add(var1, var2);
         return this;
      }
   }

   public StreamCodec<B, Packet<? super L>> build() {
      return this.dispatchBuilder.build();
   }
}
