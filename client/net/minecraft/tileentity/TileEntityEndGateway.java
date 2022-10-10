package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.gen.feature.EndGatewayConfig;
import net.minecraft.world.gen.feature.EndIslandFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TileEntityEndGateway extends TileEntityEndPortal implements ITickable {
   private static final Logger field_195503_a = LogManager.getLogger();
   private long field_195504_f;
   private int field_195505_g;
   private BlockPos field_195506_h;
   private boolean field_195507_i;

   public TileEntityEndGateway() {
      super(TileEntityType.field_200991_v);
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      var1.func_74772_a("Age", this.field_195504_f);
      if (this.field_195506_h != null) {
         var1.func_74782_a("ExitPortal", NBTUtil.func_186859_a(this.field_195506_h));
      }

      if (this.field_195507_i) {
         var1.func_74757_a("ExactTeleport", this.field_195507_i);
      }

      return var1;
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_195504_f = var1.func_74763_f("Age");
      if (var1.func_150297_b("ExitPortal", 10)) {
         this.field_195506_h = NBTUtil.func_186861_c(var1.func_74775_l("ExitPortal"));
      }

      this.field_195507_i = var1.func_74767_n("ExactTeleport");
   }

   public double func_145833_n() {
      return 65536.0D;
   }

   public void func_73660_a() {
      boolean var1 = this.func_195499_c();
      boolean var2 = this.func_195500_d();
      ++this.field_195504_f;
      if (var2) {
         --this.field_195505_g;
      } else if (!this.field_145850_b.field_72995_K) {
         List var3 = this.field_145850_b.func_72872_a(Entity.class, new AxisAlignedBB(this.func_174877_v()));
         if (!var3.isEmpty()) {
            this.func_195496_a((Entity)var3.get(0));
         }

         if (this.field_195504_f % 2400L == 0L) {
            this.func_195490_f();
         }
      }

      if (var1 != this.func_195499_c() || var2 != this.func_195500_d()) {
         this.func_70296_d();
      }

   }

   public boolean func_195499_c() {
      return this.field_195504_f < 200L;
   }

   public boolean func_195500_d() {
      return this.field_195505_g > 0;
   }

   public float func_195497_a(float var1) {
      return MathHelper.func_76131_a(((float)this.field_195504_f + var1) / 200.0F, 0.0F, 1.0F);
   }

   public float func_195491_b(float var1) {
      return 1.0F - MathHelper.func_76131_a(((float)this.field_195505_g - var1) / 40.0F, 0.0F, 1.0F);
   }

   @Nullable
   public SPacketUpdateTileEntity func_189518_D_() {
      return new SPacketUpdateTileEntity(this.field_174879_c, 8, this.func_189517_E_());
   }

   public NBTTagCompound func_189517_E_() {
      return this.func_189515_b(new NBTTagCompound());
   }

   public void func_195490_f() {
      if (!this.field_145850_b.field_72995_K) {
         this.field_195505_g = 40;
         this.field_145850_b.func_175641_c(this.func_174877_v(), this.func_195044_w().func_177230_c(), 1, 0);
         this.func_70296_d();
      }

   }

   public boolean func_145842_c(int var1, int var2) {
      if (var1 == 1) {
         this.field_195505_g = 40;
         return true;
      } else {
         return super.func_145842_c(var1, var2);
      }
   }

   public void func_195496_a(Entity var1) {
      if (!this.field_145850_b.field_72995_K && !this.func_195500_d()) {
         this.field_195505_g = 100;
         if (this.field_195506_h == null && this.field_145850_b.field_73011_w instanceof EndDimension) {
            this.func_195501_j();
         }

         if (this.field_195506_h != null) {
            BlockPos var2 = this.field_195507_i ? this.field_195506_h : this.func_195502_i();
            var1.func_70634_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D);
         }

         this.func_195490_f();
      }
   }

   private BlockPos func_195502_i() {
      BlockPos var1 = func_195494_a(this.field_145850_b, this.field_195506_h, 5, false);
      field_195503_a.debug("Best exit position for portal at {} is {}", this.field_195506_h, var1);
      return var1.func_177984_a();
   }

   private void func_195501_j() {
      Vec3d var1 = (new Vec3d((double)this.func_174877_v().func_177958_n(), 0.0D, (double)this.func_174877_v().func_177952_p())).func_72432_b();
      Vec3d var2 = var1.func_186678_a(1024.0D);

      int var3;
      for(var3 = 16; func_195495_a(this.field_145850_b, var2).func_76625_h() > 0 && var3-- > 0; var2 = var2.func_178787_e(var1.func_186678_a(-16.0D))) {
         field_195503_a.debug("Skipping backwards past nonempty chunk at {}", var2);
      }

      for(var3 = 16; func_195495_a(this.field_145850_b, var2).func_76625_h() == 0 && var3-- > 0; var2 = var2.func_178787_e(var1.func_186678_a(16.0D))) {
         field_195503_a.debug("Skipping forward past empty chunk at {}", var2);
      }

      field_195503_a.debug("Found chunk at {}", var2);
      Chunk var4 = func_195495_a(this.field_145850_b, var2);
      this.field_195506_h = func_195498_a(var4);
      if (this.field_195506_h == null) {
         this.field_195506_h = new BlockPos(var2.field_72450_a + 0.5D, 75.0D, var2.field_72449_c + 0.5D);
         field_195503_a.debug("Failed to find suitable block, settling on {}", this.field_195506_h);
         (new EndIslandFeature()).func_212245_a(this.field_145850_b, this.field_145850_b.func_72863_F().func_201711_g(), new Random(this.field_195506_h.func_177986_g()), this.field_195506_h, (NoFeatureConfig)IFeatureConfig.field_202429_e);
      } else {
         field_195503_a.debug("Found block at {}", this.field_195506_h);
      }

      this.field_195506_h = func_195494_a(this.field_145850_b, this.field_195506_h, 16, true);
      field_195503_a.debug("Creating portal at {}", this.field_195506_h);
      this.field_195506_h = this.field_195506_h.func_177981_b(10);
      this.func_195492_c(this.field_195506_h);
      this.func_70296_d();
   }

   private static BlockPos func_195494_a(IBlockReader var0, BlockPos var1, int var2, boolean var3) {
      BlockPos var4 = null;

      for(int var5 = -var2; var5 <= var2; ++var5) {
         for(int var6 = -var2; var6 <= var2; ++var6) {
            if (var5 != 0 || var6 != 0 || var3) {
               for(int var7 = 255; var7 > (var4 == null ? 0 : var4.func_177956_o()); --var7) {
                  BlockPos var8 = new BlockPos(var1.func_177958_n() + var5, var7, var1.func_177952_p() + var6);
                  IBlockState var9 = var0.func_180495_p(var8);
                  if (var9.func_185898_k() && (var3 || var9.func_177230_c() != Blocks.field_150357_h)) {
                     var4 = var8;
                     break;
                  }
               }
            }
         }
      }

      return var4 == null ? var1 : var4;
   }

   private static Chunk func_195495_a(World var0, Vec3d var1) {
      return var0.func_72964_e(MathHelper.func_76128_c(var1.field_72450_a / 16.0D), MathHelper.func_76128_c(var1.field_72449_c / 16.0D));
   }

   @Nullable
   private static BlockPos func_195498_a(Chunk var0) {
      BlockPos var1 = new BlockPos(var0.field_76635_g * 16, 30, var0.field_76647_h * 16);
      int var2 = var0.func_76625_h() + 16 - 1;
      BlockPos var3 = new BlockPos(var0.field_76635_g * 16 + 16 - 1, var2, var0.field_76647_h * 16 + 16 - 1);
      BlockPos var4 = null;
      double var5 = 0.0D;
      Iterator var7 = BlockPos.func_177980_a(var1, var3).iterator();

      while(true) {
         BlockPos var8;
         double var10;
         do {
            do {
               IBlockState var9;
               do {
                  do {
                     if (!var7.hasNext()) {
                        return var4;
                     }

                     var8 = (BlockPos)var7.next();
                     var9 = var0.func_180495_p(var8);
                  } while(var9.func_177230_c() != Blocks.field_150377_bs);
               } while(var0.func_180495_p(var8.func_177981_b(1)).func_185898_k());
            } while(var0.func_180495_p(var8.func_177981_b(2)).func_185898_k());

            var10 = var8.func_177957_d(0.0D, 0.0D, 0.0D);
         } while(var4 != null && var10 >= var5);

         var4 = var8;
         var5 = var10;
      }
   }

   private void func_195492_c(BlockPos var1) {
      Feature.field_202299_as.func_212245_a(this.field_145850_b, this.field_145850_b.func_72863_F().func_201711_g(), new Random(), var1, new EndGatewayConfig(false));
      TileEntity var2 = this.field_145850_b.func_175625_s(var1);
      if (var2 instanceof TileEntityEndGateway) {
         TileEntityEndGateway var3 = (TileEntityEndGateway)var2;
         var3.field_195506_h = new BlockPos(this.func_174877_v());
         var3.func_70296_d();
      } else {
         field_195503_a.warn("Couldn't save exit portal at {}", var1);
      }

   }

   public boolean func_184313_a(EnumFacing var1) {
      return Block.func_176225_a(this.func_195044_w(), this.field_145850_b, this.func_174877_v(), var1);
   }

   public int func_195493_h() {
      int var1 = 0;
      EnumFacing[] var2 = EnumFacing.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing var5 = var2[var4];
         var1 += this.func_184313_a(var5) ? 1 : 0;
      }

      return var1;
   }

   public void func_195489_b(BlockPos var1) {
      this.field_195507_i = true;
      this.field_195506_h = var1;
   }
}
