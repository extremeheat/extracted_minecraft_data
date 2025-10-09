package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.realmsclient.dto.RealmsRegion;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RegionSelectionPreference;
import com.mojang.realmsclient.dto.RegionSelectionPreferenceDto;
import com.mojang.realmsclient.dto.ServiceQuality;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class RealmsSettingsTab extends GridLayoutTab implements RealmsConfigurationTab {
   private static final int COMPONENT_WIDTH = 212;
   private static final int EXTRA_SPACING = 2;
   private static final int DEFAULT_SPACING = 6;
   static final Component TITLE = Component.translatable("mco.configure.world.settings.title");
   private static final Component NAME_LABEL = Component.translatable("mco.configure.world.name");
   private static final Component DESCRIPTION_LABEL = Component.translatable("mco.configure.world.description");
   private static final Component REGION_PREFERENCE_LABEL = Component.translatable("mco.configure.world.region_preference");
   private static final Tooltip REALM_NAME_VALIDATION_ERROR_TOOLTIP = Tooltip.create(Component.translatable("mco.configure.world.name.validation.whitespace"));
   private final RealmsConfigureWorldScreen configurationScreen;
   private final Minecraft minecraft;
   private RealmsServer serverData;
   private final Map<RealmsRegion, ServiceQuality> regionServiceQuality;
   final Button closeOpenButton;
   private final EditBox descEdit;
   private final EditBox nameEdit;
   private final StringWidget selectedRegionStringWidget;
   private final ImageWidget selectedRegionImageWidget;
   private RegionSelection preferredRegionSelection;

   RealmsSettingsTab(RealmsConfigureWorldScreen var1, Minecraft var2, RealmsServer var3, Map<RealmsRegion, ServiceQuality> var4) {
      super(TITLE);
      this.configurationScreen = var1;
      this.minecraft = var2;
      this.serverData = var3;
      this.regionServiceQuality = var4;
      GridLayout.RowHelper var5 = this.layout.rowSpacing(6).createRowHelper(1);
      var5.addChild(new StringWidget(NAME_LABEL, var1.getFont()));
      this.nameEdit = new EditBox(var2.font, 0, 0, 212, 20, Component.translatable("mco.configure.world.name"));
      this.nameEdit.setMaxLength(32);
      this.nameEdit.setResponder((var1x) -> {
         if (!this.isRealmNameValid()) {
            this.nameEdit.setTextColor(-2142128);
            this.nameEdit.setTooltip(REALM_NAME_VALIDATION_ERROR_TOOLTIP);
         } else {
            this.nameEdit.setTooltip((Tooltip)null);
            this.nameEdit.setTextColor(-2039584);
         }
      });
      var5.addChild(this.nameEdit);
      var5.addChild(SpacerElement.height(2));
      var5.addChild(new StringWidget(DESCRIPTION_LABEL, var1.getFont()));
      this.descEdit = new EditBox(var2.font, 0, 0, 212, 20, Component.translatable("mco.configure.world.description"));
      this.descEdit.setMaxLength(32);
      var5.addChild(this.descEdit);
      var5.addChild(SpacerElement.height(2));
      var5.addChild(new StringWidget(REGION_PREFERENCE_LABEL, var1.getFont()));
      Objects.requireNonNull(var1.getFont());
      EqualSpacingLayout var6 = new EqualSpacingLayout(0, 0, 212, 9, EqualSpacingLayout.Orientation.HORIZONTAL);
      Objects.requireNonNull(var1.getFont());
      this.selectedRegionStringWidget = (StringWidget)var6.addChild(new StringWidget(192, 9, Component.empty(), var1.getFont()));
      this.selectedRegionImageWidget = (ImageWidget)var6.addChild(ImageWidget.sprite(10, 8, ServiceQuality.UNKNOWN.getIcon()));
      var5.addChild(var6);
      var5.addChild(Button.builder(Component.translatable("mco.configure.world.buttons.region_preference"), (var1x) -> this.openPreferenceSelector()).bounds(0, 0, 212, 20).build());
      var5.addChild(SpacerElement.height(2));
      this.closeOpenButton = (Button)var5.addChild(Button.builder(Component.empty(), (var4x) -> {
         if (var3.state == RealmsServer.State.OPEN) {
            var2.setScreen(RealmsPopups.customPopupScreen(var1, Component.translatable("mco.configure.world.close.question.title"), Component.translatable("mco.configure.world.close.question.line1"), (var2x) -> {
               this.save();
               var1.closeTheWorld();
            }));
         } else {
            this.save();
            var1.openTheWorld(false);
         }

      }).bounds(0, 0, 212, 20).build());
      this.closeOpenButton.active = false;
      this.updateData(var3);
   }

   private static MutableComponent getTranslatableFromPreference(RegionSelection var0) {
      return (var0.preference().equals(RegionSelectionPreference.MANUAL) && var0.region() != null ? Component.translatable(var0.region().translationKey) : Component.translatable(var0.preference().translationKey)).withStyle(ChatFormatting.GRAY);
   }

   private static ResourceLocation getServiceQualityIcon(RegionSelection var0, Map<RealmsRegion, ServiceQuality> var1) {
      if (var0.region() != null && var1.containsKey(var0.region())) {
         ServiceQuality var2 = (ServiceQuality)var1.getOrDefault(var0.region(), ServiceQuality.UNKNOWN);
         return var2.getIcon();
      } else {
         return ServiceQuality.UNKNOWN.getIcon();
      }
   }

   private boolean isRealmNameValid() {
      String var1 = this.nameEdit.getValue();
      String var2 = var1.trim();
      return !var2.isEmpty() && var1.length() == var2.length();
   }

   private void openPreferenceSelector() {
      this.minecraft.setScreen(new RealmsPreferredRegionSelectionScreen(this.configurationScreen, this::applyRegionPreferenceSelection, this.regionServiceQuality, this.preferredRegionSelection));
   }

   private void applyRegionPreferenceSelection(RegionSelectionPreference var1, RealmsRegion var2) {
      this.preferredRegionSelection = new RegionSelection(var1, var2);
      this.updateRegionPreferenceValues();
   }

   private void updateRegionPreferenceValues() {
      this.selectedRegionStringWidget.setMessage(getTranslatableFromPreference(this.preferredRegionSelection));
      this.selectedRegionImageWidget.updateResource(getServiceQualityIcon(this.preferredRegionSelection, this.regionServiceQuality));
      this.selectedRegionImageWidget.visible = this.preferredRegionSelection.preference == RegionSelectionPreference.MANUAL;
   }

   public void onSelected(RealmsServer var1) {
      this.updateData(var1);
   }

   public void updateData(RealmsServer var1) {
      this.serverData = var1;
      if (var1.regionSelectionPreference == null) {
         var1.regionSelectionPreference = RegionSelectionPreferenceDto.DEFAULT;
      }

      if (var1.regionSelectionPreference.regionSelectionPreference == RegionSelectionPreference.MANUAL && var1.regionSelectionPreference.preferredRegion == null) {
         Optional var2 = this.regionServiceQuality.keySet().stream().findFirst();
         var2.ifPresent((var1x) -> var1.regionSelectionPreference.preferredRegion = var1x);
      }

      String var3 = var1.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
      this.closeOpenButton.setMessage(Component.translatable(var3));
      this.closeOpenButton.active = true;
      this.preferredRegionSelection = new RegionSelection(var1.regionSelectionPreference.regionSelectionPreference, var1.regionSelectionPreference.preferredRegion);
      this.nameEdit.setValue((String)Objects.requireNonNullElse(var1.getName(), ""));
      this.descEdit.setValue(var1.getDescription());
      this.updateRegionPreferenceValues();
   }

   public void onDeselected(RealmsServer var1) {
      this.save();
   }

   public void save() {
      if (this.serverData.regionSelectionPreference == null || !Objects.equals(this.nameEdit.getValue(), this.serverData.name) || !Objects.equals(this.descEdit.getValue(), this.serverData.motd) || this.preferredRegionSelection.preference() != this.serverData.regionSelectionPreference.regionSelectionPreference || this.preferredRegionSelection.region() != this.serverData.regionSelectionPreference.preferredRegion) {
         if (this.isRealmNameValid()) {
            this.configurationScreen.saveSettings(this.nameEdit.getValue(), this.descEdit.getValue(), this.preferredRegionSelection.preference(), this.preferredRegionSelection.region());
         }

      }
   }

   public static record RegionSelection(RegionSelectionPreference preference, @Nullable RealmsRegion region) {
      final RegionSelectionPreference preference;

      public RegionSelection(RegionSelectionPreference var1, @Nullable RealmsRegion var2) {
         super();
         this.preference = var1;
         this.region = var2;
      }
   }
}
