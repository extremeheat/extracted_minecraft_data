package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2DPacketOpenWindow extends Packet {
   private int field_148909_a;
   private int field_148907_b;
   private String field_148908_c;
   private int field_148905_d;
   private boolean field_148906_e;
   private int field_148904_f;

   public S2DPacketOpenWindow() {
      super();
   }

   public S2DPacketOpenWindow(int var1, int var2, String var3, int var4, boolean var5) {
      super();
      this.field_148909_a = var1;
      this.field_148907_b = var2;
      this.field_148908_c = var3;
      this.field_148905_d = var4;
      this.field_148906_e = var5;
   }

   public S2DPacketOpenWindow(int var1, int var2, String var3, int var4, boolean var5, int var6) {
      this(var1, var2, var3, var4, var5);
      this.field_148904_f = var6;
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147265_a(this);
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_148909_a = var1.readUnsignedByte();
      this.field_148907_b = var1.readUnsignedByte();
      this.field_148908_c = var1.func_150789_c(32);
      this.field_148905_d = var1.readUnsignedByte();
      this.field_148906_e = var1.readBoolean();
      if (this.field_148907_b == 11) {
         this.field_148904_f = var1.readInt();
      }
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeByte(this.field_148909_a);
      var1.writeByte(this.field_148907_b);
      var1.func_150785_a(this.field_148908_c);
      var1.writeByte(this.field_148905_d);
      var1.writeBoolean(this.field_148906_e);
      if (this.field_148907_b == 11) {
         var1.writeInt(this.field_148904_f);
      }
   }

   public int func_148901_c() {
      return this.field_148909_a;
   }

   public int func_148899_d() {
      return this.field_148907_b;
   }

   public String func_148902_e() {
      return this.field_148908_c;
   }

   public int func_148898_f() {
      return this.field_148905_d;
   }

   public boolean func_148900_g() {
      return this.field_148906_e;
   }

   public int func_148897_h() {
      return this.field_148904_f;
   }
}
