package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

public class MushroomCow extends Cow {
   private static final EntityDataAccessor<String> DATA_TYPE;
   private MobEffect effect;
   private int effectDuration;
   private UUID lastLightningBoltUUID;

   public MushroomCow(EntityType<? extends MushroomCow> var1, Level var2) {
      super(var1, var2);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return var2.getBlockState(var1.below()).getBlock() == Blocks.MYCELIUM ? 10.0F : var2.getBrightness(var1) - 0.5F;
   }

   public static boolean checkMushroomSpawnRules(EntityType<MushroomCow> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      return var1.getBlockState(var3.below()).getBlock() == Blocks.MYCELIUM && var1.getRawBrightness(var3, 0) > 8;
   }

   public void thunderHit(LightningBolt var1) {
      UUID var2 = var1.getUUID();
      if (!var2.equals(this.lastLightningBoltUUID)) {
         this.setMushroomType(this.getMushroomType() == MushroomCow.MushroomType.RED ? MushroomCow.MushroomType.BROWN : MushroomCow.MushroomType.RED);
         this.lastLightningBoltUUID = var2;
         this.playSound(SoundEvents.MOOSHROOM_CONVERT, 2.0F, 1.0F);
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TYPE, MushroomCow.MushroomType.RED.type);
   }

   public boolean mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.getItem() == Items.BOWL && this.getAge() >= 0 && !var1.abilities.instabuild) {
         var3.shrink(1);
         boolean var10 = false;
         ItemStack var9;
         if (this.effect != null) {
            var10 = true;
            var9 = new ItemStack(Items.SUSPICIOUS_STEW);
            SuspiciousStewItem.saveMobEffect(var9, this.effect, this.effectDuration);
            this.effect = null;
            this.effectDuration = 0;
         } else {
            var9 = new ItemStack(Items.MUSHROOM_STEW);
         }

         if (var3.isEmpty()) {
            var1.setItemInHand(var2, var9);
         } else if (!var1.inventory.add(var9)) {
            var1.drop(var9, false);
         }

         SoundEvent var6;
         if (var10) {
            var6 = SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY;
         } else {
            var6 = SoundEvents.MOOSHROOM_MILK;
         }

         this.playSound(var6, 1.0F, 1.0F);
         return true;
      } else {
         int var5;
         if (var3.getItem() == Items.SHEARS && this.getAge() >= 0) {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y + (double)(this.getBbHeight() / 2.0F), this.z, 0.0D, 0.0D, 0.0D);
            if (!this.level.isClientSide) {
               this.remove();
               Cow var8 = (Cow)EntityType.COW.create(this.level);
               var8.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
               var8.setHealth(this.getHealth());
               var8.yBodyRot = this.yBodyRot;
               if (this.hasCustomName()) {
                  var8.setCustomName(this.getCustomName());
               }

               this.level.addFreshEntity(var8);

               for(var5 = 0; var5 < 5; ++var5) {
                  this.level.addFreshEntity(new ItemEntity(this.level, this.x, this.y + (double)this.getBbHeight(), this.z, new ItemStack(this.getMushroomType().blockState.getBlock())));
               }

               var3.hurtAndBreak(1, var1, (var1x) -> {
                  var1x.broadcastBreakEvent(var2);
               });
               this.playSound(SoundEvents.MOOSHROOM_SHEAR, 1.0F, 1.0F);
            }

            return true;
         } else {
            if (this.getMushroomType() == MushroomCow.MushroomType.BROWN && var3.getItem().is(ItemTags.SMALL_FLOWERS)) {
               if (this.effect != null) {
                  for(int var4 = 0; var4 < 2; ++var4) {
                     this.level.addParticle(ParticleTypes.SMOKE, this.x + (double)(this.random.nextFloat() / 2.0F), this.y + (double)(this.getBbHeight() / 2.0F), this.z + (double)(this.random.nextFloat() / 2.0F), 0.0D, (double)(this.random.nextFloat() / 5.0F), 0.0D);
                  }
               } else {
                  Pair var7 = this.getEffectFromItemStack(var3);
                  if (!var1.abilities.instabuild) {
                     var3.shrink(1);
                  }

                  for(var5 = 0; var5 < 4; ++var5) {
                     this.level.addParticle(ParticleTypes.EFFECT, this.x + (double)(this.random.nextFloat() / 2.0F), this.y + (double)(this.getBbHeight() / 2.0F), this.z + (double)(this.random.nextFloat() / 2.0F), 0.0D, (double)(this.random.nextFloat() / 5.0F), 0.0D);
                  }

                  this.effect = (MobEffect)var7.getLeft();
                  this.effectDuration = (Integer)var7.getRight();
                  this.playSound(SoundEvents.MOOSHROOM_EAT, 2.0F, 1.0F);
               }
            }

            return super.mobInteract(var1, var2);
         }
      }
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putString("Type", this.getMushroomType().type);
      if (this.effect != null) {
         var1.putByte("EffectId", (byte)MobEffect.getId(this.effect));
         var1.putInt("EffectDuration", this.effectDuration);
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setMushroomType(MushroomCow.MushroomType.byType(var1.getString("Type")));
      if (var1.contains("EffectId", 1)) {
         this.effect = MobEffect.byId(var1.getByte("EffectId"));
      }

      if (var1.contains("EffectDuration", 3)) {
         this.effectDuration = var1.getInt("EffectDuration");
      }

   }

   private Pair<MobEffect, Integer> getEffectFromItemStack(ItemStack var1) {
      FlowerBlock var2 = (FlowerBlock)((BlockItem)var1.getItem()).getBlock();
      return Pair.of(var2.getSuspiciousStewEffect(), var2.getEffectDuration());
   }

   private void setMushroomType(MushroomCow.MushroomType var1) {
      this.entityData.set(DATA_TYPE, var1.type);
   }

   public MushroomCow.MushroomType getMushroomType() {
      return MushroomCow.MushroomType.byType((String)this.entityData.get(DATA_TYPE));
   }

   public MushroomCow getBreedOffspring(AgableMob var1) {
      MushroomCow var2 = (MushroomCow)EntityType.MOOSHROOM.create(this.level);
      var2.setMushroomType(this.getOffspringType((MushroomCow)var1));
      return var2;
   }

   private MushroomCow.MushroomType getOffspringType(MushroomCow var1) {
      MushroomCow.MushroomType var2 = this.getMushroomType();
      MushroomCow.MushroomType var3 = var1.getMushroomType();
      MushroomCow.MushroomType var4;
      if (var2 == var3 && this.random.nextInt(1024) == 0) {
         var4 = var2 == MushroomCow.MushroomType.BROWN ? MushroomCow.MushroomType.RED : MushroomCow.MushroomType.BROWN;
      } else {
         var4 = this.random.nextBoolean() ? var2 : var3;
      }

      return var4;
   }

   // $FF: synthetic method
   public Cow getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }

   static {
      DATA_TYPE = SynchedEntityData.defineId(MushroomCow.class, EntityDataSerializers.STRING);
   }

   public static enum MushroomType {
      RED("red", Blocks.RED_MUSHROOM.defaultBlockState()),
      BROWN("brown", Blocks.BROWN_MUSHROOM.defaultBlockState());

      private final String type;
      private final BlockState blockState;

      private MushroomType(String var3, BlockState var4) {
         this.type = var3;
         this.blockState = var4;
      }

      public BlockState getBlockState() {
         return this.blockState;
      }

      private static MushroomCow.MushroomType byType(String var0) {
         MushroomCow.MushroomType[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            MushroomCow.MushroomType var4 = var1[var3];
            if (var4.type.equals(var0)) {
               return var4;
            }
         }

         return RED;
      }
   }
}
