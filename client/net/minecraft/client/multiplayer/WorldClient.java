package net.minecraft.client.multiplayer;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.particles.IParticleData;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.GameType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;

public class WorldClient extends World {
   private final NetHandlerPlayClient field_73035_a;
   private ChunkProviderClient field_73033_b;
   private final Set<Entity> field_73032_d = Sets.newHashSet();
   private final Set<Entity> field_73036_L = Sets.newHashSet();
   private final Minecraft field_73037_M = Minecraft.func_71410_x();
   private final Set<ChunkPos> field_73038_N = Sets.newHashSet();
   private int field_184158_M;
   protected Set<ChunkPos> field_184157_a;
   private Scoreboard field_200261_M;

   public WorldClient(NetHandlerPlayClient var1, WorldSettings var2, DimensionType var3, EnumDifficulty var4, Profiler var5) {
      super(new SaveHandlerMP(), new SaveDataMemoryStorage(), new WorldInfo(var2, "MpServer"), var3.func_186070_d(), var5, true);
      this.field_184158_M = this.field_73012_v.nextInt(12000);
      this.field_184157_a = Sets.newHashSet();
      this.field_200261_M = new Scoreboard();
      this.field_73035_a = var1;
      this.func_72912_H().func_176144_a(var4);
      this.func_175652_B(new BlockPos(8, 64, 8));
      this.field_73011_w.func_76558_a(this);
      this.field_73020_y = this.func_72970_h();
      this.func_72966_v();
      this.func_72947_a();
   }

   public void func_72835_b(BooleanSupplier var1) {
      super.func_72835_b(var1);
      this.func_82738_a(this.func_82737_E() + 1L);
      if (this.func_82736_K().func_82766_b("doDaylightCycle")) {
         this.func_72877_b(this.func_72820_D() + 1L);
      }

      this.field_72984_F.func_76320_a("reEntryProcessing");

      for(int var2 = 0; var2 < 10 && !this.field_73036_L.isEmpty(); ++var2) {
         Entity var3 = (Entity)this.field_73036_L.iterator().next();
         this.field_73036_L.remove(var3);
         if (!this.field_72996_f.contains(var3)) {
            this.func_72838_d(var3);
         }
      }

      this.field_72984_F.func_76318_c("chunkCache");
      this.field_73033_b.func_73156_b(var1);
      this.field_72984_F.func_76318_c("blocks");
      this.func_147456_g();
      this.field_72984_F.func_76319_b();
   }

   protected IChunkProvider func_72970_h() {
      this.field_73033_b = new ChunkProviderClient(this);
      return this.field_73033_b;
   }

   public boolean func_175680_a(int var1, int var2, boolean var3) {
      return var3 || this.func_72863_F().func_186025_d(var1, var2, true, false) != null;
   }

   protected void func_184154_a() {
      this.field_184157_a.clear();
      int var1 = this.field_73037_M.field_71474_y.field_151451_c;
      this.field_72984_F.func_76320_a("buildList");
      int var2 = MathHelper.func_76128_c(this.field_73037_M.field_71439_g.field_70165_t / 16.0D);
      int var3 = MathHelper.func_76128_c(this.field_73037_M.field_71439_g.field_70161_v / 16.0D);

      for(int var4 = -var1; var4 <= var1; ++var4) {
         for(int var5 = -var1; var5 <= var1; ++var5) {
            this.field_184157_a.add(new ChunkPos(var4 + var2, var5 + var3));
         }
      }

      this.field_72984_F.func_76319_b();
   }

   protected void func_147456_g() {
      this.func_184154_a();
      if (this.field_184158_M > 0) {
         --this.field_184158_M;
      }

      this.field_73038_N.retainAll(this.field_184157_a);
      if (this.field_73038_N.size() == this.field_184157_a.size()) {
         this.field_73038_N.clear();
      }

      int var1 = 0;
      Iterator var2 = this.field_184157_a.iterator();

      while(var2.hasNext()) {
         ChunkPos var3 = (ChunkPos)var2.next();
         if (!this.field_73038_N.contains(var3)) {
            int var4 = var3.field_77276_a * 16;
            int var5 = var3.field_77275_b * 16;
            this.field_72984_F.func_76320_a("getChunk");
            Chunk var6 = this.func_72964_e(var3.field_77276_a, var3.field_77275_b);
            this.func_147467_a(var4, var5, var6);
            this.field_72984_F.func_76319_b();
            this.field_73038_N.add(var3);
            ++var1;
            if (var1 >= 10) {
               return;
            }
         }
      }

   }

