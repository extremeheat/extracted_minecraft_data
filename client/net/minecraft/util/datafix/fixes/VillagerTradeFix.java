package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class VillagerTradeFix extends DataFix {
   public VillagerTradeFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.VILLAGER_TRADE);
      OpticFinder var2 = var1.findField("buy");
      OpticFinder var3 = var1.findField("buyB");
      OpticFinder var4 = var1.findField("sell");
      OpticFinder var5 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      Function var6 = (var2x) -> {
         return this.updateItemStack(var5, var2x);
      };
      return this.fixTypeEverywhereTyped("Villager trade fix", var1, (var4x) -> {
         return var4x.updateTyped(var2, var6).updateTyped(var3, var6).updateTyped(var4, var6);
      });
   }

   private Typed<?> updateItemStack(OpticFinder<Pair<String, String>> var1, Typed<?> var2) {
      return var2.update(var1, (var0) -> {
         return var0.mapSecond((var0x) -> {
            return Objects.equals(var0x, "minecraft:carved_pumpkin") ? "minecraft:pumpkin" : var0x;
         });
      });
   }
}
