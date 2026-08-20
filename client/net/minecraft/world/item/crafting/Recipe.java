package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;

public interface Recipe<T extends RecipeInput> {
   Codec<Recipe<?>> DIRECT_CODEC = BuiltInRegistries.RECIPE_SERIALIZER.byNameCodec().dispatch(Recipe::getSerializer, RecipeSerializer::codec);
   Codec<ResourceKey<Recipe<?>>> KEY_CODEC = ResourceKey.codec(Registries.RECIPE);
   StreamCodec<RegistryFriendlyByteBuf, Recipe<?>> STREAM_CODEC = ByteBufCodecs.registry(Registries.RECIPE_SERIALIZER).dispatch(Recipe::getSerializer, RecipeSerializer::streamCodec);
   Codec<Holder<Recipe<?>>> CODEC = RegistryCodecs.holder(Registries.RECIPE, DIRECT_CODEC);
   Codec<HolderSet<Recipe<?>>> LIST_CODEC = RegistryCodecs.holderSet(Registries.RECIPE, DIRECT_CODEC);

   boolean matches(T input, Level level);

   ItemStack assemble(T input);

   default boolean isSpecial() {
      return false;
   }

   boolean showNotification();

   String group();

   RecipeSerializer<? extends Recipe<T>> getSerializer();

   RecipeType<? extends Recipe<T>> getType();

   PlacementInfo placementInfo();

   default List<RecipeDisplay> display() {
      return List.of();
   }

   RecipeBookCategory recipeBookCategory();

   public static record CommonInfo(boolean showNotification) {
      public static final MapCodec<CommonInfo> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(CommonInfo::showNotification)).apply(i, CommonInfo::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, CommonInfo> STREAM_CODEC;

      public CommonInfo {
         super();
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, CommonInfo::showNotification, CommonInfo::new);
      }
   }

   public interface BookInfo<CategoryType> {
      CategoryType category();

      String group();

      static <CategoryType, SelfType extends BookInfo<CategoryType>> MapCodec<SelfType> mapCodec(final Codec<CategoryType> categoryCodec, final CategoryType defaultCategory, final Constructor<CategoryType, SelfType> constructor) {
         return RecordCodecBuilder.mapCodec((i) -> i.group(categoryCodec.optionalFieldOf("category", defaultCategory).forGetter(BookInfo::category), Codec.STRING.optionalFieldOf("group", "").forGetter(BookInfo::group)).apply(i, constructor));
      }

      static <CategoryType, SelfType extends BookInfo<CategoryType>> StreamCodec<RegistryFriendlyByteBuf, SelfType> streamCodec(final StreamCodec<? super RegistryFriendlyByteBuf, CategoryType> categoryCodec, final Constructor<CategoryType, SelfType> constructor) {
         return StreamCodec.composite(categoryCodec, BookInfo::category, ByteBufCodecs.STRING_UTF8, BookInfo::group, constructor);
      }

      @FunctionalInterface
      public interface Constructor<CategoryType, SelfType extends BookInfo<CategoryType>> extends BiFunction<CategoryType, String, SelfType> {
      }
   }
}
