package subaraki.exsartagine.gui.client;

import net.minecraft.client.gui.GuiButton;

public class TankSwapButton extends GuiButton {
    public TankSwapButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public TankSwapButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }
}
