package net.minecraft.world.item.equipment;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public interface EquipmentModels {
   ResourceLocation LEATHER = ResourceLocation.withDefaultNamespace("leather");
   ResourceLocation CHAINMAIL = ResourceLocation.withDefaultNamespace("chainmail");
   ResourceLocation IRON = ResourceLocation.withDefaultNamespace("iron");
   ResourceLocation GOLD = ResourceLocation.withDefaultNamespace("gold");
   ResourceLocation DIAMOND = ResourceLocation.withDefaultNamespace("diamond");
   ResourceLocation TURTLE_SCUTE = ResourceLocation.withDefaultNamespace("turtle_scute");
   ResourceLocation NETHERITE = ResourceLocation.withDefaultNamespace("netherite");
   ResourceLocation ARMADILLO_SCUTE = ResourceLocation.withDefaultNamespace("armadillo_scute");
   ResourceLocation ELYTRA = ResourceLocation.withDefaultNamespace("elytra");
   Map<DyeColor, ResourceLocation> CARPETS = Util.makeEnumMap(DyeColor.class, (var0) -> {
      return ResourceLocation.withDefaultNamespace(var0.getSerializedName() + "_carpet");
   });
   ResourceLocation TRADER_LLAMA = ResourceLocation.withDefaultNamespace("trader_llama");

   static void bootstrap(BiConsumer<ResourceLocation, EquipmentModel> var0) {
      var0.accept(LEATHER, EquipmentModel.builder().addHumanoidLayers(ResourceLocation.withDefaultNamespace("leather"), true).addHumanoidLayers(ResourceLocation.withDefaultNamespace("leather_overlay"), false).addLayers(EquipmentModel.LayerType.HORSE_BODY, EquipmentModel.Layer.leatherDyeable(ResourceLocation.withDefaultNamespace("leather"), true)).build());
      var0.accept(CHAINMAIL, onlyHumanoid("chainmail"));
      var0.accept(IRON, humanoidAndHorse("iron"));
      var0.accept(GOLD, humanoidAndHorse("gold"));
      var0.accept(DIAMOND, humanoidAndHorse("diamond"));
      var0.accept(TURTLE_SCUTE, EquipmentModel.builder().addMainHumanoidLayer(ResourceLocation.withDefaultNamespace("turtle_scute"), false).build());
      var0.accept(NETHERITE, onlyHumanoid("netherite"));
      var0.accept(ARMADILLO_SCUTE, EquipmentModel.builder().addLayers(EquipmentModel.LayerType.WOLF_BODY, EquipmentModel.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute"), false)).addLayers(EquipmentModel.LayerType.WOLF_BODY, EquipmentModel.Layer.onlyIfDyed(ResourceLocation.withDefaultNamespace("armadillo_scute_overlay"), true)).build());
      var0.accept(ELYTRA, EquipmentModel.builder().addLayers(EquipmentModel.LayerType.WINGS, new EquipmentModel.Layer(ResourceLocation.withDefaultNamespace("elytra"), Optional.empty(), true)).build());
      Iterator var1 = CARPETS.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry var2 = (Map.Entry)var1.next();
         DyeColor var3 = (DyeColor)var2.getKey();
         ResourceLocation var4 = (ResourceLocation)var2.getValue();
         var0.accept(var4, EquipmentModel.builder().addLayers(EquipmentModel.LayerType.LLAMA_BODY, new EquipmentModel.Layer(ResourceLocation.withDefaultNamespace(var3.getSerializedName()))).build());
      }

      var0.accept(TRADER_LLAMA, EquipmentModel.builder().addLayers(EquipmentModel.LayerType.LLAMA_BODY, new EquipmentModel.Layer(ResourceLocation.withDefaultNamespace("trader_llama"))).build());
   }

   private static EquipmentModel onlyHumanoid(String var0) {
      return EquipmentModel.builder().addHumanoidLayers(ResourceLocation.withDefaultNamespace(var0)).build();
   }

   private static EquipmentModel humanoidAndHorse(String var0) {
      return EquipmentModel.builder().addHumanoidLayers(ResourceLocation.withDefaultNamespace(var0)).addLayers(EquipmentModel.LayerType.HORSE_BODY, EquipmentModel.Layer.leatherDyeable(ResourceLocation.withDefaultNamespace(var0), false)).build();
   }
}
