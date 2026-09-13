package net.minecraft.network.play.server;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

public class S3BPacketScoreboardObjective extends Packet {
   private String field_149343_a;
   private String field_149341_b;
   private int field_149342_c;

   public S3BPacketScoreboardObjective() {
      super();
   }

   public S3BPacketScoreboardObjective(ScoreObjective var1, int var2) {
      super();
      this.field_149343_a = var1.func_96679_b();
      this.field_149341_b = var1.func_96678_d();
      this.field_149342_c = var2;
   }

   @Override
   public void func_148837_a(PacketBuffer var1) {
      this.field_149343_a = var1.func_150789_c(16);
      this.field_149341_b = var1.func_150789_c(32);
      this.field_149342_c = var1.readByte();
   }

   @Override
   public void func_148840_b(PacketBuffer var1) {
      var1.func_150785_a(this.field_149343_a);
      var1.func_150785_a(this.field_149341_b);
      var1.writeByte(this.field_149342_c);
   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147291_a(this);
   }

   public String func_149339_c() {
      return this.field_149343_a;
   }

   public String func_149337_d() {
      return this.field_149341_b;
   }

   public int func_149338_e() {
      return this.field_149342_c;
   }
}
