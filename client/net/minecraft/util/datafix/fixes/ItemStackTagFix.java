package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
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
      return this.fixTypeEverywhereTyped(this.name, var1, createFixer(var1, this.idFilter, this::fixItemStackTag));
   }

   public static UnaryOperator<Typed<?>> createFixer(Type<?> var0, Predicate<String> var1, UnaryOperator<Dynamic<?>> var2) {
      OpticFinder var3 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), NamespacedSchema.namespacedString()));
      OpticFinder var4 = var0.findField("tag");
      return (var4x) -> {
         Optional var5 = var4x.getOptional(var3);
         return var5.isPresent() && var1.test((String)((Pair)var5.get()).getSecond()) ? var4x.updateTyped(var4, (var1x) -> {
            return var1x.update(DSL.remainderFinder(), var2);
         }) : var4x;
      };
   }

   protected abstract <T> Dynamic<T> fixItemStackTag(Dynamic<T> var1);
}
