package net.minecraft.util.datafix.fixes;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.function.Supplier;
import net.minecraft.util.datafix.ExtraDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class ThrownPotionSplitFix extends EntityRenameFix {
   private final Supplier<ItemIdFinder> itemIdFinder = Suppliers.memoize(() -> {
      Type var1 = this.getInputSchema().getChoiceType(References.ENTITY, "minecraft:potion");
      Type var2 = ExtraDataFixUtils.patchSubType(var1, this.getInputSchema().getType(References.ENTITY), this.getOutputSchema().getType(References.ENTITY));
      OpticFinder var3 = var2.findField("Item");
      OpticFinder var4 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      return new ItemIdFinder(var3, var4);
   });

   public ThrownPotionSplitFix(Schema var1) {
      super("ThrownPotionSplitFix", var1, true);
   }

   protected Pair<String, Typed<?>> fix(String var1, Typed<?> var2) {
      if (!var1.equals("minecraft:potion")) {
         return Pair.of(var1, var2);
      } else {
         String var3 = ((ItemIdFinder)this.itemIdFinder.get()).getItemId(var2);
         return "minecraft:lingering_potion".equals(var3) ? Pair.of("minecraft:lingering_potion", var2) : Pair.of("minecraft:splash_potion", var2);
      }
   }

   static record ItemIdFinder(OpticFinder<?> itemFinder, OpticFinder<Pair<String, String>> itemIdFinder) {
      ItemIdFinder(OpticFinder<?> var1, OpticFinder<Pair<String, String>> var2) {
         super();
         this.itemFinder = var1;
         this.itemIdFinder = var2;
      }

      public String getItemId(Typed<?> var1) {
         return (String)var1.getOptionalTyped(this.itemFinder).flatMap((var1x) -> var1x.getOptional(this.itemIdFinder)).map(Pair::getSecond).map(NamespacedSchema::ensureNamespaced).orElse("");
      }
   }
}
