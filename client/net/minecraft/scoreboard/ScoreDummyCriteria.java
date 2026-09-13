package net.minecraft.scoreboard;

import java.util.List;

public class ScoreDummyCriteria implements IScoreObjectiveCriteria {
   private final String field_96644_g;

   public ScoreDummyCriteria(String var1) {
      super();
      this.field_96644_g = var1;
      IScoreObjectiveCriteria.field_96643_a.put(var1, this);
   }

   @Override
   public String func_96636_a() {
      return this.field_96644_g;
   }

   @Override
   public int func_96635_a(List var1) {
      return 0;
   }

   @Override
   public boolean func_96637_b() {
      return false;
   }
}
