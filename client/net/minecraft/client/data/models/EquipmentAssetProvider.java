package net.minecraft.client.data.models;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class EquipmentAssetProvider implements DataProvider {
   private final PackOutput.PathProvider pathProvider;

   public EquipmentAssetProvider(PackOutput var1) {
      super();
      this.pathProvider = var1.createPathProvider(PackOutput.Target.RESOURCE_PACK, "equipment");
   }

   private static void bootstrap(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> var0) {
      var0.accept(EquipmentAssets.LEATHER, EquipmentClientInfo.builder().addHumanoidLayers(ResourceLocation.withDefaultNamespace("leather"), true).addHumanoidLayers(ResourceLocation.withDefaultNamespace("leather_overlay"), false).addLayers(EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(ResourceLocation.withDefaultNamespace("leather"), true)).build());
      var0.accept(EquipmentAssets.CHAINMAIL, onlyHumanoid("chainmail"));
      var0.accept(EquipmentAssets.COPPER, humanoidAndHorse("copper"));
      var0.accept(EquipmentAssets.IRON, humanoidAndHorse("iron"));
      var0.accept(EquipmentAssets.GOLD, humanoidAndHorse("gold"));
      var0.accept(EquipmentAssets.DIAMOND, humanoidAndHorse("diamond"));
      var0.accept(EquipmentAssets.TURTLE_SCUTE, EquipmentClientInfo.builder().addMainHumanoidLayer(ResourceLocation.withDefaultNamespace("turtle_scute"), false).build());
      var0.accept(EquipmentAssets.NETHERITE, onlyHumanoid("netherite"));
      var0.accept(EquipmentAssets.ARMADILLO_SCUTE, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.WOLF_BODY, EquipmentClientInfo.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute"), false)).addLayers(EquipmentClientInfo.LayerType.WOLF_BODY, EquipmentClientInfo.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute_overlay"), true)).build());
      var0.accept(EquipmentAssets.ELYTRA, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.WINGS, new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace("elytra"), Optional.empty(), true)).build());
      EquipmentClientInfo.Layer var1 = new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace("saddle"));
      var0.accept(EquipmentAssets.SADDLE, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.PIG_SADDLE, var1).addLayers(EquipmentClientInfo.LayerType.STRIDER_SADDLE, var1).addLayers(EquipmentClientInfo.LayerType.CAMEL_SADDLE, var1).addLayers(EquipmentClientInfo.LayerType.HORSE_SADDLE, var1).addLayers(EquipmentClientInfo.LayerType.DONKEY_SADDLE, var1).addLayers(EquipmentClientInfo.LayerType.MULE_SADDLE, var1).addLayers(EquipmentClientInfo.LayerType.SKELETON_HORSE_SADDLE, var1).addLayers(EquipmentClientInfo.LayerType.ZOMBIE_HORSE_SADDLE, var1).build());

      for(Map.Entry var3 : EquipmentAssets.HARNESSES.entrySet()) {
         DyeColor var4 = (DyeColor)var3.getKey();
         ResourceKey var5 = (ResourceKey)var3.getValue();
         var0.accept(var5, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.HAPPY_GHAST_BODY, EquipmentClientInfo.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace(var4.getSerializedName() + "_harness"), false)).build());
      }

      for(Map.Entry var7 : EquipmentAssets.CARPETS.entrySet()) {
         DyeColor var8 = (DyeColor)var7.getKey();
         ResourceKey var9 = (ResourceKey)var7.getValue();
         var0.accept(var9, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.LLAMA_BODY, new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace(var8.getSerializedName()))).build());
      }

      var0.accept(EquipmentAssets.TRADER_LLAMA, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.LLAMA_BODY, new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace("trader_llama"))).build());
   }

   private static EquipmentClientInfo onlyHumanoid(String var0) {
      return EquipmentClientInfo.builder().addHumanoidLayers(ResourceLocation.withDefaultNamespace(var0)).build();
   }

   private static EquipmentClientInfo humanoidAndHorse(String var0) {
      return EquipmentClientInfo.builder().addHumanoidLayers(ResourceLocation.withDefaultNamespace(var0)).addLayers(EquipmentClientInfo.LayerType.HORSE_BODY, EquipmentClientInfo.Layer.leatherDyeable(ResourceLocation.withDefaultNamespace(var0), false)).build();
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      HashMap var2 = new HashMap();
      bootstrap((var1x, var2x) -> {
         if (var2.putIfAbsent(var1x, var2x) != null) {
            throw new IllegalStateException("Tried to register equipment asset twice for id: " + String.valueOf(var1x));
         }
      });
      Codec var10001 = EquipmentClientInfo.CODEC;
      PackOutput.PathProvider var10002 = this.pathProvider;
      Objects.requireNonNull(var10002);
      return DataProvider.saveAll(var1, var10001, (Function)(var10002::json), var2);
   }

   public String getName() {
      return "Equipment Asset Definitions";
   }
}
