package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class HorseBodyArmorItemFix extends NamedEntityWriteReadFix {
   private final String previousBodyArmorTag;

   public HorseBodyArmorItemFix(Schema var1, String var2, String var3) {
      super(var1, true, "Horse armor fix for " + var2, References.ENTITY, var2);
      this.previousBodyArmorTag = var3;
   }

   protected <T> Dynamic<T> fix(Dynamic<T> var1) {
      Optional var2 = var1.get(this.previousBodyArmorTag).result();
      if (var2.isPresent()) {
         Dynamic var3 = (Dynamic)var2.get();
         Dynamic var4 = var1.remove(this.previousBodyArmorTag);
         var4 = var4.set("body_armor_item", var3);
         var4 = var4.set("body_armor_drop_chance", var1.createFloat(2.0F));
         return var4;
      } else {
         return var1;
      }
   }
}
