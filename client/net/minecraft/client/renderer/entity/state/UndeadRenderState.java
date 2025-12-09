package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class UndeadRenderState extends HumanoidRenderState {
   public UndeadRenderState() {
      super();
   }

   public ItemStack getUseItemStackForArm(HumanoidArm var1) {
      return this.getMainHandItemStack();
   }
}
