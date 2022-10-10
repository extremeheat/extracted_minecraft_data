package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.text.ITextComponent;

public class SPacketScoreboardObjective implements Packet<INetHandlerPlayClient> {
   private String field_149343_a;
   private ITextComponent field_149341_b;
   private ScoreCriteria.RenderType field_199857_c;
   private int field_149342_c;

   public SPacketScoreboardObjective() {
      super();
   }

   public SPacketScoreboardObjective(ScoreObjective var1, int var2) {
      super();
      this.field_149343_a = var1.func_96679_b();
      this.field_149341_b = var1.func_96678_d();
      this.field_199857_c = var1.func_199865_f();
      this.field_149342_c = var2;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149343_a = var1.func_150789_c(16);
      this.field_149342_c = var1.readByte();
      if (this.field_149342_c == 0 || this.field_149342_c == 2) {
         this.field_149341_b = var1.func_179258_d();
         this.field_199857_c = (ScoreCriteria.RenderType)var1.func_179257_a(ScoreCriteria.RenderType.class);
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(this.field_149343_a);
      var1.writeByte(this.field_149342_c);
      if (this.field_149342_c == 0 || this.field_149342_c == 2) {
         var1.func_179256_a(this.field_149341_b);
         var1.func_179249_a(this.field_199857_c);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147291_a(this);
   }

   public String func_149339_c() {
      return this.field_149343_a;
   }

   public ITextComponent func_149337_d() {
      return this.field_149341_b;
   }

   public int func_149338_e() {
      return this.field_149342_c;
   }

   public ScoreCriteria.RenderType func_199856_d() {
      return this.field_199857_c;
   }
}
