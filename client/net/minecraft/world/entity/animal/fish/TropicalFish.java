package net.minecraft.world.entity.animal.fish;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class TropicalFish extends AbstractSchoolingFish {
   public static final Variant DEFAULT_VARIANT;
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT;
   public static final List<Variant> COMMON_VARIANTS;
   private boolean isSchool = true;

   public TropicalFish(final EntityType<? extends TropicalFish> type, final Level level) {
      super(type, level);
   }

   public static String getPredefinedName(final int index) {
      return "entity.minecraft.tropical_fish.predefined." + index;
   }

   private static int packVariant(final Pattern pattern, final DyeColor baseColor, final DyeColor patternColor) {
      return pattern.getPackedId() & '\uffff' | (baseColor.getId() & 255) << 16 | (patternColor.getId() & 255) << 24;
   }

   public static DyeColor getBaseColor(final int packedVariant) {
      return DyeColor.byId(packedVariant >> 16 & 255);
   }

   public static DyeColor getPatternColor(final int packedVariant) {
      return DyeColor.byId(packedVariant >> 24 & 255);
   }

   public static Pattern getPattern(final int packedVariant) {
      return TropicalFish.Pattern.byId(packedVariant & '\uffff');
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_ID_TYPE_VARIANT, DEFAULT_VARIANT.getPackedId());
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.store("Variant", TropicalFish.Variant.CODEC, new Variant(this.getPackedVariant()));
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      Variant variant = (Variant)input.read("Variant", TropicalFish.Variant.CODEC).orElse(DEFAULT_VARIANT);
      this.setPackedVariant(variant.getPackedId());
   }

   private void setPackedVariant(final int i) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, i);
   }

   public boolean isMaxGroupSizeReached(final int groupSize) {
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

   public Pattern getPattern() {
      return getPattern(this.getPackedVariant());
   }

   private void setPattern(final Pattern pattern) {
      int base = this.getPackedVariant();
      DyeColor baseColor = getBaseColor(base);
      DyeColor patternColor = getPatternColor(base);
      this.setPackedVariant(packVariant(pattern, baseColor, patternColor));
   }

   private void setBaseColor(final DyeColor baseColor) {
      int base = this.getPackedVariant();
      Pattern pattern = getPattern(base);
      DyeColor patternColor = getPatternColor(base);
      this.setPackedVariant(packVariant(pattern, baseColor, patternColor));
   }

   private void setPatternColor(final DyeColor patternColor) {
      int base = this.getPackedVariant();
      Pattern pattern = getPattern(base);
      DyeColor baseColor = getBaseColor(base);
      this.setPackedVariant(packVariant(pattern, baseColor, patternColor));
   }

   public <T> @Nullable T get(final DataComponentType<? extends T> type) {
      if (type == DataComponents.TROPICAL_FISH_PATTERN) {
         return (T)castComponentValue(type, this.getPattern());
      } else if (type == DataComponents.TROPICAL_FISH_BASE_COLOR) {
         return (T)castComponentValue(type, this.getBaseColor());
      } else {
         return (T)(type == DataComponents.TROPICAL_FISH_PATTERN_COLOR ? castComponentValue(type, this.getPatternColor()) : super.get(type));
      }
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      this.applyImplicitComponentIfPresent(components, DataComponents.TROPICAL_FISH_PATTERN);
      this.applyImplicitComponentIfPresent(components, DataComponents.TROPICAL_FISH_BASE_COLOR);
      this.applyImplicitComponentIfPresent(components, DataComponents.TROPICAL_FISH_PATTERN_COLOR);
      super.applyImplicitComponents(components);
   }

   protected <T> boolean applyImplicitComponent(final DataComponentType<T> type, final T value) {
      if (type == DataComponents.TROPICAL_FISH_PATTERN) {
         this.setPattern((Pattern)castComponentValue(DataComponents.TROPICAL_FISH_PATTERN, value));
         return true;
      } else if (type == DataComponents.TROPICAL_FISH_BASE_COLOR) {
         this.setBaseColor((DyeColor)castComponentValue(DataComponents.TROPICAL_FISH_BASE_COLOR, value));
         return true;
      } else if (type == DataComponents.TROPICAL_FISH_PATTERN_COLOR) {
         this.setPatternColor((DyeColor)castComponentValue(DataComponents.TROPICAL_FISH_PATTERN_COLOR, value));
         return true;
      } else {
         return super.applyImplicitComponent(type, value);
      }
   }

   public void saveToBucketTag(final ItemStack bucket) {
      super.saveToBucketTag(bucket);
      bucket.copyFrom(DataComponents.TROPICAL_FISH_PATTERN, this);
      bucket.copyFrom(DataComponents.TROPICAL_FISH_BASE_COLOR, this);
      bucket.copyFrom(DataComponents.TROPICAL_FISH_PATTERN_COLOR, this);
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

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.TROPICAL_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.TROPICAL_FISH_FLOP;
   }

   public @Nullable SpawnGroupData finalizeSpawn(final ServerLevelAccessor level, final DifficultyInstance difficulty, final EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
      groupData = super.finalizeSpawn(level, difficulty, spawnReason, groupData);
      RandomSource random = level.getRandom();
      Variant variant;
      if (groupData instanceof TropicalFishGroupData tropicalFishGroupData) {
         variant = tropicalFishGroupData.variant;
      } else if ((double)random.nextFloat() < 0.9) {
         variant = (Variant)Util.getRandom(COMMON_VARIANTS, random);
         groupData = new TropicalFishGroupData(this, variant);
      } else {
         this.isSchool = false;
         Pattern[] patterns = TropicalFish.Pattern.values();
         DyeColor[] colors = DyeColor.values();
         Pattern pattern = (Pattern)Util.getRandom(patterns, random);
         DyeColor baseColor = (DyeColor)Util.getRandom(colors, random);
         DyeColor patternColor = (DyeColor)Util.getRandom(colors, random);
         variant = new Variant(pattern, baseColor, patternColor);
      }

      this.setPackedVariant(variant.getPackedId());
      return groupData;
   }

   public static boolean checkTropicalFishSpawnRules(final EntityType<TropicalFish> type, final LevelAccessor level, final EntitySpawnReason spawnReason, final BlockPos pos, final RandomSource random) {
      return level.getFluidState(pos.below()).is(FluidTags.WATER) && level.getBlockState(pos.above()).is(Blocks.WATER) && (level.getBiome(pos).is(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT) || WaterAnimal.checkSurfaceWaterAnimalSpawnRules(type, level, spawnReason, pos, random));
   }

   static {
      DEFAULT_VARIANT = new Variant(TropicalFish.Pattern.KOB, DyeColor.WHITE, DyeColor.WHITE);
      DATA_ID_TYPE_VARIANT = SynchedEntityData.<Integer>defineId(TropicalFish.class, EntityDataSerializers.INT);
      COMMON_VARIANTS = List.of(new Variant(TropicalFish.Pattern.STRIPEY, DyeColor.ORANGE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.GRAY), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.GRAY, DyeColor.BLUE), new Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.SUNSTREAK, DyeColor.BLUE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.KOB, DyeColor.ORANGE, DyeColor.WHITE), new Variant(TropicalFish.Pattern.SPOTTY, DyeColor.PINK, DyeColor.LIGHT_BLUE), new Variant(TropicalFish.Pattern.BLOCKFISH, DyeColor.PURPLE, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.RED), new Variant(TropicalFish.Pattern.SPOTTY, DyeColor.WHITE, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.GLITTER, DyeColor.WHITE, DyeColor.GRAY), new Variant(TropicalFish.Pattern.CLAYFISH, DyeColor.WHITE, DyeColor.ORANGE), new Variant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.PINK), new Variant(TropicalFish.Pattern.BRINELY, DyeColor.LIME, DyeColor.LIGHT_BLUE), new Variant(TropicalFish.Pattern.BETTY, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFish.Pattern.SNOOPER, DyeColor.GRAY, DyeColor.RED), new Variant(TropicalFish.Pattern.BLOCKFISH, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.WHITE, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.KOB, DyeColor.RED, DyeColor.WHITE), new Variant(TropicalFish.Pattern.SUNSTREAK, DyeColor.GRAY, DyeColor.WHITE), new Variant(TropicalFish.Pattern.DASHER, DyeColor.CYAN, DyeColor.YELLOW), new Variant(TropicalFish.Pattern.FLOPPER, DyeColor.YELLOW, DyeColor.YELLOW));
   }

   public static enum Base {
      SMALL(0),
      LARGE(1);

      private final int id;

      private Base(final int id) {
         this.id = id;
      }

      // $FF: synthetic method
      private static Base[] $values() {
         return new Base[]{SMALL, LARGE};
      }
   }

   public static record Variant(Pattern pattern, DyeColor baseColor, DyeColor patternColor) {
      public static final Codec<Variant> CODEC;

      public Variant(final int packedId) {
         this(TropicalFish.getPattern(packedId), TropicalFish.getBaseColor(packedId), TropicalFish.getPatternColor(packedId));
      }

      public Variant {
         super();
      }

      public int getPackedId() {
         return TropicalFish.packVariant(this.pattern, this.baseColor, this.patternColor);
      }

      static {
         CODEC = Codec.INT.xmap(Variant::new, Variant::getPackedId);
      }
   }

   public static enum Pattern implements StringRepresentable, TooltipProvider {
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

      public static final Codec<Pattern> CODEC = StringRepresentable.<Pattern>fromEnum(Pattern::values);
      private static final IntFunction<Pattern> BY_ID = ByIdMap.<Pattern>sparse(Pattern::getPackedId, values(), KOB);
      public static final StreamCodec<ByteBuf, Pattern> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Pattern::getPackedId);
      private final String name;
      private final Component displayName;
      private final Base base;
      private final int packedId;

      private Pattern(final String name, final Base base, final int index) {
         this.name = name;
         this.base = base;
         this.packedId = base.id | index << 8;
         this.displayName = Component.translatable("entity.minecraft.tropical_fish.type." + this.name);
      }

      public static Pattern byId(final int packedId) {
         return (Pattern)BY_ID.apply(packedId);
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

      public void addToTooltip(final Item.TooltipContext context, final Consumer<Component> consumer, final TooltipFlag flag, final DataComponentGetter components) {
         DyeColor baseColor = (DyeColor)components.getOrDefault(DataComponents.TROPICAL_FISH_BASE_COLOR, TropicalFish.DEFAULT_VARIANT.baseColor());
         DyeColor patternColor = (DyeColor)components.getOrDefault(DataComponents.TROPICAL_FISH_PATTERN_COLOR, TropicalFish.DEFAULT_VARIANT.patternColor());
         ChatFormatting[] styles = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};
         int commonIndex = TropicalFish.COMMON_VARIANTS.indexOf(new Variant(this, baseColor, patternColor));
         if (commonIndex != -1) {
            consumer.accept(Component.translatable(TropicalFish.getPredefinedName(commonIndex)).withStyle(styles));
         } else {
            consumer.accept(this.displayName.plainCopy().withStyle(styles));
            MutableComponent colorComponent = Component.translatable("color.minecraft." + baseColor.getName());
            if (baseColor != patternColor) {
               colorComponent.append(", ").append((Component)Component.translatable("color.minecraft." + patternColor.getName()));
            }

            colorComponent.withStyle(styles);
            consumer.accept(colorComponent);
         }
      }

      // $FF: synthetic method
      private static Pattern[] $values() {
         return new Pattern[]{KOB, SUNSTREAK, SNOOPER, DASHER, BRINELY, SPOTTY, FLOPPER, STRIPEY, GLITTER, BLOCKFISH, BETTY, CLAYFISH};
      }
   }

   private static class TropicalFishGroupData extends AbstractSchoolingFish.SchoolSpawnGroupData {
      private final Variant variant;

      private TropicalFishGroupData(final TropicalFish leader, final Variant variant) {
         super(leader);
         this.variant = variant;
      }
   }
}
