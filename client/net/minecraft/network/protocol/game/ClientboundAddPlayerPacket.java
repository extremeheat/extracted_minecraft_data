package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;

public class ClientboundAddPlayerPacket implements Packet<ClientGamePacketListener> {
   private int entityId;
   private UUID playerId;
   private double x;
   private double y;
   private double z;
   private byte yRot;
   private byte xRot;
   private SynchedEntityData entityData;
   private List<SynchedEntityData.DataItem<?>> unpack;

   public ClientboundAddPlayerPacket() {
      super();
   }

   public ClientboundAddPlayerPacket(Player var1) {
      super();
      this.entityId = var1.getId();
      this.playerId = var1.getGameProfile().getId();
      this.x = var1.x;
      this.y = var1.y;
      this.z = var1.z;
      this.yRot = (byte)((int)(var1.yRot * 256.0F / 360.0F));
      this.xRot = (byte)((int)(var1.xRot * 256.0F / 360.0F));
      this.entityData = var1.getEntityData();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.entityId = var1.readVarInt();
      this.playerId = var1.readUUID();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.yRot = var1.readByte();
      this.xRot = var1.readByte();
      this.unpack = SynchedEntityData.unpack(var1);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.entityId);
      var1.writeUUID(this.playerId);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeByte(this.yRot);
      var1.writeByte(this.xRot);
      this.entityData.packAll(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddPlayer(this);
   }

   @Nullable
   public List<SynchedEntityData.DataItem<?>> getUnpackedData() {
      return this.unpack;
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
