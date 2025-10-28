package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class InventoryScreen extends AbstractRecipeBookScreen<InventoryMenu> {
   private float xMouse;
   private float yMouse;
   private boolean buttonClicked;
   private final EffectsInInventory effects;

   public InventoryScreen(Player var1) {
      super(var1.inventoryMenu, new CraftingRecipeBookComponent(var1.inventoryMenu), var1.getInventory(), Component.translatable("container.crafting"));
      this.titleLabelX = 97;
      this.effects = new EffectsInInventory(this);
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
      }
   }

   protected ScreenPosition getRecipeBookButtonPosition() {
      return new ScreenPosition(this.leftPos + 104, this.height / 2 - 22);
   }

   protected void onRecipeBookButtonClick() {
      this.buttonClicked = true;
   }

   protected void renderLabels(GuiGraphics var1, int var2, int var3) {
      var1.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.effects.render(var1, var2, var3);
      super.render(var1, var2, var3, var4);
      this.xMouse = (float)var2;
      this.yMouse = (float)var3;
   }

   public boolean showsActiveEffects() {
      return this.effects.canSeeEffects();
   }

   protected boolean isBiggerResultSlot() {
      return false;
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = this.leftPos;
      int var6 = this.topPos;
      var1.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      renderEntityInInventoryFollowsMouse(var1, var5 + 26, var6 + 8, var5 + 75, var6 + 78, 30, 0.0625F, this.xMouse, this.yMouse, this.minecraft.player);
   }

   public static void renderEntityInInventoryFollowsMouse(GuiGraphics var0, int var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, LivingEntity var9) {
      float var10 = (float)(var1 + var3) / 2.0F;
      float var11 = (float)(var2 + var4) / 2.0F;
      float var12 = (float)Math.atan((double)((var10 - var7) / 40.0F));
      float var13 = (float)Math.atan((double)((var11 - var8) / 40.0F));
      Quaternionf var14 = (new Quaternionf()).rotateZ(3.1415927F);
      Quaternionf var15 = (new Quaternionf()).rotateX(var13 * 20.0F * 0.017453292F);
      var14.mul(var15);
      EntityRenderState var16 = extractRenderState(var9);
      if (var16 instanceof LivingEntityRenderState var17) {
         var17.bodyRot = 180.0F + var12 * 20.0F;
         var17.yRot = var12 * 20.0F;
         if (var17.pose != Pose.FALL_FLYING) {
            var17.xRot = -var13 * 20.0F;
         } else {
            var17.xRot = 0.0F;
         }

         var17.boundingBoxWidth /= var17.scale;
         var17.boundingBoxHeight /= var17.scale;
         var17.scale = 1.0F;
      }

      Vector3f var18 = new Vector3f(0.0F, var16.boundingBoxHeight / 2.0F + var6, 0.0F);
      var0.submitEntityRenderState(var16, (float)var5, var18, var14, var15, var1, var2, var3, var4);
   }

   private static EntityRenderState extractRenderState(LivingEntity var0) {
      EntityRenderDispatcher var1 = Minecraft.getInstance().getEntityRenderDispatcher();
      EntityRenderer var2 = var1.getRenderer(var0);
      EntityRenderState var3 = var2.createRenderState(var0, 1.0F);
      var3.lightCoords = 15728880;
      var3.shadowPieces.clear();
      var3.outlineColor = 0;
      return var3;
   }

   public boolean mouseReleased(MouseButtonEvent var1) {
      if (this.buttonClicked) {
         this.buttonClicked = false;
         return true;
      } else {
         return super.mouseReleased(var1);
      }
   }
}
