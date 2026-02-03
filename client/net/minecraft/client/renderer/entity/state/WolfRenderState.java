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

   public float getBodyRollAngle(final float offset) {
      float progress = (this.shakeAnim + offset) / 1.8F;
      if (progress < 0.0F) {
         progress = 0.0F;
      } else if (progress > 1.0F) {
         progress = 1.0F;
      }

      return Mth.sin((double)(progress * 3.1415927F)) * Mth.sin((double)(progress * 3.1415927F * 11.0F)) * 0.15F * 3.1415927F;
   }
}
