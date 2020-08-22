package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.monster.ZombieVillager;

public class ZombieVillagerRenderer extends HumanoidMobRenderer {
   private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

   public ZombieVillagerRenderer(EntityRenderDispatcher var1, ReloadableResourceManager var2) {
      super(var1, new ZombieVillagerModel(0.0F, false), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new ZombieVillagerModel(0.5F, true), new ZombieVillagerModel(1.0F, true)));
      this.addLayer(new VillagerProfessionLayer(this, var2, "zombie_villager"));
   }

   public ResourceLocation getTextureLocation(ZombieVillager var1) {
      return ZOMBIE_VILLAGER_LOCATION;
   }

   protected void setupRotations(ZombieVillager var1, PoseStack var2, float var3, float var4, float var5) {
      if (var1.isConverting()) {
         var4 += (float)(Math.cos((double)var1.tickCount * 3.25D) * 3.141592653589793D * 0.25D);
      }

      super.setupRotations(var1, var2, var3, var4, var5);
   }
}
