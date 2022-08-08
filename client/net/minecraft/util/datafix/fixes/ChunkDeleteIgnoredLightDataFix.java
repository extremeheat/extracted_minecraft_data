package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class ChunkDeleteIgnoredLightDataFix extends DataFix {
   public ChunkDeleteIgnoredLightDataFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      OpticFinder var2 = var1.findField("sections");
      return this.fixTypeEverywhereTyped("ChunkDeleteIgnoredLightDataFix", var1, (var1x) -> {
         boolean var2x = ((Dynamic)var1x.get(DSL.remainderFinder())).get("isLightOn").asBoolean(false);
         return !var2x ? var1x.updateTyped(var2, (var0) -> {
            return var0.update(DSL.remainderFinder(), (var0x) -> {
               return var0x.remove("BlockLight").remove("SkyLight");
            });
         }) : var1x;
      });
   }
}
