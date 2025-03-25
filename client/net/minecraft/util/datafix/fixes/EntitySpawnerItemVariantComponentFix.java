package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.util.datafix.ExtraDataFixUtils;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EntitySpawnerItemVariantComponentFix extends DataFix {
   public EntitySpawnerItemVariantComponentFix(Schema var1) {
      super(var1, false);
   }

   public final TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder var3 = var1.findField("components");
      return this.fixTypeEverywhereTyped("ItemStack bucket_entity_data variants to separate components", var1, (var2x) -> {
         Typed var10000;
         switch ((String)var2x.getOptional(var2).map(Pair::getSecond).orElse("")) {
            case "minecraft:salmon_bucket" -> var10000 = var2x.updateTyped(var3, EntitySpawnerItemVariantComponentFix::fixSalmonBucket);
            case "minecraft:axolotl_bucket" -> var10000 = var2x.updateTyped(var3, EntitySpawnerItemVariantComponentFix::fixAxolotlBucket);
            case "minecraft:tropical_fish_bucket" -> var10000 = var2x.updateTyped(var3, EntitySpawnerItemVariantComponentFix::fixTropicalFishBucket);
            case "minecraft:painting" -> var10000 = var2x.updateTyped(var3, (var0) -> Util.writeAndReadTypedOrThrow(var0, var0.getType(), EntitySpawnerItemVariantComponentFix::fixPainting));
            default -> var10000 = var2x;
         }

         return var10000;
      });
   }

   private static String getBaseColor(int var0) {
      return ExtraDataFixUtils.dyeColorIdToName(var0 >> 16 & 255);
   }

   private static String getPatternColor(int var0) {
      return ExtraDataFixUtils.dyeColorIdToName(var0 >> 24 & 255);
   }

   private static String getPattern(int var0) {
      String var10000;
      switch (var0 & 65535) {
         case 1 -> var10000 = "flopper";
         case 256 -> var10000 = "sunstreak";
         case 257 -> var10000 = "stripey";
         case 512 -> var10000 = "snooper";
         case 513 -> var10000 = "glitter";
         case 768 -> var10000 = "dasher";
         case 769 -> var10000 = "blockfish";
         case 1024 -> var10000 = "brinely";
         case 1025 -> var10000 = "betty";
         case 1280 -> var10000 = "spotty";
         case 1281 -> var10000 = "clayfish";
         default -> var10000 = "kob";
      }

      return var10000;
   }

   private static <T> Dynamic<T> fixTropicalFishBucket(Dynamic<T> var0, Dynamic<T> var1) {
      Optional var2 = var1.get("BucketVariantTag").asNumber().result();
      if (var2.isEmpty()) {
         return var0;
      } else {
         int var3 = ((Number)var2.get()).intValue();
         String var4 = getPattern(var3);
         String var5 = getBaseColor(var3);
         String var6 = getPatternColor(var3);
         return var0.update("minecraft:bucket_entity_data", (var0x) -> var0x.remove("BucketVariantTag")).set("minecraft:tropical_fish/pattern", var0.createString(var4)).set("minecraft:tropical_fish/base_color", var0.createString(var5)).set("minecraft:tropical_fish/pattern_color", var0.createString(var6));
      }
   }

   private static <T> Dynamic<T> fixAxolotlBucket(Dynamic<T> var0, Dynamic<T> var1) {
      Optional var2 = var1.get("Variant").asNumber().result();
      if (var2.isEmpty()) {
         return var0;
      } else {
         String var10000;
         switch (((Number)var2.get()).intValue()) {
            case 1 -> var10000 = "wild";
            case 2 -> var10000 = "gold";
            case 3 -> var10000 = "cyan";
            case 4 -> var10000 = "blue";
            default -> var10000 = "lucy";
         }

         String var3 = var10000;
         return var0.update("minecraft:bucket_entity_data", (var0x) -> var0x.remove("Variant")).set("minecraft:axolotl/variant", var0.createString(var3));
      }
   }

   private static <T> Dynamic<T> fixSalmonBucket(Dynamic<T> var0, Dynamic<T> var1) {
      Optional var2 = var1.get("type").result();
      return var2.isEmpty() ? var0 : var0.update("minecraft:bucket_entity_data", (var0x) -> var0x.remove("type")).set("minecraft:salmon/size", (Dynamic)var2.get());
   }

   private static <T> Dynamic<T> fixPainting(Dynamic<T> var0) {
      Optional var1 = var0.get("minecraft:entity_data").result();
      if (var1.isEmpty()) {
         return var0;
      } else if (((Dynamic)var1.get()).get("id").asString().result().filter((var0x) -> var0x.equals("minecraft:painting")).isEmpty()) {
         return var0;
      } else {
         Optional var2 = ((Dynamic)var1.get()).get("variant").result();
         Dynamic var3 = ((Dynamic)var1.get()).remove("variant");
         if (var3.remove("id").equals(var3.emptyMap())) {
            var0 = var0.remove("minecraft:entity_data");
         } else {
            var0 = var0.set("minecraft:entity_data", var3);
         }

         if (var2.isPresent()) {
            var0 = var0.set("minecraft:painting/variant", (Dynamic)var2.get());
         }

         return var0;
      }
   }

   @FunctionalInterface
   interface Fixer extends Function<Typed<?>, Typed<?>> {
      default Typed<?> apply(Typed<?> var1) {
         return var1.update(DSL.remainderFinder(), this::fixRemainder);
      }

      default <T> Dynamic<T> fixRemainder(Dynamic<T> var1) {
         return (Dynamic)var1.get("minecraft:bucket_entity_data").result().map((var2) -> this.fixRemainder(var1, var2)).orElse(var1);
      }

      <T> Dynamic<T> fixRemainder(Dynamic<T> var1, Dynamic<T> var2);
   }
}
