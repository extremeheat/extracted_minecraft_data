package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class LecternBlockEntity extends BlockEntity implements Clearable, MenuProvider {
   public static final int DATA_PAGE = 0;
   public static final int NUM_DATA = 1;
   public static final int SLOT_BOOK = 0;
   public static final int NUM_SLOTS = 1;
   private final Container bookAccess = new Container() {
      public int getContainerSize() {
         return 1;
      }

      public boolean isEmpty() {
         return LecternBlockEntity.this.book.isEmpty();
      }

      public ItemStack getItem(int var1) {
         return var1 == 0 ? LecternBlockEntity.this.book : ItemStack.EMPTY;
      }

      public ItemStack removeItem(int var1, int var2) {
         if (var1 == 0) {
            ItemStack var3 = LecternBlockEntity.this.book.split(var2);
            if (LecternBlockEntity.this.book.isEmpty()) {
               LecternBlockEntity.this.onBookItemRemove();
            }

            return var3;
         } else {
            return ItemStack.EMPTY;
         }
      }

      public ItemStack removeItemNoUpdate(int var1) {
         if (var1 == 0) {
            ItemStack var2 = LecternBlockEntity.this.book;
            LecternBlockEntity.this.book = ItemStack.EMPTY;
            LecternBlockEntity.this.onBookItemRemove();
            return var2;
         } else {
            return ItemStack.EMPTY;
         }
      }

      public void setItem(int var1, ItemStack var2) {
      }

      public int getMaxStackSize() {
         return 1;
      }

      public void setChanged() {
         LecternBlockEntity.this.setChanged();
      }

      public boolean stillValid(Player var1) {
         return Container.stillValidBlockEntity(LecternBlockEntity.this, var1) && LecternBlockEntity.this.hasBook();
      }

      public boolean canPlaceItem(int var1, ItemStack var2) {
         return false;
      }

      public void clearContent() {
      }
   };
   private final ContainerData dataAccess = new ContainerData() {
      public int get(int var1) {
         return var1 == 0 ? LecternBlockEntity.this.page : 0;
      }

      public void set(int var1, int var2) {
         if (var1 == 0) {
            LecternBlockEntity.this.setPage(var2);
         }

      }

      public int getCount() {
         return 1;
      }
   };
   ItemStack book;
   int page;
   private int pageCount;

   public LecternBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.LECTERN, var1, var2);
      this.book = ItemStack.EMPTY;
   }

   public ItemStack getBook() {
      return this.book;
   }

   public boolean hasBook() {
      return this.book.is(Items.WRITABLE_BOOK) || this.book.is(Items.WRITTEN_BOOK);
   }

   public void setBook(ItemStack var1) {
      this.setBook(var1, (Player)null);
   }

   void onBookItemRemove() {
      this.page = 0;
      this.pageCount = 0;
      LecternBlock.resetBookState((Entity)null, this.getLevel(), this.getBlockPos(), this.getBlockState(), false);
   }

   public void setBook(ItemStack var1, @Nullable Player var2) {
      this.book = this.resolveBook(var1, var2);
      this.page = 0;
      this.pageCount = getPageCount(this.book);
      this.setChanged();
   }

   void setPage(int var1) {
      int var2 = Mth.clamp(var1, 0, this.pageCount - 1);
      if (var2 != this.page) {
         this.page = var2;
         this.setChanged();
         LecternBlock.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
      }

   }

   public int getPage() {
      return this.page;
   }

   public int getRedstoneSignal() {
      float var1 = this.pageCount > 1 ? (float)this.getPage() / ((float)this.pageCount - 1.0F) : 1.0F;
      return Mth.floor(var1 * 14.0F) + (this.hasBook() ? 1 : 0);
   }

   private ItemStack resolveBook(ItemStack var1, @Nullable Player var2) {
      if (this.level instanceof ServerLevel && var1.is(Items.WRITTEN_BOOK)) {
         WrittenBookItem.resolveBookComponents(var1, this.createCommandSourceStack(var2), var2);
      }

      return var1;
   }

   private CommandSourceStack createCommandSourceStack(@Nullable Player var1) {
      String var2;
      Object var3;
      if (var1 == null) {
         var2 = "Lectern";
         var3 = Component.literal("Lectern");
      } else {
         var2 = var1.getName().getString();
         var3 = var1.getDisplayName();
      }

      Vec3 var4 = Vec3.atCenterOf(this.worldPosition);
      return new CommandSourceStack(CommandSource.NULL, var4, Vec2.ZERO, (ServerLevel)this.level, 2, var2, (Component)var3, this.level.getServer(), var1);
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      if (var1.contains("Book", 10)) {
         this.book = this.resolveBook((ItemStack)ItemStack.parse(var2, var1.getCompound("Book")).orElse(ItemStack.EMPTY), (Player)null);
      } else {
         this.book = ItemStack.EMPTY;
      }

      this.pageCount = getPageCount(this.book);
      this.page = Mth.clamp(var1.getInt("Page"), 0, this.pageCount - 1);
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      if (!this.getBook().isEmpty()) {
         var1.put("Book", this.getBook().save(var2));
         var1.putInt("Page", this.page);
      }

   }

   public void clearContent() {
      this.setBook(ItemStack.EMPTY);
   }

   public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      return new LecternMenu(var1, this.bookAccess, this.dataAccess);
   }

   public Component getDisplayName() {
      return Component.translatable("container.lectern");
   }

   private static int getPageCount(ItemStack var0) {
      WrittenBookContent var1 = (WrittenBookContent)var0.get(DataComponents.WRITTEN_BOOK_CONTENT);
      if (var1 != null) {
         return var1.pages().size();
      } else {
         WritableBookContent var2 = (WritableBookContent)var0.get(DataComponents.WRITABLE_BOOK_CONTENT);
         return var2 != null ? var2.pages().size() : 0;
      }
   }
}
