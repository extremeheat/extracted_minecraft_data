package net.minecraft.world.item.equipment;

import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public interface EquipmentAssets {
   ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID = ResourceKey.createRegistryKey(ResourceLocation.withDefaultNamespace("equipment_asset"));
   ResourceKey<EquipmentAsset> LEATHER = createId("leather");
   ResourceKey<EquipmentAsset> CHAINMAIL = createId("chainmail");
   ResourceKey<EquipmentAsset> IRON = createId("iron");
   ResourceKey<EquipmentAsset> GOLD = createId("gold");
   ResourceKey<EquipmentAsset> DIAMOND = createId("diamond");
   ResourceKey<EquipmentAsset> TURTLE_SCUTE = createId("turtle_scute");
   ResourceKey<EquipmentAsset> NETHERITE = createId("netherite");
   ResourceKey<EquipmentAsset> ARMADILLO_SCUTE = createId("armadillo_scute");
   ResourceKey<EquipmentAsset> ELYTRA = createId("elytra");
   Map<DyeColor, ResourceKey<EquipmentAsset>> CARPETS = Util.<DyeColor, ResourceKey<EquipmentAsset>>makeEnumMap(DyeColor.class, (var0) -> createId(var0.getSerializedName() + "_carpet"));
   ResourceKey<EquipmentAsset> TRADER_LLAMA = createId("trader_llama");

   static ResourceKey<EquipmentAsset> createId(String var0) {
      return ResourceKey.create(ROOT_ID, ResourceLocation.withDefaultNamespace(var0));
   }
}
