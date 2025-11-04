package net.minecraft.client.renderer.entity.state;

import net.minecraft.world.entity.animal.ZombieNautilusVariant;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class NautilusRenderState extends LivingEntityRenderState {
   public ItemStack saddle;
   public ItemStack bodyArmorItem;
   public @Nullable ZombieNautilusVariant variant;

   public NautilusRenderState() {
      super();
      this.saddle = ItemStack.EMPTY;
      this.bodyArmorItem = ItemStack.EMPTY;
   }
}
