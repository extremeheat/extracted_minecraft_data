package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.joml.Vector3f;

public class HangingSignEditScreen extends AbstractSignEditScreen {
   public static final float MAGIC_BACKGROUND_SCALE = 4.5F;
   private static final Vector3f TEXT_SCALE = new Vector3f(1.0F, 1.0F, 1.0F);
   private static final int TEXTURE_WIDTH = 16;
   private static final int TEXTURE_HEIGHT = 16;
   private final Identifier texture;

   public HangingSignEditScreen(final SignBlockEntity sign, final boolean isFrontText, final boolean shouldFilter) {
      super(sign, isFrontText, shouldFilter, Component.translatable("hanging_sign.edit"));
      this.texture = Identifier.withDefaultNamespace("textures/gui/hanging_signs/" + this.woodType.name() + ".png");
   }

   protected float getSignYOffset() {
      return 125.0F;
   }

   protected void renderSignBackground(final GuiGraphics graphics) {
      graphics.pose().translate(0.0F, -13.0F);
      graphics.pose().scale(4.5F, 4.5F);
      graphics.blit(RenderPipelines.GUI_TEXTURED, this.texture, -8, -8, 0.0F, 0.0F, 16, 16, 16, 16);
   }

   protected Vector3f getSignTextScale() {
      return TEXT_SCALE;
   }
}
