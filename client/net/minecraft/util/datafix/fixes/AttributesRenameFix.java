package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
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
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped(this.name + " (Components)", this.getInputSchema().getType(References.DATA_COMPONENTS), this::fixDataComponents), new TypeRewriteRule[]{this.fixTypeEverywhereTyped(this.name + " (Entity)", this.getInputSchema().getType(References.ENTITY), this::fixEntity), this.fixTypeEverywhereTyped(this.name + " (Player)", this.getInputSchema().getType(References.PLAYER), this::fixEntity)});
   }

   private Typed<?> fixDataComponents(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var1x) -> var1x.update("minecraft:attribute_modifiers", (var1) -> var1.update("modifiers", (var1x) -> {
               Optional var10000 = var1x.asStreamOpt().result().map((var1) -> var1.map(this::fixTypeField));
               Objects.requireNonNull(var1x);
               return (Dynamic)DataFixUtils.orElse(var10000.map(var1x::createList), var1x);
            })));
   }

   private Typed<?> fixEntity(Typed<?> var1) {
      return var1.update(DSL.remainderFinder(), (var1x) -> var1x.update("attributes", (var1) -> {
            Optional var10000 = var1.asStreamOpt().result().map((var1x) -> var1x.map(this::fixIdField));
            Objects.requireNonNull(var1);
            return (Dynamic)DataFixUtils.orElse(var10000.map(var1::createList), var1);
         }));
   }

   private Dynamic<?> fixIdField(Dynamic<?> var1) {
      return ExtraDataFixUtils.fixStringField(var1, "id", this.renames);
   }

   private Dynamic<?> fixTypeField(Dynamic<?> var1) {
      return ExtraDataFixUtils.fixStringField(var1, "type", this.renames);
   }
}
