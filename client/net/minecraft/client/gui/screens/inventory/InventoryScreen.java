package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class InventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {
   private float xMouse;
   private float yMouse;
   private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
   private boolean widthTooNarrow;
   private boolean buttonClicked;

   public InventoryScreen(Player var1) {
      super(var1.inventoryMenu, var1.getInventory(), Component.translatable("container.crafting"));
      this.titleLabelX = 97;
   }

   public void containerTick() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), (Boolean)this.minecraft.options.operatorItemsTab().get()));
      } else {
         this.recipeBookComponent.tick();
      }
   }

   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), (Boolean)this.minecraft.options.operatorItemsTab().get()));
      } else {
         super.init();
         this.widthTooNarrow = this.width < 379;
         this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, (RecipeBookMenu)this.menu);
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
         this.addRenderableWidget(new ImageButton(this.leftPos + 104, this.height / 2 - 22, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, (var1) -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            var1.setPosition(this.leftPos + 104, this.height / 2 - 22);
            this.buttonClicked = true;
         }));
         this.addWidget(this.recipeBookComponent);
      }
   }

   protected void renderLabels(GuiGraphics var1, int var2, int var3) {
      var1.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.renderBackground(var1, var2, var3, var4);
         this.recipeBookComponent.render(var1, var2, var3, var4);
      } else {
         super.render(var1, var2, var3, var4);
         this.recipeBookComponent.render(var1, var2, var3, var4);
         this.recipeBookComponent.renderGhostRecipe(var1, this.leftPos, this.topPos, false, var4);
      }

      this.renderTooltip(var1, var2, var3);
      this.recipeBookComponent.renderTooltip(var1, this.leftPos, this.topPos, var2, var3);
      this.xMouse = (float)var2;
      this.yMouse = (float)var3;
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = this.leftPos;
      int var6 = this.topPos;
      var1.blit(INVENTORY_LOCATION, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      renderEntityInInventoryFollowsMouse(var1, var5 + 26, var6 + 8, var5 + 75, var6 + 78, 30, 0.0625F, this.xMouse, this.yMouse, this.minecraft.player);
   }

   public static void renderEntityInInventoryFollowsMouse(GuiGraphics var0, int var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, LivingEntity var9) {
      float var10 = (float)(var1 + var3) / 2.0F;
      float var11 = (float)(var2 + var4) / 2.0F;
      var0.enableScissor(var1, var2, var3, var4);
      float var12 = (float)Math.atan((double)((var10 - var7) / 40.0F));
      float var13 = (float)Math.atan((double)((var11 - var8) / 40.0F));
      Quaternionf var14 = (new Quaternionf()).rotateZ(3.1415927F);
      Quaternionf var15 = (new Quaternionf()).rotateX(var13 * 20.0F * 0.017453292F);
      var14.mul(var15);
      float var16 = var9.yBodyRot;
      float var17 = var9.getYRot();
      float var18 = var9.getXRot();
      float var19 = var9.yHeadRotO;
      float var20 = var9.yHeadRot;
      var9.yBodyRot = 180.0F + var12 * 20.0F;
      var9.setYRot(180.0F + var12 * 40.0F);
      var9.setXRot(-var13 * 20.0F);
      var9.yHeadRot = var9.getYRot();
      var9.yHeadRotO = var9.getYRot();
      float var21 = var9.getScale();
      Vector3f var22 = new Vector3f(0.0F, var9.getBbHeight() / 2.0F + var6 * var21, 0.0F);
      float var23 = (float)var5 / var21;
      renderEntityInInventory(var0, var10, var11, var23, var22, var14, var15, var9);
      var9.yBodyRot = var16;
      var9.setYRot(var17);
      var9.setXRot(var18);
      var9.yHeadRotO = var19;
      var9.yHeadRot = var20;
      var0.disableScissor();
   }

   public static void renderEntityInInventory(GuiGraphics var0, float var1, float var2, float var3, Vector3f var4, Quaternionf var5, @Nullable Quaternionf var6, LivingEntity var7) {
      var0.pose().pushPose();
      var0.pose().translate((double)var1, (double)var2, 50.0);
      var0.pose().scale(var3, var3, -var3);
      var0.pose().translate(var4.x, var4.y, var4.z);
      var0.pose().mulPose(var5);
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher var8 = Minecraft.getInstance().getEntityRenderDispatcher();
      if (var6 != null) {
         var8.overrideCameraOrientation(var6.conjugate(new Quaternionf()).rotateY(3.1415927F));
      }

      var8.setRenderShadow(false);
      RenderSystem.runAsFancy(() -> {
         var8.render(var7, 0.0, 0.0, 0.0, 0.0F, 1.0F, var0.pose(), var0.bufferSource(), 15728880);
      });
      var0.flush();
      var8.setRenderShadow(true);
      var0.pose().popPose();
      Lighting.setupFor3DItems();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.recipeBookComponent.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
   }

   public boolean charTyped(char var1, int var2) {
      return this.recipeBookComponent.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
   }

   protected boolean isHovering(int var1, int var2, int var3, int var4, double var5, double var7) {
      return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(var1, var2, var3, var4, var5, var7);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.recipeBookComponent.mouseClicked(var1, var3, var5)) {
         this.setFocused(this.recipeBookComponent);
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? false : super.mouseClicked(var1, var3, var5);
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.buttonClicked) {
         this.buttonClicked = false;
         return true;
      } else {
         return super.mouseReleased(var1, var3, var5);
      }
   }

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      boolean var8 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
      return this.recipeBookComponent.hasClickedOutside(var1, var3, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, var7) && var8;
   }

   protected void slotClicked(Slot var1, int var2, int var3, ClickType var4) {
      super.slotClicked(var1, var2, var3, var4);
      this.recipeBookComponent.slotClicked(var1);
   }

   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   public RecipeBookComponent getRecipeBookComponent() {
      return this.recipeBookComponent;
   }
}
