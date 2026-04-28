package net.minecraft.world.item.crafting;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public abstract class AbstractCookingRecipe extends SingleItemRecipe {
   protected final CookingBookInfo bookInfo;
   private final float experience;
   private final int cookingTime;

   public AbstractCookingRecipe(final Recipe.CommonInfo commonInfo, final CookingBookInfo bookInfo, final Ingredient ingredient, final ItemStackTemplate result, final float experience, final int cookingTime) {
      super(commonInfo, ingredient, result);
      this.bookInfo = bookInfo;
      this.experience = experience;
      this.cookingTime = cookingTime;
   }

   public abstract RecipeSerializer<? extends AbstractCookingRecipe> getSerializer();

   public abstract RecipeType<? extends AbstractCookingRecipe> getType();

   public float experience() {
      return this.experience;
   }

   public int cookingTime() {
      return this.cookingTime;
   }

   public CookingBookCategory category() {
      return this.bookInfo.category;
   }

   public String group() {
      return this.bookInfo.group;
   }

   protected abstract Item furnaceIcon();

   public List<RecipeDisplay> display() {
      return List.of(new FurnaceRecipeDisplay(this.input().display(), SlotDisplay.AnyFuel.INSTANCE, new SlotDisplay.ItemStackSlotDisplay(this.result()), new SlotDisplay.ItemSlotDisplay(this.furnaceIcon()), this.cookingTime, this.experience));
   }

   public static <T extends AbstractCookingRecipe> MapCodec<T> cookingMapCodec(final Factory<T> factory, final int defaultCookingTime) {
      return RecordCodecBuilder.mapCodec((i) -> {
         Products.P6 var10000 = i.group(Recipe.CommonInfo.MAP_CODEC.forGetter((o) -> o.commonInfo), AbstractCookingRecipe.CookingBookInfo.MAP_CODEC.forGetter((o) -> o.bookInfo), Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleItemRecipe::input), ItemStackTemplate.CODEC.fieldOf("result").forGetter(SingleItemRecipe::result), Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(AbstractCookingRecipe::experience), Codec.INT.optionalFieldOf("cookingtime", defaultCookingTime).forGetter(AbstractCookingRecipe::cookingTime));
         Objects.requireNonNull(factory);
         return var10000.apply(i, factory::create);
      });
   }

   public static <T extends AbstractCookingRecipe> StreamCodec<RegistryFriendlyByteBuf, T> cookingStreamCodec(final Factory<T> factory) {
      StreamCodec var10000 = Recipe.CommonInfo.STREAM_CODEC;
      Function var10001 = (o) -> o.commonInfo;
      StreamCodec var10002 = AbstractCookingRecipe.CookingBookInfo.STREAM_CODEC;
      Function var10003 = (o) -> o.bookInfo;
      StreamCodec var10004 = Ingredient.CONTENTS_STREAM_CODEC;
      Function var10005 = SingleItemRecipe::input;
      StreamCodec var10006 = ItemStackTemplate.STREAM_CODEC;
      Function var10007 = SingleItemRecipe::result;
      StreamCodec var10008 = ByteBufCodecs.FLOAT;
      Function var10009 = AbstractCookingRecipe::experience;
      StreamCodec var10010 = ByteBufCodecs.INT;
      Function var10011 = AbstractCookingRecipe::cookingTime;
      Objects.requireNonNull(factory);
      return StreamCodec.composite(var10000, var10001, var10002, var10003, var10004, var10005, var10006, var10007, var10008, var10009, var10010, var10011, factory::create);
   }

   public static record CookingBookInfo(CookingBookCategory category, String group) implements Recipe.BookInfo<CookingBookCategory> {
      public static final MapCodec<CookingBookInfo> MAP_CODEC;
      public static final StreamCodec<RegistryFriendlyByteBuf, CookingBookInfo> STREAM_CODEC;

      public CookingBookInfo {
         super();
      }

      static {
         MAP_CODEC = Recipe.BookInfo.mapCodec(CookingBookCategory.CODEC, CookingBookCategory.MISC, CookingBookInfo::new);
         STREAM_CODEC = Recipe.BookInfo.streamCodec(CookingBookCategory.STREAM_CODEC, CookingBookInfo::new);
      }
   }

   @FunctionalInterface
   public interface Factory<T extends AbstractCookingRecipe> {
      T create(Recipe.CommonInfo commonInfo, CookingBookInfo cbookInfotegory, Ingredient ingredient, ItemStackTemplate result, float experience, int cookingTime);
   }
}
