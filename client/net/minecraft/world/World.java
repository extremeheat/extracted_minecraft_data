package net.minecraft.world;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMaterialMatcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathWorldListener;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SessionLockException;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World implements IEntityReader, IWorld, IWorldReader, AutoCloseable {
   protected static final Logger field_195596_d = LogManager.getLogger();
   private static final EnumFacing[] field_200007_a = EnumFacing.values();
   private int field_181546_a = 63;
   public final List<Entity> field_72996_f = Lists.newArrayList();
   protected final List<Entity> field_72997_g = Lists.newArrayList();
   public final List<TileEntity> field_147482_g = Lists.newArrayList();
   public final List<TileEntity> field_175730_i = Lists.newArrayList();
   private final List<TileEntity> field_147484_a = Lists.newArrayList();
   private final List<TileEntity> field_147483_b = Lists.newArrayList();
   public final List<EntityPlayer> field_73010_i = Lists.newArrayList();
   public final List<Entity> field_73007_j = Lists.newArrayList();
   protected final IntHashMap<Entity> field_175729_l = new IntHashMap();
   private final long field_73001_c = 16777215L;
   private int field_73008_k;
   protected int field_73005_l = (new Random()).nextInt();
   protected final int field_73006_m = 1013904223;
   protected float field_73003_n;
   protected float field_73004_o;
   protected float field_73018_p;
   protected float field_73017_q;
   private int field_73016_r;
   public final Random field_73012_v = new Random();
   public final Dimension field_73011_w;
   protected PathWorldListener field_184152_t = new PathWorldListener();
   protected List<IWorldEventListener> field_73021_x;
   protected IChunkProvider field_73020_y;
   protected final ISaveHandler field_73019_z;
   protected WorldInfo field_72986_A;
   @Nullable
   private final WorldSavedDataStorage field_72988_C;
   protected VillageCollection field_72982_D;
   public final Profiler field_72984_F;
   public final boolean field_72995_K;
   protected boolean field_72985_G;
   protected boolean field_72992_H;
   private boolean field_147481_N;
   private final WorldBorder field_175728_M;
   int[] field_72994_J;

   protected World(ISaveHandler var1, @Nullable WorldSavedDataStorage var2, WorldInfo var3, Dimension var4, Profiler var5, boolean var6) {
      super();
      this.field_73021_x = Lists.newArrayList(new IWorldEventListener[]{this.field_184152_t});
      this.field_72985_G = true;
      this.field_72992_H = true;
      this.field_72994_J = new int['\u8000'];
      this.field_73019_z = var1;
      this.field_72988_C = var2;
      this.field_72984_F = var5;
      this.field_72986_A = var3;
      this.field_73011_w = var4;
      this.field_72995_K = var6;
      this.field_175728_M = var4.func_177501_r();
   }

   public Biome func_180494_b(BlockPos var1) {
      if (this.func_175667_e(var1)) {
         Chunk var2 = this.func_175726_f(var1);

         try {
            return var2.func_201600_k(var1);
         } catch (Throwable var6) {
            CrashReport var4 = CrashReport.func_85055_a(var6, "Getting biome");
            CrashReportCategory var5 = var4.func_85058_a("Coordinates of biome request");
            var5.func_189529_a("Location", () -> {
               return CrashReportCategory.func_180522_a(var1);
            });
            throw new ReportedException(var4);
         }
      } else {
         return this.field_73020_y.func_201711_g().func_202090_b().func_180300_a(var1, Biomes.field_76772_c);
      }
   }

   protected abstract IChunkProvider func_72970_h();

   public void func_72963_a(WorldSettings var1) {
      this.field_72986_A.func_76091_d(true);
   }

   public boolean func_201670_d() {
      return this.field_72995_K;
   }

   @Nullable
   public MinecraftServer func_73046_m() {
      return null;
   }

   public void func_72974_f() {
      this.func_175652_B(new BlockPos(8, 64, 8));
   }

   public IBlockState func_184141_c(BlockPos var1) {
      BlockPos var2;
      for(var2 = new BlockPos(var1.func_177958_n(), this.func_181545_F(), var1.func_177952_p()); !this.func_175623_d(var2.func_177984_a()); var2 = var2.func_177984_a()) {
      }

      return this.func_180495_p(var2);
   }

   public static boolean func_175701_a(BlockPos var0) {
      return !func_189509_E(var0) && var0.func_177958_n() >= -30000000 && var0.func_177952_p() >= -30000000 && var0.func_177958_n() < 30000000 && var0.func_177952_p() < 30000000;
   }

   public static boolean func_189509_E(BlockPos var0) {
      return var0.func_177956_o() < 0 || var0.func_177956_o() >= 256;
   }

   public boolean func_175623_d(BlockPos var1) {
      return this.func_180495_p(var1).func_196958_f();
   }

   public Chunk func_175726_f(BlockPos var1) {
      return this.func_72964_e(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4);
   }

   public Chunk func_72964_e(int var1, int var2) {
      Chunk var3 = this.field_73020_y.func_186025_d(var1, var2, true, true);
      if (var3 == null) {
         throw new IllegalStateException("Should always be able to create a chunk!");
      } else {
         return var3;
      }
   }

   public boolean func_180501_a(BlockPos var1, IBlockState var2, int var3) {
      if (func_189509_E(var1)) {
         return false;
      } else if (!this.field_72995_K && this.field_72986_A.func_76067_t() == WorldType.field_180272_g) {
         return false;
      } else {
         Chunk var4 = this.func_175726_f(var1);
         Block var5 = var2.func_177230_c();
         IBlockState var6 = var4.func_177436_a(var1, var2, (var3 & 64) != 0);
         if (var6 == null) {
            return false;
         } else {
            IBlockState var7 = this.func_180495_p(var1);
            if (var7.func_200016_a(this, var1) != var6.func_200016_a(this, var1) || var7.func_185906_d() != var6.func_185906_d()) {
               this.field_72984_F.func_76320_a("checkLight");
               this.func_175664_x(var1);
               this.field_72984_F.func_76319_b();
            }

            if (var7 == var2) {
               if (var6 != var7) {
                  this.func_175704_b(var1, var1);
               }

               if ((var3 & 2) != 0 && (!this.field_72995_K || (var3 & 4) == 0) && var4.func_150802_k()) {
                  this.func_184138_a(var1, var6, var2, var3);
               }

               if (!this.field_72995_K && (var3 & 1) != 0) {
                  this.func_195592_c(var1, var6.func_177230_c());
                  if (var2.func_185912_n()) {
                     this.func_175666_e(var1, var5);
                  }
               }

               if ((var3 & 16) == 0) {
                  int var8 = var3 & -2;
                  var6.func_196948_b(this, var1, var8);
                  var2.func_196946_a(this, var1, var8);
                  var2.func_196948_b(this, var1, var8);
               }
            }

            return true;
         }
      }
   }

   public boolean func_175698_g(BlockPos var1) {
      IFluidState var2 = this.func_204610_c(var1);
      return this.func_180501_a(var1, var2.func_206883_i(), 3);
   }

   public boolean func_175655_b(BlockPos var1, boolean var2) {
      IBlockState var3 = this.func_180495_p(var1);
      if (var3.func_196958_f()) {
         return false;
      } else {
         IFluidState var4 = this.func_204610_c(var1);
         this.func_175718_b(2001, var1, Block.func_196246_j(var3));
         if (var2) {
            var3.func_196949_c(this, var1, 0);
         }

         return this.func_180501_a(var1, var4.func_206883_i(), 3);
      }
   }

   public boolean func_175656_a(BlockPos var1, IBlockState var2) {
      return this.func_180501_a(var1, var2, 3);
   }

   public void func_184138_a(BlockPos var1, IBlockState var2, IBlockState var3, int var4) {
      for(int var5 = 0; var5 < this.field_73021_x.size(); ++var5) {
         ((IWorldEventListener)this.field_73021_x.get(var5)).func_184376_a(this, var1, var2, var3, var4);
      }

   }

   public void func_195592_c(BlockPos var1, Block var2) {
      if (this.field_72986_A.func_76067_t() != WorldType.field_180272_g) {
         this.func_195593_d(var1, var2);
      }

   }

   public void func_72975_g(int var1, int var2, int var3, int var4) {
      int var5;
      if (var3 > var4) {
         var5 = var4;
         var4 = var3;
         var3 = var5;
      }

      if (this.field_73011_w.func_191066_m()) {
         for(var5 = var3; var5 <= var4; ++var5) {
            this.func_180500_c(EnumLightType.SKY, new BlockPos(var1, var5, var2));
         }
      }

      this.func_147458_c(var1, var3, var2, var1, var4, var2);
   }

   public void func_175704_b(BlockPos var1, BlockPos var2) {
      this.func_147458_c(var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p(), var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p());
   }

   public void func_147458_c(int var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = 0; var7 < this.field_73021_x.size(); ++var7) {
         ((IWorldEventListener)this.field_73021_x.get(var7)).func_147585_a(var1, var2, var3, var4, var5, var6);
      }

   }

   public void func_195593_d(BlockPos var1, Block var2) {
      this.func_190524_a(var1.func_177976_e(), var2, var1);
      this.func_190524_a(var1.func_177974_f(), var2, var1);
      this.func_190524_a(var1.func_177977_b(), var2, var1);
      this.func_190524_a(var1.func_177984_a(), var2, var1);
      this.func_190524_a(var1.func_177978_c(), var2, var1);
      this.func_190524_a(var1.func_177968_d(), var2, var1);
   }

   public void func_175695_a(BlockPos var1, Block var2, EnumFacing var3) {
      if (var3 != EnumFacing.WEST) {
         this.func_190524_a(var1.func_177976_e(), var2, var1);
      }

      if (var3 != EnumFacing.EAST) {
         this.func_190524_a(var1.func_177974_f(), var2, var1);
      }

      if (var3 != EnumFacing.DOWN) {
         this.func_190524_a(var1.func_177977_b(), var2, var1);
      }

      if (var3 != EnumFacing.UP) {
         this.func_190524_a(var1.func_177984_a(), var2, var1);
      }

      if (var3 != EnumFacing.NORTH) {
         this.func_190524_a(var1.func_177978_c(), var2, var1);
      }

      if (var3 != EnumFacing.SOUTH) {
         this.func_190524_a(var1.func_177968_d(), var2, var1);
      }

   }

   public void func_190524_a(BlockPos var1, Block var2, BlockPos var3) {
      if (!this.field_72995_K) {
         IBlockState var4 = this.func_180495_p(var1);

         try {
            var4.func_189546_a(this, var1, var2, var3);
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.func_85055_a(var8, "Exception while updating neighbours");
            CrashReportCategory var7 = var6.func_85058_a("Block being updated");
            var7.func_189529_a("Source block type", () -> {
               try {
                  return String.format("ID #%s (%s // %s)", IRegistry.field_212618_g.func_177774_c(var2), var2.func_149739_a(), var2.getClass().getCanonicalName());
               } catch (Throwable var2x) {
                  return "ID #" + IRegistry.field_212618_g.func_177774_c(var2);
               }
            });
            CrashReportCategory.func_175750_a(var7, var1, var4);
            throw new ReportedException(var6);
         }
      }
   }

   public boolean func_175678_i(BlockPos var1) {
      return this.func_175726_f(var1).func_177444_d(var1);
   }

   public int func_201669_a(BlockPos var1, int var2) {
      if (var1.func_177958_n() >= -30000000 && var1.func_177952_p() >= -30000000 && var1.func_177958_n() < 30000000 && var1.func_177952_p() < 30000000) {
         if (var1.func_177956_o() < 0) {
            return 0;
         } else {
            if (var1.func_177956_o() >= 256) {
               var1 = new BlockPos(var1.func_177958_n(), 255, var1.func_177952_p());
            }

            return this.func_175726_f(var1).func_177443_a(var1, var2);
         }
      } else {
         return 15;
      }
   }

   public int func_201676_a(Heightmap.Type var1, int var2, int var3) {
      int var4;
      if (var2 >= -30000000 && var3 >= -30000000 && var2 < 30000000 && var3 < 30000000) {
         if (this.func_175680_a(var2 >> 4, var3 >> 4, true)) {
            var4 = this.func_72964_e(var2 >> 4, var3 >> 4).func_201576_a(var1, var2 & 15, var3 & 15) + 1;
         } else {
            var4 = 0;
         }
      } else {
         var4 = this.func_181545_F() + 1;
      }

      return var4;
   }

   @Deprecated
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

   public int func_175705_a(EnumLightType var1, BlockPos var2) {
      if (!this.field_73011_w.func_191066_m() && var1 == EnumLightType.SKY) {
         return 0;
      } else {
         if (var2.func_177956_o() < 0) {
            var2 = new BlockPos(var2.func_177958_n(), 0, var2.func_177952_p());
         }

         if (!func_175701_a(var2)) {
            return var1.field_77198_c;
         } else if (!this.func_175667_e(var2)) {
            return var1.field_77198_c;
         } else if (this.func_180495_p(var2).func_200130_c(this, var2)) {
            int var3 = this.func_175642_b(var1, var2.func_177984_a());
            int var4 = this.func_175642_b(var1, var2.func_177974_f());
            int var5 = this.func_175642_b(var1, var2.func_177976_e());
            int var6 = this.func_175642_b(var1, var2.func_177968_d());
            int var7 = this.func_175642_b(var1, var2.func_177978_c());
            if (var4 > var3) {
               var3 = var4;
            }

            if (var5 > var3) {
               var3 = var5;
            }

            if (var6 > var3) {
               var3 = var6;
            }

            if (var7 > var3) {
               var3 = var7;
            }

            return var3;
         } else {
            return this.func_175726_f(var2).func_177413_a(var1, var2);
         }
      }
   }

   public int func_175642_b(EnumLightType var1, BlockPos var2) {
      if (var2.func_177956_o() < 0) {
         var2 = new BlockPos(var2.func_177958_n(), 0, var2.func_177952_p());
      }

      if (!func_175701_a(var2)) {
         return var1.field_77198_c;
      } else {
         return !this.func_175667_e(var2) ? var1.field_77198_c : this.func_175726_f(var2).func_177413_a(var1, var2);
      }
   }

   public void func_175653_a(EnumLightType var1, BlockPos var2, int var3) {
      if (func_175701_a(var2)) {
         if (this.func_175667_e(var2)) {
            this.func_175726_f(var2).func_177431_a(var1, var2, var3);
            this.func_175679_n(var2);
         }
      }
   }

   public void func_175679_n(BlockPos var1) {
      for(int var2 = 0; var2 < this.field_73021_x.size(); ++var2) {
         ((IWorldEventListener)this.field_73021_x.get(var2)).func_174959_b(var1);
      }

   }

   public int func_175626_b(BlockPos var1, int var2) {
      int var3 = this.func_175705_a(EnumLightType.SKY, var1);
      int var4 = this.func_175705_a(EnumLightType.BLOCK, var1);
      if (var4 < var2) {
         var4 = var2;
      }

      return var3 << 20 | var4 << 4;
   }

   public IBlockState func_180495_p(BlockPos var1) {
      if (func_189509_E(var1)) {
         return Blocks.field_201940_ji.func_176223_P();
      } else {
         Chunk var2 = this.func_175726_f(var1);
         return var2.func_180495_p(var1);
      }
   }

   public IFluidState func_204610_c(BlockPos var1) {
      if (func_189509_E(var1)) {
         return Fluids.field_204541_a.func_207188_f();
      } else {
         Chunk var2 = this.func_175726_f(var1);
         return var2.func_204610_c(var1);
      }
   }

   public boolean func_72935_r() {
      return this.field_73008_k < 4;
   }

   @Nullable
   public RayTraceResult func_72933_a(Vec3d var1, Vec3d var2) {
      return this.func_200259_a(var1, var2, RayTraceFluidMode.NEVER, false, false);
   }

   @Nullable
   public RayTraceResult func_200260_a(Vec3d var1, Vec3d var2, RayTraceFluidMode var3) {
      return this.func_200259_a(var1, var2, var3, false, false);
   }

   @Nullable
   public RayTraceResult func_200259_a(Vec3d var1, Vec3d var2, RayTraceFluidMode var3, boolean var4, boolean var5) {
      double var6 = var1.field_72450_a;
      double var8 = var1.field_72448_b;
      double var10 = var1.field_72449_c;
      if (!Double.isNaN(var6) && !Double.isNaN(var8) && !Double.isNaN(var10)) {
         if (!Double.isNaN(var2.field_72450_a) && !Double.isNaN(var2.field_72448_b) && !Double.isNaN(var2.field_72449_c)) {
            int var12 = MathHelper.func_76128_c(var2.field_72450_a);
            int var13 = MathHelper.func_76128_c(var2.field_72448_b);
            int var14 = MathHelper.func_76128_c(var2.field_72449_c);
            int var15 = MathHelper.func_76128_c(var6);
            int var16 = MathHelper.func_76128_c(var8);
            int var17 = MathHelper.func_76128_c(var10);
            BlockPos var18 = new BlockPos(var15, var16, var17);
            IBlockState var19 = this.func_180495_p(var18);
            IFluidState var20 = this.func_204610_c(var18);
            boolean var21;
            boolean var22;
            if (!var4 || !var19.func_196952_d(this, var18).func_197766_b()) {
               var21 = var19.func_177230_c().func_200293_a(var19);
               var22 = var3.field_209544_d.test(var20);
               if (var21 || var22) {
                  RayTraceResult var23 = null;
                  if (var21) {
                     var23 = Block.func_180636_a(var19, this, var18, var1, var2);
                  }

                  if (var23 == null && var22) {
                     var23 = VoxelShapes.func_197873_a(0.0D, 0.0D, 0.0D, 1.0D, (double)var20.func_206885_f(), 1.0D).func_212433_a(var1, var2, var18);
                  }

                  if (var23 != null) {
                     return var23;
                  }
               }
            }

            RayTraceResult var48 = null;
            int var49 = 200;

            while(var49-- >= 0) {
               if (Double.isNaN(var6) || Double.isNaN(var8) || Double.isNaN(var10)) {
                  return null;
               }

               if (var15 == var12 && var16 == var13 && var17 == var14) {
                  return var5 ? var48 : null;
               }

               var21 = true;
               var22 = true;
               boolean var50 = true;
               double var24 = 999.0D;
               double var26 = 999.0D;
               double var28 = 999.0D;
               if (var12 > var15) {
                  var24 = (double)var15 + 1.0D;
               } else if (var12 < var15) {
                  var24 = (double)var15 + 0.0D;
               } else {
                  var21 = false;
               }

               if (var13 > var16) {
                  var26 = (double)var16 + 1.0D;
               } else if (var13 < var16) {
                  var26 = (double)var16 + 0.0D;
               } else {
                  var22 = false;
               }

               if (var14 > var17) {
                  var28 = (double)var17 + 1.0D;
               } else if (var14 < var17) {
                  var28 = (double)var17 + 0.0D;
               } else {
                  var50 = false;
               }

               double var30 = 999.0D;
               double var32 = 999.0D;
               double var34 = 999.0D;
               double var36 = var2.field_72450_a - var6;
               double var38 = var2.field_72448_b - var8;
               double var40 = var2.field_72449_c - var10;
               if (var21) {
                  var30 = (var24 - var6) / var36;
               }

               if (var22) {
                  var32 = (var26 - var8) / var38;
               }

               if (var50) {
                  var34 = (var28 - var10) / var40;
               }

               if (var30 == -0.0D) {
                  var30 = -1.0E-4D;
               }

               if (var32 == -0.0D) {
                  var32 = -1.0E-4D;
               }

               if (var34 == -0.0D) {
                  var34 = -1.0E-4D;
               }

               EnumFacing var42;
               if (var30 < var32 && var30 < var34) {
                  var42 = var12 > var15 ? EnumFacing.WEST : EnumFacing.EAST;
                  var6 = var24;
                  var8 += var38 * var30;
                  var10 += var40 * var30;
               } else if (var32 < var34) {
                  var42 = var13 > var16 ? EnumFacing.DOWN : EnumFacing.UP;
                  var6 += var36 * var32;
                  var8 = var26;
                  var10 += var40 * var32;
               } else {
                  var42 = var14 > var17 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                  var6 += var36 * var34;
                  var8 += var38 * var34;
                  var10 = var28;
               }

               var15 = MathHelper.func_76128_c(var6) - (var42 == EnumFacing.EAST ? 1 : 0);
               var16 = MathHelper.func_76128_c(var8) - (var42 == EnumFacing.UP ? 1 : 0);
               var17 = MathHelper.func_76128_c(var10) - (var42 == EnumFacing.SOUTH ? 1 : 0);
               var18 = new BlockPos(var15, var16, var17);
               IBlockState var43 = this.func_180495_p(var18);
               IFluidState var44 = this.func_204610_c(var18);
               if (!var4 || var43.func_185904_a() == Material.field_151567_E || !var43.func_196952_d(this, var18).func_197766_b()) {
                  boolean var45 = var43.func_177230_c().func_200293_a(var43);
                  boolean var46 = var3.field_209544_d.test(var44);
                  if (!var45 && !var46) {
                     var48 = new RayTraceResult(RayTraceResult.Type.MISS, new Vec3d(var6, var8, var10), var42, var18);
                  } else {
                     RayTraceResult var47 = null;
                     if (var45) {
                        var47 = Block.func_180636_a(var43, this, var18, var1, var2);
                     }

                     if (var47 == null && var46) {
                        var47 = VoxelShapes.func_197873_a(0.0D, 0.0D, 0.0D, 1.0D, (double)var44.func_206885_f(), 1.0D).func_212433_a(var1, var2, var18);
                     }

                     if (var47 != null) {
                        return var47;
                     }
                  }
               }
            }

            return var5 ? var48 : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void func_184133_a(@Nullable EntityPlayer var1, BlockPos var2, SoundEvent var3, SoundCategory var4, float var5, float var6) {
      this.func_184148_a(var1, (double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, var3, var4, var5, var6);
   }

   public void func_184148_a(@Nullable EntityPlayer var1, double var2, double var4, double var6, SoundEvent var8, SoundCategory var9, float var10, float var11) {
      for(int var12 = 0; var12 < this.field_73021_x.size(); ++var12) {
         ((IWorldEventListener)this.field_73021_x.get(var12)).func_184375_a(var1, var8, var9, var2, var4, var6, var10, var11);
      }

   }

   public void func_184134_a(double var1, double var3, double var5, SoundEvent var7, SoundCategory var8, float var9, float var10, boolean var11) {
   }

   public void func_184149_a(BlockPos var1, @Nullable SoundEvent var2) {
      for(int var3 = 0; var3 < this.field_73021_x.size(); ++var3) {
         ((IWorldEventListener)this.field_73021_x.get(var3)).func_184377_a(var2, var1);
      }

   }

   public void func_195594_a(IParticleData var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      for(int var14 = 0; var14 < this.field_73021_x.size(); ++var14) {
         ((IWorldEventListener)this.field_73021_x.get(var14)).func_195461_a(var1, var1.func_197554_b().func_197575_f(), var2, var4, var6, var8, var10, var12);
      }

   }

   public void func_195590_a(IParticleData var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      for(int var15 = 0; var15 < this.field_73021_x.size(); ++var15) {
         ((IWorldEventListener)this.field_73021_x.get(var15)).func_195461_a(var1, var1.func_197554_b().func_197575_f() || var2, var3, var5, var7, var9, var11, var13);
      }

   }

   public void func_195589_b(IParticleData var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      for(int var14 = 0; var14 < this.field_73021_x.size(); ++var14) {
         ((IWorldEventListener)this.field_73021_x.get(var14)).func_195462_a(var1, false, true, var2, var4, var6, var8, var10, var12);
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

      if (!var4 && !this.func_175680_a(var2, var3, false)) {
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
         ((IWorldEventListener)this.field_73021_x.get(var2)).func_72703_a(var1);
      }

   }

   protected void func_72847_b(Entity var1) {
      for(int var2 = 0; var2 < this.field_73021_x.size(); ++var2) {
         ((IWorldEventListener)this.field_73021_x.get(var2)).func_72709_b(var1);
      }

   }

   public void func_72900_e(Entity var1) {
      if (var1.func_184207_aI()) {
         var1.func_184226_ay();
      }

      if (var1.func_184218_aH()) {
         var1.func_184210_p();
      }

      var1.func_70106_y();
      if (var1 instanceof EntityPlayer) {
         this.field_73010_i.remove(var1);
         this.func_72854_c();
         this.func_72847_b(var1);
      }

   }

   public void func_72973_f(Entity var1) {
      var1.func_184174_b(false);
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

   public void func_72954_a(IWorldEventListener var1) {
      this.field_73021_x.add(var1);
   }

   public void func_72848_b(IWorldEventListener var1) {
      this.field_73021_x.remove(var1);
   }

   public int func_72967_a(float var1) {
      float var2 = this.func_72826_c(var1);
      float var3 = 1.0F - (MathHelper.func_76134_b(var2 * 6.2831855F) * 2.0F + 0.5F);
      var3 = MathHelper.func_76131_a(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0D - (double)(this.func_72867_j(var1) * 5.0F) / 16.0D));
      var3 = (float)((double)var3 * (1.0D - (double)(this.func_72819_i(var1) * 5.0F) / 16.0D));
      var3 = 1.0F - var3;
      return (int)(var3 * 11.0F);
   }

   public float func_72971_b(float var1) {
      float var2 = this.func_72826_c(var1);
      float var3 = 1.0F - (MathHelper.func_76134_b(var2 * 6.2831855F) * 2.0F + 0.2F);
      var3 = MathHelper.func_76131_a(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0D - (double)(this.func_72867_j(var1) * 5.0F) / 16.0D));
      var3 = (float)((double)var3 * (1.0D - (double)(this.func_72819_i(var1) * 5.0F) / 16.0D));
      return var3 * 0.8F + 0.2F;
   }

   public Vec3d func_72833_a(Entity var1, float var2) {
      float var3 = this.func_72826_c(var2);
      float var4 = MathHelper.func_76134_b(var3 * 6.2831855F) * 2.0F + 0.5F;
      var4 = MathHelper.func_76131_a(var4, 0.0F, 1.0F);
      int var5 = MathHelper.func_76128_c(var1.field_70165_t);
      int var6 = MathHelper.func_76128_c(var1.field_70163_u);
      int var7 = MathHelper.func_76128_c(var1.field_70161_v);
      BlockPos var8 = new BlockPos(var5, var6, var7);
      Biome var9 = this.func_180494_b(var8);
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

      return new Vec3d((double)var12, (double)var13, (double)var14);
   }

   public float func_72929_e(float var1) {
      float var2 = this.func_72826_c(var1);
      return var2 * 6.2831855F;
   }

   public Vec3d func_72824_f(float var1) {
      float var2 = this.func_72826_c(var1);
      float var3 = MathHelper.func_76134_b(var2 * 6.2831855F) * 2.0F + 0.5F;
      var3 = MathHelper.func_76131_a(var3, 0.0F, 1.0F);
      float var4 = 1.0F;
      float var5 = 1.0F;
      float var6 = 1.0F;
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

      return new Vec3d((double)var4, (double)var5, (double)var6);
   }

   public Vec3d func_72948_g(float var1) {
      float var2 = this.func_72826_c(var1);
      return this.field_73011_w.func_76562_b(var2, var1);
   }

   public float func_72880_h(float var1) {
      float var2 = this.func_72826_c(var1);
      float var3 = 1.0F - (MathHelper.func_76134_b(var2 * 6.2831855F) * 2.0F + 0.25F);
      var3 = MathHelper.func_76131_a(var3, 0.0F, 1.0F);
      return var3 * var3 * 0.5F;
   }

   public void func_72939_s() {
      this.field_72984_F.func_76320_a("entities");
      this.field_72984_F.func_76320_a("global");

      int var1;
      Entity var2;
      for(var1 = 0; var1 < this.field_73007_j.size(); ++var1) {
         var2 = (Entity)this.field_73007_j.get(var1);

         try {
            ++var2.field_70173_aa;
            var2.func_70071_h_();
         } catch (Throwable var9) {
            CrashReport var4 = CrashReport.func_85055_a(var9, "Ticking entity");
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

      int var15;
      for(var1 = 0; var1 < this.field_72997_g.size(); ++var1) {
         var2 = (Entity)this.field_72997_g.get(var1);
         int var3 = var2.field_70176_ah;
         var15 = var2.field_70164_aj;
         if (var2.field_70175_ag && this.func_175680_a(var3, var15, true)) {
            this.func_72964_e(var3, var15).func_76622_b(var2);
         }
      }

      for(var1 = 0; var1 < this.field_72997_g.size(); ++var1) {
         this.func_72847_b((Entity)this.field_72997_g.get(var1));
      }

      this.field_72997_g.clear();
      this.func_184147_l();
      this.field_72984_F.func_76318_c("regular");

      CrashReportCategory var6;
      CrashReport var18;
      for(var1 = 0; var1 < this.field_72996_f.size(); ++var1) {
         var2 = (Entity)this.field_72996_f.get(var1);
         Entity var11 = var2.func_184187_bx();
         if (var11 != null) {
            if (!var11.field_70128_L && var11.func_184196_w(var2)) {
               continue;
            }

            var2.func_184210_p();
         }

         this.field_72984_F.func_76320_a("tick");
         if (!var2.field_70128_L && !(var2 instanceof EntityPlayerMP)) {
            try {
               this.func_72870_g(var2);
            } catch (Throwable var8) {
               var18 = CrashReport.func_85055_a(var8, "Ticking entity");
               var6 = var18.func_85058_a("Entity being ticked");
               var2.func_85029_a(var6);
               throw new ReportedException(var18);
            }
         }

         this.field_72984_F.func_76319_b();
         this.field_72984_F.func_76320_a("remove");
         if (var2.field_70128_L) {
            var15 = var2.field_70176_ah;
            int var19 = var2.field_70164_aj;
            if (var2.field_70175_ag && this.func_175680_a(var15, var19, true)) {
               this.func_72964_e(var15, var19).func_76622_b(var2);
            }

            this.field_72996_f.remove(var1--);
            this.func_72847_b(var2);
         }

         this.field_72984_F.func_76319_b();
      }

      this.field_72984_F.func_76318_c("blockEntities");
      if (!this.field_147483_b.isEmpty()) {
         this.field_175730_i.removeAll(this.field_147483_b);
         this.field_147482_g.removeAll(this.field_147483_b);
         this.field_147483_b.clear();
      }

      this.field_147481_N = true;
      Iterator var16 = this.field_175730_i.iterator();

      while(var16.hasNext()) {
         TileEntity var10 = (TileEntity)var16.next();
         if (!var10.func_145837_r() && var10.func_145830_o()) {
            BlockPos var13 = var10.func_174877_v();
            if (this.func_175667_e(var13) && this.field_175728_M.func_177746_a(var13)) {
               try {
                  this.field_72984_F.func_194340_a(() -> {
                     return String.valueOf(TileEntityType.func_200969_a(var10.func_200662_C()));
                  });
                  ((ITickable)var10).func_73660_a();
                  this.field_72984_F.func_76319_b();
               } catch (Throwable var7) {
                  var18 = CrashReport.func_85055_a(var7, "Ticking block entity");
                  var6 = var18.func_85058_a("Block entity being ticked");
                  var10.func_145828_a(var6);
                  throw new ReportedException(var18);
               }
            }
         }

         if (var10.func_145837_r()) {
            var16.remove();
            this.field_147482_g.remove(var10);
            if (this.func_175667_e(var10.func_174877_v())) {
               this.func_175726_f(var10.func_174877_v()).func_177425_e(var10.func_174877_v());
            }
         }
      }

      this.field_147481_N = false;
      this.field_72984_F.func_76318_c("pendingBlockEntities");
      if (!this.field_147484_a.isEmpty()) {
         for(int var12 = 0; var12 < this.field_147484_a.size(); ++var12) {
            TileEntity var14 = (TileEntity)this.field_147484_a.get(var12);
            if (!var14.func_145837_r()) {
               if (!this.field_147482_g.contains(var14)) {
                  this.func_175700_a(var14);
               }

               if (this.func_175667_e(var14.func_174877_v())) {
                  Chunk var17 = this.func_175726_f(var14.func_174877_v());
                  IBlockState var20 = var17.func_180495_p(var14.func_174877_v());
                  var17.func_177426_a(var14.func_174877_v(), var14);
                  this.func_184138_a(var14.func_174877_v(), var20, var20, 3);
               }
            }
         }

         this.field_147484_a.clear();
      }

      this.field_72984_F.func_76319_b();
      this.field_72984_F.func_76319_b();
   }

   protected void func_184147_l() {
   }

   public boolean func_175700_a(TileEntity var1) {
      boolean var2 = this.field_147482_g.add(var1);
      if (var2 && var1 instanceof ITickable) {
         this.field_175730_i.add(var1);
      }

      if (this.field_72995_K) {
         BlockPos var3 = var1.func_174877_v();
         IBlockState var4 = this.func_180495_p(var3);
         this.func_184138_a(var3, var4, var4, 2);
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
            this.func_175700_a(var3);
         }
      }

   }

   public void func_72870_g(Entity var1) {
      this.func_72866_a(var1, true);
   }

   public void func_72866_a(Entity var1, boolean var2) {
      int var3;
      int var4;
      if (!(var1 instanceof EntityPlayer)) {
         var3 = MathHelper.func_76128_c(var1.field_70165_t);
         var4 = MathHelper.func_76128_c(var1.field_70161_v);
         boolean var5 = true;
         if (var2 && !this.func_175663_a(var3 - 32, 0, var4 - 32, var3 + 32, 0, var4 + 32, true)) {
            return;
         }
      }

      var1.field_70142_S = var1.field_70165_t;
      var1.field_70137_T = var1.field_70163_u;
      var1.field_70136_U = var1.field_70161_v;
      var1.field_70126_B = var1.field_70177_z;
      var1.field_70127_C = var1.field_70125_A;
      if (var2 && var1.field_70175_ag) {
         ++var1.field_70173_aa;
         if (var1.func_184218_aH()) {
            var1.func_70098_U();
         } else {
            this.field_72984_F.func_194340_a(() -> {
               return IRegistry.field_212629_r.func_177774_c(var1.func_200600_R()).toString();
            });
            var1.func_70071_h_();
            this.field_72984_F.func_76319_b();
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

      var3 = MathHelper.func_76128_c(var1.field_70165_t / 16.0D);
      var4 = MathHelper.func_76128_c(var1.field_70163_u / 16.0D);
      int var8 = MathHelper.func_76128_c(var1.field_70161_v / 16.0D);
      if (!var1.field_70175_ag || var1.field_70176_ah != var3 || var1.field_70162_ai != var4 || var1.field_70164_aj != var8) {
         if (var1.field_70175_ag && this.func_175680_a(var1.field_70176_ah, var1.field_70164_aj, true)) {
            this.func_72964_e(var1.field_70176_ah, var1.field_70164_aj).func_76608_a(var1, var1.field_70162_ai);
         }

         if (!var1.func_184189_br() && !this.func_175680_a(var3, var8, true)) {
            var1.field_70175_ag = false;
         } else {
            this.func_72964_e(var3, var8).func_76612_a(var1);
         }
      }

      this.field_72984_F.func_76319_b();
      if (var2 && var1.field_70175_ag) {
         Iterator var6 = var1.func_184188_bt().iterator();

         while(true) {
            while(var6.hasNext()) {
               Entity var7 = (Entity)var6.next();
               if (!var7.field_70128_L && var7.func_184187_bx() == var1) {
                  this.func_72870_g(var7);
               } else {
                  var7.func_184210_p();
               }
            }

            return;
         }
      }
   }

   public boolean func_195585_a(@Nullable Entity var1, VoxelShape var2) {
      if (var2.func_197766_b()) {
         return true;
      } else {
         List var3 = this.func_72839_b((Entity)null, var2.func_197752_a());

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            Entity var5 = (Entity)var3.get(var4);
            if (!var5.field_70128_L && var5.field_70156_m && var5 != var1 && (var1 == null || !var5.func_184223_x(var1)) && VoxelShapes.func_197879_c(var2, VoxelShapes.func_197881_a(var5.func_174813_aQ()), IBooleanFunction.AND)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean func_72829_c(AxisAlignedBB var1) {
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76143_f(var1.field_72336_d);
      int var4 = MathHelper.func_76128_c(var1.field_72338_b);
      int var5 = MathHelper.func_76143_f(var1.field_72337_e);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76143_f(var1.field_72334_f);
      BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var9 = null;

      try {
         for(int var10 = var2; var10 < var3; ++var10) {
            for(int var11 = var4; var11 < var5; ++var11) {
               for(int var12 = var6; var12 < var7; ++var12) {
                  IBlockState var13 = this.func_180495_p(var8.func_181079_c(var10, var11, var12));
                  if (!var13.func_196958_f()) {
                     boolean var14 = true;
                     return var14;
                  }
               }
            }
         }

         return false;
      } catch (Throwable var24) {
         var9 = var24;
         throw var24;
      } finally {
         if (var8 != null) {
            if (var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var23) {
                  var9.addSuppressed(var23);
               }
            } else {
               var8.close();
            }
         }

      }
   }

   public boolean func_147470_e(AxisAlignedBB var1) {
      int var2 = MathHelper.func_76128_c(var1.field_72340_a);
      int var3 = MathHelper.func_76143_f(var1.field_72336_d);
      int var4 = MathHelper.func_76128_c(var1.field_72338_b);
      int var5 = MathHelper.func_76143_f(var1.field_72337_e);
      int var6 = MathHelper.func_76128_c(var1.field_72339_c);
      int var7 = MathHelper.func_76143_f(var1.field_72334_f);
      if (this.func_175663_a(var2, var4, var6, var3, var5, var7, true)) {
         BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.func_185346_s();
         Throwable var9 = null;

         try {
            for(int var10 = var2; var10 < var3; ++var10) {
               for(int var11 = var4; var11 < var5; ++var11) {
                  for(int var12 = var6; var12 < var7; ++var12) {
                     Block var13 = this.func_180495_p(var8.func_181079_c(var10, var11, var12)).func_177230_c();
                     if (var13 == Blocks.field_150480_ab || var13 == Blocks.field_150353_l) {
                        boolean var14 = true;
                        return var14;
                     }
                  }
               }
            }

            return false;
         } catch (Throwable var24) {
            var9 = var24;
            throw var24;
         } finally {
            if (var8 != null) {
               if (var9 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var23) {
                     var9.addSuppressed(var23);
                  }
               } else {
                  var8.close();
               }
            }

         }
      } else {
         return false;
      }
   }

   @Nullable
   public IBlockState func_203067_a(AxisAlignedBB var1, Block var2) {
      int var3 = MathHelper.func_76128_c(var1.field_72340_a);
      int var4 = MathHelper.func_76143_f(var1.field_72336_d);
      int var5 = MathHelper.func_76128_c(var1.field_72338_b);
      int var6 = MathHelper.func_76143_f(var1.field_72337_e);
      int var7 = MathHelper.func_76128_c(var1.field_72339_c);
      int var8 = MathHelper.func_76143_f(var1.field_72334_f);
      if (this.func_175663_a(var3, var5, var7, var4, var6, var8, true)) {
         BlockPos.PooledMutableBlockPos var9 = BlockPos.PooledMutableBlockPos.func_185346_s();
         Throwable var10 = null;

         try {
            for(int var11 = var3; var11 < var4; ++var11) {
               for(int var12 = var5; var12 < var6; ++var12) {
                  for(int var13 = var7; var13 < var8; ++var13) {
                     IBlockState var14 = this.func_180495_p(var9.func_181079_c(var11, var12, var13));
                     if (var14.func_177230_c() == var2) {
                        IBlockState var15 = var14;
                        return var15;
                     }
                  }
               }
            }

            return null;
         } catch (Throwable var25) {
            var10 = var25;
            throw var25;
         } finally {
            if (var9 != null) {
               if (var10 != null) {
                  try {
                     var9.close();
                  } catch (Throwable var24) {
                     var10.addSuppressed(var24);
                  }
               } else {
                  var9.close();
               }
            }

         }
      } else {
         return null;
      }
   }

   public boolean func_72875_a(AxisAlignedBB var1, Material var2) {
      int var3 = MathHelper.func_76128_c(var1.field_72340_a);
      int var4 = MathHelper.func_76143_f(var1.field_72336_d);
      int var5 = MathHelper.func_76128_c(var1.field_72338_b);
      int var6 = MathHelper.func_76143_f(var1.field_72337_e);
      int var7 = MathHelper.func_76128_c(var1.field_72339_c);
      int var8 = MathHelper.func_76143_f(var1.field_72334_f);
      BlockMaterialMatcher var9 = BlockMaterialMatcher.func_189886_a(var2);
      BlockPos.PooledMutableBlockPos var10 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var11 = null;

      try {
         for(int var12 = var3; var12 < var4; ++var12) {
            for(int var13 = var5; var13 < var6; ++var13) {
               for(int var14 = var7; var14 < var8; ++var14) {
                  if (var9.test(this.func_180495_p(var10.func_181079_c(var12, var13, var14)))) {
                     boolean var15 = true;
                     return var15;
                  }
               }
            }
         }
      } catch (Throwable var25) {
         var11 = var25;
         throw var25;
      } finally {
         if (var10 != null) {
            if (var11 != null) {
               try {
                  var10.close();
               } catch (Throwable var24) {
                  var11.addSuppressed(var24);
               }
            } else {
               var10.close();
            }
         }

      }

      return false;
   }

   public Explosion func_72876_a(@Nullable Entity var1, double var2, double var4, double var6, float var8, boolean var9) {
      return this.func_211529_a(var1, (DamageSource)null, var2, var4, var6, var8, false, var9);
   }

   public Explosion func_72885_a(@Nullable Entity var1, double var2, double var4, double var6, float var8, boolean var9, boolean var10) {
      return this.func_211529_a(var1, (DamageSource)null, var2, var4, var6, var8, var9, var10);
   }

   public Explosion func_211529_a(@Nullable Entity var1, @Nullable DamageSource var2, double var3, double var5, double var7, float var9, boolean var10, boolean var11) {
      Explosion var12 = new Explosion(this, var1, var3, var5, var7, var9, var10, var11);
      if (var2 != null) {
         var12.func_199592_a(var2);
      }

      var12.func_77278_a();
      var12.func_77279_a(true);
      return var12;
   }

   public float func_72842_a(Vec3d var1, AxisAlignedBB var2) {
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
                  if (this.func_72933_a(new Vec3d(var18 + var9, var20, var22 + var11), var1) == null) {
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

   public boolean func_175719_a(@Nullable EntityPlayer var1, BlockPos var2, EnumFacing var3) {
      var2 = var2.func_177972_a(var3);
      if (this.func_180495_p(var2).func_177230_c() == Blocks.field_150480_ab) {
         this.func_180498_a(var1, 1009, var2, 0);
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

   @Nullable
   public TileEntity func_175625_s(BlockPos var1) {
      if (func_189509_E(var1)) {
         return null;
      } else {
         TileEntity var2 = null;
         if (this.field_147481_N) {
            var2 = this.func_189508_F(var1);
         }

         if (var2 == null) {
            var2 = this.func_175726_f(var1).func_177424_a(var1, Chunk.EnumCreateEntityType.IMMEDIATE);
         }

         if (var2 == null) {
            var2 = this.func_189508_F(var1);
         }

         return var2;
      }
   }

   @Nullable
   private TileEntity func_189508_F(BlockPos var1) {
      for(int var2 = 0; var2 < this.field_147484_a.size(); ++var2) {
         TileEntity var3 = (TileEntity)this.field_147484_a.get(var2);
         if (!var3.func_145837_r() && var3.func_174877_v().equals(var1)) {
            return var3;
         }
      }

      return null;
   }

   public void func_175690_a(BlockPos var1, @Nullable TileEntity var2) {
      if (!func_189509_E(var1)) {
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
               this.func_175726_f(var1).func_177426_a(var1, var2);
               this.func_175700_a(var2);
            }
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
      return Block.func_208062_a(this.func_180495_p(var1).func_196952_d(this, var1));
   }

   public boolean func_195588_v(BlockPos var1) {
      if (func_189509_E(var1)) {
         return false;
      } else {
         Chunk var2 = this.field_73020_y.func_186025_d(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4, false, false);
         return var2 != null && !var2.func_76621_g();
      }
   }

   public boolean func_195595_w(BlockPos var1) {
      return this.func_195588_v(var1) && this.func_180495_p(var1).func_185896_q();
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

   public void func_72835_b(BooleanSupplier var1) {
      this.field_175728_M.func_212673_r();
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

   public void close() {
      this.field_73020_y.close();
   }

   protected void func_72979_l() {
      if (this.field_73011_w.func_191066_m()) {
         if (!this.field_72995_K) {
            boolean var1 = this.func_82736_K().func_82766_b("doWeatherCycle");
            if (var1) {
               int var2 = this.field_72986_A.func_176133_A();
               if (var2 > 0) {
                  --var2;
                  this.field_72986_A.func_176142_i(var2);
                  this.field_72986_A.func_76090_f(this.field_72986_A.func_76061_m() ? 1 : 2);
                  this.field_72986_A.func_76080_g(this.field_72986_A.func_76059_o() ? 1 : 2);
               }

               int var3 = this.field_72986_A.func_76071_n();
               if (var3 <= 0) {
                  if (this.field_72986_A.func_76061_m()) {
                     this.field_72986_A.func_76090_f(this.field_73012_v.nextInt(12000) + 3600);
                  } else {
                     this.field_72986_A.func_76090_f(this.field_73012_v.nextInt(168000) + 12000);
                  }
               } else {
                  --var3;
                  this.field_72986_A.func_76090_f(var3);
                  if (var3 <= 0) {
                     this.field_72986_A.func_76069_a(!this.field_72986_A.func_76061_m());
                  }
               }

               int var4 = this.field_72986_A.func_76083_p();
               if (var4 <= 0) {
                  if (this.field_72986_A.func_76059_o()) {
                     this.field_72986_A.func_76080_g(this.field_73012_v.nextInt(12000) + 12000);
                  } else {
                     this.field_72986_A.func_76080_g(this.field_73012_v.nextInt(168000) + 12000);
                  }
               } else {
                  --var4;
                  this.field_72986_A.func_76080_g(var4);
                  if (var4 <= 0) {
                     this.field_72986_A.func_76084_b(!this.field_72986_A.func_76059_o());
                  }
               }
            }

            this.field_73018_p = this.field_73017_q;
            if (this.field_72986_A.func_76061_m()) {
               this.field_73017_q = (float)((double)this.field_73017_q + 0.01D);
            } else {
               this.field_73017_q = (float)((double)this.field_73017_q - 0.01D);
            }

            this.field_73017_q = MathHelper.func_76131_a(this.field_73017_q, 0.0F, 1.0F);
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

   protected void func_147467_a(int var1, int var2, Chunk var3) {
      var3.func_76594_o();
   }

   protected void func_147456_g() {
   }

   public boolean func_175664_x(BlockPos var1) {
      boolean var2 = false;
      if (this.field_73011_w.func_191066_m()) {
         var2 |= this.func_180500_c(EnumLightType.SKY, var1);
      }

      var2 |= this.func_180500_c(EnumLightType.BLOCK, var1);
      return var2;
   }

   private int func_175638_a(BlockPos var1, EnumLightType var2) {
      if (var2 == EnumLightType.SKY && this.func_175678_i(var1)) {
         return 15;
      } else {
         IBlockState var3 = this.func_180495_p(var1);
         int var4 = var2 == EnumLightType.SKY ? 0 : var3.func_185906_d();
         int var5 = var3.func_200016_a(this, var1);
         if (var5 >= 15 && var3.func_185906_d() > 0) {
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
            BlockPos.PooledMutableBlockPos var6 = BlockPos.PooledMutableBlockPos.func_185346_s();
            Throwable var7 = null;

            try {
               EnumFacing[] var8 = field_200007_a;
               int var9 = var8.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  EnumFacing var11 = var8[var10];
                  var6.func_189533_g(var1).func_189536_c(var11);
                  int var12 = this.func_175642_b(var2, var6) - var5;
                  if (var12 > var4) {
                     var4 = var12;
                  }

                  if (var4 >= 14) {
                     int var13 = var4;
                     return var13;
                  }
               }
            } catch (Throwable var23) {
               var7 = var23;
               throw var23;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var22) {
                        var7.addSuppressed(var22);
                     }
                  } else {
                     var6.close();
                  }
               }

            }

            return var4;
         }
      }
   }

   public boolean func_180500_c(EnumLightType var1, BlockPos var2) {
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

            label241:
            while(true) {
               int var14;
               do {
                  do {
                     BlockPos var15;
                     do {
                        if (var3 >= var4) {
                           var3 = 0;
                           break label241;
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

               BlockPos.PooledMutableBlockPos var20 = BlockPos.PooledMutableBlockPos.func_185346_s();
               Throwable var21 = null;

               try {
                  EnumFacing[] var22 = field_200007_a;
                  int var23 = var22.length;

                  for(int var24 = 0; var24 < var23; ++var24) {
                     EnumFacing var25 = var22[var24];
                     int var26 = var11 + var25.func_82601_c();
                     int var27 = var12 + var25.func_96559_d();
                     int var28 = var13 + var25.func_82599_e();
                     var20.func_181079_c(var26, var27, var28);
                     int var29 = Math.max(1, this.func_180495_p(var20).func_200016_a(this, var20));
                     var16 = this.func_175642_b(var1, var20);
                     if (var16 == var14 - var29 && var4 < this.field_72994_J.length) {
                        this.field_72994_J[var4++] = var26 - var7 + 32 | var27 - var8 + 32 << 6 | var28 - var9 + 32 << 12 | var14 - var29 << 18;
                     }
                  }
               } catch (Throwable var37) {
                  var21 = var37;
                  throw var37;
               } finally {
                  if (var20 != null) {
                     if (var21 != null) {
                        try {
                           var20.close();
                        } catch (Throwable var36) {
                           var21.addSuppressed(var36);
                        }
                     } else {
                        var20.close();
                     }
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
            BlockPos var39 = new BlockPos(var11, var12, var13);
            int var40 = this.func_175642_b(var1, var39);
            var16 = this.func_175638_a(var39, var1);
            if (var16 != var40) {
               this.func_175653_a(var1, var39, var16);
               if (var16 > var40) {
                  var17 = Math.abs(var11 - var7);
                  var18 = Math.abs(var12 - var8);
                  var19 = Math.abs(var13 - var9);
                  boolean var41 = var4 < this.field_72994_J.length - 6;
                  if (var17 + var18 + var19 < 17 && var41) {
                     if (this.func_175642_b(var1, var39.func_177976_e()) < var16) {
                        this.field_72994_J[var4++] = var11 - 1 - var7 + 32 + (var12 - var8 + 32 << 6) + (var13 - var9 + 32 << 12);
                     }

                     if (this.func_175642_b(var1, var39.func_177974_f()) < var16) {
                        this.field_72994_J[var4++] = var11 + 1 - var7 + 32 + (var12 - var8 + 32 << 6) + (var13 - var9 + 32 << 12);
                     }

                     if (this.func_175642_b(var1, var39.func_177977_b()) < var16) {
                        this.field_72994_J[var4++] = var11 - var7 + 32 + (var12 - 1 - var8 + 32 << 6) + (var13 - var9 + 32 << 12);
                     }

                     if (this.func_175642_b(var1, var39.func_177984_a()) < var16) {
                        this.field_72994_J[var4++] = var11 - var7 + 32 + (var12 + 1 - var8 + 32 << 6) + (var13 - var9 + 32 << 12);
                     }

                     if (this.func_175642_b(var1, var39.func_177978_c()) < var16) {
                        this.field_72994_J[var4++] = var11 - var7 + 32 + (var12 - var8 + 32 << 6) + (var13 - 1 - var9 + 32 << 12);
                     }

                     if (this.func_175642_b(var1, var39.func_177968_d()) < var16) {
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

   public Stream<VoxelShape> func_212392_a(@Nullable Entity var1, VoxelShape var2, VoxelShape var3, Set<Entity> var4) {
      Stream var5 = IWorld.super.func_212392_a(var1, var2, var3, var4);
      return var1 == null ? var5 : Stream.concat(var5, this.func_211155_a(var1, var2, var4));
   }

   public List<Entity> func_175674_a(@Nullable Entity var1, AxisAlignedBB var2, @Nullable Predicate<? super Entity> var3) {
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
         if (var1.isAssignableFrom(var5.getClass()) && var2.test(var5)) {
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
         if (var1.isAssignableFrom(var5.getClass()) && var2.test(var5)) {
            var3.add(var5);
         }
      }

      return var3;
   }

   public <T extends Entity> List<T> func_72872_a(Class<? extends T> var1, AxisAlignedBB var2) {
      return this.func_175647_a(var1, var2, EntitySelectors.field_180132_d);
   }

   public <T extends Entity> List<T> func_175647_a(Class<? extends T> var1, AxisAlignedBB var2, @Nullable Predicate<? super T> var3) {
      int var4 = MathHelper.func_76128_c((var2.field_72340_a - 2.0D) / 16.0D);
      int var5 = MathHelper.func_76143_f((var2.field_72336_d + 2.0D) / 16.0D);
      int var6 = MathHelper.func_76128_c((var2.field_72339_c - 2.0D) / 16.0D);
      int var7 = MathHelper.func_76143_f((var2.field_72334_f + 2.0D) / 16.0D);
      ArrayList var8 = Lists.newArrayList();

      for(int var9 = var4; var9 < var5; ++var9) {
         for(int var10 = var6; var10 < var7; ++var10) {
            if (this.func_175680_a(var9, var10, true)) {
               this.func_72964_e(var9, var10).func_177430_a(var1, var2, var8, var3);
            }
         }
      }

      return var8;
   }

   @Nullable
   public <T extends Entity> T func_72857_a(Class<? extends T> var1, AxisAlignedBB var2, T var3) {
      List var4 = this.func_72872_a(var1, var2);
      Entity var5 = null;
      double var6 = 1.7976931348623157E308D;

      for(int var8 = 0; var8 < var4.size(); ++var8) {
         Entity var9 = (Entity)var4.get(var8);
         if (var9 != var3 && EntitySelectors.field_180132_d.test(var9)) {
            double var10 = var3.func_70068_e(var9);
            if (var10 <= var6) {
               var5 = var9;
               var6 = var10;
            }
         }
      }

      return var5;
   }

   @Nullable
   public Entity func_73045_a(int var1) {
      return (Entity)this.field_175729_l.func_76041_a(var1);
   }

   public int func_212419_R() {
      return this.field_72996_f.size();
   }

   public void func_175646_b(BlockPos var1, TileEntity var2) {
      if (this.func_175667_e(var1)) {
         this.func_175726_f(var1).func_76630_e();
      }

   }

   public int func_72907_a(Class<?> var1, int var2) {
      int var3 = 0;
      Iterator var4 = this.field_72996_f.iterator();

      do {
         Entity var5;
         do {
            if (!var4.hasNext()) {
               return var3;
            }

            var5 = (Entity)var4.next();
         } while(var5 instanceof EntityLiving && ((EntityLiving)var5).func_104002_bU());

         if (var1.isAssignableFrom(var5.getClass())) {
            ++var3;
         }
      } while(var3 <= var2);

      return var3;
   }

   public void func_212420_a(Stream<Entity> var1) {
      var1.forEach((var1x) -> {
         this.field_72996_f.add(var1x);
         this.func_72923_a(var1x);
      });
   }

   public void func_175681_c(Collection<Entity> var1) {
      this.field_72997_g.addAll(var1);
   }

   public int func_181545_F() {
      return this.field_181546_a;
   }

   public World func_201672_e() {
      return this;
   }

   public void func_181544_b(int var1) {
      this.field_181546_a = var1;
   }

   public int func_175627_a(BlockPos var1, EnumFacing var2) {
      return this.func_180495_p(var1).func_185893_b(this, var1, var2);
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
      return var3.func_185915_l() ? this.func_175676_y(var1) : var3.func_185911_a(this, var1, var2);
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
      EnumFacing[] var3 = field_200007_a;
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

   @Nullable
   public EntityPlayer func_190525_a(double var1, double var3, double var5, double var7, Predicate<Entity> var9) {
      double var10 = -1.0D;
      EntityPlayer var12 = null;

      for(int var13 = 0; var13 < this.field_73010_i.size(); ++var13) {
         EntityPlayer var14 = (EntityPlayer)this.field_73010_i.get(var13);
         if (var9.test(var14)) {
            double var15 = var14.func_70092_e(var1, var3, var5);
            if ((var7 < 0.0D || var15 < var7 * var7) && (var10 == -1.0D || var15 < var10)) {
               var10 = var15;
               var12 = var14;
            }
         }
      }

      return var12;
   }

   public boolean func_175636_b(double var1, double var3, double var5, double var7) {
      for(int var9 = 0; var9 < this.field_73010_i.size(); ++var9) {
         EntityPlayer var10 = (EntityPlayer)this.field_73010_i.get(var9);
         if (EntitySelectors.field_180132_d.test(var10)) {
            double var11 = var10.func_70092_e(var1, var3, var5);
            if (var7 < 0.0D || var11 < var7 * var7) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean func_212417_b(double var1, double var3, double var5, double var7) {
      Iterator var9 = this.field_73010_i.iterator();

      double var11;
      do {
         EntityPlayer var10;
         do {
            do {
               if (!var9.hasNext()) {
                  return false;
               }

               var10 = (EntityPlayer)var9.next();
            } while(!EntitySelectors.field_180132_d.test(var10));
         } while(!EntitySelectors.field_212545_b.test(var10));

         var11 = var10.func_70092_e(var1, var3, var5);
      } while(var7 >= 0.0D && var11 >= var7 * var7);

      return true;
   }

   @Nullable
   public EntityPlayer func_212817_a(double var1, double var3, double var5) {
      double var7 = -1.0D;
      EntityPlayer var9 = null;

      for(int var10 = 0; var10 < this.field_73010_i.size(); ++var10) {
         EntityPlayer var11 = (EntityPlayer)this.field_73010_i.get(var10);
         if (EntitySelectors.field_180132_d.test(var11)) {
            double var12 = var11.func_70092_e(var1, var11.field_70163_u, var3);
            if ((var5 < 0.0D || var12 < var5 * var5) && (var7 == -1.0D || var12 < var7)) {
               var7 = var12;
               var9 = var11;
            }
         }
      }

      return var9;
   }

   @Nullable
   public EntityPlayer func_184142_a(Entity var1, double var2, double var4) {
      return this.func_184150_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var2, var4, (Function)null, (Predicate)null);
   }

   @Nullable
   public EntityPlayer func_184139_a(BlockPos var1, double var2, double var4) {
      return this.func_184150_a((double)((float)var1.func_177958_n() + 0.5F), (double)((float)var1.func_177956_o() + 0.5F), (double)((float)var1.func_177952_p() + 0.5F), var2, var4, (Function)null, (Predicate)null);
   }

   @Nullable
   public EntityPlayer func_184150_a(double var1, double var3, double var5, double var7, double var9, @Nullable Function<EntityPlayer, Double> var11, @Nullable Predicate<EntityPlayer> var12) {
      double var13 = -1.0D;
      EntityPlayer var15 = null;

      for(int var16 = 0; var16 < this.field_73010_i.size(); ++var16) {
         EntityPlayer var17 = (EntityPlayer)this.field_73010_i.get(var16);
         if (!var17.field_71075_bZ.field_75102_a && var17.func_70089_S() && !var17.func_175149_v() && (var12 == null || var12.test(var17))) {
            double var18 = var17.func_70092_e(var1, var17.field_70163_u, var5);
            double var20 = var7;
            if (var17.func_70093_af()) {
               var20 = var7 * 0.800000011920929D;
            }

            if (var17.func_82150_aj()) {
               float var22 = var17.func_82243_bO();
               if (var22 < 0.1F) {
                  var22 = 0.1F;
               }

               var20 *= (double)(0.7F * var22);
            }

            if (var11 != null) {
               var20 *= (Double)MoreObjects.firstNonNull(var11.apply(var17), 1.0D);
            }

            if ((var9 < 0.0D || Math.abs(var17.field_70163_u - var3) < var9 * var9) && (var7 < 0.0D || var18 < var20 * var20) && (var13 == -1.0D || var18 < var13)) {
               var13 = var18;
               var15 = var17;
            }
         }
      }

      return var15;
   }

   @Nullable
   public EntityPlayer func_72924_a(String var1) {
      for(int var2 = 0; var2 < this.field_73010_i.size(); ++var2) {
         EntityPlayer var3 = (EntityPlayer)this.field_73010_i.get(var2);
         if (var1.equals(var3.func_200200_C_().getString())) {
            return var3;
         }
      }

      return null;
   }

   @Nullable
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

   public void func_72906_B() throws SessionLockException {
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
         var1 = this.func_205770_a(Heightmap.Type.MOTION_BLOCKING, new BlockPos(this.func_175723_af().func_177731_f(), 0.0D, this.func_175723_af().func_177721_g()));
      }

      return var1;
   }

   public void func_175652_B(BlockPos var1) {
      this.field_72986_A.func_176143_a(var1);
   }

   public void func_72897_h(Entity var1) {
      int var2 = MathHelper.func_76128_c(var1.field_70165_t / 16.0D);
      int var3 = MathHelper.func_76128_c(var1.field_70161_v / 16.0D);
      boolean var4 = true;

      for(int var5 = -2; var5 <= 2; ++var5) {
         for(int var6 = -2; var6 <= 2; ++var6) {
            this.func_72964_e(var2 + var5, var3 + var6);
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
      this.func_180495_p(var1).func_189547_a(this, var1, var3, var4);
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
      if (this.field_73011_w.func_191066_m() && !this.field_73011_w.func_177495_o()) {
         return (double)this.func_72819_i(1.0F) > 0.9D;
      } else {
         return false;
      }
   }

   public boolean func_72896_J() {
      return (double)this.func_72867_j(1.0F) > 0.2D;
   }

   public boolean func_175727_C(BlockPos var1) {
      if (!this.func_72896_J()) {
         return false;
      } else if (!this.func_175678_i(var1)) {
         return false;
      } else if (this.func_205770_a(Heightmap.Type.MOTION_BLOCKING, var1).func_177956_o() > var1.func_177956_o()) {
         return false;
      } else {
         return this.func_180494_b(var1).func_201851_b() == Biome.RainType.RAIN;
      }
   }

   public boolean func_180502_D(BlockPos var1) {
      Biome var2 = this.func_180494_b(var1);
      return var2.func_76736_e();
   }

   @Nullable
   public WorldSavedDataStorage func_175693_T() {
      return this.field_72988_C;
   }

   public void func_175669_a(int var1, BlockPos var2, int var3) {
      for(int var4 = 0; var4 < this.field_73021_x.size(); ++var4) {
         ((IWorldEventListener)this.field_73021_x.get(var4)).func_180440_a(var1, var2, var3);
      }

   }

   public void func_175718_b(int var1, BlockPos var2, int var3) {
      this.func_180498_a((EntityPlayer)null, var1, var2, var3);
   }

   public void func_180498_a(@Nullable EntityPlayer var1, int var2, BlockPos var3, int var4) {
      try {
         for(int var5 = 0; var5 < this.field_73021_x.size(); ++var5) {
            ((IWorldEventListener)this.field_73021_x.get(var5)).func_180439_a(var1, var2, var3, var4);
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

   public double func_72919_O() {
      return this.field_72986_A.func_76067_t() == WorldType.field_77138_c ? 0.0D : 63.0D;
   }

   public CrashReportCategory func_72914_a(CrashReport var1) {
      CrashReportCategory var2 = var1.func_85057_a("Affected level", 1);
      var2.func_71507_a("Level name", this.field_72986_A == null ? "????" : this.field_72986_A.func_76065_j());
      var2.func_189529_a("All players", () -> {
         return this.field_73010_i.size() + " total; " + this.field_73010_i;
      });
      var2.func_189529_a("Chunk stats", () -> {
         return this.field_73020_y.func_73148_d();
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
         IWorldEventListener var5 = (IWorldEventListener)this.field_73021_x.get(var4);
         var5.func_180441_b(var1, var2, var3);
      }

   }

   public void func_92088_a(double var1, double var3, double var5, double var7, double var9, double var11, @Nullable NBTTagCompound var13) {
   }

   public abstract Scoreboard func_96441_U();

   public void func_175666_e(BlockPos var1, Block var2) {
      Iterator var3 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(var3.hasNext()) {
         EnumFacing var4 = (EnumFacing)var3.next();
         BlockPos var5 = var1.func_177972_a(var4);
         if (this.func_175667_e(var5)) {
            IBlockState var6 = this.func_180495_p(var5);
            if (var6.func_177230_c() == Blocks.field_196762_fd) {
               var6.func_189546_a(this, var5, var2, var1);
            } else if (var6.func_185915_l()) {
               var5 = var5.func_177972_a(var4);
               var6 = this.func_180495_p(var5);
               if (var6.func_177230_c() == Blocks.field_196762_fd) {
                  var6.func_189546_a(this, var5, var2, var1);
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
      boolean var6 = true;
      return var4 >= -128 && var4 <= 128 && var5 >= -128 && var5 <= 128;
   }

   public LongSet func_212412_ag() {
      ForcedChunksSaveData var1 = (ForcedChunksSaveData)this.func_212411_a(this.field_73011_w.func_186058_p(), ForcedChunksSaveData::new, "chunks");
      return (LongSet)(var1 != null ? LongSets.unmodifiable(var1.func_212438_a()) : LongSets.EMPTY_SET);
   }

   public boolean func_212416_f(int var1, int var2) {
      ForcedChunksSaveData var3 = (ForcedChunksSaveData)this.func_212411_a(this.field_73011_w.func_186058_p(), ForcedChunksSaveData::new, "chunks");
      return var3 != null && var3.func_212438_a().contains(ChunkPos.func_77272_a(var1, var2));
   }

   public boolean func_212414_b(int var1, int var2, boolean var3) {
      String var4 = "chunks";
      ForcedChunksSaveData var5 = (ForcedChunksSaveData)this.func_212411_a(this.field_73011_w.func_186058_p(), ForcedChunksSaveData::new, "chunks");
      if (var5 == null) {
         var5 = new ForcedChunksSaveData("chunks");
         this.func_212409_a(this.field_73011_w.func_186058_p(), "chunks", var5);
      }

      long var6 = ChunkPos.func_77272_a(var1, var2);
      boolean var8;
      if (var3) {
         var8 = var5.func_212438_a().add(var6);
         if (var8) {
            this.func_72964_e(var1, var2);
         }
      } else {
         var8 = var5.func_212438_a().remove(var6);
      }

      var5.func_76186_a(var8);
      return var8;
   }

   public void func_184135_a(Packet<?> var1) {
      throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
   }

   @Nullable
   public BlockPos func_211157_a(String var1, BlockPos var2, int var3, boolean var4) {
      return null;
   }

   public Dimension func_201675_m() {
      return this.field_73011_w;
   }

   public Random func_201674_k() {
      return this.field_73012_v;
   }

   public abstract RecipeManager func_199532_z();

   public abstract NetworkTagManager func_205772_D();

   // $FF: synthetic method
   public IChunk func_72964_e(int var1, int var2) {
      return this.func_72964_e(var1, var2);
   }
}
