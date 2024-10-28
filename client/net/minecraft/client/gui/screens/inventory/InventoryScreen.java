package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
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
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), (Boolean)this.minecraft.options.operatorItemsTab().get()));
      }

   }

   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
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
      var1.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.effects.render(var1, var2, var3, var4);
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
      var1.blit(RenderType::guiTextured, INVENTORY_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
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
      var0.flush();
      Lighting.setupForEntityInInventory();
      EntityRenderDispatcher var8 = Minecraft.getInstance().getEntityRenderDispatcher();
      if (var6 != null) {
         var8.overrideCameraOrientation(var6.conjugate(new Quaternionf()).rotateY(3.1415927F));
      }

      var8.setRenderShadow(false);
      var0.drawSpecial((var3x) -> {
         var8.render(var7, 0.0, 0.0, 0.0, 1.0F, var0.pose(), var3x, 15728880);
      });
      var0.flush();
      var8.setRenderShadow(true);
      var0.pose().popPose();
      Lighting.setupFor3DItems();
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.buttonClicked) {
         this.buttonClicked = false;
         return true;
      } else {
         return super.mouseReleased(var1, var3, var5);
      }
   }
}
