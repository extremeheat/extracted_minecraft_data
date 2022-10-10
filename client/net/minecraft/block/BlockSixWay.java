package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockSixWay extends Block {
   private static final EnumFacing[] field_196494_D = EnumFacing.values();
   public static final BooleanProperty field_196488_a;
   public static final BooleanProperty field_196490_b;
   public static final BooleanProperty field_196492_c;
   public static final BooleanProperty field_196495_y;
   public static final BooleanProperty field_196496_z;
   public static final BooleanProperty field_196489_A;
   public static final Map<EnumFacing, BooleanProperty> field_196491_B;
   protected final VoxelShape[] field_196493_C;

   protected BlockSixWay(float var1, Block.Properties var2) {
      super(var2);
      this.field_196493_C = this.func_196487_d(var1);
   }

   private VoxelShape[] func_196487_d(float var1) {
      float var2 = 0.5F - var1;
      float var3 = 0.5F + var1;
      VoxelShape var4 = Block.func_208617_a((double)(var2 * 16.0F), (double)(var2 * 16.0F), (double)(var2 * 16.0F), (double)(var3 * 16.0F), (double)(var3 * 16.0F), (double)(var3 * 16.0F));
      VoxelShape[] var5 = new VoxelShape[field_196494_D.length];

      for(int var6 = 0; var6 < field_196494_D.length; ++var6) {
         EnumFacing var7 = field_196494_D[var6];
         var5[var6] = VoxelShapes.func_197873_a(0.5D + Math.min((double)(-var1), (double)var7.func_82601_c() * 0.5D), 0.5D + Math.min((double)(-var1), (double)var7.func_96559_d() * 0.5D), 0.5D + Math.min((double)(-var1), (double)var7.func_82599_e() * 0.5D), 0.5D + Math.max((double)var1, (double)var7.func_82601_c() * 0.5D), 0.5D + Math.max((double)var1, (double)var7.func_96559_d() * 0.5D), 0.5D + Math.max((double)var1, (double)var7.func_82599_e() * 0.5D));
      }

      VoxelShape[] var10 = new VoxelShape[64];

      for(int var11 = 0; var11 < 64; ++var11) {
         VoxelShape var8 = var4;

         for(int var9 = 0; var9 < field_196494_D.length; ++var9) {
            if ((var11 & 1 << var9) != 0) {
               var8 = VoxelShapes.func_197872_a(var8, var5[var9]);
            }
         }

         var10[var11] = var8;
      }

      return var10;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return this.field_196493_C[this.func_196486_i(var1)];
   }

   protected int func_196486_i(IBlockState var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < field_196494_D.length; ++var3) {
         if ((Boolean)var1.func_177229_b((IProperty)field_196491_B.get(field_196494_D[var3]))) {
            var2 |= 1 << var3;
         }
      }

      return var2;
   }

   static {
      field_196488_a = BlockStateProperties.field_208151_D;
      field_196490_b = BlockStateProperties.field_208152_E;
      field_196492_c = BlockStateProperties.field_208153_F;
      field_196495_y = BlockStateProperties.field_208154_G;
      field_196496_z = BlockStateProperties.field_208149_B;
      field_196489_A = BlockStateProperties.field_208150_C;
      field_196491_B = (Map)Util.func_200696_a(Maps.newEnumMap(EnumFacing.class), (var0) -> {
         var0.put(EnumFacing.NORTH, field_196488_a);
         var0.put(EnumFacing.EAST, field_196490_b);
         var0.put(EnumFacing.SOUTH, field_196492_c);
         var0.put(EnumFacing.WEST, field_196495_y);
         var0.put(EnumFacing.UP, field_196496_z);
         var0.put(EnumFacing.DOWN, field_196489_A);
      });
   }
}
