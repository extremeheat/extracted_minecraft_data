package net.minecraft.core.component;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public final class PatchedDataComponentMap implements DataComponentMap {
   private final DataComponentMap prototype;
   private Reference2ObjectMap<DataComponentType<?>, Optional<?>> patch;
   private boolean copyOnWrite;

   public PatchedDataComponentMap(DataComponentMap var1) {
      this(var1, Reference2ObjectMaps.emptyMap(), true);
   }

   private PatchedDataComponentMap(DataComponentMap var1, Reference2ObjectMap<DataComponentType<?>, Optional<?>> var2, boolean var3) {
      super();
      this.prototype = var1;
      this.patch = var2;
      this.copyOnWrite = var3;
   }

   public static PatchedDataComponentMap fromPatch(DataComponentMap var0, DataComponentPatch var1) {
      if (isPatchSanitized(var0, var1.map)) {
         return new PatchedDataComponentMap(var0, var1.map, true);
      } else {
         PatchedDataComponentMap var2 = new PatchedDataComponentMap(var0);
         var2.applyPatch(var1);
         return var2;
      }
   }

   private static boolean isPatchSanitized(DataComponentMap var0, Reference2ObjectMap<DataComponentType<?>, Optional<?>> var1) {
      ObjectIterator var2 = Reference2ObjectMaps.fastIterable(var1).iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         Object var4 = var0.get((DataComponentType)var3.getKey());
         Optional var5 = (Optional)var3.getValue();
         if (var5.isPresent() && var5.get().equals(var4)) {
            return false;
         }

         if (var5.isEmpty() && var4 == null) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   @Override
   public <T> T get(DataComponentType<? extends T> var1) {
      Optional var2 = (Optional)this.patch.get(var1);
      return (T)(var2 != null ? var2.orElse((T)null) : this.prototype.get(var1));
   }

   @Nullable
   public <T> T set(DataComponentType<? super T> var1, @Nullable T var2) {
      this.ensureMapOwnership();
      Object var3 = this.prototype.get(var1);
      Optional var4;
      if (Objects.equals(var2, var3)) {
         var4 = (Optional)this.patch.remove(var1);
      } else {
         var4 = (Optional)this.patch.put(var1, Optional.ofNullable(var2));
      }

      return (T)(var4 != null ? var4.orElse(var3) : var3);
   }

   @Nullable
   public <T> T remove(DataComponentType<? extends T> var1) {
      this.ensureMapOwnership();
      Object var2 = this.prototype.get(var1);
      Optional var3;
      if (var2 != null) {
         var3 = (Optional)this.patch.put(var1, Optional.empty());
      } else {
         var3 = (Optional)this.patch.remove(var1);
      }

      return (T)(var3 != null ? var3.orElse((T)null) : var2);
   }

   public void applyPatch(DataComponentPatch var1) {
      this.ensureMapOwnership();
      ObjectIterator var2 = Reference2ObjectMaps.fastIterable(var1.map).iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         this.applyPatch((DataComponentType<?>)var3.getKey(), (Optional<?>)var3.getValue());
      }
   }

   private void applyPatch(DataComponentType<?> var1, Optional<?> var2) {
      Object var3 = this.prototype.get(var1);
      if (var2.isPresent()) {
         if (var2.get().equals(var3)) {
            this.patch.remove(var1);
         } else {
            this.patch.put(var1, var2);
         }
      } else if (var3 != null) {
         this.patch.put(var1, Optional.empty());
      } else {
         this.patch.remove(var1);
      }
   }

   public void setAll(DataComponentMap var1) {
      for(TypedDataComponent var3 : var1) {
         var3.applyTo(this);
      }
   }

   private void ensureMapOwnership() {
      if (this.copyOnWrite) {
         this.patch = new Reference2ObjectArrayMap(this.patch);
         this.copyOnWrite = false;
      }
   }

   @Override
   public Set<DataComponentType<?>> keySet() {
      if (this.patch.isEmpty()) {
         return this.prototype.keySet();
      } else {
         ReferenceArraySet var1 = new ReferenceArraySet(this.prototype.keySet());
         ObjectIterator var2 = Reference2ObjectMaps.fastIterable(this.patch).iterator();

         while(var2.hasNext()) {
            it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry var3 = (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry)var2.next();
            Optional var4 = (Optional)var3.getValue();
            if (var4.isPresent()) {
               var1.add((DataComponentType)var3.getKey());
            } else {
               var1.remove(var3.getKey());
            }
         }

         return var1;
      }
   }

   @Override
   public Iterator<TypedDataComponent<?>> iterator() {
      if (this.patch.isEmpty()) {
         return this.prototype.iterator();
      } else {
         ArrayList var1 = new ArrayList(this.patch.size() + this.prototype.size());
         ObjectIterator var2 = Reference2ObjectMaps.fastIterable(this.patch).iterator();

         while(var2.hasNext()) {
            it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry var3 = (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry)var2.next();
            if (((Optional)var3.getValue()).isPresent()) {
               var1.add(TypedDataComponent.createUnchecked((DataComponentType)var3.getKey(), ((Optional)var3.getValue()).get()));
            }
         }

         for(TypedDataComponent var5 : this.prototype) {
            if (!this.patch.containsKey(var5.type())) {
               var1.add(var5);
            }
         }

         return var1.iterator();
      }
   }

   @Override
   public int size() {
      int var1 = this.prototype.size();
      ObjectIterator var2 = Reference2ObjectMaps.fastIterable(this.patch).iterator();

      while(var2.hasNext()) {
         it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry var3 = (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry)var2.next();
         boolean var4 = ((Optional)var3.getValue()).isPresent();
         boolean var5 = this.prototype.has((DataComponentType<?>)var3.getKey());
         if (var4 != var5) {
            var1 += var4 ? 1 : -1;
         }
      }

      return var1;
   }

   public DataComponentPatch asPatch() {
      if (this.patch.isEmpty()) {
         return DataComponentPatch.EMPTY;
      } else {
         this.copyOnWrite = true;
         return new DataComponentPatch(this.patch);
      }
   }

   public PatchedDataComponentMap copy() {
      this.copyOnWrite = true;
      return new PatchedDataComponentMap(this.prototype, this.patch, true);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof PatchedDataComponentMap var2 && this.prototype.equals(var2.prototype) && this.patch.equals(var2.patch)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.prototype.hashCode() + this.patch.hashCode() * 31;
   }

   @Override
   public String toString() {
      return "{" + (String)this.stream().map(TypedDataComponent::toString).collect(Collectors.joining(", ")) + "}";
   }
}