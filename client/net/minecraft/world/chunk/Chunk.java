package net.minecraft.world.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
   private static final Logger field_150817_t = LogManager.getLogger();
   public static boolean field_76640_a;
   private ExtendedBlockStorage[] field_76652_q = new ExtendedBlockStorage[16];
   private byte[] field_76651_r = new byte[256];
   public int[] field_76638_b = new int[256];
   public boolean[] field_76639_c = new boolean[256];
   public boolean field_76636_d;
   public World field_76637_e;
   public int[] field_76634_f;
   public final int field_76635_g;
   public final int field_76647_h;
   private boolean field_76650_s;
   public Map field_150816_i = new HashMap();
   public List[] field_76645_j;
   public boolean field_76646_k;
   public boolean field_150814_l;
   public boolean field_150815_m;
   public boolean field_76643_l;
   public boolean field_76644_m;
   public long field_76641_n;
   public boolean field_76642_o;
   public int field_82912_p;
   public long field_111204_q;
   private int field_76649_t = 4096;

   public Chunk(World var1, int var2, int var3) {
      super();
      this.field_76645_j = new List[16];
      this.field_76637_e = var1;
      this.field_76635_g = var2;
      this.field_76647_h = var3;
      this.field_76634_f = new int[256];

      for(int var4 = 0; var4 < this.field_76645_j.length; ++var4) {
         this.field_76645_j[var4] = new ArrayList();
      }

      Arrays.fill(this.field_76638_b, -999);
      Arrays.fill(this.field_76651_r, (byte)-1);
   }

   public Chunk(World var1, Block[] var2, int var3, int var4) {
      this(var1, var3, var4);
      int var5 = var2.length / 256;
      boolean var6 = !var1.field_73011_w.field_76576_e;

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            for(int var9 = 0; var9 < var5; ++var9) {
               Block var10 = var2[var7 << 11 | var8 << 7 | var9];
               if (var10 != null && var10.func_149688_o() != Material.field_151579_a) {
                  int var11 = var9 >> 4;
                  if (this.field_76652_q[var11] == null) {
                     this.field_76652_q[var11] = new ExtendedBlockStorage(var11 << 4, var6);
                  }

                  this.field_76652_q[var11].func_150818_a(var7, var9 & 15, var8, var10);
               }
            }
         }
      }
   }

   public Chunk(World var1, Block[] var2, byte[] var3, int var4, int var5) {
      this(var1, var4, var5);
      int var6 = var2.length / 256;
      boolean var7 = !var1.field_73011_w.field_76576_e;

      for(int var8 = 0; var8 < 16; ++var8) {
         for(int var9 = 0; var9 < 16; ++var9) {
            for(int var10 = 0; var10 < var6; ++var10) {
               int var11 = var8 * var6 * 16 | var9 * var6 | var10;
               Block var12 = var2[var11];
               if (var12 != null && var12 != Blocks.field_150350_a) {
                  int var13 = var10 >> 4;
                  if (this.field_76652_q[var13] == null) {
                     this.field_76652_q[var13] = new ExtendedBlockStorage(var13 << 4, var7);
                  }

                  this.field_76652_q[var13].func_150818_a(var8, var10 & 15, var9, var12);
                  this.field_76652_q[var13].func_76654_b(var8, var10 & 15, var9, var3[var11]);
               }
            }
         }
      }
   }

   public boolean func_76600_a(int var1, int var2) {
      return var1 == this.field_76635_g && var2 == this.field_76647_h;
   }

   public int func_76611_b(int var1, int var2) {
      return this.field_76634_f[var2 << 4 | var1];
   }

   public int func_76625_h() {
      for(int var1 = this.field_76652_q.length - 1; var1 >= 0; --var1) {
         if (this.field_76652_q[var1] != null) {
            return this.field_76652_q[var1].func_76662_d();
         }
      }

      return 0;
   }

   public ExtendedBlockStorage[] func_76587_i() {
      return this.field_76652_q;
   }

   public void func_76590_a() {
      int var1 = this.func_76625_h();
      this.field_82912_p = 2147483647;

      for(int var2 = 0; var2 < 16; ++var2) {
         for(int var3 = 0; var3 < 16; ++var3) {
            this.field_76638_b[var2 + (var3 << 4)] = -999;

            for(int var4 = var1 + 16 - 1; var4 > 0; --var4) {
               Block var5 = this.func_150810_a(var2, var4 - 1, var3);
               if (var5.func_149717_k() != 0) {
                  this.field_76634_f[var3 << 4 | var2] = var4;
                  if (var4 < this.field_82912_p) {
                     this.field_82912_p = var4;
                  }
                  break;
               }
            }
         }
      }

      this.field_76643_l = true;
   }

   public void func_76603_b() {
      int var1 = this.func_76625_h();
      this.field_82912_p = 2147483647;

      for(int var2 = 0; var2 < 16; ++var2) {
         for(int var3 = 0; var3 < 16; ++var3) {
            this.field_76638_b[var2 + (var3 << 4)] = -999;

            for(int var4 = var1 + 16 - 1; var4 > 0; --var4) {
               if (this.func_150808_b(var2, var4 - 1, var3) != 0) {
                  this.field_76634_f[var3 << 4 | var2] = var4;
                  if (var4 < this.field_82912_p) {
                     this.field_82912_p = var4;
                  }
                  break;
               }
            }

            if (!this.field_76637_e.field_73011_w.field_76576_e) {
               int var8 = 15;
               int var5 = var1 + 16 - 1;

               while(true) {
                  int var6 = this.func_150808_b(var2, var5, var3);
                  if (var6 == 0 && var8 != 15) {
                     var6 = 1;
                  }

                  var8 -= var6;
                  if (var8 > 0) {
                     ExtendedBlockStorage var7 = this.field_76652_q[var5 >> 4];
                     if (var7 != null) {
                        var7.func_76657_c(var2, var5 & 15, var3, var8);
                        this.field_76637_e.func_147479_m((this.field_76635_g << 4) + var2, var5, (this.field_76647_h << 4) + var3);
                     }
                  }

                  if (--var5 <= 0 || var8 <= 0) {
                     break;
                  }
               }
            }
         }
      }

      this.field_76643_l = true;
   }

   private void func_76595_e(int var1, int var2) {
      this.field_76639_c[var1 + var2 * 16] = true;
      this.field_76650_s = true;
   }

   private void func_150803_c(boolean var1) {
      this.field_76637_e.field_72984_F.func_76320_a("recheckGaps");
      if (this.field_76637_e.func_72873_a(this.field_76635_g * 16 + 8, 0, this.field_76647_h * 16 + 8, 16)) {
         for(int var2 = 0; var2 < 16; ++var2) {
            for(int var3 = 0; var3 < 16; ++var3) {
               if (this.field_76639_c[var2 + var3 * 16]) {
                  this.field_76639_c[var2 + var3 * 16] = false;
                  int var4 = this.func_76611_b(var2, var3);
                  int var5 = this.field_76635_g * 16 + var2;
                  int var6 = this.field_76647_h * 16 + var3;
                  int var7 = this.field_76637_e.func_82734_g(var5 - 1, var6);
                  int var8 = this.field_76637_e.func_82734_g(var5 + 1, var6);
                  int var9 = this.field_76637_e.func_82734_g(var5, var6 - 1);
                  int var10 = this.field_76637_e.func_82734_g(var5, var6 + 1);
                  if (var8 < var7) {
                     var7 = var8;
                  }

                  if (var9 < var7) {
                     var7 = var9;
                  }

                  if (var10 < var7) {
                     var7 = var10;
                  }

                  this.func_76599_g(var5, var6, var7);
                  this.func_76599_g(var5 - 1, var6, var4);
                  this.func_76599_g(var5 + 1, var6, var4);
                  this.func_76599_g(var5, var6 - 1, var4);
                  this.func_76599_g(var5, var6 + 1, var4);
                  if (var1) {
                     this.field_76637_e.field_72984_F.func_76319_b();
                     return;
                  }
               }
            }
         }

         this.field_76650_s = false;
      }

      this.field_76637_e.field_72984_F.func_76319_b();
   }

   private void func_76599_g(int var1, int var2, int var3) {
      int var4 = this.field_76637_e.func_72976_f(var1, var2);
      if (var4 > var3) {
         this.func_76609_d(var1, var2, var3, var4 + 1);
      } else if (var4 < var3) {
         this.func_76609_d(var1, var2, var4, var3 + 1);
      }
   }

   private void func_76609_d(int var1, int var2, int var3, int var4) {
      if (var4 > var3 && this.field_76637_e.func_72873_a(var1, 0, var2, 16)) {
         for(int var5 = var3; var5 < var4; ++var5) {
            this.field_76637_e.func_147463_c(EnumSkyBlock.Sky, var1, var5, var2);
         }

         this.field_76643_l = true;
      }
   }

   private void func_76615_h(int var1, int var2, int var3) {
      int var4 = this.field_76634_f[var3 << 4 | var1] & 0xFF;
      int var5 = var4;
      if (var2 > var4) {
         var5 = var2;
      }

      while(var5 > 0 && this.func_150808_b(var1, var5 - 1, var3) == 0) {
         --var5;
      }

      if (var5 != var4) {
         this.field_76637_e.func_72975_g(var1 + this.field_76635_g * 16, var3 + this.field_76647_h * 16, var5, var4);
         this.field_76634_f[var3 << 4 | var1] = var5;
         int var6 = this.field_76635_g * 16 + var1;
         int var7 = this.field_76647_h * 16 + var3;
         if (!this.field_76637_e.field_73011_w.field_76576_e) {
            if (var5 < var4) {
               for(int var12 = var5; var12 < var4; ++var12) {
                  ExtendedBlockStorage var15 = this.field_76652_q[var12 >> 4];
                  if (var15 != null) {
                     var15.func_76657_c(var1, var12 & 15, var3, 15);
                     this.field_76637_e.func_147479_m((this.field_76635_g << 4) + var1, var12, (this.field_76647_h << 4) + var3);
                  }
               }
            } else {
               for(int var8 = var4; var8 < var5; ++var8) {
                  ExtendedBlockStorage var9 = this.field_76652_q[var8 >> 4];
                  if (var9 != null) {
                     var9.func_76657_c(var1, var8 & 15, var3, 0);
                     this.field_76637_e.func_147479_m((this.field_76635_g << 4) + var1, var8, (this.field_76647_h << 4) + var3);
                  }
               }
            }

            int var13 = 15;

            while(var5 > 0 && var13 > 0) {
               int var16 = this.func_150808_b(var1, --var5, var3);
               if (var16 == 0) {
                  var16 = 1;
               }

               var13 -= var16;
               if (var13 < 0) {
                  var13 = 0;
               }

               ExtendedBlockStorage var10 = this.field_76652_q[var5 >> 4];
               if (var10 != null) {
                  var10.func_76657_c(var1, var5 & 15, var3, var13);
               }
            }
         }

         int var14 = this.field_76634_f[var3 << 4 | var1];
         int var17 = var4;
         int var18 = var14;
         if (var14 < var4) {
            var17 = var14;
            var18 = var4;
         }

         if (var14 < this.field_82912_p) {
            this.field_82912_p = var14;
         }

         if (!this.field_76637_e.field_73011_w.field_76576_e) {
            this.func_76609_d(var6 - 1, var7, var17, var18);
            this.func_76609_d(var6 + 1, var7, var17, var18);
            this.func_76609_d(var6, var7 - 1, var17, var18);
            this.func_76609_d(var6, var7 + 1, var17, var18);
            this.func_76609_d(var6, var7, var17, var18);
         }

         this.field_76643_l = true;
      }
   }

   public int func_150808_b(int var1, int var2, int var3) {
      return this.func_150810_a(var1, var2, var3).func_149717_k();
   }

   public Block func_150810_a(int var1, int var2, int var3) {
      Block var4 = Blocks.field_150350_a;
      if (var2 >> 4 < this.field_76652_q.length) {
         ExtendedBlockStorage var5 = this.field_76652_q[var2 >> 4];
         if (var5 != null) {
            try {
               var4 = var5.func_150819_a(var1, var2 & 15, var3);
            } catch (Throwable var9) {
               CrashReport var7 = CrashReport.func_85055_a(var9, "Getting block");
               CrashReportCategory var8 = var7.func_85058_a("Block being got");
               var8.func_71500_a("Location", new Chunk$1(this, var1, var2, var3));
               throw new ReportedException(var7);
            }
         }
      }

      return var4;
   }

   public int func_76628_c(int var1, int var2, int var3) {
      if (var2 >> 4 >= this.field_76652_q.length) {
         return 0;
      } else {
         ExtendedBlockStorage var4 = this.field_76652_q[var2 >> 4];
         return var4 != null ? var4.func_76665_b(var1, var2 & 15, var3) : 0;
      }
   }

   public boolean func_150807_a(int var1, int var2, int var3, Block var4, int var5) {
      int var6 = var3 << 4 | var1;
      if (var2 >= this.field_76638_b[var6] - 1) {
         this.field_76638_b[var6] = -999;
      }

      int var7 = this.field_76634_f[var6];
      Block var8 = this.func_150810_a(var1, var2, var3);
      int var9 = this.func_76628_c(var1, var2, var3);
      if (var8 == var4 && var9 == var5) {
         return false;
      } else {
         ExtendedBlockStorage var10 = this.field_76652_q[var2 >> 4];
         boolean var11 = false;
         if (var10 == null) {
            if (var4 == Blocks.field_150350_a) {
               return false;
            }

            var10 = this.field_76652_q[var2 >> 4] = new ExtendedBlockStorage(var2 >> 4 << 4, !this.field_76637_e.field_73011_w.field_76576_e);
            var11 = var2 >= var7;
         }

         int var12 = this.field_76635_g * 16 + var1;
         int var13 = this.field_76647_h * 16 + var3;
         if (!this.field_76637_e.field_72995_K) {
            var8.func_149725_f(this.field_76637_e, var12, var2, var13, var9);
         }

         var10.func_150818_a(var1, var2 & 15, var3, var4);
         if (!this.field_76637_e.field_72995_K) {
            var8.func_149749_a(this.field_76637_e, var12, var2, var13, var8, var9);
         } else if (var8 instanceof ITileEntityProvider && var8 != var4) {
            this.field_76637_e.func_147475_p(var12, var2, var13);
         }

         if (var10.func_150819_a(var1, var2 & 15, var3) != var4) {
            return false;
         } else {
            var10.func_76654_b(var1, var2 & 15, var3, var5);
            if (var11) {
               this.func_76603_b();
            } else {
               int var14 = var4.func_149717_k();
               int var15 = var8.func_149717_k();
               if (var14 > 0) {
                  if (var2 >= var7) {
                     this.func_76615_h(var1, var2 + 1, var3);
                  }
               } else if (var2 == var7 - 1) {
                  this.func_76615_h(var1, var2, var3);
               }

               if (var14 != var15
                  && (
                     var14 < var15 || this.func_76614_a(EnumSkyBlock.Sky, var1, var2, var3) > 0 || this.func_76614_a(EnumSkyBlock.Block, var1, var2, var3) > 0
                  )) {
                  this.func_76595_e(var1, var3);
               }
            }

            if (var8 instanceof ITileEntityProvider) {
               TileEntity var16 = this.func_150806_e(var1, var2, var3);
               if (var16 != null) {
                  var16.func_145836_u();
               }
            }

            if (!this.field_76637_e.field_72995_K) {
               var4.func_149726_b(this.field_76637_e, var12, var2, var13);
            }

            if (var4 instanceof ITileEntityProvider) {
               TileEntity var17 = this.func_150806_e(var1, var2, var3);
               if (var17 == null) {
                  var17 = ((ITileEntityProvider)var4).func_149915_a(this.field_76637_e, var5);
                  this.field_76637_e.func_147455_a(var12, var2, var13, var17);
               }

               if (var17 != null) {
                  var17.func_145836_u();
               }
            }

            this.field_76643_l = true;
            return true;
         }
      }
   }

   public boolean func_76589_b(int var1, int var2, int var3, int var4) {
      ExtendedBlockStorage var5 = this.field_76652_q[var2 >> 4];
      if (var5 == null) {
         return false;
      } else {
         int var6 = var5.func_76665_b(var1, var2 & 15, var3);
         if (var6 == var4) {
            return false;
         } else {
            this.field_76643_l = true;
            var5.func_76654_b(var1, var2 & 15, var3, var4);
            if (var5.func_150819_a(var1, var2 & 15, var3) instanceof ITileEntityProvider) {
               TileEntity var7 = this.func_150806_e(var1, var2, var3);
               if (var7 != null) {
                  var7.func_145836_u();
                  var7.field_145847_g = var4;
               }
            }

            return true;
         }
      }
   }

   public int func_76614_a(EnumSkyBlock var1, int var2, int var3, int var4) {
      ExtendedBlockStorage var5 = this.field_76652_q[var3 >> 4];
      if (var5 == null) {
         return this.func_76619_d(var2, var3, var4) ? var1.field_77198_c : 0;
      } else if (var1 == EnumSkyBlock.Sky) {
         return this.field_76637_e.field_73011_w.field_76576_e ? 0 : var5.func_76670_c(var2, var3 & 15, var4);
      } else {
         return var1 == EnumSkyBlock.Block ? var5.func_76674_d(var2, var3 & 15, var4) : var1.field_77198_c;
      }
   }

   public void func_76633_a(EnumSkyBlock var1, int var2, int var3, int var4, int var5) {
      ExtendedBlockStorage var6 = this.field_76652_q[var3 >> 4];
      if (var6 == null) {
         var6 = this.field_76652_q[var3 >> 4] = new ExtendedBlockStorage(var3 >> 4 << 4, !this.field_76637_e.field_73011_w.field_76576_e);
         this.func_76603_b();
      }

      this.field_76643_l = true;
      if (var1 == EnumSkyBlock.Sky) {
         if (!this.field_76637_e.field_73011_w.field_76576_e) {
            var6.func_76657_c(var2, var3 & 15, var4, var5);
         }
      } else if (var1 == EnumSkyBlock.Block) {
         var6.func_76677_d(var2, var3 & 15, var4, var5);
      }
   }

   public int func_76629_c(int var1, int var2, int var3, int var4) {
      ExtendedBlockStorage var5 = this.field_76652_q[var2 >> 4];
      if (var5 == null) {
         return !this.field_76637_e.field_73011_w.field_76576_e && var4 < EnumSkyBlock.Sky.field_77198_c ? EnumSkyBlock.Sky.field_77198_c - var4 : 0;
      } else {
         int var6 = this.field_76637_e.field_73011_w.field_76576_e ? 0 : var5.func_76670_c(var1, var2 & 15, var3);
         if (var6 > 0) {
            field_76640_a = true;
         }

         var6 -= var4;
         int var7 = var5.func_76674_d(var1, var2 & 15, var3);
         if (var7 > var6) {
            var6 = var7;
         }

         return var6;
      }
   }

   public void func_76612_a(Entity var1) {
      this.field_76644_m = true;
      int var2 = MathHelper.func_76128_c(var1.field_70165_t / 16.0);
      int var3 = MathHelper.func_76128_c(var1.field_70161_v / 16.0);
      if (var2 != this.field_76635_g || var3 != this.field_76647_h) {
         field_150817_t.warn("Wrong location! " + var1 + " (at " + var2 + ", " + var3 + " instead of " + this.field_76635_g + ", " + this.field_76647_h + ")");
         Thread.dumpStack();
      }

      int var4 = MathHelper.func_76128_c(var1.field_70163_u / 16.0);
      if (var4 < 0) {
         var4 = 0;
      }

      if (var4 >= this.field_76645_j.length) {
         var4 = this.field_76645_j.length - 1;
      }

      var1.field_70175_ag = true;
      var1.field_70176_ah = this.field_76635_g;
      var1.field_70162_ai = var4;
      var1.field_70164_aj = this.field_76647_h;
      this.field_76645_j[var4].add(var1);
   }

   public void func_76622_b(Entity var1) {
      this.func_76608_a(var1, var1.field_70162_ai);
   }

   public void func_76608_a(Entity var1, int var2) {
      if (var2 < 0) {
         var2 = 0;
      }

      if (var2 >= this.field_76645_j.length) {
         var2 = this.field_76645_j.length - 1;
      }

      this.field_76645_j[var2].remove(var1);
   }

   public boolean func_76619_d(int var1, int var2, int var3) {
      return var2 >= this.field_76634_f[var3 << 4 | var1];
   }

   public TileEntity func_150806_e(int var1, int var2, int var3) {
      ChunkPosition var4 = new ChunkPosition(var1, var2, var3);
      TileEntity var5 = (TileEntity)this.field_150816_i.get(var4);
      if (var5 == null) {
         Block var6 = this.func_150810_a(var1, var2, var3);
         if (!var6.func_149716_u()) {
            return null;
         }

         var5 = ((ITileEntityProvider)var6).func_149915_a(this.field_76637_e, this.func_76628_c(var1, var2, var3));
         this.field_76637_e.func_147455_a(this.field_76635_g * 16 + var1, var2, this.field_76647_h * 16 + var3, var5);
      }

      if (var5 != null && var5.func_145837_r()) {
         this.field_150816_i.remove(var4);
         return null;
      } else {
         return var5;
      }
   }

   public void func_150813_a(TileEntity var1) {
      int var2 = var1.field_145851_c - this.field_76635_g * 16;
      int var3 = var1.field_145848_d;
      int var4 = var1.field_145849_e - this.field_76647_h * 16;
      this.func_150812_a(var2, var3, var4, var1);
      if (this.field_76636_d) {
         this.field_76637_e.field_147482_g.add(var1);
      }
   }

   public void func_150812_a(int var1, int var2, int var3, TileEntity var4) {
      ChunkPosition var5 = new ChunkPosition(var1, var2, var3);
      var4.func_145834_a(this.field_76637_e);
      var4.field_145851_c = this.field_76635_g * 16 + var1;
      var4.field_145848_d = var2;
      var4.field_145849_e = this.field_76647_h * 16 + var3;
      if (this.func_150810_a(var1, var2, var3) instanceof ITileEntityProvider) {
         if (this.field_150816_i.containsKey(var5)) {
            ((TileEntity)this.field_150816_i.get(var5)).func_145843_s();
         }

         var4.func_145829_t();
         this.field_150816_i.put(var5, var4);
      }
   }

   public void func_150805_f(int var1, int var2, int var3) {
      ChunkPosition var4 = new ChunkPosition(var1, var2, var3);
      if (this.field_76636_d) {
         TileEntity var5 = (TileEntity)this.field_150816_i.remove(var4);
         if (var5 != null) {
            var5.func_145843_s();
         }
      }
   }

   public void func_76631_c() {
      this.field_76636_d = true;
      this.field_76637_e.func_147448_a(this.field_150816_i.values());

      for(int var1 = 0; var1 < this.field_76645_j.length; ++var1) {
         for(Entity var3 : this.field_76645_j[var1]) {
            var3.func_110123_P();
         }

         this.field_76637_e.func_72868_a(this.field_76645_j[var1]);
      }
   }

   public void func_76623_d() {
      this.field_76636_d = false;

      for(TileEntity var2 : this.field_150816_i.values()) {
         this.field_76637_e.func_147457_a(var2);
      }

      for(int var3 = 0; var3 < this.field_76645_j.length; ++var3) {
         this.field_76637_e.func_72828_b(this.field_76645_j[var3]);
      }
   }

   public void func_76630_e() {
      this.field_76643_l = true;
   }

   public void func_76588_a(Entity var1, AxisAlignedBB var2, List var3, IEntitySelector var4) {
      int var5 = MathHelper.func_76128_c((var2.field_72338_b - 2.0) / 16.0);
      int var6 = MathHelper.func_76128_c((var2.field_72337_e + 2.0) / 16.0);
      var5 = MathHelper.func_76125_a(var5, 0, this.field_76645_j.length - 1);
      var6 = MathHelper.func_76125_a(var6, 0, this.field_76645_j.length - 1);

      for(int var7 = var5; var7 <= var6; ++var7) {
         List var8 = this.field_76645_j[var7];

         for(int var9 = 0; var9 < var8.size(); ++var9) {
            Entity var10 = (Entity)var8.get(var9);
            if (var10 != var1 && var10.field_70121_D.func_72326_a(var2) && (var4 == null || var4.func_82704_a(var10))) {
               var3.add(var10);
               Entity[] var11 = var10.func_70021_al();
               if (var11 != null) {
                  for(int var12 = 0; var12 < var11.length; ++var12) {
                     var10 = var11[var12];
                     if (var10 != var1 && var10.field_70121_D.func_72326_a(var2) && (var4 == null || var4.func_82704_a(var10))) {
                        var3.add(var10);
                     }
                  }
               }
            }
         }
      }
   }

   public void func_76618_a(Class var1, AxisAlignedBB var2, List var3, IEntitySelector var4) {
      int var5 = MathHelper.func_76128_c((var2.field_72338_b - 2.0) / 16.0);
      int var6 = MathHelper.func_76128_c((var2.field_72337_e + 2.0) / 16.0);
      var5 = MathHelper.func_76125_a(var5, 0, this.field_76645_j.length - 1);
      var6 = MathHelper.func_76125_a(var6, 0, this.field_76645_j.length - 1);

      for(int var7 = var5; var7 <= var6; ++var7) {
         List var8 = this.field_76645_j[var7];

         for(int var9 = 0; var9 < var8.size(); ++var9) {
            Entity var10 = (Entity)var8.get(var9);
            if (var1.isAssignableFrom(var10.getClass()) && var10.field_70121_D.func_72326_a(var2) && (var4 == null || var4.func_82704_a(var10))) {
               var3.add(var10);
            }
         }
      }
   }

   public boolean func_76601_a(boolean var1) {
      if (var1) {
         if (this.field_76644_m && this.field_76637_e.func_82737_E() != this.field_76641_n || this.field_76643_l) {
            return true;
         }
      } else if (this.field_76644_m && this.field_76637_e.func_82737_E() >= this.field_76641_n + 600L) {
         return true;
      }

      return this.field_76643_l;
   }

   public Random func_76617_a(long var1) {
      return new Random(
         this.field_76637_e.func_72905_C()
               + (long)(this.field_76635_g * this.field_76635_g * 4987142)
               + (long)(this.field_76635_g * 5947611)
               + (long)(this.field_76647_h * this.field_76647_h) * 4392871L
               + (long)(this.field_76647_h * 389711)
            ^ var1
      );
   }

   public boolean func_76621_g() {
      return false;
   }

   public void func_76624_a(IChunkProvider var1, IChunkProvider var2, int var3, int var4) {
      if (!this.field_76646_k && var1.func_73149_a(var3 + 1, var4 + 1) && var1.func_73149_a(var3, var4 + 1) && var1.func_73149_a(var3 + 1, var4)) {
         var1.func_73153_a(var2, var3, var4);
      }

      if (var1.func_73149_a(var3 - 1, var4)
         && !var1.func_73154_d(var3 - 1, var4).field_76646_k
         && var1.func_73149_a(var3 - 1, var4 + 1)
         && var1.func_73149_a(var3, var4 + 1)
         && var1.func_73149_a(var3 - 1, var4 + 1)) {
         var1.func_73153_a(var2, var3 - 1, var4);
      }

      if (var1.func_73149_a(var3, var4 - 1)
         && !var1.func_73154_d(var3, var4 - 1).field_76646_k
         && var1.func_73149_a(var3 + 1, var4 - 1)
         && var1.func_73149_a(var3 + 1, var4 - 1)
         && var1.func_73149_a(var3 + 1, var4)) {
         var1.func_73153_a(var2, var3, var4 - 1);
      }

      if (var1.func_73149_a(var3 - 1, var4 - 1)
         && !var1.func_73154_d(var3 - 1, var4 - 1).field_76646_k
         && var1.func_73149_a(var3, var4 - 1)
         && var1.func_73149_a(var3 - 1, var4)) {
         var1.func_73153_a(var2, var3 - 1, var4 - 1);
      }
   }

   public int func_76626_d(int var1, int var2) {
      int var3 = var1 | var2 << 4;
      int var4 = this.field_76638_b[var3];
      if (var4 == -999) {
         int var5 = this.func_76625_h() + 15;
         var4 = -1;

         while(var5 > 0 && var4 == -1) {
            Block var6 = this.func_150810_a(var1, var5, var2);
            Material var7 = var6.func_149688_o();
            if (!var7.func_76230_c() && !var7.func_76224_d()) {
               --var5;
            } else {
               var4 = var5 + 1;
            }
         }

         this.field_76638_b[var3] = var4;
      }

      return var4;
   }

   public void func_150804_b(boolean var1) {
      if (this.field_76650_s && !this.field_76637_e.field_73011_w.field_76576_e && !var1) {
         this.func_150803_c(this.field_76637_e.field_72995_K);
      }

      this.field_150815_m = true;
      if (!this.field_150814_l && this.field_76646_k) {
         this.func_150809_p();
      }
   }

   public boolean func_150802_k() {
      return this.field_150815_m && this.field_76646_k && this.field_150814_l;
   }

   public ChunkCoordIntPair func_76632_l() {
      return new ChunkCoordIntPair(this.field_76635_g, this.field_76647_h);
   }

   public boolean func_76606_c(int var1, int var2) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var2 >= 256) {
         var2 = 255;
      }

      for(int var3 = var1; var3 <= var2; var3 += 16) {
         ExtendedBlockStorage var4 = this.field_76652_q[var3 >> 4];
         if (var4 != null && !var4.func_76663_a()) {
            return false;
         }
      }

      return true;
   }

   public void func_76602_a(ExtendedBlockStorage[] var1) {
      this.field_76652_q = var1;
   }

   public void func_76607_a(byte[] var1, int var2, int var3, boolean var4) {
      int var5 = 0;
      boolean var6 = !this.field_76637_e.field_73011_w.field_76576_e;

      for(int var7 = 0; var7 < this.field_76652_q.length; ++var7) {
         if ((var2 & 1 << var7) != 0) {
            if (this.field_76652_q[var7] == null) {
               this.field_76652_q[var7] = new ExtendedBlockStorage(var7 << 4, var6);
            }

            byte[] var8 = this.field_76652_q[var7].func_76658_g();
            System.arraycopy(var1, var5, var8, 0, var8.length);
            var5 += var8.length;
         } else if (var4 && this.field_76652_q[var7] != null) {
            this.field_76652_q[var7] = null;
         }
      }

      for(int var10 = 0; var10 < this.field_76652_q.length; ++var10) {
         if ((var2 & 1 << var10) != 0 && this.field_76652_q[var10] != null) {
            NibbleArray var16 = this.field_76652_q[var10].func_76669_j();
            System.arraycopy(var1, var5, var16.field_76585_a, 0, var16.field_76585_a.length);
            var5 += var16.field_76585_a.length;
         }
      }

      for(int var11 = 0; var11 < this.field_76652_q.length; ++var11) {
         if ((var2 & 1 << var11) != 0 && this.field_76652_q[var11] != null) {
            NibbleArray var17 = this.field_76652_q[var11].func_76661_k();
            System.arraycopy(var1, var5, var17.field_76585_a, 0, var17.field_76585_a.length);
            var5 += var17.field_76585_a.length;
         }
      }

      if (var6) {
         for(int var12 = 0; var12 < this.field_76652_q.length; ++var12) {
            if ((var2 & 1 << var12) != 0 && this.field_76652_q[var12] != null) {
               NibbleArray var18 = this.field_76652_q[var12].func_76671_l();
               System.arraycopy(var1, var5, var18.field_76585_a, 0, var18.field_76585_a.length);
               var5 += var18.field_76585_a.length;
            }
         }
      }

      for(int var13 = 0; var13 < this.field_76652_q.length; ++var13) {
         if ((var3 & 1 << var13) != 0) {
            if (this.field_76652_q[var13] == null) {
               var5 += 2048;
            } else {
               NibbleArray var19 = this.field_76652_q[var13].func_76660_i();
               if (var19 == null) {
                  var19 = this.field_76652_q[var13].func_76667_m();
               }

               System.arraycopy(var1, var5, var19.field_76585_a, 0, var19.field_76585_a.length);
               var5 += var19.field_76585_a.length;
            }
         } else if (var4 && this.field_76652_q[var13] != null && this.field_76652_q[var13].func_76660_i() != null) {
            this.field_76652_q[var13].func_76676_h();
         }
      }

      if (var4) {
         System.arraycopy(var1, var5, this.field_76651_r, 0, this.field_76651_r.length);
         var5 += this.field_76651_r.length;
      }

      for(int var14 = 0; var14 < this.field_76652_q.length; ++var14) {
         if (this.field_76652_q[var14] != null && (var2 & 1 << var14) != 0) {
            this.field_76652_q[var14].func_76672_e();
         }
      }

      this.field_150814_l = true;
      this.field_76646_k = true;
      this.func_76590_a();

      for(TileEntity var20 : this.field_150816_i.values()) {
         var20.func_145836_u();
      }
   }

   public BiomeGenBase func_76591_a(int var1, int var2, WorldChunkManager var3) {
      int var4 = this.field_76651_r[var2 << 4 | var1] & 255;
      if (var4 == 255) {
         BiomeGenBase var5 = var3.func_76935_a((this.field_76635_g << 4) + var1, (this.field_76647_h << 4) + var2);
         var4 = var5.field_76756_M;
         this.field_76651_r[var2 << 4 | var1] = (byte)(var4 & 0xFF);
      }

      return BiomeGenBase.func_150568_d(var4) == null ? BiomeGenBase.field_76772_c : BiomeGenBase.func_150568_d(var4);
   }

   public byte[] func_76605_m() {
      return this.field_76651_r;
   }

   public void func_76616_a(byte[] var1) {
      this.field_76651_r = var1;
   }

   public void func_76613_n() {
      this.field_76649_t = 0;
   }

   public void func_76594_o() {
      for(int var1 = 0; var1 < 8; ++var1) {
         if (this.field_76649_t >= 4096) {
            return;
         }

         int var2 = this.field_76649_t % 16;
         int var3 = this.field_76649_t / 16 % 16;
         int var4 = this.field_76649_t / 256;
         ++this.field_76649_t;
         int var5 = (this.field_76635_g << 4) + var3;
         int var6 = (this.field_76647_h << 4) + var4;

         for(int var7 = 0; var7 < 16; ++var7) {
            int var8 = (var2 << 4) + var7;
            if (this.field_76652_q[var2] == null && (var7 == 0 || var7 == 15 || var3 == 0 || var3 == 15 || var4 == 0 || var4 == 15)
               || this.field_76652_q[var2] != null && this.field_76652_q[var2].func_150819_a(var3, var7, var4).func_149688_o() == Material.field_151579_a) {
               if (this.field_76637_e.func_147439_a(var5, var8 - 1, var6).func_149750_m() > 0) {
                  this.field_76637_e.func_147451_t(var5, var8 - 1, var6);
               }

               if (this.field_76637_e.func_147439_a(var5, var8 + 1, var6).func_149750_m() > 0) {
                  this.field_76637_e.func_147451_t(var5, var8 + 1, var6);
               }

               if (this.field_76637_e.func_147439_a(var5 - 1, var8, var6).func_149750_m() > 0) {
                  this.field_76637_e.func_147451_t(var5 - 1, var8, var6);
               }

               if (this.field_76637_e.func_147439_a(var5 + 1, var8, var6).func_149750_m() > 0) {
                  this.field_76637_e.func_147451_t(var5 + 1, var8, var6);
               }

               if (this.field_76637_e.func_147439_a(var5, var8, var6 - 1).func_149750_m() > 0) {
                  this.field_76637_e.func_147451_t(var5, var8, var6 - 1);
               }

               if (this.field_76637_e.func_147439_a(var5, var8, var6 + 1).func_149750_m() > 0) {
                  this.field_76637_e.func_147451_t(var5, var8, var6 + 1);
               }

               this.field_76637_e.func_147451_t(var5, var8, var6);
            }
         }
      }
   }

   public void func_150809_p() {
      this.field_76646_k = true;
      this.field_150814_l = true;
      if (!this.field_76637_e.field_73011_w.field_76576_e) {
         if (this.field_76637_e
            .func_72904_c(this.field_76635_g * 16 - 1, 0, this.field_76647_h * 16 - 1, this.field_76635_g * 16 + 1, 63, this.field_76647_h * 16 + 1)) {
            for(int var1 = 0; var1 < 16; ++var1) {
               for(int var2 = 0; var2 < 16; ++var2) {
                  if (!this.func_150811_f(var1, var2)) {
                     this.field_150814_l = false;
                     break;
                  }
               }
            }

            if (this.field_150814_l) {
               Chunk var3 = this.field_76637_e.func_72938_d(this.field_76635_g * 16 - 1, this.field_76647_h * 16);
               var3.func_150801_a(3);
               var3 = this.field_76637_e.func_72938_d(this.field_76635_g * 16 + 16, this.field_76647_h * 16);
               var3.func_150801_a(1);
               var3 = this.field_76637_e.func_72938_d(this.field_76635_g * 16, this.field_76647_h * 16 - 1);
               var3.func_150801_a(0);
               var3 = this.field_76637_e.func_72938_d(this.field_76635_g * 16, this.field_76647_h * 16 + 16);
               var3.func_150801_a(2);
            }
         } else {
            this.field_150814_l = false;
         }
      }
   }

   private void func_150801_a(int var1) {
      if (this.field_76646_k) {
         if (var1 == 3) {
            for(int var2 = 0; var2 < 16; ++var2) {
               this.func_150811_f(15, var2);
            }
         } else if (var1 == 1) {
            for(int var3 = 0; var3 < 16; ++var3) {
               this.func_150811_f(0, var3);
            }
         } else if (var1 == 0) {
            for(int var4 = 0; var4 < 16; ++var4) {
               this.func_150811_f(var4, 15);
            }
         } else if (var1 == 2) {
            for(int var5 = 0; var5 < 16; ++var5) {
               this.func_150811_f(var5, 0);
            }
         }
      }
   }

   private boolean func_150811_f(int var1, int var2) {
      int var3 = this.func_76625_h();
      boolean var4 = false;
      boolean var5 = false;

      int var6;
      for(var6 = var3 + 16 - 1; var6 > 63 || var6 > 0 && !var5; --var6) {
         int var7 = this.func_150808_b(var1, var6, var2);
         if (var7 == 255 && var6 < 63) {
            var5 = true;
         }

         if (!var4 && var7 > 0) {
            var4 = true;
         } else if (var4 && var7 == 0 && !this.field_76637_e.func_147451_t(this.field_76635_g * 16 + var1, var6, this.field_76647_h * 16 + var2)) {
            return false;
         }
      }

      for(; var6 > 0; --var6) {
         if (this.func_150810_a(var1, var6, var2).func_149750_m() > 0) {
            this.field_76637_e.func_147451_t(this.field_76635_g * 16 + var1, var6, this.field_76647_h * 16 + var2);
         }
      }

      return true;
   }
}
