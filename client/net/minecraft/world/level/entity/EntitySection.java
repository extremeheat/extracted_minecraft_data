package net.minecraft.world.level.entity;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.ClassInstanceMultiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntitySection<T> {
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

   public void getEntities(Predicate<? super T> var1, Consumer<T> var2) {
      Iterator var3 = this.storage.iterator();

      while(var3.hasNext()) {
         Object var4 = var3.next();
         if (var1.test(var4)) {
            var2.accept(var4);
         }
      }

   }

   public <U extends T> void getEntities(EntityTypeTest<T, U> var1, Predicate<? super U> var2, Consumer<? super U> var3) {
      Iterator var4 = this.storage.find(var1.getBaseClass()).iterator();

      while(var4.hasNext()) {
         Object var5 = var4.next();
         Object var6 = var1.tryCast(var5);
         if (var6 != null && var2.test(var6)) {
            var3.accept(var6);
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

   public int size() {
      return this.storage.size();
   }
}
