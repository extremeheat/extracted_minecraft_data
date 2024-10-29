package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;

public class LivingEntityRenderState extends EntityRenderState {
   public float bodyRot;
   public float yRot;
   public float xRot;
   public float deathTime;
   public float walkAnimationPos;
   public float walkAnimationSpeed;
   public float wornHeadAnimationPos;
   public float scale = 1.0F;
   public float ageScale = 1.0F;
   public boolean isUpsideDown;
   public boolean isFullyFrozen;
   public boolean isBaby;
   public boolean isInWater;
   public boolean isAutoSpinAttack;
   public boolean hasRedOverlay;
   public boolean isInvisibleToPlayer;
   public boolean appearsGlowing;
   @Nullable
   public Direction bedOrientation;
   @Nullable
   public Component customName;
   public Pose pose;
   @Nullable
   public BakedModel headItemModel;
   public ItemStack headItem;
   public HumanoidArm mainArm;
   @Nullable
   public BakedModel rightHandItemModel;
   public ItemStack rightHandItem;
   @Nullable
   public BakedModel leftHandItemModel;
   public ItemStack leftHandItem;

   public LivingEntityRenderState() {
      super();
      this.pose = Pose.STANDING;
      this.headItem = ItemStack.EMPTY;
      this.mainArm = HumanoidArm.RIGHT;
      this.rightHandItem = ItemStack.EMPTY;
      this.leftHandItem = ItemStack.EMPTY;
   }

   public ItemStack getMainHandItem() {
      return this.mainArm == HumanoidArm.RIGHT ? this.rightHandItem : this.leftHandItem;
   }

   @Nullable
   public BakedModel getMainHandItemModel() {
      return this.mainArm == HumanoidArm.RIGHT ? this.rightHandItemModel : this.leftHandItemModel;
   }

   public boolean hasPose(Pose var1) {
      return this.pose == var1;
   }
}
