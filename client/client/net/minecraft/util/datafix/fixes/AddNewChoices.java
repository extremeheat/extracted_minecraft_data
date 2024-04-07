package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Locale;

public class AddNewChoices extends DataFix {
   private final String name;
   private final TypeReference type;

   public AddNewChoices(Schema var1, String var2, TypeReference var3) {
      super(var1, true);
      this.name = var2;
      this.type = var3;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType var1 = this.getInputSchema().findChoiceType(this.type);
      TaggedChoiceType var2 = this.getOutputSchema().findChoiceType(this.type);
      return this.cap(var1, var2);
   }

   private <K> TypeRewriteRule cap(TaggedChoiceType<K> var1, TaggedChoiceType<?> var2) {
      if (var1.getKeyType() != var2.getKeyType()) {
         throw new IllegalStateException("Could not inject: key type is not the same");
      } else {
         return this.fixTypeEverywhere(
            this.name,
            var1,
            var2,
            var2x -> var2xx -> {
                  if (!var2.hasType(var2xx.getFirst())) {
                     throw new IllegalArgumentException(
                        String.format(Locale.ROOT, "%s: Unknown type %s in '%s'", this.name, var2xx.getFirst(), this.type.typeName())
                     );
                  } else {
                     return var2xx;
                  }
               }
         );
      }
   }
}
