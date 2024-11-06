package net.minecraft.client.gui.screens.recipebook;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class GhostSlots {
   private final Reference2ObjectMap<Slot, GhostSlot> ingredients = new Reference2ObjectArrayMap();
   private final SlotSelectTime slotSelectTime;

   public GhostSlots(SlotSelectTime var1) {
      super();
      this.slotSelectTime = var1;
   }

   public void clear() {
      this.ingredients.clear();
   }

   private void setSlot(Slot var1, ContextMap var2, SlotDisplay var3, boolean var4) {
      List var5 = var3.resolveForStacks(var2);
      if (!var5.isEmpty()) {
         this.ingredients.put(var1, new GhostSlot(var5, var4));
      }

   }

   protected void setInput(Slot var1, ContextMap var2, SlotDisplay var3) {
      this.setSlot(var1, var2, var3, false);
   }

   protected void setResult(Slot var1, ContextMap var2, SlotDisplay var3) {
      this.setSlot(var1, var2, var3, true);
   }

   public void render(GuiGraphics var1, Minecraft var2, boolean var3) {
      this.ingredients.forEach((var4, var5) -> {
         int var6 = var4.x;
         int var7 = var4.y;
         if (var5.isResultSlot && var3) {
            var1.fill(var6 - 4, var7 - 4, var6 + 20, var7 + 20, 822018048);
         } else {
            var1.fill(var6, var7, var6 + 16, var7 + 16, 822018048);
         }

         ItemStack var8 = var5.getItem(this.slotSelectTime.currentIndex());
         var1.renderFakeItem(var8, var6, var7);
         var1.fill(RenderType.guiGhostRecipeOverlay(), var6, var7, var6 + 16, var7 + 16, 822083583);
         if (var5.isResultSlot) {
            var1.renderItemDecorations(var2.font, var8, var6, var7);
         }

      });
   }

   public void renderTooltip(GuiGraphics var1, Minecraft var2, int var3, int var4, @Nullable Slot var5) {
      if (var5 != null) {
         GhostSlot var6 = (GhostSlot)this.ingredients.get(var5);
         if (var6 != null) {
            ItemStack var7 = var6.getItem(this.slotSelectTime.currentIndex());
            var1.renderComponentTooltip(var2.font, Screen.getTooltipFromItem(var2, var7), var3, var4, (ResourceLocation)var7.get(DataComponents.TOOLTIP_STYLE));
         }

      }
   }

   private static record GhostSlot(List<ItemStack> items, boolean isResultSlot) {
      final boolean isResultSlot;

      GhostSlot(List<ItemStack> var1, boolean var2) {
         super();
         this.items = var1;
         this.isResultSlot = var2;
      }

      public ItemStack getItem(int var1) {
         int var2 = this.items.size();
         return var2 == 0 ? ItemStack.EMPTY : (ItemStack)this.items.get(var1 % var2);
      }

      public List<ItemStack> items() {
         return this.items;
      }

      public boolean isResultSlot() {
         return this.isResultSlot;
      }
   }
}