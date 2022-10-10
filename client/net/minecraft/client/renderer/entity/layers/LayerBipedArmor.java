package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.inventory.EntityEquipmentSlot;

public class LayerBipedArmor extends LayerArmorBase<ModelBiped> {
   public LayerBipedArmor(RenderLivingBase<?> var1) {
      super(var1);
   }

   protected void func_177177_a() {
      this.field_177189_c = new ModelBiped(0.5F);
      this.field_177186_d = new ModelBiped(1.0F);
   }

   protected void func_188359_a(ModelBiped var1, EntityEquipmentSlot var2) {
      this.func_177194_a(var1);
      switch(var2) {
      case HEAD:
         var1.field_78116_c.field_78806_j = true;
         var1.field_178720_f.field_78806_j = true;
         break;
      case CHEST:
         var1.field_78115_e.field_78806_j = true;
         var1.field_178723_h.field_78806_j = true;
         var1.field_178724_i.field_78806_j = true;
         break;
      case LEGS:
         var1.field_78115_e.field_78806_j = true;
         var1.field_178721_j.field_78806_j = true;
         var1.field_178722_k.field_78806_j = true;
         break;
      case FEET:
         var1.field_178721_j.field_78806_j = true;
         var1.field_178722_k.field_78806_j = true;
      }

   }

   protected void func_177194_a(ModelBiped var1) {
      var1.func_178719_a(false);
   }
}
