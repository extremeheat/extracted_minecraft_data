package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4301 extends NamespacedSchema {
   public V4301(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(true, References.ENTITY_EQUIPMENT, () -> DSL.optional(DSL.field("equipment", DSL.optionalFields(new Pair[]{Pair.of("mainhand", References.ITEM_STACK.in(var1)), Pair.of("offhand", References.ITEM_STACK.in(var1)), Pair.of("feet", References.ITEM_STACK.in(var1)), Pair.of("legs", References.ITEM_STACK.in(var1)), Pair.of("chest", References.ITEM_STACK.in(var1)), Pair.of("head", References.ITEM_STACK.in(var1)), Pair.of("body", References.ITEM_STACK.in(var1)), Pair.of("saddle", References.ITEM_STACK.in(var1))}))));
   }
}
