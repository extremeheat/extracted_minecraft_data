package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class BlockWorkbench extends Block {
   protected BlockWorkbench(Block.Properties var1) {
      super(var1);
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else {
         var4.func_180468_a(new BlockWorkbench.InterfaceCraftingTable(var2, var3));
         var4.func_195066_a(StatList.field_188062_ab);
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

      public ITextComponent func_200200_C_() {
         return new TextComponentTranslation(Blocks.field_150462_ai.func_149739_a() + ".name", new Object[0]);
      }

      public boolean func_145818_k_() {
         return false;
      }

      @Nullable
      public ITextComponent func_200201_e() {
         return null;
      }

      public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
         return new ContainerWorkbench(var1, this.field_175128_a, this.field_175127_b);
      }

      public String func_174875_k() {
         return "minecraft:crafting_table";
      }
   }
}
