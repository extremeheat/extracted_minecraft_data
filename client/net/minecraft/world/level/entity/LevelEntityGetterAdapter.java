package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.phys.AABB;

public class LevelEntityGetterAdapter<T extends EntityAccess> implements LevelEntityGetter<T> {
   private final EntityLookup<T> visibleEntities;
   private final EntitySectionStorage<T> sectionStorage;

   public LevelEntityGetterAdapter(EntityLookup<T> var1, EntitySectionStorage<T> var2) {
      super();
      this.visibleEntities = var1;
      this.sectionStorage = var2;
   }

   @Nullable
   public T get(int var1) {
      return this.visibleEntities.getEntity(var1);
   }

   @Nullable
   public T get(UUID var1) {
      return this.visibleEntities.getEntity(var1);
   }

   public Iterable<T> getAll() {
      return this.visibleEntities.getAllEntities();
   }

   public <U extends T> void get(EntityTypeTest<T, U> var1, AbortableIterationConsumer<U> var2) {
      this.visibleEntities.getEntities(var1, var2);
   }

   public void get(AABB var1, Consumer<T> var2) {
      this.sectionStorage.getEntities(var1, AbortableIterationConsumer.forConsumer(var2));
   }

   public <U extends T> void get(EntityTypeTest<T, U> var1, AABB var2, AbortableIterationConsumer<U> var3) {
      this.sectionStorage.getEntities(var1, var2, var3);
   }
}
