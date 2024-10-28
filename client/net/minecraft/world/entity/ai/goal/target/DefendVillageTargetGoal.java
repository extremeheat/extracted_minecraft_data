package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
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
      ServerLevel var2 = getServerLevel(this.golem);
      List var3 = var2.getNearbyEntities(Villager.class, this.attackTargeting, this.golem, var1);
      List var4 = var2.getNearbyPlayers(this.attackTargeting, this.golem, var1);
      Iterator var5 = var3.iterator();

      while(var5.hasNext()) {
         LivingEntity var6 = (LivingEntity)var5.next();
         Villager var7 = (Villager)var6;
         Iterator var8 = var4.iterator();

         while(var8.hasNext()) {
            Player var9 = (Player)var8.next();
            int var10 = var7.getPlayerReputation(var9);
            if (var10 <= -100) {
               this.potentialTarget = var9;
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
