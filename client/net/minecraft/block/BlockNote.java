package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockNote extends Block {
   public static final EnumProperty<NoteBlockInstrument> field_196483_a;
   public static final BooleanProperty field_196484_b;
   public static final IntegerProperty field_196485_c;

   public BlockNote(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_196483_a, NoteBlockInstrument.HARP)).func_206870_a(field_196485_c, 0)).func_206870_a(field_196484_b, false));
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_196483_a, NoteBlockInstrument.func_208087_a(var1.func_195991_k().func_180495_p(var1.func_195995_a().func_177977_b())));
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var2 == EnumFacing.DOWN ? (IBlockState)var1.func_206870_a(field_196483_a, NoteBlockInstrument.func_208087_a(var3)) : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      boolean var6 = var2.func_175640_z(var3);
      if (var6 != (Boolean)var1.func_177229_b(field_196484_b)) {
         if (var6) {
            this.func_196482_a(var2, var3);
         }

         var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_196484_b, var6), 3);
      }

   }

   private void func_196482_a(World var1, BlockPos var2) {
      if (var1.func_180495_p(var2.func_177984_a()).func_196958_f()) {
         var1.func_175641_c(var2, this, 0, 0);
      }

   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else {
         var1 = (IBlockState)var1.func_177231_a(field_196485_c);
         var2.func_180501_a(var3, var1, 3);
         this.func_196482_a(var2, var3);
         var4.func_195066_a(StatList.field_188087_U);
         return true;
      }
   }

   public void func_196270_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4) {
      if (!var2.field_72995_K) {
         this.func_196482_a(var2, var3);
         var4.func_195066_a(StatList.field_188086_T);
      }
   }

   public boolean func_189539_a(IBlockState var1, World var2, BlockPos var3, int var4, int var5) {
      int var6 = (Integer)var1.func_177229_b(field_196485_c);
      float var7 = (float)Math.pow(2.0D, (double)(var6 - 12) / 12.0D);
      var2.func_184133_a((EntityPlayer)null, var3, ((NoteBlockInstrument)var1.func_177229_b(field_196483_a)).func_208088_a(), SoundCategory.RECORDS, 3.0F, var7);
      var2.func_195594_a(Particles.field_197597_H, (double)var3.func_177958_n() + 0.5D, (double)var3.func_177956_o() + 1.2D, (double)var3.func_177952_p() + 0.5D, (double)var6 / 24.0D, 0.0D, 0.0D);
      return true;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_196483_a, field_196484_b, field_196485_c);
   }

   static {
      field_196483_a = BlockStateProperties.field_208143_ar;
      field_196484_b = BlockStateProperties.field_208194_u;
      field_196485_c = BlockStateProperties.field_208134_ai;
   }
}
