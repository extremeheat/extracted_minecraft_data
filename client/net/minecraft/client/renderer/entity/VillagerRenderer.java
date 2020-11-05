package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.npc.Villager;

public class VillagerRenderer extends MobRenderer<Villager, VillagerModel<Villager>> {
   private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/villager/villager.png");

   public VillagerRenderer(EntityRenderDispatcher var1, ReloadableResourceManager var2) {
      super(var1, new VillagerModel(0.0F), 0.5F);
      this.addLayer(new CustomHeadLayer(this));
      this.addLayer(new VillagerProfessionLayer(this, var2, "villager"));
      this.addLayer(new CrossedArmsItemLayer(this));
   }

   public ResourceLocation getTextureLocation(Villager var1) {
      return VILLAGER_BASE_SKIN;
   }

   protected void scale(Villager var1, PoseStack var2, float var3) {
      float var4 = 0.9375F;
      if (var1.isBaby()) {
         var4 = (float)((double)var4 * 0.5D);
         this.shadowRadius = 0.25F;
      } else {
         this.shadowRadius = 0.5F;
      }

      var2.scale(var4, var4, var4);
   }
}
