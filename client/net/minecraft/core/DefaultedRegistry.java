package net.minecraft.core;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class DefaultedRegistry<T> extends MappedRegistry<T> {
   private final ResourceLocation defaultKey;
   private T defaultValue;

   public DefaultedRegistry(String var1) {
      super();
      this.defaultKey = new ResourceLocation(var1);
   }

   public <V extends T> V registerMapping(int var1, ResourceLocation var2, V var3) {
      if (this.defaultKey.equals(var2)) {
         this.defaultValue = var3;
      }

      return super.registerMapping(var1, var2, var3);
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
