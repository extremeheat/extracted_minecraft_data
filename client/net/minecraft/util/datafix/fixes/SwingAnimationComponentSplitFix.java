package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class SwingAnimationComponentSplitFix extends DataFix {
   public SwingAnimationComponentSplitFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("SwingAnimationComponentSplitFix", this.getInputSchema().getType(References.DATA_COMPONENTS), (input) -> input.update(DSL.remainderFinder(), (tag) -> {
            Optional<? extends Dynamic<?>> swingAnimationOpt = tag.get("minecraft:swing_animation").result();
            if (swingAnimationOpt.isPresent()) {
               Dynamic<?> swingAnimation = (Dynamic)swingAnimationOpt.get();
               return tag.remove("minecraft:swing_animation").set("minecraft:attack_animation", swingAnimation).set("minecraft:interact_animation", swingAnimation).update("minecraft:tooltip_display", SwingAnimationComponentSplitFix::fixTooltipDisplay);
            } else {
               return tag;
            }
         }));
   }

   private static Dynamic<?> fixTooltipDisplay(final Dynamic<?> tooltipDisplay) {
      return tooltipDisplay.update("hidden_components", (hiddenComponents) -> (Dynamic)DataFixUtils.orElse(hiddenComponents.asStreamOpt().result().map((stream) -> hiddenComponents.createList(stream.mapMulti((component, output) -> {
               if (NamespacedSchema.namespacedIdMatches(component, "minecraft:swing_animation")) {
                  output.accept(component.createString("minecraft:attack_animation"));
                  output.accept(component.createString("minecraft:interact_animation"));
               } else {
                  output.accept(component);
               }

            }))), hiddenComponents));
   }
}
