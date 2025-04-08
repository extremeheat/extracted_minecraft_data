package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class WolfRenderState extends LivingEntityRenderState {
   private static final ResourceLocation DEFAULT_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/wolf/wolf.png");
   public boolean isAngry;
   public boolean isSitting;
   public float tailAngle = 0.62831855F;
   public float headRollAngle;
   public float shakeAnim;
   public float wetShade = 1.0F;
   public ResourceLocation texture;
   @Nullable
   public DyeColor collarColor;
   public ItemStack bodyArmorItem;

   public WolfRenderState() {
      super();
      this.texture = DEFAULT_TEXTURE;
      this.bodyArmorItem = ItemStack.EMPTY;
   }

   public float getBodyRollAngle(float var1) {
      float var2 = (this.shakeAnim + var1) / 1.8F;
      if (var2 < 0.0F) {
         var2 = 0.0F;
      } else if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      return Mth.sin(var2 * 3.1415927F) * Mth.sin(var2 * 3.1415927F * 11.0F) * 0.15F * 3.1415927F;
   }
}
