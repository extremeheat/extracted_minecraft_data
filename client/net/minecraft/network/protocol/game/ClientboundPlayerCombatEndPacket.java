package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatEndPacket implements Packet<ClientGamePacketListener> {
   private final int killerId;
   private final int duration;

   public ClientboundPlayerCombatEndPacket(CombatTracker var1) {
      this(var1.getKillerId(), var1.getCombatDuration());
   }

   public ClientboundPlayerCombatEndPacket(int var1, int var2) {
      super();
      this.killerId = var1;
      this.duration = var2;
   }

   public ClientboundPlayerCombatEndPacket(FriendlyByteBuf var1) {
      super();
      this.duration = var1.readVarInt();
      this.killerId = var1.readInt();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.duration);
      var1.writeInt(this.killerId);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerCombatEnd(this);
   }
}
