package net.minecraft.client.renderer.entity.state;

import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.DyeColor;

public class SheepRenderState extends LivingEntityRenderState {
   public float headEatPositionScale;
   public float headEatAngleScale;
   public boolean isSheared;
   public DyeColor woolColor;
   public int id;

   public SheepRenderState() {
      super();
      this.woolColor = DyeColor.WHITE;
   }

   public int getWoolColor() {
      if (this.isJebSheep()) {
         boolean var1 = true;
         int var2 = Mth.floor(this.ageInTicks);
         int var3 = var2 / 25 + this.id;
         int var4 = DyeColor.values().length;
         int var5 = var3 % var4;
         int var6 = (var3 + 1) % var4;
         float var7 = ((float)(var2 % 25) + Mth.frac(this.ageInTicks)) / 25.0F;
         int var8 = Sheep.getColor(DyeColor.byId(var5));
         int var9 = Sheep.getColor(DyeColor.byId(var6));
         return ARGB.lerp(var7, var8, var9);
      } else {
         return Sheep.getColor(this.woolColor);
      }
   }

   public boolean isJebSheep() {
      return this.customName != null && "jeb_".equals(this.customName.getString());
   }
}
