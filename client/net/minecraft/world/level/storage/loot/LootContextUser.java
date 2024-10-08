package net.minecraft.world.level.storage.loot;

import java.util.Set;
import net.minecraft.util.context.ContextKey;

public interface LootContextUser {
   default Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of();
   }

   default void validate(ValidationContext var1) {
      var1.validateContextUsage(this);
   }
}
