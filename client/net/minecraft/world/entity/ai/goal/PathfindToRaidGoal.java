package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.phys.Vec3;

public class PathfindToRaidGoal<T extends Raider> extends Goal {
   private static final int RECRUITMENT_SEARCH_TICK_DELAY = 20;
   private static final float SPEED_MODIFIER = 1.0F;
   private final T mob;
   private int recruitmentTick;

   public PathfindToRaidGoal(T var1) {
      super();
      this.mob = var1;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      return this.mob.getTarget() == null && !this.mob.isVehicle() && this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && !((ServerLevel)this.mob.level).isVillage(this.mob.blockPosition());
   }

   public boolean canContinueToUse() {
      return this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && this.mob.level instanceof ServerLevel && !((ServerLevel)this.mob.level).isVillage(this.mob.blockPosition());
   }

   public void tick() {
      if (this.mob.hasActiveRaid()) {
         Raid var1 = this.mob.getCurrentRaid();
         if (this.mob.tickCount > this.recruitmentTick) {
            this.recruitmentTick = this.mob.tickCount + 20;
            this.recruitNearby(var1);
         }

         if (!this.mob.isPathFinding()) {
            Vec3 var2 = DefaultRandomPos.getPosTowards(this.mob, 15, 4, Vec3.atBottomCenterOf(var1.getCenter()), 1.5707963705062866D);
            if (var2 != null) {
               this.mob.getNavigation().moveTo(var2.field_414, var2.field_415, var2.field_416, 1.0D);
            }
         }
      }

   }

   private void recruitNearby(Raid var1) {
      if (var1.isActive()) {
         HashSet var2 = Sets.newHashSet();
         List var3 = this.mob.level.getEntitiesOfClass(Raider.class, this.mob.getBoundingBox().inflate(16.0D), (var1x) -> {
            return !var1x.hasActiveRaid() && Raids.canJoinRaid(var1x, var1);
         });
         var2.addAll(var3);
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            Raider var5 = (Raider)var4.next();
            var1.joinRaid(var1.getGroupsSpawned(), var5, (BlockPos)null, true);
         }
      }

   }
}
