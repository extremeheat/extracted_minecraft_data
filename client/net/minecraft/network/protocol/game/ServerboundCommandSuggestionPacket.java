package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.SkipPacketDecoderException;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundCommandSuggestionPacket(int id, String command) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<ByteBuf, ServerboundCommandSuggestionPacket> STREAM_CODEC = createStreamCodec(32500);
   public static final StreamCodec<ByteBuf, ServerboundCommandSuggestionPacket> CHAT_ONLY_STREAM_CODEC = new StreamCodec<ByteBuf, ServerboundCommandSuggestionPacket>() {
      private final StreamCodec<ByteBuf, ServerboundCommandSuggestionPacket> downstream = ServerboundCommandSuggestionPacket.createStreamCodec(256);

      public ServerboundCommandSuggestionPacket decode(final ByteBuf input) {
         try {
            return (ServerboundCommandSuggestionPacket)this.downstream.decode(input);
         } catch (DecoderException e) {
            throw new SkipPacketDecoderException(e);
         }
      }

      public void encode(final ByteBuf output, final ServerboundCommandSuggestionPacket packet) {
         ServerboundCommandSuggestionPacket.STREAM_CODEC.encode(output, packet);
      }
   };

   public ServerboundCommandSuggestionPacket {
      super();
   }

   private static StreamCodec<ByteBuf, ServerboundCommandSuggestionPacket> createStreamCodec(final int maxLength) {
      return StreamCodec.composite(ByteBufCodecs.VAR_INT, ServerboundCommandSuggestionPacket::id, ByteBufCodecs.stringUtf8(maxLength), ServerboundCommandSuggestionPacket::command, ServerboundCommandSuggestionPacket::new);
   }

   public PacketType<ServerboundCommandSuggestionPacket> type() {
      return GamePacketTypes.SERVERBOUND_COMMAND_SUGGESTION;
   }

   public void handle(final ServerGamePacketListener listener) {
      listener.handleCustomCommandSuggestions(this);
   }
}
