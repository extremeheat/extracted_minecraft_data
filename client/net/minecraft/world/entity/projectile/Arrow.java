package net.minecraft.world.entity.projectile;

import java.util.Iterator;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

public class Arrow extends AbstractArrow {
   private static final int EXPOSED_POTION_DECAY_TIME = 600;
   private static final int NO_EFFECT_COLOR = -1;
   private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR;
   private static final byte EVENT_POTION_PUFF = 0;

   public Arrow(EntityType<? extends Arrow> var1, Level var2) {
      super(var1, var2);
   }

   public Arrow(Level var1, double var2, double var4, double var6, ItemStack var8) {
      super(EntityType.ARROW, var2, var4, var6, var1, var8);
      this.updateColor();
   }

   public Arrow(Level var1, LivingEntity var2, ItemStack var3) {
      super(EntityType.ARROW, var2, var1, var3);
      this.updateColor();
   }

   private PotionContents getPotionContents() {
      return (PotionContents)this.getPickupItemStackOrigin().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
   }

   private void setPotionContents(PotionContents var1) {
      this.getPickupItemStackOrigin().set(DataComponents.POTION_CONTENTS, var1);
      this.updateColor();
   }

   protected void setPickupItemStack(ItemStack var1) {
      super.setPickupItemStack(var1);
      this.updateColor();
   }

   private void updateColor() {
      PotionContents var1 = this.getPotionContents();
      this.entityData.set(ID_EFFECT_COLOR, var1.equals(PotionContents.EMPTY) ? -1 : var1.getColorForArrow());
   }

   public void addEffect(MobEffectInstance var1) {
      this.setPotionContents(this.getPotionContents().withEffectAdded(var1));
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(ID_EFFECT_COLOR, -1);
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         if (this.inGround) {
            if (this.inGroundTime % 5 == 0) {
               this.makeParticle(1);
            }
         } else {
            this.makeParticle(2);
         }
      } else if (this.inGround && this.inGroundTime != 0 && !this.getPotionContents().equals(PotionContents.EMPTY) && this.inGroundTime >= 600) {
         this.level().broadcastEntityEvent(this, (byte)0);
         this.setPickupItemStack(new ItemStack(Items.ARROW));
      }

   }

   private void makeParticle(int var1) {
      int var2 = this.getColor();
      if (var2 != -1 && var1 > 0) {
         for(int var3 = 0; var3 < var1; ++var3) {
            this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, var2), this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
         }

      }
   }

   public int getColor() {
      return (Integer)this.entityData.get(ID_EFFECT_COLOR);
   }

   protected void doPostHurtEffects(LivingEntity var1) {
      super.doPostHurtEffects(var1);
      Entity var2 = this.getEffectSource();
      PotionContents var3 = this.getPotionContents();
      Iterator var4;
      MobEffectInstance var5;
      if (var3.potion().isPresent()) {
         var4 = ((Potion)((Holder)var3.potion().get()).value()).getEffects().iterator();

         while(var4.hasNext()) {
            var5 = (MobEffectInstance)var4.next();
            var1.addEffect(new MobEffectInstance(var5.getEffect(), Math.max(var5.mapDuration((var0) -> {
               return var0 / 8;
            }), 1), var5.getAmplifier(), var5.isAmbient(), var5.isVisible()), var2);
         }
      }

      var4 = var3.customEffects().iterator();

      while(var4.hasNext()) {
         var5 = (MobEffectInstance)var4.next();
         var1.addEffect(var5, var2);
      }

   }

   protected ItemStack getDefaultPickupItem() {
      return new ItemStack(Items.ARROW);
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 0) {
         int var2 = this.getColor();
         if (var2 != -1) {
            float var3 = (float)(var2 >> 16 & 255) / 255.0F;
            float var4 = (float)(var2 >> 8 & 255) / 255.0F;
            float var5 = (float)(var2 >> 0 & 255) / 255.0F;

            for(int var6 = 0; var6 < 20; ++var6) {
               this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, var3, var4, var5), this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
         }
      } else {
         super.handleEntityEvent(var1);
      }

   }

   static {
      ID_EFFECT_COLOR = SynchedEntityData.defineId(Arrow.class, EntityDataSerializers.INT);
   }
}
