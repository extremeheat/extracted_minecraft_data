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
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class BlendingDataFix extends DataFix {
   private final String name;
   private static final Set<String> STATUSES_TO_SKIP_BLENDING = Set.of("minecraft:empty", "minecraft:structure_starts", "minecraft:structure_references", "minecraft:noise_biomes", "minecraft:biomes");
   private static final long SECTION_BITS = 4L;

   public BlendingDataFix(final Schema outputSchema) {
      super(outputSchema, false);
      this.name = "Blending Data Fix v" + outputSchema.getVersionKey();
   }

   protected TypeRewriteRule makeRule() {
      Type<?> chunkType = this.getOutputSchema().getType(References.CHUNK);
      return this.fixTypeEverywhereTyped(this.name, chunkType, (chunk) -> chunk.update(DSL.remainderFinder(), (chunkTag) -> updateChunkTag(chunkTag, chunkTag.get("__context"))));
   }

   private static Dynamic<?> updateChunkTag(Dynamic<?> chunkTag, final OptionalDynamic<?> contextTag) {
      chunkTag = chunkTag.remove("blending_data");
      boolean isOverworld = "minecraft:overworld".equals(contextTag.get("dimension").asString().result().orElse(""));
      Optional<? extends Dynamic<?>> statusOpt = chunkTag.get("status").result();
      if (isOverworld && statusOpt.isPresent()) {
         String status = NamespacedSchema.ensureNamespaced(((Dynamic)statusOpt.get()).asString("empty"));
         Optional<? extends Dynamic<?>> retroGenOpt = chunkTag.get("retrogen").result();
         if (!STATUSES_TO_SKIP_BLENDING.contains(status)) {
            chunkTag = updateBlendingData(chunkTag, 384, -64);
         } else if (retroGenOpt.isPresent()) {
            Dynamic<?> retroGen = (Dynamic)retroGenOpt.get();
            String targetStatus = NamespacedSchema.ensureNamespaced(retroGen.get("target_status").asString("empty"));
            if (!STATUSES_TO_SKIP_BLENDING.contains(targetStatus)) {
               boolean hasBelowZeroRetrogen = retroGen.get("has_below_zero_retrogen").asBoolean(false);
               if (hasBelowZeroRetrogen) {
                  chunkTag = updateBlendingData(chunkTag, 256, 0);
               } else {
                  chunkTag = updateBlendingData(chunkTag, 384, -64);
               }
            }
         }
      }

      return chunkTag;
   }

   private static Dynamic<?> updateBlendingData(final Dynamic<?> chunkTag, final int height, final int minY) {
      return chunkTag.set("blending_data", chunkTag.createMap(Map.of(chunkTag.createString("min_section"), chunkTag.createInt(blockToSectionCoord(minY)), chunkTag.createString("max_section"), chunkTag.createInt(blockToSectionCoord(minY + height)))));
   }

   public static int blockToSectionCoord(final int blockCoord) {
      return blockCoord >> 4;
   }
}
