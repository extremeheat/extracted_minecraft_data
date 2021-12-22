package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class RecipesRenameFix extends DataFix {
   private final String name;
   private final Function<String, String> renamer;

   public RecipesRenameFix(Schema var1, boolean var2, String var3, Function<String, String> var4) {
      super(var1, var2);
      this.name = var3;
      this.renamer = var4;
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(References.RECIPE.typeName(), NamespacedSchema.namespacedString());
      if (!Objects.equals(var1, this.getInputSchema().getType(References.RECIPE))) {
         throw new IllegalStateException("Recipe type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.name, var1, (var1x) -> {
            return (var1) -> {
               return var1.mapSecond(this.renamer);
            };
         });
      }
   }
}
