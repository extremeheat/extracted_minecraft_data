package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class CarvingStepRemoveFix extends DataFix {
   public CarvingStepRemoveFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("CarvingStepRemoveFix", this.getInputSchema().getType(References.CHUNK), CarvingStepRemoveFix::fixChunk);
   }

   private static Typed<?> fixChunk(Typed<?> var0) {
      return var0.update(DSL.remainderFinder(), (var0x) -> {
         Dynamic var1 = var0x;
         Optional var2 = var0x.get("CarvingMasks").result();
         if (var2.isPresent()) {
            Optional var3 = ((Dynamic)var2.get()).get("AIR").result();
            if (var3.isPresent()) {
               var1 = var0x.set("carving_mask", (Dynamic)var3.get());
            }
         }

         return var1.remove("CarvingMasks");
      });
   }
}
