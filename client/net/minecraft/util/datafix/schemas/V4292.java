package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4292 extends NamespacedSchema {
   public V4292(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(true, References.TEXT_COMPONENT, () -> DSL.or(DSL.or(DSL.constType(DSL.string()), DSL.list(References.TEXT_COMPONENT.in(var1))), DSL.optionalFields("extra", DSL.list(References.TEXT_COMPONENT.in(var1)), "separator", References.TEXT_COMPONENT.in(var1), "hover_event", DSL.taggedChoice("action", DSL.string(), Map.of("show_text", DSL.optionalFields("value", References.TEXT_COMPONENT.in(var1)), "show_item", References.ITEM_STACK.in(var1), "show_entity", DSL.optionalFields("id", References.ENTITY_NAME.in(var1), "name", References.TEXT_COMPONENT.in(var1)))))));
   }
}
