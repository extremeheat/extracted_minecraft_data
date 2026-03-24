package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;

public class GossipUUIDFix extends NamedEntityFix {
   public GossipUUIDFix(final Schema outputSchema, final String entityName) {
      super(outputSchema, false, "Gossip for for " + entityName, References.ENTITY, entityName);
   }

   protected Typed<?> fix(final Typed<?> entity) {
      return entity.update(DSL.remainderFinder(), (tag) -> tag.update("Gossips", (gossips) -> {
            Optional var10000 = gossips.asStreamOpt().result().map((s) -> s.map((gossip) -> (Dynamic)AbstractUUIDFix.replaceUUIDLeastMost(gossip, "Target", "Target").orElse(gossip)));
            Objects.requireNonNull(gossips);
            return (Dynamic)DataFixUtils.orElse(var10000.map(gossips::createList), gossips);
         }));
   }
}
