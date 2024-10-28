package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class AttributesRename extends DataFix {
   private final String name;
   private final UnaryOperator<String> renames;

   public AttributesRename(Schema var1, String var2, UnaryOperator<String> var3) {
      super(var1, false);
      this.name = var2;
      this.renames = var3;
   }

   protected TypeRewriteRule makeRule() {
      Type var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder var2 = var1.findField("tag");
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped(this.name + " (ItemStack)", var1, (var2x) -> {
         return var2x.updateTyped(var2, this::fixItemStackTag);
      }), new TypeRewriteRule[]{this.fixTypeEverywhereTyped(this.name + " (Entity)", this.getInputSchema().getType(References.ENTITY), this::fixEntity), this.fixTypeEverywhereTyped(this.name + " (Player)", this.getInputSchema().getType(References.PLAYER), this::fixEntity)});
   }

   private Dynamic<?> fixName(Dynamic<?> var1) {
      Optional var10000 = var1.asString().result().map(this.renames);
      Objects.requireNonNull(var1);
      return (Dynamic)DataFixUtils.orElse(var10000.map(var1::createString), var1);
   }

   private Typed<?> fixItemStackTag(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var1x) -> {
         return var1x.update("AttributeModifiers", (var1) -> {
            Optional var10000 = var1.asStreamOpt().result().map((var1x) -> {
               return var1x.map((var1) -> {
                  return var1.update("AttributeName", this::fixName);
               });
            });
            Objects.requireNonNull(var1);
            return (Dynamic)DataFixUtils.orElse(var10000.map(var1::createList), var1);
         });
      });
   }

   private Typed<?> fixEntity(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var1x) -> {
         return var1x.update("Attributes", (var1) -> {
            Optional var10000 = var1.asStreamOpt().result().map((var1x) -> {
               return var1x.map((var1) -> {
                  return var1.update("Name", this::fixName);
               });
            });
            Objects.requireNonNull(var1);
            return (Dynamic)DataFixUtils.orElse(var10000.map(var1::createList), var1);
         });
      });
   }
}
