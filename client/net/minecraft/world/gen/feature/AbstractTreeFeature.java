package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public abstract class AbstractTreeFeature<T extends IFeatureConfig> extends Feature<T> {
   public AbstractTreeFeature(boolean var1) {
      super(var1);
   }

   protected boolean func_150523_a(Block var1) {
      IBlockState var2 = var1.func_176223_P();
      return var2.func_196958_f() || var2.func_203425_a(BlockTags.field_206952_E) || var1 == Blocks.field_196658_i || Block.func_196245_f(var1) || var1.func_203417_a(BlockTags.field_200031_h) || var1.func_203417_a(BlockTags.field_200030_g) || var1 == Blocks.field_150395_bd;
   }

   protected void func_175921_a(IWorld var1, BlockPos var2) {
      if (!Block.func_196245_f(var1.func_180495_p(var2).func_177230_c())) {
         this.func_202278_a(var1, var2, Blocks.field_150346_d.func_176223_P());
      }

   }

   protected void func_202278_a(IWorld var1, BlockPos var2, IBlockState var3) {
      this.func_208521_b(var1, var2, var3);
   }

   protected final void func_208520_a(Set<BlockPos> var1, IWorld var2, BlockPos var3, IBlockState var4) {
      this.func_208521_b(var2, var3, var4);
      if (BlockTags.field_200031_h.func_199685_a_(var4.func_177230_c())) {
         var1.add(var3.func_185334_h());
      }

   }

   private void func_208521_b(IWorld var1, BlockPos var2, IBlockState var3) {
      if (this.field_76488_a) {
         var1.func_180501_a(var2, var3, 19);
      } else {
         var1.func_180501_a(var2, var3, 18);
      }

   }

   public final boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, T var5) {
      HashSet var6 = Sets.newHashSet();
      boolean var7 = this.func_208519_a(var6, var1, var3, var4);
      ArrayList var8 = Lists.newArrayList();
      boolean var9 = true;

      for(int var10 = 0; var10 < 6; ++var10) {
         var8.add(Sets.newHashSet());
      }

      BlockPos.PooledMutableBlockPos var33 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var11 = null;

      try {
         if (var7 && !var6.isEmpty()) {
            Iterator var12 = Lists.newArrayList(var6).iterator();

            while(var12.hasNext()) {
               BlockPos var13 = (BlockPos)var12.next();
               EnumFacing[] var14 = EnumFacing.values();
               int var15 = var14.length;

               for(int var16 = 0; var16 < var15; ++var16) {
                  EnumFacing var17 = var14[var16];
                  var33.func_189533_g(var13).func_189536_c(var17);
                  if (!var6.contains(var33)) {
                     IBlockState var18 = var1.func_180495_p(var33);
                     if (var18.func_196959_b(BlockStateProperties.field_208514_aa)) {
                        ((Set)var8.get(0)).add(var33.func_185334_h());
                        this.func_208521_b(var1, var33, (IBlockState)var18.func_206870_a(BlockStateProperties.field_208514_aa, 1));
                     }
                  }
               }
            }
         }

         for(int var34 = 1; var34 < 6; ++var34) {
            Set var35 = (Set)var8.get(var34 - 1);
            Set var36 = (Set)var8.get(var34);
            Iterator var37 = var35.iterator();

            while(var37.hasNext()) {
               BlockPos var38 = (BlockPos)var37.next();
               EnumFacing[] var39 = EnumFacing.values();
               int var40 = var39.length;

               for(int var19 = 0; var19 < var40; ++var19) {
                  EnumFacing var20 = var39[var19];
                  var33.func_189533_g(var38).func_189536_c(var20);
                  if (!var35.contains(var33) && !var36.contains(var33)) {
                     IBlockState var21 = var1.func_180495_p(var33);
                     if (var21.func_196959_b(BlockStateProperties.field_208514_aa)) {
                        int var22 = (Integer)var21.func_177229_b(BlockStateProperties.field_208514_aa);
                        if (var22 > var34 + 1) {
                           IBlockState var23 = (IBlockState)var21.func_206870_a(BlockStateProperties.field_208514_aa, var34 + 1);
                           this.func_208521_b(var1, var33, var23);
                           var36.add(var33.func_185334_h());
                        }
                     }
                  }
               }
            }
         }
      } catch (Throwable var31) {
         var11 = var31;
         throw var31;
      } finally {
         if (var33 != null) {
            if (var11 != null) {
               try {
                  var33.close();
               } catch (Throwable var30) {
                  var11.addSuppressed(var30);
               }
            } else {
               var33.close();
            }
         }

      }

      return var7;
   }

   protected abstract boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4);
}
