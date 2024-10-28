package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1470 extends NamespacedSchema {
   public V1470(int var1, Schema var2) {
      super(var1, var2);
   }

   protected static void registerMob(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return V100.equipment(var0);
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerEntities(Schema var1) {
      Map var2 = super.registerEntities(var1);
      registerMob(var1, var2, "minecraft:turtle");
      registerMob(var1, var2, "minecraft:cod_mob");
      registerMob(var1, var2, "minecraft:tropical_fish");
      registerMob(var1, var2, "minecraft:salmon_mob");
      registerMob(var1, var2, "minecraft:puffer_fish");
      registerMob(var1, var2, "minecraft:phantom");
      registerMob(var1, var2, "minecraft:dolphin");
      registerMob(var1, var2, "minecraft:drowned");
      var1.register(var2, "minecraft:trident", (var1x) -> {
         return DSL.optionalFields("inBlockState", References.BLOCK_STATE.in(var1), "Trident", References.ITEM_STACK.in(var1));
      });
      return var2;
   }
}
