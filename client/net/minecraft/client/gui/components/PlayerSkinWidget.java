package net.minecraft.client.gui.components;

import java.util.function.Supplier;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jspecify.annotations.Nullable;

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

   public PlayerSkinWidget(final int width, final int height, final EntityModelSet models, final Supplier<PlayerSkin> skin) {
      super(0, 0, width, height, CommonComponents.EMPTY);
      this.wideModel = new PlayerModel(models.bakeLayer(ModelLayers.PLAYER), false);
      this.slimModel = new PlayerModel(models.bakeLayer(ModelLayers.PLAYER_SLIM), true);
      this.skin = skin;
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      float scale = 0.97F * (float)this.getHeight() / 2.125F;
      float pivotY = -1.0625F;
      PlayerSkin skin = (PlayerSkin)this.skin.get();
      PlayerModel model = skin.model() == PlayerModelType.SLIM ? this.slimModel : this.wideModel;
      graphics.skin(model, skin.body().texturePath(), scale, this.rotationX, this.rotationY, -1.0625F, this.getX(), this.getY(), this.getRight(), this.getBottom());
   }

   protected void onDrag(final MouseButtonEvent event, final double dx, final double dy) {
      this.rotationX = Mth.clamp(this.rotationX - (float)dy * 2.5F, -50.0F, 50.0F);
      this.rotationY += (float)dx * 2.5F;
   }

   public void playDownSound(final SoundManager soundManager) {
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
   }

   public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent navigationEvent) {
      return null;
   }
}
