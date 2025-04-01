package net.minecraft.world.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.DimensionGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.WorldModifiers;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.MineCrafterBlockEntity;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.mines.SpecialMine;
import net.minecraft.world.level.mines.WorldEffect;
import net.minecraft.world.level.mines.WorldEffectSet;
import net.minecraft.world.level.mines.WorldEffects;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.Vec2;

public class MineCraftingMenu extends AbstractContainerMenu {
   public static final List<Vec2> SPIRAL_POSITIONS = List.of(new Vec2(93.0F, 82.0F), new Vec2(117.0F, 69.0F), new Vec2(115.0F, 41.0F), new Vec2(85.0F, 31.0F), new Vec2(57.0F, 54.0F), new Vec2(63.0F, 89.0F), new Vec2(128.0F, 96.0F), new Vec2(150.0F, 68.0F), new Vec2(148.0F, 30.0F), new Vec2(111.0F, 15.0F), new Vec2(60.0F, 15.0F), new Vec2(31.0F, 32.0F), new Vec2(22.0F, 57.0F), new Vec2(27.0F, 89.0F));
   private final GhettoSpline ghettoSpline;
   private final ContainerLevelAccess access;
   private final Consumer<ResourceKey<LevelStem>> dimensionCreatedCallback;
   private final List<MineCraftingSlot> craftingSlots;
   private final MineCraftingResultSlot resultSlot;
   private final List<MineCraftingDrawerSlot> drawerSlots;
   private final List<MineCraftingHintSlot> hintSlots;
   public static final int DRAWER_NUM_COLS = 9;
   public static final int DRAWER_NUM_ROWS = 4;
   private static final int HINTS_NUM_COLS = 6;
   private static final int HINTS_NUM_ROWS = 10;
   private int nrOfSlots;
   private int nrOfRandomSlots;
   private int currentLevel;
   private int currentExp;
   private final Optional<ServerLevel> serverLevel;
   private final Optional<ServerPlayer> serverPlayer;
   private final Optional<BlockPos> pos;

   public MineCraftingMenu(int var1, Inventory var2, List<Integer> var3) {
      this(var1, var2, ContainerLevelAccess.NULL, new SimpleContainer(99), (var0) -> {
      }, var3);
   }

   public MineCraftingMenu(int var1, Inventory var2, ContainerLevelAccess var3, Container var4, Consumer<ResourceKey<LevelStem>> var5, List<Integer> var6) {
      super(MenuType.MAP_MAKING, var1);
      this.ghettoSpline = new GhettoSpline(SPIRAL_POSITIONS);
      this.craftingSlots = new ArrayList();
      this.drawerSlots = new ArrayList();
      this.hintSlots = new ArrayList();
      this.setAdditionalData(var6);
      checkContainerSize(var4, 99);
      this.access = var3;
      this.dimensionCreatedCallback = var5;
      var4.startOpen(var2.getPlayer());
      Player var8 = var2.getPlayer();
      if (var8 instanceof ServerPlayer var7) {
         this.serverPlayer = Optional.of(var7);
      } else {
         this.serverPlayer = Optional.empty();
      }

      this.pos = var3.<BlockPos>evaluate((var0, var1x) -> var1x);
      this.serverLevel = var3.<ServerLevel>evaluate((var0, var1x) -> {
         if (var0 instanceof ServerLevel var2) {
            return var2;
         } else {
            return null;
         }
      });
      this.resultSlot = (MineCraftingResultSlot)this.addSlot(new MineCraftingResultSlot(var4, 0, 88, 58, this));
      this.addCraftingSlots(var4);
      this.updateCraftingSlots();
      this.populateRandomEffects();
      this.addMineEffectDrawer();
      this.addLockedEffectHintBox();
      this.slotsChanged(var4);
   }

   private void setAdditionalData(List<Integer> var1) {
      this.currentLevel = (Integer)var1.get(0);
      this.currentExp = (Integer)var1.get(1);
      this.nrOfSlots = MineCrafterBlockEntity.craftingSlotsForLevel(this.currentLevel);
      this.nrOfRandomSlots = MineCrafterBlockEntity.randomCraftingSlotsForLevel(this.currentLevel);
   }

   public int getCurrentLevel() {
      return this.currentLevel;
   }

   public int getCurrentExp() {
      return this.currentExp;
   }

   private void addCraftingSlots(Container var1) {
      for(int var2 = 0; var2 < 50; ++var2) {
         MineCraftingSlot var3 = (MineCraftingSlot)this.addSlot(new MineCraftingSlot(var1, 1 + var2, 0, 0, this, true));
         var3.setActive(false);
         this.craftingSlots.add(var3);
      }

   }

