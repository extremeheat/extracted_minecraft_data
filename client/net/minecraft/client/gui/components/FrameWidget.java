package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class FrameWidget extends AbstractContainerWidget {
   private final List<FrameWidget.ChildContainer> children = new ArrayList<>();
   private final List<AbstractWidget> containedChildrenView = Collections.unmodifiableList(Lists.transform(this.children, var0 -> var0.child));
   private int minWidth;
   private int minHeight;
   private final LayoutSettings defaultChildLayoutSettings = LayoutSettings.defaults().align(0.5F, 0.5F);

   public static FrameWidget withMinDimensions(int var0, int var1) {
      return new FrameWidget(0, 0, 0, 0).setMinDimensions(var0, var1);
   }

   public FrameWidget() {
      this(0, 0, 0, 0);
   }

   public FrameWidget(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, Component.empty());
   }

   public FrameWidget setMinDimensions(int var1, int var2) {
      return this.setMinWidth(var1).setMinHeight(var2);
   }

   public FrameWidget setMinHeight(int var1) {
      this.minHeight = var1;
      return this;
   }

   public FrameWidget setMinWidth(int var1) {
      this.minWidth = var1;
      return this;
   }

   public LayoutSettings newChildLayoutSettings() {
      return this.defaultChildLayoutSettings.copy();
   }

   public LayoutSettings defaultChildLayoutSetting() {
      return this.defaultChildLayoutSettings;
   }

   public void pack() {
      int var1 = this.minWidth;
      int var2 = this.minHeight;

      for(FrameWidget.ChildContainer var4 : this.children) {
         var1 = Math.max(var1, var4.getWidth());
         var2 = Math.max(var2, var4.getHeight());
      }

      for(FrameWidget.ChildContainer var6 : this.children) {
         var6.setX(this.getX(), var1);
         var6.setY(this.getY(), var2);
      }

      this.width = var1;
      this.height = var2;
   }

   public <T extends AbstractWidget> T addChild(T var1) {
      return this.addChild((T)var1, this.newChildLayoutSettings());
   }

   public <T extends AbstractWidget> T addChild(T var1, LayoutSettings var2) {
      this.children.add(new FrameWidget.ChildContainer(var1, var2));
      return (T)var1;
   }

   @Override
   protected List<AbstractWidget> getContainedChildren() {
      return this.containedChildrenView;
   }

   public static void centerInRectangle(AbstractWidget var0, int var1, int var2, int var3, int var4) {
      alignInRectangle(var0, var1, var2, var3, var4, 0.5F, 0.5F);
   }

   public static void alignInRectangle(AbstractWidget var0, int var1, int var2, int var3, int var4, float var5, float var6) {
      alignInDimension(var1, var3, var0.getWidth(), var0::setX, var5);
      alignInDimension(var2, var4, var0.getHeight(), var0::setY, var6);
   }

   public static void alignInDimension(int var0, int var1, int var2, Consumer<Integer> var3, float var4) {
      int var5 = (int)Mth.lerp(var4, 0.0F, (float)(var1 - var2));
      var3.accept(var0 + var5);
   }

   static class ChildContainer extends AbstractContainerWidget.AbstractChildWrapper {
      protected ChildContainer(AbstractWidget var1, LayoutSettings var2) {
         super(var1, var2);
      }
   }
}
