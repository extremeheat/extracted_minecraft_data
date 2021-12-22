package net.minecraft.world.level.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public class EntityTickList {
   private Int2ObjectMap<Entity> active = new Int2ObjectLinkedOpenHashMap();
   private Int2ObjectMap<Entity> passive = new Int2ObjectLinkedOpenHashMap();
   @Nullable
   private Int2ObjectMap<Entity> iterated;

   public EntityTickList() {
      super();
   }

   private void ensureActiveIsNotIterated() {
      if (this.iterated == this.active) {
         this.passive.clear();
         ObjectIterator var1 = Int2ObjectMaps.fastIterable(this.active).iterator();

         while(var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            this.passive.put(var2.getIntKey(), (Entity)var2.getValue());
         }

         Int2ObjectMap var3 = this.active;
         this.active = this.passive;
         this.passive = var3;
      }

   }

   public void add(Entity var1) {
      this.ensureActiveIsNotIterated();
      this.active.put(var1.getId(), var1);
   }

   public void remove(Entity var1) {
      this.ensureActiveIsNotIterated();
      this.active.remove(var1.getId());
   }

   public boolean contains(Entity var1) {
      return this.active.containsKey(var1.getId());
   }

   public void forEach(Consumer<Entity> var1) {
      if (this.iterated != null) {
         throw new UnsupportedOperationException("Only one concurrent iteration supported");
      } else {
         this.iterated = this.active;

         try {
            ObjectIterator var2 = this.active.values().iterator();

            while(var2.hasNext()) {
               Entity var3 = (Entity)var2.next();
               var1.accept(var3);
            }
         } finally {
            this.iterated = null;
         }

      }
   }
}