   private void updateCraftingSlots() {
      float var1 = (float)(SPIRAL_POSITIONS.size() - 1) / (float)this.nrOfSlots;
      int var2 = 0;

      for(MineCraftingSlot var4 : this.craftingSlots) {
         if (var2 >= this.nrOfSlots) {
            var4.randomSlot = false;
            var4.setActive(false);
         } else {
            float var5 = var1 * (float)var2;
            Vec2 var6 = (Vec2)SPIRAL_POSITIONS.get((int)Math.floor((double)var5));
            Vec2 var7 = (Vec2)SPIRAL_POSITIONS.get((int)Math.floor((double)var5) + 1);
            Vec2 var8 = var6.add(var7.add(var6.negated()).scale(var5 - (float)Mth.floor(var5)));
            boolean var9 = var2 < this.nrOfRandomSlots;
            if (!var4.randomSlot && var9) {
               var4.set(ItemStack.EMPTY);
            }

            var4.randomSlot = var9;
            var4.x = (int)var8.x;
            var4.y = (int)var8.y;
            var4.setActive(true);
            ++var2;
         }
      }

   }

   private void populateRandomEffects() {
      if (this.serverLevel.isPresent()) {
         if (!this.isBossMine()) {
            boolean var1 = this.getCraftingEffects().findAny().isEmpty();
            if (var1 && this.resultSlot.getItem().isEmpty()) {
               Optional var2 = ((ServerLevel)this.serverLevel.get()).getNextSpecialMine();
               if (var2.isPresent()) {
                  ItemStack var8 = new ItemStack(Items.MINE);
                  var8.set(DataComponents.WORLD_MODIFIERS, new WorldModifiers(new ArrayList(), false));
                  var8.set(DataComponents.ITEM_NAME, ((SpecialMine)var2.get()).name());
                  var8.set(DataComponents.LORE, new ItemLore(List.of(((SpecialMine)var2.get()).description())));
                  var8.set(DataComponents.SPECIAL_MINE, (SpecialMine)var2.get());
                  this.resultSlot.set(var8);
                  this.broadcastChanges();
                  return;
               }
            }

            List var7 = this.getMustHaveEffects();
            ArrayList var3 = new ArrayList();

            for(MineCraftingSlot var5 : this.craftingSlots) {
               if (var5.randomSlot && !var5.hasItem()) {
                  if (!var7.isEmpty()) {
                     int var6 = this.getNextLevel();
                     var5.set(WorldEffects.createEffectItem(Component.translatableEscape("world.mine.base", var6), false, List.copyOf(var7)));
                     var3.addAll(var7);
                     var7.clear();
                  } else {
                     Optional var9 = getRandomEffect((ServerLevel)this.serverLevel.get(), var3, (Set)this.getCraftingEffects().collect(Collectors.toSet()));
                     var9.ifPresent((var2x) -> {
                        var5.set(WorldEffects.createEffectItem(var2x, false));
                        var3.add(var2x);
                     });
                  }
               }
            }

         }
      }
   }

   public boolean isBossMine() {
      return this.resultSlot.hasItem() && this.resultSlot.getItem().has(DataComponents.SPECIAL_MINE);
   }

   public static Optional<WorldEffect> getRandomEffect(ServerLevel var0, List<WorldEffect> var1, Set<WorldEffect> var2) {
      WeightedList.Builder var3 = WeightedList.builder();

      for(WorldEffect var5 : BuiltInRegistries.WORLD_EFFECT) {
         if (!var5.inSets().stream().anyMatch(WorldEffectSet::exclusive) && var5.randomWeight() > 0 && var5.isValidWith(var1) && !var2.contains(var5) && var5.canRandomize(var0)) {
            boolean var6 = var0.isEffectUnlocked(var5);
            if (var6) {
               var3.add(var5, var5.randomWeight());
            } else {
               var3.add(var5, (int)((float)var5.randomWeight() * 0.1F));
            }
         }
      }

      return var3.build().getRandom(var0.getRandom());
   }

   private List<WorldEffect> getMustHaveEffects() {
      ServerLevel var1 = (ServerLevel)this.serverLevel.get();
      RandomSource var2 = var1.getRandom();
      ArrayList var3 = new ArrayList();

      for(WorldEffectSet var5 : BuiltInRegistries.WORLD_EFFECT_SET) {
         var3.add(this.pickRandomEffect(var5.effects().stream().filter((var1x) -> var1x.canRandomize(var1)).toList(), var3, var2));
      }

      Collections.shuffle(var3);
      return var3;
   }

