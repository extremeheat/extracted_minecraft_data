package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class MapIdFix extends DataFix {
   public MapIdFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("Map id fix", this.getInputSchema().getType(References.SAVED_DATA_MAP_DATA), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            return var0x.createMap(ImmutableMap.of(var0x.createString("data"), var0x));
         });
      });
   }
}
