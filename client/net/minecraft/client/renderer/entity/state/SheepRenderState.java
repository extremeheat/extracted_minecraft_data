package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.item.DyeColor;

public class SheepRenderState extends LivingEntityRenderState {
   public float headEatPositionScale;
   public float headEatAngleScale;
   public boolean isSheared;
   public DyeColor woolColor = DyeColor.WHITE;
   public int id;

   public SheepRenderState() {
      super();
   }
}
