package net.minecraft.client.data.models;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.resources.WaypointStyle;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.waypoints.WaypointStyleAsset;
import net.minecraft.world.waypoints.WaypointStyleAssets;

public class WaypointStyleProvider implements DataProvider {
   private final PackOutput.PathProvider pathProvider;

   public WaypointStyleProvider(PackOutput var1) {
      super();
      this.pathProvider = var1.createPathProvider(PackOutput.Target.RESOURCE_PACK, "waypoint_style");
   }

   private static void bootstrap(BiConsumer<ResourceKey<WaypointStyleAsset>, WaypointStyle> var0) {
      var0.accept(WaypointStyleAssets.DEFAULT, new WaypointStyle(128, 332, List.of(ResourceLocation.withDefaultNamespace("default_0"), ResourceLocation.withDefaultNamespace("default_1"), ResourceLocation.withDefaultNamespace("default_2"), ResourceLocation.withDefaultNamespace("default_3"))));
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      HashMap var2 = new HashMap();
      bootstrap((var1x, var2x) -> {
         if (var2.putIfAbsent(var1x, var2x) != null) {
            throw new IllegalStateException("Tried to register waypoint style twice for id: " + String.valueOf(var1x));
         }
      });
      Codec var10001 = WaypointStyle.CODEC;
      PackOutput.PathProvider var10002 = this.pathProvider;
      Objects.requireNonNull(var10002);
      return DataProvider.saveAll(var1, var10001, (Function)(var10002::json), var2);
   }

   public String getName() {
      return "Waypoint Style Definitions";
   }
}
