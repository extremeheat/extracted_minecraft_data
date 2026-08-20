package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class SwingAnimationComponentSplitFix extends DataFix {
   public SwingAnimationComponentSplitFix(final Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("SwingAnimationComponentSplitFix", this.getInputSchema().getType(References.DATA_COMPONENTS), (input) -> input.update(DSL.remainderFinder(), (tag) -> {
            Optional<? extends Dynamic<?>> swingAnimationOpt = tag.get("minecraft:swing_animation").result();
            if (swingAnimationOpt.isPresent()) {
               Dynamic<?> swingAnimation = (Dynamic)swingAnimationOpt.get();
               String oldType = (String)swingAnimation.get("type").asString().result().orElse("none");
               if (oldType.equals("none")) {
                  swingAnimation = swingAnimation.set("type", tag.createString("whack"));
               }

               return tag.remove("minecraft:swing_animation").set("minecraft:attack_animation", swingAnimation).set("minecraft:interact_animation", swingAnimation);
            } else {
               return tag;
            }
         }));
   }
}
