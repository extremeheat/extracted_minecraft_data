package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V3818_3 extends NamespacedSchema {
   public V3818_3(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(true, References.DATA_COMPONENTS, () -> {
         return DSL.optionalFields(new Pair[]{Pair.of("minecraft:bees", DSL.list(DSL.optionalFields("entity_data", References.ENTITY_TREE.in(var1)))), Pair.of("minecraft:block_entity_data", References.BLOCK_ENTITY.in(var1)), Pair.of("minecraft:bundle_contents", DSL.list(References.ITEM_STACK.in(var1))), Pair.of("minecraft:can_break", DSL.optionalFields("predicates", DSL.list(DSL.optionalFields("blocks", DSL.or(References.BLOCK_NAME.in(var1), DSL.list(References.BLOCK_NAME.in(var1))))))), Pair.of("minecraft:can_place_on", DSL.optionalFields("predicates", DSL.list(DSL.optionalFields("blocks", DSL.or(References.BLOCK_NAME.in(var1), DSL.list(References.BLOCK_NAME.in(var1))))))), Pair.of("minecraft:charged_projectiles", DSL.list(References.ITEM_STACK.in(var1))), Pair.of("minecraft:container", DSL.list(DSL.optionalFields("item", References.ITEM_STACK.in(var1)))), Pair.of("minecraft:entity_data", References.ENTITY_TREE.in(var1)), Pair.of("minecraft:pot_decorations", DSL.list(References.ITEM_NAME.in(var1))), Pair.of("minecraft:food", DSL.optionalFields("using_converts_to", References.ITEM_STACK.in(var1)))});
      });
   }
}
