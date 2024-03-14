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
      return new CustomPacketPayload.Type<>(new ResourceLocation(var0));
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
            var4.encode(var1, var3);
         }

         public void encode(B var1, CustomPacketPayload var2x) {
            this.writeCap((B)var1, var2x.type(), var2x);
         }

         public CustomPacketPayload decode(B var1) {
            ResourceLocation var2x = var1.readResourceLocation();
            return (CustomPacketPayload)this.findCodec(var2x).decode(var1);
         }
      };
   }

   public interface FallbackProvider<B extends FriendlyByteBuf> {
      StreamCodec<B, ? extends CustomPacketPayload> create(ResourceLocation var1);
   }

   public static record Type<T extends CustomPacketPayload>(ResourceLocation a) {
      final ResourceLocation id;

      public Type(ResourceLocation var1) {
         super();
         this.id = var1;
      }
   }

   public static record TypeAndCodec<B extends FriendlyByteBuf, T extends CustomPacketPayload>(CustomPacketPayload.Type<T> a, StreamCodec<B, T> b) {
      private final CustomPacketPayload.Type<T> type;
      private final StreamCodec<B, T> codec;

      public TypeAndCodec(CustomPacketPayload.Type<T> var1, StreamCodec<B, T> var2) {
         super();
         this.type = var1;
         this.codec = var2;
      }
   }
}
