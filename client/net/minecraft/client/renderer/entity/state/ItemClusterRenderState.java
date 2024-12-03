package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemClusterRenderState extends EntityRenderState {
   public final ItemStackRenderState item = new ItemStackRenderState();
   public int count;
   public int seed;

   public ItemClusterRenderState() {
      super();
   }

   public void extractItemGroupRenderState(Entity var1, ItemStack var2, ItemModelResolver var3) {
      var3.updateForNonLiving(this.item, var2, ItemDisplayContext.GROUND, var1);
      this.count = getRenderedAmount(var2.getCount());
      this.seed = getSeedForItemStack(var2);
   }

   public static int getSeedForItemStack(ItemStack var0) {
      return var0.isEmpty() ? 187 : Item.getId(var0.getItem()) + var0.getDamageValue();
   }

   public static int getRenderedAmount(int var0) {
      if (var0 <= 1) {
         return 1;
      } else if (var0 <= 16) {
         return 2;
      } else if (var0 <= 32) {
         return 3;
      } else {
         return var0 <= 48 ? 4 : 5;
      }
   }
}
