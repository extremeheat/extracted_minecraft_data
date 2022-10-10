package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPrimer implements IChunk {
   private static final Logger field_201653_a = LogManager.getLogger();
   private final ChunkPos field_201654_b;
   private boolean field_201655_c;
   private final AtomicInteger field_205768_d;
   private Biome[] field_201656_d;
   private final Map<Heightmap.Type, Heightmap> field_201657_e;
   private volatile ChunkStatus field_201658_f;
   private final Map<BlockPos, TileEntity> field_201659_g;
   private final Map<BlockPos, NBTTagCompound> field_201660_h;
   private final ChunkSection[] field_201661_i;
   private final List<NBTTagCompound> field_201662_j;
   private final List<BlockPos> field_201663_k;
   private final ShortList[] field_201665_m;
   private final Map<String, StructureStart> field_201666_n;
   private final Map<String, LongSet> field_201667_o;
   private final UpgradeData field_201668_p;
   private final ChunkPrimerTickList<Block> field_201664_l;
   private final ChunkPrimerTickList<Fluid> field_205333_q;
   private long field_209217_s;
   private final Map<GenerationStage.Carving, BitSet> field_205769_s;
   private boolean field_207740_t;

   public ChunkPrimer(int var1, int var2, UpgradeData var3) {
      this(new ChunkPos(var1, var2), var3);
   }

   public ChunkPrimer(ChunkPos var1, UpgradeData var2) {
      super();
      this.field_205768_d = new AtomicInteger();
      this.field_201657_e = Maps.newEnumMap(Heightmap.Type.class);
      this.field_201658_f = ChunkStatus.EMPTY;
      this.field_201659_g = Maps.newHashMap();
      this.field_201660_h = Maps.newHashMap();
      this.field_201661_i = new ChunkSection[16];
      this.field_201662_j = Lists.newArrayList();
      this.field_201663_k = Lists.newArrayList();
      this.field_201665_m = new ShortList[16];
      this.field_201666_n = Maps.newHashMap();
      this.field_201667_o = Maps.newHashMap();
      this.field_205769_s = Maps.newHashMap();
      this.field_201654_b = var1;
      this.field_201668_p = var2;
      this.field_201664_l = new ChunkPrimerTickList((var0) -> {
         return var0 == null || var0.func_176223_P().func_196958_f();
      }, IRegistry.field_212618_g::func_177774_c, IRegistry.field_212618_g::func_82594_a, var1);
      this.field_205333_q = new ChunkPrimerTickList((var0) -> {
         return var0 == null || var0 == Fluids.field_204541_a;
      }, IRegistry.field_212619_h::func_177774_c, IRegistry.field_212619_h::func_82594_a, var1);
   }

   public static ShortList func_205330_a(ShortList[] var0, int var1) {
      if (var0[var1] == null) {
         var0[var1] = new ShortArrayList();
      }

      return var0[var1];
   }

   @Nullable
   public IBlockState func_180495_p(BlockPos var1) {
      int var2 = var1.func_177958_n();
      int var3 = var1.func_177956_o();
      int var4 = var1.func_177952_p();
      if (var3 >= 0 && var3 < 256) {
         return this.field_201661_i[var3 >> 4] == Chunk.field_186036_a ? Blocks.field_150350_a.func_176223_P() : this.field_201661_i[var3 >> 4].func_177485_a(var2 & 15, var3 & 15, var4 & 15);
      } else {
         return Blocks.field_201940_ji.func_176223_P();
      }
   }

   public IFluidState func_204610_c(BlockPos var1) {
      int var2 = var1.func_177958_n();
      int var3 = var1.func_177956_o();
      int var4 = var1.func_177952_p();
      return var3 >= 0 && var3 < 256 && this.field_201661_i[var3 >> 4] != Chunk.field_186036_a ? this.field_201661_i[var3 >> 4].func_206914_b(var2 & 15, var3 & 15, var4 & 15) : Fluids.field_204541_a.func_207188_f();
   }

   public List<BlockPos> func_201582_h() {
      return this.field_201663_k;
   }

   public ShortList[] func_201647_i() {
      ShortList[] var1 = new ShortList[16];
      Iterator var2 = this.field_201663_k.iterator();

      while(var2.hasNext()) {
         BlockPos var3 = (BlockPos)var2.next();
         func_205330_a(var1, var3.func_177956_o() >> 4).add(func_201651_i(var3));
      }

      return var1;
   }

   public void func_201646_a(short var1, int var2) {
      this.func_201637_h(func_201635_a(var1, var2, this.field_201654_b));
   }

   public void func_201637_h(BlockPos var1) {
      this.field_201663_k.add(var1);
   }

   @Nullable
   public IBlockState func_177436_a(BlockPos var1, IBlockState var2, boolean var3) {
      int var4 = var1.func_177958_n();
      int var5 = var1.func_177956_o();
      int var6 = var1.func_177952_p();
      if (var5 >= 0 && var5 < 256) {
         if (var2.func_185906_d() > 0) {
            this.field_201663_k.add(new BlockPos((var4 & 15) + this.func_76632_l().func_180334_c(), var5, (var6 & 15) + this.func_76632_l().func_180333_d()));
         }

         if (this.field_201661_i[var5 >> 4] == Chunk.field_186036_a) {
            if (var2.func_177230_c() == Blocks.field_150350_a) {
               return var2;
            }

            this.field_201661_i[var5 >> 4] = new ChunkSection(var5 >> 4 << 4, this.func_201649_r());
         }

         IBlockState var7 = this.field_201661_i[var5 >> 4].func_177485_a(var4 & 15, var5 & 15, var6 & 15);
         this.field_201661_i[var5 >> 4].func_177484_a(var4 & 15, var5 & 15, var6 & 15, var2);
         if (this.field_207740_t) {
            this.func_207902_c(Heightmap.Type.MOTION_BLOCKING).func_202270_a(var4 & 15, var5, var6 & 15, var2);
            this.func_207902_c(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).func_202270_a(var4 & 15, var5, var6 & 15, var2);
            this.func_207902_c(Heightmap.Type.OCEAN_FLOOR).func_202270_a(var4 & 15, var5, var6 & 15, var2);
            this.func_207902_c(Heightmap.Type.WORLD_SURFACE).func_202270_a(var4 & 15, var5, var6 & 15, var2);
         }

         return var7;
      } else {
         return Blocks.field_201940_ji.func_176223_P();
      }
   }

   public void func_177426_a(BlockPos var1, TileEntity var2) {
      var2.func_174878_a(var1);
      this.field_201659_g.put(var1, var2);
   }

   public Set<BlockPos> func_201638_j() {
      HashSet var1 = Sets.newHashSet(this.field_201660_h.keySet());
      var1.addAll(this.field_201659_g.keySet());
      return var1;
   }

   @Nullable
   public TileEntity func_175625_s(BlockPos var1) {
      return (TileEntity)this.field_201659_g.get(var1);
   }

   public Map<BlockPos, TileEntity> func_201627_k() {
      return this.field_201659_g;
   }

   public void func_201626_b(NBTTagCompound var1) {
      this.field_201662_j.add(var1);
   }

   public void func_76612_a(Entity var1) {
      NBTTagCompound var2 = new NBTTagCompound();
      var1.func_70039_c(var2);
      this.func_201626_b(var2);
   }

   public List<NBTTagCompound> func_201652_l() {
      return this.field_201662_j;
   }

   public void func_201577_a(Biome[] var1) {
      this.field_201656_d = var1;
   }

   public Biome[] func_201590_e() {
      return this.field_201656_d;
   }

   public void func_177427_f(boolean var1) {
      this.field_201655_c = var1;
   }

   public boolean func_201593_f() {
      return this.field_201655_c;
   }

   public ChunkStatus func_201589_g() {
      return this.field_201658_f;
   }

   public void func_201574_a(ChunkStatus var1) {
      this.field_201658_f = var1;
      this.func_177427_f(true);
   }

   public void func_201650_c(String var1) {
      this.func_201574_a(ChunkStatus.func_202127_a(var1));
   }

   public ChunkSection[] func_76587_i() {
      return this.field_201661_i;
   }

   public int func_201587_a(EnumLightType var1, BlockPos var2, boolean var3) {
      int var4 = var2.func_177958_n() & 15;
      int var5 = var2.func_177956_o();
      int var6 = var2.func_177952_p() & 15;
      int var7 = var5 >> 4;
      if (var7 >= 0 && var7 <= this.field_201661_i.length - 1) {
         ChunkSection var8 = this.field_201661_i[var7];
         if (var8 == Chunk.field_186036_a) {
            return this.func_177444_d(var2) ? var1.field_77198_c : 0;
         } else if (var1 == EnumLightType.SKY) {
            return !var3 ? 0 : var8.func_76670_c(var4, var5 & 15, var6);
         } else {
            return var1 == EnumLightType.BLOCK ? var8.func_76674_d(var4, var5 & 15, var6) : var1.field_77198_c;
         }
      } else {
         return 0;
      }
   }

   public int func_201586_a(BlockPos var1, int var2, boolean var3) {
      int var4 = var1.func_177958_n() & 15;
      int var5 = var1.func_177956_o();
      int var6 = var1.func_177952_p() & 15;
      int var7 = var5 >> 4;
      if (var7 >= 0 && var7 <= this.field_201661_i.length - 1) {
         ChunkSection var8 = this.field_201661_i[var7];
         if (var8 == Chunk.field_186036_a) {
            return this.func_201649_r() && var2 < EnumLightType.SKY.field_77198_c ? EnumLightType.SKY.field_77198_c - var2 : 0;
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

   public boolean func_177444_d(BlockPos var1) {
      int var2 = var1.func_177958_n() & 15;
      int var3 = var1.func_177956_o();
      int var4 = var1.func_177952_p() & 15;
      return var3 >= this.func_201576_a(Heightmap.Type.MOTION_BLOCKING, var2, var4);
   }

   public void func_201630_a(ChunkSection[] var1) {
      if (this.field_201661_i.length != var1.length) {
         field_201653_a.warn("Could not set level chunk sections, array length is {} instead of {}", var1.length, this.field_201661_i.length);
      } else {
         System.arraycopy(var1, 0, this.field_201661_i, 0, this.field_201661_i.length);
      }
   }

   public Set<Heightmap.Type> func_201634_m() {
      return this.field_201657_e.keySet();
   }

   @Nullable
   public Heightmap func_201642_a(Heightmap.Type var1) {
      return (Heightmap)this.field_201657_e.get(var1);
   }

   public void func_201643_a(Heightmap.Type var1, long[] var2) {
      this.func_207902_c(var1).func_202268_a(var2);
   }

   public void func_201588_a(Heightmap.Type... var1) {
      Heightmap.Type[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Heightmap.Type var5 = var2[var4];
         this.func_207902_c(var5);
      }

   }

   private Heightmap func_207902_c(Heightmap.Type var1) {
      return (Heightmap)this.field_201657_e.computeIfAbsent(var1, (var1x) -> {
         Heightmap var2 = new Heightmap(this, var1x);
         var2.func_202266_a();
         return var2;
      });
   }

   public int func_201576_a(Heightmap.Type var1, int var2, int var3) {
      Heightmap var4 = (Heightmap)this.field_201657_e.get(var1);
      if (var4 == null) {
         this.func_201588_a(var1);
         var4 = (Heightmap)this.field_201657_e.get(var1);
      }

      return var4.func_202273_a(var2 & 15, var3 & 15) - 1;
   }

   public ChunkPos func_76632_l() {
      return this.field_201654_b;
   }

   public void func_177432_b(long var1) {
   }

   @Nullable
   public StructureStart func_201585_a(String var1) {
      return (StructureStart)this.field_201666_n.get(var1);
   }

   public void func_201584_a(String var1, StructureStart var2) {
      this.field_201666_n.put(var1, var2);
      this.field_201655_c = true;
   }

   public Map<String, StructureStart> func_201609_c() {
      return Collections.unmodifiableMap(this.field_201666_n);
   }

   public void func_201648_a(Map<String, StructureStart> var1) {
      this.field_201666_n.clear();
      this.field_201666_n.putAll(var1);
      this.field_201655_c = true;
   }

   @Nullable
   public LongSet func_201578_b(String var1) {
      return (LongSet)this.field_201667_o.computeIfAbsent(var1, (var0) -> {
         return new LongOpenHashSet();
      });
   }

   public void func_201583_a(String var1, long var2) {
      ((LongSet)this.field_201667_o.computeIfAbsent(var1, (var0) -> {
         return new LongOpenHashSet();
      })).add(var2);
      this.field_201655_c = true;
   }

   public Map<String, LongSet> func_201604_d() {
      return Collections.unmodifiableMap(this.field_201667_o);
   }

   public void func_201641_b(Map<String, LongSet> var1) {
      this.field_201667_o.clear();
      this.field_201667_o.putAll(var1);
      this.field_201655_c = true;
   }

   public void func_201580_a(EnumLightType var1, boolean var2, BlockPos var3, int var4) {
      int var5 = var3.func_177958_n() & 15;
      int var6 = var3.func_177956_o();
      int var7 = var3.func_177952_p() & 15;
      int var8 = var6 >> 4;
      if (var8 < 16 && var8 >= 0) {
         if (this.field_201661_i[var8] == Chunk.field_186036_a) {
            if (var4 == var1.field_77198_c) {
               return;
            }

            this.field_201661_i[var8] = new ChunkSection(var8 << 4, this.func_201649_r());
         }

         if (var1 == EnumLightType.SKY) {
            if (var2) {
               this.field_201661_i[var8].func_76657_c(var5, var6 & 15, var7, var4);
            }
         } else if (var1 == EnumLightType.BLOCK) {
            this.field_201661_i[var8].func_76677_d(var5, var6 & 15, var7, var4);
         }

      }
   }

   public static short func_201651_i(BlockPos var0) {
      int var1 = var0.func_177958_n();
      int var2 = var0.func_177956_o();
      int var3 = var0.func_177952_p();
      int var4 = var1 & 15;
      int var5 = var2 & 15;
      int var6 = var3 & 15;
      return (short)(var4 | var5 << 4 | var6 << 8);
   }

   public static BlockPos func_201635_a(short var0, int var1, ChunkPos var2) {
      int var3 = (var0 & 15) + (var2.field_77276_a << 4);
      int var4 = (var0 >>> 4 & 15) + (var1 << 4);
      int var5 = (var0 >>> 8 & 15) + (var2.field_77275_b << 4);
      return new BlockPos(var3, var4, var5);
   }

   public void func_201594_d(BlockPos var1) {
      if (!World.func_189509_E(var1)) {
         func_205330_a(this.field_201665_m, var1.func_177956_o() >> 4).add(func_201651_i(var1));
      }

   }

   public ShortList[] func_201645_n() {
      return this.field_201665_m;
   }

   public void func_201636_b(short var1, int var2) {
      func_205330_a(this.field_201665_m, var2).add(var1);
   }

   public ChunkPrimerTickList<Block> func_205218_i_() {
      return this.field_201664_l;
   }

   public ChunkPrimerTickList<Fluid> func_212247_j() {
      return this.field_205333_q;
   }

   private boolean func_201649_r() {
      return true;
   }

   public UpgradeData func_201631_p() {
      return this.field_201668_p;
   }

   public void func_209215_b(long var1) {
      this.field_209217_s = var1;
   }

   public long func_209216_m() {
      return this.field_209217_s;
   }

   public void func_201591_a(NBTTagCompound var1) {
      this.field_201660_h.put(new BlockPos(var1.func_74762_e("x"), var1.func_74762_e("y"), var1.func_74762_e("z")), var1);
   }

   public Map<BlockPos, NBTTagCompound> func_201632_q() {
      return Collections.unmodifiableMap(this.field_201660_h);
   }

   public NBTTagCompound func_201579_g(BlockPos var1) {
      return (NBTTagCompound)this.field_201660_h.get(var1);
   }

   public void func_177425_e(BlockPos var1) {
      this.field_201659_g.remove(var1);
      this.field_201660_h.remove(var1);
   }

   public BitSet func_205749_a(GenerationStage.Carving var1) {
      return (BitSet)this.field_205769_s.computeIfAbsent(var1, (var0) -> {
         return new BitSet(65536);
      });
   }

   public void func_205767_a(GenerationStage.Carving var1, BitSet var2) {
      this.field_205769_s.put(var1, var2);
   }

   public void func_205747_a(int var1) {
      this.field_205768_d.addAndGet(var1);
   }

   public boolean func_205748_B() {
      return this.field_205768_d.get() > 0;
   }

   public void func_207739_b(boolean var1) {
      this.field_207740_t = var1;
   }

   // $FF: synthetic method
   public ITickList func_212247_j() {
      return this.func_212247_j();
   }

   // $FF: synthetic method
   public ITickList func_205218_i_() {
      return this.func_205218_i_();
   }
}
