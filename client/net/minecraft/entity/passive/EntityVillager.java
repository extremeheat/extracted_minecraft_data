package net.minecraft.entity.passive;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.World;

public class EntityVillager extends EntityAgeable implements IMerchant, INpc {
   private int field_70955_e;
   private boolean field_70952_f;
   private boolean field_70953_g;
   Village field_70954_d;
   private EntityPlayer field_70962_h;
   private MerchantRecipeList field_70963_i;
   private int field_70961_j;
   private boolean field_70959_by;
   private int field_70956_bz;
   private String field_82189_bL;
   private boolean field_82190_bM;
   private float field_82191_bN;
   private static final Map field_70958_bB = new HashMap();
   private static final Map field_70960_bC = new HashMap();

   public EntityVillager(World var1) {
      this(var1, 0);
   }

   public EntityVillager(World var1, int var2) {
      super(var1);
      this.func_70938_b(var2);
      this.func_70105_a(0.6F, 1.8F);
      this.func_70661_as().func_75498_b(true);
      this.func_70661_as().func_75491_a(true);
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6, 0.6));
      this.field_70714_bg.func_75776_a(1, new EntityAITradePlayer(this));
      this.field_70714_bg.func_75776_a(1, new EntityAILookAtTradePlayer(this));
      this.field_70714_bg.func_75776_a(2, new EntityAIMoveIndoors(this));
      this.field_70714_bg.func_75776_a(3, new EntityAIRestrictOpenDoor(this));
      this.field_70714_bg.func_75776_a(4, new EntityAIOpenDoor(this, true));
      this.field_70714_bg.func_75776_a(5, new EntityAIMoveTowardsRestriction(this, 0.6));
      this.field_70714_bg.func_75776_a(6, new EntityAIVillagerMate(this));
      this.field_70714_bg.func_75776_a(7, new EntityAIFollowGolem(this));
      this.field_70714_bg.func_75776_a(8, new EntityAIPlay(this, 0.32));
      this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
      this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosest2(this, EntityVillager.class, 5.0F, 0.02F));
      this.field_70714_bg.func_75776_a(9, new EntityAIWander(this, 0.6));
      this.field_70714_bg.func_75776_a(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5);
   }

   @Override
   public boolean func_70650_aV() {
      return true;
   }

   @Override
   protected void func_70629_bd() {
      if (--this.field_70955_e <= 0) {
         this.field_70170_p
            .field_72982_D
            .func_75551_a(
               MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)
            );
         this.field_70955_e = 70 + this.field_70146_Z.nextInt(50);
         this.field_70954_d = this.field_70170_p
            .field_72982_D
            .func_75550_a(
               MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v), 32
            );
         if (this.field_70954_d == null) {
            this.func_110177_bN();
         } else {
            ChunkCoordinates var1 = this.field_70954_d.func_75577_a();
            this.func_110171_b(var1.field_71574_a, var1.field_71572_b, var1.field_71573_c, (int)((float)this.field_70954_d.func_75568_b() * 0.6F));
            if (this.field_82190_bM) {
               this.field_82190_bM = false;
               this.field_70954_d.func_82683_b(5);
            }
         }
      }

      if (!this.func_70940_q() && this.field_70961_j > 0) {
         --this.field_70961_j;
         if (this.field_70961_j <= 0) {
            if (this.field_70959_by) {
               if (this.field_70963_i.size() > 1) {
                  for(MerchantRecipe var2 : this.field_70963_i) {
                     if (var2.func_82784_g()) {
                        var2.func_82783_a(this.field_70146_Z.nextInt(6) + this.field_70146_Z.nextInt(6) + 2);
                     }
                  }
               }

               this.func_70950_c(1);
               this.field_70959_by = false;
               if (this.field_70954_d != null && this.field_82189_bL != null) {
                  this.field_70170_p.func_72960_a(this, (byte)14);
                  this.field_70954_d.func_82688_a(this.field_82189_bL, 1);
               }
            }

            this.func_70690_d(new PotionEffect(Potion.field_76428_l.field_76415_H, 200, 0));
         }
      }

      super.func_70629_bd();
   }

   @Override
   public boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      boolean var3 = var2 != null && var2.func_77973_b() == Items.field_151063_bx;
      if (!var3 && this.func_70089_S() && !this.func_70940_q() && !this.func_70631_g_()) {
         if (!this.field_70170_p.field_72995_K) {
            this.func_70932_a_(var1);
            var1.func_71030_a(this, this.func_94057_bL());
         }

         return true;
      } else {
         return super.func_70085_c(var1);
      }
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, 0);
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("Profession", this.func_70946_n());
      var1.func_74768_a("Riches", this.field_70956_bz);
      if (this.field_70963_i != null) {
         var1.func_74782_a("Offers", this.field_70963_i.func_77202_a());
      }
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_70938_b(var1.func_74762_e("Profession"));
      this.field_70956_bz = var1.func_74762_e("Riches");
      if (var1.func_150297_b("Offers", 10)) {
         NBTTagCompound var2 = var1.func_74775_l("Offers");
         this.field_70963_i = new MerchantRecipeList(var2);
      }
   }

   @Override
   protected boolean func_70692_ba() {
      return false;
   }

   @Override
   protected String func_70639_aQ() {
      return this.func_70940_q() ? "mob.villager.haggle" : "mob.villager.idle";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.villager.hit";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.villager.death";
   }

   public void func_70938_b(int var1) {
      this.field_70180_af.func_75692_b(16, var1);
   }

   public int func_70946_n() {
      return this.field_70180_af.func_75679_c(16);
   }

   public boolean func_70941_o() {
      return this.field_70952_f;
   }

   public void func_70947_e(boolean var1) {
      this.field_70952_f = var1;
   }

   public void func_70939_f(boolean var1) {
      this.field_70953_g = var1;
   }

   public boolean func_70945_p() {
      return this.field_70953_g;
   }

   @Override
   public void func_70604_c(EntityLivingBase var1) {
      super.func_70604_c(var1);
      if (this.field_70954_d != null && var1 != null) {
         this.field_70954_d.func_75575_a(var1);
         if (var1 instanceof EntityPlayer) {
            byte var2 = -1;
            if (this.func_70631_g_()) {
               var2 = -3;
            }

            this.field_70954_d.func_82688_a(var1.func_70005_c_(), var2);
            if (this.func_70089_S()) {
               this.field_70170_p.func_72960_a(this, (byte)13);
            }
         }
      }
   }

   @Override
   public void func_70645_a(DamageSource var1) {
      if (this.field_70954_d != null) {
         Entity var2 = var1.func_76346_g();
         if (var2 != null) {
            if (var2 instanceof EntityPlayer) {
               this.field_70954_d.func_82688_a(var2.func_70005_c_(), -2);
            } else if (var2 instanceof IMob) {
               this.field_70954_d.func_82692_h();
            }
         } else if (var2 == null) {
            EntityPlayer var3 = this.field_70170_p.func_72890_a(this, 16.0);
            if (var3 != null) {
               this.field_70954_d.func_82692_h();
            }
         }
      }

      super.func_70645_a(var1);
   }

   @Override
   public void func_70932_a_(EntityPlayer var1) {
      this.field_70962_h = var1;
   }

   @Override
   public EntityPlayer func_70931_l_() {
      return this.field_70962_h;
   }

   public boolean func_70940_q() {
      return this.field_70962_h != null;
   }

   @Override
   public void func_70933_a(MerchantRecipe var1) {
      var1.func_77399_f();
      this.field_70757_a = -this.func_70627_aG();
      this.func_85030_a("mob.villager.yes", this.func_70599_aP(), this.func_70647_i());
      if (var1.func_77393_a((MerchantRecipe)this.field_70963_i.get(this.field_70963_i.size() - 1))) {
         this.field_70961_j = 40;
         this.field_70959_by = true;
         if (this.field_70962_h != null) {
            this.field_82189_bL = this.field_70962_h.func_70005_c_();
         } else {
            this.field_82189_bL = null;
         }
      }

      if (var1.func_77394_a().func_77973_b() == Items.field_151166_bC) {
         this.field_70956_bz += var1.func_77394_a().field_77994_a;
      }
   }

   @Override
   public void func_110297_a_(ItemStack var1) {
      if (!this.field_70170_p.field_72995_K && this.field_70757_a > -this.func_70627_aG() + 20) {
         this.field_70757_a = -this.func_70627_aG();
         if (var1 != null) {
            this.func_85030_a("mob.villager.yes", this.func_70599_aP(), this.func_70647_i());
         } else {
            this.func_85030_a("mob.villager.no", this.func_70599_aP(), this.func_70647_i());
         }
      }
   }

   @Override
   public MerchantRecipeList func_70934_b(EntityPlayer var1) {
      if (this.field_70963_i == null) {
         this.func_70950_c(1);
      }

      return this.field_70963_i;
   }

   private float func_82188_j(float var1) {
      float var2 = var1 + this.field_82191_bN;
      return var2 > 0.9F ? 0.9F - (var2 - 0.9F) : var2;
   }

   private void func_70950_c(int var1) {
      if (this.field_70963_i != null) {
         this.field_82191_bN = MathHelper.func_76129_c((float)this.field_70963_i.size()) * 0.2F;
      } else {
         this.field_82191_bN = 0.0F;
      }

      MerchantRecipeList var2 = new MerchantRecipeList();
      switch(this.func_70946_n()) {
         case 0:
            func_146091_a(var2, Items.field_151015_O, this.field_70146_Z, this.func_82188_j(0.9F));
            func_146091_a(var2, Item.func_150898_a(Blocks.field_150325_L), this.field_70146_Z, this.func_82188_j(0.5F));
            func_146091_a(var2, Items.field_151076_bf, this.field_70146_Z, this.func_82188_j(0.5F));
            func_146091_a(var2, Items.field_151101_aQ, this.field_70146_Z, this.func_82188_j(0.4F));
            func_146089_b(var2, Items.field_151025_P, this.field_70146_Z, this.func_82188_j(0.9F));
            func_146089_b(var2, Items.field_151127_ba, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151034_e, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151106_aX, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151097_aZ, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151033_d, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151077_bg, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151032_g, this.field_70146_Z, this.func_82188_j(0.5F));
            if (this.field_70146_Z.nextFloat() < this.func_82188_j(0.5F)) {
               var2.add(
                  new MerchantRecipe(
                     new ItemStack(Blocks.field_150351_n, 10),
                     new ItemStack(Items.field_151166_bC),
                     new ItemStack(Items.field_151145_ak, 4 + this.field_70146_Z.nextInt(2), 0)
                  )
               );
            }
            break;
         case 1:
            func_146091_a(var2, Items.field_151121_aF, this.field_70146_Z, this.func_82188_j(0.8F));
            func_146091_a(var2, Items.field_151122_aG, this.field_70146_Z, this.func_82188_j(0.8F));
            func_146091_a(var2, Items.field_151164_bB, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Item.func_150898_a(Blocks.field_150342_X), this.field_70146_Z, this.func_82188_j(0.8F));
            func_146089_b(var2, Item.func_150898_a(Blocks.field_150359_w), this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151111_aL, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151113_aN, this.field_70146_Z, this.func_82188_j(0.2F));
            if (this.field_70146_Z.nextFloat() < this.func_82188_j(0.07F)) {
               Enchantment var8 = Enchantment.field_92090_c[this.field_70146_Z.nextInt(Enchantment.field_92090_c.length)];
               int var10 = MathHelper.func_76136_a(this.field_70146_Z, var8.func_77319_d(), var8.func_77325_b());
               ItemStack var11 = Items.field_151134_bR.func_92111_a(new EnchantmentData(var8, var10));
               int var12 = 2 + this.field_70146_Z.nextInt(5 + var10 * 10) + 3 * var10;
               var2.add(new MerchantRecipe(new ItemStack(Items.field_151122_aG), new ItemStack(Items.field_151166_bC, var12), var11));
            }
            break;
         case 2:
            func_146089_b(var2, Items.field_151061_bv, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151062_by, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151137_ax, this.field_70146_Z, this.func_82188_j(0.4F));
            func_146089_b(var2, Item.func_150898_a(Blocks.field_150426_aN), this.field_70146_Z, this.func_82188_j(0.3F));
            Item[] var3 = new Item[]{
               Items.field_151040_l,
               Items.field_151048_u,
               Items.field_151030_Z,
               Items.field_151163_ad,
               Items.field_151036_c,
               Items.field_151056_x,
               Items.field_151035_b,
               Items.field_151046_w
            };

            for(Item var7 : var3) {
               if (this.field_70146_Z.nextFloat() < this.func_82188_j(0.05F)) {
                  var2.add(
                     new MerchantRecipe(
                        new ItemStack(var7, 1, 0),
                        new ItemStack(Items.field_151166_bC, 2 + this.field_70146_Z.nextInt(3), 0),
                        EnchantmentHelper.func_77504_a(this.field_70146_Z, new ItemStack(var7, 1, 0), 5 + this.field_70146_Z.nextInt(15))
                     )
                  );
               }
            }
            break;
         case 3:
            func_146091_a(var2, Items.field_151044_h, this.field_70146_Z, this.func_82188_j(0.7F));
            func_146091_a(var2, Items.field_151042_j, this.field_70146_Z, this.func_82188_j(0.5F));
            func_146091_a(var2, Items.field_151043_k, this.field_70146_Z, this.func_82188_j(0.5F));
            func_146091_a(var2, Items.field_151045_i, this.field_70146_Z, this.func_82188_j(0.5F));
            func_146089_b(var2, Items.field_151040_l, this.field_70146_Z, this.func_82188_j(0.5F));
            func_146089_b(var2, Items.field_151048_u, this.field_70146_Z, this.func_82188_j(0.5F));
            func_146089_b(var2, Items.field_151036_c, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151056_x, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151035_b, this.field_70146_Z, this.func_82188_j(0.5F));
            func_146089_b(var2, Items.field_151046_w, this.field_70146_Z, this.func_82188_j(0.5F));
            func_146089_b(var2, Items.field_151037_a, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151047_v, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151019_K, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151012_L, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151167_ab, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151175_af, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151028_Y, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151161_ac, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151030_Z, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151163_ad, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151165_aa, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151173_ae, this.field_70146_Z, this.func_82188_j(0.2F));
            func_146089_b(var2, Items.field_151029_X, this.field_70146_Z, this.func_82188_j(0.1F));
            func_146089_b(var2, Items.field_151020_U, this.field_70146_Z, this.func_82188_j(0.1F));
            func_146089_b(var2, Items.field_151023_V, this.field_70146_Z, this.func_82188_j(0.1F));
            func_146089_b(var2, Items.field_151022_W, this.field_70146_Z, this.func_82188_j(0.1F));
            break;
         case 4:
            func_146091_a(var2, Items.field_151044_h, this.field_70146_Z, this.func_82188_j(0.7F));
            func_146091_a(var2, Items.field_151147_al, this.field_70146_Z, this.func_82188_j(0.5F));
            func_146091_a(var2, Items.field_151082_bd, this.field_70146_Z, this.func_82188_j(0.5F));
            func_146089_b(var2, Items.field_151141_av, this.field_70146_Z, this.func_82188_j(0.1F));
            func_146089_b(var2, Items.field_151027_R, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151021_T, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151024_Q, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151026_S, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151157_am, this.field_70146_Z, this.func_82188_j(0.3F));
            func_146089_b(var2, Items.field_151083_be, this.field_70146_Z, this.func_82188_j(0.3F));
      }

      if (var2.isEmpty()) {
         func_146091_a(var2, Items.field_151043_k, this.field_70146_Z, 1.0F);
      }

      Collections.shuffle(var2);
      if (this.field_70963_i == null) {
         this.field_70963_i = new MerchantRecipeList();
      }

      for(int var9 = 0; var9 < var1 && var9 < var2.size(); ++var9) {
         this.field_70963_i.func_77205_a((MerchantRecipe)var2.get(var9));
      }
   }

   @Override
   public void func_70930_a(MerchantRecipeList var1) {
   }

   private static void func_146091_a(MerchantRecipeList var0, Item var1, Random var2, float var3) {
      if (var2.nextFloat() < var3) {
         var0.add(new MerchantRecipe(func_146088_a(var1, var2), Items.field_151166_bC));
      }
   }

   private static ItemStack func_146088_a(Item var0, Random var1) {
      return new ItemStack(var0, func_146092_b(var0, var1), 0);
   }

   private static int func_146092_b(Item var0, Random var1) {
      Tuple var2 = (Tuple)field_70958_bB.get(var0);
      if (var2 == null) {
         return 1;
      } else {
         return var2.func_76341_a() >= var2.func_76340_b()
            ? var2.func_76341_a()
            : var2.func_76341_a() + var1.nextInt(var2.func_76340_b() - var2.func_76341_a());
      }
   }

   private static void func_146089_b(MerchantRecipeList var0, Item var1, Random var2, float var3) {
      if (var2.nextFloat() < var3) {
         int var4 = func_146090_c(var1, var2);
         ItemStack var5;
         ItemStack var6;
         if (var4 < 0) {
            var5 = new ItemStack(Items.field_151166_bC, 1, 0);
            var6 = new ItemStack(var1, -var4, 0);
         } else {
            var5 = new ItemStack(Items.field_151166_bC, var4, 0);
            var6 = new ItemStack(var1, 1, 0);
         }

         var0.add(new MerchantRecipe(var5, var6));
      }
   }

   private static int func_146090_c(Item var0, Random var1) {
      Tuple var2 = (Tuple)field_70960_bC.get(var0);
      if (var2 == null) {
         return 1;
      } else {
         return var2.func_76341_a() >= var2.func_76340_b()
            ? var2.func_76341_a()
            : var2.func_76341_a() + var1.nextInt(var2.func_76340_b() - var2.func_76341_a());
      }
   }

   @Override
   public void func_70103_a(byte var1) {
      if (var1 == 12) {
         this.func_70942_a("heart");
      } else if (var1 == 13) {
         this.func_70942_a("angryVillager");
      } else if (var1 == 14) {
         this.func_70942_a("happyVillager");
      } else {
         super.func_70103_a(var1);
      }
   }

   private void func_70942_a(String var1) {
      for(int var2 = 0; var2 < 5; ++var2) {
         double var3 = this.field_70146_Z.nextGaussian() * 0.02;
         double var5 = this.field_70146_Z.nextGaussian() * 0.02;
         double var7 = this.field_70146_Z.nextGaussian() * 0.02;
         this.field_70170_p
            .func_72869_a(
               var1,
               this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N,
               this.field_70163_u + 1.0 + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O),
               this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N,
               var3,
               var5,
               var7
            );
      }
   }

   @Override
   public IEntityLivingData func_110161_a(IEntityLivingData var1) {
      var1 = super.func_110161_a(var1);
      this.func_70938_b(this.field_70170_p.field_73012_v.nextInt(5));
      return var1;
   }

   public void func_82187_q() {
      this.field_82190_bM = true;
   }

   public EntityVillager func_90011_a(EntityAgeable var1) {
      EntityVillager var2 = new EntityVillager(this.field_70170_p);
      var2.func_110161_a(null);
      return var2;
   }

   @Override
   public boolean func_110164_bC() {
      return false;
   }

   static {
      field_70958_bB.put(Items.field_151044_h, new Tuple(16, 24));
      field_70958_bB.put(Items.field_151042_j, new Tuple(8, 10));
      field_70958_bB.put(Items.field_151043_k, new Tuple(8, 10));
      field_70958_bB.put(Items.field_151045_i, new Tuple(4, 6));
      field_70958_bB.put(Items.field_151121_aF, new Tuple(24, 36));
      field_70958_bB.put(Items.field_151122_aG, new Tuple(11, 13));
      field_70958_bB.put(Items.field_151164_bB, new Tuple(1, 1));
      field_70958_bB.put(Items.field_151079_bi, new Tuple(3, 4));
      field_70958_bB.put(Items.field_151061_bv, new Tuple(2, 3));
      field_70958_bB.put(Items.field_151147_al, new Tuple(14, 18));
      field_70958_bB.put(Items.field_151082_bd, new Tuple(14, 18));
      field_70958_bB.put(Items.field_151076_bf, new Tuple(14, 18));
      field_70958_bB.put(Items.field_151101_aQ, new Tuple(9, 13));
      field_70958_bB.put(Items.field_151014_N, new Tuple(34, 48));
      field_70958_bB.put(Items.field_151081_bc, new Tuple(30, 38));
      field_70958_bB.put(Items.field_151080_bb, new Tuple(30, 38));
      field_70958_bB.put(Items.field_151015_O, new Tuple(18, 22));
      field_70958_bB.put(Item.func_150898_a(Blocks.field_150325_L), new Tuple(14, 22));
      field_70958_bB.put(Items.field_151078_bh, new Tuple(36, 64));
      field_70960_bC.put(Items.field_151033_d, new Tuple(3, 4));
      field_70960_bC.put(Items.field_151097_aZ, new Tuple(3, 4));
      field_70960_bC.put(Items.field_151040_l, new Tuple(7, 11));
      field_70960_bC.put(Items.field_151048_u, new Tuple(12, 14));
      field_70960_bC.put(Items.field_151036_c, new Tuple(6, 8));
      field_70960_bC.put(Items.field_151056_x, new Tuple(9, 12));
      field_70960_bC.put(Items.field_151035_b, new Tuple(7, 9));
      field_70960_bC.put(Items.field_151046_w, new Tuple(10, 12));
      field_70960_bC.put(Items.field_151037_a, new Tuple(4, 6));
      field_70960_bC.put(Items.field_151047_v, new Tuple(7, 8));
      field_70960_bC.put(Items.field_151019_K, new Tuple(4, 6));
      field_70960_bC.put(Items.field_151012_L, new Tuple(7, 8));
      field_70960_bC.put(Items.field_151167_ab, new Tuple(4, 6));
      field_70960_bC.put(Items.field_151175_af, new Tuple(7, 8));
      field_70960_bC.put(Items.field_151028_Y, new Tuple(4, 6));
      field_70960_bC.put(Items.field_151161_ac, new Tuple(7, 8));
      field_70960_bC.put(Items.field_151030_Z, new Tuple(10, 14));
      field_70960_bC.put(Items.field_151163_ad, new Tuple(16, 19));
      field_70960_bC.put(Items.field_151165_aa, new Tuple(8, 10));
      field_70960_bC.put(Items.field_151173_ae, new Tuple(11, 14));
      field_70960_bC.put(Items.field_151029_X, new Tuple(5, 7));
      field_70960_bC.put(Items.field_151020_U, new Tuple(5, 7));
      field_70960_bC.put(Items.field_151023_V, new Tuple(11, 15));
      field_70960_bC.put(Items.field_151022_W, new Tuple(9, 11));
      field_70960_bC.put(Items.field_151025_P, new Tuple(-4, -2));
      field_70960_bC.put(Items.field_151127_ba, new Tuple(-8, -4));
      field_70960_bC.put(Items.field_151034_e, new Tuple(-8, -4));
      field_70960_bC.put(Items.field_151106_aX, new Tuple(-10, -7));
      field_70960_bC.put(Item.func_150898_a(Blocks.field_150359_w), new Tuple(-5, -3));
      field_70960_bC.put(Item.func_150898_a(Blocks.field_150342_X), new Tuple(3, 4));
      field_70960_bC.put(Items.field_151027_R, new Tuple(4, 5));
      field_70960_bC.put(Items.field_151021_T, new Tuple(2, 4));
      field_70960_bC.put(Items.field_151024_Q, new Tuple(2, 4));
      field_70960_bC.put(Items.field_151026_S, new Tuple(2, 4));
      field_70960_bC.put(Items.field_151141_av, new Tuple(6, 8));
      field_70960_bC.put(Items.field_151062_by, new Tuple(-4, -1));
      field_70960_bC.put(Items.field_151137_ax, new Tuple(-4, -1));
      field_70960_bC.put(Items.field_151111_aL, new Tuple(10, 12));
      field_70960_bC.put(Items.field_151113_aN, new Tuple(10, 12));
      field_70960_bC.put(Item.func_150898_a(Blocks.field_150426_aN), new Tuple(-3, -1));
      field_70960_bC.put(Items.field_151157_am, new Tuple(-7, -5));
      field_70960_bC.put(Items.field_151083_be, new Tuple(-7, -5));
      field_70960_bC.put(Items.field_151077_bg, new Tuple(-8, -6));
      field_70960_bC.put(Items.field_151061_bv, new Tuple(7, 11));
      field_70960_bC.put(Items.field_151032_g, new Tuple(-12, -8));
   }
}
