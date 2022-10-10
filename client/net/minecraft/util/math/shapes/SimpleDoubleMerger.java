package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

public class SimpleDoubleMerger implements IDoubleListMerger {
   private final DoubleList field_210220_a;

   public SimpleDoubleMerger(DoubleList var1) {
      super();
      this.field_210220_a = var1;
   }

   public boolean func_197855_a(IDoubleListMerger.Consumer var1) {
      for(int var2 = 0; var2 <= this.field_210220_a.size(); ++var2) {
         if (!var1.merge(var2, var2, var2)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList func_212435_a() {
      return this.field_210220_a;
   }
}
