package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public abstract class AbstractSelectionList<E extends AbstractSelectionList.Entry<E>> extends AbstractContainerEventHandler implements Widget, NarratableEntry {
   protected final Minecraft minecraft;
   protected final int itemHeight;
   private final List<E> children = new AbstractSelectionList.TrackedList();
   protected int width;
   protected int height;
   // $FF: renamed from: y0 int
   protected int field_31;
   // $FF: renamed from: y1 int
   protected int field_32;
   // $FF: renamed from: x1 int
   protected int field_33;
   // $FF: renamed from: x0 int
   protected int field_34;
   protected boolean centerListVertically = true;
   private double scrollAmount;
   private boolean renderSelection = true;
   private boolean renderHeader;
   protected int headerHeight;
   private boolean scrolling;
   @Nullable
   private E selected;
   private boolean renderBackground = true;
   private boolean renderTopAndBottom = true;
   @Nullable
   private E hovered;

   public AbstractSelectionList(Minecraft var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.minecraft = var1;
      this.width = var2;
      this.height = var3;
      this.field_31 = var4;
      this.field_32 = var5;
      this.itemHeight = var6;
      this.field_34 = 0;
      this.field_33 = var2;
   }

   public void setRenderSelection(boolean var1) {
      this.renderSelection = var1;
   }

   protected void setRenderHeader(boolean var1, int var2) {
      this.renderHeader = var1;
      this.headerHeight = var2;
      if (!var1) {
         this.headerHeight = 0;
      }

   }

   public int getRowWidth() {
      return 220;
   }

   @Nullable
   public E getSelected() {
      return this.selected;
   }

   public void setSelected(@Nullable E var1) {
      this.selected = var1;
   }

   public void setRenderBackground(boolean var1) {
      this.renderBackground = var1;
   }

   public void setRenderTopAndBottom(boolean var1) {
      this.renderTopAndBottom = var1;
   }

   @Nullable
   public E getFocused() {
      return (AbstractSelectionList.Entry)super.getFocused();
   }

   public final List<E> children() {
      return this.children;
   }

   protected final void clearEntries() {
      this.children.clear();
   }

   protected void replaceEntries(Collection<E> var1) {
      this.children.clear();
      this.children.addAll(var1);
   }

   protected E getEntry(int var1) {
      return (AbstractSelectionList.Entry)this.children().get(var1);
   }

   protected int addEntry(E var1) {
      this.children.add(var1);
      return this.children.size() - 1;
   }

   protected int getItemCount() {
      return this.children().size();
   }

   protected boolean isSelectedItem(int var1) {
      return Objects.equals(this.getSelected(), this.children().get(var1));
   }

   @Nullable
   protected final E getEntryAtPosition(double var1, double var3) {
      int var5 = this.getRowWidth() / 2;
      int var6 = this.field_34 + this.width / 2;
      int var7 = var6 - var5;
      int var8 = var6 + var5;
      int var9 = Mth.floor(var3 - (double)this.field_31) - this.headerHeight + (int)this.getScrollAmount() - 4;
      int var10 = var9 / this.itemHeight;
      return var1 < (double)this.getScrollbarPosition() && var1 >= (double)var7 && var1 <= (double)var8 && var10 >= 0 && var9 >= 0 && var10 < this.getItemCount() ? (AbstractSelectionList.Entry)this.children().get(var10) : null;
   }

   public void updateSize(int var1, int var2, int var3, int var4) {
      this.width = var1;
      this.height = var2;
      this.field_31 = var3;
      this.field_32 = var4;
      this.field_34 = 0;
      this.field_33 = var1;
   }

   public void setLeftPos(int var1) {
      this.field_34 = var1;
      this.field_33 = var1 + this.width;
   }

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected void clickedHeader(int var1, int var2) {
   }

   protected void renderHeader(PoseStack var1, int var2, int var3, Tesselator var4) {
   }

   protected void renderBackground(PoseStack var1) {
   }

   protected void renderDecorations(PoseStack var1, int var2, int var3) {
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      int var5 = this.getScrollbarPosition();
      int var6 = var5 + 6;
      Tesselator var7 = Tesselator.getInstance();
      BufferBuilder var8 = var7.getBuilder();
      RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
      this.hovered = this.isMouseOver((double)var2, (double)var3) ? this.getEntryAtPosition((double)var2, (double)var3) : null;
      if (this.renderBackground) {
         RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         float var9 = 32.0F;
         var8.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         var8.vertex((double)this.field_34, (double)this.field_32, 0.0D).method_7((float)this.field_34 / 32.0F, (float)(this.field_32 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         var8.vertex((double)this.field_33, (double)this.field_32, 0.0D).method_7((float)this.field_33 / 32.0F, (float)(this.field_32 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         var8.vertex((double)this.field_33, (double)this.field_31, 0.0D).method_7((float)this.field_33 / 32.0F, (float)(this.field_31 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         var8.vertex((double)this.field_34, (double)this.field_31, 0.0D).method_7((float)this.field_34 / 32.0F, (float)(this.field_31 + (int)this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
         var7.end();
      }

      int var14 = this.getRowLeft();
      int var10 = this.field_31 + 4 - (int)this.getScrollAmount();
      if (this.renderHeader) {
         this.renderHeader(var1, var14, var10, var7);
      }

      this.renderList(var1, var14, var10, var2, var3, var4);
      if (this.renderTopAndBottom) {
         RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
         RenderSystem.setShaderTexture(0, GuiComponent.BACKGROUND_LOCATION);
         RenderSystem.enableDepthTest();
         RenderSystem.depthFunc(519);
         float var11 = 32.0F;
         boolean var12 = true;
         var8.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
         var8.vertex((double)this.field_34, (double)this.field_31, -100.0D).method_7(0.0F, (float)this.field_31 / 32.0F).color(64, 64, 64, 255).endVertex();
         var8.vertex((double)(this.field_34 + this.width), (double)this.field_31, -100.0D).method_7((float)this.width / 32.0F, (float)this.field_31 / 32.0F).color(64, 64, 64, 255).endVertex();
         var8.vertex((double)(this.field_34 + this.width), 0.0D, -100.0D).method_7((float)this.width / 32.0F, 0.0F).color(64, 64, 64, 255).endVertex();
         var8.vertex((double)this.field_34, 0.0D, -100.0D).method_7(0.0F, 0.0F).color(64, 64, 64, 255).endVertex();
         var8.vertex((double)this.field_34, (double)this.height, -100.0D).method_7(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
         var8.vertex((double)(this.field_34 + this.width), (double)this.height, -100.0D).method_7((float)this.width / 32.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
         var8.vertex((double)(this.field_34 + this.width), (double)this.field_32, -100.0D).method_7((float)this.width / 32.0F, (float)this.field_32 / 32.0F).color(64, 64, 64, 255).endVertex();
         var8.vertex((double)this.field_34, (double)this.field_32, -100.0D).method_7(0.0F, (float)this.field_32 / 32.0F).color(64, 64, 64, 255).endVertex();
         var7.end();
         RenderSystem.depthFunc(515);
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         RenderSystem.disableTexture();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         boolean var13 = true;
         var8.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         var8.vertex((double)this.field_34, (double)(this.field_31 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
         var8.vertex((double)this.field_33, (double)(this.field_31 + 4), 0.0D).color(0, 0, 0, 0).endVertex();
         var8.vertex((double)this.field_33, (double)this.field_31, 0.0D).color(0, 0, 0, 255).endVertex();
         var8.vertex((double)this.field_34, (double)this.field_31, 0.0D).color(0, 0, 0, 255).endVertex();
         var8.vertex((double)this.field_34, (double)this.field_32, 0.0D).color(0, 0, 0, 255).endVertex();
         var8.vertex((double)this.field_33, (double)this.field_32, 0.0D).color(0, 0, 0, 255).endVertex();
         var8.vertex((double)this.field_33, (double)(this.field_32 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
         var8.vertex((double)this.field_34, (double)(this.field_32 - 4), 0.0D).color(0, 0, 0, 0).endVertex();
         var7.end();
      }

      int var15 = this.getMaxScroll();
      if (var15 > 0) {
         RenderSystem.disableTexture();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         int var16 = (int)((float)((this.field_32 - this.field_31) * (this.field_32 - this.field_31)) / (float)this.getMaxPosition());
         var16 = Mth.clamp((int)var16, (int)32, (int)(this.field_32 - this.field_31 - 8));
         int var17 = (int)this.getScrollAmount() * (this.field_32 - this.field_31 - var16) / var15 + this.field_31;
         if (var17 < this.field_31) {
            var17 = this.field_31;
         }

         var8.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         var8.vertex((double)var5, (double)this.field_32, 0.0D).color(0, 0, 0, 255).endVertex();
         var8.vertex((double)var6, (double)this.field_32, 0.0D).color(0, 0, 0, 255).endVertex();
         var8.vertex((double)var6, (double)this.field_31, 0.0D).color(0, 0, 0, 255).endVertex();
         var8.vertex((double)var5, (double)this.field_31, 0.0D).color(0, 0, 0, 255).endVertex();
         var8.vertex((double)var5, (double)(var17 + var16), 0.0D).color(128, 128, 128, 255).endVertex();
         var8.vertex((double)var6, (double)(var17 + var16), 0.0D).color(128, 128, 128, 255).endVertex();
         var8.vertex((double)var6, (double)var17, 0.0D).color(128, 128, 128, 255).endVertex();
         var8.vertex((double)var5, (double)var17, 0.0D).color(128, 128, 128, 255).endVertex();
         var8.vertex((double)var5, (double)(var17 + var16 - 1), 0.0D).color(192, 192, 192, 255).endVertex();
         var8.vertex((double)(var6 - 1), (double)(var17 + var16 - 1), 0.0D).color(192, 192, 192, 255).endVertex();
         var8.vertex((double)(var6 - 1), (double)var17, 0.0D).color(192, 192, 192, 255).endVertex();
         var8.vertex((double)var5, (double)var17, 0.0D).color(192, 192, 192, 255).endVertex();
         var7.end();
      }

      this.renderDecorations(var1, var2, var3);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
   }

   protected void centerScrollOn(E var1) {
      this.setScrollAmount((double)(this.children().indexOf(var1) * this.itemHeight + this.itemHeight / 2 - (this.field_32 - this.field_31) / 2));
   }

   protected void ensureVisible(E var1) {
      int var2 = this.getRowTop(this.children().indexOf(var1));
      int var3 = var2 - this.field_31 - 4 - this.itemHeight;
      if (var3 < 0) {
         this.scroll(var3);
      }

      int var4 = this.field_32 - var2 - this.itemHeight - this.itemHeight;
      if (var4 < 0) {
         this.scroll(-var4);
      }

   }

   private void scroll(int var1) {
      this.setScrollAmount(this.getScrollAmount() + (double)var1);
   }

   public double getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double var1) {
      this.scrollAmount = Mth.clamp(var1, 0.0D, (double)this.getMaxScroll());
   }

   public int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - (this.field_32 - this.field_31 - 4));
   }

   public int getScrollBottom() {
      return (int)this.getScrollAmount() - this.height - this.headerHeight;
   }

   protected void updateScrollingState(double var1, double var3, int var5) {
      this.scrolling = var5 == 0 && var1 >= (double)this.getScrollbarPosition() && var1 < (double)(this.getScrollbarPosition() + 6);
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.updateScrollingState(var1, var3, var5);
      if (!this.isMouseOver(var1, var3)) {
         return false;
      } else {
         AbstractSelectionList.Entry var6 = this.getEntryAtPosition(var1, var3);
         if (var6 != null) {
            if (var6.mouseClicked(var1, var3, var5)) {
               this.setFocused(var6);
               this.setDragging(true);
               return true;
            }
         } else if (var5 == 0) {
            this.clickedHeader((int)(var1 - (double)(this.field_34 + this.width / 2 - this.getRowWidth() / 2)), (int)(var3 - (double)this.field_31) + (int)this.getScrollAmount() - 4);
            return true;
         }

         return this.scrolling;
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (this.getFocused() != null) {
         this.getFocused().mouseReleased(var1, var3, var5);
      }

      return false;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (super.mouseDragged(var1, var3, var5, var6, var8)) {
         return true;
      } else if (var5 == 0 && this.scrolling) {
         if (var3 < (double)this.field_31) {
            this.setScrollAmount(0.0D);
         } else if (var3 > (double)this.field_32) {
            this.setScrollAmount((double)this.getMaxScroll());
         } else {
            double var10 = (double)Math.max(1, this.getMaxScroll());
            int var12 = this.field_32 - this.field_31;
            int var13 = Mth.clamp((int)((int)((float)(var12 * var12) / (float)this.getMaxPosition())), (int)32, (int)(var12 - 8));
            double var14 = Math.max(1.0D, var10 / (double)(var12 - var13));
            this.setScrollAmount(this.getScrollAmount() + var8 * var14);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      this.setScrollAmount(this.getScrollAmount() - var5 * (double)this.itemHeight / 2.0D);
      return true;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (var1 == 264) {
         this.moveSelection(AbstractSelectionList.SelectionDirection.DOWN);
         return true;
      } else if (var1 == 265) {
         this.moveSelection(AbstractSelectionList.SelectionDirection.field_481);
         return true;
      } else {
         return false;
      }
   }

   protected void moveSelection(AbstractSelectionList.SelectionDirection var1) {
      this.moveSelection(var1, (var0) -> {
         return true;
      });
   }

   protected void refreshSelection() {
      AbstractSelectionList.Entry var1 = this.getSelected();
      if (var1 != null) {
         this.setSelected(var1);
         this.ensureVisible(var1);
      }

   }

   protected void moveSelection(AbstractSelectionList.SelectionDirection var1, Predicate<E> var2) {
      int var3 = var1 == AbstractSelectionList.SelectionDirection.field_481 ? -1 : 1;
      if (!this.children().isEmpty()) {
         int var4 = this.children().indexOf(this.getSelected());

         while(true) {
            int var5 = Mth.clamp((int)(var4 + var3), (int)0, (int)(this.getItemCount() - 1));
            if (var4 == var5) {
               break;
            }

            AbstractSelectionList.Entry var6 = (AbstractSelectionList.Entry)this.children().get(var5);
            if (var2.test(var6)) {
               this.setSelected(var6);
               this.ensureVisible(var6);
               break;
            }

            var4 = var5;
         }
      }

   }

   public boolean isMouseOver(double var1, double var3) {
      return var3 >= (double)this.field_31 && var3 <= (double)this.field_32 && var1 >= (double)this.field_34 && var1 <= (double)this.field_33;
   }

   protected void renderList(PoseStack var1, int var2, int var3, int var4, int var5, float var6) {
      int var7 = this.getItemCount();
      Tesselator var8 = Tesselator.getInstance();
      BufferBuilder var9 = var8.getBuilder();

      for(int var10 = 0; var10 < var7; ++var10) {
         int var11 = this.getRowTop(var10);
         int var12 = this.getRowBottom(var10);
         if (var12 >= this.field_31 && var11 <= this.field_32) {
            int var13 = var3 + var10 * this.itemHeight + this.headerHeight;
            int var14 = this.itemHeight - 4;
            AbstractSelectionList.Entry var15 = this.getEntry(var10);
            int var16 = this.getRowWidth();
            int var17;
            if (this.renderSelection && this.isSelectedItem(var10)) {
               var17 = this.field_34 + this.width / 2 - var16 / 2;
               int var18 = this.field_34 + this.width / 2 + var16 / 2;
               RenderSystem.disableTexture();
               RenderSystem.setShader(GameRenderer::getPositionShader);
               float var19 = this.isFocused() ? 1.0F : 0.5F;
               RenderSystem.setShaderColor(var19, var19, var19, 1.0F);
               var9.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
               var9.vertex((double)var17, (double)(var13 + var14 + 2), 0.0D).endVertex();
               var9.vertex((double)var18, (double)(var13 + var14 + 2), 0.0D).endVertex();
               var9.vertex((double)var18, (double)(var13 - 2), 0.0D).endVertex();
               var9.vertex((double)var17, (double)(var13 - 2), 0.0D).endVertex();
               var8.end();
               RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
               var9.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
               var9.vertex((double)(var17 + 1), (double)(var13 + var14 + 1), 0.0D).endVertex();
               var9.vertex((double)(var18 - 1), (double)(var13 + var14 + 1), 0.0D).endVertex();
               var9.vertex((double)(var18 - 1), (double)(var13 - 1), 0.0D).endVertex();
               var9.vertex((double)(var17 + 1), (double)(var13 - 1), 0.0D).endVertex();
               var8.end();
               RenderSystem.enableTexture();
            }

            var17 = this.getRowLeft();
            var15.render(var1, var10, var11, var17, var16, var14, var4, var5, Objects.equals(this.hovered, var15), var6);
         }
      }

   }

   public int getRowLeft() {
      return this.field_34 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   public int getRowRight() {
      return this.getRowLeft() + this.getRowWidth();
   }

   protected int getRowTop(int var1) {
      return this.field_31 + 4 - (int)this.getScrollAmount() + var1 * this.itemHeight + this.headerHeight;
   }

   private int getRowBottom(int var1) {
      return this.getRowTop(var1) + this.itemHeight;
   }

   protected boolean isFocused() {
      return false;
   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      if (this.isFocused()) {
         return NarratableEntry.NarrationPriority.FOCUSED;
      } else {
         return this.hovered != null ? NarratableEntry.NarrationPriority.HOVERED : NarratableEntry.NarrationPriority.NONE;
      }
   }

   @Nullable
   protected E remove(int var1) {
      AbstractSelectionList.Entry var2 = (AbstractSelectionList.Entry)this.children.get(var1);
      return this.removeEntry((AbstractSelectionList.Entry)this.children.get(var1)) ? var2 : null;
   }

   protected boolean removeEntry(E var1) {
      boolean var2 = this.children.remove(var1);
      if (var2 && var1 == this.getSelected()) {
         this.setSelected((AbstractSelectionList.Entry)null);
      }

      return var2;
   }

   @Nullable
   protected E getHovered() {
      return this.hovered;
   }

   void bindEntryToSelf(AbstractSelectionList.Entry<E> var1) {
      var1.list = this;
   }

   protected void narrateListElementPosition(NarrationElementOutput var1, E var2) {
      List var3 = this.children();
      if (var3.size() > 1) {
         int var4 = var3.indexOf(var2);
         if (var4 != -1) {
            var1.add(NarratedElementType.POSITION, (Component)(new TranslatableComponent("narrator.position.list", new Object[]{var4 + 1, var3.size()})));
         }
      }

   }

   // $FF: synthetic method
   @Nullable
   public GuiEventListener getFocused() {
      return this.getFocused();
   }

   private class TrackedList extends AbstractList<E> {
      private final List<E> delegate = Lists.newArrayList();

      TrackedList() {
         super();
      }

      public E get(int var1) {
         return (AbstractSelectionList.Entry)this.delegate.get(var1);
      }

      public int size() {
         return this.delegate.size();
      }

      public E set(int var1, E var2) {
         AbstractSelectionList.Entry var3 = (AbstractSelectionList.Entry)this.delegate.set(var1, var2);
         AbstractSelectionList.this.bindEntryToSelf(var2);
         return var3;
      }

      public void add(int var1, E var2) {
         this.delegate.add(var1, var2);
         AbstractSelectionList.this.bindEntryToSelf(var2);
      }

      public E remove(int var1) {
         return (AbstractSelectionList.Entry)this.delegate.remove(var1);
      }

      // $FF: synthetic method
      public Object remove(int var1) {
         return this.remove(var1);
      }

      // $FF: synthetic method
      public void add(int var1, Object var2) {
         this.add(var1, (AbstractSelectionList.Entry)var2);
      }

      // $FF: synthetic method
      public Object set(int var1, Object var2) {
         return this.set(var1, (AbstractSelectionList.Entry)var2);
      }

      // $FF: synthetic method
      public Object get(int var1) {
         return this.get(var1);
      }
   }

   public abstract static class Entry<E extends AbstractSelectionList.Entry<E>> implements GuiEventListener {
      /** @deprecated */
      @Deprecated
      AbstractSelectionList<E> list;

      public Entry() {
         super();
      }

      public abstract void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10);

      public boolean isMouseOver(double var1, double var3) {
         return Objects.equals(this.list.getEntryAtPosition(var1, var3), this);
      }
   }

   protected static enum SelectionDirection {
      // $FF: renamed from: UP net.minecraft.client.gui.components.AbstractSelectionList$SelectionDirection
      field_481,
      DOWN;

      private SelectionDirection() {
      }

      // $FF: synthetic method
      private static AbstractSelectionList.SelectionDirection[] $values() {
         return new AbstractSelectionList.SelectionDirection[]{field_481, DOWN};
      }
   }
}
