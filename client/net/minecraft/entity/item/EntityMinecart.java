package net.minecraft.entity.item;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.INameable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;

public abstract class EntityMinecart extends Entity implements INameable {
   private static final DataParameter<Integer> field_184265_a;
   private static final DataParameter<Integer> field_184266_b;
   private static final DataParameter<Float> field_184267_c;
   private static final DataParameter<Integer> field_184268_d;
   private static final DataParameter<Integer> field_184269_e;
   private static final DataParameter<Boolean> field_184270_f;
   private boolean field_70499_f;
   private static final int[][][] field_70500_g;
   private int field_70510_h;
   private double field_70511_i;
   private double field_70509_j;
   private double field_70514_an;
   private double field_70512_ao;
   private double field_70513_ap;
   private double field_70508_aq;
   private double field_70507_ar;
   private double field_70506_as;

   protected EntityMinecart(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_70156_m = true;
      this.func_70105_a(0.98F, 0.7F);
   }

   protected EntityMinecart(EntityType<?> var1, World var2, double var3, double var5, double var7) {
      this(var1, var2);
      this.func_70107_b(var3, var5, var7);
      this.field_70159_w = 0.0D;
      this.field_70181_x = 0.0D;
      this.field_70179_y = 0.0D;
      this.field_70169_q = var3;
      this.field_70167_r = var5;
      this.field_70166_s = var7;
   }

   public static EntityMinecart func_184263_a(World var0, double var1, double var3, double var5, EntityMinecart.Type var7) {
      switch(var7) {
      case CHEST:
         return new EntityMinecartChest(var0, var1, var3, var5);
      case FURNACE:
         return new EntityMinecartFurnace(var0, var1, var3, var5);
      case TNT:
         return new EntityMinecartTNT(var0, var1, var3, var5);
      case SPAWNER:
         return new EntityMinecartMobSpawner(var0, var1, var3, var5);
      case HOPPER:
         return new EntityMinecartHopper(var0, var1, var3, var5);
      case COMMAND_BLOCK:
         return new EntityMinecartCommandBlock(var0, var1, var3, var5);
      default:
         return new EntityMinecartEmpty(var0, var1, var3, var5);
      }
   }

   protected boolean func_70041_e_() {
      return false;
   }

   protected void func_70088_a() {
      this.field_70180_af.func_187214_a(field_184265_a, 0);
      this.field_70180_af.func_187214_a(field_184266_b, 1);
      this.field_70180_af.func_187214_a(field_184267_c, 0.0F);
      this.field_70180_af.func_187214_a(field_184268_d, Block.func_196246_j(Blocks.field_150350_a.func_176223_P()));
      this.field_70180_af.func_187214_a(field_184269_e, 6);
      this.field_70180_af.func_187214_a(field_184270_f, false);
   }

   @Nullable
   public AxisAlignedBB func_70114_g(Entity var1) {
      return var1.func_70104_M() ? var1.func_174813_aQ() : null;
   }

   public boolean func_70104_M() {
      return true;
   }

