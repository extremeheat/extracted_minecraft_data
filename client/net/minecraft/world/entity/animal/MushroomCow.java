package net.minecraft.world.entity.animal;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public class MushroomCow extends Cow implements Shearable, VariantHolder<MushroomCow.Variant> {
   private static final EntityDataAccessor<String> DATA_TYPE = SynchedEntityData.defineId(MushroomCow.class, EntityDataSerializers.STRING);
   private static final int MUTATE_CHANCE = 1024;
   private static final String TAG_STEW_EFFECTS = "stew_effects";
   @Nullable
   private SuspiciousStewEffects stewEffects;
   @Nullable
   private UUID lastLightningBoltUUID;

   public MushroomCow(EntityType<? extends MushroomCow> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return var2.getBlockState(var1.below()).is(Blocks.MYCELIUM) ? 10.0F : var2.getPathfindingCostFromLightLevels(var1);
   }

   public static boolean checkMushroomSpawnRules(EntityType<MushroomCow> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.MOOSHROOMS_SPAWNABLE_ON) && isBrightEnoughToSpawn(var1, var3);
   }

   @Override
   public void thunderHit(ServerLevel var1, LightningBolt var2) {
      UUID var3 = var2.getUUID();
      if (!var3.equals(this.lastLightningBoltUUID)) {
         this.setVariant(this.getVariant() == MushroomCow.Variant.RED ? MushroomCow.Variant.BROWN : MushroomCow.Variant.RED);
         this.lastLightningBoltUUID = var3;
         this.playSound(SoundEvents.MOOSHROOM_CONVERT, 2.0F, 1.0F);
      }
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_TYPE, MushroomCow.Variant.RED.type);
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(Items.BOWL) && !this.isBaby()) {
         boolean var10 = false;
         ItemStack var9;
         if (this.stewEffects != null) {
            var10 = true;
            var9 = new ItemStack(Items.SUSPICIOUS_STEW);
            var9.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, this.stewEffects);
            this.stewEffects = null;
         } else {
            var9 = new ItemStack(Items.MUSHROOM_STEW);
         }

         ItemStack var6 = ItemUtils.createFilledResult(var3, var1, var9, false);
         var1.setItemInHand(var2, var6);
         SoundEvent var7;
         if (var10) {
            var7 = SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY;
         } else {
            var7 = SoundEvents.MOOSHROOM_MILK;
         }

         this.playSound(var7, 1.0F, 1.0F);
         return InteractionResult.SUCCESS;
      } else if (var3.is(Items.SHEARS) && this.readyForShearing()) {
         this.shear(SoundSource.PLAYERS);
         this.gameEvent(GameEvent.SHEAR, var1);
         if (!this.level().isClientSide) {
            var3.hurtAndBreak(1, var1, getSlotForHand(var2));
         }

         return InteractionResult.SUCCESS;
      } else if (this.getVariant() == MushroomCow.Variant.BROWN && var3.is(ItemTags.SMALL_FLOWERS)) {
         if (this.stewEffects != null) {
            for (int var4 = 0; var4 < 2; var4++) {
               this.level()
                  .addParticle(
                     ParticleTypes.SMOKE,
                     this.getX() + this.random.nextDouble() / 2.0,
                     this.getY(0.5),
                     this.getZ() + this.random.nextDouble() / 2.0,
                     0.0,
                     this.random.nextDouble() / 5.0,
                     0.0
                  );
            }
         } else {
            Optional var8 = this.getEffectsFromItemStack(var3);
            if (var8.isEmpty()) {
               return InteractionResult.PASS;
            }

            var3.consume(1, var1);

            for (int var5 = 0; var5 < 4; var5++) {
               this.level()
                  .addParticle(
                     ParticleTypes.EFFECT,
                     this.getX() + this.random.nextDouble() / 2.0,
                     this.getY(0.5),
                     this.getZ() + this.random.nextDouble() / 2.0,
                     0.0,
                     this.random.nextDouble() / 5.0,
                     0.0
                  );
            }

            this.stewEffects = (SuspiciousStewEffects)var8.get();
            this.playSound(SoundEvents.MOOSHROOM_EAT, 2.0F, 1.0F);
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   @Override
   public void shear(SoundSource var1) {
      this.level().playSound(null, this, SoundEvents.MOOSHROOM_SHEAR, var1, 1.0F, 1.0F);
      if (!this.level().isClientSide()) {
         Cow var2 = EntityType.COW.create(this.level(), EntitySpawnReason.CONVERSION);
         if (var2 != null) {
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(0.5), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            this.discard();
            var2.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            var2.setHealth(this.getHealth());
            var2.yBodyRot = this.yBodyRot;
            if (this.hasCustomName()) {
               var2.setCustomName(this.getCustomName());
               var2.setCustomNameVisible(this.isCustomNameVisible());
            }

            if (this.isPersistenceRequired()) {
               var2.setPersistenceRequired();
            }

            var2.setInvulnerable(this.isInvulnerable());
            this.level().addFreshEntity(var2);
            this.dropFromShearingLootTable(this.getVariant().lootTable(), var1x -> {
               for (int var2x = 0; var2x < var1x.getCount(); var2x++) {
                  this.level().addFreshEntity(new ItemEntity(this.level(), this.getX(), this.getY(1.0), this.getZ(), var1x.copyWithCount(1)));
               }
            });
         }
      }
   }

   @Override
   public boolean readyForShearing() {
      return this.isAlive() && !this.isBaby();
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putString("Type", this.getVariant().getSerializedName());
      if (this.stewEffects != null) {
         SuspiciousStewEffects.CODEC.encodeStart(NbtOps.INSTANCE, this.stewEffects).ifSuccess(var1x -> var1.put("stew_effects", var1x));
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setVariant(MushroomCow.Variant.byName(var1.getString("Type")));
      if (var1.contains("stew_effects", 9)) {
         SuspiciousStewEffects.CODEC.parse(NbtOps.INSTANCE, var1.get("stew_effects")).ifSuccess(var1x -> this.stewEffects = var1x);
      }
   }

   private Optional<SuspiciousStewEffects> getEffectsFromItemStack(ItemStack var1) {
      SuspiciousEffectHolder var2 = SuspiciousEffectHolder.tryGet(var1.getItem());
      return var2 != null ? Optional.of(var2.getSuspiciousEffects()) : Optional.empty();
   }

   public void setVariant(MushroomCow.Variant var1) {
      this.entityData.set(DATA_TYPE, var1.type);
   }

   public MushroomCow.Variant getVariant() {
      return MushroomCow.Variant.byName(this.entityData.get(DATA_TYPE));
   }

   @Nullable
   public MushroomCow getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      MushroomCow var3 = EntityType.MOOSHROOM.create(var1, EntitySpawnReason.BREEDING);
      if (var3 != null) {
         var3.setVariant(this.getOffspringVariant((MushroomCow)var2));
      }

      return var3;
   }

   private MushroomCow.Variant getOffspringVariant(MushroomCow var1) {
      MushroomCow.Variant var2 = this.getVariant();
      MushroomCow.Variant var3 = var1.getVariant();
      MushroomCow.Variant var4;
      if (var2 == var3 && this.random.nextInt(1024) == 0) {
         var4 = var2 == MushroomCow.Variant.BROWN ? MushroomCow.Variant.RED : MushroomCow.Variant.BROWN;
      } else {
         var4 = this.random.nextBoolean() ? var2 : var3;
      }

      return var4;
   }

   public static enum Variant implements StringRepresentable {
      RED("red", Blocks.RED_MUSHROOM.defaultBlockState(), BuiltInLootTables.SHEAR_RED_MOOSHROOM),
      BROWN("brown", Blocks.BROWN_MUSHROOM.defaultBlockState(), BuiltInLootTables.SHEAR_BROWN_MOOSHROOM);

      public static final StringRepresentable.EnumCodec<MushroomCow.Variant> CODEC = StringRepresentable.fromEnum(MushroomCow.Variant::values);
      final String type;
      private final BlockState blockState;
      private final ResourceKey<LootTable> lootTable;

      private Variant(final String nullxx, final BlockState nullxxx, final ResourceKey<LootTable> nullxxxx) {
         this.type = nullxx;
         this.blockState = nullxxx;
         this.lootTable = nullxxxx;
      }

      public BlockState getBlockState() {
         return this.blockState;
      }

      @Override
      public String getSerializedName() {
         return this.type;
      }

      public ResourceKey<LootTable> lootTable() {
         return this.lootTable;
      }

      static MushroomCow.Variant byName(String var0) {
         return CODEC.byName(var0, RED);
      }
   }
}
