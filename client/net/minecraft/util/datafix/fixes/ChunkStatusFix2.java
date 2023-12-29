package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Objects;

public class ChunkStatusFix2 extends DataFix {
   private static final Map<String, String> RENAMES_AND_DOWNGRADES = ImmutableMap.builder()
      .put("structure_references", "empty")
      .put("biomes", "empty")
      .put("base", "surface")
      .put("carved", "carvers")
      .put("liquid_carved", "liquid_carvers")
      .put("decorated", "features")
      .put("lighted", "light")
      .put("mobs_spawned", "spawn")
      .put("finalized", "heightmaps")
      .put("fullchunk", "full")
      .build();

   public ChunkStatusFix2(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      Type var2 = var1.findFieldType("Level");
      OpticFinder var3 = DSL.fieldFinder("Level", var2);
      return this.fixTypeEverywhereTyped(
         "ChunkStatusFix2", var1, this.getOutputSchema().getType(References.CHUNK), var1x -> var1x.updateTyped(var3, var0x -> {
               Dynamic var1xxx = (Dynamic)var0x.get(DSL.remainderFinder());
               String var2xx = var1xxx.get("Status").asString("empty");
               String var3xx = RENAMES_AND_DOWNGRADES.getOrDefault(var2xx, "empty");
               return Objects.equals(var2xx, var3xx) ? var0x : var0x.set(DSL.remainderFinder(), var1xxx.set("Status", var1xxx.createString(var3xx)));
            })
      );
   }
}
