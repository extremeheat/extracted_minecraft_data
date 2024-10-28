package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class OptionsProgrammerArtFix extends DataFix {
   public OptionsProgrammerArtFix(Schema var1) {
      super(var1, false);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsProgrammerArtFix", this.getInputSchema().getType(References.OPTIONS), (var1) -> {
         return var1.update(DSL.remainderFinder(), (var1x) -> {
            return var1x.update("resourcePacks", this::fixList).update("incompatibleResourcePacks", this::fixList);
         });
      });
   }

   private <T> Dynamic<T> fixList(Dynamic<T> var1) {
      return (Dynamic)var1.asString().result().map((var1x) -> {
         return var1.createString(var1x.replace("\"programer_art\"", "\"programmer_art\""));
      }).orElse(var1);
   }
}
