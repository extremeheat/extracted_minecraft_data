package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer {
   public EntityMinecartChest(World var1) {
      super(var1);
   }

   public EntityMinecartChest(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public void func_94095_a(DamageSource var1) {
      super.func_94095_a(var1);
      this.func_145778_a(Item.func_150898_a(Blocks.field_150486_ae), 1, 0.0F);
   }

   @Override
   public int func_70302_i_() {
      return 27;
   }

   @Override
   public int func_94087_l() {
      return 1;
   }

   @Override
   public Block func_145817_o() {
      return Blocks.field_150486_ae;
   }

   @Override
   public int func_94085_r() {
      return 8;
   }
}
