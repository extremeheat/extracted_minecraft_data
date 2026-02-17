package net.minecraft.world.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class Avatar extends LivingEntity {
   public static final HumanoidArm DEFAULT_MAIN_HAND;
   public static final int DEFAULT_MODEL_CUSTOMIZATION = 0;
   public static final float DEFAULT_EYE_HEIGHT = 1.62F;
   public static final Vec3 DEFAULT_VEHICLE_ATTACHMENT;
   private static final float CROUCH_BB_HEIGHT = 1.5F;
   private static final float SWIMMING_BB_WIDTH = 0.6F;
   public static final float SWIMMING_BB_HEIGHT = 0.6F;
   protected static final EntityDimensions STANDING_DIMENSIONS;
   protected static final Map<Pose, EntityDimensions> POSES;
   protected static final EntityDataAccessor<HumanoidArm> DATA_PLAYER_MAIN_HAND;
   protected static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION;

   protected Avatar(final EntityType<? extends LivingEntity> type, final Level level) {
      super(type, level);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_PLAYER_MAIN_HAND, DEFAULT_MAIN_HAND);
      entityData.define(DATA_PLAYER_MODE_CUSTOMISATION, (byte)0);
   }

   public HumanoidArm getMainArm() {
      return (HumanoidArm)this.entityData.get(DATA_PLAYER_MAIN_HAND);
   }

   public void setMainArm(final HumanoidArm mainArm) {
      this.entityData.set(DATA_PLAYER_MAIN_HAND, mainArm);
   }

   public boolean isModelPartShown(final PlayerModelPart part) {
      return ((Byte)this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & part.getMask()) == part.getMask();
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return (EntityDimensions)POSES.getOrDefault(pose, STANDING_DIMENSIONS);
   }

   public abstract ResolvableProfile getProfile();

   static {
      DEFAULT_MAIN_HAND = HumanoidArm.RIGHT;
      DEFAULT_VEHICLE_ATTACHMENT = new Vec3(0.0, 0.6, 0.0);
      STANDING_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.8F).withEyeHeight(1.62F).withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, DEFAULT_VEHICLE_ATTACHMENT));
      POSES = ImmutableMap.builder().put(Pose.STANDING, STANDING_DIMENSIONS).put(Pose.SLEEPING, SLEEPING_DIMENSIONS).put(Pose.FALL_FLYING, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F)).put(Pose.SWIMMING, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F)).put(Pose.SPIN_ATTACK, EntityDimensions.scalable(0.6F, 0.6F).withEyeHeight(0.4F)).put(Pose.CROUCHING, EntityDimensions.scalable(0.6F, 1.5F).withEyeHeight(1.27F).withAttachments(EntityAttachments.builder().attach(EntityAttachment.VEHICLE, DEFAULT_VEHICLE_ATTACHMENT))).put(Pose.DYING, EntityDimensions.fixed(0.2F, 0.2F).withEyeHeight(1.62F)).build();
      DATA_PLAYER_MAIN_HAND = SynchedEntityData.<HumanoidArm>defineId(Avatar.class, EntityDataSerializers.HUMANOID_ARM);
      DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.<Byte>defineId(Avatar.class, EntityDataSerializers.BYTE);
   }
}
