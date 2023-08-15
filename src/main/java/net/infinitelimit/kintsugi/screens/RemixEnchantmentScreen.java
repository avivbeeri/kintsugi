package net.infinitelimit.kintsugi.screens;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.infinitelimit.kintsugi.Kintsugi;
import net.infinitelimit.kintsugi.menus.RemixEnchantmentMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RemixEnchantmentScreen extends AbstractContainerScreen<RemixEnchantmentMenu> {
    /** The ResourceLocation containing the Enchantment GUI texture location */
    private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation(Kintsugi.MOD_ID, "textures/gui/container/enchanting_table.png");
    /** The ResourceLocation containing the texture for the Book rendered above the enchantment table */
    private static final ResourceLocation ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");

    /** Text for UI **/
    private static final ResourceLocation ALT_FONT = new ResourceLocation("minecraft", "alt");
    private static final Style ROOT_STYLE = Style.EMPTY.withFont(ALT_FONT);
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_WIDTH = 112;

    /** A Random instance for use with the enchantment gui */
    private final RandomSource random = RandomSource.create();
    private BookModel bookModel;
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;

    private int scrollOffset = 0;
    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;
    private boolean isDragging = false;

    public RemixEnchantmentScreen(RemixEnchantmentMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 300;
        this.inventoryLabelX = 131;

    }

    protected void init() {
        super.init();
        this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    public void onEnchantmentClick(int i) {
        this.menu.clickMenuButton(this.minecraft.player, i);
        this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, i);
    }

    public void containerTick() {
        super.containerTick();
        this.tickBook();
    }

    /**
     * Called when a mouse button is clicked within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @param pButton the button that was clicked.
     */
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int pX = (this.width - this.imageWidth) / 2;
        int pY = (this.height - this.imageHeight) / 2;
        List<Enchantment> enchantments = this.menu.getAvailableEnchantments();

        this.isDragging = false;
        if (this.canScroll(enchantments.size()) && pMouseX > (double)(pX + 118) && pMouseX < (double)(pX + 118 + 6) && pMouseY > (double)(pY + 18) && pMouseY <= (double)(pY + 18 + 139 + 1)) {
            this.isDragging = true;
            return true;
        }

        Set<Enchantment> itemEnchantments = this.menu.slots.get(0).getItem().getAllEnchantments().keySet();

        for (int k = 0; k < enchantments.size(); k++) {
            if (k < this.scrollOffset || k >= this.scrollOffset + 7) {
                continue;
            }
            Set<Enchantment> proposed = new HashSet<>(itemEnchantments);
            proposed.add(enchantments.get(k));

            double d0 = pMouseX - (double) (pX + 4);
            double d1 = pMouseY - (double) (pY + 18 + BUTTON_HEIGHT * (k - this.scrollOffset));

            if (d0 >= 0.0D && d1 >= 0.0D && d0 < BUTTON_WIDTH && d1 < BUTTON_HEIGHT && RemixEnchantmentMenu.calculateCompatibility(proposed)) {
                this.onEnchantmentClick(k);
                return true;
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int pX = (this.width - this.imageWidth) / 2;
        int pY = (this.height - this.imageHeight) / 2;
        pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, pX, pY, 0, 0F, 0F,  this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        this.renderBook(pGuiGraphics, pX + 122, pY + 15, pPartialTick);
        int xOffset = pX + 5;
        int textXOffset = xOffset + 2;

        List<Enchantment> enchantments = this.menu.getAvailableEnchantments();
        if (!canScroll(enchantments.size())) {
            this.scrollOffset = 0;
        }
        int yOffset = -(this.scrollOffset * BUTTON_HEIGHT);

        Set<Enchantment> itemEnchantments = this.menu.slots.get(0).getItem().getAllEnchantments().keySet();
        int total = enchantments.size();
        
        for (int i = 0; i < total; i++) {
            if (i < this.scrollOffset || i >= this.scrollOffset + 7) {
                continue;
            }
            int maxWidth = BUTTON_WIDTH - 2;
            Enchantment enchantment = enchantments.get(i);

            Set<Enchantment> proposed = new HashSet<>(itemEnchantments);
            proposed.add(enchantment);

            if (this.menu.getSelectedEnchantment() == i) {
                pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, xOffset, pY + yOffset + 18 + BUTTON_HEIGHT * i, BUTTON_WIDTH, 166, BUTTON_WIDTH, BUTTON_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            } else if (!RemixEnchantmentMenu.calculateCompatibility(proposed)) {
                pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, xOffset, pY + yOffset + 18 + BUTTON_HEIGHT * i, BUTTON_WIDTH, 166 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            } else if (pMouseY >= pY + yOffset + 18 + BUTTON_HEIGHT * i && pMouseY < pY + yOffset + 18 + BUTTON_HEIGHT * (i + 1) && pMouseX >= xOffset && pMouseX < xOffset + BUTTON_WIDTH) {
                pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, xOffset, pY + yOffset + 18 + BUTTON_HEIGHT * i, 0, 166 + BUTTON_HEIGHT, BUTTON_WIDTH, BUTTON_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            } else {
                pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, xOffset, pY + yOffset + 18 + BUTTON_HEIGHT * i, 0, 166, BUTTON_WIDTH, BUTTON_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }

            if (!RemixEnchantmentMenu.calculateCompatibility(proposed)) {
                pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, xOffset + BUTTON_WIDTH - 9, pY + yOffset + 18 + BUTTON_HEIGHT * (i + 1) - 9, 0, 240, 7, 7, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }

            MutableComponent formattedText = Component.translatable(enchantment.getDescriptionId());

            int color = 0x685e4a;
            pGuiGraphics.drawWordWrap(this.font, formattedText, textXOffset, pY + yOffset + 20 + BUTTON_HEIGHT * i, maxWidth, color);
            FormattedText runes = formattedText.withStyle(ROOT_STYLE);
            pGuiGraphics.drawWordWrap(font, runes, textXOffset,  pY + yOffset + 20 + BUTTON_HEIGHT * i + 9, 102, color);
        }

        int range = this.menu.getMaxPower();

        // Lapis Arrows
        for (int i = 0; i < range; i++) {
            int k;
            if (i < this.menu.getFuelCount()) {
              k = 0;
            } else {
              k = 1;
            }
            pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, pX + 206 + 12 * i, pY + 53, k * 10, 234, 10, 6, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

        if (!this.menu.getValidity()) {
            pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, pX + 216, pY + 22, imageWidth, 0, 28, 21, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

        boolean flag = this.minecraft.player.getAbilities().instabuild;
        if (!flag && this.menu.getLevelCost() >= 0) {
            int color = 0x685e4a;
            MutableComponent text = Component.translatable("container.kintsugi.enchant.levelcost", this.menu.getLevelCost());
            int leftX = pX + this.imageWidth - 8 - this.font.width(text) - 2;
            pGuiGraphics.fill(leftX - 2, pY + 66, pX + this.imageWidth - 8, pY + 78, 0x4f000000);
            pGuiGraphics.drawWordWrap(font, text, leftX, pY + 68, 102, color);


        }
    }

    private void renderBook(GuiGraphics pGuiGraphics, int pX, int pY, float pPartialTick) {
        float f = Mth.lerp(pPartialTick, this.oOpen, this.open);
        float f1 = Mth.lerp(pPartialTick, this.oFlip, this.flip);
        Lighting.setupForEntityInInventory();
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float)pX + 33.0F, (float)pY + 31.0F, 100.0F);
        float f2 = 40.0F;
        pGuiGraphics.pose().scale(-40.0F, 40.0F, 40.0F);
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(25.0F));
        pGuiGraphics.pose().translate((1.0F - f) * 0.2F, (1.0F - f) * 0.1F, (1.0F - f) * 0.25F);
        float f3 = -(1.0F - f) * 90.0F - 90.0F;
        pGuiGraphics.pose().mulPose(Axis.YP.rotationDegrees(f3));
        pGuiGraphics.pose().mulPose(Axis.XP.rotationDegrees(180.0F));
        float f4 = Mth.clamp(Mth.frac(f1 + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
        float f5 = Mth.clamp(Mth.frac(f1 + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
        this.bookModel.setupAnim(0.0F, f4, f5, f);
        VertexConsumer vertexconsumer = pGuiGraphics.bufferSource().getBuffer(this.bookModel.renderType(ENCHANTING_BOOK_LOCATION));
        this.bookModel.renderToBuffer(pGuiGraphics.pose(), vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pGuiGraphics.flush();
        pGuiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

    private void renderScroller(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, List<Enchantment> enchantments) {
        int pX = (this.width - this.imageWidth) / 2;
        int pY = (this.height - this.imageHeight) / 2;
        int offscreenCount = enchantments.size() + 1 - 7;

        if (offscreenCount >= 1) {
            int j = 139 - (27 + (offscreenCount - 1) * 139 / offscreenCount);
            int k = 1 + j / offscreenCount + 139 / offscreenCount;
            int l = 113;
            int scrollPosition = Math.min(l, this.scrollOffset * k);
            if (this.scrollOffset == offscreenCount - 1) {
                scrollPosition = l;
            }

            if (isDragging || this.canScroll(enchantments.size()) && pMouseX > (double)(pX + 118) && pMouseX < (double)(pX + 118 + 6) && pMouseY > (double)(pY + 18) && pMouseY <= (double)(pY + 18 + 139 + 1)) {
                pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, pX + 118, pY + 18 + scrollPosition, 0, 6, 206, 6, 27,  TEXTURE_WIDTH, TEXTURE_HEIGHT);
            } else {
                pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, pX + 118, pY + 18 + scrollPosition, 0, 0, 206, 6, 27,  TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
        } else {
            pGuiGraphics.blit(ENCHANTING_TABLE_LOCATION, pX + 118, pY + 18, 0, 6, 206, 6, 27,  TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }
    }

    /**
     * Renders the graphical user interface (GUI) element.
     * @param pGuiGraphics the GuiGraphics object used for rendering.
     * @param pMouseX the x-coordinate of the mouse cursor.
     * @param pMouseY the y-coordinate of the mouse cursor.
     * @param pPartialTick the partial tick time.
     */
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pPartialTick = this.minecraft.getFrameTime();

        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        this.renderScroller(pGuiGraphics, pMouseX, pMouseY, this.menu.getAvailableEnchantments());
    }

    public void tickBook() {
        ItemStack itemstack = this.menu.getSlot(2).getItem();
        if (!ItemStack.matches(itemstack, this.last)) {
            this.last = itemstack;

            do {
                this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while(this.flip <= this.flipT + 1.0F && this.flip >= this.flipT - 1.0F);
        }

        ++this.time;
        this.oFlip = this.flip;
        this.oOpen = this.open;
        boolean flag = !itemstack.isEmpty();

        if (flag) {
            this.open += 0.2F;
        } else {
            this.open -= 0.2F;
        }

        this.open = Mth.clamp(this.open, 0.0F, 1.0F);
        float f1 = (this.flipT - this.flip) * 0.4F;
        float f = 0.2F;
        f1 = Mth.clamp(f1, -0.2F, 0.2F);
        this.flipA += (f1 - this.flipA) * 0.9F;
        this.flip += this.flipA;
    }

    private boolean canScroll(int pNumEnchantments) {
        return pNumEnchantments > 7;
    }
    /**
     * Called when the mouse wheel is scrolled within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @param pDelta the scrolling delta.
     */
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        int i = this.menu.getAvailableEnchantments().size();
        if (this.canScroll(i)) {
            int j = i - 7;
            this.scrollOffset = Mth.clamp((int)Math.round((double)this.scrollOffset - pDelta), 0, j);
        }

        return true;
    }

    /**
     * Called when the mouse is dragged within the GUI element.
     * <p>
     * @return {@code true} if the event is consumed, {@code false} otherwise.
     * @param pMouseX the X coordinate of the mouse.
     * @param pMouseY the Y coordinate of the mouse.
     * @param pButton the button that is being dragged.
     * @param pDragX the X distance of the drag.
     * @param pDragY the Y distance of the drag.
     */
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        int count = this.menu.getAvailableEnchantments().size();
        if (this.isDragging) {
            int j = this.topPos + 18;
            int k = j + 139;
            int l = count - 7;
            float f = ((float)pMouseY - (float)j - 13.5F) / ((float)(k - j) - 27.0F);
            f = f * (float)l + 0.5F;
            this.scrollOffset = Mth.clamp((int)f, 0, l);
            return true;
        } else {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        this.isDragging = false;
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }
}
