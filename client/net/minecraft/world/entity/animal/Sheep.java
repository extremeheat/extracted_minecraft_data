package net.minecraft.world.entity.animal;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class Sheep extends Animal implements Shearable {
   private static final int EAT_ANIMATION_TICKS = 40;
   private static final EntityDataAccessor<Byte> DATA_WOOL_ID;
   private static final Map<DyeColor, Integer> COLOR_BY_DYE;
   private int eatAnimationTick;
   private EatBlockGoal eatBlockGoal;

   private static int createSheepColor(DyeColor var0) {
      if (var0 == DyeColor.WHITE) {
         return -1644826;
      } else {
         int var1 = var0.getTextureDiffuseColor();
         float var2 = 0.75F;
         return ARGB.color(255, Mth.floor((float)ARGB.red(var1) * 0.75F), Mth.floor((float)ARGB.green(var1) * 0.75F), Mth.floor((float)ARGB.blue(var1) * 0.75F));
      }
   }

   public static int getColor(DyeColor var0) {
      return (Integer)COLOR_BY_DYE.get(var0);
   }

   public Sheep(EntityType<? extends Sheep> var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      this.eatBlockGoal = new EatBlockGoal(this);
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, (var0) -> var0.is(ItemTags.SHEEP_FOOD), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(5, this.eatBlockGoal);
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(ItemTags.SHEEP_FOOD);
   }

   protected void customServerAiStep(ServerLevel var1) {
      this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
      super.customServerAiStep(var1);
   }

   public void aiStep() {
      if (this.level().isClientSide) {
         this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      }

      super.aiStep();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_WOOL_ID, (byte)0);
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 10) {
         this.eatAnimationTick = 40;
      } else {
         super.handleEntityEvent(var1);
      }

   }

   public float getHeadEatPositionScale(float var1) {
      if (this.eatAnimationTick <= 0) {
         return 0.0F;
      } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
         return 1.0F;
      } else {
         return this.eatAnimationTick < 4 ? ((float)this.eatAnimationTick - var1) / 4.0F : -((float)(this.eatAnimationTick - 40) - var1) / 4.0F;
      }
   }

   public float getHeadEatAngleScale(float var1) {
      if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
         float var2 = ((float)(this.eatAnimationTick - 4) - var1) / 32.0F;
         return 0.62831855F + 0.21991149F * Mth.sin(var2 * 28.7F);
      } else {
         return this.eatAnimationTick > 0 ? 0.62831855F : this.getXRot() * 0.017453292F;
      }
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(Items.SHEARS)) {
         Level var5 = this.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel var4 = (ServerLevel)var5;
            if (this.readyForShearing()) {
               this.shear(var4, SoundSource.PLAYERS, var3);
               this.gameEvent(GameEvent.SHEAR, var1);
               var3.hurtAndBreak(1, var1, getSlotForHand(var2));
               return InteractionResult.SUCCESS_SERVER;
            }
         }

         return InteractionResult.CONSUME;
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   public void shear(ServerLevel var1, SoundSource var2, ItemStack var3) {
      var1.playSound((Player)null, this, SoundEvents.SHEEP_SHEAR, var2, 1.0F, 1.0F);
      this.dropFromShearingLootTable(var1, BuiltInLootTables.SHEAR_SHEEP, var3, (var1x, var2x) -> {
         for(int var3 = 0; var3 < var2x.getCount(); ++var3) {
            ItemEntity var4 = this.spawnAtLocation(var1x, var2x.copyWithCount(1), 1.0F);
            if (var4 != null) {
               var4.setDeltaMovement(var4.getDeltaMovement().add((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(this.random.nextFloat() * 0.05F), (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)));
            }
         }

      });
      this.setSheared(true);
   }

   public boolean readyForShearing() {
      return this.isAlive() && !this.isSheared() && !this.isBaby();
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("Sheared", this.isSheared());
      var1.putByte("Color", (byte)this.getColor().getId());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setSheared(var1.getBoolean("Sheared"));
      this.setColor(DyeColor.byId(var1.getByte("Color")));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SHEEP_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.SHEEP_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SHEEP_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
   }

   public DyeColor getColor() {
      return DyeColor.byId((Byte)this.entityData.get(DATA_WOOL_ID) & 15);
   }

   public void setColor(DyeColor var1) {
      byte var2 = (Byte)this.entityData.get(DATA_WOOL_ID);
      this.entityData.set(DATA_WOOL_ID, (byte)(var2 & 240 | var1.getId() & 15));
   }

   public boolean isSheared() {
      return ((Byte)this.entityData.get(DATA_WOOL_ID) & 16) != 0;
   }

   public void setSheared(boolean var1) {
      byte var2 = (Byte)this.entityData.get(DATA_WOOL_ID);
      if (var1) {
         this.entityData.set(DATA_WOOL_ID, (byte)(var2 | 16));
      } else {
         this.entityData.set(DATA_WOOL_ID, (byte)(var2 & -17));
      }

   }

   public static DyeColor getRandomSheepColor(RandomSource var0) {
      int var1 = var0.nextInt(100);
      if (var1 < 5) {
         return DyeColor.BLACK;
      } else if (var1 < 10) {
         return DyeColor.GRAY;
      } else if (var1 < 15) {
         return DyeColor.LIGHT_GRAY;
      } else if (var1 < 18) {
         return DyeColor.BROWN;
      } else {
         return var0.nextInt(500) == 0 ? DyeColor.PINK : DyeColor.WHITE;
      }
   }

   @Nullable
   public Sheep getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Sheep var3 = EntityType.SHEEP.create(var1, EntitySpawnReason.BREEDING);
      if (var3 != null) {
         DyeColor var4 = this.getColor();
         DyeColor var5 = ((Sheep)var2).getColor();
         var3.setColor(DyeColor.getMixedColor(var1, var4, var5));
      }

      return var3;
   }

   public void ate() {
      super.ate();
      this.setSheared(false);
      if (this.isBaby()) {
         this.ageUp(60);
      }

   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      this.setColor(getRandomSheepColor(var1.getRandom()));
      return super.finalizeSpawn(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   @Nullable
   public AgeableMob getBreedOffspring(final ServerLevel var1, final AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      DATA_WOOL_ID = SynchedEntityData.<Byte>defineId(Sheep.class, EntityDataSerializers.BYTE);
      COLOR_BY_DYE = Maps.newEnumMap((Map)Arrays.stream(DyeColor.values()).collect(Collectors.toMap((var0) -> var0, Sheep::createSheepColor)));
   }
}
