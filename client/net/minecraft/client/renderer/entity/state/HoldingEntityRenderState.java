package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;

public class HoldingEntityRenderState extends LivingEntityRenderState {
   public final ItemStackRenderState heldItem = new ItemStackRenderState();

   public HoldingEntityRenderState() {
      super();
   }

   public static void extractHoldingEntityRenderState(LivingEntity var0, HoldingEntityRenderState var1, ItemModelResolver var2) {
      var2.updateForLiving(var1.heldItem, var0.getMainHandItem(), ItemDisplayContext.GROUND, false, var0);
   }
}
