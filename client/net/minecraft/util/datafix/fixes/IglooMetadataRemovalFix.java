package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class IglooMetadataRemovalFix extends DataFix {
   public IglooMetadataRemovalFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.STRUCTURE_FEATURE);
      Type var2 = this.getOutputSchema().getType(References.STRUCTURE_FEATURE);
      return this.writeFixAndRead("IglooMetadataRemovalFix", var1, var2, IglooMetadataRemovalFix::fixTag);
   }

   private static <T> Dynamic<T> fixTag(Dynamic<T> var0) {
      boolean var1 = var0.get("Children").asStreamOpt().map(var0x -> var0x.allMatch(IglooMetadataRemovalFix::isIglooPiece)).result().orElse(false);
      return var1 ? var0.set("id", var0.createString("Igloo")).remove("Children") : var0.update("Children", IglooMetadataRemovalFix::removeIglooPieces);
   }

   private static <T> Dynamic<T> removeIglooPieces(Dynamic<T> var0) {
      return (Dynamic<T>)var0.asStreamOpt().map(var0x -> var0x.filter(var0xx -> !isIglooPiece(var0xx))).map(var0::createList).result().orElse((T)var0);
   }

   private static boolean isIglooPiece(Dynamic<?> var0) {
      return var0.get("id").asString("").equals("Iglu");
   }
}
