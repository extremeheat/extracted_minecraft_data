package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class BlockAbstractSkull extends BlockContainer {
   private final BlockSkull.ISkullType field_196293_a;

   public BlockAbstractSkull(BlockSkull.ISkullType var1, Block.Properties var2) {
      super(var2);
      this.field_196293_a = var1;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_190946_v(IBlockState var1) {
      return true;
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntitySkull();
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (!var1.field_72995_K && var4.field_71075_bZ.field_75098_d) {
         TileEntitySkull.func_195486_a(var1, var2);
      }

      super.func_176208_a(var1, var2, var3, var4);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c() && !var2.field_72995_K) {
         TileEntity var6 = var2.func_175625_s(var3);
         if (var6 instanceof TileEntitySkull) {
            TileEntitySkull var7 = (TileEntitySkull)var6;
            if (var7.func_195487_d()) {
               ItemStack var8 = this.func_185473_a(var2, var3, var1);
               Block var9 = var7.func_195044_w().func_177230_c();
               if ((var9 == Blocks.field_196710_eS || var9 == Blocks.field_196709_eR) && var7.func_152108_a() != null) {
                  NBTTagCompound var10 = new NBTTagCompound();
                  NBTUtil.func_180708_a(var10, var7.func_152108_a());
                  var8.func_196082_o().func_74782_a("SkullOwner", var10);
               }

               func_180635_a(var2, var3, var8);
            }
         }

         super.func_196243_a(var1, var2, var3, var4, var5);
      }
   }

   public BlockSkull.ISkullType func_196292_N_() {
      return this.field_196293_a;
   }
}
