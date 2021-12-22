package net.minecraft.client.gui.screens.packs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
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
      return this.unselected.stream().map((var1) -> {
         return new PackSelectionModel.UnselectedPackEntry(var1);
      });
   }

   public Stream<PackSelectionModel.Entry> getSelected() {
      return this.selected.stream().map((var1) -> {
         return new PackSelectionModel.SelectedPackEntry(var1);
      });
   }

   public void commit() {
      this.repository.setSelected((Collection)Lists.reverse(this.selected).stream().map(Pack::getId).collect(ImmutableList.toImmutableList()));
      this.output.accept(this.repository);
   }

   public void findNewPacks() {
      this.repository.reload();
      this.selected.retainAll(this.repository.getAvailablePacks());
      this.unselected.clear();
      this.unselected.addAll(this.repository.getAvailablePacks());
      this.unselected.removeAll(this.selected);
   }

   class SelectedPackEntry extends PackSelectionModel.EntryBase {
      public SelectedPackEntry(Pack var2) {
         super(var2);
      }

      protected List<Pack> getSelfList() {
         return PackSelectionModel.this.selected;
      }

      protected List<Pack> getOtherList() {
         return PackSelectionModel.this.unselected;
      }

      public boolean isSelected() {
         return true;
      }

      public void select() {
      }

      public void unselect() {
         this.toggleSelection();
      }
   }

   class UnselectedPackEntry extends PackSelectionModel.EntryBase {
      public UnselectedPackEntry(Pack var2) {
         super(var2);
      }

      protected List<Pack> getSelfList() {
         return PackSelectionModel.this.unselected;
      }

      protected List<Pack> getOtherList() {
         return PackSelectionModel.this.selected;
      }

      public boolean isSelected() {
         return false;
      }

      public void select() {
         this.toggleSelection();
      }

      public void unselect() {
      }
   }

   private abstract class EntryBase implements PackSelectionModel.Entry {
      private final Pack pack;

      public EntryBase(Pack var2) {
         super();
         this.pack = var2;
      }

      protected abstract List<Pack> getSelfList();

      protected abstract List<Pack> getOtherList();

      public ResourceLocation getIconTexture() {
         return (ResourceLocation)PackSelectionModel.this.iconGetter.apply(this.pack);
      }

      public PackCompatibility getCompatibility() {
         return this.pack.getCompatibility();
      }

      public Component getTitle() {
         return this.pack.getTitle();
      }

      public Component getDescription() {
         return this.pack.getDescription();
      }

      public PackSource getPackSource() {
         return this.pack.getPackSource();
      }

      public boolean isFixedPosition() {
         return this.pack.isFixedPosition();
      }

      public boolean isRequired() {
         return this.pack.isRequired();
      }

      protected void toggleSelection() {
         this.getSelfList().remove(this.pack);
         this.pack.getDefaultPosition().insert(this.getOtherList(), this.pack, Function.identity(), true);
         PackSelectionModel.this.onListChanged.run();
      }

      protected void move(int var1) {
         List var2 = this.getSelfList();
         int var3 = var2.indexOf(this.pack);
         var2.remove(var3);
         var2.add(var3 + var1, this.pack);
         PackSelectionModel.this.onListChanged.run();
      }

      public boolean canMoveUp() {
         List var1 = this.getSelfList();
         int var2 = var1.indexOf(this.pack);
         return var2 > 0 && !((Pack)var1.get(var2 - 1)).isFixedPosition();
      }

      public void moveUp() {
         this.move(-1);
      }

      public boolean canMoveDown() {
         List var1 = this.getSelfList();
         int var2 = var1.indexOf(this.pack);
         return var2 >= 0 && var2 < var1.size() - 1 && !((Pack)var1.get(var2 + 1)).isFixedPosition();
      }

      public void moveDown() {
         this.move(1);
      }
   }

   public interface Entry {
      ResourceLocation getIconTexture();

      PackCompatibility getCompatibility();

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
}
