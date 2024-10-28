package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundTagQueryPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundTagQueryPacket> STREAM_CODEC = Packet.codec(ClientboundTagQueryPacket::write, ClientboundTagQueryPacket::new);
   private final int transactionId;
   @Nullable
   private final CompoundTag tag;

   public ClientboundTagQueryPacket(int var1, @Nullable CompoundTag var2) {
      super();
      this.transactionId = var1;
      this.tag = var2;
   }

   private ClientboundTagQueryPacket(FriendlyByteBuf var1) {
      super();
      this.transactionId = var1.readVarInt();
      this.tag = var1.readNbt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.transactionId);
      var1.writeNbt(this.tag);
   }

   public PacketType<ClientboundTagQueryPacket> type() {
      return GamePacketTypes.CLIENTBOUND_TAG_QUERY;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTagQueryPacket(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   @Nullable
   public CompoundTag getTag() {
      return this.tag;
   }

   public boolean isSkippable() {
      return true;
   }
}
