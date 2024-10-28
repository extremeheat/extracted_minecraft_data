package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class FireResistantToDamageResistantComponentFix extends ItemStackComponentRemainderFix {
   public FireResistantToDamageResistantComponentFix(Schema var1) {
      super(var1, "FireResistantToDamageResistantComponentFix", "minecraft:fire_resistant", "minecraft:damage_resistant");
   }

   protected <T> Dynamic<T> fixComponent(Dynamic<T> var1) {
      return var1.emptyMap().set("types", var1.createString("#minecraft:is_fire"));
   }
}
