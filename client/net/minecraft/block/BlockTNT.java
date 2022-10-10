package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockTNT extends Block {
   public static final BooleanProperty field_212569_a;

   public BlockTNT(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)this.func_176223_P().func_206870_a(field_212569_a, false));
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c()) {
         if (var2.func_175640_z(var3)) {
            this.func_196534_a(var2, var3);
            var2.func_175698_g(var3);
         }

      }
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (var2.func_175640_z(var3)) {
         this.func_196534_a(var2, var3);
         var2.func_175698_g(var3);
      }

   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      if (!(Boolean)var1.func_177229_b(field_212569_a)) {
         super.func_196255_a(var1, var2, var3, var4, var5);
      }
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (!var1.func_201670_d() && !var4.func_184812_l_() && (Boolean)var3.func_177229_b(field_212569_a)) {
         this.func_196534_a(var1, var2);
      }

      super.func_176208_a(var1, var2, var3, var4);
   }

   public void func_180652_a(World var1, BlockPos var2, Explosion var3) {
      if (!var1.field_72995_K) {
         EntityTNTPrimed var4 = new EntityTNTPrimed(var1, (double)((float)var2.func_177958_n() + 0.5F), (double)var2.func_177956_o(), (double)((float)var2.func_177952_p() + 0.5F), var3.func_94613_c());
         var4.func_184534_a((short)(var1.field_73012_v.nextInt(var4.func_184536_l() / 4) + var4.func_184536_l() / 8));
         var1.func_72838_d(var4);
      }
   }

   public void func_196534_a(World var1, BlockPos var2) {
      this.func_196535_a(var1, var2, (EntityLivingBase)null);
   }

   private void func_196535_a(World var1, BlockPos var2, @Nullable EntityLivingBase var3) {
      if (!var1.field_72995_K) {
         EntityTNTPrimed var4 = new EntityTNTPrimed(var1, (double)((float)var2.func_177958_n() + 0.5F), (double)var2.func_177956_o(), (double)((float)var2.func_177952_p() + 0.5F), var3);
         var1.func_72838_d(var4);
         var1.func_184148_a((EntityPlayer)null, var4.field_70165_t, var4.field_70163_u, var4.field_70161_v, SoundEvents.field_187904_gd, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      ItemStack var10 = var4.func_184586_b(var5);
      Item var11 = var10.func_77973_b();
      if (var11 != Items.field_151033_d && var11 != Items.field_151059_bz) {
         return super.func_196250_a(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         this.func_196535_a(var2, var3, var4);
         var2.func_180501_a(var3, Blocks.field_150350_a.func_176223_P(), 11);
         if (var11 == Items.field_151033_d) {
            var10.func_77972_a(1, var4);
         } else {
            var10.func_190918_g(1);
         }

         return true;
      }
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      if (!var2.field_72995_K && var4 instanceof EntityArrow) {
         EntityArrow var5 = (EntityArrow)var4;
         Entity var6 = var5.func_212360_k();
         if (var5.func_70027_ad()) {
            this.func_196535_a(var2, var3, var6 instanceof EntityLivingBase ? (EntityLivingBase)var6 : null);
            var2.func_175698_g(var3);
         }
      }

   }

   public boolean func_149659_a(Explosion var1) {
      return false;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_212569_a);
   }

   static {
      field_212569_a = BlockStateProperties.field_212646_x;
   }
}
