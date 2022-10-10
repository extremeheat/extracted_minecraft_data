package net.minecraft.block.state;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IStateHolder;
import net.minecraft.tags.Tag;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public interface IBlockState extends IStateHolder<IBlockState> {
   ThreadLocal<Object2ByteMap<IBlockState>> field_208776_a = ThreadLocal.withInitial(() -> {
      Object2ByteOpenHashMap var0 = new Object2ByteOpenHashMap();
      var0.defaultReturnValue((byte)127);
      return var0;
   });
   ThreadLocal<Object2ByteMap<IBlockState>> field_208777_b = ThreadLocal.withInitial(() -> {
      Object2ByteOpenHashMap var0 = new Object2ByteOpenHashMap();
      var0.defaultReturnValue((byte)127);
      return var0;
   });
   ThreadLocal<Object2ByteMap<IBlockState>> field_209224_c = ThreadLocal.withInitial(() -> {
      Object2ByteOpenHashMap var0 = new Object2ByteOpenHashMap();
      var0.defaultReturnValue((byte)127);
      return var0;
   });

   Block func_177230_c();

   default Material func_185904_a() {
      return this.func_177230_c().func_149688_o(this);
   }

   default boolean func_189884_a(Entity var1) {
      return this.func_177230_c().func_189872_a(this, var1);
   }

   default boolean func_200131_a(IBlockReader var1, BlockPos var2) {
      Block var3 = this.func_177230_c();
      Object2ByteMap var4 = var3.func_208619_r() ? null : (Object2ByteMap)field_208776_a.get();
      if (var4 != null) {
         byte var5 = var4.getByte(this);
         if (var5 != var4.defaultReturnValue()) {
            return var5 != 0;
         }
      }

      boolean var6 = var3.func_200123_i(this, var1, var2);
      if (var4 != null) {
         var4.put(this, (byte)(var6 ? 1 : 0));
      }

      return var6;
   }

   default int func_200016_a(IBlockReader var1, BlockPos var2) {
      Block var3 = this.func_177230_c();
      Object2ByteMap var4 = var3.func_208619_r() ? null : (Object2ByteMap)field_208777_b.get();
      if (var4 != null) {
         byte var5 = var4.getByte(this);
         if (var5 != var4.defaultReturnValue()) {
            return var5;
         }
      }

      int var6 = var3.func_200011_d(this, var1, var2);
      if (var4 != null) {
         var4.put(this, (byte)Math.min(var6, var1.func_201572_C()));
      }

      return var6;
   }

   default int func_185906_d() {
      return this.func_177230_c().func_149750_m(this);
   }

   default boolean func_196958_f() {
      return this.func_177230_c().func_196261_e(this);
   }

   default boolean func_200130_c(IBlockReader var1, BlockPos var2) {
      return this.func_177230_c().func_200125_k(this, var1, var2);
   }

   default MaterialColor func_185909_g(IBlockReader var1, BlockPos var2) {
      return this.func_177230_c().func_180659_g(this, var1, var2);
   }

   default IBlockState func_185907_a(Rotation var1) {
      return this.func_177230_c().func_185499_a(this, var1);
   }

   default IBlockState func_185902_a(Mirror var1) {
      return this.func_177230_c().func_185471_a(this, var1);
   }

   default boolean func_185917_h() {
      return this.func_177230_c().func_149686_d(this);
   }

   default boolean func_191057_i() {
      return this.func_177230_c().func_190946_v(this);
   }

   default EnumBlockRenderType func_185901_i() {
      return this.func_177230_c().func_149645_b(this);
   }

   default int func_185889_a(IWorldReader var1, BlockPos var2) {
      return this.func_177230_c().func_185484_c(this, var1, var2);
   }

   default float func_185892_j() {
      return this.func_177230_c().func_185485_f(this);
   }

   default boolean func_185898_k() {
      return this.func_177230_c().func_149637_q(this);
   }

   default boolean func_185915_l() {
      return this.func_177230_c().func_149721_r(this);
   }

   default boolean func_185897_m() {
      return this.func_177230_c().func_149744_f(this);
   }

   default int func_185911_a(IBlockReader var1, BlockPos var2, EnumFacing var3) {
      return this.func_177230_c().func_180656_a(this, var1, var2, var3);
   }

   default boolean func_185912_n() {
      return this.func_177230_c().func_149740_M(this);
   }

   default int func_185888_a(World var1, BlockPos var2) {
      return this.func_177230_c().func_180641_l(this, var1, var2);
   }

   default float func_185887_b(IBlockReader var1, BlockPos var2) {
      return this.func_177230_c().func_176195_g(this, var1, var2);
   }

   default float func_185903_a(EntityPlayer var1, IBlockReader var2, BlockPos var3) {
      return this.func_177230_c().func_180647_a(this, var1, var2, var3);
   }

   default int func_185893_b(IBlockReader var1, BlockPos var2, EnumFacing var3) {
      return this.func_177230_c().func_176211_b(this, var1, var2, var3);
   }

   default EnumPushReaction func_185905_o() {
      return this.func_177230_c().func_149656_h(this);
   }

   default boolean func_200015_d(IBlockReader var1, BlockPos var2) {
      Block var3 = this.func_177230_c();
      Object2ByteMap var4 = var3.func_208619_r() ? null : (Object2ByteMap)field_209224_c.get();
      if (var4 != null) {
         byte var5 = var4.getByte(this);
         if (var5 != var4.defaultReturnValue()) {
            return var5 != 0;
         }
      }

      boolean var6 = var3.func_200012_i(this, var1, var2);
      if (var4 != null) {
         var4.put(this, (byte)(var6 ? 1 : 0));
      }

      return var6;
   }

   default boolean func_200132_m() {
      return this.func_177230_c().func_200124_e(this);
   }

   default boolean func_200017_a(IBlockState var1, EnumFacing var2) {
      return this.func_177230_c().func_200122_a(this, var1, var2);
   }

   default VoxelShape func_196954_c(IBlockReader var1, BlockPos var2) {
      return this.func_177230_c().func_196244_b(this, var1, var2);
   }

   default VoxelShape func_196952_d(IBlockReader var1, BlockPos var2) {
      return this.func_177230_c().func_196268_f(this, var1, var2);
   }

   default VoxelShape func_196951_e(IBlockReader var1, BlockPos var2) {
      return this.func_177230_c().func_196247_c(this, var1, var2);
   }

   default VoxelShape func_199611_f(IBlockReader var1, BlockPos var2) {
      return this.func_177230_c().func_199600_g(this, var1, var2);
   }

   default boolean func_185896_q() {
      return this.func_177230_c().func_185481_k(this);
   }

   default Vec3d func_191059_e(IBlockReader var1, BlockPos var2) {
      return this.func_177230_c().func_190949_e(this, var1, var2);
   }

   default boolean func_189547_a(World var1, BlockPos var2, int var3, int var4) {
      return this.func_177230_c().func_189539_a(this, var1, var2, var3, var4);
   }

   default void func_189546_a(World var1, BlockPos var2, Block var3, BlockPos var4) {
      this.func_177230_c().func_189540_a(this, var1, var2, var3, var4);
   }

   default void func_196946_a(IWorld var1, BlockPos var2, int var3) {
      this.func_177230_c().func_196242_c(this, var1, var2, var3);
   }

   default void func_196948_b(IWorld var1, BlockPos var2, int var3) {
      this.func_177230_c().func_196248_b(this, var1, var2, var3);
   }

   default void func_196945_a(World var1, BlockPos var2, IBlockState var3) {
      this.func_177230_c().func_196259_b(this, var1, var2, var3);
   }

   default void func_196947_b(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      this.func_177230_c().func_196243_a(this, var1, var2, var3, var4);
   }

   default void func_196940_a(World var1, BlockPos var2, Random var3) {
      this.func_177230_c().func_196267_b(this, var1, var2, var3);
   }

   default void func_196944_b(World var1, BlockPos var2, Random var3) {
      this.func_177230_c().func_196265_a(this, var1, var2, var3);
   }

   default void func_196950_a(World var1, BlockPos var2, Entity var3) {
      this.func_177230_c().func_196262_a(this, var1, var2, var3);
   }

   default void func_196949_c(World var1, BlockPos var2, int var3) {
      this.func_196941_a(var1, var2, 1.0F, var3);
   }

   default void func_196941_a(World var1, BlockPos var2, float var3, int var4) {
      this.func_177230_c().func_196255_a(this, var1, var2, var3, var4);
   }

   default boolean func_196943_a(World var1, BlockPos var2, EntityPlayer var3, EnumHand var4, EnumFacing var5, float var6, float var7, float var8) {
      return this.func_177230_c().func_196250_a(this, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   default void func_196942_a(World var1, BlockPos var2, EntityPlayer var3) {
      this.func_177230_c().func_196270_a(this, var1, var2, var3);
   }

   default boolean func_191058_s() {
      return this.func_177230_c().func_176214_u(this);
   }

   default BlockFaceShape func_193401_d(IBlockReader var1, BlockPos var2, EnumFacing var3) {
      return this.func_177230_c().func_193383_a(var1, this, var2, var3);
   }

   default IBlockState func_196956_a(EnumFacing var1, IBlockState var2, IWorld var3, BlockPos var4, BlockPos var5) {
      return this.func_177230_c().func_196271_a(this, var1, var2, var3, var4, var5);
   }

   default boolean func_196957_g(IBlockReader var1, BlockPos var2, PathType var3) {
      return this.func_177230_c().func_196266_a(this, var1, var2, var3);
   }

   default boolean func_196953_a(BlockItemUseContext var1) {
      return this.func_177230_c().func_196253_a(this, var1);
   }

   default boolean func_196955_c(IWorldReaderBase var1, BlockPos var2) {
      return this.func_177230_c().func_196260_a(this, var1, var2);
   }

   default boolean func_202065_c(IBlockReader var1, BlockPos var2) {
      return this.func_177230_c().func_201783_b(this, var1, var2);
   }

   default boolean func_203425_a(Tag<Block> var1) {
      return this.func_177230_c().func_203417_a(var1);
   }

   default IFluidState func_204520_s() {
      return this.func_177230_c().func_204507_t(this);
   }

   default boolean func_204519_t() {
      return this.func_177230_c().func_149653_t(this);
   }

   default long func_209533_a(BlockPos var1) {
      return this.func_177230_c().func_209900_a(this, var1);
   }
}
