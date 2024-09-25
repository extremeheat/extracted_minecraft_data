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
      return switch (var0) {
         case "spruce" -> "minecraft:spruce_boat";
         case "birch" -> "minecraft:birch_boat";
         case "jungle" -> "minecraft:jungle_boat";
         case "acacia" -> "minecraft:acacia_boat";
         case "cherry" -> "minecraft:cherry_boat";
         case "dark_oak" -> "minecraft:dark_oak_boat";
         case "mangrove" -> "minecraft:mangrove_boat";
         case "bamboo" -> "minecraft:bamboo_raft";
         default -> "minecraft:oak_boat";
      };
   }

   private static String mapVariantToChestBoat(String var0) {
      return switch (var0) {
         case "spruce" -> "minecraft:spruce_chest_boat";
         case "birch" -> "minecraft:birch_chest_boat";
         case "jungle" -> "minecraft:jungle_chest_boat";
         case "acacia" -> "minecraft:acacia_chest_boat";
         case "cherry" -> "minecraft:cherry_chest_boat";
         case "dark_oak" -> "minecraft:dark_oak_chest_boat";
         case "mangrove" -> "minecraft:mangrove_chest_boat";
         case "bamboo" -> "minecraft:bamboo_chest_raft";
         default -> "minecraft:oak_chest_boat";
      };
   }

   public TypeRewriteRule makeRule() {
      OpticFinder var1 = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      Type var2 = this.getInputSchema().getType(References.ENTITY);
      Type var3 = this.getOutputSchema().getType(References.ENTITY);
      return this.fixTypeEverywhereTyped("BoatSplitFix", var2, var3, var2x -> {
         Optional var3x = var2x.getOptional(var1);
         if (var3x.isPresent() && isAnyBoat((String)var3x.get())) {
            Dynamic var4 = (Dynamic)var2x.getOrCreate(DSL.remainderFinder());
            Optional var5 = var4.get("Type").asString().result();
            String var6;
            if (isChestBoat((String)var3x.get())) {
               var6 = var5.map(BoatSplitFix::mapVariantToChestBoat).orElse("minecraft:oak_chest_boat");
            } else {
               var6 = var5.map(BoatSplitFix::mapVariantToNormalBoat).orElse("minecraft:oak_boat");
            }

            return ExtraDataFixUtils.cast(var3, var2x).update(DSL.remainderFinder(), var0x -> var0x.remove("Type")).set(var1, var6);
         } else {
            return ExtraDataFixUtils.cast(var3, var2x);
         }
      });
   }
}
