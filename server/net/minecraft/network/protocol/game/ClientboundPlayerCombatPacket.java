package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.LivingEntity;

public class ClientboundPlayerCombatPacket implements Packet<ClientGamePacketListener> {
   public ClientboundPlayerCombatPacket.Event event;
   public int playerId;
   public int killerId;
   public int duration;
   public Component message;

   public ClientboundPlayerCombatPacket() {
      super();
   }

   public ClientboundPlayerCombatPacket(CombatTracker var1, ClientboundPlayerCombatPacket.Event var2) {
      this(var1, var2, TextComponent.EMPTY);
   }

   public ClientboundPlayerCombatPacket(CombatTracker var1, ClientboundPlayerCombatPacket.Event var2, Component var3) {
      super();
      this.event = var2;
      LivingEntity var4 = var1.getKiller();
      switch(var2) {
      case END_COMBAT:
         this.duration = var1.getCombatDuration();
         this.killerId = var4 == null ? -1 : var4.getId();
         break;
      case ENTITY_DIED:
         this.playerId = var1.getMob().getId();
         this.killerId = var4 == null ? -1 : var4.getId();
         this.message = var3;
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.event = (ClientboundPlayerCombatPacket.Event)var1.readEnum(ClientboundPlayerCombatPacket.Event.class);
      if (this.event == ClientboundPlayerCombatPacket.Event.END_COMBAT) {
         this.duration = var1.readVarInt();
         this.killerId = var1.readInt();
      } else if (this.event == ClientboundPlayerCombatPacket.Event.ENTITY_DIED) {
         this.playerId = var1.readVarInt();
         this.killerId = var1.readInt();
         this.message = var1.readComponent();
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.event);
      if (this.event == ClientboundPlayerCombatPacket.Event.END_COMBAT) {
         var1.writeVarInt(this.duration);
         var1.writeInt(this.killerId);
      } else if (this.event == ClientboundPlayerCombatPacket.Event.ENTITY_DIED) {
         var1.writeVarInt(this.playerId);
         var1.writeInt(this.killerId);
         var1.writeComponent(this.message);
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerCombat(this);
   }

   public boolean isSkippable() {
      return this.event == ClientboundPlayerCombatPacket.Event.ENTITY_DIED;
   }

   public static enum Event {
      ENTER_COMBAT,
      END_COMBAT,
      ENTITY_DIED;

      private Event() {
      }
   }
}
