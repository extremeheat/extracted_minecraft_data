package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class ObjectiveDisplayNameFix extends DataFix {
   public ObjectiveDisplayNameFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(References.OBJECTIVE.typeName(), DSL.remainderType());
      if (!Objects.equals(var1, this.getInputSchema().getType(References.OBJECTIVE))) {
         throw new IllegalStateException("Objective type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("ObjectiveDisplayNameFix", var1, (var0) -> {
            return (var0x) -> {
               return var0x.mapSecond((var0) -> {
                  return var0.update("DisplayName", (var1) -> {
                     DataResult var10000 = var1.asString().map((var0x) -> {
                        return Component.Serializer.toJson(new TextComponent(var0x));
                     });
                     var0.getClass();
                     return (Dynamic)DataFixUtils.orElse(var10000.map(var0::createString).result(), var1);
                  });
               });
            };
         });
      }
   }
}
