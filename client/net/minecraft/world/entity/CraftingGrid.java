package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.livingblock.LivingBlockGroup;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class CraftingGrid extends Entity {
   private static final EntityDataAccessor<Integer> DATA_SIZE;
   private static final List<EntityDataAccessor<SlotDisplay>> DATA_GHOST_ITEMS;
   private static final String SIZE_TAG = "size";
   private static final String OWNER_TAG = "owner";
   private @Nullable BlockPos ownerPos;
   private @Nullable EntityReference<Entity> owner;
   private @Nullable HoveringItem hoveringCraftingIndicator;
   private final IngredientData[][] ingredients = new IngredientData[3][3];

   public static boolean placementOk(final Level level, final AABB box) {
      return !level.noBlockCollision((Entity)null, box) ? false : BlockPos.betweenClosedStream(box.move(0.0, -1.0, 0.0).contract(0.1, 0.1, 0.1)).allMatch((pos) -> level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP));
   }

   public CraftingGrid(final EntityType<?> type, final Level level) {
      super(type, level);
      this.noPhysics = true;
   }

   private void init() {
      Direction right = this.getDirection().getClockWise();
      Direction down = this.getDirection().getOpposite();
      BlockPos topLeft = this.getTopLeft();

      for(int x = 0; x < 3; ++x) {
         for(int y = 0; y < 3; ++y) {
            BlockPos pos = topLeft.relative(right, x).relative(down, y);
            this.ingredients[x][y] = new IngredientData(this.level(), pos);
         }
      }

   }

   public static CraftingGrid create(final Level level) {
      return new CraftingGrid(EntityType.CRAFTING_GRID, level);
   }

   public static void createAt(final ServerLevel level, final int size, final Vec3 position, final Direction direction, final BlockPos spawnPos, final @Nullable Entity owner) {
      CraftingGrid grid = create(level);
      grid.setOwner(owner);
      grid.setSize(size);
      grid.setPos(position.add(0.0, 0.009999999776482582, 0.0));
      grid.setYRot(direction.toYRot());
      level.addFreshEntity(grid);
      level.gameEvent(GameEvent.ENTITY_PLACE, spawnPos, GameEvent.Context.of(owner, level.getBlockState(spawnPos.below())));
      grid.init();
   }

   public @Nullable Entity getOwner() {
      return EntityReference.getEntity(this.owner, this.level());
   }

   public void setOwner(final @Nullable Entity owner) {
      this.owner = EntityReference.of(owner);
   }

   public boolean isOwnedBy(final Entity entity) {
      return this.owner != null && this.owner.matches(entity);
   }

   public boolean isAutomatic() {
      Entity owner = this.getOwner();
      if (owner instanceof LivingBlock block) {
         return block.isBlock(Blocks.CRAFTER);
      } else {
         return false;
      }
   }

   public void tick() {
      super.tick();
      Level var2 = this.level();
      if (var2 instanceof ServerLevel level) {
         if (this.ingredients[0][0] == null) {
            this.discard();
         } else {
            Entity owner = this.getOwner();
            if (owner instanceof LivingBlock && this.getSize() == 3) {
               if (this.ownerPos == null) {
                  this.ownerPos = owner.blockPosition();
               } else if (!owner.blockPosition().equals(this.ownerPos)) {
                  this.discard();
                  return;
               }
            }

            if (owner == null || !owner.isAlive()) {
               this.discard();
            }

            Direction right = this.getDirection().getClockWise();
            Direction down = this.getDirection().getOpposite();
            BlockPos topLeft = this.getTopLeft();
            boolean hasAnyIngredient = false;
            boolean recipeMatches = true;
            int size = this.getSize();
            ContextMap context = SlotDisplayContext.fromLevel(this.level());

            for(int x = 0; x < size; ++x) {
               for(int y = 0; y < size; ++y) {
                  BlockPos pos = topLeft.relative(right, x).relative(down, y);
                  AABB box = AABB.encapsulatingFullBlocks(pos, pos);
                  List<LivingBlock> entities = this.level().getEntities((EntityTypeTest)EntityTypeTest.forClass(LivingBlock.class), box, (e) -> true);
                  LivingBlock block = this.pickIngredient(pos, entities);
                  IngredientData ingredientData = this.ingredients[x][y];
                  ingredientData.tick(block);
                  ItemStack ingredientItem = ingredientData.getItemStack();
                  if (!ingredientItem.isEmpty() && !ingredientItem.is(Items.CRAFTING_TABLE) && !ingredientItem.is(Items.CRAFTER)) {
                     hasAnyIngredient = true;
                  }

                  SlotDisplay ghostItem = this.getGhostItem(x, y);
                  if (!ghostItem.matches(context, ingredientItem)) {
                     recipeMatches = false;
                  }
               }
            }

            if (owner instanceof LivingBlock) {
               LivingBlock ownerBlock = (LivingBlock)owner;
               ownerBlock.setPinned(hasAnyIngredient);
            }

            boolean automatic = this.isAutomatic() && recipeMatches;
            ItemStack recipeResult = this.tryCraft(level, !automatic);
            if (!automatic && recipeResult != null && (this.hoveringCraftingIndicator == null || !ItemStack.matches(recipeResult, this.hoveringCraftingIndicator.getItem()))) {
               if (this.hoveringCraftingIndicator != null) {
                  this.hoveringCraftingIndicator.discard();
               }

               HoveringItem entity = new HoveringItem(level, this.getX(), this.getY() + 1.6, this.getZ(), recipeResult.copy(), (e) -> this.tryCraft(e, false));
               level.addFreshEntity(entity);
               entity.setDeltaMovement(0.0, 0.0, 0.0);
               this.hoveringCraftingIndicator = entity;
            } else if (recipeResult == null && this.hoveringCraftingIndicator != null) {
               this.hoveringCraftingIndicator.discard();
               this.hoveringCraftingIndicator = null;
            }

         }
      }
   }

   private @Nullable ItemStack tryCraft(final ServerLevel level, final boolean dryRun) {
      List<ItemStack> grid = new ArrayList();
      int size = this.getSize();

      for(int y = 0; y < size; ++y) {
         for(int x = 0; x < size; ++x) {
            grid.add(this.ingredients[x][y].getItemStack());
         }
      }

      ServerPlayer player = this.getPlayer(level);
      CraftingInput input = CraftingInput.of(size, size, grid);
      Optional<RecipeHolder<CraftingRecipe>> maybeRecipe = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level, (RecipeHolder)null);
      if (maybeRecipe.isPresent()) {
         RecipeHolder<CraftingRecipe> recipe = (RecipeHolder)maybeRecipe.get();
         CraftingRecipe craftingRecipe = recipe.value();
         if (((CraftingRecipe)recipe.value()).isSpecial() || !(Boolean)level.getGameRules().get(GameRules.LIMITED_CRAFTING) || player != null && player.getRecipeBook().contains(recipe.id())) {
            ItemStack recipeResult = craftingRecipe.assemble(input);
            if (recipeResult.isItemEnabled(level.enabledFeatures())) {
               if (!dryRun) {
                  if (player != null) {
                     player.triggerRecipeCrafted(recipe, List.of(recipeResult));
                  }

                  this.craft(level, player, recipeResult);
               }

               return recipeResult;
            }
         }
      }

      return null;
   }

   private @Nullable ServerPlayer getPlayer(final ServerLevel level) {
      Entity owner = EntityReference.getEntity(this.owner, level);
      if (owner instanceof ServerPlayer playerOwner) {
         return playerOwner;
      } else {
         if (owner instanceof LivingBlock block) {
            ServerPlayer player = block.getAttributablePlayer();
            if (player != null) {
               return player;
            }
         }

         return (ServerPlayer)level.getNearestPlayer(this, 25.0);
      }
   }

   private void craft(final ServerLevel level, final @Nullable ServerPlayer player, final ItemStack recipeResult) {
      LivingBlockGroup group = LivingBlockGroup.ALL;
      int size = this.getSize();

      for(int x = 0; x < size; ++x) {
         for(int y = 0; y < size; ++y) {
            IngredientData ingredient = this.ingredients[x][y];
            if (ingredient.block != null) {
               LivingBlockGroup ingredientGroup = ingredient.block.getGroup();
               if (group == LivingBlockGroup.ALL) {
                  group = ingredientGroup;
               } else if (group != ingredientGroup) {
                  group = LivingBlockGroup.NONE;
               }
            }

            ingredient.kill(level);
         }
      }

      if (this.hoveringCraftingIndicator != null) {
         this.hoveringCraftingIndicator.kill(level);
         this.hoveringCraftingIndicator = null;
      }

      Collection<LivingBlock> crafted = LivingBlock.createStack(level, this.blockPosition(), player, recipeResult);
      if (group != LivingBlockGroup.NONE && group != LivingBlockGroup.ALL) {
         for(LivingBlock item : crafted) {
            item.setGroup(group);
            item.setOwner(player);
         }
      }

      Vec3 p = this.position();
      level.sendParticles(ParticleTypes.GUST, p.x(), p.y(), p.z(), 20, (double)size, 1.0, (double)size, 0.30000001192092896);
      level.playSound(this, this.getOnPos(), this.isAutomatic() ? SoundEvents.CRAFTER_CRAFT : SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
      this.clearGhostItems();
   }

   private @Nullable LivingBlock pickIngredient(final BlockPos pos, final List<LivingBlock> blocks) {
      Vec3 bottomCenter = Vec3.atBottomCenterOf(pos);
      double closest = 1.7976931348623157E308;
      LivingBlock selected = null;

      for(LivingBlock block : blocks) {
         if (block.getBoundingBox().intersects(AABB.encapsulatingFullBlocks(pos, pos)) && block.blockPosition().atY(pos.getY()).equals(pos)) {
            double distance = block.position().distanceTo(bottomCenter);
            if (distance < closest) {
               closest = distance;
               selected = block;
            }
         }
      }

      return selected;
   }

   private BlockPos getTopLeft() {
      if (this.getSize() == 2) {
         BlockPos var10000;
         switch (this.getDirection()) {
            case SOUTH -> var10000 = this.blockPosition();
            case WEST -> var10000 = this.blockPosition().west();
            case NORTH -> var10000 = this.blockPosition().north().west();
            case EAST -> var10000 = this.blockPosition().north();
            default -> throw new IllegalStateException("wtf");
         }

         return var10000;
      } else {
         Direction direction = this.getDirection();
         return this.blockPosition().relative(direction).relative(direction.getCounterClockWise());
      }
   }

   public BlockPos getSlotPosition(final int x, final int y) {
      Direction right = this.getDirection().getClockWise();
      Direction down = this.getDirection().getOpposite();
      BlockPos topLeft = this.getTopLeft();
      return topLeft.relative(right, x).relative(down, y);
   }

   public void onRemoval(final Entity.RemovalReason reason) {
      super.onRemoval(reason);
      if (this.hoveringCraftingIndicator != null) {
         this.hoveringCraftingIndicator.discard();
      }

   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      entityData.define(DATA_SIZE, 3);

      for(int i = 0; i < 9; ++i) {
         entityData.define((EntityDataAccessor)DATA_GHOST_ITEMS.get(i), SlotDisplay.Empty.INSTANCE);
      }

   }

   public int getSize() {
      return (Integer)this.entityData.get(DATA_SIZE);
   }

   public void setSize(final int size) {
      this.entityData.set(DATA_SIZE, size);
   }

   public @Nullable LivingBlock getIngredientBlock(final int x, final int y) {
      return this.ingredients[x][y].block;
   }

   public @Nullable LivingBlock getIngredientBlockThatIsTrying(final int x, final int y) {
      return this.ingredients[x][y].blockThatIsTrying;
   }

   public void startTrying(final LivingBlock block, final int x, final int y) {
      this.ingredients[x][y].blockThatIsTrying = block;
   }

   public void stopTrying(final LivingBlock block, final int x, final int y) {
      IngredientData ingredient = this.ingredients[x][y];
      if (ingredient.blockThatIsTrying == block) {
         ingredient.blockThatIsTrying = null;
      }

   }

   public ItemStack getIngredient(final int x, final int y) {
      return this.ingredients[x][y].getItemStack();
   }

   public SlotDisplay getGhostItem(final int x, final int y) {
      return this.getGhostItem(y * 3 + x);
   }

   public SlotDisplay getGhostItem(final int index) {
      return (SlotDisplay)this.entityData.get((EntityDataAccessor)DATA_GHOST_ITEMS.get(index));
   }

   public void setGhostItem(final int x, final int y, final SlotDisplay item) {
      this.setGhostItem(y * 3 + x, item);
   }

   public void setGhostItem(final int index, final SlotDisplay item) {
      this.entityData.set((EntityDataAccessor)DATA_GHOST_ITEMS.get(index), item);
   }

   public void clearGhostItems() {
      for(int i = 0; i < 9; ++i) {
         this.setGhostItem(i, SlotDisplay.Empty.INSTANCE);
      }

   }

   public boolean hasGhostItems() {
      for(int i = 0; i < 9; ++i) {
         if (this.getGhostItem(i).type() != SlotDisplay.Empty.TYPE) {
            return true;
         }
      }

      return false;
   }

   public boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
      return false;
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      this.setSize(input.getIntOr("size", 3));
      this.owner = EntityReference.<Entity>read(input, "owner");
      input.read("ingredients", CraftingGrid.IngredientData.codec(this.level()).listOf()).ifPresent((ingredients) -> {
         int size = this.getSize();

         for(int y = 0; y < size; ++y) {
            for(int x = 0; x < size; ++x) {
               this.ingredients[x][y] = (IngredientData)ingredients.get(y * size + x);
            }
         }

      });
      input.read("ghosts", SlotDisplay.CODEC.listOf()).ifPresent((ghosts) -> {
         int size = this.getSize();

         for(int y = 0; y < size; ++y) {
            for(int x = 0; x < size; ++x) {
               this.setGhostItem(x, y, (SlotDisplay)ghosts.get(y * size + x));
            }
         }

      });
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      int size = this.getSize();
      output.putInt("size", size);
      EntityReference.store(this.owner, output, "owner");
      List<IngredientData> ingredients = new ArrayList();
      List<SlotDisplay> ghosts = new ArrayList();

      for(int y = 0; y < size; ++y) {
         for(int x = 0; x < size; ++x) {
            ingredients.add(this.ingredients[x][y]);
            ghosts.add(this.getGhostItem(x, y));
         }
      }

      output.store("ingredients", CraftingGrid.IngredientData.codec(this.level()).listOf(), ingredients);
      output.store("ghosts", SlotDisplay.CODEC.listOf(), ghosts);
   }

   static {
      DATA_SIZE = SynchedEntityData.<Integer>defineId(CraftingGrid.class, EntityDataSerializers.INT);
      DATA_GHOST_ITEMS = IntStream.range(0, 9).mapToObj((i) -> SynchedEntityData.defineId(CraftingGrid.class, EntityDataSerializers.SLOT_DISPLAY)).toList();
   }

   public static class HoveringItem extends Entity implements TraceableEntity {
      private static final EntityDataAccessor<ItemStack> DATA_ITEM;
      private @Nullable Consumer<ServerLevel> crafter;

      public HoveringItem(final Level level, final double x, final double y, final double z, final ItemStack itemStack, final Consumer<ServerLevel> crafter) {
         this(EntityType.HOVERING_ITEM, level);
         this.crafter = crafter;
         this.setPos(x, y, z);
         this.setDeltaMovement(level.getRandom().nextDouble() * 0.2 - 0.1, 0.2, level.getRandom().nextDouble() * 0.2 - 0.1);
         this.setItem(itemStack);
      }

      public HoveringItem(final EntityType<HoveringItem> type, final Level level) {
         super(type, level);
         this.crafter = null;
      }

      public @Nullable Entity getOwner() {
         return null;
      }

      protected Entity.MovementEmission getMovementEmission() {
         return Entity.MovementEmission.NONE;
      }

      protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
         entityData.define(DATA_ITEM, ItemStack.EMPTY);
      }

      public void tick() {
         if (this.getItem().isEmpty()) {
            this.discard();
         }

      }

      public final boolean hurtServer(final ServerLevel level, final DamageSource source, final float damage) {
         this.crafter.accept(level);
         return false;
      }

      public boolean ignoreExplosion(final Explosion explosion) {
         return true;
      }

      protected void addAdditionalSaveData(final ValueOutput output) {
      }

      protected void readAdditionalSaveData(final ValueInput input) {
      }

      public ItemStack getItem() {
         return (ItemStack)this.getEntityData().get(DATA_ITEM);
      }

      public void setItem(final ItemStack itemStack) {
         this.getEntityData().set(DATA_ITEM, itemStack);
      }

      public boolean isPickable() {
         return true;
      }

      public InteractionResult interact(final Player player, final InteractionHand hand, final Vec3 location) {
         Level var5 = this.level();
         if (var5 instanceof ServerLevel level) {
            this.crafter.accept(level);
         }

         return InteractionResult.SUCCESS;
      }

      protected AABB makeBoundingBox(final Vec3 position) {
         return EntityDimensions.scalable(0.7F, 0.8F).makeBoundingBox(position);
      }

      static {
         DATA_ITEM = SynchedEntityData.<ItemStack>defineId(HoveringItem.class, EntityDataSerializers.ITEM_STACK);
      }
   }

   private static class IngredientData {
      private final Level level;
      private final BlockPos pos;
      private BlockState preExistingBlockState;
      private @Nullable LivingBlock block;
      private @Nullable LivingBlock blockThatIsTrying;

      public static final Codec<IngredientData> codec(final Level level) {
         return RecordCodecBuilder.create((i) -> i.group(BlockPos.CODEC.fieldOf("pos").forGetter((ig) -> ig.pos), BlockState.CODEC.fieldOf("state").forGetter((ig) -> ig.preExistingBlockState)).apply(i, (pos, state) -> new IngredientData(level, pos, state)));
      }

      public IngredientData(final Level level, final BlockPos pos, final BlockState preExistingBlockState) {
         super();
         this.level = level;
         this.pos = pos;
         this.preExistingBlockState = preExistingBlockState;
      }

      public IngredientData(final Level level, final BlockPos pos) {
         this(level, pos, level.getBlockState(pos));
      }

      public void tick(final @Nullable LivingBlock currentBlock) {
         this.block = currentBlock;
      }

      public ItemStack getItemStack() {
         if (this.block != null && this.block.isAlive()) {
            return this.block.getItemStack();
         } else {
            BlockState blockState = this.level.getBlockState(this.pos);
            return blockState != this.preExistingBlockState ? blockState.getCloneItemStack(this.level, this.pos, true) : ItemStack.EMPTY;
         }
      }

      public void kill(final ServerLevel level) {
         if (this.block != null) {
            this.block.discard();
            this.block = null;
            this.blockThatIsTrying = null;
         } else {
            BlockState blockState = level.getBlockState(this.pos);
            if (blockState != this.preExistingBlockState) {
               level.setBlock(this.pos, Blocks.AIR.defaultBlockState(), 3);
               this.preExistingBlockState = Blocks.AIR.defaultBlockState();
            }
         }

      }
   }
}
