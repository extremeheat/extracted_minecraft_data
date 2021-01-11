package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderDebug;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
   private static final Logger field_150817_t = LogManager.getLogger();
   private final ExtendedBlockStorage[] field_76652_q;
   private final byte[] field_76651_r;
   private final int[] field_76638_b;
   private final boolean[] field_76639_c;
   private boolean field_76636_d;
   private final World field_76637_e;
   private final int[] field_76634_f;
   public final int field_76635_g;
   public final int field_76647_h;
   private boolean field_76650_s;
   private final Map<BlockPos, TileEntity> field_150816_i;
   private final ClassInheritanceMultiMap<Entity>[] field_76645_j;
   private boolean field_76646_k;
   private boolean field_150814_l;
   private boolean field_150815_m;
   private boolean field_76643_l;
   private boolean field_76644_m;
   private long field_76641_n;
   private int field_82912_p;
   private long field_111204_q;
   private int field_76649_t;
   private ConcurrentLinkedQueue<BlockPos> field_177447_w;

   public Chunk(World var1, int var2, int var3) {
      super();
      this.field_76652_q = new ExtendedBlockStorage[16];
      this.field_76651_r = new byte[256];
      this.field_76638_b = new int[256];
      this.field_76639_c = new boolean[256];
      this.field_150816_i = Maps.newHashMap();
      this.field_76649_t = 4096;
      this.field_177447_w = Queues.newConcurrentLinkedQueue();
      this.field_76645_j = (ClassInheritanceMultiMap[])(new ClassInheritanceMultiMap[16]);
      this.field_76637_e = var1;
      this.field_76635_g = var2;
      this.field_76647_h = var3;
      this.field_76634_f = new int[256];

      for(int var4 = 0; var4 < this.field_76645_j.length; ++var4) {
         this.field_76645_j[var4] = new ClassInheritanceMultiMap(Entity.class);
      }

      Arrays.fill(this.field_76638_b, -999);
      Arrays.fill(this.field_76651_r, (byte)-1);
   }

   public Chunk(World var1, ChunkPrimer var2, int var3, int var4) {
      this(var1, var3, var4);
      short var5 = 256;
      boolean var6 = !var1.field_73011_w.func_177495_o();

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            for(int var9 = 0; var9 < var5; ++var9) {
               int var10 = var7 * var5 * 16 | var8 * var5 | var9;
               IBlockState var11 = var2.func_177858_a(var10);
               if (var11.func_177230_c().func_149688_o() != Material.field_151579_a) {
                  int var12 = var9 >> 4;
                  if (this.field_76652_q[var12] == null) {
                     this.field_76652_q[var12] = new ExtendedBlockStorage(var12 << 4, var6);
                  }

                  this.field_76652_q[var12].func_177484_a(var7, var9 & 15, var8, var11);
               }
            }
         }
      }

   }

   public boolean func_76600_a(int var1, int var2) {
      return var1 == this.field_76635_g && var2 == this.field_76647_h;
   }

   public int func_177433_f(BlockPos var1) {
      return this.func_76611_b(var1.func_177958_n() & 15, var1.func_177952_p() & 15);
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

   protected void func_76590_a() {
      int var1 = this.func_76625_h();
      this.field_82912_p = 2147483647;

      for(int var2 = 0; var2 < 16; ++var2) {
         for(int var3 = 0; var3 < 16; ++var3) {
            this.field_76638_b[var2 + (var3 << 4)] = -999;

            for(int var4 = var1 + 16; var4 > 0; --var4) {
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

            int var4;
            for(var4 = var1 + 16; var4 > 0; --var4) {
               if (this.func_150808_b(var2, var4 - 1, var3) != 0) {
                  this.field_76634_f[var3 << 4 | var2] = var4;
                  if (var4 < this.field_82912_p) {
                     this.field_82912_p = var4;
                  }
                  break;
               }
            }

            if (!this.field_76637_e.field_73011_w.func_177495_o()) {
               var4 = 15;
               int var5 = var1 + 16 - 1;

               do {
                  int var6 = this.func_150808_b(var2, var5, var3);
                  if (var6 == 0 && var4 != 15) {
                     var6 = 1;
                  }

                  var4 -= var6;
                  if (var4 > 0) {
                     ExtendedBlockStorage var7 = this.field_76652_q[var5 >> 4];
                     if (var7 != null) {
                        var7.func_76657_c(var2, var5 & 15, var3, var4);
                        this.field_76637_e.func_175679_n(new BlockPos((this.field_76635_g << 4) + var2, var5, (this.field_76647_h << 4) + var3));
                     }
                  }

                  --var5;
               } while(var5 > 0 && var4 > 0);
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
      if (this.field_76637_e.func_175697_a(new BlockPos(this.field_76635_g * 16 + 8, 0, this.field_76647_h * 16 + 8), 16)) {
         for(int var2 = 0; var2 < 16; ++var2) {
            for(int var3 = 0; var3 < 16; ++var3) {
               if (this.field_76639_c[var2 + var3 * 16]) {
                  this.field_76639_c[var2 + var3 * 16] = false;
                  int var4 = this.func_76611_b(var2, var3);
                  int var5 = this.field_76635_g * 16 + var2;
                  int var6 = this.field_76647_h * 16 + var3;
                  int var7 = 2147483647;

                  Iterator var8;
                  EnumFacing var9;
                  for(var8 = EnumFacing.Plane.HORIZONTAL.iterator(); var8.hasNext(); var7 = Math.min(var7, this.field_76637_e.func_82734_g(var5 + var9.func_82601_c(), var6 + var9.func_82599_e()))) {
                     var9 = (EnumFacing)var8.next();
                  }

                  this.func_76599_g(var5, var6, var7);
                  var8 = EnumFacing.Plane.HORIZONTAL.iterator();

                  while(var8.hasNext()) {
                     var9 = (EnumFacing)var8.next();
                     this.func_76599_g(var5 + var9.func_82601_c(), var6 + var9.func_82599_e(), var4);
                  }

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
      int var4 = this.field_76637_e.func_175645_m(new BlockPos(var1, 0, var2)).func_177956_o();
      if (var4 > var3) {
         this.func_76609_d(var1, var2, var3, var4 + 1);
      } else if (var4 < var3) {
         this.func_76609_d(var1, var2, var4, var3 + 1);
      }

   }

   private void func_76609_d(int var1, int var2, int var3, int var4) {
      if (var4 > var3 && this.field_76637_e.func_175697_a(new BlockPos(var1, 0, var2), 16)) {
         for(int var5 = var3; var5 < var4; ++var5) {
            this.field_76637_e.func_180500_c(EnumSkyBlock.SKY, new BlockPos(var1, var5, var2));
         }

         this.field_76643_l = true;
      }

   }

   private void func_76615_h(int var1, int var2, int var3) {
      int var4 = this.field_76634_f[var3 << 4 | var1] & 255;
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
         int var8;
         int var13;
         if (!this.field_76637_e.field_73011_w.func_177495_o()) {
            ExtendedBlockStorage var9;
            if (var5 < var4) {
               for(var8 = var5; var8 < var4; ++var8) {
                  var9 = this.field_76652_q[var8 >> 4];
                  if (var9 != null) {
                     var9.func_76657_c(var1, var8 & 15, var3, 15);
                     this.field_76637_e.func_175679_n(new BlockPos((this.field_76635_g << 4) + var1, var8, (this.field_76647_h << 4) + var3));
                  }
               }
            } else {
               for(var8 = var4; var8 < var5; ++var8) {
                  var9 = this.field_76652_q[var8 >> 4];
                  if (var9 != null) {
                     var9.func_76657_c(var1, var8 & 15, var3, 0);
                     this.field_76637_e.func_175679_n(new BlockPos((this.field_76635_g << 4) + var1, var8, (this.field_76647_h << 4) + var3));
                  }
               }
            }

            var8 = 15;

            while(var5 > 0 && var8 > 0) {
               --var5;
               var13 = this.func_150808_b(var1, var5, var3);
               if (var13 == 0) {
                  var13 = 1;
               }

               var8 -= var13;
               if (var8 < 0) {
                  var8 = 0;
               }

               ExtendedBlockStorage var10 = this.field_76652_q[var5 >> 4];
               if (var10 != null) {
                  var10.func_76657_c(var1, var5 & 15, var3, var8);
               }
            }
         }

         var8 = this.field_76634_f[var3 << 4 | var1];
         var13 = var4;
         int var14 = var8;
         if (var8 < var4) {
            var13 = var8;
            var14 = var4;
         }

         if (var8 < this.field_82912_p) {
            this.field_82912_p = var8;
         }

         if (!this.field_76637_e.field_73011_w.func_177495_o()) {
            Iterator var11 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(var11.hasNext()) {
               EnumFacing var12 = (EnumFacing)var11.next();
               this.func_76609_d(var6 + var12.func_82601_c(), var7 + var12.func_82599_e(), var13, var14);
            }

            this.func_76609_d(var6, var7, var13, var14);
         }

         this.field_76643_l = true;
      }
   }

   public int func_177437_b(BlockPos var1) {
      return this.func_177428_a(var1).func_149717_k();
   }

   private int func_150808_b(int var1, int var2, int var3) {
      return this.func_150810_a(var1, var2, var3).func_149717_k();
   }

   private Block func_150810_a(int var1, int var2, int var3) {
      Block var4 = Blocks.field_150350_a;
      if (var2 >= 0 && var2 >> 4 < this.field_76652_q.length) {
         ExtendedBlockStorage var5 = this.field_76652_q[var2 >> 4];
         if (var5 != null) {
            try {
               var4 = var5.func_150819_a(var1, var2 & 15, var3);
            } catch (Throwable var8) {
               CrashReport var7 = CrashReport.func_85055_a(var8, "Getting block");
               throw new ReportedException(var7);
            }
         }
      }

      return var4;
   }

   public Block func_177438_a(final int var1, final int var2, final int var3) {
      try {
         return this.func_150810_a(var1 & 15, var2, var3 & 15);
      } catch (ReportedException var6) {
         CrashReportCategory var5 = var6.func_71575_a().func_85058_a("Block being got");
         var5.func_71500_a("Location", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.func_180522_a(new BlockPos(Chunk.this.field_76635_g * 16 + var1, var2, Chunk.this.field_76647_h * 16 + var3));
            }

            // $FF: synthetic method
            public Object call() throws Exception {
               return this.call();
            }
         });
         throw var6;
      }
   }

   public Block func_177428_a(final BlockPos var1) {
      try {
         return this.func_150810_a(var1.func_177958_n() & 15, var1.func_177956_o(), var1.func_177952_p() & 15);
      } catch (ReportedException var4) {
         CrashReportCategory var3 = var4.func_71575_a().func_85058_a("Block being got");
         var3.func_71500_a("Location", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.func_180522_a(var1);
            }

            // $FF: synthetic method
            public Object call() throws Exception {
               return this.call();
            }
         });
         throw var4;
      }
   }

   public IBlockState func_177435_g(final BlockPos var1) {
      if (this.field_76637_e.func_175624_G() == WorldType.field_180272_g) {
         IBlockState var7 = null;
         if (var1.func_177956_o() == 60) {
            var7 = Blocks.field_180401_cv.func_176223_P();
         }

         if (var1.func_177956_o() == 70) {
            var7 = ChunkProviderDebug.func_177461_b(var1.func_177958_n(), var1.func_177952_p());
         }

         return var7 == null ? Blocks.field_150350_a.func_176223_P() : var7;
      } else {
         try {
            if (var1.func_177956_o() >= 0 && var1.func_177956_o() >> 4 < this.field_76652_q.length) {
               ExtendedBlockStorage var2 = this.field_76652_q[var1.func_177956_o() >> 4];
               if (var2 != null) {
                  int var8 = var1.func_177958_n() & 15;
                  int var9 = var1.func_177956_o() & 15;
                  int var5 = var1.func_177952_p() & 15;
                  return var2.func_177485_a(var8, var9, var5);
               }
            }

            return Blocks.field_150350_a.func_176223_P();
         } catch (Throwable var6) {
            CrashReport var3 = CrashReport.func_85055_a(var6, "Getting block state");
            CrashReportCategory var4 = var3.func_85058_a("Block being got");
            var4.func_71500_a("Location", new Callable<String>() {
               public String call() throws Exception {
                  return CrashReportCategory.func_180522_a(var1);
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            throw new ReportedException(var3);
         }
      }
   }

   private int func_76628_c(int var1, int var2, int var3) {
      if (var2 >> 4 >= this.field_76652_q.length) {
         return 0;
      } else {
         ExtendedBlockStorage var4 = this.field_76652_q[var2 >> 4];
         return var4 != null ? var4.func_76665_b(var1, var2 & 15, var3) : 0;
      }
   }

   public int func_177418_c(BlockPos var1) {
      return this.func_76628_c(var1.func_177958_n() & 15, var1.func_177956_o(), var1.func_177952_p() & 15);
   }

   public IBlockState func_177436_a(BlockPos var1, IBlockState var2) {
      int var3 = var1.func_177958_n() & 15;
      int var4 = var1.func_177956_o();
      int var5 = var1.func_177952_p() & 15;
      int var6 = var5 << 4 | var3;
      if (var4 >= this.field_76638_b[var6] - 1) {
         this.field_76638_b[var6] = -999;
      }

      int var7 = this.field_76634_f[var6];
      IBlockState var8 = this.func_177435_g(var1);
      if (var8 == var2) {
         return null;
      } else {
         Block var9 = var2.func_177230_c();
         Block var10 = var8.func_177230_c();
         ExtendedBlockStorage var11 = this.field_76652_q[var4 >> 4];
         boolean var12 = false;
         if (var11 == null) {
            if (var9 == Blocks.field_150350_a) {
               return null;
            }

            var11 = this.field_76652_q[var4 >> 4] = new ExtendedBlockStorage(var4 >> 4 << 4, !this.field_76637_e.field_73011_w.func_177495_o());
            var12 = var4 >= var7;
         }

         var11.func_177484_a(var3, var4 & 15, var5, var2);
         if (var10 != var9) {
            if (!this.field_76637_e.field_72995_K) {
               var10.func_180663_b(this.field_76637_e, var1, var8);
            } else if (var10 instanceof ITileEntityProvider) {
               this.field_76637_e.func_175713_t(var1);
            }
         }

         if (var11.func_150819_a(var3, var4 & 15, var5) != var9) {
            return null;
         } else {
            if (var12) {
               this.func_76603_b();
            } else {
               int var13 = var9.func_149717_k();
               int var14 = var10.func_149717_k();
               if (var13 > 0) {
                  if (var4 >= var7) {
                     this.func_76615_h(var3, var4 + 1, var5);
                  }
               } else if (var4 == var7 - 1) {
                  this.func_76615_h(var3, var4, var5);
               }

               if (var13 != var14 && (var13 < var14 || this.func_177413_a(EnumSkyBlock.SKY, var1) > 0 || this.func_177413_a(EnumSkyBlock.BLOCK, var1) > 0)) {
                  this.func_76595_e(var3, var5);
               }
            }

            TileEntity var15;
            if (var10 instanceof ITileEntityProvider) {
               var15 = this.func_177424_a(var1, Chunk.EnumCreateEntityType.CHECK);
               if (var15 != null) {
                  var15.func_145836_u();
               }
            }

            if (!this.field_76637_e.field_72995_K && var10 != var9) {
               var9.func_176213_c(this.field_76637_e, var1, var2);
            }

            if (var9 instanceof ITileEntityProvider) {
               var15 = this.func_177424_a(var1, Chunk.EnumCreateEntityType.CHECK);
               if (var15 == null) {
                  var15 = ((ITileEntityProvider)var9).func_149915_a(this.field_76637_e, var9.func_176201_c(var2));
                  this.field_76637_e.func_175690_a(var1, var15);
               }

               if (var15 != null) {
                  var15.func_145836_u();
               }
            }

            this.field_76643_l = true;
            return var8;
         }
      }
   }

   public int func_177413_a(EnumSkyBlock var1, BlockPos var2) {
      int var3 = var2.func_177958_n() & 15;
      int var4 = var2.func_177956_o();
      int var5 = var2.func_177952_p() & 15;
      ExtendedBlockStorage var6 = this.field_76652_q[var4 >> 4];
      if (var6 == null) {
         return this.func_177444_d(var2) ? var1.field_77198_c : 0;
      } else if (var1 == EnumSkyBlock.SKY) {
         return this.field_76637_e.field_73011_w.func_177495_o() ? 0 : var6.func_76670_c(var3, var4 & 15, var5);
      } else {
         return var1 == EnumSkyBlock.BLOCK ? var6.func_76674_d(var3, var4 & 15, var5) : var1.field_77198_c;
      }
   }

   public void func_177431_a(EnumSkyBlock var1, BlockPos var2, int var3) {
      int var4 = var2.func_177958_n() & 15;
      int var5 = var2.func_177956_o();
      int var6 = var2.func_177952_p() & 15;
      ExtendedBlockStorage var7 = this.field_76652_q[var5 >> 4];
      if (var7 == null) {
         var7 = this.field_76652_q[var5 >> 4] = new ExtendedBlockStorage(var5 >> 4 << 4, !this.field_76637_e.field_73011_w.func_177495_o());
         this.func_76603_b();
      }

      this.field_76643_l = true;
      if (var1 == EnumSkyBlock.SKY) {
         if (!this.field_76637_e.field_73011_w.func_177495_o()) {
            var7.func_76657_c(var4, var5 & 15, var6, var3);
         }
      } else if (var1 == EnumSkyBlock.BLOCK) {
         var7.func_76677_d(var4, var5 & 15, var6, var3);
      }

   }

   public int func_177443_a(BlockPos var1, int var2) {
      int var3 = var1.func_177958_n() & 15;
      int var4 = var1.func_177956_o();
      int var5 = var1.func_177952_p() & 15;
      ExtendedBlockStorage var6 = this.field_76652_q[var4 >> 4];
      if (var6 == null) {
         return !this.field_76637_e.field_73011_w.func_177495_o() && var2 < EnumSkyBlock.SKY.field_77198_c ? EnumSkyBlock.SKY.field_77198_c - var2 : 0;
      } else {
         int var7 = this.field_76637_e.field_73011_w.func_177495_o() ? 0 : var6.func_76670_c(var3, var4 & 15, var5);
         var7 -= var2;
         int var8 = var6.func_76674_d(var3, var4 & 15, var5);
         if (var8 > var7) {
            var7 = var8;
         }

         return var7;
      }
   }

   public void func_76612_a(Entity var1) {
      this.field_76644_m = true;
      int var2 = MathHelper.func_76128_c(var1.field_70165_t / 16.0D);
      int var3 = MathHelper.func_76128_c(var1.field_70161_v / 16.0D);
      if (var2 != this.field_76635_g || var3 != this.field_76647_h) {
         field_150817_t.warn("Wrong location! (" + var2 + ", " + var3 + ") should be (" + this.field_76635_g + ", " + this.field_76647_h + "), " + var1, new Object[]{var1});
         var1.func_70106_y();
      }

      int var4 = MathHelper.func_76128_c(var1.field_70163_u / 16.0D);
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

   public boolean func_177444_d(BlockPos var1) {
      int var2 = var1.func_177958_n() & 15;
      int var3 = var1.func_177956_o();
      int var4 = var1.func_177952_p() & 15;
      return var3 >= this.field_76634_f[var4 << 4 | var2];
   }

   private TileEntity func_177422_i(BlockPos var1) {
      Block var2 = this.func_177428_a(var1);
      return !var2.func_149716_u() ? null : ((ITileEntityProvider)var2).func_149915_a(this.field_76637_e, this.func_177418_c(var1));
   }

   public TileEntity func_177424_a(BlockPos var1, Chunk.EnumCreateEntityType var2) {
      TileEntity var3 = (TileEntity)this.field_150816_i.get(var1);
      if (var3 == null) {
         if (var2 == Chunk.EnumCreateEntityType.IMMEDIATE) {
            var3 = this.func_177422_i(var1);
            this.field_76637_e.func_175690_a(var1, var3);
         } else if (var2 == Chunk.EnumCreateEntityType.QUEUED) {
            this.field_177447_w.add(var1);
         }
      } else if (var3.func_145837_r()) {
         this.field_150816_i.remove(var1);
         return null;
      }

      return var3;
   }

   public void func_150813_a(TileEntity var1) {
      this.func_177426_a(var1.func_174877_v(), var1);
      if (this.field_76636_d) {
         this.field_76637_e.func_175700_a(var1);
      }

   }

   public void func_177426_a(BlockPos var1, TileEntity var2) {
      var2.func_145834_a(this.field_76637_e);
      var2.func_174878_a(var1);
      if (this.func_177428_a(var1) instanceof ITileEntityProvider) {
         if (this.field_150816_i.containsKey(var1)) {
            ((TileEntity)this.field_150816_i.get(var1)).func_145843_s();
         }

         var2.func_145829_t();
         this.field_150816_i.put(var1, var2);
      }
   }

   public void func_177425_e(BlockPos var1) {
      if (this.field_76636_d) {
         TileEntity var2 = (TileEntity)this.field_150816_i.remove(var1);
         if (var2 != null) {
            var2.func_145843_s();
         }
      }

   }

   public void func_76631_c() {
      this.field_76636_d = true;
      this.field_76637_e.func_147448_a(this.field_150816_i.values());

      for(int var1 = 0; var1 < this.field_76645_j.length; ++var1) {
         Iterator var2 = this.field_76645_j[var1].iterator();

         while(var2.hasNext()) {
            Entity var3 = (Entity)var2.next();
            var3.func_110123_P();
         }

         this.field_76637_e.func_175650_b(this.field_76645_j[var1]);
      }

   }

   public void func_76623_d() {
      this.field_76636_d = false;
      Iterator var1 = this.field_150816_i.values().iterator();

      while(var1.hasNext()) {
         TileEntity var2 = (TileEntity)var1.next();
         this.field_76637_e.func_147457_a(var2);
      }

      for(int var3 = 0; var3 < this.field_76645_j.length; ++var3) {
         this.field_76637_e.func_175681_c(this.field_76645_j[var3]);
      }

   }

   public void func_76630_e() {
      this.field_76643_l = true;
   }

   public void func_177414_a(Entity var1, AxisAlignedBB var2, List<Entity> var3, Predicate<? super Entity> var4) {
      int var5 = MathHelper.func_76128_c((var2.field_72338_b - 2.0D) / 16.0D);
      int var6 = MathHelper.func_76128_c((var2.field_72337_e + 2.0D) / 16.0D);
      var5 = MathHelper.func_76125_a(var5, 0, this.field_76645_j.length - 1);
      var6 = MathHelper.func_76125_a(var6, 0, this.field_76645_j.length - 1);

      label68:
      for(int var7 = var5; var7 <= var6; ++var7) {
         if (!this.field_76645_j[var7].isEmpty()) {
            Iterator var8 = this.field_76645_j[var7].iterator();

            while(true) {
               Entity var9;
               Entity[] var10;
               do {
                  do {
                     do {
                        if (!var8.hasNext()) {
                           continue label68;
                        }

                        var9 = (Entity)var8.next();
                     } while(!var9.func_174813_aQ().func_72326_a(var2));
                  } while(var9 == var1);

                  if (var4 == null || var4.apply(var9)) {
                     var3.add(var9);
                  }

                  var10 = var9.func_70021_al();
               } while(var10 == null);

               for(int var11 = 0; var11 < var10.length; ++var11) {
                  var9 = var10[var11];
                  if (var9 != var1 && var9.func_174813_aQ().func_72326_a(var2) && (var4 == null || var4.apply(var9))) {
                     var3.add(var9);
                  }
               }
            }
         }
      }

   }

   public <T extends Entity> void func_177430_a(Class<? extends T> var1, AxisAlignedBB var2, List<T> var3, Predicate<? super T> var4) {
      int var5 = MathHelper.func_76128_c((var2.field_72338_b - 2.0D) / 16.0D);
      int var6 = MathHelper.func_76128_c((var2.field_72337_e + 2.0D) / 16.0D);
      var5 = MathHelper.func_76125_a(var5, 0, this.field_76645_j.length - 1);
      var6 = MathHelper.func_76125_a(var6, 0, this.field_76645_j.length - 1);

      label33:
      for(int var7 = var5; var7 <= var6; ++var7) {
         Iterator var8 = this.field_76645_j[var7].func_180215_b(var1).iterator();

         while(true) {
            Entity var9;
            do {
               do {
                  if (!var8.hasNext()) {
                     continue label33;
                  }

                  var9 = (Entity)var8.next();
               } while(!var9.func_174813_aQ().func_72326_a(var2));
            } while(var4 != null && !var4.apply(var9));

            var3.add(var9);
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
      return new Random(this.field_76637_e.func_72905_C() + (long)(this.field_76635_g * this.field_76635_g * 4987142) + (long)(this.field_76635_g * 5947611) + (long)(this.field_76647_h * this.field_76647_h) * 4392871L + (long)(this.field_76647_h * 389711) ^ var1);
   }

   public boolean func_76621_g() {
      return false;
   }

   public void func_76624_a(IChunkProvider var1, IChunkProvider var2, int var3, int var4) {
      boolean var5 = var1.func_73149_a(var3, var4 - 1);
      boolean var6 = var1.func_73149_a(var3 + 1, var4);
      boolean var7 = var1.func_73149_a(var3, var4 + 1);
      boolean var8 = var1.func_73149_a(var3 - 1, var4);
      boolean var9 = var1.func_73149_a(var3 - 1, var4 - 1);
      boolean var10 = var1.func_73149_a(var3 + 1, var4 + 1);
      boolean var11 = var1.func_73149_a(var3 - 1, var4 + 1);
      boolean var12 = var1.func_73149_a(var3 + 1, var4 - 1);
      if (var6 && var7 && var10) {
         if (!this.field_76646_k) {
            var1.func_73153_a(var2, var3, var4);
         } else {
            var1.func_177460_a(var2, this, var3, var4);
         }
      }

      Chunk var13;
      if (var8 && var7 && var11) {
         var13 = var1.func_73154_d(var3 - 1, var4);
         if (!var13.field_76646_k) {
            var1.func_73153_a(var2, var3 - 1, var4);
         } else {
            var1.func_177460_a(var2, var13, var3 - 1, var4);
         }
      }

      if (var5 && var6 && var12) {
         var13 = var1.func_73154_d(var3, var4 - 1);
         if (!var13.field_76646_k) {
            var1.func_73153_a(var2, var3, var4 - 1);
         } else {
            var1.func_177460_a(var2, var13, var3, var4 - 1);
         }
      }

      if (var9 && var5 && var8) {
         var13 = var1.func_73154_d(var3 - 1, var4 - 1);
         if (!var13.field_76646_k) {
            var1.func_73153_a(var2, var3 - 1, var4 - 1);
         } else {
            var1.func_177460_a(var2, var13, var3 - 1, var4 - 1);
         }
      }

   }

   public BlockPos func_177440_h(BlockPos var1) {
      int var2 = var1.func_177958_n() & 15;
      int var3 = var1.func_177952_p() & 15;
      int var4 = var2 | var3 << 4;
      BlockPos var5 = new BlockPos(var1.func_177958_n(), this.field_76638_b[var4], var1.func_177952_p());
      if (var5.func_177956_o() == -999) {
         int var6 = this.func_76625_h() + 15;
         var5 = new BlockPos(var1.func_177958_n(), var6, var1.func_177952_p());
         int var7 = -1;

         while(true) {
            while(var5.func_177956_o() > 0 && var7 == -1) {
               Block var8 = this.func_177428_a(var5);
               Material var9 = var8.func_149688_o();
               if (!var9.func_76230_c() && !var9.func_76224_d()) {
                  var5 = var5.func_177977_b();
               } else {
                  var7 = var5.func_177956_o() + 1;
               }
            }

            this.field_76638_b[var4] = var7;
            break;
         }
      }

      return new BlockPos(var1.func_177958_n(), this.field_76638_b[var4], var1.func_177952_p());
   }

   public void func_150804_b(boolean var1) {
      if (this.field_76650_s && !this.field_76637_e.field_73011_w.func_177495_o() && !var1) {
         this.func_150803_c(this.field_76637_e.field_72995_K);
      }

      this.field_150815_m = true;
      if (!this.field_150814_l && this.field_76646_k) {
         this.func_150809_p();
      }

      while(!this.field_177447_w.isEmpty()) {
         BlockPos var2 = (BlockPos)this.field_177447_w.poll();
         if (this.func_177424_a(var2, Chunk.EnumCreateEntityType.CHECK) == null && this.func_177428_a(var2).func_149716_u()) {
            TileEntity var3 = this.func_177422_i(var2);
            this.field_76637_e.func_175690_a(var2, var3);
            this.field_76637_e.func_175704_b(var2, var2);
         }
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
      if (this.field_76652_q.length != var1.length) {
         field_150817_t.warn("Could not set level chunk sections, array length is " + var1.length + " instead of " + this.field_76652_q.length);
      } else {
         for(int var2 = 0; var2 < this.field_76652_q.length; ++var2) {
            this.field_76652_q[var2] = var1[var2];
         }

      }
   }

   public void func_177439_a(byte[] var1, int var2, boolean var3) {
      int var4 = 0;
      boolean var5 = !this.field_76637_e.field_73011_w.func_177495_o();

      int var6;
      for(var6 = 0; var6 < this.field_76652_q.length; ++var6) {
         if ((var2 & 1 << var6) != 0) {
            if (this.field_76652_q[var6] == null) {
               this.field_76652_q[var6] = new ExtendedBlockStorage(var6 << 4, var5);
            }

            char[] var7 = this.field_76652_q[var6].func_177487_g();

            for(int var8 = 0; var8 < var7.length; ++var8) {
               var7[var8] = (char)((var1[var4 + 1] & 255) << 8 | var1[var4] & 255);
               var4 += 2;
            }
         } else if (var3 && this.field_76652_q[var6] != null) {
            this.field_76652_q[var6] = null;
         }
      }

      NibbleArray var9;
      for(var6 = 0; var6 < this.field_76652_q.length; ++var6) {
         if ((var2 & 1 << var6) != 0 && this.field_76652_q[var6] != null) {
            var9 = this.field_76652_q[var6].func_76661_k();
            System.arraycopy(var1, var4, var9.func_177481_a(), 0, var9.func_177481_a().length);
            var4 += var9.func_177481_a().length;
         }
      }

      if (var5) {
         for(var6 = 0; var6 < this.field_76652_q.length; ++var6) {
            if ((var2 & 1 << var6) != 0 && this.field_76652_q[var6] != null) {
               var9 = this.field_76652_q[var6].func_76671_l();
               System.arraycopy(var1, var4, var9.func_177481_a(), 0, var9.func_177481_a().length);
               var4 += var9.func_177481_a().length;
            }
         }
      }

      if (var3) {
         System.arraycopy(var1, var4, this.field_76651_r, 0, this.field_76651_r.length);
         int var10000 = var4 + this.field_76651_r.length;
      }

      for(var6 = 0; var6 < this.field_76652_q.length; ++var6) {
         if (this.field_76652_q[var6] != null && (var2 & 1 << var6) != 0) {
            this.field_76652_q[var6].func_76672_e();
         }
      }

      this.field_150814_l = true;
      this.field_76646_k = true;
      this.func_76590_a();
      Iterator var11 = this.field_150816_i.values().iterator();

      while(var11.hasNext()) {
         TileEntity var10 = (TileEntity)var11.next();
         var10.func_145836_u();
      }

   }

   public BiomeGenBase func_177411_a(BlockPos var1, WorldChunkManager var2) {
      int var3 = var1.func_177958_n() & 15;
      int var4 = var1.func_177952_p() & 15;
      int var5 = this.field_76651_r[var4 << 4 | var3] & 255;
      BiomeGenBase var6;
      if (var5 == 255) {
         var6 = var2.func_180300_a(var1, BiomeGenBase.field_76772_c);
         var5 = var6.field_76756_M;
         this.field_76651_r[var4 << 4 | var3] = (byte)(var5 & 255);
      }

      var6 = BiomeGenBase.func_150568_d(var5);
      return var6 == null ? BiomeGenBase.field_76772_c : var6;
   }

   public byte[] func_76605_m() {
      return this.field_76651_r;
   }

   public void func_76616_a(byte[] var1) {
      if (this.field_76651_r.length != var1.length) {
         field_150817_t.warn("Could not set level chunk biomes, array length is " + var1.length + " instead of " + this.field_76651_r.length);
      } else {
         for(int var2 = 0; var2 < this.field_76651_r.length; ++var2) {
            this.field_76651_r[var2] = var1[var2];
         }

      }
   }

   public void func_76613_n() {
      this.field_76649_t = 0;
   }

   public void func_76594_o() {
      BlockPos var1 = new BlockPos(this.field_76635_g << 4, 0, this.field_76647_h << 4);

      for(int var2 = 0; var2 < 8; ++var2) {
         if (this.field_76649_t >= 4096) {
            return;
         }

         int var3 = this.field_76649_t % 16;
         int var4 = this.field_76649_t / 16 % 16;
         int var5 = this.field_76649_t / 256;
         ++this.field_76649_t;

         for(int var6 = 0; var6 < 16; ++var6) {
            BlockPos var7 = var1.func_177982_a(var4, (var3 << 4) + var6, var5);
            boolean var8 = var6 == 0 || var6 == 15 || var4 == 0 || var4 == 15 || var5 == 0 || var5 == 15;
            if (this.field_76652_q[var3] == null && var8 || this.field_76652_q[var3] != null && this.field_76652_q[var3].func_150819_a(var4, var6, var5).func_149688_o() == Material.field_151579_a) {
               EnumFacing[] var9 = EnumFacing.values();
               int var10 = var9.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  EnumFacing var12 = var9[var11];
                  BlockPos var13 = var7.func_177972_a(var12);
                  if (this.field_76637_e.func_180495_p(var13).func_177230_c().func_149750_m() > 0) {
                     this.field_76637_e.func_175664_x(var13);
                  }
               }

               this.field_76637_e.func_175664_x(var7);
            }
         }
      }

   }

   public void func_150809_p() {
      this.field_76646_k = true;
      this.field_150814_l = true;
      BlockPos var1 = new BlockPos(this.field_76635_g << 4, 0, this.field_76647_h << 4);
      if (!this.field_76637_e.field_73011_w.func_177495_o()) {
         if (this.field_76637_e.func_175707_a(var1.func_177982_a(-1, 0, -1), var1.func_177982_a(16, this.field_76637_e.func_181545_F(), 16))) {
            label44:
            for(int var2 = 0; var2 < 16; ++var2) {
               for(int var3 = 0; var3 < 16; ++var3) {
                  if (!this.func_150811_f(var2, var3)) {
                     this.field_150814_l = false;
                     break label44;
                  }
               }
            }

            if (this.field_150814_l) {
               Iterator var5 = EnumFacing.Plane.HORIZONTAL.iterator();

               while(var5.hasNext()) {
                  EnumFacing var6 = (EnumFacing)var5.next();
                  int var4 = var6.func_176743_c() == EnumFacing.AxisDirection.POSITIVE ? 16 : 1;
                  this.field_76637_e.func_175726_f(var1.func_177967_a(var6, var4)).func_180700_a(var6.func_176734_d());
               }

               this.func_177441_y();
            }
         } else {
            this.field_150814_l = false;
         }
      }

   }

   private void func_177441_y() {
      for(int var1 = 0; var1 < this.field_76639_c.length; ++var1) {
         this.field_76639_c[var1] = true;
      }

      this.func_150803_c(false);
   }

   private void func_180700_a(EnumFacing var1) {
      if (this.field_76646_k) {
         int var2;
         if (var1 == EnumFacing.EAST) {
            for(var2 = 0; var2 < 16; ++var2) {
               this.func_150811_f(15, var2);
            }
         } else if (var1 == EnumFacing.WEST) {
            for(var2 = 0; var2 < 16; ++var2) {
               this.func_150811_f(0, var2);
            }
         } else if (var1 == EnumFacing.SOUTH) {
            for(var2 = 0; var2 < 16; ++var2) {
               this.func_150811_f(var2, 15);
            }
         } else if (var1 == EnumFacing.NORTH) {
            for(var2 = 0; var2 < 16; ++var2) {
               this.func_150811_f(var2, 0);
            }
         }

      }
   }

   private boolean func_150811_f(int var1, int var2) {
      int var3 = this.func_76625_h();
      boolean var4 = false;
      boolean var5 = false;
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos((this.field_76635_g << 4) + var1, 0, (this.field_76647_h << 4) + var2);

      int var7;
      for(var7 = var3 + 16 - 1; var7 > this.field_76637_e.func_181545_F() || var7 > 0 && !var5; --var7) {
         var6.func_181079_c(var6.func_177958_n(), var7, var6.func_177952_p());
         int var8 = this.func_177437_b(var6);
         if (var8 == 255 && var6.func_177956_o() < this.field_76637_e.func_181545_F()) {
            var5 = true;
         }

         if (!var4 && var8 > 0) {
            var4 = true;
         } else if (var4 && var8 == 0 && !this.field_76637_e.func_175664_x(var6)) {
            return false;
         }
      }

      for(var7 = var6.func_177956_o(); var7 > 0; --var7) {
         var6.func_181079_c(var6.func_177958_n(), var7, var6.func_177952_p());
         if (this.func_177428_a(var6).func_149750_m() > 0) {
            this.field_76637_e.func_175664_x(var6);
         }
      }

      return true;
   }

   public boolean func_177410_o() {
      return this.field_76636_d;
   }

   public void func_177417_c(boolean var1) {
      this.field_76636_d = var1;
   }

   public World func_177412_p() {
      return this.field_76637_e;
   }

   public int[] func_177445_q() {
      return this.field_76634_f;
   }

   public void func_177420_a(int[] var1) {
      if (this.field_76634_f.length != var1.length) {
         field_150817_t.warn("Could not set level chunk heightmap, array length is " + var1.length + " instead of " + this.field_76634_f.length);
      } else {
         for(int var2 = 0; var2 < this.field_76634_f.length; ++var2) {
            this.field_76634_f[var2] = var1[var2];
         }

      }
   }

   public Map<BlockPos, TileEntity> func_177434_r() {
      return this.field_150816_i;
   }

   public ClassInheritanceMultiMap<Entity>[] func_177429_s() {
      return this.field_76645_j;
   }

   public boolean func_177419_t() {
      return this.field_76646_k;
   }

   public void func_177446_d(boolean var1) {
      this.field_76646_k = var1;
   }

   public boolean func_177423_u() {
      return this.field_150814_l;
   }

   public void func_177421_e(boolean var1) {
      this.field_150814_l = var1;
   }

   public void func_177427_f(boolean var1) {
      this.field_76643_l = var1;
   }

   public void func_177409_g(boolean var1) {
      this.field_76644_m = var1;
   }

   public void func_177432_b(long var1) {
      this.field_76641_n = var1;
   }

   public int func_177442_v() {
      return this.field_82912_p;
   }

   public long func_177416_w() {
      return this.field_111204_q;
   }

   public void func_177415_c(long var1) {
      this.field_111204_q = var1;
   }

   public static enum EnumCreateEntityType {
      IMMEDIATE,
      QUEUED,
      CHECK;

      private EnumCreateEntityType() {
      }
   }
}
