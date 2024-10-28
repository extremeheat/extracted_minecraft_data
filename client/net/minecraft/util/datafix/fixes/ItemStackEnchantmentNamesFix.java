package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Objects;
import java.util.Optional;

public class ItemStackEnchantmentNamesFix extends DataFix {
   private static final Int2ObjectMap<String> MAP = (Int2ObjectMap)DataFixUtils.make(new Int2ObjectOpenHashMap(), (var0) -> {
      var0.put(0, "minecraft:protection");
      var0.put(1, "minecraft:fire_protection");
      var0.put(2, "minecraft:feather_falling");
      var0.put(3, "minecraft:blast_protection");
      var0.put(4, "minecraft:projectile_protection");
      var0.put(5, "minecraft:respiration");
      var0.put(6, "minecraft:aqua_affinity");
      var0.put(7, "minecraft:thorns");
      var0.put(8, "minecraft:depth_strider");
      var0.put(9, "minecraft:frost_walker");
      var0.put(10, "minecraft:binding_curse");
      var0.put(16, "minecraft:sharpness");
      var0.put(17, "minecraft:smite");
      var0.put(18, "minecraft:bane_of_arthropods");
      var0.put(19, "minecraft:knockback");
      var0.put(20, "minecraft:fire_aspect");
      var0.put(21, "minecraft:looting");
      var0.put(22, "minecraft:sweeping");
      var0.put(32, "minecraft:efficiency");
      var0.put(33, "minecraft:silk_touch");
      var0.put(34, "minecraft:unbreaking");
      var0.put(35, "minecraft:fortune");
      var0.put(48, "minecraft:power");
      var0.put(49, "minecraft:punch");
      var0.put(50, "minecraft:flame");
      var0.put(51, "minecraft:infinity");
      var0.put(61, "minecraft:luck_of_the_sea");
      var0.put(62, "minecraft:lure");
      var0.put(65, "minecraft:loyalty");
      var0.put(66, "minecraft:impaling");
      var0.put(67, "minecraft:riptide");
      var0.put(68, "minecraft:channeling");
      var0.put(70, "minecraft:mending");
      var0.put(71, "minecraft:vanishing_curse");
   });

   public ItemStackEnchantmentNamesFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("ItemStackEnchantmentFix", var1, (var2x) -> {
         return var2x.updateTyped(var2, (var1) -> {
            return var1.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }

   private Dynamic<?> fixTag(Dynamic<?> var1) {
      DataResult var10000 = var1.get("ench").asStreamOpt().map((var0) -> {
         return var0.map((var0x) -> {
            return var0x.set("id", var0x.createString((String)MAP.getOrDefault(var0x.get("id").asInt(0), "null")));
         });
      });
      Objects.requireNonNull(var1);
      Optional var2 = var10000.map(var1::createList).result();
      if (var2.isPresent()) {
         var1 = var1.remove("ench").set("Enchantments", (Dynamic)var2.get());
      }

      return var1.update("StoredEnchantments", (var0) -> {
         DataResult var10000 = var0.asStreamOpt().map((var0x) -> {
            return var0x.map((var0) -> {
               return var0.set("id", var0.createString((String)MAP.getOrDefault(var0.get("id").asInt(0), "null")));
            });
         });
         Objects.requireNonNull(var0);
         return (Dynamic)DataFixUtils.orElse(var10000.map(var0::createList).result(), var0);
      });
   }
}
