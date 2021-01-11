package net.minecraft.scoreboard;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class GoalColor implements IScoreObjectiveCriteria {
   private final String field_178794_j;

   public GoalColor(String var1, EnumChatFormatting var2) {
      super();
      this.field_178794_j = var1 + var2.func_96297_d();
      IScoreObjectiveCriteria.field_96643_a.put(this.field_178794_j, this);
   }

   public String func_96636_a() {
      return this.field_178794_j;
   }

   public int func_96635_a(List<EntityPlayer> var1) {
      return 0;
   }

   public boolean func_96637_b() {
      return false;
   }

   public IScoreObjectiveCriteria.EnumRenderType func_178790_c() {
      return IScoreObjectiveCriteria.EnumRenderType.INTEGER;
   }
}
