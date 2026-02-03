package net.minecraft.network.protocol.game;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import org.jspecify.annotations.Nullable;

public class ClientboundTagQueryPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundTagQueryPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundTagQueryPacket>codec(ClientboundTagQueryPacket::write, ClientboundTagQueryPacket::new);
   private final int transactionId;
   private final @Nullable CompoundTag tag;

   public ClientboundTagQueryPacket(final int transactionId, final @Nullable CompoundTag tag) {
      super();
      this.transactionId = transactionId;
      this.tag = tag;
   }

   private ClientboundTagQueryPacket(final FriendlyByteBuf input) {
      super();
      this.transactionId = input.readVarInt();
      this.tag = input.readNbt();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.transactionId);
      output.writeNbt(this.tag);
   }

   public PacketType<ClientboundTagQueryPacket> type() {
      return GamePacketTypes.CLIENTBOUND_TAG_QUERY;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleTagQueryPacket(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public @Nullable CompoundTag getTag() {
      return this.tag;
   }

   public boolean isSkippable() {
      return true;
   }
}
