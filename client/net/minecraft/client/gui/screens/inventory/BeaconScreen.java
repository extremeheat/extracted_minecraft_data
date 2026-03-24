package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.jspecify.annotations.Nullable;

public class BeaconScreen extends AbstractContainerScreen<BeaconMenu> {
   private static final Identifier BEACON_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/beacon.png");
   private static final Identifier BUTTON_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/beacon/button_disabled");
   private static final Identifier BUTTON_SELECTED_SPRITE = Identifier.withDefaultNamespace("container/beacon/button_selected");
   private static final Identifier BUTTON_HIGHLIGHTED_SPRITE = Identifier.withDefaultNamespace("container/beacon/button_highlighted");
   private static final Identifier BUTTON_SPRITE = Identifier.withDefaultNamespace("container/beacon/button");
   private static final Identifier CONFIRM_SPRITE = Identifier.withDefaultNamespace("container/beacon/confirm");
   private static final Identifier CANCEL_SPRITE = Identifier.withDefaultNamespace("container/beacon/cancel");
   private static final Component PRIMARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.primary");
   private static final Component SECONDARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.secondary");
   private final List<BeaconButton> beaconButtons = Lists.newArrayList();
   private @Nullable Holder<MobEffect> primary;
   private @Nullable Holder<MobEffect> secondary;

   public BeaconScreen(final BeaconMenu menu, final Inventory inventory, final Component title) {
      super(menu, inventory, title, 230, 219);
      menu.addSlotListener(new ContainerListener() {
         {
            Objects.requireNonNull(BeaconScreen.this);
         }

         public void slotChanged(final AbstractContainerMenu container, final int slotIndex, final ItemStack itemStack) {
         }

         public void dataChanged(final AbstractContainerMenu container, final int id, final int value) {
            BeaconScreen.this.primary = menu.getPrimaryEffect();
            BeaconScreen.this.secondary = menu.getSecondaryEffect();
         }
      });
   }

   private <T extends AbstractWidget & BeaconButton> void addBeaconButton(final T beaconButton) {
      this.addRenderableWidget(beaconButton);
      this.beaconButtons.add(beaconButton);
   }

   protected void init() {
      super.init();
      this.beaconButtons.clear();

      for(int tier = 0; tier <= 2; ++tier) {
         int count = ((List)BeaconBlockEntity.BEACON_EFFECTS.get(tier)).size();
         int totalWidth = count * 22 + (count - 1) * 2;

         for(int c = 0; c < count; ++c) {
            Holder<MobEffect> effect = (Holder)((List)BeaconBlockEntity.BEACON_EFFECTS.get(tier)).get(c);
            BeaconPowerButton beaconPowerButton = new BeaconPowerButton(this.leftPos + 76 + c * 24 - totalWidth / 2, this.topPos + 22 + tier * 25, effect, true, tier);
            beaconPowerButton.active = false;
            this.addBeaconButton(beaconPowerButton);
         }
      }

      int tier = 3;
      int count = ((List)BeaconBlockEntity.BEACON_EFFECTS.get(3)).size() + 1;
      int totalWidth = count * 22 + (count - 1) * 2;

      for(int c = 0; c < count - 1; ++c) {
         Holder<MobEffect> effect = (Holder)((List)BeaconBlockEntity.BEACON_EFFECTS.get(3)).get(c);
         BeaconPowerButton beaconPowerButton = new BeaconPowerButton(this.leftPos + 167 + c * 24 - totalWidth / 2, this.topPos + 47, effect, false, 3);
         beaconPowerButton.active = false;
         this.addBeaconButton(beaconPowerButton);
      }

      Holder<MobEffect> dummyEffect = (Holder)((List)BeaconBlockEntity.BEACON_EFFECTS.get(0)).get(0);
      BeaconPowerButton beaconPowerButton = new BeaconUpgradePowerButton(this.leftPos + 167 + (count - 1) * 24 - totalWidth / 2, this.topPos + 47, dummyEffect);
      beaconPowerButton.visible = false;
      this.addBeaconButton(beaconPowerButton);
      this.addBeaconButton(new BeaconConfirmButton(this.leftPos + 164, this.topPos + 107));
      this.addBeaconButton(new BeaconCancelButton(this.leftPos + 190, this.topPos + 107));
   }

   public void containerTick() {
      super.containerTick();
      this.updateButtons();
   }

   private void updateButtons() {
      int levels = ((BeaconMenu)this.menu).getLevels();
      this.beaconButtons.forEach((b) -> b.updateStatus(levels));
   }

