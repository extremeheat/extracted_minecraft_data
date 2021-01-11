package net.minecraft.scoreboard;

public class ScoreObjective {
   private final Scoreboard field_96686_a;
   private final String field_96684_b;
   private final IScoreObjectiveCriteria field_96685_c;
   private IScoreObjectiveCriteria.EnumRenderType field_178768_d;
   private String field_96683_d;

   public ScoreObjective(Scoreboard var1, String var2, IScoreObjectiveCriteria var3) {
      super();
      this.field_96686_a = var1;
      this.field_96684_b = var2;
      this.field_96685_c = var3;
      this.field_96683_d = var2;
      this.field_178768_d = var3.func_178790_c();
   }

   public Scoreboard func_96682_a() {
      return this.field_96686_a;
   }

   public String func_96679_b() {
      return this.field_96684_b;
   }

   public IScoreObjectiveCriteria func_96680_c() {
      return this.field_96685_c;
   }

   public String func_96678_d() {
      return this.field_96683_d;
   }

   public void func_96681_a(String var1) {
      this.field_96683_d = var1;
      this.field_96686_a.func_96532_b(this);
   }

   public IScoreObjectiveCriteria.EnumRenderType func_178766_e() {
      return this.field_178768_d;
   }

   public void func_178767_a(IScoreObjectiveCriteria.EnumRenderType var1) {
      this.field_178768_d = var1;
      this.field_96686_a.func_96532_b(this);
   }
}
