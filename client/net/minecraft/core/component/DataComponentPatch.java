package net.minecraft.core.component;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;

public final class DataComponentPatch {
   public static final DataComponentPatch EMPTY = new DataComponentPatch(Reference2ObjectMaps.emptyMap());
   public static final Codec<DataComponentPatch> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentPatch> STREAM_CODEC;
   private static final String REMOVED_PREFIX = "!";
   final Reference2ObjectMap<DataComponentType<?>, Optional<?>> map;

   DataComponentPatch(Reference2ObjectMap<DataComponentType<?>, Optional<?>> var1) {
      super();
      this.map = var1;
   }

   public static Builder builder() {
      return new Builder();
   }

   @Nullable
   public <T> Optional<? extends T> get(DataComponentType<? extends T> var1) {
      return (Optional)this.map.get(var1);
   }

   public Set<Map.Entry<DataComponentType<?>, Optional<?>>> entrySet() {
      return this.map.entrySet();
   }

   public int size() {
      return this.map.size();
   }

   public DataComponentPatch forget(Predicate<DataComponentType<?>> var1) {
      if (this.isEmpty()) {
         return EMPTY;
      } else {
         Reference2ObjectArrayMap var2 = new Reference2ObjectArrayMap(this.map);
         var2.keySet().removeIf(var1);
         return var2.isEmpty() ? EMPTY : new DataComponentPatch(var2);
      }
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public SplitResult split() {
      if (this.isEmpty()) {
         return DataComponentPatch.SplitResult.EMPTY;
      } else {
         DataComponentMap.Builder var1 = DataComponentMap.builder();
         Set var2 = Sets.newIdentityHashSet();
         this.map.forEach((var2x, var3) -> {
            if (var3.isPresent()) {
               var1.setUnchecked(var2x, var3.get());
            } else {
               var2.add(var2x);
            }

         });
         return new SplitResult(var1.build(), var2);
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof DataComponentPatch) {
            DataComponentPatch var2 = (DataComponentPatch)var1;
            if (this.map.equals(var2.map)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.map.hashCode();
   }

   public String toString() {
      return toString(this.map);
   }

   static String toString(Reference2ObjectMap<DataComponentType<?>, Optional<?>> var0) {
      StringBuilder var1 = new StringBuilder();
      var1.append('{');
      boolean var2 = true;
      ObjectIterator var3 = Reference2ObjectMaps.fastIterable(var0).iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         if (var2) {
            var2 = false;
         } else {
            var1.append(", ");
         }

         Optional var5 = (Optional)var4.getValue();
         if (var5.isPresent()) {
            var1.append(var4.getKey());
            var1.append("=>");
            var1.append(var5.get());
         } else {
            var1.append("!");
            var1.append(var4.getKey());
         }
      }

      var1.append('}');
      return var1.toString();
   }

   static {
      CODEC = Codec.dispatchedMap(DataComponentPatch.PatchKey.CODEC, PatchKey::valueCodec).xmap((var0) -> {
         if (var0.isEmpty()) {
            return EMPTY;
         } else {
            Reference2ObjectArrayMap var1 = new Reference2ObjectArrayMap(var0.size());
            Iterator var2 = var0.entrySet().iterator();

            while(var2.hasNext()) {
               Map.Entry var3 = (Map.Entry)var2.next();
               PatchKey var4 = (PatchKey)var3.getKey();
               if (var4.removed()) {
                  var1.put(var4.type(), Optional.empty());
               } else {
                  var1.put(var4.type(), Optional.of(var3.getValue()));
               }
            }

            return new DataComponentPatch(var1);
         }
      }, (var0) -> {
         Reference2ObjectArrayMap var1 = new Reference2ObjectArrayMap(var0.map.size());
         ObjectIterator var2 = Reference2ObjectMaps.fastIterable(var0.map).iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            DataComponentType var4 = (DataComponentType)var3.getKey();
            if (!var4.isTransient()) {
               Optional var5 = (Optional)var3.getValue();
               if (var5.isPresent()) {
                  var1.put(new PatchKey(var4, false), var5.get());
               } else {
                  var1.put(new PatchKey(var4, true), Unit.INSTANCE);
               }
            }
         }

         return var1;
      });
      STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, DataComponentPatch>() {
         public DataComponentPatch decode(RegistryFriendlyByteBuf var1) {
            int var2 = var1.readVarInt();
            int var3 = var1.readVarInt();
            if (var2 == 0 && var3 == 0) {
               return DataComponentPatch.EMPTY;
            } else {
               Reference2ObjectArrayMap var4 = new Reference2ObjectArrayMap(var2 + var3);

               int var5;
               DataComponentType var6;
               for(var5 = 0; var5 < var2; ++var5) {
                  var6 = (DataComponentType)DataComponentType.STREAM_CODEC.decode(var1);
                  Object var7 = var6.streamCodec().decode(var1);
                  var4.put(var6, Optional.of(var7));
               }

               for(var5 = 0; var5 < var3; ++var5) {
                  var6 = (DataComponentType)DataComponentType.STREAM_CODEC.decode(var1);
                  var4.put(var6, Optional.empty());
               }

               return new DataComponentPatch(var4);
            }
         }

         public void encode(RegistryFriendlyByteBuf var1, DataComponentPatch var2) {
            if (var2.isEmpty()) {
               var1.writeVarInt(0);
               var1.writeVarInt(0);
            } else {
               int var3 = 0;
               int var4 = 0;
               ObjectIterator var5 = Reference2ObjectMaps.fastIterable(var2.map).iterator();

               Reference2ObjectMap.Entry var6;
               while(var5.hasNext()) {
                  var6 = (Reference2ObjectMap.Entry)var5.next();
                  if (((Optional)var6.getValue()).isPresent()) {
                     ++var3;
                  } else {
                     ++var4;
                  }
               }

               var1.writeVarInt(var3);
               var1.writeVarInt(var4);
               var5 = Reference2ObjectMaps.fastIterable(var2.map).iterator();

               while(var5.hasNext()) {
                  var6 = (Reference2ObjectMap.Entry)var5.next();
                  Optional var7 = (Optional)var6.getValue();
                  if (var7.isPresent()) {
                     DataComponentType var8 = (DataComponentType)var6.getKey();
                     DataComponentType.STREAM_CODEC.encode(var1, var8);
                     encodeComponent(var1, var8, var7.get());
                  }
               }

               var5 = Reference2ObjectMaps.fastIterable(var2.map).iterator();

               while(var5.hasNext()) {
                  var6 = (Reference2ObjectMap.Entry)var5.next();
                  if (((Optional)var6.getValue()).isEmpty()) {
                     DataComponentType var9 = (DataComponentType)var6.getKey();
                     DataComponentType.STREAM_CODEC.encode(var1, var9);
                  }
               }

            }
         }

         private static <T> void encodeComponent(RegistryFriendlyByteBuf var0, DataComponentType<T> var1, Object var2) {
            var1.streamCodec().encode(var0, var2);
         }

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((RegistryFriendlyByteBuf)var1, (DataComponentPatch)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((RegistryFriendlyByteBuf)var1);
         }
      };
   }

   public static class Builder {
      private final Reference2ObjectMap<DataComponentType<?>, Optional<?>> map = new Reference2ObjectArrayMap();

      Builder() {
         super();
      }

      public <T> Builder set(DataComponentType<T> var1, T var2) {
         this.map.put(var1, Optional.of(var2));
         return this;
      }

      public <T> Builder remove(DataComponentType<T> var1) {
         this.map.put(var1, Optional.empty());
         return this;
      }

      public <T> Builder set(TypedDataComponent<T> var1) {
         return this.set(var1.type(), var1.value());
      }

      public DataComponentPatch build() {
         return this.map.isEmpty() ? DataComponentPatch.EMPTY : new DataComponentPatch(this.map);
      }
   }

   public static record SplitResult(DataComponentMap added, Set<DataComponentType<?>> removed) {
      public static final SplitResult EMPTY;

      public SplitResult(DataComponentMap added, Set<DataComponentType<?>> removed) {
         super();
         this.added = added;
         this.removed = removed;
      }

      public DataComponentMap added() {
         return this.added;
      }

      public Set<DataComponentType<?>> removed() {
         return this.removed;
      }

      static {
         EMPTY = new SplitResult(DataComponentMap.EMPTY, Set.of());
      }
   }

   static record PatchKey(DataComponentType<?> type, boolean removed) {
      public static final Codec<PatchKey> CODEC;

      PatchKey(DataComponentType<?> type, boolean removed) {
         super();
         this.type = type;
         this.removed = removed;
      }

      public Codec<?> valueCodec() {
         return this.removed ? Codec.EMPTY.codec() : this.type.codecOrThrow();
      }

      public DataComponentType<?> type() {
         return this.type;
      }

      public boolean removed() {
         return this.removed;
      }

      static {
         CODEC = Codec.STRING.flatXmap((var0) -> {
            boolean var1 = var0.startsWith("!");
            if (var1) {
               var0 = var0.substring("!".length());
            }

            ResourceLocation var2 = ResourceLocation.tryParse(var0);
            DataComponentType var3 = (DataComponentType)BuiltInRegistries.DATA_COMPONENT_TYPE.get(var2);
            if (var3 == null) {
               return DataResult.error(() -> {
                  return "No component with type: '" + String.valueOf(var2) + "'";
               });
            } else {
               return var3.isTransient() ? DataResult.error(() -> {
                  return "'" + String.valueOf(var2) + "' is not a persistent component";
               }) : DataResult.success(new PatchKey(var3, var1));
            }
         }, (var0) -> {
            DataComponentType var1 = var0.type();
            ResourceLocation var2 = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(var1);
            return var2 == null ? DataResult.error(() -> {
               return "Unregistered component: " + String.valueOf(var1);
            }) : DataResult.success(var0.removed() ? "!" + String.valueOf(var2) : var2.toString());
         });
      }
   }
}
