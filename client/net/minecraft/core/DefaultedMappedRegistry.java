package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class DefaultedMappedRegistry<T> extends MappedRegistry<T> implements DefaultedRegistry<T> {
   private final ResourceLocation defaultKey;
   private Holder.Reference<T> defaultValue;

   public DefaultedMappedRegistry(String var1, ResourceKey<? extends Registry<T>> var2, Lifecycle var3, boolean var4) {
      super(var2, var3, var4);
      this.defaultKey = ResourceLocation.parse(var1);
   }

   @Override
   public Holder.Reference<T> register(ResourceKey<T> var1, T var2, RegistrationInfo var3) {
      Holder.Reference var4 = super.register(var1, (T)var2, var3);
      if (this.defaultKey.equals(var1.location())) {
         this.defaultValue = var4;
      }

      return var4;
   }

   @Override
   public int getId(@Nullable T var1) {
      int var2 = super.getId((T)var1);
      return var2 == -1 ? super.getId(this.defaultValue.value()) : var2;
   }

   @Nonnull
   @Override
   public ResourceLocation getKey(T var1) {
      ResourceLocation var2 = super.getKey((T)var1);
      return var2 == null ? this.defaultKey : var2;
   }

   @Nonnull
   @Override
   public T getValue(@Nullable ResourceLocation var1) {
      Object var2 = super.getValue(var1);
      return (T)(var2 == null ? this.defaultValue.value() : var2);
   }

   @Override
   public Optional<T> getOptional(@Nullable ResourceLocation var1) {
      return Optional.ofNullable(super.getValue(var1));
   }

   @Override
   public Optional<Holder.Reference<T>> getAny() {
      return Optional.ofNullable(this.defaultValue);
   }

   @Nonnull
   @Override
   public T byId(int var1) {
      Object var2 = super.byId(var1);
      return (T)(var2 == null ? this.defaultValue.value() : var2);
   }

   @Override
   public Optional<Holder.Reference<T>> getRandom(RandomSource var1) {
      return super.getRandom(var1).or(() -> Optional.of(this.defaultValue));
   }

   @Override
   public ResourceLocation getDefaultKey() {
      return this.defaultKey;
   }
}
