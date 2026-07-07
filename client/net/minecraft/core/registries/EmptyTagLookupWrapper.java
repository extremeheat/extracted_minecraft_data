package net.minecraft.core.registries;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;

public record EmptyTagLookupWrapper<T>(HolderLookup.RegistryLookup<T> parent) implements HolderLookup.RegistryLookup.Delegate<T> {
   public EmptyTagLookupWrapper {
      super();
   }

   public static <T> HolderLookup.RegistryLookup<T> wrap(final HolderLookup.RegistryLookup<T> registryLookup) {
      return (HolderLookup.RegistryLookup<T>)(registryLookup instanceof EmptyTagLookupWrapper ? registryLookup : new EmptyTagLookupWrapper(registryLookup));
   }

   public static HolderLookup.Provider wrap(final HolderLookup.Provider provider) {
      return HolderLookup.Provider.create(provider.listRegistries().map(EmptyTagLookupWrapper::wrap));
   }

   public Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
      return Optional.of(this.getOrThrow(id));
   }

   public HolderSet.Named<T> getOrThrow(final TagKey<T> id) {
      return HolderSet.<T>emptyNamed(this.parent, id);
   }

   public boolean canSerialize(final HolderOwner<T> owner) {
      return this.parent.canSerialize(owner);
   }

   public Stream<HolderSet.Named<T>> listTags() {
      throw new UnsupportedOperationException("Tags are not available in datagen");
   }
}
