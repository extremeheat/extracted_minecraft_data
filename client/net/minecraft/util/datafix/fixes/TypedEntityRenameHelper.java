package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Objects;
import net.minecraft.util.datafix.TypeReferences;

public abstract class TypedEntityRenameHelper extends DataFix {
   private final String field_211312_a;

   public TypedEntityRenameHelper(String var1, Schema var2, boolean var3) {
      super(var2, var3);
      this.field_211312_a = var1;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType var1 = this.getInputSchema().findChoiceType(TypeReferences.field_211299_o);
      TaggedChoiceType var2 = this.getOutputSchema().findChoiceType(TypeReferences.field_211299_o);
      Type var3 = DSL.named(TypeReferences.field_211297_m.typeName(), DSL.namespacedString());
      if (!Objects.equals(this.getOutputSchema().getType(TypeReferences.field_211297_m), var3)) {
         throw new IllegalStateException("Entity name type is not what was expected.");
      } else {
         return TypeRewriteRule.seq(this.fixTypeEverywhere(this.field_211312_a, var1, var2, (var3x) -> {
            return (var3) -> {
               return var3.mapFirst((var3x) -> {
                  String var4 = this.func_211311_a(var3x);
                  Type var5 = (Type)var1.types().get(var3x);
                  Type var6 = (Type)var2.types().get(var4);
                  if (!var6.equals(var5, true, true)) {
                     throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", var6, var5));
                  } else {
                     return var4;
                  }
               });
            };
         }), this.fixTypeEverywhere(this.field_211312_a + " for entity name", var3, (var1x) -> {
            return (var1) -> {
               return var1.mapSecond(this::func_211311_a);
            };
         }));
      }
   }

   protected abstract String func_211311_a(String var1);
}
