package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ServerboundSeenAdvancementsPacket implements Packet<ServerGamePacketListener> {
   private ServerboundSeenAdvancementsPacket.Action action;
   private ResourceLocation tab;

   public ServerboundSeenAdvancementsPacket() {
      super();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.action = (ServerboundSeenAdvancementsPacket.Action)var1.readEnum(ServerboundSeenAdvancementsPacket.Action.class);
      if (this.action == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
         this.tab = var1.readResourceLocation();
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.action);
      if (this.action == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
         var1.writeResourceLocation(this.tab);
      }

   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSeenAdvancements(this);
   }

   public ServerboundSeenAdvancementsPacket.Action getAction() {
      return this.action;
   }

   public ResourceLocation getTab() {
      return this.tab;
   }

   public static enum Action {
      OPENED_TAB,
      CLOSED_SCREEN;

      private Action() {
      }
   }
}
