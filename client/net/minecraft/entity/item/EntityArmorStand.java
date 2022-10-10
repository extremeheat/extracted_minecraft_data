package net.minecraft.entity.item;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityArmorStand extends EntityLivingBase {
   private static final Rotations field_175435_a = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations field_175433_b = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations field_175434_c = new Rotations(-10.0F, 0.0F, -10.0F);
   private static final Rotations field_175431_d = new Rotations(-15.0F, 0.0F, 10.0F);
   private static final Rotations field_175432_e = new Rotations(-1.0F, 0.0F, -1.0F);
   private static final Rotations field_175429_f = new Rotations(1.0F, 0.0F, 1.0F);
   public static final DataParameter<Byte> field_184801_a;
   public static final DataParameter<Rotations> field_184802_b;
   public static final DataParameter<Rotations> field_184803_c;
   public static final DataParameter<Rotations> field_184804_d;
   public static final DataParameter<Rotations> field_184805_e;
   public static final DataParameter<Rotations> field_184806_f;
   public static final DataParameter<Rotations> field_184807_g;
   private static final Predicate<Entity> field_184798_bv;
   private final NonNullList<ItemStack> field_184799_bw;
   private final NonNullList<ItemStack> field_184800_bx;
   private boolean field_175436_h;
   public long field_175437_i;
   private int field_175442_bg;
   private boolean field_181028_bj;
   private Rotations field_175443_bh;
   private Rotations field_175444_bi;
   private Rotations field_175438_bj;
   private Rotations field_175439_bk;
   private Rotations field_175440_bl;
   private Rotations field_175441_bm;

   public EntityArmorStand(World var1) {
      super(EntityType.field_200789_c, var1);
      this.field_184799_bw = NonNullList.func_191197_a(2, ItemStack.field_190927_a);
      this.field_184800_bx = NonNullList.func_191197_a(4, ItemStack.field_190927_a);
      this.field_175443_bh = field_175435_a;
      this.field_175444_bi = field_175433_b;
      this.field_175438_bj = field_175434_c;
      this.field_175439_bk = field_175431_d;
      this.field_175440_bl = field_175432_e;
      this.field_175441_bm = field_175429_f;
      this.field_70145_X = this.func_189652_ae();
      this.func_70105_a(0.5F, 1.975F);
      this.field_70138_W = 0.0F;
   }

   public EntityArmorStand(World var1, double var2, double var4, double var6) {
      this(var1);
      this.func_70107_b(var2, var4, var6);
   }

   protected final void func_70105_a(float var1, float var2) {
      double var3 = this.field_70165_t;
      double var5 = this.field_70163_u;
      double var7 = this.field_70161_v;
      float var9 = this.func_181026_s() ? 0.0F : (this.func_70631_g_() ? 0.5F : 1.0F);
      super.func_70105_a(var1 * var9, var2 * var9);
      this.func_70107_b(var3, var5, var7);
   }

   public boolean func_70613_aW() {
      return super.func_70613_aW() && !this.func_189652_ae();
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184801_a, (byte)0);
      this.field_70180_af.func_187214_a(field_184802_b, field_175435_a);
      this.field_70180_af.func_187214_a(field_184803_c, field_175433_b);
      this.field_70180_af.func_187214_a(field_184804_d, field_175434_c);
      this.field_70180_af.func_187214_a(field_184805_e, field_175431_d);
      this.field_70180_af.func_187214_a(field_184806_f, field_175432_e);
      this.field_70180_af.func_187214_a(field_184807_g, field_175429_f);
   }

   public Iterable<ItemStack> func_184214_aD() {
      return this.field_184799_bw;
   }

   public Iterable<ItemStack> func_184193_aE() {
      return this.field_184800_bx;
   }

   public ItemStack func_184582_a(EntityEquipmentSlot var1) {
      switch(var1.func_188453_a()) {
      case HAND:
         return (ItemStack)this.field_184799_bw.get(var1.func_188454_b());
      case ARMOR:
         return (ItemStack)this.field_184800_bx.get(var1.func_188454_b());
      default:
         return ItemStack.field_190927_a;
      }
   }

   public void func_184201_a(EntityEquipmentSlot var1, ItemStack var2) {
      switch(var1.func_188453_a()) {
      case HAND:
         this.func_184606_a_(var2);
         this.field_184799_bw.set(var1.func_188454_b(), var2);
         break;
      case ARMOR:
         this.func_184606_a_(var2);
         this.field_184800_bx.set(var1.func_188454_b(), var2);
      }

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

      if (!var2.func_190926_b() && !EntityLiving.func_184648_b(var3, var2) && var3 != EntityEquipmentSlot.HEAD) {
         return false;
      } else {
         this.func_184201_a(var3, var2);
         return true;
      }
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      NBTTagList var2 = new NBTTagList();

      NBTTagCompound var5;
      for(Iterator var3 = this.field_184800_bx.iterator(); var3.hasNext(); var2.add((INBTBase)var5)) {
         ItemStack var4 = (ItemStack)var3.next();
         var5 = new NBTTagCompound();
         if (!var4.func_190926_b()) {
            var4.func_77955_b(var5);
         }
      }

      var1.func_74782_a("ArmorItems", var2);
      NBTTagList var7 = new NBTTagList();

      NBTTagCompound var6;
      for(Iterator var8 = this.field_184799_bw.iterator(); var8.hasNext(); var7.add((INBTBase)var6)) {
         ItemStack var9 = (ItemStack)var8.next();
         var6 = new NBTTagCompound();
         if (!var9.func_190926_b()) {
            var9.func_77955_b(var6);
         }
      }

      var1.func_74782_a("HandItems", var7);
      var1.func_74757_a("Invisible", this.func_82150_aj());
      var1.func_74757_a("Small", this.func_175410_n());
      var1.func_74757_a("ShowArms", this.func_175402_q());
      var1.func_74768_a("DisabledSlots", this.field_175442_bg);
      var1.func_74757_a("NoBasePlate", this.func_175414_r());
      if (this.func_181026_s()) {
         var1.func_74757_a("Marker", this.func_181026_s());
      }

      var1.func_74782_a("Pose", this.func_175419_y());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      NBTTagList var2;
      int var3;
      if (var1.func_150297_b("ArmorItems", 9)) {
         var2 = var1.func_150295_c("ArmorItems", 10);

         for(var3 = 0; var3 < this.field_184800_bx.size(); ++var3) {
            this.field_184800_bx.set(var3, ItemStack.func_199557_a(var2.func_150305_b(var3)));
         }
      }

      if (var1.func_150297_b("HandItems", 9)) {
         var2 = var1.func_150295_c("HandItems", 10);

         for(var3 = 0; var3 < this.field_184799_bw.size(); ++var3) {
            this.field_184799_bw.set(var3, ItemStack.func_199557_a(var2.func_150305_b(var3)));
         }
      }

      this.func_82142_c(var1.func_74767_n("Invisible"));
      this.func_175420_a(var1.func_74767_n("Small"));
      this.func_175413_k(var1.func_74767_n("ShowArms"));
      this.field_175442_bg = var1.func_74762_e("DisabledSlots");
      this.func_175426_l(var1.func_74767_n("NoBasePlate"));
      this.func_181027_m(var1.func_74767_n("Marker"));
      this.field_181028_bj = !this.func_181026_s();
      this.field_70145_X = this.func_189652_ae();
      NBTTagCompound var4 = var1.func_74775_l("Pose");
      this.func_175416_h(var4);
   }

   private void func_175416_h(NBTTagCompound var1) {
      NBTTagList var2 = var1.func_150295_c("Head", 5);
      this.func_175415_a(var2.isEmpty() ? field_175435_a : new Rotations(var2));
      NBTTagList var3 = var1.func_150295_c("Body", 5);
      this.func_175424_b(var3.isEmpty() ? field_175433_b : new Rotations(var3));
      NBTTagList var4 = var1.func_150295_c("LeftArm", 5);
      this.func_175405_c(var4.isEmpty() ? field_175434_c : new Rotations(var4));
      NBTTagList var5 = var1.func_150295_c("RightArm", 5);
      this.func_175428_d(var5.isEmpty() ? field_175431_d : new Rotations(var5));
      NBTTagList var6 = var1.func_150295_c("LeftLeg", 5);
      this.func_175417_e(var6.isEmpty() ? field_175432_e : new Rotations(var6));
      NBTTagList var7 = var1.func_150295_c("RightLeg", 5);
      this.func_175427_f(var7.isEmpty() ? field_175429_f : new Rotations(var7));
   }

   private NBTTagCompound func_175419_y() {
      NBTTagCompound var1 = new NBTTagCompound();
      if (!field_175435_a.equals(this.field_175443_bh)) {
         var1.func_74782_a("Head", this.field_175443_bh.func_179414_a());
      }

      if (!field_175433_b.equals(this.field_175444_bi)) {
         var1.func_74782_a("Body", this.field_175444_bi.func_179414_a());
      }

      if (!field_175434_c.equals(this.field_175438_bj)) {
         var1.func_74782_a("LeftArm", this.field_175438_bj.func_179414_a());
      }

      if (!field_175431_d.equals(this.field_175439_bk)) {
         var1.func_74782_a("RightArm", this.field_175439_bk.func_179414_a());
      }

      if (!field_175432_e.equals(this.field_175440_bl)) {
         var1.func_74782_a("LeftLeg", this.field_175440_bl.func_179414_a());
      }

      if (!field_175429_f.equals(this.field_175441_bm)) {
         var1.func_74782_a("RightLeg", this.field_175441_bm.func_179414_a());
      }

      return var1;
   }

   public boolean func_70104_M() {
      return false;
   }

   protected void func_82167_n(Entity var1) {
   }

   protected void func_85033_bc() {
      List var1 = this.field_70170_p.func_175674_a(this, this.func_174813_aQ(), field_184798_bv);

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         Entity var3 = (Entity)var1.get(var2);
         if (this.func_70068_e(var3) <= 0.2D) {
            var3.func_70108_f(this);
         }
      }

   }

   public EnumActionResult func_184199_a(EntityPlayer var1, Vec3d var2, EnumHand var3) {
      ItemStack var4 = var1.func_184586_b(var3);
      if (!this.func_181026_s() && var4.func_77973_b() != Items.field_151057_cb) {
         if (!this.field_70170_p.field_72995_K && !var1.func_175149_v()) {
            EntityEquipmentSlot var5 = EntityLiving.func_184640_d(var4);
            if (var4.func_190926_b()) {
               EntityEquipmentSlot var6 = this.func_190772_a(var2);
               EntityEquipmentSlot var7 = this.func_184796_b(var6) ? var5 : var6;
               if (this.func_190630_a(var7)) {
                  this.func_184795_a(var1, var7, var4, var3);
               }
            } else {
               if (this.func_184796_b(var5)) {
                  return EnumActionResult.FAIL;
               }

               if (var5.func_188453_a() == EntityEquipmentSlot.Type.HAND && !this.func_175402_q()) {
                  return EnumActionResult.FAIL;
               }

               this.func_184795_a(var1, var5, var4, var3);
            }

            return EnumActionResult.SUCCESS;
         } else {
            return EnumActionResult.SUCCESS;
         }
      } else {
         return EnumActionResult.PASS;
      }
   }

   protected EntityEquipmentSlot func_190772_a(Vec3d var1) {
      EntityEquipmentSlot var2 = EntityEquipmentSlot.MAINHAND;
      boolean var3 = this.func_175410_n();
      double var4 = var3 ? var1.field_72448_b * 2.0D : var1.field_72448_b;
      EntityEquipmentSlot var6 = EntityEquipmentSlot.FEET;
      if (var4 >= 0.1D && var4 < 0.1D + (var3 ? 0.8D : 0.45D) && this.func_190630_a(var6)) {
         var2 = EntityEquipmentSlot.FEET;
      } else if (var4 >= 0.9D + (var3 ? 0.3D : 0.0D) && var4 < 0.9D + (var3 ? 1.0D : 0.7D) && this.func_190630_a(EntityEquipmentSlot.CHEST)) {
         var2 = EntityEquipmentSlot.CHEST;
      } else if (var4 >= 0.4D && var4 < 0.4D + (var3 ? 1.0D : 0.8D) && this.func_190630_a(EntityEquipmentSlot.LEGS)) {
         var2 = EntityEquipmentSlot.LEGS;
      } else if (var4 >= 1.6D && this.func_190630_a(EntityEquipmentSlot.HEAD)) {
         var2 = EntityEquipmentSlot.HEAD;
      } else if (!this.func_190630_a(EntityEquipmentSlot.MAINHAND) && this.func_190630_a(EntityEquipmentSlot.OFFHAND)) {
         var2 = EntityEquipmentSlot.OFFHAND;
      }

      return var2;
   }

   public boolean func_184796_b(EntityEquipmentSlot var1) {
      return (this.field_175442_bg & 1 << var1.func_188452_c()) != 0 || var1.func_188453_a() == EntityEquipmentSlot.Type.HAND && !this.func_175402_q();
   }

   private void func_184795_a(EntityPlayer var1, EntityEquipmentSlot var2, ItemStack var3, EnumHand var4) {
      ItemStack var5 = this.func_184582_a(var2);
      if (var5.func_190926_b() || (this.field_175442_bg & 1 << var2.func_188452_c() + 8) == 0) {
         if (!var5.func_190926_b() || (this.field_175442_bg & 1 << var2.func_188452_c() + 16) == 0) {
            ItemStack var6;
            if (var1.field_71075_bZ.field_75098_d && var5.func_190926_b() && !var3.func_190926_b()) {
               var6 = var3.func_77946_l();
               var6.func_190920_e(1);
               this.func_184201_a(var2, var6);
            } else if (!var3.func_190926_b() && var3.func_190916_E() > 1) {
               if (var5.func_190926_b()) {
                  var6 = var3.func_77946_l();
                  var6.func_190920_e(1);
                  this.func_184201_a(var2, var6);
                  var3.func_190918_g(1);
               }
            } else {
               this.func_184201_a(var2, var3);
               var1.func_184611_a(var4, var5);
            }
         }
      }
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
         if (DamageSource.field_76380_i.equals(var1)) {
            this.func_70106_y();
            return false;
         } else if (!this.func_180431_b(var1) && !this.field_175436_h && !this.func_181026_s()) {
            if (var1.func_94541_c()) {
               this.func_175409_C();
               this.func_70106_y();
               return false;
            } else if (DamageSource.field_76372_a.equals(var1)) {
               if (this.func_70027_ad()) {
                  this.func_175406_a(0.15F);
               } else {
                  this.func_70015_d(5);
               }

               return false;
            } else if (DamageSource.field_76370_b.equals(var1) && this.func_110143_aJ() > 0.5F) {
               this.func_175406_a(4.0F);
               return false;
            } else {
               boolean var3 = var1.func_76364_f() instanceof EntityArrow;
               boolean var4 = "player".equals(var1.func_76355_l());
               if (!var4 && !var3) {
                  return false;
               } else if (var1.func_76346_g() instanceof EntityPlayer && !((EntityPlayer)var1.func_76346_g()).field_71075_bZ.field_75099_e) {
                  return false;
               } else if (var1.func_180136_u()) {
                  this.func_190773_I();
                  this.func_175412_z();
                  this.func_70106_y();
                  return false;
               } else {
                  long var5 = this.field_70170_p.func_82737_E();
                  if (var5 - this.field_175437_i > 5L && !var3) {
                     this.field_70170_p.func_72960_a(this, (byte)32);
                     this.field_175437_i = var5;
                  } else {
                     this.func_175421_A();
                     this.func_175412_z();
                     this.func_70106_y();
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void func_70103_a(byte var1) {
      if (var1 == 32) {
         if (this.field_70170_p.field_72995_K) {
            this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187707_l, this.func_184176_by(), 0.3F, 1.0F, false);
            this.field_175437_i = this.field_70170_p.func_82737_E();
         }
      } else {
         super.func_70103_a(var1);
      }

   }

   public boolean func_70112_a(double var1) {
      double var3 = this.func_174813_aQ().func_72320_b() * 4.0D;
      if (Double.isNaN(var3) || var3 == 0.0D) {
         var3 = 4.0D;
      }

      var3 *= 64.0D;
      return var1 < var3 * var3;
   }

   private void func_175412_z() {
      if (this.field_70170_p instanceof WorldServer) {
         ((WorldServer)this.field_70170_p).func_195598_a(new BlockParticleData(Particles.field_197611_d, Blocks.field_196662_n.func_176223_P()), this.field_70165_t, this.field_70163_u + (double)this.field_70131_O / 1.5D, this.field_70161_v, 10, (double)(this.field_70130_N / 4.0F), (double)(this.field_70131_O / 4.0F), (double)(this.field_70130_N / 4.0F), 0.05D);
      }

   }

   private void func_175406_a(float var1) {
      float var2 = this.func_110143_aJ();
      var2 -= var1;
      if (var2 <= 0.5F) {
         this.func_175409_C();
         this.func_70106_y();
      } else {
         this.func_70606_j(var2);
      }

   }

   private void func_175421_A() {
      Block.func_180635_a(this.field_70170_p, new BlockPos(this), new ItemStack(Items.field_179565_cj));
      this.func_175409_C();
   }

   private void func_175409_C() {
      this.func_190773_I();

      int var1;
      ItemStack var2;
      for(var1 = 0; var1 < this.field_184799_bw.size(); ++var1) {
         var2 = (ItemStack)this.field_184799_bw.get(var1);
         if (!var2.func_190926_b()) {
            Block.func_180635_a(this.field_70170_p, (new BlockPos(this)).func_177984_a(), var2);
            this.field_184799_bw.set(var1, ItemStack.field_190927_a);
         }
      }

      for(var1 = 0; var1 < this.field_184800_bx.size(); ++var1) {
         var2 = (ItemStack)this.field_184800_bx.get(var1);
         if (!var2.func_190926_b()) {
            Block.func_180635_a(this.field_70170_p, (new BlockPos(this)).func_177984_a(), var2);
            this.field_184800_bx.set(var1, ItemStack.field_190927_a);
         }
      }

   }

   private void func_190773_I() {
      this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187701_j, this.func_184176_by(), 1.0F, 1.0F);
   }

   protected float func_110146_f(float var1, float var2) {
      this.field_70760_ar = this.field_70126_B;
      this.field_70761_aq = this.field_70177_z;
      return 0.0F;
   }

   public float func_70047_e() {
      return this.func_70631_g_() ? this.field_70131_O * 0.5F : this.field_70131_O * 0.9F;
   }

   public double func_70033_W() {
      return this.func_181026_s() ? 0.0D : 0.10000000149011612D;
   }

   public void func_191986_a(float var1, float var2, float var3) {
      if (!this.func_189652_ae()) {
         super.func_191986_a(var1, var2, var3);
      }
   }

   public void func_181013_g(float var1) {
      this.field_70760_ar = this.field_70126_B = var1;
      this.field_70758_at = this.field_70759_as = var1;
   }

   public void func_70034_d(float var1) {
      this.field_70760_ar = this.field_70126_B = var1;
      this.field_70758_at = this.field_70759_as = var1;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      Rotations var1 = (Rotations)this.field_70180_af.func_187225_a(field_184802_b);
      if (!this.field_175443_bh.equals(var1)) {
         this.func_175415_a(var1);
      }

      Rotations var2 = (Rotations)this.field_70180_af.func_187225_a(field_184803_c);
      if (!this.field_175444_bi.equals(var2)) {
         this.func_175424_b(var2);
      }

      Rotations var3 = (Rotations)this.field_70180_af.func_187225_a(field_184804_d);
      if (!this.field_175438_bj.equals(var3)) {
         this.func_175405_c(var3);
      }

      Rotations var4 = (Rotations)this.field_70180_af.func_187225_a(field_184805_e);
      if (!this.field_175439_bk.equals(var4)) {
         this.func_175428_d(var4);
      }

      Rotations var5 = (Rotations)this.field_70180_af.func_187225_a(field_184806_f);
      if (!this.field_175440_bl.equals(var5)) {
         this.func_175417_e(var5);
      }

      Rotations var6 = (Rotations)this.field_70180_af.func_187225_a(field_184807_g);
      if (!this.field_175441_bm.equals(var6)) {
         this.func_175427_f(var6);
      }

      boolean var7 = this.func_181026_s();
      if (this.field_181028_bj != var7) {
         this.func_181550_a(var7);
         this.field_70156_m = !var7;
         this.field_181028_bj = var7;
      }

   }

   private void func_181550_a(boolean var1) {
      if (var1) {
         this.func_70105_a(0.0F, 0.0F);
      } else {
         this.func_70105_a(0.5F, 1.975F);
      }

   }

   protected void func_175135_B() {
      this.func_82142_c(this.field_175436_h);
   }

   public void func_82142_c(boolean var1) {
      this.field_175436_h = var1;
      super.func_82142_c(var1);
   }

   public boolean func_70631_g_() {
      return this.func_175410_n();
   }

   public void func_174812_G() {
      this.func_70106_y();
   }

   public boolean func_180427_aV() {
      return this.func_82150_aj();
   }

   public EnumPushReaction func_184192_z() {
      return this.func_181026_s() ? EnumPushReaction.IGNORE : super.func_184192_z();
   }

   private void func_175420_a(boolean var1) {
      this.field_70180_af.func_187227_b(field_184801_a, this.func_184797_a((Byte)this.field_70180_af.func_187225_a(field_184801_a), 1, var1));
      this.func_70105_a(0.5F, 1.975F);
   }

   public boolean func_175410_n() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184801_a) & 1) != 0;
   }

   private void func_175413_k(boolean var1) {
      this.field_70180_af.func_187227_b(field_184801_a, this.func_184797_a((Byte)this.field_70180_af.func_187225_a(field_184801_a), 4, var1));
   }

   public boolean func_175402_q() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184801_a) & 4) != 0;
   }

   private void func_175426_l(boolean var1) {
      this.field_70180_af.func_187227_b(field_184801_a, this.func_184797_a((Byte)this.field_70180_af.func_187225_a(field_184801_a), 8, var1));
   }

   public boolean func_175414_r() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184801_a) & 8) != 0;
   }

   private void func_181027_m(boolean var1) {
      this.field_70180_af.func_187227_b(field_184801_a, this.func_184797_a((Byte)this.field_70180_af.func_187225_a(field_184801_a), 16, var1));
      this.func_70105_a(0.5F, 1.975F);
   }

   public boolean func_181026_s() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184801_a) & 16) != 0;
   }

   private byte func_184797_a(byte var1, int var2, boolean var3) {
      if (var3) {
         var1 = (byte)(var1 | var2);
      } else {
         var1 = (byte)(var1 & ~var2);
      }

      return var1;
   }

   public void func_175415_a(Rotations var1) {
      this.field_175443_bh = var1;
      this.field_70180_af.func_187227_b(field_184802_b, var1);
   }

   public void func_175424_b(Rotations var1) {
      this.field_175444_bi = var1;
      this.field_70180_af.func_187227_b(field_184803_c, var1);
   }

   public void func_175405_c(Rotations var1) {
      this.field_175438_bj = var1;
      this.field_70180_af.func_187227_b(field_184804_d, var1);
   }

   public void func_175428_d(Rotations var1) {
      this.field_175439_bk = var1;
      this.field_70180_af.func_187227_b(field_184805_e, var1);
   }

   public void func_175417_e(Rotations var1) {
      this.field_175440_bl = var1;
      this.field_70180_af.func_187227_b(field_184806_f, var1);
   }

   public void func_175427_f(Rotations var1) {
      this.field_175441_bm = var1;
      this.field_70180_af.func_187227_b(field_184807_g, var1);
   }

   public Rotations func_175418_s() {
      return this.field_175443_bh;
   }

   public Rotations func_175408_t() {
      return this.field_175444_bi;
   }

   public Rotations func_175404_u() {
      return this.field_175438_bj;
   }

   public Rotations func_175411_v() {
      return this.field_175439_bk;
   }

   public Rotations func_175403_w() {
      return this.field_175440_bl;
   }

   public Rotations func_175407_x() {
      return this.field_175441_bm;
   }

   public boolean func_70067_L() {
      return super.func_70067_L() && !this.func_181026_s();
   }

   public EnumHandSide func_184591_cq() {
      return EnumHandSide.RIGHT;
   }

   protected SoundEvent func_184588_d(int var1) {
      return SoundEvents.field_187704_k;
   }

   @Nullable
   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187707_l;
   }

   @Nullable
   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187701_j;
   }

   public void func_70077_a(EntityLightningBolt var1) {
   }

   public boolean func_184603_cC() {
      return false;
   }

   public void func_184206_a(DataParameter<?> var1) {
      if (field_184801_a.equals(var1)) {
         this.func_70105_a(0.5F, 1.975F);
      }

      super.func_184206_a(var1);
   }

   public boolean func_190631_cK() {
      return false;
   }

   static {
      field_184801_a = EntityDataManager.func_187226_a(EntityArmorStand.class, DataSerializers.field_187191_a);
      field_184802_b = EntityDataManager.func_187226_a(EntityArmorStand.class, DataSerializers.field_187199_i);
      field_184803_c = EntityDataManager.func_187226_a(EntityArmorStand.class, DataSerializers.field_187199_i);
      field_184804_d = EntityDataManager.func_187226_a(EntityArmorStand.class, DataSerializers.field_187199_i);
      field_184805_e = EntityDataManager.func_187226_a(EntityArmorStand.class, DataSerializers.field_187199_i);
      field_184806_f = EntityDataManager.func_187226_a(EntityArmorStand.class, DataSerializers.field_187199_i);
      field_184807_g = EntityDataManager.func_187226_a(EntityArmorStand.class, DataSerializers.field_187199_i);
      field_184798_bv = (var0) -> {
         return var0 instanceof EntityMinecart && ((EntityMinecart)var0).func_184264_v() == EntityMinecart.Type.RIDEABLE;
      };
   }
}
