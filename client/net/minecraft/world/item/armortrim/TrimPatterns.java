package net.minecraft.world.item.armortrim;

import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TrimPatterns {
   public static final ResourceKey<TrimPattern> SENTRY = registryKey("sentry");
   public static final ResourceKey<TrimPattern> DUNE = registryKey("dune");
   public static final ResourceKey<TrimPattern> COAST = registryKey("coast");
   public static final ResourceKey<TrimPattern> WILD = registryKey("wild");
   public static final ResourceKey<TrimPattern> WARD = registryKey("ward");
   public static final ResourceKey<TrimPattern> EYE = registryKey("eye");
   public static final ResourceKey<TrimPattern> VEX = registryKey("vex");
   public static final ResourceKey<TrimPattern> TIDE = registryKey("tide");
   public static final ResourceKey<TrimPattern> SNOUT = registryKey("snout");
   public static final ResourceKey<TrimPattern> RIB = registryKey("rib");
   public static final ResourceKey<TrimPattern> SPIRE = registryKey("spire");
   public static final ResourceKey<TrimPattern> WAYFINDER = registryKey("wayfinder");
   public static final ResourceKey<TrimPattern> SHAPER = registryKey("shaper");
   public static final ResourceKey<TrimPattern> SILENCE = registryKey("silence");
   public static final ResourceKey<TrimPattern> RAISER = registryKey("raiser");
   public static final ResourceKey<TrimPattern> HOST = registryKey("host");

   public TrimPatterns() {
      super();
   }

   public static void bootstrap(BootstapContext<TrimPattern> var0) {
      register(var0, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, SENTRY);
      register(var0, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, DUNE);
      register(var0, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, COAST);
      register(var0, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, WILD);
      register(var0, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, WARD);
      register(var0, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, EYE);
      register(var0, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, VEX);
      register(var0, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, TIDE);
      register(var0, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, SNOUT);
      register(var0, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, RIB);
      register(var0, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, SPIRE);
      register(var0, Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE, WAYFINDER);
      register(var0, Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, SHAPER);
      register(var0, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, SILENCE);
      register(var0, Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, RAISER);
      register(var0, Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, HOST);
   }

   public static Optional<Holder.Reference<TrimPattern>> getFromTemplate(RegistryAccess var0, ItemStack var1) {
      return var0.registryOrThrow(Registries.TRIM_PATTERN).holders().filter(var1x -> var1.is(var1x.value().templateItem())).findFirst();
   }

   private static void register(BootstapContext<TrimPattern> var0, Item var1, ResourceKey<TrimPattern> var2) {
      TrimPattern var3 = new TrimPattern(
         var2.location(), BuiltInRegistries.ITEM.wrapAsHolder(var1), Component.translatable(Util.makeDescriptionId("trim_pattern", var2.location()))
      );
      var0.register(var2, var3);
   }

   private static ResourceKey<TrimPattern> registryKey(String var0) {
      return ResourceKey.create(Registries.TRIM_PATTERN, new ResourceLocation(var0));
   }
}
