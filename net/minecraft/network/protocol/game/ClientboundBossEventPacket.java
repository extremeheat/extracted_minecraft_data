package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.BossEvent;

public class ClientboundBossEventPacket implements Packet {
   private UUID id;
   private ClientboundBossEventPacket.Operation operation;
   private Component name;
   private float pct;
   private BossEvent.BossBarColor color;
   private BossEvent.BossBarOverlay overlay;
   private boolean darkenScreen;
   private boolean playMusic;
   private boolean createWorldFog;

   public ClientboundBossEventPacket() {
   }

   public ClientboundBossEventPacket(ClientboundBossEventPacket.Operation var1, BossEvent var2) {
      this.operation = var1;
      this.id = var2.getId();
      this.name = var2.getName();
      this.pct = var2.getPercent();
      this.color = var2.getColor();
      this.overlay = var2.getOverlay();
      this.darkenScreen = var2.shouldDarkenScreen();
      this.playMusic = var2.shouldPlayBossMusic();
      this.createWorldFog = var2.shouldCreateWorldFog();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.id = var1.readUUID();
      this.operation = (ClientboundBossEventPacket.Operation)var1.readEnum(ClientboundBossEventPacket.Operation.class);
      switch(this.operation) {
      case ADD:
         this.name = var1.readComponent();
         this.pct = var1.readFloat();
         this.color = (BossEvent.BossBarColor)var1.readEnum(BossEvent.BossBarColor.class);
         this.overlay = (BossEvent.BossBarOverlay)var1.readEnum(BossEvent.BossBarOverlay.class);
         this.decodeProperties(var1.readUnsignedByte());
      case REMOVE:
      default:
         break;
      case UPDATE_PCT:
         this.pct = var1.readFloat();
         break;
      case UPDATE_NAME:
         this.name = var1.readComponent();
         break;
      case UPDATE_STYLE:
         this.color = (BossEvent.BossBarColor)var1.readEnum(BossEvent.BossBarColor.class);
         this.overlay = (BossEvent.BossBarOverlay)var1.readEnum(BossEvent.BossBarOverlay.class);
         break;
      case UPDATE_PROPERTIES:
         this.decodeProperties(var1.readUnsignedByte());
      }

   }

   private void decodeProperties(int var1) {
      this.darkenScreen = (var1 & 1) > 0;
      this.playMusic = (var1 & 2) > 0;
      this.createWorldFog = (var1 & 4) > 0;
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeUUID(this.id);
      var1.writeEnum(this.operation);
      switch(this.operation) {
      case ADD:
         var1.writeComponent(this.name);
         var1.writeFloat(this.pct);
         var1.writeEnum(this.color);
         var1.writeEnum(this.overlay);
         var1.writeByte(this.encodeProperties());
      case REMOVE:
      default:
         break;
      case UPDATE_PCT:
         var1.writeFloat(this.pct);
         break;
      case UPDATE_NAME:
         var1.writeComponent(this.name);
         break;
      case UPDATE_STYLE:
         var1.writeEnum(this.color);
         var1.writeEnum(this.overlay);
         break;
      case UPDATE_PROPERTIES:
         var1.writeByte(this.encodeProperties());
      }

   }

   private int encodeProperties() {
      int var1 = 0;
      if (this.darkenScreen) {
         var1 |= 1;
      }

      if (this.playMusic) {
         var1 |= 2;
      }

      if (this.createWorldFog) {
         var1 |= 4;
      }

      return var1;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBossUpdate(this);
   }

   public UUID getId() {
      return this.id;
   }

   public ClientboundBossEventPacket.Operation getOperation() {
      return this.operation;
   }

   public Component getName() {
      return this.name;
   }

   public float getPercent() {
      return this.pct;
   }

   public BossEvent.BossBarColor getColor() {
      return this.color;
   }

   public BossEvent.BossBarOverlay getOverlay() {
      return this.overlay;
   }

   public boolean shouldDarkenScreen() {
      return this.darkenScreen;
   }

   public boolean shouldPlayMusic() {
      return this.playMusic;
   }

   public boolean shouldCreateWorldFog() {
      return this.createWorldFog;
   }

   public static enum Operation {
      ADD,
      REMOVE,
      UPDATE_PCT,
      UPDATE_NAME,
      UPDATE_STYLE,
      UPDATE_PROPERTIES;
   }
}
