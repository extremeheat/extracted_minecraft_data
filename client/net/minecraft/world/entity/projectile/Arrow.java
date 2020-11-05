package net.minecraft.world.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class Arrow extends AbstractArrow {
   private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR;
   private Potion potion;
   private final Set<MobEffectInstance> effects;
   private boolean fixedColor;

   public Arrow(EntityType<? extends Arrow> var1, Level var2) {
      super(var1, var2);
      this.potion = Potions.EMPTY;
      this.effects = Sets.newHashSet();
   }

   public Arrow(Level var1, double var2, double var4, double var6) {
      super(EntityType.ARROW, var2, var4, var6, var1);
      this.potion = Potions.EMPTY;
      this.effects = Sets.newHashSet();
   }

   public Arrow(Level var1, LivingEntity var2) {
      super(EntityType.ARROW, var2, var1);
      this.potion = Potions.EMPTY;
      this.effects = Sets.newHashSet();
   }

   public void setEffectsFromItem(ItemStack var1) {
      if (var1.is(Items.TIPPED_ARROW)) {
         this.potion = PotionUtils.getPotion(var1);
         List var2 = PotionUtils.getCustomEffects(var1);
         if (!var2.isEmpty()) {
            Iterator var3 = var2.iterator();

            while(var3.hasNext()) {
               MobEffectInstance var4 = (MobEffectInstance)var3.next();
               this.effects.add(new MobEffectInstance(var4));
            }
         }

         int var5 = getCustomColor(var1);
         if (var5 == -1) {
            this.updateColor();
         } else {
            this.setFixedColor(var5);
         }
      } else if (var1.is(Items.ARROW)) {
         this.potion = Potions.EMPTY;
         this.effects.clear();
         this.entityData.set(ID_EFFECT_COLOR, -1);
      }

   }

   public static int getCustomColor(ItemStack var0) {
      CompoundTag var1 = var0.getTag();
      return var1 != null && var1.contains("CustomPotionColor", 99) ? var1.getInt("CustomPotionColor") : -1;
   }

   private void updateColor() {
      this.fixedColor = false;
      if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
         this.entityData.set(ID_EFFECT_COLOR, -1);
      } else {
         this.entityData.set(ID_EFFECT_COLOR, PotionUtils.getColor((Collection)PotionUtils.getAllEffects(this.potion, this.effects)));
      }

   }

   public void addEffect(MobEffectInstance var1) {
      this.effects.add(var1);
      this.getEntityData().set(ID_EFFECT_COLOR, PotionUtils.getColor((Collection)PotionUtils.getAllEffects(this.potion, this.effects)));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_EFFECT_COLOR, -1);
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         if (this.inGround) {
            if (this.inGroundTime % 5 == 0) {
               this.makeParticle(1);
            }
         } else {
            this.makeParticle(2);
         }
      } else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
         this.level.broadcastEntityEvent(this, (byte)0);
         this.potion = Potions.EMPTY;
         this.effects.clear();
         this.entityData.set(ID_EFFECT_COLOR, -1);
      }

   }

   private void makeParticle(int var1) {
      int var2 = this.getColor();
      if (var2 != -1 && var1 > 0) {
         double var3 = (double)(var2 >> 16 & 255) / 255.0D;
         double var5 = (double)(var2 >> 8 & 255) / 255.0D;
         double var7 = (double)(var2 >> 0 & 255) / 255.0D;

         for(int var9 = 0; var9 < var1; ++var9) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), var3, var5, var7);
         }

      }
   }

   public int getColor() {
      return (Integer)this.entityData.get(ID_EFFECT_COLOR);
   }

   private void setFixedColor(int var1) {
      this.fixedColor = true;
      this.entityData.set(ID_EFFECT_COLOR, var1);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.potion != Potions.EMPTY && this.potion != null) {
         var1.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if (this.fixedColor) {
         var1.putInt("Color", this.getColor());
      }

      if (!this.effects.isEmpty()) {
         ListTag var2 = new ListTag();
         Iterator var3 = this.effects.iterator();

         while(var3.hasNext()) {
            MobEffectInstance var4 = (MobEffectInstance)var3.next();
            var2.add(var4.save(new CompoundTag()));
         }

         var1.put("CustomPotionEffects", var2);
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Potion", 8)) {
         this.potion = PotionUtils.getPotion(var1);
      }

      Iterator var2 = PotionUtils.getCustomEffects(var1).iterator();

      while(var2.hasNext()) {
         MobEffectInstance var3 = (MobEffectInstance)var2.next();
         this.addEffect(var3);
      }

      if (var1.contains("Color", 99)) {
         this.setFixedColor(var1.getInt("Color"));
      } else {
         this.updateColor();
      }

   }

   protected void doPostHurtEffects(LivingEntity var1) {
      super.doPostHurtEffects(var1);
      Iterator var2 = this.potion.getEffects().iterator();

      MobEffectInstance var3;
      while(var2.hasNext()) {
         var3 = (MobEffectInstance)var2.next();
         var1.addEffect(new MobEffectInstance(var3.getEffect(), Math.max(var3.getDuration() / 8, 1), var3.getAmplifier(), var3.isAmbient(), var3.isVisible()));
      }

      if (!this.effects.isEmpty()) {
         var2 = this.effects.iterator();

         while(var2.hasNext()) {
            var3 = (MobEffectInstance)var2.next();
            var1.addEffect(var3);
         }
      }

   }

   protected ItemStack getPickupItem() {
      if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
         return new ItemStack(Items.ARROW);
      } else {
         ItemStack var1 = new ItemStack(Items.TIPPED_ARROW);
         PotionUtils.setPotion(var1, this.potion);
         PotionUtils.setCustomEffects(var1, this.effects);
         if (this.fixedColor) {
            var1.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
         }

         return var1;
      }
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 0) {
         int var2 = this.getColor();
         if (var2 != -1) {
            double var3 = (double)(var2 >> 16 & 255) / 255.0D;
            double var5 = (double)(var2 >> 8 & 255) / 255.0D;
            double var7 = (double)(var2 >> 0 & 255) / 255.0D;

            for(int var9 = 0; var9 < 20; ++var9) {
               this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), var3, var5, var7);
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
