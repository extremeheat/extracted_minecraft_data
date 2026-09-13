package net.minecraft.scoreboard;

import java.util.Comparator;
import java.util.List;

public class Score {
   public static final Comparator field_96658_a = new Score$1();
   private final Scoreboard field_96656_b;
   private final ScoreObjective field_96657_c;
   private final String field_96654_d;
   private int field_96655_e;

   public Score(Scoreboard var1, ScoreObjective var2, String var3) {
      super();
      this.field_96656_b = var1;
      this.field_96657_c = var2;
      this.field_96654_d = var3;
   }

   public void func_96649_a(int var1) {
      if (this.field_96657_c.func_96680_c().func_96637_b()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.func_96647_c(this.func_96652_c() + var1);
      }
   }

   public void func_96646_b(int var1) {
      if (this.field_96657_c.func_96680_c().func_96637_b()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.func_96647_c(this.func_96652_c() - var1);
      }
   }

   public void func_96648_a() {
      if (this.field_96657_c.func_96680_c().func_96637_b()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.func_96649_a(1);
      }
   }

   public int func_96652_c() {
      return this.field_96655_e;
   }

   public void func_96647_c(int var1) {
      int var2 = this.field_96655_e;
      this.field_96655_e = var1;
      if (var2 != var1) {
         this.func_96650_f().func_96536_a(this);
      }
   }

   public ScoreObjective func_96645_d() {
      return this.field_96657_c;
   }

   public String func_96653_e() {
      return this.field_96654_d;
   }

   public Scoreboard func_96650_f() {
      return this.field_96656_b;
   }

   public void func_96651_a(List var1) {
      this.func_96647_c(this.field_96657_c.func_96680_c().func_96635_a(var1));
   }
}
