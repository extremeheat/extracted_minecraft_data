package net.minecraft.world.inventory;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.RoomerinoComponentino;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.HubLevelSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class DoorMenu extends AbstractContainerMenu {
   public static final int BUTTON_NEXT = 0;
   public static final int BUTTON_PREV = 1;
   public static final int BUTTON_PLOP = 2;
   private static final int MAGIC_SLOT = 0;
   private static final int INV_SLOT_START = 1;
   private static final int INV_SLOT_END = 28;
   private static final int USE_ROW_SLOT_START = 28;
   private static final int USE_ROW_SLOT_END = 37;
   public static final Layout ERROR;
   private final ContainerLevelAccess access;
   private final List<Layout> displays;
   @Nullable
   private final List<PlacementInfo> actualPlacements;
   private final DataSlot index;
   private final SimpleContainer storage;

   private static Layout fromText(FieldType var0, String... var1) {
      int var2 = var1[0].length();
      ArrayList var3 = new ArrayList(var1.length * var2);

      for(String var7 : var1) {
         if (var7.length() != var2) {
            throw new IllegalStateException("WRONG!");
         }

         Stream var10000 = var7.chars().mapToObj((var0x) -> var0x == 32 ? DoorMenu.FieldType.EMPTY : DoorMenu.FieldType.AWWW_SAD);
         Objects.requireNonNull(var3);
         var10000.forEach(var3::add);
      }

      return new Layout(var3, var2);
   }

   public DoorMenu(int var1, Inventory var2, List<Layout> var3) {
      this(var1, var2, ContainerLevelAccess.NULL, var3, (List)null);
   }

   public DoorMenu(int var1, Inventory var2, ContainerLevelAccess var3, List<Layout> var4, @Nullable List<PlacementInfo> var5) {
      super((MenuType)null, var1);
      this.index = DataSlot.standalone();
      this.storage = new SimpleContainer(1);
      this.access = var3;
      this.displays = var4;
      this.actualPlacements = var5;
      this.addSlot(new NonInteractiveResultSlot(this.storage, 0, 8, 8) {
         public boolean isHighlightable() {
            return true;
         }
      });
      this.addStandardInventorySlots(var2, 8, 165);
      this.addDataSlot(this.index);
   }

   public void setKey(ItemStack var1) {
      this.storage.setItem(0, var1);
   }

   public ItemStack getKey() {
      return this.storage.getItem(0);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 >= 1 && var2 < 28) {
            if (!this.moveItemStackTo(var5, 28, 37, false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= 28 && var2 < 37 && !this.moveItemStackTo(var5, 1, 28, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
      }

      return var3;
   }

   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.SHIMMERING_DOOR);
   }

   @Nullable
   public Layout getOption() {
      return this.displays.isEmpty() ? null : (Layout)this.displays.get(this.index.get());
   }

   public boolean clickMenuButton(Player var1, int var2) {
      int var3 = this.displays.size();
      if (var3 == 0) {
         return false;
      } else {
         switch (var2) {
            case 0 -> this.index.set(Math.floorMod(this.index.get() + 1, var3));
            case 1 -> this.index.set(Math.floorMod(this.index.get() - 1, var3));
            case 2 -> this.access.execute((var2x, var3x) -> {
   if (this.actualPlacements == null) {
      var1.closeContainer();
   } else {
      PlacementInfo var4 = (PlacementInfo)this.actualPlacements.get(this.index.get());
      if (var2x instanceof ServerLevel) {
         ServerLevel var5 = (ServerLevel)var2x;
         this.storage.setItem(0, ItemStack.EMPTY);
         var1.closeContainer();
         RandomSource var6 = RandomSource.create(var4.seed);
         var4.settings.setRandom(var6);
         var4.structure.placeInWorld(var5, var4.origin, var4.origin, var4.settings, var2x.random, 3);

         for(BlockPos var8 : var4.doorsToOpen) {
            BlockState var9 = var5.getBlockState(var8);
            BlockState var10 = (BlockState)var9.trySetValue(DoorBlock.OPEN, true);
            var5.setBlock(var8, var10, 3);
         }
      }

   }
});
         }

         return super.clickMenuButton(var1, var2);
      }
   }

   private static ColumnPos column(BlockPos var0) {
      return new ColumnPos(var0.getX(), var0.getZ());
   }

   private static List<ColumnPos> columnNeighbours(ColumnPos var0) {
      int var1 = var0.x();
      int var2 = var0.z();
      return List.of(new ColumnPos(var1 - 1, var2 - 1), new ColumnPos(var1, var2 - 1), new ColumnPos(var1 + 1, var2 - 1), new ColumnPos(var1 - 1, var2), new ColumnPos(var1 + 1, var2), new ColumnPos(var1 - 1, var2 + 1), new ColumnPos(var1, var2 + 1), new ColumnPos(var1 + 1, var2 + 1));
   }

   public static List<Pair<Layout, PlacementInfo>> extractLayout(ServerLevel var0, BlockPos var1, Direction var2, DoubleBlockHalf var3, RoomerinoComponentino var4) {
      Optional var5 = var0.theGame().getStructureManager().get(var4.structureId());
      if (var5.isPresent()) {
         StructureTemplate var6 = (StructureTemplate)var5.get();
         Direction var7 = var2.getOpposite();
         BlockPos var8 = var1.relative(var7);
         ArrayList var9 = new ArrayList();
         StructurePlaceSettings var10 = new StructurePlaceSettings();
         long var11 = var0.random.nextLong();
         RandomSource var13 = RandomSource.create(var11);
         var10.setRandom(var13);
         var10.addProcessor(HubLevelSource.SKYIFIER);

         for(Rotation var17 : Rotation.values()) {
            var10.setRotation(var17);
            var13.setSeed(var11);
            BlockPos var18 = BlockPos.ZERO;
            ObjectArrayList var19 = var6.getBlocks(var18, var10, true);
            Map var20 = findInterestingDoors(var19);
            List var21 = var20.entrySet().stream().filter((var2x) -> isMatchingDoor((BlockState)var2x.getValue(), var3, var7)).map((var1x) -> var8.subtract((Vec3i)var1x.getKey())).toList();
            if (!var21.isEmpty()) {
               Object2ObjectMap var22 = computeBaseRoomLayout(var19);
               var21.forEach((var10x) -> {
                  HashSet var11x = new HashSet();
                  Map var12 = scanSurroundings(var0, var1, var10x, var19, var22, var20, var11x);
                  if (var12 != null) {
                     Layout var13 = createLayout(var12);
                     if (var13 != null) {
                        PlacementInfo var14 = new PlacementInfo(var6, var10x, var10.copy(), var11, var11x);
                        var9.add(Pair.of(var13, var14));
                     }
                  }

               });
            }
         }

         return var9;
      } else {
         return List.of();
      }
   }

   private static List<BlockPos> wallNeighbours(BlockPos var0) {
      return List.of(var0.relative(Direction.NORTH), var0.relative(Direction.SOUTH), var0.relative(Direction.EAST), var0.relative(Direction.WEST));
   }

   @Nullable
   private static Map<ColumnPos, FieldType> scanSurroundings(ServerLevel var0, BlockPos var1, BlockPos var2, List<StructureTemplate.StructureBlockInfo> var3, Map<ColumnPos, FieldType> var4, Map<BlockPos, BlockState> var5, Set<BlockPos> var6) {
      Object2ObjectOpenHashMap var7 = new Object2ObjectOpenHashMap();
      var7.defaultReturnValue(DoorMenu.FieldType.EMPTY);

      for(StructureTemplate.StructureBlockInfo var9 : var3) {
         BlockPos var10 = var9.pos().offset(var2);
         if (!var0.getBlockState(var10).isAir()) {
            return null;
         }
      }

      shiftAndCopyRoom(var2, var4, var7);
      markAdjacentWalls(var0, var2, var3, var7);
      markMatchingDoors(var0, var2, var5, var7, var6);
      var7.put(column(var1), DoorMenu.FieldType.ACTIVE_DOOR);
      return var7;
   }

   private static void markMatchingDoors(ServerLevel var0, BlockPos var1, Map<BlockPos, BlockState> var2, Object2ObjectMap<ColumnPos, FieldType> var3, Set<BlockPos> var4) {
      var2.forEach((var4x, var5) -> {
         BlockPos var6 = var4x.offset(var1);
         Direction var7 = (Direction)var5.getValue(DoorBlock.FACING);
         DoubleBlockHalf var8 = (DoubleBlockHalf)var5.getValue(DoorBlock.HALF);
         Direction var9 = var7.getOpposite();
         BlockPos var10 = var6.relative(var9);
         BlockState var11 = var0.getBlockState(var10);
         if (isMatchingDoor(var11, var8, var9)) {
            var3.put(column(var10), DoorMenu.FieldType.EXISTING_DOOR);
            var4.add(var6);
            var4.add(var10);
         }

      });
   }

   private static void shiftAndCopyRoom(BlockPos var0, Map<ColumnPos, FieldType> var1, Object2ObjectMap<ColumnPos, FieldType> var2) {
      var1.forEach((var2x, var3) -> {
         ColumnPos var4 = new ColumnPos(var2x.x() + var0.getX(), var2x.z() + var0.getZ());
         var2.put(var4, var3);
      });
   }

   private static void markAdjacentWalls(ServerLevel var0, BlockPos var1, List<StructureTemplate.StructureBlockInfo> var2, Map<ColumnPos, FieldType> var3) {
      Set var4 = (Set)var2.stream().map((var1x) -> var1x.pos().offset(var1)).collect(Collectors.toSet());
      Stream var10000 = var4.stream().flatMap((var0x) -> wallNeighbours(var0x).stream()).filter((var1x) -> !var4.contains(var1x)).distinct();
      Function var10001 = (var0x) -> var0x;
      Objects.requireNonNull(var0);
      Map var5 = (Map)var10000.collect(Collectors.toMap(var10001, var0::getBlockState));
      Map var6 = (Map)var5.entrySet().stream().collect(Collectors.groupingBy((var0x) -> column((BlockPos)var0x.getKey())));
      var6.forEach((var1x, var2x) -> {
         boolean var3x = var2x.stream().anyMatch((var0) -> !((BlockState)var0.getValue()).isAir());
         if (var3x) {
            var3.put(var1x, DoorMenu.FieldType.EXISTING_WALL);
         }

      });
   }

   private static Map<BlockPos, BlockState> findInterestingDoors(List<StructureTemplate.StructureBlockInfo> var0) {
      HashMap var1 = new HashMap();

      for(StructureTemplate.StructureBlockInfo var3 : var0) {
         if (isInterestingDoor(var3.state())) {
            var1.put(var3.pos(), var3.state());
         }
      }

      return var1;
   }

   private static boolean isInterestingDoor(BlockState var0) {
      return var0.is(Blocks.SHIMMERING_DOOR) && !(Boolean)var0.getValue(DoorBlock.OPEN);
   }

   private static boolean isMatchingDoor(BlockState var0, DoubleBlockHalf var1, Direction var2) {
      return isInterestingDoor(var0) && var0.getValue(DoorBlock.FACING) == var2 && var0.getValue(DoorBlock.HALF) == var1;
   }

   private static Object2ObjectMap<ColumnPos, FieldType> computeBaseRoomLayout(List<StructureTemplate.StructureBlockInfo> var0) {
      Map var1 = (Map)var0.stream().collect(Collectors.groupingBy((var0x) -> column(var0x.pos())));
      Object2ObjectOpenHashMap var2 = new Object2ObjectOpenHashMap();
      var2.defaultReturnValue(DoorMenu.FieldType.EMPTY);
      var1.forEach((var1x, var2x) -> var2.put(var1x, categorizeRoomColumn(var2x)));
      Set var3 = (Set)var2.entrySet().stream().filter((var0x) -> isRoom((FieldType)var0x.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
      fixupWalls(var2, var3);
      return var2;
   }

   @Nullable
   private static Layout createLayout(Map<ColumnPos, FieldType> var0) {
      Rect var1 = DoorMenu.Rect.compute(var0.keySet());
      Layout var2;
      if (var1 != null) {
         int var3 = var1.width();
         int var4 = var1.height();
         ArrayList var5 = new ArrayList(var3 * var4);

         for(int var6 = 0; var6 < var3 * var4; ++var6) {
            var5.add(DoorMenu.FieldType.EMPTY);
         }

         var0.forEach((var2x, var3x) -> var5.set(var1.index(var2x), var3x));
         var2 = new Layout(var5, var3);
      } else {
         var2 = null;
      }

      return var2;
   }

   private static void fixupWalls(Object2ObjectMap<ColumnPos, FieldType> var0, Set<ColumnPos> var1) {
      for(ColumnPos var3 : var1) {
         if (var0.get(var3) == DoorMenu.FieldType.NEW_ROOM) {
            boolean var4 = columnNeighbours(var3).stream().anyMatch((var1x) -> shouldHaveWall((FieldType)var0.get(var1x)));
            if (var4) {
               var0.put(var3, DoorMenu.FieldType.NEW_WALL);
            }
         }
      }

   }

   private static boolean isRoom(FieldType var0) {
      return var0 == DoorMenu.FieldType.NEW_DOOR || var0 == DoorMenu.FieldType.NEW_ROOM || var0 == DoorMenu.FieldType.NEW_WALL;
   }

   private static boolean shouldHaveWall(FieldType var0) {
      return !isRoom(var0);
   }

   private static FieldType categorizeRoomColumn(List<StructureTemplate.StructureBlockInfo> var0) {
      boolean var1 = false;

      for(StructureTemplate.StructureBlockInfo var3 : var0) {
         if (var3.state().is(Blocks.SHIMMERING_DOOR)) {
            return DoorMenu.FieldType.NEW_DOOR;
         }

         if (!var3.state().isAir()) {
            var1 = true;
         }
      }

      return var1 ? DoorMenu.FieldType.NEW_ROOM : DoorMenu.FieldType.EMPTY;
   }

   public void removed(Player var1) {
      super.removed(var1);
      this.access.execute((var2, var3) -> this.clearContainer(var1, this.storage));
   }

   static {
      ERROR = fromText(DoorMenu.FieldType.AWWW_SAD, "X X   X X", " X     X ", "X X   X X", "         ", "    X    ", "   X     ", "  XXXX   ", "         ", "         ", "  XXXX   ", " X    X  ", "X      X ");
   }

   public static enum FieldType {
      EMPTY(0, (DyeColor)null, ' ', (Component)null),
      NEW_ROOM(1, DyeColor.GREEN, '\u2588', Component.translatable("gui.door.field.new_room")),
      NEW_WALL(2, DyeColor.LIME, '+', Component.translatable("gui.door.field.new_wall")),
      NEW_DOOR(3, DyeColor.PURPLE, '#', Component.translatable("gui.door.field.new_door")),
      EXISTING_WALL(4, DyeColor.GRAY, 'o', Component.translatable("gui.door.field.existing_wall")),
      EXISTING_DOOR(5, DyeColor.RED, 'x', Component.translatable("gui.door.field.existing_door")),
      ACTIVE_DOOR(6, DyeColor.YELLOW, '@', Component.translatable("gui.door.field.active_door")),
      AWWW_SAD(7, DyeColor.CYAN, '!', Component.translatable("gui.door.field.error"));

      private final int id;
      @Nullable
      public final DyeColor color;
      public final char debugChar;
      @Nullable
      public final Component tooltip;
      public static final IntFunction<FieldType> BY_ID = ByIdMap.<FieldType>continuous(FieldType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      public static final StreamCodec<ByteBuf, FieldType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, FieldType::getId);

      private FieldType(final int var3, @Nullable final DyeColor var4, final char var5, @Nullable final Component var6) {
         this.id = var3;
         this.color = var4;
         this.debugChar = var5;
         this.tooltip = var6;
      }

      public int getId() {
         return this.id;
      }

      // $FF: synthetic method
      private static FieldType[] $values() {
         return new FieldType[]{EMPTY, NEW_ROOM, NEW_WALL, NEW_DOOR, EXISTING_WALL, EXISTING_DOOR, ACTIVE_DOOR, AWWW_SAD};
      }
   }

   public static record Layout(List<FieldType> fields, int width) {
      public static final StreamCodec<ByteBuf, Layout> STREAM_CODEC;

      public Layout(List<FieldType> var1, int var2) {
         super();
         this.fields = var1;
         this.width = var2;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         int var2 = this.fields.size();

         for(int var3 = 0; var3 < var2; ++var3) {
            var1.append(((FieldType)this.fields.get(var3)).debugChar);
            if (var3 % this.width == this.width - 1) {
               var1.append("\n");
            }
         }

         return var1.toString();
      }

      static {
         STREAM_CODEC = StreamCodec.composite(DoorMenu.FieldType.STREAM_CODEC.apply(ByteBufCodecs.list()), Layout::fields, ByteBufCodecs.VAR_INT, Layout::width, Layout::new);
      }
   }

   static record Rect(int zMin, int zMax, int xMin, int xMax) {
      private Rect(int var1, int var2, int var3, int var4) {
         super();
         this.zMin = var1;
         this.zMax = var2;
         this.xMin = var3;
         this.xMax = var4;
      }

      public static Rect single(ColumnPos var0) {
         return new Rect(var0.z(), var0.z(), var0.x(), var0.x());
      }

      public int width() {
         return this.xMax - this.xMin + 1;
      }

      public int height() {
         return this.zMax - this.zMin + 1;
      }

      public int index(ColumnPos var1) {
         int var2 = var1.x() - this.xMin;
         int var3 = var1.z() - this.zMin;
         return var3 * this.width() + var2;
      }

      public Rect append(Rect var1) {
         return new Rect(Math.min(this.zMin, var1.zMin), Math.max(this.zMax, var1.zMax), Math.min(this.xMin, var1.xMin), Math.max(this.xMax, var1.xMax));
      }

      @Nullable
      public static Rect compute(Collection<ColumnPos> var0) {
         List var1 = var0.stream().map(Rect::single).toList();
         if (var1.isEmpty()) {
            return null;
         } else {
            Rect var2 = (Rect)var1.getFirst();

            for(int var3 = 1; var3 < var1.size(); ++var3) {
               var2 = var2.append((Rect)var1.get(var3));
            }

            return var2;
         }
      }
   }

   public static record PlacementInfo(StructureTemplate structure, BlockPos origin, StructurePlaceSettings settings, long seed, Set<BlockPos> doorsToOpen) {
      final StructureTemplate structure;
      final BlockPos origin;
      final StructurePlaceSettings settings;
      final long seed;
      final Set<BlockPos> doorsToOpen;

      public PlacementInfo(StructureTemplate var1, BlockPos var2, StructurePlaceSettings var3, long var4, Set<BlockPos> var6) {
         super();
         this.structure = var1;
         this.origin = var2;
         this.settings = var3;
         this.seed = var4;
         this.doorsToOpen = var6;
      }
   }
}
