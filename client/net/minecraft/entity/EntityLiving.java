package net.minecraft.entity;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntitySenses;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public abstract class EntityLiving extends EntityLivingBase {
   private static final DataParameter<Byte> field_184654_a;
   public int field_70757_a;
   protected int field_70728_aV;
   protected EntityLookHelper field_70749_g;
   protected EntityMoveHelper field_70765_h;
   protected EntityJumpHelper field_70767_i;
   private final EntityBodyHelper field_70762_j;
   protected PathNavigate field_70699_by;
   protected final EntityAITasks field_70714_bg;
   protected final EntityAITasks field_70715_bh;
   private EntityLivingBase field_70696_bz;
   private final EntitySenses field_70723_bA;
   private final NonNullList<ItemStack> field_184656_bv;
   protected float[] field_82174_bp;
   private final NonNullList<ItemStack> field_184657_bw;
   protected float[] field_184655_bs;
   private boolean field_82172_bs;
   private boolean field_82179_bU;
   private final Map<PathNodeType, Float> field_184658_bz;
   private ResourceLocation field_184659_bA;
   private long field_184653_bB;
   private boolean field_110169_bv;
   private Entity field_110168_bw;
   private NBTTagCompound field_110170_bx;

   protected EntityLiving(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_184656_bv = NonNullList.func_191197_a(2, ItemStack.field_190927_a);
      this.field_82174_bp = new float[2];
      this.field_184657_bw = NonNullList.func_191197_a(4, ItemStack.field_190927_a);
      this.field_184655_bs = new float[4];
      this.field_184658_bz = Maps.newEnumMap(PathNodeType.class);
      this.field_70714_bg = new EntityAITasks(var2 != null && var2.field_72984_F != null ? var2.field_72984_F : null);
      this.field_70715_bh = new EntityAITasks(var2 != null && var2.field_72984_F != null ? var2.field_72984_F : null);
      this.field_70749_g = new EntityLookHelper(this);
      this.field_70765_h = new EntityMoveHelper(this);
      this.field_70767_i = new EntityJumpHelper(this);
      this.field_70762_j = this.func_184650_s();
      this.field_70699_by = this.func_175447_b(var2);
      this.field_70723_bA = new EntitySenses(this);
      Arrays.fill(this.field_184655_bs, 0.085F);
      Arrays.fill(this.field_82174_bp, 0.085F);
      if (var2 != null && !var2.field_72995_K) {
         this.func_184651_r();
      }

   }

   protected void func_184651_r() {
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111265_b).func_111128_a(16.0D);
   }

   protected PathNavigate func_175447_b(World var1) {
      return new PathNavigateGround(this, var1);
   }

   public float func_184643_a(PathNodeType var1) {
      Float var2 = (Float)this.field_184658_bz.get(var1);
      return var2 == null ? var1.func_186289_a() : var2;
   }

   public void func_184644_a(PathNodeType var1, float var2) {
      this.field_184658_bz.put(var1, var2);
   }

   protected EntityBodyHelper func_184650_s() {
      return new EntityBodyHelper(this);
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

   @Nullable
   public EntityLivingBase func_70638_az() {
      return this.field_70696_bz;
   }

   public void func_70624_b(@Nullable EntityLivingBase var1) {
      this.field_70696_bz = var1;
   }

   public boolean func_70686_a(Class<? extends EntityLivingBase> var1) {
      return var1 != EntityGhast.class;
   }

   public void func_70615_aA() {
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184654_a, (byte)0);
   }

   public int func_70627_aG() {
      return 80;
   }

   public void func_70642_aH() {
      SoundEvent var1 = this.func_184639_G();
      if (var1 != null) {
         this.func_184185_a(var1, this.func_70599_aP(), this.func_70647_i());
      }

   }

   public void func_70030_z() {
      super.func_70030_z();
      this.field_70170_p.field_72984_F.func_76320_a("mobBaseTick");
      if (this.func_70089_S() && this.field_70146_Z.nextInt(1000) < this.field_70757_a++) {
         this.func_175456_n();
         this.func_70642_aH();
      }

      this.field_70170_p.field_72984_F.func_76319_b();
   }

   protected void func_184581_c(DamageSource var1) {
      this.func_175456_n();
      super.func_184581_c(var1);
   }

   private void func_175456_n() {
      this.field_70757_a = -this.func_70627_aG();
   }

   protected int func_70693_a(EntityPlayer var1) {
      if (this.field_70728_aV > 0) {
         int var2 = this.field_70728_aV;

         int var3;
         for(var3 = 0; var3 < this.field_184657_bw.size(); ++var3) {
            if (!((ItemStack)this.field_184657_bw.get(var3)).func_190926_b() && this.field_184655_bs[var3] <= 1.0F) {
               var2 += 1 + this.field_70146_Z.nextInt(3);
            }
         }

         for(var3 = 0; var3 < this.field_184656_bv.size(); ++var3) {
            if (!((ItemStack)this.field_184656_bv.get(var3)).func_190926_b() && this.field_82174_bp[var3] <= 1.0F) {
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
            this.field_70170_p.func_195594_a(Particles.field_197598_I, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N - var2 * 10.0D, this.field_70163_u + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O) - var4 * 10.0D, this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N - var6 * 10.0D, var2, var4, var6);
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
         if (this.field_70173_aa % 5 == 0) {
            boolean var1 = !(this.func_184179_bs() instanceof EntityLiving);
            boolean var2 = !(this.func_184187_bx() instanceof EntityBoat);
            this.field_70714_bg.func_188527_a(1, var1);
            this.field_70714_bg.func_188527_a(4, var1 && var2);
            this.field_70714_bg.func_188527_a(2, var1);
         }
      }

   }

   protected float func_110146_f(float var1, float var2) {
      this.field_70762_j.func_75664_a();
      return var2;
   }

   @Nullable
   protected SoundEvent func_184639_G() {
      return null;
   }

   @Nullable
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
            this.func_199703_a(var3);
         }
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("CanPickUpLoot", this.func_98052_bS());
      var1.func_74757_a("PersistenceRequired", this.field_82179_bU);
      NBTTagList var2 = new NBTTagList();

      NBTTagCompound var5;
      for(Iterator var3 = this.field_184657_bw.iterator(); var3.hasNext(); var2.add((INBTBase)var5)) {
         ItemStack var4 = (ItemStack)var3.next();
         var5 = new NBTTagCompound();
         if (!var4.func_190926_b()) {
            var4.func_77955_b(var5);
         }
      }

      var1.func_74782_a("ArmorItems", var2);
      NBTTagList var10 = new NBTTagList();

      NBTTagCompound var6;
      for(Iterator var11 = this.field_184656_bv.iterator(); var11.hasNext(); var10.add((INBTBase)var6)) {
         ItemStack var13 = (ItemStack)var11.next();
         var6 = new NBTTagCompound();
         if (!var13.func_190926_b()) {
            var13.func_77955_b(var6);
         }
      }

      var1.func_74782_a("HandItems", var10);
      NBTTagList var12 = new NBTTagList();
      float[] var14 = this.field_184655_bs;
      int var16 = var14.length;

      int var7;
      for(var7 = 0; var7 < var16; ++var7) {
         float var8 = var14[var7];
         var12.add((INBTBase)(new NBTTagFloat(var8)));
      }

      var1.func_74782_a("ArmorDropChances", var12);
      NBTTagList var15 = new NBTTagList();
      float[] var17 = this.field_82174_bp;
      var7 = var17.length;

      for(int var19 = 0; var19 < var7; ++var19) {
         float var9 = var17[var19];
         var15.add((INBTBase)(new NBTTagFloat(var9)));
      }

      var1.func_74782_a("HandDropChances", var15);
      var1.func_74757_a("Leashed", this.field_110169_bv);
      if (this.field_110168_bw != null) {
         var6 = new NBTTagCompound();
         if (this.field_110168_bw instanceof EntityLivingBase) {
            UUID var18 = this.field_110168_bw.func_110124_au();
            var6.func_186854_a("UUID", var18);
         } else if (this.field_110168_bw instanceof EntityHanging) {
            BlockPos var20 = ((EntityHanging)this.field_110168_bw).func_174857_n();
            var6.func_74768_a("X", var20.func_177958_n());
            var6.func_74768_a("Y", var20.func_177956_o());
            var6.func_74768_a("Z", var20.func_177952_p());
         }

         var1.func_74782_a("Leash", var6);
      }

      var1.func_74757_a("LeftHanded", this.func_184638_cS());
      if (this.field_184659_bA != null) {
         var1.func_74778_a("DeathLootTable", this.field_184659_bA.toString());
         if (this.field_184653_bB != 0L) {
            var1.func_74772_a("DeathLootTableSeed", this.field_184653_bB);
         }
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
      if (var1.func_150297_b("ArmorItems", 9)) {
         var2 = var1.func_150295_c("ArmorItems", 10);

         for(var3 = 0; var3 < this.field_184657_bw.size(); ++var3) {
            this.field_184657_bw.set(var3, ItemStack.func_199557_a(var2.func_150305_b(var3)));
         }
      }

      if (var1.func_150297_b("HandItems", 9)) {
         var2 = var1.func_150295_c("HandItems", 10);

         for(var3 = 0; var3 < this.field_184656_bv.size(); ++var3) {
            this.field_184656_bv.set(var3, ItemStack.func_199557_a(var2.func_150305_b(var3)));
         }
      }

      if (var1.func_150297_b("ArmorDropChances", 9)) {
         var2 = var1.func_150295_c("ArmorDropChances", 5);

         for(var3 = 0; var3 < var2.size(); ++var3) {
            this.field_184655_bs[var3] = var2.func_150308_e(var3);
         }
      }

      if (var1.func_150297_b("HandDropChances", 9)) {
         var2 = var1.func_150295_c("HandDropChances", 5);

         for(var3 = 0; var3 < var2.size(); ++var3) {
            this.field_82174_bp[var3] = var2.func_150308_e(var3);
         }
      }

      this.field_110169_bv = var1.func_74767_n("Leashed");
      if (this.field_110169_bv && var1.func_150297_b("Leash", 10)) {
         this.field_110170_bx = var1.func_74775_l("Leash");
      }

      this.func_184641_n(var1.func_74767_n("LeftHanded"));
      if (var1.func_150297_b("DeathLootTable", 8)) {
         this.field_184659_bA = new ResourceLocation(var1.func_74779_i("DeathLootTable"));
         this.field_184653_bB = var1.func_74763_f("DeathLootTableSeed");
      }

      this.func_94061_f(var1.func_74767_n("NoAI"));
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return null;
   }

   protected void func_184610_a(boolean var1, int var2, DamageSource var3) {
      ResourceLocation var4 = this.field_184659_bA;
      if (var4 == null) {
         var4 = this.func_184647_J();
      }

      if (var4 != null) {
         LootTable var5 = this.field_70170_p.func_73046_m().func_200249_aQ().func_186521_a(var4);
         this.field_184659_bA = null;
         LootContext.Builder var6 = (new LootContext.Builder((WorldServer)this.field_70170_p)).func_186472_a(this).func_186473_a(var3).func_204313_a(new BlockPos(this));
         if (var1 && this.field_70717_bb != null) {
            var6 = var6.func_186470_a(this.field_70717_bb).func_186469_a(this.field_70717_bb.func_184817_da());
         }

         List var7 = var5.func_186462_a(this.field_184653_bB == 0L ? this.field_70146_Z : new Random(this.field_184653_bB), var6.func_186471_a());
         Iterator var8 = var7.iterator();

         while(var8.hasNext()) {
            ItemStack var9 = (ItemStack)var8.next();
            this.func_199701_a_(var9);
         }

         this.func_82160_b(var1, var2);
      } else {
         super.func_184610_a(var1, var2, var3);
      }

   }

   public void func_191989_p(float var1) {
      this.field_191988_bg = var1;
   }

   public void func_70657_f(float var1) {
      this.field_70701_bs = var1;
   }

   public void func_184646_p(float var1) {
      this.field_70702_br = var1;
   }

   public void func_70659_e(float var1) {
      super.func_70659_e(var1);
      this.func_191989_p(var1);
   }

   public void func_70636_d() {
      super.func_70636_d();
      this.field_70170_p.field_72984_F.func_76320_a("looting");
      if (!this.field_70170_p.field_72995_K && this.func_98052_bS() && !this.field_70729_aU && this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
         List var1 = this.field_70170_p.func_72872_a(EntityItem.class, this.func_174813_aQ().func_72314_b(1.0D, 0.0D, 1.0D));
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            EntityItem var3 = (EntityItem)var2.next();
            if (!var3.field_70128_L && !var3.func_92059_d().func_190926_b() && !var3.func_174874_s()) {
               this.func_175445_a(var3);
            }
         }
      }

      this.field_70170_p.field_72984_F.func_76319_b();
   }

   protected void func_175445_a(EntityItem var1) {
      ItemStack var2 = var1.func_92059_d();
      EntityEquipmentSlot var3 = func_184640_d(var2);
      ItemStack var4 = this.func_184582_a(var3);
      boolean var5 = this.func_208003_a(var2, var4, var3);
      if (var5 && this.func_175448_a(var2)) {
         double var6 = (double)this.func_205712_c(var3);
         if (!var4.func_190926_b() && (double)(this.field_70146_Z.nextFloat() - 0.1F) < var6) {
            this.func_199701_a_(var4);
         }

         this.func_184201_a(var3, var2);
         switch(var3.func_188453_a()) {
         case HAND:
            this.field_82174_bp[var3.func_188454_b()] = 2.0F;
            break;
         case ARMOR:
            this.field_184655_bs[var3.func_188454_b()] = 2.0F;
         }

         this.field_82179_bU = true;
         this.func_71001_a(var1, var2.func_190916_E());
         var1.func_70106_y();
      }

   }

   protected boolean func_208003_a(ItemStack var1, ItemStack var2, EntityEquipmentSlot var3) {
      boolean var4 = true;
      if (!var2.func_190926_b()) {
         if (var3.func_188453_a() == EntityEquipmentSlot.Type.HAND) {
            if (var1.func_77973_b() instanceof ItemSword && !(var2.func_77973_b() instanceof ItemSword)) {
               var4 = true;
            } else if (var1.func_77973_b() instanceof ItemSword && var2.func_77973_b() instanceof ItemSword) {
               ItemSword var5 = (ItemSword)var1.func_77973_b();
               ItemSword var6 = (ItemSword)var2.func_77973_b();
               if (var5.func_200894_d() == var6.func_200894_d()) {
                  var4 = var1.func_77952_i() < var2.func_77952_i() || var1.func_77942_o() && !var2.func_77942_o();
               } else {
                  var4 = var5.func_200894_d() > var6.func_200894_d();
               }
            } else if (var1.func_77973_b() instanceof ItemBow && var2.func_77973_b() instanceof ItemBow) {
               var4 = var1.func_77942_o() && !var2.func_77942_o();
            } else {
               var4 = false;
            }
         } else if (var1.func_77973_b() instanceof ItemArmor && !(var2.func_77973_b() instanceof ItemArmor)) {
            var4 = true;
         } else if (var1.func_77973_b() instanceof ItemArmor && var2.func_77973_b() instanceof ItemArmor && !EnchantmentHelper.func_190938_b(var2)) {
            ItemArmor var7 = (ItemArmor)var1.func_77973_b();
            ItemArmor var8 = (ItemArmor)var2.func_77973_b();
            if (var7.func_200881_e() == var8.func_200881_e()) {
               var4 = var1.func_77952_i() < var2.func_77952_i() || var1.func_77942_o() && !var2.func_77942_o();
            } else {
               var4 = var7.func_200881_e() > var8.func_200881_e();
            }
         } else {
            var4 = false;
         }
      }

      return var4;
   }

   protected boolean func_175448_a(ItemStack var1) {
      return true;
   }

   public boolean func_70692_ba() {
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
      if (this.func_184218_aH() && this.func_184187_bx() instanceof EntityLiving) {
         EntityLiving var1 = (EntityLiving)this.func_184187_bx();
         var1.func_70661_as().func_75484_a(this.func_70661_as().func_75505_d(), 1.5D);
         var1.func_70605_aq().func_188487_a(this.func_70605_aq());
      }

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

   public int func_184649_cE() {
      return 10;
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
      float var12 = (float)(MathHelper.func_181159_b(var8, var4) * 57.2957763671875D) - 90.0F;
      float var13 = (float)(-(MathHelper.func_181159_b(var6, var14) * 57.2957763671875D));
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

   public boolean func_205020_a(IWorld var1, boolean var2) {
      IBlockState var3 = var1.func_180495_p((new BlockPos(this)).func_177977_b());
      return var3.func_189884_a(this);
   }

   public final boolean func_70058_J() {
      return this.func_205019_a(this.field_70170_p);
   }

   public boolean func_205019_a(IWorldReaderBase var1) {
      return !var1.func_72953_d(this.func_174813_aQ()) && var1.func_195586_b(this, this.func_174813_aQ()) && var1.func_195587_c(this, this.func_174813_aQ());
   }

   public float func_70603_bj() {
      return 1.0F;
   }

   public int func_70641_bl() {
      return 4;
   }

   public boolean func_204209_c(int var1) {
      return false;
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

   public Iterable<ItemStack> func_184214_aD() {
      return this.field_184656_bv;
   }

   public Iterable<ItemStack> func_184193_aE() {
      return this.field_184657_bw;
   }

   public ItemStack func_184582_a(EntityEquipmentSlot var1) {
      switch(var1.func_188453_a()) {
      case HAND:
         return (ItemStack)this.field_184656_bv.get(var1.func_188454_b());
      case ARMOR:
         return (ItemStack)this.field_184657_bw.get(var1.func_188454_b());
      default:
         return ItemStack.field_190927_a;
      }
   }

   public void func_184201_a(EntityEquipmentSlot var1, ItemStack var2) {
      switch(var1.func_188453_a()) {
      case HAND:
         this.field_184656_bv.set(var1.func_188454_b(), var2);
         break;
      case ARMOR:
         this.field_184657_bw.set(var1.func_188454_b(), var2);
      }

   }

   protected void func_82160_b(boolean var1, int var2) {
      EntityEquipmentSlot[] var3 = EntityEquipmentSlot.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EntityEquipmentSlot var6 = var3[var5];
         ItemStack var7 = this.func_184582_a(var6);
         float var8 = this.func_205712_c(var6);
         boolean var9 = var8 > 1.0F;
         if (!var7.func_190926_b() && !EnchantmentHelper.func_190939_c(var7) && (var1 || var9) && this.field_70146_Z.nextFloat() - (float)var2 * 0.01F < var8) {
            if (!var9 && var7.func_77984_f()) {
               var7.func_196085_b(var7.func_77958_k() - this.field_70146_Z.nextInt(1 + this.field_70146_Z.nextInt(Math.max(var7.func_77958_k() - 3, 1))));
            }

            this.func_199701_a_(var7);
         }
      }

   }

   protected float func_205712_c(EntityEquipmentSlot var1) {
      float var2;
      switch(var1.func_188453_a()) {
      case HAND:
         var2 = this.field_82174_bp[var1.func_188454_b()];
         break;
      case ARMOR:
         var2 = this.field_184655_bs[var1.func_188454_b()];
         break;
      default:
         var2 = 0.0F;
      }

      return var2;
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

         boolean var4 = true;
         EntityEquipmentSlot[] var5 = EntityEquipmentSlot.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EntityEquipmentSlot var8 = var5[var7];
            if (var8.func_188453_a() == EntityEquipmentSlot.Type.ARMOR) {
               ItemStack var9 = this.func_184582_a(var8);
               if (!var4 && this.field_70146_Z.nextFloat() < var3) {
                  break;
               }

               var4 = false;
               if (var9.func_190926_b()) {
                  Item var10 = func_184636_a(var8, var2);
                  if (var10 != null) {
                     this.func_184201_a(var8, new ItemStack(var10));
                  }
               }
            }
         }
      }

   }

   public static EntityEquipmentSlot func_184640_d(ItemStack var0) {
      Item var1 = var0.func_77973_b();
      if (var1 != Blocks.field_196625_cS.func_199767_j() && (!(var1 instanceof ItemBlock) || !(((ItemBlock)var1).func_179223_d() instanceof BlockAbstractSkull))) {
         if (var1 instanceof ItemArmor) {
            return ((ItemArmor)var1).func_185083_B_();
         } else if (var1 == Items.field_185160_cR) {
            return EntityEquipmentSlot.CHEST;
         } else {
            return var1 == Items.field_185159_cQ ? EntityEquipmentSlot.OFFHAND : EntityEquipmentSlot.MAINHAND;
         }
      } else {
         return EntityEquipmentSlot.HEAD;
      }
   }

   @Nullable
   public static Item func_184636_a(EntityEquipmentSlot var0, int var1) {
      switch(var0) {
      case HEAD:
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
      case CHEST:
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
      case LEGS:
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
      case FEET:
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
      if (!this.func_184614_ca().func_190926_b() && this.field_70146_Z.nextFloat() < 0.25F * var2) {
         this.func_184201_a(EntityEquipmentSlot.MAINHAND, EnchantmentHelper.func_77504_a(this.field_70146_Z, this.func_184614_ca(), (int)(5.0F + var2 * (float)this.field_70146_Z.nextInt(18)), false));
      }

      EntityEquipmentSlot[] var3 = EntityEquipmentSlot.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EntityEquipmentSlot var6 = var3[var5];
         if (var6.func_188453_a() == EntityEquipmentSlot.Type.ARMOR) {
            ItemStack var7 = this.func_184582_a(var6);
            if (!var7.func_190926_b() && this.field_70146_Z.nextFloat() < 0.5F * var2) {
               this.func_184201_a(var6, EnchantmentHelper.func_77504_a(this.field_70146_Z, var7, (int)(5.0F + var2 * (float)this.field_70146_Z.nextInt(18)), false));
            }
         }
      }

   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111121_a(new AttributeModifier("Random spawn bonus", this.field_70146_Z.nextGaussian() * 0.05D, 1));
      if (this.field_70146_Z.nextFloat() < 0.05F) {
         this.func_184641_n(true);
      } else {
         this.func_184641_n(false);
      }

      return var2;
   }

   public boolean func_82171_bF() {
      return false;
   }

   public void func_110163_bv() {
      this.field_82179_bU = true;
   }

   public void func_184642_a(EntityEquipmentSlot var1, float var2) {
      switch(var1.func_188453_a()) {
      case HAND:
         this.field_82174_bp[var1.func_188454_b()] = var2;
         break;
      case ARMOR:
         this.field_184655_bs[var1.func_188454_b()] = var2;
      }

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

   public final boolean func_184230_a(EntityPlayer var1, EnumHand var2) {
      if (this.func_110167_bD() && this.func_110166_bE() == var1) {
         this.func_110160_i(true, !var1.field_71075_bZ.field_75098_d);
         return true;
      } else {
         ItemStack var3 = var1.func_184586_b(var2);
         if (var3.func_77973_b() == Items.field_151058_ca && this.func_184652_a(var1)) {
            this.func_110162_b(var1, true);
            var3.func_190918_g(1);
            return true;
         } else {
            return this.func_184645_a(var1, var2) ? true : super.func_184230_a(var1, var2);
         }
      }
   }

   protected boolean func_184645_a(EntityPlayer var1, EnumHand var2) {
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
            this.func_199703_a(Items.field_151058_ca);
         }

         if (!this.field_70170_p.field_72995_K && var1 && this.field_70170_p instanceof WorldServer) {
            ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new SPacketEntityAttach(this, (Entity)null));
         }
      }

   }

   public boolean func_184652_a(EntityPlayer var1) {
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
         ((WorldServer)this.field_70170_p).func_73039_n().func_151247_a(this, new SPacketEntityAttach(this, this.field_110168_bw));
      }

      if (this.func_184218_aH()) {
         this.func_184210_p();
      }

   }

   public boolean func_184205_a(Entity var1, boolean var2) {
      boolean var3 = super.func_184205_a(var1, var2);
      if (var3 && this.func_110167_bD()) {
         this.func_110160_i(true, true);
      }

      return var3;
   }

   private void func_110165_bF() {
      if (this.field_110169_bv && this.field_110170_bx != null) {
         if (this.field_110170_bx.func_186855_b("UUID")) {
            UUID var1 = this.field_110170_bx.func_186857_a("UUID");
            List var2 = this.field_70170_p.func_72872_a(EntityLivingBase.class, this.func_174813_aQ().func_186662_g(10.0D));
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               EntityLivingBase var4 = (EntityLivingBase)var3.next();
               if (var4.func_110124_au().equals(var1)) {
                  this.func_110162_b(var4, true);
                  break;
               }
            }
         } else if (this.field_110170_bx.func_150297_b("X", 99) && this.field_110170_bx.func_150297_b("Y", 99) && this.field_110170_bx.func_150297_b("Z", 99)) {
            BlockPos var5 = new BlockPos(this.field_110170_bx.func_74762_e("X"), this.field_110170_bx.func_74762_e("Y"), this.field_110170_bx.func_74762_e("Z"));
            EntityLeashKnot var6 = EntityLeashKnot.func_174863_b(this.field_70170_p, var5);
            if (var6 == null) {
               var6 = EntityLeashKnot.func_174862_a(this.field_70170_p, var5);
            }

            this.func_110162_b(var6, true);
         } else {
            this.func_110160_i(false, true);
         }
      }

      this.field_110170_bx = null;
   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      EntityEquipmentSlot var3;
      if (var1 == 98) {
         var3 = EntityEquipmentSlot.MAINHAND;
      } else if (var1 == 99) {
         var3 = EntityEquipmentSlot.OFFHAND;
      } else if (var1 == 100 + EntityEquipmentSlot.HEAD.func_188454_b()) {
         var3 = EntityEquipmentSlot.HEAD;
      } else if (var1 == 100 + EntityEquipmentSlot.CHEST.func_188454_b()) {
         var3 = EntityEquipmentSlot.CHEST;
      } else if (var1 == 100 + EntityEquipmentSlot.LEGS.func_188454_b()) {
         var3 = EntityEquipmentSlot.LEGS;
      } else {
         if (var1 != 100 + EntityEquipmentSlot.FEET.func_188454_b()) {
            return false;
         }

         var3 = EntityEquipmentSlot.FEET;
      }

      if (!var2.func_190926_b() && !func_184648_b(var3, var2) && var3 != EntityEquipmentSlot.HEAD) {
         return false;
      } else {
         this.func_184201_a(var3, var2);
         return true;
      }
   }

   public boolean func_184186_bw() {
      return this.func_82171_bF() && super.func_184186_bw();
   }

   public static boolean func_184648_b(EntityEquipmentSlot var0, ItemStack var1) {
      EntityEquipmentSlot var2 = func_184640_d(var1);
      return var2 == var0 || var2 == EntityEquipmentSlot.MAINHAND && var0 == EntityEquipmentSlot.OFFHAND || var2 == EntityEquipmentSlot.OFFHAND && var0 == EntityEquipmentSlot.MAINHAND;
   }

   public boolean func_70613_aW() {
      return super.func_70613_aW() && !this.func_175446_cd();
   }

   public void func_94061_f(boolean var1) {
      byte var2 = (Byte)this.field_70180_af.func_187225_a(field_184654_a);
      this.field_70180_af.func_187227_b(field_184654_a, var1 ? (byte)(var2 | 1) : (byte)(var2 & -2));
   }

   public void func_184641_n(boolean var1) {
      byte var2 = (Byte)this.field_70180_af.func_187225_a(field_184654_a);
      this.field_70180_af.func_187227_b(field_184654_a, var1 ? (byte)(var2 | 2) : (byte)(var2 & -3));
   }

   public boolean func_175446_cd() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184654_a) & 1) != 0;
   }

   public boolean func_184638_cS() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184654_a) & 2) != 0;
   }

   public EnumHandSide func_184591_cq() {
      return this.func_184638_cS() ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
   }

   public boolean func_70652_k(Entity var1) {
      float var2 = (float)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
      int var3 = 0;
      if (var1 instanceof EntityLivingBase) {
         var2 += EnchantmentHelper.func_152377_a(this.func_184614_ca(), ((EntityLivingBase)var1).func_70668_bt());
         var3 += EnchantmentHelper.func_77501_a(this);
      }

      boolean var4 = var1.func_70097_a(DamageSource.func_76358_a(this), var2);
      if (var4) {
         if (var3 > 0 && var1 instanceof EntityLivingBase) {
            ((EntityLivingBase)var1).func_70653_a(this, (float)var3 * 0.5F, (double)MathHelper.func_76126_a(this.field_70177_z * 0.017453292F), (double)(-MathHelper.func_76134_b(this.field_70177_z * 0.017453292F)));
            this.field_70159_w *= 0.6D;
            this.field_70179_y *= 0.6D;
         }

         int var5 = EnchantmentHelper.func_90036_a(this);
         if (var5 > 0) {
            var1.func_70015_d(var5 * 4);
         }

         if (var1 instanceof EntityPlayer) {
            EntityPlayer var6 = (EntityPlayer)var1;
            ItemStack var7 = this.func_184614_ca();
            ItemStack var8 = var6.func_184587_cr() ? var6.func_184607_cu() : ItemStack.field_190927_a;
            if (!var7.func_190926_b() && !var8.func_190926_b() && var7.func_77973_b() instanceof ItemAxe && var8.func_77973_b() == Items.field_185159_cQ) {
               float var9 = 0.25F + (float)EnchantmentHelper.func_185293_e(this) * 0.05F;
               if (this.field_70146_Z.nextFloat() < var9) {
                  var6.func_184811_cZ().func_185145_a(Items.field_185159_cQ, 100);
                  this.field_70170_p.func_72960_a(var6, (byte)30);
               }
            }
         }

         this.func_174815_a(this, var1);
      }

      return var4;
   }

   protected boolean func_204609_dp() {
      if (this.field_70170_p.func_72935_r() && !this.field_70170_p.field_72995_K) {
         float var1 = this.func_70013_c();
         BlockPos var2 = this.func_184187_bx() instanceof EntityBoat ? (new BlockPos(this.field_70165_t, (double)Math.round(this.field_70163_u), this.field_70161_v)).func_177984_a() : new BlockPos(this.field_70165_t, (double)Math.round(this.field_70163_u), this.field_70161_v);
         if (var1 > 0.5F && this.field_70146_Z.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F && this.field_70170_p.func_175678_i(var2)) {
            return true;
         }
      }

      return false;
   }

   protected void func_180466_bG(Tag<Fluid> var1) {
      if (this.func_70661_as().func_212238_t()) {
         super.func_180466_bG(var1);
      } else {
         this.field_70181_x += 0.30000001192092896D;
      }

   }

   static {
      field_184654_a = EntityDataManager.func_187226_a(EntityLiving.class, DataSerializers.field_187191_a);
   }
}
