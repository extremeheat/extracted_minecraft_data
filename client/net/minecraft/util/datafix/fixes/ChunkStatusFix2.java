package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Objects;

public class ChunkStatusFix2 extends DataFix {
   private static final Map<String, String> RENAMES_AND_DOWNGRADES = ImmutableMap.builder().put("structure_references", "empty").put("biomes", "empty").put("base", "surface").put("carved", "carvers").put("liquid_carved", "liquid_carvers").put("decorated", "features").put("lighted", "light").put("mobs_spawned", "spawn").put("finalized", "heightmaps").put("fullchunk", "full").build();

   public ChunkStatusFix2(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      Type var2 = var1.findFieldType("Level");
      OpticFinder var3 = DSL.fieldFinder("Level", var2);
      return this.fixTypeEverywhereTyped("ChunkStatusFix2", var1, this.getOutputSchema().getType(References.CHUNK), (var1x) -> {
         return var1x.updateTyped(var3, (var0) -> {
            Dynamic var1 = (Dynamic)var0.get(DSL.remainderFinder());
            String var2 = var1.get("Status").asString("empty");
            String var3 = (String)RENAMES_AND_DOWNGRADES.getOrDefault(var2, "empty");
            return Objects.equals(var2, var3) ? var0 : var0.set(DSL.remainderFinder(), var1.set("Status", var1.createString(var3)));
         });
      });
   }
}
