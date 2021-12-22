package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class EntityArmorStandSilentFix extends NamedEntityFix {
   public EntityArmorStandSilentFix(Schema var1, boolean var2) {
      super(var1, var2, "EntityArmorStandSilentFix", References.ENTITY, "ArmorStand");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      return var1.get("Silent").asBoolean(false) && !var1.get("Marker").asBoolean(false) ? var1.remove("Silent") : var1;
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
