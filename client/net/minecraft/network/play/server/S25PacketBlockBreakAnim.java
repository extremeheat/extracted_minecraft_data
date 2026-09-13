package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S25PacketBlockBreakAnim extends Packet {
   private int field_148852_a;
   private int field_148850_b;
   private int field_148851_c;
   private int field_148848_d;
   private int field_148849_e;

   public S25PacketBlockBreakAnim() {
      super();
   }

   public S25PacketBlockBreakAnim(int var1, int var2, int var3, int var4, int var5) {
      super();
      this.field_148852_a = var1;
      this.field_148850_b = var2;
      this.field_148851_c = var3;
      this.field_148848_d = var4;
      this.field_148849_e = var5;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_148852_a = var1.func_150792_a();
      this.field_148850_b = var1.readInt();
      this.field_148851_c = var1.readInt();
      this.field_148848_d = var1.readInt();
      this.field_148849_e = var1.readUnsignedByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150787_b(this.field_148852_a);
      var1.writeInt(this.field_148850_b);
      var1.writeInt(this.field_148851_c);
      var1.writeInt(this.field_148848_d);
      var1.writeByte(this.field_148849_e);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147294_a(this);
   }

   public int func_148845_c() {
      return this.field_148852_a;
   }

   public int func_148844_d() {
      return this.field_148850_b;
   }

   public int func_148843_e() {
      return this.field_148851_c;
   }

   public int func_148842_f() {
      return this.field_148848_d;
   }

   public int func_148846_g() {
      return this.field_148849_e;
   }
}
