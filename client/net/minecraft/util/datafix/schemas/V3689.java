package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V3689 extends NamespacedSchema {
   public V3689(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var1.register(var2, "minecraft:breeze", () -> {
         return V100.equipment(var1);
      });
      var1.registerSimple(var2, "minecraft:wind_charge");
      var1.registerSimple(var2, "minecraft:breeze_wind_charge");
      return var2;
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.register(var2, "minecraft:trial_spawner", () -> {
         return DSL.optionalFields("spawn_potentials", DSL.list(DSL.fields("data", DSL.fields("entity", References.ENTITY_TREE.in(var1)))), "spawn_data", DSL.fields("entity", References.ENTITY_TREE.in(var1)));
      });
      return var2;
   }
}
