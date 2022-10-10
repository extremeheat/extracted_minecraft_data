package net.minecraft.init;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarvedPumpkin;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSkullWither;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.EntityOptions;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBoneMeal;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionType;
import net.minecraft.server.DebugLoggingPrintStream;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LoggingPrintStream;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   public static final PrintStream field_179872_a;
   private static boolean field_151355_a;
   private static final Logger field_179871_c;

   public static boolean func_179869_a() {
      return field_151355_a;
   }

   static void func_151353_a() {
      BlockDispenser.func_199774_a(Items.field_151032_g, new BehaviorProjectileDispense() {
         protected IProjectile func_82499_a(World var1, IPosition var2, ItemStack var3) {
            EntityTippedArrow var4 = new EntityTippedArrow(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
            var4.field_70251_a = EntityArrow.PickupStatus.ALLOWED;
            return var4;
         }
      });
      BlockDispenser.func_199774_a(Items.field_185167_i, new BehaviorProjectileDispense() {
         protected IProjectile func_82499_a(World var1, IPosition var2, ItemStack var3) {
            EntityTippedArrow var4 = new EntityTippedArrow(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
            var4.func_184555_a(var3);
            var4.field_70251_a = EntityArrow.PickupStatus.ALLOWED;
            return var4;
         }
      });
      BlockDispenser.func_199774_a(Items.field_185166_h, new BehaviorProjectileDispense() {
         protected IProjectile func_82499_a(World var1, IPosition var2, ItemStack var3) {
            EntitySpectralArrow var4 = new EntitySpectralArrow(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
            var4.field_70251_a = EntityArrow.PickupStatus.ALLOWED;
            return var4;
         }
      });
      BlockDispenser.func_199774_a(Items.field_151110_aK, new BehaviorProjectileDispense() {
         protected IProjectile func_82499_a(World var1, IPosition var2, ItemStack var3) {
            return new EntityEgg(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
         }
      });
      BlockDispenser.func_199774_a(Items.field_151126_ay, new BehaviorProjectileDispense() {
         protected IProjectile func_82499_a(World var1, IPosition var2, ItemStack var3) {
            return new EntitySnowball(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
         }
      });
      BlockDispenser.func_199774_a(Items.field_151062_by, new BehaviorProjectileDispense() {
         protected IProjectile func_82499_a(World var1, IPosition var2, ItemStack var3) {
            return new EntityExpBottle(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
         }

         protected float func_82498_a() {
            return super.func_82498_a() * 0.5F;
         }

         protected float func_82500_b() {
            return super.func_82500_b() * 1.25F;
         }
      });
      BlockDispenser.func_199774_a(Items.field_185155_bH, new IBehaviorDispenseItem() {
         public ItemStack dispense(IBlockSource var1, final ItemStack var2) {
            return (new BehaviorProjectileDispense() {
               protected IProjectile func_82499_a(World var1, IPosition var2x, ItemStack var3) {
                  return new EntityPotion(var1, var2x.func_82615_a(), var2x.func_82617_b(), var2x.func_82616_c(), var2.func_77946_l());
               }

               protected float func_82498_a() {
                  return super.func_82498_a() * 0.5F;
               }

               protected float func_82500_b() {
                  return super.func_82500_b() * 1.25F;
               }
            }).dispense(var1, var2);
         }
      });
      BlockDispenser.func_199774_a(Items.field_185156_bI, new IBehaviorDispenseItem() {
         public ItemStack dispense(IBlockSource var1, final ItemStack var2) {
            return (new BehaviorProjectileDispense() {
               protected IProjectile func_82499_a(World var1, IPosition var2x, ItemStack var3) {
                  return new EntityPotion(var1, var2x.func_82615_a(), var2x.func_82617_b(), var2x.func_82616_c(), var2.func_77946_l());
               }

               protected float func_82498_a() {
                  return super.func_82498_a() * 0.5F;
               }

               protected float func_82500_b() {
                  return super.func_82500_b() * 1.25F;
               }
            }).dispense(var1, var2);
         }
      });
      BehaviorDefaultDispenseItem var0 = new BehaviorDefaultDispenseItem() {
         public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            EnumFacing var3 = (EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a);
            EntityType var4 = ((ItemSpawnEgg)var2.func_77973_b()).func_208076_b(var2.func_77978_p());
            if (var4 != null) {
               var4.func_208049_a(var1.func_197524_h(), var2, (EntityPlayer)null, var1.func_180699_d().func_177972_a(var3), var3 != EnumFacing.UP, false);
            }

            var2.func_190918_g(1);
            return var2;
         }
      };
      Iterator var1 = ItemSpawnEgg.func_195985_g().iterator();

      while(var1.hasNext()) {
         ItemSpawnEgg var2 = (ItemSpawnEgg)var1.next();
         BlockDispenser.func_199774_a(var2, var0);
      }

      BlockDispenser.func_199774_a(Items.field_196152_dE, new BehaviorDefaultDispenseItem() {
         public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            EnumFacing var3 = (EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a);
            double var4 = var1.func_82615_a() + (double)var3.func_82601_c();
            double var6 = (double)((float)var1.func_180699_d().func_177956_o() + 0.2F);
            double var8 = var1.func_82616_c() + (double)var3.func_82599_e();
            EntityFireworkRocket var10 = new EntityFireworkRocket(var1.func_197524_h(), var4, var6, var8, var2);
            var1.func_197524_h().func_72838_d(var10);
            var2.func_190918_g(1);
            return var2;
         }

         protected void func_82485_a(IBlockSource var1) {
            var1.func_197524_h().func_175718_b(1004, var1.func_180699_d(), 0);
         }
      });
      BlockDispenser.func_199774_a(Items.field_151059_bz, new BehaviorDefaultDispenseItem() {
         public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            EnumFacing var3 = (EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a);
            IPosition var4 = BlockDispenser.func_149939_a(var1);
            double var5 = var4.func_82615_a() + (double)((float)var3.func_82601_c() * 0.3F);
            double var7 = var4.func_82617_b() + (double)((float)var3.func_96559_d() * 0.3F);
            double var9 = var4.func_82616_c() + (double)((float)var3.func_82599_e() * 0.3F);
            World var11 = var1.func_197524_h();
            Random var12 = var11.field_73012_v;
            double var13 = var12.nextGaussian() * 0.05D + (double)var3.func_82601_c();
            double var15 = var12.nextGaussian() * 0.05D + (double)var3.func_96559_d();
            double var17 = var12.nextGaussian() * 0.05D + (double)var3.func_82599_e();
            var11.func_72838_d(new EntitySmallFireball(var11, var5, var7, var9, var13, var15, var17));
            var2.func_190918_g(1);
            return var2;
         }

         protected void func_82485_a(IBlockSource var1) {
            var1.func_197524_h().func_175718_b(1018, var1.func_180699_d(), 0);
         }
      });
      BlockDispenser.func_199774_a(Items.field_151124_az, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.OAK));
      BlockDispenser.func_199774_a(Items.field_185150_aH, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.SPRUCE));
      BlockDispenser.func_199774_a(Items.field_185151_aI, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.BIRCH));
      BlockDispenser.func_199774_a(Items.field_185152_aJ, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.JUNGLE));
      BlockDispenser.func_199774_a(Items.field_185154_aL, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.DARK_OAK));
      BlockDispenser.func_199774_a(Items.field_185153_aK, new Bootstrap.BehaviorDispenseBoat(EntityBoat.Type.ACACIA));
      BehaviorDefaultDispenseItem var7 = new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150841_b = new BehaviorDefaultDispenseItem();

         public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            ItemBucket var3 = (ItemBucket)var2.func_77973_b();
            BlockPos var4 = var1.func_180699_d().func_177972_a((EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a));
            World var5 = var1.func_197524_h();
            if (var3.func_180616_a((EntityPlayer)null, var5, var4, (RayTraceResult)null)) {
               var3.func_203792_a(var5, var2, var4);
               return new ItemStack(Items.field_151133_ar);
            } else {
               return this.field_150841_b.dispense(var1, var2);
            }
         }
      };
      BlockDispenser.func_199774_a(Items.field_151129_at, var7);
      BlockDispenser.func_199774_a(Items.field_151131_as, var7);
      BlockDispenser.func_199774_a(Items.field_203796_aM, var7);
      BlockDispenser.func_199774_a(Items.field_203797_aN, var7);
      BlockDispenser.func_199774_a(Items.field_203795_aL, var7);
      BlockDispenser.func_199774_a(Items.field_204272_aO, var7);
      BlockDispenser.func_199774_a(Items.field_151133_ar, new BehaviorDefaultDispenseItem() {
         private final BehaviorDefaultDispenseItem field_150840_b = new BehaviorDefaultDispenseItem();

         public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            World var3 = var1.func_197524_h();
            BlockPos var4 = var1.func_180699_d().func_177972_a((EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a));
            IBlockState var5 = var3.func_180495_p(var4);
            Block var6 = var5.func_177230_c();
            if (var6 instanceof IBucketPickupHandler) {
               Fluid var8 = ((IBucketPickupHandler)var6).func_204508_a(var3, var4, var5);
               if (!(var8 instanceof FlowingFluid)) {
                  return super.func_82487_b(var1, var2);
               } else {
                  Item var7 = var8.func_204524_b();
                  var2.func_190918_g(1);
                  if (var2.func_190926_b()) {
                     return new ItemStack(var7);
                  } else {
                     if (((TileEntityDispenser)var1.func_150835_j()).func_146019_a(new ItemStack(var7)) < 0) {
                        this.field_150840_b.dispense(var1, new ItemStack(var7));
                     }

                     return var2;
                  }
               }
            } else {
               return super.func_82487_b(var1, var2);
            }
         }
      });
      BlockDispenser.func_199774_a(Items.field_151033_d, new Bootstrap.BehaviorDispenseOptional() {
         protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            World var3 = var1.func_197524_h();
            this.field_190911_b = true;
            BlockPos var4 = var1.func_180699_d().func_177972_a((EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a));
            if (ItemFlintAndSteel.func_201825_a(var3, var4)) {
               var3.func_175656_a(var4, Blocks.field_150480_ab.func_176223_P());
            } else {
               Block var5 = var3.func_180495_p(var4).func_177230_c();
               if (var5 instanceof BlockTNT) {
                  ((BlockTNT)var5).func_196534_a(var3, var4);
                  var3.func_175698_g(var4);
               } else {
                  this.field_190911_b = false;
               }
            }

            if (this.field_190911_b && var2.func_96631_a(1, var3.field_73012_v, (EntityPlayerMP)null)) {
               var2.func_190920_e(0);
            }

            return var2;
         }
      });
      BlockDispenser.func_199774_a(Items.field_196106_bc, new Bootstrap.BehaviorDispenseOptional() {
         protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            this.field_190911_b = true;
            World var3 = var1.func_197524_h();
            BlockPos var4 = var1.func_180699_d().func_177972_a((EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a));
            if (!ItemBoneMeal.func_195966_a(var2, var3, var4) && !ItemBoneMeal.func_203173_b(var2, var3, var4, (EnumFacing)null)) {
               this.field_190911_b = false;
            } else if (!var3.field_72995_K) {
               var3.func_175718_b(2005, var4, 0);
            }

            return var2;
         }
      });
      BlockDispenser.func_199774_a(Blocks.field_150335_W, new BehaviorDefaultDispenseItem() {
         protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            World var3 = var1.func_197524_h();
            BlockPos var4 = var1.func_180699_d().func_177972_a((EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a));
            EntityTNTPrimed var5 = new EntityTNTPrimed(var3, (double)var4.func_177958_n() + 0.5D, (double)var4.func_177956_o(), (double)var4.func_177952_p() + 0.5D, (EntityLivingBase)null);
            var3.func_72838_d(var5);
            var3.func_184148_a((EntityPlayer)null, var5.field_70165_t, var5.field_70163_u, var5.field_70161_v, SoundEvents.field_187904_gd, SoundCategory.BLOCKS, 1.0F, 1.0F);
            var2.func_190918_g(1);
            return var2;
         }
      });
      Bootstrap.BehaviorDispenseOptional var8 = new Bootstrap.BehaviorDispenseOptional() {
         protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            this.field_190911_b = !ItemArmor.func_185082_a(var1, var2).func_190926_b();
            return var2;
         }
      };
      BlockDispenser.func_199774_a(Items.field_196185_dy, var8);
      BlockDispenser.func_199774_a(Items.field_196186_dz, var8);
      BlockDispenser.func_199774_a(Items.field_196151_dA, var8);
      BlockDispenser.func_199774_a(Items.field_196182_dv, var8);
      BlockDispenser.func_199774_a(Items.field_196184_dx, var8);
      BlockDispenser.func_199774_a(Items.field_196183_dw, new Bootstrap.BehaviorDispenseOptional() {
         protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            World var3 = var1.func_197524_h();
            EnumFacing var4 = (EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a);
            BlockPos var5 = var1.func_180699_d().func_177972_a(var4);
            this.field_190911_b = true;
            if (var3.func_175623_d(var5) && BlockSkullWither.func_196299_b(var3, var5, var2)) {
               var3.func_180501_a(var5, (IBlockState)Blocks.field_196705_eO.func_176223_P().func_206870_a(BlockSkull.field_196294_a, var4.func_176740_k() == EnumFacing.Axis.Y ? 0 : var4.func_176734_d().func_176736_b() * 4), 3);
               TileEntity var6 = var3.func_175625_s(var5);
               if (var6 instanceof TileEntitySkull) {
                  BlockSkullWither.func_196298_a(var3, var5, (TileEntitySkull)var6);
               }

               var2.func_190918_g(1);
            } else if (ItemArmor.func_185082_a(var1, var2).func_190926_b()) {
               this.field_190911_b = false;
            }

            return var2;
         }
      });
      BlockDispenser.func_199774_a(Blocks.field_196625_cS, new Bootstrap.BehaviorDispenseOptional() {
         protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
            World var3 = var1.func_197524_h();
            BlockPos var4 = var1.func_180699_d().func_177972_a((EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a));
            BlockCarvedPumpkin var5 = (BlockCarvedPumpkin)Blocks.field_196625_cS;
            this.field_190911_b = true;
            if (var3.func_175623_d(var4) && var5.func_196354_a(var3, var4)) {
               if (!var3.field_72995_K) {
                  var3.func_180501_a(var4, var5.func_176223_P(), 3);
               }

               var2.func_190918_g(1);
            } else {
               ItemStack var6 = ItemArmor.func_185082_a(var1, var2);
               if (var6.func_190926_b()) {
                  this.field_190911_b = false;
               }
            }

            return var2;
         }
      });
      BlockDispenser.func_199774_a(Blocks.field_204409_il.func_199767_j(), new Bootstrap.BehaviorDispenseShulkerBox());
      EnumDyeColor[] var3 = EnumDyeColor.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumDyeColor var6 = var3[var5];
         BlockDispenser.func_199774_a(BlockShulkerBox.func_190952_a(var6).func_199767_j(), new Bootstrap.BehaviorDispenseShulkerBox());
      }

   }

   public static void func_151354_b() {
      if (!field_151355_a) {
         field_151355_a = true;
         SoundEvent.func_187504_b();
         Fluid.func_207195_i();
         Block.func_149671_p();
         BlockFire.func_149843_e();
         Potion.func_188411_k();
         Enchantment.func_185257_f();
         if (EntityType.func_200718_a(EntityType.field_200729_aH) == null) {
            throw new IllegalStateException("Failed loading EntityTypes");
         } else {
            Item.func_150900_l();
            PotionType.func_185175_b();
            PotionBrewing.func_185207_a();
            Biome.func_185358_q();
            EntityOptions.func_197445_a();
            ParticleType.func_197576_c();
            func_151353_a();
            ArgumentTypes.func_197483_a();
            BiomeProviderType.func_212580_a();
            TileEntityType.func_212641_a();
            ChunkGeneratorType.func_212675_a();
            DimensionType.func_212680_a();
            PaintingType.func_200831_a();
            StatList.func_212734_a();
            IRegistry.func_212613_e();
            if (SharedConstants.field_206244_b) {
               func_210839_a("block", IRegistry.field_212618_g, Block::func_149739_a);
               func_210839_a("biome", IRegistry.field_212624_m, Biome::func_210773_k);
               func_210839_a("enchantment", IRegistry.field_212628_q, Enchantment::func_77320_a);
               func_210839_a("item", IRegistry.field_212630_s, Item::func_77658_a);
               func_210839_a("effect", IRegistry.field_212631_t, Potion::func_76393_a);
               func_210839_a("entity", IRegistry.field_212629_r, EntityType::func_210760_d);
            }

            func_179868_d();
         }
      }
   }

   private static <T> void func_210839_a(String var0, IRegistry<T> var1, Function<T, String> var2) {
      LanguageMap var3 = LanguageMap.func_74808_a();
      var1.iterator().forEachRemaining((var4) -> {
         String var5 = (String)var2.apply(var4);
         if (!var3.func_210813_b(var5)) {
            field_179871_c.warn("Missing translation for {}: {} (key: '{}')", var0, var1.func_177774_c(var4), var5);
         }

      });
   }

   private static void func_179868_d() {
      if (field_179871_c.isDebugEnabled()) {
         System.setErr(new DebugLoggingPrintStream("STDERR", System.err));
         System.setOut(new DebugLoggingPrintStream("STDOUT", field_179872_a));
      } else {
         System.setErr(new LoggingPrintStream("STDERR", System.err));
         System.setOut(new LoggingPrintStream("STDOUT", field_179872_a));
      }

   }

   public static void func_179870_a(String var0) {
      field_179872_a.println(var0);
   }

   static {
      field_179872_a = System.out;
      field_179871_c = LogManager.getLogger();
   }

   static class DispensePlaceContext extends BlockItemUseContext {
      private final EnumFacing field_196015_j;

      public DispensePlaceContext(World var1, BlockPos var2, EnumFacing var3, ItemStack var4, EnumFacing var5) {
         super(var1, (EntityPlayer)null, var4, var2, var5, 0.5F, 0.0F, 0.5F);
         this.field_196015_j = var3;
      }

      public BlockPos func_195995_a() {
         return this.field_196008_i;
      }

      public boolean func_196011_b() {
         return this.field_196006_g.func_180495_p(this.field_196008_i).func_196953_a(this);
      }

      public boolean func_196012_c() {
         return this.func_196011_b();
      }

      public EnumFacing func_196010_d() {
         return EnumFacing.DOWN;
      }

      public EnumFacing[] func_196009_e() {
         switch(this.field_196015_j) {
         case DOWN:
         default:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.UP};
         case UP:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
         case NORTH:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.SOUTH};
         case SOUTH:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST, EnumFacing.UP, EnumFacing.NORTH};
         case WEST:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST};
         case EAST:
            return new EnumFacing[]{EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.WEST};
         }
      }

      public EnumFacing func_195992_f() {
         return this.field_196015_j.func_176740_k() == EnumFacing.Axis.Y ? EnumFacing.NORTH : this.field_196015_j;
      }

      public boolean func_195998_g() {
         return false;
      }

      public float func_195990_h() {
         return (float)(this.field_196015_j.func_176736_b() * 90);
      }
   }

   static class BehaviorDispenseShulkerBox extends Bootstrap.BehaviorDispenseOptional {
      private BehaviorDispenseShulkerBox() {
         super();
      }

      protected ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
         this.field_190911_b = false;
         Item var3 = var2.func_77973_b();
         if (var3 instanceof ItemBlock) {
            EnumFacing var4 = (EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a);
            BlockPos var5 = var1.func_180699_d().func_177972_a(var4);
            EnumFacing var6 = var1.func_197524_h().func_175623_d(var5.func_177977_b()) ? var4 : EnumFacing.UP;
            this.field_190911_b = ((ItemBlock)var3).func_195942_a(new Bootstrap.DispensePlaceContext(var1.func_197524_h(), var5, var4, var2, var6)) == EnumActionResult.SUCCESS;
            if (this.field_190911_b) {
               var2.func_190918_g(1);
            }
         }

         return var2;
      }

      // $FF: synthetic method
      BehaviorDispenseShulkerBox(Object var1) {
         this();
      }
   }

   public abstract static class BehaviorDispenseOptional extends BehaviorDefaultDispenseItem {
      protected boolean field_190911_b = true;

      public BehaviorDispenseOptional() {
         super();
      }

      protected void func_82485_a(IBlockSource var1) {
         var1.func_197524_h().func_175718_b(this.field_190911_b ? 1000 : 1001, var1.func_180699_d(), 0);
      }
   }

   public static class BehaviorDispenseBoat extends BehaviorDefaultDispenseItem {
      private final BehaviorDefaultDispenseItem field_185026_b = new BehaviorDefaultDispenseItem();
      private final EntityBoat.Type field_185027_c;

      public BehaviorDispenseBoat(EntityBoat.Type var1) {
         super();
         this.field_185027_c = var1;
      }

      public ItemStack func_82487_b(IBlockSource var1, ItemStack var2) {
         EnumFacing var3 = (EnumFacing)var1.func_189992_e().func_177229_b(BlockDispenser.field_176441_a);
         World var4 = var1.func_197524_h();
         double var5 = var1.func_82615_a() + (double)((float)var3.func_82601_c() * 1.125F);
         double var7 = var1.func_82617_b() + (double)((float)var3.func_96559_d() * 1.125F);
         double var9 = var1.func_82616_c() + (double)((float)var3.func_82599_e() * 1.125F);
         BlockPos var11 = var1.func_180699_d().func_177972_a(var3);
         double var12;
         if (var4.func_204610_c(var11).func_206884_a(FluidTags.field_206959_a)) {
            var12 = 1.0D;
         } else {
            if (!var4.func_180495_p(var11).func_196958_f() || !var4.func_204610_c(var11.func_177977_b()).func_206884_a(FluidTags.field_206959_a)) {
               return this.field_185026_b.dispense(var1, var2);
            }

            var12 = 0.0D;
         }

         EntityBoat var14 = new EntityBoat(var4, var5, var7 + var12, var9);
         var14.func_184458_a(this.field_185027_c);
         var14.field_70177_z = var3.func_185119_l();
         var4.func_72838_d(var14);
         var2.func_190918_g(1);
         return var2;
      }

      protected void func_82485_a(IBlockSource var1) {
         var1.func_197524_h().func_175718_b(1000, var1.func_180699_d(), 0);
      }
   }
}
