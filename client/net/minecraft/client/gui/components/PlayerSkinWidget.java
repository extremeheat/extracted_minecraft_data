package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;

public class PlayerSkinWidget extends AbstractWidget {
   private static final float MODEL_OFFSET = 0.0625F;
   private static final float MODEL_HEIGHT = 2.125F;
   private static final float Z_OFFSET = 100.0F;
   private static final float ROTATION_SENSITIVITY = 2.5F;
   private static final float DEFAULT_ROTATION_X = -5.0F;
   private static final float DEFAULT_ROTATION_Y = 30.0F;
   private static final float ROTATION_X_LIMIT = 50.0F;
   private final Model model;
   private final Supplier<PlayerSkin> skin;
   private float rotationX = -5.0F;
   private float rotationY = 30.0F;

   public PlayerSkinWidget(int var1, int var2, EntityModelSet var3, Supplier<PlayerSkin> var4) {
      super(0, 0, var1, var2, CommonComponents.EMPTY);
      this.model = PlayerSkinWidget.Model.bake(var3);
      this.skin = var4;
   }

   protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      var1.pose().pushPose();
      var1.pose().translate((float)this.getX() + (float)this.getWidth() / 2.0F, (float)(this.getY() + this.getHeight()), 100.0F);
      float var5 = (float)this.getHeight() / 2.125F;
      var1.pose().scale(var5, var5, var5);
      var1.pose().translate(0.0F, -0.0625F, 0.0F);
      var1.pose().rotateAround(Axis.XP.rotationDegrees(this.rotationX), 0.0F, -1.0625F, 0.0F);
      var1.pose().mulPose(Axis.YP.rotationDegrees(this.rotationY));
      var1.flush();
      Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(this.rotationX));
      this.model.render(var1, (PlayerSkin)this.skin.get());
      var1.flush();
      Lighting.setupFor3DItems();
      var1.pose().popPose();
   }

   protected void onDrag(double var1, double var3, double var5, double var7) {
      this.rotationX = Mth.clamp(this.rotationX - (float)var7 * 2.5F, -50.0F, 50.0F);
      this.rotationY += (float)var5 * 2.5F;
   }

   public void playDownSound(SoundManager var1) {
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
   }

   public boolean isActive() {
      return false;
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      return null;
   }

   static record Model(PlayerModel wideModel, PlayerModel slimModel) {
      private Model(PlayerModel var1, PlayerModel var2) {
         super();
         this.wideModel = var1;
         this.slimModel = var2;
      }

      public static Model bake(EntityModelSet var0) {
         PlayerModel var1 = new PlayerModel(var0.bakeLayer(ModelLayers.PLAYER), false);
         PlayerModel var2 = new PlayerModel(var0.bakeLayer(ModelLayers.PLAYER_SLIM), true);
         return new Model(var1, var2);
      }

      public void render(GuiGraphics var1, PlayerSkin var2) {
         var1.pose().pushPose();
         var1.pose().scale(1.0F, 1.0F, -1.0F);
         var1.pose().translate(0.0F, -1.501F, 0.0F);
         PlayerModel var3 = var2.model() == PlayerSkin.Model.SLIM ? this.slimModel : this.wideModel;
         RenderType var4 = var3.renderType(var2.texture());
         var1.drawSpecial((var3x) -> var3.renderToBuffer(var1.pose(), var3x.getBuffer(var4), 15728880, OverlayTexture.NO_OVERLAY));
         var1.pose().popPose();
      }
   }
}
