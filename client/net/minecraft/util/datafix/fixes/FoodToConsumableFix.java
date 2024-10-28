package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;

public class FoodToConsumableFix extends DataFix {
   public FoodToConsumableFix(Schema var1) {
      super(var1, true);
   }

   protected TypeRewriteRule makeRule() {
      return this.writeFixAndRead("Food to consumable fix", this.getInputSchema().getType(References.DATA_COMPONENTS), this.getOutputSchema().getType(References.DATA_COMPONENTS), (var0) -> {
         Optional var1 = var0.get("minecraft:food").result();
         if (var1.isPresent()) {
            float var2 = ((Dynamic)var1.get()).get("eat_seconds").asFloat(1.6F);
            Stream var3 = ((Dynamic)var1.get()).get("effects").asStream();
            Stream var4 = var3.map((var0x) -> {
               return var0x.emptyMap().set("type", var0x.createString("minecraft:apply_effects")).set("effects", var0x.createList(var0x.get("effect").result().stream())).set("probability", var0x.createFloat(var0x.get("probability").asFloat(1.0F)));
            });
            var0 = Dynamic.copyField((Dynamic)var1.get(), "using_converts_to", var0, "minecraft:use_remainder");
            var0 = var0.set("minecraft:food", ((Dynamic)var1.get()).remove("eat_seconds").remove("effects").remove("using_converts_to"));
            var0 = var0.set("minecraft:consumable", var0.emptyMap().set("consume_seconds", var0.createFloat(var2)).set("on_consume_effects", var0.createList(var4)));
            return var0;
         } else {
            return var0;
         }
      });
   }
}
