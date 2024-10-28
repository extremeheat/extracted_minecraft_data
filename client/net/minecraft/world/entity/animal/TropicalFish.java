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

public class TropicalFish extends AbstractSchoolingFish implements VariantHolder<Pattern> {
   public static final String BUCKET_VARIANT_TAG = "BucketVariantTag";
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT;
   public static final List<Variant> COMMON_VARIANTS;
   private boolean isSchool = true;

   public TropicalFish(EntityType<? extends TropicalFish> var1, Level var2) {
      super(var1, var2);
   }

   public static String getPredefinedName(int var0) {
      return "entity.minecraft.tropical_fish.predefined." + var0;
   }

   static int packVariant(Pattern var0, DyeColor var1, DyeColor var2) {
      return var0.getPackedId() & '\uffff' | (var1.getId() & 255) << 16 | (var2.getId() & 255) << 24;
   }

   public static DyeColor getBaseColor(int var0) {
      return DyeColor.byId(var0 >> 16 & 255);
   }

   public static DyeColor getPatternColor(int var0) {
      return DyeColor.byId(var0 >> 24 & 255);
   }

   public static Pattern getPattern(int var0) {
      return TropicalFish.Pattern.byId(var0 & '\uffff');
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Variant", this.getPackedVariant());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setPackedVariant(var1.getInt("Variant"));
   }

   private void setPackedVariant(int var1) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, var1);
   }

   public boolean isMaxGroupSizeReached(int var1) {
      return !this.isSchool;
   }

   private int getPackedVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   public DyeColor getBaseColor() {
      return getBaseColor(this.getPackedVariant());
   }

   public DyeColor getPatternColor() {
      return getPatternColor(this.getPackedVariant());
   }

   public Pattern getVariant() {
      return getPattern(this.getPackedVariant());
   }

   public void setVariant(Pattern var1) {
      int var2 = this.getPackedVariant();
      DyeColor var3 = getBaseColor(var2);
      DyeColor var4 = getPatternColor(var2);
      this.setPackedVariant(packVariant(var1, var3, var4));
   }

   public void saveToBucketTag(ItemStack var1) {
      super.saveToBucketTag(var1);
      CustomData.update(DataComponents.BUCKET_ENTITY_DATA, var1, (var1x) -> {
         var1x.putInt("BucketVariantTag", this.getPackedVariant());
      });
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.TROPICAL_FISH_BUCKET);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.TROPICAL_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.TROPICAL_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.TROPICAL_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.TROPICAL_FISH_FLOP;
   }

   public void loadFromBucketTag(CompoundTag var1) {
      super.loadFromBucketTag(var1);
      if (var1.contains("BucketVariantTag", 3)) {
         this.setPackedVariant(var1.getInt("BucketVariantTag"));
      }

   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      Object var13 = super.finalizeSpawn(var1, var2, var3, var4);
      RandomSource var6 = var1.getRandom();
      Variant var5;
      if (var13 instanceof TropicalFishGroupData var7) {
         var5 = var7.variant;
      } else if ((double)var6.nextFloat() < 0.9) {
         var5 = (Variant)Util.getRandom(COMMON_VARIANTS, var6);
         var13 = new TropicalFishGroupData(this, var5);
      } else {
         this.isSchool = false;
         Pattern[] var8 = TropicalFish.Pattern.values();
         DyeColor[] var9 = DyeColor.values();
         Pattern var10 = (Pattern)Util.getRandom((Object[])var8, var6);
         DyeColor var11 = (DyeColor)Util.getRandom((Object[])var9, var6);
         DyeColor var12 = (DyeColor)Util.getRandom((Object[])var9, var6);
         var5 = new Variant(var10, var11, var12);
      }

      this.setPackedVariant(var5.getPackedId());
      return (SpawnGroupData)var13;
   }

   public static boolean checkTropicalFishSpawnRules(EntityType<TropicalFish> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getFluidState(var3.below()).is(FluidTags.WATER) && var1.getBlockState(var3.above()).is(Blocks.WATER) && (var1.getBiome(var3).is(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT) || WaterAnimal.checkSurfaceWaterAnimalSpawnRules(var0, var1, var2, var3, var4));
   }

   // $FF: synthetic method
   public Object getVariant() {
      return this.getVariant();
   }

   static {
      DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(TropicalFish.class, EntityDataSerializers.INT);
      COMMON_VARIANTS = List.of(new Variant(TropicalFish.Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), new Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE), new Variant(TropicalFish.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), new Variant(TropicalFish.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED), new Variant(TropicalFish.Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), new Variant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK), new Variant(TropicalFish.Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), new Variant(TropicalFish.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFish.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED), new Variant(TropicalFish.Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.KOB, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFish.Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), new Variant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW));
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

      public static final Codec<Pattern> CODEC = StringRepresentable.fromEnum(Pattern::values);
      private static final IntFunction<Pattern> BY_ID = ByIdMap.sparse(Pattern::getPackedId, values(), KOB);
      private final String name;
      private final Component displayName;
      private final Base base;
      private final int packedId;

      private Pattern(final String var3, final Base var4, final int var5) {
         this.name = var3;
         this.base = var4;
         this.packedId = var4.id | var5 << 8;
         this.displayName = Component.translatable("entity.minecraft.tropical_fish.type." + this.name);
      }

      public static Pattern byId(int var0) {
         return (Pattern)BY_ID.apply(var0);
      }

      public Base base() {
         return this.base;
      }

      public int getPackedId() {
         return this.packedId;
      }

      public String getSerializedName() {
         return this.name;
      }

      public Component displayName() {
         return this.displayName;
      }

      // $FF: synthetic method
      private static Pattern[] $values() {
         return new Pattern[]{KOB, SUNSTREAK, SNOOPER, DASHER, BRINELY, SPOTTY, FLOPPER, STRIPEY, GLITTER, BLOCKFISH, BETTY, CLAYFISH};
      }
   }

   private static class TropicalFishGroupData extends AbstractSchoolingFish.SchoolSpawnGroupData {
      final Variant variant;

      TropicalFishGroupData(TropicalFish var1, Variant var2) {
         super(var1);
         this.variant = var2;
      }
   }

   public static record Variant(Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
      public static final Codec<Variant> CODEC;

      public Variant(int var1) {
         this(TropicalFish.getPattern(var1), TropicalFish.getBaseColor(var1), TropicalFish.getPatternColor(var1));
      }

      public Variant(Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
         super();
         this.pattern = pattern;
         this.baseColor = baseColor;
         this.patternColor = patternColor;
      }

      public int getPackedId() {
         return TropicalFish.packVariant(this.pattern, this.baseColor, this.patternColor);
      }

      public Pattern pattern() {
         return this.pattern;
      }

      public DyeColor baseColor() {
         return this.baseColor;
      }

      public DyeColor patternColor() {
         return this.patternColor;
      }

      static {
         CODEC = Codec.INT.xmap(Variant::new, Variant::getPackedId);
      }
   }

   public static enum Base {
      SMALL(0),
      LARGE(1);

      final int id;

      private Base(final int var3) {
         this.id = var3;
      }

      // $FF: synthetic method
      private static Base[] $values() {
         return new Base[]{SMALL, LARGE};
      }
   }
}
