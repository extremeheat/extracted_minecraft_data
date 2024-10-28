package net.minecraft.world.entity.animal;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LandOnOwnersShoulderGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class Parrot extends ShoulderRidingEntity implements VariantHolder<Variant>, FlyingAnimal {
   private static final EntityDataAccessor<Integer> DATA_VARIANT_ID;
   private static final Predicate<Mob> NOT_PARROT_PREDICATE;
   static final Map<EntityType<?>, SoundEvent> MOB_SOUND_MAP;
   public float flap;
   public float flapSpeed;
   public float oFlapSpeed;
   public float oFlap;
   private float flapping = 1.0F;
   private float nextFlap = 1.0F;
   private boolean partyParrot;
   @Nullable
   private BlockPos jukebox;

   public Parrot(EntityType<? extends Parrot> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new FlyingMoveControl(this, 10, false);
      this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
      this.setPathfindingMalus(PathType.COCOA, -1.0F);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      this.setVariant((Variant)Util.getRandom((Object[])Parrot.Variant.values(), var1.getRandom()));
      if (var4 == null) {
         var4 = new AgeableMob.AgeableMobGroupData(false);
      }

      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
   }

   public boolean isBaby() {
      return false;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new TamableAnimal.TamableAnimalPanicGoal(1.25));
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
      this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0, 5.0F, 1.0F));
      this.goalSelector.addGoal(2, new ParrotWanderGoal(this, 1.0));
      this.goalSelector.addGoal(3, new LandOnOwnersShoulderGoal(this));
      this.goalSelector.addGoal(3, new FollowMobGoal(this, 1.0, 3.0F, 7.0F));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0).add(Attributes.FLYING_SPEED, 0.4000000059604645).add(Attributes.MOVEMENT_SPEED, 0.20000000298023224).add(Attributes.ATTACK_DAMAGE, 3.0);
   }

   protected PathNavigation createNavigation(Level var1) {
      FlyingPathNavigation var2 = new FlyingPathNavigation(this, var1);
      var2.setCanOpenDoors(false);
      var2.setCanFloat(true);
      var2.setCanPassDoors(true);
      return var2;
   }

   public void aiStep() {
      if (this.jukebox == null || !this.jukebox.closerToCenterThan(this.position(), 3.46) || !this.level().getBlockState(this.jukebox).is(Blocks.JUKEBOX)) {
         this.partyParrot = false;
         this.jukebox = null;
      }

      if (this.level().random.nextInt(400) == 0) {
         imitateNearbyMobs(this.level(), this);
      }

      super.aiStep();
      this.calculateFlapping();
   }

   public void setRecordPlayingNearby(BlockPos var1, boolean var2) {
      this.jukebox = var1;
      this.partyParrot = var2;
   }

   public boolean isPartyParrot() {
      return this.partyParrot;
   }

   private void calculateFlapping() {
      this.oFlap = this.flap;
      this.oFlapSpeed = this.flapSpeed;
      this.flapSpeed += (float)(!this.onGround() && !this.isPassenger() ? 4 : -1) * 0.3F;
      this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
      if (!this.onGround() && this.flapping < 1.0F) {
         this.flapping = 1.0F;
      }

      this.flapping *= 0.9F;
      Vec3 var1 = this.getDeltaMovement();
      if (!this.onGround() && var1.y < 0.0) {
         this.setDeltaMovement(var1.multiply(1.0, 0.6, 1.0));
      }

      this.flap += this.flapping * 2.0F;
   }

   public static boolean imitateNearbyMobs(Level var0, Entity var1) {
      if (var1.isAlive() && !var1.isSilent() && var0.random.nextInt(2) == 0) {
         List var2 = var0.getEntitiesOfClass(Mob.class, var1.getBoundingBox().inflate(20.0), NOT_PARROT_PREDICATE);
         if (!var2.isEmpty()) {
            Mob var3 = (Mob)var2.get(var0.random.nextInt(var2.size()));
            if (!var3.isSilent()) {
               SoundEvent var4 = getImitatedSound(var3.getType());
               var0.playSound((Player)null, var1.getX(), var1.getY(), var1.getZ(), (SoundEvent)var4, var1.getSoundSource(), 0.7F, getPitch(var0.random));
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (!this.isTame() && var3.is(ItemTags.PARROT_FOOD)) {
         var3.consume(1, var1);
         if (!this.isSilent()) {
            this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), (SoundEvent)SoundEvents.PARROT_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
         }

         if (!this.level().isClientSide) {
            if (this.random.nextInt(10) == 0) {
               this.tame(var1);
               this.level().broadcastEntityEvent(this, (byte)7);
            } else {
               this.level().broadcastEntityEvent(this, (byte)6);
            }
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else if (!var3.is(ItemTags.PARROT_POISONOUS_FOOD)) {
         if (!this.isFlying() && this.isTame() && this.isOwnedBy(var1)) {
            if (!this.level().isClientSide) {
               this.setOrderedToSit(!this.isOrderedToSit());
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         } else {
            return super.mobInteract(var1, var2);
         }
      } else {
         var3.consume(1, var1);
         this.addEffect(new MobEffectInstance(MobEffects.POISON, 900));
         if (var1.isCreative() || !this.isInvulnerable()) {
            this.hurt(this.damageSources().playerAttack(var1), 3.4028235E38F);
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
      }
   }

   public boolean isFood(ItemStack var1) {
      return false;
   }

   public static boolean checkParrotSpawnRules(EntityType<Parrot> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.PARROTS_SPAWNABLE_ON) && isBrightEnoughToSpawn(var1, var3);
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   public boolean canMate(Animal var1) {
      return false;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      return null;
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return getAmbient(this.level(), this.level().random);
   }

   public static SoundEvent getAmbient(Level var0, RandomSource var1) {
      if (var0.getDifficulty() != Difficulty.PEACEFUL && var1.nextInt(1000) == 0) {
         ArrayList var2 = Lists.newArrayList(MOB_SOUND_MAP.keySet());
         return getImitatedSound((EntityType)var2.get(var1.nextInt(var2.size())));
      } else {
         return SoundEvents.PARROT_AMBIENT;
      }
   }

   private static SoundEvent getImitatedSound(EntityType<?> var0) {
      return (SoundEvent)MOB_SOUND_MAP.getOrDefault(var0, SoundEvents.PARROT_AMBIENT);
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PARROT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PARROT_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.PARROT_STEP, 0.15F, 1.0F);
   }

   protected boolean isFlapping() {
      return this.flyDist > this.nextFlap;
   }

   protected void onFlap() {
      this.playSound(SoundEvents.PARROT_FLY, 0.15F, 1.0F);
      this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
   }

   public float getVoicePitch() {
      return getPitch(this.random);
   }

   public static float getPitch(RandomSource var0) {
      return (var0.nextFloat() - var0.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundSource getSoundSource() {
      return SoundSource.NEUTRAL;
   }

   public boolean isPushable() {
      return true;
   }

   protected void doPush(Entity var1) {
      if (!(var1 instanceof Player)) {
         super.doPush(var1);
      }
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         if (!this.level().isClientSide) {
            this.setOrderedToSit(false);
         }

         return super.hurt(var1, var2);
      }
   }

   public Variant getVariant() {
      return Parrot.Variant.byId((Integer)this.entityData.get(DATA_VARIANT_ID));
   }

   public void setVariant(Variant var1) {
      this.entityData.set(DATA_VARIANT_ID, var1.id);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_VARIANT_ID, 0);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Variant", this.getVariant().id);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setVariant(Parrot.Variant.byId(var1.getInt("Variant")));
   }

   public boolean isFlying() {
      return !this.onGround();
   }

   protected boolean canFlyToOwner() {
      return true;
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.5F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   // $FF: synthetic method
   public Object getVariant() {
      return this.getVariant();
   }

   static {
      DATA_VARIANT_ID = SynchedEntityData.defineId(Parrot.class, EntityDataSerializers.INT);
      NOT_PARROT_PREDICATE = new Predicate<Mob>() {
         public boolean test(@Nullable Mob var1) {
            return var1 != null && Parrot.MOB_SOUND_MAP.containsKey(var1.getType());
         }

         // $FF: synthetic method
         public boolean test(@Nullable final Object var1) {
            return this.test((Mob)var1);
         }
      };
      MOB_SOUND_MAP = (Map)Util.make(Maps.newHashMap(), (var0) -> {
         var0.put(EntityType.BLAZE, SoundEvents.PARROT_IMITATE_BLAZE);
         var0.put(EntityType.BOGGED, SoundEvents.PARROT_IMITATE_BOGGED);
         var0.put(EntityType.BREEZE, SoundEvents.PARROT_IMITATE_BREEZE);
         var0.put(EntityType.CAVE_SPIDER, SoundEvents.PARROT_IMITATE_SPIDER);
         var0.put(EntityType.CREEPER, SoundEvents.PARROT_IMITATE_CREEPER);
         var0.put(EntityType.DROWNED, SoundEvents.PARROT_IMITATE_DROWNED);
         var0.put(EntityType.ELDER_GUARDIAN, SoundEvents.PARROT_IMITATE_ELDER_GUARDIAN);
         var0.put(EntityType.ENDER_DRAGON, SoundEvents.PARROT_IMITATE_ENDER_DRAGON);
         var0.put(EntityType.ENDERMITE, SoundEvents.PARROT_IMITATE_ENDERMITE);
         var0.put(EntityType.EVOKER, SoundEvents.PARROT_IMITATE_EVOKER);
         var0.put(EntityType.GHAST, SoundEvents.PARROT_IMITATE_GHAST);
         var0.put(EntityType.GUARDIAN, SoundEvents.PARROT_IMITATE_GUARDIAN);
         var0.put(EntityType.HOGLIN, SoundEvents.PARROT_IMITATE_HOGLIN);
         var0.put(EntityType.HUSK, SoundEvents.PARROT_IMITATE_HUSK);
         var0.put(EntityType.ILLUSIONER, SoundEvents.PARROT_IMITATE_ILLUSIONER);
         var0.put(EntityType.MAGMA_CUBE, SoundEvents.PARROT_IMITATE_MAGMA_CUBE);
         var0.put(EntityType.PHANTOM, SoundEvents.PARROT_IMITATE_PHANTOM);
         var0.put(EntityType.PIGLIN, SoundEvents.PARROT_IMITATE_PIGLIN);
         var0.put(EntityType.PIGLIN_BRUTE, SoundEvents.PARROT_IMITATE_PIGLIN_BRUTE);
         var0.put(EntityType.PILLAGER, SoundEvents.PARROT_IMITATE_PILLAGER);
         var0.put(EntityType.RAVAGER, SoundEvents.PARROT_IMITATE_RAVAGER);
         var0.put(EntityType.SHULKER, SoundEvents.PARROT_IMITATE_SHULKER);
         var0.put(EntityType.SILVERFISH, SoundEvents.PARROT_IMITATE_SILVERFISH);
         var0.put(EntityType.SKELETON, SoundEvents.PARROT_IMITATE_SKELETON);
         var0.put(EntityType.SLIME, SoundEvents.PARROT_IMITATE_SLIME);
         var0.put(EntityType.SPIDER, SoundEvents.PARROT_IMITATE_SPIDER);
         var0.put(EntityType.STRAY, SoundEvents.PARROT_IMITATE_STRAY);
         var0.put(EntityType.VEX, SoundEvents.PARROT_IMITATE_VEX);
         var0.put(EntityType.VINDICATOR, SoundEvents.PARROT_IMITATE_VINDICATOR);
         var0.put(EntityType.WARDEN, SoundEvents.PARROT_IMITATE_WARDEN);
         var0.put(EntityType.WITCH, SoundEvents.PARROT_IMITATE_WITCH);
         var0.put(EntityType.WITHER, SoundEvents.PARROT_IMITATE_WITHER);
         var0.put(EntityType.WITHER_SKELETON, SoundEvents.PARROT_IMITATE_WITHER_SKELETON);
         var0.put(EntityType.ZOGLIN, SoundEvents.PARROT_IMITATE_ZOGLIN);
         var0.put(EntityType.ZOMBIE, SoundEvents.PARROT_IMITATE_ZOMBIE);
         var0.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.PARROT_IMITATE_ZOMBIE_VILLAGER);
      });
   }

   public static enum Variant implements StringRepresentable {
      RED_BLUE(0, "red_blue"),
      BLUE(1, "blue"),
      GREEN(2, "green"),
      YELLOW_BLUE(3, "yellow_blue"),
      GRAY(4, "gray");

      public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
      private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);
      final int id;
      private final String name;

      private Variant(final int var3, final String var4) {
         this.id = var3;
         this.name = var4;
      }

      public int getId() {
         return this.id;
      }

      public static Variant byId(int var0) {
         return (Variant)BY_ID.apply(var0);
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Variant[] $values() {
         return new Variant[]{RED_BLUE, BLUE, GREEN, YELLOW_BLUE, GRAY};
      }
   }

   static class ParrotWanderGoal extends WaterAvoidingRandomFlyingGoal {
      public ParrotWanderGoal(PathfinderMob var1, double var2) {
         super(var1, var2);
      }

      @Nullable
      protected Vec3 getPosition() {
         Vec3 var1 = null;
         if (this.mob.isInWater()) {
            var1 = LandRandomPos.getPos(this.mob, 15, 15);
         }

         if (this.mob.getRandom().nextFloat() >= this.probability) {
            var1 = this.getTreePos();
         }

         return var1 == null ? super.getPosition() : var1;
      }

      @Nullable
      private Vec3 getTreePos() {
         BlockPos var1 = this.mob.blockPosition();
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
         BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
         Iterable var4 = BlockPos.betweenClosed(Mth.floor(this.mob.getX() - 3.0), Mth.floor(this.mob.getY() - 6.0), Mth.floor(this.mob.getZ() - 3.0), Mth.floor(this.mob.getX() + 3.0), Mth.floor(this.mob.getY() + 6.0), Mth.floor(this.mob.getZ() + 3.0));
         Iterator var5 = var4.iterator();

         BlockPos var6;
         boolean var8;
         do {
            do {
               if (!var5.hasNext()) {
                  return null;
               }

               var6 = (BlockPos)var5.next();
            } while(var1.equals(var6));

            BlockState var7 = this.mob.level().getBlockState(var3.setWithOffset(var6, (Direction)Direction.DOWN));
            var8 = var7.getBlock() instanceof LeavesBlock || var7.is(BlockTags.LOGS);
         } while(!var8 || !this.mob.level().isEmptyBlock(var6) || !this.mob.level().isEmptyBlock(var2.setWithOffset(var6, (Direction)Direction.UP)));

         return Vec3.atBottomCenterOf(var6);
      }
   }
}
