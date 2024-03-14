package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundCustomChatCompletionsPacket(ClientboundCustomChatCompletionsPacket.Action b, List<String> c)
   implements Packet<ClientGamePacketListener> {
   private final ClientboundCustomChatCompletionsPacket.Action action;
   private final List<String> entries;
   public static final StreamCodec<FriendlyByteBuf, ClientboundCustomChatCompletionsPacket> STREAM_CODEC = Packet.codec(
      ClientboundCustomChatCompletionsPacket::write, ClientboundCustomChatCompletionsPacket::new
   );

   private ClientboundCustomChatCompletionsPacket(FriendlyByteBuf var1) {
      this(var1.readEnum(ClientboundCustomChatCompletionsPacket.Action.class), var1.readList(FriendlyByteBuf::readUtf));
   }

   public ClientboundCustomChatCompletionsPacket(ClientboundCustomChatCompletionsPacket.Action var1, List<String> var2) {
      super();
      this.action = var1;
      this.entries = var2;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.action);
      var1.writeCollection(this.entries, FriendlyByteBuf::writeUtf);
   }

   @Override
   public PacketType<ClientboundCustomChatCompletionsPacket> type() {
      return GamePacketTypes.CLIENTBOUND_CUSTOM_CHAT_COMPLETIONS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCustomChatCompletions(this);
   }

   public static enum Action {
      ADD,
      REMOVE,
      SET;

      private Action() {
      }
   }
}
