package net.minecraft.world.entity.animal;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class AbstractSchoolingFish extends AbstractFish {
   @Nullable
   private AbstractSchoolingFish leader;
   private int schoolSize = 1;

   public AbstractSchoolingFish(EntityType<? extends AbstractSchoolingFish> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(5, new FollowFlockLeaderGoal(this));
   }

   @Override
   public int getMaxSpawnClusterSize() {
      return this.getMaxSchoolSize();
   }

   public int getMaxSchoolSize() {
      return super.getMaxSpawnClusterSize();
   }

   @Override
   protected boolean canRandomSwim() {
      return !this.isFollower();
   }

   public boolean isFollower() {
      return this.leader != null && this.leader.isAlive();
   }

   public AbstractSchoolingFish startFollowing(AbstractSchoolingFish var1) {
      this.leader = var1;
      var1.addFollower();
      return var1;
   }

   public void stopFollowing() {
      this.leader.removeFollower();
      this.leader = null;
   }

   private void addFollower() {
      ++this.schoolSize;
   }

   private void removeFollower() {
      --this.schoolSize;
   }

   public boolean canBeFollowed() {
      return this.hasFollowers() && this.schoolSize < this.getMaxSchoolSize();
   }

   @Override
   public void tick() {
      super.tick();
      if (this.hasFollowers() && this.level.random.nextInt(200) == 1) {
         List var1 = this.level.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(8.0, 8.0, 8.0));
         if (var1.size() <= 1) {
            this.schoolSize = 1;
         }
      }
   }

   public boolean hasFollowers() {
      return this.schoolSize > 1;
   }

   public boolean inRangeOfLeader() {
      return this.distanceToSqr(this.leader) <= 121.0;
   }

   public void pathToLeader() {
      if (this.isFollower()) {
         this.getNavigation().moveTo(this.leader, 1.0);
      }
   }

   public void addFollowers(Stream<? extends AbstractSchoolingFish> var1) {
      var1.limit((long)(this.getMaxSchoolSize() - this.schoolSize)).filter(var1x -> var1x != this).forEach(var1x -> var1x.startFollowing(this));
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5
   ) {
      super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4, var5);
      if (var4 == null) {
         var4 = new AbstractSchoolingFish.SchoolSpawnGroupData(this);
      } else {
         this.startFollowing(((AbstractSchoolingFish.SchoolSpawnGroupData)var4).leader);
      }

      return (SpawnGroupData)var4;
   }

   public static class SchoolSpawnGroupData implements SpawnGroupData {
      public final AbstractSchoolingFish leader;

      public SchoolSpawnGroupData(AbstractSchoolingFish var1) {
         super();
         this.leader = var1;
      }
   }
}
