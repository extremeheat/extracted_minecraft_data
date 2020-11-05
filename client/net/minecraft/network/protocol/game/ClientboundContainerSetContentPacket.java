package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.item.ItemStack;

public class ClientboundContainerSetContentPacket implements Packet<ClientGamePacketListener> {
   private int containerId;
   private List<ItemStack> items;

   public ClientboundContainerSetContentPacket() {
      super();
   }

   public ClientboundContainerSetContentPacket(int var1, NonNullList<ItemStack> var2) {
      super();
      this.containerId = var1;
      this.items = NonNullList.withSize(var2.size(), ItemStack.EMPTY);

      for(int var3 = 0; var3 < this.items.size(); ++var3) {
         this.items.set(var3, ((ItemStack)var2.get(var3)).copy());
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readUnsignedByte();
      short var2 = var1.readShort();
      this.items = NonNullList.withSize(var2, ItemStack.EMPTY);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.items.set(var3, var1.readItem());
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.containerId);
      var1.writeShort(this.items.size());
      Iterator var2 = this.items.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.writeItem(var3);
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleContainerContent(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public List<ItemStack> getItems() {
      return this.items;
   }
}
