package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class BeaconScreen extends AbstractContainerScreen<BeaconMenu> {
   static final ResourceLocation BEACON_LOCATION = new ResourceLocation("textures/gui/container/beacon.png");
   private static final Component PRIMARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.primary");
   private static final Component SECONDARY_EFFECT_LABEL = Component.translatable("block.minecraft.beacon.secondary");
   private final List<BeaconScreen.BeaconButton> beaconButtons = Lists.newArrayList();
   @Nullable
   MobEffect primary;
   @Nullable
   MobEffect secondary;

   public BeaconScreen(final BeaconMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.imageWidth = 230;
      this.imageHeight = 219;
      var1.addSlotListener(new ContainerListener() {
         @Override
         public void slotChanged(AbstractContainerMenu var1x, int var2, ItemStack var3) {
         }

         @Override
         public void dataChanged(AbstractContainerMenu var1x, int var2, int var3) {
            BeaconScreen.this.primary = var1.getPrimaryEffect();
            BeaconScreen.this.secondary = var1.getSecondaryEffect();
         }
      });
   }

   private <T extends AbstractWidget & BeaconScreen.BeaconButton> void addBeaconButton(T var1) {
      this.addRenderableWidget(var1);
      this.beaconButtons.add((BeaconScreen.BeaconButton)var1);
   }

   @Override
   protected void init() {
      super.init();
      this.beaconButtons.clear();
      this.addBeaconButton(new BeaconScreen.BeaconConfirmButton(this.leftPos + 164, this.topPos + 107));
      this.addBeaconButton(new BeaconScreen.BeaconCancelButton(this.leftPos + 190, this.topPos + 107));

      for(int var1 = 0; var1 <= 2; ++var1) {
         int var2 = BeaconBlockEntity.BEACON_EFFECTS[var1].length;
         int var3 = var2 * 22 + (var2 - 1) * 2;

         for(int var4 = 0; var4 < var2; ++var4) {
            MobEffect var5 = BeaconBlockEntity.BEACON_EFFECTS[var1][var4];
            BeaconScreen.BeaconPowerButton var6 = new BeaconScreen.BeaconPowerButton(
               this.leftPos + 76 + var4 * 24 - var3 / 2, this.topPos + 22 + var1 * 25, var5, true, var1
            );
            var6.active = false;
            this.addBeaconButton(var6);
         }
      }

      boolean var7 = true;
      int var8 = BeaconBlockEntity.BEACON_EFFECTS[3].length + 1;
      int var9 = var8 * 22 + (var8 - 1) * 2;

      for(int var10 = 0; var10 < var8 - 1; ++var10) {
         MobEffect var12 = BeaconBlockEntity.BEACON_EFFECTS[3][var10];
         BeaconScreen.BeaconPowerButton var13 = new BeaconScreen.BeaconPowerButton(
            this.leftPos + 167 + var10 * 24 - var9 / 2, this.topPos + 47, var12, false, 3
         );
         var13.active = false;
         this.addBeaconButton(var13);
      }

      BeaconScreen.BeaconUpgradePowerButton var11 = new BeaconScreen.BeaconUpgradePowerButton(
         this.leftPos + 167 + (var8 - 1) * 24 - var9 / 2, this.topPos + 47, BeaconBlockEntity.BEACON_EFFECTS[0][0]
      );
      var11.visible = false;
      this.addBeaconButton(var11);
   }

   @Override
   public void containerTick() {
      super.containerTick();
      this.updateButtons();
   }

   void updateButtons() {
      int var1 = this.menu.getLevels();
      this.beaconButtons.forEach(var1x -> var1x.updateStatus(var1));
   }

   @Override
   protected void renderLabels(PoseStack var1, int var2, int var3) {
      drawCenteredString(var1, this.font, PRIMARY_EFFECT_LABEL, 62, 10, 14737632);
      drawCenteredString(var1, this.font, SECONDARY_EFFECT_LABEL, 169, 10, 14737632);
   }

   @Override
   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BEACON_LOCATION);
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      this.blit(var1, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      this.itemRenderer.blitOffset = 100.0F;
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.NETHERITE_INGOT), var5 + 20, var6 + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD), var5 + 41, var6 + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND), var5 + 41 + 22, var6 + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.GOLD_INGOT), var5 + 42 + 44, var6 + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.IRON_INGOT), var5 + 42 + 66, var6 + 109);
      this.itemRenderer.blitOffset = 0.0F;
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   interface BeaconButton {
      void updateStatus(int var1);
   }

   class BeaconCancelButton extends BeaconScreen.BeaconSpriteScreenButton {
      public BeaconCancelButton(int var2, int var3) {
         super(var2, var3, 112, 220, CommonComponents.GUI_CANCEL);
      }

      @Override
      public void onPress() {
         BeaconScreen.this.minecraft.player.closeContainer();
      }

      @Override
      public void updateStatus(int var1) {
      }
   }

   class BeaconConfirmButton extends BeaconScreen.BeaconSpriteScreenButton {
      public BeaconConfirmButton(int var2, int var3) {
         super(var2, var3, 90, 220, CommonComponents.GUI_DONE);
      }

      @Override
      public void onPress() {
         BeaconScreen.this.minecraft
            .getConnection()
            .send(new ServerboundSetBeaconPacket(Optional.ofNullable(BeaconScreen.this.primary), Optional.ofNullable(BeaconScreen.this.secondary)));
         BeaconScreen.this.minecraft.player.closeContainer();
      }

      @Override
      public void updateStatus(int var1) {
         this.active = BeaconScreen.this.menu.hasPayment() && BeaconScreen.this.primary != null;
      }
   }

   class BeaconPowerButton extends BeaconScreen.BeaconScreenButton {
      private final boolean isPrimary;
      protected final int tier;
      private MobEffect effect;
      private TextureAtlasSprite sprite;

      public BeaconPowerButton(int var2, int var3, MobEffect var4, boolean var5, int var6) {
         super(var2, var3);
         this.isPrimary = var5;
         this.tier = var6;
         this.setEffect(var4);
      }

      protected void setEffect(MobEffect var1) {
         this.effect = var1;
         this.sprite = Minecraft.getInstance().getMobEffectTextures().get(var1);
         this.setTooltip(Tooltip.create(this.createEffectDescription(var1), null));
      }

      protected MutableComponent createEffectDescription(MobEffect var1) {
         return Component.translatable(var1.getDescriptionId());
      }

      @Override
      public void onPress() {
         if (!this.isSelected()) {
            if (this.isPrimary) {
               BeaconScreen.this.primary = this.effect;
            } else {
               BeaconScreen.this.secondary = this.effect;
            }

            BeaconScreen.this.updateButtons();
         }
      }

      @Override
      protected void renderIcon(PoseStack var1) {
         RenderSystem.setShaderTexture(0, this.sprite.atlasLocation());
         blit(var1, this.getX() + 2, this.getY() + 2, this.getBlitOffset(), 18, 18, this.sprite);
      }

      @Override
      public void updateStatus(int var1) {
         this.active = this.tier < var1;
         this.setSelected(this.effect == (this.isPrimary ? BeaconScreen.this.primary : BeaconScreen.this.secondary));
      }

      @Override
      protected MutableComponent createNarrationMessage() {
         return this.createEffectDescription(this.effect);
      }
   }

   abstract static class BeaconScreenButton extends AbstractButton implements BeaconScreen.BeaconButton {
      private boolean selected;

      protected BeaconScreenButton(int var1, int var2) {
         super(var1, var2, 22, 22, CommonComponents.EMPTY);
      }

      protected BeaconScreenButton(int var1, int var2, Component var3) {
         super(var1, var2, 22, 22, var3);
      }

      @Override
      public void renderButton(PoseStack var1, int var2, int var3, float var4) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, BeaconScreen.BEACON_LOCATION);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var5 = true;
         int var6 = 0;
         if (!this.active) {
            var6 += this.width * 2;
         } else if (this.selected) {
            var6 += this.width * 1;
         } else if (this.isHoveredOrFocused()) {
            var6 += this.width * 3;
         }

         this.blit(var1, this.getX(), this.getY(), var6, 219, this.width, this.height);
         this.renderIcon(var1);
      }

      protected abstract void renderIcon(PoseStack var1);

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(boolean var1) {
         this.selected = var1;
      }

      @Override
      public void updateWidgetNarration(NarrationElementOutput var1) {
         this.defaultButtonNarrationText(var1);
      }
   }

   abstract static class BeaconSpriteScreenButton extends BeaconScreen.BeaconScreenButton {
      private final int iconX;
      private final int iconY;

      protected BeaconSpriteScreenButton(int var1, int var2, int var3, int var4, Component var5) {
         super(var1, var2, var5);
         this.iconX = var3;
         this.iconY = var4;
      }

      @Override
      protected void renderIcon(PoseStack var1) {
         this.blit(var1, this.getX() + 2, this.getY() + 2, this.iconX, this.iconY, 18, 18);
      }
   }

   class BeaconUpgradePowerButton extends BeaconScreen.BeaconPowerButton {
      public BeaconUpgradePowerButton(int var2, int var3, MobEffect var4) {
         super(var2, var3, var4, false, 3);
      }

      @Override
      protected MutableComponent createEffectDescription(MobEffect var1) {
         return Component.translatable(var1.getDescriptionId()).append(" II");
      }

      @Override
      public void updateStatus(int var1) {
         if (BeaconScreen.this.primary != null) {
            this.visible = true;
            this.setEffect(BeaconScreen.this.primary);
            super.updateStatus(var1);
         } else {
            this.visible = false;
         }
      }
   }
}
