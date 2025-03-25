package net.minecraft.network.protocol;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.ClientboundPacketListener;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketListener;
import net.minecraft.network.ProtocolInfo;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

public class ProtocolInfoBuilder<T extends PacketListener, B extends ByteBuf, C> {
   final ConnectionProtocol protocol;
   final PacketFlow flow;
   private final List<CodecEntry<T, ?, B, C>> codecs = new ArrayList();
   @Nullable
   private BundlerInfo bundlerInfo;

   public ProtocolInfoBuilder(ConnectionProtocol var1, PacketFlow var2) {
      super();
      this.protocol = var1;
      this.flow = var2;
   }

   public <P extends Packet<? super T>> ProtocolInfoBuilder<T, B, C> addPacket(PacketType<P> var1, StreamCodec<? super B, P> var2) {
      this.codecs.add(new CodecEntry(var1, var2, (CodecModifier)null));
      return this;
   }

   public <P extends Packet<? super T>> ProtocolInfoBuilder<T, B, C> addPacket(PacketType<P> var1, StreamCodec<? super B, P> var2, CodecModifier<B, P, C> var3) {
      this.codecs.add(new CodecEntry(var1, var2, var3));
      return this;
   }

   public <P extends BundlePacket<? super T>, D extends BundleDelimiterPacket<? super T>> ProtocolInfoBuilder<T, B, C> withBundlePacket(PacketType<P> var1, Function<Iterable<Packet<? super T>>, P> var2, D var3) {
      StreamCodec var4 = StreamCodec.unit(var3);
      PacketType var5 = var3.type();
      this.codecs.add(new CodecEntry(var5, var4, (CodecModifier)null));
      this.bundlerInfo = BundlerInfo.createForPacket(var1, var2, var3);
      return this;
   }

   StreamCodec<ByteBuf, Packet<? super T>> buildPacketCodec(Function<ByteBuf, B> var1, List<CodecEntry<T, ?, B, C>> var2, C var3) {
      ProtocolCodecBuilder var4 = new ProtocolCodecBuilder(this.flow);

      for(CodecEntry var6 : var2) {
         var6.addToBuilder(var4, var1, var3);
      }

      return var4.build();
   }

   private static ProtocolInfo.Details buildDetails(final ConnectionProtocol var0, final PacketFlow var1, final List<? extends CodecEntry<?, ?, ?, ?>> var2) {
      return new ProtocolInfo.Details() {
         public ConnectionProtocol id() {
            return var0;
         }

         public PacketFlow flow() {
            return var1;
         }

         public void listPackets(ProtocolInfo.Details.PacketVisitor var1x) {
            for(int var2x = 0; var2x < var2.size(); ++var2x) {
               CodecEntry var3 = (CodecEntry)var2.get(var2x);
               var1x.accept(var3.type, var2x);
            }

         }
      };
   }

   public SimpleUnboundProtocol<T, B> buildUnbound(final C var1) {
      final List var2 = List.copyOf(this.codecs);
      final BundlerInfo var3 = this.bundlerInfo;
      final ProtocolInfo.Details var4 = buildDetails(this.protocol, this.flow, var2);
      return new SimpleUnboundProtocol<T, B>() {
         public ProtocolInfo<T> bind(Function<ByteBuf, B> var1x) {
            return new Implementation<T>(ProtocolInfoBuilder.this.protocol, ProtocolInfoBuilder.this.flow, ProtocolInfoBuilder.this.buildPacketCodec(var1x, var2, var1), var3);
         }

         public ProtocolInfo.Details details() {
            return var4;
         }
      };
   }

   public UnboundProtocol<T, B, C> buildUnbound() {
      final List var1 = List.copyOf(this.codecs);
      final BundlerInfo var2 = this.bundlerInfo;
      final ProtocolInfo.Details var3 = buildDetails(this.protocol, this.flow, var1);
      return new UnboundProtocol<T, B, C>() {
         public ProtocolInfo<T> bind(Function<ByteBuf, B> var1x, C var2x) {
            return new Implementation<T>(ProtocolInfoBuilder.this.protocol, ProtocolInfoBuilder.this.flow, ProtocolInfoBuilder.this.buildPacketCodec(var1x, var1, var2x), var2);
         }

         public ProtocolInfo.Details details() {
            return var3;
         }
      };
   }

