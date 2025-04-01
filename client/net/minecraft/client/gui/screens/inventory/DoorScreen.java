package net.minecraft.client.gui.screens.inventory;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DoorMenu;

public class DoorScreen extends AbstractContainerScreen<DoorMenu> {
   private static final Component TITLE = Component.translatable("gui.door");
   private static final Component LABEL_PREVIOUS = Component.translatable("gui.door.previous");
   private static final Component LABEL_NEXT = Component.translatable("gui.door.next");
   private static final Component LABEL_OPEN = Component.translatable("gui.door.open");
   public static final ResourceLocation BOOK_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/door.png");
   protected static final int BG_WIDTH = 176;
   protected static final int BG_HEIGHT = 247;
   private final Layout layout;

   public DoorScreen(int var1, Inventory var2, List<DoorMenu.Layout> var3) {
      super(new DoorMenu(var1, var2, var3), var2, TITLE);
      EqualSpacingLayout var4 = new EqualSpacingLayout(162, 16, EqualSpacingLayout.Orientation.HORIZONTAL);
      if (var3.size() > 1) {
         var4.addChild(Button.builder(LABEL_PREVIOUS, (var2x) -> this.clickButton(var1, 1)).width(48).build());
      }

      if (!var3.isEmpty()) {
         var4.addChild(Button.builder(LABEL_OPEN, (var2x) -> this.clickButton(var1, 2)).width(48).build());
      }

      if (var3.size() > 1) {
         var4.addChild(Button.builder(LABEL_NEXT, (var2x) -> this.clickButton(var1, 0)).width(48).build());
      }

      LinearLayout var5 = LinearLayout.vertical();
      var5.addChild(SpacerElement.height(30));
      var5.addChild(new DisplayWidget());
      var5.addChild(SpacerElement.height(4));
      var5.addChild(var4);
      var5.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.layout = var5;
      this.imageWidth = 176;
      this.imageHeight = 247;
   }

   private void clickButton(int var1, int var2) {
      this.minecraft.gameMode.handleInventoryButtonClick(var1, var2);
   }

   protected void init() {
      super.init();
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      this.layout.setX(this.leftPos + 7);
      this.layout.setY(this.topPos);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderLabels(GuiGraphics var1, int var2, int var3) {
      var1.drawString(this.font, (Component)((DoorMenu)this.menu).getKey().getHoverName(), 30, 12, -1);
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      var1.blit(RenderType::guiTextured, BOOK_LOCATION, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
   }

   class DisplayWidget extends AbstractWidget {
      public DisplayWidget() {
         super(0, 0, 162, 108, Component.literal("rather big box with a lot of smaller boxes inside"));
      }

      protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
         ScreenRectangle var5 = this.getRectangle();
         DoorMenu.Layout var6 = (DoorMenu.Layout)Objects.requireNonNullElse(((DoorMenu)DoorScreen.this.menu).getOption(), DoorMenu.ERROR);
         List var7 = var6.fields();
         int var8 = var7.size();
         int var9 = var6.width();
         int var10 = Mth.positiveCeilDiv(var8, var9);
         boolean var11 = true;
         boolean var12 = true;
         boolean var13 = true;
         int var14 = var5.width() - 16;
         int var15 = var14 / var9;
         int var16 = var5.height() - 16;
         int var17 = var16 / var10;
         int var18 = Math.max(Math.min(var17, var15), 4);
         int var19 = var18 - 1;
         int var20 = var5.left() + (var14 - var18 * var9) / 2 + 8;
         int var21 = var5.top() + (var16 - var18 * var10) / 2 + 8;
         Component var22 = null;
         int var23 = 0;

         for(int var24 = 0; var24 < var10; ++var24) {
            for(int var25 = 0; var25 < var9; ++var25) {
               DoorMenu.FieldType var26 = (DoorMenu.FieldType)var7.get(var23++);
               if (var26.color != null) {
                  int var27 = var20 + var25 * var18;
                  int var28 = var21 + var24 * var18;
                  int var29 = var27 + var19;
                  int var30 = var28 + var19;
                  var1.fill(var27, var28, var29, var30, -16777216 | var26.color.getTextureDiffuseColor());
                  if (var2 >= var27 && var2 < var29 && var3 >= var28 && var3 < var30) {
                     var22 = var26.tooltip;
                  }
               }
            }
         }

         if (var22 != null) {
            var1.renderTooltip(DoorScreen.this.font, var22, var2, var3);
         }

      }

      protected void updateWidgetNarration(NarrationElementOutput var1) {
      }
   }
}
