package net.minecraft.network.protocol;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.ClientboundPacketListener;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.codec.StreamCodec;

public class ProtocolInfoBuilder<T extends PacketListener, B extends ByteBuf> {
   private final ConnectionProtocol protocol;
   private final PacketFlow flow;
   private final List<CodecEntry<T, ?, B>> codecs = new ArrayList();
   @Nullable
   private BundlerInfo bundlerInfo;

   public ProtocolInfoBuilder(ConnectionProtocol var1, PacketFlow var2) {
      super();
      this.protocol = var1;
      this.flow = var2;
   }

   public <P extends Packet<? super T>> ProtocolInfoBuilder<T, B> addPacket(PacketType<P> var1, StreamCodec<? super B, P> var2) {
      this.codecs.add(new CodecEntry(var1, var2));
      return this;
   }

   public <P extends BundlePacket<? super T>, D extends BundleDelimiterPacket<? super T>> ProtocolInfoBuilder<T, B> withBundlePacket(PacketType<P> var1, Function<Iterable<Packet<? super T>>, P> var2, D var3) {
      StreamCodec var4 = StreamCodec.unit(var3);
      PacketType var5 = var3.type();
      this.codecs.add(new CodecEntry(var5, var4));
      this.bundlerInfo = BundlerInfo.createForPacket(var1, var2, var3);
      return this;
   }

   private StreamCodec<ByteBuf, Packet<? super T>> buildPacketCodec(Function<ByteBuf, B> var1, List<CodecEntry<T, ?, B>> var2) {
      ProtocolCodecBuilder var3 = new ProtocolCodecBuilder(this.flow);
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         CodecEntry var5 = (CodecEntry)var4.next();
         var5.addToBuilder(var3, var1);
      }

      return var3.build();
   }

   public ProtocolInfo<T> build(Function<ByteBuf, B> var1) {
      return new Implementation(this.protocol, this.flow, this.buildPacketCodec(var1, this.codecs), this.bundlerInfo);
   }

   public ProtocolInfo.Unbound<T, B> buildUnbound() {
      List var1 = List.copyOf(this.codecs);
      BundlerInfo var2 = this.bundlerInfo;
      return (var3) -> {
         return new Implementation(this.protocol, this.flow, this.buildPacketCodec(var3, var1), var2);
      };
   }

   private static <L extends PacketListener> ProtocolInfo<L> protocol(ConnectionProtocol var0, PacketFlow var1, Consumer<ProtocolInfoBuilder<L, FriendlyByteBuf>> var2) {
      ProtocolInfoBuilder var3 = new ProtocolInfoBuilder(var0, var1);
      var2.accept(var3);
      return var3.build(FriendlyByteBuf::new);
   }

   public static <T extends ServerboundPacketListener> ProtocolInfo<T> serverboundProtocol(ConnectionProtocol var0, Consumer<ProtocolInfoBuilder<T, FriendlyByteBuf>> var1) {
      return protocol(var0, PacketFlow.SERVERBOUND, var1);
   }

   public static <T extends ClientboundPacketListener> ProtocolInfo<T> clientboundProtocol(ConnectionProtocol var0, Consumer<ProtocolInfoBuilder<T, FriendlyByteBuf>> var1) {
      return protocol(var0, PacketFlow.CLIENTBOUND, var1);
   }

   private static <L extends PacketListener, B extends ByteBuf> ProtocolInfo.Unbound<L, B> protocolUnbound(ConnectionProtocol var0, PacketFlow var1, Consumer<ProtocolInfoBuilder<L, B>> var2) {
      ProtocolInfoBuilder var3 = new ProtocolInfoBuilder(var0, var1);
      var2.accept(var3);
      return var3.buildUnbound();
   }

   public static <T extends ServerboundPacketListener, B extends ByteBuf> ProtocolInfo.Unbound<T, B> serverboundProtocolUnbound(ConnectionProtocol var0, Consumer<ProtocolInfoBuilder<T, B>> var1) {
      return protocolUnbound(var0, PacketFlow.SERVERBOUND, var1);
   }

   public static <T extends ClientboundPacketListener, B extends ByteBuf> ProtocolInfo.Unbound<T, B> clientboundProtocolUnbound(ConnectionProtocol var0, Consumer<ProtocolInfoBuilder<T, B>> var1) {
      return protocolUnbound(var0, PacketFlow.CLIENTBOUND, var1);
   }

   private static record CodecEntry<T extends PacketListener, P extends Packet<? super T>, B extends ByteBuf>(PacketType<P> type, StreamCodec<? super B, P> serializer) {
      CodecEntry(PacketType<P> type, StreamCodec<? super B, P> serializer) {
         super();
         this.type = type;
         this.serializer = serializer;
      }

      public void addToBuilder(ProtocolCodecBuilder<ByteBuf, T> var1, Function<ByteBuf, B> var2) {
         StreamCodec var3 = this.serializer.mapStream(var2);
         var1.add(this.type, var3);
      }

      public PacketType<P> type() {
         return this.type;
      }

      public StreamCodec<? super B, P> serializer() {
         return this.serializer;
      }
   }

   static record Implementation<L extends PacketListener>(ConnectionProtocol id, PacketFlow flow, StreamCodec<ByteBuf, Packet<? super L>> codec, @Nullable BundlerInfo bundlerInfo) implements ProtocolInfo<L> {
      Implementation(ConnectionProtocol id, PacketFlow flow, StreamCodec<ByteBuf, Packet<? super L>> codec, @Nullable BundlerInfo bundlerInfo) {
         super();
         this.id = id;
         this.flow = flow;
         this.codec = codec;
         this.bundlerInfo = bundlerInfo;
      }

      @Nullable
      public BundlerInfo bundlerInfo() {
         return this.bundlerInfo;
      }

      public ConnectionProtocol id() {
         return this.id;
      }

      public PacketFlow flow() {
         return this.flow;
      }

      public StreamCodec<ByteBuf, Packet<? super L>> codec() {
         return this.codec;
      }
   }
}
