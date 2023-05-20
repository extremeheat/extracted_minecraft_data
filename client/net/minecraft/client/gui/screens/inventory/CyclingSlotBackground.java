package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

public class CyclingSlotBackground {
   private static final int ICON_CHANGE_TICK_RATE = 30;
   private static final int ICON_SIZE = 16;
   private static final int ICON_TRANSITION_TICK_DURATION = 4;
   private final int slotIndex;
   private List<ResourceLocation> icons = List.of();
   private int tick;
   private int iconIndex;

   public CyclingSlotBackground(int var1) {
      super();
      this.slotIndex = var1;
   }

   public void tick(List<ResourceLocation> var1) {
      if (!this.icons.equals(var1)) {
         this.icons = var1;
         this.iconIndex = 0;
      }

      if (!this.icons.isEmpty() && ++this.tick % 30 == 0) {
         this.iconIndex = (this.iconIndex + 1) % this.icons.size();
      }
   }

   public void render(AbstractContainerMenu var1, PoseStack var2, float var3, int var4, int var5) {
      Slot var6 = var1.getSlot(this.slotIndex);
      if (!this.icons.isEmpty() && !var6.hasItem()) {
         boolean var7 = this.icons.size() > 1 && this.tick >= 30;
         float var8 = var7 ? this.getIconTransitionTransparency(var3) : 1.0F;
         if (var8 < 1.0F) {
            int var9 = Math.floorMod(this.iconIndex - 1, this.icons.size());
            this.renderIcon(var6, this.icons.get(var9), 1.0F - var8, var2, var4, var5);
         }

         this.renderIcon(var6, this.icons.get(this.iconIndex), var8, var2, var4, var5);
      }
   }

   private void renderIcon(Slot var1, ResourceLocation var2, float var3, PoseStack var4, int var5, int var6) {
      TextureAtlasSprite var7 = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(var2);
      RenderSystem.setShaderTexture(0, var7.atlasLocation());
      GuiComponent.blit(var4, var5 + var1.x, var6 + var1.y, 0, 16, 16, var7, 1.0F, 1.0F, 1.0F, var3);
   }

   private float getIconTransitionTransparency(float var1) {
      float var2 = (float)(this.tick % 30) + var1;
      return Math.min(var2, 4.0F) / 4.0F;
   }
}
