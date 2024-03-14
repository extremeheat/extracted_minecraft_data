package net.minecraft.client.gui.screens.packs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;

public class PackSelectionModel {
   private final PackRepository repository;
   final List<Pack> selected;
   final List<Pack> unselected;
   final Function<Pack, ResourceLocation> iconGetter;
   final Runnable onListChanged;
   private final Consumer<PackRepository> output;

   public PackSelectionModel(Runnable var1, Function<Pack, ResourceLocation> var2, PackRepository var3, Consumer<PackRepository> var4) {
      super();
      this.onListChanged = var1;
      this.iconGetter = var2;
      this.repository = var3;
      this.selected = Lists.newArrayList(var3.getSelectedPacks());
      Collections.reverse(this.selected);
      this.unselected = Lists.newArrayList(var3.getAvailablePacks());
      this.unselected.removeAll(this.selected);
      this.output = var4;
   }

   public Stream<PackSelectionModel.Entry> getUnselected() {
      return this.unselected.stream().map(var1 -> new PackSelectionModel.UnselectedPackEntry(var1));
   }

   public Stream<PackSelectionModel.Entry> getSelected() {
      return this.selected.stream().map(var1 -> new PackSelectionModel.SelectedPackEntry(var1));
   }

   void updateRepoSelectedList() {
      this.repository.setSelected(Lists.reverse(this.selected).stream().map(Pack::getId).collect(ImmutableList.toImmutableList()));
   }

   public void commit() {
      this.updateRepoSelectedList();
      this.output.accept(this.repository);
   }

   public void findNewPacks() {
      this.repository.reload();
      this.selected.retainAll(this.repository.getAvailablePacks());
      this.unselected.clear();
      this.unselected.addAll(this.repository.getAvailablePacks());
      this.unselected.removeAll(this.selected);
   }

   public interface Entry {
      ResourceLocation getIconTexture();

      PackCompatibility getCompatibility();

      String getId();

      Component getTitle();

      Component getDescription();

      PackSource getPackSource();

      default Component getExtendedDescription() {
         return this.getPackSource().decorate(this.getDescription());
      }

      boolean isFixedPosition();

      boolean isRequired();

      void select();

      void unselect();

      void moveUp();

      void moveDown();

      boolean isSelected();

      default boolean canSelect() {
         return !this.isSelected();
      }

      default boolean canUnselect() {
         return this.isSelected() && !this.isRequired();
      }

      boolean canMoveUp();

      boolean canMoveDown();
   }

   abstract class EntryBase implements PackSelectionModel.Entry {
      private final Pack pack;

      public EntryBase(Pack var2) {
         super();
         this.pack = var2;
      }

      protected abstract List<Pack> getSelfList();

      protected abstract List<Pack> getOtherList();

      @Override
      public ResourceLocation getIconTexture() {
         return PackSelectionModel.this.iconGetter.apply(this.pack);
      }

      @Override
      public PackCompatibility getCompatibility() {
         return this.pack.getCompatibility();
      }

      @Override
      public String getId() {
         return this.pack.getId();
      }

      @Override
      public Component getTitle() {
         return this.pack.getTitle();
      }

      @Override
      public Component getDescription() {
         return this.pack.getDescription();
      }

      @Override
      public PackSource getPackSource() {
         return this.pack.getPackSource();
      }

      @Override
      public boolean isFixedPosition() {
         return this.pack.isFixedPosition();
      }

      @Override
      public boolean isRequired() {
         return this.pack.isRequired();
      }

      protected void toggleSelection() {
         this.getSelfList().remove(this.pack);
         this.pack.getDefaultPosition().insert(this.getOtherList(), this.pack, Pack::selectionConfig, true);
         PackSelectionModel.this.onListChanged.run();
         PackSelectionModel.this.updateRepoSelectedList();
         this.updateHighContrastOptionInstance();
      }

      private void updateHighContrastOptionInstance() {
         if (this.pack.getId().equals("high_contrast")) {
            OptionInstance var1 = Minecraft.getInstance().options.highContrast();
            var1.set(!var1.get());
         }
      }

      protected void move(int var1) {
         List var2 = this.getSelfList();
         int var3 = var2.indexOf(this.pack);
         var2.remove(var3);
         var2.add(var3 + var1, this.pack);
         PackSelectionModel.this.onListChanged.run();
      }

      @Override
      public boolean canMoveUp() {
         List var1 = this.getSelfList();
         int var2 = var1.indexOf(this.pack);
         return var2 > 0 && !((Pack)var1.get(var2 - 1)).isFixedPosition();
      }

      @Override
      public void moveUp() {
         this.move(-1);
      }

      @Override
      public boolean canMoveDown() {
         List var1 = this.getSelfList();
         int var2 = var1.indexOf(this.pack);
         return var2 >= 0 && var2 < var1.size() - 1 && !((Pack)var1.get(var2 + 1)).isFixedPosition();
      }

      @Override
      public void moveDown() {
         this.move(1);
      }
   }

   class SelectedPackEntry extends PackSelectionModel.EntryBase {
      public SelectedPackEntry(Pack var2) {
         super(var2);
      }

      @Override
      protected List<Pack> getSelfList() {
         return PackSelectionModel.this.selected;
      }

      @Override
      protected List<Pack> getOtherList() {
         return PackSelectionModel.this.unselected;
      }

      @Override
      public boolean isSelected() {
         return true;
      }

      @Override
      public void select() {
      }

      @Override
      public void unselect() {
         this.toggleSelection();
      }
   }

   class UnselectedPackEntry extends PackSelectionModel.EntryBase {
      public UnselectedPackEntry(Pack var2) {
         super(var2);
      }

      @Override
      protected List<Pack> getSelfList() {
         return PackSelectionModel.this.unselected;
      }

      @Override
      protected List<Pack> getOtherList() {
         return PackSelectionModel.this.selected;
      }

      @Override
      public boolean isSelected() {
         return false;
      }

      @Override
      public void select() {
         this.toggleSelection();
      }

      @Override
      public void unselect() {
      }
   }
}
