package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V5000 extends NamespacedSchema {
   public V5000(final int versionKey, final Schema parent) {
      super(versionKey, parent);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
      schema.registerSimple(map, "minecraft:living_block");
      schema.registerSimple(map, "minecraft:living_block_command");
      schema.registerSimple(map, "minecraft:crafting_grid");
      schema.registerSimple(map, "minecraft:hovering_item");
      return map;
   }
}
