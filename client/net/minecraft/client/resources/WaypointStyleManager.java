package net.minecraft.client.resources;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.waypoints.WaypointStyleAsset;
import net.minecraft.world.waypoints.WaypointStyleAssets;

public class WaypointStyleManager extends SimpleJsonResourceReloadListener<WaypointStyle> {
   private static final FileToIdConverter ASSET_LISTER = FileToIdConverter.json("waypoint_style");
   private static final WaypointStyle MISSING = new WaypointStyle(0, 1, List.of(MissingTextureAtlasSprite.getLocation()));
   private Map<ResourceKey<WaypointStyleAsset>, WaypointStyle> waypointStyles = Map.of();

   public WaypointStyleManager() {
      super(WaypointStyle.CODEC, ASSET_LISTER);
   }

   protected void apply(Map<Identifier, WaypointStyle> var1, ResourceManager var2, ProfilerFiller var3) {
      this.waypointStyles = (Map)var1.entrySet().stream().collect(Collectors.toUnmodifiableMap((var0) -> ResourceKey.create(WaypointStyleAssets.ROOT_ID, (Identifier)var0.getKey()), Map.Entry::getValue));
   }

   public WaypointStyle get(ResourceKey<WaypointStyleAsset> var1) {
      return (WaypointStyle)this.waypointStyles.getOrDefault(var1, MISSING);
   }
}
