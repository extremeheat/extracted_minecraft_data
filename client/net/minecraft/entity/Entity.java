package net.minecraft.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockWall;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.INameable;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements INameable, ICommandSource {
   protected static final Logger field_184243_a = LogManager.getLogger();
   private static final List<ItemStack> field_190535_b = Collections.emptyList();
   private static final AxisAlignedBB field_174836_a = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static double field_70155_l = 1.0D;
   private static int field_70152_a;
   private final EntityType<?> field_200606_g;
   private int field_145783_c;
   public boolean field_70156_m;
   private final List<Entity> field_184244_h;
   protected int field_184245_j;
   private Entity field_184239_as;
   public boolean field_98038_p;
   public World field_70170_p;
   public double field_70169_q;
   public double field_70167_r;
   public double field_70166_s;
   public double field_70165_t;
   public double field_70163_u;
   public double field_70161_v;
   public double field_70159_w;
   public double field_70181_x;
   public double field_70179_y;
   public float field_70177_z;
   public float field_70125_A;
   public float field_70126_B;
   public float field_70127_C;
   private AxisAlignedBB field_70121_D;
   public boolean field_70122_E;
   public boolean field_70123_F;
   public boolean field_70124_G;
   public boolean field_70132_H;
   public boolean field_70133_I;
   protected boolean field_70134_J;
   private boolean field_174835_g;
   public boolean field_70128_L;
   public float field_70130_N;
   public float field_70131_O;
   public float field_70141_P;
   public float field_70140_Q;
   public float field_82151_R;
   public float field_70143_R;
   private float field_70150_b;
   private float field_191959_ay;
   public double field_70142_S;
   public double field_70137_T;
   public double field_70136_U;
   public float field_70138_W;
   public boolean field_70145_X;
   public float field_70144_Y;
   protected Random field_70146_Z;
   public int field_70173_aa;
   private int field_190534_ay;
   protected boolean field_70171_ac;
   protected double field_211517_W;
   protected boolean field_205013_W;
   public int field_70172_ad;
   protected boolean field_70148_d;
   protected boolean field_70178_ae;
   protected EntityDataManager field_70180_af;
   protected static final DataParameter<Byte> field_184240_ax;
   private static final DataParameter<Integer> field_184241_ay;
   private static final DataParameter<Optional<ITextComponent>> field_184242_az;
   private static final DataParameter<Boolean> field_184233_aA;
   private static final DataParameter<Boolean> field_184234_aB;
   private static final DataParameter<Boolean> field_189655_aD;
   public boolean field_70175_ag;
   public int field_70176_ah;
   public int field_70162_ai;
   public int field_70164_aj;
   public long field_70118_ct;
   public long field_70117_cu;
   public long field_70116_cv;
   public boolean field_70158_ak;
   public boolean field_70160_al;
   public int field_71088_bW;
   protected boolean field_71087_bX;
   protected int field_82153_h;
   public DimensionType field_71093_bK;
   protected BlockPos field_181016_an;
   protected Vec3d field_181017_ao;
   protected EnumFacing field_181018_ap;
   private boolean field_83001_bt;
   protected UUID field_96093_i;
   protected String field_189513_ar;
   protected boolean field_184238_ar;
   private final Set<String> field_184236_aF;
   private boolean field_184237_aG;
   private final double[] field_191505_aI;
   private long field_191506_aJ;

   public Entity(EntityType<?> var1, World var2) {
      super();
      this.field_145783_c = field_70152_a++;
      this.field_184244_h = Lists.newArrayList();
      this.field_70121_D = field_174836_a;
      this.field_70130_N = 0.6F;
      this.field_70131_O = 1.8F;
      this.field_70150_b = 1.0F;
      this.field_191959_ay = 1.0F;
      this.field_70146_Z = new Random();
      this.field_190534_ay = -this.func_190531_bD();
      this.field_70148_d = true;
      this.field_96093_i = MathHelper.func_180182_a(this.field_70146_Z);
      this.field_189513_ar = this.field_96093_i.toString();
      this.field_184236_aF = Sets.newHashSet();
      this.field_191505_aI = new double[]{0.0D, 0.0D, 0.0D};
      this.field_200606_g = var1;
      this.field_70170_p = var2;
      this.func_70107_b(0.0D, 0.0D, 0.0D);
      if (var2 != null) {
         this.field_71093_bK = var2.field_73011_w.func_186058_p();
      }

      this.field_70180_af = new EntityDataManager(this);
      this.field_70180_af.func_187214_a(field_184240_ax, (byte)0);
      this.field_70180_af.func_187214_a(field_184241_ay, this.func_205010_bg());
      this.field_70180_af.func_187214_a(field_184233_aA, false);
      this.field_70180_af.func_187214_a(field_184242_az, Optional.empty());
      this.field_70180_af.func_187214_a(field_184234_aB, false);
      this.field_70180_af.func_187214_a(field_189655_aD, false);
      this.func_70088_a();
   }

   public EntityType<?> func_200600_R() {
      return this.field_200606_g;
   }

   public int func_145782_y() {
      return this.field_145783_c;
   }

   public void func_145769_d(int var1) {
      this.field_145783_c = var1;
   }

   public Set<String> func_184216_O() {
      return this.field_184236_aF;
   }

   public boolean func_184211_a(String var1) {
      return this.field_184236_aF.size() >= 1024 ? false : this.field_184236_aF.add(var1);
   }

   public boolean func_184197_b(String var1) {
      return this.field_184236_aF.remove(var1);
   }

   public void func_174812_G() {
      this.func_70106_y();
   }

   protected abstract void func_70088_a();

   public EntityDataManager func_184212_Q() {
      return this.field_70180_af;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof Entity) {
         return ((Entity)var1).field_145783_c == this.field_145783_c;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.field_145783_c;
   }

   protected void func_70065_x() {
      if (this.field_70170_p != null) {
         while(this.field_70163_u > 0.0D && this.field_70163_u < 256.0D) {
            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            if (this.field_70170_p.func_195586_b(this, this.func_174813_aQ())) {
               break;
            }

            ++this.field_70163_u;
         }

         this.field_70159_w = 0.0D;
         this.field_70181_x = 0.0D;
         this.field_70179_y = 0.0D;
         this.field_70125_A = 0.0F;
      }
   }

   public void func_70106_y() {
      this.field_70128_L = true;
   }

   public void func_184174_b(boolean var1) {
   }

   protected void func_70105_a(float var1, float var2) {
      if (var1 != this.field_70130_N || var2 != this.field_70131_O) {
         float var3 = this.field_70130_N;
         this.field_70130_N = var1;
         this.field_70131_O = var2;
         if (this.field_70130_N < var3) {
            double var6 = (double)var1 / 2.0D;
            this.func_174826_a(new AxisAlignedBB(this.field_70165_t - var6, this.field_70163_u, this.field_70161_v - var6, this.field_70165_t + var6, this.field_70163_u + (double)this.field_70131_O, this.field_70161_v + var6));
            return;
         }

         AxisAlignedBB var4 = this.func_174813_aQ();
         this.func_174826_a(new AxisAlignedBB(var4.field_72340_a, var4.field_72338_b, var4.field_72339_c, var4.field_72340_a + (double)this.field_70130_N, var4.field_72338_b + (double)this.field_70131_O, var4.field_72339_c + (double)this.field_70130_N));
         if (this.field_70130_N > var3 && !this.field_70148_d && !this.field_70170_p.field_72995_K) {
            this.func_70091_d(MoverType.SELF, (double)(var3 - this.field_70130_N), 0.0D, (double)(var3 - this.field_70130_N));
         }
      }

   }

   protected void func_70101_b(float var1, float var2) {
      this.field_70177_z = var1 % 360.0F;
      this.field_70125_A = var2 % 360.0F;
   }

   public void func_70107_b(double var1, double var3, double var5) {
      this.field_70165_t = var1;
      this.field_70163_u = var3;
      this.field_70161_v = var5;
      float var7 = this.field_70130_N / 2.0F;
      float var8 = this.field_70131_O;
      this.func_174826_a(new AxisAlignedBB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7));
   }

   public void func_195049_a(double var1, double var3) {
      double var5 = var3 * 0.15D;
      double var7 = var1 * 0.15D;
      this.field_70125_A = (float)((double)this.field_70125_A + var5);
      this.field_70177_z = (float)((double)this.field_70177_z + var7);
      this.field_70125_A = MathHelper.func_76131_a(this.field_70125_A, -90.0F, 90.0F);
      this.field_70127_C = (float)((double)this.field_70127_C + var5);
      this.field_70126_B = (float)((double)this.field_70126_B + var7);
      this.field_70127_C = MathHelper.func_76131_a(this.field_70127_C, -90.0F, 90.0F);
      if (this.field_184239_as != null) {
         this.field_184239_as.func_184190_l(this);
      }

   }

   public void func_70071_h_() {
      if (!this.field_70170_p.field_72995_K) {
         this.func_70052_a(6, this.func_184202_aL());
      }

      this.func_70030_z();
   }

   public void func_70030_z() {
      this.field_70170_p.field_72984_F.func_76320_a("entityBaseTick");
      if (this.func_184218_aH() && this.func_184187_bx().field_70128_L) {
         this.func_184210_p();
      }

      if (this.field_184245_j > 0) {
         --this.field_184245_j;
      }

      this.field_70141_P = this.field_70140_Q;
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      this.field_70127_C = this.field_70125_A;
      this.field_70126_B = this.field_70177_z;
      if (!this.field_70170_p.field_72995_K && this.field_70170_p instanceof WorldServer) {
         this.field_70170_p.field_72984_F.func_76320_a("portal");
         if (this.field_71087_bX) {
            MinecraftServer var1 = this.field_70170_p.func_73046_m();
            if (var1.func_71255_r()) {
               if (!this.func_184218_aH()) {
                  int var2 = this.func_82145_z();
                  if (this.field_82153_h++ >= var2) {
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

         this.func_184173_H();
         this.field_70170_p.field_72984_F.func_76319_b();
      }

      this.func_174830_Y();
      this.func_205011_p();
      if (this.field_70170_p.field_72995_K) {
         this.func_70066_B();
      } else if (this.field_190534_ay > 0) {
         if (this.field_70178_ae) {
            this.field_190534_ay -= 4;
            if (this.field_190534_ay < 0) {
               this.func_70066_B();
            }
         } else {
            if (this.field_190534_ay % 20 == 0) {
               this.func_70097_a(DamageSource.field_76370_b, 1.0F);
            }

            --this.field_190534_ay;
         }
      }

      if (this.func_180799_ab()) {
         this.func_70044_A();
         this.field_70143_R *= 0.5F;
      }

      if (this.field_70163_u < -64.0D) {
         this.func_70076_C();
      }

      if (!this.field_70170_p.field_72995_K) {
         this.func_70052_a(0, this.field_190534_ay > 0);
      }

      this.field_70148_d = false;
      this.field_70170_p.field_72984_F.func_76319_b();
   }

   protected void func_184173_H() {
      if (this.field_71088_bW > 0) {
         --this.field_71088_bW;
      }

   }

   public int func_82145_z() {
      return 1;
   }

   protected void func_70044_A() {
      if (!this.field_70178_ae) {
         this.func_70015_d(15);
         this.func_70097_a(DamageSource.field_76371_c, 4.0F);
      }
   }

   public void func_70015_d(int var1) {
      int var2 = var1 * 20;
      if (this instanceof EntityLivingBase) {
         var2 = EnchantmentProtection.func_92093_a((EntityLivingBase)this, var2);
      }

      if (this.field_190534_ay < var2) {
         this.field_190534_ay = var2;
      }

   }

   public void func_70066_B() {
      this.field_190534_ay = 0;
   }

   protected void func_70076_C() {
      this.func_70106_y();
   }

   public boolean func_70038_c(double var1, double var3, double var5) {
      return this.func_174809_b(this.func_174813_aQ().func_72317_d(var1, var3, var5));
   }

   private boolean func_174809_b(AxisAlignedBB var1) {
      return this.field_70170_p.func_195586_b(this, var1) && !this.field_70170_p.func_72953_d(var1);
   }

   public void func_70091_d(MoverType var1, double var2, double var4, double var6) {
      if (this.field_70145_X) {
         this.func_174826_a(this.func_174813_aQ().func_72317_d(var2, var4, var6));
         this.func_174829_m();
      } else {
         if (var1 == MoverType.PISTON) {
            long var8 = this.field_70170_p.func_82737_E();
            if (var8 != this.field_191506_aJ) {
               Arrays.fill(this.field_191505_aI, 0.0D);
               this.field_191506_aJ = var8;
            }

            int var10;
            double var11;
            if (var2 != 0.0D) {
               var10 = EnumFacing.Axis.X.ordinal();
               var11 = MathHelper.func_151237_a(var2 + this.field_191505_aI[var10], -0.51D, 0.51D);
               var2 = var11 - this.field_191505_aI[var10];
               this.field_191505_aI[var10] = var11;
               if (Math.abs(var2) <= 9.999999747378752E-6D) {
                  return;
               }
            } else if (var4 != 0.0D) {
               var10 = EnumFacing.Axis.Y.ordinal();
               var11 = MathHelper.func_151237_a(var4 + this.field_191505_aI[var10], -0.51D, 0.51D);
               var4 = var11 - this.field_191505_aI[var10];
               this.field_191505_aI[var10] = var11;
               if (Math.abs(var4) <= 9.999999747378752E-6D) {
                  return;
               }
            } else {
               if (var6 == 0.0D) {
                  return;
               }

               var10 = EnumFacing.Axis.Z.ordinal();
               var11 = MathHelper.func_151237_a(var6 + this.field_191505_aI[var10], -0.51D, 0.51D);
               var6 = var11 - this.field_191505_aI[var10];
               this.field_191505_aI[var10] = var11;
               if (Math.abs(var6) <= 9.999999747378752E-6D) {
                  return;
               }
            }
         }

         this.field_70170_p.field_72984_F.func_76320_a("move");
         double var50 = this.field_70165_t;
         double var51 = this.field_70163_u;
         double var12 = this.field_70161_v;
         if (this.field_70134_J) {
            this.field_70134_J = false;
            var2 *= 0.25D;
            var4 *= 0.05000000074505806D;
            var6 *= 0.25D;
            this.field_70159_w = 0.0D;
            this.field_70181_x = 0.0D;
            this.field_70179_y = 0.0D;
         }

         double var14 = var2;
         double var16 = var4;
         double var18 = var6;
         if ((var1 == MoverType.SELF || var1 == MoverType.PLAYER) && this.field_70122_E && this.func_70093_af() && this instanceof EntityPlayer) {
            for(double var20 = 0.05D; var2 != 0.0D && this.field_70170_p.func_195586_b(this, this.func_174813_aQ().func_72317_d(var2, (double)(-this.field_70138_W), 0.0D)); var14 = var2) {
               if (var2 < 0.05D && var2 >= -0.05D) {
                  var2 = 0.0D;
               } else if (var2 > 0.0D) {
                  var2 -= 0.05D;
               } else {
                  var2 += 0.05D;
               }
            }

            for(; var6 != 0.0D && this.field_70170_p.func_195586_b(this, this.func_174813_aQ().func_72317_d(0.0D, (double)(-this.field_70138_W), var6)); var18 = var6) {
               if (var6 < 0.05D && var6 >= -0.05D) {
                  var6 = 0.0D;
               } else if (var6 > 0.0D) {
                  var6 -= 0.05D;
               } else {
                  var6 += 0.05D;
               }
            }

            for(; var2 != 0.0D && var6 != 0.0D && this.field_70170_p.func_195586_b(this, this.func_174813_aQ().func_72317_d(var2, (double)(-this.field_70138_W), var6)); var18 = var6) {
               if (var2 < 0.05D && var2 >= -0.05D) {
                  var2 = 0.0D;
               } else if (var2 > 0.0D) {
                  var2 -= 0.05D;
               } else {
                  var2 += 0.05D;
               }

               var14 = var2;
               if (var6 < 0.05D && var6 >= -0.05D) {
                  var6 = 0.0D;
               } else if (var6 > 0.0D) {
                  var6 -= 0.05D;
               } else {
                  var6 += 0.05D;
               }
            }
         }

         AxisAlignedBB var52 = this.func_174813_aQ();
         if (var2 != 0.0D || var4 != 0.0D || var6 != 0.0D) {
            ReuseableStream var21 = new ReuseableStream(this.field_70170_p.func_199406_a(this, this.func_174813_aQ(), var2, var4, var6));
            if (var4 != 0.0D) {
               var4 = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, this.func_174813_aQ(), var21.func_212761_a(), var4);
               this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0D, var4, 0.0D));
            }

            if (var2 != 0.0D) {
               var2 = VoxelShapes.func_212437_a(EnumFacing.Axis.X, this.func_174813_aQ(), var21.func_212761_a(), var2);
               if (var2 != 0.0D) {
                  this.func_174826_a(this.func_174813_aQ().func_72317_d(var2, 0.0D, 0.0D));
               }
            }

            if (var6 != 0.0D) {
               var6 = VoxelShapes.func_212437_a(EnumFacing.Axis.Z, this.func_174813_aQ(), var21.func_212761_a(), var6);
               if (var6 != 0.0D) {
                  this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0D, 0.0D, var6));
               }
            }
         }

         boolean var53 = this.field_70122_E || var4 != var4 && var4 < 0.0D;
         double var32;
         if (this.field_70138_W > 0.0F && var53 && (var14 != var2 || var18 != var6)) {
            double var22 = var2;
            double var24 = var4;
            double var26 = var6;
            AxisAlignedBB var28 = this.func_174813_aQ();
            this.func_174826_a(var52);
            var2 = var14;
            var4 = (double)this.field_70138_W;
            var6 = var18;
            if (var14 != 0.0D || var4 != 0.0D || var18 != 0.0D) {
               ReuseableStream var29 = new ReuseableStream(this.field_70170_p.func_199406_a(this, this.func_174813_aQ(), var14, var4, var18));
               AxisAlignedBB var30 = this.func_174813_aQ();
               AxisAlignedBB var31 = var30.func_72321_a(var14, 0.0D, var18);
               var32 = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, var31, var29.func_212761_a(), var4);
               if (var32 != 0.0D) {
                  var30 = var30.func_72317_d(0.0D, var32, 0.0D);
               }

               double var34 = VoxelShapes.func_212437_a(EnumFacing.Axis.X, var30, var29.func_212761_a(), var14);
               if (var34 != 0.0D) {
                  var30 = var30.func_72317_d(var34, 0.0D, 0.0D);
               }

               double var36 = VoxelShapes.func_212437_a(EnumFacing.Axis.Z, var30, var29.func_212761_a(), var18);
               if (var36 != 0.0D) {
                  var30 = var30.func_72317_d(0.0D, 0.0D, var36);
               }

               AxisAlignedBB var38 = this.func_174813_aQ();
               double var39 = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, var38, var29.func_212761_a(), var4);
               if (var39 != 0.0D) {
                  var38 = var38.func_72317_d(0.0D, var39, 0.0D);
               }

               double var41 = VoxelShapes.func_212437_a(EnumFacing.Axis.X, var38, var29.func_212761_a(), var14);
               if (var41 != 0.0D) {
                  var38 = var38.func_72317_d(var41, 0.0D, 0.0D);
               }

               double var43 = VoxelShapes.func_212437_a(EnumFacing.Axis.Z, var38, var29.func_212761_a(), var18);
               if (var43 != 0.0D) {
                  var38 = var38.func_72317_d(0.0D, 0.0D, var43);
               }

               double var45 = var34 * var34 + var36 * var36;
               double var47 = var41 * var41 + var43 * var43;
               if (var45 > var47) {
                  var2 = var34;
                  var6 = var36;
                  var4 = -var32;
                  this.func_174826_a(var30);
               } else {
                  var2 = var41;
                  var6 = var43;
                  var4 = -var39;
                  this.func_174826_a(var38);
               }

               var4 = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, this.func_174813_aQ(), var29.func_212761_a(), var4);
               if (var4 != 0.0D) {
                  this.func_174826_a(this.func_174813_aQ().func_72317_d(0.0D, var4, 0.0D));
               }
            }

            if (var22 * var22 + var26 * var26 >= var2 * var2 + var6 * var6) {
               var2 = var22;
               var4 = var24;
               var6 = var26;
               this.func_174826_a(var28);
            }
         }

         this.field_70170_p.field_72984_F.func_76319_b();
         this.field_70170_p.field_72984_F.func_76320_a("rest");
         this.func_174829_m();
         this.field_70123_F = var14 != var2 || var18 != var6;
         this.field_70124_G = var4 != var4;
         this.field_70122_E = this.field_70124_G && var16 < 0.0D;
         this.field_70132_H = this.field_70123_F || this.field_70124_G;
         int var54 = MathHelper.func_76128_c(this.field_70165_t);
         int var23 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D);
         int var55 = MathHelper.func_76128_c(this.field_70161_v);
         BlockPos var25 = new BlockPos(var54, var23, var55);
         IBlockState var56 = this.field_70170_p.func_180495_p(var25);
         if (var56.func_196958_f()) {
            BlockPos var27 = var25.func_177977_b();
            IBlockState var58 = this.field_70170_p.func_180495_p(var27);
            Block var61 = var58.func_177230_c();
            if (var61 instanceof BlockFence || var61 instanceof BlockWall || var61 instanceof BlockFenceGate) {
               var56 = var58;
               var25 = var27;
            }
         }

         this.func_184231_a(var4, this.field_70122_E, var56, var25);
         if (var14 != var2) {
            this.field_70159_w = 0.0D;
         }

         if (var18 != var6) {
            this.field_70179_y = 0.0D;
         }

         Block var57 = var56.func_177230_c();
         if (var16 != var4) {
            var57.func_176216_a(this.field_70170_p, this);
         }

         if (this.func_70041_e_() && (!this.field_70122_E || !this.func_70093_af() || !(this instanceof EntityPlayer)) && !this.func_184218_aH()) {
            double var59 = this.field_70165_t - var50;
            double var63 = this.field_70163_u - var51;
            var32 = this.field_70161_v - var12;
            if (var57 != Blocks.field_150468_ap) {
               var63 = 0.0D;
            }

            if (var57 != null && this.field_70122_E) {
               var57.func_176199_a(this.field_70170_p, var25, this);
            }

            this.field_70140_Q = (float)((double)this.field_70140_Q + (double)MathHelper.func_76133_a(var59 * var59 + var32 * var32) * 0.6D);
            this.field_82151_R = (float)((double)this.field_82151_R + (double)MathHelper.func_76133_a(var59 * var59 + var63 * var63 + var32 * var32) * 0.6D);
            if (this.field_82151_R > this.field_70150_b && !var56.func_196958_f()) {
               this.field_70150_b = this.func_203009_ad();
               if (this.func_70090_H()) {
                  Entity var65 = this.func_184207_aI() && this.func_184179_bs() != null ? this.func_184179_bs() : this;
                  float var35 = var65 == this ? 0.35F : 0.4F;
                  float var66 = MathHelper.func_76133_a(var65.field_70159_w * var65.field_70159_w * 0.20000000298023224D + var65.field_70181_x * var65.field_70181_x + var65.field_70179_y * var65.field_70179_y * 0.20000000298023224D) * var35;
                  if (var66 > 1.0F) {
                     var66 = 1.0F;
                  }

                  this.func_203006_d(var66);
               } else {
                  this.func_180429_a(var25, var56);
               }
            } else if (this.field_82151_R > this.field_191959_ay && this.func_191957_ae() && var56.func_196958_f()) {
               this.field_191959_ay = this.func_191954_d(this.field_82151_R);
            }
         }

         try {
            this.func_145775_I();
         } catch (Throwable var49) {
            CrashReport var62 = CrashReport.func_85055_a(var49, "Checking entity block collision");
            CrashReportCategory var64 = var62.func_85058_a("Entity being checked for collision");
            this.func_85029_a(var64);
            throw new ReportedException(var62);
         }

         boolean var60 = this.func_203008_ap();
         if (this.field_70170_p.func_147470_e(this.func_174813_aQ().func_186664_h(0.001D))) {
            if (!var60) {
               ++this.field_190534_ay;
               if (this.field_190534_ay == 0) {
                  this.func_70015_d(8);
               }
            }

            this.func_70081_e(1);
         } else if (this.field_190534_ay <= 0) {
            this.field_190534_ay = -this.func_190531_bD();
         }

         if (var60 && this.func_70027_ad()) {
            this.func_184185_a(SoundEvents.field_187541_bC, 0.7F, 1.6F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
            this.field_190534_ay = -this.func_190531_bD();
         }

         this.field_70170_p.field_72984_F.func_76319_b();
      }
   }

   protected float func_203009_ad() {
      return (float)((int)this.field_82151_R + 1);
   }

   public void func_174829_m() {
      AxisAlignedBB var1 = this.func_174813_aQ();
      this.field_70165_t = (var1.field_72340_a + var1.field_72336_d) / 2.0D;
      this.field_70163_u = var1.field_72338_b;
      this.field_70161_v = (var1.field_72339_c + var1.field_72334_f) / 2.0D;
   }

   protected SoundEvent func_184184_Z() {
      return SoundEvents.field_187549_bG;
   }

   protected SoundEvent func_184181_aa() {
      return SoundEvents.field_187547_bF;
   }

   protected SoundEvent func_204208_ah() {
      return SoundEvents.field_187547_bF;
   }

   protected void func_145775_I() {
      AxisAlignedBB var1 = this.func_174813_aQ();
      BlockPos.PooledMutableBlockPos var2 = BlockPos.PooledMutableBlockPos.func_185345_c(var1.field_72340_a + 0.001D, var1.field_72338_b + 0.001D, var1.field_72339_c + 0.001D);
      Throwable var3 = null;

      try {
         BlockPos.PooledMutableBlockPos var4 = BlockPos.PooledMutableBlockPos.func_185345_c(var1.field_72336_d - 0.001D, var1.field_72337_e - 0.001D, var1.field_72334_f - 0.001D);
         Throwable var5 = null;

         try {
            BlockPos.PooledMutableBlockPos var6 = BlockPos.PooledMutableBlockPos.func_185346_s();
            Throwable var7 = null;

            try {
               if (this.field_70170_p.func_175707_a(var2, var4)) {
                  for(int var8 = var2.func_177958_n(); var8 <= var4.func_177958_n(); ++var8) {
                     for(int var9 = var2.func_177956_o(); var9 <= var4.func_177956_o(); ++var9) {
                        for(int var10 = var2.func_177952_p(); var10 <= var4.func_177952_p(); ++var10) {
                           var6.func_181079_c(var8, var9, var10);
                           IBlockState var11 = this.field_70170_p.func_180495_p(var6);

                           try {
                              var11.func_196950_a(this.field_70170_p, var6, this);
                              this.func_191955_a(var11);
                           } catch (Throwable var60) {
                              CrashReport var13 = CrashReport.func_85055_a(var60, "Colliding entity with block");
                              CrashReportCategory var14 = var13.func_85058_a("Block being collided with");
                              CrashReportCategory.func_175750_a(var14, var6, var11);
                              throw new ReportedException(var13);
                           }
                        }
                     }
                  }
               }
            } catch (Throwable var61) {
               var7 = var61;
               throw var61;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var59) {
                        var7.addSuppressed(var59);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } catch (Throwable var63) {
            var5 = var63;
            throw var63;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var58) {
                     var5.addSuppressed(var58);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Throwable var65) {
         var3 = var65;
         throw var65;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var57) {
                  var3.addSuppressed(var57);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   protected void func_191955_a(IBlockState var1) {
   }

   protected void func_180429_a(BlockPos var1, IBlockState var2) {
      if (!var2.func_185904_a().func_76224_d()) {
         SoundType var3 = this.field_70170_p.func_180495_p(var1.func_177984_a()).func_177230_c() == Blocks.field_150433_aE ? Blocks.field_150433_aE.func_185467_w() : var2.func_177230_c().func_185467_w();
         this.func_184185_a(var3.func_185844_d(), var3.func_185843_a() * 0.15F, var3.func_185847_b());
      }
   }

   protected void func_203006_d(float var1) {
      this.func_184185_a(this.func_184184_Z(), var1, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
   }

   protected float func_191954_d(float var1) {
      return 0.0F;
   }

   protected boolean func_191957_ae() {
      return false;
   }

   public void func_184185_a(SoundEvent var1, float var2, float var3) {
      if (!this.func_174814_R()) {
         this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, var1, this.func_184176_by(), var2, var3);
      }

   }

   public boolean func_174814_R() {
      return (Boolean)this.field_70180_af.func_187225_a(field_184234_aB);
   }

   public void func_174810_b(boolean var1) {
      this.field_70180_af.func_187227_b(field_184234_aB, var1);
   }

   public boolean func_189652_ae() {
      return (Boolean)this.field_70180_af.func_187225_a(field_189655_aD);
   }

   public void func_189654_d(boolean var1) {
      this.field_70180_af.func_187227_b(field_189655_aD, var1);
   }

   protected boolean func_70041_e_() {
      return true;
   }

   protected void func_184231_a(double var1, boolean var3, IBlockState var4, BlockPos var5) {
      if (var3) {
         if (this.field_70143_R > 0.0F) {
            var4.func_177230_c().func_180658_a(this.field_70170_p, var5, this, this.field_70143_R);
         }

         this.field_70143_R = 0.0F;
      } else if (var1 < 0.0D) {
         this.field_70143_R = (float)((double)this.field_70143_R - var1);
      }

   }

   @Nullable
   public AxisAlignedBB func_70046_E() {
      return null;
   }

   protected void func_70081_e(int var1) {
      if (!this.field_70178_ae) {
         this.func_70097_a(DamageSource.field_76372_a, (float)var1);
      }

   }

   public final boolean func_70045_F() {
      return this.field_70178_ae;
   }

   public void func_180430_e(float var1, float var2) {
      if (this.func_184207_aI()) {
         Iterator var3 = this.func_184188_bt().iterator();

         while(var3.hasNext()) {
            Entity var4 = (Entity)var3.next();
            var4.func_180430_e(var1, var2);
         }
      }

   }

   public boolean func_70090_H() {
      return this.field_70171_ac;
   }

   private boolean func_209511_p() {
      BlockPos.PooledMutableBlockPos var1 = BlockPos.PooledMutableBlockPos.func_209907_b(this);
      Throwable var2 = null;

      boolean var3;
      try {
         var3 = this.field_70170_p.func_175727_C(var1) || this.field_70170_p.func_175727_C(var1.func_189532_c(this.field_70165_t, this.field_70163_u + (double)this.field_70131_O, this.field_70161_v));
      } catch (Throwable var12) {
         var2 = var12;
         throw var12;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var11) {
                  var2.addSuppressed(var11);
               }
            } else {
               var1.close();
            }
         }

      }

      return var3;
   }

   private boolean func_209512_q() {
      return this.field_70170_p.func_180495_p(new BlockPos(this)).func_177230_c() == Blocks.field_203203_C;
   }

   public boolean func_70026_G() {
      return this.func_70090_H() || this.func_209511_p();
   }

   public boolean func_203008_ap() {
      return this.func_70090_H() || this.func_209511_p() || this.func_209512_q();
   }

   public boolean func_203005_aq() {
      return this.func_70090_H() || this.func_209512_q();
   }

   public boolean func_204231_K() {
      return this.field_205013_W && this.func_70090_H();
   }

   private void func_205011_p() {
      this.func_70072_I();
      this.func_205012_q();
      this.func_205343_av();
   }

   public void func_205343_av() {
      if (this.func_203007_ba()) {
         this.func_204711_a(this.func_70051_ag() && this.func_70090_H() && !this.func_184218_aH());
      } else {
         this.func_204711_a(this.func_70051_ag() && this.func_204231_K() && !this.func_184218_aH());
      }

   }

   public boolean func_70072_I() {
      if (this.func_184187_bx() instanceof EntityBoat) {
         this.field_70171_ac = false;
      } else if (this.func_210500_b(FluidTags.field_206959_a)) {
         if (!this.field_70171_ac && !this.field_70148_d) {
            this.func_71061_d_();
         }

         this.field_70143_R = 0.0F;
         this.field_70171_ac = true;
         this.func_70066_B();
      } else {
         this.field_70171_ac = false;
      }

      return this.field_70171_ac;
   }

   private void func_205012_q() {
      this.field_205013_W = this.func_208600_a(FluidTags.field_206959_a);
   }

   protected void func_71061_d_() {
      Entity var1 = this.func_184207_aI() && this.func_184179_bs() != null ? this.func_184179_bs() : this;
      float var2 = var1 == this ? 0.2F : 0.9F;
      float var3 = MathHelper.func_76133_a(var1.field_70159_w * var1.field_70159_w * 0.20000000298023224D + var1.field_70181_x * var1.field_70181_x + var1.field_70179_y * var1.field_70179_y * 0.20000000298023224D) * var2;
      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      if ((double)var3 < 0.25D) {
         this.func_184185_a(this.func_184181_aa(), var3, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
      } else {
         this.func_184185_a(this.func_204208_ah(), var3, 1.0F + (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.4F);
      }

      float var4 = (float)MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b);

      int var5;
      float var6;
      float var7;
      for(var5 = 0; (float)var5 < 1.0F + this.field_70130_N * 20.0F; ++var5) {
         var6 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
         var7 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
         this.field_70170_p.func_195594_a(Particles.field_197612_e, this.field_70165_t + (double)var6, (double)(var4 + 1.0F), this.field_70161_v + (double)var7, this.field_70159_w, this.field_70181_x - (double)(this.field_70146_Z.nextFloat() * 0.2F), this.field_70179_y);
      }

      for(var5 = 0; (float)var5 < 1.0F + this.field_70130_N * 20.0F; ++var5) {
         var6 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
         var7 = (this.field_70146_Z.nextFloat() * 2.0F - 1.0F) * this.field_70130_N;
         this.field_70170_p.func_195594_a(Particles.field_197606_Q, this.field_70165_t + (double)var6, (double)(var4 + 1.0F), this.field_70161_v + (double)var7, this.field_70159_w, this.field_70181_x, this.field_70179_y);
      }

   }

   public void func_174830_Y() {
      if (this.func_70051_ag() && !this.func_70090_H()) {
         this.func_174808_Z();
      }

   }

   protected void func_174808_Z() {
      int var1 = MathHelper.func_76128_c(this.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      BlockPos var4 = new BlockPos(var1, var2, var3);
      IBlockState var5 = this.field_70170_p.func_180495_p(var4);
      if (var5.func_185901_i() != EnumBlockRenderType.INVISIBLE) {
         this.field_70170_p.func_195594_a(new BlockParticleData(Particles.field_197611_d, var5), this.field_70165_t + ((double)this.field_70146_Z.nextFloat() - 0.5D) * (double)this.field_70130_N, this.func_174813_aQ().field_72338_b + 0.1D, this.field_70161_v + ((double)this.field_70146_Z.nextFloat() - 0.5D) * (double)this.field_70130_N, -this.field_70159_w * 4.0D, 1.5D, -this.field_70179_y * 4.0D);
      }

   }

   public boolean func_208600_a(Tag<Fluid> var1) {
      if (this.func_184187_bx() instanceof EntityBoat) {
         return false;
      } else {
         double var2 = this.field_70163_u + (double)this.func_70047_e();
         BlockPos var4 = new BlockPos(this.field_70165_t, var2, this.field_70161_v);
         IFluidState var5 = this.field_70170_p.func_204610_c(var4);
         return var5.func_206884_a(var1) && var2 < (double)((float)var4.func_177956_o() + var5.func_206885_f() + 0.11111111F);
      }
   }

   public boolean func_180799_ab() {
      return this.field_70170_p.func_72875_a(this.func_174813_aQ().func_211539_f(0.10000000149011612D, 0.4000000059604645D, 0.10000000149011612D), Material.field_151587_i);
   }

   public void func_191958_b(float var1, float var2, float var3, float var4) {
      float var5 = var1 * var1 + var2 * var2 + var3 * var3;
      if (var5 >= 1.0E-4F) {
         var5 = MathHelper.func_76129_c(var5);
         if (var5 < 1.0F) {
            var5 = 1.0F;
         }

         var5 = var4 / var5;
         var1 *= var5;
         var2 *= var5;
         var3 *= var5;
         float var6 = MathHelper.func_76126_a(this.field_70177_z * 0.017453292F);
         float var7 = MathHelper.func_76134_b(this.field_70177_z * 0.017453292F);
         this.field_70159_w += (double)(var1 * var7 - var3 * var6);
         this.field_70181_x += (double)var2;
         this.field_70179_y += (double)(var3 * var7 + var1 * var6);
      }
   }

   public int func_70070_b() {
      BlockPos var1 = new BlockPos(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v);
      return this.field_70170_p.func_175667_e(var1) ? this.field_70170_p.func_175626_b(var1, 0) : 0;
   }

   public float func_70013_c() {
      BlockPos.MutableBlockPos var1 = new BlockPos.MutableBlockPos(MathHelper.func_76128_c(this.field_70165_t), 0, MathHelper.func_76128_c(this.field_70161_v));
      if (this.field_70170_p.func_175667_e(var1)) {
         var1.func_185336_p(MathHelper.func_76128_c(this.field_70163_u + (double)this.func_70047_e()));
         return this.field_70170_p.func_205052_D(var1);
      } else {
         return 0.0F;
      }
   }

   public void func_70029_a(World var1) {
      this.field_70170_p = var1;
   }

   public void func_70080_a(double var1, double var3, double var5, float var7, float var8) {
      this.field_70165_t = MathHelper.func_151237_a(var1, -3.0E7D, 3.0E7D);
      this.field_70163_u = var3;
      this.field_70161_v = MathHelper.func_151237_a(var5, -3.0E7D, 3.0E7D);
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      var8 = MathHelper.func_76131_a(var8, -90.0F, 90.0F);
      this.field_70177_z = var7;
      this.field_70125_A = var8;
      this.field_70126_B = this.field_70177_z;
      this.field_70127_C = this.field_70125_A;
      double var9 = (double)(this.field_70126_B - var7);
      if (var9 < -180.0D) {
         this.field_70126_B += 360.0F;
      }

      if (var9 >= 180.0D) {
         this.field_70126_B -= 360.0F;
      }

      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.func_70101_b(var7, var8);
   }

   public void func_174828_a(BlockPos var1, float var2, float var3) {
      this.func_70012_b((double)var1.func_177958_n() + 0.5D, (double)var1.func_177956_o(), (double)var1.func_177952_p() + 0.5D, var2, var3);
   }

   public void func_70012_b(double var1, double var3, double var5, float var7, float var8) {
      this.field_70165_t = var1;
      this.field_70163_u = var3;
      this.field_70161_v = var5;
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      this.field_70142_S = this.field_70165_t;
      this.field_70137_T = this.field_70163_u;
      this.field_70136_U = this.field_70161_v;
      this.field_70177_z = var7;
      this.field_70125_A = var8;
      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public float func_70032_d(Entity var1) {
      float var2 = (float)(this.field_70165_t - var1.field_70165_t);
      float var3 = (float)(this.field_70163_u - var1.field_70163_u);
      float var4 = (float)(this.field_70161_v - var1.field_70161_v);
      return MathHelper.func_76129_c(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public double func_70092_e(double var1, double var3, double var5) {
      double var7 = this.field_70165_t - var1;
      double var9 = this.field_70163_u - var3;
      double var11 = this.field_70161_v - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double func_174818_b(BlockPos var1) {
      return var1.func_177954_c(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public double func_174831_c(BlockPos var1) {
      return var1.func_177957_d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public double func_70011_f(double var1, double var3, double var5) {
      double var7 = this.field_70165_t - var1;
      double var9 = this.field_70163_u - var3;
      double var11 = this.field_70161_v - var5;
      return (double)MathHelper.func_76133_a(var7 * var7 + var9 * var9 + var11 * var11);
   }

   public double func_70068_e(Entity var1) {
      double var2 = this.field_70165_t - var1.field_70165_t;
      double var4 = this.field_70163_u - var1.field_70163_u;
      double var6 = this.field_70161_v - var1.field_70161_v;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public double func_195048_a(Vec3d var1) {
      double var2 = this.field_70165_t - var1.field_72450_a;
      double var4 = this.field_70163_u - var1.field_72448_b;
      double var6 = this.field_70161_v - var1.field_72449_c;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public void func_70100_b_(EntityPlayer var1) {
   }

   public void func_70108_f(Entity var1) {
      if (!this.func_184223_x(var1)) {
         if (!var1.field_70145_X && !this.field_70145_X) {
            double var2 = var1.field_70165_t - this.field_70165_t;
            double var4 = var1.field_70161_v - this.field_70161_v;
            double var6 = MathHelper.func_76132_a(var2, var4);
            if (var6 >= 0.009999999776482582D) {
               var6 = (double)MathHelper.func_76133_a(var6);
               var2 /= var6;
               var4 /= var6;
               double var8 = 1.0D / var6;
               if (var8 > 1.0D) {
                  var8 = 1.0D;
               }

               var2 *= var8;
               var4 *= var8;
               var2 *= 0.05000000074505806D;
               var4 *= 0.05000000074505806D;
               var2 *= (double)(1.0F - this.field_70144_Y);
               var4 *= (double)(1.0F - this.field_70144_Y);
               if (!this.func_184207_aI()) {
                  this.func_70024_g(-var2, 0.0D, -var4);
               }

               if (!var1.func_184207_aI()) {
                  var1.func_70024_g(var2, 0.0D, var4);
               }
            }

         }
      }
   }

   public void func_70024_g(double var1, double var3, double var5) {
      this.field_70159_w += var1;
      this.field_70181_x += var3;
      this.field_70179_y += var5;
      this.field_70160_al = true;
   }

   protected void func_70018_K() {
      this.field_70133_I = true;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         this.func_70018_K();
         return false;
      }
   }

   public final Vec3d func_70676_i(float var1) {
      return this.func_174806_f(this.func_195050_f(var1), this.func_195046_g(var1));
   }

   public float func_195050_f(float var1) {
      return var1 == 1.0F ? this.field_70125_A : this.field_70127_C + (this.field_70125_A - this.field_70127_C) * var1;
   }

   public float func_195046_g(float var1) {
      return var1 == 1.0F ? this.field_70177_z : this.field_70126_B + (this.field_70177_z - this.field_70126_B) * var1;
   }

   protected final Vec3d func_174806_f(float var1, float var2) {
      float var3 = var1 * 0.017453292F;
      float var4 = -var2 * 0.017453292F;
      float var5 = MathHelper.func_76134_b(var4);
      float var6 = MathHelper.func_76126_a(var4);
      float var7 = MathHelper.func_76134_b(var3);
      float var8 = MathHelper.func_76126_a(var3);
      return new Vec3d((double)(var6 * var7), (double)(-var8), (double)(var5 * var7));
   }

   public Vec3d func_174824_e(float var1) {
      if (var1 == 1.0F) {
         return new Vec3d(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v);
      } else {
         double var2 = this.field_70169_q + (this.field_70165_t - this.field_70169_q) * (double)var1;
         double var4 = this.field_70167_r + (this.field_70163_u - this.field_70167_r) * (double)var1 + (double)this.func_70047_e();
         double var6 = this.field_70166_s + (this.field_70161_v - this.field_70166_s) * (double)var1;
         return new Vec3d(var2, var4, var6);
      }
   }

   @Nullable
   public RayTraceResult func_174822_a(double var1, float var3, RayTraceFluidMode var4) {
      Vec3d var5 = this.func_174824_e(var3);
      Vec3d var6 = this.func_70676_i(var3);
      Vec3d var7 = var5.func_72441_c(var6.field_72450_a * var1, var6.field_72448_b * var1, var6.field_72449_c * var1);
      return this.field_70170_p.func_200259_a(var5, var7, var4, false, true);
   }

   public boolean func_70067_L() {
      return false;
   }

   public boolean func_70104_M() {
      return false;
   }

   public void func_191956_a(Entity var1, int var2, DamageSource var3) {
      if (var1 instanceof EntityPlayerMP) {
         CriteriaTriggers.field_192123_c.func_192211_a((EntityPlayerMP)var1, this, var3);
      }

   }

   public boolean func_145770_h(double var1, double var3, double var5) {
      double var7 = this.field_70165_t - var1;
      double var9 = this.field_70163_u - var3;
      double var11 = this.field_70161_v - var5;
      double var13 = var7 * var7 + var9 * var9 + var11 * var11;
      return this.func_70112_a(var13);
   }

   public boolean func_70112_a(double var1) {
      double var3 = this.func_174813_aQ().func_72320_b();
      if (Double.isNaN(var3)) {
         var3 = 1.0D;
      }

      var3 *= 64.0D * field_70155_l;
      return var1 < var3 * var3;
   }

   public boolean func_184198_c(NBTTagCompound var1) {
      String var2 = this.func_70022_Q();
      if (!this.field_70128_L && var2 != null) {
         var1.func_74778_a("id", var2);
         this.func_189511_e(var1);
         return true;
      } else {
         return false;
      }
   }

   public boolean func_70039_c(NBTTagCompound var1) {
      return this.func_184218_aH() ? false : this.func_184198_c(var1);
   }

   public NBTTagCompound func_189511_e(NBTTagCompound var1) {
      try {
         var1.func_74782_a("Pos", this.func_70087_a(this.field_70165_t, this.field_70163_u, this.field_70161_v));
         var1.func_74782_a("Motion", this.func_70087_a(this.field_70159_w, this.field_70181_x, this.field_70179_y));
         var1.func_74782_a("Rotation", this.func_70049_a(this.field_70177_z, this.field_70125_A));
         var1.func_74776_a("FallDistance", this.field_70143_R);
         var1.func_74777_a("Fire", (short)this.field_190534_ay);
         var1.func_74777_a("Air", (short)this.func_70086_ai());
         var1.func_74757_a("OnGround", this.field_70122_E);
         var1.func_74768_a("Dimension", this.field_71093_bK.func_186068_a());
         var1.func_74757_a("Invulnerable", this.field_83001_bt);
         var1.func_74768_a("PortalCooldown", this.field_71088_bW);
         var1.func_186854_a("UUID", this.func_110124_au());
         ITextComponent var2 = this.func_200201_e();
         if (var2 != null) {
            var1.func_74778_a("CustomName", ITextComponent.Serializer.func_150696_a(var2));
         }

         if (this.func_174833_aM()) {
            var1.func_74757_a("CustomNameVisible", this.func_174833_aM());
         }

         if (this.func_174814_R()) {
            var1.func_74757_a("Silent", this.func_174814_R());
         }

         if (this.func_189652_ae()) {
            var1.func_74757_a("NoGravity", this.func_189652_ae());
         }

         if (this.field_184238_ar) {
            var1.func_74757_a("Glowing", this.field_184238_ar);
         }

         NBTTagList var8;
         Iterator var9;
         if (!this.field_184236_aF.isEmpty()) {
            var8 = new NBTTagList();
            var9 = this.field_184236_aF.iterator();

            while(var9.hasNext()) {
               String var5 = (String)var9.next();
               var8.add((INBTBase)(new NBTTagString(var5)));
            }

            var1.func_74782_a("Tags", var8);
         }

         this.func_70014_b(var1);
         if (this.func_184207_aI()) {
            var8 = new NBTTagList();
            var9 = this.func_184188_bt().iterator();

            while(var9.hasNext()) {
               Entity var10 = (Entity)var9.next();
               NBTTagCompound var6 = new NBTTagCompound();
               if (var10.func_184198_c(var6)) {
                  var8.add((INBTBase)var6);
               }
            }

            if (!var8.isEmpty()) {
               var1.func_74782_a("Passengers", var8);
            }
         }

         return var1;
      } catch (Throwable var7) {
         CrashReport var3 = CrashReport.func_85055_a(var7, "Saving entity NBT");
         CrashReportCategory var4 = var3.func_85058_a("Entity being saved");
         this.func_85029_a(var4);
         throw new ReportedException(var3);
      }
   }

   public void func_70020_e(NBTTagCompound var1) {
      try {
         NBTTagList var2 = var1.func_150295_c("Pos", 6);
         NBTTagList var9 = var1.func_150295_c("Motion", 6);
         NBTTagList var10 = var1.func_150295_c("Rotation", 5);
         this.field_70159_w = var9.func_150309_d(0);
         this.field_70181_x = var9.func_150309_d(1);
         this.field_70179_y = var9.func_150309_d(2);
         if (Math.abs(this.field_70159_w) > 10.0D) {
            this.field_70159_w = 0.0D;
         }

         if (Math.abs(this.field_70181_x) > 10.0D) {
            this.field_70181_x = 0.0D;
         }

         if (Math.abs(this.field_70179_y) > 10.0D) {
            this.field_70179_y = 0.0D;
         }

         this.field_70165_t = var2.func_150309_d(0);
         this.field_70163_u = var2.func_150309_d(1);
         this.field_70161_v = var2.func_150309_d(2);
         this.field_70142_S = this.field_70165_t;
         this.field_70137_T = this.field_70163_u;
         this.field_70136_U = this.field_70161_v;
         this.field_70169_q = this.field_70165_t;
         this.field_70167_r = this.field_70163_u;
         this.field_70166_s = this.field_70161_v;
         this.field_70177_z = var10.func_150308_e(0);
         this.field_70125_A = var10.func_150308_e(1);
         this.field_70126_B = this.field_70177_z;
         this.field_70127_C = this.field_70125_A;
         this.func_70034_d(this.field_70177_z);
         this.func_181013_g(this.field_70177_z);
         this.field_70143_R = var1.func_74760_g("FallDistance");
         this.field_190534_ay = var1.func_74765_d("Fire");
         this.func_70050_g(var1.func_74765_d("Air"));
         this.field_70122_E = var1.func_74767_n("OnGround");
         if (var1.func_74764_b("Dimension")) {
            this.field_71093_bK = DimensionType.func_186069_a(var1.func_74762_e("Dimension"));
         }

         this.field_83001_bt = var1.func_74767_n("Invulnerable");
         this.field_71088_bW = var1.func_74762_e("PortalCooldown");
         if (var1.func_186855_b("UUID")) {
            this.field_96093_i = var1.func_186857_a("UUID");
            this.field_189513_ar = this.field_96093_i.toString();
         }

         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
         if (var1.func_150297_b("CustomName", 8)) {
            this.func_200203_b(ITextComponent.Serializer.func_150699_a(var1.func_74779_i("CustomName")));
         }

         this.func_174805_g(var1.func_74767_n("CustomNameVisible"));
         this.func_174810_b(var1.func_74767_n("Silent"));
         this.func_189654_d(var1.func_74767_n("NoGravity"));
         this.func_184195_f(var1.func_74767_n("Glowing"));
         if (var1.func_150297_b("Tags", 9)) {
            this.field_184236_aF.clear();
            NBTTagList var5 = var1.func_150295_c("Tags", 8);
            int var6 = Math.min(var5.size(), 1024);

            for(int var7 = 0; var7 < var6; ++var7) {
               this.field_184236_aF.add(var5.func_150307_f(var7));
            }
         }

         this.func_70037_a(var1);
         if (this.func_142008_O()) {
            this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         }

      } catch (Throwable var8) {
         CrashReport var3 = CrashReport.func_85055_a(var8, "Loading entity NBT");
         CrashReportCategory var4 = var3.func_85058_a("Entity being loaded");
         this.func_85029_a(var4);
         throw new ReportedException(var3);
      }
   }

   protected boolean func_142008_O() {
      return true;
   }

   @Nullable
   protected final String func_70022_Q() {
      EntityType var1 = this.func_200600_R();
      ResourceLocation var2 = EntityType.func_200718_a(var1);
      return var1.func_200715_a() && var2 != null ? var2.toString() : null;
   }

   protected abstract void func_70037_a(NBTTagCompound var1);

   protected abstract void func_70014_b(NBTTagCompound var1);

   protected NBTTagList func_70087_a(double... var1) {
      NBTTagList var2 = new NBTTagList();
      double[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         double var6 = var3[var5];
         var2.add((INBTBase)(new NBTTagDouble(var6)));
      }

      return var2;
   }

   protected NBTTagList func_70049_a(float... var1) {
      NBTTagList var2 = new NBTTagList();
      float[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         float var6 = var3[var5];
         var2.add((INBTBase)(new NBTTagFloat(var6)));
      }

      return var2;
   }

   @Nullable
   public EntityItem func_199703_a(IItemProvider var1) {
      return this.func_199702_a(var1, 0);
   }

   @Nullable
   public EntityItem func_199702_a(IItemProvider var1, int var2) {
      return this.func_70099_a(new ItemStack(var1), (float)var2);
   }

   @Nullable
   public EntityItem func_199701_a_(ItemStack var1) {
      return this.func_70099_a(var1, 0.0F);
   }

   @Nullable
   public EntityItem func_70099_a(ItemStack var1, float var2) {
      if (var1.func_190926_b()) {
         return null;
      } else {
         EntityItem var3 = new EntityItem(this.field_70170_p, this.field_70165_t, this.field_70163_u + (double)var2, this.field_70161_v, var1);
         var3.func_174869_p();
         this.field_70170_p.func_72838_d(var3);
         return var3;
      }
   }

   public boolean func_70089_S() {
      return !this.field_70128_L;
   }

   public boolean func_70094_T() {
      if (this.field_70145_X) {
         return false;
      } else {
         BlockPos.PooledMutableBlockPos var1 = BlockPos.PooledMutableBlockPos.func_185346_s();
         Throwable var2 = null;

         try {
            for(int var3 = 0; var3 < 8; ++var3) {
               int var4 = MathHelper.func_76128_c(this.field_70163_u + (double)(((float)((var3 >> 0) % 2) - 0.5F) * 0.1F) + (double)this.func_70047_e());
               int var5 = MathHelper.func_76128_c(this.field_70165_t + (double)(((float)((var3 >> 1) % 2) - 0.5F) * this.field_70130_N * 0.8F));
               int var6 = MathHelper.func_76128_c(this.field_70161_v + (double)(((float)((var3 >> 2) % 2) - 0.5F) * this.field_70130_N * 0.8F));
               if (var1.func_177958_n() != var5 || var1.func_177956_o() != var4 || var1.func_177952_p() != var6) {
                  var1.func_181079_c(var5, var4, var6);
                  if (this.field_70170_p.func_180495_p(var1).func_191058_s()) {
                     boolean var7 = true;
                     return var7;
                  }
               }
            }
         } catch (Throwable var17) {
            var2 = var17;
            throw var17;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var16) {
                     var2.addSuppressed(var16);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return false;
      }
   }

   public boolean func_184230_a(EntityPlayer var1, EnumHand var2) {
      return false;
   }

   @Nullable
   public AxisAlignedBB func_70114_g(Entity var1) {
      return null;
   }

   public void func_70098_U() {
      Entity var1 = this.func_184187_bx();
      if (this.func_184218_aH() && var1.field_70128_L) {
         this.func_184210_p();
      } else {
         this.field_70159_w = 0.0D;
         this.field_70181_x = 0.0D;
         this.field_70179_y = 0.0D;
         this.func_70071_h_();
         if (this.func_184218_aH()) {
            var1.func_184232_k(this);
         }
      }
   }

   public void func_184232_k(Entity var1) {
      if (this.func_184196_w(var1)) {
         var1.func_70107_b(this.field_70165_t, this.field_70163_u + this.func_70042_X() + var1.func_70033_W(), this.field_70161_v);
      }
   }

   public void func_184190_l(Entity var1) {
   }

   public double func_70033_W() {
      return 0.0D;
   }

   public double func_70042_X() {
      return (double)this.field_70131_O * 0.75D;
   }

   public boolean func_184220_m(Entity var1) {
      return this.func_184205_a(var1, false);
   }

   public boolean func_203003_aK() {
      return this instanceof EntityLivingBase;
   }

   public boolean func_184205_a(Entity var1, boolean var2) {
      for(Entity var3 = var1; var3.field_184239_as != null; var3 = var3.field_184239_as) {
         if (var3.field_184239_as == this) {
            return false;
         }
      }

      if (!var2 && (!this.func_184228_n(var1) || !var1.func_184219_q(this))) {
         return false;
      } else {
         if (this.func_184218_aH()) {
            this.func_184210_p();
         }

         this.field_184239_as = var1;
         this.field_184239_as.func_184200_o(this);
         return true;
      }
   }

   protected boolean func_184228_n(Entity var1) {
      return this.field_184245_j <= 0;
   }

   public void func_184226_ay() {
      for(int var1 = this.field_184244_h.size() - 1; var1 >= 0; --var1) {
         ((Entity)this.field_184244_h.get(var1)).func_184210_p();
      }

   }

   public void func_184210_p() {
      if (this.field_184239_as != null) {
         Entity var1 = this.field_184239_as;
         this.field_184239_as = null;
         var1.func_184225_p(this);
      }

   }

   protected void func_184200_o(Entity var1) {
      if (var1.func_184187_bx() != this) {
         throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
      } else {
         if (!this.field_70170_p.field_72995_K && var1 instanceof EntityPlayer && !(this.func_184179_bs() instanceof EntityPlayer)) {
            this.field_184244_h.add(0, var1);
         } else {
            this.field_184244_h.add(var1);
         }

      }
   }

   protected void func_184225_p(Entity var1) {
      if (var1.func_184187_bx() == this) {
         throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
      } else {
         this.field_184244_h.remove(var1);
         var1.field_184245_j = 60;
      }
   }

   protected boolean func_184219_q(Entity var1) {
      return this.func_184188_bt().size() < 1;
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.func_70107_b(var1, var3, var5);
      this.func_70101_b(var7, var8);
   }

   public void func_208000_a(float var1, int var2) {
      this.func_70034_d(var1);
   }

   public float func_70111_Y() {
      return 0.0F;
   }

   public Vec3d func_70040_Z() {
      return this.func_174806_f(this.field_70125_A, this.field_70177_z);
   }

   public Vec2f func_189653_aC() {
      return new Vec2f(this.field_70125_A, this.field_70177_z);
   }

   public Vec3d func_189651_aD() {
      return Vec3d.func_189984_a(this.func_189653_aC());
   }

   public void func_181015_d(BlockPos var1) {
      if (this.field_71088_bW > 0) {
         this.field_71088_bW = this.func_82147_ab();
      } else {
         if (!this.field_70170_p.field_72995_K && !var1.equals(this.field_181016_an)) {
            this.field_181016_an = new BlockPos(var1);
            BlockPattern.PatternHelper var2 = ((BlockPortal)Blocks.field_150427_aO).func_181089_f(this.field_70170_p, this.field_181016_an);
            double var3 = var2.func_177669_b().func_176740_k() == EnumFacing.Axis.X ? (double)var2.func_181117_a().func_177952_p() : (double)var2.func_181117_a().func_177958_n();
            double var5 = var2.func_177669_b().func_176740_k() == EnumFacing.Axis.X ? this.field_70161_v : this.field_70165_t;
            var5 = Math.abs(MathHelper.func_181160_c(var5 - (double)(var2.func_177669_b().func_176746_e().func_176743_c() == EnumFacing.AxisDirection.NEGATIVE ? 1 : 0), var3, var3 - (double)var2.func_181118_d()));
            double var7 = MathHelper.func_181160_c(this.field_70163_u - 1.0D, (double)var2.func_181117_a().func_177956_o(), (double)(var2.func_181117_a().func_177956_o() - var2.func_181119_e()));
            this.field_181017_ao = new Vec3d(var5, var7, 0.0D);
            this.field_181018_ap = var2.func_177669_b();
         }

         this.field_71087_bX = true;
      }
   }

   public int func_82147_ab() {
      return 300;
   }

   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
   }

   public void func_70103_a(byte var1) {
   }

   public void func_70057_ab() {
   }

   public Iterable<ItemStack> func_184214_aD() {
      return field_190535_b;
   }

   public Iterable<ItemStack> func_184193_aE() {
      return field_190535_b;
   }

   public Iterable<ItemStack> func_184209_aF() {
      return Iterables.concat(this.func_184214_aD(), this.func_184193_aE());
   }

   public void func_184201_a(EntityEquipmentSlot var1, ItemStack var2) {
   }

   public boolean func_70027_ad() {
      boolean var1 = this.field_70170_p != null && this.field_70170_p.field_72995_K;
      return !this.field_70178_ae && (this.field_190534_ay > 0 || var1 && this.func_70083_f(0));
   }

   public boolean func_184218_aH() {
      return this.func_184187_bx() != null;
   }

   public boolean func_184207_aI() {
      return !this.func_184188_bt().isEmpty();
   }

   public boolean func_205710_ba() {
      return true;
   }

   public boolean func_70093_af() {
      return this.func_70083_f(1);
   }

   public void func_70095_a(boolean var1) {
      this.func_70052_a(1, var1);
   }

   public boolean func_70051_ag() {
      return this.func_70083_f(3);
   }

   public void func_70031_b(boolean var1) {
      this.func_70052_a(3, var1);
   }

   public boolean func_203007_ba() {
      return this.func_70083_f(4);
   }

   public void func_204711_a(boolean var1) {
      this.func_70052_a(4, var1);
   }

   public boolean func_184202_aL() {
      return this.field_184238_ar || this.field_70170_p.field_72995_K && this.func_70083_f(6);
   }

   public void func_184195_f(boolean var1) {
      this.field_184238_ar = var1;
      if (!this.field_70170_p.field_72995_K) {
         this.func_70052_a(6, this.field_184238_ar);
      }

   }

   public boolean func_82150_aj() {
      return this.func_70083_f(5);
   }

   public boolean func_98034_c(EntityPlayer var1) {
      if (var1.func_175149_v()) {
         return false;
      } else {
         Team var2 = this.func_96124_cp();
         return var2 != null && var1 != null && var1.func_96124_cp() == var2 && var2.func_98297_h() ? false : this.func_82150_aj();
      }
   }

   @Nullable
   public Team func_96124_cp() {
      return this.field_70170_p.func_96441_U().func_96509_i(this.func_195047_I_());
   }

   public boolean func_184191_r(Entity var1) {
      return this.func_184194_a(var1.func_96124_cp());
   }

   public boolean func_184194_a(Team var1) {
      return this.func_96124_cp() != null ? this.func_96124_cp().func_142054_a(var1) : false;
   }

   public void func_82142_c(boolean var1) {
      this.func_70052_a(5, var1);
   }

   protected boolean func_70083_f(int var1) {
      return ((Byte)this.field_70180_af.func_187225_a(field_184240_ax) & 1 << var1) != 0;
   }

   protected void func_70052_a(int var1, boolean var2) {
      byte var3 = (Byte)this.field_70180_af.func_187225_a(field_184240_ax);
      if (var2) {
         this.field_70180_af.func_187227_b(field_184240_ax, (byte)(var3 | 1 << var1));
      } else {
         this.field_70180_af.func_187227_b(field_184240_ax, (byte)(var3 & ~(1 << var1)));
      }

   }

   public int func_205010_bg() {
      return 300;
   }

   public int func_70086_ai() {
      return (Integer)this.field_70180_af.func_187225_a(field_184241_ay);
   }

   public void func_70050_g(int var1) {
      this.field_70180_af.func_187227_b(field_184241_ay, var1);
   }

   public void func_70077_a(EntityLightningBolt var1) {
      ++this.field_190534_ay;
      if (this.field_190534_ay == 0) {
         this.func_70015_d(8);
      }

      this.func_70097_a(DamageSource.field_180137_b, 5.0F);
   }

   public void func_203002_i(boolean var1) {
      if (var1) {
         this.field_70181_x = Math.max(-0.9D, this.field_70181_x - 0.03D);
      } else {
         this.field_70181_x = Math.min(1.8D, this.field_70181_x + 0.1D);
      }

   }

   public void func_203004_j(boolean var1) {
      if (var1) {
         this.field_70181_x = Math.max(-0.3D, this.field_70181_x - 0.03D);
      } else {
         this.field_70181_x = Math.min(0.7D, this.field_70181_x + 0.06D);
      }

      this.field_70143_R = 0.0F;
   }

   public void func_70074_a(EntityLivingBase var1) {
   }

   protected boolean func_145771_j(double var1, double var3, double var5) {
      BlockPos var7 = new BlockPos(var1, var3, var5);
      double var8 = var1 - (double)var7.func_177958_n();
      double var10 = var3 - (double)var7.func_177956_o();
      double var12 = var5 - (double)var7.func_177952_p();
      if (this.field_70170_p.func_195586_b((Entity)null, this.func_174813_aQ())) {
         return false;
      } else {
         EnumFacing var14 = EnumFacing.UP;
         double var15 = 1.7976931348623157E308D;
         if (!this.field_70170_p.func_175665_u(var7.func_177976_e()) && var8 < var15) {
            var15 = var8;
            var14 = EnumFacing.WEST;
         }

         if (!this.field_70170_p.func_175665_u(var7.func_177974_f()) && 1.0D - var8 < var15) {
            var15 = 1.0D - var8;
            var14 = EnumFacing.EAST;
         }

         if (!this.field_70170_p.func_175665_u(var7.func_177978_c()) && var12 < var15) {
            var15 = var12;
            var14 = EnumFacing.NORTH;
         }

         if (!this.field_70170_p.func_175665_u(var7.func_177968_d()) && 1.0D - var12 < var15) {
            var15 = 1.0D - var12;
            var14 = EnumFacing.SOUTH;
         }

         if (!this.field_70170_p.func_175665_u(var7.func_177984_a()) && 1.0D - var10 < var15) {
            var15 = 1.0D - var10;
            var14 = EnumFacing.UP;
         }

         float var17 = this.field_70146_Z.nextFloat() * 0.2F + 0.1F;
         float var18 = (float)var14.func_176743_c().func_179524_a();
         if (var14.func_176740_k() == EnumFacing.Axis.X) {
            this.field_70159_w = (double)(var18 * var17);
            this.field_70181_x *= 0.75D;
            this.field_70179_y *= 0.75D;
         } else if (var14.func_176740_k() == EnumFacing.Axis.Y) {
            this.field_70159_w *= 0.75D;
            this.field_70181_x = (double)(var18 * var17);
            this.field_70179_y *= 0.75D;
         } else if (var14.func_176740_k() == EnumFacing.Axis.Z) {
            this.field_70159_w *= 0.75D;
            this.field_70181_x *= 0.75D;
            this.field_70179_y = (double)(var18 * var17);
         }

         return true;
      }
   }

   public void func_70110_aj() {
      this.field_70134_J = true;
      this.field_70143_R = 0.0F;
   }

   private static void func_207712_c(ITextComponent var0) {
      var0.func_211710_a((var0x) -> {
         var0x.func_150241_a((ClickEvent)null);
      }).func_150253_a().forEach(Entity::func_207712_c);
   }

   public ITextComponent func_200200_C_() {
      ITextComponent var1 = this.func_200201_e();
      if (var1 != null) {
         ITextComponent var2 = var1.func_212638_h();
         func_207712_c(var2);
         return var2;
      } else {
         return this.field_200606_g.func_212546_e();
      }
   }

   @Nullable
   public Entity[] func_70021_al() {
      return null;
   }

   public boolean func_70028_i(Entity var1) {
      return this == var1;
   }

   public float func_70079_am() {
      return 0.0F;
   }

   public void func_70034_d(float var1) {
   }

   public void func_181013_g(float var1) {
   }

   public boolean func_70075_an() {
      return true;
   }

   public boolean func_85031_j(Entity var1) {
      return false;
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.func_200200_C_().func_150261_e(), this.field_145783_c, this.field_70170_p == null ? "~NULL~" : this.field_70170_p.func_72912_H().func_76065_j(), this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public boolean func_180431_b(DamageSource var1) {
      return this.field_83001_bt && var1 != DamageSource.field_76380_i && !var1.func_180136_u();
   }

   public boolean func_190530_aW() {
      return this.field_83001_bt;
   }

   public void func_184224_h(boolean var1) {
      this.field_83001_bt = var1;
   }

   public void func_82149_j(Entity var1) {
      this.func_70012_b(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var1.field_70177_z, var1.field_70125_A);
   }

   public void func_180432_n(Entity var1) {
      NBTTagCompound var2 = var1.func_189511_e(new NBTTagCompound());
      var2.func_82580_o("Dimension");
      this.func_70020_e(var2);
      this.field_71088_bW = var1.field_71088_bW;
      this.field_181016_an = var1.field_181016_an;
      this.field_181017_ao = var1.field_181017_ao;
      this.field_181018_ap = var1.field_181018_ap;
   }

   @Nullable
   public Entity func_212321_a(DimensionType var1) {
      if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
         this.field_70170_p.field_72984_F.func_76320_a("changeDimension");
         MinecraftServer var2 = this.func_184102_h();
         DimensionType var3 = this.field_71093_bK;
         WorldServer var4 = var2.func_71218_a(var3);
         WorldServer var5 = var2.func_71218_a(var1);
         this.field_71093_bK = var1;
         if (var3 == DimensionType.THE_END && var1 == DimensionType.THE_END) {
            var5 = var2.func_71218_a(DimensionType.OVERWORLD);
            this.field_71093_bK = DimensionType.OVERWORLD;
         }

         this.field_70170_p.func_72900_e(this);
         this.field_70128_L = false;
         this.field_70170_p.field_72984_F.func_76320_a("reposition");
         BlockPos var6;
         if (var1 == DimensionType.THE_END) {
            var6 = var5.func_180504_m();
         } else {
            double var7 = this.field_70165_t;
            double var9 = this.field_70161_v;
            double var11 = 8.0D;
            if (var1 == DimensionType.NETHER) {
               var7 = MathHelper.func_151237_a(var7 / 8.0D, var5.func_175723_af().func_177726_b() + 16.0D, var5.func_175723_af().func_177728_d() - 16.0D);
               var9 = MathHelper.func_151237_a(var9 / 8.0D, var5.func_175723_af().func_177736_c() + 16.0D, var5.func_175723_af().func_177733_e() - 16.0D);
            } else if (var1 == DimensionType.OVERWORLD) {
               var7 = MathHelper.func_151237_a(var7 * 8.0D, var5.func_175723_af().func_177726_b() + 16.0D, var5.func_175723_af().func_177728_d() - 16.0D);
               var9 = MathHelper.func_151237_a(var9 * 8.0D, var5.func_175723_af().func_177736_c() + 16.0D, var5.func_175723_af().func_177733_e() - 16.0D);
            }

            var7 = (double)MathHelper.func_76125_a((int)var7, -29999872, 29999872);
            var9 = (double)MathHelper.func_76125_a((int)var9, -29999872, 29999872);
            float var13 = this.field_70177_z;
            this.func_70012_b(var7, this.field_70163_u, var9, 90.0F, 0.0F);
            Teleporter var14 = var5.func_85176_s();
            var14.func_180620_b(this, var13);
            var6 = new BlockPos(this);
         }

         var4.func_72866_a(this, false);
         this.field_70170_p.field_72984_F.func_76318_c("reloading");
         Entity var16 = this.func_200600_R().func_200721_a(var5);
         if (var16 != null) {
            var16.func_180432_n(this);
            if (var3 == DimensionType.THE_END && var1 == DimensionType.THE_END) {
               BlockPos var8 = var5.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, var5.func_175694_M());
               var16.func_174828_a(var8, var16.field_70177_z, var16.field_70125_A);
            } else {
               var16.func_174828_a(var6, var16.field_70177_z, var16.field_70125_A);
            }

            boolean var15 = var16.field_98038_p;
            var16.field_98038_p = true;
            var5.func_72838_d(var16);
            var16.field_98038_p = var15;
            var5.func_72866_a(var16, false);
         }

         this.field_70128_L = true;
         this.field_70170_p.field_72984_F.func_76319_b();
         var4.func_82742_i();
         var5.func_82742_i();
         this.field_70170_p.field_72984_F.func_76319_b();
         return var16;
      } else {
         return null;
      }
   }

   public boolean func_184222_aU() {
      return true;
   }

   public float func_180428_a(Explosion var1, IBlockReader var2, BlockPos var3, IBlockState var4, IFluidState var5, float var6) {
      return var6;
   }

   public boolean func_174816_a(Explosion var1, IBlockReader var2, BlockPos var3, IBlockState var4, float var5) {
      return true;
   }

   public int func_82143_as() {
      return 3;
   }

   public Vec3d func_181014_aG() {
      return this.field_181017_ao;
   }

   public EnumFacing func_181012_aH() {
      return this.field_181018_ap;
   }

   public boolean func_145773_az() {
      return false;
   }

   public void func_85029_a(CrashReportCategory var1) {
      var1.func_189529_a("Entity Type", () -> {
         return EntityType.func_200718_a(this.func_200600_R()) + " (" + this.getClass().getCanonicalName() + ")";
      });
      var1.func_71507_a("Entity ID", this.field_145783_c);
      var1.func_189529_a("Entity Name", () -> {
         return this.func_200200_C_().getString();
      });
      var1.func_71507_a("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.field_70165_t, this.field_70163_u, this.field_70161_v));
      var1.func_71507_a("Entity's Block location", CrashReportCategory.func_184876_a(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)));
      var1.func_71507_a("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.field_70159_w, this.field_70181_x, this.field_70179_y));
      var1.func_189529_a("Entity's Passengers", () -> {
         return this.func_184188_bt().toString();
      });
      var1.func_189529_a("Entity's Vehicle", () -> {
         return this.func_184187_bx().toString();
      });
   }

   public boolean func_90999_ad() {
      return this.func_70027_ad();
   }

   public void func_184221_a(UUID var1) {
      this.field_96093_i = var1;
      this.field_189513_ar = this.field_96093_i.toString();
   }

   public UUID func_110124_au() {
      return this.field_96093_i;
   }

   public String func_189512_bd() {
      return this.field_189513_ar;
   }

   public String func_195047_I_() {
      return this.field_189513_ar;
   }

   public boolean func_96092_aw() {
      return true;
   }

   public static double func_184183_bd() {
      return field_70155_l;
   }

   public static void func_184227_b(double var0) {
      field_70155_l = var0;
   }

   public ITextComponent func_145748_c_() {
      return ScorePlayerTeam.func_200541_a(this.func_96124_cp(), this.func_200200_C_()).func_211710_a((var1) -> {
         var1.func_150209_a(this.func_174823_aP()).func_179989_a(this.func_189512_bd());
      });
   }

   public void func_200203_b(@Nullable ITextComponent var1) {
      this.field_70180_af.func_187227_b(field_184242_az, Optional.ofNullable(var1));
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return (ITextComponent)((Optional)this.field_70180_af.func_187225_a(field_184242_az)).orElse((Object)null);
   }

   public boolean func_145818_k_() {
      return ((Optional)this.field_70180_af.func_187225_a(field_184242_az)).isPresent();
   }

   public void func_174805_g(boolean var1) {
      this.field_70180_af.func_187227_b(field_184233_aA, var1);
   }

   public boolean func_174833_aM() {
      return (Boolean)this.field_70180_af.func_187225_a(field_184233_aA);
   }

   public void func_70634_a(double var1, double var3, double var5) {
      this.field_184237_aG = true;
      this.func_70012_b(var1, var3, var5, this.field_70177_z, this.field_70125_A);
      this.field_70170_p.func_72866_a(this, false);
   }

   public boolean func_94059_bO() {
      return this.func_174833_aM();
   }

   public void func_184206_a(DataParameter<?> var1) {
   }

   public EnumFacing func_174811_aO() {
      return EnumFacing.func_176733_a((double)this.field_70177_z);
   }

   public EnumFacing func_184172_bi() {
      return this.func_174811_aO();
   }

   protected HoverEvent func_174823_aP() {
      NBTTagCompound var1 = new NBTTagCompound();
      ResourceLocation var2 = EntityType.func_200718_a(this.func_200600_R());
      var1.func_74778_a("id", this.func_189512_bd());
      if (var2 != null) {
         var1.func_74778_a("type", var2.toString());
      }

      var1.func_74778_a("name", ITextComponent.Serializer.func_150696_a(this.func_200200_C_()));
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new TextComponentString(var1.toString()));
   }

   public boolean func_174827_a(EntityPlayerMP var1) {
      return true;
   }

   public AxisAlignedBB func_174813_aQ() {
      return this.field_70121_D;
   }

   public AxisAlignedBB func_184177_bl() {
      return this.func_174813_aQ();
   }

   public void func_174826_a(AxisAlignedBB var1) {
      this.field_70121_D = var1;
   }

   public float func_70047_e() {
      return this.field_70131_O * 0.85F;
   }

   public boolean func_174832_aS() {
      return this.field_174835_g;
   }

   public void func_174821_h(boolean var1) {
      this.field_174835_g = var1;
   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      return false;
   }

   public void func_145747_a(ITextComponent var1) {
   }

   public BlockPos func_180425_c() {
      return new BlockPos(this);
   }

   public Vec3d func_174791_d() {
      return new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public World func_130014_f_() {
      return this.field_70170_p;
   }

   @Nullable
   public MinecraftServer func_184102_h() {
      return this.field_70170_p.func_73046_m();
   }

   public EnumActionResult func_184199_a(EntityPlayer var1, Vec3d var2, EnumHand var3) {
      return EnumActionResult.PASS;
   }

   public boolean func_180427_aV() {
      return false;
   }

   protected void func_174815_a(EntityLivingBase var1, Entity var2) {
      if (var2 instanceof EntityLivingBase) {
         EnchantmentHelper.func_151384_a((EntityLivingBase)var2, var1);
      }

      EnchantmentHelper.func_151385_b(var1, var2);
   }

   public void func_184178_b(EntityPlayerMP var1) {
   }

   public void func_184203_c(EntityPlayerMP var1) {
   }

   public float func_184229_a(Rotation var1) {
      float var2 = MathHelper.func_76142_g(this.field_70177_z);
      switch(var1) {
      case CLOCKWISE_180:
         return var2 + 180.0F;
      case COUNTERCLOCKWISE_90:
         return var2 + 270.0F;
      case CLOCKWISE_90:
         return var2 + 90.0F;
      default:
         return var2;
      }
   }

   public float func_184217_a(Mirror var1) {
      float var2 = MathHelper.func_76142_g(this.field_70177_z);
      switch(var1) {
      case LEFT_RIGHT:
         return -var2;
      case FRONT_BACK:
         return 180.0F - var2;
      default:
         return var2;
      }
   }

   public boolean func_184213_bq() {
      return false;
   }

   public boolean func_184189_br() {
      boolean var1 = this.field_184237_aG;
      this.field_184237_aG = false;
      return var1;
   }

   @Nullable
   public Entity func_184179_bs() {
      return null;
   }

   public List<Entity> func_184188_bt() {
      return (List)(this.field_184244_h.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.field_184244_h));
   }

   public boolean func_184196_w(Entity var1) {
      Iterator var2 = this.func_184188_bt().iterator();

      Entity var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Entity)var2.next();
      } while(!var3.equals(var1));

      return true;
   }

   public boolean func_205708_a(Class<? extends Entity> var1) {
      Iterator var2 = this.func_184188_bt().iterator();

      Entity var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Entity)var2.next();
      } while(!var1.isAssignableFrom(var3.getClass()));

      return true;
   }

   public Collection<Entity> func_184182_bu() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.func_184188_bt().iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         var1.add(var3);
         var3.func_200604_a(false, var1);
      }

      return var1;
   }

   public boolean func_200601_bK() {
      HashSet var1 = Sets.newHashSet();
      this.func_200604_a(true, var1);
      return var1.size() == 1;
   }

   private void func_200604_a(boolean var1, Set<Entity> var2) {
      Entity var4;
      for(Iterator var3 = this.func_184188_bt().iterator(); var3.hasNext(); var4.func_200604_a(var1, var2)) {
         var4 = (Entity)var3.next();
         if (!var1 || EntityPlayerMP.class.isAssignableFrom(var4.getClass())) {
            var2.add(var4);
         }
      }

   }

   public Entity func_184208_bv() {
      Entity var1;
      for(var1 = this; var1.func_184218_aH(); var1 = var1.func_184187_bx()) {
      }

      return var1;
   }

   public boolean func_184223_x(Entity var1) {
      return this.func_184208_bv() == var1.func_184208_bv();
   }

   public boolean func_184215_y(Entity var1) {
      Iterator var2 = this.func_184188_bt().iterator();

      Entity var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Entity)var2.next();
         if (var3.equals(var1)) {
            return true;
         }
      } while(!var3.func_184215_y(var1));

      return true;
   }

   public boolean func_184186_bw() {
      Entity var1 = this.func_184179_bs();
      if (var1 instanceof EntityPlayer) {
         return ((EntityPlayer)var1).func_175144_cb();
      } else {
         return !this.field_70170_p.field_72995_K;
      }
   }

   @Nullable
   public Entity func_184187_bx() {
      return this.field_184239_as;
   }

   public EnumPushReaction func_184192_z() {
      return EnumPushReaction.NORMAL;
   }

   public SoundCategory func_184176_by() {
      return SoundCategory.NEUTRAL;
   }

   protected int func_190531_bD() {
      return 1;
   }

   public CommandSource func_195051_bN() {
      return new CommandSource(this, new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v), this.func_189653_aC(), this.field_70170_p instanceof WorldServer ? (WorldServer)this.field_70170_p : null, this.func_184840_I(), this.func_200200_C_().getString(), this.func_145748_c_(), this.field_70170_p.func_73046_m(), this);
   }

   protected int func_184840_I() {
      return 0;
   }

   public boolean func_211513_k(int var1) {
      return this.func_184840_I() >= var1;
   }

   public boolean func_195039_a() {
      return this.field_70170_p.func_82736_K().func_82766_b("sendCommandFeedback");
   }

   public boolean func_195040_b() {
      return true;
   }

   public boolean func_195041_r_() {
      return true;
   }

   public void func_200602_a(EntityAnchorArgument.Type var1, Vec3d var2) {
      Vec3d var3 = var1.func_201017_a(this);
      double var4 = var2.field_72450_a - var3.field_72450_a;
      double var6 = var2.field_72448_b - var3.field_72448_b;
      double var8 = var2.field_72449_c - var3.field_72449_c;
      double var10 = (double)MathHelper.func_76133_a(var4 * var4 + var8 * var8);
      this.field_70125_A = MathHelper.func_76142_g((float)(-(MathHelper.func_181159_b(var6, var10) * 57.2957763671875D)));
      this.field_70177_z = MathHelper.func_76142_g((float)(MathHelper.func_181159_b(var8, var4) * 57.2957763671875D) - 90.0F);
      this.func_70034_d(this.field_70177_z);
      this.field_70127_C = this.field_70125_A;
      this.field_70126_B = this.field_70177_z;
   }

   public boolean func_210500_b(Tag<Fluid> var1) {
      AxisAlignedBB var2 = this.func_174813_aQ().func_186664_h(0.001D);
      int var3 = MathHelper.func_76128_c(var2.field_72340_a);
      int var4 = MathHelper.func_76143_f(var2.field_72336_d);
      int var5 = MathHelper.func_76128_c(var2.field_72338_b);
      int var6 = MathHelper.func_76143_f(var2.field_72337_e);
      int var7 = MathHelper.func_76128_c(var2.field_72339_c);
      int var8 = MathHelper.func_76143_f(var2.field_72334_f);
      if (!this.field_70170_p.func_175663_a(var3, var5, var7, var4, var6, var8, true)) {
         return false;
      } else {
         double var9 = 0.0D;
         boolean var11 = this.func_96092_aw();
         boolean var12 = false;
         Vec3d var13 = Vec3d.field_186680_a;
         int var14 = 0;
         BlockPos.PooledMutableBlockPos var15 = BlockPos.PooledMutableBlockPos.func_185346_s();
         Throwable var16 = null;

         try {
            for(int var17 = var3; var17 < var4; ++var17) {
               for(int var18 = var5; var18 < var6; ++var18) {
                  for(int var19 = var7; var19 < var8; ++var19) {
                     var15.func_181079_c(var17, var18, var19);
                     IFluidState var20 = this.field_70170_p.func_204610_c(var15);
                     if (var20.func_206884_a(var1)) {
                        double var21 = (double)((float)var18 + var20.func_206885_f());
                        if (var21 >= var2.field_72338_b) {
                           var12 = true;
                           var9 = Math.max(var21 - var2.field_72338_b, var9);
                           if (var11) {
                              Vec3d var23 = var20.func_206887_a(this.field_70170_p, var15);
                              if (var9 < 0.4D) {
                                 var23 = var23.func_186678_a(var9);
                              }

                              var13 = var13.func_178787_e(var23);
                              ++var14;
                           }
                        }
                     }
                  }
               }
            }
         } catch (Throwable var31) {
            var16 = var31;
            throw var31;
         } finally {
            if (var15 != null) {
               if (var16 != null) {
                  try {
                     var15.close();
                  } catch (Throwable var30) {
                     var16.addSuppressed(var30);
                  }
               } else {
                  var15.close();
               }
            }

         }

         if (var13.func_72433_c() > 0.0D) {
            if (var14 > 0) {
               var13 = var13.func_186678_a(1.0D / (double)var14);
            }

            if (!(this instanceof EntityPlayer)) {
               var13 = var13.func_72432_b();
            }

            double var33 = 0.014D;
            this.field_70159_w += var13.field_72450_a * 0.014D;
            this.field_70181_x += var13.field_72448_b * 0.014D;
            this.field_70179_y += var13.field_72449_c * 0.014D;
         }

         this.field_211517_W = var9;
         return var12;
      }
   }

   public double func_212107_bY() {
      return this.field_211517_W;
   }

   static {
      field_184240_ax = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_187191_a);
      field_184241_ay = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_187192_b);
      field_184242_az = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_200544_f);
      field_184233_aA = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_187198_h);
      field_184234_aB = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_187198_h);
      field_189655_aD = EntityDataManager.func_187226_a(Entity.class, DataSerializers.field_187198_h);
   }
}
