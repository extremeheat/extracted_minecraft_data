package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FletchingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FletchingBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
   public static final int INGREDIENT_SLOT = 0;
   public static final int OUTPUT_SLOT = 1;
   public static final int FLETCHING_SLOT = 2;
   public static final int TOTAL_SLOTS = 3;
   public static final int DATA_PROGRESS = 0;
   public static final int DATA_QUALITY = 1;
   public static final int DATA_SOURCE_IMPURITIES = 2;
   public static final int DATA_RESULT_IMPURITIES = 3;
   public static final int DATA_PROCESSS_TIME = 4;
   public static final int DATA_EXPLORED = 5;
   public static final int NUM_DATA_VALUES = 6;
   short progresss;
   public static final char START_CLARITY_CODE = 'a';
   public static final char END_CLARITY_CODE = 'j';
   public static final char START_IMPURITY_CODE = 'a';
   public static final char END_IMPURITY_CODE = 'p';
   public static final int MAX_PROCESSS_TIME = 200;
   char quality;
   char impurities;
   char nextLevelImpurities;
   boolean explored;
   short processsTime;
   private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{1};
   protected final ContainerData dataAccess = new ContainerData() {
      @Override
      public int get(int var1) {
         return switch(var1) {
            case 0 -> FletchingBlockEntity.this.progresss;
            case 1 -> FletchingBlockEntity.this.quality;
            case 2 -> FletchingBlockEntity.this.impurities;
            case 3 -> FletchingBlockEntity.this.nextLevelImpurities;
            case 4 -> FletchingBlockEntity.this.processsTime;
            case 5 -> FletchingBlockEntity.this.explored ? 1 : 0;
            default -> 0;
         };
      }

      @Override
      public void set(int var1, int var2) {
         if (var1 == 0) {
            FletchingBlockEntity.this.progresss = (short)var2;
         }
      }

      @Override
      public int getCount() {
         return 6;
      }
   };

   public FletchingBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.FLETCHING, var1, var2);
   }

   @Override
   protected Component getDefaultName() {
      return Component.translatable("container.fletching");
   }

   @Override
   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   @Override
   protected void setItems(NonNullList<ItemStack> var1) {
      this.items = var1;
   }

   @Override
   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new FletchingMenu(var1, var2, this, this.dataAccess);
   }

   @Override
   public void load(CompoundTag var1, HolderLookup.Provider var2) {
      super.load(var1, var2);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(var1, this.items, var2);
      this.quality = var1.getString("quality").charAt(0);
      this.impurities = var1.getString("impurities").charAt(0);
      this.nextLevelImpurities = var1.getString("nextLevelImpurities").charAt(0);
      this.processsTime = var1.getShort("processsTime");
      this.explored = var1.getBoolean("explored");
      this.progresss = var1.getShort("progresss");
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.putShort("progresss", this.progresss);
      var1.putString("quality", String.valueOf(this.quality));
      var1.putString("impurities", String.valueOf(this.impurities));
      var1.putString("nextLevelImpurities", String.valueOf(this.nextLevelImpurities));
      var1.putShort("processsTime", this.processsTime);
      var1.putBoolean("explored", this.explored);
      ContainerHelper.saveAllItems(var1, this.items, var2);
   }

   @Override
   public void applyComponents(DataComponentMap var1) {
      super.applyComponents(var1);
      FletchingBlockEntity.Fletching var2 = var1.getOrDefault(DataComponents.FLETCHING, FletchingBlockEntity.Fletching.EMPTY);
      this.quality = var2.quality();
      this.impurities = var2.impurities();
      this.nextLevelImpurities = var2.nextLevelImpurities();
      this.processsTime = var2.processsTime();
      this.explored = var2.explored();
   }

   @Override
   public void collectComponents(DataComponentMap.Builder var1) {
      super.collectComponents(var1);
      var1.set(
         DataComponents.FLETCHING,
         new FletchingBlockEntity.Fletching(this.quality, this.impurities, this.nextLevelImpurities, this.processsTime, this.explored)
      );
   }

   @Override
   public void removeComponentsFromTag(CompoundTag var1) {
      super.removeComponentsFromTag(var1);
      var1.remove("quality");
      var1.remove("impurities");
      var1.remove("nextLevelImpurities");
      var1.remove("processsTime");
      var1.remove("explored");
   }

   @Override
   public int getContainerSize() {
      return this.items.size();
   }

   @Override
   public int[] getSlotsForFace(Direction var1) {
      return var1 == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_UP;
   }

   @Override
   public boolean canPlaceItem(int var1, ItemStack var2) {
      if (var1 == 1) {
         return false;
      } else if (var1 == 0) {
         return this.processsTime == 0 ? false : canAcceptItem(var2, this.quality, this.impurities);
      } else if (var1 != 2) {
         return true;
      } else {
         return this.progresss == 0 && var2.is(Items.FEATHER);
      }
   }

   public static boolean canAcceptItem(ItemStack var0, char var1, char var2) {
      if (!var0.is(Items.TOXIC_RESIN)) {
         return false;
      } else {
         FletchingBlockEntity.Resin var3 = var0.getComponents().get(DataComponents.RESIN);
         if (var3 == null) {
            throw new IllegalStateException("Resin item without resin quality");
         } else {
            return var1 == var3.quality() && var3.impurities() == var2;
         }
      }
   }

   public static ItemStack createOutput(char var0, char var1) {
      if (var0 > 'j') {
         return new ItemStack(Items.AMBER_GEM);
      } else {
         ItemStack var2 = new ItemStack(Items.TOXIC_RESIN);
         var2.set(DataComponents.RESIN, new FletchingBlockEntity.Resin(var0, var1));
         return var2;
      }
   }

   @Override
   public boolean canPlaceItemThroughFace(int var1, ItemStack var2, @Nullable Direction var3) {
      return this.canPlaceItem(var1, var2);
   }

   @Override
   public boolean canTakeItemThroughFace(int var1, ItemStack var2, Direction var3) {
      return var1 != 0;
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, FletchingBlockEntity var3) {
      if (var3.processsTime == 0) {
         var3.processsTime = (short)var0.random.nextInt(10, 200);
         boolean var4 = true;
         var3.quality = (char)(97 + var0.random.nextInt(10));
         var3.impurities = FletchingBlockEntity.Resin.getRandomImpurities(var0.getRandom());
         var3.nextLevelImpurities = FletchingBlockEntity.Resin.getRandomImpurities(var0.getRandom());
         var3.explored = false;
      }

      ItemStack var6 = var3.items.get(1);
      if (var6.isEmpty() || var6.getCount() != var6.getMaxStackSize()) {
         if (var3.progresss > 0) {
            --var3.progresss;
            if (var3.progresss <= 0) {
               ItemStack var5 = createOutput((char)(var3.quality + 1), var3.nextLevelImpurities);
               if (!var6.isEmpty()) {
                  var5.setCount(var6.getCount() + 1);
               }

               var3.items.set(2, Items.FEATHER.getDefaultInstance());
               var3.items.set(1, var5);
               var3.explored = true;
               setChanged(var0, var1, var2);
            }
         }

         ItemStack var7 = var3.items.get(0);
         if (!var7.isEmpty()) {
            if (var3.progresss <= 0 && var3.items.get(2).is(Items.FEATHER)) {
               var3.items.set(2, ItemStack.EMPTY);
               var3.progresss = var3.processsTime;
               var7.shrink(1);
               setChanged(var0, var1, var2);
            }
         }
      }
   }

   public static final class Fletching implements TooltipProvider {
      public static final Codec<FletchingBlockEntity.Fletching> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.CHAR.fieldOf("quality").forGetter(FletchingBlockEntity.Fletching::quality),
                  ExtraCodecs.CHAR.fieldOf("impurities").forGetter(FletchingBlockEntity.Fletching::impurities),
                  ExtraCodecs.CHAR.fieldOf("next_level_impurities").forGetter(FletchingBlockEntity.Fletching::nextLevelImpurities),
                  Codec.SHORT.fieldOf("processs_time").forGetter(FletchingBlockEntity.Fletching::processsTime),
                  Codec.BOOL.optionalFieldOf("explored", false).forGetter(FletchingBlockEntity.Fletching::explored)
               )
               .apply(var0, FletchingBlockEntity.Fletching::new)
      );
      public static final StreamCodec<ByteBuf, FletchingBlockEntity.Fletching> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.CHAR,
         FletchingBlockEntity.Fletching::quality,
         ByteBufCodecs.CHAR,
         FletchingBlockEntity.Fletching::impurities,
         ByteBufCodecs.CHAR,
         FletchingBlockEntity.Fletching::nextLevelImpurities,
         ByteBufCodecs.SHORT,
         FletchingBlockEntity.Fletching::processsTime,
         ByteBufCodecs.BOOL,
         FletchingBlockEntity.Fletching::explored,
         FletchingBlockEntity.Fletching::new
      );
      public static final FletchingBlockEntity.Fletching EMPTY = new FletchingBlockEntity.Fletching('a', 'a', 'a', (short)0, false);
      private final char quality;
      private final char impurities;
      private final char nextLevelImpurities;
      private final short processsTime;
      private final boolean explored;

      public Fletching(char var1, char var2, char var3, short var4, boolean var5) {
         super();
         this.quality = var1;
         this.impurities = var2;
         this.nextLevelImpurities = var3;
         this.processsTime = var4;
         this.explored = var5;
      }

      public char quality() {
         return this.quality;
      }

      public char impurities() {
         return this.impurities;
      }

      public char nextLevelImpurities() {
         return this.nextLevelImpurities;
      }

      public short processsTime() {
         return this.processsTime;
      }

      public boolean explored() {
         return this.explored;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (var1 != null && var1.getClass() == this.getClass()) {
            FletchingBlockEntity.Fletching var2 = (FletchingBlockEntity.Fletching)var1;
            return this.quality == var2.quality
               && this.impurities == var2.impurities
               && this.nextLevelImpurities == var2.nextLevelImpurities
               && this.processsTime == var2.processsTime
               && this.explored == var2.explored;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.quality, this.impurities, this.nextLevelImpurities, this.processsTime, this.explored);
      }

      @Override
      public String toString() {
         return "Fletching[quality="
            + this.quality
            + ", impurities="
            + this.impurities
            + ", nextLevelImpurities="
            + this.nextLevelImpurities
            + ", processsTime="
            + this.processsTime
            + ", explored="
            + this.explored
            + "]";
      }

      @Override
      public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
         var1.accept(Component.translatable("block.minecraft.fletching_table.from"));
         var1.accept(CommonComponents.space().append(FletchingBlockEntity.Resin.getQualityComponent(this.quality)).withStyle(ChatFormatting.GRAY));
         var1.accept(CommonComponents.space().append(FletchingBlockEntity.Resin.getImpuritiesComponent(this.impurities)).withStyle(ChatFormatting.GRAY));
         var1.accept(Component.translatable("block.minecraft.fletching_table.to"));
         var1.accept(
            CommonComponents.space()
               .append(
                  this.quality >= 'j'
                     ? Component.translatable("item.minecraft.amber_gem").withStyle(ChatFormatting.GOLD)
                     : FletchingBlockEntity.Resin.getImpuritiesComponent(!this.explored ? "unknown" : this.nextLevelImpurities).withStyle(ChatFormatting.GRAY)
               )
         );
      }
   }

   public static final class Resin implements TooltipProvider {
      public static final Codec<FletchingBlockEntity.Resin> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.CHAR.fieldOf("quality").forGetter(FletchingBlockEntity.Resin::quality),
                  ExtraCodecs.CHAR.fieldOf("impurities").forGetter(FletchingBlockEntity.Resin::impurities)
               )
               .apply(var0, FletchingBlockEntity.Resin::new)
      );
      public static final StreamCodec<ByteBuf, FletchingBlockEntity.Resin> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.CHAR, FletchingBlockEntity.Resin::quality, ByteBufCodecs.CHAR, FletchingBlockEntity.Resin::impurities, FletchingBlockEntity.Resin::new
      );
      public static final FletchingBlockEntity.Resin EMPTY = new FletchingBlockEntity.Resin('a', 'a');
      private final char quality;
      private final char impurities;

      public Resin(char var1, char var2) {
         super();
         this.quality = var1;
         this.impurities = var2;
      }

      @Override
      public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
         var1.accept(getQualityComponent(this.quality).withStyle(ChatFormatting.GRAY));
         var1.accept(getImpuritiesComponent(this.impurities).withStyle(ChatFormatting.GRAY));
      }

      public static MutableComponent getQualityComponent(char var0) {
         return Component.translatable("item.resin.quality", Component.translatable("item.resin.clarity.adjective." + var0));
      }

      public static MutableComponent getImpuritiesComponent(Object var0) {
         return Component.translatable("item.resin.impurities", Component.translatable("item.resin.impurity.adjective." + var0));
      }

      public char quality() {
         return this.quality;
      }

      public char impurities() {
         return this.impurities;
      }

      public static char getRandomImpurities(RandomSource var0) {
         boolean var1 = true;
         return (char)(97 + var0.nextInt(16));
      }

      @Override
      public boolean equals(Object var1) {
         if (!(var1 instanceof FletchingBlockEntity.Resin)) {
            return false;
         } else {
            FletchingBlockEntity.Resin var2 = (FletchingBlockEntity.Resin)var1;
            return this.quality == var2.quality && this.impurities == var2.impurities;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.quality, this.impurities);
      }
   }
}
