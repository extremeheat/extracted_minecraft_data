package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.realmsclient.dto.RealmsRegion;
import com.mojang.realmsclient.dto.RegionSelectionPreference;
import com.mojang.realmsclient.dto.ServiceQuality;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class RealmsPreferredRegionSelectionScreen extends Screen {
   private static final Component REGION_SELECTION_LABEL = Component.translatable("mco.configure.world.region_preference.title");
   private static final int SPACING = 8;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final Screen parent;
   private final BiConsumer<RegionSelectionPreference, RealmsRegion> applySettings;
   private final Map<RealmsRegion, ServiceQuality> regionServiceQuality;
   private @Nullable RegionSelectionList list;
   private RealmsSettingsTab.RegionSelection selection;
   private @Nullable Button doneButton;

   public RealmsPreferredRegionSelectionScreen(final Screen parent, final BiConsumer<RegionSelectionPreference, RealmsRegion> applySettings, final Map<RealmsRegion, ServiceQuality> regionServiceQuality, final RealmsSettingsTab.RegionSelection currentSelection) {
      super(REGION_SELECTION_LABEL);
      this.parent = parent;
      this.applySettings = applySettings;
      this.regionServiceQuality = regionServiceQuality;
      this.selection = currentSelection;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   protected void init() {
      LinearLayout header = (LinearLayout)this.layout.addToHeader(LinearLayout.vertical().spacing(8));
      header.defaultCellSetting().alignHorizontallyCenter();
      header.addChild(new StringWidget(this.getTitle(), this.font));
      this.list = (RegionSelectionList)this.layout.addToContents(new RegionSelectionList());
      LinearLayout footer = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.doneButton = (Button)footer.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onDone()).build());
      footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, (button) -> this.onClose()).build());
      this.list.setSelected((RegionSelectionList.Entry)this.list.children().stream().filter((e) -> Objects.equals(e.regionSelection, this.selection)).findFirst().orElse((Object)null));
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.list != null) {
         this.list.updateSize(this.width, this.layout);
      }

   }

   private void onDone() {
      if (this.selection.region() != null) {
         this.applySettings.accept(this.selection.preference(), this.selection.region());
      }

      this.onClose();
   }

   private void updateButtonValidity() {
      if (this.doneButton != null && this.list != null) {
         this.doneButton.active = this.list.getSelected() != null;
      }

   }

   private class RegionSelectionList extends ObjectSelectionList<Entry> {
      private RegionSelectionList() {
         Objects.requireNonNull(RealmsPreferredRegionSelectionScreen.this);
         super(RealmsPreferredRegionSelectionScreen.this.minecraft, RealmsPreferredRegionSelectionScreen.this.width, RealmsPreferredRegionSelectionScreen.this.height - 77, 40, 16);
         this.addEntry(new Entry(RegionSelectionPreference.AUTOMATIC_PLAYER, (RealmsRegion)null));
         this.addEntry(new Entry(RegionSelectionPreference.AUTOMATIC_OWNER, (RealmsRegion)null));
         RealmsPreferredRegionSelectionScreen.this.regionServiceQuality.keySet().stream().map((region) -> new Entry(RegionSelectionPreference.MANUAL, region)).forEach((x$0) -> this.addEntry(x$0));
      }

      public void setSelected(final Entry selected) {
         super.setSelected(selected);
         if (selected != null) {
            RealmsPreferredRegionSelectionScreen.this.selection = selected.regionSelection;
         }

         RealmsPreferredRegionSelectionScreen.this.updateButtonValidity();
      }

      private class Entry extends ObjectSelectionList.Entry<Entry> {
         private final RealmsSettingsTab.RegionSelection regionSelection;
         private final Component name;

         public Entry(final @Nullable RegionSelectionPreference preference, final RealmsRegion region) {
            this(new RealmsSettingsTab.RegionSelection(preference, region));
         }

         public Entry(final RealmsSettingsTab.RegionSelection regionSelection) {
            Objects.requireNonNull(RegionSelectionList.this);
            super();
            this.regionSelection = regionSelection;
            if (regionSelection.preference() == RegionSelectionPreference.MANUAL) {
               if (regionSelection.region() != null) {
                  this.name = Component.translatable(regionSelection.region().translationKey);
               } else {
                  this.name = Component.empty();
               }
            } else {
               this.name = Component.translatable(regionSelection.preference().translationKey);
            }

         }

         public Component getNarration() {
            return Component.translatable("narrator.select", this.name);
         }

         public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
            graphics.text(RealmsPreferredRegionSelectionScreen.this.font, (Component)this.name, this.getContentX() + 5, this.getContentY() + 2, -1);
            if (this.regionSelection.region() != null && RealmsPreferredRegionSelectionScreen.this.regionServiceQuality.containsKey(this.regionSelection.region())) {
               ServiceQuality serviceQuality = (ServiceQuality)RealmsPreferredRegionSelectionScreen.this.regionServiceQuality.getOrDefault(this.regionSelection.region(), ServiceQuality.UNKNOWN);
               graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)serviceQuality.getIcon(), this.getContentRight() - 18, this.getContentY() + 2, 10, 8);
            }

         }

         public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
            RegionSelectionList.this.setSelected(this);
            if (doubleClick) {
               RegionSelectionList.this.playDownSound(RegionSelectionList.this.minecraft.getSoundManager());
               RealmsPreferredRegionSelectionScreen.this.onDone();
               return true;
            } else {
               return super.mouseClicked(event, doubleClick);
            }
         }

         public boolean keyPressed(final KeyEvent event) {
            if (event.isSelection()) {
               RegionSelectionList.this.playDownSound(RegionSelectionList.this.minecraft.getSoundManager());
               RealmsPreferredRegionSelectionScreen.this.onDone();
               return true;
            } else {
               return super.keyPressed(event);
            }
         }
      }
   }
}
