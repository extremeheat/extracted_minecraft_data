package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import org.slf4j.Logger;

public class SavedDataUUIDFix extends AbstractUUIDFix {
   private static final Logger LOGGER = LogUtils.getLogger();

   public SavedDataUUIDFix(Schema var1) {
      super(var1, References.SAVED_DATA_RAIDS);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("SavedDataUUIDFix", this.getInputSchema().getType(this.typeReference), (var0) -> {
         return var0.update(DSL.remainderFinder(), (var0x) -> {
            return var0x.update("data", (var0) -> {
               return var0.update("Raids", (var0x) -> {
                  return var0x.createList(var0x.asStream().map((var0) -> {
                     return var0.update("HeroesOfTheVillage", (var0x) -> {
                        return var0x.createList(var0x.asStream().map((var0) -> {
                           return (Dynamic)createUUIDFromLongs(var0, "UUIDMost", "UUIDLeast").orElseGet(() -> {
                              LOGGER.warn("HeroesOfTheVillage contained invalid UUIDs.");
                              return var0;
                           });
                        }));
                     });
                  }));
               });
            });
         });
      });
   }
}
