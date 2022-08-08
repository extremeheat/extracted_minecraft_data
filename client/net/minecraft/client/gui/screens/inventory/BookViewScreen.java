package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;

public class BookViewScreen extends Screen {
   public static final int PAGE_INDICATOR_TEXT_Y_OFFSET = 16;
   public static final int PAGE_TEXT_X_OFFSET = 36;
   public static final int PAGE_TEXT_Y_OFFSET = 30;
   public static final BookAccess EMPTY_ACCESS = new BookAccess() {
      public int getPageCount() {
         return 0;
      }

      public FormattedText getPageRaw(int var1) {
         return FormattedText.EMPTY;
      }
   };
   public static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/gui/book.png");
   protected static final int TEXT_WIDTH = 114;
   protected static final int TEXT_HEIGHT = 128;
   protected static final int IMAGE_WIDTH = 192;
   protected static final int IMAGE_HEIGHT = 192;
   private BookAccess bookAccess;
   private int currentPage;
   private List<FormattedCharSequence> cachedPageComponents;
   private int cachedPage;
   private Component pageMsg;
   private PageButton forwardButton;
   private PageButton backButton;
   private final boolean playTurnSound;

   public BookViewScreen(BookAccess var1) {
      this(var1, true);
   }

   public BookViewScreen() {
      this(EMPTY_ACCESS, false);
   }

   private BookViewScreen(BookAccess var1, boolean var2) {
      super(NarratorChatListener.NO_TITLE);
      this.cachedPageComponents = Collections.emptyList();
      this.cachedPage = -1;
      this.pageMsg = CommonComponents.EMPTY;
      this.bookAccess = var1;
      this.playTurnSound = var2;
   }

   public void setBookAccess(BookAccess var1) {
      this.bookAccess = var1;
      this.currentPage = Mth.clamp((int)this.currentPage, (int)0, (int)var1.getPageCount());
      this.updateButtonVisibility();
      this.cachedPage = -1;
   }

