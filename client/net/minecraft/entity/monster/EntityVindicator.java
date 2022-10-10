package net.minecraft.entity.monster;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityVindicator extends AbstractIllager {
   private boolean field_190643_b;
   private static final Predicate<Entity> field_190644_c = (var0) -> {
      return var0 instanceof EntityLivingBase && ((EntityLivingBase)var0).func_190631_cK();
   };

   public EntityVindicator(World var1) {
      super(EntityType.field_200758_ax, var1);
      this.func_70105_a(0.6F, 1.95F);
   }

   protected void func_184651_r() {
      super.func_184651_r();
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(4, new EntityAIAttackMelee(this, 1.0D, false));
      this.field_70714_bg.func_75776_a(8, new EntityAIWander(this, 0.6D));
      this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
      this.field_70714_bg.func_75776_a(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, true, new Class[]{EntityVindicator.class}));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, true));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
      this.field_70715_bh.func_75776_a(4, new EntityVindicator.AIJohnnyAttack(this));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.3499999940395355D);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(12.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(24.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(5.0D);
   }

   protected void func_70088_a() {
      super.func_70088_a();
   }

   protected ResourceLocation func_184647_J() {
      return LootTableList.field_191186_av;
   }

   public boolean func_190639_o() {
      return this.func_193078_a(1);
   }

   public void func_190636_a(boolean var1) {
      this.func_193079_a(1, var1);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      if (this.field_190643_b) {
         var1.func_74757_a("Johnny", true);
      }

   }

   public AbstractIllager.IllagerArmPose func_193077_p() {
      return this.func_190639_o() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.CROSSED;
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("Johnny", 99)) {
         this.field_190643_b = var1.func_74767_n("Johnny");
      }

   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      IEntityLivingData var4 = super.func_204210_a(var1, var2, var3);
      this.func_180481_a(var1);
      this.func_180483_b(var1);
      return var4;
   }

   protected void func_180481_a(DifficultyInstance var1) {
      this.func_184201_a(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.field_151036_c));
   }

   protected void func_70619_bc() {
      super.func_70619_bc();
      this.func_190636_a(this.func_70638_az() != null);
   }

   public boolean func_184191_r(Entity var1) {
      if (super.func_184191_r(var1)) {
         return true;
      } else if (var1 instanceof EntityLivingBase && ((EntityLivingBase)var1).func_70668_bt() == CreatureAttribute.ILLAGER) {
         return this.func_96124_cp() == null && var1.func_96124_cp() == null;
      } else {
         return false;
      }
   }

   public void func_200203_b(@Nullable ITextComponent var1) {
      super.func_200203_b(var1);
      if (!this.field_190643_b && var1 != null && var1.getString().equals("Johnny")) {
         this.field_190643_b = true;
      }

   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_191268_hm;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_191269_hn;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_191270_ho;
   }

   static class AIJohnnyAttack extends EntityAINearestAttackableTarget<EntityLivingBase> {
      public AIJohnnyAttack(EntityVindicator var1) {
         super(var1, EntityLivingBase.class, 0, true, true, EntityVindicator.field_190644_c);
      }

      public boolean func_75250_a() {
         return ((EntityVindicator)this.field_75299_d).field_190643_b && super.func_75250_a();
      }
   }
}
