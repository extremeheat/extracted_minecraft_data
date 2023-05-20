package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.joml.Matrix4f;

public class EnchantmentScreen extends AbstractContainerScreen<EnchantmentMenu> {
   private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final RandomSource random = RandomSource.create();
   private BookModel bookModel;
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   private ItemStack last = ItemStack.EMPTY;

   public EnchantmentScreen(EnchantmentMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
   }

   @Override
   protected void init() {
      super.init();
      this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
   }

   @Override
   public void containerTick() {
      super.containerTick();
      this.tickBook();
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      int var6 = (this.width - this.imageWidth) / 2;
      int var7 = (this.height - this.imageHeight) / 2;

      for(int var8 = 0; var8 < 3; ++var8) {
         double var9 = var1 - (double)(var6 + 60);
         double var11 = var3 - (double)(var7 + 14 + 19 * var8);
         if (var9 >= 0.0 && var11 >= 0.0 && var9 < 108.0 && var11 < 19.0 && this.menu.clickMenuButton(this.minecraft.player, var8)) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, var8);
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   @Override
   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      Lighting.setupForFlatItems();
      RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      blit(var1, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      int var7 = (int)this.minecraft.getWindow().getGuiScale();
      RenderSystem.viewport((this.width - 320) / 2 * var7, (this.height - 240) / 2 * var7, 320 * var7, 240 * var7);
      Matrix4f var8 = new Matrix4f().translation(-0.34F, 0.23F, 0.0F).perspective(1.5707964F, 1.3333334F, 9.0F, 80.0F);
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(var8);
      var1.pushPose();
      var1.setIdentity();
      var1.translate(0.0F, 3.3F, 1984.0F);
      float var9 = 5.0F;
      var1.scale(5.0F, 5.0F, 5.0F);
      var1.mulPose(Axis.ZP.rotationDegrees(180.0F));
      var1.mulPose(Axis.XP.rotationDegrees(20.0F));
      float var10 = Mth.lerp(var2, this.oOpen, this.open);
      var1.translate((1.0F - var10) * 0.2F, (1.0F - var10) * 0.1F, (1.0F - var10) * 0.25F);
      float var11 = -(1.0F - var10) * 90.0F - 90.0F;
      var1.mulPose(Axis.YP.rotationDegrees(var11));
      var1.mulPose(Axis.XP.rotationDegrees(180.0F));
      float var12 = Mth.lerp(var2, this.oFlip, this.flip) + 0.25F;
      float var13 = Mth.lerp(var2, this.oFlip, this.flip) + 0.75F;
      var12 = (var12 - (float)Mth.floor(var12)) * 1.6F - 0.3F;
      var13 = (var13 - (float)Mth.floor(var13)) * 1.6F - 0.3F;
      if (var12 < 0.0F) {
         var12 = 0.0F;
      }

      if (var13 < 0.0F) {
         var13 = 0.0F;
      }

      if (var12 > 1.0F) {
         var12 = 1.0F;
      }

      if (var13 > 1.0F) {
         var13 = 1.0F;
      }

      this.bookModel.setupAnim(0.0F, var12, var13, var10);
      MultiBufferSource.BufferSource var14 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      VertexConsumer var15 = var14.getBuffer(this.bookModel.renderType(ENCHANTING_BOOK_LOCATION));
      this.bookModel.renderToBuffer(var1, var15, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      var14.endBatch();
      var1.popPose();
      RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
      RenderSystem.restoreProjectionMatrix();
      Lighting.setupFor3DItems();
      EnchantmentNames.getInstance().initSeed((long)this.menu.getEnchantmentSeed());
      int var16 = this.menu.getGoldCount();

      for(int var17 = 0; var17 < 3; ++var17) {
         int var18 = var5 + 60;
         int var19 = var18 + 20;
         RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
         int var20 = this.menu.costs[var17];
         if (var20 == 0) {
            blit(var1, var18, var6 + 14 + 19 * var17, 0, 185, 108, 19);
         } else {
            String var21 = var20 + "";
            int var22 = 86 - this.font.width(var21);
            FormattedText var23 = EnchantmentNames.getInstance().getRandomName(this.font, var22);
            int var24 = 6839882;
            if ((var16 < var17 + 1 || this.minecraft.player.experienceLevel < var20) && !this.minecraft.player.getAbilities().instabuild) {
               blit(var1, var18, var6 + 14 + 19 * var17, 0, 185, 108, 19);
               blit(var1, var18 + 1, var6 + 15 + 19 * var17, 16 * var17, 239, 16, 16);
               this.font.drawWordWrap(var1, var23, var19, var6 + 16 + 19 * var17, var22, (var24 & 16711422) >> 1);
               var24 = 4226832;
            } else {
               int var25 = var3 - (var5 + 60);
               int var26 = var4 - (var6 + 14 + 19 * var17);
               if (var25 >= 0 && var26 >= 0 && var25 < 108 && var26 < 19) {
                  blit(var1, var18, var6 + 14 + 19 * var17, 0, 204, 108, 19);
                  var24 = 16777088;
               } else {
                  blit(var1, var18, var6 + 14 + 19 * var17, 0, 166, 108, 19);
               }

               blit(var1, var18 + 1, var6 + 15 + 19 * var17, 16 * var17, 223, 16, 16);
               this.font.drawWordWrap(var1, var23, var19, var6 + 16 + 19 * var17, var22, var24);
               var24 = 8453920;
            }

            this.font.drawShadow(var1, var21, (float)(var19 + 86 - this.font.width(var21)), (float)(var6 + 16 + 19 * var17 + 7), var24);
         }
      }
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      var4 = this.minecraft.getFrameTime();
      this.renderBackground(var1);
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
      boolean var5 = this.minecraft.player.getAbilities().instabuild;
      int var6 = this.menu.getGoldCount();

      for(int var7 = 0; var7 < 3; ++var7) {
         int var8 = this.menu.costs[var7];
         Enchantment var9 = Enchantment.byId(this.menu.enchantClue[var7]);
         int var10 = this.menu.levelClue[var7];
         int var11 = var7 + 1;
         if (this.isHovering(60, 14 + 19 * var7, 108, 17, (double)var2, (double)var3) && var8 > 0 && var10 >= 0 && var9 != null) {
            ArrayList var12 = Lists.newArrayList();
            var12.add(Component.translatable("container.enchant.clue", var9.getFullname(var10)).withStyle(ChatFormatting.WHITE));
            if (!var5) {
               var12.add(CommonComponents.EMPTY);
               if (this.minecraft.player.experienceLevel < var8) {
                  var12.add(Component.translatable("container.enchant.level.requirement", this.menu.costs[var7]).withStyle(ChatFormatting.RED));
               } else {
                  MutableComponent var13;
                  if (var11 == 1) {
                     var13 = Component.translatable("container.enchant.lapis.one");
                  } else {
                     var13 = Component.translatable("container.enchant.lapis.many", var11);
                  }

                  var12.add(var13.withStyle(var6 >= var11 ? ChatFormatting.GRAY : ChatFormatting.RED));
                  MutableComponent var14;
                  if (var11 == 1) {
                     var14 = Component.translatable("container.enchant.level.one");
                  } else {
                     var14 = Component.translatable("container.enchant.level.many", var11);
                  }

                  var12.add(var14.withStyle(ChatFormatting.GRAY));
               }
            }

            this.renderComponentTooltip(var1, var12, var2, var3);
            break;
         }
      }
   }

   public void tickBook() {
      ItemStack var1 = this.menu.getSlot(0).getItem();
      if (!ItemStack.matches(var1, this.last)) {
         this.last = var1;

         do {
            this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
         } while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
      }

      ++this.time;
      this.oFlip = this.flip;
      this.oOpen = this.open;
      boolean var2 = false;

      for(int var3 = 0; var3 < 3; ++var3) {
         if (this.menu.costs[var3] != 0) {
            var2 = true;
         }
      }

      if (var2) {
         this.open += 0.2F;
      } else {
         this.open -= 0.2F;
      }

      this.open = Mth.clamp(this.open, 0.0F, 1.0F);
      float var5 = (this.flipT - this.flip) * 0.4F;
      float var4 = 0.2F;
      var5 = Mth.clamp(var5, -0.2F, 0.2F);
      this.flipA += (var5 - this.flipA) * 0.9F;
      this.flip += this.flipA;
   }
}
