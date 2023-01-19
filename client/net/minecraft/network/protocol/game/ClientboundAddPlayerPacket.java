package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Player;

public class ClientboundAddPlayerPacket implements Packet<ClientGamePacketListener> {
   private final int entityId;
   private final UUID playerId;
   private final double x;
   private final double y;
   private final double z;
   private final byte yRot;
   private final byte xRot;

   public ClientboundAddPlayerPacket(Player var1) {
      super();
      this.entityId = var1.getId();
      this.playerId = var1.getGameProfile().getId();
      this.x = var1.getX();
      this.y = var1.getY();
      this.z = var1.getZ();
      this.yRot = (byte)((int)(var1.getYRot() * 256.0F / 360.0F));
      this.xRot = (byte)((int)(var1.getXRot() * 256.0F / 360.0F));
   }

   public ClientboundAddPlayerPacket(FriendlyByteBuf var1) {
      super();
      this.entityId = var1.readVarInt();
      this.playerId = var1.readUUID();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.yRot = var1.readByte();
      this.xRot = var1.readByte();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.entityId);
      var1.writeUUID(this.playerId);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeByte(this.yRot);
      var1.writeByte(this.xRot);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddPlayer(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public UUID getPlayerId() {
      return this.playerId;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public byte getyRot() {
      return this.yRot;
   }

   public byte getxRot() {
      return this.xRot;
   }
}
