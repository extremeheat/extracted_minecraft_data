package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockConcretePowder;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityFallingBlock extends Entity {
   private IBlockState field_175132_d;
   public int field_145812_b;
   public boolean field_145813_c;
   private boolean field_145808_f;
   private boolean field_145809_g;
   private int field_145815_h;
   private float field_145816_i;
   public NBTTagCompound field_145810_d;
   protected static final DataParameter<BlockPos> field_184532_d;

   public EntityFallingBlock(World var1) {
      super(EntityType.field_200809_w, var1);
      this.field_175132_d = Blocks.field_150354_m.func_176223_P();
      this.field_145813_c = true;
      this.field_145815_h = 40;
      this.field_145816_i = 2.0F;
   }

   public EntityFallingBlock(World var1, double var2, double var4, double var6, IBlockState var8) {
      this(var1);
      this.field_175132_d = var8;
      this.field_70156_m = true;
      this.func_70105_a(0.98F, 0.98F);
      this.func_70107_b(var2, var4 + (double)((1.0F - this.field_70131_O) / 2.0F), var6);
      this.field_70159_w = 0.0D;
      this.field_70181_x = 0.0D;
      this.field_70179_y = 0.0D;
      this.field_70169_q = var2;
      this.field_70167_r = var4;
      this.field_70166_s = var6;
      this.func_184530_a(new BlockPos(this));
   }

   public boolean func_70075_an() {
      return false;
   }

   public void func_184530_a(BlockPos var1) {
      this.field_70180_af.func_187227_b(field_184532_d, var1);
   }

   public BlockPos func_184531_j() {
      return (BlockPos)this.field_70180_af.func_187225_a(field_184532_d);
   }

   protected boolean func_70041_e_() {
      return false;
   }

   protected void func_70088_a() {
      this.field_70180_af.func_187214_a(field_184532_d, BlockPos.field_177992_a);
   }

   public boolean func_70067_L() {
      return !this.field_70128_L;
   }

   public void func_70071_h_() {
      if (this.field_175132_d.func_196958_f()) {
         this.func_70106_y();
      } else {
         this.field_70169_q = this.field_70165_t;
         this.field_70167_r = this.field_70163_u;
         this.field_70166_s = this.field_70161_v;
         Block var1 = this.field_175132_d.func_177230_c();
         BlockPos var2;
         if (this.field_145812_b++ == 0) {
            var2 = new BlockPos(this);
            if (this.field_70170_p.func_180495_p(var2).func_177230_c() == var1) {
               this.field_70170_p.func_175698_g(var2);
            } else if (!this.field_70170_p.field_72995_K) {
               this.func_70106_y();
               return;
            }
         }

         if (!this.func_189652_ae()) {
            this.field_70181_x -= 0.03999999910593033D;
         }

         this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
         if (!this.field_70170_p.field_72995_K) {
            var2 = new BlockPos(this);
            boolean var3 = this.field_175132_d.func_177230_c() instanceof BlockConcretePowder;
            boolean var4 = var3 && this.field_70170_p.func_204610_c(var2).func_206884_a(FluidTags.field_206959_a);
            double var5 = this.field_70159_w * this.field_70159_w + this.field_70181_x * this.field_70181_x + this.field_70179_y * this.field_70179_y;
            if (var3 && var5 > 1.0D) {
               RayTraceResult var7 = this.field_70170_p.func_200260_a(new Vec3d(this.field_70169_q, this.field_70167_r, this.field_70166_s), new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v), RayTraceFluidMode.SOURCE_ONLY);
               if (var7 != null && this.field_70170_p.func_204610_c(var7.func_178782_a()).func_206884_a(FluidTags.field_206959_a)) {
                  var2 = var7.func_178782_a();
                  var4 = true;
               }
            }

            if (!this.field_70122_E && !var4) {
               if (this.field_145812_b > 100 && !this.field_70170_p.field_72995_K && (var2.func_177956_o() < 1 || var2.func_177956_o() > 256) || this.field_145812_b > 600) {
                  if (this.field_145813_c && this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
                     this.func_199703_a(var1);
                  }

                  this.func_70106_y();
               }
            } else {
               IBlockState var13 = this.field_70170_p.func_180495_p(var2);
               if (!var4 && BlockFalling.func_185759_i(this.field_70170_p.func_180495_p(new BlockPos(this.field_70165_t, this.field_70163_u - 0.009999999776482582D, this.field_70161_v)))) {
                  this.field_70122_E = false;
                  return;
               }

               this.field_70159_w *= 0.699999988079071D;
               this.field_70179_y *= 0.699999988079071D;
               this.field_70181_x *= -0.5D;
               if (var13.func_177230_c() != Blocks.field_196603_bb) {
                  this.func_70106_y();
                  if (!this.field_145808_f) {
                     if (var13.func_185904_a().func_76222_j() && (var4 || !BlockFalling.func_185759_i(this.field_70170_p.func_180495_p(var2.func_177977_b()))) && this.field_70170_p.func_180501_a(var2, this.field_175132_d, 3)) {
                        if (var1 instanceof BlockFalling) {
                           ((BlockFalling)var1).func_176502_a_(this.field_70170_p, var2, this.field_175132_d, var13);
                        }

                        if (this.field_145810_d != null && var1 instanceof ITileEntityProvider) {
                           TileEntity var8 = this.field_70170_p.func_175625_s(var2);
                           if (var8 != null) {
                              NBTTagCompound var9 = var8.func_189515_b(new NBTTagCompound());
                              Iterator var10 = this.field_145810_d.func_150296_c().iterator();

                              while(var10.hasNext()) {
                                 String var11 = (String)var10.next();
                                 INBTBase var12 = this.field_145810_d.func_74781_a(var11);
                                 if (!"x".equals(var11) && !"y".equals(var11) && !"z".equals(var11)) {
                                    var9.func_74782_a(var11, var12.func_74737_b());
                                 }
                              }

                              var8.func_145839_a(var9);
                              var8.func_70296_d();
                           }
                        }
                     } else if (this.field_145813_c && this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
                        this.func_199703_a(var1);
                     }
                  } else if (var1 instanceof BlockFalling) {
                     ((BlockFalling)var1).func_190974_b(this.field_70170_p, var2);
                  }
               }
            }
         }

         this.field_70159_w *= 0.9800000190734863D;
         this.field_70181_x *= 0.9800000190734863D;
         this.field_70179_y *= 0.9800000190734863D;
      }
   }

   public void func_180430_e(float var1, float var2) {
      if (this.field_145809_g) {
         int var3 = MathHelper.func_76123_f(var1 - 1.0F);
         if (var3 > 0) {
            ArrayList var4 = Lists.newArrayList(this.field_70170_p.func_72839_b(this, this.func_174813_aQ()));
            boolean var5 = this.field_175132_d.func_203425_a(BlockTags.field_200572_k);
            DamageSource var6 = var5 ? DamageSource.field_82728_o : DamageSource.field_82729_p;
            Iterator var7 = var4.iterator();

            while(var7.hasNext()) {
               Entity var8 = (Entity)var7.next();
               var8.func_70097_a(var6, (float)Math.min(MathHelper.func_76141_d((float)var3 * this.field_145816_i), this.field_145815_h));
            }

            if (var5 && (double)this.field_70146_Z.nextFloat() < 0.05000000074505806D + (double)var3 * 0.05D) {
               IBlockState var9 = BlockAnvil.func_196433_f(this.field_175132_d);
               if (var9 == null) {
                  this.field_145808_f = true;
               } else {
                  this.field_175132_d = var9;
               }
            }
         }
      }

   }

   protected void func_70014_b(NBTTagCompound var1) {
      var1.func_74782_a("BlockState", NBTUtil.func_190009_a(this.field_175132_d));
      var1.func_74768_a("Time", this.field_145812_b);
      var1.func_74757_a("DropItem", this.field_145813_c);
      var1.func_74757_a("HurtEntities", this.field_145809_g);
      var1.func_74776_a("FallHurtAmount", this.field_145816_i);
      var1.func_74768_a("FallHurtMax", this.field_145815_h);
      if (this.field_145810_d != null) {
         var1.func_74782_a("TileEntityData", this.field_145810_d);
      }

   }

   protected void func_70037_a(NBTTagCompound var1) {
      this.field_175132_d = NBTUtil.func_190008_d(var1.func_74775_l("BlockState"));
      this.field_145812_b = var1.func_74762_e("Time");
      if (var1.func_150297_b("HurtEntities", 99)) {
         this.field_145809_g = var1.func_74767_n("HurtEntities");
         this.field_145816_i = var1.func_74760_g("FallHurtAmount");
         this.field_145815_h = var1.func_74762_e("FallHurtMax");
      } else if (this.field_175132_d.func_203425_a(BlockTags.field_200572_k)) {
         this.field_145809_g = true;
      }

      if (var1.func_150297_b("DropItem", 99)) {
         this.field_145813_c = var1.func_74767_n("DropItem");
      }

      if (var1.func_150297_b("TileEntityData", 10)) {
         this.field_145810_d = var1.func_74775_l("TileEntityData");
      }

      if (this.field_175132_d.func_196958_f()) {
         this.field_175132_d = Blocks.field_150354_m.func_176223_P();
      }

   }

   public World func_145807_e() {
      return this.field_70170_p;
   }

   public void func_145806_a(boolean var1) {
      this.field_145809_g = var1;
   }

   public boolean func_90999_ad() {
      return false;
   }

   public void func_85029_a(CrashReportCategory var1) {
      super.func_85029_a(var1);
      var1.func_71507_a("Immitating BlockState", this.field_175132_d.toString());
   }

   public IBlockState func_195054_l() {
      return this.field_175132_d;
   }

   public boolean func_184213_bq() {
      return true;
   }

   static {
      field_184532_d = EntityDataManager.func_187226_a(EntityFallingBlock.class, DataSerializers.field_187200_j);
   }
}
