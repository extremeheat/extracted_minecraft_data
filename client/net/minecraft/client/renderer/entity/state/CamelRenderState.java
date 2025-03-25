package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemStack;

public class CamelRenderState extends LivingEntityRenderState {
   public ItemStack saddle;
   public boolean isRidden;
   public float jumpCooldown;
   public final AnimationState sitAnimationState;
   public final AnimationState sitPoseAnimationState;
   public final AnimationState sitUpAnimationState;
   public final AnimationState idleAnimationState;
   public final AnimationState dashAnimationState;

   public CamelRenderState() {
      super();
      this.saddle = ItemStack.EMPTY;
      this.sitAnimationState = new AnimationState();
      this.sitPoseAnimationState = new AnimationState();
      this.sitUpAnimationState = new AnimationState();
      this.idleAnimationState = new AnimationState();
      this.dashAnimationState = new AnimationState();
   }
}
