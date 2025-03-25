package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class AbstractBlockPropertyFix extends DataFix {
   private final String name;

   public AbstractBlockPropertyFix(Schema var1, String var2) {
      super(var1, false);
      this.name = var2;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(References.BLOCK_STATE), (var1) -> var1.update(DSL.remainderFinder(), this::fixBlockState));
   }

   private Dynamic<?> fixBlockState(Dynamic<?> var1) {
      Optional var2 = var1.get("Name").asString().result().map(NamespacedSchema::ensureNamespaced);
      return var2.isPresent() && this.shouldFix((String)var2.get()) ? var1.update("Properties", (var2x) -> this.fixProperties((String)var2.get(), var2x)) : var1;
   }

   protected abstract boolean shouldFix(String var1);

   protected abstract <T> Dynamic<T> fixProperties(String var1, Dynamic<T> var2);
}
