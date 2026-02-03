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
   public BoatSplitFix(final Schema outputSchema) {
      super(outputSchema, true);
   }

   private static boolean isNormalBoat(final String id) {
      return id.equals("minecraft:boat");
   }

   private static boolean isChestBoat(final String id) {
      return id.equals("minecraft:chest_boat");
   }

   private static boolean isAnyBoat(final String id) {
      return isNormalBoat(id) || isChestBoat(id);
   }

   private static String mapVariantToNormalBoat(final String id) {
      String var10000;
      switch (id) {
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

   private static String mapVariantToChestBoat(final String id) {
      String var10000;
      switch (id) {
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
      OpticFinder<String> idF = DSL.fieldFinder("id", NamespacedSchema.namespacedString());
      Type<?> oldType = this.getInputSchema().getType(References.ENTITY);
      Type<?> newType = this.getOutputSchema().getType(References.ENTITY);
      return this.fixTypeEverywhereTyped("BoatSplitFix", oldType, newType, (input) -> {
         Optional<String> id = input.getOptional(idF);
         if (id.isPresent() && isAnyBoat((String)id.get())) {
            Dynamic<?> tag = (Dynamic)input.getOrCreate(DSL.remainderFinder());
            Optional<String> maybeBoatId = tag.get("Type").asString().result();
            String newId;
            if (isChestBoat((String)id.get())) {
               newId = (String)maybeBoatId.map(BoatSplitFix::mapVariantToChestBoat).orElse("minecraft:oak_chest_boat");
            } else {
               newId = (String)maybeBoatId.map(BoatSplitFix::mapVariantToNormalBoat).orElse("minecraft:oak_boat");
            }

            return ExtraDataFixUtils.cast(newType, input).update(DSL.remainderFinder(), (remainder) -> remainder.remove("Type")).set(idF, newId);
         } else {
            return ExtraDataFixUtils.cast(newType, input);
         }
      });
   }
}
