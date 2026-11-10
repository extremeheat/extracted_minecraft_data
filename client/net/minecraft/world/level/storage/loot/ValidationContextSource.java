package net.minecraft.world.level.storage.loot;

import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jspecify.annotations.Nullable;

public class ValidationContextSource {
   private final ProblemReporter reporter;
   private @Nullable ValidationContext entityContext;

   public ValidationContextSource(final ProblemReporter reporter) {
      super();
      this.reporter = reporter;
   }

   public ValidationContext context(final ContextKeySet params) {
      return new ValidationContext(this.reporter, params);
   }

   public ValidationContext entityContext() {
      if (this.entityContext == null) {
         this.entityContext = this.context(LootContextParamSets.ADVANCEMENT_ENTITY);
      }

      return this.entityContext;
   }
}
