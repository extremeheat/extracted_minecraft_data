package net.minecraft.entity;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class EntityHanging extends Entity {
   protected static final Predicate<Entity> field_184524_c = (var0) -> {
      return var0 instanceof EntityHanging;
   };
   private int field_70520_f;
   protected BlockPos field_174861_a;
   @Nullable
   public EnumFacing field_174860_b;

   protected EntityHanging(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.func_70105_a(0.5F, 0.5F);
   }

   protected EntityHanging(EntityType<?> var1, World var2, BlockPos var3) {
      this(var1, var2);
      this.field_174861_a = var3;
   }

   protected void func_70088_a() {
   }

   protected void func_174859_a(EnumFacing var1) {
      Validate.notNull(var1);
      Validate.isTrue(var1.func_176740_k().func_176722_c());
      this.field_174860_b = var1;
      this.field_70177_z = (float)(this.field_174860_b.func_176736_b() * 90);
      this.field_70126_B = this.field_70177_z;
      this.func_174856_o();
   }

   protected void func_174856_o() {
      if (this.field_174860_b != null) {
         double var1 = (double)this.field_174861_a.func_177958_n() + 0.5D;
         double var3 = (double)this.field_174861_a.func_177956_o() + 0.5D;
         double var5 = (double)this.field_174861_a.func_177952_p() + 0.5D;
         double var7 = 0.46875D;
         double var9 = this.func_190202_a(this.func_82329_d());
         double var11 = this.func_190202_a(this.func_82330_g());
         var1 -= (double)this.field_174860_b.func_82601_c() * 0.46875D;
         var5 -= (double)this.field_174860_b.func_82599_e() * 0.46875D;
         var3 += var11;
         EnumFacing var13 = this.field_174860_b.func_176735_f();
         var1 += var9 * (double)var13.func_82601_c();
         var5 += var9 * (double)var13.func_82599_e();
         this.field_70165_t = var1;
         this.field_70163_u = var3;
         this.field_70161_v = var5;
         double var14 = (double)this.func_82329_d();
         double var16 = (double)this.func_82330_g();
         double var18 = (double)this.func_82329_d();
         if (this.field_174860_b.func_176740_k() == EnumFacing.Axis.Z) {
            var18 = 1.0D;
         } else {
            var14 = 1.0D;
         }

         var14 /= 32.0D;
         var16 /= 32.0D;
         var18 /= 32.0D;
         this.func_174826_a(new AxisAlignedBB(var1 - var14, var3 - var16, var5 - var18, var1 + var14, var3 + var16, var5 + var18));
      }
   }

   private double func_190202_a(int var1) {
      return var1 % 32 == 0 ? 0.5D : 0.0D;
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70520_f++ == 100 && !this.field_70170_p.field_72995_K) {
         this.field_70520_f = 0;
         if (!this.field_70128_L && !this.func_70518_d()) {
            this.func_70106_y();
            this.func_110128_b((Entity)null);
         }
      }

   }

   public boolean func_70518_d() {
      if (!this.field_70170_p.func_195586_b(this, this.func_174813_aQ())) {
         return false;
      } else {
         int var1 = Math.max(1, this.func_82329_d() / 16);
         int var2 = Math.max(1, this.func_82330_g() / 16);
         BlockPos var3 = this.field_174861_a.func_177972_a(this.field_174860_b.func_176734_d());
         EnumFacing var4 = this.field_174860_b.func_176735_f();
         BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

         for(int var6 = 0; var6 < var1; ++var6) {
            for(int var7 = 0; var7 < var2; ++var7) {
               int var8 = (var1 - 1) / -2;
               int var9 = (var2 - 1) / -2;
               var5.func_189533_g(var3).func_189534_c(var4, var6 + var8).func_189534_c(EnumFacing.UP, var7 + var9);
               IBlockState var10 = this.field_70170_p.func_180495_p(var5);
               if (!var10.func_185904_a().func_76220_a() && !BlockRedstoneDiode.func_185546_B(var10)) {
                  return false;
               }
            }
         }

         return this.field_70170_p.func_175674_a(this, this.func_174813_aQ(), field_184524_c).isEmpty();
      }
   }

   public boolean func_70067_L() {
      return true;
   }

   public boolean func_85031_j(Entity var1) {
      return var1 instanceof EntityPlayer ? this.func_70097_a(DamageSource.func_76365_a((EntityPlayer)var1), 0.0F) : false;
   }

   public EnumFacing func_174811_aO() {
      return this.field_174860_b;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         if (!this.field_70128_L && !this.field_70170_p.field_72995_K) {
            this.func_70106_y();
            this.func_70018_K();
            this.func_110128_b(var1.func_76346_g());
         }

         return true;
      }
   }

   public void func_70091_d(MoverType var1, double var2, double var4, double var6) {
      if (!this.field_70170_p.field_72995_K && !this.field_70128_L && var2 * var2 + var4 * var4 + var6 * var6 > 0.0D) {
         this.func_70106_y();
         this.func_110128_b((Entity)null);
      }

   }

   public void func_70024_g(double var1, double var3, double var5) {
      if (!this.field_70170_p.field_72995_K && !this.field_70128_L && var1 * var1 + var3 * var3 + var5 * var5 > 0.0D) {
         this.func_70106_y();
         this.func_110128_b((Entity)null);
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74774_a("Facing", (byte)this.field_174860_b.func_176736_b());
      BlockPos var2 = this.func_174857_n();
      var1.func_74768_a("TileX", var2.func_177958_n());
      var1.func_74768_a("TileY", var2.func_177956_o());
      var1.func_74768_a("TileZ", var2.func_177952_p());
   }

   public void func_70037_a(NBTTagCompound var1) {
      this.field_174861_a = new BlockPos(var1.func_74762_e("TileX"), var1.func_74762_e("TileY"), var1.func_74762_e("TileZ"));
      this.func_174859_a(EnumFacing.func_176731_b(var1.func_74771_c("Facing")));
   }

   public abstract int func_82329_d();

   public abstract int func_82330_g();

   public abstract void func_110128_b(@Nullable Entity var1);

   public abstract void func_184523_o();

   public EntityItem func_70099_a(ItemStack var1, float var2) {
      EntityItem var3 = new EntityItem(this.field_70170_p, this.field_70165_t + (double)((float)this.field_174860_b.func_82601_c() * 0.15F), this.field_70163_u + (double)var2, this.field_70161_v + (double)((float)this.field_174860_b.func_82599_e() * 0.15F), var1);
      var3.func_174869_p();
      this.field_70170_p.func_72838_d(var3);
      return var3;
   }

   protected boolean func_142008_O() {
      return false;
   }

   public void func_70107_b(double var1, double var3, double var5) {
      this.field_174861_a = new BlockPos(var1, var3, var5);
      this.func_174856_o();
      this.field_70160_al = true;
   }

   public BlockPos func_174857_n() {
      return this.field_174861_a;
   }

   public float func_184229_a(Rotation var1) {
      if (this.field_174860_b != null && this.field_174860_b.func_176740_k() != EnumFacing.Axis.Y) {
         switch(var1) {
         case CLOCKWISE_180:
            this.field_174860_b = this.field_174860_b.func_176734_d();
            break;
         case COUNTERCLOCKWISE_90:
            this.field_174860_b = this.field_174860_b.func_176735_f();
            break;
         case CLOCKWISE_90:
            this.field_174860_b = this.field_174860_b.func_176746_e();
         }
      }

      float var2 = MathHelper.func_76142_g(this.field_70177_z);
      switch(var1) {
      case CLOCKWISE_180:
         return var2 + 180.0F;
      case COUNTERCLOCKWISE_90:
         return var2 + 90.0F;
      case CLOCKWISE_90:
         return var2 + 270.0F;
      default:
         return var2;
      }
   }

   public float func_184217_a(Mirror var1) {
      return this.func_184229_a(var1.func_185800_a(this.field_174860_b));
   }

   public void func_70077_a(EntityLightningBolt var1) {
   }
}
