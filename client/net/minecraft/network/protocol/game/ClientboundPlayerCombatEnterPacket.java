package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerCombatEnterPacket implements Packet<ClientGamePacketListener> {
   public ClientboundPlayerCombatEnterPacket() {
      super();
   }

   public ClientboundPlayerCombatEnterPacket(FriendlyByteBuf var1) {
      super();
   }

   public void write(FriendlyByteBuf var1) {
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerCombatEnter(this);
   }
}
