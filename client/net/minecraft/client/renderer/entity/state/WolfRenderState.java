package net.minecraft.client.renderer.entity.state;

import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class WolfRenderState extends LivingEntityRenderState {
   private static final Identifier DEFAULT_TEXTURE = Identifier.withDefaultNamespace("textures/entity/wolf/wolf.png");
   public boolean isAngry;
   public boolean isSitting;
   public float tailAngle = 0.62831855F;
   public float headRollAngle;
   public float shakeAnim;
   public float wetShade = 1.0F;
   public Identifier texture;
   public @Nullable DyeColor collarColor;
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

      return Mth.sin((double)(var2 * 3.1415927F)) * Mth.sin((double)(var2 * 3.1415927F * 11.0F)) * 0.15F * 3.1415927F;
   }
}
