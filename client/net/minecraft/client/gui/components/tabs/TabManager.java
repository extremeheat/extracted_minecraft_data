package net.minecraft.client.gui.components.tabs;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;

public class TabManager {
   private final Consumer<AbstractWidget> addWidget;
   private final Consumer<AbstractWidget> removeWidget;
   private final Consumer<Tab> onSelected;
   private final Consumer<Tab> onDeselected;
   @Nullable
   private Tab currentTab;
   @Nullable
   private ScreenRectangle tabArea;

   public TabManager(Consumer<AbstractWidget> var1, Consumer<AbstractWidget> var2) {
      this(var1, var2, (var0) -> {
      }, (var0) -> {
      });
   }

   public TabManager(Consumer<AbstractWidget> var1, Consumer<AbstractWidget> var2, Consumer<Tab> var3, Consumer<Tab> var4) {
      super();
      this.addWidget = var1;
      this.removeWidget = var2;
      this.onSelected = var3;
      this.onDeselected = var4;
   }

   public void setTabArea(ScreenRectangle var1) {
      this.tabArea = var1;
      Tab var2 = this.getCurrentTab();
      if (var2 != null) {
         var2.doLayout(var1);
      }

   }

   public void setCurrentTab(Tab var1, boolean var2) {
      if (!Objects.equals(this.currentTab, var1)) {
         if (this.currentTab != null) {
            this.currentTab.visitChildren(this.removeWidget);
         }

         Tab var3 = this.currentTab;
         this.currentTab = var1;
         var1.visitChildren(this.addWidget);
         if (this.tabArea != null) {
            var1.doLayout(this.tabArea);
         }

         if (var2) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI((Holder)SoundEvents.UI_BUTTON_CLICK, 1.0F));
         }

         this.onDeselected.accept(var3);
         this.onSelected.accept(this.currentTab);
      }

   }

   @Nullable
   public Tab getCurrentTab() {
      return this.currentTab;
   }
}
