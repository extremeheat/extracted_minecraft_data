package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class CarvingStepRemoveFix extends DataFix {
   public CarvingStepRemoveFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("CarvingStepRemoveFix", this.getInputSchema().getType(References.CHUNK), CarvingStepRemoveFix::fixChunk);
   }

   private static Typed<?> fixChunk(final Typed<?> input) {
      return input.update(DSL.remainderFinder(), (chunkIn) -> {
         Dynamic<?> chunk = chunkIn;
         Optional<? extends Dynamic<?>> carvingMasks = chunkIn.get("CarvingMasks").result();
         if (carvingMasks.isPresent()) {
            Optional<? extends Dynamic<?>> mask = ((Dynamic)carvingMasks.get()).get("AIR").result();
            if (mask.isPresent()) {
               chunk = chunkIn.set("carving_mask", (Dynamic)mask.get());
            }
         }

         return chunk.remove("CarvingMasks");
      });
   }
}
