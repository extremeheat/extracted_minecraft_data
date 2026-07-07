package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;

public class BrewingRecipe implements Recipe<BrewingInput> {
   public static final MapCodec<BrewingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(PotionIngredient.MAP_CODEC.fieldOf("input").forGetter((o) -> o.input), PotionIngredient.MAP_CODEC.fieldOf("reagent").forGetter((o) -> o.reagent), ItemStackTemplate.CODEC.fieldOf("output").forGetter((o) -> o.output)).apply(i, BrewingRecipe::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, BrewingRecipe> STREAM_CODEC;
   public static final RecipeSerializer<BrewingRecipe> SERIALIZER;
   private final PotionIngredient input;
   private final PotionIngredient reagent;
   private final ItemStackTemplate output;

   public BrewingRecipe(final PotionIngredient input, final PotionIngredient reagent, final ItemStackTemplate output) {
      super();
      this.input = input;
      this.reagent = reagent;
      this.output = output;
   }

   public PotionIngredient getInput() {
      return this.input;
   }

   public PotionIngredient getReagent() {
      return this.reagent;
   }

   public ItemStackTemplate getOutput() {
      return this.output;
   }

   public boolean matches(final BrewingInput brewingInput, final Level level) {
      return this.matches(brewingInput);
   }

   public boolean matches(final BrewingInput brewingInput) {
      return this.input.test(brewingInput.input()) && this.reagent.test(brewingInput.reagent());
   }

   public ItemStack assemble(final BrewingInput input) {
      return this.output.create();
   }

   public RecipeType<BrewingRecipe> getType() {
      return RecipeType.BREWING;
   }

   public PlacementInfo placementInfo() {
      return PlacementInfo.NOT_PLACEABLE;
   }

   public boolean isSpecial() {
      return true;
   }

   public boolean showNotification() {
      return false;
   }

   public String group() {
      return "";
   }

   public RecipeBookCategory recipeBookCategory() {
      return RecipeBookCategories.CRAFTING_MISC;
   }

   public RecipeSerializer<BrewingRecipe> getSerializer() {
      return SERIALIZER;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(PotionIngredient.STREAM_CODEC, (o) -> o.input, PotionIngredient.STREAM_CODEC, (o) -> o.reagent, ItemStackTemplate.STREAM_CODEC, (o) -> o.output, BrewingRecipe::new);
      SERIALIZER = new RecipeSerializer<BrewingRecipe>(MAP_CODEC, STREAM_CODEC);
   }

   @FunctionalInterface
   public interface Factory<T extends BrewingRecipe> {
      T create(final Recipe.CommonInfo commonInfo, final PotionIngredient input, final PotionIngredient reagent, final ItemStackTemplate output);
   }
}
