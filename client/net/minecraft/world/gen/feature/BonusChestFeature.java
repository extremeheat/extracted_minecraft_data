package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.loot.LootTableList;

public class BonusChestFeature extends Feature<NoFeatureConfig> {
   public BonusChestFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      for(IBlockState var6 = var1.func_180495_p(var4); (var6.func_196958_f() || var6.func_203425_a(BlockTags.field_206952_E)) && var4.func_177956_o() > 1; var6 = var1.func_180495_p(var4)) {
         var4 = var4.func_177977_b();
      }

      if (var4.func_177956_o() < 1) {
         return false;
      } else {
         var4 = var4.func_177984_a();

         for(int var7 = 0; var7 < 4; ++var7) {
            BlockPos var8 = var4.func_177982_a(var3.nextInt(4) - var3.nextInt(4), var3.nextInt(3) - var3.nextInt(3), var3.nextInt(4) - var3.nextInt(4));
            if (var1.func_175623_d(var8) && var1.func_180495_p(var8.func_177977_b()).func_185896_q()) {
               var1.func_180501_a(var8, Blocks.field_150486_ae.func_176223_P(), 2);
               TileEntityLockableLoot.func_195479_a(var1, var3, var8, LootTableList.field_186420_b);
               BlockPos var9 = var8.func_177974_f();
               BlockPos var10 = var8.func_177976_e();
               BlockPos var11 = var8.func_177978_c();
               BlockPos var12 = var8.func_177968_d();
               if (var1.func_175623_d(var10) && var1.func_180495_p(var10.func_177977_b()).func_185896_q()) {
                  var1.func_180501_a(var10, Blocks.field_150478_aa.func_176223_P(), 2);
               }

               if (var1.func_175623_d(var9) && var1.func_180495_p(var9.func_177977_b()).func_185896_q()) {
                  var1.func_180501_a(var9, Blocks.field_150478_aa.func_176223_P(), 2);
               }

               if (var1.func_175623_d(var11) && var1.func_180495_p(var11.func_177977_b()).func_185896_q()) {
                  var1.func_180501_a(var11, Blocks.field_150478_aa.func_176223_P(), 2);
               }

               if (var1.func_175623_d(var12) && var1.func_180495_p(var12.func_177977_b()).func_185896_q()) {
                  var1.func_180501_a(var12, Blocks.field_150478_aa.func_176223_P(), 2);
               }

               return true;
            }
         }

         return false;
      }
   }
}
