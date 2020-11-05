package net.minecraft.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.RegistryDataPackCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MappedRegistry<T> extends WritableRegistry<T> {
   protected static final Logger LOGGER = LogManager.getLogger();
   private final ObjectList<T> byId = new ObjectArrayList(256);
   private final Object2IntMap<T> toId = new Object2IntOpenCustomHashMap(Util.identityStrategy());
   private final BiMap<ResourceLocation, T> storage;
   private final BiMap<ResourceKey<T>, T> keyStorage;
   private final Map<T, Lifecycle> lifecycles;
   private Lifecycle elementsLifecycle;
   protected Object[] randomCache;
   private int nextId;

   public MappedRegistry(ResourceKey<? extends Registry<T>> var1, Lifecycle var2) {
      super(var1, var2);
      this.toId.defaultReturnValue(-1);
      this.storage = HashBiMap.create();
      this.keyStorage = HashBiMap.create();
      this.lifecycles = Maps.newIdentityHashMap();
      this.elementsLifecycle = var2;
   }

   public static <T> MapCodec<MappedRegistry.RegistryEntry<T>> withNameAndId(ResourceKey<? extends Registry<T>> var0, MapCodec<T> var1) {
      return RecordCodecBuilder.mapCodec((var2) -> {
         return var2.group(ResourceLocation.CODEC.xmap(ResourceKey.elementKey(var0), ResourceKey::location).fieldOf("name").forGetter((var0x) -> {
            return var0x.key;
         }), Codec.INT.fieldOf("id").forGetter((var0x) -> {
            return var0x.id;
         }), var1.forGetter((var0x) -> {
            return var0x.value;
         })).apply(var2, MappedRegistry.RegistryEntry::new);
      });
   }

   public <V extends T> V registerMapping(int var1, ResourceKey<T> var2, V var3, Lifecycle var4) {
      return this.registerMapping(var1, var2, var3, var4, true);
   }

   private <V extends T> V registerMapping(int var1, ResourceKey<T> var2, V var3, Lifecycle var4, boolean var5) {
      Validate.notNull(var2);
      Validate.notNull(var3);
      this.byId.size(Math.max(this.byId.size(), var1 + 1));
      this.byId.set(var1, var3);
      this.toId.put(var3, var1);
      this.randomCache = null;
      if (var5 && this.keyStorage.containsKey(var2)) {
         LOGGER.debug("Adding duplicate key '{}' to registry", var2);
      }

      if (this.storage.containsValue(var3)) {
         LOGGER.error("Adding duplicate value '{}' to registry", var3);
      }

      this.storage.put(var2.location(), var3);
      this.keyStorage.put(var2, var3);
      this.lifecycles.put(var3, var4);
      this.elementsLifecycle = this.elementsLifecycle.add(var4);
      if (this.nextId <= var1) {
         this.nextId = var1 + 1;
      }

      return var3;
   }

   public <V extends T> V register(ResourceKey<T> var1, V var2, Lifecycle var3) {
      return this.registerMapping(this.nextId, var1, var2, var3);
   }

   public <V extends T> V registerOrOverride(OptionalInt var1, ResourceKey<T> var2, V var3, Lifecycle var4) {
      Validate.notNull(var2);
      Validate.notNull(var3);
      Object var5 = this.keyStorage.get(var2);
      int var6;
      if (var5 == null) {
         var6 = var1.isPresent() ? var1.getAsInt() : this.nextId;
      } else {
         var6 = this.toId.getInt(var5);
         if (var1.isPresent() && var1.getAsInt() != var6) {
            throw new IllegalStateException("ID mismatch");
         }

         this.toId.removeInt(var5);
         this.lifecycles.remove(var5);
      }

      return this.registerMapping(var6, var2, var3, var4, false);
   }

   @Nullable
   public ResourceLocation getKey(T var1) {
      return (ResourceLocation)this.storage.inverse().get(var1);
   }

   public Optional<ResourceKey<T>> getResourceKey(T var1) {
      return Optional.ofNullable(this.keyStorage.inverse().get(var1));
   }

   public int getId(@Nullable T var1) {
      return this.toId.getInt(var1);
   }

   @Nullable
   public T get(@Nullable ResourceKey<T> var1) {
      return this.keyStorage.get(var1);
   }

   @Nullable
   public T byId(int var1) {
      return var1 >= 0 && var1 < this.byId.size() ? this.byId.get(var1) : null;
   }

   public Lifecycle lifecycle(T var1) {
      return (Lifecycle)this.lifecycles.get(var1);
   }

   public Lifecycle elementsLifecycle() {
      return this.elementsLifecycle;
   }

   public Iterator<T> iterator() {
      return Iterators.filter(this.byId.iterator(), Objects::nonNull);
   }

   @Nullable
   public T get(@Nullable ResourceLocation var1) {
      return this.storage.get(var1);
   }

   public Set<ResourceLocation> keySet() {
      return Collections.unmodifiableSet(this.storage.keySet());
   }

   public Set<Entry<ResourceKey<T>, T>> entrySet() {
      return Collections.unmodifiableMap(this.keyStorage).entrySet();
   }

   @Nullable
   public T getRandom(Random var1) {
      if (this.randomCache == null) {
         Set var2 = this.storage.values();
         if (var2.isEmpty()) {
            return null;
         }

         this.randomCache = var2.toArray(new Object[var2.size()]);
      }

      return Util.getRandom(this.randomCache, var1);
   }

   public boolean containsKey(ResourceLocation var1) {
      return this.storage.containsKey(var1);
   }

   public static <T> Codec<MappedRegistry<T>> networkCodec(ResourceKey<? extends Registry<T>> var0, Lifecycle var1, Codec<T> var2) {
      return withNameAndId(var0, var2.fieldOf("element")).codec().listOf().xmap((var2x) -> {
         MappedRegistry var3 = new MappedRegistry(var0, var1);
         Iterator var4 = var2x.iterator();

         while(var4.hasNext()) {
            MappedRegistry.RegistryEntry var5 = (MappedRegistry.RegistryEntry)var4.next();
            var3.registerMapping(var5.id, var5.key, var5.value, var1);
         }

         return var3;
      }, (var0x) -> {
         Builder var1 = ImmutableList.builder();
         Iterator var2 = var0x.iterator();

         while(var2.hasNext()) {
            Object var3 = var2.next();
            var1.add(new MappedRegistry.RegistryEntry((ResourceKey)var0x.getResourceKey(var3).get(), var0x.getId(var3), var3));
         }

         return var1.build();
      });
   }

   public static <T> Codec<MappedRegistry<T>> dataPackCodec(ResourceKey<? extends Registry<T>> var0, Lifecycle var1, Codec<T> var2) {
      return RegistryDataPackCodec.create(var0, var1, var2);
   }

   public static <T> Codec<MappedRegistry<T>> directCodec(ResourceKey<? extends Registry<T>> var0, Lifecycle var1, Codec<T> var2) {
      return Codec.unboundedMap(ResourceLocation.CODEC.xmap(ResourceKey.elementKey(var0), ResourceKey::location), var2).xmap((var2x) -> {
         MappedRegistry var3 = new MappedRegistry(var0, var1);
         var2x.forEach((var2, var3x) -> {
            var3.register(var2, var3x, var1);
         });
         return var3;
      }, (var0x) -> {
         return ImmutableMap.copyOf(var0x.keyStorage);
      });
   }

   public static class RegistryEntry<T> {
      public final ResourceKey<T> key;
      public final int id;
      public final T value;

      public RegistryEntry(ResourceKey<T> var1, int var2, T var3) {
         super();
         this.key = var1;
         this.id = var2;
         this.value = var3;
      }
   }
}
