package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelZombieVillager;

public class LayerVillagerArmor extends LayerBipedArmor {
   public LayerVillagerArmor(RenderLivingBase<?> var1) {
      super(var1);
   }

   protected void func_177177_a() {
      this.field_177189_c = new ModelZombieVillager(0.5F, 0.0F, true);
      this.field_177186_d = new ModelZombieVillager(1.0F, 0.0F, true);
   }
}
