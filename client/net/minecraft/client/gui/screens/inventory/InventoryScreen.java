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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class InventoryScreen extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {
   private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
   private float xMouse;
   private float yMouse;
   private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
   private boolean widthTooNarrow;
   private boolean buttonClicked;

   public InventoryScreen(Player var1) {
      super(var1.inventoryMenu, var1.getInventory(), Component.translatable("container.crafting"));
      this.titleLabelX = 97;
   }

   @Override
   public void containerTick() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft
            .setScreen(
               new CreativeModeInventoryScreen(
                  this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), this.minecraft.options.operatorItemsTab().get()
               )
            );
      } else {
         this.recipeBookComponent.tick();
      }
   }

   @Override
   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft
            .setScreen(
               new CreativeModeInventoryScreen(
                  this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), this.minecraft.options.operatorItemsTab().get()
               )
            );
      } else {
         super.init();
         this.widthTooNarrow = this.width < 379;
         this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
         this.addRenderableWidget(new ImageButton(this.leftPos + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, var1 -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            var1.setPosition(this.leftPos + 104, this.height / 2 - 22);
            this.buttonClicked = true;
         }));
         this.addWidget(this.recipeBookComponent);
         this.setInitialFocus(this.recipeBookComponent);
      }
   }

   @Override
   protected void renderLabels(GuiGraphics var1, int var2, int var3) {
      var1.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.renderBg(var1, var4, var2, var3);
         this.recipeBookComponent.render(var1, var2, var3, var4);
      } else {
         this.recipeBookComponent.render(var1, var2, var3, var4);
         super.render(var1, var2, var3, var4);
         this.recipeBookComponent.renderGhostRecipe(var1, this.leftPos, this.topPos, false, var4);
      }

      this.renderTooltip(var1, var2, var3);
      this.recipeBookComponent.renderTooltip(var1, this.leftPos, this.topPos, var2, var3);
      this.xMouse = (float)var2;
      this.yMouse = (float)var3;
   }

   @Override
   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = this.leftPos;
      int var6 = this.topPos;
      var1.blit(INVENTORY_LOCATION, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      renderEntityInInventoryFollowsMouse(
         var1, var5 + 51, var6 + 75, 30, (float)(var5 + 51) - this.xMouse, (float)(var6 + 75 - 50) - this.yMouse, this.minecraft.player
      );
   }

   public static void renderEntityInInventoryFollowsMouse(GuiGraphics var0, int var1, int var2, int var3, float var4, float var5, LivingEntity var6) {
      float var7 = (float)Math.atan((double)(var4 / 40.0F));
      float var8 = (float)Math.atan((double)(var5 / 40.0F));
      Quaternionf var9 = new Quaternionf().rotateZ(3.1415927F);
      Quaternionf var10 = new Quaternionf().rotateX(var8 * 20.0F * 0.017453292F);
      var9.mul(var10);
      float var11 = var6.yBodyRot;
      float var12 = var6.getYRot();
      float var13 = var6.getXRot();
      float var14 = var6.yHeadRotO;
      float var15 = var6.yHeadRot;
      var6.yBodyRot = 180.0F + var7 * 20.0F;
      var6.setYRot(180.0F + var7 * 40.0F);
      var6.setXRot(-var8 * 20.0F);
      var6.yHeadRot = var6.getYRot();
      var6.yHeadRotO = var6.getYRot();
      renderEntityInInventory(var0, var1, var2, var3, var9, var10, var6);
      var6.yBodyRot = var11;
      var6.setYRot(var12);
      var6.setXRot(var13);
      var6.yHeadRotO = var14;
      var6.yHeadRot = var15;
   }

   public static void renderEntityInInventory(GuiGraphics var0, int var1, int var2, int var3, Quaternionf var4, @Nullable Quaternionf var5, LivingEntity var6) {
      var0.pose().pushPose();
      var0.pose().translate((double)var1, (double)var2, 50.0);
      var0.pose().mulPoseMatrix(new Matrix4f().scaling((float)var3, (float)var3, (float)(-var3)));
      var0.pose().mulPose(var4);
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher var7 = Minecraft.getInstance().getEntityRenderDispatcher();
      if (var5 != null) {
         var5.conjugate();
         var7.overrideCameraOrientation(var5);
      }

      var7.setRenderShadow(false);
      RenderSystem.runAsFancy(() -> var7.render(var6, 0.0, 0.0, 0.0, 0.0F, 1.0F, var0.pose(), var0.bufferSource(), 15728880));
      var0.flush();
      var7.setRenderShadow(true);
      var0.pose().popPose();
      Lighting.setupFor3DItems();
   }

   @Override
   protected boolean isHovering(int var1, int var2, int var3, int var4, double var5, double var7) {
      return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(var1, var2, var3, var4, var5, var7);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.recipeBookComponent.mouseClicked(var1, var3, var5)) {
         this.setFocused(this.recipeBookComponent);
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? false : super.mouseClicked(var1, var3, var5);
      }
   }

   @Override
   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.buttonClicked) {
         this.buttonClicked = false;
         return true;
      } else {
         return super.mouseReleased(var1, var3, var5);
      }
   }

   @Override
   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      boolean var8 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
      return this.recipeBookComponent.hasClickedOutside(var1, var3, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, var7) && var8;
   }

   @Override
   protected void slotClicked(Slot var1, int var2, int var3, ClickType var4) {
      super.slotClicked(var1, var2, var3, var4);
      this.recipeBookComponent.slotClicked(var1);
   }

   @Override
   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   @Override
   public RecipeBookComponent getRecipeBookComponent() {
      return this.recipeBookComponent;
   }
}
