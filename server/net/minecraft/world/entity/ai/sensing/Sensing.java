package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class Sensing {
   private final Mob mob;
   private final List<Entity> seen = Lists.newArrayList();
   private final List<Entity> unseen = Lists.newArrayList();

   public Sensing(Mob var1) {
      super();
      this.mob = var1;
   }

   public void tick() {
      this.seen.clear();
      this.unseen.clear();
   }

   public boolean canSee(Entity var1) {
      if (this.seen.contains(var1)) {
         return true;
      } else if (this.unseen.contains(var1)) {
         return false;
      } else {
         this.mob.level.getProfiler().push("canSee");
         boolean var2 = this.mob.canSee(var1);
         this.mob.level.getProfiler().pop();
         if (var2) {
            this.seen.add(var1);
         } else {
            this.unseen.add(var1);
         }

         return var2;
      }
   }
}
