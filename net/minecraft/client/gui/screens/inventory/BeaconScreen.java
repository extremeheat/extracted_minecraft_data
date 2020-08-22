package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class BeaconScreen extends AbstractContainerScreen {
   private static final ResourceLocation BEACON_LOCATION = new ResourceLocation("textures/gui/container/beacon.png");
   private BeaconScreen.BeaconConfirmButton confirmButton;
   private boolean initPowerButtons;
   private MobEffect primary;
   private MobEffect secondary;

   public BeaconScreen(final BeaconMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.imageWidth = 230;
      this.imageHeight = 219;
      var1.addSlotListener(new ContainerListener() {
         public void refreshContainer(AbstractContainerMenu var1x, NonNullList var2) {
         }

         public void slotChanged(AbstractContainerMenu var1x, int var2, ItemStack var3) {
         }

         public void setContainerData(AbstractContainerMenu var1x, int var2, int var3) {
            BeaconScreen.this.primary = var1.getPrimaryEffect();
            BeaconScreen.this.secondary = var1.getSecondaryEffect();
            BeaconScreen.this.initPowerButtons = true;
         }
      });
   }

   protected void init() {
      super.init();
      this.confirmButton = (BeaconScreen.BeaconConfirmButton)this.addButton(new BeaconScreen.BeaconConfirmButton(this.leftPos + 164, this.topPos + 107));
      this.addButton(new BeaconScreen.BeaconCancelButton(this.leftPos + 190, this.topPos + 107));
      this.initPowerButtons = true;
      this.confirmButton.active = false;
   }

   public void tick() {
      super.tick();
      int var1 = ((BeaconMenu)this.menu).getLevels();
      if (this.initPowerButtons && var1 >= 0) {
         this.initPowerButtons = false;

         int var3;
         int var4;
         int var5;
         MobEffect var6;
         BeaconScreen.BeaconPowerButton var7;
         for(int var2 = 0; var2 <= 2; ++var2) {
            var3 = BeaconBlockEntity.BEACON_EFFECTS[var2].length;
            var4 = var3 * 22 + (var3 - 1) * 2;

            for(var5 = 0; var5 < var3; ++var5) {
               var6 = BeaconBlockEntity.BEACON_EFFECTS[var2][var5];
               var7 = new BeaconScreen.BeaconPowerButton(this.leftPos + 76 + var5 * 24 - var4 / 2, this.topPos + 22 + var2 * 25, var6, true);
               this.addButton(var7);
               if (var2 >= var1) {
                  var7.active = false;
               } else if (var6 == this.primary) {
                  var7.setSelected(true);
               }
            }
         }

         boolean var8 = true;
         var3 = BeaconBlockEntity.BEACON_EFFECTS[3].length + 1;
         var4 = var3 * 22 + (var3 - 1) * 2;

         for(var5 = 0; var5 < var3 - 1; ++var5) {
            var6 = BeaconBlockEntity.BEACON_EFFECTS[3][var5];
            var7 = new BeaconScreen.BeaconPowerButton(this.leftPos + 167 + var5 * 24 - var4 / 2, this.topPos + 47, var6, false);
            this.addButton(var7);
            if (3 >= var1) {
               var7.active = false;
            } else if (var6 == this.secondary) {
               var7.setSelected(true);
            }
         }

         if (this.primary != null) {
            BeaconScreen.BeaconPowerButton var9 = new BeaconScreen.BeaconPowerButton(this.leftPos + 167 + (var3 - 1) * 24 - var4 / 2, this.topPos + 47, this.primary, false);
            this.addButton(var9);
            if (3 >= var1) {
               var9.active = false;
            } else if (this.primary == this.secondary) {
               var9.setSelected(true);
            }
         }
      }

      this.confirmButton.active = ((BeaconMenu)this.menu).hasPayment() && this.primary != null;
   }

   protected void renderLabels(int var1, int var2) {
      this.drawCenteredString(this.font, I18n.get("block.minecraft.beacon.primary"), 62, 10, 14737632);
      this.drawCenteredString(this.font, I18n.get("block.minecraft.beacon.secondary"), 169, 10, 14737632);
      Iterator var3 = this.buttons.iterator();

      while(var3.hasNext()) {
         AbstractWidget var4 = (AbstractWidget)var3.next();
         if (var4.isHovered()) {
            var4.renderToolTip(var1 - this.leftPos, var2 - this.topPos);
            break;
         }
      }

   }

   protected void renderBg(float var1, int var2, int var3) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BEACON_LOCATION);
      int var4 = (this.width - this.imageWidth) / 2;
      int var5 = (this.height - this.imageHeight) / 2;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
      this.itemRenderer.blitOffset = 100.0F;
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD), var4 + 42, var5 + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND), var4 + 42 + 22, var5 + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.GOLD_INGOT), var4 + 42 + 44, var5 + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.IRON_INGOT), var4 + 42 + 66, var5 + 109);
      this.itemRenderer.blitOffset = 0.0F;
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
   }

   class BeaconCancelButton extends BeaconScreen.BeaconSpriteScreenButton {
      public BeaconCancelButton(int var2, int var3) {
         super(var2, var3, 112, 220);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.player.connection.send((Packet)(new ServerboundContainerClosePacket(BeaconScreen.this.minecraft.player.containerMenu.containerId)));
         BeaconScreen.this.minecraft.setScreen((Screen)null);
      }

      public void renderToolTip(int var1, int var2) {
         BeaconScreen.this.renderTooltip(I18n.get("gui.cancel"), var1, var2);
      }
   }

   class BeaconConfirmButton extends BeaconScreen.BeaconSpriteScreenButton {
      public BeaconConfirmButton(int var2, int var3) {
         super(var2, var3, 90, 220);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.getConnection().send((Packet)(new ServerboundSetBeaconPacket(MobEffect.getId(BeaconScreen.this.primary), MobEffect.getId(BeaconScreen.this.secondary))));
         BeaconScreen.this.minecraft.player.connection.send((Packet)(new ServerboundContainerClosePacket(BeaconScreen.this.minecraft.player.containerMenu.containerId)));
         BeaconScreen.this.minecraft.setScreen((Screen)null);
      }

      public void renderToolTip(int var1, int var2) {
         BeaconScreen.this.renderTooltip(I18n.get("gui.done"), var1, var2);
      }
   }

   abstract static class BeaconSpriteScreenButton extends BeaconScreen.BeaconScreenButton {
      private final int iconX;
      private final int iconY;

      protected BeaconSpriteScreenButton(int var1, int var2, int var3, int var4) {
         super(var1, var2);
         this.iconX = var3;
         this.iconY = var4;
      }

      protected void renderIcon() {
         this.blit(this.x + 2, this.y + 2, this.iconX, this.iconY, 18, 18);
      }
   }

   class BeaconPowerButton extends BeaconScreen.BeaconScreenButton {
      private final MobEffect effect;
      private final TextureAtlasSprite sprite;
      private final boolean isPrimary;

      public BeaconPowerButton(int var2, int var3, MobEffect var4, boolean var5) {
         super(var2, var3);
         this.effect = var4;
         this.sprite = Minecraft.getInstance().getMobEffectTextures().get(var4);
         this.isPrimary = var5;
      }

      public void onPress() {
         if (!this.isSelected()) {
            if (this.isPrimary) {
               BeaconScreen.this.primary = this.effect;
            } else {
               BeaconScreen.this.secondary = this.effect;
            }

            BeaconScreen.this.buttons.clear();
            BeaconScreen.this.children.clear();
            BeaconScreen.this.init();
            BeaconScreen.this.tick();
         }
      }

      public void renderToolTip(int var1, int var2) {
         String var3 = I18n.get(this.effect.getDescriptionId());
         if (!this.isPrimary && this.effect != MobEffects.REGENERATION) {
            var3 = var3 + " II";
         }

         BeaconScreen.this.renderTooltip(var3, var1, var2);
      }

      protected void renderIcon() {
         Minecraft.getInstance().getTextureManager().bind(this.sprite.atlas().location());
         blit(this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.sprite);
      }
   }

   abstract static class BeaconScreenButton extends AbstractButton {
      private boolean selected;

      protected BeaconScreenButton(int var1, int var2) {
         super(var1, var2, 22, 22, "");
      }

      public void renderButton(int var1, int var2, float var3) {
         Minecraft.getInstance().getTextureManager().bind(BeaconScreen.BEACON_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         boolean var4 = true;
         int var5 = 0;
         if (!this.active) {
            var5 += this.width * 2;
         } else if (this.selected) {
            var5 += this.width * 1;
         } else if (this.isHovered()) {
            var5 += this.width * 3;
         }

         this.blit(this.x, this.y, var5, 219, this.width, this.height);
         this.renderIcon();
      }

      protected abstract void renderIcon();

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(boolean var1) {
         this.selected = var1;
      }
   }
}
