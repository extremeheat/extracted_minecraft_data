package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.EnumStreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;

public enum DyeColor implements StringRepresentable {
   WHITE(0, "white"),
   ORANGE(1, "orange"),
   MAGENTA(2, "magenta"),
   LIGHT_BLUE(3, "light_blue"),
   YELLOW(4, "yellow"),
   LIME(5, "lime"),
   PINK(6, "pink"),
   GRAY(7, "gray"),
   LIGHT_GRAY(8, "light_gray"),
   CYAN(9, "cyan"),
   PURPLE(10, "purple"),
   BLUE(11, "blue"),
   BROWN(12, "brown"),
   GREEN(13, "green"),
   RED(14, "red"),
   BLACK(15, "black");

   public static final List<DyeColor> VALUES = List.of(values());
   public static final StringRepresentable.EnumCodec<DyeColor> CODEC = StringRepresentable.<DyeColor>fromEnum(DyeColor::values);
   public static final EnumStreamCodec<DyeColor> STREAM_CODEC = ByteBufCodecs.<DyeColor>enumCodec(DyeColor.class, DyeColor::getId);
   /** @deprecated */
   @Deprecated
   public static final Codec<DyeColor> LEGACY_ID_CODEC = Codec.BYTE.xmap(DyeColor::byId, (color) -> (byte)color.id);
   private final int id;
   private final String name;

   private DyeColor(final int id, final String name) {
      this.id = id;
      this.name = name;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public static DyeColor byId(final int id) {
      return STREAM_CODEC.byId(id);
   }

   @Contract("_,!null->!null;_,null->_")
   public static @Nullable DyeColor byName(final String name, final @Nullable DyeColor def) {
      DyeColor result = CODEC.byName(name);
      return result != null ? result : def;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   public static DyeColor getMixedColor(final ServerLevel level, final DyeColor dyeColor1, final DyeColor dyeColor2) {
      DyeColor mixedColor = findColorMixInRecipes(level, dyeColor1, dyeColor2);
      if (mixedColor != null) {
         return mixedColor;
      } else {
         return level.getRandom().nextBoolean() ? dyeColor1 : dyeColor2;
      }
   }

   private static @Nullable DyeColor findColorMixInRecipes(final ServerLevel level, final DyeColor dyeColor1, final DyeColor dyeColor2) {
      DataComponentLookup<Item> itemComponents = level.registryAccess().lookupOrThrow(Registries.ITEM).componentLookup();
      Collection<Holder<Item>> dye1Items = itemComponents.findAll(DataComponents.DYE, dyeColor1);
      if (dye1Items.isEmpty()) {
         return null;
      } else {
         Collection<Holder<Item>> dye2Items = itemComponents.findAll(DataComponents.DYE, dyeColor2);
         if (dye2Items.isEmpty()) {
            return null;
         } else {
            for(Holder<Item> dye1Item : dye1Items) {
               for(Holder<Item> dye2Item : dye2Items) {
                  CraftingInput input = CraftingInput.of(2, 1, List.of(new ItemStack(dye1Item), new ItemStack(dye2Item)));
                  Optional<RecipeHolder<CraftingRecipe>> foundRecipe = level.recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, level);
                  if (foundRecipe.isPresent()) {
                     ItemStack craftingResult = ((CraftingRecipe)((RecipeHolder)foundRecipe.get()).value()).assemble(input);
                     DyeColor craftedDyeColor = (DyeColor)craftingResult.get(DataComponents.DYE);
                     if (craftedDyeColor != null) {
                        return craftedDyeColor;
                     }
                  }
               }
            }

            return null;
         }
      }
   }

   // $FF: synthetic method
   private static DyeColor[] $values() {
      return new DyeColor[]{WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK};
   }
}
