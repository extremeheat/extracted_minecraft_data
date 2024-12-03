package net.minecraft.world.item.crafting;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

public class ShapedRecipe implements CraftingRecipe {
   final ShapedRecipePattern pattern;
   final ItemStack result;
   final String group;
   final CraftingBookCategory category;
   final boolean showNotification;
   @Nullable
   private PlacementInfo placementInfo;

   public ShapedRecipe(String var1, CraftingBookCategory var2, ShapedRecipePattern var3, ItemStack var4, boolean var5) {
      super();
      this.group = var1;
      this.category = var2;
      this.pattern = var3;
      this.result = var4;
      this.showNotification = var5;
   }

   public ShapedRecipe(String var1, CraftingBookCategory var2, ShapedRecipePattern var3, ItemStack var4) {
      this(var1, var2, var3, var4, true);
   }

   public RecipeSerializer<? extends ShapedRecipe> getSerializer() {
      return RecipeSerializer.SHAPED_RECIPE;
   }

   public String group() {
      return this.group;
   }

   public CraftingBookCategory category() {
      return this.category;
   }

   @VisibleForTesting
   public List<Optional<Ingredient>> getIngredients() {
      return this.pattern.ingredients();
   }

   public PlacementInfo placementInfo() {
      if (this.placementInfo == null) {
         this.placementInfo = PlacementInfo.createFromOptionals(this.pattern.ingredients());
      }

      return this.placementInfo;
   }

   public boolean showNotification() {
      return this.showNotification;
   }

   public boolean matches(CraftingInput var1, Level var2) {
      return this.pattern.matches(var1);
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      return this.result.copy();
   }

   public int getWidth() {
      return this.pattern.width();
   }

   public int getHeight() {
      return this.pattern.height();
   }

   public List<RecipeDisplay> display() {
      return List.of(new ShapedCraftingRecipeDisplay(this.pattern.width(), this.pattern.height(), this.pattern.ingredients().stream().map((var0) -> (SlotDisplay)var0.map(Ingredient::display).orElse(SlotDisplay.Empty.INSTANCE)).toList(), new SlotDisplay.ItemStackSlotDisplay(this.result), new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
   }

   public static class Serializer implements RecipeSerializer<ShapedRecipe> {
      public static final MapCodec<ShapedRecipe> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.optionalFieldOf("group", "").forGetter((var0x) -> var0x.group), CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter((var0x) -> var0x.category), ShapedRecipePattern.MAP_CODEC.forGetter((var0x) -> var0x.pattern), ItemStack.STRICT_CODEC.fieldOf("result").forGetter((var0x) -> var0x.result), Codec.BOOL.optionalFieldOf("show_notification", true).forGetter((var0x) -> var0x.showNotification)).apply(var0, ShapedRecipe::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, ShapedRecipe>of(Serializer::toNetwork, Serializer::fromNetwork);

      public Serializer() {
         super();
      }

      public MapCodec<ShapedRecipe> codec() {
         return CODEC;
      }

      public StreamCodec<RegistryFriendlyByteBuf, ShapedRecipe> streamCodec() {
         return STREAM_CODEC;
      }

      private static ShapedRecipe fromNetwork(RegistryFriendlyByteBuf var0) {
         String var1 = var0.readUtf();
         CraftingBookCategory var2 = (CraftingBookCategory)var0.readEnum(CraftingBookCategory.class);
         ShapedRecipePattern var3 = (ShapedRecipePattern)ShapedRecipePattern.STREAM_CODEC.decode(var0);
         ItemStack var4 = (ItemStack)ItemStack.STREAM_CODEC.decode(var0);
         boolean var5 = var0.readBoolean();
         return new ShapedRecipe(var1, var2, var3, var4, var5);
      }

      private static void toNetwork(RegistryFriendlyByteBuf var0, ShapedRecipe var1) {
         var0.writeUtf(var1.group);
         var0.writeEnum(var1.category);
         ShapedRecipePattern.STREAM_CODEC.encode(var0, var1.pattern);
         ItemStack.STREAM_CODEC.encode(var0, var1.result);
         var0.writeBoolean(var1.showNotification);
      }
   }
}
