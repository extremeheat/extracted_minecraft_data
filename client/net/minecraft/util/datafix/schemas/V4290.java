package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4290 extends NamespacedSchema {
   public V4290(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(true, References.TEXT_COMPONENT, () -> DSL.or(DSL.or(DSL.constType(DSL.string()), DSL.list(References.TEXT_COMPONENT.in(var1))), DSL.optionalFields("extra", DSL.list(References.TEXT_COMPONENT.in(var1)), "separator", References.TEXT_COMPONENT.in(var1), "hoverEvent", DSL.taggedChoice("action", DSL.string(), Map.of("show_text", DSL.optionalFields("contents", References.TEXT_COMPONENT.in(var1)), "show_item", DSL.optionalFields("contents", DSL.or(References.ITEM_STACK.in(var1), References.ITEM_NAME.in(var1))), "show_entity", DSL.optionalFields("type", References.ENTITY_NAME.in(var1), "name", References.TEXT_COMPONENT.in(var1)))))));
   }
}
