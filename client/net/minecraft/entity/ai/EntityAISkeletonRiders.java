package net.minecraft.entity.ai;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntitySkeletonHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;

public class EntityAISkeletonRiders extends EntityAIBase {
   private final EntitySkeletonHorse field_188516_a;

   public EntityAISkeletonRiders(EntitySkeletonHorse var1) {
      super();
      this.field_188516_a = var1;
   }

   public boolean func_75250_a() {
      return this.field_188516_a.field_70170_p.func_175636_b(this.field_188516_a.field_70165_t, this.field_188516_a.field_70163_u, this.field_188516_a.field_70161_v, 10.0D);
   }

   public void func_75246_d() {
      DifficultyInstance var1 = this.field_188516_a.field_70170_p.func_175649_E(new BlockPos(this.field_188516_a));
      this.field_188516_a.func_190691_p(false);
      this.field_188516_a.func_110234_j(true);
      this.field_188516_a.func_70873_a(0);
      this.field_188516_a.field_70170_p.func_72942_c(new EntityLightningBolt(this.field_188516_a.field_70170_p, this.field_188516_a.field_70165_t, this.field_188516_a.field_70163_u, this.field_188516_a.field_70161_v, true));
      EntitySkeleton var2 = this.func_188514_a(var1, this.field_188516_a);
      var2.func_184220_m(this.field_188516_a);

      for(int var3 = 0; var3 < 3; ++var3) {
         AbstractHorse var4 = this.func_188515_a(var1);
         EntitySkeleton var5 = this.func_188514_a(var1, var4);
         var5.func_184220_m(var4);
         var4.func_70024_g(this.field_188516_a.func_70681_au().nextGaussian() * 0.5D, 0.0D, this.field_188516_a.func_70681_au().nextGaussian() * 0.5D);
      }

   }

   private AbstractHorse func_188515_a(DifficultyInstance var1) {
      EntitySkeletonHorse var2 = new EntitySkeletonHorse(this.field_188516_a.field_70170_p);
      var2.func_204210_a(var1, (IEntityLivingData)null, (NBTTagCompound)null);
      var2.func_70107_b(this.field_188516_a.field_70165_t, this.field_188516_a.field_70163_u, this.field_188516_a.field_70161_v);
      var2.field_70172_ad = 60;
      var2.func_110163_bv();
      var2.func_110234_j(true);
      var2.func_70873_a(0);
      var2.field_70170_p.func_72838_d(var2);
      return var2;
   }

   private EntitySkeleton func_188514_a(DifficultyInstance var1, AbstractHorse var2) {
      EntitySkeleton var3 = new EntitySkeleton(var2.field_70170_p);
      var3.func_204210_a(var1, (IEntityLivingData)null, (NBTTagCompound)null);
      var3.func_70107_b(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v);
      var3.field_70172_ad = 60;
      var3.func_110163_bv();
      if (var3.func_184582_a(EntityEquipmentSlot.HEAD).func_190926_b()) {
         var3.func_184201_a(EntityEquipmentSlot.HEAD, new ItemStack(Items.field_151028_Y));
      }

      var3.func_184201_a(EntityEquipmentSlot.MAINHAND, EnchantmentHelper.func_77504_a(var3.func_70681_au(), var3.func_184614_ca(), (int)(5.0F + var1.func_180170_c() * (float)var3.func_70681_au().nextInt(18)), false));
      var3.func_184201_a(EntityEquipmentSlot.HEAD, EnchantmentHelper.func_77504_a(var3.func_70681_au(), var3.func_184582_a(EntityEquipmentSlot.HEAD), (int)(5.0F + var1.func_180170_c() * (float)var3.func_70681_au().nextInt(18)), false));
      var3.field_70170_p.func_72838_d(var3);
      return var3;
   }
}
