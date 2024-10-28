package net.minecraft.world.level.entity;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class EntitySection<T extends EntityAccess> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ClassInstanceMultiMap<T> storage;
   private Visibility chunkStatus;

   public EntitySection(Class<T> var1, Visibility var2) {
      super();
      this.chunkStatus = var2;
      this.storage = new ClassInstanceMultiMap(var1);
   }

   public void add(T var1) {
      this.storage.add(var1);
   }

   public boolean remove(T var1) {
      return this.storage.remove(var1);
   }

   public AbortableIterationConsumer.Continuation getEntities(AABB var1, AbortableIterationConsumer<T> var2) {
      Iterator var3 = this.storage.iterator();

      EntityAccess var4;
      do {
         if (!var3.hasNext()) {
            return AbortableIterationConsumer.Continuation.CONTINUE;
         }

         var4 = (EntityAccess)var3.next();
      } while(!var4.getBoundingBox().intersects(var1) || !var2.accept(var4).shouldAbort());

      return AbortableIterationConsumer.Continuation.ABORT;
   }

   public <U extends T> AbortableIterationConsumer.Continuation getEntities(EntityTypeTest<T, U> var1, AABB var2, AbortableIterationConsumer<? super U> var3) {
      Collection var4 = this.storage.find(var1.getBaseClass());
      if (var4.isEmpty()) {
         return AbortableIterationConsumer.Continuation.CONTINUE;
      } else {
         Iterator var5 = var4.iterator();

         EntityAccess var6;
         EntityAccess var7;
         do {
            if (!var5.hasNext()) {
               return AbortableIterationConsumer.Continuation.CONTINUE;
            }

            var6 = (EntityAccess)var5.next();
            var7 = (EntityAccess)var1.tryCast(var6);
         } while(var7 == null || !var6.getBoundingBox().intersects(var2) || !var3.accept(var7).shouldAbort());

         return AbortableIterationConsumer.Continuation.ABORT;
      }
   }

   public boolean isEmpty() {
      return this.storage.isEmpty();
   }

   public Stream<T> getEntities() {
      return this.storage.stream();
   }

   public Visibility getStatus() {
      return this.chunkStatus;
   }

   public Visibility updateChunkStatus(Visibility var1) {
      Visibility var2 = this.chunkStatus;
      this.chunkStatus = var1;
      return var2;
   }

   @VisibleForDebug
   public int size() {
      return this.storage.size();
   }
}
