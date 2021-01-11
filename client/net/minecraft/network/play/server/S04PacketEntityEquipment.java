package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S04PacketEntityEquipment implements Packet<INetHandlerPlayClient> {
   private int field_149394_a;
   private int field_149392_b;
   private ItemStack field_149393_c;

   public S04PacketEntityEquipment() {
      super();
   }

   public S04PacketEntityEquipment(int var1, int var2, ItemStack var3) {
      super();
      this.field_149394_a = var1;
      this.field_149392_b = var2;
      this.field_149393_c = var3 == null ? null : var3.func_77946_l();
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149394_a = var1.func_150792_a();
      this.field_149392_b = var1.readShort();
      this.field_149393_c = var1.func_150791_c();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_150787_b(this.field_149394_a);
      var1.writeShort(this.field_149392_b);
      var1.func_150788_a(this.field_149393_c);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147242_a(this);
   }

   public ItemStack func_149390_c() {
      return this.field_149393_c;
   }

   public int func_149389_d() {
      return this.field_149394_a;
   }

   public int func_149388_e() {
      return this.field_149392_b;
   }
}
