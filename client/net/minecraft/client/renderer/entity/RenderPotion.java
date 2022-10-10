package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RenderPotion extends RenderSprite<EntityPotion> {
   public RenderPotion(RenderManager var1, ItemRenderer var2) {
      super(var1, Items.field_151068_bn, var2);
   }

   public ItemStack func_177082_d(EntityPotion var1) {
      return var1.func_184543_l();
   }

   // $FF: synthetic method
   public ItemStack func_177082_d(Entity var1) {
      return this.func_177082_d((EntityPotion)var1);
   }
}
