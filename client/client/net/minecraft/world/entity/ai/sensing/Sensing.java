package net.minecraft.world.entity.ai.sensing;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class Sensing {
   private final Mob mob;
   private final IntSet seen = new IntOpenHashSet();
   private final IntSet unseen = new IntOpenHashSet();

   public Sensing(Mob var1) {
      super();
      this.mob = var1;
   }

   public void tick() {
      this.seen.clear();
      this.unseen.clear();
   }

   public boolean hasLineOfSight(Entity var1) {
      int var2 = var1.getId();
      if (this.seen.contains(var2)) {
         return true;
      } else if (this.unseen.contains(var2)) {
         return false;
      } else {
         this.mob.level().getProfiler().push("hasLineOfSight");
         boolean var3 = this.mob.hasLineOfSight(var1);
         this.mob.level().getProfiler().pop();
         if (var3) {
            this.seen.add(var2);
         } else {
            this.unseen.add(var2);
         }

         return var3;
      }
   }
}
