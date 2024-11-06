package net.minecraft.client.resources.model;

import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class EquipmentAssetManager extends SimpleJsonResourceReloadListener<EquipmentClientInfo> {
   public static final EquipmentClientInfo MISSING = new EquipmentClientInfo(Map.of());
   private static final FileToIdConverter ASSET_LISTER = FileToIdConverter.json("equipment");
   private Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssets = Map.of();

   public EquipmentAssetManager() {
      super(EquipmentClientInfo.CODEC, ASSET_LISTER);
   }

   protected void apply(Map<ResourceLocation, EquipmentClientInfo> var1, ResourceManager var2, ProfilerFiller var3) {
      this.equipmentAssets = (Map)var1.entrySet().stream().collect(Collectors.toUnmodifiableMap((var0) -> {
         return ResourceKey.create(EquipmentAssets.ROOT_ID, (ResourceLocation)var0.getKey());
      }, Map.Entry::getValue));
   }

   public EquipmentClientInfo get(ResourceKey<EquipmentAsset> var1) {
      return (EquipmentClientInfo)this.equipmentAssets.getOrDefault(var1, MISSING);
   }
}
