package net.minecraft.world.item.crafting;

import com.mojang.datafixers.Products;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public abstract class SingleItemRecipe implements Recipe<SingleRecipeInput> {
   protected final Recipe.CommonInfo commonInfo;
   private final Ingredient input;
   private final ItemStackTemplate result;
   private @Nullable PlacementInfo placementInfo;

   public SingleItemRecipe(final Recipe.CommonInfo commonInfo, final Ingredient input, final ItemStackTemplate result) {
      super();
      this.commonInfo = commonInfo;
      this.input = input;
      this.result = result;
   }

   public abstract RecipeSerializer<? extends SingleItemRecipe> getSerializer();

   public abstract RecipeType<? extends SingleItemRecipe> getType();

   public boolean matches(final SingleRecipeInput input, final Level level) {
      return this.input.test(input.item());
   }

   public boolean showNotification() {
      return this.commonInfo.showNotification();
   }

   public Ingredient input() {
      return this.input;
   }

   protected ItemStackTemplate result() {
      return this.result;
   }

   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.create(this.input);
      }

      return this.placementInfo;
   }

   public ItemStack assemble(final SingleRecipeInput input) {
      return this.result.create();
   }

   public static <T extends SingleItemRecipe> MapCodec<T> simpleMapCodec(final Factory<T> factory) {
      return RecordCodecBuilder.mapCodec((i) -> {
         Products.P3 var10000 = i.group(Recipe.CommonInfo.MAP_CODEC.forGetter((o) -> o.commonInfo), Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleItemRecipe::input), ItemStackTemplate.CODEC.fieldOf("result").forGetter(SingleItemRecipe::result));
         Objects.requireNonNull(factory);
         return var10000.apply(i, factory::create);
      });
   }

   public static <T extends SingleItemRecipe> StreamCodec<RegistryFriendlyByteBuf, T> simpleStreamCodec(final Factory<T> factory) {
      StreamCodec var10000 = Recipe.CommonInfo.STREAM_CODEC;
      Function var10001 = (o) -> o.commonInfo;
      StreamCodec var10002 = Ingredient.CONTENTS_STREAM_CODEC;
      Function var10003 = SingleItemRecipe::input;
      StreamCodec var10004 = ItemStackTemplate.STREAM_CODEC;
      Function var10005 = SingleItemRecipe::result;
      Objects.requireNonNull(factory);
      return StreamCodec.composite(var10000, var10001, var10002, var10003, var10004, var10005, factory::create);
   }

   @FunctionalInterface
   public interface Factory<T extends SingleItemRecipe> {
      T create(Recipe.CommonInfo commonInfo, Ingredient ingredient, ItemStackTemplate result);
   }
}
