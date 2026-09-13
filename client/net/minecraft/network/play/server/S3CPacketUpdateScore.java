package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.Score;

public class S3CPacketUpdateScore extends Packet {
   private String field_149329_a = "";
   private String field_149327_b = "";
   private int field_149328_c;
   private int field_149326_d;

   public S3CPacketUpdateScore() {
      super();
   }

   public S3CPacketUpdateScore(Score var1, int var2) {
      super();
      this.field_149329_a = var1.func_96653_e();
      this.field_149327_b = var1.func_96645_d().func_96679_b();
      this.field_149328_c = var1.func_96652_c();
      this.field_149326_d = var2;
   }

   public S3CPacketUpdateScore(String var1) {
      super();
      this.field_149329_a = var1;
      this.field_149327_b = "";
      this.field_149328_c = 0;
      this.field_149326_d = 1;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149329_a = var1.func_150789_c(16);
      this.field_149326_d = var1.readByte();
      if (this.field_149326_d != 1) {
         this.field_149327_b = var1.func_150789_c(16);
         this.field_149328_c = var1.readInt();
      }
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(this.field_149329_a);
      var1.writeByte(this.field_149326_d);
      if (this.field_149326_d != 1) {
         var1.func_150785_a(this.field_149327_b);
         var1.writeInt(this.field_149328_c);
      }
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147250_a(this);
   }

   public String func_149324_c() {
      return this.field_149329_a;
   }

   public String func_149321_d() {
      return this.field_149327_b;
   }

   public int func_149323_e() {
      return this.field_149328_c;
   }

   public int func_149322_f() {
      return this.field_149326_d;
   }
}
