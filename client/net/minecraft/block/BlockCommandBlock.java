package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockCommandBlock extends BlockContainer {
   private static final Logger field_193388_c = LogManager.getLogger();
   public static final DirectionProperty field_185564_a;
   public static final BooleanProperty field_185565_b;

   public BlockCommandBlock(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_185564_a, EnumFacing.NORTH)).func_206870_a(field_185565_b, false));
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      TileEntityCommandBlock var2 = new TileEntityCommandBlock();
      var2.func_184253_b(this == Blocks.field_185777_dd);
      return var2;
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (!var2.field_72995_K) {
         TileEntity var6 = var2.func_175625_s(var3);
         if (var6 instanceof TileEntityCommandBlock) {
            TileEntityCommandBlock var7 = (TileEntityCommandBlock)var6;
            boolean var8 = var2.func_175640_z(var3);
            boolean var9 = var7.func_184255_d();
            var7.func_184250_a(var8);
            if (!var9 && !var7.func_184254_e() && var7.func_184251_i() != TileEntityCommandBlock.Mode.SEQUENCE) {
               if (var8) {
                  var7.func_184249_c();
                  var2.func_205220_G_().func_205360_a(var3, this, this.func_149738_a(var2));
               }

            }
         }
      }
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (!var2.field_72995_K) {
         TileEntity var5 = var2.func_175625_s(var3);
         if (var5 instanceof TileEntityCommandBlock) {
            TileEntityCommandBlock var6 = (TileEntityCommandBlock)var5;
            CommandBlockBaseLogic var7 = var6.func_145993_a();
            boolean var8 = !StringUtils.func_151246_b(var7.func_145753_i());
            TileEntityCommandBlock.Mode var9 = var6.func_184251_i();
            boolean var10 = var6.func_184256_g();
            if (var9 == TileEntityCommandBlock.Mode.AUTO) {
               var6.func_184249_c();
               if (var10) {
                  this.func_193387_a(var1, var2, var3, var7, var8);
               } else if (var6.func_184258_j()) {
                  var7.func_184167_a(0);
               }

               if (var6.func_184255_d() || var6.func_184254_e()) {
                  var2.func_205220_G_().func_205360_a(var3, this, this.func_149738_a(var2));
               }
            } else if (var9 == TileEntityCommandBlock.Mode.REDSTONE) {
               if (var10) {
                  this.func_193387_a(var1, var2, var3, var7, var8);
               } else if (var6.func_184258_j()) {
                  var7.func_184167_a(0);
               }
            }

            var2.func_175666_e(var3, this);
         }

      }
   }

   private void func_193387_a(IBlockState var1, World var2, BlockPos var3, CommandBlockBaseLogic var4, boolean var5) {
      if (var5) {
         var4.func_145755_a(var2);
      } else {
         var4.func_184167_a(0);
      }

      func_193386_c(var2, var3, (EnumFacing)var1.func_177229_b(field_185564_a));
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 1;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      TileEntity var10 = var2.func_175625_s(var3);
      if (var10 instanceof TileEntityCommandBlock && var4.func_195070_dx()) {
         var4.func_184824_a((TileEntityCommandBlock)var10);
         return true;
      } else {
         return false;
      }
   }

   public boolean func_149740_M(IBlockState var1) {
      return true;
   }

   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      TileEntity var4 = var2.func_175625_s(var3);
      return var4 instanceof TileEntityCommandBlock ? ((TileEntityCommandBlock)var4).func_145993_a().func_145760_g() : 0;
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      TileEntity var6 = var1.func_175625_s(var2);
      if (var6 instanceof TileEntityCommandBlock) {
         TileEntityCommandBlock var7 = (TileEntityCommandBlock)var6;
         CommandBlockBaseLogic var8 = var7.func_145993_a();
         if (var5.func_82837_s()) {
            var8.func_207405_b(var5.func_200301_q());
         }

         if (!var1.field_72995_K) {
            if (var5.func_179543_a("BlockEntityTag") == null) {
               var8.func_175573_a(var1.func_82736_K().func_82766_b("sendCommandFeedback"));
               var7.func_184253_b(this == Blocks.field_185777_dd);
            }

            if (var7.func_184251_i() == TileEntityCommandBlock.Mode.SEQUENCE) {
               boolean var9 = var1.func_175640_z(var2);
               var7.func_184250_a(var9);
            }
         }

      }
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.MODEL;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_185564_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_185564_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_185564_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_185564_a, field_185565_b);
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_185564_a, var1.func_196010_d().func_176734_d());
   }

   private static void func_193386_c(World var0, BlockPos var1, EnumFacing var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos(var1);
      GameRules var4 = var0.func_82736_K();

      int var5;
      IBlockState var6;
      for(var5 = var4.func_180263_c("maxCommandChainLength"); var5-- > 0; var2 = (EnumFacing)var6.func_177229_b(field_185564_a)) {
         var3.func_189536_c(var2);
         var6 = var0.func_180495_p(var3);
         Block var7 = var6.func_177230_c();
         if (var7 != Blocks.field_185777_dd) {
            break;
         }

         TileEntity var8 = var0.func_175625_s(var3);
         if (!(var8 instanceof TileEntityCommandBlock)) {
            break;
         }

         TileEntityCommandBlock var9 = (TileEntityCommandBlock)var8;
         if (var9.func_184251_i() != TileEntityCommandBlock.Mode.SEQUENCE) {
            break;
         }

         if (var9.func_184255_d() || var9.func_184254_e()) {
            CommandBlockBaseLogic var10 = var9.func_145993_a();
            if (var9.func_184249_c()) {
               if (!var10.func_145755_a(var0)) {
                  break;
               }

               var0.func_175666_e(var3, var7);
            } else if (var9.func_184258_j()) {
               var10.func_184167_a(0);
            }
         }
      }

      if (var5 <= 0) {
         int var11 = Math.max(var4.func_180263_c("maxCommandChainLength"), 0);
         field_193388_c.warn("Command Block chain tried to execute more than {} steps!", var11);
      }

   }

   static {
      field_185564_a = BlockDirectional.field_176387_N;
      field_185565_b = BlockStateProperties.field_208176_c;
   }
}
