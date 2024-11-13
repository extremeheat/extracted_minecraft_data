package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.stream.Stream;

public class CustomModelDataExpandFix extends DataFix {
   public CustomModelDataExpandFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.DATA_COMPONENTS);
      return this.fixTypeEverywhereTyped("Custom Model Data expansion", var1, (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.update("minecraft:custom_model_data", (var0) -> {
               float var1 = var0.asNumber(0.0F).floatValue();
               return var0.createMap(Map.of(var0.createString("floats"), var0.createList(Stream.of(var0.createFloat(var1)))));
            })));
   }
}
