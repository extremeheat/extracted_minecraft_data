package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class EntityPaintingFieldsRenameFix extends NamedEntityFix {
   public EntityPaintingFieldsRenameFix(Schema var1) {
      super(var1, false, "EntityPaintingFieldsRenameFix", References.ENTITY, "minecraft:painting");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      return ExtraDataFixUtils.renameField(ExtraDataFixUtils.renameField(var1, "Motive", "variant"), "Facing", "facing");
   }

   @Override
   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
