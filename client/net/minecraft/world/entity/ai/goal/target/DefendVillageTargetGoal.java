package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
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

   @Override
   public boolean canUse() {
      AABB var1 = this.golem.getBoundingBox().inflate(10.0, 8.0, 10.0);
      List var2 = this.golem.level().getNearbyEntities(Villager.class, this.attackTargeting, this.golem, var1);
      List var3 = this.golem.level().getNearbyPlayers(this.attackTargeting, this.golem, var1);

      for (LivingEntity var5 : var2) {
         Villager var6 = (Villager)var5;

         for (Player var8 : var3) {
            int var9 = var6.getPlayerReputation(var8);
            if (var9 <= -100) {
               this.potentialTarget = var8;
            }
         }
      }

      return this.potentialTarget == null
         ? false
         : !(this.potentialTarget instanceof Player) || !this.potentialTarget.isSpectator() && !((Player)this.potentialTarget).isCreative();
   }

   @Override
   public void start() {
      this.golem.setTarget(this.potentialTarget);
      super.start();
   }
}
