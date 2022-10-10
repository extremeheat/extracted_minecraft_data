package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CPacketClickWindow implements Packet<INetHandlerPlayServer> {
   private int field_149554_a;
   private int field_149552_b;
   private int field_149553_c;
   private short field_149550_d;
   private ItemStack field_149551_e;
   private ClickType field_149549_f;

   public CPacketClickWindow() {
      super();
      this.field_149551_e = ItemStack.field_190927_a;
   }

   public CPacketClickWindow(int var1, int var2, int var3, ClickType var4, ItemStack var5, short var6) {
      super();
      this.field_149551_e = ItemStack.field_190927_a;
      this.field_149554_a = var1;
      this.field_149552_b = var2;
      this.field_149553_c = var3;
      this.field_149551_e = var5.func_77946_l();
      this.field_149550_d = var6;
      this.field_149549_f = var4;
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147351_a(this);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149554_a = var1.readByte();
      this.field_149552_b = var1.readShort();
      this.field_149553_c = var1.readByte();
      this.field_149550_d = var1.readShort();
      this.field_149549_f = (ClickType)var1.func_179257_a(ClickType.class);
      this.field_149551_e = var1.func_150791_c();
   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.writeByte(this.field_149554_a);
      var1.writeShort(this.field_149552_b);
      var1.writeByte(this.field_149553_c);
      var1.writeShort(this.field_149550_d);
      var1.func_179249_a(this.field_149549_f);
      var1.func_150788_a(this.field_149551_e);
   }

   public int func_149548_c() {
      return this.field_149554_a;
   }

   public int func_149544_d() {
      return this.field_149552_b;
   }

   public int func_149543_e() {
      return this.field_149553_c;
   }

   public short func_149547_f() {
      return this.field_149550_d;
   }

   public ItemStack func_149546_g() {
      return this.field_149551_e;
   }

   public ClickType func_186993_f() {
      return this.field_149549_f;
   }
}
