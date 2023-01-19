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
      super(var1, References.SAVED_DATA);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(
         "SavedDataUUIDFix",
         this.getInputSchema().getType(this.typeReference),
         var0 -> var0.updateTyped(
               var0.getType().findField("data"),
               var0x -> var0x.update(
                     DSL.remainderFinder(),
                     var0xx -> var0xx.update(
                           "Raids",
                           var0xxx -> var0xxx.createList(
                                 var0xxx.asStream()
                                    .map(
                                       var0xxxx -> var0xxxx.update(
                                             "HeroesOfTheVillage",
                                             var0xxxxx -> var0xxxxx.createList(
                                                   var0xxxxx.asStream()
                                                      .map(var0xxxxxx -> (Dynamic)createUUIDFromLongs(var0xxxxxx, "UUIDMost", "UUIDLeast").orElseGet(() -> {
                                                            LOGGER.warn("HeroesOfTheVillage contained invalid UUIDs.");
                                                            return var0xxxxxx;
                                                         }))
                                                )
                                          )
                                    )
                              )
                        )
                  )
            )
      );
   }
}
