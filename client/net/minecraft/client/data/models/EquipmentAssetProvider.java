package net.minecraft.client.data.models;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.TrimMaterials;

public class EquipmentAssetProvider implements DataProvider {
   private final PackOutput.PathProvider pathProvider;

   public EquipmentAssetProvider(final PackOutput output) {
      super();
      this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
   }

   private static void bootstrap(final BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> consumer) {
      consumer.accept(EquipmentAssets.LEATHER, EquipmentClientInfo.builder().addHumanoidLayers(Identifier.withDefaultNamespace("leather"), true).addHumanoidLayers(Identifier.withDefaultNamespace("leather_overlay"), false).addLayers(EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(Identifier.withDefaultNamespace("leather"), true), EquipmentClientInfo.Layer.leatherDyeable(Identifier.withDefaultNamespace("leather_overlay"), false)).build());
      consumer.accept(EquipmentAssets.CHAINMAIL, onlyHumanoid("chainmail").build());
      consumer.accept(EquipmentAssets.COPPER, humanoidAndMountArmor("copper").replaceTrimPalette(TrimMaterials.Palette.COPPER.id(), TrimMaterials.Palette.COPPER_DARKER.id()).build());
      consumer.accept(EquipmentAssets.IRON, humanoidAndMountArmor("iron").replaceTrimPalette(TrimMaterials.Palette.IRON.id(), TrimMaterials.Palette.IRON_DARKER.id()).build());
      consumer.accept(EquipmentAssets.GOLD, humanoidAndMountArmor("gold").replaceTrimPalette(TrimMaterials.Palette.GOLD.id(), TrimMaterials.Palette.GOLD_DARKER.id()).build());
      consumer.accept(EquipmentAssets.DIAMOND, humanoidAndMountArmor("diamond").replaceTrimPalette(TrimMaterials.Palette.DIAMOND.id(), TrimMaterials.Palette.DIAMOND_DARKER.id()).build());
      consumer.accept(EquipmentAssets.TURTLE_SCUTE, EquipmentClientInfo.builder().addMainHumanoidLayer(Identifier.withDefaultNamespace("turtle_scute"), false).build());
      consumer.accept(EquipmentAssets.NETHERITE, humanoidAndMountArmor("netherite").replaceTrimPalette(TrimMaterials.Palette.NETHERITE.id(), TrimMaterials.Palette.NETHERITE_DARKER.id()).build());
      consumer.accept(EquipmentAssets.ARMADILLO_SCUTE, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.WOLF_BODY, EquipmentClientInfo.Layer.onlyIfDyed(Identifier.withDefaultNamespace("armadillo_scute"), false)).addLayers(EquipmentClientInfo.LayerType.WOLF_BODY, EquipmentClientInfo.Layer.onlyIfDyed(Identifier.withDefaultNamespace("armadillo_scute_overlay"), true)).build());
      consumer.accept(EquipmentAssets.ELYTRA, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.WINGS, new EquipmentClientInfo.Layer(Identifier.withDefaultNamespace("elytra"), Optional.empty(), true)).build());
      EquipmentClientInfo.Layer saddleLayer = new EquipmentClientInfo.Layer(Identifier.withDefaultNamespace("saddle"));
      consumer.accept(EquipmentAssets.SADDLE, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.PIG_SADDLE, saddleLayer).addLayers(EquipmentClientInfo.LayerType.STRIDER_SADDLE, saddleLayer).addLayers(EquipmentClientInfo.LayerType.CAMEL_SADDLE, saddleLayer).addLayers(EquipmentClientInfo.LayerType.CAMEL_HUSK_SADDLE, saddleLayer).addLayers(EquipmentClientInfo.LayerType.HORSE_SADDLE, saddleLayer).addLayers(EquipmentClientInfo.LayerType.DONKEY_SADDLE, saddleLayer).addLayers(EquipmentClientInfo.LayerType.MULE_SADDLE, saddleLayer).addLayers(EquipmentClientInfo.LayerType.SKELETON_HORSE_SADDLE, saddleLayer).addLayers(EquipmentClientInfo.LayerType.ZOMBIE_HORSE_SADDLE, saddleLayer).addLayers(EquipmentClientInfo.LayerType.NAUTILUS_SADDLE, saddleLayer).build());

      for(Map.Entry<DyeColor, ResourceKey<EquipmentAsset>> entry : EquipmentAssets.HARNESSES.entrySet()) {
         DyeColor color = (DyeColor)entry.getKey();
         ResourceKey<EquipmentAsset> id = (ResourceKey)entry.getValue();
         consumer.accept(id, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.HAPPY_GHAST_BODY, EquipmentClientInfo.Layer.onlyIfDyed(Identifier.withDefaultNamespace(color.getSerializedName() + "_harness"), false)).build());
      }

      for(Map.Entry<DyeColor, ResourceKey<EquipmentAsset>> entry : EquipmentAssets.CARPETS.entrySet()) {
         DyeColor color = (DyeColor)entry.getKey();
         ResourceKey<EquipmentAsset> id = (ResourceKey)entry.getValue();
         consumer.accept(id, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.LLAMA_BODY, new EquipmentClientInfo.Layer(Identifier.withDefaultNamespace(color.getSerializedName()))).build());
      }

      consumer.accept(EquipmentAssets.TRADER_LLAMA, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.LLAMA_BODY, new EquipmentClientInfo.Layer(Identifier.withDefaultNamespace("trader_llama"))).build());
      consumer.accept(EquipmentAssets.TRADER_LLAMA_BABY, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.LLAMA_BODY, new EquipmentClientInfo.Layer(Identifier.withDefaultNamespace("trader_llama_baby"))).build());
   }

   private static EquipmentClientInfo.Builder onlyHumanoid(final String name) {
      return EquipmentClientInfo.builder().addHumanoidLayers(Identifier.withDefaultNamespace(name));
   }

   private static EquipmentClientInfo.Builder humanoidAndMountArmor(final String name) {
      return EquipmentClientInfo.builder().addHumanoidLayers(Identifier.withDefaultNamespace(name)).addLayers(EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(Identifier.withDefaultNamespace(name), false)).addLayers(EquipmentClientInfo.LayerType.NAUTILUS_BODY, EquipmentClientInfo.Layer.leatherDyeable(Identifier.withDefaultNamespace(name), false));
   }

   public CompletableFuture<?> run(final CachedOutput cache) {
      Map<ResourceKey<EquipmentAsset>, EquipmentClientInfo> equipmentAssets = new HashMap();
      bootstrap((id, asset) -> {
         if (equipmentAssets.putIfAbsent(id, asset) != null) {
            throw new IllegalStateException("Tried to register equipment asset twice for id: " + String.valueOf(id));
         }
      });
      Codec var10001 = EquipmentClientInfo.CODEC;
      PackOutput.PathProvider var10002 = this.pathProvider;
      Objects.requireNonNull(var10002);
      return DataProvider.saveAll(cache, var10001, var10002::json, equipmentAssets);
   }

   public String getName() {
      return "Equipment Asset Definitions";
   }
}
