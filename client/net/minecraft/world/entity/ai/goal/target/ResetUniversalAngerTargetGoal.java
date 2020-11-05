package net.minecraft.world.entity.ai.goal.target;

import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;

public class ResetUniversalAngerTargetGoal<T extends Mob & NeutralMob> extends Goal {
   private final T mob;
   private final boolean alertOthersOfSameType;
   private int lastHurtByPlayerTimestamp;

   public ResetUniversalAngerTargetGoal(T var1, boolean var2) {
      super();
      this.mob = var1;
      this.alertOthersOfSameType = var2;
   }

   public boolean canUse() {
      return this.mob.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER) && this.wasHurtByPlayer();
   }

   private boolean wasHurtByPlayer() {
      return this.mob.getLastHurtByMob() != null && this.mob.getLastHurtByMob().getType() == EntityType.PLAYER && this.mob.getLastHurtByMobTimestamp() > this.lastHurtByPlayerTimestamp;
   }

   public void start() {
      this.lastHurtByPlayerTimestamp = this.mob.getLastHurtByMobTimestamp();
      ((NeutralMob)this.mob).forgetCurrentTargetAndRefreshUniversalAnger();
      if (this.alertOthersOfSameType) {
         this.getNearbyMobsOfSameType().stream().filter((var1) -> {
            return var1 != this.mob;
         }).map((var0) -> {
            return (NeutralMob)var0;
         }).forEach(NeutralMob::forgetCurrentTargetAndRefreshUniversalAnger);
      }

      super.start();
   }

   private List<Mob> getNearbyMobsOfSameType() {
      double var1 = this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
      AABB var3 = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(var1, 10.0D, var1);
      return this.mob.level.getLoadedEntitiesOfClass(this.mob.getClass(), var3);
   }
}
