package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S33PacketUpdateSign extends Packet {
   private int field_149352_a;
   private int field_149350_b;
   private int field_149351_c;
   private String[] field_149349_d;

   public S33PacketUpdateSign() {
      super();
   }

   public S33PacketUpdateSign(int var1, int var2, int var3, String[] var4) {
      super();
      this.field_149352_a = var1;
      this.field_149350_b = var2;
      this.field_149351_c = var3;
      this.field_149349_d = new String[]{var4[0], var4[1], var4[2], var4[3]};
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149352_a = var1.readInt();
      this.field_149350_b = var1.readShort();
      this.field_149351_c = var1.readInt();
      this.field_149349_d = new String[4];

      for(int var2 = 0; var2 < 4; ++var2) {
         this.field_149349_d[var2] = var1.func_150789_c(15);
      }
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.writeInt(this.field_149352_a);
      var1.writeShort(this.field_149350_b);
      var1.writeInt(this.field_149351_c);

      for(int var2 = 0; var2 < 4; ++var2) {
         var1.func_150785_a(this.field_149349_d[var2]);
      }
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147248_a(this);
   }

   public int func_149346_c() {
      return this.field_149352_a;
   }

   public int func_149345_d() {
      return this.field_149350_b;
   }

   public int func_149344_e() {
      return this.field_149351_c;
   }

   public String[] func_149347_f() {
      return this.field_149349_d;
   }
}
