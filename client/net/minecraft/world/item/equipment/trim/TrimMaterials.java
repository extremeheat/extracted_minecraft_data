package net.minecraft.world.item.equipment.trim;

import java.util.Map;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class TrimMaterials {
   public static final ResourceKey<TrimMaterial> QUARTZ = registryKey("quartz");
   public static final ResourceKey<TrimMaterial> IRON = registryKey("iron");
   public static final ResourceKey<TrimMaterial> NETHERITE = registryKey("netherite");
   public static final ResourceKey<TrimMaterial> REDSTONE = registryKey("redstone");
   public static final ResourceKey<TrimMaterial> COPPER = registryKey("copper");
   public static final ResourceKey<TrimMaterial> GOLD = registryKey("gold");
   public static final ResourceKey<TrimMaterial> EMERALD = registryKey("emerald");
   public static final ResourceKey<TrimMaterial> DIAMOND = registryKey("diamond");
   public static final ResourceKey<TrimMaterial> LAPIS = registryKey("lapis");
   public static final ResourceKey<TrimMaterial> AMETHYST = registryKey("amethyst");
   public static final ResourceKey<TrimMaterial> RESIN = registryKey("resin");

   public TrimMaterials() {
      super();
   }

   public static void bootstrap(BootstrapContext<TrimMaterial> var0) {
      register(var0, QUARTZ, Items.QUARTZ, Style.EMPTY.withColor(14931140));
      register(var0, IRON, Items.IRON_INGOT, Style.EMPTY.withColor(15527148), Map.of(EquipmentAssets.IRON, "iron_darker"));
      register(var0, NETHERITE, Items.NETHERITE_INGOT, Style.EMPTY.withColor(6445145), Map.of(EquipmentAssets.NETHERITE, "netherite_darker"));
      register(var0, REDSTONE, Items.REDSTONE, Style.EMPTY.withColor(9901575));
      register(var0, COPPER, Items.COPPER_INGOT, Style.EMPTY.withColor(11823181));
      register(var0, GOLD, Items.GOLD_INGOT, Style.EMPTY.withColor(14594349), Map.of(EquipmentAssets.GOLD, "gold_darker"));
      register(var0, EMERALD, Items.EMERALD, Style.EMPTY.withColor(1155126));
      register(var0, DIAMOND, Items.DIAMOND, Style.EMPTY.withColor(7269586), Map.of(EquipmentAssets.DIAMOND, "diamond_darker"));
      register(var0, LAPIS, Items.LAPIS_LAZULI, Style.EMPTY.withColor(4288151));
      register(var0, AMETHYST, Items.AMETHYST_SHARD, Style.EMPTY.withColor(10116294));
      register(var0, RESIN, Items.RESIN_BRICK, Style.EMPTY.withColor(16545810));
   }

   public static Optional<Holder.Reference<TrimMaterial>> getFromIngredient(HolderLookup.Provider var0, ItemStack var1) {
      return var0.lookupOrThrow(Registries.TRIM_MATERIAL).listElements().filter((var1x) -> {
         return var1.is(((TrimMaterial)var1x.value()).ingredient());
      }).findFirst();
   }

   private static void register(BootstrapContext<TrimMaterial> var0, ResourceKey<TrimMaterial> var1, Item var2, Style var3) {
      register(var0, var1, var2, var3, Map.of());
   }

   private static void register(BootstrapContext<TrimMaterial> var0, ResourceKey<TrimMaterial> var1, Item var2, Style var3, Map<ResourceKey<EquipmentAsset>, String> var4) {
      TrimMaterial var5 = TrimMaterial.create(var1.location().getPath(), var2, Component.translatable(Util.makeDescriptionId("trim_material", var1.location())).withStyle(var3), var4);
      var0.register(var1, var5);
   }

   private static ResourceKey<TrimMaterial> registryKey(String var0) {
      return ResourceKey.create(Registries.TRIM_MATERIAL, ResourceLocation.withDefaultNamespace(var0));
   }
}
