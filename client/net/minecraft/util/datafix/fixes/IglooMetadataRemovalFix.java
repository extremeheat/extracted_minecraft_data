package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class IglooMetadataRemovalFix extends DataFix {
   public IglooMetadataRemovalFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.STRUCTURE_FEATURE);
      return this.fixTypeEverywhereTyped("IglooMetadataRemovalFix", var1, (var0) -> {
         return var0.update(DSL.remainderFinder(), IglooMetadataRemovalFix::fixTag);
      });
   }

   private static <T> Dynamic<T> fixTag(Dynamic<T> var0) {
      boolean var1 = (Boolean)var0.get("Children").asStreamOpt().map((var0x) -> {
         return var0x.allMatch(IglooMetadataRemovalFix::isIglooPiece);
      }).result().orElse(false);
      return var1 ? var0.set("id", var0.createString("Igloo")).remove("Children") : var0.update("Children", IglooMetadataRemovalFix::removeIglooPieces);
   }

   private static <T> Dynamic<T> removeIglooPieces(Dynamic<T> var0) {
      DataResult var10000 = var0.asStreamOpt().map((var0x) -> {
         return var0x.filter((var0) -> {
            return !isIglooPiece(var0);
         });
      });
      Objects.requireNonNull(var0);
      return (Dynamic)var10000.map(var0::createList).result().orElse(var0);
   }

   private static boolean isIglooPiece(Dynamic<?> var0) {
      return var0.get("id").asString("").equals("Iglu");
   }
}