   private WorldEffect pickRandomEffect(List<WorldEffect> var1, List<WorldEffect> var2, RandomSource var3) {
      if (var1.isEmpty()) {
         throw new IllegalArgumentException("Tried to pick random effect from fully incompatible sets");
      } else {
         WorldEffect var4 = (WorldEffect)Util.getRandom(var1, var3);
         return var4.isValidWith(var2) ? var4 : this.pickRandomEffect(var1.stream().filter((var1x) -> var1x != var4).toList(), var2, var3);
      }
   }

   private int getNextLevel() {
      if (this.serverLevel.isEmpty()) {
         return 0;
      } else {
         ServerLevelData var1 = ((ServerLevel)this.serverLevel.get()).theGame().getWorldData().overworldData();
         return var1.getLevelCount() + 1;
      }
   }

   private void addMineEffectDrawer() {
      int var1 = Mth.ceil((float)BuiltInRegistries.WORLD_EFFECT.size() / 9.0F);
      SimpleContainer var2 = new SimpleContainer(var1 * 9);
      if (this.serverLevel.isPresent()) {
         for(WorldEffect var4 : BuiltInRegistries.WORLD_EFFECT) {
            ItemStack var5 = WorldEffects.createEffectItem(var4, false);
            if (var4.canUse((ServerLevel)this.serverLevel.get()) && ((ServerLevel)this.serverLevel.get()).isEffectUnlocked(var4)) {
               var2.addItem(var5);
            }
         }
      }

      for(int var6 = 0; var6 < var1; ++var6) {
         for(int var7 = 0; var7 < 9; ++var7) {
            this.drawerSlots.add((MineCraftingDrawerSlot)this.addSlot(new MineCraftingDrawerSlot(var2, var6 * 9 + var7, 8 + var7 * 18, 124 + var6 * 18, this)));
         }
      }

   }

   private void addLockedEffectHintBox() {
      SimpleContainer var1 = new SimpleContainer(60);
      if (this.serverLevel.isPresent()) {
         boolean var2 = ((ServerLevel)this.serverLevel.get()).theGame().server().isSingleplayer();
         ArrayList var3 = new ArrayList(BuiltInRegistries.WORLD_EFFECT.stream().toList());
         Collections.shuffle(var3);

         for(WorldEffect var5 : var3) {
            if (!var5.multiplayerOnly() || !var2) {
               ItemStack var6 = WorldEffects.createEffectItem(var5, false);
               if (var1.canAddItem(var6) && !((ServerLevel)this.serverLevel.get()).isEffectUnlocked(var5) && var5.canUnlock((ServerLevel)this.serverLevel.get())) {
                  var6.set(DataComponents.WORLD_EFFECT_HINT, Unit.INSTANCE);
                  var1.addItem(var6);
               }
            }
         }
      }

      for(int var7 = 0; var7 < 10; ++var7) {
         for(int var8 = 0; var8 < 6; ++var8) {
            this.hintSlots.add((MineCraftingHintSlot)this.addSlot(new MineCraftingHintSlot(var1, var7 * 6 + var8, 205 + var8 * 18, 20 + var7 * 18, this)));
         }
      }

   }

   public GhettoSpline getSpline() {
      return this.ghettoSpline;
   }

   public void updateData(List<Integer> var1) {
      this.setAdditionalData(var1);
      this.updateCraftingSlots();
      this.populateRandomEffects();
   }

