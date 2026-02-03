package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class SignEditScreen extends AbstractSignEditScreen {
   public static final float MAGIC_SCALE_NUMBER = 62.500004F;
   public static final float MAGIC_TEXT_SCALE = 0.9765628F;
   private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);
   private Model.@Nullable Simple signModel;

   public SignEditScreen(final SignBlockEntity sign, final boolean isFrontText, final boolean shouldFilter) {
      super(sign, isFrontText, shouldFilter);
   }

   protected void init() {
      super.init();
      boolean standing = this.sign.getBlockState().getBlock() instanceof StandingSignBlock;
      this.signModel = SignRenderer.createSignModel(this.minecraft.getEntityModels(), this.woodType, standing);
   }

   protected float getSignYOffset() {
      return 90.0F;
   }

   protected void renderSignBackground(final GuiGraphics graphics) {
      if (this.signModel != null) {
         int centerX = this.width / 2;
         int x0 = centerX - 48;
         int y0 = 66;
         int x1 = centerX + 48;
         int y1 = 168;
         graphics.submitSignRenderState(this.signModel, 62.500004F, this.woodType, x0, 66, x1, 168);
      }
   }

   protected Vector3f getSignTextScale() {
      return TEXT_SCALE;
   }
}
