package stellarapi.lib.gui.button;

import stellarapi.lib.gui.IElementController;
import stellarapi.lib.gui.IRenderer;

public interface IButtonDetectorController extends IElementController {

	public boolean canClick(int eventButton);

	public void onClicked(int eventButton, float ratioX, float ratioY);

	public void onClicking(float ratioX, float ratioY);

	public void onClickEnded(int eventButton, float ratioX, float ratioY);

	public void setupRenderer(boolean mouseOver, IRenderer renderer);

	public String setupOverlay(boolean mouseOver, IRenderer renderer);

	public String setupMain(boolean mouseOver, IRenderer renderer);

}
