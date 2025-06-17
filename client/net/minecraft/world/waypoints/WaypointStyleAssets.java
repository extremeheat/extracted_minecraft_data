package net.minecraft.world.waypoints;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface WaypointStyleAssets {
   ResourceKey<? extends Registry<WaypointStyleAsset>> ROOT_ID = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("waypoint_style_asset"));
   ResourceKey<WaypointStyleAsset> DEFAULT = createId("default");
   ResourceKey<WaypointStyleAsset> BOWTIE = createId("bowtie");

   static ResourceKey<WaypointStyleAsset> createId(String var0) {
      return ResourceKey.create(ROOT_ID, ResourceLocation.withDefaultNamespace(var0));
   }
}
