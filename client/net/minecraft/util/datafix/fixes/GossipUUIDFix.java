package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class GossipUUIDFix extends NamedEntityFix {
   public GossipUUIDFix(Schema var1, String var2) {
      super(var1, false, "Gossip for for " + var2, References.ENTITY, var2);
   }

   @Override
   protected Typed<?> fix(Typed<?> var1) {
      return var1.update(
         DSL.remainderFinder(),
         var0 -> var0.update(
               "Gossips",
               var0x -> (Dynamic)DataFixUtils.orElse(
                     var0x.asStreamOpt()
                        .result()
                        .map(
                           var0xx -> var0xx.map(
                                 var0xxx -> (Dynamic)AbstractUUIDFix.replaceUUIDLeastMost((Dynamic<?>)var0xxx, "Target", "Target").orElse((Dynamic<?>)var0xxx)
                              )
                        )
                        .map(var0x::createList),
                     var0x
                  )
            )
      );
   }
}
