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
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class GhostSlots {
   private final Reference2ObjectMap<Slot, GhostSlots.GhostSlot> ingredients = new Reference2ObjectArrayMap();
   private final SlotSelectTime slotSelectTime;

   public GhostSlots(SlotSelectTime var1) {
      super();
      this.slotSelectTime = var1;
   }

   public void clear() {
      this.ingredients.clear();
   }

   public void addResult(ItemStack var1, Slot var2) {
      this.ingredients.put(var2, new GhostSlots.GhostSlot(List.of(var1), true));
   }

   public void addIngredient(List<ItemStack> var1, Slot var2) {
      this.ingredients.put(var2, new GhostSlots.GhostSlot(var1, false));
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
         GhostSlots.GhostSlot var6 = (GhostSlots.GhostSlot)this.ingredients.get(var5);
         if (var6 != null) {
            ItemStack var7 = var6.getItem(this.slotSelectTime.currentIndex());
            var1.renderComponentTooltip(var2.font, Screen.getTooltipFromItem(var2, var7), var3, var4, var7.get(DataComponents.TOOLTIP_STYLE));
         }
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
