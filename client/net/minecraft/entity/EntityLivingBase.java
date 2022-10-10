package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.PotionUtils;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.CombatRules;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EntityLivingBase extends Entity {
   private static final Logger field_190632_a = LogManager.getLogger();
   private static final UUID field_110156_b = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
   private static final AttributeModifier field_110157_c;
   protected static final DataParameter<Byte> field_184621_as;
   private static final DataParameter<Float> field_184632_c;
   private static final DataParameter<Integer> field_184633_f;
   private static final DataParameter<Boolean> field_184634_g;
   private static final DataParameter<Integer> field_184635_h;
   private AbstractAttributeMap field_110155_d;
   private final CombatTracker field_94063_bt = new CombatTracker(this);
   private final Map<Potion, PotionEffect> field_70713_bf = Maps.newHashMap();
   private final NonNullList<ItemStack> field_184630_bs;
   private final NonNullList<ItemStack> field_184631_bt;
   public boolean field_82175_bq;
   public EnumHand field_184622_au;
   public int field_110158_av;
   public int field_70720_be;
   public int field_70737_aN;
   public int field_70738_aO;
   public float field_70739_aP;
   public int field_70725_aQ;
   public float field_70732_aI;
   public float field_70733_aJ;
   protected int field_184617_aD;
   public float field_184618_aE;
   public float field_70721_aZ;
   public float field_184619_aG;
   public int field_70771_an;
   public float field_70727_aS;
   public float field_70726_aT;
   public float field_70769_ao;
   public float field_70770_ap;
   public float field_70761_aq;
   public float field_70760_ar;
   public float field_70759_as;
   public float field_70758_at;
   public float field_70747_aH;
   protected EntityPlayer field_70717_bb;
   protected int field_70718_bc;
   protected boolean field_70729_aU;
   protected int field_70708_bq;
   protected float field_70768_au;
   protected float field_110154_aX;
   protected float field_70764_aw;
   protected float field_70763_ax;
   protected float field_70741_aB;
   protected int field_70744_aE;
   protected float field_110153_bc;
   protected boolean field_70703_bu;
   public float field_70702_br;
   public float field_70701_bs;
   public float field_191988_bg;
   public float field_70704_bt;
   protected int field_70716_bi;
   protected double field_184623_bh;
   protected double field_184624_bi;
   protected double field_184625_bj;
   protected double field_184626_bk;
   protected double field_70709_bj;
   protected double field_208001_bq;
   protected int field_208002_br;
   private boolean field_70752_e;
   private EntityLivingBase field_70755_b;
   private int field_70756_c;
   private EntityLivingBase field_110150_bn;
   private int field_142016_bo;
   private float field_70746_aG;
   private int field_70773_bE;
   private float field_110151_bq;
   protected ItemStack field_184627_bm;
   protected int field_184628_bn;
   protected int field_184629_bo;
   private BlockPos field_184620_bC;
   private DamageSource field_189750_bF;
   private long field_189751_bG;
   protected int field_204807_bs;
   private float field_205017_bL;
   private float field_205018_bM;

   protected EntityLivingBase(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_184630_bs = NonNullList.func_191197_a(2, ItemStack.field_190927_a);
      this.field_184631_bt = NonNullList.func_191197_a(4, ItemStack.field_190927_a);
      this.field_70771_an = 20;
      this.field_70747_aH = 0.02F;
      this.field_70752_e = true;
      this.field_184627_bm = ItemStack.field_190927_a;
      this.func_110147_ax();
      this.func_70606_j(this.func_110138_aP());
      this.field_70156_m = true;
      this.field_70770_ap = (float)((Math.random() + 1.0D) * 0.009999999776482582D);
      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.field_70769_ao = (float)Math.random() * 12398.0F;
      this.field_70177_z = (float)(Math.random() * 6.2831854820251465D);
      this.field_70759_as = this.field_70177_z;
      this.field_70138_W = 0.6F;
   }

   public void func_174812_G() {
      this.func_70097_a(DamageSource.field_76380_i, 3.4028235E38F);
   }

   protected void func_70088_a() {
      this.field_70180_af.func_187214_a(field_184621_as, (byte)0);
      this.field_70180_af.func_187214_a(field_184633_f, 0);
      this.field_70180_af.func_187214_a(field_184634_g, false);
      this.field_70180_af.func_187214_a(field_184635_h, 0);
      this.field_70180_af.func_187214_a(field_184632_c, 1.0F);
   }

   protected void func_110147_ax() {
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111267_a);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111266_c);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111263_d);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_188791_g);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_189429_h);
   }

   protected void func_184231_a(double var1, boolean var3, IBlockState var4, BlockPos var5) {
      if (!this.func_70090_H()) {
         this.func_70072_I();
      }

      if (!this.field_70170_p.field_72995_K && this.field_70143_R > 3.0F && var3) {
         float var6 = (float)MathHelper.func_76123_f(this.field_70143_R - 3.0F);
         if (!var4.func_196958_f()) {
            double var7 = Math.min((double)(0.2F + var6 / 15.0F), 2.5D);
            int var9 = (int)(150.0D * var7);
            ((WorldServer)this.field_70170_p).func_195598_a(new BlockParticleData(Particles.field_197611_d, var4), this.field_70165_t, this.field_70163_u, this.field_70161_v, var9, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
         }
      }

      super.func_184231_a(var1, var3, var4, var5);
   }

   public boolean func_70648_aU() {
      return this.func_70668_bt() == CreatureAttribute.UNDEAD;
   }

   public float func_205015_b(float var1) {
      return this.func_205016_b(this.field_205018_bM, this.field_205017_bL, var1);
   }

   protected float func_205016_b(float var1, float var2, float var3) {
      return var1 + (var2 - var1) * var3;
   }

   public void func_70030_z() {
      this.field_70732_aI = this.field_70733_aJ;
      super.func_70030_z();
      this.field_70170_p.field_72984_F.func_76320_a("livingEntityBaseTick");
      boolean var1 = this instanceof EntityPlayer;
      if (this.func_70089_S()) {
         if (this.func_70094_T()) {
            this.func_70097_a(DamageSource.field_76368_d, 1.0F);
         } else if (var1 && !this.field_70170_p.func_175723_af().func_177743_a(this.func_174813_aQ())) {
            double var2 = this.field_70170_p.func_175723_af().func_177745_a(this) + this.field_70170_p.func_175723_af().func_177742_m();
            if (var2 < 0.0D) {
               double var4 = this.field_70170_p.func_175723_af().func_177727_n();
               if (var4 > 0.0D) {
                  this.func_70097_a(DamageSource.field_76368_d, (float)Math.max(1, MathHelper.func_76128_c(-var2 * var4)));
               }
            }
         }
      }

      if (this.func_70045_F() || this.field_70170_p.field_72995_K) {
         this.func_70066_B();
      }

      boolean var7 = var1 && ((EntityPlayer)this).field_71075_bZ.field_75102_a;
      if (this.func_70089_S()) {
         if (this.func_208600_a(FluidTags.field_206959_a) && this.field_70170_p.func_180495_p(new BlockPos(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v)).func_177230_c() != Blocks.field_203203_C) {
            if (!this.func_70648_aU() && !PotionUtil.func_205133_c(this) && !var7) {
               this.func_70050_g(this.func_70682_h(this.func_70086_ai()));
               if (this.func_70086_ai() == -20) {
                  this.func_70050_g(0);

                  for(int var3 = 0; var3 < 8; ++var3) {
                     float var9 = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();
                     float var5 = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();
                     float var6 = this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat();
                     this.field_70170_p.func_195594_a(Particles.field_197612_e, this.field_70165_t + (double)var9, this.field_70163_u + (double)var5, this.field_70161_v + (double)var6, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                  }

                  this.func_70097_a(DamageSource.field_76369_e, 2.0F);
               }
            }

            if (!this.field_70170_p.field_72995_K && this.func_184218_aH() && this.func_184187_bx() != null && !this.func_184187_bx().func_205710_ba()) {
               this.func_184210_p();
            }
         } else if (this.func_70086_ai() < this.func_205010_bg()) {
            this.func_70050_g(this.func_207300_l(this.func_70086_ai()));
         }

         if (!this.field_70170_p.field_72995_K) {
            BlockPos var8 = new BlockPos(this);
            if (!Objects.equal(this.field_184620_bC, var8)) {
               this.field_184620_bC = var8;
               this.func_184594_b(var8);
            }
         }
      }

      if (this.func_70089_S() && this.func_203008_ap()) {
         this.func_70066_B();
      }

      this.field_70727_aS = this.field_70726_aT;
      if (this.field_70737_aN > 0) {
         --this.field_70737_aN;
      }

      if (this.field_70172_ad > 0 && !(this instanceof EntityPlayerMP)) {
         --this.field_70172_ad;
      }

      if (this.func_110143_aJ() <= 0.0F) {
         this.func_70609_aI();
      }

      if (this.field_70718_bc > 0) {
         --this.field_70718_bc;
      } else {
         this.field_70717_bb = null;
      }

      if (this.field_110150_bn != null && !this.field_110150_bn.func_70089_S()) {
         this.field_110150_bn = null;
      }

      if (this.field_70755_b != null) {
         if (!this.field_70755_b.func_70089_S()) {
            this.func_70604_c((EntityLivingBase)null);
         } else if (this.field_70173_aa - this.field_70756_c > 100) {
            this.func_70604_c((EntityLivingBase)null);
         }
      }

      this.func_70679_bo();
      this.field_70763_ax = this.field_70764_aw;
      this.field_70760_ar = this.field_70761_aq;
      this.field_70758_at = this.field_70759_as;
      this.field_70126_B = this.field_70177_z;
      this.field_70127_C = this.field_70125_A;
      this.field_70170_p.field_72984_F.func_76319_b();
   }

   protected void func_184594_b(BlockPos var1) {
      int var2 = EnchantmentHelper.func_185284_a(Enchantments.field_185301_j, this);
      if (var2 > 0) {
         EnchantmentFrostWalker.func_185266_a(this, this.field_70170_p, var1, var2);
      }

   }

   public boolean func_70631_g_() {
      return false;
   }

   public boolean func_205710_ba() {
      return false;
   }

   protected void func_70609_aI() {
      ++this.field_70725_aQ;
      if (this.field_70725_aQ == 20) {
         int var1;
         if (!this.field_70170_p.field_72995_K && (this.func_70684_aJ() || this.field_70718_bc > 0 && this.func_146066_aG() && this.field_70170_p.func_82736_K().func_82766_b("doMobLoot"))) {
            var1 = this.func_70693_a(this.field_70717_bb);

            while(var1 > 0) {
               int var2 = EntityXPOrb.func_70527_a(var1);
               var1 -= var2;
               this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, var2));
            }
         }

         this.func_70106_y();

         for(var1 = 0; var1 < 20; ++var1) {
            double var8 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var4 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var6 = this.field_70146_Z.nextGaussian() * 0.02D;
            this.field_70170_p.func_195594_a(Particles.field_197598_I, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var8, var4, var6);
         }
      }

   }

   protected boolean func_146066_aG() {
      return !this.func_70631_g_();
   }

   protected int func_70682_h(int var1) {
      int var2 = EnchantmentHelper.func_185292_c(this);
      return var2 > 0 && this.field_70146_Z.nextInt(var2 + 1) > 0 ? var1 : var1 - 1;
   }

   protected int func_207300_l(int var1) {
      return Math.min(var1 + 4, this.func_205010_bg());
   }

   protected int func_70693_a(EntityPlayer var1) {
      return 0;
   }

   protected boolean func_70684_aJ() {
      return false;
   }

   public Random func_70681_au() {
      return this.field_70146_Z;
   }

   @Nullable
   public EntityLivingBase func_70643_av() {
      return this.field_70755_b;
   }

   public int func_142015_aE() {
      return this.field_70756_c;
   }

   public void func_70604_c(@Nullable EntityLivingBase var1) {
      this.field_70755_b = var1;
      this.field_70756_c = this.field_70173_aa;
   }

   public EntityLivingBase func_110144_aD() {
      return this.field_110150_bn;
   }

   public int func_142013_aG() {
      return this.field_142016_bo;
   }

   public void func_130011_c(Entity var1) {
      if (var1 instanceof EntityLivingBase) {
         this.field_110150_bn = (EntityLivingBase)var1;
      } else {
         this.field_110150_bn = null;
      }

      this.field_142016_bo = this.field_70173_aa;
   }

   public int func_70654_ax() {
      return this.field_70708_bq;
   }

   protected void func_184606_a_(ItemStack var1) {
      if (!var1.func_190926_b()) {
         SoundEvent var2 = SoundEvents.field_187719_p;
         Item var3 = var1.func_77973_b();
         if (var3 instanceof ItemArmor) {
            var2 = ((ItemArmor)var3).func_200880_d().func_200899_b();
         } else if (var3 == Items.field_185160_cR) {
            var2 = SoundEvents.field_191258_p;
         }

         this.func_184185_a(var2, 1.0F, 1.0F);
      }
   }

   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74776_a("Health", this.func_110143_aJ());
      var1.func_74777_a("HurtTime", (short)this.field_70737_aN);
      var1.func_74768_a("HurtByTimestamp", this.field_70756_c);
      var1.func_74777_a("DeathTime", (short)this.field_70725_aQ);
      var1.func_74776_a("AbsorptionAmount", this.func_110139_bj());
      EntityEquipmentSlot[] var2 = EntityEquipmentSlot.values();
      int var3 = var2.length;

      int var4;
      EntityEquipmentSlot var5;
      ItemStack var6;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         var6 = this.func_184582_a(var5);
         if (!var6.func_190926_b()) {
            this.func_110140_aT().func_111148_a(var6.func_111283_C(var5));
         }
      }

      var1.func_74782_a("Attributes", SharedMonsterAttributes.func_111257_a(this.func_110140_aT()));
      var2 = EntityEquipmentSlot.values();
      var3 = var2.length;

      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         var6 = this.func_184582_a(var5);
         if (!var6.func_190926_b()) {
            this.func_110140_aT().func_111147_b(var6.func_111283_C(var5));
         }
      }

      if (!this.field_70713_bf.isEmpty()) {
         NBTTagList var7 = new NBTTagList();
         Iterator var8 = this.field_70713_bf.values().iterator();

         while(var8.hasNext()) {
            PotionEffect var9 = (PotionEffect)var8.next();
            var7.add((INBTBase)var9.func_82719_a(new NBTTagCompound()));
         }

         var1.func_74782_a("ActiveEffects", var7);
      }

      var1.func_74757_a("FallFlying", this.func_184613_cA());
   }

   public void func_70037_a(NBTTagCompound var1) {
      this.func_110149_m(var1.func_74760_g("AbsorptionAmount"));
      if (var1.func_150297_b("Attributes", 9) && this.field_70170_p != null && !this.field_70170_p.field_72995_K) {
         SharedMonsterAttributes.func_151475_a(this.func_110140_aT(), var1.func_150295_c("Attributes", 10));
      }

      if (var1.func_150297_b("ActiveEffects", 9)) {
         NBTTagList var2 = var1.func_150295_c("ActiveEffects", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            NBTTagCompound var4 = var2.func_150305_b(var3);
            PotionEffect var5 = PotionEffect.func_82722_b(var4);
            if (var5 != null) {
               this.field_70713_bf.put(var5.func_188419_a(), var5);
            }
         }
      }

      if (var1.func_150297_b("Health", 99)) {
         this.func_70606_j(var1.func_74760_g("Health"));
      }

      this.field_70737_aN = var1.func_74765_d("HurtTime");
      this.field_70725_aQ = var1.func_74765_d("DeathTime");
      this.field_70756_c = var1.func_74762_e("HurtByTimestamp");
      if (var1.func_150297_b("Team", 8)) {
         String var6 = var1.func_74779_i("Team");
         ScorePlayerTeam var7 = this.field_70170_p.func_96441_U().func_96508_e(var6);
         boolean var8 = var7 != null && this.field_70170_p.func_96441_U().func_197901_a(this.func_189512_bd(), var7);
         if (!var8) {
            field_190632_a.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", var6);
         }
      }

      if (var1.func_74767_n("FallFlying")) {
         this.func_70052_a(7, true);
      }

   }

   protected void func_70679_bo() {
      Iterator var1 = this.field_70713_bf.keySet().iterator();

      try {
         while(var1.hasNext()) {
            Potion var2 = (Potion)var1.next();
            PotionEffect var3 = (PotionEffect)this.field_70713_bf.get(var2);
            if (!var3.func_76455_a(this)) {
               if (!this.field_70170_p.field_72995_K) {
                  var1.remove();
                  this.func_70688_c(var3);
               }
            } else if (var3.func_76459_b() % 600 == 0) {
               this.func_70695_b(var3, false);
            }
         }
      } catch (ConcurrentModificationException var11) {
      }

      if (this.field_70752_e) {
         if (!this.field_70170_p.field_72995_K) {
            this.func_175135_B();
         }

         this.field_70752_e = false;
      }

      int var12 = (Integer)this.field_70180_af.func_187225_a(field_184633_f);
      boolean var13 = (Boolean)this.field_70180_af.func_187225_a(field_184634_g);
      if (var12 > 0) {
         boolean var4;
         if (this.func_82150_aj()) {
            var4 = this.field_70146_Z.nextInt(15) == 0;
         } else {
            var4 = this.field_70146_Z.nextBoolean();
         }

         if (var13) {
            var4 &= this.field_70146_Z.nextInt(5) == 0;
         }

         if (var4 && var12 > 0) {
            double var5 = (double)(var12 >> 16 & 255) / 255.0D;
            double var7 = (double)(var12 >> 8 & 255) / 255.0D;
            double var9 = (double)(var12 >> 0 & 255) / 255.0D;
            this.field_70170_p.func_195594_a(var13 ? Particles.field_197608_a : Particles.field_197625_r, this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, var5, var7, var9);
         }
      }

   }

   protected void func_175135_B() {
      if (this.field_70713_bf.isEmpty()) {
         this.func_175133_bi();
         this.func_82142_c(false);
      } else {
         Collection var1 = this.field_70713_bf.values();
         this.field_70180_af.func_187227_b(field_184634_g, func_184593_a(var1));
         this.field_70180_af.func_187227_b(field_184633_f, PotionUtils.func_185181_a(var1));
         this.func_82142_c(this.func_70644_a(MobEffects.field_76441_p));
      }

   }

   public static boolean func_184593_a(Collection<PotionEffect> var0) {
      Iterator var1 = var0.iterator();

      PotionEffect var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (PotionEffect)var1.next();
      } while(var2.func_82720_e());

      return false;
   }

   protected void func_175133_bi() {
      this.field_70180_af.func_187227_b(field_184634_g, false);
      this.field_70180_af.func_187227_b(field_184633_f, 0);
   }

   public boolean func_195061_cb() {
      if (this.field_70170_p.field_72995_K) {
         return false;
      } else {
         Iterator var1 = this.field_70713_bf.values().iterator();

         boolean var2;
         for(var2 = false; var1.hasNext(); var2 = true) {
            this.func_70688_c((PotionEffect)var1.next());
            var1.remove();
         }

         return var2;
      }
   }

   public Collection<PotionEffect> func_70651_bq() {
      return this.field_70713_bf.values();
   }

   public Map<Potion, PotionEffect> func_193076_bZ() {
      return this.field_70713_bf;
   }

   public boolean func_70644_a(Potion var1) {
      return this.field_70713_bf.containsKey(var1);
   }

   @Nullable
   public PotionEffect func_70660_b(Potion var1) {
      return (PotionEffect)this.field_70713_bf.get(var1);
   }

   public boolean func_195064_c(PotionEffect var1) {
      if (!this.func_70687_e(var1)) {
         return false;
      } else {
         PotionEffect var2 = (PotionEffect)this.field_70713_bf.get(var1.func_188419_a());
         if (var2 == null) {
            this.field_70713_bf.put(var1.func_188419_a(), var1);
            this.func_70670_a(var1);
            return true;
         } else if (var2.func_199308_a(var1)) {
            this.func_70695_b(var2, true);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean func_70687_e(PotionEffect var1) {
      if (this.func_70668_bt() == CreatureAttribute.UNDEAD) {
         Potion var2 = var1.func_188419_a();
         if (var2 == MobEffects.field_76428_l || var2 == MobEffects.field_76436_u) {
            return false;
         }
      }

      return true;
   }

   public boolean func_70662_br() {
      return this.func_70668_bt() == CreatureAttribute.UNDEAD;
   }

   @Nullable
   public PotionEffect func_184596_c(@Nullable Potion var1) {
      return (PotionEffect)this.field_70713_bf.remove(var1);
   }

   public boolean func_195063_d(Potion var1) {
      PotionEffect var2 = this.func_184596_c(var1);
      if (var2 != null) {
         this.func_70688_c(var2);
         return true;
      } else {
         return false;
      }
   }

   protected void func_70670_a(PotionEffect var1) {
      this.field_70752_e = true;
      if (!this.field_70170_p.field_72995_K) {
         var1.func_188419_a().func_111185_a(this, this.func_110140_aT(), var1.func_76458_c());
      }

   }

   protected void func_70695_b(PotionEffect var1, boolean var2) {
      this.field_70752_e = true;
      if (var2 && !this.field_70170_p.field_72995_K) {
         Potion var3 = var1.func_188419_a();
         var3.func_111187_a(this, this.func_110140_aT(), var1.func_76458_c());
         var3.func_111185_a(this, this.func_110140_aT(), var1.func_76458_c());
      }

   }

   protected void func_70688_c(PotionEffect var1) {
      this.field_70752_e = true;
      if (!this.field_70170_p.field_72995_K) {
         var1.func_188419_a().func_111187_a(this, this.func_110140_aT(), var1.func_76458_c());
      }

   }

   public void func_70691_i(float var1) {
      float var2 = this.func_110143_aJ();
      if (var2 > 0.0F) {
         this.func_70606_j(var2 + var1);
      }

   }

   public float func_110143_aJ() {
      return (Float)this.field_70180_af.func_187225_a(field_184632_c);
   }

   public void func_70606_j(float var1) {
      this.field_70180_af.func_187227_b(field_184632_c, MathHelper.func_76131_a(var1, 0.0F, this.func_110138_aP()));
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else if (this.field_70170_p.field_72995_K) {
         return false;
      } else if (this.func_110143_aJ() <= 0.0F) {
         return false;
      } else if (var1.func_76347_k() && this.func_70644_a(MobEffects.field_76426_n)) {
         return false;
      } else {
         this.field_70708_bq = 0;
         float var3 = var2;
         if ((var1 == DamageSource.field_82728_o || var1 == DamageSource.field_82729_p) && !this.func_184582_a(EntityEquipmentSlot.HEAD).func_190926_b()) {
            this.func_184582_a(EntityEquipmentSlot.HEAD).func_77972_a((int)(var2 * 4.0F + this.field_70146_Z.nextFloat() * var2 * 2.0F), this);
            var2 *= 0.75F;
         }

         boolean var4 = false;
         float var5 = 0.0F;
         if (var2 > 0.0F && this.func_184583_d(var1)) {
            this.func_184590_k(var2);
            var5 = var2;
            var2 = 0.0F;
            if (!var1.func_76352_a()) {
               Entity var6 = var1.func_76364_f();
               if (var6 instanceof EntityLivingBase) {
                  this.func_190629_c((EntityLivingBase)var6);
               }
            }

            var4 = true;
         }

         this.field_70721_aZ = 1.5F;
         boolean var12 = true;
         if ((float)this.field_70172_ad > (float)this.field_70771_an / 2.0F) {
            if (var2 <= this.field_110153_bc) {
               return false;
            }

            this.func_70665_d(var1, var2 - this.field_110153_bc);
            this.field_110153_bc = var2;
            var12 = false;
         } else {
            this.field_110153_bc = var2;
            this.field_70172_ad = this.field_70771_an;
            this.func_70665_d(var1, var2);
            this.field_70738_aO = 10;
            this.field_70737_aN = this.field_70738_aO;
         }

         this.field_70739_aP = 0.0F;
         Entity var7 = var1.func_76346_g();
         if (var7 != null) {
            if (var7 instanceof EntityLivingBase) {
               this.func_70604_c((EntityLivingBase)var7);
            }

            if (var7 instanceof EntityPlayer) {
               this.field_70718_bc = 100;
               this.field_70717_bb = (EntityPlayer)var7;
            } else if (var7 instanceof EntityWolf) {
               EntityWolf var8 = (EntityWolf)var7;
               if (var8.func_70909_n()) {
                  this.field_70718_bc = 100;
                  this.field_70717_bb = null;
               }
            }
         }

         if (var12) {
            if (var4) {
               this.field_70170_p.func_72960_a(this, (byte)29);
            } else if (var1 instanceof EntityDamageSource && ((EntityDamageSource)var1).func_180139_w()) {
               this.field_70170_p.func_72960_a(this, (byte)33);
            } else {
               byte var13;
               if (var1 == DamageSource.field_76369_e) {
                  var13 = 36;
               } else if (var1.func_76347_k()) {
                  var13 = 37;
               } else {
                  var13 = 2;
               }

               this.field_70170_p.func_72960_a(this, var13);
            }

            if (var1 != DamageSource.field_76369_e && (!var4 || var2 > 0.0F)) {
               this.func_70018_K();
            }

            if (var7 != null) {
               double var14 = var7.field_70165_t - this.field_70165_t;

               double var10;
               for(var10 = var7.field_70161_v - this.field_70161_v; var14 * var14 + var10 * var10 < 1.0E-4D; var10 = (Math.random() - Math.random()) * 0.01D) {
                  var14 = (Math.random() - Math.random()) * 0.01D;
               }

               this.field_70739_aP = (float)(MathHelper.func_181159_b(var10, var14) * 57.2957763671875D - (double)this.field_70177_z);
               this.func_70653_a(var7, 0.4F, var14, var10);
            } else {
               this.field_70739_aP = (float)((int)(Math.random() * 2.0D) * 180);
            }
         }

         if (this.func_110143_aJ() <= 0.0F) {
            if (!this.func_190628_d(var1)) {
               SoundEvent var15 = this.func_184615_bR();
               if (var12 && var15 != null) {
                  this.func_184185_a(var15, this.func_70599_aP(), this.func_70647_i());
               }

               this.func_70645_a(var1);
            }
         } else if (var12) {
            this.func_184581_c(var1);
         }

         boolean var16 = !var4 || var2 > 0.0F;
         if (var16) {
            this.field_189750_bF = var1;
            this.field_189751_bG = this.field_70170_p.func_82737_E();
         }

         if (this instanceof EntityPlayerMP) {
            CriteriaTriggers.field_192128_h.func_192200_a((EntityPlayerMP)this, var1, var3, var2, var4);
            if (var5 > 0.0F && var5 < 3.4028235E37F) {
               ((EntityPlayerMP)this).func_195067_a(StatList.field_212737_I, Math.round(var5 * 10.0F));
            }
         }

         if (var7 instanceof EntityPlayerMP) {
            CriteriaTriggers.field_192127_g.func_192220_a((EntityPlayerMP)var7, this, var1, var3, var2, var4);
         }

         return var16;
      }
   }

   protected void func_190629_c(EntityLivingBase var1) {
      var1.func_70653_a(this, 0.5F, this.field_70165_t - var1.field_70165_t, this.field_70161_v - var1.field_70161_v);
   }

   private boolean func_190628_d(DamageSource var1) {
      if (var1.func_76357_e()) {
         return false;
      } else {
         ItemStack var2 = null;
         EnumHand[] var4 = EnumHand.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumHand var7 = var4[var6];
            ItemStack var3 = this.func_184586_b(var7);
            if (var3.func_77973_b() == Items.field_190929_cY) {
               var2 = var3.func_77946_l();
               var3.func_190918_g(1);
               break;
            }
         }

         if (var2 != null) {
            if (this instanceof EntityPlayerMP) {
               EntityPlayerMP var8 = (EntityPlayerMP)this;
               var8.func_71029_a(StatList.field_75929_E.func_199076_b(Items.field_190929_cY));
               CriteriaTriggers.field_193130_A.func_193187_a(var8, var2);
            }

            this.func_70606_j(1.0F);
            this.func_195061_cb();
            this.func_195064_c(new PotionEffect(MobEffects.field_76428_l, 900, 1));
            this.func_195064_c(new PotionEffect(MobEffects.field_76444_x, 100, 1));
            this.field_70170_p.func_72960_a(this, (byte)35);
         }

         return var2 != null;
      }
   }

   @Nullable
   public DamageSource func_189748_bU() {
      if (this.field_70170_p.func_82737_E() - this.field_189751_bG > 40L) {
         this.field_189750_bF = null;
      }

      return this.field_189750_bF;
   }

   protected void func_184581_c(DamageSource var1) {
      SoundEvent var2 = this.func_184601_bQ(var1);
      if (var2 != null) {
         this.func_184185_a(var2, this.func_70599_aP(), this.func_70647_i());
      }

   }

   private boolean func_184583_d(DamageSource var1) {
      if (!var1.func_76363_c() && this.func_184585_cz()) {
         Vec3d var2 = var1.func_188404_v();
         if (var2 != null) {
            Vec3d var3 = this.func_70676_i(1.0F);
            Vec3d var4 = var2.func_72444_a(new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v)).func_72432_b();
            var4 = new Vec3d(var4.field_72450_a, 0.0D, var4.field_72449_c);
            if (var4.func_72430_b(var3) < 0.0D) {
               return true;
            }
         }
      }

      return false;
   }

   public void func_70669_a(ItemStack var1) {
      super.func_184185_a(SoundEvents.field_187635_cQ, 0.8F, 0.8F + this.field_70170_p.field_73012_v.nextFloat() * 0.4F);
      this.func_195062_a(var1, 5);
   }

   public void func_70645_a(DamageSource var1) {
      if (!this.field_70729_aU) {
         Entity var2 = var1.func_76346_g();
         EntityLivingBase var3 = this.func_94060_bK();
         if (this.field_70744_aE >= 0 && var3 != null) {
            var3.func_191956_a(this, this.field_70744_aE, var1);
         }

         if (var2 != null) {
            var2.func_70074_a(this);
         }

         this.field_70729_aU = true;
         this.func_110142_aN().func_94549_h();
         if (!this.field_70170_p.field_72995_K) {
            int var4 = 0;
            if (var2 instanceof EntityPlayer) {
               var4 = EnchantmentHelper.func_185283_h((EntityLivingBase)var2);
            }

            if (this.func_146066_aG() && this.field_70170_p.func_82736_K().func_82766_b("doMobLoot")) {
               boolean var5 = this.field_70718_bc > 0;
               this.func_184610_a(var5, var4, var1);
            }
         }

         this.field_70170_p.func_72960_a(this, (byte)3);
      }
   }

   protected void func_184610_a(boolean var1, int var2, DamageSource var3) {
      this.func_70628_a(var1, var2);
      this.func_82160_b(var1, var2);
   }

   protected void func_82160_b(boolean var1, int var2) {
   }

   public void func_70653_a(Entity var1, float var2, double var3, double var5) {
      if (this.field_70146_Z.nextDouble() >= this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111126_e()) {
         this.field_70160_al = true;
         float var7 = MathHelper.func_76133_a(var3 * var3 + var5 * var5);
         this.field_70159_w /= 2.0D;
         this.field_70179_y /= 2.0D;
         this.field_70159_w -= var3 / (double)var7 * (double)var2;
         this.field_70179_y -= var5 / (double)var7 * (double)var2;
         if (this.field_70122_E) {
            this.field_70181_x /= 2.0D;
            this.field_70181_x += (double)var2;
            if (this.field_70181_x > 0.4000000059604645D) {
               this.field_70181_x = 0.4000000059604645D;
            }
         }

      }
   }

   @Nullable
   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187543_bD;
   }

   @Nullable
   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187661_by;
   }

   protected SoundEvent func_184588_d(int var1) {
      return var1 > 4 ? SoundEvents.field_187655_bw : SoundEvents.field_187545_bE;
   }

   protected void func_70628_a(boolean var1, int var2) {
   }

   public boolean func_70617_f_() {
      int var1 = MathHelper.func_76128_c(this.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      if (this instanceof EntityPlayer && ((EntityPlayer)this).func_175149_v()) {
         return false;
      } else {
         BlockPos var4 = new BlockPos(var1, var2, var3);
         IBlockState var5 = this.field_70170_p.func_180495_p(var4);
         Block var6 = var5.func_177230_c();
         if (var6 != Blocks.field_150468_ap && var6 != Blocks.field_150395_bd) {
            return var6 instanceof BlockTrapDoor && this.func_184604_a(var4, var5);
         } else {
            return true;
         }
      }
   }

   private boolean func_184604_a(BlockPos var1, IBlockState var2) {
      if ((Boolean)var2.func_177229_b(BlockTrapDoor.field_176283_b)) {
         IBlockState var3 = this.field_70170_p.func_180495_p(var1.func_177977_b());
         if (var3.func_177230_c() == Blocks.field_150468_ap && var3.func_177229_b(BlockLadder.field_176382_a) == var2.func_177229_b(BlockTrapDoor.field_185512_D)) {
            return true;
         }
      }

      return false;
   }

   public boolean func_70089_S() {
      return !this.field_70128_L && this.func_110143_aJ() > 0.0F;
   }

   public void func_180430_e(float var1, float var2) {
      super.func_180430_e(var1, var2);
      PotionEffect var3 = this.func_70660_b(MobEffects.field_76430_j);
      float var4 = var3 == null ? 0.0F : (float)(var3.func_76458_c() + 1);
      int var5 = MathHelper.func_76123_f((var1 - 3.0F - var4) * var2);
      if (var5 > 0) {
         this.func_184185_a(this.func_184588_d(var5), 1.0F, 1.0F);
         this.func_70097_a(DamageSource.field_76379_h, (float)var5);
         int var6 = MathHelper.func_76128_c(this.field_70165_t);
         int var7 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D);
         int var8 = MathHelper.func_76128_c(this.field_70161_v);
         IBlockState var9 = this.field_70170_p.func_180495_p(new BlockPos(var6, var7, var8));
         if (!var9.func_196958_f()) {
            SoundType var10 = var9.func_177230_c().func_185467_w();
            this.func_184185_a(var10.func_185842_g(), var10.func_185843_a() * 0.5F, var10.func_185847_b() * 0.75F);
         }
      }

   }

   public void func_70057_ab() {
      this.field_70738_aO = 10;
      this.field_70737_aN = this.field_70738_aO;
      this.field_70739_aP = 0.0F;
   }

   public int func_70658_aO() {
      IAttributeInstance var1 = this.func_110148_a(SharedMonsterAttributes.field_188791_g);
      return MathHelper.func_76128_c(var1.func_111126_e());
   }

   protected void func_70675_k(float var1) {
   }

   protected void func_184590_k(float var1) {
   }

   protected float func_70655_b(DamageSource var1, float var2) {
      if (!var1.func_76363_c()) {
         this.func_70675_k(var2);
         var2 = CombatRules.func_189427_a(var2, (float)this.func_70658_aO(), (float)this.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
      }

      return var2;
   }

   protected float func_70672_c(DamageSource var1, float var2) {
      if (var1.func_151517_h()) {
         return var2;
      } else {
         int var3;
         if (this.func_70644_a(MobEffects.field_76429_m) && var1 != DamageSource.field_76380_i) {
            var3 = (this.func_70660_b(MobEffects.field_76429_m).func_76458_c() + 1) * 5;
            int var4 = 25 - var3;
            float var5 = var2 * (float)var4;
            float var6 = var2;
            var2 = Math.max(var5 / 25.0F, 0.0F);
            float var7 = var6 - var2;
            if (var7 > 0.0F && var7 < 3.4028235E37F) {
               if (this instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)this).func_195067_a(StatList.field_212739_K, Math.round(var7 * 10.0F));
               } else if (var1.func_76346_g() instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)var1.func_76346_g()).func_195067_a(StatList.field_212736_G, Math.round(var7 * 10.0F));
               }
            }
         }

         if (var2 <= 0.0F) {
            return 0.0F;
         } else {
            var3 = EnchantmentHelper.func_77508_a(this.func_184193_aE(), var1);
            if (var3 > 0) {
               var2 = CombatRules.func_188401_b(var2, (float)var3);
            }

            return var2;
         }
      }
   }

   protected void func_70665_d(DamageSource var1, float var2) {
      if (!this.func_180431_b(var1)) {
         var2 = this.func_70655_b(var1, var2);
         var2 = this.func_70672_c(var1, var2);
         float var3 = var2;
         var2 = Math.max(var2 - this.func_110139_bj(), 0.0F);
         this.func_110149_m(this.func_110139_bj() - (var3 - var2));
         float var4 = var3 - var2;
         if (var4 > 0.0F && var4 < 3.4028235E37F && var1.func_76346_g() instanceof EntityPlayerMP) {
            ((EntityPlayerMP)var1.func_76346_g()).func_195067_a(StatList.field_212735_F, Math.round(var4 * 10.0F));
         }

         if (var2 != 0.0F) {
            float var5 = this.func_110143_aJ();
            this.func_70606_j(var5 - var2);
            this.func_110142_aN().func_94547_a(var1, var5, var2);
            this.func_110149_m(this.func_110139_bj() - var2);
         }
      }
   }

   public CombatTracker func_110142_aN() {
      return this.field_94063_bt;
   }

   @Nullable
   public EntityLivingBase func_94060_bK() {
      if (this.field_94063_bt.func_94550_c() != null) {
         return this.field_94063_bt.func_94550_c();
      } else if (this.field_70717_bb != null) {
         return this.field_70717_bb;
      } else {
         return this.field_70755_b != null ? this.field_70755_b : null;
      }
   }

   public final float func_110138_aP() {
      return (float)this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111126_e();
   }

   public final int func_85035_bI() {
      return (Integer)this.field_70180_af.func_187225_a(field_184635_h);
   }

   public final void func_85034_r(int var1) {
      this.field_70180_af.func_187227_b(field_184635_h, var1);
   }

   private int func_82166_i() {
      if (PotionUtil.func_205135_a(this)) {
         return 6 - (1 + PotionUtil.func_205134_b(this));
      } else {
         return this.func_70644_a(MobEffects.field_76419_f) ? 6 + (1 + this.func_70660_b(MobEffects.field_76419_f).func_76458_c()) * 2 : 6;
      }
   }

   public void func_184609_a(EnumHand var1) {
      if (!this.field_82175_bq || this.field_110158_av >= this.func_82166_i() / 2 || this.field_110158_av < 0) {
         this.field_110158_av = -1;
         this.field_82175_bq = true;
         this.field_184622_au = var1;
         if (this.field_70170_p instanceof WorldServer) {
            ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new SPacketAnimation(this, var1 == EnumHand.MAIN_HAND ? 0 : 3));
         }
      }

   }

   public void func_70103_a(byte var1) {
      boolean var2 = var1 == 33;
      boolean var3 = var1 == 36;
      boolean var4 = var1 == 37;
      if (var1 != 2 && !var2 && !var3 && !var4) {
         if (var1 == 3) {
            SoundEvent var7 = this.func_184615_bR();
            if (var7 != null) {
               this.func_184185_a(var7, this.func_70599_aP(), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
            }

            this.func_70606_j(0.0F);
            this.func_70645_a(DamageSource.field_76377_j);
         } else if (var1 == 30) {
            this.func_184185_a(SoundEvents.field_187769_eM, 0.8F, 0.8F + this.field_70170_p.field_73012_v.nextFloat() * 0.4F);
         } else if (var1 == 29) {
            this.func_184185_a(SoundEvents.field_187767_eL, 1.0F, 0.8F + this.field_70170_p.field_73012_v.nextFloat() * 0.4F);
         } else {
            super.func_70103_a(var1);
         }
      } else {
         this.field_70721_aZ = 1.5F;
         this.field_70172_ad = this.field_70771_an;
         this.field_70738_aO = 10;
         this.field_70737_aN = this.field_70738_aO;
         this.field_70739_aP = 0.0F;
         if (var2) {
            this.func_184185_a(SoundEvents.field_187903_gc, this.func_70599_aP(), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
         }

         DamageSource var5;
         if (var4) {
            var5 = DamageSource.field_76370_b;
         } else if (var3) {
            var5 = DamageSource.field_76369_e;
         } else {
            var5 = DamageSource.field_76377_j;
         }

         SoundEvent var6 = this.func_184601_bQ(var5);
         if (var6 != null) {
            this.func_184185_a(var6, this.func_70599_aP(), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
         }

         this.func_70097_a(DamageSource.field_76377_j, 0.0F);
      }

   }

   protected void func_70076_C() {
      this.func_70097_a(DamageSource.field_76380_i, 4.0F);
   }

   protected void func_82168_bl() {
      int var1 = this.func_82166_i();
      if (this.field_82175_bq) {
         ++this.field_110158_av;
         if (this.field_110158_av >= var1) {
            this.field_110158_av = 0;
            this.field_82175_bq = false;
         }
      } else {
         this.field_110158_av = 0;
      }

      this.field_70733_aJ = (float)this.field_110158_av / (float)var1;
   }

   public IAttributeInstance func_110148_a(IAttribute var1) {
      return this.func_110140_aT().func_111151_a(var1);
   }

   public AbstractAttributeMap func_110140_aT() {
      if (this.field_110155_d == null) {
         this.field_110155_d = new AttributeMap();
      }

      return this.field_110155_d;
   }

   public CreatureAttribute func_70668_bt() {
      return CreatureAttribute.UNDEFINED;
   }

   public ItemStack func_184614_ca() {
      return this.func_184582_a(EntityEquipmentSlot.MAINHAND);
   }

   public ItemStack func_184592_cb() {
      return this.func_184582_a(EntityEquipmentSlot.OFFHAND);
   }

   public ItemStack func_184586_b(EnumHand var1) {
      if (var1 == EnumHand.MAIN_HAND) {
         return this.func_184582_a(EntityEquipmentSlot.MAINHAND);
      } else if (var1 == EnumHand.OFF_HAND) {
         return this.func_184582_a(EntityEquipmentSlot.OFFHAND);
      } else {
         throw new IllegalArgumentException("Invalid hand " + var1);
      }
   }

   public void func_184611_a(EnumHand var1, ItemStack var2) {
      if (var1 == EnumHand.MAIN_HAND) {
         this.func_184201_a(EntityEquipmentSlot.MAINHAND, var2);
      } else {
         if (var1 != EnumHand.OFF_HAND) {
            throw new IllegalArgumentException("Invalid hand " + var1);
         }

         this.func_184201_a(EntityEquipmentSlot.OFFHAND, var2);
      }

   }

   public boolean func_190630_a(EntityEquipmentSlot var1) {
      return !this.func_184582_a(var1).func_190926_b();
   }

   public abstract Iterable<ItemStack> func_184193_aE();

   public abstract ItemStack func_184582_a(EntityEquipmentSlot var1);

   public abstract void func_184201_a(EntityEquipmentSlot var1, ItemStack var2);

   public void func_70031_b(boolean var1) {
      super.func_70031_b(var1);
      IAttributeInstance var2 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
      if (var2.func_111127_a(field_110156_b) != null) {
         var2.func_111124_b(field_110157_c);
      }

      if (var1) {
         var2.func_111121_a(field_110157_c);
      }

   }

   protected float func_70599_aP() {
      return 1.0F;
   }

   protected float func_70647_i() {
      return this.func_70631_g_() ? (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.5F : (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F;
   }

   protected boolean func_70610_aX() {
      return this.func_110143_aJ() <= 0.0F;
   }

   public void func_110145_l(Entity var1) {
      double var7;
      if (!(var1 instanceof EntityBoat) && !(var1 instanceof AbstractHorse)) {
         double var3 = var1.field_70165_t;
         double var35 = var1.func_174813_aQ().field_72338_b + (double)var1.field_70131_O;
         var7 = var1.field_70161_v;
         EnumFacing var36 = var1.func_184172_bi();
         if (var36 != null) {
            EnumFacing var10 = var36.func_176746_e();
            int[][] var37 = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
            double var12 = Math.floor(this.field_70165_t) + 0.5D;
            double var14 = Math.floor(this.field_70161_v) + 0.5D;
            double var16 = this.func_174813_aQ().field_72336_d - this.func_174813_aQ().field_72340_a;
            double var18 = this.func_174813_aQ().field_72334_f - this.func_174813_aQ().field_72339_c;
            AxisAlignedBB var20 = new AxisAlignedBB(var12 - var16 / 2.0D, var1.func_174813_aQ().field_72338_b, var14 - var18 / 2.0D, var12 + var16 / 2.0D, Math.floor(var1.func_174813_aQ().field_72338_b) + (double)this.field_70131_O, var14 + var18 / 2.0D);
            int[][] var21 = var37;
            int var22 = var37.length;

            for(int var23 = 0; var23 < var22; ++var23) {
               int[] var24 = var21[var23];
               double var25 = (double)(var36.func_82601_c() * var24[0] + var10.func_82601_c() * var24[1]);
               double var27 = (double)(var36.func_82599_e() * var24[0] + var10.func_82599_e() * var24[1]);
               double var29 = var12 + var25;
               double var31 = var14 + var27;
               AxisAlignedBB var34 = var20.func_72317_d(var25, 0.0D, var27);
               if (this.field_70170_p.func_195586_b(this, var34)) {
                  if (this.field_70170_p.func_180495_p(new BlockPos(var29, this.field_70163_u, var31)).func_185896_q()) {
                     this.func_70634_a(var29, this.field_70163_u + 1.0D, var31);
                     return;
                  }

                  BlockPos var33 = new BlockPos(var29, this.field_70163_u - 1.0D, var31);
                  if (this.field_70170_p.func_180495_p(var33).func_185896_q() || this.field_70170_p.func_204610_c(var33).func_206884_a(FluidTags.field_206959_a)) {
                     var3 = var29;
                     var35 = this.field_70163_u + 1.0D;
                     var7 = var31;
                  }
               } else if (this.field_70170_p.func_195586_b(this, var34.func_72317_d(0.0D, 1.0D, 0.0D)) && this.field_70170_p.func_180495_p(new BlockPos(var29, this.field_70163_u + 1.0D, var31)).func_185896_q()) {
                  var3 = var29;
                  var35 = this.field_70163_u + 2.0D;
                  var7 = var31;
               }
            }
         }

         this.func_70634_a(var3, var35, var7);
      } else {
         double var2 = (double)(this.field_70130_N / 2.0F + var1.field_70130_N / 2.0F) + 0.4D;
         float var4;
         if (var1 instanceof EntityBoat) {
            var4 = 0.0F;
         } else {
            var4 = 1.5707964F * (float)(this.func_184591_cq() == EnumHandSide.RIGHT ? -1 : 1);
         }

         float var5 = -MathHelper.func_76126_a(-this.field_70177_z * 0.017453292F - 3.1415927F + var4);
         float var6 = -MathHelper.func_76134_b(-this.field_70177_z * 0.017453292F - 3.1415927F + var4);
         var7 = Math.abs(var5) > Math.abs(var6) ? var2 / (double)Math.abs(var5) : var2 / (double)Math.abs(var6);
         double var9 = this.field_70165_t + (double)var5 * var7;
         double var11 = this.field_70161_v + (double)var6 * var7;
         this.func_70107_b(var9, var1.field_70163_u + (double)var1.field_70131_O + 0.001D, var11);
         if (!this.field_70170_p.func_195586_b(this, this.func_174813_aQ().func_111270_a(var1.func_174813_aQ()))) {
            this.func_70107_b(var9, var1.field_70163_u + (double)var1.field_70131_O + 1.001D, var11);
            if (!this.field_70170_p.func_195586_b(this, this.func_174813_aQ().func_111270_a(var1.func_174813_aQ()))) {
               this.func_70107_b(var1.field_70165_t, var1.field_70163_u + (double)this.field_70131_O + 0.001D, var1.field_70161_v);
            }
         }
      }
   }

   public boolean func_94059_bO() {
      return this.func_174833_aM();
   }

   protected float func_175134_bD() {
      return 0.42F;
   }

   protected void func_70664_aZ() {
      this.field_70181_x = (double)this.func_175134_bD();
      if (this.func_70644_a(MobEffects.field_76430_j)) {
         this.field_70181_x += (double)((float)(this.func_70660_b(MobEffects.field_76430_j).func_76458_c() + 1) * 0.1F);
      }

      if (this.func_70051_ag()) {
         float var1 = this.field_70177_z * 0.017453292F;
         this.field_70159_w -= (double)(MathHelper.func_76126_a(var1) * 0.2F);
         this.field_70179_y += (double)(MathHelper.func_76134_b(var1) * 0.2F);
      }

      this.field_70160_al = true;
   }

   protected void func_203010_cG() {
      this.field_70181_x -= 0.03999999910593033D;
   }

   protected void func_180466_bG(Tag<Fluid> var1) {
      this.field_70181_x += 0.03999999910593033D;
   }

   protected float func_189749_co() {
      return 0.8F;
   }

   public void func_191986_a(float var1, float var2, float var3) {
      double var4;
      double var6;
      float var10;
      double var32;
      if (this.func_70613_aW() || this.func_184186_bw()) {
         var4 = 0.08D;
         if (this.field_70181_x <= 0.0D && this.func_70644_a(MobEffects.field_204839_B)) {
            var4 = 0.01D;
            this.field_70143_R = 0.0F;
         }

         float var9;
         if (!this.func_70090_H() || this instanceof EntityPlayer && ((EntityPlayer)this).field_71075_bZ.field_75100_b) {
            if (!this.func_180799_ab() || this instanceof EntityPlayer && ((EntityPlayer)this).field_71075_bZ.field_75100_b) {
               if (this.func_184613_cA()) {
                  if (this.field_70181_x > -0.5D) {
                     this.field_70143_R = 1.0F;
                  }

                  Vec3d var29 = this.func_70040_Z();
                  float var7 = this.field_70125_A * 0.017453292F;
                  var32 = Math.sqrt(var29.field_72450_a * var29.field_72450_a + var29.field_72449_c * var29.field_72449_c);
                  double var34 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
                  double var12 = var29.func_72433_c();
                  float var14 = MathHelper.func_76134_b(var7);
                  var14 = (float)((double)var14 * (double)var14 * Math.min(1.0D, var12 / 0.4D));
                  this.field_70181_x += var4 * (-1.0D + (double)var14 * 0.75D);
                  double var15;
                  if (this.field_70181_x < 0.0D && var32 > 0.0D) {
                     var15 = this.field_70181_x * -0.1D * (double)var14;
                     this.field_70181_x += var15;
                     this.field_70159_w += var29.field_72450_a * var15 / var32;
                     this.field_70179_y += var29.field_72449_c * var15 / var32;
                  }

                  if (var7 < 0.0F && var32 > 0.0D) {
                     var15 = var34 * (double)(-MathHelper.func_76126_a(var7)) * 0.04D;
                     this.field_70181_x += var15 * 3.2D;
                     this.field_70159_w -= var29.field_72450_a * var15 / var32;
                     this.field_70179_y -= var29.field_72449_c * var15 / var32;
                  }

                  if (var32 > 0.0D) {
                     this.field_70159_w += (var29.field_72450_a / var32 * var34 - this.field_70159_w) * 0.1D;
                     this.field_70179_y += (var29.field_72449_c / var32 * var34 - this.field_70179_y) * 0.1D;
                  }

                  this.field_70159_w *= 0.9900000095367432D;
                  this.field_70181_x *= 0.9800000190734863D;
                  this.field_70179_y *= 0.9900000095367432D;
                  this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                  if (this.field_70123_F && !this.field_70170_p.field_72995_K) {
                     var15 = Math.sqrt(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
                     double var17 = var34 - var15;
                     float var19 = (float)(var17 * 10.0D - 3.0D);
                     if (var19 > 0.0F) {
                        this.func_184185_a(this.func_184588_d((int)var19), 1.0F, 1.0F);
                        this.func_70097_a(DamageSource.field_188406_j, var19);
                     }
                  }

                  if (this.field_70122_E && !this.field_70170_p.field_72995_K) {
                     this.func_70052_a(7, false);
                  }
               } else {
                  float var30 = 0.91F;
                  BlockPos.PooledMutableBlockPos var31 = BlockPos.PooledMutableBlockPos.func_185345_c(this.field_70165_t, this.func_174813_aQ().field_72338_b - 1.0D, this.field_70161_v);
                  Throwable var33 = null;

                  try {
                     if (this.field_70122_E) {
                        var30 = this.field_70170_p.func_180495_p(var31).func_177230_c().func_208618_m() * 0.91F;
                     }

                     var9 = 0.16277137F / (var30 * var30 * var30);
                     if (this.field_70122_E) {
                        var10 = this.func_70689_ay() * var9;
                     } else {
                        var10 = this.field_70747_aH;
                     }

                     this.func_191958_b(var1, var2, var3, var10);
                     var30 = 0.91F;
                     if (this.field_70122_E) {
                        var30 = this.field_70170_p.func_180495_p(var31.func_189532_c(this.field_70165_t, this.func_174813_aQ().field_72338_b - 1.0D, this.field_70161_v)).func_177230_c().func_208618_m() * 0.91F;
                     }

                     if (this.func_70617_f_()) {
                        float var11 = 0.15F;
                        this.field_70159_w = MathHelper.func_151237_a(this.field_70159_w, -0.15000000596046448D, 0.15000000596046448D);
                        this.field_70179_y = MathHelper.func_151237_a(this.field_70179_y, -0.15000000596046448D, 0.15000000596046448D);
                        this.field_70143_R = 0.0F;
                        if (this.field_70181_x < -0.15D) {
                           this.field_70181_x = -0.15D;
                        }

                        boolean var35 = this.func_70093_af() && this instanceof EntityPlayer;
                        if (var35 && this.field_70181_x < 0.0D) {
                           this.field_70181_x = 0.0D;
                        }
                     }

                     this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
                     if (this.field_70123_F && this.func_70617_f_()) {
                        this.field_70181_x = 0.2D;
                     }

                     if (this.func_70644_a(MobEffects.field_188424_y)) {
                        this.field_70181_x += (0.05D * (double)(this.func_70660_b(MobEffects.field_188424_y).func_76458_c() + 1) - this.field_70181_x) * 0.2D;
                        this.field_70143_R = 0.0F;
                     } else {
                        var31.func_189532_c(this.field_70165_t, 0.0D, this.field_70161_v);
                        if (!this.field_70170_p.field_72995_K || this.field_70170_p.func_175667_e(var31) && this.field_70170_p.func_175726_f(var31).func_177410_o()) {
                           if (!this.func_189652_ae()) {
                              this.field_70181_x -= var4;
                           }
                        } else if (this.field_70163_u > 0.0D) {
                           this.field_70181_x = -0.1D;
                        } else {
                           this.field_70181_x = 0.0D;
                        }
                     }

                     this.field_70181_x *= 0.9800000190734863D;
                     this.field_70159_w *= (double)var30;
                     this.field_70179_y *= (double)var30;
                  } catch (Throwable var27) {
                     var33 = var27;
                     throw var27;
                  } finally {
                     if (var31 != null) {
                        if (var33 != null) {
                           try {
                              var31.close();
                           } catch (Throwable var26) {
                              var33.addSuppressed(var26);
                           }
                        } else {
                           var31.close();
                        }
                     }

                  }
               }
            } else {
               var6 = this.field_70163_u;
               this.func_191958_b(var1, var2, var3, 0.02F);
               this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
               this.field_70159_w *= 0.5D;
               this.field_70181_x *= 0.5D;
               this.field_70179_y *= 0.5D;
               if (!this.func_189652_ae()) {
                  this.field_70181_x -= var4 / 4.0D;
               }

               if (this.field_70123_F && this.func_70038_c(this.field_70159_w, this.field_70181_x + 0.6000000238418579D - this.field_70163_u + var6, this.field_70179_y)) {
                  this.field_70181_x = 0.30000001192092896D;
               }
            }
         } else {
            var6 = this.field_70163_u;
            float var8 = this.func_70051_ag() ? 0.9F : this.func_189749_co();
            var9 = 0.02F;
            var10 = (float)EnchantmentHelper.func_185294_d(this);
            if (var10 > 3.0F) {
               var10 = 3.0F;
            }

            if (!this.field_70122_E) {
               var10 *= 0.5F;
            }

            if (var10 > 0.0F) {
               var8 += (0.54600006F - var8) * var10 / 3.0F;
               var9 += (this.func_70689_ay() - var9) * var10 / 3.0F;
            }

            if (this.func_70644_a(MobEffects.field_206827_D)) {
               var8 = 0.96F;
            }

            this.func_191958_b(var1, var2, var3, var9);
            this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
            this.field_70159_w *= (double)var8;
            this.field_70181_x *= 0.800000011920929D;
            this.field_70179_y *= (double)var8;
            if (!this.func_189652_ae() && !this.func_70051_ag()) {
               if (this.field_70181_x <= 0.0D && Math.abs(this.field_70181_x - 0.005D) >= 0.003D && Math.abs(this.field_70181_x - var4 / 16.0D) < 0.003D) {
                  this.field_70181_x = -0.003D;
               } else {
                  this.field_70181_x -= var4 / 16.0D;
               }
            }

            if (this.field_70123_F && this.func_70038_c(this.field_70159_w, this.field_70181_x + 0.6000000238418579D - this.field_70163_u + var6, this.field_70179_y)) {
               this.field_70181_x = 0.30000001192092896D;
            }
         }
      }

      this.field_184618_aE = this.field_70721_aZ;
      var4 = this.field_70165_t - this.field_70169_q;
      var6 = this.field_70161_v - this.field_70166_s;
      var32 = this instanceof IFlyingAnimal ? this.field_70163_u - this.field_70167_r : 0.0D;
      var10 = MathHelper.func_76133_a(var4 * var4 + var32 * var32 + var6 * var6) * 4.0F;
      if (var10 > 1.0F) {
         var10 = 1.0F;
      }

      this.field_70721_aZ += (var10 - this.field_70721_aZ) * 0.4F;
      this.field_184619_aG += this.field_70721_aZ;
   }

   public float func_70689_ay() {
      return this.field_70746_aG;
   }

   public void func_70659_e(float var1) {
      this.field_70746_aG = var1;
   }

   public boolean func_70652_k(Entity var1) {
      this.func_130011_c(var1);
      return false;
   }

   public boolean func_70608_bn() {
      return false;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      this.func_184608_ct();
      this.func_205014_p();
      if (!this.field_70170_p.field_72995_K) {
         int var1 = this.func_85035_bI();
         if (var1 > 0) {
            if (this.field_70720_be <= 0) {
               this.field_70720_be = 20 * (30 - var1);
            }

            --this.field_70720_be;
            if (this.field_70720_be <= 0) {
               this.func_85034_r(var1 - 1);
            }
         }

         EntityEquipmentSlot[] var2 = EntityEquipmentSlot.values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EntityEquipmentSlot var5 = var2[var4];
            ItemStack var6;
            switch(var5.func_188453_a()) {
            case HAND:
               var6 = (ItemStack)this.field_184630_bs.get(var5.func_188454_b());
               break;
            case ARMOR:
               var6 = (ItemStack)this.field_184631_bt.get(var5.func_188454_b());
               break;
            default:
               continue;
            }

            ItemStack var7 = this.func_184582_a(var5);
            if (!ItemStack.func_77989_b(var7, var6)) {
               ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new SPacketEntityEquipment(this.func_145782_y(), var5, var7));
               if (!var6.func_190926_b()) {
                  this.func_110140_aT().func_111148_a(var6.func_111283_C(var5));
               }

               if (!var7.func_190926_b()) {
                  this.func_110140_aT().func_111147_b(var7.func_111283_C(var5));
               }

               switch(var5.func_188453_a()) {
               case HAND:
                  this.field_184630_bs.set(var5.func_188454_b(), var7.func_190926_b() ? ItemStack.field_190927_a : var7.func_77946_l());
                  break;
               case ARMOR:
                  this.field_184631_bt.set(var5.func_188454_b(), var7.func_190926_b() ? ItemStack.field_190927_a : var7.func_77946_l());
               }
            }
         }

         if (this.field_70173_aa % 20 == 0) {
            this.func_110142_aN().func_94549_h();
         }

         if (!this.field_184238_ar) {
            boolean var12 = this.func_70644_a(MobEffects.field_188423_x);
            if (this.func_70083_f(6) != var12) {
               this.func_70052_a(6, var12);
            }
         }
      }

      this.func_70636_d();
      double var11 = this.field_70165_t - this.field_70169_q;
      double var13 = this.field_70161_v - this.field_70166_s;
      float var14 = (float)(var11 * var11 + var13 * var13);
      float var15 = this.field_70761_aq;
      float var16 = 0.0F;
      this.field_70768_au = this.field_110154_aX;
      float var8 = 0.0F;
      if (var14 > 0.0025000002F) {
         var8 = 1.0F;
         var16 = (float)Math.sqrt((double)var14) * 3.0F;
         float var9 = (float)MathHelper.func_181159_b(var13, var11) * 57.295776F - 90.0F;
         float var10 = MathHelper.func_76135_e(MathHelper.func_76142_g(this.field_70177_z) - var9);
         if (95.0F < var10 && var10 < 265.0F) {
            var15 = var9 - 180.0F;
         } else {
            var15 = var9;
         }
      }

      if (this.field_70733_aJ > 0.0F) {
         var15 = this.field_70177_z;
      }

      if (!this.field_70122_E) {
         var8 = 0.0F;
      }

      this.field_110154_aX += (var8 - this.field_110154_aX) * 0.3F;
      this.field_70170_p.field_72984_F.func_76320_a("headTurn");
      var16 = this.func_110146_f(var15, var16);
      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("rangeChecks");

      while(this.field_70177_z - this.field_70126_B < -180.0F) {
         this.field_70126_B -= 360.0F;
      }

      while(this.field_70177_z - this.field_70126_B >= 180.0F) {
         this.field_70126_B += 360.0F;
      }

      while(this.field_70761_aq - this.field_70760_ar < -180.0F) {
         this.field_70760_ar -= 360.0F;
      }

      while(this.field_70761_aq - this.field_70760_ar >= 180.0F) {
         this.field_70760_ar += 360.0F;
      }

      while(this.field_70125_A - this.field_70127_C < -180.0F) {
         this.field_70127_C -= 360.0F;
      }

      while(this.field_70125_A - this.field_70127_C >= 180.0F) {
         this.field_70127_C += 360.0F;
      }

      while(this.field_70759_as - this.field_70758_at < -180.0F) {
         this.field_70758_at -= 360.0F;
      }

      while(this.field_70759_as - this.field_70758_at >= 180.0F) {
         this.field_70758_at += 360.0F;
      }

      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70764_aw += var16;
      if (this.func_184613_cA()) {
         ++this.field_184629_bo;
      } else {
         this.field_184629_bo = 0;
      }

   }

   protected float func_110146_f(float var1, float var2) {
      float var3 = MathHelper.func_76142_g(var1 - this.field_70761_aq);
      this.field_70761_aq += var3 * 0.3F;
      float var4 = MathHelper.func_76142_g(this.field_70177_z - this.field_70761_aq);
      boolean var5 = var4 < -90.0F || var4 >= 90.0F;
      if (var4 < -75.0F) {
         var4 = -75.0F;
      }

      if (var4 >= 75.0F) {
         var4 = 75.0F;
      }

      this.field_70761_aq = this.field_70177_z - var4;
      if (var4 * var4 > 2500.0F) {
         this.field_70761_aq += var4 * 0.2F;
      }

      if (var5) {
         var2 *= -1.0F;
      }

      return var2;
   }

   public void func_70636_d() {
      if (this.field_70773_bE > 0) {
         --this.field_70773_bE;
      }

      if (this.field_70716_bi > 0 && !this.func_184186_bw()) {
         double var1 = this.field_70165_t + (this.field_184623_bh - this.field_70165_t) / (double)this.field_70716_bi;
         double var3 = this.field_70163_u + (this.field_184624_bi - this.field_70163_u) / (double)this.field_70716_bi;
         double var5 = this.field_70161_v + (this.field_184625_bj - this.field_70161_v) / (double)this.field_70716_bi;
         double var7 = MathHelper.func_76138_g(this.field_184626_bk - (double)this.field_70177_z);
         this.field_70177_z = (float)((double)this.field_70177_z + var7 / (double)this.field_70716_bi);
         this.field_70125_A = (float)((double)this.field_70125_A + (this.field_70709_bj - (double)this.field_70125_A) / (double)this.field_70716_bi);
         --this.field_70716_bi;
         this.func_70107_b(var1, var3, var5);
         this.func_70101_b(this.field_70177_z, this.field_70125_A);
      } else if (!this.func_70613_aW()) {
         this.field_70159_w *= 0.98D;
         this.field_70181_x *= 0.98D;
         this.field_70179_y *= 0.98D;
      }

      if (this.field_208002_br > 0) {
         this.field_70759_as = (float)((double)this.field_70759_as + MathHelper.func_76138_g(this.field_208001_bq - (double)this.field_70759_as) / (double)this.field_208002_br);
         --this.field_208002_br;
      }

      if (Math.abs(this.field_70159_w) < 0.003D) {
         this.field_70159_w = 0.0D;
      }

      if (Math.abs(this.field_70181_x) < 0.003D) {
         this.field_70181_x = 0.0D;
      }

      if (Math.abs(this.field_70179_y) < 0.003D) {
         this.field_70179_y = 0.0D;
      }

      this.field_70170_p.field_72984_F.func_76320_a("ai");
      if (this.func_70610_aX()) {
         this.field_70703_bu = false;
         this.field_70702_br = 0.0F;
         this.field_191988_bg = 0.0F;
         this.field_70704_bt = 0.0F;
      } else if (this.func_70613_aW()) {
         this.field_70170_p.field_72984_F.func_76320_a("newAi");
         this.func_70626_be();
         this.field_70170_p.field_72984_F.func_76319_b();
      }

      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("jump");
      if (this.field_70703_bu) {
         if (this.field_211517_W > 0.0D && (!this.field_70122_E || this.field_211517_W > 0.4D)) {
            this.func_180466_bG(FluidTags.field_206959_a);
         } else if (this.func_180799_ab()) {
            this.func_180466_bG(FluidTags.field_206960_b);
         } else if ((this.field_70122_E || this.field_211517_W > 0.0D && this.field_211517_W <= 0.4D) && this.field_70773_bE == 0) {
            this.func_70664_aZ();
            this.field_70773_bE = 10;
         }
      } else {
         this.field_70773_bE = 0;
      }

      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("travel");
      this.field_70702_br *= 0.98F;
      this.field_191988_bg *= 0.98F;
      this.field_70704_bt *= 0.9F;
      this.func_184616_r();
      AxisAlignedBB var9 = this.func_174813_aQ();
      this.func_191986_a(this.field_70702_br, this.field_70701_bs, this.field_191988_bg);
      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("push");
      if (this.field_204807_bs > 0) {
         --this.field_204807_bs;
         this.func_204801_a(var9, this.func_174813_aQ());
      }

      this.func_85033_bc();
      this.field_70170_p.field_72984_F.func_76319_b();
   }

   private void func_184616_r() {
      boolean var1 = this.func_70083_f(7);
      if (var1 && !this.field_70122_E && !this.func_184218_aH()) {
         ItemStack var2 = this.func_184582_a(EntityEquipmentSlot.CHEST);
         if (var2.func_77973_b() == Items.field_185160_cR && ItemElytra.func_185069_d(var2)) {
            var1 = true;
            if (!this.field_70170_p.field_72995_K && (this.field_184629_bo + 1) % 20 == 0) {
               var2.func_77972_a(1, this);
            }
         } else {
            var1 = false;
         }
      } else {
         var1 = false;
      }

      if (!this.field_70170_p.field_72995_K) {
         this.func_70052_a(7, var1);
      }

   }

   protected void func_70626_be() {
   }

   protected void func_85033_bc() {
      List var1 = this.field_70170_p.func_175674_a(this, this.func_174813_aQ(), EntitySelectors.func_200823_a(this));
      if (!var1.isEmpty()) {
         int var2 = this.field_70170_p.func_82736_K().func_180263_c("maxEntityCramming");
         int var3;
         if (var2 > 0 && var1.size() > var2 - 1 && this.field_70146_Z.nextInt(4) == 0) {
            var3 = 0;

            for(int var4 = 0; var4 < var1.size(); ++var4) {
               if (!((Entity)var1.get(var4)).func_184218_aH()) {
                  ++var3;
               }
            }

            if (var3 > var2 - 1) {
               this.func_70097_a(DamageSource.field_191291_g, 6.0F);
            }
         }

         for(var3 = 0; var3 < var1.size(); ++var3) {
            Entity var5 = (Entity)var1.get(var3);
            this.func_82167_n(var5);
         }
      }

   }

   protected void func_204801_a(AxisAlignedBB var1, AxisAlignedBB var2) {
      AxisAlignedBB var3 = var1.func_111270_a(var2);
      List var4 = this.field_70170_p.func_72839_b(this, var3);
      if (!var4.isEmpty()) {
         for(int var5 = 0; var5 < var4.size(); ++var5) {
            Entity var6 = (Entity)var4.get(var5);
            if (var6 instanceof EntityLivingBase) {
               this.func_204804_d((EntityLivingBase)var6);
               this.field_204807_bs = 0;
               this.field_70159_w *= -0.2D;
               this.field_70181_x *= -0.2D;
               this.field_70179_y *= -0.2D;
               break;
            }
         }
      } else if (this.field_70123_F) {
         this.field_204807_bs = 0;
      }

      if (!this.field_70170_p.field_72995_K && this.field_204807_bs <= 0) {
         this.func_204802_c(4, false);
      }

   }

   protected void func_82167_n(Entity var1) {
      var1.func_70108_f(this);
   }

   protected void func_204804_d(EntityLivingBase var1) {
   }

   public void func_204803_n(int var1) {
      this.field_204807_bs = var1;
      if (!this.field_70170_p.field_72995_K) {
         this.func_204802_c(4, true);
      }

   }

   public boolean func_204805_cN() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184621_as) & 4) != 0;
   }

   public void func_184210_p() {
      Entity var1 = this.func_184187_bx();
      super.func_184210_p();
      if (var1 != null && var1 != this.func_184187_bx() && !this.field_70170_p.field_72995_K) {
         this.func_110145_l(var1);
      }

   }

   public void func_70098_U() {
      super.func_70098_U();
      this.field_70768_au = this.field_110154_aX;
      this.field_110154_aX = 0.0F;
      this.field_70143_R = 0.0F;
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.field_184623_bh = var1;
      this.field_184624_bi = var3;
      this.field_184625_bj = var5;
      this.field_184626_bk = (double)var7;
      this.field_70709_bj = (double)var8;
      this.field_70716_bi = var9;
   }

   public void func_208000_a(float var1, int var2) {
      this.field_208001_bq = (double)var1;
      this.field_208002_br = var2;
   }

   public void func_70637_d(boolean var1) {
      this.field_70703_bu = var1;
   }

   public void func_71001_a(Entity var1, int var2) {
      if (!var1.field_70128_L && !this.field_70170_p.field_72995_K) {
         EntityTracker var3 = ((WorldServer)this.field_70170_p).func_73039_n();
         if (var1 instanceof EntityItem || var1 instanceof EntityArrow || var1 instanceof EntityXPOrb) {
            var3.func_151247_a(var1, new SPacketCollectItem(var1.func_145782_y(), this.func_145782_y(), var2));
         }
      }

   }

   public boolean func_70685_l(Entity var1) {
      return this.field_70170_p.func_200259_a(new Vec3d(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v), new Vec3d(var1.field_70165_t, var1.field_70163_u + (double)var1.func_70047_e(), var1.field_70161_v), RayTraceFluidMode.NEVER, true, false) == null;
   }

   public float func_195046_g(float var1) {
      return var1 == 1.0F ? this.field_70759_as : this.field_70758_at + (this.field_70759_as - this.field_70758_at) * var1;
   }

   public float func_70678_g(float var1) {
      float var2 = this.field_70733_aJ - this.field_70732_aI;
      if (var2 < 0.0F) {
         ++var2;
      }

      return this.field_70732_aI + var2 * var1;
   }

   public boolean func_70613_aW() {
      return !this.field_70170_p.field_72995_K;
   }

   public boolean func_70067_L() {
      return !this.field_70128_L;
   }

   public boolean func_70104_M() {
      return this.func_70089_S() && !this.func_70617_f_();
   }

   protected void func_70018_K() {
      this.field_70133_I = this.field_70146_Z.nextDouble() >= this.func_110148_a(SharedMonsterAttributes.field_111266_c).func_111126_e();
   }

   public float func_70079_am() {
      return this.field_70759_as;
   }

   public void func_70034_d(float var1) {
      this.field_70759_as = var1;
   }

   public void func_181013_g(float var1) {
      this.field_70761_aq = var1;
   }

   public float func_110139_bj() {
      return this.field_110151_bq;
   }

   public void func_110149_m(float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      this.field_110151_bq = var1;
   }

   public void func_152111_bt() {
   }

   public void func_152112_bu() {
   }

   protected void func_175136_bO() {
      this.field_70752_e = true;
   }

   public abstract EnumHandSide func_184591_cq();

   public boolean func_184587_cr() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184621_as) & 1) > 0;
   }

   public EnumHand func_184600_cs() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184621_as) & 2) > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
   }

   protected void func_184608_ct() {
      if (this.func_184587_cr()) {
         if (this.func_184586_b(this.func_184600_cs()) == this.field_184627_bm) {
            if (this.func_184605_cv() <= 25 && this.func_184605_cv() % 4 == 0) {
               this.func_184584_a(this.field_184627_bm, 5);
            }

            if (--this.field_184628_bn == 0 && !this.field_70170_p.field_72995_K) {
               this.func_71036_o();
            }
         } else {
            this.func_184602_cy();
         }
      }

   }

   private void func_205014_p() {
      this.field_205018_bM = this.field_205017_bL;
      if (this.func_203007_ba()) {
         this.field_205017_bL = Math.min(1.0F, this.field_205017_bL + 0.09F);
      } else {
         this.field_205017_bL = Math.max(0.0F, this.field_205017_bL - 0.09F);
      }

   }

   protected void func_204802_c(int var1, boolean var2) {
      byte var3 = (Byte)this.field_70180_af.func_187225_a(field_184621_as);
      int var4;
      if (var2) {
         var4 = var3 | var1;
      } else {
         var4 = var3 & ~var1;
      }

      this.field_70180_af.func_187227_b(field_184621_as, (byte)var4);
   }

   public void func_184598_c(EnumHand var1) {
      ItemStack var2 = this.func_184586_b(var1);
      if (!var2.func_190926_b() && !this.func_184587_cr()) {
         this.field_184627_bm = var2;
         this.field_184628_bn = var2.func_77988_m();
         if (!this.field_70170_p.field_72995_K) {
            this.func_204802_c(1, true);
            this.func_204802_c(2, var1 == EnumHand.OFF_HAND);
         }

      }
   }

   public void func_184206_a(DataParameter<?> var1) {
      super.func_184206_a(var1);
      if (field_184621_as.equals(var1) && this.field_70170_p.field_72995_K) {
         if (this.func_184587_cr() && this.field_184627_bm.func_190926_b()) {
            this.field_184627_bm = this.func_184586_b(this.func_184600_cs());
            if (!this.field_184627_bm.func_190926_b()) {
               this.field_184628_bn = this.field_184627_bm.func_77988_m();
            }
         } else if (!this.func_184587_cr() && !this.field_184627_bm.func_190926_b()) {
            this.field_184627_bm = ItemStack.field_190927_a;
            this.field_184628_bn = 0;
         }
      }

   }

   public void func_200602_a(EntityAnchorArgument.Type var1, Vec3d var2) {
      super.func_200602_a(var1, var2);
      this.field_70758_at = this.field_70759_as;
      this.field_70761_aq = this.field_70759_as;
      this.field_70760_ar = this.field_70761_aq;
   }

   protected void func_184584_a(ItemStack var1, int var2) {
      if (!var1.func_190926_b() && this.func_184587_cr()) {
         if (var1.func_77975_n() == EnumAction.DRINK) {
            this.func_184185_a(SoundEvents.field_187664_bz, 0.5F, this.field_70170_p.field_73012_v.nextFloat() * 0.1F + 0.9F);
         }

         if (var1.func_77975_n() == EnumAction.EAT) {
            this.func_195062_a(var1, var2);
            this.func_184185_a(SoundEvents.field_187537_bA, 0.5F + 0.5F * (float)this.field_70146_Z.nextInt(2), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
         }

      }
   }

   private void func_195062_a(ItemStack var1, int var2) {
      for(int var3 = 0; var3 < var2; ++var3) {
         Vec3d var4 = new Vec3d(((double)this.field_70146_Z.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
         var4 = var4.func_178789_a(-this.field_70125_A * 0.017453292F);
         var4 = var4.func_178785_b(-this.field_70177_z * 0.017453292F);
         double var5 = (double)(-this.field_70146_Z.nextFloat()) * 0.6D - 0.3D;
         Vec3d var7 = new Vec3d(((double)this.field_70146_Z.nextFloat() - 0.5D) * 0.3D, var5, 0.6D);
         var7 = var7.func_178789_a(-this.field_70125_A * 0.017453292F);
         var7 = var7.func_178785_b(-this.field_70177_z * 0.017453292F);
         var7 = var7.func_72441_c(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v);
         this.field_70170_p.func_195594_a(new ItemParticleData(Particles.field_197591_B, var1), var7.field_72450_a, var7.field_72448_b, var7.field_72449_c, var4.field_72450_a, var4.field_72448_b + 0.05D, var4.field_72449_c);
      }

   }

   protected void func_71036_o() {
      if (!this.field_184627_bm.func_190926_b() && this.func_184587_cr()) {
         this.func_184584_a(this.field_184627_bm, 16);
         this.func_184611_a(this.func_184600_cs(), this.field_184627_bm.func_77950_b(this.field_70170_p, this));
         this.func_184602_cy();
      }

   }

   public ItemStack func_184607_cu() {
      return this.field_184627_bm;
   }

   public int func_184605_cv() {
      return this.field_184628_bn;
   }

   public int func_184612_cw() {
      return this.func_184587_cr() ? this.field_184627_bm.func_77988_m() - this.func_184605_cv() : 0;
   }

   public void func_184597_cx() {
      if (!this.field_184627_bm.func_190926_b()) {
         this.field_184627_bm.func_77974_b(this.field_70170_p, this, this.func_184605_cv());
      }

      this.func_184602_cy();
   }

   public void func_184602_cy() {
      if (!this.field_70170_p.field_72995_K) {
         this.func_204802_c(1, false);
      }

      this.field_184627_bm = ItemStack.field_190927_a;
      this.field_184628_bn = 0;
   }

   public boolean func_184585_cz() {
      if (this.func_184587_cr() && !this.field_184627_bm.func_190926_b()) {
         Item var1 = this.field_184627_bm.func_77973_b();
         if (var1.func_77661_b(this.field_184627_bm) != EnumAction.BLOCK) {
            return false;
         } else {
            return var1.func_77626_a(this.field_184627_bm) - this.field_184628_bn >= 5;
         }
      } else {
         return false;
      }
   }

   public boolean func_184613_cA() {
      return this.func_70083_f(7);
   }

   public int func_184599_cB() {
      return this.field_184629_bo;
   }

   public boolean func_184595_k(double var1, double var3, double var5) {
      double var7 = this.field_70165_t;
      double var9 = this.field_70163_u;
      double var11 = this.field_70161_v;
      this.field_70165_t = var1;
      this.field_70163_u = var3;
      this.field_70161_v = var5;
      boolean var13 = false;
      BlockPos var14 = new BlockPos(this);
      World var15 = this.field_70170_p;
      Random var16 = this.func_70681_au();
      boolean var17;
      if (var15.func_175667_e(var14)) {
         var17 = false;

         while(!var17 && var14.func_177956_o() > 0) {
            BlockPos var18 = var14.func_177977_b();
            IBlockState var19 = var15.func_180495_p(var18);
            if (var19.func_185904_a().func_76230_c()) {
               var17 = true;
            } else {
               --this.field_70163_u;
               var14 = var18;
            }
         }

         if (var17) {
            this.func_70634_a(this.field_70165_t, this.field_70163_u, this.field_70161_v);
            if (var15.func_195586_b(this, this.func_174813_aQ()) && !var15.func_72953_d(this.func_174813_aQ())) {
               var13 = true;
            }
         }
      }

      if (!var13) {
         this.func_70634_a(var7, var9, var11);
         return false;
      } else {
         var17 = true;

         for(int var30 = 0; var30 < 128; ++var30) {
            double var31 = (double)var30 / 127.0D;
            float var21 = (var16.nextFloat() - 0.5F) * 0.2F;
            float var22 = (var16.nextFloat() - 0.5F) * 0.2F;
            float var23 = (var16.nextFloat() - 0.5F) * 0.2F;
            double var24 = var7 + (this.field_70165_t - var7) * var31 + (var16.nextDouble() - 0.5D) * (double)this.field_70130_N * 2.0D;
            double var26 = var9 + (this.field_70163_u - var9) * var31 + var16.nextDouble() * (double)this.field_70131_O;
            double var28 = var11 + (this.field_70161_v - var11) * var31 + (var16.nextDouble() - 0.5D) * (double)this.field_70130_N * 2.0D;
            var15.func_195594_a(Particles.field_197599_J, var24, var26, var28, (double)var21, (double)var22, (double)var23);
         }

         if (this instanceof EntityCreature) {
            ((EntityCreature)this).func_70661_as().func_75499_g();
         }

         return true;
      }
   }

   public boolean func_184603_cC() {
      return true;
   }

   public boolean func_190631_cK() {
      return true;
   }

   public void func_191987_a(BlockPos var1, boolean var2) {
   }

   static {
      field_110157_c = (new AttributeModifier(field_110156_b, "Sprinting speed boost", 0.30000001192092896D, 2)).func_111168_a(false);
      field_184621_as = EntityDataManager.func_187226_a(EntityLivingBase.class, DataSerializers.field_187191_a);
      field_184632_c = EntityDataManager.func_187226_a(EntityLivingBase.class, DataSerializers.field_187193_c);
      field_184633_f = EntityDataManager.func_187226_a(EntityLivingBase.class, DataSerializers.field_187192_b);
      field_184634_g = EntityDataManager.func_187226_a(EntityLivingBase.class, DataSerializers.field_187198_h);
      field_184635_h = EntityDataManager.func_187226_a(EntityLivingBase.class, DataSerializers.field_187192_b);
   }
}
