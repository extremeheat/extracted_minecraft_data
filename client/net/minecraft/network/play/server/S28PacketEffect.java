package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S28PacketEffect extends Packet {
   private int field_149251_a;
   private int field_149249_b;
   private int field_149250_c;
   private int field_149247_d;
   private int field_149248_e;
   private boolean field_149246_f;

   public S28PacketEffect() {
      super();
   }

   public S28PacketEffect(int var1, int var2, int var3, int var4, int var5, boolean var6) {
      super();
      this.field_149251_a = var1;
      this.field_149250_c = var2;
      this.field_149247_d = var3;
      this.field_149248_e = var4;
      this.field_149249_b = var5;
      this.field_149246_f = var6;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149251_a = var1.readInt();
      this.field_149250_c = var1.readInt();
      this.field_149247_d = var1.readByte() & 255;
      this.field_149248_e = var1.readInt();
      this.field_149249_b = var1.readInt();
      this.field_149246_f = var1.readBoolean();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149251_a);
      var1.writeInt(this.field_149250_c);
      var1.writeByte(this.field_149247_d & 0xFF);
      var1.writeInt(this.field_149248_e);
      var1.writeInt(this.field_149249_b);
      var1.writeBoolean(this.field_149246_f);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147277_a(this);
   }

   public boolean func_149244_c() {
      return this.field_149246_f;
   }

   public int func_149242_d() {
      return this.field_149251_a;
   }

   public int func_149241_e() {
      return this.field_149249_b;
   }

   public int func_149240_f() {
      return this.field_149250_c;
   }

   public int func_149243_g() {
      return this.field_149247_d;
   }

   public int func_149239_h() {
      return this.field_149248_e;
   }
}
