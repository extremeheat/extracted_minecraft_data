package net.minecraft.world.item.crafting.display;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategory;

public record RecipeDisplayEntry(RecipeDisplayId id, RecipeDisplay display, OptionalInt group, RecipeBookCategory category, Optional<List<Ingredient>> craftingRequirements) {
   public static final StreamCodec<RegistryFriendlyByteBuf, RecipeDisplayEntry> STREAM_CODEC;

   public RecipeDisplayEntry(RecipeDisplayId var1, RecipeDisplay var2, OptionalInt var3, RecipeBookCategory var4, Optional<List<Ingredient>> var5) {
      super();
      this.id = var1;
      this.display = var2;
      this.group = var3;
      this.category = var4;
      this.craftingRequirements = var5;
   }

   public List<ItemStack> resultItems(ContextMap var1) {
      return this.display.result().resolveForStacks(var1);
   }

   public boolean canCraft(StackedItemContents var1) {
      return this.craftingRequirements.isEmpty() ? false : var1.canCraft((List)this.craftingRequirements.get(), (StackedContents.Output)null);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(RecipeDisplayId.STREAM_CODEC, RecipeDisplayEntry::id, RecipeDisplay.STREAM_CODEC, RecipeDisplayEntry::display, ByteBufCodecs.OPTIONAL_VAR_INT, RecipeDisplayEntry::group, ByteBufCodecs.registry(Registries.RECIPE_BOOK_CATEGORY), RecipeDisplayEntry::category, Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).apply(ByteBufCodecs::optional), RecipeDisplayEntry::craftingRequirements, RecipeDisplayEntry::new);
   }
}
