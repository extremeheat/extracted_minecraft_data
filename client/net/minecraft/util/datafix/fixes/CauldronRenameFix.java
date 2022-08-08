package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class CauldronRenameFix extends DataFix {
   public CauldronRenameFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   private static Dynamic<?> fix(Dynamic<?> var0) {
      Optional var1 = var0.get("Name").asString().result();
      if (var1.equals(Optional.of("minecraft:cauldron"))) {
         Dynamic var2 = var0.get("Properties").orElseEmptyMap();
         return var2.get("level").asString("0").equals("0") ? var0.remove("Properties") : var0.set("Name", var0.createString("minecraft:water_cauldron"));
      } else {
         return var0;
      }
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("cauldron_rename_fix", this.getInputSchema().getType(References.BLOCK_STATE), (var0) -> {
         return var0.update(DSL.remainderFinder(), CauldronRenameFix::fix);
      });
   }
}
