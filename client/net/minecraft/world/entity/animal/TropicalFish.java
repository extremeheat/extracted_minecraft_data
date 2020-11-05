package net.minecraft.world.entity.animal;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class TropicalFish extends AbstractSchoolingFish {
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT;
   private static final ResourceLocation[] BASE_TEXTURE_LOCATIONS;
   private static final ResourceLocation[] PATTERN_A_TEXTURE_LOCATIONS;
   private static final ResourceLocation[] PATTERN_B_TEXTURE_LOCATIONS;
   public static final int[] COMMON_VARIANTS;
   private boolean isSchool = true;

   private static int calculateVariant(TropicalFish.Pattern var0, DyeColor var1, DyeColor var2) {
      return var0.getBase() & 255 | (var0.getIndex() & 255) << 8 | (var1.getId() & 255) << 16 | (var2.getId() & 255) << 24;
   }

   public TropicalFish(EntityType<? extends TropicalFish> var1, Level var2) {
      super(var1, var2);
   }

   public static String getPredefinedName(int var0) {
      return "entity.minecraft.tropical_fish.predefined." + var0;
   }

   public static DyeColor getBaseColor(int var0) {
      return DyeColor.byId(getBaseColorIdx(var0));
   }

   public static DyeColor getPatternColor(int var0) {
      return DyeColor.byId(getPatternColorIdx(var0));
   }

   public static String getFishTypeName(int var0) {
      int var1 = getBaseVariant(var0);
      int var2 = getPatternVariant(var0);
      return "entity.minecraft.tropical_fish.type." + TropicalFish.Pattern.getPatternName(var1, var2);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Variant", this.getVariant());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setVariant(var1.getInt("Variant"));
   }

   public void setVariant(int var1) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, var1);
   }

   public boolean isMaxGroupSizeReached(int var1) {
      return !this.isSchool;
   }

   public int getVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   protected void saveToBucketTag(ItemStack var1) {
      super.saveToBucketTag(var1);
      CompoundTag var2 = var1.getOrCreateTag();
      var2.putInt("BucketVariantTag", this.getVariant());
   }

   protected ItemStack getBucketItemStack() {
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

   private static int getBaseColorIdx(int var0) {
      return (var0 & 16711680) >> 16;
   }

   public float[] getBaseColor() {
      return DyeColor.byId(getBaseColorIdx(this.getVariant())).getTextureDiffuseColors();
   }

   private static int getPatternColorIdx(int var0) {
      return (var0 & -16777216) >> 24;
   }

   public float[] getPatternColor() {
      return DyeColor.byId(getPatternColorIdx(this.getVariant())).getTextureDiffuseColors();
   }

   public static int getBaseVariant(int var0) {
      return Math.min(var0 & 255, 1);
   }

   public int getBaseVariant() {
      return getBaseVariant(this.getVariant());
   }

   private static int getPatternVariant(int var0) {
      return Math.min((var0 & '\uff00') >> 8, 5);
   }

   public ResourceLocation getPatternTextureLocation() {
      return getBaseVariant(this.getVariant()) == 0 ? PATTERN_A_TEXTURE_LOCATIONS[getPatternVariant(this.getVariant())] : PATTERN_B_TEXTURE_LOCATIONS[getPatternVariant(this.getVariant())];
   }

   public ResourceLocation getBaseTextureLocation() {
      return BASE_TEXTURE_LOCATIONS[getBaseVariant(this.getVariant())];
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      Object var11 = super.finalizeSpawn(var1, var2, var3, var4, var5);
      if (var5 != null && var5.contains("BucketVariantTag", 3)) {
         this.setVariant(var5.getInt("BucketVariantTag"));
         return (SpawnGroupData)var11;
      } else {
         int var6;
         int var7;
         int var8;
         int var9;
         if (var11 instanceof TropicalFish.TropicalFishGroupData) {
            TropicalFish.TropicalFishGroupData var10 = (TropicalFish.TropicalFishGroupData)var11;
            var6 = var10.base;
            var7 = var10.pattern;
            var8 = var10.baseColor;
            var9 = var10.patternColor;
         } else if ((double)this.random.nextFloat() < 0.9D) {
            int var12 = Util.getRandom(COMMON_VARIANTS, this.random);
            var6 = var12 & 255;
            var7 = (var12 & '\uff00') >> 8;
            var8 = (var12 & 16711680) >> 16;
            var9 = (var12 & -16777216) >> 24;
            var11 = new TropicalFish.TropicalFishGroupData(this, var6, var7, var8, var9);
         } else {
            this.isSchool = false;
            var6 = this.random.nextInt(2);
            var7 = this.random.nextInt(6);
            var8 = this.random.nextInt(15);
            var9 = this.random.nextInt(15);
         }

         this.setVariant(var6 | var7 << 8 | var8 << 16 | var9 << 24);
         return (SpawnGroupData)var11;
      }
   }

   static {
      DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(TropicalFish.class, EntityDataSerializers.INT);
      BASE_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a.png"), new ResourceLocation("textures/entity/fish/tropical_b.png")};
      PATTERN_A_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_a_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_a_pattern_6.png")};
      PATTERN_B_TEXTURE_LOCATIONS = new ResourceLocation[]{new ResourceLocation("textures/entity/fish/tropical_b_pattern_1.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_2.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_3.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_4.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_5.png"), new ResourceLocation("textures/entity/fish/tropical_b_pattern_6.png")};
      COMMON_VARIANTS = new int[]{calculateVariant(TropicalFish.Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), calculateVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), calculateVariant(TropicalFish.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), calculateVariant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE), calculateVariant(TropicalFish.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), calculateVariant(TropicalFish.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), calculateVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED), calculateVariant(TropicalFish.Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), calculateVariant(TropicalFish.Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY), calculateVariant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), calculateVariant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK), calculateVariant(TropicalFish.Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), calculateVariant(TropicalFish.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE), calculateVariant(TropicalFish.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED), calculateVariant(TropicalFish.Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), calculateVariant(TropicalFish.Pattern.KOB, DyeColor.RED, DyeColor.WHITE), calculateVariant(TropicalFish.Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), calculateVariant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW), calculateVariant(TropicalFish.Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW)};
   }

   static class TropicalFishGroupData extends AbstractSchoolingFish.SchoolSpawnGroupData {
      private final int base;
      private final int pattern;
      private final int baseColor;
      private final int patternColor;

      private TropicalFishGroupData(TropicalFish var1, int var2, int var3, int var4, int var5) {
         super(var1);
         this.base = var2;
         this.pattern = var3;
         this.baseColor = var4;
         this.patternColor = var5;
      }

      // $FF: synthetic method
      TropicalFishGroupData(TropicalFish var1, int var2, int var3, int var4, int var5, Object var6) {
         this(var1, var2, var3, var4, var5);
      }
   }

   static enum Pattern {
      KOB(0, 0),
      SUNSTREAK(0, 1),
      SNOOPER(0, 2),
      DASHER(0, 3),
      BRINELY(0, 4),
      SPOTTY(0, 5),
      FLOPPER(1, 0),
      STRIPEY(1, 1),
      GLITTER(1, 2),
      BLOCKFISH(1, 3),
      BETTY(1, 4),
      CLAYFISH(1, 5);

      private final int base;
      private final int index;
      private static final TropicalFish.Pattern[] VALUES = values();

      private Pattern(int var3, int var4) {
         this.base = var3;
         this.index = var4;
      }

      public int getBase() {
         return this.base;
      }

      public int getIndex() {
         return this.index;
      }

      public static String getPatternName(int var0, int var1) {
         return VALUES[var1 + 6 * var0].getName();
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
