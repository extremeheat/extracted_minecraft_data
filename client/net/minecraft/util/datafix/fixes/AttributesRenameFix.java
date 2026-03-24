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

   public AttributesRenameFix(final Schema outputSchema, final String name, final UnaryOperator<String> renames) {
      super(outputSchema, false);
      this.name = name;
      this.renames = renames;
   }

   protected TypeRewriteRule makeRule() {
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped(this.name + " (Components)", this.getInputSchema().getType(References.DATA_COMPONENTS), this::fixDataComponents), new TypeRewriteRule[]{this.fixTypeEverywhereTyped(this.name + " (Entity)", this.getInputSchema().getType(References.ENTITY), this::fixEntity), this.fixTypeEverywhereTyped(this.name + " (Player)", this.getInputSchema().getType(References.PLAYER), this::fixEntity)});
   }

   private Typed<?> fixDataComponents(final Typed<?> components) {
      return components.update(DSL.remainderFinder(), (componentData) -> componentData.update("minecraft:attribute_modifiers", (attributeModifiers) -> attributeModifiers.update("modifiers", (modifiers) -> {
               Optional var10000 = modifiers.asStreamOpt().result().map((modifierStream) -> modifierStream.map(this::fixTypeField));
               Objects.requireNonNull(modifiers);
               return (Dynamic)DataFixUtils.orElse(var10000.map(modifiers::createList), modifiers);
            })));
   }

   private Typed<?> fixEntity(final Typed<?> entity) {
      return entity.update(DSL.remainderFinder(), (tag) -> tag.update("attributes", (attributeList) -> {
            Optional var10000 = attributeList.asStreamOpt().result().map((s) -> s.map(this::fixIdField));
            Objects.requireNonNull(attributeList);
            return (Dynamic)DataFixUtils.orElse(var10000.map(attributeList::createList), attributeList);
         }));
   }

   private Dynamic<?> fixIdField(final Dynamic<?> dynamic) {
      return ExtraDataFixUtils.fixStringField(dynamic, "id", this.renames);
   }

   private Dynamic<?> fixTypeField(final Dynamic<?> dynamic) {
      return ExtraDataFixUtils.fixStringField(dynamic, "type", this.renames);
   }
}
