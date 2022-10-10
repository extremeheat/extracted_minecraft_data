package net.minecraft.world.gen;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldGenTickList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenRegion implements IWorld {
   private static final Logger field_208303_a = LogManager.getLogger();
   private final ChunkPrimer[] field_201684_a;
   private final int field_201685_b;
   private final int field_201686_c;
   private final int field_201687_d;
   private final int field_201688_e;
   private final World field_201689_f;
   private final long field_201690_g;
   private final int field_201691_h;
   private final WorldInfo field_201692_i;
   private final Random field_201693_j;
   private final Dimension field_201694_k;
   private final IChunkGenSettings field_201695_l;
   private final ITickList<Block> field_205336_m = new WorldGenTickList((var1x) -> {
      return this.func_205771_y(var1x).func_205218_i_();
   });
   private final ITickList<Fluid> field_205337_n = new WorldGenTickList((var1x) -> {
      return this.func_205771_y(var1x).func_212247_j();
   });

   public WorldGenRegion(ChunkPrimer[] var1, int var2, int var3, int var4, int var5, World var6) {
      super();
      this.field_201684_a = var1;
      this.field_201685_b = var4;
      this.field_201686_c = var5;
      this.field_201687_d = var2;
      this.field_201688_e = var3;
      this.field_201689_f = var6;
      this.field_201690_g = var6.func_72905_C();
      this.field_201695_l = var6.func_72863_F().func_201711_g().func_201496_a_();
      this.field_201691_h = var6.func_181545_F();
      this.field_201692_i = var6.func_72912_H();
      this.field_201693_j = var6.func_201674_k();
      this.field_201694_k = var6.func_201675_m();
   }

   public int func_201679_a() {
      return this.field_201685_b;
   }

   public int func_201680_b() {
      return this.field_201686_c;
   }

   public boolean func_201678_a(int var1, int var2) {
      ChunkPrimer var3 = this.field_201684_a[0];
      ChunkPrimer var4 = this.field_201684_a[this.field_201684_a.length - 1];
      return var1 >= var3.func_76632_l().field_77276_a && var1 <= var4.func_76632_l().field_77276_a && var2 >= var3.func_76632_l().field_77275_b && var2 <= var4.func_76632_l().field_77275_b;
   }

   public IChunk func_72964_e(int var1, int var2) {
      if (this.func_201678_a(var1, var2)) {
         int var5 = var1 - this.field_201684_a[0].func_76632_l().field_77276_a;
         int var6 = var2 - this.field_201684_a[0].func_76632_l().field_77275_b;
         return this.field_201684_a[var5 + var6 * this.field_201687_d];
      } else {
         ChunkPrimer var3 = this.field_201684_a[0];
         ChunkPrimer var4 = this.field_201684_a[this.field_201684_a.length - 1];
         field_208303_a.error("Requested chunk : {} {}", var1, var2);
         field_208303_a.error("Region bounds : {} {} | {} {}", var3.func_76632_l().field_77276_a, var3.func_76632_l().field_77275_b, var4.func_76632_l().field_77276_a, var4.func_76632_l().field_77275_b);
         throw new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", var1, var2));
      }
   }

   public IBlockState func_180495_p(BlockPos var1) {
      return this.func_205771_y(var1).func_180495_p(var1);
   }

   public IFluidState func_204610_c(BlockPos var1) {
      return this.func_205771_y(var1).func_204610_c(var1);
   }

   @Nullable
   public EntityPlayer func_190525_a(double var1, double var3, double var5, double var7, Predicate<Entity> var9) {
      return null;
   }

   public int func_175657_ab() {
      return 0;
   }

   public boolean func_175623_d(BlockPos var1) {
      return this.func_180495_p(var1).func_196958_f();
   }

   public Biome func_180494_b(BlockPos var1) {
      Biome var2 = this.func_205771_y(var1).func_201590_e()[var1.func_177958_n() & 15 | (var1.func_177952_p() & 15) << 4];
      if (var2 == null) {
         throw new RuntimeException(String.format("Biome is null @ %s", var1));
      } else {
         return var2;
      }
   }

   public int func_175642_b(EnumLightType var1, BlockPos var2) {
      IChunk var3 = this.func_205771_y(var2);
      return var3.func_201587_a(var1, var2, this.func_201675_m().func_191066_m());
   }

   public int func_201669_a(BlockPos var1, int var2) {
      return this.func_205771_y(var1).func_201586_a(var1, var2, this.func_201675_m().func_191066_m());
   }

   public boolean func_175680_a(int var1, int var2, boolean var3) {
      return this.func_201678_a(var1, var2);
   }

   public boolean func_175655_b(BlockPos var1, boolean var2) {
      IBlockState var3 = this.func_180495_p(var1);
      if (var3.func_196958_f()) {
         return false;
      } else {
         if (var2) {
            var3.func_196949_c(this.field_201689_f, var1, 0);
         }

         return this.func_180501_a(var1, Blocks.field_150350_a.func_176223_P(), 3);
      }
   }

   public boolean func_175678_i(BlockPos var1) {
      return this.func_205771_y(var1).func_177444_d(var1);
   }

   @Nullable
   public TileEntity func_175625_s(BlockPos var1) {
      IChunk var2 = this.func_205771_y(var1);
      TileEntity var3 = var2.func_175625_s(var1);
      if (var3 != null) {
         return var3;
      } else {
         NBTTagCompound var4 = var2.func_201579_g(var1);
         if (var4 != null) {
            if ("DUMMY".equals(var4.func_74779_i("id"))) {
               var3 = ((ITileEntityProvider)this.func_180495_p(var1).func_177230_c()).func_196283_a_(this.field_201689_f);
            } else {
               var3 = TileEntity.func_203403_c(var4);
            }

            if (var3 != null) {
               var2.func_177426_a(var1, var3);
               return var3;
            }
         }

         if (var2.func_180495_p(var1).func_177230_c() instanceof ITileEntityProvider) {
            field_208303_a.warn("Tried to access a block entity before it was created. {}", var1);
         }

         return null;
      }
   }

   public boolean func_180501_a(BlockPos var1, IBlockState var2, int var3) {
      IChunk var4 = this.func_205771_y(var1);
      IBlockState var5 = var4.func_177436_a(var1, var2, false);
      Block var6 = var2.func_177230_c();
      if (var6.func_149716_u()) {
         if (var4.func_201589_g().func_202129_d() == ChunkStatus.Type.LEVELCHUNK) {
            var4.func_177426_a(var1, ((ITileEntityProvider)var6).func_196283_a_(this));
         } else {
            NBTTagCompound var7 = new NBTTagCompound();
            var7.func_74768_a("x", var1.func_177958_n());
            var7.func_74768_a("y", var1.func_177956_o());
            var7.func_74768_a("z", var1.func_177952_p());
            var7.func_74778_a("id", "DUMMY");
            var4.func_201591_a(var7);
         }
      } else if (var5 != null && var5.func_177230_c().func_149716_u()) {
         var4.func_177425_e(var1);
      }

      if (var2.func_202065_c(this, var1)) {
         this.func_201683_l(var1);
      }

      return true;
   }

   private void func_201683_l(BlockPos var1) {
      this.func_205771_y(var1).func_201594_d(var1);
   }

   public boolean func_72838_d(Entity var1) {
      int var2 = MathHelper.func_76128_c(var1.field_70165_t / 16.0D);
      int var3 = MathHelper.func_76128_c(var1.field_70161_v / 16.0D);
      this.func_72964_e(var2, var3).func_76612_a(var1);
      return true;
   }

   public boolean func_175698_g(BlockPos var1) {
      return this.func_180501_a(var1, Blocks.field_150350_a.func_176223_P(), 3);
   }

   public void func_175653_a(EnumLightType var1, BlockPos var2, int var3) {
      this.func_205771_y(var2).func_201580_a(var1, this.field_201694_k.func_191066_m(), var2, var3);
   }

   public WorldBorder func_175723_af() {
      return this.field_201689_f.func_175723_af();
   }

   public boolean func_195585_a(@Nullable Entity var1, VoxelShape var2) {
      return true;
   }

   public int func_175627_a(BlockPos var1, EnumFacing var2) {
      return this.func_180495_p(var1).func_185893_b(this, var1, var2);
   }

   public boolean func_201670_d() {
      return false;
   }

   @Deprecated
   public World func_201672_e() {
      return this.field_201689_f;
   }

   public WorldInfo func_72912_H() {
      return this.field_201692_i;
   }

   public DifficultyInstance func_175649_E(BlockPos var1) {
      if (!this.func_201678_a(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4)) {
         throw new RuntimeException("We are asking a region for a chunk out of bound");
      } else {
         return new DifficultyInstance(this.field_201689_f.func_175659_aa(), this.field_201689_f.func_72820_D(), 0L, this.field_201689_f.func_130001_d());
      }
   }

   @Nullable
   public WorldSavedDataStorage func_175693_T() {
      return this.field_201689_f.func_175693_T();
   }

   public IChunkProvider func_72863_F() {
      return this.field_201689_f.func_72863_F();
   }

   public ISaveHandler func_72860_G() {
      return this.field_201689_f.func_72860_G();
   }

   public long func_72905_C() {
      return this.field_201690_g;
   }

   public ITickList<Block> func_205220_G_() {
      return this.field_205336_m;
   }

   public ITickList<Fluid> func_205219_F_() {
      return this.field_205337_n;
   }

   public int func_181545_F() {
      return this.field_201691_h;
   }

   public Random func_201674_k() {
      return this.field_201693_j;
   }

   public void func_195592_c(BlockPos var1, Block var2) {
   }

   public int func_201676_a(Heightmap.Type var1, int var2, int var3) {
      return this.func_72964_e(var2 >> 4, var3 >> 4).func_201576_a(var1, var2 & 15, var3 & 15) + 1;
   }

   public void func_184133_a(@Nullable EntityPlayer var1, BlockPos var2, SoundEvent var3, SoundCategory var4, float var5, float var6) {
   }

   public void func_195594_a(IParticleData var1, double var2, double var4, double var6, double var8, double var10, double var12) {
   }

   public BlockPos func_175694_M() {
      return this.field_201689_f.func_175694_M();
   }

   public Dimension func_201675_m() {
      return this.field_201694_k;
   }
}
