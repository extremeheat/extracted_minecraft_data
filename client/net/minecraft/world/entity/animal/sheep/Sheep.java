package net.minecraft.world.entity.animal.sheep;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.jspecify.annotations.Nullable;

public class Sheep extends Animal implements Shearable {
   private static final int EAT_ANIMATION_TICKS = 40;
   private static final EntityDataAccessor<Byte> DATA_WOOL_ID;
   private static final DyeColor DEFAULT_COLOR;
   private static final boolean DEFAULT_SHEARED = false;
   private int eatAnimationTick;
   private EatBlockGoal eatBlockGoal;

   public Sheep(final EntityType<? extends Sheep> type, final Level level) {
      super(type, level);
   }

   protected void registerGoals() {
      this.eatBlockGoal = new EatBlockGoal(this);
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, (i) -> i.is(ItemTags.SHEEP_FOOD), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
      this.goalSelector.addGoal(5, this.eatBlockGoal);
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
   }

   public boolean isFood(final ItemStack itemStack) {
      return itemStack.is(ItemTags.SHEEP_FOOD);
   }

   protected void customServerAiStep(final ServerLevel level) {
      this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
      super.customServerAiStep(level);
   }

   public void aiStep() {
      if (this.level().isClientSide()) {
         this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      }

      super.aiStep();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Animal.createAnimalAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513);
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_WOOL_ID, (byte)0);
   }

   public void handleEntityEvent(final byte id) {
      if (id == 10) {
         this.eatAnimationTick = 40;
      } else {
         super.handleEntityEvent(id);
      }

   }

   public float getHeadEatPositionScale(final float a) {
      if (this.eatAnimationTick <= 0) {
         return 0.0F;
      } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
         return 1.0F;
      } else {
         return this.eatAnimationTick < 4 ? ((float)this.eatAnimationTick - a) / 4.0F : -((float)(this.eatAnimationTick - 40) - a) / 4.0F;
      }
   }

   public float getHeadEatAngleScale(final float a) {
      if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
         float scale = ((float)(this.eatAnimationTick - 4) - a) / 32.0F;
         return 0.62831855F + 0.21991149F * Mth.sin((double)(scale * 28.7F));
      } else {
         return this.eatAnimationTick > 0 ? 0.62831855F : this.getXRot(a) * 0.017453292F;
      }
   }

   public InteractionResult mobInteract(final Player player, final InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (itemStack.is(Items.SHEARS)) {
         Level var5 = this.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel level = (ServerLevel)var5;
            if (this.readyForShearing()) {
               this.shear(level, SoundSource.PLAYERS, itemStack);
               this.gameEvent(GameEvent.SHEAR, player);
               itemStack.hurtAndBreak(1, player, (EquipmentSlot)hand.asEquipmentSlot());
               return InteractionResult.SUCCESS_SERVER;
            }
         }

         return InteractionResult.CONSUME;
      } else {
         return super.mobInteract(player, hand);
      }
   }

   public void shear(final ServerLevel level, final SoundSource soundSource, final ItemStack tool) {
      level.playSound((Entity)null, this, SoundEvents.SHEEP_SHEAR, soundSource, 1.0F, 1.0F);
      this.dropFromShearingLootTable(level, BuiltInLootTables.SHEAR_SHEEP, tool, (l, drop) -> {
         for(int i = 0; i < drop.getCount(); ++i) {
            ItemEntity entity = this.spawnAtLocation(l, drop.copyWithCount(1), 1.0F);
            if (entity != null) {
               entity.setDeltaMovement(entity.getDeltaMovement().add((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(this.random.nextFloat() * 0.05F), (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)));
            }
         }

      });
      this.setSheared(true);
   }

   public boolean readyForShearing() {
      return this.isAlive() && !this.isSheared() && !this.isBaby();
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("Sheared", this.isSheared());
      output.store("Color", DyeColor.LEGACY_ID_CODEC, this.getColor());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setSheared(input.getBooleanOr("Sheared", false));
      this.setColor((DyeColor)input.read("Color", DyeColor.LEGACY_ID_CODEC).orElse(DEFAULT_COLOR));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SHEEP_AMBIENT;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.SHEEP_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SHEEP_DEATH;
   }

   protected void playStepSound(final BlockPos pos, final BlockState blockState) {
      this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
   }

   public DyeColor getColor() {
      return DyeColor.byId((Byte)this.entityData.get(DATA_WOOL_ID) & 15);
   }

   public void setColor(final DyeColor color) {
      byte current = (Byte)this.entityData.get(DATA_WOOL_ID);
      this.entityData.set(DATA_WOOL_ID, (byte)(current & 240 | color.getId() & 15));
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      return (T)(type == DataComponents.SHEEP_COLOR ? castComponentValue(type, this.getColor()) : super.get(type));
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.SHEEP_COLOR);
      super.applyImplicitComponents(components);
   }

   protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
      if (type == DataComponents.SHEEP_COLOR) {
         this.setColor((DyeColor)castComponentValue(DataComponents.SHEEP_COLOR, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   public boolean isSheared() {
      return ((Byte)this.entityData.get(DATA_WOOL_ID) & 16) != 0;
   }

   public void setSheared(final boolean value) {
      byte current = (Byte)this.entityData.get(DATA_WOOL_ID);
      if (value) {
         this.entityData.set(DATA_WOOL_ID, (byte)(current | 16));
      } else {
         this.entityData.set(DATA_WOOL_ID, (byte)(current & -17));
      }

   }

   public static DyeColor getRandomSheepColor(final ServerLevelAccessor level, final BlockPos pos) {
      Holder<Biome> biome = level.getBiome(pos);
      return SheepColorSpawnRules.getSheepColor(biome, level.getRandom());
   }

   public @Nullable Sheep getBreedOffspring(final ServerLevel level, final AgeableMob partner) {
      Sheep sheep = EntityType.SHEEP.create(level, EntitySpawnReason.BREEDING);
      if (sheep != null) {
         DyeColor parent1DyeColor = this.getColor();
         DyeColor parent2DyeColor = ((Sheep)partner).getColor();
         sheep.setColor(DyeColor.getMixedColor(level, parent1DyeColor, parent2DyeColor));
      }

      return sheep;
   }

   public void ate() {
      super.ate();
      this.setSheared(false);
      if (this.canAgeUp()) {
         this.ageUp(60);
      }

   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, final @Nullable SpawnGroupData groupData) {
      this.setColor(getRandomSheepColor(level, this.blockPosition()));
      return super.finalizeSpawn(level, difficulty, spawnReason, groupData);
   }

   static {
      DATA_WOOL_ID = SynchedEntityData.<Byte>defineId(Sheep.class, EntityDataSerializers.BYTE);
      DEFAULT_COLOR = DyeColor.WHITE;
   }
}
