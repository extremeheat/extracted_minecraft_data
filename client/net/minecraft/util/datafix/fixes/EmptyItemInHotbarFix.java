package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class EmptyItemInHotbarFix extends DataFix {
   public EmptyItemInHotbarFix(Schema var1) {
      super(var1, false);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder var1 = DSL.typeFinder(this.getInputSchema().getType(References.ITEM_STACK));
      return this.fixTypeEverywhereTyped(
         "EmptyItemInHotbarFix", this.getInputSchema().getType(References.HOTBAR), var1x -> var1x.update(var1, var0x -> var0x.mapSecond(var0xx -> {
                  Optional var1xx = ((Either)var0xx.getFirst()).left().map(Pair::getSecond);
                  Dynamic var2 = (Dynamic)((Pair)var0xx.getSecond()).getSecond();
                  boolean var3 = var1xx.isEmpty() || ((String)var1xx.get()).equals("minecraft:air");
                  boolean var4 = var2.get("Count").asInt(0) <= 0;
                  return !var3 && !var4 ? var0xx : Pair.of(Either.right(Unit.INSTANCE), Pair.of(Either.right(Unit.INSTANCE), var2.emptyMap()));
               }))
      );
   }
}
