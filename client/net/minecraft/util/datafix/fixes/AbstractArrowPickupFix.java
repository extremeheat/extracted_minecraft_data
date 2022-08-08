package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;

public class AbstractArrowPickupFix extends DataFix {
   public AbstractArrowPickupFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      Schema var1 = this.getInputSchema();
      return this.fixTypeEverywhereTyped("AbstractArrowPickupFix", var1.getType(References.ENTITY), this::updateProjectiles);
   }

   private Typed<?> updateProjectiles(Typed<?> var1) {
      var1 = this.updateEntity(var1, "minecraft:arrow", AbstractArrowPickupFix::updatePickup);
      var1 = this.updateEntity(var1, "minecraft:spectral_arrow", AbstractArrowPickupFix::updatePickup);
      var1 = this.updateEntity(var1, "minecraft:trident", AbstractArrowPickupFix::updatePickup);
      return var1;
   }

   private static Dynamic<?> updatePickup(Dynamic<?> var0) {
      if (var0.get("pickup").result().isPresent()) {
         return var0;
      } else {
         boolean var1 = var0.get("player").asBoolean(true);
         return var0.set("pickup", var0.createByte((byte)(var1 ? 1 : 0))).remove("player");
      }
   }

   private Typed<?> updateEntity(Typed<?> var1, String var2, Function<Dynamic<?>, Dynamic<?>> var3) {
      Type var4 = this.getInputSchema().getChoiceType(References.ENTITY, var2);
      Type var5 = this.getOutputSchema().getChoiceType(References.ENTITY, var2);
      return var1.updateTyped(DSL.namedChoice(var2, var4), var5, (var1x) -> {
         return var1x.update(DSL.remainderFinder(), var3);
      });
   }
}
