package net.minecraft.client.gui.components;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;

public class PlayerSkinWidget extends AbstractWidget {
   private static final float MODEL_HEIGHT = 2.125F;
   private static final float FIT_SCALE = 0.97F;
   private static final float ROTATION_SENSITIVITY = 2.5F;
   private static final float DEFAULT_ROTATION_X = -5.0F;
   private static final float DEFAULT_ROTATION_Y = 30.0F;
   private static final float ROTATION_X_LIMIT = 50.0F;
   private final PlayerModel wideModel;
   private final PlayerModel slimModel;
   private final Supplier<PlayerSkin> skin;
   private float rotationX = -5.0F;
   private float rotationY = 30.0F;

   public PlayerSkinWidget(int var1, int var2, EntityModelSet var3, Supplier<PlayerSkin> var4) {
      super(0, 0, var1, var2, CommonComponents.EMPTY);
      this.wideModel = new PlayerModel(var3.bakeLayer(ModelLayers.PLAYER), false);
      this.slimModel = new PlayerModel(var3.bakeLayer(ModelLayers.PLAYER_SLIM), true);
      this.skin = var4;
   }

   protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      float var5 = 0.97F * (float)this.getHeight() / 2.125F;
      float var6 = -1.0625F;
      PlayerSkin var7 = (PlayerSkin)this.skin.get();
      PlayerModel var8 = var7.model() == PlayerModelType.SLIM ? this.slimModel : this.wideModel;
      var1.submitSkinRenderState(var8, var7.body().texturePath(), var5, this.rotationX, this.rotationY, -1.0625F, this.getX(), this.getY(), this.getRight(), this.getBottom());
   }

   protected void onDrag(MouseButtonEvent var1, double var2, double var4) {
      this.rotationX = Mth.clamp(this.rotationX - (float)var4 * 2.5F, -50.0F, 50.0F);
      this.rotationY += (float)var2 * 2.5F;
   }

   public void playDownSound(SoundManager var1) {
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
   }

   @Nullable
   public ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      return null;
   }
}
