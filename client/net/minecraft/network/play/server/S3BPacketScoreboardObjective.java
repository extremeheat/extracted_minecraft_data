package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;

public class S3BPacketScoreboardObjective implements Packet<INetHandlerPlayClient> {
   private String field_149343_a;
   private String field_149341_b;
   private IScoreObjectiveCriteria.EnumRenderType field_179818_c;
   private int field_149342_c;

   public S3BPacketScoreboardObjective() {
      super();
   }

   public S3BPacketScoreboardObjective(ScoreObjective var1, int var2) {
      super();
      this.field_149343_a = var1.func_96679_b();
      this.field_149341_b = var1.func_96678_d();
      this.field_179818_c = var1.func_96680_c().func_178790_c();
      this.field_149342_c = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149343_a = var1.func_150789_c(16);
      this.field_149342_c = var1.readByte();
      if (this.field_149342_c == 0 || this.field_149342_c == 2) {
         this.field_149341_b = var1.func_150789_c(32);
         this.field_179818_c = IScoreObjectiveCriteria.EnumRenderType.func_178795_a(var1.func_150789_c(16));
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(this.field_149343_a);
      var1.writeByte(this.field_149342_c);
      if (this.field_149342_c == 0 || this.field_149342_c == 2) {
         var1.func_180714_a(this.field_149341_b);
         var1.func_180714_a(this.field_179818_c.func_178796_a());
      }

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

   public IScoreObjectiveCriteria.EnumRenderType func_179817_d() {
      return this.field_179818_c;
   }
}
