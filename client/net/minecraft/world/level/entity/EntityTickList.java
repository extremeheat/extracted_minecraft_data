package net.minecraft.world.level.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public class EntityTickList {
   private Int2ObjectMap<Entity> active = new Int2ObjectLinkedOpenHashMap();
   private Int2ObjectMap<Entity> passive = new Int2ObjectLinkedOpenHashMap();
   private @Nullable Int2ObjectMap<Entity> iterated;

   public EntityTickList() {
      super();
   }

   private void ensureActiveIsNotIterated() {
      if (this.iterated == this.active) {
         this.passive.clear();
         ObjectIterator var1 = Int2ObjectMaps.fastIterable(this.active).iterator();

         while(var1.hasNext()) {
            Int2ObjectMap.Entry<Entity> entry = (Int2ObjectMap.Entry)var1.next();
            this.passive.put(entry.getIntKey(), (Entity)entry.getValue());
         }

         Int2ObjectMap<Entity> tmp = this.active;
         this.active = this.passive;
         this.passive = tmp;
      }

   }

   public void add(final Entity entity) {
      this.ensureActiveIsNotIterated();
      this.active.put(entity.getId(), entity);
   }

   public void remove(final Entity entity) {
      this.ensureActiveIsNotIterated();
      this.active.remove(entity.getId());
   }

   public boolean contains(final Entity entity) {
      return this.active.containsKey(entity.getId());
   }

   public void forEach(final Consumer<Entity> output) {
      if (this.iterated != null) {
         throw new UnsupportedOperationException("Only one concurrent iteration supported");
      } else {
         this.iterated = this.active;

         try {
            ObjectIterator var2 = this.active.values().iterator();

            while(var2.hasNext()) {
               Entity entity = (Entity)var2.next();
               output.accept(entity);
            }
         } finally {
            this.iterated = null;
         }

      }
   }
}
