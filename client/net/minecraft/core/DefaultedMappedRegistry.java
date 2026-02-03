package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import org.jspecify.annotations.Nullable;

public class DefaultedMappedRegistry<T> extends MappedRegistry<T> implements DefaultedRegistry<T> {
   private final Identifier defaultKey;
   private Holder.Reference<T> defaultValue;

   public DefaultedMappedRegistry(final String defaultKey, final ResourceKey<? extends Registry<T>> key, final Lifecycle lifecycle, final boolean intrusiveHolders) {
      super(key, lifecycle, intrusiveHolders);
      this.defaultKey = Identifier.parse(defaultKey);
   }

   public Holder.Reference<T> register(final ResourceKey<T> key, final T value, final RegistrationInfo registrationInfo) {
      Holder.Reference<T> result = super.register(key, value, registrationInfo);
      if (this.defaultKey.equals(key.identifier())) {
         this.defaultValue = result;
      }

      return result;
   }

   public int getId(final @Nullable T thing) {
      int id = super.getId(thing);
      return id == -1 ? super.getId(this.defaultValue.value()) : id;
   }

   public Identifier getKey(final T thing) {
      Identifier k = super.getKey(thing);
      return k == null ? this.defaultKey : k;
   }

   public T getValue(final @Nullable Identifier key) {
      T t = (T)super.getValue(key);
      return (T)(t == null ? this.defaultValue.value() : t);
   }

   public Optional<T> getOptional(final @Nullable Identifier key) {
      return Optional.ofNullable(super.getValue(key));
   }

   public Optional<Holder.Reference<T>> getAny() {
      return Optional.ofNullable(this.defaultValue);
   }

   public T byId(final int id) {
      T t = (T)super.byId(id);
      return (T)(t == null ? this.defaultValue.value() : t);
   }

   public Optional<Holder.Reference<T>> getRandom(final RandomSource random) {
      return super.getRandom(random).or(() -> Optional.of(this.defaultValue));
   }

   public Identifier getDefaultKey() {
      return this.defaultKey;
   }
}
