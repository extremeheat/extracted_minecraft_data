package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class BlockWorkbench extends Block {
   protected BlockWorkbench() {
      super(Material.field_151575_d);
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         var4.func_180468_a(new BlockWorkbench.InterfaceCraftingTable(var1, var2));
         var4.func_71029_a(StatList.field_181742_Z);
         return true;
      }
   }

   public static class InterfaceCraftingTable implements IInteractionObject {
      private final World field_175128_a;
      private final BlockPos field_175127_b;

      public InterfaceCraftingTable(World var1, BlockPos var2) {
         super();
         this.field_175128_a = var1;
         this.field_175127_b = var2;
      }

      public String func_70005_c_() {
         return null;
      }

      public boolean func_145818_k_() {
         return false;
      }

      public IChatComponent func_145748_c_() {
         return new ChatComponentTranslation(Blocks.field_150462_ai.func_149739_a() + ".name", new Object[0]);
      }

      public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
         return new ContainerWorkbench(var1, this.field_175128_a, this.field_175127_b);
      }

      public String func_174875_k() {
         return "minecraft:crafting_table";
      }
   }
}
