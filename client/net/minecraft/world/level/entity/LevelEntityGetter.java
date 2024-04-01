package net.minecraft.world.level.entity;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.phys.AABB;

public interface LevelEntityGetter<T extends EntityAccess> {
   LevelEntityGetter<?> EMPTY = new LevelEntityGetter<EntityAccess>() {
      @Nullable
      @Override
      public EntityAccess get(int var1) {
         return null;
      }

      @Nullable
      @Override
      public EntityAccess get(UUID var1) {
         return null;
      }

      @Override
      public Iterable<EntityAccess> getAll() {
         return List.of();
      }

      @Override
      public <U extends EntityAccess> void get(EntityTypeTest<EntityAccess, U> var1, AbortableIterationConsumer<U> var2) {
      }

      @Override
      public void get(AABB var1, Consumer<EntityAccess> var2) {
      }

      @Override
      public <U extends EntityAccess> void get(EntityTypeTest<EntityAccess, U> var1, AABB var2, AbortableIterationConsumer<U> var3) {
      }
   };

   static <T extends EntityAccess> LevelEntityGetter<T> empty() {
      return EMPTY;
   }

   @Nullable
   T get(int var1);

   @Nullable
   T get(UUID var1);

   Iterable<T> getAll();

   <U extends T> void get(EntityTypeTest<T, U> var1, AbortableIterationConsumer<U> var2);

   void get(AABB var1, Consumer<T> var2);

   <U extends T> void get(EntityTypeTest<T, U> var1, AABB var2, AbortableIterationConsumer<U> var3);
}
