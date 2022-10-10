package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockTurtleEgg extends Block {
   private static final VoxelShape field_203172_c = Block.func_208617_a(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
   private static final VoxelShape field_206843_t = Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);
   public static final IntegerProperty field_203170_a;
   public static final IntegerProperty field_203171_b;

   public BlockTurtleEgg(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_203170_a, 0)).func_206870_a(field_203171_b, 1));
   }

   public void func_176199_a(World var1, BlockPos var2, Entity var3) {
      this.func_203167_a(var1, var2, var3, 100);
      super.func_176199_a(var1, var2, var3);
   }

   public void func_180658_a(World var1, BlockPos var2, Entity var3, float var4) {
      if (!(var3 instanceof EntityZombie)) {
         this.func_203167_a(var1, var2, var3, 3);
      }

      super.func_180658_a(var1, var2, var3, var4);
   }

   private void func_203167_a(World var1, BlockPos var2, Entity var3, int var4) {
      if (!this.func_212570_a(var1, var3)) {
         super.func_176199_a(var1, var2, var3);
      } else {
         if (!var1.field_72995_K && var1.field_73012_v.nextInt(var4) == 0) {
            this.func_203166_c(var1, var2, var1.func_180495_p(var2));
         }

      }
   }

   private void func_203166_c(World var1, BlockPos var2, IBlockState var3) {
      var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_203281_iz, SoundCategory.BLOCKS, 0.7F, 0.9F + var1.field_73012_v.nextFloat() * 0.2F);
      int var4 = (Integer)var3.func_177229_b(field_203171_b);
      if (var4 <= 1) {
         var1.func_175655_b(var2, false);
      } else {
         var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(field_203171_b, var4 - 1), 2);
         var1.func_175718_b(2001, var2, Block.func_196246_j(var3));
      }

   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (this.func_203169_a(var2) && this.func_203168_a(var2, var3)) {
         int var5 = (Integer)var1.func_177229_b(field_203170_a);
         if (var5 < 2) {
            var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_203280_iy, SoundCategory.BLOCKS, 0.7F, 0.9F + var4.nextFloat() * 0.2F);
            var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_203170_a, var5 + 1), 2);
         } else {
            var2.func_184133_a((EntityPlayer)null, var3, SoundEvents.field_203279_ix, SoundCategory.BLOCKS, 0.7F, 0.9F + var4.nextFloat() * 0.2F);
            var2.func_175698_g(var3);
            if (!var2.field_72995_K) {
               for(int var6 = 0; var6 < (Integer)var1.func_177229_b(field_203171_b); ++var6) {
                  var2.func_175718_b(2001, var3, Block.func_196246_j(var1));
                  EntityTurtle var7 = new EntityTurtle(var2);
                  var7.func_70873_a(-24000);
                  var7.func_203011_g(var3);
                  var7.func_70012_b((double)var3.func_177958_n() + 0.3D + (double)var6 * 0.2D, (double)var3.func_177956_o(), (double)var3.func_177952_p() + 0.3D, 0.0F, 0.0F);
                  var2.func_72838_d(var7);
               }
            }
         }
      }

   }

   private boolean func_203168_a(IBlockReader var1, BlockPos var2) {
      return var1.func_180495_p(var2.func_177977_b()).func_177230_c() == Blocks.field_150354_m;
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (this.func_203168_a(var2, var3) && !var2.field_72995_K) {
         var2.func_175718_b(2005, var3, 0);
      }

   }

   private boolean func_203169_a(World var1) {
      float var2 = var1.func_72826_c(1.0F);
      if ((double)var2 < 0.69D && (double)var2 > 0.65D) {
         return true;
      } else {
         return var1.field_73012_v.nextInt(500) == 0;
      }
   }

   protected boolean func_149700_E() {
      return true;
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      super.func_180657_a(var1, var2, var3, var4, var5, var6);
      this.func_203166_c(var1, var3, var4);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public boolean func_196253_a(IBlockState var1, BlockItemUseContext var2) {
      return var2.func_195996_i().func_77973_b() == this.func_199767_j() && (Integer)var1.func_177229_b(field_203171_b) < 4 ? true : super.func_196253_a(var1, var2);
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = var1.func_195991_k().func_180495_p(var1.func_195995_a());
      return var2.func_177230_c() == this ? (IBlockState)var2.func_206870_a(field_203171_b, Math.min(4, (Integer)var2.func_177229_b(field_203171_b) + 1)) : super.func_196258_a(var1);
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return (Integer)var1.func_177229_b(field_203171_b) > 1 ? field_206843_t : field_203172_c;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_203170_a, field_203171_b);
   }

   private boolean func_212570_a(World var1, Entity var2) {
      if (var2 instanceof EntityTurtle) {
         return false;
      } else {
         return var2 instanceof EntityLivingBase && !(var2 instanceof EntityPlayer) ? var1.func_82736_K().func_82766_b("mobGriefing") : true;
      }
   }

   static {
      field_203170_a = BlockStateProperties.field_208128_ac;
      field_203171_b = BlockStateProperties.field_208127_ab;
   }
}
