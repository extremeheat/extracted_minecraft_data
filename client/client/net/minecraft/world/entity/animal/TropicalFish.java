package net.minecraft.world.entity.animal;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class TropicalFish extends AbstractSchoolingFish implements VariantHolder<TropicalFish.Pattern> {
   public static final String BUCKET_VARIANT_TAG = "BucketVariantTag";
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(TropicalFish.class, EntityDataSerializers.INT);
   public static final List<TropicalFish.Variant> COMMON_VARIANTS = List.of(
      new TropicalFish.Variant(TropicalFish.Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY),
      new TropicalFish.Variant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY),
      new TropicalFish.Variant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE),
      new TropicalFish.Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY),
      new TropicalFish.Variant(TropicalFish.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY),
      new TropicalFish.Variant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE),
      new TropicalFish.Variant(TropicalFish.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE),
      new TropicalFish.Variant(TropicalFish.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW),
      new TropicalFish.Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED),
      new TropicalFish.Variant(TropicalFish.Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW),
      new TropicalFish.Variant(TropicalFish.Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY),
      new TropicalFish.Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE),
      new TropicalFish.Variant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK),
      new TropicalFish.Variant(TropicalFish.Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE),
      new TropicalFish.Variant(TropicalFish.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE),
      new TropicalFish.Variant(TropicalFish.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED),
      new TropicalFish.Variant(TropicalFish.Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE),
      new TropicalFish.Variant(TropicalFish.Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW),
      new TropicalFish.Variant(TropicalFish.Pattern.KOB, DyeColor.RED, DyeColor.WHITE),
      new TropicalFish.Variant(TropicalFish.Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE),
      new TropicalFish.Variant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW),
      new TropicalFish.Variant(TropicalFish.Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)
   );
   private boolean isSchool = true;

   public TropicalFish(EntityType<? extends TropicalFish> var1, Level var2) {
      super(var1, var2);
   }

   public static String getPredefinedName(int var0) {
      return "entity.minecraft.tropical_fish.predefined." + var0;
   }

   static int packVariant(TropicalFish.Pattern var0, DyeColor var1, DyeColor var2) {
      return var0.getPackedId() & 65535 | (var1.getId() & 0xFF) << 16 | (var2.getId() & 0xFF) << 24;
   }

   public static DyeColor getBaseColor(int var0) {
      return DyeColor.byId(var0 >> 16 & 0xFF);
   }

   public static DyeColor getPatternColor(int var0) {
      return DyeColor.byId(var0 >> 24 & 0xFF);
   }

   public static TropicalFish.Pattern getPattern(int var0) {
      return TropicalFish.Pattern.byId(var0 & 65535);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_ID_TYPE_VARIANT, 0);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Variant", this.getPackedVariant());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setPackedVariant(var1.getInt("Variant"));
   }

   private void setPackedVariant(int var1) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, var1);
   }

   @Override
   public boolean isMaxGroupSizeReached(int var1) {
      return !this.isSchool;
   }

   private int getPackedVariant() {
      return this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public DyeColor getBaseColor() {
      return getBaseColor(this.getPackedVariant());
   }

   public DyeColor getPatternColor() {
      return getPatternColor(this.getPackedVariant());
   }

   public TropicalFish.Pattern getVariant() {
      return getPattern(this.getPackedVariant());
   }

   public void setVariant(TropicalFish.Pattern var1) {
      int var2 = this.getPackedVariant();
      DyeColor var3 = getBaseColor(var2);
      DyeColor var4 = getPatternColor(var2);
      this.setPackedVariant(packVariant(var1, var3, var4));
   }

   @Override
   public void saveToBucketTag(ItemStack var1) {
      super.saveToBucketTag(var1);
      CustomData.update(DataComponents.BUCKET_ENTITY_DATA, var1, var1x -> var1x.putInt("BucketVariantTag", this.getPackedVariant()));
   }

   @Override
   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.TROPICAL_FISH_BUCKET);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.TROPICAL_FISH_AMBIENT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.TROPICAL_FISH_DEATH;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.TROPICAL_FISH_HURT;
   }

   @Override
   protected SoundEvent getFlopSound() {
      return SoundEvents.TROPICAL_FISH_FLOP;
   }

   @Override
   public void loadFromBucketTag(CompoundTag var1) {
      super.loadFromBucketTag(var1);
      if (var1.contains("BucketVariantTag", 3)) {
         this.setPackedVariant(var1.getInt("BucketVariantTag"));
      }
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      var4 = super.finalizeSpawn(var1, var2, var3, var4);
      RandomSource var6 = var1.getRandom();
      TropicalFish.Variant var5;
      if (var4 instanceof TropicalFish.TropicalFishGroupData var7) {
         var5 = var7.variant;
      } else if ((double)var6.nextFloat() < 0.9) {
         var5 = Util.getRandom(COMMON_VARIANTS, var6);
         var4 = new TropicalFish.TropicalFishGroupData(this, var5);
      } else {
         this.isSchool = false;
         TropicalFish.Pattern[] var8 = TropicalFish.Pattern.values();
         DyeColor[] var9 = DyeColor.values();
         TropicalFish.Pattern var10 = Util.getRandom(var8, var6);
         DyeColor var11 = Util.getRandom(var9, var6);
         DyeColor var12 = Util.getRandom(var9, var6);
         var5 = new TropicalFish.Variant(var10, var11, var12);
      }

      this.setPackedVariant(var5.getPackedId());
      return var4;
   }

   public static boolean checkTropicalFishSpawnRules(EntityType<TropicalFish> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getFluidState(var3.below()).is(FluidTags.WATER)
         && var1.getBlockState(var3.above()).is(Blocks.WATER)
         && (
            var1.getBiome(var3).is(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT)
               || WaterAnimal.checkSurfaceWaterAnimalSpawnRules(var0, var1, var2, var3, var4)
         );
   }

   public static enum Base {
      SMALL(0),
      LARGE(1);

      final int id;

      private Base(int var3) {
         this.id = var3;
      }
   }

   public static enum Pattern implements StringRepresentable {
      KOB("kob", TropicalFish.Base.SMALL, 0),
      SUNSTREAK("sunstreak", TropicalFish.Base.SMALL, 1),
      SNOOPER("snooper", TropicalFish.Base.SMALL, 2),
      DASHER("dasher", TropicalFish.Base.SMALL, 3),
      BRINELY("brinely", TropicalFish.Base.SMALL, 4),
      SPOTTY("spotty", TropicalFish.Base.SMALL, 5),
      FLOPPER("flopper", TropicalFish.Base.LARGE, 0),
      STRIPEY("stripey", TropicalFish.Base.LARGE, 1),
      GLITTER("glitter", TropicalFish.Base.LARGE, 2),
      BLOCKFISH("blockfish", TropicalFish.Base.LARGE, 3),
      BETTY("betty", TropicalFish.Base.LARGE, 4),
      CLAYFISH("clayfish", TropicalFish.Base.LARGE, 5);

      public static final Codec<TropicalFish.Pattern> CODEC = StringRepresentable.fromEnum(TropicalFish.Pattern::values);
      private static final IntFunction<TropicalFish.Pattern> BY_ID = ByIdMap.sparse(TropicalFish.Pattern::getPackedId, values(), KOB);
      private final String name;
      private final Component displayName;
      private final TropicalFish.Base base;
      private final int packedId;

      private Pattern(String var3, TropicalFish.Base var4, int var5) {
         this.name = var3;
         this.base = var4;
         this.packedId = var4.id | var5 << 8;
         this.displayName = Component.translatable("entity.minecraft.tropical_fish.type." + this.name);
      }

      public static TropicalFish.Pattern byId(int var0) {
         return BY_ID.apply(var0);
      }

      public TropicalFish.Base base() {
         return this.base;
      }

      public int getPackedId() {
         return this.packedId;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      public Component displayName() {
         return this.displayName;
      }
   }

   static class TropicalFishGroupData extends AbstractSchoolingFish.SchoolSpawnGroupData {
      final TropicalFish.Variant variant;

      TropicalFishGroupData(TropicalFish var1, TropicalFish.Variant var2) {
         super(var1);
         this.variant = var2;
      }
   }

   public static record Variant(TropicalFish.Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
      public static final Codec<TropicalFish.Variant> CODEC = Codec.INT.xmap(TropicalFish.Variant::new, TropicalFish.Variant::getPackedId);

      public Variant(int var1) {
         this(TropicalFish.getPattern(var1), TropicalFish.getBaseColor(var1), TropicalFish.getPatternColor(var1));
      }

      public Variant(TropicalFish.Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
         super();
         this.pattern = pattern;
         this.baseColor = baseColor;
         this.patternColor = patternColor;
      }

      public int getPackedId() {
         return TropicalFish.packVariant(this.pattern, this.baseColor, this.patternColor);
      }
   }
}