   public double func_70042_X() {
      return 0.0D;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
         if (this.func_180431_b(var1)) {
            return false;
         } else {
            this.func_70494_i(-this.func_70493_k());
            this.func_70497_h(10);
            this.func_70018_K();
            this.func_70492_c(this.func_70491_i() + var2 * 10.0F);
            boolean var3 = var1.func_76346_g() instanceof EntityPlayer && ((EntityPlayer)var1.func_76346_g()).field_71075_bZ.field_75098_d;
            if (var3 || this.func_70491_i() > 40.0F) {
               this.func_184226_ay();
               if (var3 && !this.func_145818_k_()) {
                  this.func_70106_y();
               } else {
                  this.func_94095_a(var1);
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public void func_94095_a(DamageSource var1) {
      this.func_70106_y();
      if (this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
         ItemStack var2 = new ItemStack(Items.field_151143_au);
         if (this.func_145818_k_()) {
            var2.func_200302_a(this.func_200201_e());
         }

         this.func_199701_a_(var2);
      }

   }

   public void func_70057_ab() {
      this.func_70494_i(-this.func_70493_k());
      this.func_70497_h(10);
      this.func_70492_c(this.func_70491_i() + this.func_70491_i() * 10.0F);
   }

   public boolean func_70067_L() {
      return !this.field_70128_L;
   }

   public EnumFacing func_184172_bi() {
      return this.field_70499_f ? this.func_174811_aO().func_176734_d().func_176746_e() : this.func_174811_aO().func_176746_e();
   }

   public void func_70071_h_() {
      if (this.func_70496_j() > 0) {
         this.func_70497_h(this.func_70496_j() - 1);
      }

      if (this.func_70491_i() > 0.0F) {
         this.func_70492_c(this.func_70491_i() - 1.0F);
      }

      if (this.field_70163_u < -64.0D) {
         this.func_70076_C();
      }

      int var2;
      if (!this.field_70170_p.field_72995_K && this.field_70170_p instanceof WorldServer) {
         this.field_70170_p.field_72984_F.func_76320_a("portal");
         MinecraftServer var1 = this.field_70170_p.func_73046_m();
         var2 = this.func_82145_z();
         if (this.field_71087_bX) {
            if (var1.func_71255_r()) {
               if (!this.func_184218_aH() && this.field_82153_h++ >= var2) {
                  this.field_82153_h = var2;
                  this.field_71088_bW = this.func_82147_ab();
                  DimensionType var3;
                  if (this.field_70170_p.field_73011_w.func_186058_p() == DimensionType.NETHER) {
                     var3 = DimensionType.OVERWORLD;
                  } else {
                     var3 = DimensionType.NETHER;
                  }

                  this.func_212321_a(var3);
               }

               this.field_71087_bX = false;
            }
         } else {
            if (this.field_82153_h > 0) {
               this.field_82153_h -= 4;
            }

            if (this.field_82153_h < 0) {
               this.field_82153_h = 0;
            }
         }

         if (this.field_71088_bW > 0) {
            --this.field_71088_bW;
         }

         this.field_70170_p.field_72984_F.func_76319_b();
      }

      if (this.field_70170_p.field_72995_K) {
         if (this.field_70510_h > 0) {
            double var16 = this.field_70165_t + (this.field_70511_i - this.field_70165_t) / (double)this.field_70510_h;
            double var18 = this.field_70163_u + (this.field_70509_j - this.field_70163_u) / (double)this.field_70510_h;
            double var19 = this.field_70161_v + (this.field_70514_an - this.field_70161_v) / (double)this.field_70510_h;
            double var7 = MathHelper.func_76138_g(this.field_70512_ao - (double)this.field_70177_z);
            this.field_70177_z = (float)((double)this.field_70177_z + var7 / (double)this.field_70510_h);
            this.field_70125_A = (float)((double)this.field_70125_A + (this.field_70513_ap - (double)this.field_70125_A) / (double)this.field_70510_h);
            --this.field_70510_h;
            this.func_70107_b(var16, var18, var19);
            this.func_70101_b(this.field_70177_z, this.field_70125_A);
         } else {
            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            this.func_70101_b(this.field_70177_z, this.field_70125_A);
         }

      } else {
         this.field_70169_q = this.field_70165_t;
         this.field_70167_r = this.field_70163_u;
         this.field_70166_s = this.field_70161_v;
         if (!this.func_189652_ae()) {
            this.field_70181_x -= 0.03999999910593033D;
         }

         int var15 = MathHelper.func_76128_c(this.field_70165_t);
         var2 = MathHelper.func_76128_c(this.field_70163_u);
         int var17 = MathHelper.func_76128_c(this.field_70161_v);
         if (this.field_70170_p.func_180495_p(new BlockPos(var15, var2 - 1, var17)).func_203425_a(BlockTags.field_203437_y)) {
            --var2;
         }

         BlockPos var4 = new BlockPos(var15, var2, var17);
         IBlockState var5 = this.field_70170_p.func_180495_p(var4);
         if (var5.func_203425_a(BlockTags.field_203437_y)) {
            this.func_180460_a(var4, var5);
            if (var5.func_177230_c() == Blocks.field_150408_cc) {
               this.func_96095_a(var15, var2, var17, (Boolean)var5.func_177229_b(BlockRailPowered.field_176569_M));
            }
         } else {
            this.func_180459_n();
         }

         this.func_145775_I();
         this.field_70125_A = 0.0F;
         double var6 = this.field_70169_q - this.field_70165_t;
         double var8 = this.field_70166_s - this.field_70161_v;
         if (var6 * var6 + var8 * var8 > 0.001D) {
            this.field_70177_z = (float)(MathHelper.func_181159_b(var8, var6) * 180.0D / 3.141592653589793D);
            if (this.field_70499_f) {
               this.field_70177_z += 180.0F;
            }
         }

         double var10 = (double)MathHelper.func_76142_g(this.field_70177_z - this.field_70126_B);
         if (var10 < -170.0D || var10 >= 170.0D) {
            this.field_70177_z += 180.0F;
            this.field_70499_f = !this.field_70499_f;
         }

         this.func_70101_b(this.field_70177_z, this.field_70125_A);
         if (this.func_184264_v() == EntityMinecart.Type.RIDEABLE && this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y > 0.01D) {
            List var20 = this.field_70170_p.func_175674_a(this, this.func_174813_aQ().func_72314_b(0.20000000298023224D, 0.0D, 0.20000000298023224D), EntitySelectors.func_200823_a(this));
            if (!var20.isEmpty()) {
               for(int var21 = 0; var21 < var20.size(); ++var21) {
                  Entity var14 = (Entity)var20.get(var21);
                  if (!(var14 instanceof EntityPlayer) && !(var14 instanceof EntityIronGolem) && !(var14 instanceof EntityMinecart) && !this.func_184207_aI() && !var14.func_184218_aH()) {
                     var14.func_184220_m(this);
                  } else {
                     var14.func_70108_f(this);
                  }
               }
            }
         } else {
            Iterator var12 = this.field_70170_p.func_72839_b(this, this.func_174813_aQ().func_72314_b(0.20000000298023224D, 0.0D, 0.20000000298023224D)).iterator();

            while(var12.hasNext()) {
               Entity var13 = (Entity)var12.next();
               if (!this.func_184196_w(var13) && var13.func_70104_M() && var13 instanceof EntityMinecart) {
                  var13.func_70108_f(this);
               }
            }
         }

         this.func_70072_I();
      }
   }

   protected double func_174898_m() {
      return 0.4D;
   }

   public void func_96095_a(int var1, int var2, int var3, boolean var4) {
   }

   protected void func_180459_n() {
      double var1 = this.func_174898_m();
      this.field_70159_w = MathHelper.func_151237_a(this.field_70159_w, -var1, var1);
      this.field_70179_y = MathHelper.func_151237_a(this.field_70179_y, -var1, var1);
      if (this.field_70122_E) {
         this.field_70159_w *= 0.5D;
         this.field_70181_x *= 0.5D;
         this.field_70179_y *= 0.5D;
      }

      this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
      if (!this.field_70122_E) {
         this.field_70159_w *= 0.949999988079071D;
         this.field_70181_x *= 0.949999988079071D;
         this.field_70179_y *= 0.949999988079071D;
      }

   }

   protected void func_180460_a(BlockPos var1, IBlockState var2) {
      this.field_70143_R = 0.0F;
      Vec3d var3 = this.func_70489_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.field_70163_u = (double)var1.func_177956_o();
      boolean var4 = false;
      boolean var5 = false;
      BlockRailBase var6 = (BlockRailBase)var2.func_177230_c();
      if (var6 == Blocks.field_196552_aC) {
         var4 = (Boolean)var2.func_177229_b(BlockRailPowered.field_176569_M);
         var5 = !var4;
      }

      double var7 = 0.0078125D;
      RailShape var9 = (RailShape)var2.func_177229_b(var6.func_176560_l());
      switch(var9) {
      case ASCENDING_EAST:
         this.field_70159_w -= 0.0078125D;
         ++this.field_70163_u;
         break;
      case ASCENDING_WEST:
         this.field_70159_w += 0.0078125D;
         ++this.field_70163_u;
         break;
      case ASCENDING_NORTH:
         this.field_70179_y += 0.0078125D;
         ++this.field_70163_u;
         break;
      case ASCENDING_SOUTH:
         this.field_70179_y -= 0.0078125D;
         ++this.field_70163_u;
      }

      int[][] var10 = field_70500_g[var9.func_208091_a()];
      double var11 = (double)(var10[1][0] - var10[0][0]);
      double var13 = (double)(var10[1][2] - var10[0][2]);
      double var15 = Math.sqrt(var11 * var11 + var13 * var13);
      double var17 = this.field_70159_w * var11 + this.field_70179_y * var13;
      if (var17 < 0.0D) {
         var11 = -var11;
         var13 = -var13;
      }

      double var19 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      if (var19 > 2.0D) {
         var19 = 2.0D;
      }

      this.field_70159_w = var19 * var11 / var15;
      this.field_70179_y = var19 * var13 / var15;
      Entity var21 = this.func_184188_bt().isEmpty() ? null : (Entity)this.func_184188_bt().get(0);
      double var22;
      double var24;
      double var26;
      double var28;
      if (var21 instanceof EntityPlayer) {
         var22 = (double)((EntityPlayer)var21).field_191988_bg;
         if (var22 > 0.0D) {
            var24 = -Math.sin((double)(var21.field_70177_z * 0.017453292F));
            var26 = Math.cos((double)(var21.field_70177_z * 0.017453292F));
            var28 = this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y;
            if (var28 < 0.01D) {
               this.field_70159_w += var24 * 0.1D;
               this.field_70179_y += var26 * 0.1D;
               var5 = false;
            }
         }
      }

      if (var5) {
         var22 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         if (var22 < 0.03D) {
            this.field_70159_w *= 0.0D;
            this.field_70181_x *= 0.0D;
            this.field_70179_y *= 0.0D;
         } else {
            this.field_70159_w *= 0.5D;
            this.field_70181_x *= 0.0D;
            this.field_70179_y *= 0.5D;
         }
      }

      var22 = (double)var1.func_177958_n() + 0.5D + (double)var10[0][0] * 0.5D;
      var24 = (double)var1.func_177952_p() + 0.5D + (double)var10[0][2] * 0.5D;
      var26 = (double)var1.func_177958_n() + 0.5D + (double)var10[1][0] * 0.5D;
      var28 = (double)var1.func_177952_p() + 0.5D + (double)var10[1][2] * 0.5D;
      var11 = var26 - var22;
      var13 = var28 - var24;
      double var30;
      double var32;
      double var34;
      if (var11 == 0.0D) {
         this.field_70165_t = (double)var1.func_177958_n() + 0.5D;
         var30 = this.field_70161_v - (double)var1.func_177952_p();
      } else if (var13 == 0.0D) {
         this.field_70161_v = (double)var1.func_177952_p() + 0.5D;
         var30 = this.field_70165_t - (double)var1.func_177958_n();
      } else {
         var32 = this.field_70165_t - var22;
         var34 = this.field_70161_v - var24;
         var30 = (var32 * var11 + var34 * var13) * 2.0D;
      }

      this.field_70165_t = var22 + var11 * var30;
      this.field_70161_v = var24 + var13 * var30;
      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      var32 = this.field_70159_w;
      var34 = this.field_70179_y;
      if (this.func_184207_aI()) {
         var32 *= 0.75D;
         var34 *= 0.75D;
      }

      double var36 = this.func_174898_m();
      var32 = MathHelper.func_151237_a(var32, -var36, var36);
      var34 = MathHelper.func_151237_a(var34, -var36, var36);
      this.func_70091_d(MoverType.SELF, var32, 0.0D, var34);
      if (var10[0][1] != 0 && MathHelper.func_76128_c(this.field_70165_t) - var1.func_177958_n() == var10[0][0] && MathHelper.func_76128_c(this.field_70161_v) - var1.func_177952_p() == var10[0][2]) {
         this.func_70107_b(this.field_70165_t, this.field_70163_u + (double)var10[0][1], this.field_70161_v);
      } else if (var10[1][1] != 0 && MathHelper.func_76128_c(this.field_70165_t) - var1.func_177958_n() == var10[1][0] && MathHelper.func_76128_c(this.field_70161_v) - var1.func_177952_p() == var10[1][2]) {
         this.func_70107_b(this.field_70165_t, this.field_70163_u + (double)var10[1][1], this.field_70161_v);
      }

      this.func_94101_h();
      Vec3d var38 = this.func_70489_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      if (var38 != null && var3 != null) {
         double var39 = (var3.field_72448_b - var38.field_72448_b) * 0.05D;
         var19 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         if (var19 > 0.0D) {
            this.field_70159_w = this.field_70159_w / var19 * (var19 + var39);
            this.field_70179_y = this.field_70179_y / var19 * (var19 + var39);
         }

         this.func_70107_b(this.field_70165_t, var38.field_72448_b, this.field_70161_v);
      }

      int var45 = MathHelper.func_76128_c(this.field_70165_t);
      int var40 = MathHelper.func_76128_c(this.field_70161_v);
      if (var45 != var1.func_177958_n() || var40 != var1.func_177952_p()) {
         var19 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         this.field_70159_w = var19 * (double)(var45 - var1.func_177958_n());
         this.field_70179_y = var19 * (double)(var40 - var1.func_177952_p());
      }

      if (var4) {
         double var41 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         if (var41 > 0.01D) {
            double var43 = 0.06D;
            this.field_70159_w += this.field_70159_w / var41 * 0.06D;
            this.field_70179_y += this.field_70179_y / var41 * 0.06D;
         } else if (var9 == RailShape.EAST_WEST) {
            if (this.field_70170_p.func_180495_p(var1.func_177976_e()).func_185915_l()) {
               this.field_70159_w = 0.02D;
            } else if (this.field_70170_p.func_180495_p(var1.func_177974_f()).func_185915_l()) {
               this.field_70159_w = -0.02D;
            }
         } else if (var9 == RailShape.NORTH_SOUTH) {
            if (this.field_70170_p.func_180495_p(var1.func_177978_c()).func_185915_l()) {
               this.field_70179_y = 0.02D;
            } else if (this.field_70170_p.func_180495_p(var1.func_177968_d()).func_185915_l()) {
               this.field_70179_y = -0.02D;
            }
         }
      }

   }

   protected void func_94101_h() {
      if (this.func_184207_aI()) {
         this.field_70159_w *= 0.996999979019165D;
         this.field_70181_x *= 0.0D;
         this.field_70179_y *= 0.996999979019165D;
      } else {
         this.field_70159_w *= 0.9599999785423279D;
         this.field_70181_x *= 0.0D;
         this.field_70179_y *= 0.9599999785423279D;
      }

   }

   public void func_70107_b(double var1, double var3, double var5) {
      this.field_70165_t = var1;
      this.field_70163_u = var3;
      this.field_70161_v = var5;
      float var7 = this.field_70130_N / 2.0F;
      float var8 = this.field_70131_O;
      this.func_174826_a(new AxisAlignedBB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7));
   }

   @Nullable
   public Vec3d func_70495_a(double var1, double var3, double var5, double var7) {
      int var9 = MathHelper.func_76128_c(var1);
      int var10 = MathHelper.func_76128_c(var3);
      int var11 = MathHelper.func_76128_c(var5);
      if (this.field_70170_p.func_180495_p(new BlockPos(var9, var10 - 1, var11)).func_203425_a(BlockTags.field_203437_y)) {
         --var10;
      }

      IBlockState var12 = this.field_70170_p.func_180495_p(new BlockPos(var9, var10, var11));
      if (var12.func_203425_a(BlockTags.field_203437_y)) {
         RailShape var13 = (RailShape)var12.func_177229_b(((BlockRailBase)var12.func_177230_c()).func_176560_l());
         var3 = (double)var10;
         if (var13.func_208092_c()) {
            var3 = (double)(var10 + 1);
         }

         int[][] var14 = field_70500_g[var13.func_208091_a()];
         double var15 = (double)(var14[1][0] - var14[0][0]);
         double var17 = (double)(var14[1][2] - var14[0][2]);
         double var19 = Math.sqrt(var15 * var15 + var17 * var17);
         var15 /= var19;
         var17 /= var19;
         var1 += var15 * var7;
         var5 += var17 * var7;
         if (var14[0][1] != 0 && MathHelper.func_76128_c(var1) - var9 == var14[0][0] && MathHelper.func_76128_c(var5) - var11 == var14[0][2]) {
            var3 += (double)var14[0][1];
         } else if (var14[1][1] != 0 && MathHelper.func_76128_c(var1) - var9 == var14[1][0] && MathHelper.func_76128_c(var5) - var11 == var14[1][2]) {
            var3 += (double)var14[1][1];
         }

         return this.func_70489_a(var1, var3, var5);
      } else {
         return null;
      }
   }

   @Nullable
   public Vec3d func_70489_a(double var1, double var3, double var5) {
      int var7 = MathHelper.func_76128_c(var1);
      int var8 = MathHelper.func_76128_c(var3);
      int var9 = MathHelper.func_76128_c(var5);
      if (this.field_70170_p.func_180495_p(new BlockPos(var7, var8 - 1, var9)).func_203425_a(BlockTags.field_203437_y)) {
         --var8;
      }

      IBlockState var10 = this.field_70170_p.func_180495_p(new BlockPos(var7, var8, var9));
      if (var10.func_203425_a(BlockTags.field_203437_y)) {
         RailShape var11 = (RailShape)var10.func_177229_b(((BlockRailBase)var10.func_177230_c()).func_176560_l());
         int[][] var12 = field_70500_g[var11.func_208091_a()];
         double var13 = (double)var7 + 0.5D + (double)var12[0][0] * 0.5D;
         double var15 = (double)var8 + 0.0625D + (double)var12[0][1] * 0.5D;
         double var17 = (double)var9 + 0.5D + (double)var12[0][2] * 0.5D;
         double var19 = (double)var7 + 0.5D + (double)var12[1][0] * 0.5D;
         double var21 = (double)var8 + 0.0625D + (double)var12[1][1] * 0.5D;
         double var23 = (double)var9 + 0.5D + (double)var12[1][2] * 0.5D;
         double var25 = var19 - var13;
         double var27 = (var21 - var15) * 2.0D;
         double var29 = var23 - var17;
         double var31;
         if (var25 == 0.0D) {
            var31 = var5 - (double)var9;
         } else if (var29 == 0.0D) {
            var31 = var1 - (double)var7;
         } else {
            double var33 = var1 - var13;
            double var35 = var5 - var17;
            var31 = (var33 * var25 + var35 * var29) * 2.0D;
         }

         var1 = var13 + var25 * var31;
         var3 = var15 + var27 * var31;
         var5 = var17 + var29 * var31;
         if (var27 < 0.0D) {
            ++var3;
         }

         if (var27 > 0.0D) {
            var3 += 0.5D;
         }

         return new Vec3d(var1, var3, var5);
      } else {
         return null;
      }
   }

   public AxisAlignedBB func_184177_bl() {
      AxisAlignedBB var1 = this.func_174813_aQ();
      return this.func_94100_s() ? var1.func_186662_g((double)Math.abs(this.func_94099_q()) / 16.0D) : var1;
   }

   protected void func_70037_a(NBTTagCompound var1) {
      if (var1.func_74767_n("CustomDisplayTile")) {
         this.func_174899_a(NBTUtil.func_190008_d(var1.func_74775_l("DisplayState")));
         this.func_94086_l(var1.func_74762_e("DisplayOffset"));
      }

   }

   protected void func_70014_b(NBTTagCompound var1) {
      if (this.func_94100_s()) {
         var1.func_74757_a("CustomDisplayTile", true);
         var1.func_74782_a("DisplayState", NBTUtil.func_190009_a(this.func_174897_t()));
         var1.func_74768_a("DisplayOffset", this.func_94099_q());
      }

   }

   public void func_70108_f(Entity var1) {
      if (!this.field_70170_p.field_72995_K) {
         if (!var1.field_70145_X && !this.field_70145_X) {
            if (!this.func_184196_w(var1)) {
               double var2 = var1.field_70165_t - this.field_70165_t;
               double var4 = var1.field_70161_v - this.field_70161_v;
               double var6 = var2 * var2 + var4 * var4;
               if (var6 >= 9.999999747378752E-5D) {
                  var6 = (double)MathHelper.func_76133_a(var6);
                  var2 /= var6;
                  var4 /= var6;
                  double var8 = 1.0D / var6;
                  if (var8 > 1.0D) {
                     var8 = 1.0D;
                  }

                  var2 *= var8;
                  var4 *= var8;
                  var2 *= 0.10000000149011612D;
                  var4 *= 0.10000000149011612D;
                  var2 *= (double)(1.0F - this.field_70144_Y);
                  var4 *= (double)(1.0F - this.field_70144_Y);
                  var2 *= 0.5D;
                  var4 *= 0.5D;
                  if (var1 instanceof EntityMinecart) {
                     double var10 = var1.field_70165_t - this.field_70165_t;
                     double var12 = var1.field_70161_v - this.field_70161_v;
                     Vec3d var14 = (new Vec3d(var10, 0.0D, var12)).func_72432_b();
                     Vec3d var15 = (new Vec3d((double)MathHelper.func_76134_b(this.field_70177_z * 0.017453292F), 0.0D, (double)MathHelper.func_76126_a(this.field_70177_z * 0.017453292F))).func_72432_b();
                     double var16 = Math.abs(var14.func_72430_b(var15));
                     if (var16 < 0.800000011920929D) {
                        return;
                     }

                     double var18 = var1.field_70159_w + this.field_70159_w;
                     double var20 = var1.field_70179_y + this.field_70179_y;
                     if (((EntityMinecart)var1).func_184264_v() == EntityMinecart.Type.FURNACE && this.func_184264_v() != EntityMinecart.Type.FURNACE) {
                        this.field_70159_w *= 0.20000000298023224D;
                        this.field_70179_y *= 0.20000000298023224D;
                        this.func_70024_g(var1.field_70159_w - var2, 0.0D, var1.field_70179_y - var4);
                        var1.field_70159_w *= 0.949999988079071D;
                        var1.field_70179_y *= 0.949999988079071D;
                     } else if (((EntityMinecart)var1).func_184264_v() != EntityMinecart.Type.FURNACE && this.func_184264_v() == EntityMinecart.Type.FURNACE) {
                        var1.field_70159_w *= 0.20000000298023224D;
                        var1.field_70179_y *= 0.20000000298023224D;
                        var1.func_70024_g(this.field_70159_w + var2, 0.0D, this.field_70179_y + var4);
                        this.field_70159_w *= 0.949999988079071D;
                        this.field_70179_y *= 0.949999988079071D;
                     } else {
                        var18 /= 2.0D;
                        var20 /= 2.0D;
                        this.field_70159_w *= 0.20000000298023224D;
                        this.field_70179_y *= 0.20000000298023224D;
                        this.func_70024_g(var18 - var2, 0.0D, var20 - var4);
                        var1.field_70159_w *= 0.20000000298023224D;
                        var1.field_70179_y *= 0.20000000298023224D;
                        var1.func_70024_g(var18 + var2, 0.0D, var20 + var4);
                     }
                  } else {
                     this.func_70024_g(-var2, 0.0D, -var4);
                     var1.func_70024_g(var2 / 4.0D, 0.0D, var4 / 4.0D);
                  }
               }

            }
         }
      }
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.field_70511_i = var1;
      this.field_70509_j = var3;
      this.field_70514_an = var5;
      this.field_70512_ao = (double)var7;
      this.field_70513_ap = (double)var8;
      this.field_70510_h = var9 + 2;
      this.field_70159_w = this.field_70508_aq;
      this.field_70181_x = this.field_70507_ar;
      this.field_70179_y = this.field_70506_as;
   }

   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
      this.field_70508_aq = this.field_70159_w;
      this.field_70507_ar = this.field_70181_x;
      this.field_70506_as = this.field_70179_y;
   }

   public void func_70492_c(float var1) {
      this.field_70180_af.func_187227_b(field_184267_c, var1);
   }

   public float func_70491_i() {
      return (Float)this.field_70180_af.func_187225_a(field_184267_c);
   }

   public void func_70497_h(int var1) {
      this.field_70180_af.func_187227_b(field_184265_a, var1);
   }

   public int func_70496_j() {
      return (Integer)this.field_70180_af.func_187225_a(field_184265_a);
   }

   public void func_70494_i(int var1) {
      this.field_70180_af.func_187227_b(field_184266_b, var1);
   }

   public int func_70493_k() {
      return (Integer)this.field_70180_af.func_187225_a(field_184266_b);
   }

   public abstract EntityMinecart.Type func_184264_v();

   public IBlockState func_174897_t() {
      return !this.func_94100_s() ? this.func_180457_u() : Block.func_196257_b((Integer)this.func_184212_Q().func_187225_a(field_184268_d));
   }

   public IBlockState func_180457_u() {
      return Blocks.field_150350_a.func_176223_P();
   }

   public int func_94099_q() {
      return !this.func_94100_s() ? this.func_94085_r() : (Integer)this.func_184212_Q().func_187225_a(field_184269_e);
   }

   public int func_94085_r() {
      return 6;
   }

   public void func_174899_a(IBlockState var1) {
      this.func_184212_Q().func_187227_b(field_184268_d, Block.func_196246_j(var1));
      this.func_94096_e(true);
   }

   public void func_94086_l(int var1) {
      this.func_184212_Q().func_187227_b(field_184269_e, var1);
      this.func_94096_e(true);
   }

   public boolean func_94100_s() {
      return (Boolean)this.func_184212_Q().func_187225_a(field_184270_f);
   }

   public void func_94096_e(boolean var1) {
      this.func_184212_Q().func_187227_b(field_184270_f, var1);
   }

   static {
      field_184265_a = EntityDataManager.func_187226_a(EntityMinecart.class, DataSerializers.field_187192_b);
      field_184266_b = EntityDataManager.func_187226_a(EntityMinecart.class, DataSerializers.field_187192_b);
      field_184267_c = EntityDataManager.func_187226_a(EntityMinecart.class, DataSerializers.field_187193_c);
      field_184268_d = EntityDataManager.func_187226_a(EntityMinecart.class, DataSerializers.field_187192_b);
      field_184269_e = EntityDataManager.func_187226_a(EntityMinecart.class, DataSerializers.field_187192_b);
      field_184270_f = EntityDataManager.func_187226_a(EntityMinecart.class, DataSerializers.field_187198_h);
      field_70500_g = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
   }

   public static enum Type {
      RIDEABLE(0),
      CHEST(1),
      FURNACE(2),
      TNT(3),
      SPAWNER(4),
      HOPPER(5),
      COMMAND_BLOCK(6);

      private static final EntityMinecart.Type[] field_184965_h = (EntityMinecart.Type[])Arrays.stream(values()).sorted(Comparator.comparingInt(EntityMinecart.Type::func_184956_a)).toArray((var0) -> {
         return new EntityMinecart.Type[var0];
      });
      private final int field_184966_i;

      private Type(int var3) {
         this.field_184966_i = var3;
      }

      public int func_184956_a() {
         return this.field_184966_i;
      }

      public static EntityMinecart.Type func_184955_a(int var0) {
         return var0 >= 0 && var0 < field_184965_h.length ? field_184965_h[var0] : RIDEABLE;
      }
   }
}
