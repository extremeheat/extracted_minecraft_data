package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.function.UnaryOperator;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class NamespacedTypeRenameFix extends DataFix {
   private final String name;
   private final DSL.TypeReference type;
   private final UnaryOperator<String> renamer;

   public NamespacedTypeRenameFix(Schema var1, String var2, DSL.TypeReference var3, UnaryOperator<String> var4) {
      super(var1, false);
      this.name = var2;
      this.type = var3;
      this.renamer = var4;
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(this.type.typeName(), NamespacedSchema.namespacedString());
      if (!Objects.equals(var1, this.getInputSchema().getType(this.type))) {
         throw new IllegalStateException("\"" + this.type.typeName() + "\" is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.name, var1, (var1x) -> {
            return (var1) -> {
               return var1.mapSecond(this.renamer);
            };
         });
      }
   }
}
