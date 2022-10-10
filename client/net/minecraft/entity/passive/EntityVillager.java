package net.minecraft.entity.passive;

import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAIHarvestFarmland;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerInteract;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosestWithoutMoving;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.loot.LootTableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityVillager extends EntityAgeable implements INpc, IMerchant {
   private static final Logger field_190674_bx = LogManager.getLogger();
   private static final DataParameter<Integer> field_184752_bw;
   private int field_70955_e;
   private boolean field_70952_f;
   private boolean field_70953_g;
   private Village field_70954_d;
   @Nullable
   private EntityPlayer field_70962_h;
   @Nullable
   private MerchantRecipeList field_70963_i;
   private int field_70961_j;
   private boolean field_70959_by;
   private boolean field_175565_bs;
   private int field_70956_bz;
   private String field_82189_bL;
   private int field_175563_bv;
   private int field_175562_bw;
   private boolean field_82190_bM;
   private boolean field_175564_by;
   private final InventoryBasic field_175560_bz;
   private static final EntityVillager.ITradeList[][][][] field_175561_bA;

   public EntityVillager(World var1) {
      this(var1, 0);
   }

   public EntityVillager(World var1, int var2) {
      super(EntityType.field_200756_av, var1);
      this.field_175560_bz = new InventoryBasic(new TextComponentString("Items"), 8);
      this.func_70938_b(var2);
      this.func_70105_a(0.6F, 1.95F);
      ((PathNavigateGround)this.func_70661_as()).func_179688_b(true);
      this.func_98053_h(true);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
      this.field_70714_bg.func_75776_a(1, new EntityAIAvoidEntity(this, EntityEvoker.class, 12.0F, 0.8D, 0.8D));
      this.field_70714_bg.func_75776_a(1, new EntityAIAvoidEntity(this, EntityVindicator.class, 8.0F, 0.8D, 0.8D));
      this.field_70714_bg.func_75776_a(1, new EntityAIAvoidEntity(this, EntityVex.class, 8.0F, 0.6D, 0.6D));
      this.field_70714_bg.func_75776_a(1, new EntityAITradePlayer(this));
      this.field_70714_bg.func_75776_a(1, new EntityAILookAtTradePlayer(this));
      this.field_70714_bg.func_75776_a(2, new EntityAIMoveIndoors(this));
      this.field_70714_bg.func_75776_a(3, new EntityAIRestrictOpenDoor(this));
      this.field_70714_bg.func_75776_a(4, new EntityAIOpenDoor(this, true));
      this.field_70714_bg.func_75776_a(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
      this.field_70714_bg.func_75776_a(6, new EntityAIVillagerMate(this));
      this.field_70714_bg.func_75776_a(7, new EntityAIFollowGolem(this));
      this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosestWithoutMoving(this, EntityPlayer.class, 3.0F, 1.0F));
      this.field_70714_bg.func_75776_a(9, new EntityAIVillagerInteract(this));
      this.field_70714_bg.func_75776_a(9, new EntityAIWanderAvoidWater(this, 0.6D));
      this.field_70714_bg.func_75776_a(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
   }

   private void func_175552_ct() {
      if (!this.field_175564_by) {
         this.field_175564_by = true;
         if (this.func_70631_g_()) {
            this.field_70714_bg.func_75776_a(8, new EntityAIPlay(this, 0.32D));
         } else if (this.func_70946_n() == 0) {
            this.field_70714_bg.func_75776_a(6, new EntityAIHarvestFarmland(this, 0.6D));
         }

      }
   }

   protected void func_175500_n() {
      if (this.func_70946_n() == 0) {
         this.field_70714_bg.func_75776_a(8, new EntityAIHarvestFarmland(this, 0.6D));
      }

      super.func_175500_n();
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5D);
   }

   protected void func_70619_bc() {
      if (--this.field_70955_e <= 0) {
         BlockPos var1 = new BlockPos(this);
         this.field_70170_p.func_175714_ae().func_176060_a(var1);
         this.field_70955_e = 70 + this.field_70146_Z.nextInt(50);
         this.field_70954_d = this.field_70170_p.func_175714_ae().func_176056_a(var1, 32);
         if (this.field_70954_d == null) {
            this.func_110177_bN();
         } else {
            BlockPos var2 = this.field_70954_d.func_180608_a();
            this.func_175449_a(var2, this.field_70954_d.func_75568_b());
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
               Iterator var3 = this.field_70963_i.iterator();

               while(var3.hasNext()) {
                  MerchantRecipe var4 = (MerchantRecipe)var3.next();
                  if (var4.func_82784_g()) {
                     var4.func_82783_a(this.field_70146_Z.nextInt(6) + this.field_70146_Z.nextInt(6) + 2);
                  }
               }

               this.func_175554_cu();
               this.field_70959_by = false;
               if (this.field_70954_d != null && this.field_82189_bL != null) {
                  this.field_70170_p.func_72960_a(this, (byte)14);
                  this.field_70954_d.func_82688_a(this.field_82189_bL, 1);
               }
            }

            this.func_195064_c(new PotionEffect(MobEffects.field_76428_l, 200, 0));
         }
      }

      super.func_70619_bc();
   }

   public boolean func_184645_a(EntityPlayer var1, EnumHand var2) {
      ItemStack var3 = var1.func_184586_b(var2);
      boolean var4 = var3.func_77973_b() == Items.field_151057_cb;
      if (var4) {
         var3.func_111282_a(var1, this, var2);
         return true;
      } else if (var3.func_77973_b() != Items.field_196172_da && this.func_70089_S() && !this.func_70940_q() && !this.func_70631_g_()) {
         if (this.field_70963_i == null) {
            this.func_175554_cu();
         }

         if (var2 == EnumHand.MAIN_HAND) {
            var1.func_195066_a(StatList.field_188074_H);
         }

         if (!this.field_70170_p.field_72995_K && !this.field_70963_i.isEmpty()) {
            this.func_70932_a_(var1);
            var1.func_180472_a(this);
         } else if (this.field_70963_i.isEmpty()) {
            return super.func_184645_a(var1, var2);
         }

         return true;
      } else {
         return super.func_184645_a(var1, var2);
      }
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184752_bw, 0);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("Profession", this.func_70946_n());
      var1.func_74768_a("Riches", this.field_70956_bz);
      var1.func_74768_a("Career", this.field_175563_bv);
      var1.func_74768_a("CareerLevel", this.field_175562_bw);
      var1.func_74757_a("Willing", this.field_175565_bs);
      if (this.field_70963_i != null) {
         var1.func_74782_a("Offers", this.field_70963_i.func_77202_a());
      }

      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.field_175560_bz.func_70302_i_(); ++var3) {
         ItemStack var4 = this.field_175560_bz.func_70301_a(var3);
         if (!var4.func_190926_b()) {
            var2.add((INBTBase)var4.func_77955_b(new NBTTagCompound()));
         }
      }

      var1.func_74782_a("Inventory", var2);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_70938_b(var1.func_74762_e("Profession"));
      this.field_70956_bz = var1.func_74762_e("Riches");
      this.field_175563_bv = var1.func_74762_e("Career");
      this.field_175562_bw = var1.func_74762_e("CareerLevel");
      this.field_175565_bs = var1.func_74767_n("Willing");
      if (var1.func_150297_b("Offers", 10)) {
         NBTTagCompound var2 = var1.func_74775_l("Offers");
         this.field_70963_i = new MerchantRecipeList(var2);
      }

      NBTTagList var5 = var1.func_150295_c("Inventory", 10);

      for(int var3 = 0; var3 < var5.size(); ++var3) {
         ItemStack var4 = ItemStack.func_199557_a(var5.func_150305_b(var3));
         if (!var4.func_190926_b()) {
            this.field_175560_bz.func_174894_a(var4);
         }
      }

      this.func_98053_h(true);
      this.func_175552_ct();
   }

   public boolean func_70692_ba() {
      return false;
   }

   protected SoundEvent func_184639_G() {
      return this.func_70940_q() ? SoundEvents.field_187914_gn : SoundEvents.field_187910_gj;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187912_gl;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187911_gk;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_191184_at;
   }

   public void func_70938_b(int var1) {
      this.field_70180_af.func_187227_b(field_184752_bw, var1);
   }

   public int func_70946_n() {
      return Math.max((Integer)this.field_70180_af.func_187225_a(field_184752_bw) % 6, 0);
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

   public void func_70604_c(@Nullable EntityLivingBase var1) {
      super.func_70604_c(var1);
      if (this.field_70954_d != null && var1 != null) {
         this.field_70954_d.func_75575_a(var1);
         if (var1 instanceof EntityPlayer) {
            byte var2 = -1;
            if (this.func_70631_g_()) {
               var2 = -3;
            }

            this.field_70954_d.func_82688_a(((EntityPlayer)var1).func_146103_bH().getName(), var2);
            if (this.func_70089_S()) {
               this.field_70170_p.func_72960_a(this, (byte)13);
            }
         }
      }

   }

   public void func_70645_a(DamageSource var1) {
      if (this.field_70954_d != null) {
         Entity var2 = var1.func_76346_g();
         if (var2 != null) {
            if (var2 instanceof EntityPlayer) {
               this.field_70954_d.func_82688_a(((EntityPlayer)var2).func_146103_bH().getName(), -2);
            } else if (var2 instanceof IMob) {
               this.field_70954_d.func_82692_h();
            }
         } else {
            EntityPlayer var3 = this.field_70170_p.func_72890_a(this, 16.0D);
            if (var3 != null) {
               this.field_70954_d.func_82692_h();
            }
         }
      }

      super.func_70645_a(var1);
   }

   public void func_70932_a_(@Nullable EntityPlayer var1) {
      this.field_70962_h = var1;
   }

   @Nullable
   public EntityPlayer func_70931_l_() {
      return this.field_70962_h;
   }

   public boolean func_70940_q() {
      return this.field_70962_h != null;
   }

   public boolean func_175550_n(boolean var1) {
      if (!this.field_175565_bs && var1 && this.func_175553_cp()) {
         boolean var2 = false;

         for(int var3 = 0; var3 < this.field_175560_bz.func_70302_i_(); ++var3) {
            ItemStack var4 = this.field_175560_bz.func_70301_a(var3);
            if (!var4.func_190926_b()) {
               if (var4.func_77973_b() == Items.field_151025_P && var4.func_190916_E() >= 3) {
                  var2 = true;
                  this.field_175560_bz.func_70298_a(var3, 3);
               } else if ((var4.func_77973_b() == Items.field_151174_bG || var4.func_77973_b() == Items.field_151172_bF) && var4.func_190916_E() >= 12) {
                  var2 = true;
                  this.field_175560_bz.func_70298_a(var3, 12);
               }
            }

            if (var2) {
               this.field_70170_p.func_72960_a(this, (byte)18);
               this.field_175565_bs = true;
               break;
            }
         }
      }

      return this.field_175565_bs;
   }

   public void func_175549_o(boolean var1) {
      this.field_175565_bs = var1;
   }

   public void func_70933_a(MerchantRecipe var1) {
      var1.func_77399_f();
      this.field_70757_a = -this.func_70627_aG();
      this.func_184185_a(SoundEvents.field_187915_go, this.func_70599_aP(), this.func_70647_i());
      int var2 = 3 + this.field_70146_Z.nextInt(4);
      if (var1.func_180321_e() == 1 || this.field_70146_Z.nextInt(5) == 0) {
         this.field_70961_j = 40;
         this.field_70959_by = true;
         this.field_175565_bs = true;
         if (this.field_70962_h != null) {
            this.field_82189_bL = this.field_70962_h.func_146103_bH().getName();
         } else {
            this.field_82189_bL = null;
         }

         var2 += 5;
      }

      if (var1.func_77394_a().func_77973_b() == Items.field_151166_bC) {
         this.field_70956_bz += var1.func_77394_a().func_190916_E();
      }

      if (var1.func_180322_j()) {
         this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u + 0.5D, this.field_70161_v, var2));
      }

      if (this.field_70962_h instanceof EntityPlayerMP) {
         CriteriaTriggers.field_192138_r.func_192234_a((EntityPlayerMP)this.field_70962_h, this, var1.func_77397_d());
      }

   }

   public void func_110297_a_(ItemStack var1) {
      if (!this.field_70170_p.field_72995_K && this.field_70757_a > -this.func_70627_aG() + 20) {
         this.field_70757_a = -this.func_70627_aG();
         this.func_184185_a(var1.func_190926_b() ? SoundEvents.field_187913_gm : SoundEvents.field_187915_go, this.func_70599_aP(), this.func_70647_i());
      }

   }

   @Nullable
   public MerchantRecipeList func_70934_b(EntityPlayer var1) {
      if (this.field_70963_i == null) {
         this.func_175554_cu();
      }

      return this.field_70963_i;
   }

   private void func_175554_cu() {
      EntityVillager.ITradeList[][][] var1 = field_175561_bA[this.func_70946_n()];
      if (this.field_175563_bv != 0 && this.field_175562_bw != 0) {
         ++this.field_175562_bw;
      } else {
         this.field_175563_bv = this.field_70146_Z.nextInt(var1.length) + 1;
         this.field_175562_bw = 1;
      }

      if (this.field_70963_i == null) {
         this.field_70963_i = new MerchantRecipeList();
      }

      int var2 = this.field_175563_bv - 1;
      int var3 = this.field_175562_bw - 1;
      if (var2 >= 0 && var2 < var1.length) {
         EntityVillager.ITradeList[][] var4 = var1[var2];
         if (var3 >= 0 && var3 < var4.length) {
            EntityVillager.ITradeList[] var5 = var4[var3];
            EntityVillager.ITradeList[] var6 = var5;
            int var7 = var5.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               EntityVillager.ITradeList var9 = var6[var8];
               var9.func_190888_a(this, this.field_70963_i, this.field_70146_Z);
            }
         }

      }
   }

   public void func_70930_a(@Nullable MerchantRecipeList var1) {
   }

   public World func_190670_t_() {
      return this.field_70170_p;
   }

   public BlockPos func_190671_u_() {
      return new BlockPos(this);
   }

   public ITextComponent func_145748_c_() {
      Team var1 = this.func_96124_cp();
      ITextComponent var2 = this.func_200201_e();
      if (var2 != null) {
         return ScorePlayerTeam.func_200541_a(var1, var2).func_211710_a((var1x) -> {
            var1x.func_150209_a(this.func_174823_aP()).func_179989_a(this.func_189512_bd());
         });
      } else {
         if (this.field_70963_i == null) {
            this.func_175554_cu();
         }

         String var3 = null;
         switch(this.func_70946_n()) {
         case 0:
            if (this.field_175563_bv == 1) {
               var3 = "farmer";
            } else if (this.field_175563_bv == 2) {
               var3 = "fisherman";
            } else if (this.field_175563_bv == 3) {
               var3 = "shepherd";
            } else if (this.field_175563_bv == 4) {
               var3 = "fletcher";
            }
            break;
         case 1:
            if (this.field_175563_bv == 1) {
               var3 = "librarian";
            } else if (this.field_175563_bv == 2) {
               var3 = "cartographer";
            }
            break;
         case 2:
            var3 = "cleric";
            break;
         case 3:
            if (this.field_175563_bv == 1) {
               var3 = "armorer";
            } else if (this.field_175563_bv == 2) {
               var3 = "weapon_smith";
            } else if (this.field_175563_bv == 3) {
               var3 = "tool_smith";
            }
            break;
         case 4:
            if (this.field_175563_bv == 1) {
               var3 = "butcher";
            } else if (this.field_175563_bv == 2) {
               var3 = "leatherworker";
            }
            break;
         case 5:
            var3 = "nitwit";
         }

         if (var3 != null) {
            ITextComponent var4 = (new TextComponentTranslation(this.func_200600_R().func_210760_d() + '.' + var3, new Object[0])).func_211710_a((var1x) -> {
               var1x.func_150209_a(this.func_174823_aP()).func_179989_a(this.func_189512_bd());
            });
            if (var1 != null) {
               var4.func_211708_a(var1.func_178775_l());
            }

            return var4;
         } else {
            return super.func_145748_c_();
         }
      }
   }

   public float func_70047_e() {
      return this.func_70631_g_() ? 0.81F : 1.62F;
   }

   public void func_70103_a(byte var1) {
      if (var1 == 12) {
         this.func_195400_a(Particles.field_197633_z);
      } else if (var1 == 13) {
         this.func_195400_a(Particles.field_197609_b);
      } else if (var1 == 14) {
         this.func_195400_a(Particles.field_197632_y);
      } else {
         super.func_70103_a(var1);
      }

   }

   private void func_195400_a(IParticleData var1) {
      for(int var2 = 0; var2 < 5; ++var2) {
         double var3 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var5 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var7 = this.field_70146_Z.nextGaussian() * 0.02D;
         this.field_70170_p.func_195594_a(var1, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 1.0D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var3, var5, var7);
      }

   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      return this.func_190672_a(var1, var2, var3, true);
   }

   public IEntityLivingData func_190672_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3, boolean var4) {
      var2 = super.func_204210_a(var1, var2, var3);
      if (var4) {
         this.func_70938_b(this.field_70170_p.field_73012_v.nextInt(6));
      }

      this.func_175552_ct();
      this.func_175554_cu();
      return var2;
   }

   public void func_82187_q() {
      this.field_82190_bM = true;
   }

   public EntityVillager func_90011_a(EntityAgeable var1) {
      EntityVillager var2 = new EntityVillager(this.field_70170_p);
      var2.func_204210_a(this.field_70170_p.func_175649_E(new BlockPos(var2)), (IEntityLivingData)null, (NBTTagCompound)null);
      return var2;
   }

   public boolean func_184652_a(EntityPlayer var1) {
      return false;
   }

   public void func_70077_a(EntityLightningBolt var1) {
      if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
         EntityWitch var2 = new EntityWitch(this.field_70170_p);
         var2.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
         var2.func_204210_a(this.field_70170_p.func_175649_E(new BlockPos(var2)), (IEntityLivingData)null, (NBTTagCompound)null);
         var2.func_94061_f(this.func_175446_cd());
         if (this.func_145818_k_()) {
            var2.func_200203_b(this.func_200201_e());
            var2.func_174805_g(this.func_174833_aM());
         }

         this.field_70170_p.func_72838_d(var2);
         this.func_70106_y();
      }
   }

   public InventoryBasic func_175551_co() {
      return this.field_175560_bz;
   }

   protected void func_175445_a(EntityItem var1) {
      ItemStack var2 = var1.func_92059_d();
      Item var3 = var2.func_77973_b();
      if (this.func_175558_a(var3)) {
         ItemStack var4 = this.field_175560_bz.func_174894_a(var2);
         if (var4.func_190926_b()) {
            var1.func_70106_y();
         } else {
            var2.func_190920_e(var4.func_190916_E());
         }
      }

   }

   private boolean func_175558_a(Item var1) {
      return var1 == Items.field_151025_P || var1 == Items.field_151174_bG || var1 == Items.field_151172_bF || var1 == Items.field_151015_O || var1 == Items.field_151014_N || var1 == Items.field_185164_cV || var1 == Items.field_185163_cU;
   }

   public boolean func_175553_cp() {
      return this.func_175559_s(1);
   }

   public boolean func_175555_cq() {
      return this.func_175559_s(2);
   }

   public boolean func_175557_cr() {
      boolean var1 = this.func_70946_n() == 0;
      if (var1) {
         return !this.func_175559_s(5);
      } else {
         return !this.func_175559_s(1);
      }
   }

   private boolean func_175559_s(int var1) {
      boolean var2 = this.func_70946_n() == 0;

      for(int var3 = 0; var3 < this.field_175560_bz.func_70302_i_(); ++var3) {
         ItemStack var4 = this.field_175560_bz.func_70301_a(var3);
         Item var5 = var4.func_77973_b();
         int var6 = var4.func_190916_E();
         if (var5 == Items.field_151025_P && var6 >= 3 * var1 || var5 == Items.field_151174_bG && var6 >= 12 * var1 || var5 == Items.field_151172_bF && var6 >= 12 * var1 || var5 == Items.field_185164_cV && var6 >= 12 * var1) {
            return true;
         }

         if (var2 && var5 == Items.field_151015_O && var6 >= 9 * var1) {
            return true;
         }
      }

      return false;
   }

   public boolean func_175556_cs() {
      for(int var1 = 0; var1 < this.field_175560_bz.func_70302_i_(); ++var1) {
         Item var2 = this.field_175560_bz.func_70301_a(var1).func_77973_b();
         if (var2 == Items.field_151014_N || var2 == Items.field_151174_bG || var2 == Items.field_151172_bF || var2 == Items.field_185163_cU) {
            return true;
         }
      }

      return false;
   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      if (super.func_174820_d(var1, var2)) {
         return true;
      } else {
         int var3 = var1 - 300;
         if (var3 >= 0 && var3 < this.field_175560_bz.func_70302_i_()) {
            this.field_175560_bz.func_70299_a(var3, var2);
            return true;
         } else {
            return false;
         }
      }
   }

   // $FF: synthetic method
   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return this.func_90011_a(var1);
   }

   static {
      field_184752_bw = EntityDataManager.func_187226_a(EntityVillager.class, DataSerializers.field_187192_b);
      field_175561_bA = new EntityVillager.ITradeList[][][][]{{{{new EntityVillager.EmeraldForItems(Items.field_151015_O, new EntityVillager.PriceInfo(18, 22)), new EntityVillager.EmeraldForItems(Items.field_151174_bG, new EntityVillager.PriceInfo(15, 19)), new EntityVillager.EmeraldForItems(Items.field_151172_bF, new EntityVillager.PriceInfo(15, 19)), new EntityVillager.ListItemForEmeralds(Items.field_151025_P, new EntityVillager.PriceInfo(-4, -2))}, {new EntityVillager.EmeraldForItems(Blocks.field_150423_aK, new EntityVillager.PriceInfo(8, 13)), new EntityVillager.ListItemForEmeralds(Items.field_151158_bO, new EntityVillager.PriceInfo(-3, -2))}, {new EntityVillager.EmeraldForItems(Blocks.field_150440_ba, new EntityVillager.PriceInfo(7, 12)), new EntityVillager.ListItemForEmeralds(Items.field_151034_e, new EntityVillager.PriceInfo(-7, -5))}, {new EntityVillager.ListItemForEmeralds(Items.field_151106_aX, new EntityVillager.PriceInfo(-10, -6)), new EntityVillager.ListItemForEmeralds(Blocks.field_150414_aQ, new EntityVillager.PriceInfo(1, 1))}}, {{new EntityVillager.EmeraldForItems(Items.field_151007_F, new EntityVillager.PriceInfo(15, 20)), new EntityVillager.EmeraldForItems(Items.field_151044_h, new EntityVillager.PriceInfo(16, 24)), new EntityVillager.ItemAndEmeraldToItem(Items.field_196086_aW, new EntityVillager.PriceInfo(6, 6), Items.field_196102_ba, new EntityVillager.PriceInfo(6, 6)), new EntityVillager.ItemAndEmeraldToItem(Items.field_196087_aX, new EntityVillager.PriceInfo(6, 6), Items.field_196104_bb, new EntityVillager.PriceInfo(6, 6))}, {new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151112_aM, new EntityVillager.PriceInfo(7, 8))}}, {{new EntityVillager.EmeraldForItems(Blocks.field_196556_aL, new EntityVillager.PriceInfo(16, 22)), new EntityVillager.ListItemForEmeralds(Items.field_151097_aZ, new EntityVillager.PriceInfo(3, 4))}, {new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196556_aL), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196557_aM), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196558_aN), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196559_aO), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196560_aP), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196561_aQ), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196562_aR), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196563_aS), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196564_aT), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196565_aU), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196566_aV), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196567_aW), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196568_aX), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196569_aY), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196570_aZ), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Blocks.field_196602_ba), new EntityVillager.PriceInfo(1, 2))}}, {{new EntityVillager.EmeraldForItems(Items.field_151007_F, new EntityVillager.PriceInfo(15, 20)), new EntityVillager.ListItemForEmeralds(Items.field_151032_g, new EntityVillager.PriceInfo(-12, -8))}, {new EntityVillager.ListItemForEmeralds(Items.field_151031_f, new EntityVillager.PriceInfo(2, 3)), new EntityVillager.ItemAndEmeraldToItem(Blocks.field_150351_n, new EntityVillager.PriceInfo(10, 10), Items.field_151145_ak, new EntityVillager.PriceInfo(6, 10))}}}, {{{new EntityVillager.EmeraldForItems(Items.field_151121_aF, new EntityVillager.PriceInfo(24, 36)), new EntityVillager.ListEnchantedBookForEmeralds()}, {new EntityVillager.EmeraldForItems(Items.field_151122_aG, new EntityVillager.PriceInfo(8, 10)), new EntityVillager.ListItemForEmeralds(Items.field_151111_aL, new EntityVillager.PriceInfo(10, 12)), new EntityVillager.ListItemForEmeralds(Blocks.field_150342_X, new EntityVillager.PriceInfo(3, 4))}, {new EntityVillager.EmeraldForItems(Items.field_151164_bB, new EntityVillager.PriceInfo(2, 2)), new EntityVillager.ListItemForEmeralds(Items.field_151113_aN, new EntityVillager.PriceInfo(10, 12)), new EntityVillager.ListItemForEmeralds(Blocks.field_150359_w, new EntityVillager.PriceInfo(-5, -3))}, {new EntityVillager.ListEnchantedBookForEmeralds()}, {new EntityVillager.ListEnchantedBookForEmeralds()}, {new EntityVillager.ListItemForEmeralds(Items.field_151057_cb, new EntityVillager.PriceInfo(20, 22))}}, {{new EntityVillager.EmeraldForItems(Items.field_151121_aF, new EntityVillager.PriceInfo(24, 36))}, {new EntityVillager.EmeraldForItems(Items.field_151111_aL, new EntityVillager.PriceInfo(1, 1))}, {new EntityVillager.ListItemForEmeralds(Items.field_151148_bJ, new EntityVillager.PriceInfo(7, 11))}, {new EntityVillager.TreasureMapForEmeralds(new EntityVillager.PriceInfo(12, 20), "Monument", MapDecoration.Type.MONUMENT), new EntityVillager.TreasureMapForEmeralds(new EntityVillager.PriceInfo(16, 28), "Mansion", MapDecoration.Type.MANSION)}}}, {{{new EntityVillager.EmeraldForItems(Items.field_151078_bh, new EntityVillager.PriceInfo(36, 40)), new EntityVillager.EmeraldForItems(Items.field_151043_k, new EntityVillager.PriceInfo(8, 10))}, {new EntityVillager.ListItemForEmeralds(Items.field_151137_ax, new EntityVillager.PriceInfo(-4, -1)), new EntityVillager.ListItemForEmeralds(new ItemStack(Items.field_196128_bn), new EntityVillager.PriceInfo(-2, -1))}, {new EntityVillager.ListItemForEmeralds(Items.field_151079_bi, new EntityVillager.PriceInfo(4, 7)), new EntityVillager.ListItemForEmeralds(Blocks.field_150426_aN, new EntityVillager.PriceInfo(-3, -1))}, {new EntityVillager.ListItemForEmeralds(Items.field_151062_by, new EntityVillager.PriceInfo(3, 11))}}}, {{{new EntityVillager.EmeraldForItems(Items.field_151044_h, new EntityVillager.PriceInfo(16, 24)), new EntityVillager.ListItemForEmeralds(Items.field_151028_Y, new EntityVillager.PriceInfo(4, 6))}, {new EntityVillager.EmeraldForItems(Items.field_151042_j, new EntityVillager.PriceInfo(7, 9)), new EntityVillager.ListItemForEmeralds(Items.field_151030_Z, new EntityVillager.PriceInfo(10, 14))}, {new EntityVillager.EmeraldForItems(Items.field_151045_i, new EntityVillager.PriceInfo(3, 4)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151163_ad, new EntityVillager.PriceInfo(16, 19))}, {new EntityVillager.ListItemForEmeralds(Items.field_151029_X, new EntityVillager.PriceInfo(5, 7)), new EntityVillager.ListItemForEmeralds(Items.field_151022_W, new EntityVillager.PriceInfo(9, 11)), new EntityVillager.ListItemForEmeralds(Items.field_151020_U, new EntityVillager.PriceInfo(5, 7)), new EntityVillager.ListItemForEmeralds(Items.field_151023_V, new EntityVillager.PriceInfo(11, 15))}}, {{new EntityVillager.EmeraldForItems(Items.field_151044_h, new EntityVillager.PriceInfo(16, 24)), new EntityVillager.ListItemForEmeralds(Items.field_151036_c, new EntityVillager.PriceInfo(6, 8))}, {new EntityVillager.EmeraldForItems(Items.field_151042_j, new EntityVillager.PriceInfo(7, 9)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151040_l, new EntityVillager.PriceInfo(9, 10))}, {new EntityVillager.EmeraldForItems(Items.field_151045_i, new EntityVillager.PriceInfo(3, 4)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151048_u, new EntityVillager.PriceInfo(12, 15)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151056_x, new EntityVillager.PriceInfo(9, 12))}}, {{new EntityVillager.EmeraldForItems(Items.field_151044_h, new EntityVillager.PriceInfo(16, 24)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151037_a, new EntityVillager.PriceInfo(5, 7))}, {new EntityVillager.EmeraldForItems(Items.field_151042_j, new EntityVillager.PriceInfo(7, 9)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151035_b, new EntityVillager.PriceInfo(9, 11))}, {new EntityVillager.EmeraldForItems(Items.field_151045_i, new EntityVillager.PriceInfo(3, 4)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151046_w, new EntityVillager.PriceInfo(12, 15))}}}, {{{new EntityVillager.EmeraldForItems(Items.field_151147_al, new EntityVillager.PriceInfo(14, 18)), new EntityVillager.EmeraldForItems(Items.field_151076_bf, new EntityVillager.PriceInfo(14, 18))}, {new EntityVillager.EmeraldForItems(Items.field_151044_h, new EntityVillager.PriceInfo(16, 24)), new EntityVillager.ListItemForEmeralds(Items.field_151157_am, new EntityVillager.PriceInfo(-7, -5)), new EntityVillager.ListItemForEmeralds(Items.field_151077_bg, new EntityVillager.PriceInfo(-8, -6))}}, {{new EntityVillager.EmeraldForItems(Items.field_151116_aA, new EntityVillager.PriceInfo(9, 12)), new EntityVillager.ListItemForEmeralds(Items.field_151026_S, new EntityVillager.PriceInfo(2, 4))}, {new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151027_R, new EntityVillager.PriceInfo(7, 12))}, {new EntityVillager.ListItemForEmeralds(Items.field_151141_av, new EntityVillager.PriceInfo(8, 10))}}}, {new EntityVillager.ITradeList[0][]}};
   }

   static class ItemAndEmeraldToItem implements EntityVillager.ITradeList {
      public ItemStack field_199763_a;
      public EntityVillager.PriceInfo field_179409_b;
      public ItemStack field_199764_c;
      public EntityVillager.PriceInfo field_179408_d;

      public ItemAndEmeraldToItem(IItemProvider var1, EntityVillager.PriceInfo var2, Item var3, EntityVillager.PriceInfo var4) {
         super();
         this.field_199763_a = new ItemStack(var1);
         this.field_179409_b = var2;
         this.field_199764_c = new ItemStack(var3);
         this.field_179408_d = var4;
      }

      public void func_190888_a(IMerchant var1, MerchantRecipeList var2, Random var3) {
         int var4 = this.field_179409_b.func_179412_a(var3);
         int var5 = this.field_179408_d.func_179412_a(var3);
         var2.add(new MerchantRecipe(new ItemStack(this.field_199763_a.func_77973_b(), var4), new ItemStack(Items.field_151166_bC), new ItemStack(this.field_199764_c.func_77973_b(), var5)));
      }
   }

   static class TreasureMapForEmeralds implements EntityVillager.ITradeList {
      public EntityVillager.PriceInfo field_190889_a;
      public String field_190890_b;
      public MapDecoration.Type field_190891_c;

      public TreasureMapForEmeralds(EntityVillager.PriceInfo var1, String var2, MapDecoration.Type var3) {
         super();
         this.field_190889_a = var1;
         this.field_190890_b = var2;
         this.field_190891_c = var3;
      }

      public void func_190888_a(IMerchant var1, MerchantRecipeList var2, Random var3) {
         int var4 = this.field_190889_a.func_179412_a(var3);
         World var5 = var1.func_190670_t_();
         BlockPos var6 = var5.func_211157_a(this.field_190890_b, var1.func_190671_u_(), 100, true);
         if (var6 != null) {
            ItemStack var7 = ItemMap.func_195952_a(var5, var6.func_177958_n(), var6.func_177952_p(), (byte)2, true, true);
            ItemMap.func_190905_a(var5, var7);
            MapData.func_191094_a(var7, var6, "+", this.field_190891_c);
            var7.func_200302_a(new TextComponentTranslation("filled_map." + this.field_190890_b.toLowerCase(Locale.ROOT), new Object[0]));
            var2.add(new MerchantRecipe(new ItemStack(Items.field_151166_bC, var4), new ItemStack(Items.field_151111_aL), var7));
         }

      }
   }

   static class ListEnchantedBookForEmeralds implements EntityVillager.ITradeList {
      public ListEnchantedBookForEmeralds() {
         super();
      }

      public void func_190888_a(IMerchant var1, MerchantRecipeList var2, Random var3) {
         Enchantment var4 = (Enchantment)IRegistry.field_212628_q.func_186801_a(var3);
         int var5 = MathHelper.func_76136_a(var3, var4.func_77319_d(), var4.func_77325_b());
         ItemStack var6 = ItemEnchantedBook.func_92111_a(new EnchantmentData(var4, var5));
         int var7 = 2 + var3.nextInt(5 + var5 * 10) + 3 * var5;
         if (var4.func_185261_e()) {
            var7 *= 2;
         }

         if (var7 > 64) {
            var7 = 64;
         }

         var2.add(new MerchantRecipe(new ItemStack(Items.field_151122_aG), new ItemStack(Items.field_151166_bC, var7), var6));
      }
   }

   static class ListEnchantedItemForEmeralds implements EntityVillager.ITradeList {
      public ItemStack field_179407_a;
      public EntityVillager.PriceInfo field_179406_b;

      public ListEnchantedItemForEmeralds(Item var1, EntityVillager.PriceInfo var2) {
         super();
         this.field_179407_a = new ItemStack(var1);
         this.field_179406_b = var2;
      }

      public void func_190888_a(IMerchant var1, MerchantRecipeList var2, Random var3) {
         int var4 = 1;
         if (this.field_179406_b != null) {
            var4 = this.field_179406_b.func_179412_a(var3);
         }

         ItemStack var5 = new ItemStack(Items.field_151166_bC, var4);
         ItemStack var6 = EnchantmentHelper.func_77504_a(var3, new ItemStack(this.field_179407_a.func_77973_b()), 5 + var3.nextInt(15), false);
         var2.add(new MerchantRecipe(var5, var6));
      }
   }

   static class ListItemForEmeralds implements EntityVillager.ITradeList {
      public ItemStack field_179403_a;
      public EntityVillager.PriceInfo field_179402_b;

      public ListItemForEmeralds(Block var1, EntityVillager.PriceInfo var2) {
         this(new ItemStack(var1), var2);
      }

      public ListItemForEmeralds(Item var1, EntityVillager.PriceInfo var2) {
         this(new ItemStack(var1), var2);
      }

      public ListItemForEmeralds(ItemStack var1, EntityVillager.PriceInfo var2) {
         super();
         this.field_179403_a = var1;
         this.field_179402_b = var2;
      }

      public void func_190888_a(IMerchant var1, MerchantRecipeList var2, Random var3) {
         int var4 = 1;
         if (this.field_179402_b != null) {
            var4 = this.field_179402_b.func_179412_a(var3);
         }

         ItemStack var5;
         ItemStack var6;
         if (var4 < 0) {
            var5 = new ItemStack(Items.field_151166_bC);
            var6 = new ItemStack(this.field_179403_a.func_77973_b(), -var4);
         } else {
            var5 = new ItemStack(Items.field_151166_bC, var4);
            var6 = new ItemStack(this.field_179403_a.func_77973_b());
         }

         var2.add(new MerchantRecipe(var5, var6));
      }
   }

   static class EmeraldForItems implements EntityVillager.ITradeList {
      public Item field_179405_a;
      public EntityVillager.PriceInfo field_179404_b;

      public EmeraldForItems(IItemProvider var1, EntityVillager.PriceInfo var2) {
         super();
         this.field_179405_a = var1.func_199767_j();
         this.field_179404_b = var2;
      }

      public void func_190888_a(IMerchant var1, MerchantRecipeList var2, Random var3) {
         ItemStack var4 = new ItemStack(this.field_179405_a, this.field_179404_b == null ? 1 : this.field_179404_b.func_179412_a(var3));
         var2.add(new MerchantRecipe(var4, Items.field_151166_bC));
      }
   }

   interface ITradeList {
      void func_190888_a(IMerchant var1, MerchantRecipeList var2, Random var3);
   }

   static class PriceInfo extends Tuple<Integer, Integer> {
      public PriceInfo(int var1, int var2) {
         super(var1, var2);
         if (var2 < var1) {
            EntityVillager.field_190674_bx.warn("PriceRange({}, {}) invalid, {} smaller than {}", var1, var2, var2, var1);
         }

      }

      public int func_179412_a(Random var1) {
         return (Integer)this.func_76341_a() >= (Integer)this.func_76340_b() ? (Integer)this.func_76341_a() : (Integer)this.func_76341_a() + var1.nextInt((Integer)this.func_76340_b() - (Integer)this.func_76341_a() + 1);
      }
   }
}
