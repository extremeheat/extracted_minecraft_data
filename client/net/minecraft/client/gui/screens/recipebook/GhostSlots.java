package net.minecraft.client.gui.screens.recipebook;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
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

   public void render(GuiGraphics var1, Minecraft var2, int var3, int var4, boolean var5) {
      this.ingredients.forEach((var6, var7) -> {
         int var8 = var6.x + var3;
         int var9 = var6.y + var4;
         if (var7.isResultSlot && var5) {
            var1.fill(var8 - 4, var9 - 4, var8 + 20, var9 + 20, 822018048);
         } else {
            var1.fill(var8, var9, var8 + 16, var9 + 16, 822018048);
         }

         ItemStack var10 = var7.getItem(this.slotSelectTime.currentIndex());
         var1.renderFakeItem(var10, var8, var9);
         var1.fill(RenderType.guiGhostRecipeOverlay(), var8, var9, var8 + 16, var9 + 16, 822083583);
         if (var7.isResultSlot) {
            var1.renderItemDecorations(var2.font, var10, var8, var9);
         }
      });
   }

   public void renderTooltip(GuiGraphics var1, Minecraft var2, int var3, int var4, @Nullable Slot var5) {
      if (var5 != null) {
         GhostSlots.GhostSlot var6 = (GhostSlots.GhostSlot)this.ingredients.get(var5);
         if (var6 != null) {
            ItemStack var7 = var6.getItem(this.slotSelectTime.currentIndex());
            var1.renderComponentTooltip(var2.font, Screen.getTooltipFromItem(var2, var7), var3, var4);
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
