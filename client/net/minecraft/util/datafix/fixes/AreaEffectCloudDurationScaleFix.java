package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class AreaEffectCloudDurationScaleFix extends NamedEntityFix {
   public AreaEffectCloudDurationScaleFix(Schema var1) {
      super(var1, false, "AreaEffectCloudDurationScaleFix", References.ENTITY, "minecraft:area_effect_cloud");
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var0) -> var0.set("potion_duration_scale", var0.createFloat(0.25F)));
   }
}
