package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V1451_6 extends NamespacedSchema {
   public V1451_6(int var1, Schema var2) {
      super(var1, var2);
   }

   public void registerTypes(Schema var1, Map<String, Supplier<TypeTemplate>> var2, Map<String, Supplier<TypeTemplate>> var3) {
      super.registerTypes(var1, var2, var3);
      Supplier var4 = () -> {
         return DSL.compoundList(References.ITEM_NAME.in(var1), DSL.constType(DSL.intType()));
      };
      var1.registerType(false, References.STATS, () -> {
         return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(References.BLOCK_NAME.in(var1), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate)var4.get(), "minecraft:used", (TypeTemplate)var4.get(), "minecraft:broken", (TypeTemplate)var4.get(), "minecraft:picked_up", (TypeTemplate)var4.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate)var4.get(), "minecraft:killed", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(References.ENTITY_NAME.in(var1), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType())))));
      });
   }
}
