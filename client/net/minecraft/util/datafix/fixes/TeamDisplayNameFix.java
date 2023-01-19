package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.network.chat.Component;

public class TeamDisplayNameFix extends DataFix {
   public TeamDisplayNameFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(References.TEAM.typeName(), DSL.remainderType());
      if (!Objects.equals(var1, this.getInputSchema().getType(References.TEAM))) {
         throw new IllegalStateException("Team type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(
            "TeamDisplayNameFix",
            var1,
            var0 -> var0x -> var0x.mapSecond(
                     var0xx -> var0xx.update(
                           "DisplayName",
                           var1x -> (Dynamic)DataFixUtils.orElse(
                                 var1x.asString().map(var0xxxx -> Component.Serializer.toJson(Component.literal(var0xxxx))).map(var0xx::createString).result(),
                                 var1x
                              )
                        )
                  )
         );
      }
   }
}
