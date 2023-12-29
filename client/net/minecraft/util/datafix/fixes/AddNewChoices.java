package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DynamicOps;
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
      return this.cap(this.name, var1, var2);
   }

   protected final <K> TypeRewriteRule cap(String var1, TaggedChoiceType<K> var2, TaggedChoiceType<?> var3) {
      if (var2.getKeyType() != var3.getKeyType()) {
         throw new IllegalStateException("Could not inject: key type is not the same");
      } else {
         return this.fixTypeEverywhere(var1, var2, var3, var2x -> var2xx -> {
               if (!var3.hasType(var2xx.getFirst())) {
                  throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown type %s in %s ", var2xx.getFirst(), this.type));
               } else {
                  return var2xx;
               }
            });
      }
   }
}
