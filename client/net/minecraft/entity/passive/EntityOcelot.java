package net.minecraft.entity.passive;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityOcelot extends EntityTameable {
   private EntityAIAvoidEntity<EntityPlayer> field_175545_bm;
   private EntityAITempt field_70914_e;

   public EntityOcelot(World var1) {
      super(var1);
      this.func_70105_a(0.6F, 0.7F);
      ((PathNavigateGround)this.func_70661_as()).func_179690_a(true);
      this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, this.field_70911_d);
      this.field_70714_bg.func_75776_a(3, this.field_70914_e = new EntityAITempt(this, 0.6D, Items.field_151115_aP, true));
      this.field_70714_bg.func_75776_a(5, new EntityAIFollowOwner(this, 1.0D, 10.0F, 5.0F));
      this.field_70714_bg.func_75776_a(6, new EntityAIOcelotSit(this, 0.8D));
      this.field_70714_bg.func_75776_a(7, new EntityAILeapAtTarget(this, 0.3F));
      this.field_70714_bg.func_75776_a(8, new EntityAIOcelotAttack(this));
      this.field_70714_bg.func_75776_a(9, new EntityAIMate(this, 0.8D));
      this.field_70714_bg.func_75776_a(10, new EntityAIWander(this, 0.8D));
      this.field_70714_bg.func_75776_a(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
      this.field_70715_bh.func_75776_a(1, new EntityAITargetNonTamed(this, EntityChicken.class, false, (Predicate)null));
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(18, (byte)0);
   }

   public void func_70619_bc() {
      if (this.func_70605_aq().func_75640_a()) {
         double var1 = this.func_70605_aq().func_75638_b();
         if (var1 == 0.6D) {
            this.func_70095_a(true);
            this.func_70031_b(false);
         } else if (var1 == 1.33D) {
            this.func_70095_a(false);
            this.func_70031_b(true);
         } else {
            this.func_70095_a(false);
            this.func_70031_b(false);
         }
      } else {
         this.func_70095_a(false);
         this.func_70031_b(false);
      }

   }

   protected boolean func_70692_ba() {
      return !this.func_70909_n() && this.field_70173_aa > 2400;
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(10.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896D);
   }

   public void func_180430_e(float var1, float var2) {
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("CatType", this.func_70913_u());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_70912_b(var1.func_74762_e("CatType"));
   }

   protected String func_70639_aQ() {
      if (this.func_70909_n()) {
         if (this.func_70880_s()) {
            return "mob.cat.purr";
         } else {
            return this.field_70146_Z.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow";
         }
      } else {
         return "";
      }
   }

   protected String func_70621_aR() {
      return "mob.cat.hitt";
   }

   protected String func_70673_aS() {
      return "mob.cat.hitt";
   }

   protected float func_70599_aP() {
      return 0.4F;
   }

   protected Item func_146068_u() {
      return Items.field_151116_aA;
   }

   public boolean func_70652_k(Entity var1) {
      return var1.func_70097_a(DamageSource.func_76358_a(this), 3.0F);
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         this.field_70911_d.func_75270_a(false);
         return super.func_70097_a(var1, var2);
      }
   }

   protected void func_70628_a(boolean var1, int var2) {
   }

   public boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      if (this.func_70909_n()) {
         if (this.func_152114_e(var1) && !this.field_70170_p.field_72995_K && !this.func_70877_b(var2)) {
            this.field_70911_d.func_75270_a(!this.func_70906_o());
         }
      } else if (this.field_70914_e.func_75277_f() && var2 != null && var2.func_77973_b() == Items.field_151115_aP && var1.func_70068_e(this) < 9.0D) {
         if (!var1.field_71075_bZ.field_75098_d) {
            --var2.field_77994_a;
         }

         if (var2.field_77994_a <= 0) {
            var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, (ItemStack)null);
         }

         if (!this.field_70170_p.field_72995_K) {
            if (this.field_70146_Z.nextInt(3) == 0) {
               this.func_70903_f(true);
               this.func_70912_b(1 + this.field_70170_p.field_73012_v.nextInt(3));
               this.func_152115_b(var1.func_110124_au().toString());
               this.func_70908_e(true);
               this.field_70911_d.func_75270_a(true);
               this.field_70170_p.func_72960_a(this, (byte)7);
            } else {
               this.func_70908_e(false);
               this.field_70170_p.func_72960_a(this, (byte)6);
            }
         }

         return true;
      }

      return super.func_70085_c(var1);
   }

   public EntityOcelot func_90011_a(EntityAgeable var1) {
      EntityOcelot var2 = new EntityOcelot(this.field_70170_p);
      if (this.func_70909_n()) {
         var2.func_152115_b(this.func_152113_b());
         var2.func_70903_f(true);
         var2.func_70912_b(this.func_70913_u());
      }

      return var2;
   }

   public boolean func_70877_b(ItemStack var1) {
      return var1 != null && var1.func_77973_b() == Items.field_151115_aP;
   }

   public boolean func_70878_b(EntityAnimal var1) {
      if (var1 == this) {
         return false;
      } else if (!this.func_70909_n()) {
         return false;
      } else if (!(var1 instanceof EntityOcelot)) {
         return false;
      } else {
         EntityOcelot var2 = (EntityOcelot)var1;
         if (!var2.func_70909_n()) {
            return false;
         } else {
            return this.func_70880_s() && var2.func_70880_s();
         }
      }
   }

   public int func_70913_u() {
      return this.field_70180_af.func_75683_a(18);
   }

   public void func_70912_b(int var1) {
      this.field_70180_af.func_75692_b(18, (byte)var1);
   }

   public boolean func_70601_bi() {
      return this.field_70170_p.field_73012_v.nextInt(3) != 0;
   }

   public boolean func_70058_J() {
      if (this.field_70170_p.func_72917_a(this.func_174813_aQ(), this) && this.field_70170_p.func_72945_a(this, this.func_174813_aQ()).isEmpty() && !this.field_70170_p.func_72953_d(this.func_174813_aQ())) {
         BlockPos var1 = new BlockPos(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v);
         if (var1.func_177956_o() < this.field_70170_p.func_181545_F()) {
            return false;
         }

         Block var2 = this.field_70170_p.func_180495_p(var1.func_177977_b()).func_177230_c();
         if (var2 == Blocks.field_150349_c || var2.func_149688_o() == Material.field_151584_j) {
            return true;
         }
      }

      return false;
   }

   public String func_70005_c_() {
      if (this.func_145818_k_()) {
         return this.func_95999_t();
      } else {
         return this.func_70909_n() ? StatCollector.func_74838_a("entity.Cat.name") : super.func_70005_c_();
      }
   }

   public void func_70903_f(boolean var1) {
      super.func_70903_f(var1);
   }

   protected void func_175544_ck() {
      if (this.field_175545_bm == null) {
         this.field_175545_bm = new EntityAIAvoidEntity(this, EntityPlayer.class, 16.0F, 0.8D, 1.33D);
      }

      this.field_70714_bg.func_85156_a(this.field_175545_bm);
      if (!this.func_70909_n()) {
         this.field_70714_bg.func_75776_a(4, this.field_175545_bm);
      }

   }

   public IEntityLivingData func_180482_a(DifficultyInstance var1, IEntityLivingData var2) {
      var2 = super.func_180482_a(var1, var2);
      if (this.field_70170_p.field_73012_v.nextInt(7) == 0) {
         for(int var3 = 0; var3 < 2; ++var3) {
            EntityOcelot var4 = new EntityOcelot(this.field_70170_p);
            var4.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, 0.0F);
            var4.func_70873_a(-24000);
            this.field_70170_p.func_72838_d(var4);
         }
      }

      return var2;
   }

   // $FF: synthetic method
   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return this.func_90011_a(var1);
   }
}
