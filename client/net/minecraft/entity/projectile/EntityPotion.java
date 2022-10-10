package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPotion extends EntityThrowable {
   private static final DataParameter<ItemStack> field_184545_d;
   private static final Logger field_184546_e;
   public static final Predicate<EntityLivingBase> field_190546_d;

   public EntityPotion(World var1) {
      super(EntityType.field_200754_at, var1);
   }

   public EntityPotion(World var1, EntityLivingBase var2, ItemStack var3) {
      super(EntityType.field_200754_at, var2, var1);
      this.func_184541_a(var3);
   }

   public EntityPotion(World var1, double var2, double var4, double var6, ItemStack var8) {
      super(EntityType.field_200754_at, var2, var4, var6, var1);
      if (!var8.func_190926_b()) {
         this.func_184541_a(var8);
      }

   }

   protected void func_70088_a() {
      this.func_184212_Q().func_187214_a(field_184545_d, ItemStack.field_190927_a);
   }

   public ItemStack func_184543_l() {
      ItemStack var1 = (ItemStack)this.func_184212_Q().func_187225_a(field_184545_d);
      if (var1.func_77973_b() != Items.field_185155_bH && var1.func_77973_b() != Items.field_185156_bI) {
         if (this.field_70170_p != null) {
            field_184546_e.error("ThrownPotion entity {} has no item?!", this.func_145782_y());
         }

         return new ItemStack(Items.field_185155_bH);
      } else {
         return var1;
      }
   }

   public void func_184541_a(ItemStack var1) {
      this.func_184212_Q().func_187227_b(field_184545_d, var1);
   }

   protected float func_70185_h() {
      return 0.05F;
   }

   protected void func_70184_a(RayTraceResult var1) {
      if (!this.field_70170_p.field_72995_K) {
         ItemStack var2 = this.func_184543_l();
         PotionType var3 = PotionUtils.func_185191_c(var2);
         List var4 = PotionUtils.func_185189_a(var2);
         boolean var5 = var3 == PotionTypes.field_185230_b && var4.isEmpty();
         if (var1.field_72313_a == RayTraceResult.Type.BLOCK && var5) {
            BlockPos var6 = var1.func_178782_a().func_177972_a(var1.field_178784_b);
            this.func_184542_a(var6, var1.field_178784_b);
            Iterator var7 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(var7.hasNext()) {
               EnumFacing var8 = (EnumFacing)var7.next();
               this.func_184542_a(var6.func_177972_a(var8), var8);
            }
         }

         if (var5) {
            this.func_190545_n();
         } else if (!var4.isEmpty()) {
            if (this.func_184544_n()) {
               this.func_190542_a(var2, var3);
            } else {
               this.func_190543_a(var1, var4);
            }
         }

         int var9 = var3.func_185172_c() ? 2007 : 2002;
         this.field_70170_p.func_175718_b(var9, new BlockPos(this), PotionUtils.func_190932_c(var2));
         this.func_70106_y();
      }
   }

   private void func_190545_n() {
      AxisAlignedBB var1 = this.func_174813_aQ().func_72314_b(4.0D, 2.0D, 4.0D);
      List var2 = this.field_70170_p.func_175647_a(EntityLivingBase.class, var1, field_190546_d);
      if (!var2.isEmpty()) {
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            EntityLivingBase var4 = (EntityLivingBase)var3.next();
            double var5 = this.func_70068_e(var4);
            if (var5 < 16.0D && func_190544_c(var4)) {
               var4.func_70097_a(DamageSource.field_76369_e, 1.0F);
            }
         }
      }

   }

   private void func_190543_a(RayTraceResult var1, List<PotionEffect> var2) {
      AxisAlignedBB var3 = this.func_174813_aQ().func_72314_b(4.0D, 2.0D, 4.0D);
      List var4 = this.field_70170_p.func_72872_a(EntityLivingBase.class, var3);
      if (!var4.isEmpty()) {
         Iterator var5 = var4.iterator();

         while(true) {
            EntityLivingBase var6;
            double var7;
            do {
               do {
                  if (!var5.hasNext()) {
                     return;
                  }

                  var6 = (EntityLivingBase)var5.next();
               } while(!var6.func_184603_cC());

               var7 = this.func_70068_e(var6);
            } while(var7 >= 16.0D);

            double var9 = 1.0D - Math.sqrt(var7) / 4.0D;
            if (var6 == var1.field_72308_g) {
               var9 = 1.0D;
            }

            Iterator var11 = var2.iterator();

            while(var11.hasNext()) {
               PotionEffect var12 = (PotionEffect)var11.next();
               Potion var13 = var12.func_188419_a();
               if (var13.func_76403_b()) {
                  var13.func_180793_a(this, this.func_85052_h(), var6, var12.func_76458_c(), var9);
               } else {
                  int var14 = (int)(var9 * (double)var12.func_76459_b() + 0.5D);
                  if (var14 > 20) {
                     var6.func_195064_c(new PotionEffect(var13, var14, var12.func_76458_c(), var12.func_82720_e(), var12.func_188418_e()));
                  }
               }
            }
         }
      }
   }

   private void func_190542_a(ItemStack var1, PotionType var2) {
      EntityAreaEffectCloud var3 = new EntityAreaEffectCloud(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v);
      var3.func_184481_a(this.func_85052_h());
      var3.func_184483_a(3.0F);
      var3.func_184495_b(-0.5F);
      var3.func_184485_d(10);
      var3.func_184487_c(-var3.func_184490_j() / (float)var3.func_184489_o());
      var3.func_184484_a(var2);
      Iterator var4 = PotionUtils.func_185190_b(var1).iterator();

      while(var4.hasNext()) {
         PotionEffect var5 = (PotionEffect)var4.next();
         var3.func_184496_a(new PotionEffect(var5));
      }

      NBTTagCompound var6 = var1.func_77978_p();
      if (var6 != null && var6.func_150297_b("CustomPotionColor", 99)) {
         var3.func_184482_a(var6.func_74762_e("CustomPotionColor"));
      }

      this.field_70170_p.func_72838_d(var3);
   }

   private boolean func_184544_n() {
      return this.func_184543_l().func_77973_b() == Items.field_185156_bI;
   }

   private void func_184542_a(BlockPos var1, EnumFacing var2) {
      if (this.field_70170_p.func_180495_p(var1).func_177230_c() == Blocks.field_150480_ab) {
         this.field_70170_p.func_175719_a((EntityPlayer)null, var1.func_177972_a(var2), var2.func_176734_d());
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      ItemStack var2 = ItemStack.func_199557_a(var1.func_74775_l("Potion"));
      if (var2.func_190926_b()) {
         this.func_70106_y();
      } else {
         this.func_184541_a(var2);
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      ItemStack var2 = this.func_184543_l();
      if (!var2.func_190926_b()) {
         var1.func_74782_a("Potion", var2.func_77955_b(new NBTTagCompound()));
      }

   }

   private static boolean func_190544_c(EntityLivingBase var0) {
      return var0 instanceof EntityEnderman || var0 instanceof EntityBlaze;
   }

   static {
      field_184545_d = EntityDataManager.func_187226_a(EntityPotion.class, DataSerializers.field_187196_f);
      field_184546_e = LogManager.getLogger();
      field_190546_d = EntityPotion::func_190544_c;
   }
}
