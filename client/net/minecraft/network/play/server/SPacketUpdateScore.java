package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ServerScoreboard;

public class SPacketUpdateScore implements Packet<INetHandlerPlayClient> {
   private String field_149329_a = "";
   @Nullable
   private String field_149327_b;
   private int field_149328_c;
   private ServerScoreboard.Action field_149326_d;

   public SPacketUpdateScore() {
      super();
   }

   public SPacketUpdateScore(ServerScoreboard.Action var1, @Nullable String var2, String var3, int var4) {
      super();
      if (var1 != ServerScoreboard.Action.REMOVE && var2 == null) {
         throw new IllegalArgumentException("Need an objective name");
      } else {
         this.field_149329_a = var3;
         this.field_149327_b = var2;
         this.field_149328_c = var4;
         this.field_149326_d = var1;
      }
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_149329_a = var1.func_150789_c(40);
      this.field_149326_d = (ServerScoreboard.Action)var1.func_179257_a(ServerScoreboard.Action.class);
      String var2 = var1.func_150789_c(16);
      this.field_149327_b = Objects.equals(var2, "") ? null : var2;
      if (this.field_149326_d != ServerScoreboard.Action.REMOVE) {
         this.field_149328_c = var1.func_150792_a();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_180714_a(this.field_149329_a);
      var1.func_179249_a(this.field_149326_d);
      var1.func_180714_a(this.field_149327_b == null ? "" : this.field_149327_b);
      if (this.field_149326_d != ServerScoreboard.Action.REMOVE) {
         var1.func_150787_b(this.field_149328_c);
      }

   }

   public void func_148833_a(INetHandlerPlayClient var1) {
      var1.func_147250_a(this);
   }

   public String func_149324_c() {
      return this.field_149329_a;
   }

   @Nullable
   public String func_149321_d() {
      return this.field_149327_b;
   }

   public int func_149323_e() {
      return this.field_149328_c;
   }

   public ServerScoreboard.Action func_197701_d() {
      return this.field_149326_d;
   }
}
