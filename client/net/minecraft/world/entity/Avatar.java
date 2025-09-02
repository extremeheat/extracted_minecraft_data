package net.minecraft.world.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Avatar extends LivingEntity {
   public static final HumanoidArm DEFAULT_MAIN_HAND;
   public static final int DEFAULT_MODEL_CUSTOMIZATION = 0;
   public static final float DEFAULT_EYE_HEIGHT = 1.62F;
   public static final Vec3 DEFAULT_VEHICLE_ATTACHMENT;
   protected static final EntityDataAccessor<Byte> DATA_PLAYER_MAIN_HAND;
   protected static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION;

   protected Avatar(EntityType<? extends LivingEntity> var1, Level var2) {
      super(var1, var2);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_PLAYER_MAIN_HAND, (byte)DEFAULT_MAIN_HAND.getId());
      var1.define(DATA_PLAYER_MODE_CUSTOMISATION, (byte)0);
   }

   public HumanoidArm getMainArm() {
      return (Byte)this.entityData.get(DATA_PLAYER_MAIN_HAND) == 0 ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
   }

   public void setMainArm(HumanoidArm var1) {
      this.entityData.set(DATA_PLAYER_MAIN_HAND, (byte)(var1 == HumanoidArm.LEFT ? 0 : 1));
   }

   public boolean isModelPartShown(PlayerModelPart var1) {
      return ((Byte)this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & var1.getMask()) == var1.getMask();
   }

   static {
      DEFAULT_MAIN_HAND = HumanoidArm.RIGHT;
      DEFAULT_VEHICLE_ATTACHMENT = new Vec3(0.0, 0.6, 0.0);
      DATA_PLAYER_MAIN_HAND = SynchedEntityData.<Byte>defineId(Avatar.class, EntityDataSerializers.BYTE);
      DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.<Byte>defineId(Avatar.class, EntityDataSerializers.BYTE);
   }
}
