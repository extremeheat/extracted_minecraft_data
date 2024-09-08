package net.minecraft.client.resources.model;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.equipment.EquipmentModel;

public class EquipmentModelSet extends SimpleJsonResourceReloadListener<EquipmentModel> {
   public static final EquipmentModel MISSING_MODEL = new EquipmentModel(Map.of());
   private Map<ResourceLocation, EquipmentModel> models = Map.of();

   public EquipmentModelSet() {
      super(EquipmentModel.CODEC, "models/equipment");
   }

   protected void apply(Map<ResourceLocation, EquipmentModel> var1, ResourceManager var2, ProfilerFiller var3) {
      this.models = Map.copyOf(var1);
   }

   public EquipmentModel get(ResourceLocation var1) {
      return this.models.getOrDefault(var1, MISSING_MODEL);
   }
}
