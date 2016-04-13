package stellarapi.stellars.system;

import org.lwjgl.opengl.GL11;

import stellarapi.render.ICelestialLayerRenderer;
import stellarapi.render.StellarRenderInfo;

public class LayerSolarSystemRenderer implements ICelestialLayerRenderer {
	
	@Override
	public void preRender(StellarRenderInfo info) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, info.weathereff);
	}

	@Override
	public void postRender(StellarRenderInfo info) { }

}
