package net.minecraft.client.gui.screens.controls;

import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import org.apache.commons.lang3.ArrayUtils;

public class ControlList extends ContainerObjectSelectionList {
   private final ControlsScreen controlsScreen;
   private int maxNameWidth;

   public ControlList(ControlsScreen var1, Minecraft var2) {
      super(var2, var1.width + 45, var1.height, 43, var1.height - 32, 20);
      this.controlsScreen = var1;
      KeyMapping[] var3 = (KeyMapping[])ArrayUtils.clone(var2.options.keyMappings);
      Arrays.sort(var3);
      String var4 = null;
      KeyMapping[] var5 = var3;
      int var6 = var3.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         KeyMapping var8 = var5[var7];
         String var9 = var8.getCategory();
         if (!var9.equals(var4)) {
            var4 = var9;
            this.addEntry(new ControlList.CategoryEntry(var9));
         }

         int var10 = var2.font.width(I18n.get(var8.getName()));
         if (var10 > this.maxNameWidth) {
            this.maxNameWidth = var10;
         }

         this.addEntry(new ControlList.KeyEntry(var8));
      }

   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 15;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 32;
   }

   public class KeyEntry extends ControlList.Entry {
      private final KeyMapping key;
      private final String name;
      private final Button changeButton;
      private final Button resetButton;

      private KeyEntry(final KeyMapping var2) {
         this.key = var2;
         this.name = I18n.get(var2.getName());
         this.changeButton = new Button(0, 0, 75, 20, this.name, (var2x) -> {
            ControlList.this.controlsScreen.selectedKey = var2;
         }) {
            protected String getNarrationMessage() {
               return var2.isUnbound() ? I18n.get("narrator.controls.unbound", KeyEntry.this.name) : I18n.get("narrator.controls.bound", KeyEntry.this.name, super.getNarrationMessage());
            }
         };
         this.resetButton = new Button(0, 0, 50, 20, I18n.get("controls.reset"), (var2x) -> {
            ControlList.this.minecraft.options.setKey(var2, var2.getDefaultKey());
            KeyMapping.resetMapping();
         }) {
            protected String getNarrationMessage() {
               return I18n.get("narrator.controls.reset", KeyEntry.this.name);
            }
         };
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         boolean var10 = ControlList.this.controlsScreen.selectedKey == this.key;
         Font var10000 = ControlList.this.minecraft.font;
         String var10001 = this.name;
         float var10002 = (float)(var3 + 90 - ControlList.this.maxNameWidth);
         int var10003 = var2 + var5 / 2;
         ControlList.this.minecraft.font.getClass();
         var10000.draw(var10001, var10002, (float)(var10003 - 9 / 2), 16777215);
         this.resetButton.x = var3 + 190;
         this.resetButton.y = var2;
         this.resetButton.active = !this.key.isDefault();
         this.resetButton.render(var6, var7, var9);
         this.changeButton.x = var3 + 105;
         this.changeButton.y = var2;
         this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
         boolean var11 = false;
         if (!this.key.isUnbound()) {
            KeyMapping[] var12 = ControlList.this.minecraft.options.keyMappings;
            int var13 = var12.length;

            for(int var14 = 0; var14 < var13; ++var14) {
               KeyMapping var15 = var12[var14];
               if (var15 != this.key && this.key.same(var15)) {
                  var11 = true;
                  break;
               }
            }
         }

         if (var10) {
            this.changeButton.setMessage(ChatFormatting.WHITE + "> " + ChatFormatting.YELLOW + this.changeButton.getMessage() + ChatFormatting.WHITE + " <");
         } else if (var11) {
            this.changeButton.setMessage(ChatFormatting.RED + this.changeButton.getMessage());
         }

         this.changeButton.render(var6, var7, var9);
      }

      public List children() {
         return ImmutableList.of(this.changeButton, this.resetButton);
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (this.changeButton.mouseClicked(var1, var3, var5)) {
            return true;
         } else {
            return this.resetButton.mouseClicked(var1, var3, var5);
         }
      }

      public boolean mouseReleased(double var1, double var3, int var5) {
         return this.changeButton.mouseReleased(var1, var3, var5) || this.resetButton.mouseReleased(var1, var3, var5);
      }

      // $FF: synthetic method
      KeyEntry(KeyMapping var2, Object var3) {
         this(var2);
      }
   }

   public class CategoryEntry extends ControlList.Entry {
      private final String name;
      private final int width;

      public CategoryEntry(String var2) {
         this.name = I18n.get(var2);
         this.width = ControlList.this.minecraft.font.width(this.name);
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         Font var10000 = ControlList.this.minecraft.font;
         String var10001 = this.name;
         float var10002 = (float)(ControlList.this.minecraft.screen.width / 2 - this.width / 2);
         int var10003 = var2 + var5;
         ControlList.this.minecraft.font.getClass();
         var10000.draw(var10001, var10002, (float)(var10003 - 9 - 1), 16777215);
      }

      public boolean changeFocus(boolean var1) {
         return false;
      }

      public List children() {
         return Collections.emptyList();
      }
   }

   public abstract static class Entry extends ContainerObjectSelectionList.Entry {
   }
}
