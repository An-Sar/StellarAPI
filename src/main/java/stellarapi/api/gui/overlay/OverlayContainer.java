package stellarapi.api.gui.overlay;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import stellarapi.api.gui.pos.ElementPos;
import stellarapi.api.gui.pos.EnumHorizontalPos;
import stellarapi.api.gui.pos.EnumVerticalPos;
import stellarapi.api.lib.config.ConfigManager;

public class OverlayContainer {
	
	private int width;
	private int height;
	
	private String langKey;
	
	private EnumOverlayMode currentMode = EnumOverlayMode.OVERLAY;
	private List<Delegate> elementList = Lists.newArrayList();
	
	public class Delegate<Element extends IGuiOverlay<Settings>, Settings extends PerOverlaySettings> {
		ElementPos pos;
		final IGuiOverlayType<Element, Settings> type;
		final Element element;
		final Settings settings;
		final IRawHandler<Element> handler;
		final ConfigManager notified;
		
		private Delegate(IGuiOverlayType<Element, Settings> type, ConfigManager config) {
			this.type = type;
			this.element = type.generateElement();
			this.settings = type.generateSettings();
			this.handler = type.generateRawHandler();
			this.notified = config;
			
			notified.register(type.getName(), this.settings);
			settings.initializeSetttings(type.defaultHorizontalPos(), type.defaultVerticalPos());
		}
		
		private void initialize(Minecraft mc) {
			element.initialize(mc, this.settings);
			if(this.handler != null)
				handler.initialize(mc, OverlayContainer.this, this.element);
			
			this.pos = new ElementPos(settings.getHorizontal(), settings.getVertical());
		}
		
		public boolean canSetPos(EnumHorizontalPos horizontal, EnumVerticalPos vertical) {
			if(!type.accepts(horizontal, vertical))
				return false;
			
			ElementPos pos = new ElementPos(horizontal, vertical);
			for(Delegate delegate : elementList) {
				if(delegate.equals(pos))
					return false;
			}
			
			return true;
		}
		
		public boolean trySetPos(EnumHorizontalPos horizontal, EnumVerticalPos vertical) {
			if(!canSetPos(horizontal, vertical))
				return false;
			
			ElementPos pos = new ElementPos(horizontal, vertical);
			
			settings.setHorizontal(pos.getHorizontalPos());
			settings.setVertical(pos.getVerticalPos());
			this.pos = pos;
			return true;
		}
		
		public int getWidth() {
			return element.getWidth();
		}
		
		public int getHeight() {
			return element.getHeight();
		}
		
		public ElementPos getCurrentPos() {
			return this.pos;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof ElementPos) {
				return pos.equals(obj);
			} else if(obj instanceof Delegate) {
				return pos.equals(((Delegate) obj).pos);
				
			} return false;
		}
	}
	
	public OverlayContainer(String langKey) {
		this.langKey = langKey;
	}
	
	public <E extends IGuiOverlay<S>, S extends PerOverlaySettings> void inject(IGuiOverlayType<E, S> type, ConfigManager config) {
		elementList.add(new Delegate<E, S>(type, config));
	}
	
	public void initialize(Minecraft mc) {
		for(Delegate delegate : this.elementList)
			delegate.initialize(mc);
	}
	
	public void setResolution(ScaledResolution resolution) {
		this.width = resolution.getScaledWidth();
		this.height = resolution.getScaledHeight();
	}

	public void switchMode(EnumOverlayMode mode) {
		for(Delegate delegate : this.elementList)
			delegate.element.switchMode(mode);
	}
	
	public void updateOverlay() {
		for(Delegate delegate : this.elementList)
			delegate.element.updateOverlay();
	}
	
	public void mouseClicked(int mouseX, int mouseY, int eventButton) {
		for(Delegate delegate : this.elementList)
		{
			boolean changed = false;

			ElementPos pos = delegate.pos;
			IGuiOverlay element = delegate.element;
			int width = element.getWidth();
			int height = element.getHeight();
			int scaledMouseX = pos.getHorizontalPos().translateInto(mouseX, this.width, width);
			int scaledMouseY = pos.getVerticalPos().translateInto(mouseY, this.height, height);
			scaledMouseX -= element.animationOffsetX(0.0f);
			scaledMouseY -= element.animationOffsetY(0.0f);
			
			changed = element.mouseClicked(scaledMouseX, scaledMouseY, eventButton) || changed;
			
			if(delegate.handler != null)
				changed = delegate.handler.mouseClicked(mouseX, mouseY, eventButton) || changed;
			
			if(changed)
				delegate.notified.syncFromFields();
		}
	}
	
	public void mouseMovedOrUp(int mouseX, int mouseY, int eventButton) {
		for(Delegate delegate : this.elementList)
		{
			boolean changed = false;

			ElementPos pos = delegate.pos;
			IGuiOverlay element = delegate.element;
			int width = element.getWidth();
			int height = element.getHeight();
			int scaledMouseX = pos.getHorizontalPos().translateInto(mouseX, this.width, width);
			int scaledMouseY = pos.getVerticalPos().translateInto(mouseY, this.height, height);
			scaledMouseX -= element.animationOffsetX(0.0f);
			scaledMouseY -= element.animationOffsetY(0.0f);
			
			changed = element.mouseMovedOrUp(scaledMouseX, scaledMouseY, eventButton) || changed;
			
			if(delegate.handler != null)
				changed = delegate.handler.mouseMovedOrUp(mouseX, mouseY, eventButton) || changed;
			
			if(changed)
				delegate.notified.syncFromFields();
		}
	}
	
	public void keyTyped(char eventChar, int eventKey) {
		
		for(Delegate delegate : this.elementList)
		{
			boolean changed = false;
			
			changed = delegate.element.keyTyped(eventChar, eventKey) || changed;
			
			if(delegate.handler != null)
				changed = delegate.handler.keyTyped(eventChar, eventKey) || changed;
			
			if(changed)
				delegate.notified.syncFromFields();
		}
	}
	
	public void render(int mouseX, int mouseY, float partialTicks) {
		for(Delegate delegate : this.elementList)
		{
			ElementPos pos = delegate.pos;
			IGuiOverlay element = delegate.element;
			int width = element.getWidth();
			int height = element.getHeight();
			float animationOffsetX = element.animationOffsetX(partialTicks);
			float animationOffsetY = element.animationOffsetY(partialTicks);
			
			int scaledMouseX = pos.getHorizontalPos().translateInto(mouseX, this.width, width);
			int scaledMouseY = pos.getVerticalPos().translateInto(mouseY, this.height, height);
			scaledMouseX -= animationOffsetX;
			scaledMouseY -= animationOffsetY;
			
			GL11.glPushMatrix();
			GL11.glTranslatef((pos.getHorizontalPos().getOffset(this.width, width) + animationOffsetX),
					(pos.getVerticalPos().getOffset(this.height, height) + animationOffsetY),
					0.0f);
			
			element.render(scaledMouseX, scaledMouseY, partialTicks);
			GL11.glPopMatrix();
			
			if(delegate.handler != null)
				delegate.handler.render(mouseX, mouseY, partialTicks);
		}
	}

	
	public OverlayContainer.Delegate getElement(int mouseX, int mouseY) {
		for(Delegate delegate : this.elementList) {
			int elementWidth = delegate.getWidth();
			int elementHeight = delegate.getHeight();
			
			if(delegate.pos.getHorizontalPos().inRange(mouseX, this.width, elementWidth))
				if(delegate.pos.getVerticalPos().inRange(mouseY, this.height, elementHeight))
					return delegate;
		}
		
		return null;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public String getLangKey() {
		return this.langKey;
	}
}