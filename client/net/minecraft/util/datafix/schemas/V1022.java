package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1022 extends Schema {
   public V1022(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      var1.registerType(false, References.RECIPE, () -> {
         return DSL.constType(NamespacedSchema.namespacedString());
      });
      var1.registerType(false, References.PLAYER, () -> {
         return DSL.optionalFields(new Pair[]{Pair.of("RootVehicle", DSL.optionalFields("Entity", References.ENTITY_TREE.in(var1))), Pair.of("Inventory", DSL.list(References.ITEM_STACK.in(var1))), Pair.of("EnderItems", DSL.list(References.ITEM_STACK.in(var1))), Pair.of("ShoulderEntityLeft", References.ENTITY_TREE.in(var1)), Pair.of("ShoulderEntityRight", References.ENTITY_TREE.in(var1)), Pair.of("recipeBook", DSL.optionalFields("recipes", DSL.list(References.RECIPE.in(var1)), "toBeDisplayed", DSL.list(References.RECIPE.in(var1))))});
      });
      var1.registerType(false, References.HOTBAR, () -> {
         return DSL.compoundList(DSL.list(References.ITEM_STACK.in(var1)));
      });
   }
}
