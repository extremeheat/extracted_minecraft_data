package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PufferfishBigModel;
import net.minecraft.client.model.PufferfishMidModel;
import net.minecraft.client.model.PufferfishSmallModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Pufferfish;

public class PufferfishRenderer extends MobRenderer<Pufferfish, EntityModel<Pufferfish>> {
   private static final ResourceLocation PUFFER_LOCATION = new ResourceLocation("textures/entity/fish/pufferfish.png");
   private int puffStateO = 3;
   private final PufferfishSmallModel<Pufferfish> small = new PufferfishSmallModel();
   private final PufferfishMidModel<Pufferfish> mid = new PufferfishMidModel();
   private final PufferfishBigModel<Pufferfish> big = new PufferfishBigModel();

   public PufferfishRenderer(EntityRenderDispatcher var1) {
      super(var1, new PufferfishBigModel(), 0.2F);
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Pufferfish var1) {
      return PUFFER_LOCATION;
   }

   public void render(Pufferfish var1, double var2, double var4, double var6, float var8, float var9) {
      int var10 = var1.getPuffState();
      if (var10 != this.puffStateO) {
         if (var10 == 0) {
            this.model = this.small;
         } else if (var10 == 1) {
            this.model = this.mid;
         } else {
            this.model = this.big;
         }
      }

      this.puffStateO = var10;
      this.shadowRadius = 0.1F + 0.1F * (float)var10;
      super.render((Mob)var1, var2, var4, var6, var8, var9);
   }

   protected void setupRotations(Pufferfish var1, float var2, float var3, float var4) {
      GlStateManager.translatef(0.0F, Mth.cos(var2 * 0.05F) * 0.08F, 0.0F);
      super.setupRotations(var1, var2, var3, var4);
   }
}
