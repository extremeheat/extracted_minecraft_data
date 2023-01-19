package net.minecraft.world.level.entity;

import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Stream;
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
      this.storage = new ClassInstanceMultiMap<>(var1);
   }

   public void add(T var1) {
      this.storage.add((T)var1);
   }

   public boolean remove(T var1) {
      return this.storage.remove(var1);
   }

   public void getEntities(AABB var1, Consumer<T> var2) {
      for(EntityAccess var4 : this.storage) {
         if (var4.getBoundingBox().intersects(var1)) {
            var2.accept(var4);
         }
      }
   }

   public <U extends T> void getEntities(EntityTypeTest<T, U> var1, AABB var2, Consumer<? super U> var3) {
      Collection var4 = this.storage.find(var1.getBaseClass());
      if (!var4.isEmpty()) {
         for(EntityAccess var6 : var4) {
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
