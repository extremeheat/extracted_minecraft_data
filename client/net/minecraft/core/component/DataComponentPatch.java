package net.minecraft.core.component;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

public final class DataComponentPatch {
   public static final DataComponentPatch EMPTY = new DataComponentPatch(Reference2ObjectMaps.emptyMap());
   public static final Codec<DataComponentPatch> CODEC = Codec.dispatchedMap(DataComponentPatch.PatchKey.CODEC, DataComponentPatch.PatchKey::valueCodec)
      .xmap(var0 -> {
         if (var0.isEmpty()) {
            return EMPTY;
         } else {
            Reference2ObjectArrayMap var1 = new Reference2ObjectArrayMap(var0.size());

            for (Entry var3 : var0.entrySet()) {
               DataComponentPatch.PatchKey var4 = (DataComponentPatch.PatchKey)var3.getKey();
               if (var4.removed()) {
                  var1.put(var4.type(), Optional.empty());
               } else {
                  var1.put(var4.type(), Optional.of(var3.getValue()));
               }
            }

            return new DataComponentPatch(var1);
         }
      }, var0 -> {
         Reference2ObjectArrayMap var1 = new Reference2ObjectArrayMap(var0.map.size());
         ObjectIterator var2 = Reference2ObjectMaps.fastIterable(var0.map).iterator();

         while (var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            DataComponentType var4 = (DataComponentType)var3.getKey();
            if (!var4.isTransient()) {
               Optional var5 = (Optional)var3.getValue();
               if (var5.isPresent()) {
                  var1.put(new DataComponentPatch.PatchKey(var4, false), var5.get());
               } else {
                  var1.put(new DataComponentPatch.PatchKey(var4, true), Unit.INSTANCE);
               }
            }
         }

         return var1;
      });
   public static final StreamCodec<RegistryFriendlyByteBuf, DataComponentPatch> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, DataComponentPatch>() {
      public DataComponentPatch decode(RegistryFriendlyByteBuf var1) {
         int var2 = var1.readVarInt();
         int var3 = var1.readVarInt();
         if (var2 == 0 && var3 == 0) {
            return DataComponentPatch.EMPTY;
         } else {
            int var4 = var2 + var3;
            Reference2ObjectArrayMap var5 = new Reference2ObjectArrayMap(Math.min(var4, 65536));

            for (int var6 = 0; var6 < var2; var6++) {
               DataComponentType var7 = DataComponentType.STREAM_CODEC.decode(var1);
               Object var8 = var7.streamCodec().decode(var1);
               var5.put(var7, Optional.of(var8));
            }

            for (int var9 = 0; var9 < var3; var9++) {
               DataComponentType var10 = DataComponentType.STREAM_CODEC.decode(var1);
               var5.put(var10, Optional.empty());
            }

            return new DataComponentPatch(var5);
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

            while (var5.hasNext()) {
               it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry var6 = (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry)var5.next();
               if (((Optional)var6.getValue()).isPresent()) {
                  var3++;
               } else {
                  var4++;
               }
            }

            var1.writeVarInt(var3);
            var1.writeVarInt(var4);
            var5 = Reference2ObjectMaps.fastIterable(var2.map).iterator();

            while (var5.hasNext()) {
               it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry var11 = (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry)var5.next();
               Optional var7 = (Optional)var11.getValue();
               if (var7.isPresent()) {
                  DataComponentType var8 = (DataComponentType)var11.getKey();
                  DataComponentType.STREAM_CODEC.encode(var1, var8);
                  encodeComponent(var1, var8, var7.get());
               }
            }

            var5 = Reference2ObjectMaps.fastIterable(var2.map).iterator();

            while (var5.hasNext()) {
               it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry var12 = (it.unimi.dsi.fastutil.objects.Reference2ObjectMap.Entry)var5.next();
               if (((Optional)var12.getValue()).isEmpty()) {
                  DataComponentType var13 = (DataComponentType)var12.getKey();
                  DataComponentType.STREAM_CODEC.encode(var1, var13);
               }
            }
         }
      }

      private static <T> void encodeComponent(RegistryFriendlyByteBuf var0, DataComponentType<T> var1, Object var2) {
         var1.streamCodec().encode(var0, var2);
      }
   };
   private static final String REMOVED_PREFIX = "!";
   final Reference2ObjectMap<DataComponentType<?>, Optional<?>> map;

   DataComponentPatch(Reference2ObjectMap<DataComponentType<?>, Optional<?>> var1) {
      super();
      this.map = var1;
   }

   public static DataComponentPatch.Builder builder() {
      return new DataComponentPatch.Builder();
   }

   @Nullable
   public <T> Optional<? extends T> get(DataComponentType<? extends T> var1) {
      return (Optional<? extends T>)this.map.get(var1);
   }

   public Set<Entry<DataComponentType<?>, Optional<?>>> entrySet() {
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

   public DataComponentPatch.SplitResult split() {
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
         return new DataComponentPatch.SplitResult(var1.build(), var2);
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof DataComponentPatch var2 && this.map.equals(var2.map)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.map.hashCode();
   }

   @Override
   public String toString() {
      return toString(this.map);
   }

   static String toString(Reference2ObjectMap<DataComponentType<?>, Optional<?>> var0) {
      StringBuilder var1 = new StringBuilder();
      var1.append('{');
      boolean var2 = true;
      ObjectIterator var3 = Reference2ObjectMaps.fastIterable(var0).iterator();

      while (var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
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

   public static class Builder {
      private final Reference2ObjectMap<DataComponentType<?>, Optional<?>> map = new Reference2ObjectArrayMap();

      Builder() {
         super();
      }

      public <T> DataComponentPatch.Builder set(DataComponentType<T> var1, T var2) {
         this.map.put(var1, Optional.of(var2));
         return this;
      }

      public <T> DataComponentPatch.Builder remove(DataComponentType<T> var1) {
         this.map.put(var1, Optional.empty());
         return this;
      }

      public <T> DataComponentPatch.Builder set(TypedDataComponent<T> var1) {
         return this.set(var1.type(), var1.value());
      }

      public DataComponentPatch build() {
         return this.map.isEmpty() ? DataComponentPatch.EMPTY : new DataComponentPatch(this.map);
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
