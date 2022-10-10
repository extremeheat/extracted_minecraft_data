package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class DesertWellsFeature extends Feature<NoFeatureConfig> {
   private static final BlockStateMatcher field_175913_a;
   private final IBlockState field_175911_b;
   private final IBlockState field_175912_c;
   private final IBlockState field_175910_d;

   public DesertWellsFeature() {
      super();
      this.field_175911_b = Blocks.field_196640_bx.func_176223_P();
      this.field_175912_c = Blocks.field_150322_A.func_176223_P();
      this.field_175910_d = Blocks.field_150355_j.func_176223_P();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      for(var4 = var4.func_177984_a(); var1.func_175623_d(var4) && var4.func_177956_o() > 2; var4 = var4.func_177977_b()) {
      }

      if (!field_175913_a.test(var1.func_180495_p(var4))) {
         return false;
      } else {
         int var6;
         int var7;
         for(var6 = -2; var6 <= 2; ++var6) {
            for(var7 = -2; var7 <= 2; ++var7) {
               if (var1.func_175623_d(var4.func_177982_a(var6, -1, var7)) && var1.func_175623_d(var4.func_177982_a(var6, -2, var7))) {
                  return false;
               }
            }
         }

         for(var6 = -1; var6 <= 0; ++var6) {
            for(var7 = -2; var7 <= 2; ++var7) {
               for(int var8 = -2; var8 <= 2; ++var8) {
                  var1.func_180501_a(var4.func_177982_a(var7, var6, var8), this.field_175912_c, 2);
               }
            }
         }

         var1.func_180501_a(var4, this.field_175910_d, 2);
         Iterator var9 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var9.hasNext()) {
            EnumFacing var10 = (EnumFacing)var9.next();
            var1.func_180501_a(var4.func_177972_a(var10), this.field_175910_d, 2);
         }

         for(var6 = -2; var6 <= 2; ++var6) {
            for(var7 = -2; var7 <= 2; ++var7) {
               if (var6 == -2 || var6 == 2 || var7 == -2 || var7 == 2) {
                  var1.func_180501_a(var4.func_177982_a(var6, 1, var7), this.field_175912_c, 2);
               }
            }
         }

         var1.func_180501_a(var4.func_177982_a(2, 1, 0), this.field_175911_b, 2);
         var1.func_180501_a(var4.func_177982_a(-2, 1, 0), this.field_175911_b, 2);
         var1.func_180501_a(var4.func_177982_a(0, 1, 2), this.field_175911_b, 2);
         var1.func_180501_a(var4.func_177982_a(0, 1, -2), this.field_175911_b, 2);

         for(var6 = -1; var6 <= 1; ++var6) {
            for(var7 = -1; var7 <= 1; ++var7) {
               if (var6 == 0 && var7 == 0) {
                  var1.func_180501_a(var4.func_177982_a(var6, 4, var7), this.field_175912_c, 2);
               } else {
                  var1.func_180501_a(var4.func_177982_a(var6, 4, var7), this.field_175911_b, 2);
               }
            }
         }

         for(var6 = 1; var6 <= 3; ++var6) {
            var1.func_180501_a(var4.func_177982_a(-1, var6, -1), this.field_175912_c, 2);
            var1.func_180501_a(var4.func_177982_a(-1, var6, 1), this.field_175912_c, 2);
            var1.func_180501_a(var4.func_177982_a(1, var6, -1), this.field_175912_c, 2);
            var1.func_180501_a(var4.func_177982_a(1, var6, 1), this.field_175912_c, 2);
         }

         return true;
      }
   }

   static {
      field_175913_a = BlockStateMatcher.func_177638_a(Blocks.field_150354_m);
   }
}
