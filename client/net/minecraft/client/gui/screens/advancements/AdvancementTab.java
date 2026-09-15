package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.Map;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;

public class AdvancementTab {
   private final Minecraft minecraft;
   private final AdvancementsScreen screen;
   private final AdvancementTabType type;
   private final int index;
   private final Identifier background;
   private final ItemStack icon;
   private final Component title;
   private final AdvancementWidget root;
   private final Map<AdvancementHolder, AdvancementWidget> widgets = Maps.newLinkedHashMap();
   private double scrollX;
   private double scrollY;
   private int minX = 2147483647;
   private int minY = 2147483647;
   private int maxX = -2147483648;
   private int maxY = -2147483648;
   private float fade;
   private boolean centered;
   private @Nullable AdvancementWidget hovered;

   public AdvancementTab(final Minecraft minecraft, final AdvancementsScreen screen, final AdvancementTabType type, final int index, final AdvancementWidget root, final ItemStackTemplate icon, final Component title, final Identifier background) {
      super();
      this.minecraft = minecraft;
      this.screen = screen;
      this.type = type;
      this.index = index;
      this.background = background;
      this.icon = icon.create();
      this.title = title;
      this.root = root;
      this.addWidget(root);
   }

   public void copyPosition(final AdvancementTab source) {
      this.scrollX = source.scrollX;
      this.scrollY = source.scrollY;
      this.minX = source.minX;
      this.minY = source.minY;
      this.maxX = source.maxX;
      this.maxY = source.maxY;
      this.centered = source.centered;
   }

   public AdvancementTabType getType() {
      return this.type;
   }

   public int getIndex() {
      return this.index;
   }

   public AdvancementHolder getRootAdvancement() {
      return this.root.getAdvancement();
   }

   public Component getTitle() {
      return this.title;
   }

   public void tick(final int relativeMouseX, final int relativeMouseY) {
      boolean hovering = false;
      if (relativeMouseX > 0 && relativeMouseX < 234 && relativeMouseY > 0 && relativeMouseY < 113) {
         int intScrollX = Mth.floor(this.scrollX);
         int intScrollY = Mth.floor(this.scrollY);

         for(AdvancementWidget widget : this.widgets.values()) {
            if (widget.isMouseOver(intScrollX, intScrollY, relativeMouseX, relativeMouseY)) {
               hovering = true;
               this.hovered = widget;
               break;
            }
         }
      }

      if (hovering) {
         this.fade = Mth.clamp(this.fade + 0.06F, 0.0F, 0.3F);
      } else {
         this.fade = Mth.clamp(this.fade - 0.12F, 0.0F, 1.0F);
         if (this.hovered != null) {
            this.hovered = null;
         }
      }

   }

   public void extractTab(final GuiGraphicsExtractor graphics, final int xo, final int yo, final int mouseX, final int mouseY, final boolean selected) {
      int tabX = xo + this.type.getX(this.index);
      int tabY = yo + this.type.getY(this.index);
      this.type.extractRenderState(graphics, tabX, tabY, selected, this.index);
      if (!selected && mouseX > tabX && mouseY > tabY && mouseX < tabX + this.type.getWidth() && mouseY < tabY + this.type.getHeight()) {
         graphics.requestCursor(CursorTypes.POINTING_HAND);
      }

   }

   public void extractIcon(final GuiGraphicsExtractor graphics, final int xo, final int yo) {
      this.type.extractIcon(graphics, xo, yo, this.index, this.icon);
   }

