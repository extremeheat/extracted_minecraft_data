package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;

public class GossipUUIDFix extends NamedEntityFix {
   public GossipUUIDFix(Schema var1, String var2) {
      super(var1, false, "Gossip for for " + var2, References.ENTITY, var2);
   }

   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var0) -> {
         return var0.update("Gossips", (var0x) -> {
            Optional var10000 = var0x.asStreamOpt().result().map((var0) -> {
               return var0.map((var0x) -> {
                  return (Dynamic)AbstractUUIDFix.replaceUUIDLeastMost(var0x, "Target", "Target").orElse(var0x);
               });
            });
            Objects.requireNonNull(var0x);
            return (Dynamic)DataFixUtils.orElse(var10000.map(var0x::createList), var0x);
         });
      });
   }
}
