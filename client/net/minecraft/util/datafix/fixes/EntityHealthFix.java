package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class EntityHealthFix extends DataFix {
   public EntityHealthFix(final Schema outputSchema, final boolean changesType) {
      super(outputSchema, changesType);
   }

   public Dynamic<?> fixTag(Dynamic<?> input) {
      Optional<Number> oldHealF = input.get("HealF").asNumber().result();
      Optional<Number> oldHealth = input.get("Health").asNumber().result();
      float health;
      if (oldHealF.isPresent()) {
         health = ((Number)oldHealF.get()).floatValue();
         input = input.remove("HealF");
      } else {
         if (!oldHealth.isPresent()) {
            return input;
         }

         health = ((Number)oldHealth.get()).floatValue();
      }

      return input.set("Health", input.createFloat(health));
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityHealthFix", this.getInputSchema().getType(References.ENTITY), (input) -> input.update(DSL.remainderFinder(), this::fixTag));
   }
}
