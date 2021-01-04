package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public abstract class PatrollingMonster extends Monster {
   private BlockPos patrolTarget;
   private boolean patrolLeader;
   private boolean patrolling;

   protected PatrollingMonster(EntityType<? extends PatrollingMonster> var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(4, new PatrollingMonster.LongDistancePatrolGoal(this, 0.7D, 0.595D));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.patrolTarget != null) {
         var1.put("PatrolTarget", NbtUtils.writeBlockPos(this.patrolTarget));
      }

      var1.putBoolean("PatrolLeader", this.patrolLeader);
      var1.putBoolean("Patrolling", this.patrolling);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("PatrolTarget")) {
         this.patrolTarget = NbtUtils.readBlockPos(var1.getCompound("PatrolTarget"));
      }

      this.patrolLeader = var1.getBoolean("PatrolLeader");
      this.patrolling = var1.getBoolean("Patrolling");
   }

   public double getRidingHeight() {
      return -0.45D;
   }

   public boolean canBeLeader() {
      return true;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      if (var3 != MobSpawnType.PATROL && var3 != MobSpawnType.EVENT && var3 != MobSpawnType.STRUCTURE && this.random.nextFloat() < 0.06F && this.canBeLeader()) {
         this.patrolLeader = true;
      }

      if (this.isPatrolLeader()) {
         this.setItemSlot(EquipmentSlot.HEAD, Raid.getLeaderBannerInstance());
         this.setDropChance(EquipmentSlot.HEAD, 2.0F);
      }

      if (var3 == MobSpawnType.PATROL) {
         this.patrolling = true;
      }

      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   public static boolean checkPatrollingMonsterSpawnRules(EntityType<? extends PatrollingMonster> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      return var1.getBrightness(LightLayer.BLOCK, var3) > 8 ? false : checkAnyLightMonsterSpawnRules(var0, var1, var2, var3, var4);
   }

   public boolean removeWhenFarAway(double var1) {
      return !this.patrolling || var1 > 16384.0D;
   }

   public void setPatrolTarget(BlockPos var1) {
      this.patrolTarget = var1;
      this.patrolling = true;
   }

   public BlockPos getPatrolTarget() {
      return this.patrolTarget;
   }

   public boolean hasPatrolTarget() {
      return this.patrolTarget != null;
   }

   public void setPatrolLeader(boolean var1) {
      this.patrolLeader = var1;
      this.patrolling = true;
   }

   public boolean isPatrolLeader() {
      return this.patrolLeader;
   }

   public boolean canJoinPatrol() {
      return true;
   }

   public void findPatrolTarget() {
      this.patrolTarget = (new BlockPos(this)).offset(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
      this.patrolling = true;
   }

   protected boolean isPatrolling() {
      return this.patrolling;
   }

   public static class LongDistancePatrolGoal<T extends PatrollingMonster> extends Goal {
      private final T mob;
      private final double speedModifier;
      private final double leaderSpeedModifier;

      public LongDistancePatrolGoal(T var1, double var2, double var4) {
         super();
         this.mob = var1;
         this.speedModifier = var2;
         this.leaderSpeedModifier = var4;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         return this.mob.isPatrolling() && this.mob.getTarget() == null && !this.mob.isVehicle() && this.mob.hasPatrolTarget();
      }

      public void start() {
      }

      public void stop() {
      }

      public void tick() {
         boolean var1 = this.mob.isPatrolLeader();
         PathNavigation var2 = this.mob.getNavigation();
         if (var2.isDone()) {
            if (var1 && this.mob.getPatrolTarget().closerThan(this.mob.position(), 10.0D)) {
               this.mob.findPatrolTarget();
            } else {
               Vec3 var3 = new Vec3(this.mob.getPatrolTarget());
               Vec3 var4 = new Vec3(this.mob.x, this.mob.y, this.mob.z);
               Vec3 var5 = var4.subtract(var3);
               var3 = var5.yRot(90.0F).scale(0.4D).add(var3);
               Vec3 var6 = var3.subtract(var4).normalize().scale(10.0D).add(var4);
               BlockPos var7 = new BlockPos(var6);
               var7 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var7);
               if (!var2.moveTo((double)var7.getX(), (double)var7.getY(), (double)var7.getZ(), var1 ? this.leaderSpeedModifier : this.speedModifier)) {
                  this.moveRandomly();
               } else if (var1) {
                  List var8 = this.mob.level.getEntitiesOfClass(PatrollingMonster.class, this.mob.getBoundingBox().inflate(16.0D), (var0) -> {
                     return !var0.isPatrolLeader() && var0.canJoinPatrol();
                  });
                  Iterator var9 = var8.iterator();

                  while(var9.hasNext()) {
                     PatrollingMonster var10 = (PatrollingMonster)var9.next();
                     var10.setPatrolTarget(var7);
                  }
               }
            }
         }

      }

      private void moveRandomly() {
         Random var1 = this.mob.getRandom();
         BlockPos var2 = this.mob.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (new BlockPos(this.mob)).offset(-8 + var1.nextInt(16), 0, -8 + var1.nextInt(16)));
         this.mob.getNavigation().moveTo((double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), this.speedModifier);
      }
   }
}
