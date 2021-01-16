package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class PlayerUUIDFix extends AbstractUUIDFix {
   public PlayerUUIDFix(Schema var1) {
      super(var1, References.PLAYER);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("PlayerUUIDFix", this.getInputSchema().getType(this.typeReference), (var0) -> {
         OpticFinder var1 = var0.getType().findField("RootVehicle");
         return var0.updateTyped(var1, var1.type(), (var0x) -> {
            return var0x.update(DSL.remainderFinder(), (var0) -> {
               return (Dynamic)replaceUUIDLeastMost(var0, "Attach", "Attach").orElse(var0);
            });
         }).update(DSL.remainderFinder(), (var0x) -> {
            return EntityUUIDFix.updateEntityUUID(EntityUUIDFix.updateLivingEntity(var0x));
         });
      });
   }
}
