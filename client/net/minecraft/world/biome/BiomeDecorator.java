package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.GeneratorBushFeature;
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
   protected BlockPos field_180294_c;
   protected ChunkProviderSettings field_180293_d;
   protected WorldGenerator field_76809_f = new WorldGenClay(4);
   protected WorldGenerator field_76810_g;
   protected WorldGenerator field_76822_h;
   protected WorldGenerator field_76823_i;
   protected WorldGenerator field_76820_j;
   protected WorldGenerator field_180296_j;
   protected WorldGenerator field_180297_k;
   protected WorldGenerator field_180295_l;
   protected WorldGenerator field_76821_k;
   protected WorldGenerator field_76818_l;
   protected WorldGenerator field_76819_m;
   protected WorldGenerator field_180299_p;
   protected WorldGenerator field_180298_q;
   protected WorldGenerator field_76831_p;
   protected WorldGenFlowers field_150514_p;
   protected WorldGenerator field_76828_s;
   protected WorldGenerator field_76827_t;
   protected WorldGenerator field_76826_u;
   protected WorldGenerator field_76825_v;
   protected WorldGenerator field_76824_w;
   protected WorldGenerator field_76834_x;
   protected int field_76833_y;
   protected int field_76832_z;
   protected int field_76802_A;
   protected int field_76803_B;
   protected int field_76804_C;
   protected int field_76798_D;
   protected int field_76799_E;
   protected int field_76800_F;
   protected int field_76801_G;
   protected int field_76805_H;
   protected int field_76806_I;
   protected int field_76807_J;
   public boolean field_76808_K;

   public BiomeDecorator() {
      super();
      this.field_76810_g = new WorldGenSand(Blocks.field_150354_m, 7);
      this.field_76822_h = new WorldGenSand(Blocks.field_150351_n, 6);
      this.field_150514_p = new WorldGenFlowers(Blocks.field_150327_N, BlockFlower.EnumFlowerType.DANDELION);
      this.field_76828_s = new GeneratorBushFeature(Blocks.field_150338_P);
      this.field_76827_t = new GeneratorBushFeature(Blocks.field_150337_Q);
      this.field_76826_u = new WorldGenBigMushroom();
      this.field_76825_v = new WorldGenReed();
      this.field_76824_w = new WorldGenCactus();
      this.field_76834_x = new WorldGenWaterlily();
      this.field_76802_A = 2;
      this.field_76803_B = 1;
      this.field_76801_G = 1;
      this.field_76805_H = 3;
      this.field_76806_I = 1;
      this.field_76808_K = true;
   }

   public void func_180292_a(World var1, Random var2, BiomeGenBase var3, BlockPos var4) {
      if (this.field_76815_a != null) {
         throw new RuntimeException("Already decorating");
      } else {
         this.field_76815_a = var1;
         String var5 = var1.func_72912_H().func_82571_y();
         if (var5 != null) {
            this.field_180293_d = ChunkProviderSettings.Factory.func_177865_a(var5).func_177864_b();
         } else {
            this.field_180293_d = ChunkProviderSettings.Factory.func_177865_a("").func_177864_b();
         }

         this.field_76813_b = var2;
         this.field_180294_c = var4;
         this.field_76823_i = new WorldGenMinable(Blocks.field_150346_d.func_176223_P(), this.field_180293_d.field_177789_I);
         this.field_76820_j = new WorldGenMinable(Blocks.field_150351_n.func_176223_P(), this.field_180293_d.field_177785_M);
         this.field_180296_j = new WorldGenMinable(Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.GRANITE), this.field_180293_d.field_177796_Q);
         this.field_180297_k = new WorldGenMinable(Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.DIORITE), this.field_180293_d.field_177792_U);
         this.field_180295_l = new WorldGenMinable(Blocks.field_150348_b.func_176223_P().func_177226_a(BlockStone.field_176247_a, BlockStone.EnumType.ANDESITE), this.field_180293_d.field_177800_Y);
         this.field_76821_k = new WorldGenMinable(Blocks.field_150365_q.func_176223_P(), this.field_180293_d.field_177844_ac);
         this.field_76818_l = new WorldGenMinable(Blocks.field_150366_p.func_176223_P(), this.field_180293_d.field_177848_ag);
         this.field_76819_m = new WorldGenMinable(Blocks.field_150352_o.func_176223_P(), this.field_180293_d.field_177828_ak);
         this.field_180299_p = new WorldGenMinable(Blocks.field_150450_ax.func_176223_P(), this.field_180293_d.field_177836_ao);
         this.field_180298_q = new WorldGenMinable(Blocks.field_150482_ag.func_176223_P(), this.field_180293_d.field_177814_as);
         this.field_76831_p = new WorldGenMinable(Blocks.field_150369_x.func_176223_P(), this.field_180293_d.field_177822_aw);
         this.func_150513_a(var3);
         this.field_76815_a = null;
         this.field_76813_b = null;
      }
   }

   protected void func_150513_a(BiomeGenBase var1) {
      this.func_76797_b();

      int var2;
      int var3;
      int var4;
      for(var2 = 0; var2 < this.field_76805_H; ++var2) {
         var3 = this.field_76813_b.nextInt(16) + 8;
         var4 = this.field_76813_b.nextInt(16) + 8;
         this.field_76810_g.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_76815_a.func_175672_r(this.field_180294_c.func_177982_a(var3, 0, var4)));
      }

      for(var2 = 0; var2 < this.field_76806_I; ++var2) {
         var3 = this.field_76813_b.nextInt(16) + 8;
         var4 = this.field_76813_b.nextInt(16) + 8;
         this.field_76809_f.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_76815_a.func_175672_r(this.field_180294_c.func_177982_a(var3, 0, var4)));
      }

      for(var2 = 0; var2 < this.field_76801_G; ++var2) {
         var3 = this.field_76813_b.nextInt(16) + 8;
         var4 = this.field_76813_b.nextInt(16) + 8;
         this.field_76822_h.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_76815_a.func_175672_r(this.field_180294_c.func_177982_a(var3, 0, var4)));
      }

      var2 = this.field_76832_z;
      if (this.field_76813_b.nextInt(10) == 0) {
         ++var2;
      }

      int var5;
      BlockPos var7;
      for(var3 = 0; var3 < var2; ++var3) {
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76813_b.nextInt(16) + 8;
         WorldGenAbstractTree var6 = var1.func_150567_a(this.field_76813_b);
         var6.func_175904_e();
         var7 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var4, 0, var5));
         if (var6.func_180709_b(this.field_76815_a, this.field_76813_b, var7)) {
            var6.func_180711_a(this.field_76815_a, this.field_76813_b, var7);
         }
      }

      for(var3 = 0; var3 < this.field_76807_J; ++var3) {
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76813_b.nextInt(16) + 8;
         this.field_76826_u.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var4, 0, var5)));
      }

      BlockPos var8;
      int var11;
      int var12;
      for(var3 = 0; var3 < this.field_76802_A; ++var3) {
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76813_b.nextInt(16) + 8;
         var11 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var4, 0, var5)).func_177956_o() + 32;
         if (var11 > 0) {
            var12 = this.field_76813_b.nextInt(var11);
            var8 = this.field_180294_c.func_177982_a(var4, var12, var5);
            BlockFlower.EnumFlowerType var9 = var1.func_180623_a(this.field_76813_b, var8);
            BlockFlower var10 = var9.func_176964_a().func_180346_a();
            if (var10.func_149688_o() != Material.field_151579_a) {
               this.field_150514_p.func_175914_a(var10, var9);
               this.field_150514_p.func_180709_b(this.field_76815_a, this.field_76813_b, var8);
            }
         }
      }

      for(var3 = 0; var3 < this.field_76803_B; ++var3) {
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76813_b.nextInt(16) + 8;
         var11 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var4, 0, var5)).func_177956_o() * 2;
         if (var11 > 0) {
            var12 = this.field_76813_b.nextInt(var11);
            var1.func_76730_b(this.field_76813_b).func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(var4, var12, var5));
         }
      }

      for(var3 = 0; var3 < this.field_76804_C; ++var3) {
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76813_b.nextInt(16) + 8;
         var11 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var4, 0, var5)).func_177956_o() * 2;
         if (var11 > 0) {
            var12 = this.field_76813_b.nextInt(var11);
            (new WorldGenDeadBush()).func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(var4, var12, var5));
         }
      }

      for(var3 = 0; var3 < this.field_76833_y; ++var3) {
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76813_b.nextInt(16) + 8;
         var11 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var4, 0, var5)).func_177956_o() * 2;
         if (var11 > 0) {
            var12 = this.field_76813_b.nextInt(var11);

            BlockPos var14;
            for(var8 = this.field_180294_c.func_177982_a(var4, var12, var5); var8.func_177956_o() > 0; var8 = var14) {
               var14 = var8.func_177977_b();
               if (!this.field_76815_a.func_175623_d(var14)) {
                  break;
               }
            }

            this.field_76834_x.func_180709_b(this.field_76815_a, this.field_76813_b, var8);
         }
      }

      for(var3 = 0; var3 < this.field_76798_D; ++var3) {
         if (this.field_76813_b.nextInt(4) == 0) {
            var4 = this.field_76813_b.nextInt(16) + 8;
            var5 = this.field_76813_b.nextInt(16) + 8;
            BlockPos var13 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var4, 0, var5));
            this.field_76828_s.func_180709_b(this.field_76815_a, this.field_76813_b, var13);
         }

         if (this.field_76813_b.nextInt(8) == 0) {
            var4 = this.field_76813_b.nextInt(16) + 8;
            var5 = this.field_76813_b.nextInt(16) + 8;
            var11 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var4, 0, var5)).func_177956_o() * 2;
            if (var11 > 0) {
               var12 = this.field_76813_b.nextInt(var11);
               var8 = this.field_180294_c.func_177982_a(var4, var12, var5);
               this.field_76827_t.func_180709_b(this.field_76815_a, this.field_76813_b, var8);
            }
         }
      }

      if (this.field_76813_b.nextInt(4) == 0) {
         var3 = this.field_76813_b.nextInt(16) + 8;
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var3, 0, var4)).func_177956_o() * 2;
         if (var5 > 0) {
            var11 = this.field_76813_b.nextInt(var5);
            this.field_76828_s.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(var3, var11, var4));
         }
      }

      if (this.field_76813_b.nextInt(8) == 0) {
         var3 = this.field_76813_b.nextInt(16) + 8;
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var3, 0, var4)).func_177956_o() * 2;
         if (var5 > 0) {
            var11 = this.field_76813_b.nextInt(var5);
            this.field_76827_t.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(var3, var11, var4));
         }
      }

      for(var3 = 0; var3 < this.field_76799_E; ++var3) {
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76813_b.nextInt(16) + 8;
         var11 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var4, 0, var5)).func_177956_o() * 2;
         if (var11 > 0) {
            var12 = this.field_76813_b.nextInt(var11);
            this.field_76825_v.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(var4, var12, var5));
         }
      }

      for(var3 = 0; var3 < 10; ++var3) {
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76813_b.nextInt(16) + 8;
         var11 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var4, 0, var5)).func_177956_o() * 2;
         if (var11 > 0) {
            var12 = this.field_76813_b.nextInt(var11);
            this.field_76825_v.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(var4, var12, var5));
         }
      }

      if (this.field_76813_b.nextInt(32) == 0) {
         var3 = this.field_76813_b.nextInt(16) + 8;
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var3, 0, var4)).func_177956_o() * 2;
         if (var5 > 0) {
            var11 = this.field_76813_b.nextInt(var5);
            (new WorldGenPumpkin()).func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(var3, var11, var4));
         }
      }

      for(var3 = 0; var3 < this.field_76800_F; ++var3) {
         var4 = this.field_76813_b.nextInt(16) + 8;
         var5 = this.field_76813_b.nextInt(16) + 8;
         var11 = this.field_76815_a.func_175645_m(this.field_180294_c.func_177982_a(var4, 0, var5)).func_177956_o() * 2;
         if (var11 > 0) {
            var12 = this.field_76813_b.nextInt(var11);
            this.field_76824_w.func_180709_b(this.field_76815_a, this.field_76813_b, this.field_180294_c.func_177982_a(var4, var12, var5));
         }
      }

      if (this.field_76808_K) {
         for(var3 = 0; var3 < 50; ++var3) {
            var4 = this.field_76813_b.nextInt(16) + 8;
            var5 = this.field_76813_b.nextInt(16) + 8;
            var11 = this.field_76813_b.nextInt(248) + 8;
            if (var11 > 0) {
               var12 = this.field_76813_b.nextInt(var11);
               var8 = this.field_180294_c.func_177982_a(var4, var12, var5);
               (new WorldGenLiquids(Blocks.field_150358_i)).func_180709_b(this.field_76815_a, this.field_76813_b, var8);
            }
         }

         for(var3 = 0; var3 < 20; ++var3) {
            var4 = this.field_76813_b.nextInt(16) + 8;
            var5 = this.field_76813_b.nextInt(16) + 8;
            var11 = this.field_76813_b.nextInt(this.field_76813_b.nextInt(this.field_76813_b.nextInt(240) + 8) + 8);
            var7 = this.field_180294_c.func_177982_a(var4, var11, var5);
            (new WorldGenLiquids(Blocks.field_150356_k)).func_180709_b(this.field_76815_a, this.field_76813_b, var7);
         }
      }

   }

   protected void func_76795_a(int var1, WorldGenerator var2, int var3, int var4) {
      int var5;
      if (var4 < var3) {
         var5 = var3;
         var3 = var4;
         var4 = var5;
      } else if (var4 == var3) {
         if (var3 < 255) {
            ++var4;
         } else {
            --var3;
         }
      }

      for(var5 = 0; var5 < var1; ++var5) {
         BlockPos var6 = this.field_180294_c.func_177982_a(this.field_76813_b.nextInt(16), this.field_76813_b.nextInt(var4 - var3) + var3, this.field_76813_b.nextInt(16));
         var2.func_180709_b(this.field_76815_a, this.field_76813_b, var6);
      }

   }

   protected void func_76793_b(int var1, WorldGenerator var2, int var3, int var4) {
      for(int var5 = 0; var5 < var1; ++var5) {
         BlockPos var6 = this.field_180294_c.func_177982_a(this.field_76813_b.nextInt(16), this.field_76813_b.nextInt(var4) + this.field_76813_b.nextInt(var4) + var3 - var4, this.field_76813_b.nextInt(16));
         var2.func_180709_b(this.field_76815_a, this.field_76813_b, var6);
      }

   }

   protected void func_76797_b() {
      this.func_76795_a(this.field_180293_d.field_177790_J, this.field_76823_i, this.field_180293_d.field_177791_K, this.field_180293_d.field_177784_L);
      this.func_76795_a(this.field_180293_d.field_177786_N, this.field_76820_j, this.field_180293_d.field_177787_O, this.field_180293_d.field_177797_P);
      this.func_76795_a(this.field_180293_d.field_177795_V, this.field_180297_k, this.field_180293_d.field_177794_W, this.field_180293_d.field_177801_X);
      this.func_76795_a(this.field_180293_d.field_177799_R, this.field_180296_j, this.field_180293_d.field_177798_S, this.field_180293_d.field_177793_T);
      this.func_76795_a(this.field_180293_d.field_177802_Z, this.field_180295_l, this.field_180293_d.field_177846_aa, this.field_180293_d.field_177847_ab);
      this.func_76795_a(this.field_180293_d.field_177845_ad, this.field_76821_k, this.field_180293_d.field_177851_ae, this.field_180293_d.field_177853_af);
      this.func_76795_a(this.field_180293_d.field_177849_ah, this.field_76818_l, this.field_180293_d.field_177832_ai, this.field_180293_d.field_177834_aj);
      this.func_76795_a(this.field_180293_d.field_177830_al, this.field_76819_m, this.field_180293_d.field_177840_am, this.field_180293_d.field_177842_an);
      this.func_76795_a(this.field_180293_d.field_177838_ap, this.field_180299_p, this.field_180293_d.field_177818_aq, this.field_180293_d.field_177816_ar);
      this.func_76795_a(this.field_180293_d.field_177812_at, this.field_180298_q, this.field_180293_d.field_177826_au, this.field_180293_d.field_177824_av);
      this.func_76793_b(this.field_180293_d.field_177820_ax, this.field_76831_p, this.field_180293_d.field_177807_ay, this.field_180293_d.field_177805_az);
   }
}
