package net.minecraft.client.data.models;

import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Iterator;
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
      var0.accept(EquipmentAssets.IRON, humanoidAndHorse("iron"));
      var0.accept(EquipmentAssets.GOLD, humanoidAndHorse("gold"));
      var0.accept(EquipmentAssets.DIAMOND, humanoidAndHorse("diamond"));
      var0.accept(EquipmentAssets.TURTLE_SCUTE, EquipmentClientInfo.builder().addMainHumanoidLayer(ResourceLocation.withDefaultNamespace("turtle_scute"), false).build());
      var0.accept(EquipmentAssets.NETHERITE, onlyHumanoid("netherite"));
      var0.accept(EquipmentAssets.ARMADILLO_SCUTE, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.WOLF_BODY, EquipmentClientInfo.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute"), false)).addLayers(EquipmentClientInfo.LayerType.WOLF_BODY, EquipmentClientInfo.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute_overlay"), true)).build());
      var0.accept(EquipmentAssets.ELYTRA, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.WINGS, new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace("elytra"), Optional.empty(), true)).build());
      Iterator var1 = EquipmentAssets.CARPETS.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry var2 = (Map.Entry)var1.next();
         DyeColor var3 = (DyeColor)var2.getKey();
         ResourceKey var4 = (ResourceKey)var2.getValue();
         var0.accept(var4, EquipmentClientInfo.builder().addLayers(EquipmentClientInfo.LayerType.LLAMA_BODY, new EquipmentClientInfo.Layer(ResourceLocation.withDefaultNamespace(var3.getSerializedName()))).build());
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
      return DataProvider.saveAll(var1, (Codec)var10001, (Function)(var10002::json), var2);
   }

   public String getName() {
      return "Equipment Asset Definitions";
   }
}
