package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class EndCrystalTowerFeature extends Feature<NoFeatureConfig> {
   private boolean field_186145_a;
   private EndCrystalTowerFeature.EndSpike field_186146_b;
   private BlockPos field_186147_c;

   public EndCrystalTowerFeature() {
      super();
   }

   public void func_186143_a(EndCrystalTowerFeature.EndSpike var1) {
      this.field_186146_b = var1;
   }

   public void func_186144_a(boolean var1) {
      this.field_186145_a = var1;
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      if (this.field_186146_b == null) {
         throw new IllegalStateException("Decoration requires priming with a spike");
      } else {
         int var6 = this.field_186146_b.func_186148_c();
         Iterator var7 = BlockPos.func_177975_b(new BlockPos(var4.func_177958_n() - var6, 0, var4.func_177952_p() - var6), new BlockPos(var4.func_177958_n() + var6, this.field_186146_b.func_186149_d() + 10, var4.func_177952_p() + var6)).iterator();

         while(true) {
            while(var7.hasNext()) {
               BlockPos.MutableBlockPos var8 = (BlockPos.MutableBlockPos)var7.next();
               if (var8.func_177954_c((double)var4.func_177958_n(), (double)var8.func_177956_o(), (double)var4.func_177952_p()) <= (double)(var6 * var6 + 1) && var8.func_177956_o() < this.field_186146_b.func_186149_d()) {
                  this.func_202278_a(var1, var8, Blocks.field_150343_Z.func_176223_P());
               } else if (var8.func_177956_o() > 65) {
                  this.func_202278_a(var1, var8, Blocks.field_150350_a.func_176223_P());
               }
            }

            if (this.field_186146_b.func_186150_e()) {
               boolean var20 = true;
               boolean var22 = true;
               boolean var9 = true;
               BlockPos.MutableBlockPos var10 = new BlockPos.MutableBlockPos();

               for(int var11 = -2; var11 <= 2; ++var11) {
                  for(int var12 = -2; var12 <= 2; ++var12) {
                     for(int var13 = 0; var13 <= 3; ++var13) {
                        boolean var14 = MathHelper.func_76130_a(var11) == 2;
                        boolean var15 = MathHelper.func_76130_a(var12) == 2;
                        boolean var16 = var13 == 3;
                        if (var14 || var15 || var16) {
                           boolean var17 = var11 == -2 || var11 == 2 || var16;
                           boolean var18 = var12 == -2 || var12 == 2 || var16;
                           IBlockState var19 = (IBlockState)((IBlockState)((IBlockState)((IBlockState)Blocks.field_150411_aY.func_176223_P().func_206870_a(BlockPane.field_196409_a, var17 && var12 != -2)).func_206870_a(BlockPane.field_196413_c, var17 && var12 != 2)).func_206870_a(BlockPane.field_196414_y, var18 && var11 != -2)).func_206870_a(BlockPane.field_196411_b, var18 && var11 != 2);
                           this.func_202278_a(var1, var10.func_181079_c(var4.func_177958_n() + var11, this.field_186146_b.func_186149_d() + var13, var4.func_177952_p() + var12), var19);
                        }
                     }
                  }
               }
            }

            EntityEnderCrystal var21 = new EntityEnderCrystal(var1.func_201672_e());
            var21.func_184516_a(this.field_186147_c);
            var21.func_184224_h(this.field_186145_a);
            var21.func_70012_b((double)((float)var4.func_177958_n() + 0.5F), (double)(this.field_186146_b.func_186149_d() + 1), (double)((float)var4.func_177952_p() + 0.5F), var3.nextFloat() * 360.0F, 0.0F);
            var1.func_72838_d(var21);
            this.func_202278_a(var1, new BlockPos(var4.func_177958_n(), this.field_186146_b.func_186149_d(), var4.func_177952_p()), Blocks.field_150357_h.func_176223_P());
            return true;
         }
      }
   }

   public void func_186142_a(@Nullable BlockPos var1) {
      this.field_186147_c = var1;
   }

   public static class EndSpike {
      private final int field_186155_a;
      private final int field_186156_b;
      private final int field_186157_c;
      private final int field_186158_d;
      private final boolean field_186159_e;
      private final AxisAlignedBB field_186160_f;

      public EndSpike(int var1, int var2, int var3, int var4, boolean var5) {
         super();
         this.field_186155_a = var1;
         this.field_186156_b = var2;
         this.field_186157_c = var3;
         this.field_186158_d = var4;
         this.field_186159_e = var5;
         this.field_186160_f = new AxisAlignedBB((double)(var1 - var3), 0.0D, (double)(var2 - var3), (double)(var1 + var3), 256.0D, (double)(var2 + var3));
      }

      public boolean func_186154_a(BlockPos var1) {
         int var2 = this.field_186155_a - this.field_186157_c;
         int var3 = this.field_186156_b - this.field_186157_c;
         return var1.func_177958_n() == (var2 & -16) && var1.func_177952_p() == (var3 & -16);
      }

      public int func_186151_a() {
         return this.field_186155_a;
      }

      public int func_186152_b() {
         return this.field_186156_b;
      }

      public int func_186148_c() {
         return this.field_186157_c;
      }

      public int func_186149_d() {
         return this.field_186158_d;
      }

      public boolean func_186150_e() {
         return this.field_186159_e;
      }

      public AxisAlignedBB func_186153_f() {
         return this.field_186160_f;
      }
   }
}
