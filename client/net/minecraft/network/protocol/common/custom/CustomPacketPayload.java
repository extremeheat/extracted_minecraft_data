package net.minecraft.network.protocol.common.custom;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.resources.Identifier;

public interface CustomPacketPayload {
   Type<? extends CustomPacketPayload> type();

   static <B extends ByteBuf, T extends CustomPacketPayload> StreamCodec<B, T> codec(final StreamMemberEncoder<B, T> writer, final StreamDecoder<B, T> reader) {
      return StreamCodec.<B, T>ofMember(writer, reader);
   }

   static <T extends CustomPacketPayload> Type<T> createType(final String id) {
      return new Type<T>(Identifier.withDefaultNamespace(id));
   }

   static <B extends FriendlyByteBuf> StreamCodec<B, CustomPacketPayload> codec(final FallbackProvider<B> fallback, final List<TypeAndCodec<? super B, ?>> types) {
      final Map<Identifier, StreamCodec<? super B, ? extends CustomPacketPayload>> idToType = (Map)types.stream().collect(Collectors.toUnmodifiableMap((t) -> t.type().id(), TypeAndCodec::codec));
      return new StreamCodec<B, CustomPacketPayload>() {
         private StreamCodec<? super B, ? extends CustomPacketPayload> findCodec(final Identifier typeId) {
            StreamCodec<? super B, ? extends CustomPacketPayload> codec = (StreamCodec)idToType.get(typeId);
            return codec != null ? codec : fallback.create(typeId);
         }

         private <T extends CustomPacketPayload> void writeCap(final B output, final Type<T> type, final CustomPacketPayload payload) {
            output.writeIdentifier(type.id());
            StreamCodec<B, T> codec = this.findCodec(type.id);
            codec.encode(output, payload);
         }

         public void encode(final B output, final CustomPacketPayload value) {
            this.writeCap(output, value.type(), value);
         }

         public CustomPacketPayload decode(final B input) {
            Identifier identifier = input.readIdentifier();
            return (CustomPacketPayload)this.findCodec(identifier).decode(input);
         }
      };
   }

   public static record Type<T extends CustomPacketPayload>(Identifier id) {
      public Type {
         super();
      }
   }

   public static record TypeAndCodec<B extends FriendlyByteBuf, T extends CustomPacketPayload>(Type<T> type, StreamCodec<B, T> codec) {
      public TypeAndCodec {
         super();
      }
   }

   public interface FallbackProvider<B extends FriendlyByteBuf> {
      StreamCodec<B, ? extends CustomPacketPayload> create(Identifier typeId);
   }
}
