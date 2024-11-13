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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public abstract class AbstractCookingRecipe extends SingleItemRecipe {
   private final CookingBookCategory category;
   private final float experience;
   private final int cookingTime;

   public AbstractCookingRecipe(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      super(var1, var3, var4);
      this.category = var2;
      this.experience = var5;
      this.cookingTime = var6;
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
      return this.category;
   }

   protected abstract Item furnaceIcon();

   public List<RecipeDisplay> display() {
      return List.of(new FurnaceRecipeDisplay(this.input().display(), SlotDisplay.AnyFuel.INSTANCE, new SlotDisplay.ItemStackSlotDisplay(this.result()), new SlotDisplay.ItemSlotDisplay(this.furnaceIcon()), this.cookingTime, this.experience));
   }

   public static class Serializer<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {
      private final MapCodec<T> codec;
      private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

      public Serializer(Factory<T> var1, int var2) {
         super();
         this.codec = RecordCodecBuilder.mapCodec((var2x) -> {
            Products.P6 var10000 = var2x.group(Codec.STRING.optionalFieldOf("group", "").forGetter(SingleItemRecipe::group), CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(AbstractCookingRecipe::category), Ingredient.CODEC.fieldOf("ingredient").forGetter(SingleItemRecipe::input), ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(SingleItemRecipe::result), Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(AbstractCookingRecipe::experience), Codec.INT.fieldOf("cookingtime").orElse(var2).forGetter(AbstractCookingRecipe::cookingTime));
            Objects.requireNonNull(var1);
            return var10000.apply(var2x, var1::create);
         });
         StreamCodec var10001 = ByteBufCodecs.STRING_UTF8;
         Function var10002 = SingleItemRecipe::group;
         StreamCodec var10003 = CookingBookCategory.STREAM_CODEC;
         Function var10004 = AbstractCookingRecipe::category;
         StreamCodec var10005 = Ingredient.CONTENTS_STREAM_CODEC;
         Function var10006 = SingleItemRecipe::input;
         StreamCodec var10007 = ItemStack.STREAM_CODEC;
         Function var10008 = SingleItemRecipe::result;
         StreamCodec var10009 = ByteBufCodecs.FLOAT;
         Function var10010 = AbstractCookingRecipe::experience;
         StreamCodec var10011 = ByteBufCodecs.INT;
         Function var10012 = AbstractCookingRecipe::cookingTime;
         Objects.requireNonNull(var1);
         this.streamCodec = StreamCodec.composite(var10001, var10002, var10003, var10004, var10005, var10006, var10007, var10008, var10009, var10010, var10011, var10012, var1::create);
      }

      public MapCodec<T> codec() {
         return this.codec;
      }

      public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
         return this.streamCodec;
      }
   }

   @FunctionalInterface
   public interface Factory<T extends AbstractCookingRecipe> {
      T create(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6);
   }
}
