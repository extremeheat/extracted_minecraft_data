package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;

public abstract class NamedEntityFix extends DataFix {
   private final String name;
   private final String entityName;
   private final TypeReference type;

   public NamedEntityFix(Schema var1, boolean var2, String var3, TypeReference var4, String var5) {
      super(var1, var2);
      this.name = var3;
      this.type = var4;
      this.entityName = var5;
   }

   public TypeRewriteRule makeRule() {
      OpticFinder var1 = DSL.namedChoice(this.entityName, this.getInputSchema().getChoiceType(this.type, this.entityName));
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type), (var2) -> {
         return var2.updateTyped(var1, this.getOutputSchema().getChoiceType(this.type, this.entityName), this::fix);
      });
   }

   protected abstract Typed<?> fix(Typed<?> var1);
}
