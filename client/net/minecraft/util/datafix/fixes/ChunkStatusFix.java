package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class ChunkStatusFix extends DataFix {
   public ChunkStatusFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      Type var2 = var1.findFieldType("Level");
      OpticFinder var3 = DSL.fieldFinder("Level", var2);
      return this.fixTypeEverywhereTyped("ChunkStatusFix", var1, this.getOutputSchema().getType(References.CHUNK), (var1x) -> {
         return var1x.updateTyped(var3, (var0) -> {
            Dynamic var1 = (Dynamic)var0.get(DSL.remainderFinder());
            String var2 = var1.get("Status").asString("empty");
            if (Objects.equals(var2, "postprocessed")) {
               var1 = var1.set("Status", var1.createString("fullchunk"));
            }

            return var0.set(DSL.remainderFinder(), var1);
         });
      });
   }
}
