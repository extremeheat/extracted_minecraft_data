package net.minecraft.world;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

public abstract class World implements IBlockAccess {
   public boolean field_72999_e;
   public List field_72996_f = new ArrayList();
   protected List field_72997_g = new ArrayList();
   public List field_147482_g = new ArrayList();
   private List field_147484_a = new ArrayList();
   private List field_147483_b = new ArrayList();
   public List field_73010_i = new ArrayList();
   public List field_73007_j = new ArrayList();
   private long field_73001_c = 16777215L;
   public int field_73008_k;
   protected int field_73005_l = new Random().nextInt();
   protected final int field_73006_m = 1013904223;
   protected float field_73003_n;
   protected float field_73004_o;
   protected float field_73018_p;
   protected float field_73017_q;
   public int field_73016_r;
   public EnumDifficulty field_73013_u;
   public Random field_73012_v = new Random();
   public final WorldProvider field_73011_w;
   protected List field_73021_x = new ArrayList();
   protected IChunkProvider field_73020_y;
   protected final ISaveHandler field_73019_z;
   protected WorldInfo field_72986_A;
   public boolean field_72987_B;
   public MapStorage field_72988_C;
   public final VillageCollection field_72982_D;
   protected final VillageSiege field_72983_E = new VillageSiege(this);
   public final Profiler field_72984_F;
   private final Calendar field_83016_L = Calendar.getInstance();
   protected Scoreboard field_96442_D = new Scoreboard();
   public boolean field_72995_K;
   protected Set field_72993_I = new HashSet();
   private int field_72990_M = this.field_73012_v.nextInt(12000);
   protected boolean field_72985_G = true;
   protected boolean field_72992_H = true;
   private ArrayList field_72998_d = new ArrayList();
   private boolean field_147481_N;
   int[] field_72994_J = new int[32768];

   @Override
   public BiomeGenBase func_72807_a(int var1, int var2) {
      if (this.func_72899_e(var1, 0, var2)) {
         Chunk var3 = this.func_72938_d(var1, var2);

         try {
            return var3.func_76591_a(var1 & 15, var2 & 15, this.field_73011_w.field_76578_c);
         } catch (Throwable var7) {
            CrashReport var5 = CrashReport.func_85055_a(var7, "Getting biome");
            CrashReportCategory var6 = var5.func_85058_a("Coordinates of biome request");
            var6.func_71500_a("Location", new World$1(this, var1, var2));
            throw new ReportedException(var5);
         }
      } else {
         return this.field_73011_w.field_76578_c.func_76935_a(var1, var2);
      }
   }

   public WorldChunkManager func_72959_q() {
      return this.field_73011_w.field_76578_c;
   }

   public World(ISaveHandler var1, String var2, WorldProvider var3, WorldSettings var4, Profiler var5) {
      super();
      this.field_73019_z = var1;
      this.field_72984_F = var5;
      this.field_72986_A = new WorldInfo(var4, var2);
      this.field_73011_w = var3;
      this.field_72988_C = new MapStorage(var1);
      VillageCollection var6 = (VillageCollection)this.field_72988_C.func_75742_a(VillageCollection.class, "villages");
      if (var6 == null) {
         this.field_72982_D = new VillageCollection(this);
         this.field_72988_C.func_75745_a("villages", this.field_72982_D);
      } else {
         this.field_72982_D = var6;
         this.field_72982_D.func_82566_a(this);
      }

      var3.func_76558_a(this);
      this.field_73020_y = this.func_72970_h();
      this.func_72966_v();
      this.func_72947_a();
   }

   public World(ISaveHandler var1, String var2, WorldSettings var3, WorldProvider var4, Profiler var5) {
      super();
      this.field_73019_z = var1;
      this.field_72984_F = var5;
      this.field_72988_C = new MapStorage(var1);
      this.field_72986_A = var1.func_75757_d();
      if (var4 != null) {
         this.field_73011_w = var4;
      } else if (this.field_72986_A != null && this.field_72986_A.func_76076_i() != 0) {
         this.field_73011_w = WorldProvider.func_76570_a(this.field_72986_A.func_76076_i());
      } else {
         this.field_73011_w = WorldProvider.func_76570_a(0);
      }

      if (this.field_72986_A == null) {
         this.field_72986_A = new WorldInfo(var3, var2);
      } else {
         this.field_72986_A.func_76062_a(var2);
      }

      this.field_73011_w.func_76558_a(this);
      this.field_73020_y = this.func_72970_h();
      if (!this.field_72986_A.func_76070_v()) {
         try {
            this.func_72963_a(var3);
         } catch (Throwable var10) {
            CrashReport var7 = CrashReport.func_85055_a(var10, "Exception initializing level");

            try {
               this.func_72914_a(var7);
            } catch (Throwable var9) {
            }

            throw new ReportedException(var7);
         }

         this.field_72986_A.func_76091_d(true);
      }

      VillageCollection var6 = (VillageCollection)this.field_72988_C.func_75742_a(VillageCollection.class, "villages");
      if (var6 == null) {
         this.field_72982_D = new VillageCollection(this);
         this.field_72988_C.func_75745_a("villages", this.field_72982_D);
      } else {
         this.field_72982_D = var6;
         this.field_72982_D.func_82566_a(this);
      }

      this.func_72966_v();
      this.func_72947_a();
   }

   protected abstract IChunkProvider func_72970_h();

   protected void func_72963_a(WorldSettings var1) {
      this.field_72986_A.func_76091_d(true);
   }

   public void func_72974_f() {
      this.func_72950_A(8, 64, 8);
   }

   public Block func_147474_b(int var1, int var2) {
      int var3 = 63;

      while(!this.func_147437_c(var1, var3 + 1, var2)) {
         ++var3;
      }

      return this.func_147439_a(var1, var3, var2);
   }

   @Override
   public Block func_147439_a(int var1, int var2, int var3) {
      if (var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000 && var2 >= 0 && var2 < 256) {
         Chunk var4 = null;

         try {
            var4 = this.func_72964_e(var1 >> 4, var3 >> 4);
            return var4.func_150810_a(var1 & 15, var2, var3 & 15);
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.func_85055_a(var8, "Exception getting block type in world");
            CrashReportCategory var7 = var6.func_85058_a("Requested block coordinates");
            var7.func_71507_a("Found chunk", var4 == null);
            var7.func_71507_a("Location", CrashReportCategory.func_85071_a(var1, var2, var3));
            throw new ReportedException(var6);
         }
      } else {
         return Blocks.field_150350_a;
      }
   }

   @Override
   public boolean func_147437_c(int var1, int var2, int var3) {
      return this.func_147439_a(var1, var2, var3).func_149688_o() == Material.field_151579_a;
   }

   public boolean func_72899_e(int var1, int var2, int var3) {
      return var2 >= 0 && var2 < 256 ? this.func_72916_c(var1 >> 4, var3 >> 4) : false;
   }

   public boolean func_72873_a(int var1, int var2, int var3, int var4) {
      return this.func_72904_c(var1 - var4, var2 - var4, var3 - var4, var1 + var4, var2 + var4, var3 + var4);
   }

