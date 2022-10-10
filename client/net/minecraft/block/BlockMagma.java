package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class BlockMagma extends Block {
   public BlockMagma(Block.Properties var1) {
      super(var1);
   }

   public void func_176199_a(World var1, BlockPos var2, Entity var3) {
      if (!var3.func_70045_F() && var3 instanceof EntityLivingBase && !EnchantmentHelper.func_189869_j((EntityLivingBase)var3)) {
         var3.func_70097_a(DamageSource.field_190095_e, 1.0F);
      }

      super.func_176199_a(var1, var2, var3);
   }

   public int func_185484_c(IBlockState var1, IWorldReader var2, BlockPos var3) {
      return 15728880;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      BlockBubbleColumn.func_203159_a(var2, var3.func_177984_a(), true);
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (var2 == EnumFacing.UP && var3.func_177230_c() == Blocks.field_150355_j) {
         var4.func_205220_G_().func_205360_a(var5, this, this.func_149738_a(var4));
      }

      return super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public void func_196265_a(IBlockState var1, World var2, BlockPos var3, Random var4) {
      BlockPos var5 = var3.func_177984_a();
      if (var2.func_204610_c(var3).func_206884_a(FluidTags.field_206959_a)) {
         var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_187646_bt, SoundCategory.BLOCKS, 0.5F, 2.6F + (var2.field_73012_v.nextFloat() - var2.field_73012_v.nextFloat()) * 0.8F);
         if (var2 instanceof WorldServer) {
            ((WorldServer)var2).func_195598_a(Particles.field_197594_E, (double)var5.func_177958_n() + 0.5D, (double)var5.func_177956_o() + 0.25D, (double)var5.func_177952_p() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
         }
      }

   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 20;
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      var2.func_205220_G_().func_205360_a(var3, this, this.func_149738_a(var2));
   }

   public boolean func_189872_a(IBlockState var1, Entity var2) {
      return var2.func_70045_F();
   }

   public boolean func_201783_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return true;
   }
}
