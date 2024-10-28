package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class StructureReferenceCountFix extends DataFix {
   public StructureReferenceCountFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.STRUCTURE_FEATURE);
      return this.fixTypeEverywhereTyped("Structure Reference Fix", var1, (var0) -> {
         return var0.update(DSL.remainderFinder(), StructureReferenceCountFix::setCountToAtLeastOne);
      });
   }

   private static <T> Dynamic<T> setCountToAtLeastOne(Dynamic<T> var0) {
      return var0.update("references", (var0x) -> {
         return var0x.createInt((Integer)var0x.asNumber().map(Number::intValue).result().filter((var0) -> {
            return var0 > 0;
         }).orElse(1));
      });
   }
}
