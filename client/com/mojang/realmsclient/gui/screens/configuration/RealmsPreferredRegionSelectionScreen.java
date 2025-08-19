package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.realmsclient.dto.RealmsRegion;
import com.mojang.realmsclient.dto.RegionSelectionPreference;
import com.mojang.realmsclient.dto.ServiceQuality;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RealmsPreferredRegionSelectionScreen extends Screen {
   private static final Component REGION_SELECTION_LABEL = Component.translatable("mco.configure.world.region_preference.title");
   private static final int SPACING = 8;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final Screen parent;
   private final BiConsumer<RegionSelectionPreference, RealmsRegion> applySettings;
   final Map<RealmsRegion, ServiceQuality> regionServiceQuality;
   @Nullable
   private RegionSelectionList list;
   RealmsSettingsTab.RegionSelection selection;
   @Nullable
   private Button doneButton;

   public RealmsPreferredRegionSelectionScreen(Screen var1, BiConsumer<RegionSelectionPreference, RealmsRegion> var2, Map<RealmsRegion, ServiceQuality> var3, RealmsSettingsTab.RegionSelection var4) {
      super(REGION_SELECTION_LABEL);
      this.parent = var1;
      this.applySettings = var2;
      this.regionServiceQuality = var3;
      this.selection = var4;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   protected void init() {
      LinearLayout var1 = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(8));
      var1.defaultCellSetting().alignHorizontallyCenter();
      var1.addChild(new StringWidget(this.getTitle(), this.font));
      this.list = (RegionSelectionList)this.layout.addToContents(new RegionSelectionList());
      LinearLayout var2 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.doneButton = (Button)var2.addChild(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.applySettings.accept(this.selection.preference(), this.selection.region());
         this.onClose();
      }).build());
      var2.addChild(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> this.onClose()).build());
      this.list.setSelected((RegionSelectionList.Entry)this.list.children().stream().filter((var1x) -> Objects.equals(var1x.regionSelection, this.selection)).findFirst().orElse((Object)null));
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      this.list.updateSize(this.width, this.layout);
   }

   void updateButtonValidity() {
      this.doneButton.active = this.list.getSelected() != null;
   }

   class RegionSelectionList extends ObjectSelectionList<Entry> {
      RegionSelectionList() {
         super(RealmsPreferredRegionSelectionScreen.this.minecraft, RealmsPreferredRegionSelectionScreen.this.width, RealmsPreferredRegionSelectionScreen.this.height - 77, 40, 16);
         this.addEntry(new Entry(RegionSelectionPreference.AUTOMATIC_PLAYER, (RealmsRegion)null));
         this.addEntry(new Entry(RegionSelectionPreference.AUTOMATIC_OWNER, (RealmsRegion)null));
         RealmsPreferredRegionSelectionScreen.this.regionServiceQuality.keySet().stream().map((var1x) -> new Entry(RegionSelectionPreference.MANUAL, var1x)).forEach((var1x) -> this.addEntry(var1x));
      }

      public void setSelected(@Nullable Entry var1) {
         super.setSelected(var1);
         if (var1 != null) {
            RealmsPreferredRegionSelectionScreen.this.selection = var1.regionSelection;
         }

         RealmsPreferredRegionSelectionScreen.this.updateButtonValidity();
      }

      class Entry extends ObjectSelectionList.Entry<Entry> {
         final RealmsSettingsTab.RegionSelection regionSelection;
         private final Component name;

         public Entry(final RegionSelectionPreference var2, @Nullable final RealmsRegion var3) {
            this(new RealmsSettingsTab.RegionSelection(var2, var3));
         }

         public Entry(final RealmsSettingsTab.RegionSelection var2) {
            super();
            this.regionSelection = var2;
            if (var2.preference() == RegionSelectionPreference.MANUAL) {
               if (var2.region() != null) {
                  this.name = Component.translatable(var2.region().translationKey);
               } else {
                  this.name = Component.empty();
               }
            } else {
               this.name = Component.translatable(var2.preference().translationKey);
            }

         }

         public Component getNarration() {
            return Component.translatable("narrator.select", this.name);
         }

         public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
            var1.drawString(RealmsPreferredRegionSelectionScreen.this.font, (Component)this.name, this.getContentX() + 5, this.getContentY() + 2, -1);
            if (this.regionSelection.region() != null && RealmsPreferredRegionSelectionScreen.this.regionServiceQuality.containsKey(this.regionSelection.region())) {
               ServiceQuality var6 = (ServiceQuality)RealmsPreferredRegionSelectionScreen.this.regionServiceQuality.getOrDefault(this.regionSelection.region(), ServiceQuality.UNKNOWN);
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var6.getIcon(), this.getContentRight() - 18, this.getContentY() + 2, 10, 8);
            }

         }

         public boolean mouseClicked(double var1, double var3, int var5, boolean var6) {
            RegionSelectionList.this.setSelected(this);
            return super.mouseClicked(var1, var3, var5, var6);
         }
      }
   }
}
