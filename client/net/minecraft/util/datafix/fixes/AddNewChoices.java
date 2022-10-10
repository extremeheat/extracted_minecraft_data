package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;

public class AddNewChoices extends DataFix {
   private final String field_206292_a;
   private final TypeReference field_206293_b;

   public AddNewChoices(Schema var1, String var2, TypeReference var3) {
      super(var1, true);
      this.field_206292_a = var2;
      this.field_206293_b = var3;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType var1 = this.getInputSchema().findChoiceType(this.field_206293_b);
      TaggedChoiceType var2 = this.getOutputSchema().findChoiceType(this.field_206293_b);
      return this.func_206290_a(this.field_206292_a, var1, var2);
   }

   protected final <K> TypeRewriteRule func_206290_a(String var1, TaggedChoiceType<K> var2, TaggedChoiceType<?> var3) {
      if (var2.getKeyType() != var3.getKeyType()) {
         throw new IllegalStateException("Could not inject: key type is not the same");
      } else {
         return this.fixTypeEverywhere(var1, var2, var3, (var2x) -> {
            return (var2) -> {
               if (!var3.hasType(var2.getFirst())) {
                  throw new IllegalArgumentException(String.format("Unknown type %s in %s ", var2.getFirst(), this.field_206293_b));
               } else {
                  return var2;
               }
            };
         });
      }
   }
}
