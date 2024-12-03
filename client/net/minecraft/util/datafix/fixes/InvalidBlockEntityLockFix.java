package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class InvalidBlockEntityLockFix extends DataFix {
   public InvalidBlockEntityLockFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("BlockEntityLockToComponentFix", this.getInputSchema().getType(References.BLOCK_ENTITY), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> {
            Optional var1 = var0x.get("lock").result();
            if (var1.isEmpty()) {
               return var0x;
            } else {
               Dynamic var2 = InvalidLockComponentFix.fixLock((Dynamic)var1.get());
               return var2 != null ? var0x.set("lock", var2) : var0x.remove("lock");
            }
         }));
   }
}
