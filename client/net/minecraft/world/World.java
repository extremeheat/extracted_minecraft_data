package net.minecraft.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

public abstract class World implements IBlockAccess {
   private int field_181546_a = 63;
   protected boolean field_72999_e;
   public final List<Entity> field_72996_f = Lists.newArrayList();
   protected final List<Entity> field_72997_g = Lists.newArrayList();
   public final List<TileEntity> field_147482_g = Lists.newArrayList();
   public final List<TileEntity> field_175730_i = Lists.newArrayList();
   private final List<TileEntity> field_147484_a = Lists.newArrayList();
   private final List<TileEntity> field_147483_b = Lists.newArrayList();
   public final List<EntityPlayer> field_73010_i = Lists.newArrayList();
   public final List<Entity> field_73007_j = Lists.newArrayList();
   protected final IntHashMap<Entity> field_175729_l = new IntHashMap();
   private long field_73001_c = 16777215L;
   private int field_73008_k;
   protected int field_73005_l = (new Random()).nextInt();
   protected final int field_73006_m = 1013904223;
   protected float field_73003_n;
   protected float field_73004_o;
   protected float field_73018_p;
   protected float field_73017_q;
   private int field_73016_r;
   public final Random field_73012_v = new Random();
   public final WorldProvider field_73011_w;
   protected List<IWorldAccess> field_73021_x = Lists.newArrayList();
   protected IChunkProvider field_73020_y;
   protected final ISaveHandler field_73019_z;
   protected WorldInfo field_72986_A;
   protected boolean field_72987_B;
   protected MapStorage field_72988_C;
   protected VillageCollection field_72982_D;
   public final Profiler field_72984_F;
   private final Calendar field_83016_L = Calendar.getInstance();
   protected Scoreboard field_96442_D = new Scoreboard();
   public final boolean field_72995_K;
   protected Set<ChunkCoordIntPair> field_72993_I = Sets.newHashSet();
   private int field_72990_M;
   protected boolean field_72985_G;
   protected boolean field_72992_H;
   private boolean field_147481_N;
   private final WorldBorder field_175728_M;
   int[] field_72994_J;

   protected World(ISaveHandler var1, WorldInfo var2, WorldProvider var3, Profiler var4, boolean var5) {
      super();
      this.field_72990_M = this.field_73012_v.nextInt(12000);
      this.field_72985_G = true;
      this.field_72992_H = true;
      this.field_72994_J = new int['\u8000'];
      this.field_73019_z = var1;
      this.field_72984_F = var4;
      this.field_72986_A = var2;
      this.field_73011_w = var3;
      this.field_72995_K = var5;
      this.field_175728_M = var3.func_177501_r();
   }

   public World func_175643_b() {
      return this;
   }

