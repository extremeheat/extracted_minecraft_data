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
      return TypeRewriteRule.seq(
         this.fixTypeEverywhereTyped(this.name + " (ItemStack)", var1, var2x -> var2x.updateTyped(var2, this::fixItemStackTag)),
         new TypeRewriteRule[]{
            this.fixTypeEverywhereTyped(this.name + " (Entity)", this.getInputSchema().getType(References.ENTITY), this::fixEntity),
            this.fixTypeEverywhereTyped(this.name + " (Player)", this.getInputSchema().getType(References.PLAYER), this::fixEntity)
         }
      );
   }

   private Dynamic<?> fixName(Dynamic<?> var1) {
      return (Dynamic<?>)DataFixUtils.orElse(var1.asString().result().map(this.renames).map(var1::createString), var1);
   }

   private Typed<?> fixItemStackTag(Typed<?> var1) {
      return var1.update(
         DSL.remainderFinder(),
         var1x -> var1x.update(
               "AttributeModifiers",
               var1xx -> (Dynamic)DataFixUtils.orElse(
                     var1xx.asStreamOpt()
                        .result()
                        .map(var1xxx -> var1xxx.map(var1xxxx -> var1xxxx.update("AttributeName", this::fixName)))
                        .map(var1xx::createList),
                     var1xx
                  )
            )
      );
   }

   private Typed<?> fixEntity(Typed<?> var1) {
      return var1.update(
         DSL.remainderFinder(),
         var1x -> var1x.update(
               "Attributes",
               var1xx -> (Dynamic)DataFixUtils.orElse(
                     var1xx.asStreamOpt().result().map(var1xxx -> var1xxx.map(var1xxxx -> var1xxxx.update("Name", this::fixName))).map(var1xx::createList),
                     var1xx
                  )
            )
      );
   }
}
