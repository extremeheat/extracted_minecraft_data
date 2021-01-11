package net.minecraft.stats;

import net.minecraft.item.Item;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.util.IChatComponent;

public class StatCrafting extends StatBase {
   private final Item field_150960_a;

   public StatCrafting(String var1, String var2, IChatComponent var3, Item var4) {
      super(var1 + var2, var3);
      this.field_150960_a = var4;
      int var5 = Item.func_150891_b(var4);
      if (var5 != 0) {
         IScoreObjectiveCriteria.field_96643_a.put(var1 + var5, this.func_150952_k());
      }

   }

   public Item func_150959_a() {
      return this.field_150960_a;
   }
}
