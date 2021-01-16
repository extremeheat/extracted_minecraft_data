package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class ItemRenameFix extends DataFix {
   private final String name;

   public ItemRenameFix(Schema var1, String var2) {
      super(var1, false);
      this.name = var2;
   }

   public TypeRewriteRule makeRule() {
      Type var1 = DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString());
      if (!Objects.equals(this.getInputSchema().getType(References.ITEM_NAME), var1)) {
         throw new IllegalStateException("item name type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.name, var1, (var1x) -> {
            return (var1) -> {
               return var1.mapSecond(this::fixItem);
            };
         });
      }
   }

   protected abstract String fixItem(String var1);

   public static DataFix create(Schema var0, String var1, final Function<String, String> var2) {
      return new ItemRenameFix(var0, var1) {
         protected String fixItem(String var1) {
            return (String)var2.apply(var1);
         }
      };
   }
}
