package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class DefendVillageTargetGoal extends TargetGoal {
   private final IronGolem golem;
   @Nullable
   private LivingEntity potentialTarget;
   private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0);

   public DefendVillageTargetGoal(IronGolem var1) {
      super(var1, false, true);
      this.golem = var1;
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   public boolean canUse() {
      AABB var1 = this.golem.getBoundingBox().inflate(10.0, 8.0, 10.0);
      List var2 = this.golem.level.getNearbyEntities(Villager.class, this.attackTargeting, this.golem, var1);
      List var3 = this.golem.level.getNearbyPlayers(this.attackTargeting, this.golem, var1);
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         LivingEntity var5 = (LivingEntity)var4.next();
         Villager var6 = (Villager)var5;
         Iterator var7 = var3.iterator();

         while(var7.hasNext()) {
            Player var8 = (Player)var7.next();
            int var9 = var6.getPlayerReputation(var8);
            if (var9 <= -100) {
               this.potentialTarget = var8;
            }
         }
      }

      if (this.potentialTarget == null) {
         return false;
      } else if (!(this.potentialTarget instanceof Player) || !this.potentialTarget.isSpectator() && !((Player)this.potentialTarget).isCreative()) {
         return true;
      } else {
         return false;
      }
   }

   public void start() {
      this.golem.setTarget(this.potentialTarget);
      super.start();
   }
}
