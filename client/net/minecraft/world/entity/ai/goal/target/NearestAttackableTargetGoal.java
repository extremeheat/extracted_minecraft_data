package net.minecraft.world.entity.ai.goal.target;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class NearestAttackableTargetGoal<T extends LivingEntity> extends TargetGoal {
   private static final int DEFAULT_RANDOM_INTERVAL = 10;
   protected final Class<T> targetType;
   protected final int randomInterval;
   @Nullable
   protected LivingEntity target;
   protected TargetingConditions targetConditions;

   public NearestAttackableTargetGoal(Mob var1, Class<T> var2, boolean var3) {
      this(var1, var2, 10, var3, false, null);
   }

   public NearestAttackableTargetGoal(Mob var1, Class<T> var2, boolean var3, TargetingConditions.Selector var4) {
      this(var1, var2, 10, var3, false, var4);
   }

   public NearestAttackableTargetGoal(Mob var1, Class<T> var2, boolean var3, boolean var4) {
      this(var1, var2, 10, var3, var4, null);
   }

   public NearestAttackableTargetGoal(Mob var1, Class<T> var2, int var3, boolean var4, boolean var5, @Nullable TargetingConditions.Selector var6) {
      super(var1, var4, var5);
      this.targetType = var2;
      this.randomInterval = reducedTickDelay(var3);
      this.setFlags(EnumSet.of(Goal.Flag.TARGET));
      this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(var6);
   }

   @Override
   public boolean canUse() {
      if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
         return false;
      } else {
         this.findTarget();
         return this.target != null;
      }
   }

   protected AABB getTargetSearchArea(double var1) {
      return this.mob.getBoundingBox().inflate(var1, var1, var1);
   }

   protected void findTarget() {
      ServerLevel var1 = getServerLevel(this.mob);
      if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
         this.target = var1.getNearestEntity(
            this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), var0 -> true),
            this.getTargetConditions(),
            this.mob,
            this.mob.getX(),
            this.mob.getEyeY(),
            this.mob.getZ()
         );
      } else {
         this.target = var1.getNearestPlayer(this.getTargetConditions(), this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
      }
   }

   @Override
   public void start() {
      this.mob.setTarget(this.target);
      super.start();
   }

   public void setTarget(@Nullable LivingEntity var1) {
      this.target = var1;
   }

   private TargetingConditions getTargetConditions() {
      return this.targetConditions.range(this.getFollowDistance());
   }
}
