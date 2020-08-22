package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.util.datafix.fixes.References;

public class V1125 extends NamespacedSchema {
   public V1125(int var1, Schema var2) {
      super(var1, var2);
   }

   public Map registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      var1.registerSimple(var2, "minecraft:bed");
      return var2;
   }

   public void registerTypes(Schema var1, Map var2, Map var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, References.ADVANCEMENTS, () -> {
         return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(References.BIOME.in(var1), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.string()))));
      });
      var1.registerType(false, References.BIOME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      var1.registerType(false, References.ENTITY_NAME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
   }
}
