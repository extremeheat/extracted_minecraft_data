package net.minecraft.util;

import java.util.Iterator;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;

public class ChatComponentScore extends ChatComponentStyle {
   private final String field_179999_b;
   private final String field_180000_c;
   private String field_179998_d = "";

   public ChatComponentScore(String var1, String var2) {
      super();
      this.field_179999_b = var1;
      this.field_180000_c = var2;
   }

   public String func_179995_g() {
      return this.field_179999_b;
   }

   public String func_179994_h() {
      return this.field_180000_c;
   }

   public void func_179997_b(String var1) {
      this.field_179998_d = var1;
   }

   public String func_150261_e() {
      MinecraftServer var1 = MinecraftServer.func_71276_C();
      if (var1 != null && var1.func_175578_N() && StringUtils.func_151246_b(this.field_179998_d)) {
         Scoreboard var2 = var1.func_71218_a(0).func_96441_U();
         ScoreObjective var3 = var2.func_96518_b(this.field_180000_c);
         if (var2.func_178819_b(this.field_179999_b, var3)) {
            Score var4 = var2.func_96529_a(this.field_179999_b, var3);
            this.func_179997_b(String.format("%d", var4.func_96652_c()));
         } else {
            this.field_179998_d = "";
         }
      }

      return this.field_179998_d;
   }

   public ChatComponentScore func_150259_f() {
      ChatComponentScore var1 = new ChatComponentScore(this.field_179999_b, this.field_180000_c);
      var1.func_179997_b(this.field_179998_d);
      var1.func_150255_a(this.func_150256_b().func_150232_l());
      Iterator var2 = this.func_150253_a().iterator();

      while(var2.hasNext()) {
         IChatComponent var3 = (IChatComponent)var2.next();
         var1.func_150257_a(var3.func_150259_f());
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ChatComponentScore)) {
         return false;
      } else {
         ChatComponentScore var2 = (ChatComponentScore)var1;
         return this.field_179999_b.equals(var2.field_179999_b) && this.field_180000_c.equals(var2.field_180000_c) && super.equals(var1);
      }
   }

   public String toString() {
      return "ScoreComponent{name='" + this.field_179999_b + '\'' + "objective='" + this.field_180000_c + '\'' + ", siblings=" + this.field_150264_a + ", style=" + this.func_150256_b() + '}';
   }

   // $FF: synthetic method
   public IChatComponent func_150259_f() {
      return this.func_150259_f();
   }
}
