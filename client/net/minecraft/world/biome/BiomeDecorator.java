package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeDecorator {
   protected World field_76815_a;
   protected Random field_76813_b;
   protected int field_76814_c;
   protected int field_76811_d;
   protected WorldGenerator field_76809_f = new WorldGenClay(4);
   protected WorldGenerator field_76810_g = new WorldGenSand(Blocks.field_150354_m, 7);
   protected WorldGenerator field_76822_h = new WorldGenSand(Blocks.field_150351_n, 6);
   protected WorldGenerator field_76823_i = new WorldGenMinable(Blocks.field_150346_d, 32);
   protected WorldGenerator field_76820_j = new WorldGenMinable(Blocks.field_150351_n, 32);
   protected WorldGenerator field_76821_k = new WorldGenMinable(Blocks.field_150365_q, 16);
   protected WorldGenerator field_76818_l = new WorldGenMinable(Blocks.field_150366_p, 8);
   protected WorldGenerator field_76819_m = new WorldGenMinable(Blocks.field_150352_o, 8);
   protected WorldGenerator field_76816_n = new WorldGenMinable(Blocks.field_150450_ax, 7);
   protected WorldGenerator field_76817_o = new WorldGenMinable(Blocks.field_150482_ag, 7);
   protected WorldGenerator field_76831_p = new WorldGenMinable(Blocks.field_150369_x, 6);
   protected WorldGenFlowers field_150514_p = new WorldGenFlowers(Blocks.field_150327_N);
   protected WorldGenerator field_76828_s = new WorldGenFlowers(Blocks.field_150338_P);
   protected WorldGenerator field_76827_t = new WorldGenFlowers(Blocks.field_150337_Q);
   protected WorldGenerator field_76826_u = new WorldGenBigMushroom();
   protected WorldGenerator field_76825_v = new WorldGenReed();
   protected WorldGenerator field_76824_w = new WorldGenCactus();
   protected WorldGenerator field_76834_x = new WorldGenWaterlily();
   protected int field_76833_y;
   protected int field_76832_z;
   protected int field_76802_A = 2;
   protected int field_76803_B = 1;
   protected int field_76804_C;
   protected int field_76798_D;
   protected int field_76799_E;
   protected int field_76800_F;
   protected int field_76801_G = 1;
   protected int field_76805_H = 3;
   protected int field_76806_I = 1;
   protected int field_76807_J;
   public boolean field_76808_K = true;

   public BiomeDecorator() {
      super();
   }

   public void func_150512_a(World var1, Random var2, BiomeGenBase var3, int var4, int var5) {
      if (this.field_76815_a != null) {
         throw new RuntimeException("Already decorating!!");
      } else {
         this.field_76815_a = var1;
         this.field_76813_b = var2;
         this.field_76814_c = var4;
         this.field_76811_d = var5;
         this.func_150513_a(var3);
         this.field_76815_a = null;
         this.field_76813_b = null;
      }
   }

   protected void func_150513_a(BiomeGenBase var1) {
      this.func_76797_b();

      for(int var2 = 0; var2 < this.field_76805_H; ++var2) {
         int var3 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var4 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         this.field_76810_g.func_76484_a(this.field_76815_a, this.field_76813_b, var3, this.field_76815_a.func_72825_h(var3, var4), var4);
      }

      for(int var9 = 0; var9 < this.field_76806_I; ++var9) {
         int var12 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var29 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         this.field_76809_f.func_76484_a(this.field_76815_a, this.field_76813_b, var12, this.field_76815_a.func_72825_h(var12, var29), var29);
      }

      for(int var10 = 0; var10 < this.field_76801_G; ++var10) {
         int var13 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var30 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         this.field_76822_h.func_76484_a(this.field_76815_a, this.field_76813_b, var13, this.field_76815_a.func_72825_h(var13, var30), var30);
      }

      int var11 = this.field_76832_z;
      if (this.field_76813_b.nextInt(10) == 0) {
         ++var11;
      }

      for(int var14 = 0; var14 < var11; ++var14) {
         int var31 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var5 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var6 = this.field_76815_a.func_72976_f(var31, var5);
         WorldGenAbstractTree var7 = var1.func_150567_a(this.field_76813_b);
         var7.func_76487_a(1.0, 1.0, 1.0);
         if (var7.func_76484_a(this.field_76815_a, this.field_76813_b, var31, var6, var5)) {
            var7.func_150524_b(this.field_76815_a, this.field_76813_b, var31, var6, var5);
         }
      }

      for(int var15 = 0; var15 < this.field_76807_J; ++var15) {
         int var32 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var47 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         this.field_76826_u.func_76484_a(this.field_76815_a, this.field_76813_b, var32, this.field_76815_a.func_72976_f(var32, var47), var47);
      }

      for(int var16 = 0; var16 < this.field_76802_A; ++var16) {
         int var33 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var48 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var62 = this.field_76813_b.nextInt(this.field_76815_a.func_72976_f(var33, var48) + 32);
         String var73 = var1.func_150572_a(this.field_76813_b, var33, var62, var48);
         BlockFlower var8 = BlockFlower.func_149857_e(var73);
         if (var8.func_149688_o() != Material.field_151579_a) {
            this.field_150514_p.func_150550_a(var8, BlockFlower.func_149856_f(var73));
            this.field_150514_p.func_76484_a(this.field_76815_a, this.field_76813_b, var33, var62, var48);
         }
      }

      for(int var17 = 0; var17 < this.field_76803_B; ++var17) {
         int var34 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var49 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var63 = this.field_76813_b.nextInt(this.field_76815_a.func_72976_f(var34, var49) * 2);
         WorldGenerator var74 = var1.func_76730_b(this.field_76813_b);
         var74.func_76484_a(this.field_76815_a, this.field_76813_b, var34, var63, var49);
      }

      for(int var18 = 0; var18 < this.field_76804_C; ++var18) {
         int var35 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var50 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var64 = this.field_76813_b.nextInt(this.field_76815_a.func_72976_f(var35, var50) * 2);
         new WorldGenDeadBush(Blocks.field_150330_I).func_76484_a(this.field_76815_a, this.field_76813_b, var35, var64, var50);
      }

      for(int var19 = 0; var19 < this.field_76833_y; ++var19) {
         int var36 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var51 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var65 = this.field_76813_b.nextInt(this.field_76815_a.func_72976_f(var36, var51) * 2);

         while(var65 > 0 && this.field_76815_a.func_147437_c(var36, var65 - 1, var51)) {
            --var65;
         }

         this.field_76834_x.func_76484_a(this.field_76815_a, this.field_76813_b, var36, var65, var51);
      }

      for(int var20 = 0; var20 < this.field_76798_D; ++var20) {
         if (this.field_76813_b.nextInt(4) == 0) {
            int var37 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
            int var52 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
            int var66 = this.field_76815_a.func_72976_f(var37, var52);
            this.field_76828_s.func_76484_a(this.field_76815_a, this.field_76813_b, var37, var66, var52);
         }

         if (this.field_76813_b.nextInt(8) == 0) {
            int var38 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
            int var53 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
            int var67 = this.field_76813_b.nextInt(this.field_76815_a.func_72976_f(var38, var53) * 2);
            this.field_76827_t.func_76484_a(this.field_76815_a, this.field_76813_b, var38, var67, var53);
         }
      }

      if (this.field_76813_b.nextInt(4) == 0) {
         int var21 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var39 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var54 = this.field_76813_b.nextInt(this.field_76815_a.func_72976_f(var21, var39) * 2);
         this.field_76828_s.func_76484_a(this.field_76815_a, this.field_76813_b, var21, var54, var39);
      }

      if (this.field_76813_b.nextInt(8) == 0) {
         int var22 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var40 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var55 = this.field_76813_b.nextInt(this.field_76815_a.func_72976_f(var22, var40) * 2);
         this.field_76827_t.func_76484_a(this.field_76815_a, this.field_76813_b, var22, var55, var40);
      }

      for(int var23 = 0; var23 < this.field_76799_E; ++var23) {
         int var41 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var56 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var68 = this.field_76813_b.nextInt(this.field_76815_a.func_72976_f(var41, var56) * 2);
         this.field_76825_v.func_76484_a(this.field_76815_a, this.field_76813_b, var41, var68, var56);
      }

      for(int var24 = 0; var24 < 10; ++var24) {
         int var42 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var57 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var69 = this.field_76813_b.nextInt(this.field_76815_a.func_72976_f(var42, var57) * 2);
         this.field_76825_v.func_76484_a(this.field_76815_a, this.field_76813_b, var42, var69, var57);
      }

      if (this.field_76813_b.nextInt(32) == 0) {
         int var25 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var43 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var58 = this.field_76813_b.nextInt(this.field_76815_a.func_72976_f(var25, var43) * 2);
         new WorldGenPumpkin().func_76484_a(this.field_76815_a, this.field_76813_b, var25, var58, var43);
      }

      for(int var26 = 0; var26 < this.field_76800_F; ++var26) {
         int var44 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
         int var59 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
         int var70 = this.field_76813_b.nextInt(this.field_76815_a.func_72976_f(var44, var59) * 2);
         this.field_76824_w.func_76484_a(this.field_76815_a, this.field_76813_b, var44, var70, var59);
      }

      if (this.field_76808_K) {
         for(int var27 = 0; var27 < 50; ++var27) {
            int var45 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
            int var60 = this.field_76813_b.nextInt(this.field_76813_b.nextInt(248) + 8);
            int var71 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
            new WorldGenLiquids(Blocks.field_150358_i).func_76484_a(this.field_76815_a, this.field_76813_b, var45, var60, var71);
         }

         for(int var28 = 0; var28 < 20; ++var28) {
            int var46 = this.field_76814_c + this.field_76813_b.nextInt(16) + 8;
            int var61 = this.field_76813_b.nextInt(this.field_76813_b.nextInt(this.field_76813_b.nextInt(240) + 8) + 8);
            int var72 = this.field_76811_d + this.field_76813_b.nextInt(16) + 8;
            new WorldGenLiquids(Blocks.field_150356_k).func_76484_a(this.field_76815_a, this.field_76813_b, var46, var61, var72);
         }
      }
   }

   protected void func_76795_a(int var1, WorldGenerator var2, int var3, int var4) {
      for(int var5 = 0; var5 < var1; ++var5) {
         int var6 = this.field_76814_c + this.field_76813_b.nextInt(16);
         int var7 = this.field_76813_b.nextInt(var4 - var3) + var3;
         int var8 = this.field_76811_d + this.field_76813_b.nextInt(16);
         var2.func_76484_a(this.field_76815_a, this.field_76813_b, var6, var7, var8);
      }
   }

   protected void func_76793_b(int var1, WorldGenerator var2, int var3, int var4) {
      for(int var5 = 0; var5 < var1; ++var5) {
         int var6 = this.field_76814_c + this.field_76813_b.nextInt(16);
         int var7 = this.field_76813_b.nextInt(var4) + this.field_76813_b.nextInt(var4) + (var3 - var4);
         int var8 = this.field_76811_d + this.field_76813_b.nextInt(16);
         var2.func_76484_a(this.field_76815_a, this.field_76813_b, var6, var7, var8);
      }
   }

   protected void func_76797_b() {
      this.func_76795_a(20, this.field_76823_i, 0, 256);
      this.func_76795_a(10, this.field_76820_j, 0, 256);
      this.func_76795_a(20, this.field_76821_k, 0, 128);
      this.func_76795_a(20, this.field_76818_l, 0, 64);
      this.func_76795_a(2, this.field_76819_m, 0, 32);
      this.func_76795_a(8, this.field_76816_n, 0, 16);
      this.func_76795_a(1, this.field_76817_o, 0, 16);
      this.func_76793_b(1, this.field_76831_p, 16, 16);
   }
}
