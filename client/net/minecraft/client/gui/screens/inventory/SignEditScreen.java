package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
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
   private SignRenderer.SignModel signModel;

   public SignEditScreen(SignBlockEntity var1, boolean var2, boolean var3) {
      super(var1, var2, var3);
   }

   protected void init() {
      super.init();
      this.signModel = SignRenderer.createSignModel(this.minecraft.getEntityModels(), this.woodType);
   }

   protected void offsetSign(GuiGraphics var1, BlockState var2) {
      super.offsetSign(var1, var2);
      boolean var3 = var2.getBlock() instanceof StandingSignBlock;
      if (!var3) {
         var1.pose().translate(0.0F, 35.0F, 0.0F);
      }

   }

   protected void renderSignBackground(GuiGraphics var1, BlockState var2) {
      if (this.signModel != null) {
         boolean var3 = var2.getBlock() instanceof StandingSignBlock;
         var1.pose().translate(0.0F, 31.0F, 0.0F);
         var1.pose().scale(62.500004F, 62.500004F, -62.500004F);
         Material var4 = Sheets.getSignMaterial(this.woodType);
         MultiBufferSource.BufferSource var10001 = var1.bufferSource();
         SignRenderer.SignModel var10002 = this.signModel;
         Objects.requireNonNull(var10002);
         VertexConsumer var5 = var4.buffer(var10001, var10002::renderType);
         this.signModel.stick.visible = var3;
         this.signModel.root.render(var1.pose(), var5, 15728880, OverlayTexture.NO_OVERLAY);
      }
   }

   protected Vector3f getSignTextScale() {
      return TEXT_SCALE;
   }
}
