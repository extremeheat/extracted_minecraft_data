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

public class ProtocolInfoBuilder<T extends PacketListener, B extends ByteBuf> {
   final ConnectionProtocol protocol;
   final PacketFlow flow;
   private final List<ProtocolInfoBuilder.CodecEntry<T, ?, B>> codecs = new ArrayList<>();
   @Nullable
   private BundlerInfo bundlerInfo;

   public ProtocolInfoBuilder(ConnectionProtocol var1, PacketFlow var2) {
      super();
      this.protocol = var1;
      this.flow = var2;
   }

   public <P extends Packet<? super T>> ProtocolInfoBuilder<T, B> addPacket(PacketType<P> var1, StreamCodec<? super B, P> var2) {
      this.codecs.add(new ProtocolInfoBuilder.CodecEntry<>(var1, var2));
      return this;
   }

   public <P extends BundlePacket<? super T>, D extends BundleDelimiterPacket<? super T>> ProtocolInfoBuilder<T, B> withBundlePacket(
      PacketType<P> var1, Function<Iterable<Packet<? super T>>, P> var2, D var3
   ) {
      StreamCodec var4 = StreamCodec.unit(var3);
      PacketType var5 = var3.type();
      this.codecs.add(new ProtocolInfoBuilder.CodecEntry<>(var5, var4));
      this.bundlerInfo = BundlerInfo.createForPacket(var1, var2, var3);
      return this;
   }

   StreamCodec<ByteBuf, Packet<? super T>> buildPacketCodec(Function<ByteBuf, B> var1, List<ProtocolInfoBuilder.CodecEntry<T, ?, B>> var2) {
      ProtocolCodecBuilder var3 = new ProtocolCodecBuilder(this.flow);

      for (ProtocolInfoBuilder.CodecEntry var5 : var2) {
         var5.addToBuilder(var3, var1);
      }

      return var3.build();
   }

   public ProtocolInfo<T> build(Function<ByteBuf, B> var1) {
      return new ProtocolInfoBuilder.Implementation<>(this.protocol, this.flow, this.buildPacketCodec(var1, this.codecs), this.bundlerInfo);
   }

   public ProtocolInfo.Unbound<T, B> buildUnbound() {
      final List var1 = List.copyOf(this.codecs);
      final BundlerInfo var2 = this.bundlerInfo;
      return new ProtocolInfo.Unbound<T, B>() {
         @Override
         public ProtocolInfo<T> bind(Function<ByteBuf, B> var1x) {
            return new ProtocolInfoBuilder.Implementation<>(
               ProtocolInfoBuilder.this.protocol, ProtocolInfoBuilder.this.flow, ProtocolInfoBuilder.this.buildPacketCodec(var1x, var1), var2
            );
         }

         @Override
         public ConnectionProtocol id() {
            return ProtocolInfoBuilder.this.protocol;
         }

         @Override
         public PacketFlow flow() {
            return ProtocolInfoBuilder.this.flow;
         }

         @Override
         public void listPackets(ProtocolInfo.Unbound.PacketVisitor var1x) {
            for (int var2x = 0; var2x < var1.size(); var2x++) {
               ProtocolInfoBuilder.CodecEntry var3 = (ProtocolInfoBuilder.CodecEntry)var1.get(var2x);
               var1x.accept(var3.type, var2x);
            }
         }
      };
   }

   private static <L extends PacketListener, B extends ByteBuf> ProtocolInfo.Unbound<L, B> protocol(
      ConnectionProtocol var0, PacketFlow var1, Consumer<ProtocolInfoBuilder<L, B>> var2
   ) {
      ProtocolInfoBuilder var3 = new ProtocolInfoBuilder(var0, var1);
      var2.accept(var3);
      return var3.buildUnbound();
   }

   public static <T extends ServerboundPacketListener, B extends ByteBuf> ProtocolInfo.Unbound<T, B> serverboundProtocol(
      ConnectionProtocol var0, Consumer<ProtocolInfoBuilder<T, B>> var1
   ) {
      return protocol(var0, PacketFlow.SERVERBOUND, var1);
   }

   public static <T extends ClientboundPacketListener, B extends ByteBuf> ProtocolInfo.Unbound<T, B> clientboundProtocol(
      ConnectionProtocol var0, Consumer<ProtocolInfoBuilder<T, B>> var1
   ) {
      return protocol(var0, PacketFlow.CLIENTBOUND, var1);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
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
// java.lang.NullPointerException
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
