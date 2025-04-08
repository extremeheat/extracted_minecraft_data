package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V3439 extends NamespacedSchema {
   public V3439(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      this.register(var2, "minecraft:sign", () -> sign(var1));
      return var2;
   }

   public static TypeTemplate sign(Schema var0) {
      return DSL.optionalFields("front_text", DSL.optionalFields("messages", DSL.list(References.TEXT_COMPONENT.in(var0)), "filtered_messages", DSL.list(References.TEXT_COMPONENT.in(var0))), "back_text", DSL.optionalFields("messages", DSL.list(References.TEXT_COMPONENT.in(var0)), "filtered_messages", DSL.list(References.TEXT_COMPONENT.in(var0))));
   }
}
