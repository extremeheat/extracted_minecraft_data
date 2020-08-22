package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundTagQueryPacket implements Packet {
   private int transactionId;
   @Nullable
   private CompoundTag tag;

   public ClientboundTagQueryPacket() {
   }

   public ClientboundTagQueryPacket(int var1, @Nullable CompoundTag var2) {
      this.transactionId = var1;
      this.tag = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.transactionId = var1.readVarInt();
      this.tag = var1.readNbt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.transactionId);
      var1.writeNbt(this.tag);
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