   protected void onOpenMine(ServerPlayer var1, ItemStack var2) {
      WorldModifiers var3 = (WorldModifiers)var2.getOrDefault(DataComponents.WORLD_MODIFIERS, WorldModifiers.EMPTY);
      if (!var3.effects().isEmpty() || this.isBossMine()) {
         List var4 = var3.effects();
         Optional var5 = Optional.ofNullable((SpecialMine)var2.get(DataComponents.SPECIAL_MINE));
         if (var5.isPresent()) {
            var4 = ((SpecialMine)var5.get()).instantiate(var1.serverLevel());
         }

         DimensionGenerator.GeneratedDimension var6 = DimensionGenerator.generateDimension(var1.theGame(), var4, var5);
         ResourceKey var7 = var6.id();
         var2.set(DataComponents.DIMENSION_ID, var7);
         var2.set(DataComponents.MINE_ACTIVE, Unit.INSTANCE);
         this.dimensionCreatedCallback.accept(var7);
         var1.closeContainer();
      }
   }

   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.MINE_CRAFTER);
   }

   public void slotsChanged(Container var1) {
      if (!this.isMineActive() && !this.isMineCompleted() && !this.isBossMine() && !this.serverLevel.isEmpty()) {
         if (this.getCraftingSlots().stream().noneMatch((var0) -> var0.hasItem() && !var0.randomSlot) && this.getDrawerSlots().stream().anyMatch((var0) -> var0.hasItem() && var0.couldBeAddedToMine())) {
            this.resultSlot.set(ItemStack.EMPTY);
         } else {
            List var2 = this.getCraftingEffects().distinct().toList();
            if (var2.isEmpty()) {
               this.resultSlot.set(ItemStack.EMPTY);
            } else {
               ItemStack var3 = new ItemStack(Items.MINE);
               var3.set(DataComponents.WORLD_MODIFIERS, new WorldModifiers(var2, false));
               var3.set(DataComponents.ITEM_NAME, this.getLevelName());
               this.resultSlot.set(var3);
               this.broadcastChanges();
            }
         }
      }
   }

   private Component getLevelName() {
      int var1 = this.getNextLevel();
      return var1 == 0 ? Component.translatable("world.mine.next") : Component.translatableEscape("world.mine", var1);
   }

   protected Stream<WorldEffect> getCraftingEffects() {
      HashMap var1 = new HashMap();
      List var2 = this.craftingSlots.stream().map(Slot::getItem).map((var0) -> ((WorldModifiers)var0.getOrDefault(DataComponents.WORLD_MODIFIERS, WorldModifiers.EMPTY)).effects()).flatMap(Collection::stream).toList();

      for(WorldEffect var4 : var2) {
         for(WorldEffectSet var6 : var4.inSets()) {
            if (var6.exclusive()) {
               var1.put(var6, var4);
            }
         }
      }

      return var2.stream().filter((var1x) -> !isSuperseded(var1x, var1));
   }

   private static boolean isSuperseded(WorldEffect var0, Map<WorldEffectSet, WorldEffect> var1) {
      for(WorldEffectSet var3 : var0.inSets()) {
         if (var3.exclusive() && var1.get(var3) != var0) {
            return true;
         }
      }

      return false;
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == this.resultSlot.index) {
            return ItemStack.EMPTY;
         }

         if (var2 > 0 && var2 <= ((MineCraftingSlot)this.craftingSlots.getLast()).index) {
            var4.set(ItemStack.EMPTY);
            return ItemStack.EMPTY;
         }

         if (var2 >= ((MineCraftingDrawerSlot)this.drawerSlots.getFirst()).index && var2 <= ((MineCraftingDrawerSlot)this.drawerSlots.getLast()).index) {
            this.moveItemStackTo(var5.copy(), this.nrOfRandomSlots, this.nrOfSlots + 1, false);
            return ItemStack.EMPTY;
         }
      }

      return var3;
   }

   protected void doClick(int var1, int var2, ClickType var3, Player var4) {
      if ((var3 == ClickType.PICKUP || var3 == ClickType.QUICK_MOVE) && (var2 == 0 || var2 == 1) && var1 == -999) {
         this.setCarried(ItemStack.EMPTY);
      }

      super.doClick(var1, var2, var3, var4);
   }

   public MineCraftingResultSlot getResultSlot() {
      return this.resultSlot;
   }

   public List<MineCraftingSlot> getCraftingSlots() {
      return this.craftingSlots;
   }

   public List<MineCraftingDrawerSlot> getDrawerSlots() {
      return this.drawerSlots;
   }

   public boolean isMineActive() {
      return this.resultSlot.hasItem() && this.resultSlot.getItem().get(DataComponents.MINE_ACTIVE) != null;
   }

   public boolean isMineCompleted() {
      return this.resultSlot.hasItem() && this.resultSlot.getItem().get(DataComponents.MINE_COMPLETED) != null;
   }

   public boolean wasMineSuccess() {
      return this.resultSlot.hasItem() && (Boolean)this.resultSlot.getItem().getOrDefault(DataComponents.MINE_COMPLETED, false);
   }

   public boolean allEffectsAreUnlocked() {
      return this.hintSlots.stream().noneMatch(Slot::hasItem);
   }

   public void removed(Player var1) {
      if (var1 instanceof ServerPlayer) {
         ItemStack var2 = this.getCarried();
         if (!var2.isEmpty()) {
            this.setCarried(ItemStack.EMPTY);
         }

      }
   }
}
