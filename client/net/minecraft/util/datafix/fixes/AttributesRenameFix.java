package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class AttributesRenameFix extends DataFix {
   private final String name;
   private final UnaryOperator<String> renames;

   public AttributesRenameFix(Schema var1, String var2, UnaryOperator<String> var3) {
      super(var1, false);
      this.name = var2;
      this.renames = var3;
   }

   protected TypeRewriteRule makeRule() {
      return TypeRewriteRule.seq(
         this.fixTypeEverywhereTyped(this.name + " (Components)", this.getInputSchema().getType(References.DATA_COMPONENTS), this::fixDataComponents),
         new TypeRewriteRule[]{
            this.fixTypeEverywhereTyped(this.name + " (Entity)", this.getInputSchema().getType(References.ENTITY), this::fixEntity),
            this.fixTypeEverywhereTyped(this.name + " (Player)", this.getInputSchema().getType(References.PLAYER), this::fixEntity)
         }
      );
   }

   private Typed<?> fixDataComponents(Typed<?> var1) {
      return var1.update(
         DSL.remainderFinder(),
         var1x -> var1x.update(
               "minecraft:attribute_modifiers",
               var1xx -> var1xx.update(
                     "modifiers",
                     var1xxx -> (Dynamic)DataFixUtils.orElse(
                           var1xxx.asStreamOpt().result().map(var1xxxx -> var1xxxx.map(this::fixTypeField)).map(var1xxx::createList), var1xxx
                        )
                  )
            )
      );
   }

   private Typed<?> fixEntity(Typed<?> var1) {
      return var1.update(
         DSL.remainderFinder(),
         var1x -> var1x.update(
               "attributes",
               var1xx -> (Dynamic)DataFixUtils.orElse(
                     var1xx.asStreamOpt().result().map(var1xxx -> var1xxx.map(this::fixIdField)).map(var1xx::createList), var1xx
                  )
            )
      );
   }

   private Dynamic<?> fixIdField(Dynamic<?> var1) {
      return ExtraDataFixUtils.fixStringField((Dynamic<?>)var1, "id", this.renames);
   }

   private Dynamic<?> fixTypeField(Dynamic<?> var1) {
      return ExtraDataFixUtils.fixStringField((Dynamic<?>)var1, "type", this.renames);
   }
}
