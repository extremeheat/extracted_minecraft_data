package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.color.ColorLerper;
import net.minecraft.world.item.DyeColor;

public class SheepRenderState extends LivingEntityRenderState {
   public float headEatPositionScale;
   public float headEatAngleScale;
   public boolean isSheared;
   public DyeColor woolColor;
   public boolean isJebSheep;

   public SheepRenderState() {
      super();
      this.woolColor = DyeColor.WHITE;
   }

   public int getWoolColor() {
      return this.isJebSheep ? ColorLerper.getLerpedColor(ColorLerper.Type.SHEEP, this.ageInTicks) : ColorLerper.Type.SHEEP.getColor(this.woolColor);
   }
}
