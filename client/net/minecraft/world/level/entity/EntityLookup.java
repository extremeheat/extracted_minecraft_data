package net.minecraft.world.level.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.AbortableIterationConsumer;
import org.slf4j.Logger;

public class EntityLookup<T extends EntityAccess> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Int2ObjectMap<T> byId = new Int2ObjectLinkedOpenHashMap();
   private final Map<UUID, T> byUuid = Maps.newHashMap();

   public EntityLookup() {
      super();
   }

   public <U extends T> void getEntities(EntityTypeTest<T, U> var1, AbortableIterationConsumer<U> var2) {
      ObjectIterator var3 = this.byId.values().iterator();

      EntityAccess var5;
      do {
         if (!var3.hasNext()) {
            return;
         }

         EntityAccess var4 = (EntityAccess)var3.next();
         var5 = (EntityAccess)var1.tryCast(var4);
      } while(var5 == null || !var2.accept(var5).shouldAbort());

   }

   public Iterable<T> getAllEntities() {
      return Iterables.unmodifiableIterable(this.byId.values());
   }

   public void add(T var1) {
      UUID var2 = var1.getUUID();
      if (this.byUuid.containsKey(var2)) {
         LOGGER.warn("Duplicate entity UUID {}: {}", var2, var1);
      } else {
         this.byUuid.put(var2, var1);
         this.byId.put(var1.getId(), var1);
      }
   }

   public void remove(T var1) {
      this.byUuid.remove(var1.getUUID());
      this.byId.remove(var1.getId());
   }

   @Nullable
   public T getEntity(int var1) {
      return (EntityAccess)this.byId.get(var1);
   }

   @Nullable
   public T getEntity(UUID var1) {
      return (EntityAccess)this.byUuid.get(var1);
   }

   public int count() {
      return this.byUuid.size();
   }
}
