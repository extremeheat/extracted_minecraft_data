package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DefaultedRegistry<T> extends MappedRegistry<T> {
   private final ResourceLocation defaultKey;
   private T defaultValue;

   public DefaultedRegistry(String var1, ResourceKey<? extends Registry<T>> var2, Lifecycle var3) {
      super(var2, var3);
      this.defaultKey = new ResourceLocation(var1);
   }

   public <V extends T> V registerMapping(int var1, ResourceKey<T> var2, V var3, Lifecycle var4) {
      if (this.defaultKey.equals(var2.location())) {
         this.defaultValue = var3;
      }

      return super.registerMapping(var1, var2, var3, var4);
   }

   public int getId(@Nullable T var1) {
      int var2 = super.getId(var1);
      return var2 == -1 ? super.getId(this.defaultValue) : var2;
   }

   @Nonnull
   public ResourceLocation getKey(T var1) {
      ResourceLocation var2 = super.getKey(var1);
      return var2 == null ? this.defaultKey : var2;
   }

   @Nonnull
   public T get(@Nullable ResourceLocation var1) {
      Object var2 = super.get(var1);
      return var2 == null ? this.defaultValue : var2;
   }

   public Optional<T> getOptional(@Nullable ResourceLocation var1) {
      return Optional.ofNullable(super.get(var1));
   }

   @Nonnull
   public T byId(int var1) {
      Object var2 = super.byId(var1);
      return var2 == null ? this.defaultValue : var2;
   }

   @Nonnull
   public T getRandom(Random var1) {
      Object var2 = super.getRandom(var1);
      return var2 == null ? this.defaultValue : var2;
   }

   public ResourceLocation getDefaultKey() {
      return this.defaultKey;
   }
}
