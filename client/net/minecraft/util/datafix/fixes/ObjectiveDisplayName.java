package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ObjectiveDisplayName extends DataFix {
   public ObjectiveDisplayName(Schema var1, boolean var2) {
      super(var1, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(TypeReferences.field_211873_t.typeName(), DSL.remainderType());
      if (!Objects.equals(var1, this.getInputSchema().getType(TypeReferences.field_211873_t))) {
         throw new IllegalStateException("Objective type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("ObjectiveDisplayNameFix", var1, (var0) -> {
            return (var0x) -> {
               return var0x.mapSecond((var0) -> {
                  return var0.update("DisplayName", (var1) -> {
                     Optional var10000 = var1.getStringValue().map((var0x) -> {
                        return ITextComponent.Serializer.func_150696_a(new TextComponentString(var0x));
                     });
                     var0.getClass();
                     return (Dynamic)DataFixUtils.orElse(var10000.map(var0::createString), var1);
                  });
               });
            };
         });
      }
   }
}
