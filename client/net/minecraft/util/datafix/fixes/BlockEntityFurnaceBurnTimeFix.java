package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class BlockEntityFurnaceBurnTimeFix extends NamedEntityFix {
   public BlockEntityFurnaceBurnTimeFix(Schema var1, String var2) {
      super(var1, false, "BlockEntityFurnaceBurnTimeFix" + var2, References.BLOCK_ENTITY, var2);
   }

   public Dynamic<?> fixBurnTime(Dynamic<?> var1) {
      var1 = var1.renameField("CookTime", "cooking_time_spent");
      var1 = var1.renameField("CookTimeTotal", "cooking_total_time");
      var1 = var1.renameField("BurnTime", "lit_time_remaining");
      var1 = var1.setFieldIfPresent("lit_total_time", var1.get("lit_time_remaining").result());
      return var1;
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixBurnTime);
   }
}
