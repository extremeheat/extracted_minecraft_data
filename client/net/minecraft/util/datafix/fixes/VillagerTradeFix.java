package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class VillagerTradeFix extends NamedEntityFix {
   public VillagerTradeFix(Schema var1, boolean var2) {
      super(var1, var2, "Villager trade fix", References.ENTITY, "minecraft:villager");
   }

   protected Typed<?> fix(Typed<?> var1) {
      OpticFinder var2 = var1.getType().findField("Offers");
      OpticFinder var3 = var2.type().findField("Recipes");
      Type var4 = var3.type();
      if (!(var4 instanceof ListType)) {
         throw new IllegalStateException("Recipes are expected to be a list.");
      } else {
         ListType var5 = (ListType)var4;
         Type var6 = var5.getElement();
         OpticFinder var7 = DSL.typeFinder(var6);
         OpticFinder var8 = var6.findField("buy");
         OpticFinder var9 = var6.findField("buyB");
         OpticFinder var10 = var6.findField("sell");
         OpticFinder var11 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
         Function var12 = (var2x) -> {
            return this.updateItemStack(var11, var2x);
         };
         return var1.updateTyped(var2, (var6x) -> {
            return var6x.updateTyped(var3, (var5) -> {
               return var5.updateTyped(var7, (var4) -> {
                  return var4.updateTyped(var8, var12).updateTyped(var9, var12).updateTyped(var10, var12);
               });
            });
         });
      }
   }

   private Typed<?> updateItemStack(OpticFinder<Pair<String, String>> var1, Typed<?> var2) {
      return var2.update(var1, (var0) -> {
         return var0.mapSecond((var0x) -> {
            return Objects.equals(var0x, "minecraft:carved_pumpkin") ? "minecraft:pumpkin" : var0x;
         });
      });
   }
}