   public boolean func_72838_d(Entity var1) {
      boolean var2 = super.func_72838_d(var1);
      this.field_73032_d.add(var1);
      if (var2) {
         if (var1 instanceof EntityMinecart) {
            this.field_73037_M.func_147118_V().func_147682_a(new MovingSoundMinecart((EntityMinecart)var1));
         }
      } else {
         this.field_73036_L.add(var1);
      }

      return var2;
   }

   public void func_72900_e(Entity var1) {
      super.func_72900_e(var1);
      this.field_73032_d.remove(var1);
   }

   protected void func_72923_a(Entity var1) {
      super.func_72923_a(var1);
      if (this.field_73036_L.contains(var1)) {
         this.field_73036_L.remove(var1);
      }

   }

   protected void func_72847_b(Entity var1) {
      super.func_72847_b(var1);
      if (this.field_73032_d.contains(var1)) {
         if (var1.func_70089_S()) {
            this.field_73036_L.add(var1);
         } else {
            this.field_73032_d.remove(var1);
         }
      }

   }

   public void func_73027_a(int var1, Entity var2) {
      Entity var3 = this.func_73045_a(var1);
      if (var3 != null) {
         this.func_72900_e(var3);
      }

      this.field_73032_d.add(var2);
      var2.func_145769_d(var1);
      if (!this.func_72838_d(var2)) {
         this.field_73036_L.add(var2);
      }

      this.field_175729_l.func_76038_a(var1, var2);
   }

   @Nullable
   public Entity func_73045_a(int var1) {
      return (Entity)(var1 == this.field_73037_M.field_71439_g.func_145782_y() ? this.field_73037_M.field_71439_g : super.func_73045_a(var1));
   }

   public Entity func_73028_b(int var1) {
      Entity var2 = (Entity)this.field_175729_l.func_76049_d(var1);
      if (var2 != null) {
         this.field_73032_d.remove(var2);
         this.func_72900_e(var2);
      }

      return var2;
   }

   public void func_195597_b(BlockPos var1, IBlockState var2) {
      this.func_180501_a(var1, var2, 19);
   }

   public void func_72882_A() {
      this.field_73035_a.func_147298_b().func_150718_a(new TextComponentTranslation("multiplayer.status.quitting", new Object[0]));
   }

   protected void func_72979_l() {
   }

