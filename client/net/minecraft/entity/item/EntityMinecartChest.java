package net.minecraft.entity.item;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityMinecartChest extends EntityMinecartContainer {
   public EntityMinecartChest(World var1) {
      super(var1);
   }

   public EntityMinecartChest(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   public void func_94095_a(DamageSource var1) {
      super.func_94095_a(var1);
      if (this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
         this.func_145778_a(Item.func_150898_a(Blocks.field_150486_ae), 1, 0.0F);
      }

   }

   public int func_70302_i_() {
      return 27;
   }

   public EntityMinecart.EnumMinecartType func_180456_s() {
      return EntityMinecart.EnumMinecartType.CHEST;
   }

   public IBlockState func_180457_u() {
      return Blocks.field_150486_ae.func_176223_P().func_177226_a(BlockChest.field_176459_a, EnumFacing.NORTH);
   }

   public int func_94085_r() {
      return 8;
   }

   public String func_174875_k() {
      return "minecraft:chest";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerChest(var1, this, var2);
   }
}
