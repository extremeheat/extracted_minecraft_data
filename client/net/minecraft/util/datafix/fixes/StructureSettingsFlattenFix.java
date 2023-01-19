package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;

public class StructureSettingsFlattenFix extends DataFix {
   public StructureSettingsFlattenFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.WORLD_GEN_SETTINGS);
      OpticFinder var2 = var1.findField("dimensions");
      return this.fixTypeEverywhereTyped("StructureSettingsFlatten", var1, var1x -> var1x.updateTyped(var2, var1xx -> {
            Dynamic var2x = (Dynamic)var1xx.write().result().orElseThrow();
            Dynamic var3 = var2x.updateMapValues(StructureSettingsFlattenFix::fixDimension);
            return (Typed)((Pair)var2.type().readTyped(var3).result().orElseThrow()).getFirst();
         }));
   }

   private static Pair<Dynamic<?>, Dynamic<?>> fixDimension(Pair<Dynamic<?>, Dynamic<?>> var0) {
      Dynamic var1 = (Dynamic)var0.getSecond();
      return Pair.of(
         (Dynamic)var0.getFirst(),
         var1.update("generator", var0x -> var0x.update("settings", var0xx -> var0xx.update("structures", StructureSettingsFlattenFix::fixStructures)))
      );
   }

   private static Dynamic<?> fixStructures(Dynamic<?> var0) {
      Dynamic var1 = var0.get("structures")
         .orElseEmptyMap()
         .updateMapValues(var1x -> var1x.mapSecond(var1xx -> var1xx.set("type", var0.createString("minecraft:random_spread"))));
      return (Dynamic<?>)DataFixUtils.orElse(
         var0.get("stronghold").result().map(var2 -> var1.set("minecraft:stronghold", var2.set("type", var0.createString("minecraft:concentric_rings")))),
         var1
      );
   }
}
