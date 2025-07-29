package net.minecraft.gametest.framework;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class GameTestHelper {
   private final GameTestInfo testInfo;
   private boolean finalCheckAdded;

   public GameTestHelper(GameTestInfo var1) {
      super();
      this.testInfo = var1;
   }

   public GameTestAssertException assertionException(Component var1) {
      return new GameTestAssertException(var1, this.testInfo.getTick());
   }

   public GameTestAssertException assertionException(String var1, Object... var2) {
      return this.assertionException(Component.translatableEscape(var1, var2));
   }

   public GameTestAssertPosException assertionException(BlockPos var1, Component var2) {
      return new GameTestAssertPosException(var2, this.absolutePos(var1), var1, this.testInfo.getTick());
   }

   public GameTestAssertPosException assertionException(BlockPos var1, String var2, Object... var3) {
      return this.assertionException((BlockPos)var1, (Component)Component.translatableEscape(var2, var3));
   }

   public ServerLevel getLevel() {
      return this.testInfo.getLevel();
   }

   public BlockState getBlockState(BlockPos var1) {
      return this.getLevel().getBlockState(this.absolutePos(var1));
   }

   public <T extends BlockEntity> T getBlockEntity(BlockPos var1, Class<T> var2) {
      BlockEntity var3 = this.getLevel().getBlockEntity(this.absolutePos(var1));
      if (var3 == null) {
         throw this.assertionException(var1, "test.error.missing_block_entity");
      } else if (var2.isInstance(var3)) {
         return (T)(var2.cast(var3));
      } else {
         throw this.assertionException(var1, "test.error.wrong_block_entity", var3.getType().builtInRegistryHolder().getRegisteredName());
      }
   }

   public void killAllEntities() {
      this.killAllEntitiesOfClass(Entity.class);
   }

   public void killAllEntitiesOfClass(Class<? extends Entity> var1) {
      AABB var2 = this.getBounds();
      List var3 = this.getLevel().getEntitiesOfClass(var1, var2.inflate(1.0), (var0) -> !(var0 instanceof Player));
      var3.forEach((var1x) -> var1x.kill(this.getLevel()));
   }

   public ItemEntity spawnItem(Item var1, Vec3 var2) {
      ServerLevel var3 = this.getLevel();
      Vec3 var4 = this.absoluteVec(var2);
      ItemEntity var5 = new ItemEntity(var3, var4.x, var4.y, var4.z, new ItemStack(var1, 1));
      var5.setDeltaMovement(0.0, 0.0, 0.0);
      var3.addFreshEntity(var5);
      return var5;
   }

   public ItemEntity spawnItem(Item var1, float var2, float var3, float var4) {
      return this.spawnItem(var1, new Vec3((double)var2, (double)var3, (double)var4));
   }

   public ItemEntity spawnItem(Item var1, BlockPos var2) {
      return this.spawnItem(var1, (float)var2.getX(), (float)var2.getY(), (float)var2.getZ());
   }

   public <E extends Entity> E spawn(EntityType<E> var1, BlockPos var2) {
      return (E)this.spawn(var1, Vec3.atBottomCenterOf(var2));
   }

   public <E extends Entity> List<E> spawn(EntityType<E> var1, BlockPos var2, int var3) {
      return this.spawn(var1, Vec3.atBottomCenterOf(var2), var3);
   }

   public <E extends Entity> List<E> spawn(EntityType<E> var1, Vec3 var2, int var3) {
      ArrayList var4 = new ArrayList();

      for(int var5 = 0; var5 < var3; ++var5) {
         var4.add(this.spawn(var1, var2));
      }

      return var4;
   }

   public <E extends Entity> E spawn(EntityType<E> var1, Vec3 var2) {
      ServerLevel var3 = this.getLevel();
      Entity var4 = var1.create(var3, EntitySpawnReason.STRUCTURE);
      if (var4 == null) {
         throw this.assertionException(BlockPos.containing(var2), "test.error.spawn_failure", var1.builtInRegistryHolder().getRegisteredName());
      } else {
         if (var4 instanceof Mob) {
            Mob var5 = (Mob)var4;
            var5.setPersistenceRequired();
         }

         Vec3 var7 = this.absoluteVec(var2);
         float var6 = var4.rotate(this.getTestRotation());
         var4.snapTo(var7.x, var7.y, var7.z, var6, var4.getXRot());
         var4.setYBodyRot(var6);
         var4.setYHeadRot(var6);
         var3.addFreshEntity(var4);
         return (E)var4;
      }
   }

   public void hurt(Entity var1, DamageSource var2, float var3) {
      var1.hurtServer(this.getLevel(), var2, var3);
   }

   public void kill(Entity var1) {
      var1.kill(this.getLevel());
   }

   public <E extends Entity> E findOneEntity(EntityType<E> var1) {
      return (E)this.findClosestEntity(var1, 0, 0, 0, 2.147483647E9);
   }

   public <E extends Entity> E findClosestEntity(EntityType<E> var1, int var2, int var3, int var4, double var5) {
      List var7 = this.findEntities(var1, var2, var3, var4, var5);
      if (var7.isEmpty()) {
         throw this.assertionException("test.error.expected_entity_around", var1.getDescription(), var2, var3, var4);
      } else if (var7.size() > 1) {
         throw this.assertionException("test.error.too_many_entities", var1.toShortString(), var2, var3, var4, var7.size());
      } else {
         Vec3 var8 = this.absoluteVec(new Vec3((double)var2, (double)var3, (double)var4));
         var7.sort((var1x, var2x) -> {
            double var3 = var1x.position().distanceTo(var8);
            double var5 = var2x.position().distanceTo(var8);
            return Double.compare(var3, var5);
         });
         return (E)(var7.get(0));
      }
   }

   public <E extends Entity> List<E> findEntities(EntityType<E> var1, int var2, int var3, int var4, double var5) {
      return this.<E>findEntities(var1, Vec3.atBottomCenterOf(new BlockPos(var2, var3, var4)), var5);
   }

   public <E extends Entity> List<E> findEntities(EntityType<E> var1, Vec3 var2, double var3) {
      ServerLevel var5 = this.getLevel();
      Vec3 var6 = this.absoluteVec(var2);
      AABB var7 = this.testInfo.getStructureBounds();
      AABB var8 = new AABB(var6.add(-var3, -var3, -var3), var6.add(var3, var3, var3));
      return var5.getEntities(var1, var7, (var1x) -> var1x.getBoundingBox().intersects(var8) && var1x.isAlive());
   }

   public <E extends Entity> E spawn(EntityType<E> var1, int var2, int var3, int var4) {
      return (E)this.spawn(var1, new BlockPos(var2, var3, var4));
   }

   public <E extends Entity> E spawn(EntityType<E> var1, float var2, float var3, float var4) {
      return (E)this.spawn(var1, new Vec3((double)var2, (double)var3, (double)var4));
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> var1, BlockPos var2) {
      Mob var3 = (Mob)this.spawn(var1, var2);
      var3.removeFreeWill();
      return (E)var3;
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> var1, int var2, int var3, int var4) {
      return (E)this.spawnWithNoFreeWill(var1, new BlockPos(var2, var3, var4));
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> var1, Vec3 var2) {
      Mob var3 = (Mob)this.spawn(var1, var2);
      var3.removeFreeWill();
      return (E)var3;
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> var1, float var2, float var3, float var4) {
      return (E)this.spawnWithNoFreeWill(var1, new Vec3((double)var2, (double)var3, (double)var4));
   }

   public void moveTo(Mob var1, float var2, float var3, float var4) {
      Vec3 var5 = this.absoluteVec(new Vec3((double)var2, (double)var3, (double)var4));
      var1.snapTo(var5.x, var5.y, var5.z, var1.getYRot(), var1.getXRot());
   }

   public GameTestSequence walkTo(Mob var1, BlockPos var2, float var3) {
      return this.startSequence().thenExecuteAfter(2, () -> {
         Path var4 = var1.getNavigation().createPath(this.absolutePos(var2), 0);
         var1.getNavigation().moveTo(var4, (double)var3);
      });
   }

   public void pressButton(int var1, int var2, int var3) {
      this.pressButton(new BlockPos(var1, var2, var3));
   }

   public void pressButton(BlockPos var1) {
      this.assertBlockTag(BlockTags.BUTTONS, var1);
      BlockPos var2 = this.absolutePos(var1);
      BlockState var3 = this.getLevel().getBlockState(var2);
      ButtonBlock var4 = (ButtonBlock)var3.getBlock();
      var4.press(var3, this.getLevel(), var2, (Player)null);
   }

   public void useBlock(BlockPos var1) {
      this.useBlock(var1, this.makeMockPlayer(GameType.CREATIVE));
   }

   public void useBlock(BlockPos var1, Player var2) {
      BlockPos var3 = this.absolutePos(var1);
      this.useBlock(var1, var2, new BlockHitResult(Vec3.atCenterOf(var3), Direction.NORTH, var3, true));
   }

   public void useBlock(BlockPos var1, Player var2, BlockHitResult var3) {
      BlockPos var4 = this.absolutePos(var1);
      BlockState var5 = this.getLevel().getBlockState(var4);
      InteractionHand var6 = InteractionHand.MAIN_HAND;
      InteractionResult var7 = var5.useItemOn(var2.getItemInHand(var6), this.getLevel(), var2, var6, var3);
      if (!var7.consumesAction()) {
         if (!(var7 instanceof InteractionResult.TryEmptyHandInteraction) || !var5.useWithoutItem(this.getLevel(), var2, var3).consumesAction()) {
            UseOnContext var8 = new UseOnContext(var2, var6, var3);
            var2.getItemInHand(var6).useOn(var8);
         }
      }
   }

   public LivingEntity makeAboutToDrown(LivingEntity var1) {
      var1.setAirSupply(0);
      var1.setHealth(0.25F);
      return var1;
   }

   public LivingEntity withLowHealth(LivingEntity var1) {
      var1.setHealth(0.25F);
      return var1;
   }

   public Player makeMockPlayer(final GameType var1) {
      return new Player(this.getLevel(), new GameProfile(UUID.randomUUID(), "test-mock-player")) {
         @Nonnull
         public GameType gameMode() {
            return var1;
         }

         public boolean isClientAuthoritative() {
            return false;
         }
      };
   }

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public ServerPlayer makeMockServerPlayerInLevel() {
      CommonListenerCookie var1 = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "test-mock-player"), false);
      ServerPlayer var2 = new ServerPlayer(this.getLevel().getServer(), this.getLevel(), var1.gameProfile(), var1.clientInformation()) {
         public GameType gameMode() {
            return GameType.CREATIVE;
         }
      };
      Connection var3 = new Connection(PacketFlow.SERVERBOUND);
      new EmbeddedChannel(new ChannelHandler[]{var3});
      this.getLevel().getServer().getPlayerList().placeNewPlayer(var3, var2, var1);
      return var2;
   }

   public void pullLever(int var1, int var2, int var3) {
      this.pullLever(new BlockPos(var1, var2, var3));
   }

   public void pullLever(BlockPos var1) {
      this.assertBlockPresent(Blocks.LEVER, var1);
      BlockPos var2 = this.absolutePos(var1);
      BlockState var3 = this.getLevel().getBlockState(var2);
      LeverBlock var4 = (LeverBlock)var3.getBlock();
      var4.pull(var3, this.getLevel(), var2, (Player)null);
   }

   public void pulseRedstone(BlockPos var1, long var2) {
      this.setBlock(var1, Blocks.REDSTONE_BLOCK);
      this.runAfterDelay(var2, () -> this.setBlock(var1, Blocks.AIR));
   }

   public void destroyBlock(BlockPos var1) {
      this.getLevel().destroyBlock(this.absolutePos(var1), false, (Entity)null);
   }

   public void setBlock(int var1, int var2, int var3, Block var4) {
      this.setBlock(new BlockPos(var1, var2, var3), var4);
   }

   public void setBlock(int var1, int var2, int var3, BlockState var4) {
      this.setBlock(new BlockPos(var1, var2, var3), var4);
   }

   public void setBlock(BlockPos var1, Block var2) {
      this.setBlock(var1, var2.defaultBlockState());
   }

   public void setBlock(BlockPos var1, BlockState var2) {
      this.getLevel().setBlock(this.absolutePos(var1), var2, 3);
   }

   public void setBlock(BlockPos var1, Block var2, Direction var3) {
      this.setBlock(var1, var2.defaultBlockState(), var3);
   }

   public void setBlock(BlockPos var1, BlockState var2, Direction var3) {
      BlockState var4 = var2;
      if (var2.hasProperty(HorizontalDirectionalBlock.FACING)) {
         var4 = (BlockState)var2.setValue(HorizontalDirectionalBlock.FACING, var3);
      }

      if (var2.hasProperty(BlockStateProperties.FACING)) {
         var4 = (BlockState)var2.setValue(BlockStateProperties.FACING, var3);
      }

      this.getLevel().setBlock(this.absolutePos(var1), var4, 3);
   }

   public void setNight() {
      this.setDayTime(13000);
   }

   public void setDayTime(int var1) {
      this.getLevel().setDayTime((long)var1);
   }

   public void assertBlockPresent(Block var1, int var2, int var3, int var4) {
      this.assertBlockPresent(var1, new BlockPos(var2, var3, var4));
   }

   public void assertBlockPresent(Block var1, BlockPos var2) {
      BlockState var3 = this.getBlockState(var2);
      this.assertBlock(var2, (var2x) -> var3.is(var1), (var1x) -> Component.translatable("test.error.expected_block", var1.getName(), var1x.getName()));
   }

   public void assertBlockNotPresent(Block var1, int var2, int var3, int var4) {
      this.assertBlockNotPresent(var1, new BlockPos(var2, var3, var4));
   }

   public void assertBlockNotPresent(Block var1, BlockPos var2) {
      this.assertBlock(var2, (var3) -> !this.getBlockState(var2).is(var1), (var1x) -> Component.translatable("test.error.unexpected_block", var1.getName()));
   }

   public void assertBlockTag(TagKey<Block> var1, BlockPos var2) {
      this.assertBlockState(var2, (var1x) -> var1x.is(var1), (var1x) -> Component.translatable("test.error.expected_block_tag", Component.translationArg(var1.location()), var1x.getBlock().getName()));
   }

   public void succeedWhenBlockPresent(Block var1, int var2, int var3, int var4) {
      this.succeedWhenBlockPresent(var1, new BlockPos(var2, var3, var4));
   }

   public void succeedWhenBlockPresent(Block var1, BlockPos var2) {
      this.succeedWhen(() -> this.assertBlockPresent(var1, var2));
   }

   public void assertBlock(BlockPos var1, Predicate<Block> var2, Function<Block, Component> var3) {
      this.assertBlockState(var1, (var1x) -> var2.test(var1x.getBlock()), (var1x) -> (Component)var3.apply(var1x.getBlock()));
   }

   public <T extends Comparable<T>> void assertBlockProperty(BlockPos var1, Property<T> var2, T var3) {
      BlockState var4 = this.getBlockState(var1);
      boolean var5 = var4.hasProperty(var2);
      if (!var5) {
         throw this.assertionException(var1, "test.error.block_property_missing", var2.getName(), var3);
      } else if (!var4.getValue(var2).equals(var3)) {
         throw this.assertionException(var1, "test.error.block_property_mismatch", var2.getName(), var3, var4.getValue(var2));
      }
   }

   public <T extends Comparable<T>> void assertBlockProperty(BlockPos var1, Property<T> var2, Predicate<T> var3, Component var4) {
      this.assertBlockState(var1, (var2x) -> {
         if (!var2x.hasProperty(var2)) {
            return false;
         } else {
            Comparable var3x = var2x.getValue(var2);
            return var3.test(var3x);
         }
      }, (var1x) -> var4);
   }

   public void assertBlockState(BlockPos var1, BlockState var2) {
      BlockState var3 = this.getBlockState(var1);
      if (!var3.equals(var2)) {
         throw this.assertionException(var1, "test.error.state_not_equal", var2, var3);
      }
   }

   public void assertBlockState(BlockPos var1, Predicate<BlockState> var2, Function<BlockState, Component> var3) {
      BlockState var4 = this.getBlockState(var1);
      if (!var2.test(var4)) {
         throw this.assertionException(var1, (Component)var3.apply(var4));
      }
   }

   public <T extends BlockEntity> void assertBlockEntityData(BlockPos var1, Class<T> var2, Predicate<T> var3, Supplier<Component> var4) {
      BlockEntity var5 = this.getBlockEntity(var1, var2);
      if (!var3.test(var5)) {
         throw this.assertionException(var1, (Component)var4.get());
      }
   }

   public void assertRedstoneSignal(BlockPos var1, Direction var2, IntPredicate var3, Supplier<Component> var4) {
      BlockPos var5 = this.absolutePos(var1);
      ServerLevel var6 = this.getLevel();
      BlockState var7 = var6.getBlockState(var5);
      int var8 = var7.getSignal(var6, var5, var2);
      if (!var3.test(var8)) {
         throw this.assertionException(var1, (Component)var4.get());
      }
   }

   public void assertEntityPresent(EntityType<?> var1) {
      List var2 = this.getLevel().getEntities(var1, this.getBounds(), Entity::isAlive);
      if (var2.isEmpty()) {
         throw this.assertionException("test.error.expected_entity_in_test", var1.getDescription());
      }
   }

   public void assertEntityPresent(EntityType<?> var1, int var2, int var3, int var4) {
      this.assertEntityPresent(var1, new BlockPos(var2, var3, var4));
   }

   public void assertEntityPresent(EntityType<?> var1, BlockPos var2) {
      BlockPos var3 = this.absolutePos(var2);
      List var4 = this.getLevel().getEntities(var1, new AABB(var3), Entity::isAlive);
      if (var4.isEmpty()) {
         throw this.assertionException(var2, "test.error.expected_entity", var1.getDescription());
      }
   }

   public void assertEntityPresent(EntityType<?> var1, AABB var2) {
      AABB var3 = this.absoluteAABB(var2);
      List var4 = this.getLevel().getEntities(var1, var3, Entity::isAlive);
      if (var4.isEmpty()) {
         throw this.assertionException(BlockPos.containing(var2.getCenter()), "test.error.expected_entity", var1.getDescription());
      }
   }

   public void assertEntitiesPresent(EntityType<?> var1, int var2) {
      List var3 = this.getLevel().getEntities(var1, this.getBounds(), Entity::isAlive);
      if (var3.size() != var2) {
         throw this.assertionException("test.error.expected_entity_count", var2, var1.getDescription(), var3.size());
      }
   }

   public void assertEntitiesPresent(EntityType<?> var1, BlockPos var2, int var3, double var4) {
      this.absolutePos(var2);
      List var7 = this.getEntities(var1, var2, var4);
      if (var7.size() != var3) {
         throw this.assertionException(var2, "test.error.expected_entity_count", var3, var1.getDescription(), var7.size());
      }
   }

   public void assertEntityPresent(EntityType<?> var1, BlockPos var2, double var3) {
      List var5 = this.getEntities(var1, var2, var3);
      if (var5.isEmpty()) {
         this.absolutePos(var2);
         throw this.assertionException(var2, "test.error.expected_entity", var1.getDescription());
      }
   }

   public <T extends Entity> List<T> getEntities(EntityType<T> var1, BlockPos var2, double var3) {
      BlockPos var5 = this.absolutePos(var2);
      return this.getLevel().getEntities(var1, (new AABB(var5)).inflate(var3), Entity::isAlive);
   }

   public <T extends Entity> List<T> getEntities(EntityType<T> var1) {
      return this.getLevel().getEntities(var1, this.getBounds(), Entity::isAlive);
   }

   public void assertEntityInstancePresent(Entity var1, int var2, int var3, int var4) {
      this.assertEntityInstancePresent(var1, new BlockPos(var2, var3, var4));
   }

   public void assertEntityInstancePresent(Entity var1, BlockPos var2) {
      BlockPos var3 = this.absolutePos(var2);
      List var4 = this.getLevel().getEntities(var1.getType(), new AABB(var3), Entity::isAlive);
      var4.stream().filter((var1x) -> var1x == var1).findFirst().orElseThrow(() -> this.assertionException(var2, "test.error.expected_entity", var1.getType().getDescription()));
   }

   public void assertItemEntityCountIs(Item var1, BlockPos var2, double var3, int var5) {
      BlockPos var6 = this.absolutePos(var2);
      List var7 = this.getLevel().getEntities(EntityType.ITEM, (new AABB(var6)).inflate(var3), Entity::isAlive);
      int var8 = 0;

      for(ItemEntity var10 : var7) {
         ItemStack var11 = var10.getItem();
         if (var11.is(var1)) {
            var8 += var11.getCount();
         }
      }

      if (var8 != var5) {
         throw this.assertionException(var2, "test.error.expected_items_count", var5, var1.getName(), var8);
      }
   }

   public void assertItemEntityPresent(Item var1, BlockPos var2, double var3) {
      BlockPos var5 = this.absolutePos(var2);

      for(Entity var8 : this.getLevel().getEntities(EntityType.ITEM, (new AABB(var5)).inflate(var3), Entity::isAlive)) {
         ItemEntity var9 = (ItemEntity)var8;
         if (var9.getItem().getItem().equals(var1)) {
            return;
         }
      }

      throw this.assertionException(var2, "test.error.expected_item", var1.getName());
   }

   public void assertItemEntityNotPresent(Item var1, BlockPos var2, double var3) {
      BlockPos var5 = this.absolutePos(var2);

      for(Entity var8 : this.getLevel().getEntities(EntityType.ITEM, (new AABB(var5)).inflate(var3), Entity::isAlive)) {
         ItemEntity var9 = (ItemEntity)var8;
         if (var9.getItem().getItem().equals(var1)) {
            throw this.assertionException(var2, "test.error.unexpected_item", var1.getName());
         }
      }

   }

   public void assertItemEntityPresent(Item var1) {
      for(Entity var4 : this.getLevel().getEntities(EntityType.ITEM, this.getBounds(), Entity::isAlive)) {
         ItemEntity var5 = (ItemEntity)var4;
         if (var5.getItem().getItem().equals(var1)) {
            return;
         }
      }

      throw this.assertionException("test.error.expected_item", var1.getName());
   }

   public void assertItemEntityNotPresent(Item var1) {
      for(Entity var4 : this.getLevel().getEntities(EntityType.ITEM, this.getBounds(), Entity::isAlive)) {
         ItemEntity var5 = (ItemEntity)var4;
         if (var5.getItem().getItem().equals(var1)) {
            throw this.assertionException("test.error.unexpected_item", var1.getName());
         }
      }

   }

   public void assertEntityNotPresent(EntityType<?> var1) {
      List var2 = this.getLevel().getEntities(var1, this.getBounds(), Entity::isAlive);
      if (!var2.isEmpty()) {
         throw this.assertionException(((Entity)var2.getFirst()).blockPosition(), "test.error.unexpected_entity", var1.getDescription());
      }
   }

   public void assertEntityNotPresent(EntityType<?> var1, int var2, int var3, int var4) {
      this.assertEntityNotPresent(var1, new BlockPos(var2, var3, var4));
   }

   public void assertEntityNotPresent(EntityType<?> var1, BlockPos var2) {
      BlockPos var3 = this.absolutePos(var2);
      List var4 = this.getLevel().getEntities(var1, new AABB(var3), Entity::isAlive);
      if (!var4.isEmpty()) {
         throw this.assertionException(var2, "test.error.unexpected_entity", var1.getDescription());
      }
   }

   public void assertEntityNotPresent(EntityType<?> var1, AABB var2) {
      AABB var3 = this.absoluteAABB(var2);
      List var4 = this.getLevel().getEntities(var1, var3, Entity::isAlive);
      if (!var4.isEmpty()) {
         throw this.assertionException(((Entity)var4.getFirst()).blockPosition(), "test.error.unexpected_entity", var1.getDescription());
      }
   }

   public void assertEntityTouching(EntityType<?> var1, double var2, double var4, double var6) {
      Vec3 var8 = new Vec3(var2, var4, var6);
      Vec3 var9 = this.absoluteVec(var8);
      Predicate var10 = (var1x) -> var1x.getBoundingBox().intersects(var9, var9);
      List var11 = this.getLevel().getEntities(var1, this.getBounds(), var10);
      if (var11.isEmpty()) {
         throw this.assertionException("test.error.expected_entity_touching", var1.getDescription(), var9.x(), var9.y(), var9.z(), var2, var4, var6);
      }
   }

   public void assertEntityNotTouching(EntityType<?> var1, double var2, double var4, double var6) {
      Vec3 var8 = new Vec3(var2, var4, var6);
      Vec3 var9 = this.absoluteVec(var8);
      Predicate var10 = (var1x) -> !var1x.getBoundingBox().intersects(var9, var9);
      List var11 = this.getLevel().getEntities(var1, this.getBounds(), var10);
      if (var11.isEmpty()) {
         throw this.assertionException("test.error.expected_entity_not_touching", var1.getDescription(), var9.x(), var9.y(), var9.z(), var2, var4, var6);
      }
   }

   public <E extends Entity, T> void assertEntityData(BlockPos var1, EntityType<E> var2, Predicate<E> var3) {
      BlockPos var4 = this.absolutePos(var1);
      List var5 = this.getLevel().getEntities(var2, new AABB(var4), Entity::isAlive);
      if (var5.isEmpty()) {
         throw this.assertionException(var1, "test.error.expected_entity", var2.getDescription());
      } else {
         for(Entity var7 : var5) {
            if (!var3.test(var7)) {
               throw this.assertionException(var7.blockPosition(), "test.error.expected_entity_data_predicate", var7.getName());
            }
         }

      }
   }

   public <E extends Entity, T> void assertEntityData(BlockPos var1, EntityType<E> var2, Function<? super E, T> var3, @Nullable T var4) {
      BlockPos var5 = this.absolutePos(var1);
      List var6 = this.getLevel().getEntities(var2, new AABB(var5), Entity::isAlive);
      if (var6.isEmpty()) {
         throw this.assertionException(var1, "test.error.expected_entity", var2.getDescription());
      } else {
         for(Entity var8 : var6) {
            Object var9 = var3.apply(var8);
            if (!Objects.equals(var9, var4)) {
               throw this.assertionException(var1, "test.error.expected_entity_data", var4, var9);
            }
         }

      }
   }

   public <E extends LivingEntity> void assertEntityIsHolding(BlockPos var1, EntityType<E> var2, Item var3) {
      BlockPos var4 = this.absolutePos(var1);
      List var5 = this.getLevel().getEntities(var2, new AABB(var4), Entity::isAlive);
      if (var5.isEmpty()) {
         throw this.assertionException(var1, "test.error.expected_entity", var2.getDescription());
      } else {
         for(LivingEntity var7 : var5) {
            if (var7.isHolding(var3)) {
               return;
            }
         }

         throw this.assertionException(var1, "test.error.expected_entity_holding", var3.getName());
      }
   }

   public <E extends Entity & InventoryCarrier> void assertEntityInventoryContains(BlockPos var1, EntityType<E> var2, Item var3) {
      BlockPos var4 = this.absolutePos(var1);
      List var5 = this.getLevel().getEntities(var2, new AABB(var4), (var0) -> ((Entity)var0).isAlive());
      if (var5.isEmpty()) {
         throw this.assertionException(var1, "test.error.expected_entity", var2.getDescription());
      } else {
         for(Entity var7 : var5) {
            if (((InventoryCarrier)var7).getInventory().hasAnyMatching((var1x) -> var1x.is(var3))) {
               return;
            }
         }

         throw this.assertionException(var1, "test.error.expected_entity_having", var3.getName());
      }
   }

   public void assertContainerEmpty(BlockPos var1) {
      BaseContainerBlockEntity var2 = (BaseContainerBlockEntity)this.getBlockEntity(var1, BaseContainerBlockEntity.class);
      if (!var2.isEmpty()) {
         throw this.assertionException(var1, "test.error.expected_empty_container");
      }
   }

   public void assertContainerContainsSingle(BlockPos var1, Item var2) {
      BaseContainerBlockEntity var3 = (BaseContainerBlockEntity)this.getBlockEntity(var1, BaseContainerBlockEntity.class);
      if (var3.countItem(var2) != 1) {
         throw this.assertionException(var1, "test.error.expected_container_contents_single", var2.getName());
      }
   }

   public void assertContainerContains(BlockPos var1, Item var2) {
      BaseContainerBlockEntity var3 = (BaseContainerBlockEntity)this.getBlockEntity(var1, BaseContainerBlockEntity.class);
      if (var3.countItem(var2) == 0) {
         throw this.assertionException(var1, "test.error.expected_container_contents", var2.getName());
      }
   }

   public void assertSameBlockStates(BoundingBox var1, BlockPos var2) {
      BlockPos.betweenClosedStream(var1).forEach((var3) -> {
         BlockPos var4 = var2.offset(var3.getX() - var1.minX(), var3.getY() - var1.minY(), var3.getZ() - var1.minZ());
         this.assertSameBlockState(var3, var4);
      });
   }

   public void assertSameBlockState(BlockPos var1, BlockPos var2) {
      BlockState var3 = this.getBlockState(var1);
      BlockState var4 = this.getBlockState(var2);
      if (var3 != var4) {
         throw this.assertionException(var1, "test.error.state_not_equal", var4, var3);
      }
   }

   public void assertAtTickTimeContainerContains(long var1, BlockPos var3, Item var4) {
      this.runAtTickTime(var1, () -> this.assertContainerContainsSingle(var3, var4));
   }

   public void assertAtTickTimeContainerEmpty(long var1, BlockPos var3) {
      this.runAtTickTime(var1, () -> this.assertContainerEmpty(var3));
   }

   public <E extends Entity, T> void succeedWhenEntityData(BlockPos var1, EntityType<E> var2, Function<E, T> var3, T var4) {
      this.succeedWhen(() -> this.assertEntityData(var1, var2, var3, var4));
   }

   public void assertEntityPosition(Entity var1, AABB var2, Component var3) {
      if (!var2.contains(this.relativeVec(var1.position()))) {
         throw this.assertionException(var3);
      }
   }

   public <E extends Entity> void assertEntityProperty(E var1, Predicate<E> var2, Component var3) {
      if (!var2.test(var1)) {
         throw this.assertionException(var1.blockPosition(), "test.error.entity_property", var1.getName(), var3);
      }
   }

   public <E extends Entity, T> void assertEntityProperty(E var1, Function<E, T> var2, T var3, Component var4) {
      Object var5 = var2.apply(var1);
      if (!var5.equals(var3)) {
         throw this.assertionException(var1.blockPosition(), "test.error.entity_property_details", var1.getName(), var4, var5, var3);
      }
   }

   public void assertLivingEntityHasMobEffect(LivingEntity var1, Holder<MobEffect> var2, int var3) {
      MobEffectInstance var4 = var1.getEffect(var2);
      if (var4 == null || var4.getAmplifier() != var3) {
         throw this.assertionException("test.error.expected_entity_effect", var1.getName(), PotionContents.getPotionDescription(var2, var3));
      }
   }

   public void succeedWhenEntityPresent(EntityType<?> var1, int var2, int var3, int var4) {
      this.succeedWhenEntityPresent(var1, new BlockPos(var2, var3, var4));
   }

   public void succeedWhenEntityPresent(EntityType<?> var1, BlockPos var2) {
      this.succeedWhen(() -> this.assertEntityPresent(var1, var2));
   }

   public void succeedWhenEntityNotPresent(EntityType<?> var1, int var2, int var3, int var4) {
      this.succeedWhenEntityNotPresent(var1, new BlockPos(var2, var3, var4));
   }

   public void succeedWhenEntityNotPresent(EntityType<?> var1, BlockPos var2) {
      this.succeedWhen(() -> this.assertEntityNotPresent(var1, var2));
   }

   public void succeed() {
      this.testInfo.succeed();
   }

   private void ensureSingleFinalCheck() {
      if (this.finalCheckAdded) {
         throw new IllegalStateException("This test already has final clause");
      } else {
         this.finalCheckAdded = true;
      }
   }

   public void succeedIf(Runnable var1) {
      this.ensureSingleFinalCheck();
      this.testInfo.createSequence().thenWaitUntil(0L, var1).thenSucceed();
   }

   public void succeedWhen(Runnable var1) {
      this.ensureSingleFinalCheck();
      this.testInfo.createSequence().thenWaitUntil(var1).thenSucceed();
   }

   public void succeedOnTickWhen(int var1, Runnable var2) {
      this.ensureSingleFinalCheck();
      this.testInfo.createSequence().thenWaitUntil((long)var1, var2).thenSucceed();
   }

   public void runAtTickTime(long var1, Runnable var3) {
      this.testInfo.setRunAtTickTime(var1, var3);
   }

   public void runAfterDelay(long var1, Runnable var3) {
      this.runAtTickTime((long)this.testInfo.getTick() + var1, var3);
   }

   public void randomTick(BlockPos var1) {
      BlockPos var2 = this.absolutePos(var1);
      ServerLevel var3 = this.getLevel();
      var3.getBlockState(var2).randomTick(var3, var2, var3.random);
   }

   public void tickBlock(BlockPos var1) {
      BlockPos var2 = this.absolutePos(var1);
      ServerLevel var3 = this.getLevel();
      var3.getBlockState(var2).tick(var3, var2, var3.random);
   }

   public void tickPrecipitation(BlockPos var1) {
      BlockPos var2 = this.absolutePos(var1);
      ServerLevel var3 = this.getLevel();
      var3.tickPrecipitation(var2);
   }

   public void tickPrecipitation() {
      AABB var1 = this.getRelativeBounds();
      int var2 = (int)Math.floor(var1.maxX);
      int var3 = (int)Math.floor(var1.maxZ);
      int var4 = (int)Math.floor(var1.maxY);

      for(int var5 = (int)Math.floor(var1.minX); var5 < var2; ++var5) {
         for(int var6 = (int)Math.floor(var1.minZ); var6 < var3; ++var6) {
            this.tickPrecipitation(new BlockPos(var5, var4, var6));
         }
      }

   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      BlockPos var4 = this.absolutePos(new BlockPos(var2, 0, var3));
      return this.relativePos(this.getLevel().getHeightmapPos(var1, var4)).getY();
   }

   public void fail(Component var1, BlockPos var2) {
      throw this.assertionException(var2, var1);
   }

   public void fail(Component var1, Entity var2) {
      throw this.assertionException(var2.blockPosition(), var1);
   }

   public void fail(Component var1) {
      throw this.assertionException(var1);
   }

   public void fail(String var1) {
      throw this.assertionException(Component.literal(var1));
   }

   public void failIf(Runnable var1) {
      this.testInfo.createSequence().thenWaitUntil(var1).thenFail(() -> this.assertionException("test.error.fail"));
   }

   public void failIfEver(Runnable var1) {
      LongStream.range((long)this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks()).forEach((var2) -> {
         GameTestInfo var10000 = this.testInfo;
         Objects.requireNonNull(var1);
         var10000.setRunAtTickTime(var2, var1::run);
      });
   }

   public GameTestSequence startSequence() {
      return this.testInfo.createSequence();
   }

   public BlockPos absolutePos(BlockPos var1) {
      BlockPos var2 = this.testInfo.getTestOrigin();
      BlockPos var3 = var2.offset(var1);
      return StructureTemplate.transform(var3, Mirror.NONE, this.testInfo.getRotation(), var2);
   }

   public BlockPos relativePos(BlockPos var1) {
      BlockPos var2 = this.testInfo.getTestOrigin();
      Rotation var3 = this.testInfo.getRotation().getRotated(Rotation.CLOCKWISE_180);
      BlockPos var4 = StructureTemplate.transform(var1, Mirror.NONE, var3, var2);
      return var4.subtract(var2);
   }

   public AABB absoluteAABB(AABB var1) {
      Vec3 var2 = this.absoluteVec(var1.getMinPosition());
      Vec3 var3 = this.absoluteVec(var1.getMaxPosition());
      return new AABB(var2, var3);
   }

   public AABB relativeAABB(AABB var1) {
      Vec3 var2 = this.relativeVec(var1.getMinPosition());
      Vec3 var3 = this.relativeVec(var1.getMaxPosition());
      return new AABB(var2, var3);
   }

   public Vec3 absoluteVec(Vec3 var1) {
      Vec3 var2 = Vec3.atLowerCornerOf(this.testInfo.getTestOrigin());
      return StructureTemplate.transform(var2.add(var1), Mirror.NONE, this.testInfo.getRotation(), this.testInfo.getTestOrigin());
   }

   public Vec3 relativeVec(Vec3 var1) {
      Vec3 var2 = Vec3.atLowerCornerOf(this.testInfo.getTestOrigin());
      return StructureTemplate.transform(var1.subtract(var2), Mirror.NONE, this.testInfo.getRotation(), this.testInfo.getTestOrigin());
   }

   public Rotation getTestRotation() {
      return this.testInfo.getRotation();
   }

   public Direction getTestDirection() {
      return this.testInfo.getRotation().rotate(Direction.SOUTH);
   }

   public void assertTrue(boolean var1, Component var2) {
      if (!var1) {
         throw this.assertionException(var2);
      }
   }

   public <N> void assertValueEqual(N var1, N var2, Component var3) {
      if (!var1.equals(var2)) {
         throw this.assertionException("test.error.value_not_equal", var3, var1, var2);
      }
   }

   public void assertFalse(boolean var1, Component var2) {
      this.assertTrue(!var1, var2);
   }

   public long getTick() {
      return (long)this.testInfo.getTick();
   }

   public AABB getBounds() {
      return this.testInfo.getStructureBounds();
   }

   private AABB getRelativeBounds() {
      AABB var1 = this.testInfo.getStructureBounds();
      Rotation var2 = this.testInfo.getRotation();
      switch (var2) {
         case COUNTERCLOCKWISE_90:
         case CLOCKWISE_90:
            return new AABB(0.0, 0.0, 0.0, var1.getZsize(), var1.getYsize(), var1.getXsize());
         default:
            return new AABB(0.0, 0.0, 0.0, var1.getXsize(), var1.getYsize(), var1.getZsize());
      }
   }

   public void forEveryBlockInStructure(Consumer<BlockPos> var1) {
      AABB var2 = this.getRelativeBounds().contract(1.0, 1.0, 1.0);
      BlockPos.MutableBlockPos.betweenClosedStream(var2).forEach(var1);
   }

   public void onEachTick(Runnable var1) {
      LongStream.range((long)this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks()).forEach((var2) -> {
         GameTestInfo var10000 = this.testInfo;
         Objects.requireNonNull(var1);
         var10000.setRunAtTickTime(var2, var1::run);
      });
   }

   public void placeAt(Player var1, ItemStack var2, BlockPos var3, Direction var4) {
      BlockPos var5 = this.absolutePos(var3.relative(var4));
      BlockHitResult var6 = new BlockHitResult(Vec3.atCenterOf(var5), var4, var5, false);
      UseOnContext var7 = new UseOnContext(var1, InteractionHand.MAIN_HAND, var6);
      var2.useOn(var7);
   }

   public void setBiome(ResourceKey<Biome> var1) {
      AABB var2 = this.getBounds();
      BlockPos var3 = BlockPos.containing(var2.minX, var2.minY, var2.minZ);
      BlockPos var4 = BlockPos.containing(var2.maxX, var2.maxY, var2.maxZ);
      Either var5 = FillBiomeCommand.fill(this.getLevel(), var3, var4, this.getLevel().registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(var1));
      if (var5.right().isPresent()) {
         throw this.assertionException("test.error.set_biome");
      }
   }
}
