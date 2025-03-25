package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class MapIdFix extends DataFix {
   public MapIdFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      return this.writeFixAndRead("Map id fix", this.getInputSchema().getType(References.SAVED_DATA_MAP_DATA), this.getOutputSchema().getType(References.SAVED_DATA_MAP_DATA), (var0) -> var0.createMap(Map.of(var0.createString("data"), var0)));
   }
}
