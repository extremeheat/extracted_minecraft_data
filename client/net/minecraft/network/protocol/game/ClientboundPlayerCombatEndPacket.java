package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatEndPacket implements Packet<ClientGamePacketListener> {
   private final int duration;

   public ClientboundPlayerCombatEndPacket(CombatTracker var1) {
      this(var1.getCombatDuration());
   }

   public ClientboundPlayerCombatEndPacket(int var1) {
      super();
      this.duration = var1;
   }

   public ClientboundPlayerCombatEndPacket(FriendlyByteBuf var1) {
      super();
      this.duration = var1.readVarInt();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.duration);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerCombatEnd(this);
   }
}
