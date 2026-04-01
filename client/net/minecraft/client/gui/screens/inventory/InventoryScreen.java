package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class InventoryScreen extends AbstractRecipeBookScreen<InventoryMenu> {
   private float xMouse;
   private float yMouse;
   private boolean buttonClicked;
   private final EffectsInInventory effects;
   private @Nullable SpriteIconButton advancements;

   public InventoryScreen(final Player player) {
      super(player.inventoryMenu, new CraftingRecipeBookComponent(player.inventoryMenu), player.getInventory(), Component.translatable("container.crafting"));
      this.titleLabelX = 97;
      this.effects = new EffectsInInventory(this);
   }

   private void openAdvancementsScreen(final Button button) {
      this.minecraft.setScreen(new AdvancementsScreen(this.minecraft.player.connection.getAdvancements(), this));
   }

   public void containerTick() {
      super.containerTick();
      if (this.minecraft.player.hasInfiniteMaterials()) {
         this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), (Boolean)this.minecraft.options.operatorItemsTab().get()));
      }

   }

   protected void init() {
      if (this.minecraft.player.hasInfiniteMaterials()) {
         this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), (Boolean)this.minecraft.options.operatorItemsTab().get()));
      } else {
         super.init();
         this.advancements = (SpriteIconButton)this.addRenderableWidget(SpriteIconButton.builder(Component.translatable("gui.advancements"), this::openAdvancementsScreen, true).sprite((Identifier)Identifier.withDefaultNamespace("icon/info"), 24, 24).size(24, 24).build());
      }
   }

   protected ScreenPosition getRecipeBookButtonPosition() {
      return new ScreenPosition(this.leftPos + 77, this.topPos + 7);
   }

   protected void onRecipeBookButtonClick() {
      this.buttonClicked = true;
   }

   protected void extractLabels(final GuiGraphicsExtractor graphics, final int xm, final int ym) {
      graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      this.effects.extractRenderState(graphics, mouseX, mouseY);
      super.extractRenderState(graphics, mouseX, mouseY, a);
      this.xMouse = (float)mouseX;
      this.yMouse = (float)mouseY;
      if (this.advancements != null) {
         this.advancements.setPosition(this.leftPos, this.topPos - 68);
         this.advancements.extractRenderState(graphics, mouseX, mouseY, a);
      }

   }

   public boolean showsActiveEffects() {
      return this.effects.canSeeEffects();
   }

   protected boolean isBiggerResultSlot() {
      return false;
   }

   public static void extractEntityInInventoryFollowsMouse(final GuiGraphicsExtractor graphics, final int x0, final int y0, final int x1, final int y1, final int size, final float offsetY, final float mouseX, final float mouseY, final LivingEntity entity) {
      float centerX = (float)(x0 + x1) / 2.0F;
      float centerY = (float)(y0 + y1) / 2.0F;
      float xAngle = (float)Math.atan((double)((centerX - mouseX) / 40.0F));
      float yAngle = (float)Math.atan((double)((centerY - mouseY) / 40.0F));
      Quaternionf rotation = (new Quaternionf()).rotateZ(3.1415927F);
      Quaternionf xRotation = (new Quaternionf()).rotateX(yAngle * 20.0F * 0.017453292F);
      rotation.mul(xRotation);
      EntityRenderState renderState = extractRenderState(entity);
      if (renderState instanceof LivingEntityRenderState livingRenderState) {
         livingRenderState.bodyRot = 180.0F + xAngle * 20.0F;
         livingRenderState.yRot = xAngle * 20.0F;
         if (livingRenderState.pose != Pose.FALL_FLYING) {
            livingRenderState.xRot = -yAngle * 20.0F;
         } else {
            livingRenderState.xRot = 0.0F;
         }

         livingRenderState.boundingBoxWidth /= livingRenderState.scale;
         livingRenderState.boundingBoxHeight /= livingRenderState.scale;
         livingRenderState.scale = 1.0F;
      }

      Vector3f translation = new Vector3f(0.0F, renderState.boundingBoxHeight / 2.0F + offsetY, 0.0F);
      graphics.entity(renderState, (float)size, translation, rotation, xRotation, x0, y0, x1, y1);
   }

   private static EntityRenderState extractRenderState(final LivingEntity entity) {
      EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      EntityRenderer<? super LivingEntity, ?> renderer = entityRenderDispatcher.getRenderer(entity);
      EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
      renderState.shadowPieces.clear();
      renderState.outlineColor = 0;
      return renderState;
   }

   public boolean mouseReleased(final MouseButtonEvent event) {
      if (this.buttonClicked) {
         this.buttonClicked = false;
         return true;
      } else {
         return super.mouseReleased(event);
      }
   }
}
