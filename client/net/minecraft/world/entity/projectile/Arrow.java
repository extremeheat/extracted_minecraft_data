package net.minecraft.world.entity.projectile;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class Arrow extends AbstractArrow {
   private static final int EXPOSED_POTION_DECAY_TIME = 600;
   private static final int NO_EFFECT_COLOR = -1;
   private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId(Arrow.class, EntityDataSerializers.INT);
   private static final byte EVENT_POTION_PUFF = 0;
   private Potion potion = Potions.EMPTY;
   private final Set<MobEffectInstance> effects = Sets.newHashSet();
   private boolean fixedColor;

   public Arrow(EntityType<? extends Arrow> var1, Level var2) {
      super(var1, var2);
   }

   public Arrow(Level var1, double var2, double var4, double var6) {
      super(EntityType.ARROW, var2, var4, var6, var1);
   }

   public Arrow(Level var1, LivingEntity var2) {
      super(EntityType.ARROW, var2, var1);
   }

   public void setEffectsFromItem(ItemStack var1) {
      if (var1.is(Items.TIPPED_ARROW)) {
         this.potion = PotionUtils.getPotion(var1);
         List var2 = PotionUtils.getCustomEffects(var1);
         if (!var2.isEmpty()) {
            for(MobEffectInstance var4 : var2) {
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
         this.entityData.set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
      }
   }

   public void addEffect(MobEffectInstance var1) {
      this.effects.add(var1);
      this.getEntityData().set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_EFFECT_COLOR, -1);
   }

   @Override
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
         double var3 = (double)(var2 >> 16 & 0xFF) / 255.0;
         double var5 = (double)(var2 >> 8 & 0xFF) / 255.0;
         double var7 = (double)(var2 >> 0 & 0xFF) / 255.0;

         for(int var9 = 0; var9 < var1; ++var9) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), var3, var5, var7);
         }
      }
   }

   public int getColor() {
      return this.entityData.get(ID_EFFECT_COLOR);
   }

   private void setFixedColor(int var1) {
      this.fixedColor = true;
      this.entityData.set(ID_EFFECT_COLOR, var1);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.potion != Potions.EMPTY) {
         var1.putString("Potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
      }

      if (this.fixedColor) {
         var1.putInt("Color", this.getColor());
      }

      if (!this.effects.isEmpty()) {
         ListTag var2 = new ListTag();

         for(MobEffectInstance var4 : this.effects) {
            var2.add(var4.save(new CompoundTag()));
         }

         var1.put("CustomPotionEffects", var2);
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Potion", 8)) {
         this.potion = PotionUtils.getPotion(var1);
      }

      for(MobEffectInstance var3 : PotionUtils.getCustomEffects(var1)) {
         this.addEffect(var3);
      }

      if (var1.contains("Color", 99)) {
         this.setFixedColor(var1.getInt("Color"));
      } else {
         this.updateColor();
      }
   }

   @Override
   protected void doPostHurtEffects(LivingEntity var1) {
      super.doPostHurtEffects(var1);
      Entity var2 = this.getEffectSource();

      for(MobEffectInstance var4 : this.potion.getEffects()) {
         var1.addEffect(
            new MobEffectInstance(var4.getEffect(), Math.max(var4.mapDuration(var0 -> var0 / 8), 1), var4.getAmplifier(), var4.isAmbient(), var4.isVisible()),
            var2
         );
      }

      if (!this.effects.isEmpty()) {
         for(MobEffectInstance var6 : this.effects) {
            var1.addEffect(var6, var2);
         }
      }
   }

   @Override
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

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 0) {
         int var2 = this.getColor();
         if (var2 != -1) {
            double var3 = (double)(var2 >> 16 & 0xFF) / 255.0;
            double var5 = (double)(var2 >> 8 & 0xFF) / 255.0;
            double var7 = (double)(var2 >> 0 & 0xFF) / 255.0;

            for(int var9 = 0; var9 < 20; ++var9) {
               this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), var3, var5, var7);
            }
         }
      } else {
         super.handleEntityEvent(var1);
      }
   }
}
