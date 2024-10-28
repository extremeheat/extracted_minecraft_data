package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.border.WorldBorder;

public class ClientboundInitializeBorderPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundInitializeBorderPacket> STREAM_CODEC = Packet.codec(ClientboundInitializeBorderPacket::write, ClientboundInitializeBorderPacket::new);
   private final double newCenterX;
   private final double newCenterZ;
   private final double oldSize;
   private final double newSize;
   private final long lerpTime;
   private final int newAbsoluteMaxSize;
   private final int warningBlocks;
   private final int warningTime;

   private ClientboundInitializeBorderPacket(FriendlyByteBuf var1) {
      super();
      this.newCenterX = var1.readDouble();
      this.newCenterZ = var1.readDouble();
      this.oldSize = var1.readDouble();
      this.newSize = var1.readDouble();
      this.lerpTime = var1.readVarLong();
      this.newAbsoluteMaxSize = var1.readVarInt();
      this.warningBlocks = var1.readVarInt();
      this.warningTime = var1.readVarInt();
   }

   public ClientboundInitializeBorderPacket(WorldBorder var1) {
      super();
      this.newCenterX = var1.getCenterX();
      this.newCenterZ = var1.getCenterZ();
      this.oldSize = var1.getSize();
      this.newSize = var1.getLerpTarget();
      this.lerpTime = var1.getLerpRemainingTime();
      this.newAbsoluteMaxSize = var1.getAbsoluteMaxSize();
      this.warningBlocks = var1.getWarningBlocks();
      this.warningTime = var1.getWarningTime();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.newCenterX);
      var1.writeDouble(this.newCenterZ);
      var1.writeDouble(this.oldSize);
      var1.writeDouble(this.newSize);
      var1.writeVarLong(this.lerpTime);
      var1.writeVarInt(this.newAbsoluteMaxSize);
      var1.writeVarInt(this.warningBlocks);
      var1.writeVarInt(this.warningTime);
   }

   public PacketType<ClientboundInitializeBorderPacket> type() {
      return GamePacketTypes.CLIENTBOUND_INITIALIZE_BORDER;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleInitializeBorder(this);
   }

   public double getNewCenterX() {
      return this.newCenterX;
   }

   public double getNewCenterZ() {
      return this.newCenterZ;
   }

   public double getNewSize() {
      return this.newSize;
   }

   public double getOldSize() {
      return this.oldSize;
   }

   public long getLerpTime() {
      return this.lerpTime;
   }

   public int getNewAbsoluteMaxSize() {
      return this.newAbsoluteMaxSize;
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }
}
