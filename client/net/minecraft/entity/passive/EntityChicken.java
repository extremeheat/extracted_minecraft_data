package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityChicken extends EntityAnimal {
   private static final Ingredient field_184761_bD;
   public float field_70886_e;
   public float field_70883_f;
   public float field_70884_g;
   public float field_70888_h;
   public float field_70889_i = 1.0F;
   public int field_70887_j;
   public boolean field_152118_bv;

   public EntityChicken(World var1) {
      super(EntityType.field_200795_i, var1);
      this.func_70105_a(0.4F, 0.7F);
      this.field_70887_j = this.field_70146_Z.nextInt(6000) + 6000;
      this.func_184644_a(PathNodeType.WATER, 0.0F);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new EntityAIPanic(this, 1.4D));
      this.field_70714_bg.func_75776_a(2, new EntityAIMate(this, 1.0D));
      this.field_70714_bg.func_75776_a(3, new EntityAITempt(this, 1.0D, false, field_184761_bD));
      this.field_70714_bg.func_75776_a(4, new EntityAIFollowParent(this, 1.1D));
      this.field_70714_bg.func_75776_a(5, new EntityAIWanderAvoidWater(this, 1.0D));
      this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(7, new EntityAILookIdle(this));
   }

   public float func_70047_e() {
      return this.field_70131_O;
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(4.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25D);
   }

   public void func_70636_d() {
      super.func_70636_d();
      this.field_70888_h = this.field_70886_e;
      this.field_70884_g = this.field_70883_f;
      this.field_70883_f = (float)((double)this.field_70883_f + (double)(this.field_70122_E ? -1 : 4) * 0.3D);
      this.field_70883_f = MathHelper.func_76131_a(this.field_70883_f, 0.0F, 1.0F);
      if (!this.field_70122_E && this.field_70889_i < 1.0F) {
         this.field_70889_i = 1.0F;
      }

      this.field_70889_i = (float)((double)this.field_70889_i * 0.9D);
      if (!this.field_70122_E && this.field_70181_x < 0.0D) {
         this.field_70181_x *= 0.6D;
      }

      this.field_70886_e += this.field_70889_i * 2.0F;
      if (!this.field_70170_p.field_72995_K && !this.func_70631_g_() && !this.func_152116_bZ() && --this.field_70887_j <= 0) {
         this.func_184185_a(SoundEvents.field_187665_Y, 1.0F, (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
         this.func_199703_a(Items.field_151110_aK);
         this.field_70887_j = this.field_70146_Z.nextInt(6000) + 6000;
      }

   }

   public void func_180430_e(float var1, float var2) {
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187660_W;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187666_Z;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187663_X;
   }

   protected void func_180429_a(BlockPos var1, IBlockState var2) {
      this.func_184185_a(SoundEvents.field_187538_aa, 0.15F, 1.0F);
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186394_B;
   }

   public EntityChicken func_90011_a(EntityAgeable var1) {
      return new EntityChicken(this.field_70170_p);
   }

   public boolean func_70877_b(ItemStack var1) {
      return field_184761_bD.test(var1);
   }

   protected int func_70693_a(EntityPlayer var1) {
      return this.func_152116_bZ() ? 10 : super.func_70693_a(var1);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_152118_bv = var1.func_74767_n("IsChickenJockey");
      if (var1.func_74764_b("EggLayTime")) {
         this.field_70887_j = var1.func_74762_e("EggLayTime");
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("IsChickenJockey", this.field_152118_bv);
      var1.func_74768_a("EggLayTime", this.field_70887_j);
   }

   public boolean func_70692_ba() {
      return this.func_152116_bZ() && !this.func_184207_aI();
   }

   public void func_184232_k(Entity var1) {
      super.func_184232_k(var1);
      float var2 = MathHelper.func_76126_a(this.field_70761_aq * 0.017453292F);
      float var3 = MathHelper.func_76134_b(this.field_70761_aq * 0.017453292F);
      float var4 = 0.1F;
      float var5 = 0.0F;
      var1.func_70107_b(this.field_70165_t + (double)(0.1F * var2), this.field_70163_u + (double)(this.field_70131_O * 0.5F) + var1.func_70033_W() + 0.0D, this.field_70161_v - (double)(0.1F * var3));
      if (var1 instanceof EntityLivingBase) {
         ((EntityLivingBase)var1).field_70761_aq = this.field_70761_aq;
      }

   }

   public boolean func_152116_bZ() {
      return this.field_152118_bv;
   }

   public void func_152117_i(boolean var1) {
      this.field_152118_bv = var1;
   }

   // $FF: synthetic method
   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return this.func_90011_a(var1);
   }

   static {
      field_184761_bD = Ingredient.func_199804_a(Items.field_151014_N, Items.field_151081_bc, Items.field_151080_bb, Items.field_185163_cU);
   }
}
