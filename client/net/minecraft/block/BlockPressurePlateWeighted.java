package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
   public static final IntegerProperty field_176579_a;
   private final int field_150068_a;

   protected BlockPressurePlateWeighted(int var1, Block.Properties var2) {
      super(var2);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176579_a, 0));
      this.field_150068_a = var1;
   }

   protected int func_180669_e(World var1, BlockPos var2) {
      int var3 = Math.min(var1.func_72872_a(Entity.class, field_185511_c.func_186670_a(var2)).size(), this.field_150068_a);
      if (var3 > 0) {
         float var4 = (float)Math.min(this.field_150068_a, var3) / (float)this.field_150068_a;
         return MathHelper.func_76123_f(var4 * 15.0F);
      } else {
         return 0;
      }
   }

   protected void func_185507_b(IWorld var1, BlockPos var2) {
      var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187776_dp, SoundCategory.BLOCKS, 0.3F, 0.90000004F);
   }

   protected void func_185508_c(IWorld var1, BlockPos var2) {
      var1.func_184133_a((EntityPlayer)null, var2, SoundEvents.field_187774_do, SoundCategory.BLOCKS, 0.3F, 0.75F);
   }

   protected int func_176576_e(IBlockState var1) {
      return (Integer)var1.func_177229_b(field_176579_a);
   }

   protected IBlockState func_176575_a(IBlockState var1, int var2) {
      return (IBlockState)var1.func_206870_a(field_176579_a, var2);
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 10;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176579_a);
   }

   static {
      field_176579_a = BlockStateProperties.field_208136_ak;
   }
}
