package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeToast implements Toast {
   private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("toast/recipe");
   private static final long DISPLAY_TIME = 5000L;
   private static final Component TITLE_TEXT = Component.translatable("recipe.toast.title");
   private static final Component DESCRIPTION_TEXT = Component.translatable("recipe.toast.description");
   private final List<RecipeToast.RecipeDisplayItems> recipeItems = Lists.newArrayList();
   private long lastChanged;
   private boolean changed;
   private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;
   private int displayedRecipeIndex;

   public RecipeToast(ItemStack var1, ItemStack var2) {
      super();
      this.recipeItems.add(new RecipeToast.RecipeDisplayItems(var1, var2));
   }

   @Override
   public Toast.Visibility getWantedVisibility() {
      return this.wantedVisibility;
   }

   @Override
   public void update(ToastManager var1, long var2) {
      if (this.changed) {
         this.lastChanged = var2;
         this.changed = false;
      }

      if (this.recipeItems.isEmpty()) {
         this.wantedVisibility = Toast.Visibility.HIDE;
      } else {
         this.wantedVisibility = (double)(var2 - this.lastChanged) >= 5000.0 * var1.getNotificationDisplayTimeMultiplier()
            ? Toast.Visibility.HIDE
            : Toast.Visibility.SHOW;
      }

      this.displayedRecipeIndex = (int)(
         (double)var2 / Math.max(1.0, 5000.0 * var1.getNotificationDisplayTimeMultiplier() / (double)this.recipeItems.size()) % (double)this.recipeItems.size()
      );
   }

   @Override
   public void render(GuiGraphics var1, Font var2, long var3) {
      var1.blitSprite(RenderType::guiTextured, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
      var1.drawString(var2, TITLE_TEXT, 30, 7, -11534256, false);
      var1.drawString(var2, DESCRIPTION_TEXT, 30, 18, -16777216, false);
      RecipeToast.RecipeDisplayItems var5 = this.recipeItems.get(this.displayedRecipeIndex);
      var1.pose().pushPose();
      var1.pose().scale(0.6F, 0.6F, 1.0F);
      var1.renderFakeItem(var5.categoryItem(), 3, 3);
      var1.pose().popPose();
      var1.renderFakeItem(var5.unlockedItem(), 8, 8);
   }

   private void addItem(ItemStack var1, ItemStack var2) {
      this.recipeItems.add(new RecipeToast.RecipeDisplayItems(var1, var2));
      this.changed = true;
   }

   public static void addOrUpdate(ToastManager var0, RecipeHolder<?> var1) {
      RecipeToast var2 = var0.getToast(RecipeToast.class, NO_TOKEN);
      ItemStack var3 = var1.value().getCategoryIconItem();
      ItemStack var4 = var1.value().getResultItem(var0.getMinecraft().level.registryAccess());
      if (var2 == null) {
         var0.addToast(new RecipeToast(var3, var4));
      } else {
         var2.addItem(var3, var4);
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
