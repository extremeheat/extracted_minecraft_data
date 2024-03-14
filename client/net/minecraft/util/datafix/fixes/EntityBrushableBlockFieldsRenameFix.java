package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class EntityBrushableBlockFieldsRenameFix extends NamedEntityFix {
   public EntityBrushableBlockFieldsRenameFix(Schema var1) {
      super(var1, false, "EntityBrushableBlockFieldsRenameFix", References.BLOCK_ENTITY, "minecraft:brushable_block");
   }

   public Dynamic<?> fixTag(Dynamic<?> var1) {
      return ExtraDataFixUtils.renameField(ExtraDataFixUtils.renameField(var1, "loot_table", "LootTable"), "loot_table_seed", "LootTableSeed");
   }

   @Override
   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), this::fixTag);
   }
}
