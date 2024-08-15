package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.Crackiness;

public class IronGolemRenderState extends LivingEntityRenderState {
   public float attackTicksRemaining;
   public int offerFlowerTick;
   public Crackiness.Level crackiness = Crackiness.Level.NONE;

   public IronGolemRenderState() {
      super();
   }
}
