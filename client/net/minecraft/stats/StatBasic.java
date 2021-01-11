package net.minecraft.stats;

import net.minecraft.util.IChatComponent;

public class StatBasic extends StatBase {
   public StatBasic(String var1, IChatComponent var2, IStatType var3) {
      super(var1, var2, var3);
   }

   public StatBasic(String var1, IChatComponent var2) {
      super(var1, var2);
   }

   public StatBase func_75971_g() {
      super.func_75971_g();
      StatList.field_75941_c.add(this);
      return this;
   }
}