   private static <L extends PacketListener, B extends ByteBuf> SimpleUnboundProtocol<L, B> protocol(ConnectionProtocol var0, PacketFlow var1, Consumer<ProtocolInfoBuilder<L, B, Unit>> var2) {
      ProtocolInfoBuilder var3 = new ProtocolInfoBuilder(var0, var1);
      var2.accept(var3);
      return var3.buildUnbound(Unit.INSTANCE);
   }

   public static <T extends ServerboundPacketListener, B extends ByteBuf> SimpleUnboundProtocol<T, B> serverboundProtocol(ConnectionProtocol var0, Consumer<ProtocolInfoBuilder<T, B, Unit>> var1) {
      return protocol(var0, PacketFlow.SERVERBOUND, var1);
   }

   public static <T extends ClientboundPacketListener, B extends ByteBuf> SimpleUnboundProtocol<T, B> clientboundProtocol(ConnectionProtocol var0, Consumer<ProtocolInfoBuilder<T, B, Unit>> var1) {
      return protocol(var0, PacketFlow.CLIENTBOUND, var1);
   }

   private static <L extends PacketListener, B extends ByteBuf, C> UnboundProtocol<L, B, C> contextProtocol(ConnectionProtocol var0, PacketFlow var1, Consumer<ProtocolInfoBuilder<L, B, C>> var2) {
      ProtocolInfoBuilder var3 = new ProtocolInfoBuilder(var0, var1);
      var2.accept(var3);
      return var3.buildUnbound();
   }

   public static <T extends ServerboundPacketListener, B extends ByteBuf, C> UnboundProtocol<T, B, C> contextServerboundProtocol(ConnectionProtocol var0, Consumer<ProtocolInfoBuilder<T, B, C>> var1) {
      return contextProtocol(var0, PacketFlow.SERVERBOUND, var1);
   }

   public static <T extends ClientboundPacketListener, B extends ByteBuf, C> UnboundProtocol<T, B, C> contextClientboundProtocol(ConnectionProtocol var0, Consumer<ProtocolInfoBuilder<T, B, C>> var1) {
      return contextProtocol(var0, PacketFlow.CLIENTBOUND, var1);
   }

   static record CodecEntry<T extends PacketListener, P extends Packet<? super T>, B extends ByteBuf, C>(PacketType<P> type, StreamCodec<? super B, P> serializer, @Nullable CodecModifier<B, P, C> modifier) {
      final PacketType<P> type;

      CodecEntry(PacketType<P> var1, StreamCodec<? super B, P> var2, @Nullable CodecModifier<B, P, C> var3) {
         super();
         this.type = var1;
         this.serializer = var2;
         this.modifier = var3;
      }

      public void addToBuilder(ProtocolCodecBuilder<ByteBuf, T> var1, Function<ByteBuf, B> var2, C var3) {
         StreamCodec var4;
         if (this.modifier != null) {
            var4 = this.modifier.apply(this.serializer, var3);
         } else {
            var4 = this.serializer;
         }

         StreamCodec var5 = var4.mapStream(var2);
         var1.add(this.type, var5);
      }
   }

   static record Implementation<L extends PacketListener>(ConnectionProtocol id, PacketFlow flow, StreamCodec<ByteBuf, Packet<? super L>> codec, @Nullable BundlerInfo bundlerInfo) implements ProtocolInfo<L> {
      Implementation(ConnectionProtocol var1, PacketFlow var2, StreamCodec<ByteBuf, Packet<? super L>> var3, @Nullable BundlerInfo var4) {
         super();
         this.id = var1;
         this.flow = var2;
         this.codec = var3;
         this.bundlerInfo = var4;
      }
   }
}
