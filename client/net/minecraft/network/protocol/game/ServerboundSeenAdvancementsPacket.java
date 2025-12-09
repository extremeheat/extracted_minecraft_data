package net.minecraft.network.protocol.game;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class ServerboundSeenAdvancementsPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSeenAdvancementsPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundSeenAdvancementsPacket>codec(ServerboundSeenAdvancementsPacket::write, ServerboundSeenAdvancementsPacket::new);
   private final Action action;
   private final @Nullable Identifier tab;

   public ServerboundSeenAdvancementsPacket(Action var1, @Nullable Identifier var2) {
      super();
      this.action = var1;
      this.tab = var2;
   }

   public static ServerboundSeenAdvancementsPacket openedTab(AdvancementHolder var0) {
      return new ServerboundSeenAdvancementsPacket(ServerboundSeenAdvancementsPacket.Action.OPENED_TAB, var0.id());
   }

   public static ServerboundSeenAdvancementsPacket closedScreen() {
      return new ServerboundSeenAdvancementsPacket(ServerboundSeenAdvancementsPacket.Action.CLOSED_SCREEN, (Identifier)null);
   }

   private ServerboundSeenAdvancementsPacket(FriendlyByteBuf var1) {
      super();
      this.action = (Action)var1.readEnum(Action.class);
      if (this.action == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
         this.tab = var1.readIdentifier();
      } else {
         this.tab = null;
      }

   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.action);
      if (this.action == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
         var1.writeIdentifier(this.tab);
      }

   }

   public PacketType<ServerboundSeenAdvancementsPacket> type() {
      return GamePacketTypes.SERVERBOUND_SEEN_ADVANCEMENTS;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSeenAdvancements(this);
   }

   public Action getAction() {
      return this.action;
   }

   public @Nullable Identifier getTab() {
      return this.tab;
   }

   public static enum Action {
      OPENED_TAB,
      CLOSED_SCREEN;

      private Action() {
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{OPENED_TAB, CLOSED_SCREEN};
      }
   }
}
