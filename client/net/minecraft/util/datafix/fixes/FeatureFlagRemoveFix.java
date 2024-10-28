package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FeatureFlagRemoveFix extends DataFix {
   private final String name;
   private final Set<String> flagsToRemove;

   public FeatureFlagRemoveFix(Schema var1, String var2, Set<String> var3) {
      super(var1, false);
      this.name = var2;
      this.flagsToRemove = var3;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(References.LEVEL), (var1) -> {
         return var1.update(DSL.remainderFinder(), this::fixTag);
      });
   }

   private <T> Dynamic<T> fixTag(Dynamic<T> var1) {
      List var2 = (List)var1.get("removed_features").asStream().collect(Collectors.toCollection(ArrayList::new));
      Dynamic var3 = var1.update("enabled_features", (var3x) -> {
         Optional var10000 = var3x.asStreamOpt().result().map((var3) -> {
            return var3.filter((var3x) -> {
               Optional var4 = var3x.asString().result();
               if (var4.isEmpty()) {
                  return true;
               } else {
                  boolean var5 = this.flagsToRemove.contains(var4.get());
                  if (var5) {
                     var2.add(var1.createString((String)var4.get()));
                  }

                  return !var5;
               }
            });
         });
         Objects.requireNonNull(var1);
         return (Dynamic)DataFixUtils.orElse(var10000.map(var1::createList), var3x);
      });
      if (!var2.isEmpty()) {
         var3 = var3.set("removed_features", var1.createList(var2.stream()));
      }

      return var3;
   }
}
