package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class MapIdFix extends DataFix {
   public MapIdFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("Map id fix", this.getInputSchema().getType(References.SAVED_DATA_MAP_INDEX), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.createMap(Map.of(var0x.createString("data"), var0x))));
   }
}
