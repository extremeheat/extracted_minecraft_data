package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jspecify.annotations.Nullable;

public class CartographyTableScreen extends AbstractContainerScreen<CartographyTableMenu> {
   private static final Identifier ERROR_SPRITE = Identifier.withDefaultNamespace("container/cartography_table/error");
   private static final Identifier SCALED_MAP_SPRITE = Identifier.withDefaultNamespace("container/cartography_table/scaled_map");
   private static final Identifier DUPLICATED_MAP_SPRITE = Identifier.withDefaultNamespace("container/cartography_table/duplicated_map");
   private static final Identifier MAP_SPRITE = Identifier.withDefaultNamespace("container/cartography_table/map");
   private static final Identifier LOCKED_SPRITE = Identifier.withDefaultNamespace("container/cartography_table/locked");
   private static final Identifier BG_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/cartography_table.png");
   private final MapRenderState mapRenderState = new MapRenderState();

   public CartographyTableScreen(CartographyTableMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.titleLabelY -= 2;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = this.leftPos;
      int var6 = this.topPos;
      var1.blit(RenderPipelines.GUI_TEXTURED, BG_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      ItemStack var7 = ((CartographyTableMenu)this.menu).getSlot(1).getItem();
      boolean var8 = var7.is(Items.MAP);
      boolean var9 = var7.is(Items.PAPER);
      boolean var10 = var7.is(Items.GLASS_PANE);
      ItemStack var11 = ((CartographyTableMenu)this.menu).getSlot(0).getItem();
      MapId var12 = (MapId)var11.get(DataComponents.MAP_ID);
      boolean var14 = false;
      MapItemSavedData var13;
      if (var12 != null) {
         var13 = MapItem.getSavedData((MapId)var12, this.minecraft.level);
         if (var13 != null) {
            if (var13.locked) {
               var14 = true;
               if (var9 || var10) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)ERROR_SPRITE, var5 + 35, var6 + 31, 28, 21);
               }
            }

            if (var9 && var13.scale >= 4) {
               var14 = true;
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)ERROR_SPRITE, var5 + 35, var6 + 31, 28, 21);
            }
         }
      } else {
         var13 = null;
      }

      this.renderResultingMap(var1, var12, var13, var8, var9, var10, var14);
   }

   private void renderResultingMap(GuiGraphics var1, @Nullable MapId var2, @Nullable MapItemSavedData var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      int var8 = this.leftPos;
      int var9 = this.topPos;
      if (var5 && !var7) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SCALED_MAP_SPRITE, var8 + 67, var9 + 13, 66, 66);
         this.renderMap(var1, var2, var3, var8 + 85, var9 + 31, 0.226F);
      } else if (var4) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)DUPLICATED_MAP_SPRITE, var8 + 67 + 16, var9 + 13, 50, 66);
         this.renderMap(var1, var2, var3, var8 + 86, var9 + 16, 0.34F);
         var1.nextStratum();
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)DUPLICATED_MAP_SPRITE, var8 + 67, var9 + 13 + 16, 50, 66);
         this.renderMap(var1, var2, var3, var8 + 70, var9 + 32, 0.34F);
      } else if (var6) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)MAP_SPRITE, var8 + 67, var9 + 13, 66, 66);
         this.renderMap(var1, var2, var3, var8 + 71, var9 + 17, 0.45F);
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)LOCKED_SPRITE, var8 + 118, var9 + 60, 10, 14);
      } else {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)MAP_SPRITE, var8 + 67, var9 + 13, 66, 66);
         this.renderMap(var1, var2, var3, var8 + 71, var9 + 17, 0.45F);
      }

   }

   private void renderMap(GuiGraphics var1, @Nullable MapId var2, @Nullable MapItemSavedData var3, int var4, int var5, float var6) {
      if (var2 != null && var3 != null) {
         var1.pose().pushMatrix();
         var1.pose().translate((float)var4, (float)var5);
         var1.pose().scale(var6, var6);
         this.minecraft.getMapRenderer().extractRenderState(var2, var3, this.mapRenderState);
         var1.submitMapRenderState(this.mapRenderState);
         var1.pose().popMatrix();
      }

   }
}
