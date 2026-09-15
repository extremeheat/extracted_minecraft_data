package net.minecraft.world.item.crafting;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.predicates.PotionsPredicate;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public record PotionIngredient(Ingredient ingredient, Optional<PotionsPredicate> potions) implements Predicate<ItemStack> {
   public static final MapCodec<PotionIngredient> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Ingredient.CODEC.fieldOf("item").forGetter((o) -> o.ingredient), PotionsPredicate.CODEC.optionalFieldOf("potion_contents").forGetter((o) -> o.potions)).apply(i, PotionIngredient::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, PotionIngredient> STREAM_CODEC;

   public PotionIngredient {
      super();
   }

   public boolean test(final ItemStack input) {
      if (!this.ingredient.test(input)) {
         return false;
      } else {
         return this.potions.isEmpty() || ((PotionsPredicate)this.potions.get()).matches((DataComponentGetter)input);
      }
   }

   public static PotionIngredient of(final Item item, final PotionsPredicate potions) {
      return new PotionIngredient(Ingredient.of((ItemLike)item), Optional.of(potions));
   }

   public static PotionIngredient of(final Item item) {
      return new PotionIngredient(Ingredient.of((ItemLike)item), Optional.empty());
   }

   public static boolean isPotionInput(final ItemStack itemStack, final RecipeAccess recipeAccess) {
      RecipePropertySet brewingInputs = recipeAccess.propertySet(RecipePropertySet.BREWING_INPUTS);
      return brewingInputs.test(itemStack) || itemStack.is(ItemTags.BREWING_POTION_INPUTS);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, (o) -> o.ingredient, ByteBufCodecs.optional(PotionsPredicate.STREAM_CODEC), (o) -> o.potions, PotionIngredient::new);
   }
}
