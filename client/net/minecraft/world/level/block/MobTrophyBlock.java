package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobTrophyInfo;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.MobTrophyBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MobTrophyBlock extends BaseEntityBlock {
   private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Block.column(12.0, 0.0, 16.0));
   public static final MapCodec<MobTrophyBlock> CODEC = simpleCodec(MobTrophyBlock::new);
   public static final EnumProperty<Direction> FACING;
   public static final EnumProperty<Grade> GRADE;

   public MapCodec<MobTrophyBlock> codec() {
      return CODEC;
   }

   public MobTrophyBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(GRADE, MobTrophyBlock.Grade.GRASS));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      super.createBlockStateDefinition(var1);
      var1.add(FACING);
      var1.add(GRADE);
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new MobTrophyBlockEntity(var1, var2);
   }

   protected ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3, boolean var4) {
      ItemStack var5 = super.getCloneItemStack(var1, var2, var3, var4);
      var5.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(GRADE, (Grade)var3.getValue(GRADE)));
      return var5;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)SHAPES.get(var1.getValue(FACING));
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return (BlockState)var1.setValue(FACING, var2.mirror((Direction)var1.getValue(FACING)));
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (var2 instanceof ServerLevel var6) {
         var6.getBlockEntity(var3, BlockEntityType.MOB_TROPHY).ifPresent((var3x) -> {
            MobTrophyInfo var4 = var3x.getEntityType();
            if (var4 != null) {
               var4.type().unwrapKey().ifPresent((var3xx) -> {
                  String var4 = "entity." + var3xx.location().getPath() + ".";
                  List var5 = var6.registryAccess().lookupOrThrow(Registries.SOUND_EVENT).listElements().filter((var1) -> var1.key().location().getPath().startsWith(var4)).toList();
                  Util.getRandomSafe(var5, var2.random).ifPresent((var2x) -> var2.playSound((Entity)null, var3, (SoundEvent)var2x.value(), SoundSource.BLOCKS));
               });
            }

         });
      }

      return InteractionResult.PASS;
   }

   public static ItemStack generateForMob(Holder<EntityType<?>> var0, RandomSource var1) {
      Grade var2 = (Grade)MobTrophyBlock.Grade.GRADES.getRandom(var1).orElseThrow();
      boolean var3 = (double)var1.nextFloat() < 0.01;
      ItemStack var4 = new ItemStack(Items.MOB_TROPHY);
      var4.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(GRADE, var2));
      var4.set(DataComponents.MOB_TROPHY_TYPE, new MobTrophyInfo(var0, var3));
      return var4;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      GRADE = EnumProperty.<Grade>create("grade", Grade.class);
   }

   public static enum Grade implements StringRepresentable {
      GRASS("grass", 5295618),
      STONE("stone", 4673362),
      GOLD("gold", 14594349),
      DIAMOND("diamond", 7269586),
      NETHERITE("netherite", 6445145);

      public static final WeightedList<Grade> GRADES = WeightedList.of(new Weighted(GRASS, 100), new Weighted(STONE, 50), new Weighted(GOLD, 25), new Weighted(DIAMOND, 5), new Weighted(NETHERITE, 1));
      private final String name;
      private final int color;

      private Grade(final String var3, final int var4) {
         this.name = var3;
         this.color = var4;
      }

      public String getSerializedName() {
         return this.name;
      }

      public int color() {
         return this.color;
      }

      public String translationId() {
         return "item.minecraft.mob_trophy.grade." + this.name;
      }

      // $FF: synthetic method
      private static Grade[] $values() {
         return new Grade[]{GRASS, STONE, GOLD, DIAMOND, NETHERITE};
      }
   }
}
