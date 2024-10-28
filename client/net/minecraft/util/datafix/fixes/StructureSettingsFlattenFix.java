package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.Util;

public class StructureSettingsFlattenFix extends DataFix {
   public StructureSettingsFlattenFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.WORLD_GEN_SETTINGS);
      OpticFinder var2 = var1.findField("dimensions");
      return this.fixTypeEverywhereTyped("StructureSettingsFlatten", var1, (var1x) -> {
         return var1x.updateTyped(var2, (var1) -> {
            return Util.writeAndReadTypedOrThrow(var1, var2.type(), (var0) -> {
               return var0.updateMapValues(StructureSettingsFlattenFix::fixDimension);
            });
         });
      });
   }

   private static Pair<Dynamic<?>, Dynamic<?>> fixDimension(Pair<Dynamic<?>, Dynamic<?>> var0) {
      Dynamic var1 = (Dynamic)var0.getSecond();
      return Pair.of((Dynamic)var0.getFirst(), var1.update("generator", (var0x) -> {
         return var0x.update("settings", (var0) -> {
            return var0.update("structures", StructureSettingsFlattenFix::fixStructures);
         });
      }));
   }

   private static Dynamic<?> fixStructures(Dynamic<?> var0) {
      Dynamic var1 = var0.get("structures").orElseEmptyMap().updateMapValues((var1x) -> {
         return var1x.mapSecond((var1) -> {
            return var1.set("type", var0.createString("minecraft:random_spread"));
         });
      });
      return (Dynamic)DataFixUtils.orElse(var0.get("stronghold").result().map((var2) -> {
         return var1.set("minecraft:stronghold", var2.set("type", var0.createString("minecraft:concentric_rings")));
      }), var1);
   }
}
