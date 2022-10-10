package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntitySpectralArrow extends EntityArrow {
   private int field_184562_f = 200;

   public EntitySpectralArrow(World var1) {
      super(EntityType.field_200747_am, var1);
   }

   public EntitySpectralArrow(World var1, EntityLivingBase var2) {
      super(EntityType.field_200747_am, var2, var1);
   }

   public EntitySpectralArrow(World var1, double var2, double var4, double var6) {
      super(EntityType.field_200747_am, var2, var4, var6, var1);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K && !this.field_70254_i) {
         this.field_70170_p.func_195594_a(Particles.field_197590_A, this.field_70165_t, this.field_70163_u, this.field_70161_v, 0.0D, 0.0D, 0.0D);
      }

   }

   protected ItemStack func_184550_j() {
      return new ItemStack(Items.field_185166_h);
   }

   protected void func_184548_a(EntityLivingBase var1) {
      super.func_184548_a(var1);
      PotionEffect var2 = new PotionEffect(MobEffects.field_188423_x, this.field_184562_f, 0);
      var1.func_195064_c(var2);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_74764_b("Duration")) {
         this.field_184562_f = var1.func_74762_e("Duration");
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("Duration", this.field_184562_f);
   }
}
