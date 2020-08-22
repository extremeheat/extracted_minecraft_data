package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.Item;

public class ClientboundCooldownPacket implements Packet {
   private Item item;
   private int duration;

   public ClientboundCooldownPacket() {
   }

   public ClientboundCooldownPacket(Item var1, int var2) {
      this.item = var1;
      this.duration = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.item = Item.byId(var1.readVarInt());
      this.duration = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(Item.getId(this.item));
      var1.writeVarInt(this.duration);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleItemCooldown(this);
   }

   public Item getItem() {
      return this.item;
   }

   public int getDuration() {
      return this.duration;
   }
}
