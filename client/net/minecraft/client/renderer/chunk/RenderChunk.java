package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Sets;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class RenderChunk {
   private volatile World field_178588_d;
   private final WorldRenderer field_178589_e;
   public static int field_178592_a;
   public CompiledChunk field_178590_b;
   private final ReentrantLock field_178587_g;
   private final ReentrantLock field_178598_h;
   private ChunkRenderTask field_178599_i;
   private final Set<TileEntity> field_181056_j;
   private final FloatBuffer field_178597_k;
   private final VertexBuffer[] field_178594_l;
   public AxisAlignedBB field_178591_c;
   private int field_178595_m;
   private boolean field_178593_n;
   private final BlockPos.MutableBlockPos field_178586_f;
   private final BlockPos.MutableBlockPos[] field_181702_p;
   private boolean field_188284_q;

   public RenderChunk(World var1, WorldRenderer var2) {
      super();
      this.field_178590_b = CompiledChunk.field_178502_a;
      this.field_178587_g = new ReentrantLock();
      this.field_178598_h = new ReentrantLock();
      this.field_181056_j = Sets.newHashSet();
      this.field_178597_k = GLAllocation.func_74529_h(16);
      this.field_178594_l = new VertexBuffer[BlockRenderLayer.values().length];
      this.field_178595_m = -1;
      this.field_178593_n = true;
      this.field_178586_f = new BlockPos.MutableBlockPos(-1, -1, -1);
      this.field_181702_p = (BlockPos.MutableBlockPos[])Util.func_200696_a(new BlockPos.MutableBlockPos[6], (var0) -> {
         for(int var1 = 0; var1 < var0.length; ++var1) {
            var0[var1] = new BlockPos.MutableBlockPos();
         }

      });
      this.field_178588_d = var1;
      this.field_178589_e = var2;
      if (OpenGlHelper.func_176075_f()) {
         for(int var3 = 0; var3 < BlockRenderLayer.values().length; ++var3) {
            this.field_178594_l[var3] = new VertexBuffer(DefaultVertexFormats.field_176600_a);
         }
      }

   }

   public boolean func_178577_a(int var1) {
      if (this.field_178595_m == var1) {
         return false;
      } else {
         this.field_178595_m = var1;
         return true;
      }
   }

   public VertexBuffer func_178565_b(int var1) {
      return this.field_178594_l[var1];
   }

   public void func_189562_a(int var1, int var2, int var3) {
      if (var1 != this.field_178586_f.func_177958_n() || var2 != this.field_178586_f.func_177956_o() || var3 != this.field_178586_f.func_177952_p()) {
         this.func_178585_h();
         this.field_178586_f.func_181079_c(var1, var2, var3);
         this.field_178591_c = new AxisAlignedBB((double)var1, (double)var2, (double)var3, (double)(var1 + 16), (double)(var2 + 16), (double)(var3 + 16));
         EnumFacing[] var4 = EnumFacing.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumFacing var7 = var4[var6];
            this.field_181702_p[var7.ordinal()].func_189533_g(this.field_178586_f).func_189534_c(var7, 16);
         }

         this.func_178567_n();
      }
   }

   public void func_178570_a(float var1, float var2, float var3, ChunkRenderTask var4) {
      CompiledChunk var5 = var4.func_178544_c();
      if (var5.func_178487_c() != null && !var5.func_178491_b(BlockRenderLayer.TRANSLUCENT)) {
         this.func_178573_a(var4.func_178545_d().func_179038_a(BlockRenderLayer.TRANSLUCENT), this.field_178586_f);
         var4.func_178545_d().func_179038_a(BlockRenderLayer.TRANSLUCENT).func_178993_a(var5.func_178487_c());
         this.func_178584_a(BlockRenderLayer.TRANSLUCENT, var1, var2, var3, var4.func_178545_d().func_179038_a(BlockRenderLayer.TRANSLUCENT), var5);
      }
   }

   public void func_178581_b(float var1, float var2, float var3, ChunkRenderTask var4) {
      CompiledChunk var5 = new CompiledChunk();
      boolean var6 = true;
      BlockPos var7 = this.field_178586_f.func_185334_h();
      BlockPos var8 = var7.func_177982_a(15, 15, 15);
      World var9 = this.field_178588_d;
      if (var9 != null) {
         var4.func_178540_f().lock();

         try {
            if (var4.func_178546_a() != ChunkRenderTask.Status.COMPILING) {
               return;
            }

            var4.func_178543_a(var5);
         } finally {
            var4.func_178540_f().unlock();
         }

         RenderChunkCache var10 = RenderChunkCache.func_212397_a(var9, var7.func_177982_a(-1, -1, -1), var7.func_177982_a(16, 16, 16), 1);
         VisGraph var11 = new VisGraph();
         HashSet var12 = Sets.newHashSet();
         if (var10 != null) {
            ++field_178592_a;
            boolean[] var13 = new boolean[BlockRenderLayer.values().length];
            BlockModelRenderer.func_211847_a();
            Random var14 = new Random();
            BlockRendererDispatcher var15 = Minecraft.func_71410_x().func_175602_ab();
            Iterator var16 = BlockPos.func_177975_b(var7, var8).iterator();

            while(var16.hasNext()) {
               BlockPos.MutableBlockPos var17 = (BlockPos.MutableBlockPos)var16.next();
               IBlockState var18 = var10.func_180495_p(var17);
               Block var19 = var18.func_177230_c();
               if (var18.func_200015_d(var10, var17)) {
                  var11.func_178606_a(var17);
               }

               if (var19.func_149716_u()) {
                  TileEntity var20 = var10.func_212399_a(var17, Chunk.EnumCreateEntityType.CHECK);
                  if (var20 != null) {
                     TileEntityRenderer var21 = TileEntityRendererDispatcher.field_147556_a.func_147547_b(var20);
                     if (var21 != null) {
                        var5.func_178490_a(var20);
                        if (var21.func_188185_a(var20)) {
                           var12.add(var20);
                        }
                     }
                  }
               }

               IFluidState var37 = var10.func_204610_c(var17);
               int var22;
               BufferBuilder var23;
               BlockRenderLayer var38;
               if (!var37.func_206888_e()) {
                  var38 = var37.func_180664_k();
                  var22 = var38.ordinal();
                  var23 = var4.func_178545_d().func_179039_a(var22);
                  if (!var5.func_178492_d(var38)) {
                     var5.func_178493_c(var38);
                     this.func_178573_a(var23, var7);
                  }

                  var13[var22] |= var15.func_205318_a(var17, var10, var23, var37);
               }

               if (var18.func_185901_i() != EnumBlockRenderType.INVISIBLE) {
                  var38 = var19.func_180664_k();
                  var22 = var38.ordinal();
                  var23 = var4.func_178545_d().func_179039_a(var22);
                  if (!var5.func_178492_d(var38)) {
                     var5.func_178493_c(var38);
                     this.func_178573_a(var23, var7);
                  }

                  var13[var22] |= var15.func_195475_a(var18, var17, var10, var23, var14);
               }
            }

            BlockRenderLayer[] var33 = BlockRenderLayer.values();
            int var34 = var33.length;

            for(int var35 = 0; var35 < var34; ++var35) {
               BlockRenderLayer var36 = var33[var35];
               if (var13[var36.ordinal()]) {
                  var5.func_178486_a(var36);
               }

               if (var5.func_178492_d(var36)) {
                  this.func_178584_a(var36, var1, var2, var3, var4.func_178545_d().func_179038_a(var36), var5);
               }
            }

            BlockModelRenderer.func_210266_a();
         }

         var5.func_178488_a(var11.func_178607_a());
         this.field_178587_g.lock();

         try {
            HashSet var31 = Sets.newHashSet(var12);
            HashSet var32 = Sets.newHashSet(this.field_181056_j);
            var31.removeAll(this.field_181056_j);
            var32.removeAll(var12);
            this.field_181056_j.clear();
            this.field_181056_j.addAll(var12);
            this.field_178589_e.func_181023_a(var32, var31);
         } finally {
            this.field_178587_g.unlock();
         }

      }
   }

   protected void func_178578_b() {
      this.field_178587_g.lock();

      try {
         if (this.field_178599_i != null && this.field_178599_i.func_178546_a() != ChunkRenderTask.Status.DONE) {
            this.field_178599_i.func_178542_e();
            this.field_178599_i = null;
         }
      } finally {
         this.field_178587_g.unlock();
      }

   }

   public ReentrantLock func_178579_c() {
      return this.field_178587_g;
   }

   public ChunkRenderTask func_178574_d() {
      this.field_178587_g.lock();

      ChunkRenderTask var1;
      try {
         this.func_178578_b();
         this.field_178599_i = new ChunkRenderTask(this, ChunkRenderTask.Type.REBUILD_CHUNK, this.func_188280_f());
         var1 = this.field_178599_i;
      } finally {
         this.field_178587_g.unlock();
      }

      return var1;
   }

   @Nullable
   public ChunkRenderTask func_178582_e() {
      this.field_178587_g.lock();

      ChunkRenderTask var1;
      try {
         if (this.field_178599_i != null && this.field_178599_i.func_178546_a() == ChunkRenderTask.Status.PENDING) {
            var1 = null;
            return var1;
         }

         if (this.field_178599_i != null && this.field_178599_i.func_178546_a() != ChunkRenderTask.Status.DONE) {
            this.field_178599_i.func_178542_e();
            this.field_178599_i = null;
         }

         this.field_178599_i = new ChunkRenderTask(this, ChunkRenderTask.Type.RESORT_TRANSPARENCY, this.func_188280_f());
         this.field_178599_i.func_178543_a(this.field_178590_b);
         var1 = this.field_178599_i;
      } finally {
         this.field_178587_g.unlock();
      }

      return var1;
   }

   protected double func_188280_f() {
      EntityPlayerSP var1 = Minecraft.func_71410_x().field_71439_g;
      double var2 = this.field_178591_c.field_72340_a + 8.0D - var1.field_70165_t;
      double var4 = this.field_178591_c.field_72338_b + 8.0D - var1.field_70163_u;
      double var6 = this.field_178591_c.field_72339_c + 8.0D - var1.field_70161_v;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   private void func_178573_a(BufferBuilder var1, BlockPos var2) {
      var1.func_181668_a(7, DefaultVertexFormats.field_176600_a);
      var1.func_178969_c((double)(-var2.func_177958_n()), (double)(-var2.func_177956_o()), (double)(-var2.func_177952_p()));
   }

   private void func_178584_a(BlockRenderLayer var1, float var2, float var3, float var4, BufferBuilder var5, CompiledChunk var6) {
      if (var1 == BlockRenderLayer.TRANSLUCENT && !var6.func_178491_b(var1)) {
         var5.func_181674_a(var2, var3, var4);
         var6.func_178494_a(var5.func_181672_a());
      }

      var5.func_178977_d();
   }

   private void func_178567_n() {
      GlStateManager.func_179094_E();
      GlStateManager.func_179096_D();
      float var1 = 1.000001F;
      GlStateManager.func_179109_b(-8.0F, -8.0F, -8.0F);
      GlStateManager.func_179152_a(1.000001F, 1.000001F, 1.000001F);
      GlStateManager.func_179109_b(8.0F, 8.0F, 8.0F);
      GlStateManager.func_179111_a(2982, this.field_178597_k);
      GlStateManager.func_179121_F();
   }

   public void func_178572_f() {
      GlStateManager.func_179110_a(this.field_178597_k);
   }

   public CompiledChunk func_178571_g() {
      return this.field_178590_b;
   }

   public void func_178580_a(CompiledChunk var1) {
      this.field_178598_h.lock();

      try {
         this.field_178590_b = var1;
      } finally {
         this.field_178598_h.unlock();
      }

   }

   public void func_178585_h() {
      this.func_178578_b();
      this.field_178590_b = CompiledChunk.field_178502_a;
   }

   public void func_178566_a() {
      this.func_178585_h();
      this.field_178588_d = null;

      for(int var1 = 0; var1 < BlockRenderLayer.values().length; ++var1) {
         if (this.field_178594_l[var1] != null) {
            this.field_178594_l[var1].func_177362_c();
         }
      }

   }

   public BlockPos func_178568_j() {
      return this.field_178586_f;
   }

   public void func_178575_a(boolean var1) {
      if (this.field_178593_n) {
         var1 |= this.field_188284_q;
      }

      this.field_178593_n = true;
      this.field_188284_q = var1;
   }

   public void func_188282_m() {
      this.field_178593_n = false;
      this.field_188284_q = false;
   }

   public boolean func_178569_m() {
      return this.field_178593_n;
   }

   public boolean func_188281_o() {
      return this.field_178593_n && this.field_188284_q;
   }

   public BlockPos func_181701_a(EnumFacing var1) {
      return this.field_181702_p[var1.ordinal()];
   }

   public World func_188283_p() {
      return this.field_178588_d;
   }
}
