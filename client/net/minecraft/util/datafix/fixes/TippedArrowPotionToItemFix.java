package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class TippedArrowPotionToItemFix extends NamedEntityWriteReadFix {
   public TippedArrowPotionToItemFix(Schema var1) {
      super(var1, false, "TippedArrowPotionToItemFix", References.ENTITY, "minecraft:arrow");
   }

   @Override
   protected <T> Dynamic<T> fix(Dynamic<T> var1) {
      Optional var2 = var1.get("Potion").result();
      Optional var3 = var1.get("custom_potion_effects").result();
      Optional var4 = var1.get("Color").result();
      return var2.isEmpty() && var3.isEmpty() && var4.isEmpty()
         ? var1
         : var1.remove("Potion").remove("custom_potion_effects").remove("Color").update("item", var3x -> {
            Dynamic var4xx = var3x.get("tag").orElseEmptyMap();
            if (var2.isPresent()) {
               var4xx = var4xx.set("Potion", (Dynamic)var2.get());
            }
   
            if (var3.isPresent()) {
               var4xx = var4xx.set("custom_potion_effects", (Dynamic)var3.get());
            }
   
            if (var4.isPresent()) {
               var4xx = var4xx.set("CustomPotionColor", (Dynamic)var4.get());
            }
   
            return var3x.set("tag", var4xx);
         });
   }
}
