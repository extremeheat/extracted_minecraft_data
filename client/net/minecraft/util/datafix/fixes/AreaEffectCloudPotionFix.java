package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class AreaEffectCloudPotionFix extends NamedEntityFix {
   public AreaEffectCloudPotionFix(Schema var1) {
      super(var1, false, "AreaEffectCloudPotionFix", References.ENTITY, "minecraft:area_effect_cloud");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fix);
   }

   private <T> Dynamic<T> fix(Dynamic<T> var1) {
      Optional var2 = var1.get("Color").result();
      Optional var3 = var1.get("effects").result();
      Optional var4 = var1.get("Potion").result();
      var1 = var1.remove("Color").remove("effects").remove("Potion");
      if (var2.isEmpty() && var3.isEmpty() && var4.isEmpty()) {
         return var1;
      } else {
         Dynamic var5 = var1.emptyMap();
         if (var2.isPresent()) {
            var5 = var5.set("custom_color", (Dynamic)var2.get());
         }

         if (var3.isPresent()) {
            var5 = var5.set("custom_effects", (Dynamic)var3.get());
         }

         if (var4.isPresent()) {
            var5 = var5.set("potion", (Dynamic)var4.get());
         }

         return var1.set("potion_contents", var5);
      }
   }
}
