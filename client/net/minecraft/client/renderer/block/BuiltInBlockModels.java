package net.minecraft.client.renderer.block;

import com.google.common.collect.UnmodifiableIterator;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.MultiblockChestResources;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockStateModelWrapper;
import net.minecraft.client.renderer.block.model.CompositeBlockModel;
import net.minecraft.client.renderer.block.model.ConditionalBlockModel;
import net.minecraft.client.renderer.block.model.EmptyBlockModel;
import net.minecraft.client.renderer.block.model.SpecialBlockModelWrapper;
import net.minecraft.client.renderer.block.model.properties.conditional.IsXmas;
import net.minecraft.client.renderer.block.model.properties.select.DisplayContext;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.ConduitRenderer;
import net.minecraft.client.renderer.blockentity.CopperGolemStatueBlockRenderer;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.entity.CopperGolemRenderer;
import net.minecraft.client.renderer.special.BannerSpecialRenderer;
import net.minecraft.client.renderer.special.BedSpecialRenderer;
import net.minecraft.client.renderer.special.BellSpecialRenderer;
import net.minecraft.client.renderer.special.BookSpecialRenderer;
import net.minecraft.client.renderer.special.ChestSpecialRenderer;
import net.minecraft.client.renderer.special.ConduitSpecialRenderer;
import net.minecraft.client.renderer.special.CopperGolemStatueSpecialRenderer;
import net.minecraft.client.renderer.special.DecoratedPotSpecialRenderer;
import net.minecraft.client.renderer.special.EndCubeSpecialRenderer;
import net.minecraft.client.renderer.special.HangingSignSpecialRenderer;
import net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer;
import net.minecraft.client.renderer.special.ShulkerBoxSpecialRenderer;
import net.minecraft.client.renderer.special.SkullSpecialRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.StandingSignSpecialRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.HangingSignBlock;
import net.minecraft.world.level.block.PlainSignBlock;
import net.minecraft.world.level.block.PlayerHeadBlock;
import net.minecraft.world.level.block.PlayerWallHeadBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class BuiltInBlockModels {
   public BuiltInBlockModels() {
      super();
   }

   private static void addDefaults(final Builder builder) {
      createAir(builder, Blocks.AIR);
      createAir(builder, Blocks.CAVE_AIR);
      createAir(builder, Blocks.VOID_AIR);
      createMobHeads(builder, SkullBlock.Types.SKELETON, Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL);
      createMobHeads(builder, SkullBlock.Types.ZOMBIE, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD);
      createMobHeads(builder, SkullBlock.Types.CREEPER, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD);
      createMobHeads(builder, SkullBlock.Types.DRAGON, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD);
      createMobHeads(builder, SkullBlock.Types.PIGLIN, Blocks.PIGLIN_HEAD, Blocks.PIGLIN_WALL_HEAD);
      createMobHeads(builder, SkullBlock.Types.WITHER_SKELETON, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL);
      builder.put((ModelFactory)createPlayerHead(), Blocks.PLAYER_HEAD);
      builder.put((ModelFactory)createPlayerWallHead(), Blocks.PLAYER_WALL_HEAD);
      createBanners(builder, DyeColor.WHITE, Blocks.WHITE_BANNER, Blocks.WHITE_WALL_BANNER);
      createBanners(builder, DyeColor.ORANGE, Blocks.ORANGE_BANNER, Blocks.ORANGE_WALL_BANNER);
      createBanners(builder, DyeColor.MAGENTA, Blocks.MAGENTA_BANNER, Blocks.MAGENTA_WALL_BANNER);
      createBanners(builder, DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER);
      createBanners(builder, DyeColor.YELLOW, Blocks.YELLOW_BANNER, Blocks.YELLOW_WALL_BANNER);
      createBanners(builder, DyeColor.LIME, Blocks.LIME_BANNER, Blocks.LIME_WALL_BANNER);
      createBanners(builder, DyeColor.PINK, Blocks.PINK_BANNER, Blocks.PINK_WALL_BANNER);
      createBanners(builder, DyeColor.GRAY, Blocks.GRAY_BANNER, Blocks.GRAY_WALL_BANNER);
      createBanners(builder, DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER);
      createBanners(builder, DyeColor.CYAN, Blocks.CYAN_BANNER, Blocks.CYAN_WALL_BANNER);
      createBanners(builder, DyeColor.PURPLE, Blocks.PURPLE_BANNER, Blocks.PURPLE_WALL_BANNER);
      createBanners(builder, DyeColor.BLUE, Blocks.BLUE_BANNER, Blocks.BLUE_WALL_BANNER);
      createBanners(builder, DyeColor.BROWN, Blocks.BROWN_BANNER, Blocks.BROWN_WALL_BANNER);
      createBanners(builder, DyeColor.GREEN, Blocks.GREEN_BANNER, Blocks.GREEN_WALL_BANNER);
      createBanners(builder, DyeColor.RED, Blocks.RED_BANNER, Blocks.RED_WALL_BANNER);
      createBanners(builder, DyeColor.BLACK, Blocks.BLACK_BANNER, Blocks.BLACK_WALL_BANNER);
      builder.put((ModelFactory)createBed(DyeColor.WHITE), Blocks.WHITE_BED);
      builder.put((ModelFactory)createBed(DyeColor.ORANGE), Blocks.ORANGE_BED);
      builder.put((ModelFactory)createBed(DyeColor.MAGENTA), Blocks.MAGENTA_BED);
      builder.put((ModelFactory)createBed(DyeColor.LIGHT_BLUE), Blocks.LIGHT_BLUE_BED);
      builder.put((ModelFactory)createBed(DyeColor.YELLOW), Blocks.YELLOW_BED);
      builder.put((ModelFactory)createBed(DyeColor.LIME), Blocks.LIME_BED);
      builder.put((ModelFactory)createBed(DyeColor.PINK), Blocks.PINK_BED);
      builder.put((ModelFactory)createBed(DyeColor.GRAY), Blocks.GRAY_BED);
      builder.put((ModelFactory)createBed(DyeColor.LIGHT_GRAY), Blocks.LIGHT_GRAY_BED);
      builder.put((ModelFactory)createBed(DyeColor.CYAN), Blocks.CYAN_BED);
      builder.put((ModelFactory)createBed(DyeColor.PURPLE), Blocks.PURPLE_BED);
      builder.put((ModelFactory)createBed(DyeColor.BLUE), Blocks.BLUE_BED);
      builder.put((ModelFactory)createBed(DyeColor.BROWN), Blocks.BROWN_BED);
      builder.put((ModelFactory)createBed(DyeColor.GREEN), Blocks.GREEN_BED);
      builder.put((ModelFactory)createBed(DyeColor.RED), Blocks.RED_BED);
      builder.put((ModelFactory)createBed(DyeColor.BLACK), Blocks.BLACK_BED);
      builder.put((ModelFactory)createShulkerBox(), Blocks.SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.WHITE), Blocks.WHITE_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.ORANGE), Blocks.ORANGE_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.MAGENTA), Blocks.MAGENTA_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.LIGHT_BLUE), Blocks.LIGHT_BLUE_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.YELLOW), Blocks.YELLOW_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.LIME), Blocks.LIME_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.PINK), Blocks.PINK_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.GRAY), Blocks.GRAY_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.LIGHT_GRAY), Blocks.LIGHT_GRAY_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.CYAN), Blocks.CYAN_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.PURPLE), Blocks.PURPLE_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.BLUE), Blocks.BLUE_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.BROWN), Blocks.BROWN_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.GREEN), Blocks.GREEN_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.RED), Blocks.RED_SHULKER_BOX);
      builder.put((ModelFactory)createDyedShulkerBox(DyeColor.BLACK), Blocks.BLACK_SHULKER_BOX);
      createSigns(builder, WoodType.OAK, Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
      createSigns(builder, WoodType.SPRUCE, Blocks.SPRUCE_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN);
      createSigns(builder, WoodType.BIRCH, Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN);
      createSigns(builder, WoodType.ACACIA, Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN);
      createSigns(builder, WoodType.CHERRY, Blocks.CHERRY_SIGN, Blocks.CHERRY_WALL_SIGN, Blocks.CHERRY_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN);
      createSigns(builder, WoodType.JUNGLE, Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN);
      createSigns(builder, WoodType.DARK_OAK, Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_WALL_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN);
      createSigns(builder, WoodType.PALE_OAK, Blocks.PALE_OAK_SIGN, Blocks.PALE_OAK_WALL_SIGN, Blocks.PALE_OAK_HANGING_SIGN, Blocks.PALE_OAK_WALL_HANGING_SIGN);
      createSigns(builder, WoodType.MANGROVE, Blocks.MANGROVE_SIGN, Blocks.MANGROVE_WALL_SIGN, Blocks.MANGROVE_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN);
      createSigns(builder, WoodType.BAMBOO, Blocks.BAMBOO_SIGN, Blocks.BAMBOO_WALL_SIGN, Blocks.BAMBOO_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
      createSigns(builder, WoodType.CRIMSON, Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN, Blocks.CRIMSON_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN);
      createSigns(builder, WoodType.WARPED, Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN, Blocks.WARPED_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN);
      builder.put((ModelFactory)createSingletonChest(ChestSpecialRenderer.ENDER_CHEST), Blocks.ENDER_CHEST);
      builder.put((ModelFactory)createXmasChest(ChestSpecialRenderer.REGULAR), Blocks.CHEST);
      builder.put((ModelFactory)createXmasChest(ChestSpecialRenderer.TRAPPED), Blocks.TRAPPED_CHEST);
      builder.put(createChest(ChestSpecialRenderer.COPPER_UNAFFECTED), Blocks.COPPER_CHEST, Blocks.WAXED_COPPER_CHEST);
      builder.put(createChest(ChestSpecialRenderer.COPPER_EXPOSED), Blocks.EXPOSED_COPPER_CHEST, Blocks.WAXED_EXPOSED_COPPER_CHEST);
      builder.put(createChest(ChestSpecialRenderer.COPPER_WEATHERED), Blocks.WEATHERED_COPPER_CHEST, Blocks.WAXED_WEATHERED_COPPER_CHEST);
      builder.put(createChest(ChestSpecialRenderer.COPPER_OXIDIZED), Blocks.OXIDIZED_COPPER_CHEST, Blocks.WAXED_OXIDIZED_COPPER_CHEST);
      builder.put(createCopperGolem(WeatheringCopper.WeatherState.UNAFFECTED), Blocks.COPPER_GOLEM_STATUE, Blocks.WAXED_COPPER_GOLEM_STATUE);
      builder.put(createCopperGolem(WeatheringCopper.WeatherState.EXPOSED), Blocks.EXPOSED_COPPER_GOLEM_STATUE, Blocks.WAXED_EXPOSED_COPPER_GOLEM_STATUE);
      builder.put(createCopperGolem(WeatheringCopper.WeatherState.WEATHERED), Blocks.WEATHERED_COPPER_GOLEM_STATUE, Blocks.WAXED_WEATHERED_COPPER_GOLEM_STATUE);
      builder.put(createCopperGolem(WeatheringCopper.WeatherState.OXIDIZED), Blocks.OXIDIZED_COPPER_GOLEM_STATUE, Blocks.WAXED_OXIDIZED_COPPER_GOLEM_STATUE);
      builder.put(special(new BellSpecialRenderer.Unbaked()), Blocks.BELL);
      builder.put(special(new ConduitSpecialRenderer.Unbaked(), ConduitRenderer.DEFAULT_TRANSFORMATION), Blocks.CONDUIT);
      builder.put((ModelFactory)createDecoratedPot(), Blocks.DECORATED_POT);
      builder.put(createEnchantingTable(), Blocks.ENCHANTING_TABLE);
      builder.put(special(new EndCubeSpecialRenderer.Unbaked(EndCubeSpecialRenderer.Type.GATEWAY)), Blocks.END_GATEWAY);
      builder.put(special(new EndCubeSpecialRenderer.Unbaked(EndCubeSpecialRenderer.Type.PORTAL), TheEndPortalRenderer.TRANSFORMATION), Blocks.END_PORTAL);
      builder.put(BuiltInBlockModels::createFlowerBedModel, Blocks.WILDFLOWERS, Blocks.PINK_PETALS);
   }

   private static void createAir(final Builder builder, final Block block) {
      builder.put((BlockModel.Unbaked)(new EmptyBlockModel.Unbaked()), block);
   }

   private static BlockModel.Unbaked special(final SpecialModelRenderer.Unbaked<?> model) {
      return new SpecialBlockModelWrapper.Unbaked(model, Optional.empty());
   }

   private static BlockModel.Unbaked special(final SpecialModelRenderer.Unbaked<?> model, final Transformation transformation) {
      return new SpecialBlockModelWrapper.Unbaked(model, Optional.of(transformation));
   }

   private static SpecialModelFactory createMobHead(final SkullBlock.Types type) {
      return specialModelWithPropertyDispatch(SkullBlock.ROTATION, (rotation) -> special(new SkullSpecialRenderer.Unbaked(type), SkullBlockRenderer.TRANSFORMATIONS.freeTransformations(rotation)));
   }

   private static SpecialModelFactory createMobWallHead(final SkullBlock.Types type) {
      return specialModelWithPropertyDispatch(WallSkullBlock.FACING, (facing) -> special(new SkullSpecialRenderer.Unbaked(type), SkullBlockRenderer.TRANSFORMATIONS.wallTransformation(facing)));
   }

   private static void createMobHeads(final Builder builder, final SkullBlock.Types type, final Block ground, final Block wall) {
      builder.put((ModelFactory)createMobHead(type), ground);
      builder.put((ModelFactory)createMobWallHead(type), wall);
   }

   private static SpecialModelFactory createPlayerHead() {
      return specialModelWithPropertyDispatch(PlayerHeadBlock.ROTATION, (rotation) -> special(new PlayerHeadSpecialRenderer.Unbaked(), SkullBlockRenderer.TRANSFORMATIONS.freeTransformations(rotation)));
   }

   private static SpecialModelFactory createPlayerWallHead() {
      return specialModelWithPropertyDispatch(PlayerWallHeadBlock.FACING, (facing) -> special(new PlayerHeadSpecialRenderer.Unbaked(), SkullBlockRenderer.TRANSFORMATIONS.wallTransformation(facing)));
   }

   private static SpecialModelFactory createBanner(final DyeColor color) {
      return specialModelWithPropertyDispatch(BannerBlock.ROTATION, (rotation) -> special(new BannerSpecialRenderer.Unbaked(color, BannerBlock.AttachmentType.GROUND), BannerRenderer.TRANSFORMATIONS.freeTransformations(rotation)));
   }

   private static SpecialModelFactory createWallBanner(final DyeColor color) {
      return specialModelWithPropertyDispatch(WallBannerBlock.FACING, (facing) -> special(new BannerSpecialRenderer.Unbaked(color, BannerBlock.AttachmentType.WALL), BannerRenderer.TRANSFORMATIONS.wallTransformation(facing)));
   }

   private static void createBanners(final Builder builder, final DyeColor dye, final Block ground, final Block wall) {
      builder.put((ModelFactory)createBanner(dye), ground);
      builder.put((ModelFactory)createWallBanner(dye), wall);
   }

   private static SpecialModelFactory createBed(final DyeColor color) {
      return specialModelWithPropertyDispatch(BedBlock.FACING, BedBlock.PART, (facing, part) -> special(new BedSpecialRenderer.Unbaked(color, part), BedRenderer.modelTransform(facing)));
   }

   private static SpecialModelFactory createShulkerBox() {
      return specialModelWithPropertyDispatch(ShulkerBoxBlock.FACING, (facing) -> special(new ShulkerBoxSpecialRenderer.Unbaked(), ShulkerBoxRenderer.modelTransform(facing)));
   }

   private static SpecialModelFactory createDyedShulkerBox(final DyeColor color) {
      return specialModelWithPropertyDispatch(ShulkerBoxBlock.FACING, (facing) -> special(new ShulkerBoxSpecialRenderer.Unbaked(color), ShulkerBoxRenderer.modelTransform(facing)));
   }

   private static SpecialModelFactory createStandingSign(final WoodType type) {
      return specialModelWithPropertyDispatch(StandingSignBlock.ROTATION, (rotation) -> special(new StandingSignSpecialRenderer.Unbaked(type, PlainSignBlock.Attachment.GROUND), ((SignRenderState.SignTransformations)StandingSignRenderer.TRANSFORMATIONS.freeTransformations(rotation)).body()));
   }

   private static SpecialModelFactory createWallSign(final WoodType type) {
      return specialModelWithPropertyDispatch(WallSignBlock.FACING, (facing) -> special(new StandingSignSpecialRenderer.Unbaked(type, PlainSignBlock.Attachment.WALL), ((SignRenderState.SignTransformations)StandingSignRenderer.TRANSFORMATIONS.wallTransformation(facing)).body()));
   }

   private static SpecialModelFactory createCeilingHangingSign(final WoodType type) {
      return specialModelWithPropertyDispatch(CeilingHangingSignBlock.ROTATION, CeilingHangingSignBlock.ATTACHED, (rotation, attached) -> special(new HangingSignSpecialRenderer.Unbaked(type, CeilingHangingSignBlock.getAttachmentPoint(attached)), ((SignRenderState.SignTransformations)HangingSignRenderer.TRANSFORMATIONS.freeTransformations(rotation)).body()));
   }

   private static SpecialModelFactory createWallHangingSign(final WoodType type) {
      return specialModelWithPropertyDispatch(WallHangingSignBlock.FACING, (facing) -> special(new HangingSignSpecialRenderer.Unbaked(type, HangingSignBlock.Attachment.WALL), ((SignRenderState.SignTransformations)HangingSignRenderer.TRANSFORMATIONS.wallTransformation(facing)).body()));
   }

   private static void createSigns(final Builder builder, final WoodType woodType, final Block standing, final Block wall, final Block hanging, final Block wallHanging) {
      builder.put((ModelFactory)createStandingSign(woodType), standing);
      builder.put((ModelFactory)createWallSign(woodType), wall);
      builder.put((ModelFactory)createCeilingHangingSign(woodType), hanging);
      builder.put((ModelFactory)createWallHangingSign(woodType), wallHanging);
   }

   private static BlockModel.Unbaked createChest(final Identifier texture, final ChestType chestType, final Direction facing) {
      return special(new ChestSpecialRenderer.Unbaked(texture, chestType), ChestRenderer.modelTransformation(facing));
   }

   private static SpecialModelFactory createSingletonChest(final Identifier texture) {
      return specialModelWithPropertyDispatch(ChestBlock.FACING, (facing) -> createChest(texture, ChestType.SINGLE, facing));
   }

   private static SpecialModelFactory createChest(final MultiblockChestResources<Identifier> textures) {
      return specialModelWithPropertyDispatch(ChestBlock.FACING, ChestBlock.TYPE, (facing, type) -> createChest(textures.select(type), type, facing));
   }

   private static SpecialModelFactory createXmasChest(final MultiblockChestResources<Identifier> textures) {
      return specialModelWithPropertyDispatch(ChestBlock.FACING, ChestBlock.TYPE, (facing, type) -> new ConditionalBlockModel.Unbaked(Optional.empty(), new IsXmas(), createChest(ChestSpecialRenderer.CHRISTMAS.select(type), type, facing), createChest(textures.select(type), type, facing)));
   }

   private static SpecialModelFactory createCopperGolem(final WeatheringCopper.WeatherState weatherState) {
      return specialModelWithPropertyDispatch(CopperGolemStatueBlock.FACING, CopperGolemStatueBlock.POSE, (facing, pose) -> special(new CopperGolemStatueSpecialRenderer.Unbaked(weatherState, pose), CopperGolemStatueBlockRenderer.modelTransformation(facing)));
   }

   private static SpecialModelFactory createDecoratedPot() {
      return specialModelWithPropertyDispatch(DecoratedPotBlock.HORIZONTAL_FACING, (facing) -> special(new DecoratedPotSpecialRenderer.Unbaked(), DecoratedPotRenderer.modelTransformation(facing)));
   }

   private static BlockStateModelWrapper.Unbaked createBlockStateModelWrapper(final BlockColors blockColors, final BlockState blockState) {
      return new BlockStateModelWrapper.Unbaked(blockState, blockColors.getTintSources(blockState), Optional.empty());
   }

   private static CompositeBlockModel.Unbaked combineSpecialAndBlockModels(final BlockModel.Unbaked specialModel, final BlockColors blockColors, final BlockState blockState) {
      return new CompositeBlockModel.Unbaked(createBlockStateModelWrapper(blockColors, blockState), specialModel, Optional.empty());
   }

   private static SelectBlockModel.Unbaked createFlowerBedModel(final BlockColors blockColors, final BlockState blockState) {
      List<BlockTintSource> tintSources = blockColors.getTintSources(blockState);
      Transformation customFlowerTransform = new Transformation(new Vector3f(0.25F, 0.0F, 0.25F), (Quaternionfc)null, (Vector3fc)null, (Quaternionfc)null);
      BlockStateModelWrapper.Unbaked customTransformModel = new BlockStateModelWrapper.Unbaked(blockState, tintSources, Optional.of(customFlowerTransform));
      BlockStateModelWrapper.Unbaked normalTransformModel = new BlockStateModelWrapper.Unbaked(blockState, tintSources, Optional.empty());
      return new SelectBlockModel.Unbaked(Optional.empty(), new SelectBlockModel.UnbakedSwitch(new DisplayContext(), List.of(new SelectBlockModel.SwitchCase(List.of(CopperGolemRenderer.BLOCK_DISPLAY_CONTEXT), customTransformModel))), Optional.of(normalTransformModel));
   }

   private static BlockModel.Unbaked createEnchantingTable() {
      return special(new BookSpecialRenderer.Unbaked(0.0F, 0.0F, 0.0F), new Transformation(new Vector3f(0.5F, 0.8125F, 0.5F), Axis.ZP.rotationDegrees(180.0F), (Vector3fc)null, Axis.XP.rotationDegrees(90.0F)));
   }

   private static <P extends Comparable<P>> SpecialModelFactory specialModelWithPropertyDispatch(final Property<P> property, final Function<P, BlockModel.Unbaked> blockModel) {
      return (state) -> {
         P value = (P)state.getValue(property);
         return (BlockModel.Unbaked)blockModel.apply(value);
      };
   }

   private static <P1 extends Comparable<P1>, P2 extends Comparable<P2>> SpecialModelFactory specialModelWithPropertyDispatch(final Property<P1> property1, final Property<P2> property2, final BiFunction<P1, P2, BlockModel.Unbaked> blockModel) {
      return (state) -> {
         P1 value1 = (P1)state.getValue(property1);
         P2 value2 = (P2)state.getValue(property2);
         return (BlockModel.Unbaked)blockModel.apply(value1, value2);
      };
   }

   public static Map<BlockState, BlockModel.Unbaked> createBlockModels(final BlockColors blockColors) {
      Builder builder = new Builder(blockColors);
      addDefaults(builder);
      return builder.build();
   }

   private static class Builder {
      private final BlockColors blockColors;
      private final Map<BlockState, BlockModel.Unbaked> result = new HashMap();

      private Builder(final BlockColors blockColors) {
         super();
         this.blockColors = blockColors;
      }

      private void put(final ModelFactory factory, final Block a, final Block b) {
         this.put(factory, a);
         this.put(factory, b);
      }

      private void put(final BlockModel.Unbaked specialModel, final Block block) {
         this.put((ModelFactory)((var1) -> specialModel), block);
      }

      private void put(final ModelFactory factory, final Block block) {
         UnmodifiableIterator var3 = block.getStateDefinition().getPossibleStates().iterator();

         while(var3.hasNext()) {
            BlockState blockState = (BlockState)var3.next();
            this.result.put(blockState, factory.create(this.blockColors, blockState));
         }

      }

      public Map<BlockState, BlockModel.Unbaked> build() {
         return Map.copyOf(this.result);
      }
   }

   @FunctionalInterface
   private interface SpecialModelFactory extends ModelFactory {
      default BlockModel.Unbaked create(final BlockColors colors, final BlockState state) {
         return BuiltInBlockModels.combineSpecialAndBlockModels(this.createSpecial(state), colors, state);
      }

      BlockModel.Unbaked createSpecial(BlockState state);
   }

   @FunctionalInterface
   private interface ModelFactory {
      BlockModel.Unbaked create(BlockColors colors, BlockState state);
   }
}
