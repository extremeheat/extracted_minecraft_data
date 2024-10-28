package net.minecraft.client.gui.screens.multiplayer;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerLinks;

public class ServerLinksScreen extends Screen {
   private static final int LINK_BUTTON_WIDTH = 310;
   private static final int DEFAULT_ITEM_HEIGHT = 25;
   private static final Component TITLE = Component.translatable("menu.server_links.title");
   private final Screen lastScreen;
   @Nullable
   private LinkList list;
   final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   final ServerLinks links;

   public ServerLinksScreen(Screen var1, ServerLinks var2) {
      super(TITLE);
      this.lastScreen = var1;
      this.links = var2;
   }

   protected void init() {
      this.layout.addTitleHeader(this.title, this.font);
      this.list = (LinkList)this.layout.addToContents(new LinkList(this.minecraft, this.width, this));
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (var1) -> {
         this.onClose();
      }).width(200).build());
      this.layout.visitWidgets((var1) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.list != null) {
         this.list.updateSize(this.width, this.layout);
      }

   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private static class LinkList extends ContainerObjectSelectionList<LinkListEntry> {
      public LinkList(Minecraft var1, int var2, ServerLinksScreen var3) {
         super(var1, var2, var3.layout.getContentHeight(), var3.layout.getHeaderHeight(), 25);
         var3.links.entries().forEach((var2x) -> {
            this.addEntry(new LinkListEntry(var3, var2x));
         });
      }

      public int getRowWidth() {
         return 310;
      }

      public void updateSize(int var1, HeaderAndFooterLayout var2) {
         super.updateSize(var1, var2);
         int var3 = var1 / 2 - 155;
         this.children().forEach((var1x) -> {
            var1x.button.setX(var3);
         });
      }
   }

   private static class LinkListEntry extends ContainerObjectSelectionList.Entry<LinkListEntry> {
      final AbstractWidget button;

      LinkListEntry(Screen var1, ServerLinks.Entry var2) {
         super();
         this.button = Button.builder(var2.displayName(), ConfirmLinkScreen.confirmLink(var1, var2.url(), false)).width(310).build();
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.button.setY(var3);
         this.button.render(var1, var7, var8, var10);
      }

      public List<? extends GuiEventListener> children() {
         return List.of(this.button);
      }

      public List<? extends NarratableEntry> narratables() {
         return List.of(this.button);
      }
   }
}
