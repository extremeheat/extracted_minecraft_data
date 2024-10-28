package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SequencedMap;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V3818_3 extends NamespacedSchema {
   public V3818_3(int var1, Schema var2) {
      super(var1, var2);
   }

   public static SequencedMap<String, Supplier<TypeTemplate>> components(Schema var0) {
      LinkedHashMap var1 = new LinkedHashMap();
      var1.put("minecraft:bees", () -> {
         return DSL.list(DSL.optionalFields("entity_data", References.ENTITY_TREE.in(var0)));
      });
      var1.put("minecraft:block_entity_data", () -> {
         return References.BLOCK_ENTITY.in(var0);
      });
      var1.put("minecraft:bundle_contents", () -> {
         return DSL.list(References.ITEM_STACK.in(var0));
      });
      var1.put("minecraft:can_break", () -> {
         return DSL.optionalFields("predicates", DSL.list(DSL.optionalFields("blocks", DSL.or(References.BLOCK_NAME.in(var0), DSL.list(References.BLOCK_NAME.in(var0))))));
      });
      var1.put("minecraft:can_place_on", () -> {
         return DSL.optionalFields("predicates", DSL.list(DSL.optionalFields("blocks", DSL.or(References.BLOCK_NAME.in(var0), DSL.list(References.BLOCK_NAME.in(var0))))));
      });
      var1.put("minecraft:charged_projectiles", () -> {
         return DSL.list(References.ITEM_STACK.in(var0));
      });
      var1.put("minecraft:container", () -> {
         return DSL.list(DSL.optionalFields("item", References.ITEM_STACK.in(var0)));
      });
      var1.put("minecraft:entity_data", () -> {
         return References.ENTITY_TREE.in(var0);
      });
      var1.put("minecraft:pot_decorations", () -> {
         return DSL.list(References.ITEM_NAME.in(var0));
      });
      var1.put("minecraft:food", () -> {
         return DSL.optionalFields("using_converts_to", References.ITEM_STACK.in(var0));
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
