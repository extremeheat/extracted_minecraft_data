package net.minecraft.data.models;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.equipment.EquipmentModel;
import net.minecraft.world.item.equipment.EquipmentModels;

public class EquipmentModelProvider implements DataProvider {
   private final PackOutput.PathProvider pathProvider;

   public EquipmentModelProvider(PackOutput var1) {
      super();
      this.pathProvider = var1.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/equipment");
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      HashMap var2 = new HashMap();
      EquipmentModels.bootstrap((var1x, var2x) -> {
         if (var2.putIfAbsent(var1x, var2x) != null) {
            throw new IllegalStateException("Tried to register equipment model twice for id: " + String.valueOf(var1x));
         }
      });
      return DataProvider.saveAll(var1, EquipmentModel.CODEC, this.pathProvider, var2);
   }

   public String getName() {
      return "Equipment Model Definitions";
   }
}
