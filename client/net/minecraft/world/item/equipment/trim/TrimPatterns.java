package net.minecraft.world.item.equipment.trim;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;

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
   public static final ResourceKey<TrimPattern> FLOW = registryKey("flow");
   public static final ResourceKey<TrimPattern> BOLT = registryKey("bolt");

   public TrimPatterns() {
      super();
   }

   public static void bootstrap(BootstrapContext<TrimPattern> var0) {
      register(var0, SENTRY);
      register(var0, DUNE);
      register(var0, COAST);
      register(var0, WILD);
      register(var0, WARD);
      register(var0, EYE);
      register(var0, VEX);
      register(var0, TIDE);
      register(var0, SNOUT);
      register(var0, RIB);
      register(var0, SPIRE);
      register(var0, WAYFINDER);
      register(var0, SHAPER);
      register(var0, SILENCE);
      register(var0, RAISER);
      register(var0, HOST);
      register(var0, FLOW);
      register(var0, BOLT);
   }

   public static void register(BootstrapContext<TrimPattern> var0, ResourceKey<TrimPattern> var1) {
      TrimPattern var2 = new TrimPattern(defaultAssetId(var1), Component.translatable(Util.makeDescriptionId("trim_pattern", var1.identifier())), false);
      var0.register(var1, var2);
   }

   private static ResourceKey<TrimPattern> registryKey(String var0) {
      return ResourceKey.create(Registries.TRIM_PATTERN, Identifier.withDefaultNamespace(var0));
   }

   public static Identifier defaultAssetId(ResourceKey<TrimPattern> var0) {
      return var0.identifier();
   }
}
