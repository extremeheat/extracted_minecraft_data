package net.minecraft.world.item.crafting.display;

import net.minecraft.core.RegistryAccess;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.level.Level;

public class SlotDisplayContext {
   public static final ContextKey<RegistryAccess> REGISTRIES = ContextKey.<RegistryAccess>vanilla("registries");
   public static final ContextKeySet CONTEXT;

   public SlotDisplayContext() {
      super();
   }

   public static ContextMap fromLevel(final Level level) {
      return ContextMap.builder().set(REGISTRIES, level.registryAccess()).buildAndValidate(CONTEXT);
   }

   static {
      CONTEXT = (new ContextKeySet.Builder()).optional(REGISTRIES).build();
   }
}
