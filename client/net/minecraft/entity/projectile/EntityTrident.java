package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityTrident extends EntityArrow {
   private static final DataParameter<Byte> field_203053_g;
   private ItemStack field_203054_h;
   private boolean field_203051_au;
   public int field_203052_f;

   public EntityTrident(World var1) {
      super(EntityType.field_203098_aL, var1);
      this.field_203054_h = new ItemStack(Items.field_203184_eO);
   }

   public EntityTrident(World var1, EntityLivingBase var2, ItemStack var3) {
      super(EntityType.field_203098_aL, var2, var1);
      this.field_203054_h = new ItemStack(Items.field_203184_eO);
      this.field_203054_h = var3.func_77946_l();
      this.field_70180_af.func_187227_b(field_203053_g, (byte)EnchantmentHelper.func_203191_f(var3));
   }

   public EntityTrident(World var1, double var2, double var4, double var6) {
      super(EntityType.field_203098_aL, var2, var4, var6, var1);
      this.field_203054_h = new ItemStack(Items.field_203184_eO);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_203053_g, (byte)0);
   }

   public void func_70071_h_() {
      if (this.field_184552_b > 4) {
         this.field_203051_au = true;
      }

      Entity var1 = this.func_212360_k();
      if ((this.field_203051_au || this.func_203047_q()) && var1 != null) {
         byte var2 = (Byte)this.field_70180_af.func_187225_a(field_203053_g);
         if (var2 > 0 && !this.func_207403_q()) {
            if (!this.field_70170_p.field_72995_K && this.field_70251_a == EntityArrow.PickupStatus.ALLOWED) {
               this.func_70099_a(this.func_184550_j(), 0.1F);
            }

            this.func_70106_y();
         } else if (var2 > 0) {
            this.func_203045_n(true);
            Vec3d var3 = new Vec3d(var1.field_70165_t - this.field_70165_t, var1.field_70163_u + (double)var1.func_70047_e() - this.field_70163_u, var1.field_70161_v - this.field_70161_v);
            this.field_70163_u += var3.field_72448_b * 0.015D * (double)var2;
            if (this.field_70170_p.field_72995_K) {
               this.field_70137_T = this.field_70163_u;
            }

            var3 = var3.func_72432_b();
            double var4 = 0.05D * (double)var2;
            this.field_70159_w += var3.field_72450_a * var4 - this.field_70159_w * 0.05D;
            this.field_70181_x += var3.field_72448_b * var4 - this.field_70181_x * 0.05D;
            this.field_70179_y += var3.field_72449_c * var4 - this.field_70179_y * 0.05D;
            if (this.field_203052_f == 0) {
               this.func_184185_a(SoundEvents.field_203270_il, 10.0F, 1.0F);
            }

            ++this.field_203052_f;
         }
      }

      super.func_70071_h_();
   }

   private boolean func_207403_q() {
      Entity var1 = this.func_212360_k();
      if (var1 != null && var1.func_70089_S()) {
         return !(var1 instanceof EntityPlayerMP) || !((EntityPlayerMP)var1).func_175149_v();
      } else {
         return false;
      }
   }

   protected ItemStack func_184550_j() {
      return this.field_203054_h.func_77946_l();
   }

   @Nullable
   protected Entity func_184551_a(Vec3d var1, Vec3d var2) {
      return this.field_203051_au ? null : super.func_184551_a(var1, var2);
   }

   protected void func_203046_b(RayTraceResult var1) {
      Entity var2 = var1.field_72308_g;
      float var3 = 8.0F;
      if (var2 instanceof EntityLivingBase) {
         EntityLivingBase var4 = (EntityLivingBase)var2;
         var3 += EnchantmentHelper.func_152377_a(this.field_203054_h, var4.func_70668_bt());
      }

      Entity var10 = this.func_212360_k();
      DamageSource var5 = DamageSource.func_203096_a(this, (Entity)(var10 == null ? this : var10));
      this.field_203051_au = true;
      SoundEvent var6 = SoundEvents.field_203268_ij;
      if (var2.func_70097_a(var5, var3) && var2 instanceof EntityLivingBase) {
         EntityLivingBase var7 = (EntityLivingBase)var2;
         if (var10 instanceof EntityLivingBase) {
            EnchantmentHelper.func_151384_a(var7, var10);
            EnchantmentHelper.func_151385_b((EntityLivingBase)var10, var7);
         }

         this.func_184548_a(var7);
      }

      this.field_70159_w *= -0.009999999776482582D;
      this.field_70181_x *= -0.10000000149011612D;
      this.field_70179_y *= -0.009999999776482582D;
      float var11 = 1.0F;
      if (this.field_70170_p.func_72911_I() && EnchantmentHelper.func_203192_h(this.field_203054_h)) {
         BlockPos var8 = var2.func_180425_c();
         if (this.field_70170_p.func_175678_i(var8)) {
            EntityLightningBolt var9 = new EntityLightningBolt(this.field_70170_p, (double)var8.func_177958_n() + 0.5D, (double)var8.func_177956_o(), (double)var8.func_177952_p() + 0.5D, false);
            var9.func_204809_d(var10 instanceof EntityPlayerMP ? (EntityPlayerMP)var10 : null);
            this.field_70170_p.func_72942_c(var9);
            var6 = SoundEvents.field_203275_iq;
            var11 = 5.0F;
         }
      }

      this.func_184185_a(var6, var11, 1.0F);
   }

   protected SoundEvent func_203050_i() {
      return SoundEvents.field_203269_ik;
   }

   public void func_70100_b_(EntityPlayer var1) {
      Entity var2 = this.func_212360_k();
      if (var2 == null || var2.func_110124_au() == var1.func_110124_au()) {
         super.func_70100_b_(var1);
      }
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("Trident", 10)) {
         this.field_203054_h = ItemStack.func_199557_a(var1.func_74775_l("Trident"));
      }

      this.field_203051_au = var1.func_74767_n("DealtDamage");
      this.field_70180_af.func_187227_b(field_203053_g, (byte)EnchantmentHelper.func_203191_f(this.field_203054_h));
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74782_a("Trident", this.field_203054_h.func_77955_b(new NBTTagCompound()));
      var1.func_74757_a("DealtDamage", this.field_203051_au);
   }

   protected void func_203048_f() {
      byte var1 = (Byte)this.field_70180_af.func_187225_a(field_203053_g);
      if (this.field_70251_a != EntityArrow.PickupStatus.ALLOWED || var1 <= 0) {
         super.func_203048_f();
      }

   }

   protected float func_203044_p() {
      return 0.99F;
   }

   public boolean func_145770_h(double var1, double var3, double var5) {
      return true;
   }

   static {
      field_203053_g = EntityDataManager.func_187226_a(EntityTrident.class, DataSerializers.field_187191_a);
   }
}
