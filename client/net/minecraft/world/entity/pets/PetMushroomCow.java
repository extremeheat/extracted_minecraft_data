package net.minecraft.world.entity.pets;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

public class PetMushroomCow extends AbstractPet {
   private static final EntityDataAccessor<Integer> DATA_TYPE;
   private static final int MUTATE_CHANCE = 1024;
   private static final String TAG_STEW_EFFECTS = "stew_effects";
   @Nullable
   private SuspiciousStewEffects stewEffects;
   @Nullable
   private UUID lastLightningBoltUUID;

   public PetMushroomCow(EntityType<? extends PetMushroomCow> var1, Level var2) {
      super(var1, var2);
   }

   public float getWalkTargetValue(BlockPos var1, LevelReader var2) {
      return var2.getBlockState(var1.below()).is(Blocks.MYCELIUM) ? 10.0F : var2.getPathfindingCostFromLightLevels(var1);
   }

   public void thunderHit(ServerLevel var1, LightningBolt var2) {
      UUID var3 = var2.getUUID();
      if (!var3.equals(this.lastLightningBoltUUID)) {
         this.setVariant(this.getVariant() == MushroomCow.Variant.RED ? MushroomCow.Variant.BROWN : MushroomCow.Variant.RED);
         this.lastLightningBoltUUID = var3;
         this.playSound(SoundEvents.MOOSHROOM_CONVERT, 2.0F, 1.0F);
      }

   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_TYPE, MushroomCow.Variant.DEFAULT.id());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setVariant((MushroomCow.Variant)var1.read("Type", MushroomCow.Variant.CODEC).orElse(MushroomCow.Variant.DEFAULT));
      this.stewEffects = (SuspiciousStewEffects)var1.read("stew_effects", SuspiciousStewEffects.CODEC).orElse((Object)null);
   }

   private Optional<SuspiciousStewEffects> getEffectsFromItemStack(ItemStack var1) {
      SuspiciousEffectHolder var2 = SuspiciousEffectHolder.tryGet(var1.getItem());
      return var2 != null ? Optional.of(var2.getSuspiciousEffects()) : Optional.empty();
   }

   private void setVariant(MushroomCow.Variant var1) {
      this.entityData.set(DATA_TYPE, var1.id());
   }

   public MushroomCow.Variant getVariant() {
      return MushroomCow.Variant.byId((Integer)this.entityData.get(DATA_TYPE));
   }

   @Nullable
   public <T> T get(DataComponentType<? extends T> var1) {
      return (T)(var1 == DataComponents.MOOSHROOM_VARIANT ? castComponentValue(var1, this.getVariant()) : super.get(var1));
   }

   protected void applyImplicitComponents(DataComponentGetter var1) {
      this.applyImplicitComponentIfPresent(var1, DataComponents.MOOSHROOM_VARIANT);
      super.applyImplicitComponents(var1);
   }

   static {
      DATA_TYPE = SynchedEntityData.<Integer>defineId(PetMushroomCow.class, EntityDataSerializers.INT);
   }
}
