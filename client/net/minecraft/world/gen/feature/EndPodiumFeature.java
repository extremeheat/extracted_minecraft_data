package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.BlockTorchWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class EndPodiumFeature extends Feature<NoFeatureConfig> {
   public static final BlockPos field_186139_a;
   private final boolean field_186141_c;

   public EndPodiumFeature(boolean var1) {
      super();
      this.field_186141_c = var1;
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      Iterator var6 = BlockPos.func_177975_b(new BlockPos(var4.func_177958_n() - 4, var4.func_177956_o() - 1, var4.func_177952_p() - 4), new BlockPos(var4.func_177958_n() + 4, var4.func_177956_o() + 32, var4.func_177952_p() + 4)).iterator();

      while(var6.hasNext()) {
         BlockPos.MutableBlockPos var7 = (BlockPos.MutableBlockPos)var6.next();
         double var8 = var7.func_185332_f(var4.func_177958_n(), var7.func_177956_o(), var4.func_177952_p());
         if (var8 <= 3.5D) {
            if (var7.func_177956_o() < var4.func_177956_o()) {
               if (var8 <= 2.5D) {
                  this.func_202278_a(var1, var7, Blocks.field_150357_h.func_176223_P());
               } else if (var7.func_177956_o() < var4.func_177956_o()) {
                  this.func_202278_a(var1, var7, Blocks.field_150377_bs.func_176223_P());
               }
            } else if (var7.func_177956_o() > var4.func_177956_o()) {
               this.func_202278_a(var1, var7, Blocks.field_150350_a.func_176223_P());
            } else if (var8 > 2.5D) {
               this.func_202278_a(var1, var7, Blocks.field_150357_h.func_176223_P());
            } else if (this.field_186141_c) {
               this.func_202278_a(var1, new BlockPos(var7), Blocks.field_150384_bq.func_176223_P());
            } else {
               this.func_202278_a(var1, new BlockPos(var7), Blocks.field_150350_a.func_176223_P());
            }
         }
      }

      for(int var10 = 0; var10 < 4; ++var10) {
         this.func_202278_a(var1, var4.func_177981_b(var10), Blocks.field_150357_h.func_176223_P());
      }

      BlockPos var11 = var4.func_177981_b(2);
      Iterator var12 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var12.hasNext()) {
         EnumFacing var13 = (EnumFacing)var12.next();
         this.func_202278_a(var1, var11.func_177972_a(var13), (IBlockState)Blocks.field_196591_bQ.func_176223_P().func_206870_a(BlockTorchWall.field_196532_a, var13));
      }

      return true;
   }

   static {
      field_186139_a = BlockPos.field_177992_a;
   }
}
