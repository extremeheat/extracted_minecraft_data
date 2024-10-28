package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class ChunkLightRemoveFix extends DataFix {
   public ChunkLightRemoveFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      Type var2 = var1.findFieldType("Level");
      OpticFinder var3 = DSL.fieldFinder("Level", var2);
      return this.fixTypeEverywhereTyped("ChunkLightRemoveFix", var1, this.getOutputSchema().getType(References.CHUNK), (var1x) -> {
         return var1x.updateTyped(var3, (var0) -> {
            return var0.update(DSL.remainderFinder(), (var0x) -> {
               return var0x.remove("isLightOn");
            });
         });
      });
   }
}