   protected void extractLabels(final GuiGraphicsExtractor graphics, final int xm, final int ym) {
      graphics.centeredText(this.font, (Component)PRIMARY_EFFECT_LABEL, 62, 10, -2039584);
      graphics.centeredText(this.font, (Component)SECONDARY_EFFECT_LABEL, 169, 10, -2039584);
   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractBackground(graphics, mouseX, mouseY, a);
      int xo = (this.width - this.imageWidth) / 2;
      int yo = (this.height - this.imageHeight) / 2;
      graphics.blit(RenderPipelines.GUI_TEXTURED, BEACON_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      graphics.item(new ItemStack(Items.NETHERITE_INGOT), xo + 20, yo + 109);
      graphics.item(new ItemStack(Items.EMERALD), xo + 41, yo + 109);
      graphics.item(new ItemStack(Items.DIAMOND), xo + 41 + 22, yo + 109);
      graphics.item(new ItemStack(Items.GOLD_INGOT), xo + 42 + 44, yo + 109);
      graphics.item(new ItemStack(Items.IRON_INGOT), xo + 42 + 66, yo + 109);
   }

   private abstract static class BeaconScreenButton extends AbstractButton implements BeaconButton {
      private boolean selected;

      protected BeaconScreenButton(final int x, final int y) {
         super(x, y, 22, 22, CommonComponents.EMPTY);
      }

      protected BeaconScreenButton(final int x, final int y, final Component component) {
         super(x, y, 22, 22, component);
      }

      public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
         Identifier sprite;
         if (!this.active) {
            sprite = BeaconScreen.BUTTON_DISABLED_SPRITE;
         } else if (this.selected) {
            sprite = BeaconScreen.BUTTON_SELECTED_SPRITE;
         } else if (this.isHoveredOrFocused()) {
            sprite = BeaconScreen.BUTTON_HIGHLIGHTED_SPRITE;
         } else {
            sprite = BeaconScreen.BUTTON_SPRITE;
         }

         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.getX(), this.getY(), this.width, this.height);
         this.extractIcon(graphics);
      }

      protected abstract void extractIcon(final GuiGraphicsExtractor graphics);

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(final boolean selected) {
         this.selected = selected;
      }

      public void updateWidgetNarration(final NarrationElementOutput output) {
         this.defaultButtonNarrationText(output);
      }
   }

   private class BeaconPowerButton extends BeaconScreenButton {
      private final boolean isPrimary;
      protected final int tier;
      private Holder<MobEffect> effect;
      private Identifier sprite;

      public BeaconPowerButton(final int x, final int y, final Holder<MobEffect> effect, final boolean isPrimary, final int tier) {
         Objects.requireNonNull(BeaconScreen.this);
         super(x, y);
         this.isPrimary = isPrimary;
         this.tier = tier;
         this.setEffect(effect);
      }

      protected void setEffect(final Holder<MobEffect> effect) {
         this.effect = effect;
         this.sprite = Gui.getMobEffectSprite(effect);
         this.setTooltip(Tooltip.create(this.createEffectDescription(effect), (Component)null));
      }

      protected MutableComponent createEffectDescription(final Holder<MobEffect> effect) {
         return Component.translatable(((MobEffect)effect.value()).getDescriptionId());
      }

      public void onPress(final InputWithModifiers input) {
         if (!this.isSelected()) {
            if (this.isPrimary) {
               BeaconScreen.this.primary = this.effect;
            } else {
               BeaconScreen.this.secondary = this.effect;
            }

            BeaconScreen.this.updateButtons();
         }
      }

      protected void extractIcon(final GuiGraphicsExtractor graphics) {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
      }

      public void updateStatus(final int levels) {
         this.active = this.tier < levels;
         this.setSelected(this.effect.equals(this.isPrimary ? BeaconScreen.this.primary : BeaconScreen.this.secondary));
      }

      protected MutableComponent createNarrationMessage() {
         return this.createEffectDescription(this.effect);
      }
   }

   private class BeaconUpgradePowerButton extends BeaconPowerButton {
      public BeaconUpgradePowerButton(final int x, final int y, final Holder<MobEffect> effect) {
         Objects.requireNonNull(BeaconScreen.this);
         super(x, y, effect, false, 3);
      }

      protected MutableComponent createEffectDescription(final Holder<MobEffect> effect) {
         return Component.translatable(((MobEffect)effect.value()).getDescriptionId()).append(" II");
      }

      public void updateStatus(final int levels) {
         if (BeaconScreen.this.primary != null) {
            this.visible = true;
            this.setEffect(BeaconScreen.this.primary);
            super.updateStatus(levels);
         } else {
            this.visible = false;
         }

      }
   }

   private abstract static class BeaconSpriteScreenButton extends BeaconScreenButton {
      private final Identifier sprite;

      protected BeaconSpriteScreenButton(final int x, final int y, final Identifier sprite, final Component label) {
         super(x, y, label);
         this.setTooltip(Tooltip.create(label));
         this.sprite = sprite;
      }

      protected void extractIcon(final GuiGraphicsExtractor graphics) {
         graphics.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)this.sprite, this.getX() + 2, this.getY() + 2, 18, 18);
      }
   }

   private class BeaconConfirmButton extends BeaconSpriteScreenButton {
      public BeaconConfirmButton(final int x, final int y) {
         Objects.requireNonNull(BeaconScreen.this);
         super(x, y, BeaconScreen.CONFIRM_SPRITE, CommonComponents.GUI_DONE);
      }

      public void onPress(final InputWithModifiers input) {
         BeaconScreen.this.minecraft.getConnection().send(new ServerboundSetBeaconPacket(Optional.ofNullable(BeaconScreen.this.primary), Optional.ofNullable(BeaconScreen.this.secondary)));
         BeaconScreen.this.minecraft.player.closeContainer();
      }

      public void updateStatus(final int levels) {
         this.active = ((BeaconMenu)BeaconScreen.this.menu).hasPayment() && BeaconScreen.this.primary != null;
      }
   }

   private class BeaconCancelButton extends BeaconSpriteScreenButton {
      public BeaconCancelButton(final int x, final int y) {
         Objects.requireNonNull(BeaconScreen.this);
         super(x, y, BeaconScreen.CANCEL_SPRITE, CommonComponents.GUI_CANCEL);
      }

      public void onPress(final InputWithModifiers input) {
         BeaconScreen.this.minecraft.player.closeContainer();
      }

      public void updateStatus(final int levels) {
      }
   }

   private interface BeaconButton {
      void updateStatus(final int levels);
   }
}
