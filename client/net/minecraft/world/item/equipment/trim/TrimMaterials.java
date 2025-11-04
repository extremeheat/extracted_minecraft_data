package net.minecraft.world.item.equipment.trim;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ProvidesTrimMaterial;

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
      register(var0, QUARTZ, Style.EMPTY.withColor(14931140), MaterialAssetGroup.QUARTZ);
      register(var0, IRON, Style.EMPTY.withColor(15527148), MaterialAssetGroup.IRON);
      register(var0, NETHERITE, Style.EMPTY.withColor(6445145), MaterialAssetGroup.NETHERITE);
      register(var0, REDSTONE, Style.EMPTY.withColor(9901575), MaterialAssetGroup.REDSTONE);
      register(var0, COPPER, Style.EMPTY.withColor(11823181), MaterialAssetGroup.COPPER);
      register(var0, GOLD, Style.EMPTY.withColor(14594349), MaterialAssetGroup.GOLD);
      register(var0, EMERALD, Style.EMPTY.withColor(1155126), MaterialAssetGroup.EMERALD);
      register(var0, DIAMOND, Style.EMPTY.withColor(7269586), MaterialAssetGroup.DIAMOND);
      register(var0, LAPIS, Style.EMPTY.withColor(4288151), MaterialAssetGroup.LAPIS);
      register(var0, AMETHYST, Style.EMPTY.withColor(10116294), MaterialAssetGroup.AMETHYST);
      register(var0, RESIN, Style.EMPTY.withColor(16545810), MaterialAssetGroup.RESIN);
   }

   public static Optional<Holder<TrimMaterial>> getFromIngredient(HolderLookup.Provider var0, ItemStack var1) {
      ProvidesTrimMaterial var2 = (ProvidesTrimMaterial)var1.get(DataComponents.PROVIDES_TRIM_MATERIAL);
      return var2 != null ? var2.unwrap(var0) : Optional.empty();
   }

   private static void register(BootstrapContext<TrimMaterial> var0, ResourceKey<TrimMaterial> var1, Style var2, MaterialAssetGroup var3) {
      MutableComponent var4 = Component.translatable(Util.makeDescriptionId("trim_material", var1.identifier())).withStyle(var2);
      var0.register(var1, new TrimMaterial(var3, var4));
   }

   private static ResourceKey<TrimMaterial> registryKey(String var0) {
      return ResourceKey.create(Registries.TRIM_MATERIAL, Identifier.withDefaultNamespace(var0));
   }
}
