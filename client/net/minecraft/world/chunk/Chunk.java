package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.gen.ChunkGeneratorDebug;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk implements IChunk {
   private static final Logger field_150817_t = LogManager.getLogger();
   public static final ChunkSection field_186036_a = null;
   private final ChunkSection[] field_76652_q;
   private final Biome[] field_76651_r;
   private final boolean[] field_76639_c;
   private final Map<BlockPos, NBTTagCompound> field_201618_i;
   private boolean field_76636_d;
   private final World field_76637_e;
   private final Map<Heightmap.Type, Heightmap> field_76634_f;
   public final int field_76635_g;
   public final int field_76647_h;
   private boolean field_76650_s;
   private final UpgradeData field_196967_n;
   private final Map<BlockPos, TileEntity> field_150816_i;
   private final ClassInheritanceMultiMap<Entity>[] field_76645_j;
   private final Map<String, StructureStart> field_201619_q;
   private final Map<String, LongSet> field_201620_r;
   private final ShortList[] field_201622_t;
   private final ITickList<Block> field_201621_s;
   private final ITickList<Fluid> field_205325_u;
   private boolean field_150815_m;
   private boolean field_76644_m;
   private long field_76641_n;
   private boolean field_76643_l;
   private int field_82912_p;
   private long field_111204_q;
   private int field_76649_t;
   private final ConcurrentLinkedQueue<BlockPos> field_177447_w;
   private ChunkStatus field_201616_C;
   private int field_201617_D;
   private final AtomicInteger field_205757_F;
   private final ChunkPos field_212816_F;

   public Chunk(World var1, int var2, int var3, Biome[] var4) {
      this(var1, var2, var3, var4, UpgradeData.field_196994_a, EmptyTickList.func_205388_a(), EmptyTickList.func_205388_a(), 0L);
   }

   public Chunk(World var1, int var2, int var3, Biome[] var4, UpgradeData var5, ITickList<Block> var6, ITickList<Fluid> var7, long var8) {
      super();
      this.field_76652_q = new ChunkSection[16];
      this.field_76639_c = new boolean[256];
      this.field_201618_i = Maps.newHashMap();
      this.field_76634_f = Maps.newEnumMap(Heightmap.Type.class);
      this.field_150816_i = Maps.newHashMap();
      this.field_201619_q = Maps.newHashMap();
      this.field_201620_r = Maps.newHashMap();
      this.field_201622_t = new ShortList[16];
      this.field_76649_t = 4096;
      this.field_177447_w = Queues.newConcurrentLinkedQueue();
      this.field_201616_C = ChunkStatus.EMPTY;
      this.field_205757_F = new AtomicInteger();
      this.field_76645_j = (ClassInheritanceMultiMap[])(new ClassInheritanceMultiMap[16]);
      this.field_76637_e = var1;
      this.field_76635_g = var2;
      this.field_76647_h = var3;
      this.field_212816_F = new ChunkPos(var2, var3);
      this.field_196967_n = var5;
      Heightmap.Type[] var10 = Heightmap.Type.values();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Heightmap.Type var13 = var10[var12];
         if (var13.func_207512_c() == Heightmap.Usage.LIVE_WORLD) {
            this.field_76634_f.put(var13, new Heightmap(this, var13));
         }
      }

      for(int var14 = 0; var14 < this.field_76645_j.length; ++var14) {
         this.field_76645_j[var14] = new ClassInheritanceMultiMap(Entity.class);
      }

      this.field_76651_r = var4;
      this.field_201621_s = var6;
      this.field_205325_u = var7;
      this.field_111204_q = var8;
   }

   public Chunk(World var1, ChunkPrimer var2, int var3, int var4) {
      this(var1, var3, var4, var2.func_201590_e(), var2.func_201631_p(), var2.func_205218_i_(), var2.func_212247_j(), var2.func_209216_m());

      int var5;
      for(var5 = 0; var5 < this.field_76652_q.length; ++var5) {
         this.field_76652_q[var5] = var2.func_76587_i()[var5];
      }

      Iterator var7 = var2.func_201652_l().iterator();

      while(var7.hasNext()) {
         NBTTagCompound var6 = (NBTTagCompound)var7.next();
         AnvilChunkLoader.func_186050_a(var6, var1, this);
      }

      var7 = var2.func_201627_k().values().iterator();

      while(var7.hasNext()) {
         TileEntity var8 = (TileEntity)var7.next();
         this.func_150813_a(var8);
      }

      this.field_201618_i.putAll(var2.func_201632_q());

      for(var5 = 0; var5 < var2.func_201645_n().length; ++var5) {
         this.field_201622_t[var5] = var2.func_201645_n()[var5];
      }

      this.func_201612_a(var2.func_201609_c());
      this.func_201606_b(var2.func_201604_d());
      var7 = var2.func_201634_m().iterator();

      while(var7.hasNext()) {
         Heightmap.Type var9 = (Heightmap.Type)var7.next();
         if (var9.func_207512_c() == Heightmap.Usage.LIVE_WORLD) {
            ((Heightmap)this.field_76634_f.computeIfAbsent(var9, (var1x) -> {
               return new Heightmap(this, var1x);
            })).func_202268_a(var2.func_201642_a(var9).func_202269_a());
         }
      }

      this.field_76643_l = true;
      this.func_201574_a(ChunkStatus.FULLCHUNK);
   }

   public Set<BlockPos> func_203066_o() {
      HashSet var1 = Sets.newHashSet(this.field_201618_i.keySet());
      var1.addAll(this.field_150816_i.keySet());
      return var1;
   }

   public boolean func_76600_a(int var1, int var2) {
      return var1 == this.field_76635_g && var2 == this.field_76647_h;
   }

   public ChunkSection[] func_76587_i() {
      return this.field_76652_q;
   }

   protected void func_76590_a() {
      Iterator var1 = this.field_76634_f.values().iterator();

      while(var1.hasNext()) {
         Heightmap var2 = (Heightmap)var1.next();
         var2.func_202266_a();
      }

      this.field_76643_l = true;
   }

   public void func_76603_b() {
      int var1 = this.func_76625_h();
      this.field_82912_p = 2147483647;
      Iterator var2 = this.field_76634_f.values().iterator();

      while(var2.hasNext()) {
         Heightmap var3 = (Heightmap)var2.next();
         var3.func_202266_a();
      }

      for(int var8 = 0; var8 < 16; ++var8) {
         for(int var9 = 0; var9 < 16; ++var9) {
            if (this.field_76637_e.field_73011_w.func_191066_m()) {
               int var4 = 15;
               int var5 = var1 + 16 - 1;

               do {
                  int var6 = this.func_150808_b(var8, var5, var9);
                  if (var6 == 0 && var4 != 15) {
                     var6 = 1;
                  }

                  var4 -= var6;
                  if (var4 > 0) {
                     ChunkSection var7 = this.field_76652_q[var5 >> 4];
                     if (var7 != field_186036_a) {
                        var7.func_76657_c(var8, var5 & 15, var9, var4);
                        this.field_76637_e.func_175679_n(new BlockPos((this.field_76635_g << 4) + var8, var5, (this.field_76647_h << 4) + var9));
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
      if (this.field_76637_e.func_205050_e(new BlockPos(this.field_76635_g * 16 + 8, 0, this.field_76647_h * 16 + 8), 16)) {
         for(int var2 = 0; var2 < 16; ++var2) {
            for(int var3 = 0; var3 < 16; ++var3) {
               if (this.field_76639_c[var2 + var3 * 16]) {
                  this.field_76639_c[var2 + var3 * 16] = false;
                  int var4 = this.func_201576_a(Heightmap.Type.LIGHT_BLOCKING, var2, var3);
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
      int var4 = this.field_76637_e.func_205770_a(Heightmap.Type.MOTION_BLOCKING, new BlockPos(var1, 0, var2)).func_177956_o();
      if (var4 > var3) {
         this.func_76609_d(var1, var2, var3, var4 + 1);
      } else if (var4 < var3) {
         this.func_76609_d(var1, var2, var4, var3 + 1);
      }

   }

   private void func_76609_d(int var1, int var2, int var3, int var4) {
      if (var4 > var3 && this.field_76637_e.func_205050_e(new BlockPos(var1, 0, var2), 16)) {
         for(int var5 = var3; var5 < var4; ++var5) {
            this.field_76637_e.func_180500_c(EnumLightType.SKY, new BlockPos(var1, var5, var2));
         }

         this.field_76643_l = true;
      }

   }

   private void func_76615_h(int var1, int var2, int var3, IBlockState var4) {
      Heightmap var5 = (Heightmap)this.field_76634_f.get(Heightmap.Type.LIGHT_BLOCKING);
      int var6 = var5.func_202273_a(var1 & 15, var3 & 15) & 255;
      if (var5.func_202270_a(var1, var2, var3, var4)) {
         int var7 = var5.func_202273_a(var1 & 15, var3 & 15);
         int var8 = this.field_76635_g * 16 + var1;
         int var9 = this.field_76647_h * 16 + var3;
         this.field_76637_e.func_72975_g(var8, var9, var7, var6);
         int var10;
         int var11;
         int var12;
         if (this.field_76637_e.field_73011_w.func_191066_m()) {
            var10 = Math.min(var6, var7);
            var11 = Math.max(var6, var7);
            var12 = var7 < var6 ? 15 : 0;

            int var13;
            for(var13 = var10; var13 < var11; ++var13) {
               ChunkSection var14 = this.field_76652_q[var13 >> 4];
               if (var14 != field_186036_a) {
                  var14.func_76657_c(var1, var13 & 15, var3, var12);
                  this.field_76637_e.func_175679_n(new BlockPos((this.field_76635_g << 4) + var1, var13, (this.field_76647_h << 4) + var3));
               }
            }

            var13 = 15;

            while(var7 > 0 && var13 > 0) {
               --var7;
               int var16 = this.func_150808_b(var1, var7, var3);
               var16 = var16 == 0 ? 1 : var16;
               var13 -= var16;
               var13 = Math.max(0, var13);
               ChunkSection var15 = this.field_76652_q[var7 >> 4];
               if (var15 != field_186036_a) {
                  var15.func_76657_c(var1, var7 & 15, var3, var13);
               }
            }
         }

         if (var7 < this.field_82912_p) {
            this.field_82912_p = var7;
         }

         if (this.field_76637_e.field_73011_w.func_191066_m()) {
            var10 = var5.func_202273_a(var1 & 15, var3 & 15);
            var11 = Math.min(var6, var10);
            var12 = Math.max(var6, var10);
            Iterator var17 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(var17.hasNext()) {
               EnumFacing var18 = (EnumFacing)var17.next();
               this.func_76609_d(var8 + var18.func_82601_c(), var9 + var18.func_82599_e(), var11, var12);
            }

            this.func_76609_d(var8, var9, var11, var12);
         }

         this.field_76643_l = true;
      }
   }

   private int func_150808_b(int var1, int var2, int var3) {
      return this.func_186032_a(var1, var2, var3).func_200016_a(this.field_76637_e, new BlockPos(var1, var2, var3));
   }

   public IBlockState func_180495_p(BlockPos var1) {
      return this.func_186032_a(var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p());
   }

   public IBlockState func_186032_a(int var1, int var2, int var3) {
      if (this.field_76637_e.func_175624_G() == WorldType.field_180272_g) {
         IBlockState var8 = null;
         if (var2 == 60) {
            var8 = Blocks.field_180401_cv.func_176223_P();
         }

         if (var2 == 70) {
            var8 = ChunkGeneratorDebug.func_177461_b(var1, var3);
         }

         return var8 == null ? Blocks.field_150350_a.func_176223_P() : var8;
      } else {
         try {
            if (var2 >= 0 && var2 >> 4 < this.field_76652_q.length) {
               ChunkSection var4 = this.field_76652_q[var2 >> 4];
               if (var4 != field_186036_a) {
                  return var4.func_177485_a(var1 & 15, var2 & 15, var3 & 15);
               }
            }

            return Blocks.field_150350_a.func_176223_P();
         } catch (Throwable var7) {
            CrashReport var5 = CrashReport.func_85055_a(var7, "Getting block state");
            CrashReportCategory var6 = var5.func_85058_a("Block being got");
            var6.func_189529_a("Location", () -> {
               return CrashReportCategory.func_184876_a(var1, var2, var3);
            });
            throw new ReportedException(var5);
         }
      }
   }

   public IFluidState func_204610_c(BlockPos var1) {
      return this.func_205751_b(var1.func_177958_n(), var1.func_177956_o(), var1.func_177952_p());
   }

   public IFluidState func_205751_b(int var1, int var2, int var3) {
      try {
         if (var2 >= 0 && var2 >> 4 < this.field_76652_q.length) {
            ChunkSection var4 = this.field_76652_q[var2 >> 4];
            if (var4 != field_186036_a) {
               return var4.func_206914_b(var1 & 15, var2 & 15, var3 & 15);
            }
         }

         return Fluids.field_204541_a.func_207188_f();
      } catch (Throwable var7) {
         CrashReport var5 = CrashReport.func_85055_a(var7, "Getting fluid state");
         CrashReportCategory var6 = var5.func_85058_a("Block being got");
         var6.func_189529_a("Location", () -> {
            return CrashReportCategory.func_184876_a(var1, var2, var3);
         });
         throw new ReportedException(var5);
      }
   }

   @Nullable
   public IBlockState func_177436_a(BlockPos var1, IBlockState var2, boolean var3) {
      int var4 = var1.func_177958_n() & 15;
      int var5 = var1.func_177956_o();
      int var6 = var1.func_177952_p() & 15;
      int var7 = ((Heightmap)this.field_76634_f.get(Heightmap.Type.LIGHT_BLOCKING)).func_202273_a(var4, var6);
      IBlockState var8 = this.func_180495_p(var1);
      if (var8 == var2) {
         return null;
      } else {
         Block var9 = var2.func_177230_c();
         Block var10 = var8.func_177230_c();
         ChunkSection var11 = this.field_76652_q[var5 >> 4];
         boolean var12 = false;
         if (var11 == field_186036_a) {
            if (var2.func_196958_f()) {
               return null;
            }

            var11 = new ChunkSection(var5 >> 4 << 4, this.field_76637_e.field_73011_w.func_191066_m());
            this.field_76652_q[var5 >> 4] = var11;
            var12 = var5 >= var7;
         }

         var11.func_177484_a(var4, var5 & 15, var6, var2);
         ((Heightmap)this.field_76634_f.get(Heightmap.Type.MOTION_BLOCKING)).func_202270_a(var4, var5, var6, var2);
         ((Heightmap)this.field_76634_f.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)).func_202270_a(var4, var5, var6, var2);
         ((Heightmap)this.field_76634_f.get(Heightmap.Type.OCEAN_FLOOR)).func_202270_a(var4, var5, var6, var2);
         ((Heightmap)this.field_76634_f.get(Heightmap.Type.WORLD_SURFACE)).func_202270_a(var4, var5, var6, var2);
         if (!this.field_76637_e.field_72995_K) {
            var8.func_196947_b(this.field_76637_e, var1, var2, var3);
         } else if (var10 != var9 && var10 instanceof ITileEntityProvider) {
            this.field_76637_e.func_175713_t(var1);
         }

         if (var11.func_177485_a(var4, var5 & 15, var6).func_177230_c() != var9) {
            return null;
         } else {
            if (var12) {
               this.func_76603_b();
            } else {
               int var13 = var2.func_200016_a(this.field_76637_e, var1);
               int var14 = var8.func_200016_a(this.field_76637_e, var1);
               this.func_76615_h(var4, var5, var6, var2);
               if (var13 != var14 && (var13 < var14 || this.func_177413_a(EnumLightType.SKY, var1) > 0 || this.func_177413_a(EnumLightType.BLOCK, var1) > 0)) {
                  this.func_76595_e(var4, var6);
               }
            }

            TileEntity var15;
            if (var10 instanceof ITileEntityProvider) {
               var15 = this.func_177424_a(var1, Chunk.EnumCreateEntityType.CHECK);
               if (var15 != null) {
                  var15.func_145836_u();
               }
            }

            if (!this.field_76637_e.field_72995_K) {
               var2.func_196945_a(this.field_76637_e, var1, var8);
            }

            if (var9 instanceof ITileEntityProvider) {
               var15 = this.func_177424_a(var1, Chunk.EnumCreateEntityType.CHECK);
               if (var15 == null) {
                  var15 = ((ITileEntityProvider)var9).func_196283_a_(this.field_76637_e);
                  this.field_76637_e.func_175690_a(var1, var15);
               } else {
                  var15.func_145836_u();
               }
            }

            this.field_76643_l = true;
            return var8;
         }
      }
   }

   public int func_177413_a(EnumLightType var1, BlockPos var2) {
      return this.func_201587_a(var1, var2, this.field_76637_e.func_201675_m().func_191066_m());
   }

   public int func_201587_a(EnumLightType var1, BlockPos var2, boolean var3) {
      int var4 = var2.func_177958_n() & 15;
      int var5 = var2.func_177956_o();
      int var6 = var2.func_177952_p() & 15;
      int var7 = var5 >> 4;
      if (var7 >= 0 && var7 <= this.field_76652_q.length - 1) {
         ChunkSection var8 = this.field_76652_q[var7];
         if (var8 == field_186036_a) {
            return this.func_177444_d(var2) ? var1.field_77198_c : 0;
         } else if (var1 == EnumLightType.SKY) {
            return !var3 ? 0 : var8.func_76670_c(var4, var5 & 15, var6);
         } else {
            return var1 == EnumLightType.BLOCK ? var8.func_76674_d(var4, var5 & 15, var6) : var1.field_77198_c;
         }
      } else {
         return (var1 != EnumLightType.SKY || !var3) && var1 != EnumLightType.BLOCK ? 0 : var1.field_77198_c;
      }
   }

   public void func_177431_a(EnumLightType var1, BlockPos var2, int var3) {
      this.func_201580_a(var1, this.field_76637_e.func_201675_m().func_191066_m(), var2, var3);
   }

   public void func_201580_a(EnumLightType var1, boolean var2, BlockPos var3, int var4) {
      int var5 = var3.func_177958_n() & 15;
      int var6 = var3.func_177956_o();
      int var7 = var3.func_177952_p() & 15;
      int var8 = var6 >> 4;
      if (var8 < 16 && var8 >= 0) {
         ChunkSection var9 = this.field_76652_q[var8];
         if (var9 == field_186036_a) {
            if (var4 == var1.field_77198_c) {
               return;
            }

            var9 = new ChunkSection(var8 << 4, var2);
            this.field_76652_q[var8] = var9;
            this.func_76603_b();
         }

         if (var1 == EnumLightType.SKY) {
            if (this.field_76637_e.field_73011_w.func_191066_m()) {
               var9.func_76657_c(var5, var6 & 15, var7, var4);
            }
         } else if (var1 == EnumLightType.BLOCK) {
            var9.func_76677_d(var5, var6 & 15, var7, var4);
         }

         this.field_76643_l = true;
      }
   }

   public int func_177443_a(BlockPos var1, int var2) {
      return this.func_201586_a(var1, var2, this.field_76637_e.func_201675_m().func_191066_m());
   }

   public int func_201586_a(BlockPos var1, int var2, boolean var3) {
      int var4 = var1.func_177958_n() & 15;
      int var5 = var1.func_177956_o();
      int var6 = var1.func_177952_p() & 15;
      int var7 = var5 >> 4;
      if (var7 >= 0 && var7 <= this.field_76652_q.length - 1) {
         ChunkSection var8 = this.field_76652_q[var7];
         if (var8 == field_186036_a) {
            return var3 && var2 < EnumLightType.SKY.field_77198_c ? EnumLightType.SKY.field_77198_c - var2 : 0;
         } else {
            int var9 = var3 ? var8.func_76670_c(var4, var5 & 15, var6) : 0;
            var9 -= var2;
            int var10 = var8.func_76674_d(var4, var5 & 15, var6);
            if (var10 > var9) {
               var9 = var10;
            }

            return var9;
         }
      } else {
         return 0;
      }
   }

   public void func_76612_a(Entity var1) {
      this.field_76644_m = true;
      int var2 = MathHelper.func_76128_c(var1.field_70165_t / 16.0D);
      int var3 = MathHelper.func_76128_c(var1.field_70161_v / 16.0D);
      if (var2 != this.field_76635_g || var3 != this.field_76647_h) {
         field_150817_t.warn("Wrong location! ({}, {}) should be ({}, {}), {}", var2, var3, this.field_76635_g, this.field_76647_h, var1);
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

   public void func_201607_a(Heightmap.Type var1, long[] var2) {
      ((Heightmap)this.field_76634_f.get(var1)).func_202268_a(var2);
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
      return var3 >= ((Heightmap)this.field_76634_f.get(Heightmap.Type.LIGHT_BLOCKING)).func_202273_a(var2, var4);
   }

   public int func_201576_a(Heightmap.Type var1, int var2, int var3) {
      return ((Heightmap)this.field_76634_f.get(var1)).func_202273_a(var2 & 15, var3 & 15) - 1;
   }

   @Nullable
   private TileEntity func_177422_i(BlockPos var1) {
      IBlockState var2 = this.func_180495_p(var1);
      Block var3 = var2.func_177230_c();
      return !var3.func_149716_u() ? null : ((ITileEntityProvider)var3).func_196283_a_(this.field_76637_e);
   }

   @Nullable
   public TileEntity func_175625_s(BlockPos var1) {
      return this.func_177424_a(var1, Chunk.EnumCreateEntityType.CHECK);
   }

   @Nullable
   public TileEntity func_177424_a(BlockPos var1, Chunk.EnumCreateEntityType var2) {
      TileEntity var3 = (TileEntity)this.field_150816_i.get(var1);
      if (var3 == null) {
         NBTTagCompound var4 = (NBTTagCompound)this.field_201618_i.remove(var1);
         if (var4 != null) {
            TileEntity var5 = this.func_212815_a(var1, var4);
            if (var5 != null) {
               return var5;
            }
         }
      }

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
      if (this.func_180495_p(var1).func_177230_c() instanceof ITileEntityProvider) {
         if (this.field_150816_i.containsKey(var1)) {
            ((TileEntity)this.field_150816_i.get(var1)).func_145843_s();
         }

         var2.func_145829_t();
         this.field_150816_i.put(var1.func_185334_h(), var2);
      }
   }

   public void func_201591_a(NBTTagCompound var1) {
      this.field_201618_i.put(new BlockPos(var1.func_74762_e("x"), var1.func_74762_e("y"), var1.func_74762_e("z")), var1);
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
      ClassInheritanceMultiMap[] var1 = this.field_76645_j;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         ClassInheritanceMultiMap var4 = var1[var3];
         this.field_76637_e.func_212420_a(var4.stream().filter((var0) -> {
            return !(var0 instanceof EntityPlayer);
         }));
      }

   }

   public void func_76623_d() {
      this.field_76636_d = false;
      Iterator var1 = this.field_150816_i.values().iterator();

      while(var1.hasNext()) {
         TileEntity var2 = (TileEntity)var1.next();
         this.field_76637_e.func_147457_a(var2);
      }

      ClassInheritanceMultiMap[] var5 = this.field_76645_j;
      int var6 = var5.length;

      for(int var3 = 0; var3 < var6; ++var3) {
         ClassInheritanceMultiMap var4 = var5[var3];
         this.field_76637_e.func_175681_c(var4);
      }

   }

   public void func_76630_e() {
      this.field_76643_l = true;
   }

   public void func_177414_a(@Nullable Entity var1, AxisAlignedBB var2, List<Entity> var3, Predicate<? super Entity> var4) {
      int var5 = MathHelper.func_76128_c((var2.field_72338_b - 2.0D) / 16.0D);
      int var6 = MathHelper.func_76128_c((var2.field_72337_e + 2.0D) / 16.0D);
      var5 = MathHelper.func_76125_a(var5, 0, this.field_76645_j.length - 1);
      var6 = MathHelper.func_76125_a(var6, 0, this.field_76645_j.length - 1);

      label68:
      for(int var7 = var5; var7 <= var6; ++var7) {
         if (!this.field_76645_j[var7].isEmpty()) {
            Iterator var8 = this.field_76645_j[var7].iterator();

            while(true) {
               Entity[] var10;
               do {
                  Entity var9;
                  do {
                     do {
                        if (!var8.hasNext()) {
                           continue label68;
                        }

                        var9 = (Entity)var8.next();
                     } while(!var9.func_174813_aQ().func_72326_a(var2));
                  } while(var9 == var1);

                  if (var4 == null || var4.test(var9)) {
                     var3.add(var9);
                  }

                  var10 = var9.func_70021_al();
               } while(var10 == null);

               Entity[] var11 = var10;
               int var12 = var10.length;

               for(int var13 = 0; var13 < var12; ++var13) {
                  Entity var14 = var11[var13];
                  if (var14 != var1 && var14.func_174813_aQ().func_72326_a(var2) && (var4 == null || var4.test(var14))) {
                     var3.add(var14);
                  }
               }
            }
         }
      }

   }

   public <T extends Entity> void func_177430_a(Class<? extends T> var1, AxisAlignedBB var2, List<T> var3, @Nullable Predicate<? super T> var4) {
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
            } while(var4 != null && !var4.test(var9));

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

   public boolean func_76621_g() {
      return false;
   }

   public void func_150804_b(boolean var1) {
      if (this.field_76650_s && this.field_76637_e.field_73011_w.func_191066_m() && !var1) {
         this.func_150803_c(this.field_76637_e.field_72995_K);
      }

      this.field_150815_m = true;

      while(!this.field_177447_w.isEmpty()) {
         BlockPos var2 = (BlockPos)this.field_177447_w.poll();
         if (this.func_177424_a(var2, Chunk.EnumCreateEntityType.CHECK) == null && this.func_180495_p(var2).func_177230_c().func_149716_u()) {
            TileEntity var3 = this.func_177422_i(var2);
            this.field_76637_e.func_175690_a(var2, var3);
            this.field_76637_e.func_175704_b(var2, var2);
         }
      }

   }

   public boolean func_150802_k() {
      return this.field_201616_C.func_209003_a(ChunkStatus.POSTPROCESSED);
   }

   public boolean func_186035_j() {
      return this.field_150815_m;
   }

   public ChunkPos func_76632_l() {
      return this.field_212816_F;
   }

   public boolean func_76606_c(int var1, int var2) {
      if (var1 < 0) {
         var1 = 0;
      }

      if (var2 >= 256) {
         var2 = 255;
      }

      for(int var3 = var1; var3 <= var2; var3 += 16) {
         ChunkSection var4 = this.field_76652_q[var3 >> 4];
         if (var4 != field_186036_a && !var4.func_76663_a()) {
            return false;
         }
      }

      return true;
   }

   public void func_76602_a(ChunkSection[] var1) {
      if (this.field_76652_q.length != var1.length) {
         field_150817_t.warn("Could not set level chunk sections, array length is {} instead of {}", var1.length, this.field_76652_q.length);
      } else {
         System.arraycopy(var1, 0, this.field_76652_q, 0, this.field_76652_q.length);
      }
   }

   public void func_186033_a(PacketBuffer var1, int var2, boolean var3) {
      if (var3) {
         this.field_150816_i.clear();
      } else {
         Iterator var4 = this.field_150816_i.keySet().iterator();

         while(var4.hasNext()) {
            BlockPos var5 = (BlockPos)var4.next();
            int var6 = var5.func_177956_o() >> 4;
            if ((var2 & 1 << var6) != 0) {
               var4.remove();
            }
         }
      }

      boolean var7 = this.field_76637_e.field_73011_w.func_191066_m();

      int var8;
      for(var8 = 0; var8 < this.field_76652_q.length; ++var8) {
         ChunkSection var9 = this.field_76652_q[var8];
         if ((var2 & 1 << var8) == 0) {
            if (var3 && var9 != field_186036_a) {
               this.field_76652_q[var8] = field_186036_a;
            }
         } else {
            if (var9 == field_186036_a) {
               var9 = new ChunkSection(var8 << 4, var7);
               this.field_76652_q[var8] = var9;
            }

            var9.func_186049_g().func_186010_a(var1);
            var1.readBytes(var9.func_76661_k().func_177481_a());
            if (var7) {
               var1.readBytes(var9.func_76671_l().func_177481_a());
            }
         }
      }

      if (var3) {
         for(var8 = 0; var8 < this.field_76651_r.length; ++var8) {
            this.field_76651_r[var8] = (Biome)IRegistry.field_212624_m.func_148754_a(var1.readInt());
         }
      }

      for(var8 = 0; var8 < this.field_76652_q.length; ++var8) {
         if (this.field_76652_q[var8] != field_186036_a && (var2 & 1 << var8) != 0) {
            this.field_76652_q[var8].func_76672_e();
         }
      }

      this.func_76590_a();
      Iterator var10 = this.field_150816_i.values().iterator();

      while(var10.hasNext()) {
         TileEntity var11 = (TileEntity)var10.next();
         var11.func_145836_u();
      }

   }

   public Biome func_201600_k(BlockPos var1) {
      int var2 = var1.func_177958_n() & 15;
      int var3 = var1.func_177952_p() & 15;
      return this.field_76651_r[var3 << 4 | var2];
   }

   public Biome[] func_201590_e() {
      return this.field_76651_r;
   }

   public void func_76613_n() {
      this.field_76649_t = 0;
   }

   public void func_76594_o() {
      if (this.field_76649_t < 4096) {
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
               if (this.field_76652_q[var3] == field_186036_a && var8 || this.field_76652_q[var3] != field_186036_a && this.field_76652_q[var3].func_177485_a(var4, var6, var5).func_196958_f()) {
                  EnumFacing[] var9 = EnumFacing.values();
                  int var10 = var9.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     EnumFacing var12 = var9[var11];
                     BlockPos var13 = var7.func_177972_a(var12);
                     if (this.field_76637_e.func_180495_p(var13).func_185906_d() > 0) {
                        this.field_76637_e.func_175664_x(var13);
                     }
                  }

                  this.field_76637_e.func_175664_x(var7);
               }
            }
         }

      }
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

   public Set<Heightmap.Type> func_201615_v() {
      return this.field_76634_f.keySet();
   }

   public Heightmap func_201608_a(Heightmap.Type var1) {
      return (Heightmap)this.field_76634_f.get(var1);
   }

   public Map<BlockPos, TileEntity> func_177434_r() {
      return this.field_150816_i;
   }

   public ClassInheritanceMultiMap<Entity>[] func_177429_s() {
      return this.field_76645_j;
   }

   public NBTTagCompound func_201579_g(BlockPos var1) {
      return (NBTTagCompound)this.field_201618_i.get(var1);
   }

   public ITickList<Block> func_205218_i_() {
      return this.field_201621_s;
   }

   public ITickList<Fluid> func_212247_j() {
      return this.field_205325_u;
   }

   public BitSet func_205749_a(GenerationStage.Carving var1) {
      throw new RuntimeException("Not yet implemented");
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

   @Nullable
   public StructureStart func_201585_a(String var1) {
      return (StructureStart)this.field_201619_q.get(var1);
   }

   public void func_201584_a(String var1, StructureStart var2) {
      this.field_201619_q.put(var1, var2);
   }

   public Map<String, StructureStart> func_201609_c() {
      return this.field_201619_q;
   }

   public void func_201612_a(Map<String, StructureStart> var1) {
      this.field_201619_q.clear();
      this.field_201619_q.putAll(var1);
   }

   @Nullable
   public LongSet func_201578_b(String var1) {
      return (LongSet)this.field_201620_r.computeIfAbsent(var1, (var0) -> {
         return new LongOpenHashSet();
      });
   }

   public void func_201583_a(String var1, long var2) {
      ((LongSet)this.field_201620_r.computeIfAbsent(var1, (var0) -> {
         return new LongOpenHashSet();
      })).add(var2);
   }

   public Map<String, LongSet> func_201604_d() {
      return this.field_201620_r;
   }

   public void func_201606_b(Map<String, LongSet> var1) {
      this.field_201620_r.clear();
      this.field_201620_r.putAll(var1);
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

   public void func_201595_A() {
      if (!this.field_201616_C.func_209003_a(ChunkStatus.POSTPROCESSED) && this.field_201617_D == 8) {
         ChunkPos var1 = this.func_76632_l();

         for(int var2 = 0; var2 < this.field_201622_t.length; ++var2) {
            if (this.field_201622_t[var2] != null) {
               ShortListIterator var3 = this.field_201622_t[var2].iterator();

               while(var3.hasNext()) {
                  Short var4 = (Short)var3.next();
                  BlockPos var5 = ChunkPrimer.func_201635_a(var4, var2, var1);
                  IBlockState var6 = this.field_76637_e.func_180495_p(var5);
                  IBlockState var7 = Block.func_199770_b(var6, this.field_76637_e, var5);
                  this.field_76637_e.func_180501_a(var5, var7, 20);
               }

               this.field_201622_t[var2].clear();
            }
         }

         if (this.field_201621_s instanceof ChunkPrimerTickList) {
            ((ChunkPrimerTickList)this.field_201621_s).func_205381_a(this.field_76637_e.func_205220_G_(), (var1x) -> {
               return this.field_76637_e.func_180495_p(var1x).func_177230_c();
            });
         }

         if (this.field_205325_u instanceof ChunkPrimerTickList) {
            ((ChunkPrimerTickList)this.field_205325_u).func_205381_a(this.field_76637_e.func_205219_F_(), (var1x) -> {
               return this.field_76637_e.func_204610_c(var1x).func_206886_c();
            });
         }

         Iterator var8 = (new HashSet(this.field_201618_i.keySet())).iterator();

         while(var8.hasNext()) {
            BlockPos var9 = (BlockPos)var8.next();
            this.func_175625_s(var9);
         }

         this.field_201618_i.clear();
         this.func_201574_a(ChunkStatus.POSTPROCESSED);
         this.field_196967_n.func_196990_a(this);
      }
   }

   @Nullable
   private TileEntity func_212815_a(BlockPos var1, NBTTagCompound var2) {
      TileEntity var3;
      if ("DUMMY".equals(var2.func_74779_i("id"))) {
         Block var4 = this.func_180495_p(var1).func_177230_c();
         if (var4 instanceof ITileEntityProvider) {
            var3 = ((ITileEntityProvider)var4).func_196283_a_(this.field_76637_e);
         } else {
            var3 = null;
            field_150817_t.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", var1, this.func_180495_p(var1));
         }
      } else {
         var3 = TileEntity.func_203403_c(var2);
      }

      if (var3 != null) {
         var3.func_174878_a(var1);
         this.func_150813_a(var3);
      } else {
         field_150817_t.warn("Tried to load a block entity for block {} but failed at location {}", this.func_180495_p(var1), var1);
      }

      return var3;
   }

   public UpgradeData func_196966_y() {
      return this.field_196967_n;
   }

   public ShortList[] func_201614_D() {
      return this.field_201622_t;
   }

   public void func_201610_a(short var1, int var2) {
      ChunkPrimer.func_205330_a(this.field_201622_t, var2).add(var1);
   }

   public ChunkStatus func_201589_g() {
      return this.field_201616_C;
   }

   public void func_201574_a(ChunkStatus var1) {
      this.field_201616_C = var1;
   }

   public void func_201613_c(String var1) {
      this.func_201574_a(ChunkStatus.func_202127_a(var1));
   }

   public void func_201605_F() {
      ++this.field_201617_D;
      if (this.field_201617_D > 8) {
         throw new RuntimeException("Error while adding chunk to cache. Too many neighbors");
      } else {
         if (this.func_201596_H()) {
            ((IThreadListener)this.field_76637_e).func_152344_a(this::func_201595_A);
         }

      }
   }

   public void func_201611_G() {
      --this.field_201617_D;
      if (this.field_201617_D < 0) {
         throw new RuntimeException("Error while removing chunk from cache. Not enough neighbors");
      }
   }

   public boolean func_201596_H() {
      return this.field_201617_D == 8;
   }

   public static enum EnumCreateEntityType {
      IMMEDIATE,
      QUEUED,
      CHECK;

      private EnumCreateEntityType() {
      }
   }
}
