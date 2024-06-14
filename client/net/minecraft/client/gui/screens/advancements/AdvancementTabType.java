package net.minecraft.client.gui.screens.advancements;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

enum AdvancementTabType {
   ABOVE(
      new AdvancementTabType.Sprites(
         ResourceLocation.withDefaultNamespace("advancements/tab_above_left_selected"),
         ResourceLocation.withDefaultNamespace("advancements/tab_above_middle_selected"),
         ResourceLocation.withDefaultNamespace("advancements/tab_above_right_selected")
      ),
      new AdvancementTabType.Sprites(
         ResourceLocation.withDefaultNamespace("advancements/tab_above_left"),
         ResourceLocation.withDefaultNamespace("advancements/tab_above_middle"),
         ResourceLocation.withDefaultNamespace("advancements/tab_above_right")
      ),
      28,
      32,
      8
   ),
   BELOW(
      new AdvancementTabType.Sprites(
         ResourceLocation.withDefaultNamespace("advancements/tab_below_left_selected"),
         ResourceLocation.withDefaultNamespace("advancements/tab_below_middle_selected"),
         ResourceLocation.withDefaultNamespace("advancements/tab_below_right_selected")
      ),
      new AdvancementTabType.Sprites(
         ResourceLocation.withDefaultNamespace("advancements/tab_below_left"),
         ResourceLocation.withDefaultNamespace("advancements/tab_below_middle"),
         ResourceLocation.withDefaultNamespace("advancements/tab_below_right")
      ),
      28,
      32,
      8
   ),
   LEFT(
      new AdvancementTabType.Sprites(
         ResourceLocation.withDefaultNamespace("advancements/tab_left_top_selected"),
         ResourceLocation.withDefaultNamespace("advancements/tab_left_middle_selected"),
         ResourceLocation.withDefaultNamespace("advancements/tab_left_bottom_selected")
      ),
      new AdvancementTabType.Sprites(
         ResourceLocation.withDefaultNamespace("advancements/tab_left_top"),
         ResourceLocation.withDefaultNamespace("advancements/tab_left_middle"),
         ResourceLocation.withDefaultNamespace("advancements/tab_left_bottom")
      ),
      32,
      28,
      5
   ),
   RIGHT(
      new AdvancementTabType.Sprites(
         ResourceLocation.withDefaultNamespace("advancements/tab_right_top_selected"),
         ResourceLocation.withDefaultNamespace("advancements/tab_right_middle_selected"),
         ResourceLocation.withDefaultNamespace("advancements/tab_right_bottom_selected")
      ),
      new AdvancementTabType.Sprites(
         ResourceLocation.withDefaultNamespace("advancements/tab_right_top"),
         ResourceLocation.withDefaultNamespace("advancements/tab_right_middle"),
         ResourceLocation.withDefaultNamespace("advancements/tab_right_bottom")
      ),
      32,
      28,
      5
   );

   private final AdvancementTabType.Sprites selectedSprites;
   private final AdvancementTabType.Sprites unselectedSprites;
   private final int width;
   private final int height;
   private final int max;

   private AdvancementTabType(
      final AdvancementTabType.Sprites nullxx, final AdvancementTabType.Sprites nullxxx, final int nullxxxx, final int nullxxxxx, final int nullxxxxxx
   ) {
      this.selectedSprites = nullxx;
      this.unselectedSprites = nullxxx;
      this.width = nullxxxx;
      this.height = nullxxxxx;
      this.max = nullxxxxxx;
   }

   public int getMax() {
      return this.max;
   }

   public void draw(GuiGraphics var1, int var2, int var3, boolean var4, int var5) {
      AdvancementTabType.Sprites var6 = var4 ? this.selectedSprites : this.unselectedSprites;
      ResourceLocation var7;
      if (var5 == 0) {
         var7 = var6.first();
      } else if (var5 == this.max - 1) {
         var7 = var6.last();
      } else {
         var7 = var6.middle();
      }

      var1.blitSprite(var7, var2 + this.getX(var5), var3 + this.getY(var5), this.width, this.height);
   }

   public void drawIcon(GuiGraphics var1, int var2, int var3, int var4, ItemStack var5) {
      int var6 = var2 + this.getX(var4);
      int var7 = var3 + this.getY(var4);
      switch (this) {
         case ABOVE:
            var6 += 6;
            var7 += 9;
            break;
         case BELOW:
            var6 += 6;
            var7 += 6;
            break;
         case LEFT:
            var6 += 10;
            var7 += 5;
            break;
         case RIGHT:
            var6 += 6;
            var7 += 5;
      }

      var1.renderFakeItem(var5, var6, var7);
   }

   public int getX(int var1) {
      switch (this) {
         case ABOVE:
            return (this.width + 4) * var1;
         case BELOW:
            return (this.width + 4) * var1;
         case LEFT:
            return -this.width + 4;
         case RIGHT:
            return 248;
         default:
            throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
      }
   }

   public int getY(int var1) {
      switch (this) {
         case ABOVE:
            return -this.height + 4;
         case BELOW:
            return 136;
         case LEFT:
            return this.height * var1;
         case RIGHT:
            return this.height * var1;
         default:
            throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
      }
   }

   public boolean isMouseOver(int var1, int var2, int var3, double var4, double var6) {
      int var8 = var1 + this.getX(var3);
      int var9 = var2 + this.getY(var3);
      return var4 > (double)var8 && var4 < (double)(var8 + this.width) && var6 > (double)var9 && var6 < (double)(var9 + this.height);
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
