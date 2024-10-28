package net.minecraft.util.parsing.packrat;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.Objects;
import javax.annotation.Nullable;

public final class Scope {
   private final Object2ObjectMap<Atom<?>, Object> values = new Object2ObjectArrayMap();

   public Scope() {
      super();
   }

   public <T> void put(Atom<T> var1, @Nullable T var2) {
      this.values.put(var1, var2);
   }

   @Nullable
   public <T> T get(Atom<T> var1) {
      return this.values.get(var1);
   }

   public <T> T getOrThrow(Atom<T> var1) {
      return Objects.requireNonNull(this.get(var1));
   }

   public <T> T getOrDefault(Atom<T> var1, T var2) {
      return Objects.requireNonNullElse(this.get(var1), var2);
   }

   @Nullable
   @SafeVarargs
   public final <T> T getAny(Atom<T>... var1) {
      Atom[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Atom var5 = var2[var4];
         Object var6 = this.get(var5);
         if (var6 != null) {
            return var6;
         }
      }

      return null;
   }

   @SafeVarargs
   public final <T> T getAnyOrThrow(Atom<T>... var1) {
      return Objects.requireNonNull(this.getAny(var1));
   }

   public String toString() {
      return this.values.toString();
   }

   public void putAll(Scope var1) {
      this.values.putAll(var1.values);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof Scope) {
         Scope var2 = (Scope)var1;
         return this.values.equals(var2.values);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.values.hashCode();
   }
}
