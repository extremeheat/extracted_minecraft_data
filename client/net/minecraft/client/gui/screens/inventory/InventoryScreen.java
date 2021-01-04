package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;

public class InventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {
   private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
   private float xMouse;
   private float yMouse;
   private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
   private boolean recipeBookComponentInitialized;
   private boolean widthTooNarrow;
   private boolean buttonClicked;

   public InventoryScreen(Player var1) {
      super(var1.inventoryMenu, var1.inventory, new TranslatableComponent("container.crafting", new Object[0]));
      this.passEvents = true;
   }

   public void tick() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player));
      } else {
         this.recipeBookComponent.tick();
      }
   }

   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player));
      } else {
         super.init();
         this.widthTooNarrow = this.width < 379;
         this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, (RecipeBookMenu)this.menu);
         this.recipeBookComponentInitialized = true;
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
         this.children.add(this.recipeBookComponent);
         this.setInitialFocus(this.recipeBookComponent);
         this.addButton(new ImageButton(this.leftPos + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (var1) -> {
            this.recipeBookComponent.initVisuals(this.widthTooNarrow);
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
            ((ImageButton)var1).setPosition(this.leftPos + 104, this.height / 2 - 22);
            this.buttonClicked = true;
         }));
      }
   }

   protected void renderLabels(int var1, int var2) {
      this.font.draw(this.title.getColoredString(), 97.0F, 8.0F, 4210752);
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.doRenderEffects = !this.recipeBookComponent.isVisible();
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.renderBg(var3, var1, var2);
         this.recipeBookComponent.render(var1, var2, var3);
      } else {
         this.recipeBookComponent.render(var1, var2, var3);
         super.render(var1, var2, var3);
         this.recipeBookComponent.renderGhostRecipe(this.leftPos, this.topPos, false, var3);
      }

      this.renderTooltip(var1, var2);
      this.recipeBookComponent.renderTooltip(this.leftPos, this.topPos, var1, var2);
      this.xMouse = (float)var1;
      this.yMouse = (float)var2;
      this.magicalSpecialHackyFocus(this.recipeBookComponent);
   }

   protected void renderBg(float var1, int var2, int var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(INVENTORY_LOCATION);
      int var4 = this.leftPos;
      int var5 = this.topPos;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
      renderPlayerModel(var4 + 51, var5 + 75, 30, (float)(var4 + 51) - this.xMouse, (float)(var5 + 75 - 50) - this.yMouse, this.minecraft.player);
   }

   public static void renderPlayerModel(int var0, int var1, int var2, float var3, float var4, LivingEntity var5) {
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var0, (float)var1, 50.0F);
      GlStateManager.scalef((float)(-var2), (float)var2, (float)var2);
      GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
      float var6 = var5.yBodyRot;
      float var7 = var5.yRot;
      float var8 = var5.xRot;
      float var9 = var5.yHeadRotO;
      float var10 = var5.yHeadRot;
      GlStateManager.rotatef(135.0F, 0.0F, 1.0F, 0.0F);
      Lighting.turnOn();
      GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-((float)Math.atan((double)(var4 / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
      var5.yBodyRot = (float)Math.atan((double)(var3 / 40.0F)) * 20.0F;
      var5.yRot = (float)Math.atan((double)(var3 / 40.0F)) * 40.0F;
      var5.xRot = -((float)Math.atan((double)(var4 / 40.0F))) * 20.0F;
      var5.yHeadRot = var5.yRot;
      var5.yHeadRotO = var5.yRot;
      GlStateManager.translatef(0.0F, 0.0F, 0.0F);
      EntityRenderDispatcher var11 = Minecraft.getInstance().getEntityRenderDispatcher();
      var11.setPlayerRotY(180.0F);
      var11.setRenderShadow(false);
      var11.render(var5, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
      var11.setRenderShadow(true);
      var5.yBodyRot = var6;
      var5.yRot = var7;
      var5.xRot = var8;
      var5.yHeadRotO = var9;
      var5.yHeadRot = var10;
      GlStateManager.popMatrix();
      Lighting.turnOff();
      GlStateManager.disableRescaleNormal();
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.disableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   protected boolean isHovering(int var1, int var2, int var3, int var4, double var5, double var7) {
      return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(var1, var2, var3, var4, var5, var7);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.recipeBookComponent.mouseClicked(var1, var3, var5)) {
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

   public void removed() {
      if (this.recipeBookComponentInitialized) {
         this.recipeBookComponent.removed();
      }

      super.removed();
   }

   public RecipeBookComponent getRecipeBookComponent() {
      return this.recipeBookComponent;
   }
}