   public boolean func_72904_c(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var5 >= 0 && var2 < 256) {
         var1 >>= 4;
         var3 >>= 4;
         var4 >>= 4;
         var6 >>= 4;

         for(int var7 = var1; var7 <= var4; ++var7) {
            for(int var8 = var3; var8 <= var6; ++var8) {
               if (!this.func_72916_c(var7, var8)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean func_72916_c(int var1, int var2) {
      return this.field_73020_y.func_73149_a(var1, var2);
   }

   public Chunk func_72938_d(int var1, int var2) {
      return this.func_72964_e(var1 >> 4, var2 >> 4);
   }

   public Chunk func_72964_e(int var1, int var2) {
      return this.field_73020_y.func_73154_d(var1, var2);
   }

   public boolean func_147465_d(int var1, int var2, int var3, Block var4, int var5, int var6) {
      if (var1 < -30000000 || var3 < -30000000 || var1 >= 30000000 || var3 >= 30000000) {
         return false;
      } else if (var2 < 0) {
         return false;
      } else if (var2 >= 256) {
         return false;
      } else {
         Chunk var7 = this.func_72964_e(var1 >> 4, var3 >> 4);
         Block var8 = null;
         if ((var6 & 1) != 0) {
            var8 = var7.func_150810_a(var1 & 15, var2, var3 & 15);
         }

         boolean var9 = var7.func_150807_a(var1 & 15, var2, var3 & 15, var4, var5);
         this.field_72984_F.func_76320_a("checkLight");
         this.func_147451_t(var1, var2, var3);
         this.field_72984_F.func_76319_b();
         if (var9) {
            if ((var6 & 2) != 0 && (!this.field_72995_K || (var6 & 4) == 0) && var7.func_150802_k()) {
               this.func_147471_g(var1, var2, var3);
            }

            if (!this.field_72995_K && (var6 & 1) != 0) {
               this.func_147444_c(var1, var2, var3, var8);
               if (var4.func_149740_M()) {
                  this.func_147453_f(var1, var2, var3, var4);
               }
            }
         }

         return var9;
      }
   }

   @Override
   public int func_72805_g(int var1, int var2, int var3) {
      if (var1 < -30000000 || var3 < -30000000 || var1 >= 30000000 || var3 >= 30000000) {
         return 0;
      } else if (var2 < 0) {
         return 0;
      } else if (var2 >= 256) {
         return 0;
      } else {
         Chunk var4 = this.func_72964_e(var1 >> 4, var3 >> 4);
         var1 &= 15;
         var3 &= 15;
         return var4.func_76628_c(var1, var2, var3);
      }
   }

   public boolean func_72921_c(int var1, int var2, int var3, int var4, int var5) {
      if (var1 < -30000000 || var3 < -30000000 || var1 >= 30000000 || var3 >= 30000000) {
         return false;
      } else if (var2 < 0) {
         return false;
      } else if (var2 >= 256) {
         return false;
      } else {
         Chunk var6 = this.func_72964_e(var1 >> 4, var3 >> 4);
         int var7 = var1 & 15;
         int var8 = var3 & 15;
         boolean var9 = var6.func_76589_b(var7, var2, var8, var4);
         if (var9) {
            Block var10 = var6.func_150810_a(var7, var2, var8);
            if ((var5 & 2) != 0 && (!this.field_72995_K || (var5 & 4) == 0) && var6.func_150802_k()) {
               this.func_147471_g(var1, var2, var3);
            }

            if (!this.field_72995_K && (var5 & 1) != 0) {
               this.func_147444_c(var1, var2, var3, var10);
               if (var10.func_149740_M()) {
                  this.func_147453_f(var1, var2, var3, var10);
               }
            }
         }

         return var9;
      }
   }

   public boolean func_147468_f(int var1, int var2, int var3) {
      return this.func_147465_d(var1, var2, var3, Blocks.field_150350_a, 0, 3);
   }

   public boolean func_147480_a(int var1, int var2, int var3, boolean var4) {
      Block var5 = this.func_147439_a(var1, var2, var3);
      if (var5.func_149688_o() == Material.field_151579_a) {
         return false;
      } else {
         int var6 = this.func_72805_g(var1, var2, var3);
         this.func_72926_e(2001, var1, var2, var3, Block.func_149682_b(var5) + (var6 << 12));
         if (var4) {
            var5.func_149697_b(this, var1, var2, var3, var6, 0);
         }

         return this.func_147465_d(var1, var2, var3, Blocks.field_150350_a, 0, 3);
      }
   }

   public boolean func_147449_b(int var1, int var2, int var3, Block var4) {
      return this.func_147465_d(var1, var2, var3, var4, 0, 3);
   }

   public void func_147471_g(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.field_73021_x.size(); ++var4) {
         ((IWorldAccess)this.field_73021_x.get(var4)).func_147586_a(var1, var2, var3);
      }
   }

   public void func_147444_c(int var1, int var2, int var3, Block var4) {
      this.func_147459_d(var1, var2, var3, var4);
   }

   public void func_72975_g(int var1, int var2, int var3, int var4) {
      if (var3 > var4) {
         int var5 = var4;
         var4 = var3;
         var3 = var5;
      }

      if (!this.field_73011_w.field_76576_e) {
         for(int var6 = var3; var6 <= var4; ++var6) {
            this.func_147463_c(EnumSkyBlock.Sky, var1, var6, var2);
         }
      }

      this.func_147458_c(var1, var3, var2, var1, var4, var2);
   }

   public void func_147458_c(int var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = 0; var7 < this.field_73021_x.size(); ++var7) {
         ((IWorldAccess)this.field_73021_x.get(var7)).func_147585_a(var1, var2, var3, var4, var5, var6);
      }
   }

   public void func_147459_d(int var1, int var2, int var3, Block var4) {
      this.func_147460_e(var1 - 1, var2, var3, var4);
      this.func_147460_e(var1 + 1, var2, var3, var4);
      this.func_147460_e(var1, var2 - 1, var3, var4);
      this.func_147460_e(var1, var2 + 1, var3, var4);
      this.func_147460_e(var1, var2, var3 - 1, var4);
      this.func_147460_e(var1, var2, var3 + 1, var4);
   }

   public void func_147441_b(int var1, int var2, int var3, Block var4, int var5) {
      if (var5 != 4) {
         this.func_147460_e(var1 - 1, var2, var3, var4);
      }

      if (var5 != 5) {
         this.func_147460_e(var1 + 1, var2, var3, var4);
      }

      if (var5 != 0) {
         this.func_147460_e(var1, var2 - 1, var3, var4);
      }

      if (var5 != 1) {
         this.func_147460_e(var1, var2 + 1, var3, var4);
      }

      if (var5 != 2) {
         this.func_147460_e(var1, var2, var3 - 1, var4);
      }

      if (var5 != 3) {
         this.func_147460_e(var1, var2, var3 + 1, var4);
      }
   }

   public void func_147460_e(int var1, int var2, int var3, Block var4) {
      if (!this.field_72995_K) {
         Block var5 = this.func_147439_a(var1, var2, var3);

         try {
            var5.func_149695_a(this, var1, var2, var3, var4);
         } catch (Throwable var12) {
            CrashReport var7 = CrashReport.func_85055_a(var12, "Exception while updating neighbours");
            CrashReportCategory var8 = var7.func_85058_a("Block being updated");

            int var9;
            try {
               var9 = this.func_72805_g(var1, var2, var3);
            } catch (Throwable var11) {
               var9 = -1;
            }

            var8.func_71500_a("Source block type", new World$2(this, var4));
            CrashReportCategory.func_147153_a(var8, var1, var2, var3, var5, var9);
            throw new ReportedException(var7);
         }
      }
   }

   public boolean func_147477_a(int var1, int var2, int var3, Block var4) {
      return false;
   }

   public boolean func_72937_j(int var1, int var2, int var3) {
      return this.func_72964_e(var1 >> 4, var3 >> 4).func_76619_d(var1 & 15, var2, var3 & 15);
   }

   public int func_72883_k(int var1, int var2, int var3) {
      if (var2 < 0) {
         return 0;
      } else {
         if (var2 >= 256) {
            var2 = 255;
         }

         return this.func_72964_e(var1 >> 4, var3 >> 4).func_76629_c(var1 & 15, var2, var3 & 15, 0);
      }
   }

   public int func_72957_l(int var1, int var2, int var3) {
      return this.func_72849_a(var1, var2, var3, true);
   }

   public int func_72849_a(int var1, int var2, int var3, boolean var4) {
      if (var1 < -30000000 || var3 < -30000000 || var1 >= 30000000 || var3 >= 30000000) {
         return 15;
      } else if (var4 && this.func_147439_a(var1, var2, var3).func_149710_n()) {
         int var12 = this.func_72849_a(var1, var2 + 1, var3, false);
         int var6 = this.func_72849_a(var1 + 1, var2, var3, false);
         int var7 = this.func_72849_a(var1 - 1, var2, var3, false);
         int var8 = this.func_72849_a(var1, var2, var3 + 1, false);
         int var9 = this.func_72849_a(var1, var2, var3 - 1, false);
         if (var6 > var12) {
            var12 = var6;
         }

         if (var7 > var12) {
            var12 = var7;
         }

         if (var8 > var12) {
            var12 = var8;
         }

         if (var9 > var12) {
            var12 = var9;
         }

         return var12;
      } else if (var2 < 0) {
         return 0;
      } else {
         if (var2 >= 256) {
            var2 = 255;
         }

         Chunk var5 = this.func_72964_e(var1 >> 4, var3 >> 4);
         var1 &= 15;
         var3 &= 15;
         return var5.func_76629_c(var1, var2, var3, this.field_73008_k);
      }
   }

   public int func_72976_f(int var1, int var2) {
      if (var1 >= -30000000 && var2 >= -30000000 && var1 < 30000000 && var2 < 30000000) {
         if (!this.func_72916_c(var1 >> 4, var2 >> 4)) {
            return 0;
         } else {
            Chunk var3 = this.func_72964_e(var1 >> 4, var2 >> 4);
            return var3.func_76611_b(var1 & 15, var2 & 15);
         }
      } else {
         return 64;
      }
   }

   public int func_82734_g(int var1, int var2) {
      if (var1 >= -30000000 && var2 >= -30000000 && var1 < 30000000 && var2 < 30000000) {
         if (!this.func_72916_c(var1 >> 4, var2 >> 4)) {
            return 0;
         } else {
            Chunk var3 = this.func_72964_e(var1 >> 4, var2 >> 4);
            return var3.field_82912_p;
         }
      } else {
         return 64;
      }
   }

   public int func_72925_a(EnumSkyBlock var1, int var2, int var3, int var4) {
      if (this.field_73011_w.field_76576_e && var1 == EnumSkyBlock.Sky) {
         return 0;
      } else {
         if (var3 < 0) {
            var3 = 0;
         }

         if (var3 >= 256) {
            return var1.field_77198_c;
         } else if (var2 >= -30000000 && var4 >= -30000000 && var2 < 30000000 && var4 < 30000000) {
            int var5 = var2 >> 4;
            int var6 = var4 >> 4;
            if (!this.func_72916_c(var5, var6)) {
               return var1.field_77198_c;
            } else if (this.func_147439_a(var2, var3, var4).func_149710_n()) {
               int var12 = this.func_72972_b(var1, var2, var3 + 1, var4);
               int var8 = this.func_72972_b(var1, var2 + 1, var3, var4);
               int var9 = this.func_72972_b(var1, var2 - 1, var3, var4);
               int var10 = this.func_72972_b(var1, var2, var3, var4 + 1);
               int var11 = this.func_72972_b(var1, var2, var3, var4 - 1);
               if (var8 > var12) {
                  var12 = var8;
               }

               if (var9 > var12) {
                  var12 = var9;
               }

               if (var10 > var12) {
                  var12 = var10;
               }

               if (var11 > var12) {
                  var12 = var11;
               }

               return var12;
            } else {
               Chunk var7 = this.func_72964_e(var5, var6);
               return var7.func_76614_a(var1, var2 & 15, var3, var4 & 15);
            }
         } else {
            return var1.field_77198_c;
         }
      }
   }

   public int func_72972_b(EnumSkyBlock var1, int var2, int var3, int var4) {
      if (var3 < 0) {
         var3 = 0;
      }

      if (var3 >= 256) {
         var3 = 255;
      }

      if (var2 >= -30000000 && var4 >= -30000000 && var2 < 30000000 && var4 < 30000000) {
         int var5 = var2 >> 4;
         int var6 = var4 >> 4;
         if (!this.func_72916_c(var5, var6)) {
            return var1.field_77198_c;
         } else {
            Chunk var7 = this.func_72964_e(var5, var6);
            return var7.func_76614_a(var1, var2 & 15, var3, var4 & 15);
         }
      } else {
         return var1.field_77198_c;
      }
   }

   public void func_72915_b(EnumSkyBlock var1, int var2, int var3, int var4, int var5) {
      if (var2 >= -30000000 && var4 >= -30000000 && var2 < 30000000 && var4 < 30000000) {
         if (var3 >= 0) {
            if (var3 < 256) {
               if (this.func_72916_c(var2 >> 4, var4 >> 4)) {
                  Chunk var6 = this.func_72964_e(var2 >> 4, var4 >> 4);
                  var6.func_76633_a(var1, var2 & 15, var3, var4 & 15, var5);

                  for(int var7 = 0; var7 < this.field_73021_x.size(); ++var7) {
                     ((IWorldAccess)this.field_73021_x.get(var7)).func_147588_b(var2, var3, var4);
                  }
               }
            }
         }
      }
   }

   public void func_147479_m(int var1, int var2, int var3) {
      for(int var4 = 0; var4 < this.field_73021_x.size(); ++var4) {
         ((IWorldAccess)this.field_73021_x.get(var4)).func_147588_b(var1, var2, var3);
      }
   }

   @Override
   public int func_72802_i(int var1, int var2, int var3, int var4) {
      int var5 = this.func_72925_a(EnumSkyBlock.Sky, var1, var2, var3);
      int var6 = this.func_72925_a(EnumSkyBlock.Block, var1, var2, var3);
      if (var6 < var4) {
         var6 = var4;
      }

      return var5 << 20 | var6 << 4;
   }

   public float func_72801_o(int var1, int var2, int var3) {
      return this.field_73011_w.field_76573_f[this.func_72957_l(var1, var2, var3)];
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
      if (Double.isNaN(var1.field_72450_a) || Double.isNaN(var1.field_72448_b) || Double.isNaN(var1.field_72449_c)) {
         return null;
      } else if (!Double.isNaN(var2.field_72450_a) && !Double.isNaN(var2.field_72448_b) && !Double.isNaN(var2.field_72449_c)) {
         int var6 = MathHelper.func_76128_c(var2.field_72450_a);
         int var7 = MathHelper.func_76128_c(var2.field_72448_b);
         int var8 = MathHelper.func_76128_c(var2.field_72449_c);
         int var9 = MathHelper.func_76128_c(var1.field_72450_a);
         int var10 = MathHelper.func_76128_c(var1.field_72448_b);
         int var11 = MathHelper.func_76128_c(var1.field_72449_c);
         Block var12 = this.func_147439_a(var9, var10, var11);
         int var13 = this.func_72805_g(var9, var10, var11);
         if ((!var4 || var12.func_149668_a(this, var9, var10, var11) != null) && var12.func_149678_a(var13, var3)) {
            MovingObjectPosition var14 = var12.func_149731_a(this, var9, var10, var11, var1, var2);
            if (var14 != null) {
               return var14;
            }
         }

         MovingObjectPosition var40 = null;
         var13 = 200;

         while(var13-- >= 0) {
            if (Double.isNaN(var1.field_72450_a) || Double.isNaN(var1.field_72448_b) || Double.isNaN(var1.field_72449_c)) {
               return null;
            }

            if (var9 == var6 && var10 == var7 && var11 == var8) {
               return var5 ? var40 : null;
            }

            boolean var42 = true;
            boolean var15 = true;
            boolean var16 = true;
            double var17 = 999.0;
            double var19 = 999.0;
            double var21 = 999.0;
            if (var6 > var9) {
               var17 = (double)var9 + 1.0;
            } else if (var6 < var9) {
               var17 = (double)var9 + 0.0;
            } else {
               var42 = false;
            }

            if (var7 > var10) {
               var19 = (double)var10 + 1.0;
            } else if (var7 < var10) {
               var19 = (double)var10 + 0.0;
            } else {
               var15 = false;
            }

            if (var8 > var11) {
               var21 = (double)var11 + 1.0;
            } else if (var8 < var11) {
               var21 = (double)var11 + 0.0;
            } else {
               var16 = false;
            }

            double var23 = 999.0;
            double var25 = 999.0;
            double var27 = 999.0;
            double var29 = var2.field_72450_a - var1.field_72450_a;
            double var31 = var2.field_72448_b - var1.field_72448_b;
            double var33 = var2.field_72449_c - var1.field_72449_c;
            if (var42) {
               var23 = (var17 - var1.field_72450_a) / var29;
            }

            if (var15) {
               var25 = (var19 - var1.field_72448_b) / var31;
            }

            if (var16) {
               var27 = (var21 - var1.field_72449_c) / var33;
            }

            byte var35 = 0;
            if (var23 < var25 && var23 < var27) {
               if (var6 > var9) {
                  var35 = 4;
               } else {
                  var35 = 5;
               }

               var1.field_72450_a = var17;
               var1.field_72448_b += var31 * var23;
               var1.field_72449_c += var33 * var23;
            } else if (var25 < var27) {
               if (var7 > var10) {
                  var35 = 0;
               } else {
                  var35 = 1;
               }

               var1.field_72450_a += var29 * var25;
               var1.field_72448_b = var19;
               var1.field_72449_c += var33 * var25;
            } else {
               if (var8 > var11) {
                  var35 = 2;
               } else {
                  var35 = 3;
               }

               var1.field_72450_a += var29 * var27;
               var1.field_72448_b += var31 * var27;
               var1.field_72449_c = var21;
            }

            Vec3 var36 = Vec3.func_72443_a(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c);
            var9 = (int)(var36.field_72450_a = (double)MathHelper.func_76128_c(var1.field_72450_a));
            if (var35 == 5) {
               --var9;
               ++var36.field_72450_a;
            }

            var10 = (int)(var36.field_72448_b = (double)MathHelper.func_76128_c(var1.field_72448_b));
            if (var35 == 1) {
               --var10;
               ++var36.field_72448_b;
            }

            var11 = (int)(var36.field_72449_c = (double)MathHelper.func_76128_c(var1.field_72449_c));
            if (var35 == 3) {
               --var11;
               ++var36.field_72449_c;
            }

            Block var37 = this.func_147439_a(var9, var10, var11);
            int var38 = this.func_72805_g(var9, var10, var11);
            if (!var4 || var37.func_149668_a(this, var9, var10, var11) != null) {
               if (var37.func_149678_a(var38, var3)) {
                  MovingObjectPosition var39 = var37.func_149731_a(this, var9, var10, var11, var1, var2);
                  if (var39 != null) {
                     return var39;
                  }
               } else {
                  var40 = new MovingObjectPosition(var9, var10, var11, var35, var1, false);
               }
            }
         }

         return var5 ? var40 : null;
      } else {
         return null;
      }
   }

   public void func_72956_a(Entity var1, String var2, float var3, float var4) {
      for(int var5 = 0; var5 < this.field_73021_x.size(); ++var5) {
         ((IWorldAccess)this.field_73021_x.get(var5))
            .func_72704_a(var2, var1.field_70165_t, var1.field_70163_u - (double)var1.field_70129_M, var1.field_70161_v, var3, var4);
      }
   }

   public void func_85173_a(EntityPlayer var1, String var2, float var3, float var4) {
      for(int var5 = 0; var5 < this.field_73021_x.size(); ++var5) {
         ((IWorldAccess)this.field_73021_x.get(var5))
            .func_85102_a(var1, var2, var1.field_70165_t, var1.field_70163_u - (double)var1.field_70129_M, var1.field_70161_v, var3, var4);
      }
   }

   public void func_72908_a(double var1, double var3, double var5, String var7, float var8, float var9) {
      for(int var10 = 0; var10 < this.field_73021_x.size(); ++var10) {
         ((IWorldAccess)this.field_73021_x.get(var10)).func_72704_a(var7, var1, var3, var5, var8, var9);
      }
   }

   public void func_72980_b(double var1, double var3, double var5, String var7, float var8, float var9, boolean var10) {
   }

   public void func_72934_a(String var1, int var2, int var3, int var4) {
      for(int var5 = 0; var5 < this.field_73021_x.size(); ++var5) {
         ((IWorldAccess)this.field_73021_x.get(var5)).func_72702_a(var1, var2, var3, var4);
      }
   }

   public void func_72869_a(String var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      for(int var14 = 0; var14 < this.field_73021_x.size(); ++var14) {
         ((IWorldAccess)this.field_73021_x.get(var14)).func_72708_a(var1, var2, var4, var6, var8, var10, var12);
      }
   }

   public boolean func_72942_c(Entity var1) {
      this.field_73007_j.add(var1);
      return true;
   }

   public boolean func_72838_d(Entity var1) {
      int var2 = MathHelper.func_76128_c(var1.field_70165_t / 16.0);
      int var3 = MathHelper.func_76128_c(var1.field_70161_v / 16.0);
      boolean var4 = var1.field_98038_p;
      if (var1 instanceof EntityPlayer) {
         var4 = true;
      }

      if (!var4 && !this.func_72916_c(var2, var3)) {
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
         var1.field_70153_n.func_70078_a(null);
      }

      if (var1.field_70154_o != null) {
         var1.func_70078_a(null);
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
      if (var1.field_70175_ag && this.func_72916_c(var2, var3)) {
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

   public List func_72945_a(Entity var1, AxisAlignedBB var2) {
      this.field_72998_d.clear();
      int var3 = MathHelper.func_76128_c(var2.field_72340_a);
      int var4 = MathHelper.func_76128_c(var2.field_72336_d + 1.0);
      int var5 = MathHelper.func_76128_c(var2.field_72338_b);
      int var6 = MathHelper.func_76128_c(var2.field_72337_e + 1.0);
      int var7 = MathHelper.func_76128_c(var2.field_72339_c);
      int var8 = MathHelper.func_76128_c(var2.field_72334_f + 1.0);

      for(int var9 = var3; var9 < var4; ++var9) {
         for(int var10 = var7; var10 < var8; ++var10) {
            if (this.func_72899_e(var9, 64, var10)) {
               for(int var11 = var5 - 1; var11 < var6; ++var11) {
                  Block var12;
                  if (var9 >= -30000000 && var9 < 30000000 && var10 >= -30000000 && var10 < 30000000) {
                     var12 = this.func_147439_a(var9, var11, var10);
                  } else {
                     var12 = Blocks.field_150348_b;
                  }

                  var12.func_149743_a(this, var9, var11, var10, var2, this.field_72998_d, var1);
               }
            }
         }
      }

      double var14 = 0.25;
      List var15 = this.func_72839_b(var1, var2.func_72314_b(var14, var14, var14));

      for(int var16 = 0; var16 < var15.size(); ++var16) {
         AxisAlignedBB var13 = ((Entity)var15.get(var16)).func_70046_E();
         if (var13 != null && var13.func_72326_a(var2)) {
            this.field_72998_d.add(var13);
         }

         var13 = var1.func_70114_g((Entity)var15.get(var16));
         if (var13 != null && var13.func_72326_a(var2)) {
            this.field_72998_d.add(var13);
         }
      }

      return this.field_72998_d;
   }

   public List func_147461_a(AxisAlignedBB var1) {
      this.field_72998_d.clear();
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76128_c(var1.field_72336_d + 1.0);
      int var4 = MathHelper.func_76128_c(var1.field_72338_b);
      int var5 = MathHelper.func_76128_c(var1.field_72337_e + 1.0);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76128_c(var1.field_72334_f + 1.0);

      for(int var8 = var2; var8 < var3; ++var8) {
         for(int var9 = var6; var9 < var7; ++var9) {
            if (this.func_72899_e(var8, 64, var9)) {
               for(int var10 = var4 - 1; var10 < var5; ++var10) {
                  Block var11;
                  if (var8 >= -30000000 && var8 < 30000000 && var9 >= -30000000 && var9 < 30000000) {
                     var11 = this.func_147439_a(var8, var10, var9);
                  } else {
                     var11 = Blocks.field_150357_h;
                  }

                  var11.func_149743_a(this, var8, var10, var9, var1, this.field_72998_d, null);
               }
            }
         }
      }

      return this.field_72998_d;
   }

   public int func_72967_a(float var1) {
      float var2 = this.func_72826_c(var1);
      float var3 = 1.0F - (MathHelper.func_76134_b(var2 * 3.1415927F * 2.0F) * 2.0F + 0.5F);
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0 - (double)(this.func_72867_j(var1) * 5.0F) / 16.0));
      var3 = (float)((double)var3 * (1.0 - (double)(this.func_72819_i(var1) * 5.0F) / 16.0));
      var3 = 1.0F - var3;
      return (int)(var3 * 11.0F);
   }

   public float func_72971_b(float var1) {
      float var2 = this.func_72826_c(var1);
      float var3 = 1.0F - (MathHelper.func_76134_b(var2 * 3.1415927F * 2.0F) * 2.0F + 0.2F);
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0 - (double)(this.func_72867_j(var1) * 5.0F) / 16.0));
      var3 = (float)((double)var3 * (1.0 - (double)(this.func_72819_i(var1) * 5.0F) / 16.0));
      return var3 * 0.8F + 0.2F;
   }

   public Vec3 func_72833_a(Entity var1, float var2) {
      float var3 = this.func_72826_c(var2);
      float var4 = MathHelper.func_76134_b(var3 * 3.1415927F * 2.0F) * 2.0F + 0.5F;
      if (var4 < 0.0F) {
         var4 = 0.0F;
      }

      if (var4 > 1.0F) {
         var4 = 1.0F;
      }

      int var5 = MathHelper.func_76128_c(var1.field_70165_t);
      int var6 = MathHelper.func_76128_c(var1.field_70163_u);
      int var7 = MathHelper.func_76128_c(var1.field_70161_v);
      BiomeGenBase var8 = this.func_72807_a(var5, var7);
      float var9 = var8.func_150564_a(var5, var6, var7);
      int var10 = var8.func_76731_a(var9);
      float var11 = (float)(var10 >> 16 & 0xFF) / 255.0F;
      float var12 = (float)(var10 >> 8 & 0xFF) / 255.0F;
      float var13 = (float)(var10 & 0xFF) / 255.0F;
      var11 *= var4;
      var12 *= var4;
      var13 *= var4;
      float var14 = this.func_72867_j(var2);
      if (var14 > 0.0F) {
         float var15 = (var11 * 0.3F + var12 * 0.59F + var13 * 0.11F) * 0.6F;
         float var16 = 1.0F - var14 * 0.75F;
         var11 = var11 * var16 + var15 * (1.0F - var16);
         var12 = var12 * var16 + var15 * (1.0F - var16);
         var13 = var13 * var16 + var15 * (1.0F - var16);
      }

      float var21 = this.func_72819_i(var2);
      if (var21 > 0.0F) {
         float var22 = (var11 * 0.3F + var12 * 0.59F + var13 * 0.11F) * 0.2F;
         float var17 = 1.0F - var21 * 0.75F;
         var11 = var11 * var17 + var22 * (1.0F - var17);
         var12 = var12 * var17 + var22 * (1.0F - var17);
         var13 = var13 * var17 + var22 * (1.0F - var17);
      }

      if (this.field_73016_r > 0) {
         float var23 = (float)this.field_73016_r - var2;
         if (var23 > 1.0F) {
            var23 = 1.0F;
         }

         var23 *= 0.45F;
         var11 = var11 * (1.0F - var23) + 0.8F * var23;
         var12 = var12 * (1.0F - var23) + 0.8F * var23;
         var13 = var13 * (1.0F - var23) + 1.0F * var23;
      }

      return Vec3.func_72443_a((double)var11, (double)var12, (double)var13);
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
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      float var4 = (float)(this.field_73001_c >> 16 & 255L) / 255.0F;
      float var5 = (float)(this.field_73001_c >> 8 & 255L) / 255.0F;
      float var6 = (float)(this.field_73001_c & 255L) / 255.0F;
      float var7 = this.func_72867_j(var1);
      if (var7 > 0.0F) {
         float var8 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.6F;
         float var9 = 1.0F - var7 * 0.95F;
         var4 = var4 * var9 + var8 * (1.0F - var9);
         var5 = var5 * var9 + var8 * (1.0F - var9);
         var6 = var6 * var9 + var8 * (1.0F - var9);
      }

      var4 *= var3 * 0.9F + 0.1F;
      var5 *= var3 * 0.9F + 0.1F;
      var6 *= var3 * 0.85F + 0.15F;
      float var14 = this.func_72819_i(var1);
      if (var14 > 0.0F) {
         float var15 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.2F;
         float var10 = 1.0F - var14 * 0.95F;
         var4 = var4 * var10 + var15 * (1.0F - var10);
         var5 = var5 * var10 + var15 * (1.0F - var10);
         var6 = var6 * var10 + var15 * (1.0F - var10);
      }

      return Vec3.func_72443_a((double)var4, (double)var5, (double)var6);
   }

   public Vec3 func_72948_g(float var1) {
      float var2 = this.func_72826_c(var1);
      return this.field_73011_w.func_76562_b(var2, var1);
   }

   public int func_72874_g(int var1, int var2) {
      return this.func_72938_d(var1, var2).func_76626_d(var1 & 15, var2 & 15);
   }

   public int func_72825_h(int var1, int var2) {
      Chunk var3 = this.func_72938_d(var1, var2);
      int var4 = var3.func_76625_h() + 15;
      var1 &= 15;

      for(int var7 = var2 & 15; var4 > 0; --var4) {
         Block var5 = var3.func_150810_a(var1, var4, var7);
         if (var5.func_149688_o().func_76230_c() && var5.func_149688_o() != Material.field_151584_j) {
            return var4 + 1;
         }
      }

      return -1;
   }

   public float func_72880_h(float var1) {
      float var2 = this.func_72826_c(var1);
      float var3 = 1.0F - (MathHelper.func_76134_b(var2 * 3.1415927F * 2.0F) * 2.0F + 0.25F);
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      return var3 * var3 * 0.5F;
   }

   public void func_147464_a(int var1, int var2, int var3, Block var4, int var5) {
   }

   public void func_147454_a(int var1, int var2, int var3, Block var4, int var5, int var6) {
   }

   public void func_147446_b(int var1, int var2, int var3, Block var4, int var5, int var6) {
   }

   public void func_72939_s() {
      this.field_72984_F.func_76320_a("entities");
      this.field_72984_F.func_76320_a("global");

      for(int var1 = 0; var1 < this.field_73007_j.size(); ++var1) {
         Entity var2 = (Entity)this.field_73007_j.get(var1);

         try {
            ++var2.field_70173_aa;
            var2.func_70071_h_();
         } catch (Throwable var8) {
            CrashReport var4 = CrashReport.func_85055_a(var8, "Ticking entity");
            CrashReportCategory var5 = var4.func_85058_a("Entity being ticked");
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

      for(int var9 = 0; var9 < this.field_72997_g.size(); ++var9) {
         Entity var13 = (Entity)this.field_72997_g.get(var9);
         int var3 = var13.field_70176_ah;
         int var20 = var13.field_70164_aj;
         if (var13.field_70175_ag && this.func_72916_c(var3, var20)) {
            this.func_72964_e(var3, var20).func_76622_b(var13);
         }
      }

      for(int var10 = 0; var10 < this.field_72997_g.size(); ++var10) {
         this.func_72847_b((Entity)this.field_72997_g.get(var10));
      }

      this.field_72997_g.clear();
      this.field_72984_F.func_76318_c("regular");

      for(int var11 = 0; var11 < this.field_72996_f.size(); ++var11) {
         Entity var14 = (Entity)this.field_72996_f.get(var11);
         if (var14.field_70154_o != null) {
            if (!var14.field_70154_o.field_70128_L && var14.field_70154_o.field_70153_n == var14) {
               continue;
            }

            var14.field_70154_o.field_70153_n = null;
            var14.field_70154_o = null;
         }

         this.field_72984_F.func_76320_a("tick");
         if (!var14.field_70128_L) {
            try {
               this.func_72870_g(var14);
            } catch (Throwable var7) {
               CrashReport var21 = CrashReport.func_85055_a(var7, "Ticking entity");
               CrashReportCategory var25 = var21.func_85058_a("Entity being ticked");
               var14.func_85029_a(var25);
               throw new ReportedException(var21);
            }
         }

         this.field_72984_F.func_76319_b();
         this.field_72984_F.func_76320_a("remove");
         if (var14.field_70128_L) {
            int var17 = var14.field_70176_ah;
            int var22 = var14.field_70164_aj;
            if (var14.field_70175_ag && this.func_72916_c(var17, var22)) {
               this.func_72964_e(var17, var22).func_76622_b(var14);
            }

            this.field_72996_f.remove(var11--);
            this.func_72847_b(var14);
         }

         this.field_72984_F.func_76319_b();
      }

      this.field_72984_F.func_76318_c("blockEntities");
      this.field_147481_N = true;
      Iterator var12 = this.field_147482_g.iterator();

      while(var12.hasNext()) {
         TileEntity var15 = (TileEntity)var12.next();
         if (!var15.func_145837_r() && var15.func_145830_o() && this.func_72899_e(var15.field_145851_c, var15.field_145848_d, var15.field_145849_e)) {
            try {
               var15.func_145845_h();
            } catch (Throwable var6) {
               CrashReport var23 = CrashReport.func_85055_a(var6, "Ticking block entity");
               CrashReportCategory var26 = var23.func_85058_a("Block entity being ticked");
               var15.func_145828_a(var26);
               throw new ReportedException(var23);
            }
         }

         if (var15.func_145837_r()) {
            var12.remove();
            if (this.func_72916_c(var15.field_145851_c >> 4, var15.field_145849_e >> 4)) {
               Chunk var18 = this.func_72964_e(var15.field_145851_c >> 4, var15.field_145849_e >> 4);
               if (var18 != null) {
                  var18.func_150805_f(var15.field_145851_c & 15, var15.field_145848_d, var15.field_145849_e & 15);
               }
            }
         }
      }

      this.field_147481_N = false;
      if (!this.field_147483_b.isEmpty()) {
         this.field_147482_g.removeAll(this.field_147483_b);
         this.field_147483_b.clear();
      }

      this.field_72984_F.func_76318_c("pendingBlockEntities");
      if (!this.field_147484_a.isEmpty()) {
         for(int var16 = 0; var16 < this.field_147484_a.size(); ++var16) {
            TileEntity var19 = (TileEntity)this.field_147484_a.get(var16);
            if (!var19.func_145837_r()) {
               if (!this.field_147482_g.contains(var19)) {
                  this.field_147482_g.add(var19);
               }

               if (this.func_72916_c(var19.field_145851_c >> 4, var19.field_145849_e >> 4)) {
                  Chunk var24 = this.func_72964_e(var19.field_145851_c >> 4, var19.field_145849_e >> 4);
                  if (var24 != null) {
                     var24.func_150812_a(var19.field_145851_c & 15, var19.field_145848_d, var19.field_145849_e & 15, var19);
                  }
               }

               this.func_147471_g(var19.field_145851_c, var19.field_145848_d, var19.field_145849_e);
            }
         }

         this.field_147484_a.clear();
      }

      this.field_72984_F.func_76319_b();
      this.field_72984_F.func_76319_b();
   }

   public void func_147448_a(Collection var1) {
      if (this.field_147481_N) {
         this.field_147484_a.addAll(var1);
      } else {
         this.field_147482_g.addAll(var1);
      }
   }

   public void func_72870_g(Entity var1) {
      this.func_72866_a(var1, true);
   }

   public void func_72866_a(Entity var1, boolean var2) {
      int var3 = MathHelper.func_76128_c(var1.field_70165_t);
      int var4 = MathHelper.func_76128_c(var1.field_70161_v);
      byte var5 = 32;
      if (!var2 || this.func_72904_c(var3 - var5, 0, var4 - var5, var3 + var5, 0, var4 + var5)) {
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

         int var6 = MathHelper.func_76128_c(var1.field_70165_t / 16.0);
         int var7 = MathHelper.func_76128_c(var1.field_70163_u / 16.0);
         int var8 = MathHelper.func_76128_c(var1.field_70161_v / 16.0);
         if (!var1.field_70175_ag || var1.field_70176_ah != var6 || var1.field_70162_ai != var7 || var1.field_70164_aj != var8) {
            if (var1.field_70175_ag && this.func_72916_c(var1.field_70176_ah, var1.field_70164_aj)) {
               this.func_72964_e(var1.field_70176_ah, var1.field_70164_aj).func_76608_a(var1, var1.field_70162_ai);
            }

            if (this.func_72916_c(var6, var8)) {
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
      return this.func_72917_a(var1, null);
   }

   public boolean func_72917_a(AxisAlignedBB var1, Entity var2) {
      List var3 = this.func_72839_b(null, var1);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         Entity var5 = (Entity)var3.get(var4);
         if (!var5.field_70128_L && var5.field_70156_m && var5 != var2) {
            return false;
         }
      }

      return true;
   }

   public boolean func_72829_c(AxisAlignedBB var1) {
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76128_c(var1.field_72336_d + 1.0);
      int var4 = MathHelper.func_76128_c(var1.field_72338_b);
      int var5 = MathHelper.func_76128_c(var1.field_72337_e + 1.0);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76128_c(var1.field_72334_f + 1.0);
      if (var1.field_72340_a < 0.0) {
         --var2;
      }

      if (var1.field_72338_b < 0.0) {
         --var4;
      }

      if (var1.field_72339_c < 0.0) {
         --var6;
      }

      for(int var8 = var2; var8 < var3; ++var8) {
         for(int var9 = var4; var9 < var5; ++var9) {
            for(int var10 = var6; var10 < var7; ++var10) {
               Block var11 = this.func_147439_a(var8, var9, var10);
               if (var11.func_149688_o() != Material.field_151579_a) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean func_72953_d(AxisAlignedBB var1) {
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76128_c(var1.field_72336_d + 1.0);
      int var4 = MathHelper.func_76128_c(var1.field_72338_b);
      int var5 = MathHelper.func_76128_c(var1.field_72337_e + 1.0);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76128_c(var1.field_72334_f + 1.0);
      if (var1.field_72340_a < 0.0) {
         --var2;
      }

      if (var1.field_72338_b < 0.0) {
         --var4;
      }

      if (var1.field_72339_c < 0.0) {
         --var6;
      }

      for(int var8 = var2; var8 < var3; ++var8) {
         for(int var9 = var4; var9 < var5; ++var9) {
            for(int var10 = var6; var10 < var7; ++var10) {
               Block var11 = this.func_147439_a(var8, var9, var10);
               if (var11.func_149688_o().func_76224_d()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean func_147470_e(AxisAlignedBB var1) {
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76128_c(var1.field_72336_d + 1.0);
      int var4 = MathHelper.func_76128_c(var1.field_72338_b);
      int var5 = MathHelper.func_76128_c(var1.field_72337_e + 1.0);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76128_c(var1.field_72334_f + 1.0);
      if (this.func_72904_c(var2, var4, var6, var3, var5, var7)) {
         for(int var8 = var2; var8 < var3; ++var8) {
            for(int var9 = var4; var9 < var5; ++var9) {
               for(int var10 = var6; var10 < var7; ++var10) {
                  Block var11 = this.func_147439_a(var8, var9, var10);
                  if (var11 == Blocks.field_150480_ab || var11 == Blocks.field_150356_k || var11 == Blocks.field_150353_l) {
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
      int var5 = MathHelper.func_76128_c(var1.field_72336_d + 1.0);
      int var6 = MathHelper.func_76128_c(var1.field_72338_b);
      int var7 = MathHelper.func_76128_c(var1.field_72337_e + 1.0);
      int var8 = MathHelper.func_76128_c(var1.field_72339_c);
      int var9 = MathHelper.func_76128_c(var1.field_72334_f + 1.0);
      if (!this.func_72904_c(var4, var6, var8, var5, var7, var9)) {
         return false;
      } else {
         boolean var10 = false;
         Vec3 var11 = Vec3.func_72443_a(0.0, 0.0, 0.0);

         for(int var12 = var4; var12 < var5; ++var12) {
            for(int var13 = var6; var13 < var7; ++var13) {
               for(int var14 = var8; var14 < var9; ++var14) {
                  Block var15 = this.func_147439_a(var12, var13, var14);
                  if (var15.func_149688_o() == var2) {
                     double var16 = (double)((float)(var13 + 1) - BlockLiquid.func_149801_b(this.func_72805_g(var12, var13, var14)));
                     if ((double)var7 >= var16) {
                        var10 = true;
                        var15.func_149640_a(this, var12, var13, var14, var3, var11);
                     }
                  }
               }
            }
         }

         if (var11.func_72433_c() > 0.0 && var3.func_96092_aw()) {
            var11 = var11.func_72432_b();
            double var19 = 0.014;
            var3.field_70159_w += var11.field_72450_a * var19;
            var3.field_70181_x += var11.field_72448_b * var19;
            var3.field_70179_y += var11.field_72449_c * var19;
         }

         return var10;
      }
   }

   public boolean func_72875_a(AxisAlignedBB var1, Material var2) {
      int var3 = MathHelper.func_76128_c(var1.field_72340_a);
      int var4 = MathHelper.func_76128_c(var1.field_72336_d + 1.0);
      int var5 = MathHelper.func_76128_c(var1.field_72338_b);
      int var6 = MathHelper.func_76128_c(var1.field_72337_e + 1.0);
      int var7 = MathHelper.func_76128_c(var1.field_72339_c);
      int var8 = MathHelper.func_76128_c(var1.field_72334_f + 1.0);

      for(int var9 = var3; var9 < var4; ++var9) {
         for(int var10 = var5; var10 < var6; ++var10) {
            for(int var11 = var7; var11 < var8; ++var11) {
               if (this.func_147439_a(var9, var10, var11).func_149688_o() == var2) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean func_72830_b(AxisAlignedBB var1, Material var2) {
      int var3 = MathHelper.func_76128_c(var1.field_72340_a);
      int var4 = MathHelper.func_76128_c(var1.field_72336_d + 1.0);
      int var5 = MathHelper.func_76128_c(var1.field_72338_b);
      int var6 = MathHelper.func_76128_c(var1.field_72337_e + 1.0);
      int var7 = MathHelper.func_76128_c(var1.field_72339_c);
      int var8 = MathHelper.func_76128_c(var1.field_72334_f + 1.0);

      for(int var9 = var3; var9 < var4; ++var9) {
         for(int var10 = var5; var10 < var6; ++var10) {
            for(int var11 = var7; var11 < var8; ++var11) {
               Block var12 = this.func_147439_a(var9, var10, var11);
               if (var12.func_149688_o() == var2) {
                  int var13 = this.func_72805_g(var9, var10, var11);
                  double var14 = (double)(var10 + 1);
                  if (var13 < 8) {
                     var14 = (double)(var10 + 1) - (double)var13 / 8.0;
                  }

                  if (var14 >= var1.field_72338_b) {
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
      Explosion var11 = new Explosion(this, var1, var2, var4, var6, var8);
      var11.field_77286_a = var9;
      var11.field_82755_b = var10;
      var11.func_77278_a();
      var11.func_77279_a(true);
      return var11;
   }

   public float func_72842_a(Vec3 var1, AxisAlignedBB var2) {
      double var3 = 1.0 / ((var2.field_72336_d - var2.field_72340_a) * 2.0 + 1.0);
      double var5 = 1.0 / ((var2.field_72337_e - var2.field_72338_b) * 2.0 + 1.0);
      double var7 = 1.0 / ((var2.field_72334_f - var2.field_72339_c) * 2.0 + 1.0);
      if (!(var3 < 0.0) && !(var5 < 0.0) && !(var7 < 0.0)) {
         int var9 = 0;
         int var10 = 0;

         for(float var11 = 0.0F; var11 <= 1.0F; var11 = (float)((double)var11 + var3)) {
            for(float var12 = 0.0F; var12 <= 1.0F; var12 = (float)((double)var12 + var5)) {
               for(float var13 = 0.0F; var13 <= 1.0F; var13 = (float)((double)var13 + var7)) {
                  double var14 = var2.field_72340_a + (var2.field_72336_d - var2.field_72340_a) * (double)var11;
                  double var16 = var2.field_72338_b + (var2.field_72337_e - var2.field_72338_b) * (double)var12;
                  double var18 = var2.field_72339_c + (var2.field_72334_f - var2.field_72339_c) * (double)var13;
                  if (this.func_72933_a(Vec3.func_72443_a(var14, var16, var18), var1) == null) {
                     ++var9;
                  }

                  ++var10;
               }
            }
         }

         return (float)var9 / (float)var10;
      } else {
         return 0.0F;
      }
   }

   public boolean func_72886_a(EntityPlayer var1, int var2, int var3, int var4, int var5) {
      if (var5 == 0) {
         --var3;
      }

      if (var5 == 1) {
         ++var3;
      }

      if (var5 == 2) {
         --var4;
      }

      if (var5 == 3) {
         ++var4;
      }

      if (var5 == 4) {
         --var2;
      }

      if (var5 == 5) {
         ++var2;
      }

      if (this.func_147439_a(var2, var3, var4) == Blocks.field_150480_ab) {
         this.func_72889_a(var1, 1004, var2, var3, var4, 0);
         this.func_147468_f(var2, var3, var4);
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

   @Override
   public TileEntity func_147438_o(int var1, int var2, int var3) {
      if (var2 >= 0 && var2 < 256) {
         TileEntity var4 = null;
         if (this.field_147481_N) {
            for(int var5 = 0; var5 < this.field_147484_a.size(); ++var5) {
               TileEntity var6 = (TileEntity)this.field_147484_a.get(var5);
               if (!var6.func_145837_r() && var6.field_145851_c == var1 && var6.field_145848_d == var2 && var6.field_145849_e == var3) {
                  var4 = var6;
                  break;
               }
            }
         }

         if (var4 == null) {
            Chunk var7 = this.func_72964_e(var1 >> 4, var3 >> 4);
            if (var7 != null) {
               var4 = var7.func_150806_e(var1 & 15, var2, var3 & 15);
            }
         }

         if (var4 == null) {
            for(int var8 = 0; var8 < this.field_147484_a.size(); ++var8) {
               TileEntity var9 = (TileEntity)this.field_147484_a.get(var8);
               if (!var9.func_145837_r() && var9.field_145851_c == var1 && var9.field_145848_d == var2 && var9.field_145849_e == var3) {
                  var4 = var9;
                  break;
               }
            }
         }

         return var4;
      } else {
         return null;
      }
   }

   public void func_147455_a(int var1, int var2, int var3, TileEntity var4) {
      if (var4 != null && !var4.func_145837_r()) {
         if (this.field_147481_N) {
            var4.field_145851_c = var1;
            var4.field_145848_d = var2;
            var4.field_145849_e = var3;
            Iterator var5 = this.field_147484_a.iterator();

            while(var5.hasNext()) {
               TileEntity var6 = (TileEntity)var5.next();
               if (var6.field_145851_c == var1 && var6.field_145848_d == var2 && var6.field_145849_e == var3) {
                  var6.func_145843_s();
                  var5.remove();
               }
            }

            this.field_147484_a.add(var4);
         } else {
            this.field_147482_g.add(var4);
            Chunk var7 = this.func_72964_e(var1 >> 4, var3 >> 4);
            if (var7 != null) {
               var7.func_150812_a(var1 & 15, var2, var3 & 15, var4);
            }
         }
      }
   }

   public void func_147475_p(int var1, int var2, int var3) {
      TileEntity var4 = this.func_147438_o(var1, var2, var3);
      if (var4 != null && this.field_147481_N) {
         var4.func_145843_s();
         this.field_147484_a.remove(var4);
      } else {
         if (var4 != null) {
            this.field_147484_a.remove(var4);
            this.field_147482_g.remove(var4);
         }

         Chunk var5 = this.func_72964_e(var1 >> 4, var3 >> 4);
         if (var5 != null) {
            var5.func_150805_f(var1 & 15, var2, var3 & 15);
         }
      }
   }

   public void func_147457_a(TileEntity var1) {
      this.field_147483_b.add(var1);
   }

   public boolean func_147469_q(int var1, int var2, int var3) {
      AxisAlignedBB var4 = this.func_147439_a(var1, var2, var3).func_149668_a(this, var1, var2, var3);
      return var4 != null && var4.func_72320_b() >= 1.0;
   }

   public static boolean func_147466_a(IBlockAccess var0, int var1, int var2, int var3) {
      Block var4 = var0.func_147439_a(var1, var2, var3);
      int var5 = var0.func_72805_g(var1, var2, var3);
      if (var4.func_149688_o().func_76218_k() && var4.func_149686_d()) {
         return true;
      } else if (var4 instanceof BlockStairs) {
         return (var5 & 4) == 4;
      } else if (var4 instanceof BlockSlab) {
         return (var5 & 8) == 8;
      } else if (var4 instanceof BlockHopper) {
         return true;
      } else if (var4 instanceof BlockSnow) {
         return (var5 & 7) == 7;
      } else {
         return false;
      }
   }

   public boolean func_147445_c(int var1, int var2, int var3, boolean var4) {
      if (var1 >= -30000000 && var3 >= -30000000 && var1 < 30000000 && var3 < 30000000) {
         Chunk var5 = this.field_73020_y.func_73154_d(var1 >> 4, var3 >> 4);
         if (var5 != null && !var5.func_76621_g()) {
            Block var6 = this.func_147439_a(var1, var2, var3);
            return var6.func_149688_o().func_76218_k() && var6.func_149686_d();
         } else {
            return var4;
         }
      } else {
         return var4;
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

   private void func_72947_a() {
      if (this.field_72986_A.func_76059_o()) {
         this.field_73004_o = 1.0F;
         if (this.field_72986_A.func_76061_m()) {
            this.field_73017_q = 1.0F;
         }
      }
   }

   protected void func_72979_l() {
      if (!this.field_73011_w.field_76576_e) {
         if (!this.field_72995_K) {
            int var1 = this.field_72986_A.func_76071_n();
            if (var1 <= 0) {
               if (this.field_72986_A.func_76061_m()) {
                  this.field_72986_A.func_76090_f(this.field_73012_v.nextInt(12000) + 3600);
               } else {
                  this.field_72986_A.func_76090_f(this.field_73012_v.nextInt(168000) + 12000);
               }
            } else {
               this.field_72986_A.func_76090_f(--var1);
               if (var1 <= 0) {
                  this.field_72986_A.func_76069_a(!this.field_72986_A.func_76061_m());
               }
            }

            this.field_73018_p = this.field_73017_q;
            if (this.field_72986_A.func_76061_m()) {
               this.field_73017_q = (float)((double)this.field_73017_q + 0.01);
            } else {
               this.field_73017_q = (float)((double)this.field_73017_q - 0.01);
            }

            this.field_73017_q = MathHelper.func_76131_a(this.field_73017_q, 0.0F, 1.0F);
            int var2 = this.field_72986_A.func_76083_p();
            if (var2 <= 0) {
               if (this.field_72986_A.func_76059_o()) {
                  this.field_72986_A.func_76080_g(this.field_73012_v.nextInt(12000) + 12000);
               } else {
                  this.field_72986_A.func_76080_g(this.field_73012_v.nextInt(168000) + 12000);
               }
            } else {
               this.field_72986_A.func_76080_g(--var2);
               if (var2 <= 0) {
                  this.field_72986_A.func_76084_b(!this.field_72986_A.func_76059_o());
               }
            }

            this.field_73003_n = this.field_73004_o;
            if (this.field_72986_A.func_76059_o()) {
               this.field_73004_o = (float)((double)this.field_73004_o + 0.01);
            } else {
               this.field_73004_o = (float)((double)this.field_73004_o - 0.01);
            }

            this.field_73004_o = MathHelper.func_76131_a(this.field_73004_o, 0.0F, 1.0F);
         }
      }
   }

   protected void func_72903_x() {
      this.field_72993_I.clear();
      this.field_72984_F.func_76320_a("buildList");

      for(int var1 = 0; var1 < this.field_73010_i.size(); ++var1) {
         EntityPlayer var2 = (EntityPlayer)this.field_73010_i.get(var1);
         int var3 = MathHelper.func_76128_c(var2.field_70165_t / 16.0);
         int var4 = MathHelper.func_76128_c(var2.field_70161_v / 16.0);
         int var5 = this.func_152379_p();

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
         int var8 = this.field_73012_v.nextInt(this.field_73010_i.size());
         EntityPlayer var9 = (EntityPlayer)this.field_73010_i.get(var8);
         int var10 = MathHelper.func_76128_c(var9.field_70165_t) + this.field_73012_v.nextInt(11) - 5;
         int var11 = MathHelper.func_76128_c(var9.field_70163_u) + this.field_73012_v.nextInt(11) - 5;
         int var12 = MathHelper.func_76128_c(var9.field_70161_v) + this.field_73012_v.nextInt(11) - 5;
         this.func_147451_t(var10, var11, var12);
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
         int var7 = var4 >> 16 & 0xFF;
         Block var8 = var3.func_150810_a(var5, var7, var6);
         var5 += var1;
         var6 += var2;
         if (var8.func_149688_o() == Material.field_151579_a
            && this.func_72883_k(var5, var7, var6) <= this.field_73012_v.nextInt(8)
            && this.func_72972_b(EnumSkyBlock.Sky, var5, var7, var6) <= 0) {
            EntityPlayer var9 = this.func_72977_a((double)var5 + 0.5, (double)var7 + 0.5, (double)var6 + 0.5, 8.0);
            if (var9 != null && var9.func_70092_e((double)var5 + 0.5, (double)var7 + 0.5, (double)var6 + 0.5) > 4.0) {
               this.func_72908_a(
                  (double)var5 + 0.5, (double)var7 + 0.5, (double)var6 + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.field_73012_v.nextFloat() * 0.2F
               );
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

   public boolean func_72884_u(int var1, int var2, int var3) {
      return this.func_72834_c(var1, var2, var3, false);
   }

   public boolean func_72850_v(int var1, int var2, int var3) {
      return this.func_72834_c(var1, var2, var3, true);
   }

   public boolean func_72834_c(int var1, int var2, int var3, boolean var4) {
      BiomeGenBase var5 = this.func_72807_a(var1, var3);
      float var6 = var5.func_150564_a(var1, var2, var3);
      if (var6 > 0.15F) {
         return false;
      } else {
         if (var2 >= 0 && var2 < 256 && this.func_72972_b(EnumSkyBlock.Block, var1, var2, var3) < 10) {
            Block var7 = this.func_147439_a(var1, var2, var3);
            if ((var7 == Blocks.field_150355_j || var7 == Blocks.field_150358_i) && this.func_72805_g(var1, var2, var3) == 0) {
               if (!var4) {
                  return true;
               }

               boolean var8 = true;
               if (var8 && this.func_147439_a(var1 - 1, var2, var3).func_149688_o() != Material.field_151586_h) {
                  var8 = false;
               }

               if (var8 && this.func_147439_a(var1 + 1, var2, var3).func_149688_o() != Material.field_151586_h) {
                  var8 = false;
               }

               if (var8 && this.func_147439_a(var1, var2, var3 - 1).func_149688_o() != Material.field_151586_h) {
                  var8 = false;
               }

               if (var8 && this.func_147439_a(var1, var2, var3 + 1).func_149688_o() != Material.field_151586_h) {
                  var8 = false;
               }

               if (!var8) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean func_147478_e(int var1, int var2, int var3, boolean var4) {
      BiomeGenBase var5 = this.func_72807_a(var1, var3);
      float var6 = var5.func_150564_a(var1, var2, var3);
      if (var6 > 0.15F) {
         return false;
      } else if (!var4) {
         return true;
      } else {
         if (var2 >= 0 && var2 < 256 && this.func_72972_b(EnumSkyBlock.Block, var1, var2, var3) < 10) {
            Block var7 = this.func_147439_a(var1, var2, var3);
            if (var7.func_149688_o() == Material.field_151579_a && Blocks.field_150431_aC.func_149742_c(this, var1, var2, var3)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean func_147451_t(int var1, int var2, int var3) {
      boolean var4 = false;
      if (!this.field_73011_w.field_76576_e) {
         var4 |= this.func_147463_c(EnumSkyBlock.Sky, var1, var2, var3);
      }

      return var4 | this.func_147463_c(EnumSkyBlock.Block, var1, var2, var3);
   }

   private int func_98179_a(int var1, int var2, int var3, EnumSkyBlock var4) {
      if (var4 == EnumSkyBlock.Sky && this.func_72937_j(var1, var2, var3)) {
         return 15;
      } else {
         Block var5 = this.func_147439_a(var1, var2, var3);
         int var6 = var4 == EnumSkyBlock.Sky ? 0 : var5.func_149750_m();
         int var7 = var5.func_149717_k();
         if (var7 >= 15 && var5.func_149750_m() > 0) {
            var7 = 1;
         }

         if (var7 < 1) {
            var7 = 1;
         }

         if (var7 >= 15) {
            return 0;
         } else if (var6 >= 14) {
            return var6;
         } else {
            for(int var8 = 0; var8 < 6; ++var8) {
               int var9 = var1 + Facing.field_71586_b[var8];
               int var10 = var2 + Facing.field_71587_c[var8];
               int var11 = var3 + Facing.field_71585_d[var8];
               int var12 = this.func_72972_b(var4, var9, var10, var11) - var7;
               if (var12 > var6) {
                  var6 = var12;
               }

               if (var6 >= 14) {
                  return var6;
               }
            }

            return var6;
         }
      }
   }

   public boolean func_147463_c(EnumSkyBlock var1, int var2, int var3, int var4) {
      if (!this.func_72873_a(var2, var3, var4, 17)) {
         return false;
      } else {
         int var5 = 0;
         int var6 = 0;
         this.field_72984_F.func_76320_a("getBrightness");
         int var7 = this.func_72972_b(var1, var2, var3, var4);
         int var8 = this.func_98179_a(var2, var3, var4, var1);
         if (var8 > var7) {
            this.field_72994_J[var6++] = 133152;
         } else if (var8 < var7) {
            this.field_72994_J[var6++] = 133152 | var7 << 18;

            while(var5 < var6) {
               int var9 = this.field_72994_J[var5++];
               int var10 = (var9 & 63) - 32 + var2;
               int var11 = (var9 >> 6 & 63) - 32 + var3;
               int var12 = (var9 >> 12 & 63) - 32 + var4;
               int var13 = var9 >> 18 & 15;
               int var14 = this.func_72972_b(var1, var10, var11, var12);
               if (var14 == var13) {
                  this.func_72915_b(var1, var10, var11, var12, 0);
                  if (var13 > 0) {
                     int var15 = MathHelper.func_76130_a(var10 - var2);
                     int var16 = MathHelper.func_76130_a(var11 - var3);
                     int var17 = MathHelper.func_76130_a(var12 - var4);
                     if (var15 + var16 + var17 < 17) {
                        for(int var18 = 0; var18 < 6; ++var18) {
                           int var19 = var10 + Facing.field_71586_b[var18];
                           int var20 = var11 + Facing.field_71587_c[var18];
                           int var21 = var12 + Facing.field_71585_d[var18];
                           int var22 = Math.max(1, this.func_147439_a(var19, var20, var21).func_149717_k());
                           var14 = this.func_72972_b(var1, var19, var20, var21);
                           if (var14 == var13 - var22 && var6 < this.field_72994_J.length) {
                              this.field_72994_J[var6++] = var19 - var2 + 32 | var20 - var3 + 32 << 6 | var21 - var4 + 32 << 12 | var13 - var22 << 18;
                           }
                        }
                     }
                  }
               }
            }

            var5 = 0;
         }

         this.field_72984_F.func_76319_b();
         this.field_72984_F.func_76320_a("checkedPosition < toCheckCount");

         while(var5 < var6) {
            int var23 = this.field_72994_J[var5++];
            int var24 = (var23 & 63) - 32 + var2;
            int var25 = (var23 >> 6 & 63) - 32 + var3;
            int var26 = (var23 >> 12 & 63) - 32 + var4;
            int var27 = this.func_72972_b(var1, var24, var25, var26);
            int var29 = this.func_98179_a(var24, var25, var26, var1);
            if (var29 != var27) {
               this.func_72915_b(var1, var24, var25, var26, var29);
               if (var29 > var27) {
                  int var30 = Math.abs(var24 - var2);
                  int var31 = Math.abs(var25 - var3);
                  int var32 = Math.abs(var26 - var4);
                  boolean var33 = var6 < this.field_72994_J.length - 6;
                  if (var30 + var31 + var32 < 17 && var33) {
                     if (this.func_72972_b(var1, var24 - 1, var25, var26) < var29) {
                        this.field_72994_J[var6++] = var24 - 1 - var2 + 32 + (var25 - var3 + 32 << 6) + (var26 - var4 + 32 << 12);
                     }

                     if (this.func_72972_b(var1, var24 + 1, var25, var26) < var29) {
                        this.field_72994_J[var6++] = var24 + 1 - var2 + 32 + (var25 - var3 + 32 << 6) + (var26 - var4 + 32 << 12);
                     }

                     if (this.func_72972_b(var1, var24, var25 - 1, var26) < var29) {
                        this.field_72994_J[var6++] = var24 - var2 + 32 + (var25 - 1 - var3 + 32 << 6) + (var26 - var4 + 32 << 12);
                     }

                     if (this.func_72972_b(var1, var24, var25 + 1, var26) < var29) {
                        this.field_72994_J[var6++] = var24 - var2 + 32 + (var25 + 1 - var3 + 32 << 6) + (var26 - var4 + 32 << 12);
                     }

                     if (this.func_72972_b(var1, var24, var25, var26 - 1) < var29) {
                        this.field_72994_J[var6++] = var24 - var2 + 32 + (var25 - var3 + 32 << 6) + (var26 - 1 - var4 + 32 << 12);
                     }

                     if (this.func_72972_b(var1, var24, var25, var26 + 1) < var29) {
                        this.field_72994_J[var6++] = var24 - var2 + 32 + (var25 - var3 + 32 << 6) + (var26 + 1 - var4 + 32 << 12);
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

   public List func_72920_a(Chunk var1, boolean var2) {
      return null;
   }

   public List func_72839_b(Entity var1, AxisAlignedBB var2) {
      return this.func_94576_a(var1, var2, null);
   }

   public List func_94576_a(Entity var1, AxisAlignedBB var2, IEntitySelector var3) {
      ArrayList var4 = new ArrayList();
      int var5 = MathHelper.func_76128_c((var2.field_72340_a - 2.0) / 16.0);
      int var6 = MathHelper.func_76128_c((var2.field_72336_d + 2.0) / 16.0);
      int var7 = MathHelper.func_76128_c((var2.field_72339_c - 2.0) / 16.0);
      int var8 = MathHelper.func_76128_c((var2.field_72334_f + 2.0) / 16.0);

      for(int var9 = var5; var9 <= var6; ++var9) {
         for(int var10 = var7; var10 <= var8; ++var10) {
            if (this.func_72916_c(var9, var10)) {
               this.func_72964_e(var9, var10).func_76588_a(var1, var2, var4, var3);
            }
         }
      }

      return var4;
   }

   public List func_72872_a(Class var1, AxisAlignedBB var2) {
      return this.func_82733_a(var1, var2, null);
   }

   public List func_82733_a(Class var1, AxisAlignedBB var2, IEntitySelector var3) {
      int var4 = MathHelper.func_76128_c((var2.field_72340_a - 2.0) / 16.0);
      int var5 = MathHelper.func_76128_c((var2.field_72336_d + 2.0) / 16.0);
      int var6 = MathHelper.func_76128_c((var2.field_72339_c - 2.0) / 16.0);
      int var7 = MathHelper.func_76128_c((var2.field_72334_f + 2.0) / 16.0);
      ArrayList var8 = new ArrayList();

      for(int var9 = var4; var9 <= var5; ++var9) {
         for(int var10 = var6; var10 <= var7; ++var10) {
            if (this.func_72916_c(var9, var10)) {
               this.func_72964_e(var9, var10).func_76618_a(var1, var2, var8, var3);
            }
         }
      }

      return var8;
   }

   public Entity func_72857_a(Class var1, AxisAlignedBB var2, Entity var3) {
      List var4 = this.func_72872_a(var1, var2);
      Entity var5 = null;
      double var6 = 1.7976931348623157E308;

      for(int var8 = 0; var8 < var4.size(); ++var8) {
         Entity var9 = (Entity)var4.get(var8);
         if (var9 != var3) {
            double var10 = var3.func_70068_e(var9);
            if (!(var10 > var6)) {
               var5 = var9;
               var6 = var10;
            }
         }
      }

      return var5;
   }

   public abstract Entity func_73045_a(int var1);

   public List func_72910_y() {
      return this.field_72996_f;
   }

   public void func_147476_b(int var1, int var2, int var3, TileEntity var4) {
      if (this.func_72899_e(var1, var2, var3)) {
         this.func_72938_d(var1, var3).func_76630_e();
      }
   }

   public int func_72907_a(Class var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < this.field_72996_f.size(); ++var3) {
         Entity var4 = (Entity)this.field_72996_f.get(var3);
         if ((!(var4 instanceof EntityLiving) || !((EntityLiving)var4).func_104002_bU()) && var1.isAssignableFrom(var4.getClass())) {
            ++var2;
         }
      }

      return var2;
   }

   public void func_72868_a(List var1) {
      this.field_72996_f.addAll(var1);

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.func_72923_a((Entity)var1.get(var2));
      }
   }

   public void func_72828_b(List var1) {
      this.field_72997_g.addAll(var1);
   }

   public boolean func_147472_a(Block var1, int var2, int var3, int var4, boolean var5, int var6, Entity var7, ItemStack var8) {
      Block var9 = this.func_147439_a(var2, var3, var4);
      AxisAlignedBB var10 = var5 ? null : var1.func_149668_a(this, var2, var3, var4);
      if (var10 != null && !this.func_72917_a(var10, var7)) {
         return false;
      } else if (var9.func_149688_o() == Material.field_151594_q && var1 == Blocks.field_150467_bQ) {
         return true;
      } else {
         return var9.func_149688_o().func_76222_j() && var1.func_149705_a(this, var2, var3, var4, var6, var8);
      }
   }

   public PathEntity func_72865_a(Entity var1, Entity var2, float var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      this.field_72984_F.func_76320_a("pathfind");
      int var8 = MathHelper.func_76128_c(var1.field_70165_t);
      int var9 = MathHelper.func_76128_c(var1.field_70163_u + 1.0);
      int var10 = MathHelper.func_76128_c(var1.field_70161_v);
      int var11 = (int)(var3 + 16.0F);
      int var12 = var8 - var11;
      int var13 = var9 - var11;
      int var14 = var10 - var11;
      int var15 = var8 + var11;
      int var16 = var9 + var11;
      int var17 = var10 + var11;
      ChunkCache var18 = new ChunkCache(this, var12, var13, var14, var15, var16, var17, 0);
      PathEntity var19 = new PathFinder(var18, var4, var5, var6, var7).func_75856_a(var1, var2, var3);
      this.field_72984_F.func_76319_b();
      return var19;
   }

   public PathEntity func_72844_a(Entity var1, int var2, int var3, int var4, float var5, boolean var6, boolean var7, boolean var8, boolean var9) {
      this.field_72984_F.func_76320_a("pathfind");
      int var10 = MathHelper.func_76128_c(var1.field_70165_t);
      int var11 = MathHelper.func_76128_c(var1.field_70163_u);
      int var12 = MathHelper.func_76128_c(var1.field_70161_v);
      int var13 = (int)(var5 + 8.0F);
      int var14 = var10 - var13;
      int var15 = var11 - var13;
      int var16 = var12 - var13;
      int var17 = var10 + var13;
      int var18 = var11 + var13;
      int var19 = var12 + var13;
      ChunkCache var20 = new ChunkCache(this, var14, var15, var16, var17, var18, var19, 0);
      PathEntity var21 = new PathFinder(var20, var6, var7, var8, var9).func_75859_a(var1, var2, var3, var4, var5);
      this.field_72984_F.func_76319_b();
      return var21;
   }

   @Override
   public int func_72879_k(int var1, int var2, int var3, int var4) {
      return this.func_147439_a(var1, var2, var3).func_149748_c(this, var1, var2, var3, var4);
   }

   public int func_94577_B(int var1, int var2, int var3) {
      int var4 = 0;
      var4 = Math.max(var4, this.func_72879_k(var1, var2 - 1, var3, 0));
      if (var4 >= 15) {
         return var4;
      } else {
         var4 = Math.max(var4, this.func_72879_k(var1, var2 + 1, var3, 1));
         if (var4 >= 15) {
            return var4;
         } else {
            var4 = Math.max(var4, this.func_72879_k(var1, var2, var3 - 1, 2));
            if (var4 >= 15) {
               return var4;
            } else {
               var4 = Math.max(var4, this.func_72879_k(var1, var2, var3 + 1, 3));
               if (var4 >= 15) {
                  return var4;
               } else {
                  var4 = Math.max(var4, this.func_72879_k(var1 - 1, var2, var3, 4));
                  if (var4 >= 15) {
                     return var4;
                  } else {
                     var4 = Math.max(var4, this.func_72879_k(var1 + 1, var2, var3, 5));
                     return var4 >= 15 ? var4 : var4;
                  }
               }
            }
         }
      }
   }

   public boolean func_94574_k(int var1, int var2, int var3, int var4) {
      return this.func_72878_l(var1, var2, var3, var4) > 0;
   }

   public int func_72878_l(int var1, int var2, int var3, int var4) {
      return this.func_147439_a(var1, var2, var3).func_149721_r()
         ? this.func_94577_B(var1, var2, var3)
         : this.func_147439_a(var1, var2, var3).func_149709_b(this, var1, var2, var3, var4);
   }

   public boolean func_72864_z(int var1, int var2, int var3) {
      if (this.func_72878_l(var1, var2 - 1, var3, 0) > 0) {
         return true;
      } else if (this.func_72878_l(var1, var2 + 1, var3, 1) > 0) {
         return true;
      } else if (this.func_72878_l(var1, var2, var3 - 1, 2) > 0) {
         return true;
      } else if (this.func_72878_l(var1, var2, var3 + 1, 3) > 0) {
         return true;
      } else if (this.func_72878_l(var1 - 1, var2, var3, 4) > 0) {
         return true;
      } else {
         return this.func_72878_l(var1 + 1, var2, var3, 5) > 0;
      }
   }

   public int func_94572_D(int var1, int var2, int var3) {
      int var4 = 0;

      for(int var5 = 0; var5 < 6; ++var5) {
         int var6 = this.func_72878_l(var1 + Facing.field_71586_b[var5], var2 + Facing.field_71587_c[var5], var3 + Facing.field_71585_d[var5], var5);
         if (var6 >= 15) {
            return 15;
         }

         if (var6 > var4) {
            var4 = var6;
         }
      }

      return var4;
   }

   public EntityPlayer func_72890_a(Entity var1, double var2) {
      return this.func_72977_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var2);
   }

   public EntityPlayer func_72977_a(double var1, double var3, double var5, double var7) {
      double var9 = -1.0;
      EntityPlayer var11 = null;

      for(int var12 = 0; var12 < this.field_73010_i.size(); ++var12) {
         EntityPlayer var13 = (EntityPlayer)this.field_73010_i.get(var12);
         double var14 = var13.func_70092_e(var1, var3, var5);
         if ((var7 < 0.0 || var14 < var7 * var7) && (var9 == -1.0 || var14 < var9)) {
            var9 = var14;
            var11 = var13;
         }
      }

      return var11;
   }

   public EntityPlayer func_72856_b(Entity var1, double var2) {
      return this.func_72846_b(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var2);
   }

   public EntityPlayer func_72846_b(double var1, double var3, double var5, double var7) {
      double var9 = -1.0;
      EntityPlayer var11 = null;

      for(int var12 = 0; var12 < this.field_73010_i.size(); ++var12) {
         EntityPlayer var13 = (EntityPlayer)this.field_73010_i.get(var12);
         if (!var13.field_71075_bZ.field_75102_a && var13.func_70089_S()) {
            double var14 = var13.func_70092_e(var1, var3, var5);
            double var16 = var7;
            if (var13.func_70093_af()) {
               var16 = var7 * 0.800000011920929;
            }

            if (var13.func_82150_aj()) {
               float var18 = var13.func_82243_bO();
               if (var18 < 0.1F) {
                  var18 = 0.1F;
               }

               var16 *= (double)(0.7F * var18);
            }

            if ((var7 < 0.0 || var14 < var16 * var16) && (var9 == -1.0 || var14 < var9)) {
               var9 = var14;
               var11 = var13;
            }
         }
      }

      return var11;
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

   public void func_72906_B() {
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

   public ChunkCoordinates func_72861_E() {
      return new ChunkCoordinates(this.field_72986_A.func_76079_c(), this.field_72986_A.func_76075_d(), this.field_72986_A.func_76074_e());
   }

   public void func_72950_A(int var1, int var2, int var3) {
      this.field_72986_A.func_76081_a(var1, var2, var3);
   }

   public void func_72897_h(Entity var1) {
      int var2 = MathHelper.func_76128_c(var1.field_70165_t / 16.0);
      int var3 = MathHelper.func_76128_c(var1.field_70161_v / 16.0);
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

   public boolean func_72962_a(EntityPlayer var1, int var2, int var3, int var4) {
      return true;
   }

   public void func_72960_a(Entity var1, byte var2) {
   }

   public IChunkProvider func_72863_F() {
      return this.field_73020_y;
   }

   public void func_147452_c(int var1, int var2, int var3, Block var4, int var5, int var6) {
      var4.func_149696_a(this, var1, var2, var3, var5, var6);
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
      return (double)this.func_72819_i(1.0F) > 0.9;
   }

   public boolean func_72896_J() {
      return (double)this.func_72867_j(1.0F) > 0.2;
   }

   public boolean func_72951_B(int var1, int var2, int var3) {
      if (!this.func_72896_J()) {
         return false;
      } else if (!this.func_72937_j(var1, var2, var3)) {
         return false;
      } else if (this.func_72874_g(var1, var3) > var2) {
         return false;
      } else {
         BiomeGenBase var4 = this.func_72807_a(var1, var3);
         if (var4.func_76746_c()) {
            return false;
         } else {
            return this.func_147478_e(var1, var2, var3, false) ? false : var4.func_76738_d();
         }
      }
   }

   public boolean func_72958_C(int var1, int var2, int var3) {
      BiomeGenBase var4 = this.func_72807_a(var1, var3);
      return var4.func_76736_e();
   }

   public void func_72823_a(String var1, WorldSavedData var2) {
      this.field_72988_C.func_75745_a(var1, var2);
   }

   public WorldSavedData func_72943_a(Class var1, String var2) {
      return this.field_72988_C.func_75742_a(var1, var2);
   }

   public int func_72841_b(String var1) {
      return this.field_72988_C.func_75743_a(var1);
   }

   public void func_82739_e(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = 0; var6 < this.field_73021_x.size(); ++var6) {
         ((IWorldAccess)this.field_73021_x.get(var6)).func_82746_a(var1, var2, var3, var4, var5);
      }
   }

   public void func_72926_e(int var1, int var2, int var3, int var4, int var5) {
      this.func_72889_a(null, var1, var2, var3, var4, var5);
   }

   public void func_72889_a(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6) {
      try {
         for(int var7 = 0; var7 < this.field_73021_x.size(); ++var7) {
            ((IWorldAccess)this.field_73021_x.get(var7)).func_72706_a(var1, var2, var3, var4, var5, var6);
         }
      } catch (Throwable var10) {
         CrashReport var8 = CrashReport.func_85055_a(var10, "Playing level event");
         CrashReportCategory var9 = var8.func_85058_a("Level event being played");
         var9.func_71507_a("Block coordinates", CrashReportCategory.func_85071_a(var3, var4, var5));
         var9.func_71507_a("Event source", var1);
         var9.func_71507_a("Event type", var2);
         var9.func_71507_a("Event data", var6);
         throw new ReportedException(var8);
      }
   }

   @Override
   public int func_72800_K() {
      return 256;
   }

   public int func_72940_L() {
      return this.field_73011_w.field_76576_e ? 128 : 256;
   }

   public Random func_72843_D(int var1, int var2, int var3) {
      long var4 = (long)var1 * 341873128712L + (long)var2 * 132897987541L + this.func_72912_H().func_76063_b() + (long)var3;
      this.field_73012_v.setSeed(var4);
      return this.field_73012_v;
   }

   public ChunkPosition func_147440_b(String var1, int var2, int var3, int var4) {
      return this.func_72863_F().func_147416_a(this, var1, var2, var3, var4);
   }

   @Override
   public boolean func_72806_N() {
      return false;
   }

   public double func_72919_O() {
      return this.field_72986_A.func_76067_t() == WorldType.field_77138_c ? 0.0 : 63.0;
   }

   public CrashReportCategory func_72914_a(CrashReport var1) {
      CrashReportCategory var2 = var1.func_85057_a("Affected level", 1);
      var2.func_71507_a("Level name", this.field_72986_A == null ? "????" : this.field_72986_A.func_76065_j());
      var2.func_71500_a("All players", new World$3(this));
      var2.func_71500_a("Chunk stats", new World$4(this));

      try {
         this.field_72986_A.func_85118_a(var2);
      } catch (Throwable var4) {
         var2.func_71499_a("Level Data Unobtainable", var4);
      }

      return var2;
   }

   public void func_147443_d(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = 0; var6 < this.field_73021_x.size(); ++var6) {
         IWorldAccess var7 = (IWorldAccess)this.field_73021_x.get(var6);
         var7.func_147587_b(var1, var2, var3, var4, var5);
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

   public void func_147453_f(int var1, int var2, int var3, Block var4) {
      for(int var5 = 0; var5 < 4; ++var5) {
         int var6 = var1 + Direction.field_71583_a[var5];
         int var7 = var3 + Direction.field_71581_b[var5];
         Block var8 = this.func_147439_a(var6, var2, var7);
         if (Blocks.field_150441_bU.func_149907_e(var8)) {
            var8.func_149695_a(this, var6, var2, var7, var4);
         } else if (var8.func_149721_r()) {
            var6 += Direction.field_71583_a[var5];
            var7 += Direction.field_71581_b[var5];
            Block var9 = this.func_147439_a(var6, var2, var7);
            if (Blocks.field_150441_bU.func_149907_e(var9)) {
               var9.func_149695_a(this, var6, var2, var7, var4);
            }
         }
      }
   }

   public float func_147462_b(double var1, double var3, double var5) {
      return this.func_147473_B(MathHelper.func_76128_c(var1), MathHelper.func_76128_c(var3), MathHelper.func_76128_c(var5));
   }

   public float func_147473_B(int var1, int var2, int var3) {
      float var4 = 0.0F;
      boolean var5 = this.field_73013_u == EnumDifficulty.HARD;
      if (this.func_72899_e(var1, var2, var3)) {
         float var6 = this.func_130001_d();
         var4 += MathHelper.func_76131_a((float)this.func_72938_d(var1, var3).field_111204_q / 3600000.0F, 0.0F, 1.0F) * (var5 ? 1.0F : 0.75F);
         var4 += var6 * 0.25F;
      }

      if (this.field_73013_u == EnumDifficulty.EASY || this.field_73013_u == EnumDifficulty.PEACEFUL) {
         var4 *= (float)this.field_73013_u.func_151525_a() / 2.0F;
      }

      return MathHelper.func_76131_a(var4, 0.0F, var5 ? 1.5F : 1.0F);
   }

   public void func_147450_X() {
      for(IWorldAccess var2 : this.field_73021_x) {
         var2.func_147584_b();
      }
   }
}
