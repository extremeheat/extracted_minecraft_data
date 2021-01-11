package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S30PacketWindowItems implements Packet<INetHandlerPlayClient> {
   private int field_148914_a;
   private ItemStack[] field_148913_b;

   public S30PacketWindowItems() {
      super();
   }

   public S30PacketWindowItems(int var1, List<ItemStack> var2) {
      super();
      this.field_148914_a = var1;
      this.field_148913_b = new ItemStack[var2.size()];

      for(int var3 = 0; var3 < this.field_148913_b.length; ++var3) {
         ItemStack var4 = (ItemStack)var2.get(var3);
         this.field_148913_b[var3] = var4 == null ? null : var4.func_77946_l();
      }

   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_148914_a = var1.readUnsignedByte();
      short var2 = var1.readShort();
      this.field_148913_b = new ItemStack[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.field_148913_b[var3] = var1.func_150791_c();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_148914_a);
      var1.writeShort(this.field_148913_b.length);
      ItemStack[] var2 = this.field_148913_b;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ItemStack var5 = var2[var4];
         var1.func_150788_a(var5);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147241_a(this);
   }

   public int func_148911_c() {
      return this.field_148914_a;
   }

   public ItemStack[] func_148910_d() {
      return this.field_148913_b;
   }
}
