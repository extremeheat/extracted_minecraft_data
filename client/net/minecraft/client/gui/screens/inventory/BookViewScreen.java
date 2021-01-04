package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;

public class BookViewScreen extends Screen {
   public static final BookViewScreen.BookAccess EMPTY_ACCESS = new BookViewScreen.BookAccess() {
      public int getPageCount() {
         return 0;
      }

      public Component getPageRaw(int var1) {
         return new TextComponent("");
      }
   };
   public static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/gui/book.png");
   private BookViewScreen.BookAccess bookAccess;
   private int currentPage;
   private List<Component> cachedPageComponents;
   private int cachedPage;
   private PageButton forwardButton;
   private PageButton backButton;
   private final boolean playTurnSound;

   public BookViewScreen(BookViewScreen.BookAccess var1) {
      this(var1, true);
   }

   public BookViewScreen() {
      this(EMPTY_ACCESS, false);
   }

   private BookViewScreen(BookViewScreen.BookAccess var1, boolean var2) {
      super(NarratorChatListener.NO_TITLE);
      this.cachedPageComponents = Collections.emptyList();
      this.cachedPage = -1;
      this.bookAccess = var1;
      this.playTurnSound = var2;
   }

   public void setBookAccess(BookViewScreen.BookAccess var1) {
      this.bookAccess = var1;
      this.currentPage = Mth.clamp(this.currentPage, 0, var1.getPageCount());
      this.updateButtonVisibility();
      this.cachedPage = -1;
   }

   public boolean setPage(int var1) {
      int var2 = Mth.clamp(var1, 0, this.bookAccess.getPageCount() - 1);
      if (var2 != this.currentPage) {
         this.currentPage = var2;
         this.updateButtonVisibility();
         this.cachedPage = -1;
         return true;
      } else {
         return false;
      }
   }

   protected boolean forcePage(int var1) {
      return this.setPage(var1);
   }

   protected void init() {
      this.createMenuControls();
      this.createPageControlButtons();
   }

