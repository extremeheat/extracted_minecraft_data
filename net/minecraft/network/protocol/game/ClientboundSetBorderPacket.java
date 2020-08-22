package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundSetBorderPacket implements Packet {
   private ClientboundSetBorderPacket.Type type;
   private int newAbsoluteMaxSize;
   private double newCenterX;
   private double newCenterZ;
   private double newSize;
   private double oldSize;
   private long lerpTime;
   private int warningTime;
   private int warningBlocks;

   public ClientboundSetBorderPacket() {
   }

   public ClientboundSetBorderPacket(WorldBorder var1, ClientboundSetBorderPacket.Type var2) {
      this.type = var2;
      this.newCenterX = var1.getCenterX();
      this.newCenterZ = var1.getCenterZ();
      this.oldSize = var1.getSize();
      this.newSize = var1.getLerpTarget();
      this.lerpTime = var1.getLerpRemainingTime();
      this.newAbsoluteMaxSize = var1.getAbsoluteMaxSize();
      this.warningBlocks = var1.getWarningBlocks();
      this.warningTime = var1.getWarningTime();
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.type = (ClientboundSetBorderPacket.Type)var1.readEnum(ClientboundSetBorderPacket.Type.class);
      switch(this.type) {
      case SET_SIZE:
         this.newSize = var1.readDouble();
         break;
      case LERP_SIZE:
         this.oldSize = var1.readDouble();
         this.newSize = var1.readDouble();
         this.lerpTime = var1.readVarLong();
         break;
      case SET_CENTER:
         this.newCenterX = var1.readDouble();
         this.newCenterZ = var1.readDouble();
         break;
      case SET_WARNING_BLOCKS:
         this.warningBlocks = var1.readVarInt();
         break;
      case SET_WARNING_TIME:
         this.warningTime = var1.readVarInt();
         break;
      case INITIALIZE:
         this.newCenterX = var1.readDouble();
         this.newCenterZ = var1.readDouble();
         this.oldSize = var1.readDouble();
         this.newSize = var1.readDouble();
         this.lerpTime = var1.readVarLong();
         this.newAbsoluteMaxSize = var1.readVarInt();
         this.warningBlocks = var1.readVarInt();
         this.warningTime = var1.readVarInt();
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.type);
      switch(this.type) {
      case SET_SIZE:
         var1.writeDouble(this.newSize);
         break;
      case LERP_SIZE:
         var1.writeDouble(this.oldSize);
         var1.writeDouble(this.newSize);
         var1.writeVarLong(this.lerpTime);
         break;
      case SET_CENTER:
         var1.writeDouble(this.newCenterX);
         var1.writeDouble(this.newCenterZ);
         break;
      case SET_WARNING_BLOCKS:
         var1.writeVarInt(this.warningBlocks);
         break;
      case SET_WARNING_TIME:
         var1.writeVarInt(this.warningTime);
         break;
      case INITIALIZE:
         var1.writeDouble(this.newCenterX);
         var1.writeDouble(this.newCenterZ);
         var1.writeDouble(this.oldSize);
         var1.writeDouble(this.newSize);
         var1.writeVarLong(this.lerpTime);
         var1.writeVarInt(this.newAbsoluteMaxSize);
         var1.writeVarInt(this.warningBlocks);
         var1.writeVarInt(this.warningTime);
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSetBorder(this);
   }

   public void applyChanges(WorldBorder var1) {
      switch(this.type) {
      case SET_SIZE:
         var1.setSize(this.newSize);
         break;
      case LERP_SIZE:
         var1.lerpSizeBetween(this.oldSize, this.newSize, this.lerpTime);
         break;
      case SET_CENTER:
         var1.setCenter(this.newCenterX, this.newCenterZ);
         break;
      case SET_WARNING_BLOCKS:
         var1.setWarningBlocks(this.warningBlocks);
         break;
      case SET_WARNING_TIME:
         var1.setWarningTime(this.warningTime);
         break;
      case INITIALIZE:
         var1.setCenter(this.newCenterX, this.newCenterZ);
         if (this.lerpTime > 0L) {
            var1.lerpSizeBetween(this.oldSize, this.newSize, this.lerpTime);
         } else {
            var1.setSize(this.newSize);
         }

         var1.setAbsoluteMaxSize(this.newAbsoluteMaxSize);
         var1.setWarningBlocks(this.warningBlocks);
         var1.setWarningTime(this.warningTime);
      }

   }

   public static enum Type {
      SET_SIZE,
      LERP_SIZE,
      SET_CENTER,
      INITIALIZE,
      SET_WARNING_TIME,
      SET_WARNING_BLOCKS;
   }
}
