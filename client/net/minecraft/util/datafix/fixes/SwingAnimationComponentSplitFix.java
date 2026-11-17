package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.stream.Stream;
import net.minecraft.util.datafix.ExtraDataFixUtils;

public class SwingAnimationComponentSplitFix extends DataFix {
   public SwingAnimationComponentSplitFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      ExtraDataFixUtils.ComponentIdManipulator componentSpliter = (componentName) -> componentName.equals("minecraft:swing_animation") ? Stream.of("minecraft:attack_animation", "minecraft:interact_animation") : Stream.of(componentName);
      return this.fixTypeEverywhereTyped("SwingAnimationComponentSplitFix", this.getInputSchema().getType(References.DATA_COMPONENTS), (input) -> input.update(DSL.remainderFinder(), (tag) -> ExtraDataFixUtils.fixComponentsIds(tag, componentSpliter, true)));
   }
}
