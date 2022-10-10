package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {
   public static final BooleanProperty field_176580_a;
   private final BlockPressurePlate.Sensitivity field_150069_a;

   protected BlockPressurePlate(BlockPressurePlate.Sensitivity var1, Block.Properties var2) {
      super(var2);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176580_a, false));
      this.field_150069_a = var1;
   }

   protected int func_176576_e(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_176580_a) ? 15 : 0;
   }

   protected IBlockState func_176575_a(IBlockState var1, int var2) {
      return (IBlockState)var1.func_206870_a(field_176580_a, var2 > 0);
   }

   protected void func_185507_b(IWorld var1, BlockPos var2) {
      if (this.field_149764_J == Material.field_151575_d) {
         var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187895_gX, SoundCategory.BLOCKS, 0.3F, 0.8F);
      } else {
         var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187901_ga, SoundCategory.BLOCKS, 0.3F, 0.6F);
      }

   }

   protected void func_185508_c(IWorld var1, BlockPos var2) {
      if (this.field_149764_J == Material.field_151575_d) {
         var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187893_gW, SoundCategory.BLOCKS, 0.3F, 0.7F);
      } else {
         var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187847_fZ, SoundCategory.BLOCKS, 0.3F, 0.5F);
      }

   }

   protected int func_180669_e(World var1, BlockPos var2) {
      AxisAlignedBB var3 = field_185511_c.func_186670_a(var2);
      List var4;
      switch(this.field_150069_a) {
      case EVERYTHING:
         var4 = var1.func_72839_b((Entity)null, var3);
         break;
      case MOBS:
         var4 = var1.func_72872_a(EntityLivingBase.class, var3);
         break;
      default:
         return 0;
      }

      if (!var4.isEmpty()) {
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Entity var6 = (Entity)var5.next();
            if (!var6.func_145773_az()) {
               return 15;
            }
         }
      }

      return 0;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176580_a);
   }

   static {
      field_176580_a = BlockStateProperties.field_208194_u;
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;

      private Sensitivity() {
      }
   }
}
