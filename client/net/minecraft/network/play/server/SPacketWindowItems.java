package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.NonNullList;

public class SPacketWindowItems implements Packet<INetHandlerPlayClient> {
   private int field_148914_a;
   private List<ItemStack> field_148913_b;

   public SPacketWindowItems() {
      super();
   }

   public SPacketWindowItems(int var1, NonNullList<ItemStack> var2) {
      super();
      this.field_148914_a = var1;
      this.field_148913_b = NonNullList.func_191197_a(var2.size(), ItemStack.field_190927_a);

      for(int var3 = 0; var3 < this.field_148913_b.size(); ++var3) {
         this.field_148913_b.set(var3, ((ItemStack)var2.get(var3)).func_77946_l());
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148914_a = var1.readUnsignedByte();
      short var2 = var1.readShort();
      this.field_148913_b = NonNullList.func_191197_a(var2, ItemStack.field_190927_a);

      for(int var3 = 0; var3 < var2; ++var3) {
         this.field_148913_b.set(var3, var1.func_150791_c());
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_148914_a);
      var1.writeShort(this.field_148913_b.size());
      Iterator var2 = this.field_148913_b.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.func_150788_a(var3);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147241_a(this);
   }

   public int func_148911_c() {
      return this.field_148914_a;
   }

   public List<ItemStack> func_148910_d() {
      return this.field_148913_b;
   }
}
