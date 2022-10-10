package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityMooshroom extends EntityCow {
   public EntityMooshroom(World var1) {
      super(EntityType.field_200780_T, var1);
      this.func_70105_a(0.9F, 1.4F);
      this.field_175506_bl = Blocks.field_150391_bh;
   }

   public boolean func_184645_a(EntityPlayer var1, EnumHand var2) {
      ItemStack var3 = var1.func_184586_b(var2);
      if (var3.func_77973_b() == Items.field_151054_z && this.func_70874_b() >= 0 && !var1.field_71075_bZ.field_75098_d) {
         var3.func_190918_g(1);
         if (var3.func_190926_b()) {
            var1.func_184611_a(var2, new ItemStack(Items.field_151009_A));
         } else if (!var1.field_71071_by.func_70441_a(new ItemStack(Items.field_151009_A))) {
            var1.func_71019_a(new ItemStack(Items.field_151009_A), false);
         }

         return true;
      } else if (var3.func_77973_b() == Items.field_151097_aZ && this.func_70874_b() >= 0) {
         this.field_70170_p.func_195594_a(Particles.field_197627_t, this.field_70165_t, this.field_70163_u + (double)(this.field_70131_O / 2.0F), this.field_70161_v, 0.0D, 0.0D, 0.0D);
         if (!this.field_70170_p.field_72995_K) {
            this.func_70106_y();
            EntityCow var4 = new EntityCow(this.field_70170_p);
            var4.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
            var4.func_70606_j(this.func_110143_aJ());
            var4.field_70761_aq = this.field_70761_aq;
            if (this.func_145818_k_()) {
               var4.func_200203_b(this.func_200201_e());
            }

            this.field_70170_p.func_72838_d(var4);

            for(int var5 = 0; var5 < 5; ++var5) {
               this.field_70170_p.func_72838_d(new EntityItem(this.field_70170_p, this.field_70165_t, this.field_70163_u + (double)this.field_70131_O, this.field_70161_v, new ItemStack(Blocks.field_150337_Q)));
            }

            var3.func_77972_a(1, var1);
            this.func_184185_a(SoundEvents.field_187784_dt, 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.func_184645_a(var1, var2);
      }
   }

   public EntityMooshroom func_90011_a(EntityAgeable var1) {
      return new EntityMooshroom(this.field_70170_p);
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186400_H;
   }

   // $FF: synthetic method
   public EntityCow func_90011_a(EntityAgeable var1) {
      return this.func_90011_a(var1);
   }

   // $FF: synthetic method
   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return this.func_90011_a(var1);
   }
}
