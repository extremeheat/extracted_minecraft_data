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
      return this.fixTypeEverywhereTyped("ChunkStatusFix", var1, this.getOutputSchema().getType(References.CHUNK), var1x -> var1x.updateTyped(var3, var0x -> {
            Dynamic var1xx = (Dynamic)var0x.get(DSL.remainderFinder());
            String var2x = var1xx.get("Status").asString("empty");
            if (Objects.equals(var2x, "postprocessed")) {
               var1xx = var1xx.set("Status", var1xx.createString("fullchunk"));
            }

            return var0x.set(DSL.remainderFinder(), var1xx);
         }));
   }
}
