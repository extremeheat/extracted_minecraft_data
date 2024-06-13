package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;

public class PlayerUUIDFix extends AbstractUUIDFix {
   public PlayerUUIDFix(Schema var1) {
      super(var1, References.PLAYER);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(
         "PlayerUUIDFix",
         this.getInputSchema().getType(this.typeReference),
         var0 -> {
            OpticFinder var1 = var0.getType().findField("RootVehicle");
            return var0.updateTyped(
                  var1, var1.type(), var0x -> var0x.update(DSL.remainderFinder(), var0xx -> replaceUUIDLeastMost(var0xx, "Attach", "Attach").orElse(var0xx))
               )
               .update(DSL.remainderFinder(), var0x -> EntityUUIDFix.updateEntityUUID(EntityUUIDFix.updateLivingEntity(var0x)));
         }
      );
   }
}
