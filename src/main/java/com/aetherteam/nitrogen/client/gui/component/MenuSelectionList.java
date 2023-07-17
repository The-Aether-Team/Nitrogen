package com.aetherteam.nitrogen.client.gui.component;

//import com.aetherteam.nitrogen.api.menu.Menu;
//import com.aetherteam.nitrogen.client.gui.screen.MenuSelectionScreen;
//import com.mojang.blaze3d.vertex.PoseStack;
//import net.minecraft.client.gui.components.ObjectSelectionList;
//import net.minecraft.network.chat.Component;

//public class MenuSelectionList extends ObjectSelectionList<MenuSelectionList.MenuEntry> {
//    private MenuSelectionScreen parent;
//    private final int listWidth;
//
//    public MenuSelectionList(MenuSelectionScreen parent, int listWidth, int top, int bottom) {
//        super(parent.getMinecraftInstance(), listWidth, parent.height, top, bottom, parent.getFontRenderer().lineHeight * 2 + 8);
//        this.parent = parent;
//        this.listWidth = listWidth;
//        this.refreshList();
//    }
//
//    @Override
//    protected int getScrollbarPosition()
//    {
//        return this.listWidth;
//    }
//
//    @Override
//    public int getRowWidth()
//    {
//        return this.listWidth;
//    }
//
//    public void refreshList() {
//        this.clearEntries();
//        //parent.buildModList(this::addEntry, mod->new ModListWidget.ModEntry(mod, this.parent));
//    }
//
//    public class MenuEntry extends ObjectSelectionList.Entry<MenuEntry> {
//        private final MenuSelectionScreen parent;
//        private final Menu menu;
//
//        public MenuEntry(MenuSelectionScreen parent, Menu menu) {
//            this.parent = parent;
//            this.menu = menu;
//        }
//
//        @Override
//        public Component getNarration() {
//            return this.menu.getName();
//        }
//
//        @Override
//        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
//
//        }
//
//        @Override
//        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
//            return super.mouseClicked(pMouseX, pMouseY, pButton);
//        }
//
//        public Menu getMenu() {
//            return this.menu;
//        }
//    }
//}
