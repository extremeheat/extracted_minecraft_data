package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;

public class OverreachingTickFix extends DataFix {
   public OverreachingTickFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.CHUNK);
      OpticFinder var2 = var1.findField("block_ticks");
      return this.fixTypeEverywhereTyped("Handle ticks saved in the wrong chunk", var1, (var1x) -> {
         Optional var2x = var1x.getOptionalTyped(var2);
         Optional var3 = var2x.isPresent() ? ((Typed)var2x.get()).write().result() : Optional.empty();
         return var1x.update(DSL.remainderFinder(), (var1) -> {
            int var2 = var1.get("xPos").asInt(0);
            int var3x = var1.get("zPos").asInt(0);
            Optional var4 = var1.get("fluid_ticks").get().result();
            var1 = extractOverreachingTicks(var1, var2, var3x, var3, "neighbor_block_ticks");
            var1 = extractOverreachingTicks(var1, var2, var3x, var4, "neighbor_fluid_ticks");
            return var1;
         });
      });
   }

   private static Dynamic<?> extractOverreachingTicks(Dynamic<?> var0, int var1, int var2, Optional<? extends Dynamic<?>> var3, String var4) {
      if (var3.isPresent()) {
         List var5 = ((Dynamic)var3.get()).asStream().filter((var2x) -> {
            int var3 = var2x.get("x").asInt(0);
            int var4 = var2x.get("z").asInt(0);
            int var5 = Math.abs(var1 - (var3 >> 4));
            int var6 = Math.abs(var2 - (var4 >> 4));
            return (var5 != 0 || var6 != 0) && var5 <= 1 && var6 <= 1;
         }).toList();
         if (!var5.isEmpty()) {
            var0 = var0.set("UpgradeData", var0.get("UpgradeData").orElseEmptyMap().set(var4, var0.createList(var5.stream())));
         }
      }

      return var0;
   }
}