   public void extractContents(final GuiGraphicsExtractor graphics, final int windowLeft, final int windowTop) {
      if (!this.centered) {
         this.scrollX = (double)(117 - (this.maxX + this.minX) / 2);
         this.scrollY = (double)(56 - (this.maxY + this.minY) / 2);
         this.centered = true;
      }

      graphics.enableScissor(windowLeft, windowTop, windowLeft + 234, windowTop + 113);
      graphics.pose().pushMatrix();
      graphics.pose().translate((float)windowLeft, (float)windowTop);
      int intScrollX = Mth.floor(this.scrollX);
      int intScrollY = Mth.floor(this.scrollY);
      int left = intScrollX % 16;
      int top = intScrollY % 16;

      for(int x = -1; x <= 15; ++x) {
         for(int y = -1; y <= 8; ++y) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, this.background, left + 16 * x, top + 16 * y, 0.0F, 0.0F, 16, 16, 16, 16);
         }
      }

      this.root.extractConnectivity(graphics, intScrollX, intScrollY, true);
      this.root.extractConnectivity(graphics, intScrollX, intScrollY, false);
      this.root.extractRenderState(graphics, intScrollX, intScrollY);
      graphics.pose().popMatrix();
      graphics.disableScissor();
   }

   public void extractTooltips(final GuiGraphicsExtractor graphics, final int xo, final int yo) {
      graphics.fill(0, 0, 234, 113, Mth.floor(this.fade * 255.0F) << 24);
      if (this.hovered != null) {
         int intScrollX = Mth.floor(this.scrollX);
         int intScrollY = Mth.floor(this.scrollY);
         this.hovered.extractHover(graphics, intScrollX, intScrollY, this.fade, xo, yo, this.screen.width);
      }

   }

   public boolean isMouseOver(final int xo, final int yo, final double mx, final double my) {
      return this.type.isMouseOver(xo, yo, this.index, mx, my);
   }

   public static @Nullable AdvancementTab create(final Minecraft minecraft, final AdvancementsScreen screen, int index, final AdvancementNode root) {
      AdvancementWidget rootWidget = AdvancementWidget.createWidget(minecraft, root);
      if (rootWidget == null) {
         return null;
      } else {
         DisplayInfo rootDisplay = rootWidget.getDisplay();
         if (rootDisplay.background().isEmpty()) {
            return null;
         } else {
            Identifier background = ((ClientAsset.ResourceTexture)rootDisplay.background().get()).texturePath();

            for(AdvancementTabType type : AdvancementTabType.values()) {
               if (index < type.getMax()) {
                  return new AdvancementTab(minecraft, screen, type, index, rootWidget, rootDisplay.icon(), rootDisplay.title(), background);
               }

               index -= type.getMax();
            }

            return null;
         }
      }
   }

   public void scroll(final double x, final double y) {
      if (this.canScrollHorizontally()) {
         this.scrollX = Mth.clamp(this.scrollX + x, (double)(-(this.maxX - 234)), 0.0);
      }

      if (this.canScrollVertically()) {
         this.scrollY = Mth.clamp(this.scrollY + y, (double)(-(this.maxY - 113)), 0.0);
      }

   }

   public boolean canScrollHorizontally() {
      return this.maxX - this.minX > 234;
   }

   public boolean canScrollVertically() {
      return this.maxY - this.minY > 113;
   }

   public void addAdvancement(final AdvancementNode node) {
      AdvancementWidget widget = AdvancementWidget.createWidget(this.minecraft, node);
      if (widget != null) {
         this.addWidget(widget);
      }

   }

   private void addWidget(final AdvancementWidget widget) {
      this.widgets.put(widget.getAdvancement(), widget);
      int x0 = widget.getX();
      int x1 = x0 + 28;
      int y0 = widget.getY();
      int y1 = y0 + 27;
      this.minX = Math.min(this.minX, x0);
      this.maxX = Math.max(this.maxX, x1);
      this.minY = Math.min(this.minY, y0);
      this.maxY = Math.max(this.maxY, y1);

      for(AdvancementWidget other : this.widgets.values()) {
         other.attachToParent(this);
      }

   }

   public @Nullable AdvancementWidget getWidget(final AdvancementHolder advancement) {
      return (AdvancementWidget)this.widgets.get(advancement);
   }

   public AdvancementsScreen getScreen() {
      return this.screen;
   }
}
