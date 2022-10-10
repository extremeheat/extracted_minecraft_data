package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.client.renderer.entity.model.ModelZombieVillager;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.util.ResourceLocation;

public class RenderZombieVillager extends RenderBiped<EntityZombieVillager> {
   private static final ResourceLocation field_110864_q = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");
   private static final ResourceLocation field_188330_l = new ResourceLocation("textures/entity/zombie_villager/zombie_farmer.png");
   private static final ResourceLocation field_188331_m = new ResourceLocation("textures/entity/zombie_villager/zombie_librarian.png");
   private static final ResourceLocation field_188332_n = new ResourceLocation("textures/entity/zombie_villager/zombie_priest.png");
   private static final ResourceLocation field_188333_o = new ResourceLocation("textures/entity/zombie_villager/zombie_smith.png");
   private static final ResourceLocation field_188329_p = new ResourceLocation("textures/entity/zombie_villager/zombie_butcher.png");

   public RenderZombieVillager(RenderManager var1) {
      super(var1, new ModelZombieVillager(), 0.5F);
      this.func_177094_a(new LayerVillagerArmor(this));
   }

   protected ResourceLocation func_110775_a(EntityZombieVillager var1) {
      switch(var1.func_190736_dl()) {
      case 0:
         return field_188330_l;
      case 1:
         return field_188331_m;
      case 2:
         return field_188332_n;
      case 3:
         return field_188333_o;
      case 4:
         return field_188329_p;
      case 5:
      default:
         return field_110864_q;
      }
   }

   protected void func_77043_a(EntityZombieVillager var1, float var2, float var3, float var4) {
      if (var1.func_82230_o()) {
         var3 += (float)(Math.cos((double)var1.field_70173_aa * 3.25D) * 3.141592653589793D * 0.25D);
      }

      super.func_77043_a(var1, var2, var3, var4);
   }
}