   public BiomeGenBase func_180494_b(final BlockPos var1) {
      if (this.func_175667_e(var1)) {
         Chunk var2 = this.func_175726_f(var1);

         try {
            return var2.func_177411_a(var1, this.field_73011_w.func_177499_m());
         } catch (Throwable var6) {
            CrashReport var4 = CrashReport.func_85055_a(var6, "Getting biome");
            CrashReportCategory var5 = var4.func_85058_a("Coordinates of biome request");
            var5.func_71500_a("Location", new Callable<String>() {
               public String call() throws Exception {
                  return CrashReportCategory.func_180522_a(var1);
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            throw new ReportedException(var4);
         }
      } else {
         return this.field_73011_w.func_177499_m().func_180300_a(var1, BiomeGenBase.field_76772_c);
      }
   }

   public WorldChunkManager func_72959_q() {
      return this.field_73011_w.func_177499_m();
   }

   protected abstract IChunkProvider func_72970_h();

   public void func_72963_a(WorldSettings var1) {
      this.field_72986_A.func_76091_d(true);
   }

   public void func_72974_f() {
      this.func_175652_B(new BlockPos(8, 64, 8));
   }

   public Block func_175703_c(BlockPos var1) {
      BlockPos var2;
      for(var2 = new BlockPos(var1.func_177958_n(), this.func_181545_F(), var1.func_177952_p()); !this.func_175623_d(var2.func_177984_a()); var2 = var2.func_177984_a()) {
      }

      return this.func_180495_p(var2).func_177230_c();
   }

   private boolean func_175701_a(BlockPos var1) {
      return var1.func_177958_n() >= -30000000 && var1.func_177952_p() >= -30000000 && var1.func_177958_n() < 30000000 && var1.func_177952_p() < 30000000 && var1.func_177956_o() >= 0 && var1.func_177956_o() < 256;
   }

   public boolean func_175623_d(BlockPos var1) {
      return this.func_180495_p(var1).func_177230_c().func_149688_o() == Material.field_151579_a;
   }

   public boolean func_175667_e(BlockPos var1) {
      return this.func_175668_a(var1, true);
   }

   public boolean func_175668_a(BlockPos var1, boolean var2) {
      return !this.func_175701_a(var1) ? false : this.func_175680_a(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4, var2);
   }

   public boolean func_175697_a(BlockPos var1, int var2) {
      return this.func_175648_a(var1, var2, true);
   }

   public boolean func_175648_a(BlockPos var1, int var2, boolean var3) {
      return this.func_175663_a(var1.func_177958_n() - var2, var1.func_177956_o() - var2, var1.func_177952_p() - var2, var1.func_177958_n() + var2, var1.func_177956_o() + var2, var1.func_177952_p() + var2, var3);
   }

   public boolean func_175707_a(BlockPos var1, BlockPos var2) {
      return this.func_175706_a(var1, var2, true);
   }

   public boolean func_175706_a(BlockPos var1, BlockPos var2, boolean var3) {
      return this.func_175663_a(var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p(), var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p(), var3);
   }

   public boolean func_175711_a(StructureBoundingBox var1) {
      return this.func_175639_b(var1, true);
   }

   public boolean func_175639_b(StructureBoundingBox var1, boolean var2) {
      return this.func_175663_a(var1.field_78897_a, var1.field_78895_b, var1.field_78896_c, var1.field_78893_d, var1.field_78894_e, var1.field_78892_f, var2);
   }

   private boolean func_175663_a(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      if (var5 >= 0 && var2 < 256) {
         var1 >>= 4;
         var3 >>= 4;
         var4 >>= 4;
         var6 >>= 4;

         for(int var8 = var1; var8 <= var4; ++var8) {
            for(int var9 = var3; var9 <= var6; ++var9) {
               if (!this.func_175680_a(var8, var9, var7)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean func_175680_a(int var1, int var2, boolean var3) {
      return this.field_73020_y.func_73149_a(var1, var2) && (var3 || !this.field_73020_y.func_73154_d(var1, var2).func_76621_g());
   }

   public Chunk func_175726_f(BlockPos var1) {
      return this.func_72964_e(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4);
   }

   public Chunk func_72964_e(int var1, int var2) {
      return this.field_73020_y.func_73154_d(var1, var2);
   }

   public boolean func_180501_a(BlockPos var1, IBlockState var2, int var3) {
      if (!this.func_175701_a(var1)) {
         return false;
      } else if (!this.field_72995_K && this.field_72986_A.func_76067_t() == WorldType.field_180272_g) {
         return false;
      } else {
         Chunk var4 = this.func_175726_f(var1);
         Block var5 = var2.func_177230_c();
         IBlockState var6 = var4.func_177436_a(var1, var2);
         if (var6 == null) {
            return false;
         } else {
            Block var7 = var6.func_177230_c();
            if (var5.func_149717_k() != var7.func_149717_k() || var5.func_149750_m() != var7.func_149750_m()) {
               this.field_72984_F.func_76320_a("checkLight");
               this.func_175664_x(var1);
               this.field_72984_F.func_76319_b();
            }

            if ((var3 & 2) != 0 && (!this.field_72995_K || (var3 & 4) == 0) && var4.func_150802_k()) {
               this.func_175689_h(var1);
            }

            if (!this.field_72995_K && (var3 & 1) != 0) {
               this.func_175722_b(var1, var6.func_177230_c());
               if (var5.func_149740_M()) {
                  this.func_175666_e(var1, var5);
               }
            }

            return true;
         }
      }
   }

   public boolean func_175698_g(BlockPos var1) {
      return this.func_180501_a(var1, Blocks.field_150350_a.func_176223_P(), 3);
   }

   public boolean func_175655_b(BlockPos var1, boolean var2) {
      IBlockState var3 = this.func_180495_p(var1);
      Block var4 = var3.func_177230_c();
      if (var4.func_149688_o() == Material.field_151579_a) {
         return false;
      } else {
         this.func_175718_b(2001, var1, Block.func_176210_f(var3));
         if (var2) {
            var4.func_176226_b(this, var1, var3, 0);
         }

         return this.func_180501_a(var1, Blocks.field_150350_a.func_176223_P(), 3);
      }
   }

   public boolean func_175656_a(BlockPos var1, IBlockState var2) {
      return this.func_180501_a(var1, var2, 3);
   }

   public void func_175689_h(BlockPos var1) {
      for(int var2 = 0; var2 < this.field_73021_x.size(); ++var2) {
         ((IWorldAccess)this.field_73021_x.get(var2)).func_174960_a(var1);
      }

   }

   public void func_175722_b(BlockPos var1, Block var2) {
      if (this.field_72986_A.func_76067_t() != WorldType.field_180272_g) {
         this.func_175685_c(var1, var2);
      }

   }

   public void func_72975_g(int var1, int var2, int var3, int var4) {
      int var5;
      if (var3 > var4) {
         var5 = var4;
         var4 = var3;
         var3 = var5;
      }

      if (!this.field_73011_w.func_177495_o()) {
         for(var5 = var3; var5 <= var4; ++var5) {
            this.func_180500_c(EnumSkyBlock.SKY, new BlockPos(var1, var5, var2));
         }
      }

      this.func_147458_c(var1, var3, var2, var1, var4, var2);
   }

   public void func_175704_b(BlockPos var1, BlockPos var2) {
      this.func_147458_c(var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p(), var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p());
   }

   public void func_147458_c(int var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = 0; var7 < this.field_73021_x.size(); ++var7) {
         ((IWorldAccess)this.field_73021_x.get(var7)).func_147585_a(var1, var2, var3, var4, var5, var6);
      }

   }

   public void func_175685_c(BlockPos var1, Block var2) {
      this.func_180496_d(var1.func_177976_e(), var2);
      this.func_180496_d(var1.func_177974_f(), var2);
      this.func_180496_d(var1.func_177977_b(), var2);
      this.func_180496_d(var1.func_177984_a(), var2);
      this.func_180496_d(var1.func_177978_c(), var2);
      this.func_180496_d(var1.func_177968_d(), var2);
   }

   public void func_175695_a(BlockPos var1, Block var2, EnumFacing var3) {
      if (var3 != EnumFacing.WEST) {
         this.func_180496_d(var1.func_177976_e(), var2);
      }

      if (var3 != EnumFacing.EAST) {
         this.func_180496_d(var1.func_177974_f(), var2);
      }

      if (var3 != EnumFacing.DOWN) {
         this.func_180496_d(var1.func_177977_b(), var2);
      }

      if (var3 != EnumFacing.UP) {
         this.func_180496_d(var1.func_177984_a(), var2);
      }

      if (var3 != EnumFacing.NORTH) {
         this.func_180496_d(var1.func_177978_c(), var2);
      }

      if (var3 != EnumFacing.SOUTH) {
         this.func_180496_d(var1.func_177968_d(), var2);
      }

   }

   public void func_180496_d(BlockPos var1, final Block var2) {
      if (!this.field_72995_K) {
         IBlockState var3 = this.func_180495_p(var1);

         try {
            var3.func_177230_c().func_176204_a(this, var1, var3, var2);
         } catch (Throwable var7) {
            CrashReport var5 = CrashReport.func_85055_a(var7, "Exception while updating neighbours");
            CrashReportCategory var6 = var5.func_85058_a("Block being updated");
            var6.func_71500_a("Source block type", new Callable<String>() {
               public String call() throws Exception {
                  try {
                     return String.format("ID #%d (%s // %s)", Block.func_149682_b(var2), var2.func_149739_a(), var2.getClass().getCanonicalName());
                  } catch (Throwable var2x) {
                     return "ID #" + Block.func_149682_b(var2);
                  }
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            CrashReportCategory.func_175750_a(var6, var1, var3);
            throw new ReportedException(var5);
         }
      }
   }

   public boolean func_175691_a(BlockPos var1, Block var2) {
      return false;
   }

   public boolean func_175678_i(BlockPos var1) {
      return this.func_175726_f(var1).func_177444_d(var1);
   }

   public boolean func_175710_j(BlockPos var1) {
      if (var1.func_177956_o() >= this.func_181545_F()) {
         return this.func_175678_i(var1);
      } else {
         BlockPos var2 = new BlockPos(var1.func_177958_n(), this.func_181545_F(), var1.func_177952_p());
         if (!this.func_175678_i(var2)) {
            return false;
         } else {
            for(var2 = var2.func_177977_b(); var2.func_177956_o() > var1.func_177956_o(); var2 = var2.func_177977_b()) {
               Block var3 = this.func_180495_p(var2).func_177230_c();
               if (var3.func_149717_k() > 0 && !var3.func_149688_o().func_76224_d()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int func_175699_k(BlockPos var1) {
      if (var1.func_177956_o() < 0) {
         return 0;
      } else {
         if (var1.func_177956_o() >= 256) {
            var1 = new BlockPos(var1.func_177958_n(), 255, var1.func_177952_p());
         }

         return this.func_175726_f(var1).func_177443_a(var1, 0);
      }
   }

   public int func_175671_l(BlockPos var1) {
      return this.func_175721_c(var1, true);
   }

   public int func_175721_c(BlockPos var1, boolean var2) {
      if (var1.func_177958_n() >= -30000000 && var1.func_177952_p() >= -30000000 && var1.func_177958_n() < 30000000 && var1.func_177952_p() < 30000000) {
         if (var2 && this.func_180495_p(var1).func_177230_c().func_149710_n()) {
            int var8 = this.func_175721_c(var1.func_177984_a(), false);
            int var4 = this.func_175721_c(var1.func_177974_f(), false);
            int var5 = this.func_175721_c(var1.func_177976_e(), false);
            int var6 = this.func_175721_c(var1.func_177968_d(), false);
            int var7 = this.func_175721_c(var1.func_177978_c(), false);
            if (var4 > var8) {
               var8 = var4;
            }

            if (var5 > var8) {
               var8 = var5;
            }

            if (var6 > var8) {
               var8 = var6;
            }

            if (var7 > var8) {
               var8 = var7;
            }

            return var8;
         } else if (var1.func_177956_o() < 0) {
            return 0;
         } else {
            if (var1.func_177956_o() >= 256) {
               var1 = new BlockPos(var1.func_177958_n(), 255, var1.func_177952_p());
            }

            Chunk var3 = this.func_175726_f(var1);
            return var3.func_177443_a(var1, this.field_73008_k);
         }
      } else {
         return 15;
      }
   }

   public BlockPos func_175645_m(BlockPos var1) {
      int var2;
      if (var1.func_177958_n() >= -30000000 && var1.func_177952_p() >= -30000000 && var1.func_177958_n() < 30000000 && var1.func_177952_p() < 30000000) {
         if (this.func_175680_a(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4, true)) {
            var2 = this.func_72964_e(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4).func_76611_b(var1.func_177958_n() & 15, var1.func_177952_p() & 15);
         } else {
            var2 = 0;
         }
      } else {
         var2 = this.func_181545_F() + 1;
      }

      return new BlockPos(var1.func_177958_n(), var2, var1.func_177952_p());
   }

   public int func_82734_g(int var1, int var2) {
      if (var1 >= -30000000 && var2 >= -30000000 && var1 < 30000000 && var2 < 30000000) {
         if (!this.func_175680_a(var1 >> 4, var2 >> 4, true)) {
            return 0;
         } else {
            Chunk var3 = this.func_72964_e(var1 >> 4, var2 >> 4);
            return var3.func_177442_v();
         }
      } else {
         return this.func_181545_F() + 1;
      }
   }

   public int func_175705_a(EnumSkyBlock var1, BlockPos var2) {
      if (this.field_73011_w.func_177495_o() && var1 == EnumSkyBlock.SKY) {
         return 0;
      } else {
         if (var2.func_177956_o() < 0) {
            var2 = new BlockPos(var2.func_177958_n(), 0, var2.func_177952_p());
         }

         if (!this.func_175701_a(var2)) {
            return var1.field_77198_c;
         } else if (!this.func_175667_e(var2)) {
            return var1.field_77198_c;
         } else if (this.func_180495_p(var2).func_177230_c().func_149710_n()) {
            int var8 = this.func_175642_b(var1, var2.func_177984_a());
            int var4 = this.func_175642_b(var1, var2.func_177974_f());
            int var5 = this.func_175642_b(var1, var2.func_177976_e());
            int var6 = this.func_175642_b(var1, var2.func_177968_d());
            int var7 = this.func_175642_b(var1, var2.func_177978_c());
            if (var4 > var8) {
               var8 = var4;
            }

            if (var5 > var8) {
               var8 = var5;
            }

            if (var6 > var8) {
               var8 = var6;
            }

            if (var7 > var8) {
               var8 = var7;
            }

            return var8;
         } else {
            Chunk var3 = this.func_175726_f(var2);
            return var3.func_177413_a(var1, var2);
         }
      }
   }

   public int func_175642_b(EnumSkyBlock var1, BlockPos var2) {
      if (var2.func_177956_o() < 0) {
         var2 = new BlockPos(var2.func_177958_n(), 0, var2.func_177952_p());
      }

      if (!this.func_175701_a(var2)) {
         return var1.field_77198_c;
      } else if (!this.func_175667_e(var2)) {
         return var1.field_77198_c;
      } else {
         Chunk var3 = this.func_175726_f(var2);
         return var3.func_177413_a(var1, var2);
      }
   }

   public void func_175653_a(EnumSkyBlock var1, BlockPos var2, int var3) {
      if (this.func_175701_a(var2)) {
         if (this.func_175667_e(var2)) {
            Chunk var4 = this.func_175726_f(var2);
            var4.func_177431_a(var1, var2, var3);
            this.func_175679_n(var2);
         }
      }
   }

   public void func_175679_n(BlockPos var1) {
      for(int var2 = 0; var2 < this.field_73021_x.size(); ++var2) {
         ((IWorldAccess)this.field_73021_x.get(var2)).func_174959_b(var1);
      }

   }

   public int func_175626_b(BlockPos var1, int var2) {
      int var3 = this.func_175705_a(EnumSkyBlock.SKY, var1);
      int var4 = this.func_175705_a(EnumSkyBlock.BLOCK, var1);
      if (var4 < var2) {
         var4 = var2;
      }

      return var3 << 20 | var4 << 4;
   }

   public float func_175724_o(BlockPos var1) {
      return this.field_73011_w.func_177497_p()[this.func_175671_l(var1)];
   }

   public IBlockState func_180495_p(BlockPos var1) {
      if (!this.func_175701_a(var1)) {
         return Blocks.field_150350_a.func_176223_P();
      } else {
         Chunk var2 = this.func_175726_f(var1);
         return var2.func_177435_g(var1);
      }
   }

   public boolean func_72935_r() {
      return this.field_73008_k < 4;
   }

   public MovingObjectPosition func_72933_a(Vec3 var1, Vec3 var2) {
      return this.func_147447_a(var1, var2, false, false, false);
   }

   public MovingObjectPosition func_72901_a(Vec3 var1, Vec3 var2, boolean var3) {
      return this.func_147447_a(var1, var2, var3, false, false);
   }

   public MovingObjectPosition func_147447_a(Vec3 var1, Vec3 var2, boolean var3, boolean var4, boolean var5) {
      if (!Double.isNaN(var1.field_72450_a) && !Double.isNaN(var1.field_72448_b) && !Double.isNaN(var1.field_72449_c)) {
         if (!Double.isNaN(var2.field_72450_a) && !Double.isNaN(var2.field_72448_b) && !Double.isNaN(var2.field_72449_c)) {
            int var6 = MathHelper.func_76128_c(var2.field_72450_a);
            int var7 = MathHelper.func_76128_c(var2.field_72448_b);
            int var8 = MathHelper.func_76128_c(var2.field_72449_c);
            int var9 = MathHelper.func_76128_c(var1.field_72450_a);
            int var10 = MathHelper.func_76128_c(var1.field_72448_b);
            int var11 = MathHelper.func_76128_c(var1.field_72449_c);
            BlockPos var12 = new BlockPos(var9, var10, var11);
            IBlockState var13 = this.func_180495_p(var12);
            Block var14 = var13.func_177230_c();
            if ((!var4 || var14.func_180640_a(this, var12, var13) != null) && var14.func_176209_a(var13, var3)) {
               MovingObjectPosition var15 = var14.func_180636_a(this, var12, var1, var2);
               if (var15 != null) {
                  return var15;
               }
            }

            MovingObjectPosition var40 = null;
            int var41 = 200;

            while(var41-- >= 0) {
               if (Double.isNaN(var1.field_72450_a) || Double.isNaN(var1.field_72448_b) || Double.isNaN(var1.field_72449_c)) {
                  return null;
               }

               if (var9 == var6 && var10 == var7 && var11 == var8) {
                  return var5 ? var40 : null;
               }

               boolean var42 = true;
               boolean var16 = true;
               boolean var17 = true;
               double var18 = 999.0D;
               double var20 = 999.0D;
               double var22 = 999.0D;
               if (var6 > var9) {
                  var18 = (double)var9 + 1.0D;
               } else if (var6 < var9) {
                  var18 = (double)var9 + 0.0D;
               } else {
                  var42 = false;
               }

               if (var7 > var10) {
                  var20 = (double)var10 + 1.0D;
               } else if (var7 < var10) {
                  var20 = (double)var10 + 0.0D;
               } else {
                  var16 = false;
               }

               if (var8 > var11) {
                  var22 = (double)var11 + 1.0D;
               } else if (var8 < var11) {
                  var22 = (double)var11 + 0.0D;
               } else {
                  var17 = false;
               }

               double var24 = 999.0D;
               double var26 = 999.0D;
               double var28 = 999.0D;
               double var30 = var2.field_72450_a - var1.field_72450_a;
               double var32 = var2.field_72448_b - var1.field_72448_b;
               double var34 = var2.field_72449_c - var1.field_72449_c;
               if (var42) {
                  var24 = (var18 - var1.field_72450_a) / var30;
               }

               if (var16) {
                  var26 = (var20 - var1.field_72448_b) / var32;
               }

               if (var17) {
                  var28 = (var22 - var1.field_72449_c) / var34;
               }

               if (var24 == -0.0D) {
                  var24 = -1.0E-4D;
               }

               if (var26 == -0.0D) {
                  var26 = -1.0E-4D;
               }

               if (var28 == -0.0D) {
                  var28 = -1.0E-4D;
               }

               EnumFacing var36;
               if (var24 < var26 && var24 < var28) {
                  var36 = var6 > var9 ? EnumFacing.WEST : EnumFacing.EAST;
                  var1 = new Vec3(var18, var1.field_72448_b + var32 * var24, var1.field_72449_c + var34 * var24);
               } else if (var26 < var28) {
                  var36 = var7 > var10 ? EnumFacing.DOWN : EnumFacing.UP;
                  var1 = new Vec3(var1.field_72450_a + var30 * var26, var20, var1.field_72449_c + var34 * var26);
               } else {
                  var36 = var8 > var11 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                  var1 = new Vec3(var1.field_72450_a + var30 * var28, var1.field_72448_b + var32 * var28, var22);
               }

               var9 = MathHelper.func_76128_c(var1.field_72450_a) - (var36 == EnumFacing.EAST ? 1 : 0);
               var10 = MathHelper.func_76128_c(var1.field_72448_b) - (var36 == EnumFacing.UP ? 1 : 0);
               var11 = MathHelper.func_76128_c(var1.field_72449_c) - (var36 == EnumFacing.SOUTH ? 1 : 0);
               var12 = new BlockPos(var9, var10, var11);
               IBlockState var37 = this.func_180495_p(var12);
               Block var38 = var37.func_177230_c();
               if (!var4 || var38.func_180640_a(this, var12, var37) != null) {
                  if (var38.func_176209_a(var37, var3)) {
                     MovingObjectPosition var39 = var38.func_180636_a(this, var12, var1, var2);
                     if (var39 != null) {
                        return var39;
                     }
                  } else {
                     var40 = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, var1, var36, var12);
                  }
               }
            }

            return var5 ? var40 : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void func_72956_a(Entity var1, String var2, float var3, float var4) {
      for(int var5 = 0; var5 < this.field_73021_x.size(); ++var5) {
         ((IWorldAccess)this.field_73021_x.get(var5)).func_72704_a(var2, var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var3, var4);
      }

   }

   public void func_85173_a(EntityPlayer var1, String var2, float var3, float var4) {
      for(int var5 = 0; var5 < this.field_73021_x.size(); ++var5) {
         ((IWorldAccess)this.field_73021_x.get(var5)).func_85102_a(var1, var2, var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var3, var4);
      }

   }

   public void func_72908_a(double var1, double var3, double var5, String var7, float var8, float var9) {
      for(int var10 = 0; var10 < this.field_73021_x.size(); ++var10) {
         ((IWorldAccess)this.field_73021_x.get(var10)).func_72704_a(var7, var1, var3, var5, var8, var9);
      }

   }

   public void func_72980_b(double var1, double var3, double var5, String var7, float var8, float var9, boolean var10) {
   }

   public void func_175717_a(BlockPos var1, String var2) {
      for(int var3 = 0; var3 < this.field_73021_x.size(); ++var3) {
         ((IWorldAccess)this.field_73021_x.get(var3)).func_174961_a(var2, var1);
      }

   }

   public void func_175688_a(EnumParticleTypes var1, double var2, double var4, double var6, double var8, double var10, double var12, int... var14) {
      this.func_175720_a(var1.func_179348_c(), var1.func_179344_e(), var2, var4, var6, var8, var10, var12, var14);
   }

   public void func_175682_a(EnumParticleTypes var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
      this.func_175720_a(var1.func_179348_c(), var1.func_179344_e() | var2, var3, var5, var7, var9, var11, var13, var15);
   }

   private void func_175720_a(int var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
      for(int var16 = 0; var16 < this.field_73021_x.size(); ++var16) {
         ((IWorldAccess)this.field_73021_x.get(var16)).func_180442_a(var1, var2, var3, var5, var7, var9, var11, var13, var15);
      }

   }

   public boolean func_72942_c(Entity var1) {
      this.field_73007_j.add(var1);
      return true;
   }

   public boolean func_72838_d(Entity var1) {
      int var2 = MathHelper.func_76128_c(var1.field_70165_t / 16.0D);
      int var3 = MathHelper.func_76128_c(var1.field_70161_v / 16.0D);
      boolean var4 = var1.field_98038_p;
      if (var1 instanceof EntityPlayer) {
         var4 = true;
      }

      if (!var4 && !this.func_175680_a(var2, var3, true)) {
         return false;
      } else {
         if (var1 instanceof EntityPlayer) {
            EntityPlayer var5 = (EntityPlayer)var1;
            this.field_73010_i.add(var5);
            this.func_72854_c();
         }

         this.func_72964_e(var2, var3).func_76612_a(var1);
         this.field_72996_f.add(var1);
         this.func_72923_a(var1);
         return true;
      }
   }

   protected void func_72923_a(Entity var1) {
      for(int var2 = 0; var2 < this.field_73021_x.size(); ++var2) {
         ((IWorldAccess)this.field_73021_x.get(var2)).func_72703_a(var1);
      }

   }

   protected void func_72847_b(Entity var1) {
      for(int var2 = 0; var2 < this.field_73021_x.size(); ++var2) {
         ((IWorldAccess)this.field_73021_x.get(var2)).func_72709_b(var1);
      }

   }

   public void func_72900_e(Entity var1) {
      if (var1.field_70153_n != null) {
         var1.field_70153_n.func_70078_a((Entity)null);
      }

      if (var1.field_70154_o != null) {
         var1.func_70078_a((Entity)null);
      }

      var1.func_70106_y();
      if (var1 instanceof EntityPlayer) {
         this.field_73010_i.remove(var1);
         this.func_72854_c();
         this.func_72847_b(var1);
      }

   }

   public void func_72973_f(Entity var1) {
      var1.func_70106_y();
      if (var1 instanceof EntityPlayer) {
         this.field_73010_i.remove(var1);
         this.func_72854_c();
      }

      int var2 = var1.field_70176_ah;
      int var3 = var1.field_70164_aj;
      if (var1.field_70175_ag && this.func_175680_a(var2, var3, true)) {
         this.func_72964_e(var2, var3).func_76622_b(var1);
      }

      this.field_72996_f.remove(var1);
      this.func_72847_b(var1);
   }

   public void func_72954_a(IWorldAccess var1) {
      this.field_73021_x.add(var1);
   }

   public void func_72848_b(IWorldAccess var1) {
      this.field_73021_x.remove(var1);
   }

   public List<AxisAlignedBB> func_72945_a(Entity var1, AxisAlignedBB var2) {
      ArrayList var3 = Lists.newArrayList();
      int var4 = MathHelper.func_76128_c(var2.field_72340_a);
      int var5 = MathHelper.func_76128_c(var2.field_72336_d + 1.0D);
      int var6 = MathHelper.func_76128_c(var2.field_72338_b);
      int var7 = MathHelper.func_76128_c(var2.field_72337_e + 1.0D);
      int var8 = MathHelper.func_76128_c(var2.field_72339_c);
      int var9 = MathHelper.func_76128_c(var2.field_72334_f + 1.0D);
      WorldBorder var10 = this.func_175723_af();
      boolean var11 = var1.func_174832_aS();
      boolean var12 = this.func_175673_a(var10, var1);
      IBlockState var13 = Blocks.field_150348_b.func_176223_P();
      BlockPos.MutableBlockPos var14 = new BlockPos.MutableBlockPos();

      for(int var15 = var4; var15 < var5; ++var15) {
         for(int var16 = var8; var16 < var9; ++var16) {
            if (this.func_175667_e(var14.func_181079_c(var15, 64, var16))) {
               for(int var17 = var6 - 1; var17 < var7; ++var17) {
                  var14.func_181079_c(var15, var17, var16);
                  if (var11 && var12) {
                     var1.func_174821_h(false);
                  } else if (!var11 && !var12) {
                     var1.func_174821_h(true);
                  }

                  IBlockState var18 = var13;
                  if (var10.func_177746_a(var14) || !var12) {
                     var18 = this.func_180495_p(var14);
                  }

                  var18.func_177230_c().func_180638_a(this, var14, var18, var2, var3, var1);
               }
            }
         }
      }

      double var20 = 0.25D;
      List var21 = this.func_72839_b(var1, var2.func_72314_b(var20, var20, var20));

      for(int var22 = 0; var22 < var21.size(); ++var22) {
         if (var1.field_70153_n != var21 && var1.field_70154_o != var21) {
            AxisAlignedBB var19 = ((Entity)var21.get(var22)).func_70046_E();
            if (var19 != null && var19.func_72326_a(var2)) {
               var3.add(var19);
            }

            var19 = var1.func_70114_g((Entity)var21.get(var22));
            if (var19 != null && var19.func_72326_a(var2)) {
               var3.add(var19);
            }
         }
      }

      return var3;
   }

   public boolean func_175673_a(WorldBorder var1, Entity var2) {
      double var3 = var1.func_177726_b();
      double var5 = var1.func_177736_c();
      double var7 = var1.func_177728_d();
      double var9 = var1.func_177733_e();
      if (var2.func_174832_aS()) {
         ++var3;
         ++var5;
         --var7;
         --var9;
      } else {
         --var3;
         --var5;
         ++var7;
         ++var9;
      }

      return var2.field_70165_t > var3 && var2.field_70165_t < var7 && var2.field_70161_v > var5 && var2.field_70161_v < var9;
   }

   public List<AxisAlignedBB> func_147461_a(AxisAlignedBB var1) {
      ArrayList var2 = Lists.newArrayList();
      int var3 = MathHelper.func_76128_c(var1.field_72340_a);
      int var4 = MathHelper.func_76128_c(var1.field_72336_d + 1.0D);
      int var5 = MathHelper.func_76128_c(var1.field_72338_b);
      int var6 = MathHelper.func_76128_c(var1.field_72337_e + 1.0D);
      int var7 = MathHelper.func_76128_c(var1.field_72339_c);
      int var8 = MathHelper.func_76128_c(var1.field_72334_f + 1.0D);
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      for(int var10 = var3; var10 < var4; ++var10) {
         for(int var11 = var7; var11 < var8; ++var11) {
            if (this.func_175667_e(var9.func_181079_c(var10, 64, var11))) {
               for(int var12 = var5 - 1; var12 < var6; ++var12) {
                  var9.func_181079_c(var10, var12, var11);
                  IBlockState var13;
                  if (var10 >= -30000000 && var10 < 30000000 && var11 >= -30000000 && var11 < 30000000) {
                     var13 = this.func_180495_p(var9);
                  } else {
                     var13 = Blocks.field_150357_h.func_176223_P();
                  }

                  var13.func_177230_c().func_180638_a(this, var9, var13, var1, var2, (Entity)null);
               }
            }
         }
      }

      return var2;
   }

   public int func_72967_a(float var1) {
      float var2 = this.func_72826_c(var1);
      float var3 = 1.0F - (MathHelper.func_76134_b(var2 * 3.1415927F * 2.0F) * 2.0F + 0.5F);
      var3 = MathHelper.func_76131_a(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0D - (double)(this.func_72867_j(var1) * 5.0F) / 16.0D));
      var3 = (float)((double)var3 * (1.0D - (double)(this.func_72819_i(var1) * 5.0F) / 16.0D));
      var3 = 1.0F - var3;
      return (int)(var3 * 11.0F);
   }

   public float func_72971_b(float var1) {
      float var2 = this.func_72826_c(var1);
      float var3 = 1.0F - (MathHelper.func_76134_b(var2 * 3.1415927F * 2.0F) * 2.0F + 0.2F);
      var3 = MathHelper.func_76131_a(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0D - (double)(this.func_72867_j(var1) * 5.0F) / 16.0D));
      var3 = (float)((double)var3 * (1.0D - (double)(this.func_72819_i(var1) * 5.0F) / 16.0D));
      return var3 * 0.8F + 0.2F;
   }

   public Vec3 func_72833_a(Entity var1, float var2) {
      float var3 = this.func_72826_c(var2);
      float var4 = MathHelper.func_76134_b(var3 * 3.1415927F * 2.0F) * 2.0F + 0.5F;
      var4 = MathHelper.func_76131_a(var4, 0.0F, 1.0F);
      int var5 = MathHelper.func_76128_c(var1.field_70165_t);
      int var6 = MathHelper.func_76128_c(var1.field_70163_u);
      int var7 = MathHelper.func_76128_c(var1.field_70161_v);
      BlockPos var8 = new BlockPos(var5, var6, var7);
      BiomeGenBase var9 = this.func_180494_b(var8);
      float var10 = var9.func_180626_a(var8);
      int var11 = var9.func_76731_a(var10);
      float var12 = (float)(var11 >> 16 & 255) / 255.0F;
      float var13 = (float)(var11 >> 8 & 255) / 255.0F;
      float var14 = (float)(var11 & 255) / 255.0F;
      var12 *= var4;
      var13 *= var4;
      var14 *= var4;
      float var15 = this.func_72867_j(var2);
      float var16;
      float var17;
      if (var15 > 0.0F) {
         var16 = (var12 * 0.3F + var13 * 0.59F + var14 * 0.11F) * 0.6F;
         var17 = 1.0F - var15 * 0.75F;
         var12 = var12 * var17 + var16 * (1.0F - var17);
         var13 = var13 * var17 + var16 * (1.0F - var17);
         var14 = var14 * var17 + var16 * (1.0F - var17);
      }

      var16 = this.func_72819_i(var2);
      if (var16 > 0.0F) {
         var17 = (var12 * 0.3F + var13 * 0.59F + var14 * 0.11F) * 0.2F;
         float var18 = 1.0F - var16 * 0.75F;
         var12 = var12 * var18 + var17 * (1.0F - var18);
         var13 = var13 * var18 + var17 * (1.0F - var18);
         var14 = var14 * var18 + var17 * (1.0F - var18);
      }

      if (this.field_73016_r > 0) {
         var17 = (float)this.field_73016_r - var2;
         if (var17 > 1.0F) {
            var17 = 1.0F;
         }

         var17 *= 0.45F;
         var12 = var12 * (1.0F - var17) + 0.8F * var17;
         var13 = var13 * (1.0F - var17) + 0.8F * var17;
         var14 = var14 * (1.0F - var17) + 1.0F * var17;
      }

      return new Vec3((double)var12, (double)var13, (double)var14);
   }

   public float func_72826_c(float var1) {
      return this.field_73011_w.func_76563_a(this.field_72986_A.func_76073_f(), var1);
   }

   public int func_72853_d() {
      return this.field_73011_w.func_76559_b(this.field_72986_A.func_76073_f());
   }

   public float func_130001_d() {
      return WorldProvider.field_111203_a[this.field_73011_w.func_76559_b(this.field_72986_A.func_76073_f())];
   }

   public float func_72929_e(float var1) {
      float var2 = this.func_72826_c(var1);
      return var2 * 3.1415927F * 2.0F;
   }

   public Vec3 func_72824_f(float var1) {
      float var2 = this.func_72826_c(var1);
      float var3 = MathHelper.func_76134_b(var2 * 3.1415927F * 2.0F) * 2.0F + 0.5F;
      var3 = MathHelper.func_76131_a(var3, 0.0F, 1.0F);
      float var4 = (float)(this.field_73001_c >> 16 & 255L) / 255.0F;
      float var5 = (float)(this.field_73001_c >> 8 & 255L) / 255.0F;
      float var6 = (float)(this.field_73001_c & 255L) / 255.0F;
      float var7 = this.func_72867_j(var1);
      float var8;
      float var9;
      if (var7 > 0.0F) {
         var8 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.6F;
         var9 = 1.0F - var7 * 0.95F;
         var4 = var4 * var9 + var8 * (1.0F - var9);
         var5 = var5 * var9 + var8 * (1.0F - var9);
         var6 = var6 * var9 + var8 * (1.0F - var9);
      }

      var4 *= var3 * 0.9F + 0.1F;
      var5 *= var3 * 0.9F + 0.1F;
      var6 *= var3 * 0.85F + 0.15F;
      var8 = this.func_72819_i(var1);
      if (var8 > 0.0F) {
         var9 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.2F;
         float var10 = 1.0F - var8 * 0.95F;
         var4 = var4 * var10 + var9 * (1.0F - var10);
         var5 = var5 * var10 + var9 * (1.0F - var10);
         var6 = var6 * var10 + var9 * (1.0F - var10);
      }

      return new Vec3((double)var4, (double)var5, (double)var6);
   }

   public Vec3 func_72948_g(float var1) {
      float var2 = this.func_72826_c(var1);
      return this.field_73011_w.func_76562_b(var2, var1);
   }

   public BlockPos func_175725_q(BlockPos var1) {
      return this.func_175726_f(var1).func_177440_h(var1);
   }

   public BlockPos func_175672_r(BlockPos var1) {
      Chunk var2 = this.func_175726_f(var1);

      BlockPos var3;
      BlockPos var4;
      for(var3 = new BlockPos(var1.func_177958_n(), var2.func_76625_h() + 16, var1.func_177952_p()); var3.func_177956_o() >= 0; var3 = var4) {
         var4 = var3.func_177977_b();
         Material var5 = var2.func_177428_a(var4).func_149688_o();
         if (var5.func_76230_c() && var5 != Material.field_151584_j) {
            break;
         }
      }

      return var3;
   }

   public float func_72880_h(float var1) {
      float var2 = this.func_72826_c(var1);
      float var3 = 1.0F - (MathHelper.func_76134_b(var2 * 3.1415927F * 2.0F) * 2.0F + 0.25F);
      var3 = MathHelper.func_76131_a(var3, 0.0F, 1.0F);
      return var3 * var3 * 0.5F;
   }

   public void func_175684_a(BlockPos var1, Block var2, int var3) {
   }

   public void func_175654_a(BlockPos var1, Block var2, int var3, int var4) {
   }

   public void func_180497_b(BlockPos var1, Block var2, int var3, int var4) {
   }

   public void func_72939_s() {
      this.field_72984_F.func_76320_a("entities");
      this.field_72984_F.func_76320_a("global");

      int var1;
      Entity var2;
      CrashReport var4;
      CrashReportCategory var5;
      for(var1 = 0; var1 < this.field_73007_j.size(); ++var1) {
         var2 = (Entity)this.field_73007_j.get(var1);

         try {
            ++var2.field_70173_aa;
            var2.func_70071_h_();
         } catch (Throwable var9) {
            var4 = CrashReport.func_85055_a(var9, "Ticking entity");
            var5 = var4.func_85058_a("Entity being ticked");
            if (var2 == null) {
               var5.func_71507_a("Entity", "~~NULL~~");
            } else {
               var2.func_85029_a(var5);
            }

            throw new ReportedException(var4);
         }

         if (var2.field_70128_L) {
            this.field_73007_j.remove(var1--);
         }
      }

      this.field_72984_F.func_76318_c("remove");
      this.field_72996_f.removeAll(this.field_72997_g);

      int var3;
      int var14;
      for(var1 = 0; var1 < this.field_72997_g.size(); ++var1) {
         var2 = (Entity)this.field_72997_g.get(var1);
         var3 = var2.field_70176_ah;
         var14 = var2.field_70164_aj;
         if (var2.field_70175_ag && this.func_175680_a(var3, var14, true)) {
            this.func_72964_e(var3, var14).func_76622_b(var2);
         }
      }

      for(var1 = 0; var1 < this.field_72997_g.size(); ++var1) {
         this.func_72847_b((Entity)this.field_72997_g.get(var1));
      }

      this.field_72997_g.clear();
      this.field_72984_F.func_76318_c("regular");

      for(var1 = 0; var1 < this.field_72996_f.size(); ++var1) {
         var2 = (Entity)this.field_72996_f.get(var1);
         if (var2.field_70154_o != null) {
            if (!var2.field_70154_o.field_70128_L && var2.field_70154_o.field_70153_n == var2) {
               continue;
            }

            var2.field_70154_o.field_70153_n = null;
            var2.field_70154_o = null;
         }

         this.field_72984_F.func_76320_a("tick");
         if (!var2.field_70128_L) {
            try {
               this.func_72870_g(var2);
            } catch (Throwable var8) {
               var4 = CrashReport.func_85055_a(var8, "Ticking entity");
               var5 = var4.func_85058_a("Entity being ticked");
               var2.func_85029_a(var5);
               throw new ReportedException(var4);
            }
         }

         this.field_72984_F.func_76319_b();
         this.field_72984_F.func_76320_a("remove");
         if (var2.field_70128_L) {
            var3 = var2.field_70176_ah;
            var14 = var2.field_70164_aj;
            if (var2.field_70175_ag && this.func_175680_a(var3, var14, true)) {
               this.func_72964_e(var3, var14).func_76622_b(var2);
            }

            this.field_72996_f.remove(var1--);
            this.func_72847_b(var2);
         }

         this.field_72984_F.func_76319_b();
      }

      this.field_72984_F.func_76318_c("blockEntities");
      this.field_147481_N = true;
      Iterator var15 = this.field_175730_i.iterator();

      while(var15.hasNext()) {
         TileEntity var10 = (TileEntity)var15.next();
         if (!var10.func_145837_r() && var10.func_145830_o()) {
            BlockPos var12 = var10.func_174877_v();
            if (this.func_175667_e(var12) && this.field_175728_M.func_177746_a(var12)) {
               try {
                  ((ITickable)var10).func_73660_a();
               } catch (Throwable var7) {
                  CrashReport var16 = CrashReport.func_85055_a(var7, "Ticking block entity");
                  CrashReportCategory var6 = var16.func_85058_a("Block entity being ticked");
                  var10.func_145828_a(var6);
                  throw new ReportedException(var16);
               }
            }
         }

         if (var10.func_145837_r()) {
            var15.remove();
            this.field_147482_g.remove(var10);
            if (this.func_175667_e(var10.func_174877_v())) {
               this.func_175726_f(var10.func_174877_v()).func_177425_e(var10.func_174877_v());
            }
         }
      }

      this.field_147481_N = false;
      if (!this.field_147483_b.isEmpty()) {
         this.field_175730_i.removeAll(this.field_147483_b);
         this.field_147482_g.removeAll(this.field_147483_b);
         this.field_147483_b.clear();
      }

      this.field_72984_F.func_76318_c("pendingBlockEntities");
      if (!this.field_147484_a.isEmpty()) {
         for(int var11 = 0; var11 < this.field_147484_a.size(); ++var11) {
            TileEntity var13 = (TileEntity)this.field_147484_a.get(var11);
            if (!var13.func_145837_r()) {
               if (!this.field_147482_g.contains(var13)) {
                  this.func_175700_a(var13);
               }

               if (this.func_175667_e(var13.func_174877_v())) {
                  this.func_175726_f(var13.func_174877_v()).func_177426_a(var13.func_174877_v(), var13);
               }

               this.func_175689_h(var13.func_174877_v());
            }
         }

         this.field_147484_a.clear();
      }

      this.field_72984_F.func_76319_b();
      this.field_72984_F.func_76319_b();
   }

   public boolean func_175700_a(TileEntity var1) {
      boolean var2 = this.field_147482_g.add(var1);
      if (var2 && var1 instanceof ITickable) {
         this.field_175730_i.add(var1);
      }

      return var2;
   }

   public void func_147448_a(Collection<TileEntity> var1) {
      if (this.field_147481_N) {
         this.field_147484_a.addAll(var1);
      } else {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            TileEntity var3 = (TileEntity)var2.next();
            this.field_147482_g.add(var3);
            if (var3 instanceof ITickable) {
               this.field_175730_i.add(var3);
            }
         }
      }

   }

   public void func_72870_g(Entity var1) {
      this.func_72866_a(var1, true);
   }

   public void func_72866_a(Entity var1, boolean var2) {
      int var3 = MathHelper.func_76128_c(var1.field_70165_t);
      int var4 = MathHelper.func_76128_c(var1.field_70161_v);
      byte var5 = 32;
      if (!var2 || this.func_175663_a(var3 - var5, 0, var4 - var5, var3 + var5, 0, var4 + var5, true)) {
         var1.field_70142_S = var1.field_70165_t;
         var1.field_70137_T = var1.field_70163_u;
         var1.field_70136_U = var1.field_70161_v;
         var1.field_70126_B = var1.field_70177_z;
         var1.field_70127_C = var1.field_70125_A;
         if (var2 && var1.field_70175_ag) {
            ++var1.field_70173_aa;
            if (var1.field_70154_o != null) {
               var1.func_70098_U();
            } else {
               var1.func_70071_h_();
            }
         }

         this.field_72984_F.func_76320_a("chunkCheck");
         if (Double.isNaN(var1.field_70165_t) || Double.isInfinite(var1.field_70165_t)) {
            var1.field_70165_t = var1.field_70142_S;
         }

         if (Double.isNaN(var1.field_70163_u) || Double.isInfinite(var1.field_70163_u)) {
            var1.field_70163_u = var1.field_70137_T;
         }

         if (Double.isNaN(var1.field_70161_v) || Double.isInfinite(var1.field_70161_v)) {
            var1.field_70161_v = var1.field_70136_U;
         }

         if (Double.isNaN((double)var1.field_70125_A) || Double.isInfinite((double)var1.field_70125_A)) {
            var1.field_70125_A = var1.field_70127_C;
         }

         if (Double.isNaN((double)var1.field_70177_z) || Double.isInfinite((double)var1.field_70177_z)) {
            var1.field_70177_z = var1.field_70126_B;
         }

         int var6 = MathHelper.func_76128_c(var1.field_70165_t / 16.0D);
         int var7 = MathHelper.func_76128_c(var1.field_70163_u / 16.0D);
         int var8 = MathHelper.func_76128_c(var1.field_70161_v / 16.0D);
         if (!var1.field_70175_ag || var1.field_70176_ah != var6 || var1.field_70162_ai != var7 || var1.field_70164_aj != var8) {
            if (var1.field_70175_ag && this.func_175680_a(var1.field_70176_ah, var1.field_70164_aj, true)) {
               this.func_72964_e(var1.field_70176_ah, var1.field_70164_aj).func_76608_a(var1, var1.field_70162_ai);
            }

            if (this.func_175680_a(var6, var8, true)) {
               var1.field_70175_ag = true;
               this.func_72964_e(var6, var8).func_76612_a(var1);
            } else {
               var1.field_70175_ag = false;
            }
         }

         this.field_72984_F.func_76319_b();
         if (var2 && var1.field_70175_ag && var1.field_70153_n != null) {
            if (!var1.field_70153_n.field_70128_L && var1.field_70153_n.field_70154_o == var1) {
               this.func_72870_g(var1.field_70153_n);
            } else {
               var1.field_70153_n.field_70154_o = null;
               var1.field_70153_n = null;
            }
         }

      }
   }

   public boolean func_72855_b(AxisAlignedBB var1) {
      return this.func_72917_a(var1, (Entity)null);
   }

   public boolean func_72917_a(AxisAlignedBB var1, Entity var2) {
      List var3 = this.func_72839_b((Entity)null, var1);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         Entity var5 = (Entity)var3.get(var4);
         if (!var5.field_70128_L && var5.field_70156_m && var5 != var2 && (var2 == null || var2.field_70154_o != var5 && var2.field_70153_n != var5)) {
            return false;
         }
      }

      return true;
   }

   public boolean func_72829_c(AxisAlignedBB var1) {
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76128_c(var1.field_72336_d);
      int var4 = MathHelper.func_76128_c(var1.field_72338_b);
      int var5 = MathHelper.func_76128_c(var1.field_72337_e);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76128_c(var1.field_72334_f);
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

      for(int var9 = var2; var9 <= var3; ++var9) {
         for(int var10 = var4; var10 <= var5; ++var10) {
            for(int var11 = var6; var11 <= var7; ++var11) {
               Block var12 = this.func_180495_p(var8.func_181079_c(var9, var10, var11)).func_177230_c();
               if (var12.func_149688_o() != Material.field_151579_a) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean func_72953_d(AxisAlignedBB var1) {
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76128_c(var1.field_72336_d);
      int var4 = MathHelper.func_76128_c(var1.field_72338_b);
      int var5 = MathHelper.func_76128_c(var1.field_72337_e);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76128_c(var1.field_72334_f);
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

      for(int var9 = var2; var9 <= var3; ++var9) {
         for(int var10 = var4; var10 <= var5; ++var10) {
            for(int var11 = var6; var11 <= var7; ++var11) {
               Block var12 = this.func_180495_p(var8.func_181079_c(var9, var10, var11)).func_177230_c();
               if (var12.func_149688_o().func_76224_d()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean func_147470_e(AxisAlignedBB var1) {
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76128_c(var1.field_72336_d + 1.0D);
      int var4 = MathHelper.func_76128_c(var1.field_72338_b);
      int var5 = MathHelper.func_76128_c(var1.field_72337_e + 1.0D);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76128_c(var1.field_72334_f + 1.0D);
      if (this.func_175663_a(var2, var4, var6, var3, var5, var7, true)) {
         BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

         for(int var9 = var2; var9 < var3; ++var9) {
            for(int var10 = var4; var10 < var5; ++var10) {
               for(int var11 = var6; var11 < var7; ++var11) {
                  Block var12 = this.func_180495_p(var8.func_181079_c(var9, var10, var11)).func_177230_c();
                  if (var12 == Blocks.field_150480_ab || var12 == Blocks.field_150356_k || var12 == Blocks.field_150353_l) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean func_72918_a(AxisAlignedBB var1, Material var2, Entity var3) {
      int var4 = MathHelper.func_76128_c(var1.field_72340_a);
      int var5 = MathHelper.func_76128_c(var1.field_72336_d + 1.0D);
      int var6 = MathHelper.func_76128_c(var1.field_72338_b);
      int var7 = MathHelper.func_76128_c(var1.field_72337_e + 1.0D);
      int var8 = MathHelper.func_76128_c(var1.field_72339_c);
      int var9 = MathHelper.func_76128_c(var1.field_72334_f + 1.0D);
      if (!this.func_175663_a(var4, var6, var8, var5, var7, var9, true)) {
         return false;
      } else {
         boolean var10 = false;
         Vec3 var11 = new Vec3(0.0D, 0.0D, 0.0D);
         BlockPos.MutableBlockPos var12 = new BlockPos.MutableBlockPos();

         for(int var13 = var4; var13 < var5; ++var13) {
            for(int var14 = var6; var14 < var7; ++var14) {
               for(int var15 = var8; var15 < var9; ++var15) {
                  var12.func_181079_c(var13, var14, var15);
                  IBlockState var16 = this.func_180495_p(var12);
                  Block var17 = var16.func_177230_c();
                  if (var17.func_149688_o() == var2) {
                     double var18 = (double)((float)(var14 + 1) - BlockLiquid.func_149801_b((Integer)var16.func_177229_b(BlockLiquid.field_176367_b)));
                     if ((double)var7 >= var18) {
                        var10 = true;
                        var11 = var17.func_176197_a(this, var12, var3, var11);
                     }
                  }
               }
            }
         }

         if (var11.func_72433_c() > 0.0D && var3.func_96092_aw()) {
            var11 = var11.func_72432_b();
            double var20 = 0.014D;
            var3.field_70159_w += var11.field_72450_a * var20;
            var3.field_70181_x += var11.field_72448_b * var20;
            var3.field_70179_y += var11.field_72449_c * var20;
         }

         return var10;
      }
   }

   public boolean func_72875_a(AxisAlignedBB var1, Material var2) {
      int var3 = MathHelper.func_76128_c(var1.field_72340_a);
      int var4 = MathHelper.func_76128_c(var1.field_72336_d + 1.0D);
      int var5 = MathHelper.func_76128_c(var1.field_72338_b);
      int var6 = MathHelper.func_76128_c(var1.field_72337_e + 1.0D);
      int var7 = MathHelper.func_76128_c(var1.field_72339_c);
      int var8 = MathHelper.func_76128_c(var1.field_72334_f + 1.0D);
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      for(int var10 = var3; var10 < var4; ++var10) {
         for(int var11 = var5; var11 < var6; ++var11) {
            for(int var12 = var7; var12 < var8; ++var12) {
               if (this.func_180495_p(var9.func_181079_c(var10, var11, var12)).func_177230_c().func_149688_o() == var2) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean func_72830_b(AxisAlignedBB var1, Material var2) {
      int var3 = MathHelper.func_76128_c(var1.field_72340_a);
      int var4 = MathHelper.func_76128_c(var1.field_72336_d + 1.0D);
      int var5 = MathHelper.func_76128_c(var1.field_72338_b);
      int var6 = MathHelper.func_76128_c(var1.field_72337_e + 1.0D);
      int var7 = MathHelper.func_76128_c(var1.field_72339_c);
      int var8 = MathHelper.func_76128_c(var1.field_72334_f + 1.0D);
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      for(int var10 = var3; var10 < var4; ++var10) {
         for(int var11 = var5; var11 < var6; ++var11) {
            for(int var12 = var7; var12 < var8; ++var12) {
               IBlockState var13 = this.func_180495_p(var9.func_181079_c(var10, var11, var12));
               Block var14 = var13.func_177230_c();
               if (var14.func_149688_o() == var2) {
                  int var15 = (Integer)var13.func_177229_b(BlockLiquid.field_176367_b);
                  double var16 = (double)(var11 + 1);
                  if (var15 < 8) {
                     var16 = (double)(var11 + 1) - (double)var15 / 8.0D;
                  }

                  if (var16 >= var1.field_72338_b) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public Explosion func_72876_a(Entity var1, double var2, double var4, double var6, float var8, boolean var9) {
      return this.func_72885_a(var1, var2, var4, var6, var8, false, var9);
   }

   public Explosion func_72885_a(Entity var1, double var2, double var4, double var6, float var8, boolean var9, boolean var10) {
      Explosion var11 = new Explosion(this, var1, var2, var4, var6, var8, var9, var10);
      var11.func_77278_a();
      var11.func_77279_a(true);
      return var11;
   }

   public float func_72842_a(Vec3 var1, AxisAlignedBB var2) {
      double var3 = 1.0D / ((var2.field_72336_d - var2.field_72340_a) * 2.0D + 1.0D);
      double var5 = 1.0D / ((var2.field_72337_e - var2.field_72338_b) * 2.0D + 1.0D);
      double var7 = 1.0D / ((var2.field_72334_f - var2.field_72339_c) * 2.0D + 1.0D);
      double var9 = (1.0D - Math.floor(1.0D / var3) * var3) / 2.0D;
      double var11 = (1.0D - Math.floor(1.0D / var7) * var7) / 2.0D;
      if (var3 >= 0.0D && var5 >= 0.0D && var7 >= 0.0D) {
         int var13 = 0;
         int var14 = 0;

         for(float var15 = 0.0F; var15 <= 1.0F; var15 = (float)((double)var15 + var3)) {
            for(float var16 = 0.0F; var16 <= 1.0F; var16 = (float)((double)var16 + var5)) {
               for(float var17 = 0.0F; var17 <= 1.0F; var17 = (float)((double)var17 + var7)) {
                  double var18 = var2.field_72340_a + (var2.field_72336_d - var2.field_72340_a) * (double)var15;
                  double var20 = var2.field_72338_b + (var2.field_72337_e - var2.field_72338_b) * (double)var16;
                  double var22 = var2.field_72339_c + (var2.field_72334_f - var2.field_72339_c) * (double)var17;
                  if (this.func_72933_a(new Vec3(var18 + var9, var20, var22 + var11), var1) == null) {
                     ++var13;
                  }

                  ++var14;
               }
            }
         }

         return (float)var13 / (float)var14;
      } else {
         return 0.0F;
      }
   }

   public boolean func_175719_a(EntityPlayer var1, BlockPos var2, EnumFacing var3) {
      var2 = var2.func_177972_a(var3);
      if (this.func_180495_p(var2).func_177230_c() == Blocks.field_150480_ab) {
         this.func_180498_a(var1, 1004, var2, 0);
         this.func_175698_g(var2);
         return true;
      } else {
         return false;
      }
   }

   public String func_72981_t() {
      return "All: " + this.field_72996_f.size();
   }

   public String func_72827_u() {
      return this.field_73020_y.func_73148_d();
   }

   public TileEntity func_175625_s(BlockPos var1) {
      if (!this.func_175701_a(var1)) {
         return null;
      } else {
         TileEntity var2 = null;
         int var3;
         TileEntity var4;
         if (this.field_147481_N) {
            for(var3 = 0; var3 < this.field_147484_a.size(); ++var3) {
               var4 = (TileEntity)this.field_147484_a.get(var3);
               if (!var4.func_145837_r() && var4.func_174877_v().equals(var1)) {
                  var2 = var4;
                  break;
               }
            }
         }

         if (var2 == null) {
            var2 = this.func_175726_f(var1).func_177424_a(var1, Chunk.EnumCreateEntityType.IMMEDIATE);
         }

         if (var2 == null) {
            for(var3 = 0; var3 < this.field_147484_a.size(); ++var3) {
               var4 = (TileEntity)this.field_147484_a.get(var3);
               if (!var4.func_145837_r() && var4.func_174877_v().equals(var1)) {
                  var2 = var4;
                  break;
               }
            }
         }

         return var2;
      }
   }

   public void func_175690_a(BlockPos var1, TileEntity var2) {
      if (var2 != null && !var2.func_145837_r()) {
         if (this.field_147481_N) {
            var2.func_174878_a(var1);
            Iterator var3 = this.field_147484_a.iterator();

            while(var3.hasNext()) {
               TileEntity var4 = (TileEntity)var3.next();
               if (var4.func_174877_v().equals(var1)) {
                  var4.func_145843_s();
                  var3.remove();
               }
            }

            this.field_147484_a.add(var2);
         } else {
            this.func_175700_a(var2);
            this.func_175726_f(var1).func_177426_a(var1, var2);
         }
      }

   }

   public void func_175713_t(BlockPos var1) {
      TileEntity var2 = this.func_175625_s(var1);
      if (var2 != null && this.field_147481_N) {
         var2.func_145843_s();
         this.field_147484_a.remove(var2);
      } else {
         if (var2 != null) {
            this.field_147484_a.remove(var2);
            this.field_147482_g.remove(var2);
            this.field_175730_i.remove(var2);
         }

         this.func_175726_f(var1).func_177425_e(var1);
      }

   }

   public void func_147457_a(TileEntity var1) {
      this.field_147483_b.add(var1);
   }

   public boolean func_175665_u(BlockPos var1) {
      IBlockState var2 = this.func_180495_p(var1);
      AxisAlignedBB var3 = var2.func_177230_c().func_180640_a(this, var1, var2);
      return var3 != null && var3.func_72320_b() >= 1.0D;
   }

   public static boolean func_175683_a(IBlockAccess var0, BlockPos var1) {
      IBlockState var2 = var0.func_180495_p(var1);
      Block var3 = var2.func_177230_c();
      if (var3.func_149688_o().func_76218_k() && var3.func_149686_d()) {
         return true;
      } else if (var3 instanceof BlockStairs) {
         return var2.func_177229_b(BlockStairs.field_176308_b) == BlockStairs.EnumHalf.TOP;
      } else if (var3 instanceof BlockSlab) {
         return var2.func_177229_b(BlockSlab.field_176554_a) == BlockSlab.EnumBlockHalf.TOP;
      } else if (var3 instanceof BlockHopper) {
         return true;
      } else if (var3 instanceof BlockSnow) {
         return (Integer)var2.func_177229_b(BlockSnow.field_176315_a) == 7;
      } else {
         return false;
      }
   }

   public boolean func_175677_d(BlockPos var1, boolean var2) {
      if (!this.func_175701_a(var1)) {
         return var2;
      } else {
         Chunk var3 = this.field_73020_y.func_177459_a(var1);
         if (var3.func_76621_g()) {
            return var2;
         } else {
            Block var4 = this.func_180495_p(var1).func_177230_c();
            return var4.func_149688_o().func_76218_k() && var4.func_149686_d();
         }
      }
   }

   public void func_72966_v() {
      int var1 = this.func_72967_a(1.0F);
      if (var1 != this.field_73008_k) {
         this.field_73008_k = var1;
      }

   }

   public void func_72891_a(boolean var1, boolean var2) {
      this.field_72985_G = var1;
      this.field_72992_H = var2;
   }

   public void func_72835_b() {
      this.func_72979_l();
   }

   protected void func_72947_a() {
      if (this.field_72986_A.func_76059_o()) {
         this.field_73004_o = 1.0F;
         if (this.field_72986_A.func_76061_m()) {
            this.field_73017_q = 1.0F;
         }
      }

   }

   protected void func_72979_l() {
      if (!this.field_73011_w.func_177495_o()) {
         if (!this.field_72995_K) {
            int var1 = this.field_72986_A.func_176133_A();
            if (var1 > 0) {
               --var1;
               this.field_72986_A.func_176142_i(var1);
               this.field_72986_A.func_76090_f(this.field_72986_A.func_76061_m() ? 1 : 2);
               this.field_72986_A.func_76080_g(this.field_72986_A.func_76059_o() ? 1 : 2);
            }

            int var2 = this.field_72986_A.func_76071_n();
            if (var2 <= 0) {
               if (this.field_72986_A.func_76061_m()) {
                  this.field_72986_A.func_76090_f(this.field_73012_v.nextInt(12000) + 3600);
               } else {
                  this.field_72986_A.func_76090_f(this.field_73012_v.nextInt(168000) + 12000);
               }
            } else {
               --var2;
               this.field_72986_A.func_76090_f(var2);
               if (var2 <= 0) {
                  this.field_72986_A.func_76069_a(!this.field_72986_A.func_76061_m());
               }
            }

            this.field_73018_p = this.field_73017_q;
            if (this.field_72986_A.func_76061_m()) {
               this.field_73017_q = (float)((double)this.field_73017_q + 0.01D);
            } else {
               this.field_73017_q = (float)((double)this.field_73017_q - 0.01D);
            }

            this.field_73017_q = MathHelper.func_76131_a(this.field_73017_q, 0.0F, 1.0F);
            int var3 = this.field_72986_A.func_76083_p();
            if (var3 <= 0) {
               if (this.field_72986_A.func_76059_o()) {
                  this.field_72986_A.func_76080_g(this.field_73012_v.nextInt(12000) + 12000);
               } else {
                  this.field_72986_A.func_76080_g(this.field_73012_v.nextInt(168000) + 12000);
               }
            } else {
               --var3;
               this.field_72986_A.func_76080_g(var3);
               if (var3 <= 0) {
                  this.field_72986_A.func_76084_b(!this.field_72986_A.func_76059_o());
               }
            }

            this.field_73003_n = this.field_73004_o;
            if (this.field_72986_A.func_76059_o()) {
               this.field_73004_o = (float)((double)this.field_73004_o + 0.01D);
            } else {
               this.field_73004_o = (float)((double)this.field_73004_o - 0.01D);
            }

            this.field_73004_o = MathHelper.func_76131_a(this.field_73004_o, 0.0F, 1.0F);
         }
      }
   }

   protected void func_72903_x() {
      this.field_72993_I.clear();
      this.field_72984_F.func_76320_a("buildList");

      int var1;
      EntityPlayer var2;
      int var3;
      int var4;
      int var5;
      for(var1 = 0; var1 < this.field_73010_i.size(); ++var1) {
         var2 = (EntityPlayer)this.field_73010_i.get(var1);
         var3 = MathHelper.func_76128_c(var2.field_70165_t / 16.0D);
         var4 = MathHelper.func_76128_c(var2.field_70161_v / 16.0D);
         var5 = this.func_152379_p();

         for(int var6 = -var5; var6 <= var5; ++var6) {
            for(int var7 = -var5; var7 <= var5; ++var7) {
               this.field_72993_I.add(new ChunkCoordIntPair(var6 + var3, var7 + var4));
            }
         }
      }

      this.field_72984_F.func_76319_b();
      if (this.field_72990_M > 0) {
         --this.field_72990_M;
      }

      this.field_72984_F.func_76320_a("playerCheckLight");
      if (!this.field_73010_i.isEmpty()) {
         var1 = this.field_73012_v.nextInt(this.field_73010_i.size());
         var2 = (EntityPlayer)this.field_73010_i.get(var1);
         var3 = MathHelper.func_76128_c(var2.field_70165_t) + this.field_73012_v.nextInt(11) - 5;
         var4 = MathHelper.func_76128_c(var2.field_70163_u) + this.field_73012_v.nextInt(11) - 5;
         var5 = MathHelper.func_76128_c(var2.field_70161_v) + this.field_73012_v.nextInt(11) - 5;
         this.func_175664_x(new BlockPos(var3, var4, var5));
      }

      this.field_72984_F.func_76319_b();
   }

   protected abstract int func_152379_p();

   protected void func_147467_a(int var1, int var2, Chunk var3) {
      this.field_72984_F.func_76318_c("moodSound");
      if (this.field_72990_M == 0 && !this.field_72995_K) {
         this.field_73005_l = this.field_73005_l * 3 + 1013904223;
         int var4 = this.field_73005_l >> 2;
         int var5 = var4 & 15;
         int var6 = var4 >> 8 & 15;
         int var7 = var4 >> 16 & 255;
         BlockPos var8 = new BlockPos(var5, var7, var6);
         Block var9 = var3.func_177428_a(var8);
         var5 += var1;
         var6 += var2;
         if (var9.func_149688_o() == Material.field_151579_a && this.func_175699_k(var8) <= this.field_73012_v.nextInt(8) && this.func_175642_b(EnumSkyBlock.SKY, var8) <= 0) {
            EntityPlayer var10 = this.func_72977_a((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D, 8.0D);
            if (var10 != null && var10.func_70092_e((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D) > 4.0D) {
               this.func_72908_a((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D, "ambient.cave.cave", 0.7F, 0.8F + this.field_73012_v.nextFloat() * 0.2F);
               this.field_72990_M = this.field_73012_v.nextInt(12000) + 6000;
            }
         }
      }

      this.field_72984_F.func_76318_c("checkLight");
      var3.func_76594_o();
   }

   protected void func_147456_g() {
      this.func_72903_x();
   }

   public void func_175637_a(Block var1, BlockPos var2, Random var3) {
      this.field_72999_e = true;
      var1.func_180650_b(this, var2, this.func_180495_p(var2), var3);
      this.field_72999_e = false;
   }

   public boolean func_175675_v(BlockPos var1) {
      return this.func_175670_e(var1, false);
   }

   public boolean func_175662_w(BlockPos var1) {
      return this.func_175670_e(var1, true);
   }

   public boolean func_175670_e(BlockPos var1, boolean var2) {
      BiomeGenBase var3 = this.func_180494_b(var1);
      float var4 = var3.func_180626_a(var1);
      if (var4 > 0.15F) {
         return false;
      } else {
         if (var1.func_177956_o() >= 0 && var1.func_177956_o() < 256 && this.func_175642_b(EnumSkyBlock.BLOCK, var1) < 10) {
            IBlockState var5 = this.func_180495_p(var1);
            Block var6 = var5.func_177230_c();
            if ((var6 == Blocks.field_150355_j || var6 == Blocks.field_150358_i) && (Integer)var5.func_177229_b(BlockLiquid.field_176367_b) == 0) {
               if (!var2) {
                  return true;
               }

               boolean var7 = this.func_175696_F(var1.func_177976_e()) && this.func_175696_F(var1.func_177974_f()) && this.func_175696_F(var1.func_177978_c()) && this.func_175696_F(var1.func_177968_d());
               if (!var7) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean func_175696_F(BlockPos var1) {
      return this.func_180495_p(var1).func_177230_c().func_149688_o() == Material.field_151586_h;
   }

   public boolean func_175708_f(BlockPos var1, boolean var2) {
      BiomeGenBase var3 = this.func_180494_b(var1);
      float var4 = var3.func_180626_a(var1);
      if (var4 > 0.15F) {
         return false;
      } else if (!var2) {
         return true;
      } else {
         if (var1.func_177956_o() >= 0 && var1.func_177956_o() < 256 && this.func_175642_b(EnumSkyBlock.BLOCK, var1) < 10) {
            Block var5 = this.func_180495_p(var1).func_177230_c();
            if (var5.func_149688_o() == Material.field_151579_a && Blocks.field_150431_aC.func_176196_c(this, var1)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean func_175664_x(BlockPos var1) {
      boolean var2 = false;
      if (!this.field_73011_w.func_177495_o()) {
         var2 |= this.func_180500_c(EnumSkyBlock.SKY, var1);
      }

      var2 |= this.func_180500_c(EnumSkyBlock.BLOCK, var1);
      return var2;
   }

   private int func_175638_a(BlockPos var1, EnumSkyBlock var2) {
      if (var2 == EnumSkyBlock.SKY && this.func_175678_i(var1)) {
         return 15;
      } else {
         Block var3 = this.func_180495_p(var1).func_177230_c();
         int var4 = var2 == EnumSkyBlock.SKY ? 0 : var3.func_149750_m();
         int var5 = var3.func_149717_k();
         if (var5 >= 15 && var3.func_149750_m() > 0) {
            var5 = 1;
         }

         if (var5 < 1) {
            var5 = 1;
         }

         if (var5 >= 15) {
            return 0;
         } else if (var4 >= 14) {
            return var4;
         } else {
            EnumFacing[] var6 = EnumFacing.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               EnumFacing var9 = var6[var8];
               BlockPos var10 = var1.func_177972_a(var9);
               int var11 = this.func_175642_b(var2, var10) - var5;
               if (var11 > var4) {
                  var4 = var11;
               }

               if (var4 >= 14) {
                  return var4;
               }
            }

            return var4;
         }
      }
   }

   public boolean func_180500_c(EnumSkyBlock var1, BlockPos var2) {
      if (!this.func_175648_a(var2, 17, false)) {
         return false;
      } else {
         int var3 = 0;
         int var4 = 0;
         this.field_72984_F.func_76320_a("getBrightness");
         int var5 = this.func_175642_b(var1, var2);
         int var6 = this.func_175638_a(var2, var1);
         int var7 = var2.func_177958_n();
         int var8 = var2.func_177956_o();
         int var9 = var2.func_177952_p();
         int var10;
         int var11;
         int var12;
         int var13;
         int var16;
         int var17;
         int var18;
         int var19;
         if (var6 > var5) {
            this.field_72994_J[var4++] = 133152;
         } else if (var6 < var5) {
            this.field_72994_J[var4++] = 133152 | var5 << 18;

            label90:
            while(true) {
               int var14;
               do {
                  do {
                     BlockPos var15;
                     do {
                        if (var3 >= var4) {
                           var3 = 0;
                           break label90;
                        }

                        var10 = this.field_72994_J[var3++];
                        var11 = (var10 & 63) - 32 + var7;
                        var12 = (var10 >> 6 & 63) - 32 + var8;
                        var13 = (var10 >> 12 & 63) - 32 + var9;
                        var14 = var10 >> 18 & 15;
                        var15 = new BlockPos(var11, var12, var13);
                        var16 = this.func_175642_b(var1, var15);
                     } while(var16 != var14);

                     this.func_175653_a(var1, var15, 0);
                  } while(var14 <= 0);

                  var17 = MathHelper.func_76130_a(var11 - var7);
                  var18 = MathHelper.func_76130_a(var12 - var8);
                  var19 = MathHelper.func_76130_a(var13 - var9);
               } while(var17 + var18 + var19 >= 17);

               BlockPos.MutableBlockPos var20 = new BlockPos.MutableBlockPos();
               EnumFacing[] var21 = EnumFacing.values();
               int var22 = var21.length;

               for(int var23 = 0; var23 < var22; ++var23) {
                  EnumFacing var24 = var21[var23];
                  int var25 = var11 + var24.func_82601_c();
                  int var26 = var12 + var24.func_96559_d();
                  int var27 = var13 + var24.func_82599_e();
                  var20.func_181079_c(var25, var26, var27);
                  int var28 = Math.max(1, this.func_180495_p(var20).func_177230_c().func_149717_k());
                  var16 = this.func_175642_b(var1, var20);
                  if (var16 == var14 - var28 && var4 < this.field_72994_J.length) {
                     this.field_72994_J[var4++] = var25 - var7 + 32 | var26 - var8 + 32 << 6 | var27 - var9 + 32 << 12 | var14 - var28 << 18;
                  }
               }
            }
         }

         this.field_72984_F.func_76319_b();
         this.field_72984_F.func_76320_a("checkedPosition < toCheckCount");

         while(var3 < var4) {
            var10 = this.field_72994_J[var3++];
            var11 = (var10 & 63) - 32 + var7;
            var12 = (var10 >> 6 & 63) - 32 + var8;
            var13 = (var10 >> 12 & 63) - 32 + var9;
            BlockPos var29 = new BlockPos(var11, var12, var13);
            int var30 = this.func_175642_b(var1, var29);
            var16 = this.func_175638_a(var29, var1);
            if (var16 != var30) {
               this.func_175653_a(var1, var29, var16);
               if (var16 > var30) {
                  var17 = Math.abs(var11 - var7);
                  var18 = Math.abs(var12 - var8);
                  var19 = Math.abs(var13 - var9);
                  boolean var31 = var4 < this.field_72994_J.length - 6;
                  if (var17 + var18 + var19 < 17 && var31) {
                     if (this.func_175642_b(var1, var29.func_177976_e()) < var16) {
                        this.field_72994_J[var4++] = var11 - 1 - var7 + 32 + (var12 - var8 + 32 << 6) + (var13 - var9 + 32 << 12);
                     }

                     if (this.func_175642_b(var1, var29.func_177974_f()) < var16) {
                        this.field_72994_J[var4++] = var11 + 1 - var7 + 32 + (var12 - var8 + 32 << 6) + (var13 - var9 + 32 << 12);
                     }

                     if (this.func_175642_b(var1, var29.func_177977_b()) < var16) {
                        this.field_72994_J[var4++] = var11 - var7 + 32 + (var12 - 1 - var8 + 32 << 6) + (var13 - var9 + 32 << 12);
                     }

                     if (this.func_175642_b(var1, var29.func_177984_a()) < var16) {
                        this.field_72994_J[var4++] = var11 - var7 + 32 + (var12 + 1 - var8 + 32 << 6) + (var13 - var9 + 32 << 12);
                     }

                     if (this.func_175642_b(var1, var29.func_177978_c()) < var16) {
                        this.field_72994_J[var4++] = var11 - var7 + 32 + (var12 - var8 + 32 << 6) + (var13 - 1 - var9 + 32 << 12);
                     }

                     if (this.func_175642_b(var1, var29.func_177968_d()) < var16) {
                        this.field_72994_J[var4++] = var11 - var7 + 32 + (var12 - var8 + 32 << 6) + (var13 + 1 - var9 + 32 << 12);
                     }
                  }
               }
            }
         }

         this.field_72984_F.func_76319_b();
         return true;
      }
   }

   public boolean func_72955_a(boolean var1) {
      return false;
   }

   public List<NextTickListEntry> func_72920_a(Chunk var1, boolean var2) {
      return null;
   }

   public List<NextTickListEntry> func_175712_a(StructureBoundingBox var1, boolean var2) {
      return null;
   }

   public List<Entity> func_72839_b(Entity var1, AxisAlignedBB var2) {
      return this.func_175674_a(var1, var2, EntitySelectors.field_180132_d);
   }

   public List<Entity> func_175674_a(Entity var1, AxisAlignedBB var2, Predicate<? super Entity> var3) {
      ArrayList var4 = Lists.newArrayList();
      int var5 = MathHelper.func_76128_c((var2.field_72340_a - 2.0D) / 16.0D);
      int var6 = MathHelper.func_76128_c((var2.field_72336_d + 2.0D) / 16.0D);
      int var7 = MathHelper.func_76128_c((var2.field_72339_c - 2.0D) / 16.0D);
      int var8 = MathHelper.func_76128_c((var2.field_72334_f + 2.0D) / 16.0D);

      for(int var9 = var5; var9 <= var6; ++var9) {
         for(int var10 = var7; var10 <= var8; ++var10) {
            if (this.func_175680_a(var9, var10, true)) {
               this.func_72964_e(var9, var10).func_177414_a(var1, var2, var4, var3);
            }
         }
      }

      return var4;
   }

   public <T extends Entity> List<T> func_175644_a(Class<? extends T> var1, Predicate<? super T> var2) {
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = this.field_72996_f.iterator();

      while(var4.hasNext()) {
         Entity var5 = (Entity)var4.next();
         if (var1.isAssignableFrom(var5.getClass()) && var2.apply(var5)) {
            var3.add(var5);
         }
      }

      return var3;
   }

   public <T extends Entity> List<T> func_175661_b(Class<? extends T> var1, Predicate<? super T> var2) {
      ArrayList var3 = Lists.newArrayList();
      Iterator var4 = this.field_73010_i.iterator();

      while(var4.hasNext()) {
         Entity var5 = (Entity)var4.next();
         if (var1.isAssignableFrom(var5.getClass()) && var2.apply(var5)) {
            var3.add(var5);
         }
      }

      return var3;
   }

   public <T extends Entity> List<T> func_72872_a(Class<? extends T> var1, AxisAlignedBB var2) {
      return this.func_175647_a(var1, var2, EntitySelectors.field_180132_d);
   }

   public <T extends Entity> List<T> func_175647_a(Class<? extends T> var1, AxisAlignedBB var2, Predicate<? super T> var3) {
      int var4 = MathHelper.func_76128_c((var2.field_72340_a - 2.0D) / 16.0D);
      int var5 = MathHelper.func_76128_c((var2.field_72336_d + 2.0D) / 16.0D);
      int var6 = MathHelper.func_76128_c((var2.field_72339_c - 2.0D) / 16.0D);
      int var7 = MathHelper.func_76128_c((var2.field_72334_f + 2.0D) / 16.0D);
      ArrayList var8 = Lists.newArrayList();

      for(int var9 = var4; var9 <= var5; ++var9) {
         for(int var10 = var6; var10 <= var7; ++var10) {
            if (this.func_175680_a(var9, var10, true)) {
               this.func_72964_e(var9, var10).func_177430_a(var1, var2, var8, var3);
            }
         }
      }

      return var8;
   }

   public <T extends Entity> T func_72857_a(Class<? extends T> var1, AxisAlignedBB var2, T var3) {
      List var4 = this.func_72872_a(var1, var2);
      Entity var5 = null;
      double var6 = 1.7976931348623157E308D;

      for(int var8 = 0; var8 < var4.size(); ++var8) {
         Entity var9 = (Entity)var4.get(var8);
         if (var9 != var3 && EntitySelectors.field_180132_d.apply(var9)) {
            double var10 = var3.func_70068_e(var9);
            if (var10 <= var6) {
               var5 = var9;
               var6 = var10;
            }
         }
      }

      return var5;
   }

   public Entity func_73045_a(int var1) {
      return (Entity)this.field_175729_l.func_76041_a(var1);
   }

   public List<Entity> func_72910_y() {
      return this.field_72996_f;
   }

   public void func_175646_b(BlockPos var1, TileEntity var2) {
      if (this.func_175667_e(var1)) {
         this.func_175726_f(var1).func_76630_e();
      }

   }

   public int func_72907_a(Class<?> var1) {
      int var2 = 0;
      Iterator var3 = this.field_72996_f.iterator();

      while(true) {
         Entity var4;
         do {
            if (!var3.hasNext()) {
               return var2;
            }

            var4 = (Entity)var3.next();
         } while(var4 instanceof EntityLiving && ((EntityLiving)var4).func_104002_bU());

         if (var1.isAssignableFrom(var4.getClass())) {
            ++var2;
         }
      }
   }

   public void func_175650_b(Collection<Entity> var1) {
      this.field_72996_f.addAll(var1);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         this.func_72923_a(var3);
      }

   }

   public void func_175681_c(Collection<Entity> var1) {
      this.field_72997_g.addAll(var1);
   }

   public boolean func_175716_a(Block var1, BlockPos var2, boolean var3, EnumFacing var4, Entity var5, ItemStack var6) {
      Block var7 = this.func_180495_p(var2).func_177230_c();
      AxisAlignedBB var8 = var3 ? null : var1.func_180640_a(this, var2, var1.func_176223_P());
      if (var8 != null && !this.func_72917_a(var8, var5)) {
         return false;
      } else if (var7.func_149688_o() == Material.field_151594_q && var1 == Blocks.field_150467_bQ) {
         return true;
      } else {
         return var7.func_149688_o().func_76222_j() && var1.func_176193_a(this, var2, var4, var6);
      }
   }

   public int func_181545_F() {
      return this.field_181546_a;
   }

   public void func_181544_b(int var1) {
      this.field_181546_a = var1;
   }

   public int func_175627_a(BlockPos var1, EnumFacing var2) {
      IBlockState var3 = this.func_180495_p(var1);
      return var3.func_177230_c().func_176211_b(this, var1, var3, var2);
   }

   public WorldType func_175624_G() {
      return this.field_72986_A.func_76067_t();
   }

   public int func_175676_y(BlockPos var1) {
      byte var2 = 0;
      int var3 = Math.max(var2, this.func_175627_a(var1.func_177977_b(), EnumFacing.DOWN));
      if (var3 >= 15) {
         return var3;
      } else {
         var3 = Math.max(var3, this.func_175627_a(var1.func_177984_a(), EnumFacing.UP));
         if (var3 >= 15) {
            return var3;
         } else {
            var3 = Math.max(var3, this.func_175627_a(var1.func_177978_c(), EnumFacing.NORTH));
            if (var3 >= 15) {
               return var3;
            } else {
               var3 = Math.max(var3, this.func_175627_a(var1.func_177968_d(), EnumFacing.SOUTH));
               if (var3 >= 15) {
                  return var3;
               } else {
                  var3 = Math.max(var3, this.func_175627_a(var1.func_177976_e(), EnumFacing.WEST));
                  if (var3 >= 15) {
                     return var3;
                  } else {
                     var3 = Math.max(var3, this.func_175627_a(var1.func_177974_f(), EnumFacing.EAST));
                     return var3 >= 15 ? var3 : var3;
                  }
               }
            }
         }
      }
   }

   public boolean func_175709_b(BlockPos var1, EnumFacing var2) {
      return this.func_175651_c(var1, var2) > 0;
   }

   public int func_175651_c(BlockPos var1, EnumFacing var2) {
      IBlockState var3 = this.func_180495_p(var1);
      Block var4 = var3.func_177230_c();
      return var4.func_149721_r() ? this.func_175676_y(var1) : var4.func_180656_a(this, var1, var3, var2);
   }

   public boolean func_175640_z(BlockPos var1) {
      if (this.func_175651_c(var1.func_177977_b(), EnumFacing.DOWN) > 0) {
         return true;
      } else if (this.func_175651_c(var1.func_177984_a(), EnumFacing.UP) > 0) {
         return true;
      } else if (this.func_175651_c(var1.func_177978_c(), EnumFacing.NORTH) > 0) {
         return true;
      } else if (this.func_175651_c(var1.func_177968_d(), EnumFacing.SOUTH) > 0) {
         return true;
      } else if (this.func_175651_c(var1.func_177976_e(), EnumFacing.WEST) > 0) {
         return true;
      } else {
         return this.func_175651_c(var1.func_177974_f(), EnumFacing.EAST) > 0;
      }
   }

   public int func_175687_A(BlockPos var1) {
      int var2 = 0;
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing var6 = var3[var5];
         int var7 = this.func_175651_c(var1.func_177972_a(var6), var6);
         if (var7 >= 15) {
            return 15;
         }

         if (var7 > var2) {
            var2 = var7;
         }
      }

      return var2;
   }

   public EntityPlayer func_72890_a(Entity var1, double var2) {
      return this.func_72977_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var2);
   }

   public EntityPlayer func_72977_a(double var1, double var3, double var5, double var7) {
      double var9 = -1.0D;
      EntityPlayer var11 = null;

      for(int var12 = 0; var12 < this.field_73010_i.size(); ++var12) {
         EntityPlayer var13 = (EntityPlayer)this.field_73010_i.get(var12);
         if (EntitySelectors.field_180132_d.apply(var13)) {
            double var14 = var13.func_70092_e(var1, var3, var5);
            if ((var7 < 0.0D || var14 < var7 * var7) && (var9 == -1.0D || var14 < var9)) {
               var9 = var14;
               var11 = var13;
            }
         }
      }

      return var11;
   }

   public boolean func_175636_b(double var1, double var3, double var5, double var7) {
      for(int var9 = 0; var9 < this.field_73010_i.size(); ++var9) {
         EntityPlayer var10 = (EntityPlayer)this.field_73010_i.get(var9);
         if (EntitySelectors.field_180132_d.apply(var10)) {
            double var11 = var10.func_70092_e(var1, var3, var5);
            if (var7 < 0.0D || var11 < var7 * var7) {
               return true;
            }
         }
      }

      return false;
   }

   public EntityPlayer func_72924_a(String var1) {
      for(int var2 = 0; var2 < this.field_73010_i.size(); ++var2) {
         EntityPlayer var3 = (EntityPlayer)this.field_73010_i.get(var2);
         if (var1.equals(var3.func_70005_c_())) {
            return var3;
         }
      }

      return null;
   }

   public EntityPlayer func_152378_a(UUID var1) {
      for(int var2 = 0; var2 < this.field_73010_i.size(); ++var2) {
         EntityPlayer var3 = (EntityPlayer)this.field_73010_i.get(var2);
         if (var1.equals(var3.func_110124_au())) {
            return var3;
         }
      }

      return null;
   }

   public void func_72882_A() {
   }

   public void func_72906_B() throws MinecraftException {
      this.field_73019_z.func_75762_c();
   }

   public void func_82738_a(long var1) {
      this.field_72986_A.func_82572_b(var1);
   }

   public long func_72905_C() {
      return this.field_72986_A.func_76063_b();
   }

   public long func_82737_E() {
      return this.field_72986_A.func_82573_f();
   }

   public long func_72820_D() {
      return this.field_72986_A.func_76073_f();
   }

   public void func_72877_b(long var1) {
      this.field_72986_A.func_76068_b(var1);
   }

   public BlockPos func_175694_M() {
      BlockPos var1 = new BlockPos(this.field_72986_A.func_76079_c(), this.field_72986_A.func_76075_d(), this.field_72986_A.func_76074_e());
      if (!this.func_175723_af().func_177746_a(var1)) {
         var1 = this.func_175645_m(new BlockPos(this.func_175723_af().func_177731_f(), 0.0D, this.func_175723_af().func_177721_g()));
      }

      return var1;
   }

   public void func_175652_B(BlockPos var1) {
      this.field_72986_A.func_176143_a(var1);
   }

   public void func_72897_h(Entity var1) {
      int var2 = MathHelper.func_76128_c(var1.field_70165_t / 16.0D);
      int var3 = MathHelper.func_76128_c(var1.field_70161_v / 16.0D);
      byte var4 = 2;

      for(int var5 = var2 - var4; var5 <= var2 + var4; ++var5) {
         for(int var6 = var3 - var4; var6 <= var3 + var4; ++var6) {
            this.func_72964_e(var5, var6);
         }
      }

      if (!this.field_72996_f.contains(var1)) {
         this.field_72996_f.add(var1);
      }

   }

   public boolean func_175660_a(EntityPlayer var1, BlockPos var2) {
      return true;
   }

   public void func_72960_a(Entity var1, byte var2) {
   }

   public IChunkProvider func_72863_F() {
      return this.field_73020_y;
   }

   public void func_175641_c(BlockPos var1, Block var2, int var3, int var4) {
      var2.func_180648_a(this, var1, this.func_180495_p(var1), var3, var4);
   }

   public ISaveHandler func_72860_G() {
      return this.field_73019_z;
   }

   public WorldInfo func_72912_H() {
      return this.field_72986_A;
   }

   public GameRules func_82736_K() {
      return this.field_72986_A.func_82574_x();
   }

   public void func_72854_c() {
   }

   public float func_72819_i(float var1) {
      return (this.field_73018_p + (this.field_73017_q - this.field_73018_p) * var1) * this.func_72867_j(var1);
   }

   public void func_147442_i(float var1) {
      this.field_73018_p = var1;
      this.field_73017_q = var1;
   }

   public float func_72867_j(float var1) {
      return this.field_73003_n + (this.field_73004_o - this.field_73003_n) * var1;
   }

   public void func_72894_k(float var1) {
      this.field_73003_n = var1;
      this.field_73004_o = var1;
   }

   public boolean func_72911_I() {
      return (double)this.func_72819_i(1.0F) > 0.9D;
   }

   public boolean func_72896_J() {
      return (double)this.func_72867_j(1.0F) > 0.2D;
   }

   public boolean func_175727_C(BlockPos var1) {
      if (!this.func_72896_J()) {
         return false;
      } else if (!this.func_175678_i(var1)) {
         return false;
      } else if (this.func_175725_q(var1).func_177956_o() > var1.func_177956_o()) {
         return false;
      } else {
         BiomeGenBase var2 = this.func_180494_b(var1);
         if (var2.func_76746_c()) {
            return false;
         } else {
            return this.func_175708_f(var1, false) ? false : var2.func_76738_d();
         }
      }
   }

   public boolean func_180502_D(BlockPos var1) {
      BiomeGenBase var2 = this.func_180494_b(var1);
      return var2.func_76736_e();
   }

   public MapStorage func_175693_T() {
      return this.field_72988_C;
   }

   public void func_72823_a(String var1, WorldSavedData var2) {
      this.field_72988_C.func_75745_a(var1, var2);
   }

   public WorldSavedData func_72943_a(Class<? extends WorldSavedData> var1, String var2) {
      return this.field_72988_C.func_75742_a(var1, var2);
   }

   public int func_72841_b(String var1) {
      return this.field_72988_C.func_75743_a(var1);
   }

   public void func_175669_a(int var1, BlockPos var2, int var3) {
      for(int var4 = 0; var4 < this.field_73021_x.size(); ++var4) {
         ((IWorldAccess)this.field_73021_x.get(var4)).func_180440_a(var1, var2, var3);
      }

   }

   public void func_175718_b(int var1, BlockPos var2, int var3) {
      this.func_180498_a((EntityPlayer)null, var1, var2, var3);
   }

   public void func_180498_a(EntityPlayer var1, int var2, BlockPos var3, int var4) {
      try {
         for(int var5 = 0; var5 < this.field_73021_x.size(); ++var5) {
            ((IWorldAccess)this.field_73021_x.get(var5)).func_180439_a(var1, var2, var3, var4);
         }

      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.func_85055_a(var8, "Playing level event");
         CrashReportCategory var7 = var6.func_85058_a("Level event being played");
         var7.func_71507_a("Block coordinates", CrashReportCategory.func_180522_a(var3));
         var7.func_71507_a("Event source", var1);
         var7.func_71507_a("Event type", var2);
         var7.func_71507_a("Event data", var4);
         throw new ReportedException(var6);
      }
   }

   public int func_72800_K() {
      return 256;
   }

   public int func_72940_L() {
      return this.field_73011_w.func_177495_o() ? 128 : 256;
   }

   public Random func_72843_D(int var1, int var2, int var3) {
      long var4 = (long)var1 * 341873128712L + (long)var2 * 132897987541L + this.func_72912_H().func_76063_b() + (long)var3;
      this.field_73012_v.setSeed(var4);
      return this.field_73012_v;
   }

   public BlockPos func_180499_a(String var1, BlockPos var2) {
      return this.func_72863_F().func_180513_a(this, var1, var2);
   }

   public boolean func_72806_N() {
      return false;
   }

   public double func_72919_O() {
      return this.field_72986_A.func_76067_t() == WorldType.field_77138_c ? 0.0D : 63.0D;
   }

   public CrashReportCategory func_72914_a(CrashReport var1) {
      CrashReportCategory var2 = var1.func_85057_a("Affected level", 1);
      var2.func_71507_a("Level name", this.field_72986_A == null ? "????" : this.field_72986_A.func_76065_j());
      var2.func_71500_a("All players", new Callable<String>() {
         public String call() {
            return World.this.field_73010_i.size() + " total; " + World.this.field_73010_i.toString();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var2.func_71500_a("Chunk stats", new Callable<String>() {
         public String call() {
            return World.this.field_73020_y.func_73148_d();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });

      try {
         this.field_72986_A.func_85118_a(var2);
      } catch (Throwable var4) {
         var2.func_71499_a("Level Data Unobtainable", var4);
      }

      return var2;
   }

   public void func_175715_c(int var1, BlockPos var2, int var3) {
      for(int var4 = 0; var4 < this.field_73021_x.size(); ++var4) {
         IWorldAccess var5 = (IWorldAccess)this.field_73021_x.get(var4);
         var5.func_180441_b(var1, var2, var3);
      }

   }

   public Calendar func_83015_S() {
      if (this.func_82737_E() % 600L == 0L) {
         this.field_83016_L.setTimeInMillis(MinecraftServer.func_130071_aq());
      }

      return this.field_83016_L;
   }

   public void func_92088_a(double var1, double var3, double var5, double var7, double var9, double var11, NBTTagCompound var13) {
   }

   public Scoreboard func_96441_U() {
      return this.field_96442_D;
   }

   public void func_175666_e(BlockPos var1, Block var2) {
      Iterator var3 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var3.hasNext()) {
         EnumFacing var4 = (EnumFacing)var3.next();
         BlockPos var5 = var1.func_177972_a(var4);
         if (this.func_175667_e(var5)) {
            IBlockState var6 = this.func_180495_p(var5);
            if (Blocks.field_150441_bU.func_149907_e(var6.func_177230_c())) {
               var6.func_177230_c().func_176204_a(this, var5, var6, var2);
            } else if (var6.func_177230_c().func_149721_r()) {
               var5 = var5.func_177972_a(var4);
               var6 = this.func_180495_p(var5);
               if (Blocks.field_150441_bU.func_149907_e(var6.func_177230_c())) {
                  var6.func_177230_c().func_176204_a(this, var5, var6, var2);
               }
            }
         }
      }

   }

   public DifficultyInstance func_175649_E(BlockPos var1) {
      long var2 = 0L;
      float var4 = 0.0F;
      if (this.func_175667_e(var1)) {
         var4 = this.func_130001_d();
         var2 = this.func_175726_f(var1).func_177416_w();
      }

      return new DifficultyInstance(this.func_175659_aa(), this.func_72820_D(), var2, var4);
   }

   public EnumDifficulty func_175659_aa() {
      return this.func_72912_H().func_176130_y();
   }

   public int func_175657_ab() {
      return this.field_73008_k;
   }

   public void func_175692_b(int var1) {
      this.field_73008_k = var1;
   }

   public int func_175658_ac() {
      return this.field_73016_r;
   }

   public void func_175702_c(int var1) {
      this.field_73016_r = var1;
   }

   public boolean func_175686_ad() {
      return this.field_72987_B;
   }

   public VillageCollection func_175714_ae() {
      return this.field_72982_D;
   }

   public WorldBorder func_175723_af() {
      return this.field_175728_M;
   }

   public boolean func_72916_c(int var1, int var2) {
      BlockPos var3 = this.func_175694_M();
      int var4 = var1 * 16 + 8 - var3.func_177958_n();
      int var5 = var2 * 16 + 8 - var3.func_177952_p();
      short var6 = 128;
      return var4 >= -var6 && var4 <= var6 && var5 >= -var6 && var5 <= var6;
   }
}
