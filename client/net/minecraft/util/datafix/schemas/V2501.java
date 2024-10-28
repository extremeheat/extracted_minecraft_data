package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V2501 extends NamespacedSchema {
   public V2501(int var1, Schema var2) {
      super(var1, var2);
   }

   private static void registerFurnace(Schema var0, Map<String, Supplier<TypeTemplate>> var1, String var2) {
      var0.register(var1, var2, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(var0)), "RecipesUsed", DSL.compoundList(References.RECIPE.in(var0), DSL.constType(DSL.intType())));
      });
   }

   public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema var1) {
      Map var2 = super.registerBlockEntities(var1);
      registerFurnace(var1, var2, "minecraft:furnace");
      registerFurnace(var1, var2, "minecraft:smoker");
      registerFurnace(var1, var2, "minecraft:blast_furnace");
      return var2;
   }
}
