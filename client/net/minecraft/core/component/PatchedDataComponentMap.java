package net.minecraft.core.component;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public final class PatchedDataComponentMap implements DataComponentMap {
   private final DataComponentMap prototype;
   private Reference2ObjectMap<DataComponentType<?>, Object> patch;
   private boolean copyOnWrite;

   public PatchedDataComponentMap(final DataComponentMap prototype) {
      this(prototype, Reference2ObjectMaps.emptyMap(), true);
   }

   private PatchedDataComponentMap(final DataComponentMap prototype, final Reference2ObjectMap<DataComponentType<?>, Object> patch, final boolean copyOnWrite) {
      super();
      this.prototype = prototype;
      this.patch = patch;
      this.copyOnWrite = copyOnWrite;
   }

   public static PatchedDataComponentMap fromPatch(final DataComponentMap prototype, final DataComponentPatch patch) {
      if (isPatchSanitized(prototype, patch.map)) {
         return new PatchedDataComponentMap(prototype, patch.map, true);
      } else {
         PatchedDataComponentMap map = new PatchedDataComponentMap(prototype);
         map.applyPatch(patch);
         return map;
      }
   }

   private static boolean isPatchSanitized(final DataComponentMap prototype, final Reference2ObjectMap<DataComponentType<?>, Object> patch) {
      ObjectIterator var2 = Reference2ObjectMaps.fastIterable(patch).iterator();

      while(var2.hasNext()) {
         Map.Entry<DataComponentType<?>, Object> entry = (Map.Entry)var2.next();
         Object defaultValue = prototype.get((DataComponentType)entry.getKey());
         Object value = entry.getValue();
         if (value.equals(defaultValue)) {
            return false;
         }

         if (Removed.isRemoved(value) && defaultValue == null) {
            return false;
         }
      }

      return true;
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      return (T)DataComponentPatch.getFromPatchAndPrototype(this.patch, this.prototype, type);
   }

   public boolean hasNonDefault(final DataComponentType<?> type) {
      return this.patch.containsKey(type);
   }

   public <T> @Nullable T set(final DataComponentType<T> type, final @Nullable T value) {
      this.ensureMapOwnership();
      T defaultValue = (T)this.prototype.get(type);
      Object lastValue;
      if (Objects.equals(value, defaultValue)) {
         lastValue = this.patch.remove(type);
      } else {
         lastValue = this.patch.put(type, Removed.nullToRemoved(value));
      }

      return (T)(lastValue != null ? Objects.requireNonNullElse(Removed.removedToNull(lastValue), defaultValue) : defaultValue);
   }

   public <T> @Nullable T set(final TypedDataComponent<T> value) {
      return (T)this.set(value.type(), value.value());
   }

   public <T> T remove(final DataComponentType<? extends T> type) {
      this.ensureMapOwnership();
      T defaultValue = (T)this.prototype.get(type);
      Object lastValue;
      if (defaultValue != null) {
         lastValue = this.patch.put(type, Removed.INSTANCE);
      } else {
         lastValue = this.patch.remove(type);
      }

      return (T)(lastValue != null ? Removed.removedToNull(lastValue) : defaultValue);
   }

   public void applyPatch(final DataComponentPatch patch) {
      this.ensureMapOwnership();
      ObjectIterator var2 = Reference2ObjectMaps.fastIterable(patch.map).iterator();

      while(var2.hasNext()) {
         Map.Entry<DataComponentType<?>, Object> entry = (Map.Entry)var2.next();
         this.applyPatch((DataComponentType)entry.getKey(), entry.getValue());
      }

   }

   private void applyPatch(final DataComponentType<?> type, final Object value) {
      Object defaultValue = this.prototype.get(type);
      if (Removed.isNotRemoved(value)) {
         if (value.equals(defaultValue)) {
            this.patch.remove(type);
         } else {
            this.patch.put(type, value);
         }
      } else if (defaultValue != null) {
         this.patch.put(type, Removed.INSTANCE);
      } else {
         this.patch.remove(type);
      }

   }

   public void restorePatch(final DataComponentPatch patch) {
      this.ensureMapOwnership();
      this.patch.clear();
      this.patch.putAll(patch.map);
   }

   public void clearPatch() {
      this.ensureMapOwnership();
      this.patch.clear();
   }

   public void setAll(final DataComponentMap components) {
      for(TypedDataComponent<?> entry : components) {
         entry.applyTo(this);
      }

   }

   private void ensureMapOwnership() {
      if (this.copyOnWrite) {
         this.patch = new Reference2ObjectArrayMap(this.patch);
         this.copyOnWrite = false;
      }

   }

   public Set<DataComponentType<?>> keySet() {
      if (this.patch.isEmpty()) {
         return this.prototype.keySet();
      } else {
         Set<DataComponentType<?>> components = new ReferenceArraySet(this.prototype.keySet());
         ObjectIterator var2 = Reference2ObjectMaps.fastIterable(this.patch).iterator();

         while(var2.hasNext()) {
            Reference2ObjectMap.Entry<DataComponentType<?>, Object> entry = (Reference2ObjectMap.Entry)var2.next();
            Object value = entry.getValue();
            if (Removed.isNotRemoved(value)) {
               components.add((DataComponentType)entry.getKey());
            } else {
               components.remove(entry.getKey());
            }
         }

         return components;
      }
   }

   public Iterator<TypedDataComponent<?>> iterator() {
      if (this.patch.isEmpty()) {
         return this.prototype.iterator();
      } else {
         List<TypedDataComponent<?>> components = new ArrayList(this.patch.size() + this.prototype.size());
         ObjectIterator var2 = Reference2ObjectMaps.fastIterable(this.patch).iterator();

         while(var2.hasNext()) {
            Reference2ObjectMap.Entry<DataComponentType<?>, Object> entry = (Reference2ObjectMap.Entry)var2.next();
            Object value = entry.getValue();
            if (Removed.isNotRemoved(value)) {
               components.add(TypedDataComponent.createUnchecked((DataComponentType)entry.getKey(), value));
            }
         }

         for(TypedDataComponent<?> component : this.prototype) {
            if (!this.patch.containsKey(component.type())) {
               components.add(component);
            }
         }

         return components.iterator();
      }
   }

   public int size() {
      int size = this.prototype.size();
      ObjectIterator var2 = Reference2ObjectMaps.fastIterable(this.patch).iterator();

      while(var2.hasNext()) {
         Reference2ObjectMap.Entry<DataComponentType<?>, Object> entry = (Reference2ObjectMap.Entry)var2.next();
         boolean inPatch = Removed.isNotRemoved(entry.getValue());
         boolean inPrototype = this.prototype.has((DataComponentType)entry.getKey());
         if (inPatch != inPrototype) {
            size += inPatch ? 1 : -1;
         }
      }

      return size;
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

   public DataComponentMap toImmutableMap() {
      return (DataComponentMap)(this.patch.isEmpty() ? this.prototype : this.copy());
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         boolean var10000;
         if (obj instanceof PatchedDataComponentMap) {
            PatchedDataComponentMap otherMap = (PatchedDataComponentMap)obj;
            if (this.prototype.equals(otherMap.prototype) && this.patch.equals(otherMap.patch)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.prototype.hashCode() + this.patch.hashCode() * 31;
   }

   public String toString() {
      Stream var10000 = this.stream().map(TypedDataComponent::toString);
      return "{" + (String)var10000.collect(Collectors.joining(", ")) + "}";
   }
}
