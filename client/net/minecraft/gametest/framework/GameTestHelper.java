package net.minecraft.gametest.framework;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

   public ServerLevel getLevel() {
      return this.testInfo.getLevel();
   }

   public BlockState getBlockState(BlockPos var1) {
      return this.getLevel().getBlockState(this.absolutePos(var1));
   }

   public <T extends BlockEntity> T getBlockEntity(BlockPos var1) {
      BlockEntity var2 = this.getLevel().getBlockEntity(this.absolutePos(var1));
      if (var2 == null) {
         throw new GameTestAssertPosException("Missing block entity", this.absolutePos(var1), var1, this.testInfo.getTick());
      } else {
         return (T)var2;
      }
   }

   public void killAllEntities() {
      this.killAllEntitiesOfClass(Entity.class);
   }

   public void killAllEntitiesOfClass(Class var1) {
      AABB var2 = this.getBounds();
      List var3 = this.getLevel().getEntitiesOfClass(var1, var2.inflate(1.0), var0 -> !(var0 instanceof Player));
      var3.forEach(Entity::kill);
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
      return this.spawn(var1, Vec3.atBottomCenterOf(var2));
   }

   public <E extends Entity> E spawn(EntityType<E> var1, Vec3 var2) {
      ServerLevel var3 = this.getLevel();
      Entity var4 = var1.create(var3, EntitySpawnReason.STRUCTURE);
      if (var4 == null) {
         throw new NullPointerException("Failed to create entity " + var1.builtInRegistryHolder().key().location());
      } else {
         if (var4 instanceof Mob var5) {
            var5.setPersistenceRequired();
         }

         Vec3 var6 = this.absoluteVec(var2);
         var4.moveTo(var6.x, var6.y, var6.z, var4.getYRot(), var4.getXRot());
         var3.addFreshEntity(var4);
         return (E)var4;
      }
   }

   public <E extends Entity> E findOneEntity(EntityType<E> var1) {
      return this.findClosestEntity(var1, 0, 0, 0, 2.147483647E9);
   }

   public <E extends Entity> E findClosestEntity(EntityType<E> var1, int var2, int var3, int var4, double var5) {
      List var7 = this.findEntities(var1, var2, var3, var4, var5);
      if (var7.isEmpty()) {
         throw new GameTestAssertException("Expected " + var1.toShortString() + " to exist around " + var2 + "," + var3 + "," + var4);
      } else if (var7.size() > 1) {
         throw new GameTestAssertException(
            "Expected only one " + var1.toShortString() + " to exist around " + var2 + "," + var3 + "," + var4 + ", but found " + var7.size()
         );
      } else {
         Vec3 var8 = this.absoluteVec(new Vec3((double)var2, (double)var3, (double)var4));
         var7.sort((var1x, var2x) -> {
            double var3x = var1x.position().distanceTo(var8);
            double var5x = var2x.position().distanceTo(var8);
            return Double.compare(var3x, var5x);
         });
         return (E)var7.get(0);
      }
   }

   public <E extends Entity> List<E> findEntities(EntityType<E> var1, int var2, int var3, int var4, double var5) {
      return this.findEntities(var1, Vec3.atBottomCenterOf(new BlockPos(var2, var3, var4)), var5);
   }

   public <E extends Entity> List<E> findEntities(EntityType<E> var1, Vec3 var2, double var3) {
      ServerLevel var5 = this.getLevel();
      Vec3 var6 = this.absoluteVec(var2);
      AABB var7 = this.testInfo.getStructureBounds();
      AABB var8 = new AABB(var6.add(-var3, -var3, -var3), var6.add(var3, var3, var3));
      return var5.getEntities(var1, var7, var1x -> var1x.getBoundingBox().intersects(var8) && var1x.isAlive());
   }

   public <E extends Entity> E spawn(EntityType<E> var1, int var2, int var3, int var4) {
      return this.spawn(var1, new BlockPos(var2, var3, var4));
   }

   public <E extends Entity> E spawn(EntityType<E> var1, float var2, float var3, float var4) {
      return this.spawn(var1, new Vec3((double)var2, (double)var3, (double)var4));
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> var1, BlockPos var2) {
      Mob var3 = this.spawn(var1, var2);
      var3.removeFreeWill();
      return (E)var3;
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> var1, int var2, int var3, int var4) {
      return this.spawnWithNoFreeWill(var1, new BlockPos(var2, var3, var4));
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> var1, Vec3 var2) {
      Mob var3 = this.spawn(var1, var2);
      var3.removeFreeWill();
      return (E)var3;
   }

   public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> var1, float var2, float var3, float var4) {
      return this.spawnWithNoFreeWill(var1, new Vec3((double)var2, (double)var3, (double)var4));
   }

   public void moveTo(Mob var1, float var2, float var3, float var4) {
      Vec3 var5 = this.absoluteVec(new Vec3((double)var2, (double)var3, (double)var4));
      var1.moveTo(var5.x, var5.y, var5.z, var1.getYRot(), var1.getXRot());
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
      this.assertBlockState(var1, var0 -> var0.is(BlockTags.BUTTONS), () -> "Expected button");
      BlockPos var2 = this.absolutePos(var1);
      BlockState var3 = this.getLevel().getBlockState(var2);
      ButtonBlock var4 = (ButtonBlock)var3.getBlock();
      var4.press(var3, this.getLevel(), var2, null);
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
      return new Player(this.getLevel(), BlockPos.ZERO, 0.0F, new GameProfile(UUID.randomUUID(), "test-mock-player")) {
         @Override
         public boolean isSpectator() {
            return var1 == GameType.SPECTATOR;
         }

         @Override
         public boolean isCreative() {
            return var1.isCreative();
         }

         @Override
         public boolean isLocalPlayer() {
            return true;
         }
      };
   }

   @Deprecated(
      forRemoval = true
   )
   public ServerPlayer makeMockServerPlayerInLevel() {
      CommonListenerCookie var1 = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "test-mock-player"), false);
      ServerPlayer var2 = new ServerPlayer(this.getLevel().getServer(), this.getLevel(), var1.gameProfile(), var1.clientInformation()) {
         @Override
         public boolean isSpectator() {
            return false;
         }

         @Override
         public boolean isCreative() {
            return true;
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
      var4.pull(var3, this.getLevel(), var2, null);
   }

   public void pulseRedstone(BlockPos var1, long var2) {
      this.setBlock(var1, Blocks.REDSTONE_BLOCK);
      this.runAfterDelay(var2, () -> this.setBlock(var1, Blocks.AIR));
   }

   public void destroyBlock(BlockPos var1) {
      this.getLevel().destroyBlock(this.absolutePos(var1), false, null);
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
      this.assertBlock(var2, var2x -> var3.is(var1), "Expected " + var1.getName().getString() + ", got " + var3.getBlock().getName().getString());
   }

   public void assertBlockNotPresent(Block var1, int var2, int var3, int var4) {
      this.assertBlockNotPresent(var1, new BlockPos(var2, var3, var4));
   }

   public void assertBlockNotPresent(Block var1, BlockPos var2) {
      this.assertBlock(var2, var3 -> !this.getBlockState(var2).is(var1), "Did not expect " + var1.getName().getString());
   }

   public void succeedWhenBlockPresent(Block var1, int var2, int var3, int var4) {
      this.succeedWhenBlockPresent(var1, new BlockPos(var2, var3, var4));
   }

   public void succeedWhenBlockPresent(Block var1, BlockPos var2) {
      this.succeedWhen(() -> this.assertBlockPresent(var1, var2));
   }

   public void assertBlock(BlockPos var1, Predicate<Block> var2, String var3) {
      this.assertBlock(var1, var2, () -> var3);
   }

   public void assertBlock(BlockPos var1, Predicate<Block> var2, Supplier<String> var3) {
      this.assertBlockState(var1, var1x -> var2.test(var1x.getBlock()), var3);
   }

   public <T extends Comparable<T>> void assertBlockProperty(BlockPos var1, Property<T> var2, T var3) {
      BlockState var4 = this.getBlockState(var1);
      boolean var5 = var4.hasProperty(var2);
      if (!var5 || !var4.getValue(var2).equals(var3)) {
         String var6 = var5 ? "was " + var4.getValue(var2) : "property " + var2.getName() + " is missing";
         String var7 = String.format(Locale.ROOT, "Expected property %s to be %s, %s", var2.getName(), var3, var6);
         throw new GameTestAssertPosException(var7, this.absolutePos(var1), var1, this.testInfo.getTick());
      }
   }

   public <T extends Comparable<T>> void assertBlockProperty(BlockPos var1, Property<T> var2, Predicate<T> var3, String var4) {
      this.assertBlockState(var1, var2x -> {
         if (!var2x.hasProperty(var2)) {
            return false;
         } else {
            Comparable var3x = var2x.getValue(var2);
            return var3.test(var3x);
         }
      }, () -> var4);
   }

   public void assertBlockState(BlockPos var1, Predicate<BlockState> var2, Supplier<String> var3) {
      BlockState var4 = this.getBlockState(var1);
      if (!var2.test(var4)) {
         throw new GameTestAssertPosException((String)var3.get(), this.absolutePos(var1), var1, this.testInfo.getTick());
      }
   }

   public <T extends BlockEntity> void assertBlockEntityData(BlockPos var1, Predicate<T> var2, Supplier<String> var3) {
      BlockEntity var4 = this.getBlockEntity(var1);
      if (!var2.test(var4)) {
         throw new GameTestAssertPosException((String)var3.get(), this.absolutePos(var1), var1, this.testInfo.getTick());
      }
   }

   public void assertRedstoneSignal(BlockPos var1, Direction var2, IntPredicate var3, Supplier<String> var4) {
      BlockPos var5 = this.absolutePos(var1);
      ServerLevel var6 = this.getLevel();
      BlockState var7 = var6.getBlockState(var5);
      int var8 = var7.getSignal(var6, var5, var2);
      if (!var3.test(var8)) {
         throw new GameTestAssertPosException((String)var4.get(), var5, var1, this.testInfo.getTick());
      }
   }

   public void assertEntityPresent(EntityType<?> var1) {
      List var2 = this.getLevel().getEntities(var1, this.getBounds(), Entity::isAlive);
      if (var2.isEmpty()) {
         throw new GameTestAssertException("Expected " + var1.toShortString() + " to exist");
      }
   }

   public void assertEntityPresent(EntityType<?> var1, int var2, int var3, int var4) {
      this.assertEntityPresent(var1, new BlockPos(var2, var3, var4));
   }

   public void assertEntityPresent(EntityType<?> var1, BlockPos var2) {
      BlockPos var3 = this.absolutePos(var2);
      List var4 = this.getLevel().getEntities(var1, new AABB(var3), Entity::isAlive);
      if (var4.isEmpty()) {
         throw new GameTestAssertPosException("Expected " + var1.toShortString(), var3, var2, this.testInfo.getTick());
      }
   }

   public void assertEntityPresent(EntityType<?> var1, AABB var2) {
      AABB var3 = this.absoluteAABB(var2);
      List var4 = this.getLevel().getEntities(var1, var3, Entity::isAlive);
      if (var4.isEmpty()) {
         throw new GameTestAssertPosException(
            "Expected " + var1.toShortString(), BlockPos.containing(var3.getCenter()), BlockPos.containing(var2.getCenter()), this.testInfo.getTick()
         );
      }
   }

   public void assertEntitiesPresent(EntityType<?> var1, int var2) {
      List var3 = this.getLevel().getEntities(var1, this.getBounds(), Entity::isAlive);
      if (var3.size() != var2) {
         throw new GameTestAssertException("Expected " + var2 + " of type " + var1.toShortString() + " to exist, found " + var3.size());
      }
   }

   public void assertEntitiesPresent(EntityType<?> var1, BlockPos var2, int var3, double var4) {
      BlockPos var6 = this.absolutePos(var2);
      List var7 = this.getEntities(var1, var2, var4);
      if (var7.size() != var3) {
         throw new GameTestAssertPosException(
            "Expected " + var3 + " entities of type " + var1.toShortString() + ", actual number of entities found=" + var7.size(),
            var6,
            var2,
            this.testInfo.getTick()
         );
      }
   }

   public void assertEntityPresent(EntityType<?> var1, BlockPos var2, double var3) {
      List var5 = this.getEntities(var1, var2, var3);
      if (var5.isEmpty()) {
         BlockPos var6 = this.absolutePos(var2);
         throw new GameTestAssertPosException("Expected " + var1.toShortString(), var6, var2, this.testInfo.getTick());
      }
   }

   public <T extends Entity> List<T> getEntities(EntityType<T> var1, BlockPos var2, double var3) {
      BlockPos var5 = this.absolutePos(var2);
      return this.getLevel().getEntities(var1, new AABB(var5).inflate(var3), Entity::isAlive);
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
      var4.stream()
         .filter(var1x -> var1x == var1)
         .findFirst()
         .orElseThrow(() -> new GameTestAssertPosException("Expected " + var1.getType().toShortString(), var3, var2, this.testInfo.getTick()));
   }

   public void assertItemEntityCountIs(Item var1, BlockPos var2, double var3, int var5) {
      BlockPos var6 = this.absolutePos(var2);
      List var7 = this.getLevel().getEntities(EntityType.ITEM, new AABB(var6).inflate(var3), Entity::isAlive);
      int var8 = 0;

      for (ItemEntity var10 : var7) {
         ItemStack var11 = var10.getItem();
         if (var11.is(var1)) {
            var8 += var11.getCount();
         }
      }

      if (var8 != var5) {
         throw new GameTestAssertPosException(
            "Expected " + var5 + " " + var1.getName().getString() + " items to exist (found " + var8 + ")", var6, var2, this.testInfo.getTick()
         );
      }
   }

   public void assertItemEntityPresent(Item var1, BlockPos var2, double var3) {
      BlockPos var5 = this.absolutePos(var2);

      for (Entity var8 : this.getLevel().getEntities(EntityType.ITEM, new AABB(var5).inflate(var3), Entity::isAlive)) {
         ItemEntity var9 = (ItemEntity)var8;
         if (var9.getItem().getItem().equals(var1)) {
            return;
         }
      }

      throw new GameTestAssertPosException("Expected " + var1.getName().getString() + " item", var5, var2, this.testInfo.getTick());
   }

   public void assertItemEntityNotPresent(Item var1, BlockPos var2, double var3) {
      BlockPos var5 = this.absolutePos(var2);

      for (Entity var8 : this.getLevel().getEntities(EntityType.ITEM, new AABB(var5).inflate(var3), Entity::isAlive)) {
         ItemEntity var9 = (ItemEntity)var8;
         if (var9.getItem().getItem().equals(var1)) {
            throw new GameTestAssertPosException("Did not expect " + var1.getName().getString() + " item", var5, var2, this.testInfo.getTick());
         }
      }
   }

   public void assertItemEntityPresent(Item var1) {
      for (Entity var4 : this.getLevel().getEntities(EntityType.ITEM, this.getBounds(), Entity::isAlive)) {
         ItemEntity var5 = (ItemEntity)var4;
         if (var5.getItem().getItem().equals(var1)) {
            return;
         }
      }

      throw new GameTestAssertException("Expected " + var1.getName().getString() + " item");
   }

   public void assertItemEntityNotPresent(Item var1) {
      for (Entity var4 : this.getLevel().getEntities(EntityType.ITEM, this.getBounds(), Entity::isAlive)) {
         ItemEntity var5 = (ItemEntity)var4;
         if (var5.getItem().getItem().equals(var1)) {
            throw new GameTestAssertException("Did not expect " + var1.getName().getString() + " item");
         }
      }
   }

   public void assertEntityNotPresent(EntityType<?> var1) {
      List var2 = this.getLevel().getEntities(var1, this.getBounds(), Entity::isAlive);
      if (!var2.isEmpty()) {
         throw new GameTestAssertException("Did not expect " + var1.toShortString() + " to exist");
      }
   }

   public void assertEntityNotPresent(EntityType<?> var1, int var2, int var3, int var4) {
      this.assertEntityNotPresent(var1, new BlockPos(var2, var3, var4));
   }

   public void assertEntityNotPresent(EntityType<?> var1, BlockPos var2) {
      BlockPos var3 = this.absolutePos(var2);
      List var4 = this.getLevel().getEntities(var1, new AABB(var3), Entity::isAlive);
      if (!var4.isEmpty()) {
         throw new GameTestAssertPosException("Did not expect " + var1.toShortString(), var3, var2, this.testInfo.getTick());
      }
   }

   public void assertEntityNotPresent(EntityType<?> var1, AABB var2) {
      AABB var3 = this.absoluteAABB(var2);
      List var4 = this.getLevel().getEntities(var1, var3, Entity::isAlive);
      if (!var4.isEmpty()) {
         throw new GameTestAssertPosException(
            "Did not expect " + var1.toShortString(), BlockPos.containing(var3.getCenter()), BlockPos.containing(var2.getCenter()), this.testInfo.getTick()
         );
      }
   }

   public void assertEntityTouching(EntityType<?> var1, double var2, double var4, double var6) {
      Vec3 var8 = new Vec3(var2, var4, var6);
      Vec3 var9 = this.absoluteVec(var8);
      Predicate var10 = var1x -> var1x.getBoundingBox().intersects(var9, var9);
      List var11 = this.getLevel().getEntities(var1, this.getBounds(), var10);
      if (var11.isEmpty()) {
         throw new GameTestAssertException("Expected " + var1.toShortString() + " to touch " + var9 + " (relative " + var8 + ")");
      }
   }

   public void assertEntityNotTouching(EntityType<?> var1, double var2, double var4, double var6) {
      Vec3 var8 = new Vec3(var2, var4, var6);
      Vec3 var9 = this.absoluteVec(var8);
      Predicate var10 = var1x -> !var1x.getBoundingBox().intersects(var9, var9);
      List var11 = this.getLevel().getEntities(var1, this.getBounds(), var10);
      if (var11.isEmpty()) {
         throw new GameTestAssertException("Did not expect " + var1.toShortString() + " to touch " + var9 + " (relative " + var8 + ")");
      }
   }

   public <E extends Entity, T> void assertEntityData(BlockPos var1, EntityType<E> var2, Predicate<E> var3) {
      BlockPos var4 = this.absolutePos(var1);
      List var5 = this.getLevel().getEntities(var2, new AABB(var4), Entity::isAlive);
      if (var5.isEmpty()) {
         throw new GameTestAssertPosException("Expected " + var2.toShortString(), var4, var1, this.testInfo.getTick());
      } else {
         for (Entity var7 : var5) {
            if (!var3.test(var7)) {
               throw new GameTestAssertException("Test failed for entity " + var7);
            }
         }
      }
   }

   public <E extends Entity, T> void assertEntityData(BlockPos var1, EntityType<E> var2, Function<? super E, T> var3, @Nullable T var4) {
      BlockPos var5 = this.absolutePos(var1);
      List var6 = this.getLevel().getEntities(var2, new AABB(var5), Entity::isAlive);
      if (var6.isEmpty()) {
         throw new GameTestAssertPosException("Expected " + var2.toShortString(), var5, var1, this.testInfo.getTick());
      } else {
         for (Entity var8 : var6) {
            Object var9 = var3.apply(var8);
            if (!Objects.equals(var9, var4)) {
               throw new GameTestAssertException("Expected entity data to be: " + var4 + ", but was: " + var9);
            }
         }
      }
   }

   public <E extends LivingEntity> void assertEntityIsHolding(BlockPos var1, EntityType<E> var2, Item var3) {
      BlockPos var4 = this.absolutePos(var1);
      List var5 = this.getLevel().getEntities(var2, new AABB(var4), Entity::isAlive);
      if (var5.isEmpty()) {
         throw new GameTestAssertPosException("Expected entity of type: " + var2, var4, var1, this.getTick());
      } else {
         for (LivingEntity var7 : var5) {
            if (var7.isHolding(var3)) {
               return;
            }
         }

         throw new GameTestAssertPosException("Entity should be holding: " + var3, var4, var1, this.getTick());
      }
   }

   public <E extends Entity & InventoryCarrier> void assertEntityInventoryContains(BlockPos var1, EntityType<E> var2, Item var3) {
      BlockPos var4 = this.absolutePos(var1);
      List var5 = this.getLevel().getEntities(var2, new AABB(var4), var0 -> var0.isAlive());
      if (var5.isEmpty()) {
         throw new GameTestAssertPosException("Expected " + var2.toShortString() + " to exist", var4, var1, this.getTick());
      } else {
         for (Entity var7 : var5) {
            if (((InventoryCarrier)var7).getInventory().hasAnyMatching(var1x -> var1x.is(var3))) {
               return;
            }
         }

         throw new GameTestAssertPosException("Entity inventory should contain: " + var3, var4, var1, this.getTick());
      }
   }

   public void assertContainerEmpty(BlockPos var1) {
      BlockPos var2 = this.absolutePos(var1);
      BlockEntity var3 = this.getLevel().getBlockEntity(var2);
      if (var3 instanceof BaseContainerBlockEntity && !((BaseContainerBlockEntity)var3).isEmpty()) {
         throw new GameTestAssertException("Container should be empty");
      }
   }

   public void assertContainerContains(BlockPos var1, Item var2) {
      BlockPos var3 = this.absolutePos(var1);
      BlockEntity var4 = this.getLevel().getBlockEntity(var3);
      if (!(var4 instanceof BaseContainerBlockEntity)) {
         throw new GameTestAssertException("Expected a container at " + var1 + ", found " + BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(var4.getType()));
      } else if (((BaseContainerBlockEntity)var4).countItem(var2) != 1) {
         throw new GameTestAssertException("Container should contain: " + var2);
      }
   }

   public void assertSameBlockStates(BoundingBox var1, BlockPos var2) {
      BlockPos.betweenClosedStream(var1).forEach(var3 -> {
         BlockPos var4 = var2.offset(var3.getX() - var1.minX(), var3.getY() - var1.minY(), var3.getZ() - var1.minZ());
         this.assertSameBlockState(var3, var4);
      });
   }

   public void assertSameBlockState(BlockPos var1, BlockPos var2) {
      BlockState var3 = this.getBlockState(var1);
      BlockState var4 = this.getBlockState(var2);
      if (var3 != var4) {
         this.fail("Incorrect state. Expected " + var4 + ", got " + var3, var1);
      }
   }

   public void assertAtTickTimeContainerContains(long var1, BlockPos var3, Item var4) {
      this.runAtTickTime(var1, () -> this.assertContainerContains(var3, var4));
   }

   public void assertAtTickTimeContainerEmpty(long var1, BlockPos var3) {
      this.runAtTickTime(var1, () -> this.assertContainerEmpty(var3));
   }

   public <E extends Entity, T> void succeedWhenEntityData(BlockPos var1, EntityType<E> var2, Function<E, T> var3, T var4) {
      this.succeedWhen(() -> this.assertEntityData(var1, var2, var3, var4));
   }

   public void assertEntityPosition(Entity var1, AABB var2, String var3) {
      if (!var2.contains(this.relativeVec(var1.position()))) {
         this.fail(var3);
      }
   }

   public <E extends Entity> void assertEntityProperty(E var1, Predicate<E> var2, String var3) {
      if (!var2.test(var1)) {
         throw new GameTestAssertException("Entity " + var1 + " failed " + var3 + " test");
      }
   }

   public <E extends Entity, T> void assertEntityProperty(E var1, Function<E, T> var2, String var3, T var4) {
      Object var5 = var2.apply(var1);
      if (!var5.equals(var4)) {
         throw new GameTestAssertException("Entity " + var1 + " value " + var3 + "=" + var5 + " is not equal to expected " + var4);
      }
   }

   public void assertLivingEntityHasMobEffect(LivingEntity var1, Holder<MobEffect> var2, int var3) {
      MobEffectInstance var4 = var1.getEffect(var2);
      if (var4 == null || var4.getAmplifier() != var3) {
         int var5 = var3 + 1;
         throw new GameTestAssertException("Entity " + var1 + " failed has " + ((MobEffect)var2.value()).getDescriptionId() + " x " + var5 + " test");
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
      this.runAtTickTime(this.testInfo.getTick() + var1, var3);
   }

   public void randomTick(BlockPos var1) {
      BlockPos var2 = this.absolutePos(var1);
      ServerLevel var3 = this.getLevel();
      var3.getBlockState(var2).randomTick(var3, var2, var3.random);
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

      for (int var5 = (int)Math.floor(var1.minX); var5 < var2; var5++) {
         for (int var6 = (int)Math.floor(var1.minZ); var6 < var3; var6++) {
            this.tickPrecipitation(new BlockPos(var5, var4, var6));
         }
      }
   }

   public int getHeight(Heightmap.Types var1, int var2, int var3) {
      BlockPos var4 = this.absolutePos(new BlockPos(var2, 0, var3));
      return this.relativePos(this.getLevel().getHeightmapPos(var1, var4)).getY();
   }

   public void fail(String var1, BlockPos var2) {
      throw new GameTestAssertPosException(var1, this.absolutePos(var2), var2, this.getTick());
   }

   public void fail(String var1, Entity var2) {
      throw new GameTestAssertPosException(var1, var2.blockPosition(), this.relativePos(var2.blockPosition()), this.getTick());
   }

   public void fail(String var1) {
      throw new GameTestAssertException(var1);
   }

   public void failIf(Runnable var1) {
      this.testInfo.createSequence().thenWaitUntil(var1).thenFail(() -> new GameTestAssertException("Fail conditions met"));
   }

   public void failIfEver(Runnable var1) {
      LongStream.range(this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks()).forEach(var2 -> this.testInfo.setRunAtTickTime(var2, var1::run));
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

   public void assertTrue(boolean var1, String var2) {
      if (!var1) {
         throw new GameTestAssertException(var2);
      }
   }

   public <N> void assertValueEqual(N var1, N var2, String var3) {
      if (!var1.equals(var2)) {
         throw new GameTestAssertException("Expected " + var3 + " to be " + var2 + ", but was " + var1);
      }
   }

   public void assertFalse(boolean var1, String var2) {
      if (var1) {
         throw new GameTestAssertException(var2);
      }
   }

   public long getTick() {
      return this.testInfo.getTick();
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
      LongStream.range(this.testInfo.getTick(), (long)this.testInfo.getTimeoutTicks()).forEach(var2 -> this.testInfo.setRunAtTickTime(var2, var1::run));
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
         this.fail("Failed to set biome for test");
      }
   }
}
