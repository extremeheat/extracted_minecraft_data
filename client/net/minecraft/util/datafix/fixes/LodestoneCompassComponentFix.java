package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class LodestoneCompassComponentFix extends ItemStackComponentRemainderFix {
   public LodestoneCompassComponentFix(Schema var1) {
      super(var1, "LodestoneCompassComponentFix", "minecraft:lodestone_target", "minecraft:lodestone_tracker");
   }

   protected <T> Dynamic<T> fixComponent(Dynamic<T> var1) {
      Optional var2 = var1.get("pos").result();
      Optional var3 = var1.get("dimension").result();
      var1 = var1.remove("pos").remove("dimension");
      if (var2.isPresent() && var3.isPresent()) {
         var1 = var1.set("target", var1.emptyMap().set("pos", (Dynamic)var2.get()).set("dimension", (Dynamic)var3.get()));
      }

      return var1;
   }
}
