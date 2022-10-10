package net.minecraft.scoreboard;

import java.util.Comparator;
import javax.annotation.Nullable;

public class Score {
   public static final Comparator<Score> field_96658_a = (var0, var1) -> {
      if (var0.func_96652_c() > var1.func_96652_c()) {
         return 1;
      } else {
         return var0.func_96652_c() < var1.func_96652_c() ? -1 : var1.func_96653_e().compareToIgnoreCase(var0.func_96653_e());
      }
   };
   private final Scoreboard field_96656_b;
   @Nullable
   private final ScoreObjective field_96657_c;
   private final String field_96654_d;
   private int field_96655_e;
   private boolean field_178817_f;
   private boolean field_178818_g;

   public Score(Scoreboard var1, ScoreObjective var2, String var3) {
      super();
      this.field_96656_b = var1;
      this.field_96657_c = var2;
      this.field_96654_d = var3;
      this.field_178817_f = true;
      this.field_178818_g = true;
   }

   public void func_96649_a(int var1) {
      if (this.field_96657_c.func_96680_c().func_96637_b()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.func_96647_c(this.func_96652_c() + var1);
      }
   }

   public void func_96648_a() {
      this.func_96649_a(1);
   }

   public int func_96652_c() {
      return this.field_96655_e;
   }

   public void func_197891_c() {
      this.func_96647_c(0);
   }

   public void func_96647_c(int var1) {
      int var2 = this.field_96655_e;
      this.field_96655_e = var1;
      if (var2 != var1 || this.field_178818_g) {
         this.field_178818_g = false;
         this.func_96650_f().func_96536_a(this);
      }

   }

   @Nullable
   public ScoreObjective func_96645_d() {
      return this.field_96657_c;
   }

   public String func_96653_e() {
      return this.field_96654_d;
   }

   public Scoreboard func_96650_f() {
      return this.field_96656_b;
   }

   public boolean func_178816_g() {
      return this.field_178817_f;
   }

   public void func_178815_a(boolean var1) {
      this.field_178817_f = var1;
   }
}
