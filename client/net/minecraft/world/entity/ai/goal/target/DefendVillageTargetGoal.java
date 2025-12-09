package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class DefendVillageTargetGoal extends TargetGoal {
   private final IronGolem golem;
   private @Nullable LivingEntity potentialTarget;
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

      for(LivingEntity var6 : var3) {
         Villager var7 = (Villager)var6;

         for(Player var9 : var4) {
            int var10 = var7.getPlayerReputation(var9);
            if (var10 <= -100) {
               this.potentialTarget = var9;
            }
         }
      }

      if (this.potentialTarget == null) {
         return false;
      } else {
         LivingEntity var12 = this.potentialTarget;
         if (var12 instanceof Player) {
            Player var11 = (Player)var12;
            if (var11.isSpectator() || var11.isCreative()) {
               return false;
            }
         }

         return true;
      }
   }

   public void start() {
      this.golem.setTarget(this.potentialTarget);
      super.start();
   }
}
