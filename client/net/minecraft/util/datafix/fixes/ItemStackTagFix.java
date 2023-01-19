package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public abstract class ItemStackTagFix extends DataFix {
   private final String name;
   private final Predicate<String> idFilter;

   public ItemStackTagFix(Schema var1, String var2, Predicate<String> var3) {
      super(var1, false);
      this.name = var2;
      this.idFilter = var3;
   }

   public final TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder var3 = var1.findField("tag");
      return this.fixTypeEverywhereTyped(
         this.name,
         var1,
         var3x -> {
            Optional var4 = var3x.getOptional(var2);
            return var4.isPresent() && this.idFilter.test((String)((Pair)var4.get()).getSecond())
               ? var3x.updateTyped(var3, var1xx -> var1xx.update(DSL.remainderFinder(), this::fixItemStackTag))
               : var3x;
         }
      );
   }

   protected abstract <T> Dynamic<T> fixItemStackTag(Dynamic<T> var1);
}
