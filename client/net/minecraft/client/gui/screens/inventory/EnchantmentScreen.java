package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentScreen extends AbstractContainerScreen<EnchantmentMenu> {
   private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");
   private static final ResourceLocation ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final Random random = new Random();
   private BookModel bookModel;
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   private ItemStack last;

   public EnchantmentScreen(EnchantmentMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.last = ItemStack.EMPTY;
   }

   protected void init() {
      super.init();
      this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
   }

   public void containerTick() {
      super.containerTick();
      this.tickBook();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      int var6 = (this.width - this.imageWidth) / 2;
      int var7 = (this.height - this.imageHeight) / 2;

      for(int var8 = 0; var8 < 3; ++var8) {
         double var9 = var1 - (double)(var6 + 60);
         double var11 = var3 - (double)(var7 + 14 + 19 * var8);
         if (var9 >= 0.0D && var11 >= 0.0D && var9 < 108.0D && var11 < 19.0D && ((EnchantmentMenu)this.menu).clickMenuButton(this.minecraft.player, var8)) {
            this.minecraft.gameMode.handleInventoryButtonClick(((EnchantmentMenu)this.menu).containerId, var8);
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      Lighting.setupForFlatItems();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      this.blit(var1, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      int var7 = (int)this.minecraft.getWindow().getGuiScale();
      RenderSystem.viewport((this.width - 320) / 2 * var7, (this.height - 240) / 2 * var7, 320 * var7, 240 * var7);
      Matrix4f var8 = Matrix4f.createTranslateMatrix(-0.34F, 0.23F, 0.0F);
      var8.multiply(Matrix4f.perspective(90.0D, 1.3333334F, 9.0F, 80.0F));
      RenderSystem.backupProjectionMatrix();
      RenderSystem.setProjectionMatrix(var8);
      var1.pushPose();
      PoseStack.Pose var9 = var1.last();
      var9.pose().setIdentity();
      var9.normal().setIdentity();
      var1.translate(0.0D, 3.299999952316284D, 1984.0D);
      float var10 = 5.0F;
      var1.scale(5.0F, 5.0F, 5.0F);
      var1.mulPose(Vector3f.field_294.rotationDegrees(180.0F));
      var1.mulPose(Vector3f.field_290.rotationDegrees(20.0F));
      float var11 = Mth.lerp(var2, this.oOpen, this.open);
      var1.translate((double)((1.0F - var11) * 0.2F), (double)((1.0F - var11) * 0.1F), (double)((1.0F - var11) * 0.25F));
      float var12 = -(1.0F - var11) * 90.0F - 90.0F;
      var1.mulPose(Vector3f.field_292.rotationDegrees(var12));
      var1.mulPose(Vector3f.field_290.rotationDegrees(180.0F));
      float var13 = Mth.lerp(var2, this.oFlip, this.flip) + 0.25F;
      float var14 = Mth.lerp(var2, this.oFlip, this.flip) + 0.75F;
      var13 = (var13 - (float)Mth.fastFloor((double)var13)) * 1.6F - 0.3F;
      var14 = (var14 - (float)Mth.fastFloor((double)var14)) * 1.6F - 0.3F;
      if (var13 < 0.0F) {
         var13 = 0.0F;
      }

      if (var14 < 0.0F) {
         var14 = 0.0F;
      }

      if (var13 > 1.0F) {
         var13 = 1.0F;
      }

      if (var14 > 1.0F) {
         var14 = 1.0F;
      }

      this.bookModel.setupAnim(0.0F, var13, var14, var11);
      MultiBufferSource.BufferSource var15 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      VertexConsumer var16 = var15.getBuffer(this.bookModel.renderType(ENCHANTING_BOOK_LOCATION));
      this.bookModel.renderToBuffer(var1, var16, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      var15.endBatch();
      var1.popPose();
      RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
      RenderSystem.restoreProjectionMatrix();
      Lighting.setupFor3DItems();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      EnchantmentNames.getInstance().initSeed((long)((EnchantmentMenu)this.menu).getEnchantmentSeed());
      int var17 = ((EnchantmentMenu)this.menu).getGoldCount();

      for(int var18 = 0; var18 < 3; ++var18) {
         int var19 = var5 + 60;
         int var20 = var19 + 20;
         this.setBlitOffset(0);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
         int var21 = ((EnchantmentMenu)this.menu).costs[var18];
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         if (var21 == 0) {
            this.blit(var1, var19, var6 + 14 + 19 * var18, 0, 185, 108, 19);
         } else {
            String var22 = var21.makeConcatWithConstants<invokedynamic>(var21);
            int var23 = 86 - this.font.width(var22);
            FormattedText var24 = EnchantmentNames.getInstance().getRandomName(this.font, var23);
            int var25 = 6839882;
            if ((var17 < var18 + 1 || this.minecraft.player.experienceLevel < var21) && !this.minecraft.player.getAbilities().instabuild) {
               this.blit(var1, var19, var6 + 14 + 19 * var18, 0, 185, 108, 19);
               this.blit(var1, var19 + 1, var6 + 15 + 19 * var18, 16 * var18, 239, 16, 16);
               this.font.drawWordWrap(var24, var20, var6 + 16 + 19 * var18, var23, (var25 & 16711422) >> 1);
               var25 = 4226832;
            } else {
               int var26 = var3 - (var5 + 60);
               int var27 = var4 - (var6 + 14 + 19 * var18);
               if (var26 >= 0 && var27 >= 0 && var26 < 108 && var27 < 19) {
                  this.blit(var1, var19, var6 + 14 + 19 * var18, 0, 204, 108, 19);
                  var25 = 16777088;
               } else {
                  this.blit(var1, var19, var6 + 14 + 19 * var18, 0, 166, 108, 19);
               }

               this.blit(var1, var19 + 1, var6 + 15 + 19 * var18, 16 * var18, 223, 16, 16);
               this.font.drawWordWrap(var24, var20, var6 + 16 + 19 * var18, var23, var25);
               var25 = 8453920;
            }

            this.font.drawShadow(var1, var22, (float)(var20 + 86 - this.font.width(var22)), (float)(var6 + 16 + 19 * var18 + 7), var25);
         }
      }

   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      var4 = this.minecraft.getFrameTime();
      this.renderBackground(var1);
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
      boolean var5 = this.minecraft.player.getAbilities().instabuild;
      int var6 = ((EnchantmentMenu)this.menu).getGoldCount();

      for(int var7 = 0; var7 < 3; ++var7) {
         int var8 = ((EnchantmentMenu)this.menu).costs[var7];
         Enchantment var9 = Enchantment.byId(((EnchantmentMenu)this.menu).enchantClue[var7]);
         int var10 = ((EnchantmentMenu)this.menu).levelClue[var7];
         int var11 = var7 + 1;
         if (this.isHovering(60, 14 + 19 * var7, 108, 17, (double)var2, (double)var3) && var8 > 0 && var10 >= 0 && var9 != null) {
            ArrayList var12 = Lists.newArrayList();
            var12.add((new TranslatableComponent("container.enchant.clue", new Object[]{var9.getFullname(var10)})).withStyle(ChatFormatting.WHITE));
            if (!var5) {
               var12.add(TextComponent.EMPTY);
               if (this.minecraft.player.experienceLevel < var8) {
                  var12.add((new TranslatableComponent("container.enchant.level.requirement", new Object[]{((EnchantmentMenu)this.menu).costs[var7]})).withStyle(ChatFormatting.RED));
               } else {
                  TranslatableComponent var13;
                  if (var11 == 1) {
                     var13 = new TranslatableComponent("container.enchant.lapis.one");
                  } else {
                     var13 = new TranslatableComponent("container.enchant.lapis.many", new Object[]{var11});
                  }

                  var12.add(var13.withStyle(var6 >= var11 ? ChatFormatting.GRAY : ChatFormatting.RED));
                  TranslatableComponent var14;
                  if (var11 == 1) {
                     var14 = new TranslatableComponent("container.enchant.level.one");
                  } else {
                     var14 = new TranslatableComponent("container.enchant.level.many", new Object[]{var11});
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
      ItemStack var1 = ((EnchantmentMenu)this.menu).getSlot(0).getItem();
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
         if (((EnchantmentMenu)this.menu).costs[var3] != 0) {
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
