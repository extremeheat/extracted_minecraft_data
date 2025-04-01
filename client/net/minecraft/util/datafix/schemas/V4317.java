package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V4317 extends NamespacedSchema {
   public V4317(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.registerSimple(var2, "minecraft:mine_crafter");
      var1.registerSimple(var2, "minecraft:mine_travelling_block");
      var1.registerSimple(var2, "minecraft:mine_revisitor");
      var1.registerSimple(var2, "minecraft:mob_trophy");
      return var2;
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var1.registerSimple(var2, "minecraft:angry_ghast");
      var1.registerSimple(var2, "minecraft:pet_armadillo");
      var1.registerSimple(var2, "minecraft:pet_axolotl");
      var1.registerSimple(var2, "minecraft:pet_bee");
      var1.registerSimple(var2, "minecraft:pet_cat");
      var1.registerSimple(var2, "minecraft:pet_chicken");
      var1.registerSimple(var2, "minecraft:pet_cow");
      var1.registerSimple(var2, "minecraft:pet_creeper");
      var1.registerSimple(var2, "minecraft:pet_fox");
      var1.registerSimple(var2, "minecraft:pet_frog");
      var1.registerSimple(var2, "minecraft:pet_slime");
      var1.registerSimple(var2, "minecraft:pet_turtle");
      var1.registerSimple(var2, "minecraft:pet_wolf");
      var1.registerSimple(var2, "minecraft:pet_polar_bear");
      var1.registerSimple(var2, "minecraft:pet_mooshroom");
      return var2;
   }
}
