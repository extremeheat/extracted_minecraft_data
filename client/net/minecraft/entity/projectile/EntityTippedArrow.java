package net.minecraft.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;

public class EntityTippedArrow extends EntityArrow {
   private static final DataParameter<Integer> field_184559_f;
   private PotionType field_184560_g;
   private final Set<PotionEffect> field_184561_h;
   private boolean field_191509_at;

   public EntityTippedArrow(World var1) {
      super(EntityType.field_200790_d, var1);
      this.field_184560_g = PotionTypes.field_185229_a;
      this.field_184561_h = Sets.newHashSet();
   }

   public EntityTippedArrow(World var1, double var2, double var4, double var6) {
      super(EntityType.field_200790_d, var2, var4, var6, var1);
      this.field_184560_g = PotionTypes.field_185229_a;
      this.field_184561_h = Sets.newHashSet();
   }

   public EntityTippedArrow(World var1, EntityLivingBase var2) {
      super(EntityType.field_200790_d, var2, var1);
      this.field_184560_g = PotionTypes.field_185229_a;
      this.field_184561_h = Sets.newHashSet();
   }

   public void func_184555_a(ItemStack var1) {
      if (var1.func_77973_b() == Items.field_185167_i) {
         this.field_184560_g = PotionUtils.func_185191_c(var1);
         List var2 = PotionUtils.func_185190_b(var1);
         if (!var2.isEmpty()) {
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               PotionEffect var4 = (PotionEffect)var3.next();
               this.field_184561_h.add(new PotionEffect(var4));
            }
         }

         int var5 = func_191508_b(var1);
         if (var5 == -1) {
            this.func_190548_o();
         } else {
            this.func_191507_d(var5);
         }
      } else if (var1.func_77973_b() == Items.field_151032_g) {
         this.field_184560_g = PotionTypes.field_185229_a;
         this.field_184561_h.clear();
         this.field_70180_af.func_187227_b(field_184559_f, -1);
      }

   }

   public static int func_191508_b(ItemStack var0) {
      NBTTagCompound var1 = var0.func_77978_p();
      return var1 != null && var1.func_150297_b("CustomPotionColor", 99) ? var1.func_74762_e("CustomPotionColor") : -1;
   }

   private void func_190548_o() {
      this.field_191509_at = false;
      this.field_70180_af.func_187227_b(field_184559_f, PotionUtils.func_185181_a(PotionUtils.func_185186_a(this.field_184560_g, this.field_184561_h)));
   }

   public void func_184558_a(PotionEffect var1) {
      this.field_184561_h.add(var1);
      this.func_184212_Q().func_187227_b(field_184559_f, PotionUtils.func_185181_a(PotionUtils.func_185186_a(this.field_184560_g, this.field_184561_h)));
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184559_f, -1);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K) {
         if (this.field_70254_i) {
            if (this.field_184552_b % 5 == 0) {
               this.func_184556_b(1);
            }
         } else {
            this.func_184556_b(2);
         }
      } else if (this.field_70254_i && this.field_184552_b != 0 && !this.field_184561_h.isEmpty() && this.field_184552_b >= 600) {
         this.field_70170_p.func_72960_a(this, (byte)0);
         this.field_184560_g = PotionTypes.field_185229_a;
         this.field_184561_h.clear();
         this.field_70180_af.func_187227_b(field_184559_f, -1);
      }

   }

   private void func_184556_b(int var1) {
      int var2 = this.func_184557_n();
      if (var2 != -1 && var1 > 0) {
         double var3 = (double)(var2 >> 16 & 255) / 255.0D;
         double var5 = (double)(var2 >> 8 & 255) / 255.0D;
         double var7 = (double)(var2 >> 0 & 255) / 255.0D;

         for(int var9 = 0; var9 < var1; ++var9) {
            this.field_70170_p.func_195594_a(Particles.field_197625_r, this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, var3, var5, var7);
         }

      }
   }

   public int func_184557_n() {
      return (Integer)this.field_70180_af.func_187225_a(field_184559_f);
   }

   private void func_191507_d(int var1) {
      this.field_191509_at = true;
      this.field_70180_af.func_187227_b(field_184559_f, var1);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      if (this.field_184560_g != PotionTypes.field_185229_a && this.field_184560_g != null) {
         var1.func_74778_a("Potion", IRegistry.field_212621_j.func_177774_c(this.field_184560_g).toString());
      }

      if (this.field_191509_at) {
         var1.func_74768_a("Color", this.func_184557_n());
      }

      if (!this.field_184561_h.isEmpty()) {
         NBTTagList var2 = new NBTTagList();
         Iterator var3 = this.field_184561_h.iterator();

         while(var3.hasNext()) {
            PotionEffect var4 = (PotionEffect)var3.next();
            var2.add((INBTBase)var4.func_82719_a(new NBTTagCompound()));
         }

         var1.func_74782_a("CustomPotionEffects", var2);
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("Potion", 8)) {
         this.field_184560_g = PotionUtils.func_185187_c(var1);
      }

      Iterator var2 = PotionUtils.func_185192_b(var1).iterator();

      while(var2.hasNext()) {
         PotionEffect var3 = (PotionEffect)var2.next();
         this.func_184558_a(var3);
      }

      if (var1.func_150297_b("Color", 99)) {
         this.func_191507_d(var1.func_74762_e("Color"));
      } else {
         this.func_190548_o();
      }

   }

   protected void func_184548_a(EntityLivingBase var1) {
      super.func_184548_a(var1);
      Iterator var2 = this.field_184560_g.func_185170_a().iterator();

      PotionEffect var3;
      while(var2.hasNext()) {
         var3 = (PotionEffect)var2.next();
         var1.func_195064_c(new PotionEffect(var3.func_188419_a(), Math.max(var3.func_76459_b() / 8, 1), var3.func_76458_c(), var3.func_82720_e(), var3.func_188418_e()));
      }

      if (!this.field_184561_h.isEmpty()) {
         var2 = this.field_184561_h.iterator();

         while(var2.hasNext()) {
            var3 = (PotionEffect)var2.next();
            var1.func_195064_c(var3);
         }
      }

   }

   protected ItemStack func_184550_j() {
      if (this.field_184561_h.isEmpty() && this.field_184560_g == PotionTypes.field_185229_a) {
         return new ItemStack(Items.field_151032_g);
      } else {
         ItemStack var1 = new ItemStack(Items.field_185167_i);
         PotionUtils.func_185188_a(var1, this.field_184560_g);
         PotionUtils.func_185184_a(var1, this.field_184561_h);
         if (this.field_191509_at) {
            var1.func_196082_o().func_74768_a("CustomPotionColor", this.func_184557_n());
         }

         return var1;
      }
   }

   public void func_70103_a(byte var1) {
      if (var1 == 0) {
         int var2 = this.func_184557_n();
         if (var2 != -1) {
            double var3 = (double)(var2 >> 16 & 255) / 255.0D;
            double var5 = (double)(var2 >> 8 & 255) / 255.0D;
            double var7 = (double)(var2 >> 0 & 255) / 255.0D;

            for(int var9 = 0; var9 < 20; ++var9) {
               this.field_70170_p.func_195594_a(Particles.field_197625_r, this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, var3, var5, var7);
            }
         }
      } else {
         super.func_70103_a(var1);
      }

   }

   static {
      field_184559_f = EntityDataManager.func_187226_a(EntityTippedArrow.class, DataSerializers.field_187192_b);
   }
}
