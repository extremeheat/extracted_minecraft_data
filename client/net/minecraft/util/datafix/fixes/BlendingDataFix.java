package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.SectionPos;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class BlendingDataFix extends DataFix {
   private final String name;
   private static final Set<String> STATUSES_TO_SKIP_BLENDING = Set.of("minecraft:empty", "minecraft:structure_starts", "minecraft:structure_references", "minecraft:biomes");

   public BlendingDataFix(Schema var1) {
      super(var1, false);
      this.name = "Blending Data Fix v" + var1.getVersionKey();
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getOutputSchema().getType(References.CHUNK);
      return this.fixTypeEverywhereTyped(this.name, var1, (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            return updateChunkTag(var0x, var0x.get("__context"));
         });
      });
   }

   private static Dynamic<?> updateChunkTag(Dynamic<?> var0, OptionalDynamic<?> var1) {
      var0 = var0.remove("blending_data");
      boolean var2 = "minecraft:overworld".equals(var1.get("dimension").asString().result().orElse(""));
      Optional var3 = var0.get("Status").result();
      if (var2 && var3.isPresent()) {
         String var4 = NamespacedSchema.ensureNamespaced(((Dynamic)var3.get()).asString("empty"));
         Optional var5 = var0.get("below_zero_retrogen").result();
         if (!STATUSES_TO_SKIP_BLENDING.contains(var4)) {
            var0 = updateBlendingData(var0, 384, -64);
         } else if (var5.isPresent()) {
            Dynamic var6 = (Dynamic)var5.get();
            String var7 = NamespacedSchema.ensureNamespaced(var6.get("target_status").asString("empty"));
            if (!STATUSES_TO_SKIP_BLENDING.contains(var7)) {
               var0 = updateBlendingData(var0, 256, 0);
            }
         }
      }

      return var0;
   }

   private static Dynamic<?> updateBlendingData(Dynamic<?> var0, int var1, int var2) {
      return var0.set("blending_data", var0.createMap(Map.of(var0.createString("min_section"), var0.createInt(SectionPos.blockToSectionCoord(var2)), var0.createString("max_section"), var0.createInt(SectionPos.blockToSectionCoord(var2 + var1)))));
   }
}
