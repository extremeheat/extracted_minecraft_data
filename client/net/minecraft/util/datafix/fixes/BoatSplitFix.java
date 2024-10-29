package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.ExtraDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class BoatSplitFix extends DataFix {
   public BoatSplitFix(Schema var1) {
      super(var1, true);
   }

   private static boolean isNormalBoat(String var0) {
      return var0.equals("minecraft:boat");
   }

   private static boolean isChestBoat(String var0) {
      return var0.equals("minecraft:chest_boat");
   }

   private static boolean isAnyBoat(String var0) {
      return isNormalBoat(var0) || isChestBoat(var0);
   }

   private static String mapVariantToNormalBoat(String var0) {
      String var10000;
      switch (var0) {
         case "spruce" -> var10000 = "minecraft:spruce_boat";
         case "birch" -> var10000 = "minecraft:birch_boat";
         case "jungle" -> var10000 = "minecraft:jungle_boat";
         case "acacia" -> var10000 = "minecraft:acacia_boat";
         case "cherry" -> var10000 = "minecraft:cherry_boat";
         case "dark_oak" -> var10000 = "minecraft:dark_oak_boat";
         case "mangrove" -> var10000 = "minecraft:mangrove_boat";
         case "bamboo" -> var10000 = "minecraft:bamboo_raft";
         default -> var10000 = "minecraft:oak_boat";
      }

      return var10000;
   }

   private static String mapVariantToChestBoat(String var0) {
      String var10000;
      switch (var0) {
         case "spruce" -> var10000 = "minecraft:spruce_chest_boat";
         case "birch" -> var10000 = "minecraft:birch_chest_boat";
         case "jungle" -> var10000 = "minecraft:jungle_chest_boat";
         case "acacia" -> var10000 = "minecraft:acacia_chest_boat";
         case "cherry" -> var10000 = "minecraft:cherry_chest_boat";
         case "dark_oak" -> var10000 = "minecraft:dark_oak_chest_boat";
         case "mangrove" -> var10000 = "minecraft:mangrove_chest_boat";
         case "bamboo" -> var10000 = "minecraft:bamboo_chest_raft";
         default -> var10000 = "minecraft:oak_chest_boat";
      }

      return var10000;
   }

   public TypeRewriteRule makeRule() {
      OpticFinder var1 = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      Type var2 = this.getInputSchema().getType(References.ENTITY);
      Type var3 = this.getOutputSchema().getType(References.ENTITY);
      return this.fixTypeEverywhereTyped("BoatSplitFix", var2, var3, (var2x) -> {
         Optional var3x = var2x.getOptional(var1);
         if (var3x.isPresent() && isAnyBoat((String)var3x.get())) {
            Dynamic var4 = (Dynamic)var2x.getOrCreate(DSL.remainderFinder());
            Optional var5 = var4.get("Type").asString().result();
            String var6;
            if (isChestBoat((String)var3x.get())) {
               var6 = (String)var5.map(BoatSplitFix::mapVariantToChestBoat).orElse("minecraft:oak_chest_boat");
            } else {
               var6 = (String)var5.map(BoatSplitFix::mapVariantToNormalBoat).orElse("minecraft:oak_boat");
            }

            return ExtraDataFixUtils.cast(var3, var2x).update(DSL.remainderFinder(), (var0) -> {
               return var0.remove("Type");
            }).set(var1, var6);
         } else {
            return ExtraDataFixUtils.cast(var3, var2x);
         }
      });
   }
}