   public boolean setPage(int var1) {
      int var2 = Mth.clamp((int)var1, (int)0, (int)(this.bookAccess.getPageCount() - 1));
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
      this.addRenderableWidget(new Button(this.width / 2 - 100, 196, 200, 20, CommonComponents.GUI_DONE, (var1) -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   protected void createPageControlButtons() {
      int var1 = (this.width - 192) / 2;
      boolean var2 = true;
      this.forwardButton = (PageButton)this.addRenderableWidget(new PageButton(var1 + 116, 159, true, (var1x) -> {
         this.pageForward();
      }, this.playTurnSound));
      this.backButton = (PageButton)this.addRenderableWidget(new PageButton(var1 + 43, 159, false, (var1x) -> {
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
         switch (var1) {
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

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BOOK_LOCATION);
      int var5 = (this.width - 192) / 2;
      boolean var6 = true;
      this.blit(var1, var5, 2, 0, 0, 192, 192);
      if (this.cachedPage != this.currentPage) {
         FormattedText var7 = this.bookAccess.getPage(this.currentPage);
         this.cachedPageComponents = this.font.split(var7, 114);
         this.pageMsg = Component.translatable("book.pageIndicator", this.currentPage + 1, Math.max(this.getNumPages(), 1));
      }

      this.cachedPage = this.currentPage;
      int var11 = this.font.width((FormattedText)this.pageMsg);
      this.font.draw(var1, (Component)this.pageMsg, (float)(var5 - var11 + 192 - 44), 18.0F, 0);
      Objects.requireNonNull(this.font);
      int var8 = Math.min(128 / 9, this.cachedPageComponents.size());

      for(int var9 = 0; var9 < var8; ++var9) {
         FormattedCharSequence var10 = (FormattedCharSequence)this.cachedPageComponents.get(var9);
         Font var10000 = this.font;
         float var10003 = (float)(var5 + 36);
         Objects.requireNonNull(this.font);
         var10000.draw(var1, (FormattedCharSequence)var10, var10003, (float)(32 + var9 * 9), 0);
      }

      Style var12 = this.getClickedComponentStyleAt((double)var2, (double)var3);
      if (var12 != null) {
         this.renderComponentHoverEffect(var1, var12, var2, var3);
      }

      super.render(var1, var2, var3, var4);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         Style var6 = this.getClickedComponentStyleAt(var1, var3);
         if (var6 != null && this.handleComponentClicked(var6)) {
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean handleComponentClicked(Style var1) {
      ClickEvent var2 = var1.getClickEvent();
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
            this.closeScreen();
         }

         return var3;
      }
   }

   protected void closeScreen() {
      this.minecraft.setScreen((Screen)null);
   }

   @Nullable
   public Style getClickedComponentStyleAt(double var1, double var3) {
      if (this.cachedPageComponents.isEmpty()) {
         return null;
      } else {
         int var5 = Mth.floor(var1 - (double)((this.width - 192) / 2) - 36.0);
         int var6 = Mth.floor(var3 - 2.0 - 30.0);
         if (var5 >= 0 && var6 >= 0) {
            Objects.requireNonNull(this.font);
            int var7 = Math.min(128 / 9, this.cachedPageComponents.size());
            if (var5 <= 114) {
               Objects.requireNonNull(this.minecraft.font);
               if (var6 < 9 * var7 + var7) {
                  Objects.requireNonNull(this.minecraft.font);
                  int var8 = var6 / 9;
                  if (var8 >= 0 && var8 < this.cachedPageComponents.size()) {
                     FormattedCharSequence var9 = (FormattedCharSequence)this.cachedPageComponents.get(var8);
                     return this.minecraft.font.getSplitter().componentStyleAtWidth(var9, var5);
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

   static List<String> loadPages(CompoundTag var0) {
      ImmutableList.Builder var1 = ImmutableList.builder();
      Objects.requireNonNull(var1);
      loadPages(var0, var1::add);
      return var1.build();
   }

   public static void loadPages(CompoundTag var0, Consumer<String> var1) {
      ListTag var3 = var0.getList("pages", 8).copy();
      IntFunction var2;
      if (Minecraft.getInstance().isTextFilteringEnabled() && var0.contains("filtered_pages", 10)) {
         CompoundTag var4 = var0.getCompound("filtered_pages");
         var2 = (var2x) -> {
            String var3x = String.valueOf(var2x);
            return var4.contains(var3x) ? var4.getString(var3x) : var3.getString(var2x);
         };
      } else {
         Objects.requireNonNull(var3);
         var2 = var3::getString;
      }

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         var1.accept((String)var2.apply(var5));
      }

   }

   public interface BookAccess {
      int getPageCount();

      FormattedText getPageRaw(int var1);

      default FormattedText getPage(int var1) {
         return var1 >= 0 && var1 < this.getPageCount() ? this.getPageRaw(var1) : FormattedText.EMPTY;
      }

      static BookAccess fromItem(ItemStack var0) {
         if (var0.is(Items.WRITTEN_BOOK)) {
            return new WrittenBookAccess(var0);
         } else {
            return (BookAccess)(var0.is(Items.WRITABLE_BOOK) ? new WritableBookAccess(var0) : BookViewScreen.EMPTY_ACCESS);
         }
      }
   }

   public static class WritableBookAccess implements BookAccess {
      private final List<String> pages;

      public WritableBookAccess(ItemStack var1) {
         super();
         this.pages = readPages(var1);
      }

      private static List<String> readPages(ItemStack var0) {
         CompoundTag var1 = var0.getTag();
         return (List)(var1 != null ? BookViewScreen.loadPages(var1) : ImmutableList.of());
      }

      public int getPageCount() {
         return this.pages.size();
      }

      public FormattedText getPageRaw(int var1) {
         return FormattedText.of((String)this.pages.get(var1));
      }
   }

   public static class WrittenBookAccess implements BookAccess {
      private final List<String> pages;

      public WrittenBookAccess(ItemStack var1) {
         super();
         this.pages = readPages(var1);
      }

      private static List<String> readPages(ItemStack var0) {
         CompoundTag var1 = var0.getTag();
         return (List)(var1 != null && WrittenBookItem.makeSureTagIsValid(var1) ? BookViewScreen.loadPages(var1) : ImmutableList.of(Component.Serializer.toJson(Component.translatable("book.invalid.tag").withStyle(ChatFormatting.DARK_RED))));
      }

      public int getPageCount() {
         return this.pages.size();
      }

      public FormattedText getPageRaw(int var1) {
         String var2 = (String)this.pages.get(var1);

         try {
            MutableComponent var3 = Component.Serializer.fromJson(var2);
            if (var3 != null) {
               return var3;
            }
         } catch (Exception var4) {
         }

         return FormattedText.of(var2);
      }
   }
}
