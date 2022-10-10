package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;

public class ShulkerBoxItemColor extends DataFix {
   public static final String[] field_191278_a = new String[]{"minecraft:white_shulker_box", "minecraft:orange_shulker_box", "minecraft:magenta_shulker_box", "minecraft:light_blue_shulker_box", "minecraft:yellow_shulker_box", "minecraft:lime_shulker_box", "minecraft:pink_shulker_box", "minecraft:gray_shulker_box", "minecraft:silver_shulker_box", "minecraft:cyan_shulker_box", "minecraft:purple_shulker_box", "minecraft:blue_shulker_box", "minecraft:brown_shulker_box", "minecraft:green_shulker_box", "minecraft:red_shulker_box", "minecraft:black_shulker_box"};

   public ShulkerBoxItemColor(Schema var1, boolean var2) {
      super(var1, var2);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(TypeReferences.field_211295_k);
      OpticFinder var2 = DSL.fieldFinder("id", DSL.named(TypeReferences.field_211301_q.typeName(), DSL.namespacedString()));
      OpticFinder var3 = var1.findField("tag");
      OpticFinder var4 = var3.type().findField("BlockEntityTag");
      return this.fixTypeEverywhereTyped("ItemShulkerBoxColorFix", var1, (var3x) -> {
         Optional var4x = var3x.getOptional(var2);
         if (var4x.isPresent() && Objects.equals(((Pair)var4x.get()).getSecond(), "minecraft:shulker_box")) {
            Optional var5 = var3x.getOptionalTyped(var3);
            if (var5.isPresent()) {
               Typed var6 = (Typed)var5.get();
               Optional var7 = var6.getOptionalTyped(var4);
               if (var7.isPresent()) {
                  Typed var8 = (Typed)var7.get();
                  Dynamic var9 = (Dynamic)var8.get(DSL.remainderFinder());
                  int var10 = var9.getInt("Color");
                  var9.remove("Color");
                  return var3x.set(var3, var6.set(var4, var8.set(DSL.remainderFinder(), var9))).set(var2, Pair.of(TypeReferences.field_211301_q.typeName(), field_191278_a[var10 % 16]));
               }
            }
         }

         return var3x;
      });
   }
}
