package net.minecraft.commands;

import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public final class CommandBuildContext {
   private final RegistryAccess registryAccess;
   CommandBuildContext.MissingTagAccessPolicy missingTagAccessPolicy = CommandBuildContext.MissingTagAccessPolicy.FAIL;

   public CommandBuildContext(RegistryAccess var1) {
      super();
      this.registryAccess = var1;
   }

   public void missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy var1) {
      this.missingTagAccessPolicy = var1;
   }

   public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> var1) {
      return new HolderLookup.RegistryLookup<T>(this.registryAccess.registryOrThrow(var1)) {
         @Override
         public Optional<? extends HolderSet<T>> get(TagKey<T> var1) {
            return switch(CommandBuildContext.this.missingTagAccessPolicy) {
               case FAIL -> this.registry.getTag(var1);
               case CREATE_NEW -> Optional.of(this.registry.getOrCreateTag(var1));
               case RETURN_EMPTY -> {
                  Optional var2 = this.registry.getTag(var1);
                  yield Optional.of(var2.isPresent() ? (HolderSet)var2.get() : HolderSet.direct());
               }
            };
         }
      };
   }

   public static enum MissingTagAccessPolicy {
      CREATE_NEW,
      RETURN_EMPTY,
      FAIL;

      private MissingTagAccessPolicy() {
      }
   }
}
