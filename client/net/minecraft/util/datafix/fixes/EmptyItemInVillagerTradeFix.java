package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class EmptyItemInVillagerTradeFix extends DataFix {
   public EmptyItemInVillagerTradeFix(Schema var1) {
      super(var1, false);
   }

   public TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.VILLAGER_TRADE);
      return this.writeFixAndRead("EmptyItemInVillagerTradeFix", var1, var1, (var0) -> {
         Dynamic var1 = var0.get("buyB").orElseEmptyMap();
         String var2 = NamespacedSchema.ensureNamespaced(var1.get("id").asString("minecraft:air"));
         int var3 = var1.get("count").asInt(0);
         return !var2.equals("minecraft:air") && var3 != 0 ? var0 : var0.remove("buyB");
      });
   }
}
