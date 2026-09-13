package net.minecraft.stats;

import net.minecraft.item.Item;
import net.minecraft.util.IChatComponent;

public class StatCrafting extends StatBase {
   private final Item field_150960_a;

   public StatCrafting(String var1, IChatComponent var2, Item var3) {
      super(var1, var2);
      this.field_150960_a = var3;
   }

   public Item func_150959_a() {
      return this.field_150960_a;
   }
}
