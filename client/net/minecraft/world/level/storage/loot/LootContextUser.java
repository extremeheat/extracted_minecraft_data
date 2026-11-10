package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.util.context.ContextKey;

public interface LootContextUser extends Validatable {
   Set<ContextKey<?>> getRequiredParams();

   default void validate(final ValidationContext context) {
      this.validateContextUsage(context);
   }

   default void validateContextUsage(final ValidationContext context) {
      Set<ContextKey<?>> allReferenced = this.getRequiredParams();
      Set<ContextKey<?>> notProvided = Sets.difference(allReferenced, context.contextKeySet().allowed());
      if (!notProvided.isEmpty()) {
         context.reportProblem(new ValidationContext.ParametersNotProvidedProblem(notProvided));
      }

   }
}
