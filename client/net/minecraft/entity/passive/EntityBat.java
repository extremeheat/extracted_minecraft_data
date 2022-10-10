package net.minecraft.entity.passive;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityBat extends EntityAmbientCreature {
   private static final DataParameter<Byte> field_184660_a;
   private BlockPos field_82237_a;

   public EntityBat(World var1) {
      super(EntityType.field_200791_e, var1);
      this.func_70105_a(0.5F, 0.9F);
      this.func_82236_f(true);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184660_a, (byte)0);
   }

   protected float func_70599_aP() {
      return 0.1F;
   }

   protected float func_70647_i() {
      return super.func_70647_i() * 0.95F;
   }

   @Nullable
   public SoundEvent func_184639_G() {
      return this.func_82235_h() && this.field_70146_Z.nextInt(4) != 0 ? null : SoundEvents.field_187740_w;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187743_y;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187742_x;
   }

   public boolean func_70104_M() {
      return false;
   }

   protected void func_82167_n(Entity var1) {
   }

   protected void func_85033_bc() {
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(6.0D);
   }

   public boolean func_82235_h() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184660_a) & 1) != 0;
   }

   public void func_82236_f(boolean var1) {
      byte var2 = (Byte)this.field_70180_af.func_187225_a(field_184660_a);
      if (var1) {
         this.field_70180_af.func_187227_b(field_184660_a, (byte)(var2 | 1));
      } else {
         this.field_70180_af.func_187227_b(field_184660_a, (byte)(var2 & -2));
      }

   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.func_82235_h()) {
         this.field_70159_w = 0.0D;
         this.field_70181_x = 0.0D;
         this.field_70179_y = 0.0D;
         this.field_70163_u = (double)MathHelper.func_76128_c(this.field_70163_u) + 1.0D - (double)this.field_70131_O;
      } else {
         this.field_70181_x *= 0.6000000238418579D;
      }

   }

   protected void func_70619_bc() {
      super.func_70619_bc();
      BlockPos var1 = new BlockPos(this);
      BlockPos var2 = var1.func_177984_a();
      if (this.func_82235_h()) {
         if (this.field_70170_p.func_180495_p(var2).func_185915_l()) {
            if (this.field_70146_Z.nextInt(200) == 0) {
               this.field_70759_as = (float)this.field_70146_Z.nextInt(360);
            }

            if (this.field_70170_p.func_184136_b(this, 4.0D) != null) {
               this.func_82236_f(false);
               this.field_70170_p.func_180498_a((EntityPlayer)null, 1025, var1, 0);
            }
         } else {
            this.func_82236_f(false);
            this.field_70170_p.func_180498_a((EntityPlayer)null, 1025, var1, 0);
         }
      } else {
         if (this.field_82237_a != null && (!this.field_70170_p.func_175623_d(this.field_82237_a) || this.field_82237_a.func_177956_o() < 1)) {
            this.field_82237_a = null;
         }

         if (this.field_82237_a == null || this.field_70146_Z.nextInt(30) == 0 || this.field_82237_a.func_177954_c((double)((int)this.field_70165_t), (double)((int)this.field_70163_u), (double)((int)this.field_70161_v)) < 4.0D) {
            this.field_82237_a = new BlockPos((int)this.field_70165_t + this.field_70146_Z.nextInt(7) - this.field_70146_Z.nextInt(7), (int)this.field_70163_u + this.field_70146_Z.nextInt(6) - 2, (int)this.field_70161_v + this.field_70146_Z.nextInt(7) - this.field_70146_Z.nextInt(7));
         }

         double var3 = (double)this.field_82237_a.func_177958_n() + 0.5D - this.field_70165_t;
         double var5 = (double)this.field_82237_a.func_177956_o() + 0.1D - this.field_70163_u;
         double var7 = (double)this.field_82237_a.func_177952_p() + 0.5D - this.field_70161_v;
         this.field_70159_w += (Math.signum(var3) * 0.5D - this.field_70159_w) * 0.10000000149011612D;
         this.field_70181_x += (Math.signum(var5) * 0.699999988079071D - this.field_70181_x) * 0.10000000149011612D;
         this.field_70179_y += (Math.signum(var7) * 0.5D - this.field_70179_y) * 0.10000000149011612D;
         float var9 = (float)(MathHelper.func_181159_b(this.field_70179_y, this.field_70159_w) * 57.2957763671875D) - 90.0F;
         float var10 = MathHelper.func_76142_g(var9 - this.field_70177_z);
         this.field_191988_bg = 0.5F;
         this.field_70177_z += var10;
         if (this.field_70146_Z.nextInt(100) == 0 && this.field_70170_p.func_180495_p(var2).func_185915_l()) {
            this.func_82236_f(true);
         }
      }

   }

   protected boolean func_70041_e_() {
      return false;
   }

   public void func_180430_e(float var1, float var2) {
   }

   protected void func_184231_a(double var1, boolean var3, IBlockState var4, BlockPos var5) {
   }

   public boolean func_145773_az() {
      return true;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         if (!this.field_70170_p.field_72995_K && this.func_82235_h()) {
            this.func_82236_f(false);
         }

         return super.func_70097_a(var1, var2);
      }
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_70180_af.func_187227_b(field_184660_a, var1.func_74771_c("BatFlags"));
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74774_a("BatFlags", (Byte)this.field_70180_af.func_187225_a(field_184660_a));
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      BlockPos var3 = new BlockPos(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v);
      if (var3.func_177956_o() >= var1.func_181545_F()) {
         return false;
      } else {
         int var4 = var1.func_201696_r(var3);
         byte var5 = 4;
         if (this.func_205021_dt()) {
            var5 = 7;
         } else if (this.field_70146_Z.nextBoolean()) {
            return false;
         }

         return var4 > this.field_70146_Z.nextInt(var5) ? false : super.func_205020_a(var1, var2);
      }
   }

   private boolean func_205021_dt() {
      LocalDate var1 = LocalDate.now();
      int var2 = var1.get(ChronoField.DAY_OF_MONTH);
      int var3 = var1.get(ChronoField.MONTH_OF_YEAR);
      return var3 == 10 && var2 >= 20 || var3 == 11 && var2 <= 3;
   }

   public float func_70047_e() {
      return this.field_70131_O / 2.0F;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186377_ab;
   }

   static {
      field_184660_a = EntityDataManager.func_187226_a(EntityBat.class, DataSerializers.field_187191_a);
   }
}
