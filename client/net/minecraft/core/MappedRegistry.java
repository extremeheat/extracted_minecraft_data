package net.minecraft.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MappedRegistry<T> extends WritableRegistry<T> {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final CrudeIncrementalIntIdentityHashBiMap<T> map = new CrudeIncrementalIntIdentityHashBiMap(256);
   protected final BiMap<ResourceLocation, T> storage = HashBiMap.create();
   protected Object[] randomCache;
   private int nextId;

   public MappedRegistry() {
      super();
   }

   public <V extends T> V registerMapping(int var1, ResourceLocation var2, V var3) {
      this.map.addMapping(var3, var1);
      Validate.notNull(var2);
      Validate.notNull(var3);
      this.randomCache = null;
      if (this.storage.containsKey(var2)) {
         LOGGER.debug("Adding duplicate key '{}' to registry", var2);
      }

      this.storage.put(var2, var3);
      if (this.nextId <= var1) {
         this.nextId = var1 + 1;
      }

      return var3;
   }

   public <V extends T> V register(ResourceLocation var1, V var2) {
      return this.registerMapping(this.nextId, var1, var2);
   }

   @Nullable
   public ResourceLocation getKey(T var1) {
      return (ResourceLocation)this.storage.inverse().get(var1);
   }

   public int getId(@Nullable T var1) {
      return this.map.getId(var1);
   }

   @Nullable
   public T byId(int var1) {
      return this.map.byId(var1);
   }

   public Iterator<T> iterator() {
      return this.map.iterator();
   }

   @Nullable
   public T get(@Nullable ResourceLocation var1) {
      return this.storage.get(var1);
   }

   public Optional<T> getOptional(@Nullable ResourceLocation var1) {
      return Optional.ofNullable(this.storage.get(var1));
   }

   public Set<ResourceLocation> keySet() {
      return Collections.unmodifiableSet(this.storage.keySet());
   }

   public boolean isEmpty() {
      return this.storage.isEmpty();
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

      return this.randomCache[var1.nextInt(this.randomCache.length)];
   }

   public boolean containsKey(ResourceLocation var1) {
      return this.storage.containsKey(var1);
   }
}
