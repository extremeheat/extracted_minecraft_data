package net.minecraft.client.gui.screens.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.joml.Vector3f;

public class SignEditScreen extends AbstractSignEditScreen {
   public static final float MAGIC_SCALE_NUMBER = 62.500004F;
   public static final float MAGIC_TEXT_SCALE = 0.9765628F;
   private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);
   @Nullable
   private Model.Simple signModel;

   public SignEditScreen(SignBlockEntity var1, boolean var2, boolean var3) {
      super(var1, var2, var3);
   }

   protected void init() {
      super.init();
      boolean var1 = this.sign.getBlockState().getBlock() instanceof StandingSignBlock;
      this.signModel = SignRenderer.createSignModel(this.minecraft.getEntityModels(), this.woodType, var1);
   }

   protected float getSignYOffset() {
      return 90.0F;
   }

   protected void renderSignBackground(GuiGraphics var1) {
      if (this.signModel != null) {
         int var2 = this.width / 2;
         int var3 = var2 - 48;
         boolean var4 = true;
         int var5 = var2 + 48;
         boolean var6 = true;
         var1.submitSignRenderState(this.signModel, 62.500004F, this.woodType, var3, 66, var5, 168);
      }
   }

   protected Vector3f getSignTextScale() {
      return TEXT_SCALE;
   }
}
