package net.minecraft.client.gui.screens.resourcepacks;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.resourcepacks.lists.AvailableResourcePackList;
import net.minecraft.client.gui.screens.resourcepacks.lists.ResourcePackList;
import net.minecraft.client.gui.screens.resourcepacks.lists.SelectedResourcePackList;
import net.minecraft.client.resources.UnopenedResourcePack;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.repository.PackRepository;

public class ResourcePackSelectScreen extends Screen {
   private final Screen parentScreen;
   private AvailableResourcePackList availableResourcePackList;
   private SelectedResourcePackList selectedResourcePackList;
   private boolean changed;

   public ResourcePackSelectScreen(Screen var1) {
      super(new TranslatableComponent("resourcePack.title", new Object[0]));
      this.parentScreen = var1;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 154, this.height - 48, 150, 20, I18n.get("resourcePack.openFolder"), (var1x) -> {
         Util.getPlatform().openFile(this.minecraft.getResourcePackDirectory());
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height - 48, 150, 20, I18n.get("gui.done"), (var1x) -> {
         if (this.changed) {
            ArrayList var2 = Lists.newArrayList();
            Iterator var3 = this.selectedResourcePackList.children().iterator();

            while(var3.hasNext()) {
               ResourcePackList.ResourcePackEntry var4 = (ResourcePackList.ResourcePackEntry)var3.next();
               var2.add(var4.getResourcePack());
            }

            Collections.reverse(var2);
            this.minecraft.getResourcePackRepository().setSelected(var2);
            this.minecraft.options.resourcePacks.clear();
            this.minecraft.options.incompatibleResourcePacks.clear();
            var3 = var2.iterator();

            while(var3.hasNext()) {
               UnopenedResourcePack var5 = (UnopenedResourcePack)var3.next();
               if (!var5.isFixedPosition()) {
                  this.minecraft.options.resourcePacks.add(var5.getId());
                  if (!var5.getCompatibility().isCompatible()) {
                     this.minecraft.options.incompatibleResourcePacks.add(var5.getId());
                  }
               }
            }

            this.minecraft.options.save();
            this.minecraft.setScreen(this.parentScreen);
            this.minecraft.reloadResourcePacks();
         } else {
            this.minecraft.setScreen(this.parentScreen);
         }

      }));
      AvailableResourcePackList var1 = this.availableResourcePackList;
      SelectedResourcePackList var2 = this.selectedResourcePackList;
      this.availableResourcePackList = new AvailableResourcePackList(this.minecraft, 200, this.height);
      this.availableResourcePackList.setLeftPos(this.width / 2 - 4 - 200);
      if (var1 != null) {
         this.availableResourcePackList.children().addAll(var1.children());
      }

      this.children.add(this.availableResourcePackList);
      this.selectedResourcePackList = new SelectedResourcePackList(this.minecraft, 200, this.height);
      this.selectedResourcePackList.setLeftPos(this.width / 2 + 4);
      if (var2 != null) {
         this.selectedResourcePackList.children().addAll(var2.children());
      }

      this.children.add(this.selectedResourcePackList);
      if (!this.changed) {
         this.availableResourcePackList.children().clear();
         this.selectedResourcePackList.children().clear();
         PackRepository var3 = this.minecraft.getResourcePackRepository();
         var3.reload();
         ArrayList var4 = Lists.newArrayList(var3.getAvailable());
         var4.removeAll(var3.getSelected());
         Iterator var5 = var4.iterator();

         UnopenedResourcePack var6;
         while(var5.hasNext()) {
            var6 = (UnopenedResourcePack)var5.next();
            this.availableResourcePackList.addResourcePackEntry(new ResourcePackList.ResourcePackEntry(this.availableResourcePackList, this, var6));
         }

         var5 = Lists.reverse(Lists.newArrayList(var3.getSelected())).iterator();

         while(var5.hasNext()) {
            var6 = (UnopenedResourcePack)var5.next();
            this.selectedResourcePackList.addResourcePackEntry(new ResourcePackList.ResourcePackEntry(this.selectedResourcePackList, this, var6));
         }
      }

   }

   public void select(ResourcePackList.ResourcePackEntry var1) {
      this.availableResourcePackList.children().remove(var1);
      var1.addToList(this.selectedResourcePackList);
      this.setChanged();
   }

   public void deselect(ResourcePackList.ResourcePackEntry var1) {
      this.selectedResourcePackList.children().remove(var1);
      this.availableResourcePackList.addResourcePackEntry(var1);
      this.setChanged();
   }

   public boolean isSelected(ResourcePackList.ResourcePackEntry var1) {
      return this.selectedResourcePackList.children().contains(var1);
   }

   public void render(int var1, int var2, float var3) {
      this.renderDirtBackground(0);
      this.availableResourcePackList.render(var1, var2, var3);
      this.selectedResourcePackList.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.font, I18n.get("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
      super.render(var1, var2, var3);
   }

   public void setChanged() {
      this.changed = true;
   }
}
