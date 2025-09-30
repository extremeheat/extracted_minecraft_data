package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4302 extends NamespacedSchema {
   public V4302(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.registerSimple(var2, "minecraft:test_block");
      var1.register(var2, "minecraft:test_instance_block", () -> DSL.optionalFields("data", DSL.optionalFields("error_message", References.TEXT_COMPONENT.in(var1)), "errors", DSL.list(DSL.optionalFields("text", References.TEXT_COMPONENT.in(var1)))));
      return var2;
   }
}
