package stellarapi.stellars.system;

import sciapi.api.value.euclidian.EVector;
import stellarapi.StellarSkyResources;
import stellarapi.render.ICelestialObjectRenderer;
import stellarapi.render.StellarRenderInfo;
import stellarapi.util.math.SpCoord;
import stellarapi.util.math.VecMath;

public class SunRenderer implements ICelestialObjectRenderer<SunRenderCache> {
	
	@Override
	public void render(StellarRenderInfo info, SunRenderCache cache) {
		EVector pos = cache.appCoord.getVec();
		EVector dif = new SpCoord(cache.appCoord.x+90, 0.0).getVec();
		EVector dif2 = new SpCoord(cache.appCoord.x, cache.appCoord.y+90).getVec();

		pos.set(VecMath.mult(99.0, pos));
		dif.set(VecMath.mult(cache.size, dif));
		dif2.set(VecMath.mult(-cache.size, dif2));
		
		info.mc.renderEngine.bindTexture(StellarSkyResources.resourceSunHalo.getLocationFor(info.mc.theWorld));
		info.tessellator.startDrawingQuads();
		info.tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif), VecMath.getY(pos)+VecMath.getY(dif), VecMath.getZ(pos)+VecMath.getZ(dif),0.0,0.0);
		info.tessellator.addVertexWithUV(VecMath.getX(pos)+VecMath.getX(dif2), VecMath.getY(pos)+VecMath.getY(dif2), VecMath.getZ(pos)+VecMath.getZ(dif2),1.0,0.0);
		info.tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif), VecMath.getY(pos)-VecMath.getY(dif), VecMath.getZ(pos)-VecMath.getZ(dif),1.0,1.0);
		info.tessellator.addVertexWithUV(VecMath.getX(pos)-VecMath.getX(dif2), VecMath.getY(pos)-VecMath.getY(dif2), VecMath.getZ(pos)-VecMath.getZ(dif2),0.0,1.0);
		info.tessellator.draw();
	}

}
