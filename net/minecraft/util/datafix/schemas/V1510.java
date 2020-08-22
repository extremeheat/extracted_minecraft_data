package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import java.util.Map;

public class V1510 extends NamespacedSchema {
   public V1510(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      var2.put("minecraft:command_block_minecart", var2.remove("minecraft:commandblock_minecart"));
      var2.put("minecraft:end_crystal", var2.remove("minecraft:ender_crystal"));
      var2.put("minecraft:snow_golem", var2.remove("minecraft:snowman"));
      var2.put("minecraft:evoker", var2.remove("minecraft:evocation_illager"));
      var2.put("minecraft:evoker_fangs", var2.remove("minecraft:evocation_fangs"));
      var2.put("minecraft:illusioner", var2.remove("minecraft:illusion_illager"));
      var2.put("minecraft:vindicator", var2.remove("minecraft:vindication_illager"));
      var2.put("minecraft:iron_golem", var2.remove("minecraft:villager_golem"));
      var2.put("minecraft:experience_orb", var2.remove("minecraft:xp_orb"));
      var2.put("minecraft:experience_bottle", var2.remove("minecraft:xp_bottle"));
      var2.put("minecraft:eye_of_ender", var2.remove("minecraft:eye_of_ender_signal"));
      var2.put("minecraft:firework_rocket", var2.remove("minecraft:fireworks_rocket"));
      return var2;
   }
}
