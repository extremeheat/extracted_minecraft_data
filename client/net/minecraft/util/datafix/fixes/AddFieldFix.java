package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

public class AddFieldFix extends DataFix {
   private final String name;
   private final DSL.TypeReference type;
   private final String fieldName;
   private final String[] path;
   private final Function<Dynamic<?>, Dynamic<?>> fieldGenerator;

   public AddFieldFix(Schema var1, DSL.TypeReference var2, String var3, Function<Dynamic<?>, Dynamic<?>> var4, String... var5) {
      super(var1, false);
      this.name = String.format(Locale.ROOT, "Adding field `%s` to type `%s`", var3, var2.typeName().toLowerCase());
      this.type = var2;
      this.fieldName = var3;
      this.path = var5;
      this.fieldGenerator = var4;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type), (var1) -> var1.update(DSL.remainderFinder(), (var1x) -> this.addField(var1x, 0)));
   }

   private Dynamic<?> addField(Dynamic<?> var1, int var2) {
      if (var2 >= this.path.length) {
         return var1.set(this.fieldName, (Dynamic)this.fieldGenerator.apply(var1));
      } else {
         Optional var3 = var1.get(this.path[var2]).result();
         return var3.isEmpty() ? var1 : this.addField((Dynamic)var3.get(), var2 + 1);
      }
   }
}
