package net.minecraft.world.level.storage.loot;

import java.util.Set;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public interface LootContextUser {
   default Set<LootContextParam<?>> getReferencedContextParams() {
      return Set.of();
   }

   default void validate(ValidationContext var1) {
      var1.validateUser(this);
   }
}
