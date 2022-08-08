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
   MissingTagAccessPolicy missingTagAccessPolicy;

   public CommandBuildContext(RegistryAccess var1) {
      super();
      this.missingTagAccessPolicy = CommandBuildContext.MissingTagAccessPolicy.FAIL;
      this.registryAccess = var1;
   }

   public void missingTagAccessPolicy(MissingTagAccessPolicy var1) {
      this.missingTagAccessPolicy = var1;
   }

   public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> var1) {
      return new HolderLookup.RegistryLookup<T>(this.registryAccess.registryOrThrow(var1)) {
         public Optional<? extends HolderSet<T>> get(TagKey<T> var1) {
            Optional var10000;
            switch (CommandBuildContext.this.missingTagAccessPolicy) {
               case FAIL:
                  var10000 = this.registry.getTag(var1);
                  break;
               case CREATE_NEW:
                  var10000 = Optional.of(this.registry.getOrCreateTag(var1));
                  break;
               case RETURN_EMPTY:
                  Optional var2 = this.registry.getTag(var1);
                  var10000 = Optional.of(var2.isPresent() ? (HolderSet)var2.get() : HolderSet.direct());
                  break;
               default:
                  throw new IncompatibleClassChangeError();
            }

            return var10000;
         }
      };
   }

   public static enum MissingTagAccessPolicy {
      CREATE_NEW,
      RETURN_EMPTY,
      FAIL;

      private MissingTagAccessPolicy() {
      }

      // $FF: synthetic method
      private static MissingTagAccessPolicy[] $values() {
         return new MissingTagAccessPolicy[]{CREATE_NEW, RETURN_EMPTY, FAIL};
      }
   }
}
