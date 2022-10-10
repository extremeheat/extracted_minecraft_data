package net.minecraft.entity.passive;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityPufferFish extends AbstractFish {
   private static final DataParameter<Integer> field_203716_b;
   private int field_203717_c;
   private int field_203718_bx;
   private static final Predicate<EntityLivingBase> field_205724_bA;
   private float field_205722_bB = -1.0F;
   private float field_205723_bC;

   public EntityPufferFish(World var1) {
      super(EntityType.field_203779_Z, var1);
      this.func_70105_a(0.7F, 0.7F);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_203716_b, 0);
   }

   public int func_203715_dA() {
      return (Integer)this.field_70180_af.func_187225_a(field_203716_b);
   }

   public void func_203714_a(int var1) {
      this.field_70180_af.func_187227_b(field_203716_b, var1);
      this.func_205718_b(var1);
   }

   private void func_205718_b(int var1) {
      float var2 = 1.0F;
      if (var1 == 1) {
         var2 = 0.7F;
      } else if (var1 == 0) {
         var2 = 0.5F;
      }

      this.func_205717_a(var2);
   }

   protected final void func_70105_a(float var1, float var2) {
      boolean var3 = this.field_205722_bB > 0.0F;
      this.field_205722_bB = var1;
      this.field_205723_bC = var2;
      if (!var3) {
         this.func_205717_a(1.0F);
      }

   }

   private void func_205717_a(float var1) {
      super.func_70105_a(this.field_205722_bB * var1, this.field_205723_bC * var1);
   }

   public void func_184206_a(DataParameter<?> var1) {
      this.func_205718_b(this.func_203715_dA());
      super.func_184206_a(var1);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("PuffState", this.func_203715_dA());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_203714_a(var1.func_74762_e("PuffState"));
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_203812_az;
   }

   protected ItemStack func_203707_dx() {
      return new ItemStack(Items.field_203795_aL);
   }

   protected void func_184651_r() {
      super.func_184651_r();
      this.field_70714_bg.func_75776_a(1, new EntityPufferFish.AIPuff(this));
   }

   public void func_70071_h_() {
      if (this.func_70089_S() && !this.field_70170_p.field_72995_K) {
         if (this.field_203717_c > 0) {
            if (this.func_203715_dA() == 0) {
               this.func_184185_a(SoundEvents.field_203826_go, this.func_70599_aP(), this.func_70647_i());
               this.func_203714_a(1);
            } else if (this.field_203717_c > 40 && this.func_203715_dA() == 1) {
               this.func_184185_a(SoundEvents.field_203826_go, this.func_70599_aP(), this.func_70647_i());
               this.func_203714_a(2);
            }

            ++this.field_203717_c;
         } else if (this.func_203715_dA() != 0) {
            if (this.field_203718_bx > 60 && this.func_203715_dA() == 2) {
               this.func_184185_a(SoundEvents.field_203825_gn, this.func_70599_aP(), this.func_70647_i());
               this.func_203714_a(1);
            } else if (this.field_203718_bx > 100 && this.func_203715_dA() == 1) {
               this.func_184185_a(SoundEvents.field_203825_gn, this.func_70599_aP(), this.func_70647_i());
               this.func_203714_a(0);
            }

            ++this.field_203718_bx;
         }
      }

      super.func_70071_h_();
   }

   public void func_70636_d() {
      super.func_70636_d();
      if (this.func_203715_dA() > 0) {
         List var1 = this.field_70170_p.func_175647_a(EntityLiving.class, this.func_174813_aQ().func_186662_g(0.3D), field_205724_bA);
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            EntityLiving var3 = (EntityLiving)var2.next();
            if (var3.func_70089_S()) {
               this.func_205719_a(var3);
            }
         }
      }

   }

   private void func_205719_a(EntityLiving var1) {
      int var2 = this.func_203715_dA();
      if (var1.func_70097_a(DamageSource.func_76358_a(this), (float)(1 + var2))) {
         var1.func_195064_c(new PotionEffect(MobEffects.field_76436_u, 60 * var2, 0));
         this.func_184185_a(SoundEvents.field_203830_gs, 1.0F, 1.0F);
      }

   }

   public void func_70100_b_(EntityPlayer var1) {
      int var2 = this.func_203715_dA();
      if (var1 instanceof EntityPlayerMP && var2 > 0 && var1.func_70097_a(DamageSource.func_76358_a(this), (float)(1 + var2))) {
         ((EntityPlayerMP)var1).field_71135_a.func_147359_a(new SPacketChangeGameState(9, 0.0F));
         var1.func_195064_c(new PotionEffect(MobEffects.field_76436_u, 60 * var2, 0));
      }

   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_203824_gm;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_203827_gp;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_203829_gr;
   }

   protected SoundEvent func_203701_dz() {
      return SoundEvents.field_203828_gq;
   }

   static {
      field_203716_b = EntityDataManager.func_187226_a(EntityPufferFish.class, DataSerializers.field_187192_b);
      field_205724_bA = (var0) -> {
         if (var0 == null) {
            return false;
         } else if (!(var0 instanceof EntityPlayer) || !((EntityPlayer)var0).func_175149_v() && !((EntityPlayer)var0).func_184812_l_()) {
            return var0.func_70668_bt() != CreatureAttribute.field_203100_e;
         } else {
            return false;
         }
      };
   }

   static class AIPuff extends EntityAIBase {
      private final EntityPufferFish field_203789_a;

      public AIPuff(EntityPufferFish var1) {
         super();
         this.field_203789_a = var1;
      }

      public boolean func_75250_a() {
         List var1 = this.field_203789_a.field_70170_p.func_175647_a(EntityLivingBase.class, this.field_203789_a.func_174813_aQ().func_186662_g(2.0D), EntityPufferFish.field_205724_bA);
         return !var1.isEmpty();
      }

      public void func_75249_e() {
         this.field_203789_a.field_203717_c = 1;
         this.field_203789_a.field_203718_bx = 0;
      }

      public void func_75251_c() {
         this.field_203789_a.field_203717_c = 0;
      }

      public boolean func_75253_b() {
         List var1 = this.field_203789_a.field_70170_p.func_175647_a(EntityLivingBase.class, this.field_203789_a.func_174813_aQ().func_186662_g(2.0D), EntityPufferFish.field_205724_bA);
         return !var1.isEmpty();
      }
   }
}
