package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class SimpleRenameFix extends DataFix {
   private final String fixerName;
   private final Map<String, String> nameMapping;
   private final DSL.TypeReference typeReference;

   public SimpleRenameFix(Schema var1, DSL.TypeReference var2, Map<String, String> var3) {
      this(var1, var2, var2.typeName() + "-renames at version: " + var1.getVersionKey(), var3);
   }

   public SimpleRenameFix(Schema var1, DSL.TypeReference var2, String var3, Map<String, String> var4) {
      super(var1, false);
      this.nameMapping = var4;
      this.fixerName = var3;
      this.typeReference = var2;
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = DSL.named(this.typeReference.typeName(), NamespacedSchema.namespacedString());
      if (!Objects.equals(var1, this.getInputSchema().getType(this.typeReference))) {
         throw new IllegalStateException("\"" + this.typeReference.typeName() + "\" type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.fixerName, var1, (var1x) -> {
            return (var1) -> {
               return var1.mapSecond((var1x) -> {
                  return (String)this.nameMapping.getOrDefault(var1x, var1x);
               });
            };
         });
      }
   }
}
