package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;

public class S3CPacketUpdateScore implements Packet<INetHandlerPlayClient> {
   private String field_149329_a = "";
   private String field_149327_b = "";
   private int field_149328_c;
   private S3CPacketUpdateScore.Action field_149326_d;

   public S3CPacketUpdateScore() {
      super();
   }

   public S3CPacketUpdateScore(Score var1) {
      super();
      this.field_149329_a = var1.func_96653_e();
      this.field_149327_b = var1.func_96645_d().func_96679_b();
      this.field_149328_c = var1.func_96652_c();
      this.field_149326_d = S3CPacketUpdateScore.Action.CHANGE;
   }

   public S3CPacketUpdateScore(String var1) {
      super();
      this.field_149329_a = var1;
      this.field_149327_b = "";
      this.field_149328_c = 0;
      this.field_149326_d = S3CPacketUpdateScore.Action.REMOVE;
   }

   public S3CPacketUpdateScore(String var1, ScoreObjective var2) {
      super();
      this.field_149329_a = var1;
      this.field_149327_b = var2.func_96679_b();
      this.field_149328_c = 0;
      this.field_149326_d = S3CPacketUpdateScore.Action.REMOVE;
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149329_a = var1.func_150789_c(40);
      this.field_149326_d = (S3CPacketUpdateScore.Action)var1.func_179257_a(S3CPacketUpdateScore.Action.class);
      this.field_149327_b = var1.func_150789_c(16);
      if (this.field_149326_d != S3CPacketUpdateScore.Action.REMOVE) {
         this.field_149328_c = var1.func_150792_a();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(this.field_149329_a);
      var1.func_179249_a(this.field_149326_d);
      var1.func_180714_a(this.field_149327_b);
      if (this.field_149326_d != S3CPacketUpdateScore.Action.REMOVE) {
         var1.func_150787_b(this.field_149328_c);
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

   public S3CPacketUpdateScore.Action func_180751_d() {
      return this.field_149326_d;
   }

   public static enum Action {
      CHANGE,
      REMOVE;

      private Action() {
      }
   }
}
