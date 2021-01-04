package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class CatRenderer extends MobRenderer<Cat, CatModel<Cat>> {
   public CatRenderer(EntityRenderDispatcher var1) {
      super(var1, new CatModel(0.0F), 0.4F);
      this.addLayer(new CatCollarLayer(this));
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Cat var1) {
      return var1.getResourceLocation();
   }

   protected void scale(Cat var1, float var2) {
      super.scale(var1, var2);
      GlStateManager.scalef(0.8F, 0.8F, 0.8F);
   }

   protected void setupRotations(Cat var1, float var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      float var5 = var1.getLieDownAmount(var4);
      if (var5 > 0.0F) {
         GlStateManager.translatef(0.4F * var5, 0.15F * var5, 0.1F * var5);
         GlStateManager.rotatef(Mth.rotLerp(var5, 0.0F, 90.0F), 0.0F, 0.0F, 1.0F);
         BlockPos var6 = new BlockPos(var1);
         List var7 = var1.level.getEntitiesOfClass(Player.class, (new AABB(var6)).inflate(2.0D, 2.0D, 2.0D));
         Iterator var8 = var7.iterator();

         while(var8.hasNext()) {
            Player var9 = (Player)var8.next();
            if (var9.isSleeping()) {
               GlStateManager.translatef(0.15F * var5, 0.0F, 0.0F);
               break;
            }
         }
      }

   }
}
