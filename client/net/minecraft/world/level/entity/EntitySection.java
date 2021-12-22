package net.minecraft.world.level.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntitySection<T extends EntityAccess> {
   protected static final Logger LOGGER = LogManager.getLogger();
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

   public void getEntities(AABB var1, Consumer<T> var2) {
      Iterator var3 = this.storage.iterator();

      while(var3.hasNext()) {
         EntityAccess var4 = (EntityAccess)var3.next();
         if (var4.getBoundingBox().intersects(var1)) {
            var2.accept(var4);
         }
      }

   }

   public <U extends T> void getEntities(EntityTypeTest<T, U> var1, AABB var2, Consumer<? super U> var3) {
      Collection var4 = this.storage.find(var1.getBaseClass());
      if (!var4.isEmpty()) {
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            EntityAccess var6 = (EntityAccess)var5.next();
            EntityAccess var7 = (EntityAccess)var1.tryCast(var6);
            if (var7 != null && var6.getBoundingBox().intersects(var2)) {
               var3.accept(var7);
            }
         }

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
