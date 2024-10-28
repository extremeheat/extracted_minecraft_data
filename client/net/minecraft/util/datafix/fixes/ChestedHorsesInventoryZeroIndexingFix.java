package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;

public class ChestedHorsesInventoryZeroIndexingFix extends DataFix {
   public ChestedHorsesInventoryZeroIndexingFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      OpticFinder var1 = DSL.typeFinder(this.getInputSchema().getType(References.ITEM_STACK));
      Type var2 = this.getInputSchema().getType(References.ENTITY);
      return TypeRewriteRule.seq(this.horseLikeInventoryIndexingFixer(var1, var2, "minecraft:llama"), new TypeRewriteRule[]{this.horseLikeInventoryIndexingFixer(var1, var2, "minecraft:trader_llama"), this.horseLikeInventoryIndexingFixer(var1, var2, "minecraft:mule"), this.horseLikeInventoryIndexingFixer(var1, var2, "minecraft:donkey")});
   }

   private TypeRewriteRule horseLikeInventoryIndexingFixer(OpticFinder<Pair<String, Pair<Either<Pair<String, String>, Unit>, Pair<Either<?, Unit>, Dynamic<?>>>>> var1, Type<?> var2, String var3) {
      Type var4 = this.getInputSchema().getChoiceType(References.ENTITY, var3);
      OpticFinder var5 = DSL.namedChoice(var3, var4);
      OpticFinder var6 = var4.findField("Items");
      return this.fixTypeEverywhereTyped("Fix non-zero indexing in chest horse type " + var3, var2, (var3x) -> {
         return var3x.updateTyped(var5, (var2) -> {
            return var2.updateTyped(var6, (var1x) -> {
               return var1x.update(var1, (var0) -> {
                  return var0.mapSecond((var0x) -> {
                     return var0x.mapSecond((var0) -> {
                        return var0.mapSecond((var0x) -> {
                           return var0x.update("Slot", (var0) -> {
                              return var0.createByte((byte)(var0.asInt(2) - 2));
                           });
                        });
                     });
                  });
               });
            });
         });
      });
   }
}
