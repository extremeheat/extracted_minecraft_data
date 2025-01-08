package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;

public class DropChancesFormatFix extends DataFix {
   private static final List<String> ARMOR_SLOT_NAMES = List.of("feet", "legs", "chest", "head");
   private static final List<String> HAND_SLOT_NAMES = List.of("mainhand", "offhand");
   private static final float DEFAULT_CHANCE = 0.085F;

   public DropChancesFormatFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("DropChancesFormatFix", this.getInputSchema().getType(References.ENTITY), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> {
            List var1 = parseDropChances(var0x.get("ArmorDropChances"));
            List var2 = parseDropChances(var0x.get("HandDropChances"));
            float var3 = (Float)var0x.get("body_armor_drop_chance").asNumber().result().map(Number::floatValue).orElse(0.085F);
            var0x = var0x.remove("ArmorDropChances").remove("HandDropChances").remove("body_armor_drop_chance");
            Dynamic var4 = var0x.emptyMap();
            var4 = addSlotChances(var4, var1, ARMOR_SLOT_NAMES);
            var4 = addSlotChances(var4, var2, HAND_SLOT_NAMES);
            if (var3 != 0.085F) {
               var4 = var4.set("body", var0x.createFloat(var3));
            }

            return !var4.equals(var0x.emptyMap()) ? var0x.set("drop_chances", var4) : var0x;
         }));
   }

   private static Dynamic<?> addSlotChances(Dynamic<?> var0, List<Float> var1, List<String> var2) {
      for(int var3 = 0; var3 < var2.size() && var3 < var1.size(); ++var3) {
         String var4 = (String)var2.get(var3);
         float var5 = (Float)var1.get(var3);
         if (var5 != 0.085F) {
            var0 = var0.set(var4, var0.createFloat(var5));
         }
      }

      return var0;
   }

   private static List<Float> parseDropChances(OptionalDynamic<?> var0) {
      return var0.asStream().map((var0x) -> var0x.asFloat(0.085F)).toList();
   }
}
