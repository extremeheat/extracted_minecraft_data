package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class SavedDataUUIDFix extends AbstractUUIDFix {
   public SavedDataUUIDFix(Schema var1) {
      super(var1, References.SAVED_DATA);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("SavedDataUUIDFix", this.getInputSchema().getType(this.typeReference), (var0) -> {
         return var0.updateTyped(var0.getType().findField("data"), (var0x) -> {
            return var0x.update(DSL.remainderFinder(), (var0) -> {
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
