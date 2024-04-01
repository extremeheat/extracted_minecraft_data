package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V3819 extends NamespacedSchema {
   public V3819(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.registerSimple(var2, "minecraft:big_brain");
      var1.registerSimple(var2, "minecraft:poisonous_potato_cutter");
      var1.registerSimple(var2, "minecraft:fletching");
      var1.registerSimple(var2, "minecraft:potato_refinery");
      return var2;
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      V1460.registerMob(var1, var2, "minecraft:batato");
      V1460.registerMob(var1, var2, "minecraft:toxifin");
      V1460.registerMob(var1, var2, "minecraft:plaguewhale");
      V1460.registerMob(var1, var2, "minecraft:poisonous_potato_zombie");
      V1460.registerMob(var1, var2, "minecraft:mega_spud");
      var1.registerSimple(var2, "minecraft:grid_carrier");
      var1.registerSimple(var2, "minecraft:vine_projectile");
      var1.registerSimple(var2, "minecraft:eye_of_potato");
      return var2;
   }
}
