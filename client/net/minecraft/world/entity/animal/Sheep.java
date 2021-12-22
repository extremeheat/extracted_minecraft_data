package net.minecraft.world.entity.animal;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class Sheep extends Animal implements Shearable {
   private static final int EAT_ANIMATION_TICKS = 40;
   private static final EntityDataAccessor<Byte> DATA_WOOL_ID;
   private static final Map<DyeColor, ItemLike> ITEM_BY_DYE;
   private static final Map<DyeColor, float[]> COLORARRAY_BY_COLOR;
   private int eatAnimationTick;
   private EatBlockGoal eatBlockGoal;

   private static float[] createSheepColor(DyeColor var0) {
      if (var0 == DyeColor.WHITE) {
         return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
      } else {
         float[] var1 = var0.getTextureDiffuseColors();
         float var2 = 0.75F;
         return new float[]{var1[0] * 0.75F, var1[1] * 0.75F, var1[2] * 0.75F};
      }
   }

   public static float[] getColorArray(DyeColor var0) {
      return (float[])COLORARRAY_BY_COLOR.get(var0);
   }

   public Sheep(EntityType<? extends Sheep> var1, Level var2) {
      super(var1, var2);
   }

   protected void registerGoals() {
      this.eatBlockGoal = new EatBlockGoal(this);
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.method_110(Items.WHEAT), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(5, this.eatBlockGoal);
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
   }

   protected void customServerAiStep() {
      this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
      super.customServerAiStep();
   }

   public void aiStep() {
      if (this.level.isClientSide) {
         this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      }

      super.aiStep();
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_WOOL_ID, (byte)0);
   }

   public ResourceLocation getDefaultLootTable() {
      if (this.isSheared()) {
         return this.getType().getDefaultLootTable();
      } else {
         switch(this.getColor()) {
         case WHITE:
         default:
            return BuiltInLootTables.SHEEP_WHITE;
         case ORANGE:
            return BuiltInLootTables.SHEEP_ORANGE;
         case MAGENTA:
            return BuiltInLootTables.SHEEP_MAGENTA;
         case LIGHT_BLUE:
            return BuiltInLootTables.SHEEP_LIGHT_BLUE;
         case YELLOW:
            return BuiltInLootTables.SHEEP_YELLOW;
         case LIME:
            return BuiltInLootTables.SHEEP_LIME;
         case PINK:
            return BuiltInLootTables.SHEEP_PINK;
         case GRAY:
            return BuiltInLootTables.SHEEP_GRAY;
         case LIGHT_GRAY:
            return BuiltInLootTables.SHEEP_LIGHT_GRAY;
         case CYAN:
            return BuiltInLootTables.SHEEP_CYAN;
         case PURPLE:
            return BuiltInLootTables.SHEEP_PURPLE;
         case BLUE:
            return BuiltInLootTables.SHEEP_BLUE;
         case BROWN:
            return BuiltInLootTables.SHEEP_BROWN;
         case GREEN:
            return BuiltInLootTables.SHEEP_GREEN;
         case RED:
            return BuiltInLootTables.SHEEP_RED;
         case BLACK:
            return BuiltInLootTables.SHEEP_BLACK;
         }
      }
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
      if (var3.method_87(Items.SHEARS)) {
         if (!this.level.isClientSide && this.readyForShearing()) {
            this.shear(SoundSource.PLAYERS);
            this.gameEvent(GameEvent.SHEAR, var1);
            var3.hurtAndBreak(1, var1, (var1x) -> {
               var1x.broadcastBreakEvent(var2);
            });
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.CONSUME;
         }
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   public void shear(SoundSource var1) {
      this.level.playSound((Player)null, (Entity)this, SoundEvents.SHEEP_SHEAR, var1, 1.0F, 1.0F);
      this.setSheared(true);
      int var2 = 1 + this.random.nextInt(3);

      for(int var3 = 0; var3 < var2; ++var3) {
         ItemEntity var4 = this.spawnAtLocation((ItemLike)ITEM_BY_DYE.get(this.getColor()), 1);
         if (var4 != null) {
            var4.setDeltaMovement(var4.getDeltaMovement().add((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(this.random.nextFloat() * 0.05F), (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)));
         }
      }

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

   public static DyeColor getRandomSheepColor(Random var0) {
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

   public Sheep getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Sheep var3 = (Sheep)var2;
      Sheep var4 = (Sheep)EntityType.SHEEP.create(var1);
      var4.setColor(this.getOffspringColor(this, var3));
      return var4;
   }

   public void ate() {
      this.setSheared(false);
      if (this.isBaby()) {
         this.ageUp(60);
      }

   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      this.setColor(getRandomSheepColor(var1.getRandom()));
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   private DyeColor getOffspringColor(Animal var1, Animal var2) {
      DyeColor var3 = ((Sheep)var1).getColor();
      DyeColor var4 = ((Sheep)var2).getColor();
      CraftingContainer var5 = makeContainer(var3, var4);
      Optional var10000 = this.level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, var5, this.level).map((var1x) -> {
         return var1x.assemble(var5);
      }).map(ItemStack::getItem);
      Objects.requireNonNull(DyeItem.class);
      var10000 = var10000.filter(DyeItem.class::isInstance);
      Objects.requireNonNull(DyeItem.class);
      return (DyeColor)var10000.map(DyeItem.class::cast).map(DyeItem::getDyeColor).orElseGet(() -> {
         return this.level.random.nextBoolean() ? var3 : var4;
      });
   }

   private static CraftingContainer makeContainer(DyeColor var0, DyeColor var1) {
      CraftingContainer var2 = new CraftingContainer(new AbstractContainerMenu((MenuType)null, -1) {
         public boolean stillValid(Player var1) {
            return false;
         }
      }, 2, 1);
      var2.setItem(0, new ItemStack(DyeItem.byColor(var0)));
      var2.setItem(1, new ItemStack(DyeItem.byColor(var1)));
      return var2;
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.95F * var2.height;
   }

   // $FF: synthetic method
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return this.getBreedOffspring(var1, var2);
   }

   static {
      DATA_WOOL_ID = SynchedEntityData.defineId(Sheep.class, EntityDataSerializers.BYTE);
      ITEM_BY_DYE = (Map)Util.make(Maps.newEnumMap(DyeColor.class), (var0) -> {
         var0.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
         var0.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
         var0.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
         var0.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
         var0.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
         var0.put(DyeColor.LIME, Blocks.LIME_WOOL);
         var0.put(DyeColor.PINK, Blocks.PINK_WOOL);
         var0.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
         var0.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
         var0.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
         var0.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
         var0.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
         var0.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
         var0.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
         var0.put(DyeColor.RED, Blocks.RED_WOOL);
         var0.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
      });
      COLORARRAY_BY_COLOR = Maps.newEnumMap((Map)Arrays.stream(DyeColor.values()).collect(Collectors.toMap((var0) -> {
         return var0;
      }, Sheep::createSheepColor)));
   }
}