   protected void createMenuControls() {
      this.addButton(new Button(this.width / 2 - 100, 196, 200, 20, I18n.get("gui.done"), (var1) -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   protected void createPageControlButtons() {
      int var1 = (this.width - 192) / 2;
      boolean var2 = true;
      this.forwardButton = (PageButton)this.addButton(new PageButton(var1 + 116, 159, true, (var1x) -> {
         this.pageForward();
      }, this.playTurnSound));
      this.backButton = (PageButton)this.addButton(new PageButton(var1 + 43, 159, false, (var1x) -> {
         this.pageBack();
      }, this.playTurnSound));
      this.updateButtonVisibility();
   }

   private int getNumPages() {
      return this.bookAccess.getPageCount();
   }

   protected void pageBack() {
      if (this.currentPage > 0) {
         --this.currentPage;
      }

      this.updateButtonVisibility();
   }

   protected void pageForward() {
      if (this.currentPage < this.getNumPages() - 1) {
         ++this.currentPage;
      }

      this.updateButtonVisibility();
   }

   private void updateButtonVisibility() {
      this.forwardButton.visible = this.currentPage < this.getNumPages() - 1;
      this.backButton.visible = this.currentPage > 0;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         switch(var1) {
         case 266:
            this.backButton.onPress();
            return true;
         case 267:
            this.forwardButton.onPress();
            return true;
         default:
            return false;
         }
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BOOK_LOCATION);
      int var4 = (this.width - 192) / 2;
      boolean var5 = true;
      this.blit(var4, 2, 0, 0, 192, 192);
      String var6 = I18n.get("book.pageIndicator", this.currentPage + 1, Math.max(this.getNumPages(), 1));
      if (this.cachedPage != this.currentPage) {
         Component var7 = this.bookAccess.getPage(this.currentPage);
         this.cachedPageComponents = ComponentRenderUtils.wrapComponents(var7, 114, this.font, true, true);
      }

      this.cachedPage = this.currentPage;
      int var11 = this.strWidth(var6);
      this.font.draw(var6, (float)(var4 - var11 + 192 - 44), 18.0F, 0);
      this.font.getClass();
      int var8 = Math.min(128 / 9, this.cachedPageComponents.size());

      for(int var9 = 0; var9 < var8; ++var9) {
         Component var10 = (Component)this.cachedPageComponents.get(var9);
         Font var10000 = this.font;
         String var10001 = var10.getColoredString();
         float var10002 = (float)(var4 + 36);
         this.font.getClass();
         var10000.draw(var10001, var10002, (float)(32 + var9 * 9), 0);
      }

      Component var12 = this.getClickedComponentAt((double)var1, (double)var2);
      if (var12 != null) {
         this.renderComponentHoverEffect(var12, var1, var2);
      }

      super.render(var1, var2, var3);
   }

   private int strWidth(String var1) {
      return this.font.width(this.font.isBidirectional() ? this.font.bidirectionalShaping(var1) : var1);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         Component var6 = this.getClickedComponentAt(var1, var3);
         if (var6 != null && this.handleComponentClicked(var6)) {
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean handleComponentClicked(Component var1) {
      ClickEvent var2 = var1.getStyle().getClickEvent();
      if (var2 == null) {
         return false;
      } else if (var2.getAction() == ClickEvent.Action.CHANGE_PAGE) {
         String var6 = var2.getValue();

         try {
            int var4 = Integer.parseInt(var6) - 1;
            return this.forcePage(var4);
         } catch (Exception var5) {
            return false;
         }
      } else {
         boolean var3 = super.handleComponentClicked(var1);
         if (var3 && var2.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.minecraft.setScreen((Screen)null);
         }

         return var3;
      }
   }

   @Nullable
   public Component getClickedComponentAt(double var1, double var3) {
      if (this.cachedPageComponents == null) {
         return null;
      } else {
         int var5 = Mth.floor(var1 - (double)((this.width - 192) / 2) - 36.0D);
         int var6 = Mth.floor(var3 - 2.0D - 30.0D);
         if (var5 >= 0 && var6 >= 0) {
            this.font.getClass();
            int var7 = Math.min(128 / 9, this.cachedPageComponents.size());
            if (var5 <= 114) {
               this.minecraft.font.getClass();
               if (var6 < 9 * var7 + var7) {
                  this.minecraft.font.getClass();
                  int var8 = var6 / 9;
                  if (var8 >= 0 && var8 < this.cachedPageComponents.size()) {
                     Component var9 = (Component)this.cachedPageComponents.get(var8);
                     int var10 = 0;
                     Iterator var11 = var9.iterator();

                     while(var11.hasNext()) {
                        Component var12 = (Component)var11.next();
                        if (var12 instanceof TextComponent) {
                           var10 += this.minecraft.font.width(var12.getColoredString());
                           if (var10 > var5) {
                              return var12;
                           }
                        }
                     }
                  }

                  return null;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public static List<String> convertPages(CompoundTag var0) {
      ListTag var1 = var0.getList("pages", 8).copy();
      Builder var2 = ImmutableList.builder();

      for(int var3 = 0; var3 < var1.size(); ++var3) {
         var2.add(var1.getString(var3));
      }

      return var2.build();
   }

   public static class WritableBookAccess implements BookViewScreen.BookAccess {
      private final List<String> pages;

      public WritableBookAccess(ItemStack var1) {
         super();
         this.pages = readPages(var1);
      }

      private static List<String> readPages(ItemStack var0) {
         CompoundTag var1 = var0.getTag();
         return (List)(var1 != null ? BookViewScreen.convertPages(var1) : ImmutableList.of());
      }

      public int getPageCount() {
         return this.pages.size();
      }

      public Component getPageRaw(int var1) {
         return new TextComponent((String)this.pages.get(var1));
      }
   }

   public static class WrittenBookAccess implements BookViewScreen.BookAccess {
      private final List<String> pages;

      public WrittenBookAccess(ItemStack var1) {
         super();
         this.pages = readPages(var1);
      }

      private static List<String> readPages(ItemStack var0) {
         CompoundTag var1 = var0.getTag();
         return (List)(var1 != null && WrittenBookItem.makeSureTagIsValid(var1) ? BookViewScreen.convertPages(var1) : ImmutableList.of((new TranslatableComponent("book.invalid.tag", new Object[0])).withStyle(ChatFormatting.DARK_RED).getColoredString()));
      }

      public int getPageCount() {
         return this.pages.size();
      }

      public Component getPageRaw(int var1) {
         String var2 = (String)this.pages.get(var1);

         try {
            Component var3 = Component.Serializer.fromJson(var2);
            if (var3 != null) {
               return var3;
            }
         } catch (Exception var4) {
         }

         return new TextComponent(var2);
      }
   }

   public interface BookAccess {
      int getPageCount();

      Component getPageRaw(int var1);

      default Component getPage(int var1) {
         return (Component)(var1 >= 0 && var1 < this.getPageCount() ? this.getPageRaw(var1) : new TextComponent(""));
      }

      static BookViewScreen.BookAccess fromItem(ItemStack var0) {
         Item var1 = var0.getItem();
         if (var1 == Items.WRITTEN_BOOK) {
            return new BookViewScreen.WrittenBookAccess(var0);
         } else {
            return (BookViewScreen.BookAccess)(var1 == Items.WRITABLE_BOOK ? new BookViewScreen.WritableBookAccess(var0) : BookViewScreen.EMPTY_ACCESS);
         }
      }
   }
}
