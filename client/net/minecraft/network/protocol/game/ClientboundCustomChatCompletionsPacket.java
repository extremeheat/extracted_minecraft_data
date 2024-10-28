package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundCustomChatCompletionsPacket(Action action, List<String> entries) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundCustomChatCompletionsPacket> STREAM_CODEC = Packet.codec(ClientboundCustomChatCompletionsPacket::write, ClientboundCustomChatCompletionsPacket::new);

   private ClientboundCustomChatCompletionsPacket(FriendlyByteBuf var1) {
      this((Action)var1.readEnum(Action.class), var1.readList(FriendlyByteBuf::readUtf));
   }

   public ClientboundCustomChatCompletionsPacket(Action action, List<String> entries) {
      super();
      this.action = action;
      this.entries = entries;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.action);
      var1.writeCollection(this.entries, FriendlyByteBuf::writeUtf);
   }

   public PacketType<ClientboundCustomChatCompletionsPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CUSTOM_CHAT_COMPLETIONS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCustomChatCompletions(this);
   }

   public Action action() {
      return this.action;
   }

   public List<String> entries() {
      return this.entries;
   }

   public static enum Action {
      ADD,
      REMOVE,
      SET;

      private Action() {
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{ADD, REMOVE, SET};
      }
   }
}
