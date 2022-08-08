package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundCustomChatCompletionsPacket(Action a, List<String> b) implements Packet<ClientGamePacketListener> {
   private final Action action;
   private final List<String> entries;

   public ClientboundCustomChatCompletionsPacket(FriendlyByteBuf var1) {
      this((Action)var1.readEnum(Action.class), var1.readList(FriendlyByteBuf::readUtf));
   }

   public ClientboundCustomChatCompletionsPacket(Action var1, List<String> var2) {
      super();
      this.action = var1;
      this.entries = var2;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.action);
      var1.writeCollection(this.entries, FriendlyByteBuf::writeUtf);
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
