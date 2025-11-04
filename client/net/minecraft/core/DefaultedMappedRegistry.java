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

   public DefaultedMappedRegistry(String var1, ResourceKey<? extends Registry<T>> var2, Lifecycle var3, boolean var4) {
      super(var2, var3, var4);
      this.defaultKey = Identifier.parse(var1);
   }

   public Holder.Reference<T> register(ResourceKey<T> var1, T var2, RegistrationInfo var3) {
      Holder.Reference var4 = super.register(var1, var2, var3);
      if (this.defaultKey.equals(var1.identifier())) {
         this.defaultValue = var4;
      }

      return var4;
   }

   public int getId(@Nullable T var1) {
      int var2 = super.getId(var1);
      return var2 == -1 ? super.getId(this.defaultValue.value()) : var2;
   }

   public Identifier getKey(T var1) {
      Identifier var2 = super.getKey(var1);
      return var2 == null ? this.defaultKey : var2;
   }

   public T getValue(@Nullable Identifier var1) {
      Object var2 = super.getValue(var1);
      return (T)(var2 == null ? this.defaultValue.value() : var2);
   }

   public Optional<T> getOptional(@Nullable Identifier var1) {
      return Optional.ofNullable(super.getValue(var1));
   }

   public Optional<Holder.Reference<T>> getAny() {
      return Optional.ofNullable(this.defaultValue);
   }

   public T byId(int var1) {
      Object var2 = super.byId(var1);
      return (T)(var2 == null ? this.defaultValue.value() : var2);
   }

   public Optional<Holder.Reference<T>> getRandom(RandomSource var1) {
      return super.getRandom(var1).or(() -> Optional.of(this.defaultValue));
   }

   public Identifier getDefaultKey() {
      return this.defaultKey;
   }
}
