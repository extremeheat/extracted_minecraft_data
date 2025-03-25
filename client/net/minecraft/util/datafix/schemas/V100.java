package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V100 extends Schema {
   public V100(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(true, References.ENTITY_EQUIPMENT, () -> DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(References.ITEM_STACK.in(var1)))), new TypeTemplate[]{DSL.optional(DSL.field("HandItems", DSL.list(References.ITEM_STACK.in(var1)))), DSL.optional(DSL.field("body_armor_item", References.ITEM_STACK.in(var1))), DSL.optional(DSL.field("saddle", References.ITEM_STACK.in(var1)))}));
   }
}
