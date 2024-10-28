package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundLevelEventPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundLevelEventPacket> STREAM_CODEC = Packet.codec(ClientboundLevelEventPacket::write, ClientboundLevelEventPacket::new);
   private final int type;
   private final BlockPos pos;
   private final int data;
   private final boolean globalEvent;

   public ClientboundLevelEventPacket(int var1, BlockPos var2, int var3, boolean var4) {
      super();
      this.type = var1;
      this.pos = var2.immutable();
      this.data = var3;
      this.globalEvent = var4;
   }

   private ClientboundLevelEventPacket(FriendlyByteBuf var1) {
      super();
      this.type = var1.readInt();
      this.pos = var1.readBlockPos();
      this.data = var1.readInt();
      this.globalEvent = var1.readBoolean();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeInt(this.type);
      var1.writeBlockPos(this.pos);
      var1.writeInt(this.data);
      var1.writeBoolean(this.globalEvent);
   }

   public PacketType<ClientboundLevelEventPacket> type() {
      return GamePacketTypes.CLIENTBOUND_LEVEL_EVENT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLevelEvent(this);
   }

   public boolean isGlobalEvent() {
      return this.globalEvent;
   }

   public int getType() {
      return this.type;
   }

   public int getData() {
      return this.data;
   }

   public BlockPos getPos() {
      return this.pos;
   }
}
