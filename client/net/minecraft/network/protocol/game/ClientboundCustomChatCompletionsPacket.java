package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.ByIdMap;

public record ClientboundCustomChatCompletionsPacket(Action action, List<String> entries) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundCustomChatCompletionsPacket> STREAM_CODEC;

   public ClientboundCustomChatCompletionsPacket {
      super();
   }

   public PacketType<ClientboundCustomChatCompletionsPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CUSTOM_CHAT_COMPLETIONS;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleCustomChatCompletions(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ClientboundCustomChatCompletionsPacket.Action.STREAM_CODEC, ClientboundCustomChatCompletionsPacket::action, ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), ClientboundCustomChatCompletionsPacket::entries, ClientboundCustomChatCompletionsPacket::new);
   }

   public static enum Action {
      ADD(0),
      REMOVE(1),
      SET(2);

      private static final IntFunction<Action> BY_ID = ByIdMap.<Action>continuous((a) -> a.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, Action> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (a) -> a.id);
      private final int id;

      private Action(final int id) {
         this.id = id;
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{ADD, REMOVE, SET};
      }
   }
}
