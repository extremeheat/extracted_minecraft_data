package net.minecraft.util.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

public class TextComponentScore extends TextComponentBase {
   private final String field_179999_b;
   @Nullable
   private final EntitySelector field_197667_c;
   private final String field_180000_c;
   private String field_179998_d = "";

   public TextComponentScore(String var1, String var2) {
      super();
      this.field_179999_b = var1;
      this.field_180000_c = var2;
      EntitySelector var3 = null;

      try {
         EntitySelectorParser var4 = new EntitySelectorParser(new StringReader(var1));
         var3 = var4.func_201345_m();
      } catch (CommandSyntaxException var5) {
      }

      this.field_197667_c = var3;
   }

   public String func_179995_g() {
      return this.field_179999_b;
   }

   @Nullable
   public EntitySelector func_197666_h() {
      return this.field_197667_c;
   }

   public String func_179994_h() {
      return this.field_180000_c;
   }

   public void func_179997_b(String var1) {
      this.field_179998_d = var1;
   }

   public String func_150261_e() {
      return this.field_179998_d;
   }

   public void func_197665_b(CommandSource var1) {
      MinecraftServer var2 = var1.func_197028_i();
      if (var2 != null && var2.func_175578_N() && StringUtils.func_151246_b(this.field_179998_d)) {
         ServerScoreboard var3 = var2.func_200251_aP();
         ScoreObjective var4 = var3.func_96518_b(this.field_180000_c);
         if (var3.func_178819_b(this.field_179999_b, var4)) {
            Score var5 = var3.func_96529_a(this.field_179999_b, var4);
            this.func_179997_b(String.format("%d", var5.func_96652_c()));
         } else {
            this.field_179998_d = "";
         }
      }

   }

   public TextComponentScore func_150259_f() {
      TextComponentScore var1 = new TextComponentScore(this.field_179999_b, this.field_180000_c);
      var1.func_179997_b(this.field_179998_d);
      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TextComponentScore)) {
         return false;
      } else {
         TextComponentScore var2 = (TextComponentScore)var1;
         return this.field_179999_b.equals(var2.field_179999_b) && this.field_180000_c.equals(var2.field_180000_c) && super.equals(var1);
      }
   }

   public String toString() {
      return "ScoreComponent{name='" + this.field_179999_b + '\'' + "objective='" + this.field_180000_c + '\'' + ", siblings=" + this.field_150264_a + ", style=" + this.func_150256_b() + '}';
   }

   // $FF: synthetic method
   public ITextComponent func_150259_f() {
      return this.func_150259_f();
   }
}
