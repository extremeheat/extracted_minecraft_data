package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.SequencedMap;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V4059 extends NamespacedSchema {
   public V4059(int var1, Schema var2) {
      super(var1, var2);
   }

   public static SequencedMap<String, Supplier<TypeTemplate>> components(Schema var0) {
      SequencedMap var1 = V3818_3.components(var0);
      var1.remove("minecraft:food");
      var1.put("minecraft:use_remainder", () -> {
         return References.ITEM_STACK.in(var0);
      });
      var1.put("minecraft:equippable", () -> {
         return DSL.optionalFields("allowed_entities", DSL.or(References.ENTITY_NAME.in(var0), DSL.list(References.ENTITY_NAME.in(var0))));
      });
      return var1;
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(true, References.DATA_COMPONENTS, () -> {
         return DSL.optionalFieldsLazy(components(var1));
      });
   }
}
