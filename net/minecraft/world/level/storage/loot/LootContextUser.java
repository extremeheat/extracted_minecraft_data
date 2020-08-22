package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public interface LootContextUser {
   default Set getReferencedContextParams() {
      return ImmutableSet.of();
   }

   default void validate(ValidationContext var1) {
      var1.validateUser(this);
   }
}
