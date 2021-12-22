package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Objects;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class SimplestEntityRenameFix extends DataFix {
   private final String name;

   public SimplestEntityRenameFix(String var1, Schema var2, boolean var3) {
      super(var2, var3);
      this.name = var1;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType var1 = this.getInputSchema().findChoiceType(References.ENTITY);
      TaggedChoiceType var2 = this.getOutputSchema().findChoiceType(References.ENTITY);
      Type var3 = DSL.named(References.ENTITY_NAME.typeName(), NamespacedSchema.namespacedString());
      if (!Objects.equals(this.getOutputSchema().getType(References.ENTITY_NAME), var3)) {
         throw new IllegalStateException("Entity name type is not what was expected.");
      } else {
         return TypeRewriteRule.seq(this.fixTypeEverywhere(this.name, var1, var2, (var3x) -> {
            return (var3) -> {
               return var3.mapFirst((var3x) -> {
                  String var4 = this.rename(var3x);
                  Type var5 = (Type)var1.types().get(var3x);
                  Type var6 = (Type)var2.types().get(var4);
                  if (!var6.equals(var5, true, true)) {
                     throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", var6, var5));
                  } else {
                     return var4;
                  }
               });
            };
         }), this.fixTypeEverywhere(this.name + " for entity name", var3, (var1x) -> {
            return (var1) -> {
               return var1.mapSecond(this::rename);
            };
         }));
      }
   }

   protected abstract String rename(String var1);
}
