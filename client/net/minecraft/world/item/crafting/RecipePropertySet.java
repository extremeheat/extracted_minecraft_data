package net.minecraft.world.item.crafting;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class RecipePropertySet {
   public static final ResourceKey<? extends Registry<RecipePropertySet>> TYPE_KEY = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("recipe_property_set"));
   public static final ResourceKey<RecipePropertySet> SMITHING_BASE = registerVanilla("smithing_base");
   public static final ResourceKey<RecipePropertySet> SMITHING_TEMPLATE = registerVanilla("smithing_template");
   public static final ResourceKey<RecipePropertySet> SMITHING_ADDITION = registerVanilla("smithing_addition");
   public static final ResourceKey<RecipePropertySet> FURNACE_INPUT = registerVanilla("furnace_input");
   public static final ResourceKey<RecipePropertySet> BLAST_FURNACE_INPUT = registerVanilla("blast_furnace_input");
   public static final ResourceKey<RecipePropertySet> SMOKER_INPUT = registerVanilla("smoker_input");
   public static final ResourceKey<RecipePropertySet> CAMPFIRE_INPUT = registerVanilla("campfire_input");
   public static final StreamCodec<RegistryFriendlyByteBuf, RecipePropertySet> STREAM_CODEC;
   public static final RecipePropertySet EMPTY;
   private final Set<Holder<Item>> items;

   private RecipePropertySet(Set<Holder<Item>> var1) {
      super();
      this.items = var1;
   }

   private static ResourceKey<RecipePropertySet> registerVanilla(String var0) {
      return ResourceKey.create(TYPE_KEY, ResourceLocation.withDefaultNamespace(var0));
   }

   public boolean test(ItemStack var1) {
      return this.items.contains(var1.getItemHolder());
   }

   static RecipePropertySet create(Collection<Ingredient> var0) {
      Set var1 = (Set)var0.stream().flatMap((var0x) -> {
         return var0x.items().stream();
      }).collect(Collectors.toUnmodifiableSet());
      return new RecipePropertySet(var1);
   }

   static {
      STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.ITEM).apply(ByteBufCodecs.list()).map((var0) -> {
         return new RecipePropertySet(Set.copyOf(var0));
      }, (var0) -> {
         return List.copyOf(var0.items);
      });
      EMPTY = new RecipePropertySet(Set.of());
   }
}