   protected void func_147467_a(int var1, int var2, Chunk var3) {
      super.func_147467_a(var1, var2, var3);
      if (this.field_184158_M == 0) {
         this.field_73005_l = this.field_73005_l * 3 + 1013904223;
         int var4 = this.field_73005_l >> 2;
         int var5 = var4 & 15;
         int var6 = var4 >> 8 & 15;
         int var7 = var4 >> 16 & 255;
         BlockPos var8 = new BlockPos(var5 + var1, var7, var6 + var2);
         IBlockState var9 = var3.func_180495_p(var8);
         var5 += var1;
         var6 += var2;
         if (var9.func_196958_f() && this.func_201669_a(var8, 0) <= this.field_73012_v.nextInt(8) && this.func_175642_b(EnumLightType.SKY, var8) <= 0) {
            double var10 = this.field_73037_M.field_71439_g.func_70092_e((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D);
            if (this.field_73037_M.field_71439_g != null && var10 > 4.0D && var10 < 256.0D) {
               this.func_184134_a((double)var5 + 0.5D, (double)var7 + 0.5D, (double)var6 + 0.5D, SoundEvents.field_187674_a, SoundCategory.AMBIENT, 0.7F, 0.8F + this.field_73012_v.nextFloat() * 0.2F, false);
               this.field_184158_M = this.field_73012_v.nextInt(12000) + 6000;
            }
         }
      }

   }

   public void func_73029_E(int var1, int var2, int var3) {
      boolean var4 = true;
      Random var5 = new Random();
      ItemStack var6 = this.field_73037_M.field_71439_g.func_184614_ca();
      boolean var7 = this.field_73037_M.field_71442_b.func_178889_l() == GameType.CREATIVE && !var6.func_190926_b() && var6.func_77973_b() == Blocks.field_180401_cv.func_199767_j();
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

      for(int var9 = 0; var9 < 667; ++var9) {
         this.func_184153_a(var1, var2, var3, 16, var5, var7, var8);
         this.func_184153_a(var1, var2, var3, 32, var5, var7, var8);
      }

   }

   public void func_184153_a(int var1, int var2, int var3, int var4, Random var5, boolean var6, BlockPos.MutableBlockPos var7) {
      int var8 = var1 + this.field_73012_v.nextInt(var4) - this.field_73012_v.nextInt(var4);
      int var9 = var2 + this.field_73012_v.nextInt(var4) - this.field_73012_v.nextInt(var4);
      int var10 = var3 + this.field_73012_v.nextInt(var4) - this.field_73012_v.nextInt(var4);
      var7.func_181079_c(var8, var9, var10);
      IBlockState var11 = this.func_180495_p(var7);
      var11.func_177230_c().func_180655_c(var11, this, var7, var5);
      IFluidState var12 = this.func_204610_c(var7);
      if (!var12.func_206888_e()) {
         var12.func_206881_a(this, var7, var5);
         IParticleData var13 = var12.func_204521_c();
         if (var13 != null && this.field_73012_v.nextInt(10) == 0) {
            boolean var14 = var11.func_193401_d(this, var7, EnumFacing.DOWN) == BlockFaceShape.SOLID;
            BlockPos var15 = var7.func_177977_b();
            this.func_211530_a(var15, this.func_180495_p(var15), var13, var14);
         }
      }

      if (var6 && var11.func_177230_c() == Blocks.field_180401_cv) {
         this.func_195594_a(Particles.field_197610_c, (double)((float)var8 + 0.5F), (double)((float)var9 + 0.5F), (double)((float)var10 + 0.5F), 0.0D, 0.0D, 0.0D);
      }

   }

   private void func_211530_a(BlockPos var1, IBlockState var2, IParticleData var3, boolean var4) {
      if (var2.func_204520_s().func_206888_e()) {
         VoxelShape var5 = var2.func_196952_d(this, var1);
         double var6 = var5.func_197758_c(EnumFacing.Axis.Y);
         if (var6 < 1.0D) {
            if (var4) {
               this.func_211834_a((double)var1.func_177958_n(), (double)(var1.func_177958_n() + 1), (double)var1.func_177952_p(), (double)(var1.func_177952_p() + 1), (double)(var1.func_177956_o() + 1) - 0.05D, var3);
            }
         } else if (!var2.func_203425_a(BlockTags.field_211923_H)) {
            double var8 = var5.func_197762_b(EnumFacing.Axis.Y);
            if (var8 > 0.0D) {
               this.func_211835_a(var1, var3, var5, (double)var1.func_177956_o() + var8 - 0.05D);
            } else {
               BlockPos var10 = var1.func_177977_b();
               IBlockState var11 = this.func_180495_p(var10);
               VoxelShape var12 = var11.func_196952_d(this, var10);
               double var13 = var12.func_197758_c(EnumFacing.Axis.Y);
               if (var13 < 1.0D && var11.func_204520_s().func_206888_e()) {
                  this.func_211835_a(var1, var3, var5, (double)var1.func_177956_o() - 0.05D);
               }
            }
         }

      }
   }

   private void func_211835_a(BlockPos var1, IParticleData var2, VoxelShape var3, double var4) {
      this.func_211834_a((double)var1.func_177958_n() + var3.func_197762_b(EnumFacing.Axis.X), (double)var1.func_177958_n() + var3.func_197758_c(EnumFacing.Axis.X), (double)var1.func_177952_p() + var3.func_197762_b(EnumFacing.Axis.Z), (double)var1.func_177952_p() + var3.func_197758_c(EnumFacing.Axis.Z), var4, var2);
   }

   private void func_211834_a(double var1, double var3, double var5, double var7, double var9, IParticleData var11) {
      this.func_195594_a(var11, var1 + (var3 - var1) * this.field_73012_v.nextDouble(), var9, var5 + (var7 - var5) * this.field_73012_v.nextDouble(), 0.0D, 0.0D, 0.0D);
   }

   public void func_73022_a() {
      this.field_72996_f.removeAll(this.field_72997_g);

      int var1;
      Entity var2;
      int var4;
      for(var1 = 0; var1 < this.field_72997_g.size(); ++var1) {
         var2 = (Entity)this.field_72997_g.get(var1);
         int var3 = var2.field_70176_ah;
         var4 = var2.field_70164_aj;
         if (var2.field_70175_ag && this.func_175680_a(var3, var4, true)) {
            this.func_72964_e(var3, var4).func_76622_b(var2);
         }
      }

      for(var1 = 0; var1 < this.field_72997_g.size(); ++var1) {
         this.func_72847_b((Entity)this.field_72997_g.get(var1));
      }

      this.field_72997_g.clear();

      for(var1 = 0; var1 < this.field_72996_f.size(); ++var1) {
         var2 = (Entity)this.field_72996_f.get(var1);
         Entity var6 = var2.func_184187_bx();
         if (var6 != null) {
            if (!var6.field_70128_L && var6.func_184196_w(var2)) {
               continue;
            }

            var2.func_184210_p();
         }

         if (var2.field_70128_L) {
            var4 = var2.field_70176_ah;
            int var5 = var2.field_70164_aj;
            if (var2.field_70175_ag && this.func_175680_a(var4, var5, true)) {
               this.func_72964_e(var4, var5).func_76622_b(var2);
            }

            this.field_72996_f.remove(var1--);
            this.func_72847_b(var2);
         }
      }

   }

   public CrashReportCategory func_72914_a(CrashReport var1) {
      CrashReportCategory var2 = super.func_72914_a(var1);
      var2.func_189529_a("Forced entities", () -> {
         return this.field_73032_d.size() + " total; " + this.field_73032_d;
      });
      var2.func_189529_a("Retry entities", () -> {
         return this.field_73036_L.size() + " total; " + this.field_73036_L;
      });
      var2.func_189529_a("Server brand", () -> {
         return this.field_73037_M.field_71439_g.func_142021_k();
      });
      var2.func_189529_a("Server type", () -> {
         return this.field_73037_M.func_71401_C() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
      });
      return var2;
   }

   public void func_184148_a(@Nullable EntityPlayer var1, double var2, double var4, double var6, SoundEvent var8, SoundCategory var9, float var10, float var11) {
      if (var1 == this.field_73037_M.field_71439_g) {
         this.func_184134_a(var2, var4, var6, var8, var9, var10, var11, false);
      }

   }

   public void func_184156_a(BlockPos var1, SoundEvent var2, SoundCategory var3, float var4, float var5, boolean var6) {
      this.func_184134_a((double)var1.func_177958_n() + 0.5D, (double)var1.func_177956_o() + 0.5D, (double)var1.func_177952_p() + 0.5D, var2, var3, var4, var5, var6);
   }

   public void func_184134_a(double var1, double var3, double var5, SoundEvent var7, SoundCategory var8, float var9, float var10, boolean var11) {
      double var12 = this.field_73037_M.func_175606_aa().func_70092_e(var1, var3, var5);
      SimpleSound var14 = new SimpleSound(var7, var8, var9, var10, (float)var1, (float)var3, (float)var5);
      if (var11 && var12 > 100.0D) {
         double var15 = Math.sqrt(var12) / 40.0D;
         this.field_73037_M.func_147118_V().func_147681_a(var14, (int)(var15 * 20.0D));
      } else {
         this.field_73037_M.func_147118_V().func_147682_a(var14);
      }

   }

   public void func_92088_a(double var1, double var3, double var5, double var7, double var9, double var11, @Nullable NBTTagCompound var13) {
      this.field_73037_M.field_71452_i.func_78873_a(new ParticleFirework.Starter(this, var1, var3, var5, var7, var9, var11, this.field_73037_M.field_71452_i, var13));
   }

   public void func_184135_a(Packet<?> var1) {
      this.field_73035_a.func_147297_a(var1);
   }

   public RecipeManager func_199532_z() {
      return this.field_73035_a.func_199526_e();
   }

   public void func_96443_a(Scoreboard var1) {
      this.field_200261_M = var1;
   }

   public void func_72877_b(long var1) {
      if (var1 < 0L) {
         var1 = -var1;
         this.func_82736_K().func_82764_b("doDaylightCycle", "false", (MinecraftServer)null);
      } else {
         this.func_82736_K().func_82764_b("doDaylightCycle", "true", (MinecraftServer)null);
      }

      super.func_72877_b(var1);
   }

   public ITickList<Block> func_205220_G_() {
      return EmptyTickList.func_205388_a();
   }

   public ITickList<Fluid> func_205219_F_() {
      return EmptyTickList.func_205388_a();
   }

   public ChunkProviderClient func_72863_F() {
      return (ChunkProviderClient)super.func_72863_F();
   }

   public Scoreboard func_96441_U() {
      return this.field_200261_M;
   }

   public NetworkTagManager func_205772_D() {
      return this.field_73035_a.func_199724_l();
   }

   // $FF: synthetic method
   public IChunkProvider func_72863_F() {
      return this.func_72863_F();
   }
}
