package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.CombatTracker;

public class ClientboundPlayerCombatKillPacket implements Packet<ClientGamePacketListener> {
   private final int playerId;
   private final int killerId;
   private final Component message;

   public ClientboundPlayerCombatKillPacket(CombatTracker var1, Component var2) {
      this(var1.getMob().getId(), var1.getKillerId(), var2);
   }

   public ClientboundPlayerCombatKillPacket(int var1, int var2, Component var3) {
      super();
      this.playerId = var1;
      this.killerId = var2;
      this.message = var3;
   }

   public ClientboundPlayerCombatKillPacket(FriendlyByteBuf var1) {
      super();
      this.playerId = var1.readVarInt();
      this.killerId = var1.readInt();
      this.message = var1.readComponent();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.playerId);
      var1.writeInt(this.killerId);
      var1.writeComponent(this.message);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerCombatKill(this);
   }

   @Override
   public boolean isSkippable() {
      return true;
   }

   public int getKillerId() {
      return this.killerId;
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public Component getMessage() {
      return this.message;
   }
}
