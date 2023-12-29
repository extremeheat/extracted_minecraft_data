package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Objects;
import net.minecraft.util.datafix.ComponentDataFixUtils;

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
            var0 -> var0x -> var0x.mapSecond(var0xx -> var0xx.update("DisplayName", ComponentDataFixUtils::wrapLiteralStringAsComponent))
         );
      }
   }
}
