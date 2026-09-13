package net.minecraft.network.play.client;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C12PacketUpdateSign extends Packet {
   private int field_149593_a;
   private int field_149591_b;
   private int field_149592_c;
   private String[] field_149590_d;

   public C12PacketUpdateSign() {
      super();
   }

   public C12PacketUpdateSign(int var1, int var2, int var3, String[] var4) {
      super();
      this.field_149593_a = var1;
      this.field_149591_b = var2;
      this.field_149592_c = var3;
      this.field_149590_d = new String[]{var4[0], var4[1], var4[2], var4[3]};
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149593_a = var1.readInt();
      this.field_149591_b = var1.readShort();
      this.field_149592_c = var1.readInt();
      this.field_149590_d = new String[4];

      for(int var2 = 0; var2 < 4; ++var2) {
         this.field_149590_d[var2] = var1.func_150789_c(15);
      }
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149593_a);
      var1.writeShort(this.field_149591_b);
      var1.writeInt(this.field_149592_c);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.func_150785_a(this.field_149590_d[var2]);
      }
   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_147343_a(this);
   }

   public int func_149588_c() {
      return this.field_149593_a;
   }

   public int func_149586_d() {
      return this.field_149591_b;
   }

   public int func_149585_e() {
      return this.field_149592_c;
   }

   public String[] func_149589_f() {
      return this.field_149590_d;
   }
}
