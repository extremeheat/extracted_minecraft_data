package net.minecraft.entity;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityLiving extends EntityLivingBase {
   public int field_70757_a;
   protected int field_70728_aV;
   private EntityLookHelper field_70749_g;
   protected EntityMoveHelper field_70765_h;
   protected EntityJumpHelper field_70767_i;
   private EntityBodyHelper field_70762_j;
   protected PathNavigate field_70699_by;
   protected final EntityAITasks field_70714_bg;
   protected final EntityAITasks field_70715_bh;
   private EntityLivingBase field_70696_bz;
   private EntitySenses field_70723_bA;
   private ItemStack[] field_82182_bS = new ItemStack[5];
   protected float[] field_82174_bp = new float[5];
   private boolean field_82172_bs;
   private boolean field_82179_bU;
   private boolean field_110169_bv;
   private Entity field_110168_bw;
   private NBTTagCompound field_110170_bx;

   public EntityLiving(World var1) {
      super(var1);
      this.field_70714_bg = new EntityAITasks(var1 != null && var1.field_72984_F != null ? var1.field_72984_F : null);
      this.field_70715_bh = new EntityAITasks(var1 != null && var1.field_72984_F != null ? var1.field_72984_F : null);
      this.field_70749_g = new EntityLookHelper(this);
      this.field_70765_h = new EntityMoveHelper(this);
      this.field_70767_i = new EntityJumpHelper(this);
      this.field_70762_j = new EntityBodyHelper(this);
      this.field_70699_by = this.func_175447_b(var1);
      this.field_70723_bA = new EntitySenses(this);

      for(int var2 = 0; var2 < this.field_82174_bp.length; ++var2) {
         this.field_82174_bp[var2] = 0.085F;
      }

   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111265_b).func_111128_a(16.0D);
   }

   protected PathNavigate func_175447_b(World var1) {
      return new PathNavigateGround(this, var1);
   }

   public EntityLookHelper func_70671_ap() {
      return this.field_70749_g;
   }

   public EntityMoveHelper func_70605_aq() {
      return this.field_70765_h;
   }

   public EntityJumpHelper func_70683_ar() {
      return this.field_70767_i;
   }

   public PathNavigate func_70661_as() {
      return this.field_70699_by;
   }

   public EntitySenses func_70635_at() {
      return this.field_70723_bA;
   }

   public EntityLivingBase func_70638_az() {
      return this.field_70696_bz;
   }

   public void func_70624_b(EntityLivingBase var1) {
      this.field_70696_bz = var1;
   }

   public boolean func_70686_a(Class<? extends EntityLivingBase> var1) {
      return var1 != EntityGhast.class;
   }

   public void func_70615_aA() {
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(15, (byte)0);
   }

   public int func_70627_aG() {
      return 80;
   }

   public void func_70642_aH() {
      String var1 = this.func_70639_aQ();
      if (var1 != null) {
         this.func_85030_a(var1, this.func_70599_aP(), this.func_70647_i());
      }

   }

   public void func_70030_z() {
      super.func_70030_z();
      this.field_70170_p.field_72984_F.func_76320_a("mobBaseTick");
      if (this.func_70089_S() && this.field_70146_Z.nextInt(1000) < this.field_70757_a++) {
         this.field_70757_a = -this.func_70627_aG();
         this.func_70642_aH();
      }

      this.field_70170_p.field_72984_F.func_76319_b();
   }

   protected int func_70693_a(EntityPlayer var1) {
      if (this.field_70728_aV > 0) {
         int var2 = this.field_70728_aV;
         ItemStack[] var3 = this.func_70035_c();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var3[var4] != null && this.field_82174_bp[var4] <= 1.0F) {
               var2 += 1 + this.field_70146_Z.nextInt(3);
            }
         }

         return var2;
      } else {
         return this.field_70728_aV;
      }
   }

   public void func_70656_aK() {
      if (this.field_70170_p.field_72995_K) {
         for(int var1 = 0; var1 < 20; ++var1) {
            double var2 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var4 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var6 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var8 = 10.0D;
            this.field_70170_p.func_175688_a(EnumParticleTypes.EXPLOSION_NORMAL, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N - var2 * var8, this.field_70163_u + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O) - var4 * var8, this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N - var6 * var8, var2, var4, var6);
         }
      } else {
         this.field_70170_p.func_72960_a(this, (byte)20);
      }

   }

   public void func_70103_a(byte var1) {
      if (var1 == 20) {
         this.func_70656_aK();
      } else {
         super.func_70103_a(var1);
      }

   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K) {
         this.func_110159_bB();
      }

   }

   protected float func_110146_f(float var1, float var2) {
      this.field_70762_j.func_75664_a();
      return var2;
   }

   protected String func_70639_aQ() {
      return null;
   }

   protected Item func_146068_u() {
      return null;
   }

   protected void func_70628_a(boolean var1, int var2) {
      Item var3 = this.func_146068_u();
      if (var3 != null) {
         int var4 = this.field_70146_Z.nextInt(3);
         if (var2 > 0) {
            var4 += this.field_70146_Z.nextInt(var2 + 1);
         }

         for(int var5 = 0; var5 < var4; ++var5) {
            this.func_145779_a(var3, 1);
         }
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("CanPickUpLoot", this.func_98052_bS());
      var1.func_74757_a("PersistenceRequired", this.field_82179_bU);
      NBTTagList var2 = new NBTTagList();

      NBTTagCompound var4;
      for(int var3 = 0; var3 < this.field_82182_bS.length; ++var3) {
         var4 = new NBTTagCompound();
         if (this.field_82182_bS[var3] != null) {
            this.field_82182_bS[var3].func_77955_b(var4);
         }

         var2.func_74742_a(var4);
      }

      var1.func_74782_a("Equipment", var2);
      NBTTagList var6 = new NBTTagList();

      for(int var7 = 0; var7 < this.field_82174_bp.length; ++var7) {
         var6.func_74742_a(new NBTTagFloat(this.field_82174_bp[var7]));
      }

      var1.func_74782_a("DropChances", var6);
      var1.func_74757_a("Leashed", this.field_110169_bv);
      if (this.field_110168_bw != null) {
         var4 = new NBTTagCompound();
         if (this.field_110168_bw instanceof EntityLivingBase) {
            var4.func_74772_a("UUIDMost", this.field_110168_bw.func_110124_au().getMostSignificantBits());
            var4.func_74772_a("UUIDLeast", this.field_110168_bw.func_110124_au().getLeastSignificantBits());
         } else if (this.field_110168_bw instanceof EntityHanging) {
            BlockPos var5 = ((EntityHanging)this.field_110168_bw).func_174857_n();
            var4.func_74768_a("X", var5.func_177958_n());
            var4.func_74768_a("Y", var5.func_177956_o());
            var4.func_74768_a("Z", var5.func_177952_p());
         }

         var1.func_74782_a("Leash", var4);
      }

      if (this.func_175446_cd()) {
         var1.func_74757_a("NoAI", this.func_175446_cd());
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("CanPickUpLoot", 1)) {
         this.func_98053_h(var1.func_74767_n("CanPickUpLoot"));
      }

      this.field_82179_bU = var1.func_74767_n("PersistenceRequired");
      NBTTagList var2;
      int var3;
      if (var1.func_150297_b("Equipment", 9)) {
         var2 = var1.func_150295_c("Equipment", 10);

         for(var3 = 0; var3 < this.field_82182_bS.length; ++var3) {
            this.field_82182_bS[var3] = ItemStack.func_77949_a(var2.func_150305_b(var3));
         }
      }

      if (var1.func_150297_b("DropChances", 9)) {
         var2 = var1.func_150295_c("DropChances", 5);

         for(var3 = 0; var3 < var2.func_74745_c(); ++var3) {
            this.field_82174_bp[var3] = var2.func_150308_e(var3);
         }
      }

      this.field_110169_bv = var1.func_74767_n("Leashed");
      if (this.field_110169_bv && var1.func_150297_b("Leash", 10)) {
         this.field_110170_bx = var1.func_74775_l("Leash");
      }

      this.func_94061_f(var1.func_74767_n("NoAI"));
   }

   public void func_70657_f(float var1) {
      this.field_70701_bs = var1;
   }

   public void func_70659_e(float var1) {
      super.func_70659_e(var1);
      this.func_70657_f(var1);
   }

   public void func_70636_d() {
      super.func_70636_d();
      this.field_70170_p.field_72984_F.func_76320_a("looting");
      if (!this.field_70170_p.field_72995_K && this.func_98052_bS() && !this.field_70729_aU && this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
         List var1 = this.field_70170_p.func_72872_a(EntityItem.class, this.func_174813_aQ().func_72314_b(1.0D, 0.0D, 1.0D));
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            EntityItem var3 = (EntityItem)var2.next();
            if (!var3.field_70128_L && var3.func_92059_d() != null && !var3.func_174874_s()) {
               this.func_175445_a(var3);
            }
         }
      }

      this.field_70170_p.field_72984_F.func_76319_b();
   }

   protected void func_175445_a(EntityItem var1) {
      ItemStack var2 = var1.func_92059_d();
      int var3 = func_82159_b(var2);
      if (var3 > -1) {
         boolean var4 = true;
         ItemStack var5 = this.func_71124_b(var3);
         if (var5 != null) {
            if (var3 == 0) {
               if (var2.func_77973_b() instanceof ItemSword && !(var5.func_77973_b() instanceof ItemSword)) {
                  var4 = true;
               } else if (var2.func_77973_b() instanceof ItemSword && var5.func_77973_b() instanceof ItemSword) {
                  ItemSword var8 = (ItemSword)var2.func_77973_b();
                  ItemSword var10 = (ItemSword)var5.func_77973_b();
                  if (var8.func_150931_i() != var10.func_150931_i()) {
                     var4 = var8.func_150931_i() > var10.func_150931_i();
                  } else {
                     var4 = var2.func_77960_j() > var5.func_77960_j() || var2.func_77942_o() && !var5.func_77942_o();
                  }
               } else if (var2.func_77973_b() instanceof ItemBow && var5.func_77973_b() instanceof ItemBow) {
                  var4 = var2.func_77942_o() && !var5.func_77942_o();
               } else {
                  var4 = false;
               }
            } else if (var2.func_77973_b() instanceof ItemArmor && !(var5.func_77973_b() instanceof ItemArmor)) {
               var4 = true;
            } else if (var2.func_77973_b() instanceof ItemArmor && var5.func_77973_b() instanceof ItemArmor) {
               ItemArmor var6 = (ItemArmor)var2.func_77973_b();
               ItemArmor var7 = (ItemArmor)var5.func_77973_b();
               if (var6.field_77879_b != var7.field_77879_b) {
                  var4 = var6.field_77879_b > var7.field_77879_b;
               } else {
                  var4 = var2.func_77960_j() > var5.func_77960_j() || var2.func_77942_o() && !var5.func_77942_o();
               }
            } else {
               var4 = false;
            }
         }

         if (var4 && this.func_175448_a(var2)) {
            if (var5 != null && this.field_70146_Z.nextFloat() - 0.1F < this.field_82174_bp[var3]) {
               this.func_70099_a(var5, 0.0F);
            }

            if (var2.func_77973_b() == Items.field_151045_i && var1.func_145800_j() != null) {
               EntityPlayer var9 = this.field_70170_p.func_72924_a(var1.func_145800_j());
               if (var9 != null) {
                  var9.func_71029_a(AchievementList.field_150966_x);
               }
            }

            this.func_70062_b(var3, var2);
            this.field_82174_bp[var3] = 2.0F;
            this.field_82179_bU = true;
            this.func_71001_a(var1, 1);
            var1.func_70106_y();
         }
      }

   }

   protected boolean func_175448_a(ItemStack var1) {
      return true;
   }

   protected boolean func_70692_ba() {
      return true;
   }

   protected void func_70623_bb() {
      if (this.field_82179_bU) {
         this.field_70708_bq = 0;
      } else {
         EntityPlayer var1 = this.field_70170_p.func_72890_a(this, -1.0D);
         if (var1 != null) {
            double var2 = var1.field_70165_t - this.field_70165_t;
            double var4 = var1.field_70163_u - this.field_70163_u;
            double var6 = var1.field_70161_v - this.field_70161_v;
            double var8 = var2 * var2 + var4 * var4 + var6 * var6;
            if (this.func_70692_ba() && var8 > 16384.0D) {
               this.func_70106_y();
            }

            if (this.field_70708_bq > 600 && this.field_70146_Z.nextInt(800) == 0 && var8 > 1024.0D && this.func_70692_ba()) {
               this.func_70106_y();
            } else if (var8 < 1024.0D) {
               this.field_70708_bq = 0;
            }
         }

      }
   }

   protected final void func_70626_be() {
      ++this.field_70708_bq;
      this.field_70170_p.field_72984_F.func_76320_a("checkDespawn");
      this.func_70623_bb();
      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("sensing");
      this.field_70723_bA.func_75523_a();
      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("targetSelector");
      this.field_70715_bh.func_75774_a();
      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("goalSelector");
      this.field_70714_bg.func_75774_a();
      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("navigation");
      this.field_70699_by.func_75501_e();
      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("mob tick");
      this.func_70619_bc();
      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76320_a("controls");
      this.field_70170_p.field_72984_F.func_76320_a("move");
      this.field_70765_h.func_75641_c();
      this.field_70170_p.field_72984_F.func_76318_c("look");
      this.field_70749_g.func_75649_a();
      this.field_70170_p.field_72984_F.func_76318_c("jump");
      this.field_70767_i.func_75661_b();
      this.field_70170_p.field_72984_F.func_76319_b();
      this.field_70170_p.field_72984_F.func_76319_b();
   }

   protected void func_70619_bc() {
   }

   public int func_70646_bf() {
      return 40;
   }

   public void func_70625_a(Entity var1, float var2, float var3) {
      double var4 = var1.field_70165_t - this.field_70165_t;
      double var8 = var1.field_70161_v - this.field_70161_v;
      double var6;
      if (var1 instanceof EntityLivingBase) {
         EntityLivingBase var10 = (EntityLivingBase)var1;
         var6 = var10.field_70163_u + (double)var10.func_70047_e() - (this.field_70163_u + (double)this.func_70047_e());
      } else {
         var6 = (var1.func_174813_aQ().field_72338_b + var1.func_174813_aQ().field_72337_e) / 2.0D - (this.field_70163_u + (double)this.func_70047_e());
      }

      double var14 = (double)MathHelper.func_76133_a(var4 * var4 + var8 * var8);
      float var12 = (float)(MathHelper.func_181159_b(var8, var4) * 180.0D / 3.1415927410125732D) - 90.0F;
      float var13 = (float)(-(MathHelper.func_181159_b(var6, var14) * 180.0D / 3.1415927410125732D));
      this.field_70125_A = this.func_70663_b(this.field_70125_A, var13, var3);
      this.field_70177_z = this.func_70663_b(this.field_70177_z, var12, var2);
   }

   private float func_70663_b(float var1, float var2, float var3) {
      float var4 = MathHelper.func_76142_g(var2 - var1);
      if (var4 > var3) {
         var4 = var3;
      }

      if (var4 < -var3) {
         var4 = -var3;
      }

      return var1 + var4;
   }

   public boolean func_70601_bi() {
      return true;
   }

   public boolean func_70058_J() {
      return this.field_70170_p.func_72917_a(this.func_174813_aQ(), this) && this.field_70170_p.func_72945_a(this, this.func_174813_aQ()).isEmpty() && !this.field_70170_p.func_72953_d(this.func_174813_aQ());
   }

   public float func_70603_bj() {
      return 1.0F;
   }

   public int func_70641_bl() {
      return 4;
   }

   public int func_82143_as() {
      if (this.func_70638_az() == null) {
         return 3;
      } else {
         int var1 = (int)(this.func_110143_aJ() - this.func_110138_aP() * 0.33F);
         var1 -= (3 - this.field_70170_p.func_175659_aa().func_151525_a()) * 4;
         if (var1 < 0) {
            var1 = 0;
         }

         return var1 + 3;
      }
   }

   public ItemStack func_70694_bm() {
      return this.field_82182_bS[0];
   }

   public ItemStack func_71124_b(int var1) {
      return this.field_82182_bS[var1];
   }

   public ItemStack func_82169_q(int var1) {
      return this.field_82182_bS[var1 + 1];
   }

   public void func_70062_b(int var1, ItemStack var2) {
      this.field_82182_bS[var1] = var2;
   }

   public ItemStack[] func_70035_c() {
      return this.field_82182_bS;
   }

   protected void func_82160_b(boolean var1, int var2) {
      for(int var3 = 0; var3 < this.func_70035_c().length; ++var3) {
         ItemStack var4 = this.func_71124_b(var3);
         boolean var5 = this.field_82174_bp[var3] > 1.0F;
         if (var4 != null && (var1 || var5) && this.field_70146_Z.nextFloat() - (float)var2 * 0.01F < this.field_82174_bp[var3]) {
            if (!var5 && var4.func_77984_f()) {
               int var6 = Math.max(var4.func_77958_k() - 25, 1);
               int var7 = var4.func_77958_k() - this.field_70146_Z.nextInt(this.field_70146_Z.nextInt(var6) + 1);
               if (var7 > var6) {
                  var7 = var6;
               }

               if (var7 < 1) {
                  var7 = 1;
               }

               var4.func_77964_b(var7);
            }

            this.func_70099_a(var4, 0.0F);
         }
      }

   }

   protected void func_180481_a(DifficultyInstance var1) {
      if (this.field_70146_Z.nextFloat() < 0.15F * var1.func_180170_c()) {
         int var2 = this.field_70146_Z.nextInt(2);
         float var3 = this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD ? 0.1F : 0.25F;
         if (this.field_70146_Z.nextFloat() < 0.095F) {
            ++var2;
         }

         if (this.field_70146_Z.nextFloat() < 0.095F) {
            ++var2;
         }

         if (this.field_70146_Z.nextFloat() < 0.095F) {
            ++var2;
         }

         for(int var4 = 3; var4 >= 0; --var4) {
            ItemStack var5 = this.func_82169_q(var4);
            if (var4 < 3 && this.field_70146_Z.nextFloat() < var3) {
               break;
            }

            if (var5 == null) {
               Item var6 = func_82161_a(var4 + 1, var2);
               if (var6 != null) {
                  this.func_70062_b(var4 + 1, new ItemStack(var6));
               }
            }
         }
      }

   }

   public static int func_82159_b(ItemStack var0) {
      if (var0.func_77973_b() != Item.func_150898_a(Blocks.field_150423_aK) && var0.func_77973_b() != Items.field_151144_bL) {
         if (var0.func_77973_b() instanceof ItemArmor) {
            switch(((ItemArmor)var0.func_77973_b()).field_77881_a) {
            case 0:
               return 4;
            case 1:
               return 3;
            case 2:
               return 2;
            case 3:
               return 1;
            }
         }

         return 0;
      } else {
         return 4;
      }
   }

   public static Item func_82161_a(int var0, int var1) {
      switch(var0) {
      case 4:
         if (var1 == 0) {
            return Items.field_151024_Q;
         } else if (var1 == 1) {
            return Items.field_151169_ag;
         } else if (var1 == 2) {
            return Items.field_151020_U;
         } else if (var1 == 3) {
            return Items.field_151028_Y;
         } else if (var1 == 4) {
            return Items.field_151161_ac;
         }
      case 3:
         if (var1 == 0) {
            return Items.field_151027_R;
         } else if (var1 == 1) {
            return Items.field_151171_ah;
         } else if (var1 == 2) {
            return Items.field_151023_V;
         } else if (var1 == 3) {
            return Items.field_151030_Z;
         } else if (var1 == 4) {
            return Items.field_151163_ad;
         }
      case 2:
         if (var1 == 0) {
            return Items.field_151026_S;
         } else if (var1 == 1) {
            return Items.field_151149_ai;
         } else if (var1 == 2) {
            return Items.field_151022_W;
         } else if (var1 == 3) {
            return Items.field_151165_aa;
         } else if (var1 == 4) {
            return Items.field_151173_ae;
         }
      case 1:
         if (var1 == 0) {
            return Items.field_151021_T;
         } else if (var1 == 1) {
            return Items.field_151151_aj;
         } else if (var1 == 2) {
            return Items.field_151029_X;
         } else if (var1 == 3) {
            return Items.field_151167_ab;
         } else if (var1 == 4) {
            return Items.field_151175_af;
         }
      default:
         return null;
      }
   }

   protected void func_180483_b(DifficultyInstance var1) {
      float var2 = var1.func_180170_c();
      if (this.func_70694_bm() != null && this.field_70146_Z.nextFloat() < 0.25F * var2) {
         EnchantmentHelper.func_77504_a(this.field_70146_Z, this.func_70694_bm(), (int)(5.0F + var2 * (float)this.field_70146_Z.nextInt(18)));
      }

      for(int var3 = 0; var3 < 4; ++var3) {
         ItemStack var4 = this.func_82169_q(var3);
         if (var4 != null && this.field_70146_Z.nextFloat() < 0.5F * var2) {
            EnchantmentHelper.func_77504_a(this.field_70146_Z, var4, (int)(5.0F + var2 * (float)this.field_70146_Z.nextInt(18)));
         }
      }

   }

   public IEntityLivingData func_180482_a(DifficultyInstance var1, IEntityLivingData var2) {
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111121_a(new AttributeModifier("Random spawn bonus", this.field_70146_Z.nextGaussian() * 0.05D, 1));
      return var2;
   }

   public boolean func_82171_bF() {
      return false;
   }

   public void func_110163_bv() {
      this.field_82179_bU = true;
   }

   public void func_96120_a(int var1, float var2) {
      this.field_82174_bp[var1] = var2;
   }

   public boolean func_98052_bS() {
      return this.field_82172_bs;
   }

   public void func_98053_h(boolean var1) {
      this.field_82172_bs = var1;
   }

   public boolean func_104002_bU() {
      return this.field_82179_bU;
   }

   public final boolean func_130002_c(EntityPlayer var1) {
      if (this.func_110167_bD() && this.func_110166_bE() == var1) {
         this.func_110160_i(true, !var1.field_71075_bZ.field_75098_d);
         return true;
      } else {
         ItemStack var2 = var1.field_71071_by.func_70448_g();
         if (var2 != null && var2.func_77973_b() == Items.field_151058_ca && this.func_110164_bC()) {
            if (!(this instanceof EntityTameable) || !((EntityTameable)this).func_70909_n()) {
               this.func_110162_b(var1, true);
               --var2.field_77994_a;
               return true;
            }

            if (((EntityTameable)this).func_152114_e(var1)) {
               this.func_110162_b(var1, true);
               --var2.field_77994_a;
               return true;
            }
         }

         return this.func_70085_c(var1) ? true : super.func_130002_c(var1);
      }
   }

   protected boolean func_70085_c(EntityPlayer var1) {
      return false;
   }

   protected void func_110159_bB() {
      if (this.field_110170_bx != null) {
         this.func_110165_bF();
      }

      if (this.field_110169_bv) {
         if (!this.func_70089_S()) {
            this.func_110160_i(true, true);
         }

         if (this.field_110168_bw == null || this.field_110168_bw.field_70128_L) {
            this.func_110160_i(true, true);
         }
      }
   }

   public void func_110160_i(boolean var1, boolean var2) {
      if (this.field_110169_bv) {
         this.field_110169_bv = false;
         this.field_110168_bw = null;
         if (!this.field_70170_p.field_72995_K && var2) {
            this.func_145779_a(Items.field_151058_ca, 1);
         }

         if (!this.field_70170_p.field_72995_K && var1 && this.field_70170_p instanceof WorldServer) {
            ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new S1BPacketEntityAttach(1, this, (Entity)null));
         }
      }

   }

   public boolean func_110164_bC() {
      return !this.func_110167_bD() && !(this instanceof IMob);
   }

   public boolean func_110167_bD() {
      return this.field_110169_bv;
   }

   public Entity func_110166_bE() {
      return this.field_110168_bw;
   }

   public void func_110162_b(Entity var1, boolean var2) {
      this.field_110169_bv = true;
      this.field_110168_bw = var1;
      if (!this.field_70170_p.field_72995_K && var2 && this.field_70170_p instanceof WorldServer) {
         ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new S1BPacketEntityAttach(1, this, this.field_110168_bw));
      }

   }

   private void func_110165_bF() {
      if (this.field_110169_bv && this.field_110170_bx != null) {
         if (this.field_110170_bx.func_150297_b("UUIDMost", 4) && this.field_110170_bx.func_150297_b("UUIDLeast", 4)) {
            UUID var5 = new UUID(this.field_110170_bx.func_74763_f("UUIDMost"), this.field_110170_bx.func_74763_f("UUIDLeast"));
            List var6 = this.field_70170_p.func_72872_a(EntityLivingBase.class, this.func_174813_aQ().func_72314_b(10.0D, 10.0D, 10.0D));
            Iterator var3 = var6.iterator();

            while(var3.hasNext()) {
               EntityLivingBase var4 = (EntityLivingBase)var3.next();
               if (var4.func_110124_au().equals(var5)) {
                  this.field_110168_bw = var4;
                  break;
               }
            }
         } else if (this.field_110170_bx.func_150297_b("X", 99) && this.field_110170_bx.func_150297_b("Y", 99) && this.field_110170_bx.func_150297_b("Z", 99)) {
            BlockPos var1 = new BlockPos(this.field_110170_bx.func_74762_e("X"), this.field_110170_bx.func_74762_e("Y"), this.field_110170_bx.func_74762_e("Z"));
            EntityLeashKnot var2 = EntityLeashKnot.func_174863_b(this.field_70170_p, var1);
            if (var2 == null) {
               var2 = EntityLeashKnot.func_174862_a(this.field_70170_p, var1);
            }

            this.field_110168_bw = var2;
         } else {
            this.func_110160_i(false, true);
         }
      }

      this.field_110170_bx = null;
   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      int var3;
      if (var1 == 99) {
         var3 = 0;
      } else {
         var3 = var1 - 100 + 1;
         if (var3 < 0 || var3 >= this.field_82182_bS.length) {
            return false;
         }
      }

      if (var2 != null && func_82159_b(var2) != var3 && (var3 != 4 || !(var2.func_77973_b() instanceof ItemBlock))) {
         return false;
      } else {
         this.func_70062_b(var3, var2);
         return true;
      }
   }

   public boolean func_70613_aW() {
      return super.func_70613_aW() && !this.func_175446_cd();
   }

   public void func_94061_f(boolean var1) {
      this.field_70180_af.func_75692_b(15, Byte.valueOf((byte)(var1 ? 1 : 0)));
   }

   public boolean func_175446_cd() {
      return this.field_70180_af.func_75683_a(15) != 0;
   }

   public static enum SpawnPlacementType {
      ON_GROUND,
      IN_AIR,
      IN_WATER;

      private SpawnPlacementType() {
      }
   }
}
