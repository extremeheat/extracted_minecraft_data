package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.network.chat.Component;

public class ObjectiveDisplayNameFix extends DataFix {
   public ObjectiveDisplayNameFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.OBJECTIVE);
      return this.fixTypeEverywhereTyped(
         "ObjectiveDisplayNameFix",
         var1,
         var0 -> var0.update(
               DSL.remainderFinder(),
               var0x -> var0x.update(
                     "DisplayName",
                     var1x -> (Dynamic)DataFixUtils.orElse(
                           var1x.asString().map(var0xxx -> Component.Serializer.toJson(Component.literal(var0xxx))).map(var0x::createString).result(), var1x
                        )
                  )
            )
      );
   }
}
