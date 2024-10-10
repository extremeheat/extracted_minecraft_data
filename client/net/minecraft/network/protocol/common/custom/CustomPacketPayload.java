package net.minecraft.network.protocol.common.custom;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.resources.ResourceLocation;

public interface CustomPacketPayload {
   CustomPacketPayload.Type<? extends CustomPacketPayload> type();

   static <B extends ByteBuf, T extends CustomPacketPayload> StreamCodec<B, T> codec(StreamMemberEncoder<B, T> var0, StreamDecoder<B, T> var1) {
      return StreamCodec.ofMember(var0, var1);
   }

   static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> createType(String var0) {
      return new CustomPacketPayload.Type<>(ResourceLocation.withDefaultNamespace(var0));
   }

   static <B extends FriendlyByteBuf> StreamCodec<B, CustomPacketPayload> codec(
      final CustomPacketPayload.FallbackProvider<B> var0, List<CustomPacketPayload.TypeAndCodec<? super B, ?>> var1
   ) {
      final Map var2 = var1.stream().collect(Collectors.toUnmodifiableMap(var0x -> var0x.type().id(), CustomPacketPayload.TypeAndCodec::codec));
      return new StreamCodec<B, CustomPacketPayload>() {
         private StreamCodec<? super B, ? extends CustomPacketPayload> findCodec(ResourceLocation var1) {
            StreamCodec var2x = (StreamCodec)var2.get(var1);
            return var2x != null ? var2x : var0.create(var1);
         }

         private <T extends CustomPacketPayload> void writeCap(B var1, CustomPacketPayload.Type<T> var2x, CustomPacketPayload var3) {
            var1.writeResourceLocation(var2x.id());
            StreamCodec var4 = this.findCodec(var2x.id);
            var4.encode((B)var1, var3);
         }

         public void encode(B var1, CustomPacketPayload var2x) {
            this.writeCap((B)var1, var2x.type(), var2x);
         }

         public CustomPacketPayload decode(B var1) {
            ResourceLocation var2x = var1.readResourceLocation();
            return (CustomPacketPayload)this.findCodec(var2x).decode((B)var1);
         }
      };
   }

   public interface FallbackProvider<B extends FriendlyByteBuf> {
      StreamCodec<B, ? extends CustomPacketPayload> create(ResourceLocation var1);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
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
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
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
