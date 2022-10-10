package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.TypeReferences;

public abstract class ItemRename extends DataFix {
   private final String field_206356_a;

   public ItemRename(Schema var1, String var2) {
      super(var1, false);
      this.field_206356_a = var2;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = DSL.named(TypeReferences.field_211301_q.typeName(), DSL.namespacedString());
      if (!Objects.equals(this.getInputSchema().getType(TypeReferences.field_211301_q), var1)) {
         throw new IllegalStateException("item name type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.field_206356_a, var1, (var1x) -> {
            return (var1) -> {
               return var1.mapSecond(this::func_206355_a);
            };
         });
      }
   }

   protected abstract String func_206355_a(String var1);

   public static DataFix func_207476_a(Schema var0, String var1, final Function<String, String> var2) {
      return new ItemRename(var0, var1) {
         protected String func_206355_a(String var1) {
            return (String)var2.apply(var1);
         }
      };
   }
}
