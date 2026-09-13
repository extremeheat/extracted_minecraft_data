package net.minecraft.entity.passive;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySheep extends EntityAnimal {
   private final InventoryCrafting field_90016_e = new InventoryCrafting(new EntitySheep$1(this), 2, 1);
   public static final float[][] field_70898_d = new float[][]{
      {1.0F, 1.0F, 1.0F},
      {0.85F, 0.5F, 0.2F},
      {0.7F, 0.3F, 0.85F},
      {0.4F, 0.6F, 0.85F},
      {0.9F, 0.9F, 0.2F},
      {0.5F, 0.8F, 0.1F},
      {0.95F, 0.5F, 0.65F},
      {0.3F, 0.3F, 0.3F},
      {0.6F, 0.6F, 0.6F},
      {0.3F, 0.5F, 0.6F},
      {0.5F, 0.25F, 0.7F},
      {0.2F, 0.3F, 0.7F},
      {0.4F, 0.3F, 0.2F},
      {0.4F, 0.5F, 0.2F},
      {0.6F, 0.2F, 0.2F},
      {0.1F, 0.1F, 0.1F}
   };
   private int field_70899_e;
   private EntityAIEatGrass field_146087_bs = new EntityAIEatGrass(this);

   public EntitySheep(World var1) {
      super(var1);
      this.func_70105_a(0.9F, 1.3F);
      this.func_70661_as().func_75491_a(true);
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new EntityAIPanic(this, 1.25));
      this.field_70714_bg.func_75776_a(2, new EntityAIMate(this, 1.0));
      this.field_70714_bg.func_75776_a(3, new EntityAITempt(this, 1.1, Items.field_151015_O, false));
      this.field_70714_bg.func_75776_a(4, new EntityAIFollowParent(this, 1.1));
      this.field_70714_bg.func_75776_a(5, this.field_146087_bs);
      this.field_70714_bg.func_75776_a(6, new EntityAIWander(this, 1.0));
      this.field_70714_bg.func_75776_a(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.field_90016_e.func_70299_a(0, new ItemStack(Items.field_151100_aR, 1, 0));
      this.field_90016_e.func_70299_a(1, new ItemStack(Items.field_151100_aR, 1, 0));
   }

   @Override
   protected boolean func_70650_aV() {
      return true;
   }

   @Override
   protected void func_70619_bc() {
      this.field_70899_e = this.field_146087_bs.func_151499_f();
      super.func_70619_bc();
   }

   @Override
   public void func_70636_d() {
      if (this.field_70170_p.field_72995_K) {
         this.field_70899_e = Math.max(0, this.field_70899_e - 1);
      }

      super.func_70636_d();
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(8.0);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.23000000417232513);
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, new Byte((byte)0));
   }

   @Override
   protected void func_70628_a(boolean var1, int var2) {
      if (!this.func_70892_o()) {
         this.func_70099_a(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, this.func_70896_n()), 0.0F);
      }
   }

   @Override
   protected Item func_146068_u() {
      return Item.func_150898_a(Blocks.field_150325_L);
   }

   @Override
   public void func_70103_a(byte var1) {
      if (var1 == 10) {
         this.field_70899_e = 40;
      } else {
         super.func_70103_a(var1);
      }
   }

   public float func_70894_j(float var1) {
      if (this.field_70899_e <= 0) {
         return 0.0F;
      } else if (this.field_70899_e >= 4 && this.field_70899_e <= 36) {
         return 1.0F;
      } else {
         return this.field_70899_e < 4 ? ((float)this.field_70899_e - var1) / 4.0F : -((float)(this.field_70899_e - 40) - var1) / 4.0F;
      }
   }

   public float func_70890_k(float var1) {
      if (this.field_70899_e > 4 && this.field_70899_e <= 36) {
         float var2 = ((float)(this.field_70899_e - 4) - var1) / 32.0F;
         return 0.62831855F + 0.21991149F * MathHelper.func_76126_a(var2 * 28.7F);
      } else {
         return this.field_70899_e > 0 ? 0.62831855F : this.field_70125_A / 57.295776F;
      }
   }

   @Override
   public boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      if (var2 != null && var2.func_77973_b() == Items.field_151097_aZ && !this.func_70892_o() && !this.func_70631_g_()) {
         if (!this.field_70170_p.field_72995_K) {
            this.func_70893_e(true);
            int var3 = 1 + this.field_70146_Z.nextInt(3);

            for(int var4 = 0; var4 < var3; ++var4) {
               EntityItem var5 = this.func_70099_a(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, this.func_70896_n()), 1.0F);
               var5.field_70181_x += (double)(this.field_70146_Z.nextFloat() * 0.05F);
               var5.field_70159_w += (double)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.1F);
               var5.field_70179_y += (double)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.1F);
            }
         }

         var2.func_77972_a(1, var1);
         this.func_85030_a("mob.sheep.shear", 1.0F, 1.0F);
      }

      return super.func_70085_c(var1);
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("Sheared", this.func_70892_o());
      var1.func_74774_a("Color", (byte)this.func_70896_n());
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_70893_e(var1.func_74767_n("Sheared"));
      this.func_70891_b(var1.func_74771_c("Color"));
   }

   @Override
   protected String func_70639_aQ() {
      return "mob.sheep.say";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.sheep.say";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.sheep.say";
   }

   @Override
   protected void func_145780_a(int var1, int var2, int var3, Block var4) {
      this.func_85030_a("mob.sheep.step", 0.15F, 1.0F);
   }

   public int func_70896_n() {
      return this.field_70180_af.func_75683_a(16) & 15;
   }

   public void func_70891_b(int var1) {
      byte var2 = this.field_70180_af.func_75683_a(16);
      this.field_70180_af.func_75692_b(16, (byte)(var2 & 240 | var1 & 15));
   }

   public boolean func_70892_o() {
      return (this.field_70180_af.func_75683_a(16) & 16) != 0;
   }

   public void func_70893_e(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(16);
      if (var1) {
         this.field_70180_af.func_75692_b(16, (byte)(var2 | 16));
      } else {
         this.field_70180_af.func_75692_b(16, (byte)(var2 & -17));
      }
   }

   public static int func_70895_a(Random var0) {
      int var1 = var0.nextInt(100);
      if (var1 < 5) {
         return 15;
      } else if (var1 < 10) {
         return 7;
      } else if (var1 < 15) {
         return 8;
      } else if (var1 < 18) {
         return 12;
      } else {
         return var0.nextInt(500) == 0 ? 6 : 0;
      }
   }

   public EntitySheep func_90011_a(EntityAgeable var1) {
      EntitySheep var2 = (EntitySheep)var1;
      EntitySheep var3 = new EntitySheep(this.field_70170_p);
      int var4 = this.func_90014_a(this, var2);
      var3.func_70891_b(15 - var4);
      return var3;
   }

   @Override
   public void func_70615_aA() {
      this.func_70893_e(false);
      if (this.func_70631_g_()) {
         this.func_110195_a(60);
      }
   }

   @Override
   public IEntityLivingData func_110161_a(IEntityLivingData var1) {
      var1 = super.func_110161_a(var1);
      this.func_70891_b(func_70895_a(this.field_70170_p.field_73012_v));
      return var1;
   }

   private int func_90014_a(EntityAnimal var1, EntityAnimal var2) {
      int var3 = this.func_90013_b(var1);
      int var4 = this.func_90013_b(var2);
      this.field_90016_e.func_70301_a(0).func_77964_b(var3);
      this.field_90016_e.func_70301_a(1).func_77964_b(var4);
      ItemStack var5 = CraftingManager.func_77594_a().func_82787_a(this.field_90016_e, ((EntitySheep)var1).field_70170_p);
      int var6;
      if (var5 != null && var5.func_77973_b() == Items.field_151100_aR) {
         var6 = var5.func_77960_j();
      } else {
         var6 = this.field_70170_p.field_73012_v.nextBoolean() ? var3 : var4;
      }

      return var6;
   }

   private int func_90013_b(EntityAnimal var1) {
      return 15 - ((EntitySheep)var1).func_70896_n();
   }
}
