package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockAnvil extends BlockFalling {
   private static final Logger field_185762_e = LogManager.getLogger();
   public static final DirectionProperty field_176506_a;
   private static final VoxelShape field_196436_c;
   private static final VoxelShape field_196439_y;
   private static final VoxelShape field_196440_z;
   private static final VoxelShape field_196434_A;
   private static final VoxelShape field_196435_B;
   private static final VoxelShape field_196437_C;
   private static final VoxelShape field_196438_D;
   private static final VoxelShape field_185760_c;
   private static final VoxelShape field_185761_d;

   public BlockAnvil(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176506_a, EnumFacing.NORTH));
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_176506_a, var1.func_195992_f().func_176746_e());
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (!var2.field_72995_K) {
         var4.func_180468_a(new BlockAnvil.Anvil(var2, var3));
      }

      return true;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      EnumFacing var4 = (EnumFacing)var1.func_177229_b(field_176506_a);
      return var4.func_176740_k() == EnumFacing.Axis.X ? field_185760_c : field_185761_d;
   }

   protected void func_149829_a(EntityFallingBlock var1) {
      var1.func_145806_a(true);
   }

   public void func_176502_a_(World var1, BlockPos var2, IBlockState var3, IBlockState var4) {
      var1.func_175718_b(1031, var2, 0);
   }

   public void func_190974_b(World var1, BlockPos var2) {
      var1.func_175718_b(1029, var2, 0);
   }

   @Nullable
   public static IBlockState func_196433_f(IBlockState var0) {
      Block var1 = var0.func_177230_c();
      if (var1 == Blocks.field_150467_bQ) {
         return (IBlockState)Blocks.field_196717_eY.func_176223_P().func_206870_a(field_176506_a, var0.func_177229_b(field_176506_a));
      } else {
         return var1 == Blocks.field_196717_eY ? (IBlockState)Blocks.field_196718_eZ.func_176223_P().func_206870_a(field_176506_a, var0.func_177229_b(field_176506_a)) : null;
      }
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176506_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176506_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176506_a);
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176506_a = BlockHorizontal.field_185512_D;
      field_196436_c = Block.func_208617_a(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);
      field_196439_y = Block.func_208617_a(3.0D, 4.0D, 4.0D, 13.0D, 5.0D, 12.0D);
      field_196440_z = Block.func_208617_a(4.0D, 5.0D, 6.0D, 12.0D, 10.0D, 10.0D);
      field_196434_A = Block.func_208617_a(0.0D, 10.0D, 3.0D, 16.0D, 16.0D, 13.0D);
      field_196435_B = Block.func_208617_a(4.0D, 4.0D, 3.0D, 12.0D, 5.0D, 13.0D);
      field_196437_C = Block.func_208617_a(6.0D, 5.0D, 4.0D, 10.0D, 10.0D, 12.0D);
      field_196438_D = Block.func_208617_a(3.0D, 10.0D, 0.0D, 13.0D, 16.0D, 16.0D);
      field_185760_c = VoxelShapes.func_197872_a(field_196436_c, VoxelShapes.func_197872_a(field_196439_y, VoxelShapes.func_197872_a(field_196440_z, field_196434_A)));
      field_185761_d = VoxelShapes.func_197872_a(field_196436_c, VoxelShapes.func_197872_a(field_196435_B, VoxelShapes.func_197872_a(field_196437_C, field_196438_D)));
   }

   public static class Anvil implements IInteractionObject {
      private final World field_175130_a;
      private final BlockPos field_175129_b;

      public Anvil(World var1, BlockPos var2) {
         super();
         this.field_175130_a = var1;
         this.field_175129_b = var2;
      }

      public ITextComponent func_200200_C_() {
         return new TextComponentTranslation(Blocks.field_150467_bQ.func_149739_a(), new Object[0]);
      }

      public boolean func_145818_k_() {
         return false;
      }

      @Nullable
      public ITextComponent func_200201_e() {
         return null;
      }

      public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
         return new ContainerRepair(var1, this.field_175130_a, this.field_175129_b, var2);
      }

      public String func_174875_k() {
         return "minecraft:anvil";
      }
   }
}
