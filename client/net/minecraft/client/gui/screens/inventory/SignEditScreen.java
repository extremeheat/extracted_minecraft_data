package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.VertexConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class SignEditScreen extends AbstractSignEditScreen {
   public static final float MAGIC_SCALE_NUMBER = 62.500004F;
   public static final float MAGIC_TEXT_SCALE = 0.9765628F;
   private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);
   @Nullable
   private Model signModel;

   public SignEditScreen(SignBlockEntity var1, boolean var2, boolean var3) {
      super(var1, var2, var3);
   }

   @Override
   protected void init() {
      super.init();
      boolean var1 = this.sign.getBlockState().getBlock() instanceof StandingSignBlock;
      this.signModel = SignRenderer.createSignModel(this.minecraft.getEntityModels(), this.woodType, var1);
   }

   @Override
   protected void offsetSign(GuiGraphics var1, BlockState var2) {
      super.offsetSign(var1, var2);
      boolean var3 = var2.getBlock() instanceof StandingSignBlock;
      if (!var3) {
         var1.pose().translate(0.0F, 35.0F, 0.0F);
      }
   }

   @Override
   protected void renderSignBackground(GuiGraphics var1) {
      if (this.signModel != null) {
         var1.pose().translate(0.0F, 31.0F, 0.0F);
         var1.pose().scale(62.500004F, 62.500004F, -62.500004F);
         var1.drawSpecial(var2 -> {
            Material var3 = Sheets.getSignMaterial(this.woodType);
            VertexConsumer var4 = var3.buffer(var2, this.signModel::renderType);
            this.signModel.renderToBuffer(var1.pose(), var4, 15728880, OverlayTexture.NO_OVERLAY);
         });
      }
   }

   @Override
   protected Vector3f getSignTextScale() {
      return TEXT_SCALE;
   }
}
