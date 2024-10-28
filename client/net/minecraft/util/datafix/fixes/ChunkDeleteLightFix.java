package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class ChunkDeleteLightFix extends DataFix {
   public ChunkDeleteLightFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      OpticFinder var2 = var1.findField("sections");
      return this.fixTypeEverywhereTyped("ChunkDeleteLightFix for " + this.getOutputSchema().getVersionKey(), var1, (var1x) -> {
         var1x = var1x.update(DSL.remainderFinder(), (var0) -> {
            return var0.remove("isLightOn");
         });
         return var1x.updateTyped(var2, (var0) -> {
            return var0.update(DSL.remainderFinder(), (var0x) -> {
               return var0x.remove("BlockLight").remove("SkyLight");
            });
         });
      });
   }
}
