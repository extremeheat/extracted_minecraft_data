package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.Map;
import java.util.Objects;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class AdvancementsScreen extends Screen implements ClientAdvancements.Listener {
   private static final Identifier WINDOW_LOCATION = Identifier.withDefaultNamespace("textures/gui/advancements/window.png");
   public static final int WINDOW_WIDTH = 252;
   public static final int WINDOW_HEIGHT = 140;
   private static final int WINDOW_INSIDE_X = 9;
   private static final int WINDOW_INSIDE_Y = 18;
   public static final int WINDOW_INSIDE_WIDTH = 234;
   public static final int WINDOW_INSIDE_HEIGHT = 113;
   private static final int WINDOW_TITLE_X = 8;
   private static final int WINDOW_TITLE_Y = 6;
   private static final int BACKGROUND_TEXTURE_WIDTH = 256;
   private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   public static final int BACKGROUND_TILE_WIDTH = 16;
   public static final int BACKGROUND_TILE_HEIGHT = 16;
   public static final int BACKGROUND_TILE_COUNT_X = 14;
   public static final int BACKGROUND_TILE_COUNT_Y = 7;
   private static final double SCROLL_SPEED = 16.0;
   private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
   private static final Component NO_ADVANCEMENTS_LABEL = Component.translatable("advancements.empty");
   private static final Component TITLE = Component.translatable("gui.advancements");
   private final HeaderAndFooterLayout layout;
   private final @Nullable Screen lastScreen;
   private int leftPos;
   private int topPos;
   private final ClientAdvancements advancements;
   private final Map<AdvancementHolder, AdvancementTab> tabs;
   private @Nullable AdvancementTab selectedTab;
   private boolean isScrolling;

   public AdvancementsScreen(final ClientAdvancements advancements) {
      this(advancements, (Screen)null);
   }

   public AdvancementsScreen(final ClientAdvancements advancements, final @Nullable Screen lastScreen) {
      super(TITLE);
      this.layout = new HeaderAndFooterLayout(this);
      this.tabs = Maps.newLinkedHashMap();
      this.advancements = advancements;
      this.lastScreen = lastScreen;
   }

   protected void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      this.tabs.clear();
      this.selectedTab = null;
      this.advancements.setListener(this);
      if (this.selectedTab == null && !this.tabs.isEmpty()) {
         AdvancementTab firstTab = (AdvancementTab)this.tabs.values().iterator().next();
         this.advancements.setSelectedTab(firstTab.getRootAdvancement(), true);
      } else {
         this.advancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getRootAdvancement(), true);
      }

      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(200).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
   }

   protected void repositionElements() {
      this.leftPos = (this.width - 252) / 2;
      this.topPos = (this.height - 140) / 2;
      this.layout.arrangeElements();
   }

   public void onClose() {
      this.minecraft.gui.setScreen(this.lastScreen);
   }

   public void removed() {
      this.advancements.setListener((ClientAdvancements.Listener)null);
      ClientPacketListener connection = this.minecraft.getConnection();
      if (connection != null) {
         connection.send(ServerboundSeenAdvancementsPacket.closedScreen());
      }

   }

   public void tick() {
      super.tick();
      if (this.selectedTab != null) {
         int mouseX = (int)this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
         int mouseY = (int)this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
         this.selectedTab.tick(mouseX - this.leftPos - 9, mouseY - this.topPos - 18);
      }

   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (event.button() == 1) {
         int xo = (this.width - 252) / 2;
         int yo = (this.height - 140) / 2;

         for(AdvancementTab tab : this.tabs.values()) {
            if (tab.isMouseOver(xo, yo, event.x(), event.y())) {
               this.advancements.setSelectedTab(tab.getRootAdvancement(), true);
               break;
            }
         }
      }

      return super.mouseClicked(event, doubleClick);
   }

   public boolean keyPressed(final KeyEvent event) {
      if (this.minecraft.options.keyAdvancements.matches(event)) {
         if (this.lastScreen != null) {
            this.minecraft.gui.setScreen(this.lastScreen);
         } else {
            this.minecraft.mouseHandler.grabMouse();
         }

         return true;
      } else {
         return super.keyPressed(event);
      }
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      graphics.nextStratum();
      this.extractInside(graphics);
      graphics.nextStratum();
      this.extractWindow(graphics, mouseX, mouseY);
      if (this.isScrolling && this.selectedTab != null) {
         if (this.selectedTab.canScrollHorizontally() && this.selectedTab.canScrollVertically()) {
            graphics.requestCursor(CursorTypes.RESIZE_ALL);
         } else if (this.selectedTab.canScrollHorizontally()) {
            graphics.requestCursor(CursorTypes.RESIZE_EW);
         } else if (this.selectedTab.canScrollVertically()) {
            graphics.requestCursor(CursorTypes.RESIZE_NS);
         }
      }

      this.extractTooltips(graphics, mouseX, mouseY);
   }

   public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
      if (event.button() != 1) {
         this.isScrolling = false;
         return false;
      } else {
         if (!this.isScrolling) {
            this.isScrolling = true;
         } else if (this.selectedTab != null) {
            this.selectedTab.scroll(dx, dy);
         }

         return true;
      }
   }

   public boolean mouseReleased(final MouseButtonEvent event) {
      this.isScrolling = false;
      return super.mouseReleased(event);
   }

   public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
      if (this.selectedTab != null) {
         this.selectedTab.scroll(scrollX * 16.0, scrollY * 16.0);
         return true;
      } else {
         return false;
      }
   }

   private void extractInside(final GuiGraphicsExtractor graphics) {
      AdvancementTab tab = this.selectedTab;
      if (tab == null) {
         graphics.fill(this.leftPos + 9, this.topPos + 18, this.leftPos + 9 + 234, this.topPos + 18 + 113, -16777216);
         int midX = this.leftPos + 9 + 117;
         Font var10001 = this.font;
         Component var10002 = NO_ADVANCEMENTS_LABEL;
         int var10004 = this.topPos + 18 + 56;
         Objects.requireNonNull(this.font);
         graphics.centeredText(var10001, (Component)var10002, midX, var10004 - 9 / 2, -1);
         var10001 = this.font;
         var10002 = VERY_SAD_LABEL;
         var10004 = this.topPos + 18 + 113;
         Objects.requireNonNull(this.font);
         graphics.centeredText(var10001, (Component)var10002, midX, var10004 - 9, -1);
      } else {
         tab.extractContents(graphics, this.leftPos + 9, this.topPos + 18);
      }
   }

   public void extractWindow(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
      graphics.blit(RenderPipelines.GUI_TEXTURED, WINDOW_LOCATION, this.leftPos, this.topPos, 0.0F, 0.0F, 252, 140, 256, 256);
      if (this.tabs.size() > 1) {
         for(AdvancementTab tab : this.tabs.values()) {
            tab.extractTab(graphics, this.leftPos, this.topPos, mouseX, mouseY, tab == this.selectedTab);
         }

         for(AdvancementTab tab : this.tabs.values()) {
            tab.extractIcon(graphics, this.leftPos, this.topPos);
         }
      }

      graphics.text(this.font, this.selectedTab != null ? this.selectedTab.getTitle() : TITLE, this.leftPos + 8, this.topPos + 6, -12566464, false);
   }

   private void extractTooltips(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
      if (this.selectedTab != null) {
         graphics.pose().pushMatrix();
         graphics.pose().translate((float)(this.leftPos + 9), (float)(this.topPos + 18));
         graphics.nextStratum();
         this.selectedTab.extractTooltips(graphics, this.leftPos, this.topPos);
         graphics.pose().popMatrix();
      }

      if (this.tabs.size() > 1) {
         for(AdvancementTab tab : this.tabs.values()) {
            if (tab.isMouseOver(this.leftPos, this.topPos, (double)mouseX, (double)mouseY)) {
               graphics.setTooltipForNextFrame(this.font, tab.getTitle(), mouseX, mouseY);
            }
         }
      }

   }

   public void onAdvancementsUpdated() {
      Map<AdvancementHolder, AdvancementTab> oldTabs = Map.copyOf(this.tabs);
      this.tabs.clear();
      AdvancementTree tree = this.advancements.tree();

      for(AdvancementNode root : tree.roots()) {
         AdvancementHolder rootHolder = root.holder();
         if (!this.tabs.containsKey(rootHolder)) {
            AdvancementTab newTab = AdvancementTab.create(this.minecraft, this, this.tabs.size(), root);
            if (newTab != null) {
               AdvancementTab oldTab = (AdvancementTab)oldTabs.get(rootHolder);
               if (oldTab != null) {
                  newTab.copyPosition(oldTab);
               }

               this.tabs.put(rootHolder, newTab);
            }
         }
      }

      for(AdvancementNode task : tree.tasks()) {
         AdvancementTab tab = this.getTab(task);
         if (tab != null) {
            tab.addAdvancement(task);
         }
      }

      this.advancements.progress().forEach((holder, progress) -> {
         AdvancementNode node = tree.get(holder);
         if (node != null) {
            AdvancementWidget widget = this.getAdvancementWidget(node);
            if (widget != null) {
               widget.setProgress(progress);
            }
         }

      });
      if (this.selectedTab != null) {
         this.selectedTab = (AdvancementTab)this.tabs.get(this.selectedTab.getRootAdvancement());
      }

      if (this.selectedTab == null && !this.tabs.isEmpty()) {
         this.selectedTab = (AdvancementTab)this.tabs.values().iterator().next();
         this.advancements.setSelectedTab(this.selectedTab.getRootAdvancement(), true);
      }

   }

   public void onSelectedTabChanged(final @Nullable AdvancementHolder selectedTab) {
      this.selectedTab = (AdvancementTab)this.tabs.get(selectedTab);
   }

   public void onAdvancementsCleared() {
      this.tabs.clear();
      this.selectedTab = null;
   }

   public @Nullable AdvancementWidget getAdvancementWidget(final AdvancementNode node) {
      AdvancementTab tab = this.getTab(node);
      return tab == null ? null : tab.getWidget(node.holder());
   }

   private @Nullable AdvancementTab getTab(final AdvancementNode node) {
      AdvancementNode root = node.root();
      return (AdvancementTab)this.tabs.get(root.holder());
   }
}
