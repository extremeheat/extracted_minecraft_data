package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class ContainerBlockEntityLockPredicateFix extends DataFix {
   public ContainerBlockEntityLockPredicateFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("ContainerBlockEntityLockPredicateFix", this.getInputSchema().findChoiceType(References.BLOCK_ENTITY), ContainerBlockEntityLockPredicateFix::fixBlockEntity);
   }

   private static Typed<?> fixBlockEntity(Typed<?> var0) {
      return var0.update(DSL.remainderFinder(), (var0x) -> var0x.renameAndFixField("Lock", "lock", LockComponentPredicateFix::fixLock));
   }
}
