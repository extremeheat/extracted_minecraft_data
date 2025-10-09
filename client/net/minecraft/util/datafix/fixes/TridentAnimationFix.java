package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;

public class TridentAnimationFix extends DataComponentRemainderFix {
   public TridentAnimationFix(Schema var1) {
      super(var1, "TridentAnimationFix", "minecraft:consumable");
   }

   @Nullable
   protected <T> Dynamic<T> fixComponent(Dynamic<T> var1) {
      return var1.update("animation", (var0) -> {
         String var1 = (String)var0.asString().result().orElse("");
         return "spear".equals(var1) ? var0.createString("trident") : var0;
      });
   }
}
