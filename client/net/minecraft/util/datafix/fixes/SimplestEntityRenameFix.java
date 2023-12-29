package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
import java.util.Locale;
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
         return TypeRewriteRule.seq(this.fixTypeEverywhere(this.name, var1, var2, var3x -> var3xx -> var3xx.mapFirst(var3xxx -> {
                  String var4 = this.rename(var3xxx);
                  Type var5 = (Type)var1.types().get(var3xxx);
                  Type var6 = (Type)var2.types().get(var4);
                  if (!var6.equals(var5, true, true)) {
                     throw new IllegalStateException(String.format(Locale.ROOT, "Dynamic type check failed: %s not equal to %s", var6, var5));
                  } else {
                     return var4;
                  }
               })), this.fixTypeEverywhere(this.name + " for entity name", var3, var1x -> var1xx -> var1xx.mapSecond(this::rename)));
      }
   }

   protected abstract String rename(String var1);
}
