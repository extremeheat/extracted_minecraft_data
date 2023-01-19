package net.minecraft.commands;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;

public interface CommandBuildContext {
   <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> var1);

   static CommandBuildContext simple(final HolderLookup.Provider var0, final FeatureFlagSet var1) {
      return new CommandBuildContext() {
         @Override
         public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> var1x) {
            return var0.<T>lookupOrThrow(var1x).filterFeatures(var1);
         }
      };
   }

   static CommandBuildContext.Configurable configurable(final RegistryAccess var0, final FeatureFlagSet var1) {
      return new CommandBuildContext.Configurable() {
         CommandBuildContext.MissingTagAccessPolicy missingTagAccessPolicy = CommandBuildContext.MissingTagAccessPolicy.FAIL;

         @Override
         public void missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy var1x) {
            this.missingTagAccessPolicy = var1x;
         }

         @Override
         public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> var1x) {
            Registry var2 = var0.registryOrThrow(var1x);
            final HolderLookup.RegistryLookup var3 = var2.asLookup();
            final HolderLookup.RegistryLookup var4 = var2.asTagAddingLookup();
            HolderLookup.RegistryLookup.Delegate var5 = new HolderLookup.RegistryLookup.Delegate<T>() {
               @Override
               protected HolderLookup.RegistryLookup<T> parent() {
                  return switch(missingTagAccessPolicy) {
                     case FAIL -> var3;
                     case CREATE_NEW -> var4;
                  };
               }
            };
            return var5.filterFeatures(var1);
         }
      };
   }

   public interface Configurable extends CommandBuildContext {
      void missingTagAccessPolicy(CommandBuildContext.MissingTagAccessPolicy var1);
   }

   public static enum MissingTagAccessPolicy {
      CREATE_NEW,
      FAIL;

      private MissingTagAccessPolicy() {
      }
   }
}
