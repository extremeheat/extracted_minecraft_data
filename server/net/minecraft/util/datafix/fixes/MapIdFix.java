package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;

public class MapIdFix extends DataFix {
   public MapIdFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.SAVED_DATA);
      OpticFinder var2 = var1.findField("data");
      return this.fixTypeEverywhereTyped("Map id fix", var1, (var1x) -> {
         Optional var2x = var1x.getOptionalTyped(var2);
         return var2x.isPresent() ? var1x : var1x.update(DSL.remainderFinder(), (var0) -> {
            return var0.createMap(ImmutableMap.of(var0.createString("data"), var0));
         });
      });
   }
}
